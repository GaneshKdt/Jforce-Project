package com.nmims.services.impl;

import com.nmims.dto.PGGraceMarksDTO;
import com.nmims.services.GraceMarksService;

/**
 * Grace 10 is applicable for MSc. Applied Finance and Certificate programs
 * which has 5 subjects per semester and
 */
public class PGGraceTenImpl implements GraceMarksService {

	final int grace = 10;

	@Override
	public boolean satisfiesCondition(PGGraceMarksDTO pgGraceMarksDTO) {
		return 5 == Integer.valueOf(pgGraceMarksDTO.getNoOfSubjectsToClearSem());
	}

	@Override
	public int getGrace() {
		return grace;
	}

}
