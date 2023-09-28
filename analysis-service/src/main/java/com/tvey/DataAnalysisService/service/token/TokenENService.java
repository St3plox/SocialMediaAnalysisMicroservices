package com.tvey.DataAnalysisService.service.token;

import lombok.RequiredArgsConstructor;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TokenENService implements TokenService {

    private final TokenizerME tokenizer;

    public TokenENService() {
        try (InputStream inputStream = getClass().getResourceAsStream("/models/en-token.bin")) {
            if (inputStream == null) {
                throw new IOException("Model file not found");
            }

            TokenizerModel model = new TokenizerModel(inputStream);
            tokenizer = new TokenizerME(model);
        } catch (IOException e) {
            // Log the exception and handle it appropriately
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize TokenENService", e);
        }

    }

    @Override
    public String[] tokenize(String input) throws IOException {
        return tokenizer.tokenize(input);
    }
}
