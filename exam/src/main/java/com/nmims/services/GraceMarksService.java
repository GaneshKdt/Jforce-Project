package com.nmims.services;

import com.nmims.dto.PGGraceMarksDTO;

/**
 * Interface created to make it easy to implement new grace system and its
 * business logic or change in existing business logic conditions derived from
 * shortcut card(17699) comments on individual grace marks classes.
 * 
 * @author Swarup Singh Rajpurohit
 *
 */
public interface GraceMarksService {

	boolean satisfiesCondition(PGGraceMarksDTO pgGraceMarksDTO);

	int getGrace();

}
