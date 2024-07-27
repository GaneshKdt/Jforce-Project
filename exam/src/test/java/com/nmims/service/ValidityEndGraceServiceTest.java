package com.nmims.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.PassFailTransferDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.dto.PGGraceMarksDTO;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.services.PGGraceMarksService;
import com.nmims.services.PassFailExecutorService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ValidityEndGraceServiceTest {

	@Autowired
	private PGGraceMarksService pgGraceMarksService;

	@MockBean
	private PassFailTransferDao passFailTransferDao;

	@MockBean
	private StudentMarksDAO studentMarksDao;

	@MockBean
	private PassFailDAO passFailDao;

	@MockBean
	private PassFailExecutorService passFailExecutorService;

	@Test
	public void loadContext() {
		final String examYear = "2023";
		final String examMonth = "Apr";

		when(passFailTransferDao.getValidityEndApplicableRecords(examYear, examMonth, 4)).thenReturn(new ArrayList<>());
		pgGraceMarksService.fetchAndApplyValidityEndGrace(examYear, examMonth);
	}
	

    @Test
    public void testGetValidityEndGraceApplicableRecords() {
        List<PGGraceMarksDTO> expectedRecords = new ArrayList<>();
        when(passFailTransferDao.getValidityEndApplicableRecords(anyString(), anyString(), anyInt())).thenReturn(expectedRecords);

        List<PGGraceMarksDTO> actualRecords = pgGraceMarksService.getValidityEndGraceApplicableRecords("2023", "July", 10);

        Assert.assertEquals(expectedRecords, actualRecords);
        verify(passFailTransferDao).getValidityEndApplicableRecords("2023", "July", 10);
    }

    @Test(expected = NoRecordFoundException.class)
    public void testUpdateMarksNoRecordsFound() throws NoRecordFoundException, SQLException {
        String totalGraceMarks = "10";
        String program = "MBA";
        String sapid = "7777777777";
        String consumerType = "Retai;";
        String resultProcessedYear = "2023";
        String resultProcessedMonth = "Jun";
        String prgmStructApplicable = "Jul2022";

        when(passFailDao.getSingleStudentPassFailMarksData(sapid, program)).thenReturn(new ArrayList<>());

        pgGraceMarksService.updateMarksAndReturnMarksRecords(totalGraceMarks, program, sapid, consumerType, resultProcessedYear, resultProcessedMonth, prgmStructApplicable);

    }

}

