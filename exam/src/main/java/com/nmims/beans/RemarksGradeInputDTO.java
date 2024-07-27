/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

/**
 * @author vil_m
 *
 */
public class RemarksGradeInputDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sapid;
	private String productType;// used from Mobile display result.
	private String gradingType;// used from Mobile display result.

	public RemarksGradeInputDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RemarksGradeInputDTO(String sapid, String productType, String gradingType) {
		super();
		this.sapid = sapid;
		this.productType = productType;
		this.gradingType = gradingType;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getGradingType() {
		return gradingType;
	}

	public void setGradingType(String gradingType) {
		this.gradingType = gradingType;
	}

}
