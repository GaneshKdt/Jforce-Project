package com.nmims.beans;

import java.io.Serializable;

public class KnowYourPolicyBean implements Serializable {
	private int policyId;
	private int groupId;
	private int categoryId;
	private int subcategoryId;
	private int mapId;
	private int masterkey;
	private String title;
	private String description;
	private String categoryName;
	private String subcategoryName;
	private String groupName;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;

	public int getPolicyId() {
		return policyId;
	}

	public void setPolicyId(int policyId) {
		this.policyId = policyId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getSubcategoryId() {
		return subcategoryId;
	}

	public void setSubcategoryId(int subcategoryId) {
		this.subcategoryId = subcategoryId;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getMasterkey() {
		return masterkey;
	}

	public void setMasterkey(int masterkey) {
		this.masterkey = masterkey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getSubcategoryName() {
		return subcategoryName;
	}

	public void setSubcategoryName(String subcategoryName) {
		this.subcategoryName = subcategoryName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public String toString() {
		return "PolicyBean [policyId=" + policyId + ", groupId=" + groupId + ", categoryId=" + categoryId
				+ ", subcategoryId=" + subcategoryId + ", mapId=" + mapId + ", masterkey=" + masterkey + ", title="
				+ title + ", description=" + description + ", categoryName=" + categoryName + ", subcategoryName="
				+ subcategoryName + ", groupName=" + groupName + ", createdBy=" + createdBy + ", createdDate="
				+ createdDate + ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate + "]";
	}

}
