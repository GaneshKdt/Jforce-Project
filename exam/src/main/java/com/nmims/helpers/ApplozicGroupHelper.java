package com.nmims.helpers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ApplozicAddMemberToGroupBean;
import com.nmims.beans.ApplozicCreateGroupBean;
import com.nmims.beans.ApplozicRegisterMemberBean;
//
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//
import java.util.ArrayList;
import java.util.List;


@Service("applozicGroupHelper")
public class ApplozicGroupHelper {
    @Value("${APPLOZIC_APPLICATION_KEY}")
    private String APPLOZIC_APPLICATION_KEY;

    @Value("${APPLOZIC_AUTHORIZATION}")
    private String APPLOZIC_AUTHORIZATION;

    @Value("${APPLOZIC_OF_USER_ID}")
    private String APPLOZIC_OF_USER_ID;

    @Value("${APPLOZIC_CREATE_GROUP_URL}")
    private String APPLOZIC_CREATE_GROUP_URL;

    @Value("${APPLOZIC_REGISTER_USER_URL}")
    private String APPLOZIC_REGISTER_USER_URL;

    @Value("${APPLOZIC_ADD_MEMBER_TO_GROUP_URL}")
    private String APPLOZIC_ADD_MEMBER_URL;

    @Value("${APPLOZIC_USER_DETAIL_URL}")
    private String APPLOZIC_USER_DETAIL_URL;
    
    @Value("${APPLOZIC_USER_EXISTS_IN_GROUP_URL}")
    private String APPLOZIC_USER_EXISTS_IN_GROUP_URL;
    

    private static final Logger logger = LoggerFactory.getLogger(ApplozicGroupHelper.class);

    public JsonObject createApplozicGroup(ApplozicCreateGroupBean applozicRegisterMemberBean) {
        String response = new String();
        JsonObject responseJsonObject = new JsonObject();
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", APPLOZIC_AUTHORIZATION);
            headers.set("Application-Key", APPLOZIC_APPLICATION_KEY);
            headers.set("Of-User-Id", applozicRegisterMemberBean.getAdmin());
            logger.info("Applozic API");
            logger.info("headers" + headers.toString());
            Gson gson = new Gson();

          String parametersForApi = gson.toJson(applozicRegisterMemberBean);
          
          logger.info("parametersForApi" + parametersForApi);
            HttpEntity<String> requestForCreatingGroup = new HttpEntity<>(parametersForApi, headers);
            //Should convert to object
            response = restTemplate.postForObject(APPLOZIC_CREATE_GROUP_URL, requestForCreatingGroup, String.class);
            logger.info(response);            
            responseJsonObject = new JsonParser().parse(response).getAsJsonObject();

            return responseJsonObject;

        } catch (Exception e) {
        	logger.info("createApplozicGroup Helper", e);
        	
            throw e;
            
        } 
        
