package com.nmims.stratergies.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.nmims.beans.MBAPassFailBean;
import com.nmims.stratergies.MBAGradePointStrategyInterface;

@Service
public class MBAGradePointStrategy implements MBAGradePointStrategyInterface {

	@Override
	public String getGPA(int term, List<MBAPassFailBean> subjectResults) throws Exception {

		// GPA is only for the CURRENT semester 
		// GPA = (Sum of credits obtained) / (Total credits)
		// Credits Obtained for a subject = (Grade Point for subject) * (Number Of Credits For Subject)
		
		float totalCredits = 0;
		float totalCreditsObtained = 0;
		for (MBAPassFailBean subjectResult : subjectResults) {	

			if(subjectResult == null || !StringUtils.isNumeric(subjectResult.getTerm())) {
				throw new Exception("Error parsing result!");
			}

			if(term == Integer.parseInt(subjectResult.getTerm())) {
				if(StringUtils.isBlank(subjectResult.getCredits()) || StringUtils.isBlank(subjectResult.getPoints())) {
					throw new Exception("Credits not generated for subject!");
				}
				
				if(!NumberUtils.isParsable(subjectResult.getCredits()) || !NumberUtils.isParsable(subjectResult.getPoints()) ) {
					throw new Exception("Invalid string in credits for subject!");
				}
				
				float subjectCredits = Float.parseFloat(subjectResult.getCredits());
				float subjectGradePoint = Float.parseFloat(subjectResult.getPoints());
				
				float creditsObtained = subjectGradePoint * subjectCredits;
				
				totalCreditsObtained = totalCreditsObtained + creditsObtained;
				totalCredits = totalCredits + subjectCredits;	
			}
		}
		
		float termGPA = totalCreditsObtained / totalCredits;		
		return String.format("%,.2f", termGPA);
	}

	@Override
	public String getCGPA(int term, List<MBAPassFailBean> passFailDataListAllSem) throws Exception {

		// CGPA is only for the current and all previous semesters
		
		// CGPA = (Sum of credits obtained) / (Total credits)
		// Credits Obtained for a subject = (Grade Point for subject) * (Number Of Credits For Subject)
		// Ex. For sem 3, the CGPA would be (Sum of credits obtained in sem 1, 2 and 3) / (Total credits in sem 1, 2 and 3) 
		
		float totalCredits = 0;
		float totalCreditsObtained = 0;
		for (MBAPassFailBean subjectResult : passFailDataListAllSem) {
			
			if(subjectResult == null || !StringUtils.isNumeric(subjectResult.getTerm())) {
				throw new Exception("Error parsing result!");
			}

			if(term >= Integer.parseInt(subjectResult.getTerm())) {
				if(StringUtils.isBlank(subjectResult.getCredits()) || StringUtils.isBlank(subjectResult.getPoints())) {
					throw new Exception("Credits not generated for subject!");
				}
				
				if(!NumberUtils.isParsable(subjectResult.getCredits()) || !NumberUtils.isParsable(subjectResult.getPoints()) ) {
					throw new Exception("Invalid string in credits for subject!");
				}
				
				float subjectCredits = Float.parseFloat(subjectResult.getCredits());
				float subjectGradePoint = Float.parseFloat(subjectResult.getPoints());
				
				float creditsObtained = subjectGradePoint * subjectCredits;
				
				totalCreditsObtained = totalCreditsObtained + creditsObtained;
				totalCredits = totalCredits + subjectCredits;	
			}
		}	
		float termCGPA = totalCreditsObtained / totalCredits;
		return String.format("%,.2f", termCGPA);
	}

}
