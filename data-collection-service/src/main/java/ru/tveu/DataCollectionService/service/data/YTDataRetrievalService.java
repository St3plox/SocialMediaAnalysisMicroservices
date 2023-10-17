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
import ru.tveu.DataCollectionService.service.url.UrlProcessor;
import ru.tveu.shared.dto.ApiDTO;
import ru.tveu.shared.dto.YtContentDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
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
    public ApiDTO<YtContentDTO> retrieveData(String videoId, long maxComments) throws IOException {
        YouTube youtubeService = getService();

        List<YtContentDTO> allComments = new ArrayList<>();


        String nextPageToken = null;


        while (true) {
            YouTube.CommentThreads.List request = youtubeService.commentThreads()
                    .list("snippet,replies")
                    .setKey(developerKey)
                    .setVideoId(videoId)
                    .setMaxResults(Math.min(maxComments - allComments.size(), 100))
                    .setPageToken(nextPageToken);

            CommentThreadListResponse response = request.execute();

            List<CommentThread> comments = response.getItems();

            if (comments == null || comments.isEmpty()) {
                break;
            }

            List<YtContentDTO> commentObjects = comments.stream()
                    .flatMap(commentThread -> {

                        Comment comment = commentThread.getSnippet().getTopLevelComment();
                        long repliesCount = commentThread.getSnippet().getTotalReplyCount();

                        Stream<YtContentDTO> topLevelCommentStream = Stream.of(
                                createYtObject(comment)
                        );

                        Stream<YtContentDTO> replyStream = repliesCount > 0 ?
                                commentThread.getReplies().getComments().stream().map(this::createYtObject
                                ) : Stream.empty();

                        return Stream.concat(topLevelCommentStream, replyStream);
                    })
                    .toList();

            allComments.addAll(commentObjects);

            nextPageToken = response.getNextPageToken();

            if (nextPageToken == null || allComments.size() >= maxComments) {
                break;
            }
        }

        ApiDTO<YtContentDTO> transferObject = new ApiDTO<>(videoId);
        transferObject.setContentSize(allComments.size());
        transferObject.setContent(allComments);

        return transferObject;
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

    private YtContentDTO createYtObject(Comment comment){
        return YtContentDTO.builder()
                .id(comment.getId())
                .content(comment.getSnippet().getTextOriginal())
                .authorDisplayName(comment.getSnippet().getAuthorDisplayName())
                .publishedAt(comment.getSnippet().getPublishedAt().toString())
                .build();
    }


}