package com.tvey.DataAnalysisService.repository;

import com.tvey.DataAnalysisService.entity.AbstractAnalysisResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface AnalysisResultRepository<T extends AbstractAnalysisResult>
    extends CrudRepository<T, Long> {
}
