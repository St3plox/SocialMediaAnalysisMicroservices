package com.tvey.DataAnalysisService.service.token;

import java.io.IOException;

public interface TokenService {

    String[] tokenize(String input) throws IOException;

}