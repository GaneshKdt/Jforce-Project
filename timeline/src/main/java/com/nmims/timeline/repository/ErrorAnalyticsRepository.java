package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.ErrorAnalytics;

@Repository
public interface ErrorAnalyticsRepository extends CrudRepository<ErrorAnalytics, Long>{

	List<ErrorAnalytics> findAllByModule(String module);
}
