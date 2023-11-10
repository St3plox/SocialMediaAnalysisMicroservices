package ru.tveu.DataCollectionService.service.data;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@PropertySource("application-dev.properties")
@RequiredArgsConstructor
public class YTDataRetrievalService implements DataRetrievalService {
    private static final String APPLICATION_NAME = "SentimentAnalysisApi";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${YOUTUBE_API_KEY}")
    private String developerKey;



    @Override
    public ApiDTO<YtContentDTO> retrieveData(String videoId, long maxComments, Set<String> usedCommentIds) throws IOException {
        YouTube youtubeService = getService();

        List<YtContentDTO> allComments = new ArrayList<>();
        String nextPageToken = null;
        int usedCommentsCount = usedCommentIds.size();

        while (true) {
            YouTube.CommentThreads.List request = youtubeService.commentThreads()
                    .list("snippet,replies")
                    .setKey(developerKey)
                    .setVideoId(videoId)
                    .setMaxResults(Math.min(maxComments + usedCommentIds.size(), 100))
                    .setPageToken(nextPageToken);

            CommentThreadListResponse response = request.execute();

            List<CommentThread> comments = response.getItems();

            if (comments == null || comments.isEmpty()) {
                break;
            }

            List<YtContentDTO> commentObjects = comments.stream()
                    .flatMap(commentThread -> extractComments(commentThread, usedCommentIds))
                    .toList();

            allComments.addAll(commentObjects);

            nextPageToken = response.getNextPageToken();

            if (nextPageToken == null || allComments.size() >= maxComments - usedCommentsCount) {
                break;
            }
        }

        Set<String> uniqueCommentIds = allComments.stream()
                .map(YtContentDTO::getId)
                .collect(Collectors.toSet());

        ApiDTO<YtContentDTO> transferObject = new ApiDTO<>(videoId);
        transferObject.setContentSize(uniqueCommentIds.size());
        transferObject.setContent(allComments);

        return transferObject;
    }


    private Stream<YtContentDTO> extractComments(CommentThread commentThread, Set<String> usedCommentIds) {
        Comment comment = commentThread.getSnippet().getTopLevelComment();
        long repliesCount = commentThread.getSnippet().getTotalReplyCount();

        Stream<YtContentDTO> topLevelCommentStream = Stream.of(createYtObject(comment))
                .filter(ytContentDTO -> !usedCommentIds.contains(ytContentDTO.getId()));

        Stream<YtContentDTO> replyStream = repliesCount > 0 ?
                commentThread.getReplies().getComments().stream()
                        .map(this::createYtObject)
                        .filter(replyDto -> !usedCommentIds.contains(replyDto.getId()))
                : Stream.empty();

        return Stream.concat(topLevelCommentStream, replyStream);
    }

    private static YouTube getService() {

        final NetHttpTransport httpTransport;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();

    }

    private YtContentDTO createYtObject(Comment comment) {

        String originalContent = comment.getSnippet().getTextOriginal();
        String truncatedContent = originalContent.length() > 999 ?
                originalContent.substring(0, 999) :
                originalContent;

        return YtContentDTO.builder()
                .id(comment.getId())
                .content(truncatedContent)
                .authorDisplayName(comment.getSnippet().getAuthorDisplayName())
                .publishedAt(comment.getSnippet().getPublishedAt().toString())
                .build();
    }


}