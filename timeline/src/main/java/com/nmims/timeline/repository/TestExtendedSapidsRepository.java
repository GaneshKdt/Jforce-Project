package com.nmims.timeline.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.TestExtendedSapids;
import com.nmims.timeline.model.TestExtendedSapidsCompositeKey;

@Repository
public interface TestExtendedSapidsRepository extends CrudRepository<TestExtendedSapids, TestExtendedSapidsCompositeKey>   {
	
	TestExtendedSapids findFirstBySapidAndTestId(String sapid,Long testId);
}
