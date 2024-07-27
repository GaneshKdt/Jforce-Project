package com.nmims.services.impl;

import com.nmims.dto.PGGraceMarksDTO;
import com.nmims.services.GraceMarksService;

/**
 * Grace 4 is applicable for those program which has only 2 subjects e.g. CPM
 */
public class PGGraceFourImpl implements GraceMarksService {

	final int grace = 4;

	@Override
	public boolean satisfiesCondition(PGGraceMarksDTO pgGraceMarksDTO) {
		return 2 == Integer.valueOf(pgGraceMarksDTO.getNoOfSubjectsToClear());
	}

	@Override
	public int getGrace() {
		return grace;
	}

}
