package com.tvey.DataAnalysisService.service.result.implemenation;

import com.tvey.DataAnalysisService.entity.AbstractAnalysisResult;
import com.tvey.DataAnalysisService.repository.AnalysisResultRepository;
import com.tvey.DataAnalysisService.service.result.interfaces.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisResultServiceImpl<T extends AbstractAnalysisResult> implements AnalysisResultService<T> {

    protected final AnalysisResultRepository<T> resultRepository;

    @Override
    public void saveAnalysisResult(T result) {
        resultRepository.save(result);
    }

    @Override
    public void saveAnalysisResult(List<T> results) {
        resultRepository.saveAll(results);
    }
}
