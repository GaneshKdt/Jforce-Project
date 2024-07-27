package com.nmims.beans;


public class SrTypeMasterKeyMapping extends BaseStudentPortalBean
{
	String id;
	String masterkey;
	String srtype_Id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMasterkey() {
		return masterkey;
	}
	public void setMasterkey(String masterkey) {
		this.masterkey = masterkey;
	}
	public String getSrtype_Id() {
		return srtype_Id;
	}
	public void setSrtype_Id(String srtype_Id) {
		this.srtype_Id = srtype_Id;
	}
	
	@Override
	public String toString() {
		return "SrTypeMasterKeyMapping [id=" + id + ", masterkey=" + masterkey + ", srtype_Id=" + srtype_Id + "]";
	}
	
}