        //finally {
            //Important: Close the connect
//            try {
//         //       client.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                
//            }
        //}
           }
    
    public JsonObject addMemberToApplozicGroup(ApplozicAddMemberToGroupBean parametersFromApi, String admin) throws Exception {
//   //     CloseableHttpClient client = HttpClientBuilder.create().build();
//
        String response = new String();
        JsonObject responseJsonObject = new JsonObject();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
//
//
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headersForRegisterMember = new HttpHeaders();
            headersForRegisterMember.set("Content-Type", "application/json");

            HttpHeaders headersForAddMemberInGroupAndUserInfo = new HttpHeaders();
            headersForAddMemberInGroupAndUserInfo.set("Content-Type", "application/json");
            headersForAddMemberInGroupAndUserInfo.set("Authorization", APPLOZIC_AUTHORIZATION);
            headersForAddMemberInGroupAndUserInfo.set("Application-Key", APPLOZIC_APPLICATION_KEY);
            headersForAddMemberInGroupAndUserInfo.set("Of-User-Id", admin);
            Gson gsonparametersForApi = new Gson();

            String parametersForApi = gsonparametersForApi.toJson(parametersFromApi);
            jsonObject = (JSONObject) parser.parse(parametersForApi);
            logger.info("json parameter for create group : " + jsonObject.toString());
            String userId = (String) jsonObject.get("userId");
            logger.info("userId for register member : " + userId);

            jsonObject = new JSONObject();
            List<String> list=new ArrayList<String>();
            list.add(userId);
            jsonObject.put("userIdList", list);
            jsonObject.put("phoneNumberList", new ArrayList<>());
            HttpEntity<String> requestForFindingUser = new HttpEntity<>(jsonObject.toString(), headersForAddMemberInGroupAndUserInfo);
            logger.info(APPLOZIC_USER_DETAIL_URL.toString());
            logger.info(requestForFindingUser.toString());

            response = restTemplate.postForObject(APPLOZIC_USER_DETAIL_URL, requestForFindingUser, String.class);
            responseJsonObject = new JsonParser().parse(response).getAsJsonObject();
            logger.info(responseJsonObject.toString());
            if(responseJsonObject.get("status").equals("error")){
                ApplozicRegisterMemberBean applozicRegisterMemberBean = new ApplozicRegisterMemberBean();
                applozicRegisterMemberBean.setApplicationId(APPLOZIC_APPLICATION_KEY);
                applozicRegisterMemberBean.setUserId(userId);

                Gson gson = new Gson();
                String json = gson.toJson(applozicRegisterMemberBean);
                logger.info("Register User" + json);

                HttpEntity<String> requestForRegisterUser = new HttpEntity<>(json, headersForRegisterMember);
                
                logger.info("Register User" + json);
                logger.info(APPLOZIC_REGISTER_USER_URL.toString());
                logger.info(requestForRegisterUser.toString());

                response = restTemplate.postForObject(APPLOZIC_REGISTER_USER_URL, requestForRegisterUser, String.class);
                responseJsonObject = new JsonParser().parse(response).getAsJsonObject();
                logger.info("response Status for register user :" );
                logger.info(responseJsonObject.toString());

            }

            logger.info("RequestBody object for "+userId+ "user exists in "+parametersFromApi.getClientGroupId()+" applozic group or not? : "+jsonObject.toString());
            HttpEntity<String> checkIfUserExistInGroup = new HttpEntity<>(jsonObject.toString(), headersForAddMemberInGroupAndUserInfo);
            logger.info(APPLOZIC_USER_EXISTS_IN_GROUP_URL + "?clientGroupId="+parametersFromApi.getClientGroupId()+"&userId=" +userId.toString());
            logger.info(checkIfUserExistInGroup.toString());
            ResponseEntity<String> responseget = restTemplate.exchange(APPLOZIC_USER_EXISTS_IN_GROUP_URL + "?clientGroupId="+parametersFromApi.getClientGroupId()+"&userId=" +userId, HttpMethod.GET, checkIfUserExistInGroup, String.class);
         
            responseJsonObject = new JsonParser().parse(responseget.getBody()).getAsJsonObject();
            
            logger.info(responseJsonObject.toString());

           // if(responseJsonObject.get("response").toString().equals("false")){
            	
                jsonObject = new JSONObject();
          
                jsonObject.put("userId", parametersFromApi.getUserId());
                jsonObject.put("clientGroupId", parametersFromApi.getClientGroupId());
                
                
            	HttpEntity<String> requestForAddingMember = new HttpEntity<>(jsonObject.toString(), headersForAddMemberInGroupAndUserInfo);
                
                logger.info(APPLOZIC_ADD_MEMBER_URL.toString());
                logger.info(requestForAddingMember.toString());


            	response = restTemplate.postForObject(APPLOZIC_ADD_MEMBER_URL, requestForAddingMember, String.class);
            	logger.info("User Successfully Added to Applogic Group" + response.toString());
                responseJsonObject = new JsonParser().parse(response).getAsJsonObject();
                logger.info("response Status for adding member:" + responseJsonObject.toString());
                return responseJsonObject;
            	
          //}else {
            //    return responseJsonObject;

            //}


  

        } catch (Exception e) {
            
            logger.error("addMemberToApplozicGroup : ",e);
            throw e;
        } 
//        
////        finally {
////            //Important: Close the connect
//////            try {
////////                client.close();
//////            } catch (IOException e) {
//////                // TODO Auto-generated catch block
//////                
//////            }
////        }
    }
}
