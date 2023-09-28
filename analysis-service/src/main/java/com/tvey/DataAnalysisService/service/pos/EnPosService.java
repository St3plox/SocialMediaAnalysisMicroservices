package com.tvey.DataAnalysisService.service.pos;

import com.tvey.DataAnalysisService.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class EnPosService implements PosService {

    private final InputStream inputStreamPOSTagger = getClass()
            .getResourceAsStream("/models/opennlp-en-ud-ewt-pos-1.0-1.9.3.bin");
    ;

    private final POSModel posModel;

    {
        try {
            posModel = new POSModel(inputStreamPOSTagger);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private final POSTaggerME posTagger = new POSTaggerME(posModel);

    @Override
    public String[] tag(String[] tokens) {
        return posTagger.tag(tokens);
    }
}
