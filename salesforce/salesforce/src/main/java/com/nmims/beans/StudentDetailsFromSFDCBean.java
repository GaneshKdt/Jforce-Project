package com.nmims.beans;

public class StudentDetailsFromSFDCBean extends StudentBean {
	
	//enrollmentMonth 
	private String session;
	
	//enrollmentYear
	private String year;
	
	//mobile	
	private String mobileNo;
	
	//regDate	
	private String accountConfirmDate;
	
	//imageUrl	
	private String studentImage;
	
	private String deRegistered;
	
	
	
	/**
	 * @return the deRegistered
	 */
	public String getDeRegistered() {
		return deRegistered;
	}

	/**
	 * @param deRegistered the deRegistered to set
	 */
	public void setDeRegistered(String deRegistered) {
		this.deRegistered = deRegistered;
	}

	@Override
	public String toString() {
		try {
			return ""+ super.toString() +"\n"
					+ " session :"+session+", year:"+year+", mobileNo : "+mobileNo+","
					+ " accountConfirmDate : "+accountConfirmDate+" studentImage:"+studentImage ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "Eror in displaying StudentDetailsFromSFDCBean data.";
		}
		
	}
	
	/**
	 * @return the session
	 */
	public String getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(String session) {
		this.session = session;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the mobileNo
	 */
	public String getMobileNo() {
		return mobileNo;
	}

	/**
	 * @param mobileNo the mobileNo to set
	 */
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	/**
	 * @return the accountConfirmDate
	 */
	public String getAccountConfirmDate() {
		return accountConfirmDate;
	}

	/**
	 * @param accountConfirmDate the accountConfirmDate to set
	 */
	public void setAccountConfirmDate(String accountConfirmDate) {
		this.accountConfirmDate = accountConfirmDate;
	}

	/**
	 * @return the studentImage
	 */
	public String getStudentImage() {
		return studentImage;
	}

	/**
	 * @param studentImage the studentImage to set
	 */
	public void setStudentImage(String studentImage) {
		this.studentImage = studentImage;
	}

	
	
	
	
	




}
