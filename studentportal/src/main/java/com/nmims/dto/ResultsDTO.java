package com.nmims.dto;

import java.util.List;

public class ResultsDTO extends BaseDTO{
	public List<ResultsDataDTO> listResultsDataDTO;

	public ResultsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResultsDTO(String sapid) {
		super(sapid);
		// TODO Auto-generated constructor stub
	}

	public List<ResultsDataDTO> getListResultsDataDTO() {
		return listResultsDataDTO;
	}

	public void setListResultsDataDTO(List<ResultsDataDTO> listResultsDataDTO) {
		this.listResultsDataDTO = listResultsDataDTO;
	}
}
