package com.nmims.timeline.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.MBAXTestExtendedSapids;
import com.nmims.timeline.model.TestExtendedSapids;
import com.nmims.timeline.model.TestExtendedSapidsCompositeKey;

@Repository
public interface MBAXTestExtendedSapidsRepository extends CrudRepository<TestExtendedSapids, TestExtendedSapidsCompositeKey>   {
	
	MBAXTestExtendedSapids findFirstBySapidAndTestId(String sapid,Long testId);
}
