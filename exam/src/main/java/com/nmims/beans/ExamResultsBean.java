package com.nmims.beans;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultsBean implements Serializable {
	private Integer id;
	private String acadYear;
	private String acadMonth;
	private String examYear;
	private String examMonth;
	private String resultDeclareDate;
}
