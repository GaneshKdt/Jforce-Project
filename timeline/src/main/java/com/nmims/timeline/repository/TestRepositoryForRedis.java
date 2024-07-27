package com.nmims.timeline.repository;

import com.nmims.timeline.model.TestBean;

public interface TestRepositoryForRedis {

	String save(TestBean test);

	String delete(Long id);

	TestBean findFirstById(Long id);

}
