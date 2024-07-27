package com.nmims.beans;

import java.io.Serializable;

public class ApplozicCreateGroupBean implements Serializable{

   private String groupName;

   private String imageUrl;

   private String admin;

   private int type;
   
   private metadata metadata = new metadata();

   public metadata getApplozicMetaData() {
	return metadata;
}

public void setApplozicMetaData(metadata applozicMetaData) {
	this.metadata = applozicMetaData;
}

public String getGroupName() {
      return groupName;
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getAdmin() {
      return admin;
   }

   public void setAdmin(String admin) {
      this.admin = admin;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }

   @Override
   public String toString() {
      return "ApplozicCreateGroupBean{" +
              "groupName='" + groupName + '\'' +
              ", imageUrl='" + imageUrl + '\'' +
              ", admin='" + admin + '\'' +
              ", type=" + type +
              '}';
   }
}
