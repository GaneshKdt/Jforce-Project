package com.nmims.beans;

import java.io.Serializable;

public class LTIConsumerRequestBean  implements Serializable {

	String context_id =	"S3294476";
	String context_label =	"ST101";
	String context_title =	"Telecommuncations 101";
	String context_type	= "CourseSection";
	String custom_context_memberships_url =	"http://ltiapps.net/test/tc-memberships.php/context/c66f242622feb71bc917b008f26b227b";
	String custom_context_setting_ur1 = "http://ltiapps.net/test/tc-settings.php/context/c66f242622feb71bc917b008f26b227b";
	String custom_lineitem_url = "http://ltiapps.net/test/tc-outcomes2.php/c66f242622feb71bc917b008f26b227b/S3294476/lineitems/dyJ86SiwwA9";
	String custom_lineitems_url = "http://ltiapps.net/test/tc-outcomes2.php/c66f242622feb71bc917b008f26b227b/S3294476/lineitems";
	String custom_link_memberships_url	= "http://ltiapps.net/test/tc-memberships.php/link/c66f242622feb71bc917b008f26b227b";
	String custom_link_setting_url	= "http://ltiapps.net/test/tc-settings.php/link/c66f242622feb71bc917b008f26b227b";
	String custom_result_url =	"http://ltiapps.net/test/tc-outcomes2.php/c66f242622feb71bc917b008f26b227b/S3294476/lineitems/dyJ86SiwwA9/results/29123";
	String custom_results_url =	"http://ltiapps.net/test/tc-outcomes2.php/c66f242622feb71bc917b008f26b227b/S3294476/lineitems/dyJ86SiwwA9/results";
	String custom_system_setting_url =	"http://ltiapps.net/test/tc-settings.php/system/c66f242622feb71bc917b008f26b227b";
	String custom_tc_profile_url =	"http://ltiapps.net/test/tc-profile.php/c66f242622feb71bc917b008f26b227b";
	String ext_ims_lis_basic_outcome_url = "http://ltiapps.net/test/tc-ext-outcomes.php";
	String ext_ims_lis_memberships_id =	"c66f242622feb71bc917b008f26b227b:::4jflkkdf9s";
	String ext_ims_lis_memberships_url = "http://ltiapps.net/test/tc-ext-memberships.php";
	String ext_ims_lis_resultvalue_sourcedids	= "decimal";
	String ext_ims_lti_tool_setting_id	= "c66f242622feb71bc917b008f26b227b:::d94gjklf954kj";
	String ext_ims_lti_tool_setting_url	= "http://ltiapps.net/test/tc-ext-setting.php";
	String launch_presentation_css_url =	"http://ltiapps.net/test/css/tc.css";
	String launch_presentation_document_target =	"frame";
	String launch_presentation_locale	= "en-GB";
	String launch_presentation_return_url =	"http://ltiapps.net/test/tc-return.php";
	String lis_course_offering_sourcedid =	"DD-ST101";
	String lis_course_section_sourcedid	= "DD-ST101:C1";
	String lis_outcome_service_url	= "http://ltiapps.net/test/tc-outcomes.php";
	String lis_person_contact_email_primary = "sanketpanaskar@gmail.com";
	String lis_person_name_family =	"Jane Public";
	String lis_person_name_full =	"Jane Public";
	String lis_person_name_given =	"Jane";
	String lis_person_sourced_id	= "sis:942a8dd9"; 
	String lis_result_sourcedid =	"c66f242622feb71bc917b008f26b227b:::S3294476:::29123:::dyJ86SiwwA9";
	String lti_message_type =	"basic-lti-launch-request";
	String lti_version = "LTI-1p0";
	String oauth_callback =	"about:blank";
	String oauth_consumer_key =	"2FO2-J2FJ-E8HB";
	String oauth_nonce = "620c8b671c42b5f1a6f99d6f97ffea6d";
	String oauth_signature =	"A65jvlJYWl3XRB4UzS721uPK/FE=";
	String oauth_signature_method =	"HMAC-SHA1";
	String oauth_timestamp =	"1559028440";
	String oauth_version =	"1.0";
	String resource_link_description = "Will ET phone home, or not; click to discover more.";
	String resource_link_id	= "9e822d48-2f8d-411e-a3f4-bbeeedc801a51";
	String resource_link_title	 = "Phone home";
	String roles =	"Student";
	String tool_consumer_info_product_family_code = "jisc";
	String tool_consumer_info_version	= "1.2";
	String tool_consumer_instance_contact_email =	"vle@uni.ac.uk";
	String tool_consumer_instance_description = "A Higher Education establishment in a land far, far away.";
	String tool_consumer_instance_guid =	"vle.uni.ac.uk";
	String tool_consumer_instance_name =	"University of JISC";
	String tool_consumer_instance_url =	"https://vle.uni.ac.uk/";
	String user_id	= "7837467321";
	String user_image	= "http://ltiapps.net/test/images/lti.gif";
	String access_token = "";
	String provider_name = "";
	String launch_url = "";
	String consumer_key = "";
	String resource_id = null;
	public String getResource_id() {
		return resource_id;
	}
	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}
	public String getConsumer_key() {
		return consumer_key; 
	}
	public void setConsumer_key(String consumer_key) {
		this.consumer_key = consumer_key;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	String secret = "";
	public String getLaunch_url() {
		return launch_url;
	}
	public void setLaunch_url(String launch_url) {
		this.launch_url = launch_url;
	}
	public String getProvider_name() {
		return provider_name;
	}
	public void setProvider_name(String provider_name) {
		this.provider_name = provider_name;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getContext_id() {
		return context_id;
	}
	public void setContext_id(String context_id) {
		this.context_id = context_id;
	}
	public String getContext_label() {
		return context_label;
	}
	public void setContext_label(String context_label) {
		this.context_label = context_label;
	}
	public String getContext_title() {
		return context_title;
	}
	public void setContext_title(String context_title) {
		this.context_title = context_title;
	}
	public String getContext_type() {
		return context_type;
	}
	public void setContext_type(String context_type) {
		this.context_type = context_type;
	}
	public String getCustom_context_memberships_url() {
		return custom_context_memberships_url;
	}
	public void setCustom_context_memberships_url(String custom_context_memberships_url) {
		this.custom_context_memberships_url = custom_context_memberships_url;
	}
	public String getCustom_context_setting_ur1() {
		return custom_context_setting_ur1;
	}
	public void setCustom_context_setting_ur1(String custom_context_setting_ur1) {
		this.custom_context_setting_ur1 = custom_context_setting_ur1;
	}
	public String getCustom_lineitem_url() {
		return custom_lineitem_url;
	}
	public void setCustom_lineitem_url(String custom_lineitem_url) {
		this.custom_lineitem_url = custom_lineitem_url;
	}
	public String getCustom_lineitems_url() {
		return custom_lineitems_url;
	}
	public void setCustom_lineitems_url(String custom_lineitems_url) {
		this.custom_lineitems_url = custom_lineitems_url;
	}
	public String getCustom_link_memberships_url() {
		return custom_link_memberships_url;
	}
	public void setCustom_link_memberships_url(String custom_link_memberships_url) {
		this.custom_link_memberships_url = custom_link_memberships_url;
	}
	public String getCustom_link_setting_url() {
		return custom_link_setting_url;
	}
	public void setCustom_link_setting_url(String custom_link_setting_url) {
		this.custom_link_setting_url = custom_link_setting_url;
	}
	public String getCustom_result_url() {
		return custom_result_url;
	}
	public void setCustom_result_url(String custom_result_url) {
		this.custom_result_url = custom_result_url;
	}
	public String getCustom_results_url() {
		return custom_results_url;
	}
	public void setCustom_results_url(String custom_results_url) {
		this.custom_results_url = custom_results_url;
	}
	public String getCustom_system_setting_url() {
		return custom_system_setting_url;
	}
	public void setCustom_system_setting_url(String custom_system_setting_url) {
		this.custom_system_setting_url = custom_system_setting_url;
	}
	public String getCustom_tc_profile_url() {
		return custom_tc_profile_url;
	}
	public void setCustom_tc_profile_url(String custom_tc_profile_url) {
		this.custom_tc_profile_url = custom_tc_profile_url;
	}
	public String getExt_ims_lis_basic_outcome_url() {
		return ext_ims_lis_basic_outcome_url;
	}
	public void setExt_ims_lis_basic_outcome_url(String ext_ims_lis_basic_outcome_url) {
		this.ext_ims_lis_basic_outcome_url = ext_ims_lis_basic_outcome_url;
	}
	public String getExt_ims_lis_memberships_id() {
		return ext_ims_lis_memberships_id;
	}
	public void setExt_ims_lis_memberships_id(String ext_ims_lis_memberships_id) {
		this.ext_ims_lis_memberships_id = ext_ims_lis_memberships_id;
	}
	public String getExt_ims_lis_memberships_url() {
		return ext_ims_lis_memberships_url;
	}
	public void setExt_ims_lis_memberships_url(String ext_ims_lis_memberships_url) {
		this.ext_ims_lis_memberships_url = ext_ims_lis_memberships_url;
	}
	public String getExt_ims_lis_resultvalue_sourcedids() {
		return ext_ims_lis_resultvalue_sourcedids;
	}
	public void setExt_ims_lis_resultvalue_sourcedids(String ext_ims_lis_resultvalue_sourcedids) {
		this.ext_ims_lis_resultvalue_sourcedids = ext_ims_lis_resultvalue_sourcedids;
	}
	public String getExt_ims_lti_tool_setting_id() {
		return ext_ims_lti_tool_setting_id;
	}
	public void setExt_ims_lti_tool_setting_id(String ext_ims_lti_tool_setting_id) {
		this.ext_ims_lti_tool_setting_id = ext_ims_lti_tool_setting_id;
	}
	public String getExt_ims_lti_tool_setting_url() {
		return ext_ims_lti_tool_setting_url;
	}
	public void setExt_ims_lti_tool_setting_url(String ext_ims_lti_tool_setting_url) {
		this.ext_ims_lti_tool_setting_url = ext_ims_lti_tool_setting_url;
	}
	public String getLaunch_presentation_css_url() {
		return launch_presentation_css_url;
	}
	public void setLaunch_presentation_css_url(String launch_presentation_css_url) {
		this.launch_presentation_css_url = launch_presentation_css_url;
	}
	public String getLaunch_presentation_document_target() {
		return launch_presentation_document_target;
	}
	public void setLaunch_presentation_document_target(String launch_presentation_document_target) {
		this.launch_presentation_document_target = launch_presentation_document_target;
	}
	public String getLaunch_presentation_locale() {
		return launch_presentation_locale;
	}
	public void setLaunch_presentation_locale(String launch_presentation_locale) {
		this.launch_presentation_locale = launch_presentation_locale;
	}
	public String getLaunch_presentation_return_url() {
		return launch_presentation_return_url;
	}
	public void setLaunch_presentation_return_url(String launch_presentation_return_url) {
		this.launch_presentation_return_url = launch_presentation_return_url;
	}
	public String getLis_course_offering_sourcedid() {
		return lis_course_offering_sourcedid;
	}
	public void setLis_course_offering_sourcedid(String lis_course_offering_sourcedid) {
		this.lis_course_offering_sourcedid = lis_course_offering_sourcedid;
	}
	public String getLis_course_section_sourcedid() {
		return lis_course_section_sourcedid;
	}
	public void setLis_course_section_sourcedid(String lis_course_section_sourcedid) {
		this.lis_course_section_sourcedid = lis_course_section_sourcedid;
	}
	public String getLis_outcome_service_url() {
		return lis_outcome_service_url;
	}
	public void setLis_outcome_service_url(String lis_outcome_service_url) {
		this.lis_outcome_service_url = lis_outcome_service_url;
	}
	public String getLis_person_contact_email_primary() {
		return lis_person_contact_email_primary;
	}
	public void setLis_person_contact_email_primary(String lis_person_contact_email_primary) {
		this.lis_person_contact_email_primary = lis_person_contact_email_primary;
	}
	public String getLis_person_name_family() {
		return lis_person_name_family;
	}
	public void setLis_person_name_family(String lis_person_name_family) {
		this.lis_person_name_family = lis_person_name_family;
	}
	public String getLis_person_name_full() {
		return lis_person_name_full;
	}
	public void setLis_person_name_full(String lis_person_name_full) {
		this.lis_person_name_full = lis_person_name_full;
	}
	public String getLis_person_name_given() {
		return lis_person_name_given;
	}
	public void setLis_person_name_given(String lis_person_name_given) {
		this.lis_person_name_given = lis_person_name_given;
	}
	public String getLis_person_sourced_id() {
		return lis_person_sourced_id;
	}
	public void setLis_person_sourced_id(String lis_person_sourced_id) {
		this.lis_person_sourced_id = lis_person_sourced_id;
	}
	public String getLis_result_sourcedid() {
		return lis_result_sourcedid;
	}
	public void setLis_result_sourcedid(String lis_result_sourcedid) {
		this.lis_result_sourcedid = lis_result_sourcedid;
	}
	public String getLti_message_type() {
		return lti_message_type;
	}
	public void setLti_message_type(String lti_message_type) {
		this.lti_message_type = lti_message_type;
	}
	public String getLti_version() {
		return lti_version;
	}
	public void setLti_version(String lti_version) {
		this.lti_version = lti_version;
	}
	public String getOauth_callback() {
		return oauth_callback;
	}
	public void setOauth_callback(String oauth_callback) {
		this.oauth_callback = oauth_callback;
	}
	public String getOauth_consumer_key() {
		return oauth_consumer_key;
	}
	public void setOauth_consumer_key(String oauth_consumer_key) {
		this.oauth_consumer_key = oauth_consumer_key;
	}
	public String getOauth_nonce() {
		return oauth_nonce;
	}
	public void setOauth_nonce(String oauth_nonce) {
		this.oauth_nonce = oauth_nonce;
	}
	public String getOauth_signature() {
		return oauth_signature;
	}
	public void setOauth_signature(String oauth_signature) {
		this.oauth_signature = oauth_signature;
	}
	public String getOauth_signature_method() {
		return oauth_signature_method;
	}
	public void setOauth_signature_method(String oauth_signature_method) {
		this.oauth_signature_method = oauth_signature_method;
	}
	public String getOauth_timestamp() {
		return oauth_timestamp;
	}
	public void setOauth_timestamp(String oauth_timestamp) {
		this.oauth_timestamp = oauth_timestamp;
	}
	public String getOauth_version() {
		return oauth_version;
	}
	public void setOauth_version(String oauth_version) {
		this.oauth_version = oauth_version;
	}
	public String getResource_link_description() {
		return resource_link_description;
	}
	public void setResource_link_description(String resource_link_description) {
		this.resource_link_description = resource_link_description;
	}
	public String getResource_link_id() {
		return resource_link_id;
	}
	public void setResource_link_id(String resource_link_id) {
		this.resource_link_id = resource_link_id;
	}
	public String getResource_link_title() {
		return resource_link_title;
	}
	public void setResource_link_title(String resource_link_title) {
		this.resource_link_title = resource_link_title;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getTool_consumer_info_product_family_code() {
		return tool_consumer_info_product_family_code;
	}
	public void setTool_consumer_info_product_family_code(String tool_consumer_info_product_family_code) {
		this.tool_consumer_info_product_family_code = tool_consumer_info_product_family_code;
	}
	public String getTool_consumer_info_version() {
		return tool_consumer_info_version;
	}
	public void setTool_consumer_info_version(String tool_consumer_info_version) {
		this.tool_consumer_info_version = tool_consumer_info_version;
	}
	public String getTool_consumer_instance_contact_email() {
		return tool_consumer_instance_contact_email;
	}
	public void setTool_consumer_instance_contact_email(String tool_consumer_instance_contact_email) {
		this.tool_consumer_instance_contact_email = tool_consumer_instance_contact_email;
	}
	public String getTool_consumer_instance_description() {
		return tool_consumer_instance_description;
	}
	public void setTool_consumer_instance_description(String tool_consumer_instance_description) {
		this.tool_consumer_instance_description = tool_consumer_instance_description;
	}
	public String getTool_consumer_instance_guid() {
		return tool_consumer_instance_guid;
	}
	public void setTool_consumer_instance_guid(String tool_consumer_instance_guid) {
		this.tool_consumer_instance_guid = tool_consumer_instance_guid;
	}
	public String getTool_consumer_instance_name() {
		return tool_consumer_instance_name;
	}
	public void setTool_consumer_instance_name(String tool_consumer_instance_name) {
		this.tool_consumer_instance_name = tool_consumer_instance_name;
	}
	public String getTool_consumer_instance_url() {
		return tool_consumer_instance_url;
	}
	public void setTool_consumer_instance_url(String tool_consumer_instance_url) {
		this.tool_consumer_instance_url = tool_consumer_instance_url;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_image() {
		return user_image;
	}
	public void setUser_image(String user_image) {
		this.user_image = user_image;
	}
	
	private String endDate;
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
