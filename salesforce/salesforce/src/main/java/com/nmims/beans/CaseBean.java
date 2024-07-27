package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class CaseBean implements Serializable{
	
	private String id;
	private String parentId;
	private String caseNumber;
	private String caseStatus;
	private String createdById;
	private String createdDate;
	private String lastModifiedById;
	private String lastModifiedDate;
	private String systemModstamp;
	private String textBody;
	private String htmlBody;
	private String headers;
	private String subject;
	private String fromName;
	private String fromAddress;
	private String toAddress;
	private String ccAddress;
	private String bccAddress;
	private boolean incoming;
	private boolean hasAttachment;
	private String status;
	private String messageDate;
	private String replyToEmailMessageId;
	private boolean isPrivateDraft;
	private boolean isDeleted;
	private boolean isExternallyVisible;
	private boolean isClientManaged;
	private String relatedToId;
	private boolean isTracked;
	private boolean isBounced;
	private String emailTemplateId;
	private String iCEmail;
	private boolean success;
	private String successMessage;
	private boolean error;
	private String errorMessage;
	private String lastOpenedDate;
	private String validatedFromAddress;
	private String attachmentId;
	private String name;
	private String attachmentBody;
	private String bodyLength;
	private String contentType;
	private String description;
	private boolean isPrivate;
	private String ownerId;
	private ArrayList<CaseBean> attachments;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getCaseNumber() {
		return caseNumber;
	}
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	public String getCaseStatus() {
		return caseStatus;
	}
	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}
	public String getCreatedById() {
		return createdById;
	}
	public void setCreatedById(String createdById) {
		this.createdById = createdById;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedById() {
		return lastModifiedById;
	}
	public void setLastModifiedById(String lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getSystemModstamp() {
		return systemModstamp;
	}
	public void setSystemModstamp(String systemModstamp) {
		this.systemModstamp = systemModstamp;
	}
	public String getTextBody() {
		return textBody;
	}
	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}
	public String getHtmlBody() {
		return htmlBody;
	}
	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}
	public String getHeaders() {
		return headers;
	}
	public void setHeaders(String headers) {
		this.headers = headers;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getCcAddress() {
		return ccAddress;
	}
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	public String getBccAddress() {
		return bccAddress;
	}
	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessageDate() {
		return messageDate;
	}
	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}
	public String getReplyToEmailMessageId() {
		return replyToEmailMessageId;
	}
	public void setReplyToEmailMessageId(String replyToEmailMessageId) {
		this.replyToEmailMessageId = replyToEmailMessageId;
	}
	public boolean isPrivateDraft() {
		return isPrivateDraft;
	}
	public void setPrivateDraft(boolean isPrivateDraft) {
		this.isPrivateDraft = isPrivateDraft;
	}
	public boolean isClientManaged() {
		return isClientManaged;
	}
	public void setClientManaged(boolean isClientManaged) {
		this.isClientManaged = isClientManaged;
	}
	public String getRelatedToId() {
		return relatedToId;
	}
	public void setRelatedToId(String relatedToId) {
		this.relatedToId = relatedToId;
	}
	public boolean isTracked() {
		return isTracked;
	}
	public void setTracked(boolean isTracked) {
		this.isTracked = isTracked;
	}
	public boolean isBounced() {
		return isBounced;
	}
	public void setBounced(boolean isBounced) {
		this.isBounced = isBounced;
	}
	public String getEmailTemplateId() {
		return emailTemplateId;
	}
	public void setEmailTemplateId(String emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}
	public String getiCEmail() {
		return iCEmail;
	}
	public void setiCEmail(String iCEmail) {
		this.iCEmail = iCEmail;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getLastOpenedDate() {
		return lastOpenedDate;
	}
	public void setLastOpenedDate(String lastOpenedDate) {
		this.lastOpenedDate = lastOpenedDate;
	}
	public String getValidatedFromAddress() {
		return validatedFromAddress;
	}
	public void setValidatedFromAddress(String validatedFromAddress) {
		this.validatedFromAddress = validatedFromAddress;
	}
	public String getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAttachmentBody() {
		return attachmentBody;
	}
	public void setAttachmentBody(String attachmentBody) {
		this.attachmentBody = attachmentBody;
	}
	public String getBodyLength() {
		return bodyLength;
	}
	public void setBodyLength(String bodyLength) {
		this.bodyLength = bodyLength;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public boolean isIncoming() {
		return incoming;
	}
	public void setIncoming(boolean incoming) {
		this.incoming = incoming;
	}
	public boolean isHasAttachment() {
		return hasAttachment;
	}
	public void setHasAttachment(boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public boolean isExternallyVisible() {
		return isExternallyVisible;
	}
	public void setExternallyVisible(boolean isExternallyVisible) {
		this.isExternallyVisible = isExternallyVisible;
	}
	public ArrayList<CaseBean> getAttachments() {
		return attachments;
	}
	public void setAttachments(ArrayList<CaseBean> attachments) {
		this.attachments = attachments;
	}
	@Override
	public String toString() {
		return "CaseBean [id=" + id + ", parentId=" + parentId + ", caseNumber=" + caseNumber + ", caseStatus="
				+ caseStatus + ", createdById=" + createdById + ", createdDate=" + createdDate + ", lastModifiedById="
				+ lastModifiedById + ", lastModifiedDate=" + lastModifiedDate + ", systemModstamp=" + systemModstamp
				+ ", textBody=" + textBody + ", htmlBody=" + htmlBody + ", headers=" + headers + ", subject=" + subject
				+ ", fromName=" + fromName + ", fromAddress=" + fromAddress + ", toAddress=" + toAddress
				+ ", ccAddress=" + ccAddress + ", bccAddress=" + bccAddress + ", incoming=" + incoming
				+ ", hasAttachment=" + hasAttachment + ", status=" + status + ", messageDate=" + messageDate
				+ ", replyToEmailMessageId=" + replyToEmailMessageId + ", isPrivateDraft=" + isPrivateDraft
				+ ", isDeleted=" + isDeleted + ", isExternallyVisible=" + isExternallyVisible + ", isClientManaged="
				+ isClientManaged + ", relatedToId=" + relatedToId + ", isTracked=" + isTracked + ", isBounced="
				+ isBounced + ", emailTemplateId=" + emailTemplateId + ", iCEmail=" + iCEmail + ", success=" + success
				+ ", successMessage=" + successMessage + ", error=" + error + ", errorMessage=" + errorMessage
				+ ", lastOpenedDate=" + lastOpenedDate + ", validatedFromAddress=" + validatedFromAddress
				+ ", attachmentId=" + attachmentId + ", name=" + name + ", attachmentBody=" + attachmentBody
				+ ", bodyLength=" + bodyLength + ", contentType=" + contentType + ", description=" + description
				+ ", isPrivate=" + isPrivate + ", ownerId=" + ownerId + ", attachments=" + attachments + "]";
	}
}
