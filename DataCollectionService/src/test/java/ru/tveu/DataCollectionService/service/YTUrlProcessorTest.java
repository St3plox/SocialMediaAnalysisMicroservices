package ru.tveu.DataCollectionService.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tveu.DataCollectionService.exception.url.UrlProcessorException;
import ru.tveu.DataCollectionService.service.url.YTUrlProcessor;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class YTUrlProcessorTest {


    private static String fullUrl;

    private static String noProtocolUrl;

    private static String noDomainUrl;

    private static String wrongUrl;

    private static String videoId;


    @BeforeEach
    void init() {
        fullUrl = "https://www.youtube.com/watch?v=qOqsEKGFUIY";
        noProtocolUrl = "www.youtube.com/watch?v=qOqsEKGFUIY";
        noDomainUrl = "youtube.com/watch?v=qOqsEKGFUIY";
        videoId = "qOqsEKGFUIY";
        wrongUrl="youtube.com/watch?v";
    }


    @InjectMocks
    YTUrlProcessor urlProcessor;

    @Test
    void extractYTVideoId() {

        String idFromFullUrl = urlProcessor.extractContentId(fullUrl);
        String idFromNoProtocolUrl = urlProcessor.extractContentId(noProtocolUrl);
        String idFromNoDomainUrl = urlProcessor.extractContentId(noDomainUrl);

        UrlProcessorException thrown = Assertions.assertThrows(UrlProcessorException.class, () -> urlProcessor.extractContentId(wrongUrl));

        assertAll(
                () -> assertEquals(idFromFullUrl, videoId),
                () -> assertEquals(idFromNoProtocolUrl, videoId),
                () -> assertEquals(idFromNoDomainUrl, videoId),
                () -> assertEquals(thrown.getMessage(), YTUrlProcessor.EXCEPTION_MESSAGE)
        );
    }
}