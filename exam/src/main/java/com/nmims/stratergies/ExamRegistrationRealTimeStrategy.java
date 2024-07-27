package com.nmims.stratergies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Component;

import com.nmims.beans.ExamBookingTransactionBean;

@Component
public interface ExamRegistrationRealTimeStrategy {
	
	public void registrationOnMettlAndReleaseBooking(List<ExamBookingTransactionBean> currentTransactionBean,List<ExamBookingTransactionBean> toReleaseBookingsList);
	public void registerStudentOnMettl(HashSet<String> examDateSet,String sapid,String examMonth,String examYear);
	public void releaseBooking(List<ExamBookingTransactionBean> toReleaseBookingsList); 
}
