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
import ru.tveu.DataCollectionService.dto.DataSource;
import ru.tveu.DataCollectionService.dto.YtTransferObject;
import ru.tveu.DataCollectionService.service.url.UrlProcessor;

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

    private final UrlProcessor urlProcessor;


    @Override
    public List<YtTransferObject> retrieveData(String url, long maxComments) throws IOException {
        YouTube youtubeService = getService();

        String videoId = urlProcessor.extractContentId(url);

        List<YtTransferObject> allComments = new ArrayList<>();


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

            List<YtTransferObject> commentObjects = comments.stream()
                    .flatMap(commentThread -> {
                        Comment comment = commentThread.getSnippet().getTopLevelComment();
                        long repliesCount = commentThread.getSnippet().getTotalReplyCount();

                        Stream<YtTransferObject> topLevelCommentStream = Stream.of(
                                YtTransferObject.builder()
                                        .id(comment.getId())
                                        .videoId(videoId)
                                        .source(DataSource.YOUTUBE)
                                        .content(comment.getSnippet().getTextOriginal())
                                        .authorDisplayName(comment.getSnippet().getAuthorDisplayName())
                                        .publishedAt(comment.getSnippet().getPublishedAt().toString())
                                        .build()
                        );

                        Stream<YtTransferObject> replyStream = repliesCount > 0 ?
                                commentThread.getReplies().getComments().stream().map(reply ->
                                        YtTransferObject.builder()
                                                .id(reply.getId())
                                                .videoId(videoId)
                                                .source(DataSource.YOUTUBE)
                                                .content(reply.getSnippet().getTextOriginal())
                                                .authorDisplayName(reply.getSnippet().getAuthorDisplayName())
                                                .publishedAt(reply.getSnippet().getPublishedAt().toString())
                                                .build()
                                ) : Stream.empty();

                        return Stream.concat(topLevelCommentStream, replyStream);
                    })
                    .toList();

            allComments.addAll(commentObjects);

            nextPageToken = response.getNextPageToken();

            if (nextPageToken == null || allComments.size() >= maxComments) {
                break;  // No more comments or reached the max limit
            }
        }

        return allComments;
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


}