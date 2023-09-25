package ru.tveu.DataCollectionService.service.url;

import org.springframework.stereotype.Component;
import ru.tveu.DataCollectionService.exception.url.UrlProcessorException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class YTUrlProcessor implements UrlProcessor{

    public final static String EXCEPTION_MESSAGE = "Yt url do not contain an id";
    private final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};


    public String extractContentId(String youTubeUrl) throws UrlProcessorException{

        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(youTubeUrl);

        for(String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if(matcher.find()){
                return matcher.group(1);
            }else {
                throw new UrlProcessorException("Yt url do not contain an id");
            }
        }

        return null;
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {

        String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if(matcher.find()){
            return url.replace(matcher.group(), "");
        }
        return url;
    }
}