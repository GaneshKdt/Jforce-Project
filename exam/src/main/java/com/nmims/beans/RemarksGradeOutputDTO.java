/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

/**
 * @author vil_m
 *
 */
public class RemarksGradeOutputDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<RemarksGradeResultsDTO> listRemarksGradeResultsDTO;
	
	private String status;
	private String message;
	
	public RemarksGradeOutputDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RemarksGradeOutputDTO(List<RemarksGradeResultsDTO> listRemarksGradeResultsDTO) {
		super();
		this.listRemarksGradeResultsDTO = listRemarksGradeResultsDTO;
	}

	public List<RemarksGradeResultsDTO> getListRemarksGradeResultsDTO() {
		return listRemarksGradeResultsDTO;
	}

	public void setListRemarksGradeResultsDTO(List<RemarksGradeResultsDTO> listRemarksGradeResultsDTO) {
		this.listRemarksGradeResultsDTO = listRemarksGradeResultsDTO;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
