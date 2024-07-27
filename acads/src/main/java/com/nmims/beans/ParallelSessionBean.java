package com.nmims.beans;

import java.io.Serializable;

public class ParallelSessionBean  implements Serializable  {

	  String id =  "";
      String facultyId = "";
      String firstName = "";
      String lastName = "";
      String email = "";
      String active = "";
      String mobile = "";
      String createdBy = "";
      String createdDate = "";
      String lastModifiedBy = "";
      String lastModifiedDate = "";
      String seats = "";
      String joinFor = "";
      SessionDayTimeAcadsBean sessionBean;
    	      
     
      public String getId(){
    	  return id;
      }
      
      public void setId(String id) {
    	  this.id = id;
      }
      
      public String getFacultyId(){
    	  return facultyId;
      }

      public void setFacultyId(String facultyId) {
    	  	this.facultyId = facultyId;
      }
      
      public String getFirstName(){
    	  return firstName;
      }

      public void setFirstName(String firstName) {
    	  this.firstName = firstName;
      }
      
      public String getLastName(){
    	  return lastName;
      }

      public void setLastName(String lastName) {
    	  this.lastName = lastName;
      }
      
      public String getEmail(){
    	  return email;
      }

      public void setEmail(String email) {
    	  this.email = email;
      }
      
      public String getActive(){
    	  return active;
      }

      public void setActive(String active) {
    	  this.active = active;
      }
      
      public String getMobile(){
    	  return mobile;
      }

      public void setMobile(String mobile) {
    	  this.mobile = mobile;
      }
      
      public String getCreatedBy(){
    	  return createdBy;
      }
      
      public void setCreatedBy(String createdBy) {
    	  this.createdBy = createdBy;
      }
      
      public String getCreatedDate(){
    	  return createdDate;
      }
      
      public void setCreatedDate(String createdDate) {
    	  this.createdDate = createdDate;
      }
      
      public String getLastModifiedBy(){
    	  return lastModifiedBy;
      }
  
      public void setLastModifiedBy(String lastModifiedBy) {
    	  this.lastModifiedBy = lastModifiedBy;
      }
      
      public String getLastModifiedDate(){
    	  return lastModifiedDate;

      }
      public void setLastModifiedDate(String lastModifiedDate) {
    	  this.lastModifiedDate = lastModifiedDate;
      }
      
      public String getSeats(){
    	  return seats;
      }
      
      public void setSeats(String seats) {
    	  this.seats = seats;
      }

	public String getJoinFor() {
		return joinFor;
	}

	public void setJoinFor(String joinFor) {
		this.joinFor = joinFor;
	}

	public SessionDayTimeAcadsBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionDayTimeAcadsBean sessionBean) {
		this.sessionBean = sessionBean;
	}
}
