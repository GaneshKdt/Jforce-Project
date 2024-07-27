package com.nmims.services.impl;

import com.nmims.dto.PGGraceMarksDTO;
import com.nmims.services.GraceMarksService;

/**
 * Grace 12 is applicable for Diploma/ PG/ MBA programs which has 6 subjects per
 * semester.
 * 
 */
public class PGGraceTwelveImpl implements GraceMarksService {

	final int grace = 12;

	@Override
	public boolean satisfiesCondition(PGGraceMarksDTO pgGraceMarksDTO) {
		return 6 == Integer.valueOf(pgGraceMarksDTO.getNoOfSubjectsToClearSem());
	}

	@Override
	public int getGrace() {
		return grace;
	}

}
