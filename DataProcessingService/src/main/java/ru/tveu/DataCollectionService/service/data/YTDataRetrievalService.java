package ru.tveu.DataCollectionService.service.data;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
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
import ru.tveu.DataCollectionService.service.url.YTUrlProcessor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("application-dev.properties")
@RequiredArgsConstructor
public class YTDataRetrievalService implements DataRetrievalService {

    private static final String APPLICATION_NAME = "SentimentAnalysisApi";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${YOUTUBE_API_KEY}")
    private String developerKey;

    private final YTUrlProcessor urlProcessor;

    @Override
    public List<YtTransferObject> retrieveData(String url) throws IOException {

        YouTube youtubeService = getService();

        YouTube.CommentThreads.List request = youtubeService.commentThreads()
                .list("snippet,replies");

        String videoId = urlProcessor.extractContentId(url);

        CommentThreadListResponse response = request.setKey(developerKey)
                .setVideoId(videoId)
                .setMaxResults(100L)
                .execute();

        List<CommentThread> comments = response.getItems();
        List<YtTransferObject> transferObjects = new ArrayList<>(response.size());

        comments.forEach(
                (commentThread) ->
                {
                    Comment comment =
                            commentThread.getSnippet().getTopLevelComment();

                    long repliesCount = commentThread.getSnippet().getTotalReplyCount();

                    transferObjects.add(
                            new YtTransferObject(
                                    comment.getId(),
                                    DataSource.YOUTUBE,
                                    comment.getSnippet().getTextOriginal(),
                                    comment.getSnippet().getAuthorDisplayName(),
                                    comment.getSnippet().getPublishedAt().toString()
                            )
                    );

                    if (repliesCount == 0) return;

                    for (Comment reply : commentThread.getReplies().getComments()) {

                        transferObjects.add(
                                new YtTransferObject(
                                        reply.getId(),
                                        DataSource.YOUTUBE,
                                        reply.getSnippet().getTextOriginal(),
                                        reply.getSnippet().getAuthorDisplayName(),
                                        reply.getSnippet().getPublishedAt().toString()
                                )
                        );
                    }
                }
        );


        return transferObjects;
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