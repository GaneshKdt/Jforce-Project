package com.nmims.beans;

import java.util.ArrayList;
import java.util.HashMap;

public class LinkedInAddCertToProfileBean {

	private String startTask;
	private String name;
	private String organizationId;
	private String issueYear;
	private String issueMonth;
	private String expirationYear;
	private String expirationMonth;
	private String certUrl;
	private String certId;
	private String consumerProgramStructureId;

	private String  id;  
	private String personId; 
	private String sapId;  
	private String firstName;  
	private String localizedFirstName;  
	private String lastName;  
	private String localizedLastName;  
	private String profilePicture;  
	private String authorization_code;  
	private String access_token;  
	private String expires_in; 

	private String grant_type;
	private String code;
	private String redirect_uri;
	private String client_id;
	private String client_secret;

	private String author;
	private String lifecycleState;
	private HashMap<String,SpecificContent> specificContent;
    private String linkedInOauthRedirectURL;
    private boolean linkedInAuthorized;
    private boolean linkedInShareStatus;
    private String shareLinkURL;
    private String shareText;
    private String message;
    private String scope;

	public HashMap<String, SpecificContent> getSpecificContent() {
		return specificContent;
	}

	public void setSpecificContent(HashMap<String, SpecificContent> specificContent) {
		this.specificContent = specificContent;
	}

	public class SpecificContent {

		private HashMap<String,String> shareCommentary;
		private String shareMediaCategory;
		private ArrayList<Media> media;



		public ArrayList<Media> getMedia() {
			return media;
		}

		public void setMedia(ArrayList<Media> media) {
			this.media = media;
		}

		public class Media {
			public String getStatus() {
				return status;
			}
			public void setStatus(String status) {
				this.status = status;
			}
			public HashMap<String,String> getDescription() {
				return description;
			}
			public void setDescription(HashMap<String,String> description) {
				this.description = description;
			}
			public String getOriginalUrl() {
				return originalUrl;
			}
			public void setOriginalUrl(String originalUrl) {
				this.originalUrl = originalUrl;
			}

			private String status;
			private HashMap<String,String>  description;
			private String originalUrl;
			private HashMap<String,String> title;
			public HashMap<String, String> getTitle() {
				return title;
			}
			public void setTitle(HashMap<String, String> title) {
				this.title = title;
			}
		}

		public String getShareMediaCategory() {
			return shareMediaCategory;
		}

		public void setShareMediaCategory(String shareMediaCategory) {
			this.shareMediaCategory = shareMediaCategory;
		}

		public HashMap<String,String> getShareCommentary() {
			return shareCommentary;
		}

		public void setShareCommentary(HashMap<String,String> shareCommentary) {
			this.shareCommentary = shareCommentary;
		}


	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public HashMap<String, String> getVisibility() {
		return visibility;
	}

	public void setVisibility(HashMap<String, String> visibility) {
		this.visibility = visibility;
	}

	private String media;
	private HashMap<String,String> visibility;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLifecycleState() {
		return lifecycleState;
	}

	public void setLifecycleState(String lifecycleState) {
		this.lifecycleState = lifecycleState;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLocalizedFirstName() {
		return localizedFirstName;
	}

	public void setLocalizedFirstName(String localizedFirstName) {
		this.localizedFirstName = localizedFirstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLocalizedLastName() {
		return localizedLastName;
	}

	public void setLocalizedLastName(String localizedLastName) {
		this.localizedLastName = localizedLastName;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getAuthorization_code() {
		return authorization_code;
	}

	public void setAuthorization_code(String authorization_code) {
		this.authorization_code = authorization_code;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String programName) {
		this.name = programName;
	}

	public String getIssueYear() {
		return issueYear;
	}

	public void setIssueYear(String issueYear) {
		this.issueYear = issueYear;
	}

	public String getIssueMonth() {
		return issueMonth;
	}

	public void setIssueMonth(String issueMonth) {
		this.issueMonth = issueMonth;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public String getCertUrl() {
		return certUrl;
	}

	public void setCertUrl(String certUrl) {
		this.certUrl = certUrl;
	}

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public String getStartTask() {
		return startTask;
	}

	public void setStartTask(String startTask) {
		this.startTask = startTask;
	}

	public String getGrant_type() {
		return grant_type;
	}

	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRedirect_uri() {
		return redirect_uri;
	}

	public void setRedirect_uri(String redirect_uri) {
		this.redirect_uri = redirect_uri;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	public String getLinkedInOauthRedirectURL() {
		return linkedInOauthRedirectURL;
	}

	public void setLinkedInOauthRedirectURL(String linkedInOauthRedirectURL) {
		this.linkedInOauthRedirectURL = linkedInOauthRedirectURL;
	}

	public boolean isLinkedInAuthorized() {
		return linkedInAuthorized;
	}

	public void setLinkedInAuthorized(boolean linkedInAuthorized) {
		this.linkedInAuthorized = linkedInAuthorized;
	}

	public boolean isLinkedInShareStatus() {
		return linkedInShareStatus;
	}

	public void setLinkedInShareStatus(boolean linkedInShareStatus) {
		this.linkedInShareStatus = linkedInShareStatus;
	}

	public String getShareLinkURL() {
		return shareLinkURL;
	}

	public void setShareLinkURL(String shareLinkURL) {
		this.shareLinkURL = shareLinkURL;
	}

	public String getShareText() {
		return shareText;
	}

	public void setShareText(String shareText) {
		this.shareText = shareText;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "LinkedInAddCertToProfileBean [startTask=" + startTask + ", name=" + name + ", organizationId="
				+ organizationId + ", issueYear=" + issueYear + ", issueMonth=" + issueMonth + ", expirationYear="
				+ expirationYear + ", expirationMonth=" + expirationMonth + ", certUrl=" + certUrl + ", certId="
				+ certId + ", consumerProgramStructureId=" + consumerProgramStructureId + ", id=" + id + ", personId="
				+ personId + ", sapId=" + sapId + ", firstName=" + firstName + ", localizedFirstName="
				+ localizedFirstName + ", lastName=" + lastName + ", localizedLastName=" + localizedLastName
				+ ", profilePicture=" + profilePicture + ", authorization_code=" + authorization_code
				+ ", access_token=" + access_token + ", expires_in=" + expires_in + ", grant_type=" + grant_type
				+ ", code=" + code + ", redirect_uri=" + redirect_uri + ", client_id=" + client_id + ", client_secret="
				+ client_secret + ", author=" + author + ", lifecycleState=" + lifecycleState + ", specificContent="
				+ specificContent + ", linkedInOauthRedirectURL=" + linkedInOauthRedirectURL + ", linkedInAuthorized="
				+ linkedInAuthorized + ", linkedInShareStatus=" + linkedInShareStatus + ", shareLinkURL=" + shareLinkURL
				+ ", shareText=" + shareText + ", message=" + message + ", scope=" + scope + ", media=" + media
				+ ", visibility=" + visibility + "]";
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
