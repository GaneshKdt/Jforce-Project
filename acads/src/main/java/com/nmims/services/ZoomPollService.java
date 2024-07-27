package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionPollReportBean;
import com.nmims.beans.WebinarPollsBean;
import com.nmims.beans.WebinarPollsQuestionsBean;
import com.nmims.beans.WebinarPollsResultsBean;
import com.nmims.beans.WebinarPollsResultsQuestionDetailsBean;
import com.nmims.beans.WebinarPollsResultsQuestionsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.SessionPollsDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.helpers.ZoomManager;
import com.nmims.interfaces.PollServiceInterface;

@Service("zoomPollService")
public class ZoomPollService implements PollServiceInterface
{
	@Autowired
	private ZoomManager zoomManger;
	
	@Autowired
	private SessionPollsDAO sessionPollsDAO;
	
	@Autowired
	ContentDAO contentdao;

	@Autowired
	TimeTableDAO Tdao;
	
	
	HashMap<String,String> response;

	private static final Logger logger = Logger.getLogger(ZoomPollService.class);

	
	//To view Added Poll results.
	@Override
	public WebinarPollsResultsBean getWebinarPollsResults(String webinarID,String userId)
	{
		WebinarPollsResultsBean webinarPollsResultsBean = new WebinarPollsResultsBean();
		webinarPollsResultsBean = zoomManger.getWebinarPollsResults(webinarID);
		webinarPollsResultsBean.setCreatedBy(userId);
		webinarPollsResultsBean.setLastModifiedBy(userId);
		if(webinarPollsResultsBean.getQuestions().size()>0) {
			List<WebinarPollsResultsQuestionsBean> webinarPollsResultsQuestionsBeans=new ArrayList<>();
			for(WebinarPollsResultsQuestionsBean webinarPollsResultsQuestionsBean: webinarPollsResultsBean.getQuestions()) {
				webinarPollsResultsQuestionsBean.setWebinarId(webinarPollsResultsBean.getId());
				webinarPollsResultsQuestionsBean.setCreatedBy(userId);
				webinarPollsResultsQuestionsBean.setLastModifiedBy(userId);
				List<WebinarPollsResultsQuestionDetailsBean> webinarPollsResultsQuestionsDetailsBeans=new ArrayList<>();
				for(WebinarPollsResultsQuestionDetailsBean webinarPollsResultsQuestionDetailsBean:webinarPollsResultsQuestionsBean.getQuestion_details()) {
					webinarPollsResultsQuestionDetailsBean.setCreatedBy(userId);
					webinarPollsResultsQuestionDetailsBean.setLastModifiedBy(userId);
					webinarPollsResultsQuestionDetailsBean.setPolling_id(webinarPollsResultsQuestionDetailsBean.getPolling_id());
					String name = sessionPollsDAO.getNameOfPollId(webinarPollsResultsQuestionDetailsBean.getPolling_id());//find the Title for the particular Poll Id created By Riya
					webinarPollsResultsQuestionDetailsBean.setPollName(name);
					webinarPollsResultsQuestionsDetailsBeans.add(webinarPollsResultsQuestionDetailsBean);
				}
				webinarPollsResultsQuestionsBeans.add(webinarPollsResultsQuestionsBean);
			}
			
			webinarPollsResultsBean.setQuestions(webinarPollsResultsQuestionsBeans);
			
			
		}
		sessionPollsDAO.saveSessionPollsResults(webinarPollsResultsBean);
		
		//populate the sapids
		getTheSapidsFromWebinarId(webinarPollsResultsBean,webinarID);
		return webinarPollsResultsBean;
	}
	
	//To add create Polls
	
	
	@Override
	public HashMap<String,String> createWebinarPoll(String webinarId,WebinarPollsBean webinarPollsBean)
	{
		JsonObject jsonObject = null;
		response = new HashMap<String,String>();
		response.put("success", "false");
		response.put("error", "false");
		try {
		List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList = new ArrayList<WebinarPollsQuestionsBean>();

		if (!webinarPollsBean.getTitle().equals("") && !webinarPollsBean.getTitle().isEmpty()
				&& webinarPollsBean.getTitle() != null) {
			for (WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsBean.getQuestions()) {
				List<String> answers = new ArrayList<>();
				if (!webinarPollsQuestionsBean.getName().equals("")
						&& !webinarPollsQuestionsBean.getName().isEmpty()
						&& webinarPollsQuestionsBean.getName() != null) {
					if (!webinarPollsQuestionsBean.getType().equals("")
							&& !webinarPollsQuestionsBean.getType().isEmpty()
							&& webinarPollsQuestionsBean.getType() != null) {
						int count = 0;
						for (int i = 0; i < webinarPollsQuestionsBean.getAnswers().size(); i++) {
							if (!webinarPollsQuestionsBean.getAnswers().get(i).equals("")
									&& webinarPollsQuestionsBean.getAnswers().get(i) != null
									&& !webinarPollsQuestionsBean.getAnswers().get(i).isEmpty()) {
								count++;
							}
						}
						//answer should be minimum 2
						if (count == 0 || count == 1) {
							continue;
						} else {
							for (String a : webinarPollsQuestionsBean.getAnswers()) {
								if (!a.equals("") && !a.isEmpty() && a != null) {
									answers.add(a);
								}
							}
							webinarPollsQuestionsBean.setAnswers(answers);
							webinarPollsQuestionsBean.setCreatedBy(webinarPollsBean.getCreatedBy());
							webinarPollsQuestionsBean.setLastModifiedBy(webinarPollsBean.getLastModifiedBy());
							webinarPollsQuestionsBeanList.add(webinarPollsQuestionsBean);
						}
					} else {
						continue;
					}
				} else {
					continue;
				}
			}
			
			

			webinarPollsBean.setQuestions(webinarPollsQuestionsBeanList);
			
			jsonObject = zoomManger.createWebinarPoll(webinarId, webinarPollsBean);
			
			if (!jsonObject.get("id").getAsString().equals("") && jsonObject.get("id").getAsString() != null
					&& !jsonObject.get("id").getAsString().isEmpty()) {
				webinarPollsBean.setId(jsonObject.get("id").getAsString());
				webinarPollsBean.setWebinarId(webinarId);
				List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList1 = new ArrayList<>();
				for (WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsBean.getQuestions()) {
					webinarPollsQuestionsBean.setAnswer1(null);
					webinarPollsQuestionsBean.setAnswer2(null);
					webinarPollsQuestionsBean.setAnswer3(null);
					webinarPollsQuestionsBean.setAnswer4(null);
					webinarPollsQuestionsBean.setAnswer5(null);
					webinarPollsQuestionsBean.setAnswer6(null);
					webinarPollsQuestionsBean.setAnswer7(null);
					webinarPollsQuestionsBean.setAnswer8(null);
					webinarPollsQuestionsBean.setAnswer9(null);
					webinarPollsQuestionsBean.setAnswer10(null);
					for (String answer : webinarPollsQuestionsBean.getAnswers()) {
						if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 0) {
							webinarPollsQuestionsBean.setAnswer1(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 1) {
							webinarPollsQuestionsBean.setAnswer2(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 2) {
							webinarPollsQuestionsBean.setAnswer3(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 3) {
							webinarPollsQuestionsBean.setAnswer4(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 4) {
							webinarPollsQuestionsBean.setAnswer5(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 5) {
							webinarPollsQuestionsBean.setAnswer6(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 6) {
							webinarPollsQuestionsBean.setAnswer7(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 7) {
							webinarPollsQuestionsBean.setAnswer8(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 8) {
							webinarPollsQuestionsBean.setAnswer9(answer);
						} else {
							webinarPollsQuestionsBean.setAnswer10(answer);
						}
					}
					webinarPollsQuestionsBeanList1.add(webinarPollsQuestionsBean);//listing all the questions with its respective answer
				}
				
				webinarPollsBean.setQuestions(webinarPollsQuestionsBeanList1);//combining all the questions-answers set with the its respective webinarId
				
				sessionPollsDAO.saveSessionPolls(webinarPollsBean);
				
				response.put("success", "true");
				response.put("successMessage", "Successfully Session Poll is created.");
				return response;
				
			}
			//-------------------
		 else {
				response.put("error", "true");
				response.put("errorMessage", "Failed to create session poll!");
			
				return response;

			}

		} else {
			response.put("error", "true");
			response.put("errorMessage", "Failed to create session poll!");
			return response;

		}

		
		
		
		
		}catch(Exception e){
			  
			
			zoomManger.deleteWebinarPoll(webinarId, webinarPollsBean.getId());
			response.put("error", "true");
			response.put("errorMessage", "Failed to create session poll!");
			return response;	
		}
	}
	
	//To view added sessions Polls
	
	@Override
	public List<WebinarPollsBean> getSessionPolls(String webinarId)
	{
		List<WebinarPollsBean> webinarPollsBeans=sessionPollsDAO.getSessionPolls(webinarId);//get all the polls which has taken place by this webinarId
		
		//List<WebinarPollsBean> webinarPollsBeans=sessionPollsDAO.getSessionPolls("525960232");				
		List<WebinarPollsBean> webinarPollsBeanList=new ArrayList<>();
		for(WebinarPollsBean webinarPollsBean:webinarPollsBeans) {
			List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeans = sessionPollsDAO.getSessionPollsQuestions(webinarPollsBean.getId());//get questions and answers from particular PollId
			List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList = new ArrayList<>();
			for(WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsQuestionsBeans) {
				List<String> answers=new ArrayList<>();
				if(webinarPollsQuestionsBean.getAnswer1()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer1());
				} if(webinarPollsQuestionsBean.getAnswer2()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer2());
				} if(webinarPollsQuestionsBean.getAnswer3()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer3());
				} if(webinarPollsQuestionsBean.getAnswer4()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer4());
				} if(webinarPollsQuestionsBean.getAnswer5()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer5());
				} if(webinarPollsQuestionsBean.getAnswer6()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer6());
				} if(webinarPollsQuestionsBean.getAnswer7()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer7());
				} if(webinarPollsQuestionsBean.getAnswer8()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer8());
				} if(webinarPollsQuestionsBean.getAnswer9()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer9());
				} if(webinarPollsQuestionsBean.getAnswer10()!=null) {
					answers.add(webinarPollsQuestionsBean.getAnswer10());
				}
				
				webinarPollsQuestionsBean.setAnswers(answers);
				webinarPollsQuestionsBeanList.add(webinarPollsQuestionsBean);//list contain all the question-answers set. 
				
			}
			
			webinarPollsBean.setQuestions(webinarPollsQuestionsBeanList); //combine the questions-answers set with the main poll bean which contain its title and webinar id.
			
			webinarPollsBeanList.add(webinarPollsBean);
		}			
		return webinarPollsBeanList;
	}
	
	//To update your poll
	
	@Override
	public HashMap<String,String> updateWebinarPoll(String webinarId,WebinarPollsBean webinarPollsBean) throws Exception
	{
		HttpStatus httpStatus = null;
		response = new HashMap<String,String>();
		response.put("success", "false");
		response.put("error", "false");
		
		
		List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList = new ArrayList<WebinarPollsQuestionsBean>();

		if (!webinarPollsBean.getTitle().equals("") && !webinarPollsBean.getTitle().isEmpty()//Title Should not be empty
				&& webinarPollsBean.getTitle() != null) {
			for (WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsBean.getQuestions()) {
				List<String> answers = new ArrayList<>();
				if (!webinarPollsQuestionsBean.getName().equals("")
						&& !webinarPollsQuestionsBean.getName().isEmpty()
						&& webinarPollsQuestionsBean.getName() != null) {
					if (!webinarPollsQuestionsBean.getType().equals("")
							&& !webinarPollsQuestionsBean.getType().isEmpty()
							&& webinarPollsQuestionsBean.getType() != null) {
						int count = 0;
						for (int i = 0; i < webinarPollsQuestionsBean.getAnswers().size(); i++) {
							if (!webinarPollsQuestionsBean.getAnswers().get(i).equals("")
									&& webinarPollsQuestionsBean.getAnswers().get(i) != null
									&& !webinarPollsQuestionsBean.getAnswers().get(i).isEmpty()) {
								count++;
							}
						}
						if (count == 0 || count == 1) { //If answer count is not >=2 dont save 
							continue;
						} else {
							for (String a : webinarPollsQuestionsBean.getAnswers()) {
								if (!a.equals("") && !a.isEmpty() && a != null) {
									answers.add(a);
								}
							}
							webinarPollsQuestionsBean.setAnswers(answers);
							webinarPollsQuestionsBean.setCreatedBy(webinarPollsBean.getCreatedBy());
							webinarPollsQuestionsBean.setLastModifiedBy(webinarPollsBean.getLastModifiedBy());
							webinarPollsQuestionsBeanList.add(webinarPollsQuestionsBean);
						}
					} else {
						continue;
					}
				} else {
					continue;
				}
			}

			webinarPollsBean.setQuestions(webinarPollsQuestionsBeanList);
			
			httpStatus = zoomManger.updateWebinarPoll(webinarId, webinarPollsBean);
			
			if(httpStatus.equals(HttpStatus.NO_CONTENT)){
				webinarPollsBean.setWebinarId(webinarId);
				List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList1 = new ArrayList<>();
				//loop to arrange the question-answers set
				for (WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsBean.getQuestions()) {
					webinarPollsQuestionsBean.setAnswer1(null);
					webinarPollsQuestionsBean.setAnswer2(null);
					webinarPollsQuestionsBean.setAnswer3(null);
					webinarPollsQuestionsBean.setAnswer4(null);
					webinarPollsQuestionsBean.setAnswer5(null);
					webinarPollsQuestionsBean.setAnswer6(null);
					webinarPollsQuestionsBean.setAnswer7(null);
					webinarPollsQuestionsBean.setAnswer8(null);
					webinarPollsQuestionsBean.setAnswer9(null);
					webinarPollsQuestionsBean.setAnswer10(null);

					for (String answer : webinarPollsQuestionsBean.getAnswers()) {
						if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 0) {
							webinarPollsQuestionsBean.setAnswer1(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 1) {
							webinarPollsQuestionsBean.setAnswer2(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 2) {
							webinarPollsQuestionsBean.setAnswer3(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 3) {
							webinarPollsQuestionsBean.setAnswer4(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 4) {
							webinarPollsQuestionsBean.setAnswer5(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 5) {
							webinarPollsQuestionsBean.setAnswer6(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 6) {
							webinarPollsQuestionsBean.setAnswer7(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 7) {
							webinarPollsQuestionsBean.setAnswer8(answer);
						} else if (webinarPollsQuestionsBean.getAnswers().indexOf(answer) == 8) {
							webinarPollsQuestionsBean.setAnswer9(answer);
						} else {
							webinarPollsQuestionsBean.setAnswer10(answer);
						}
					}
					webinarPollsQuestionsBeanList1.add(webinarPollsQuestionsBean);//list contain all the question-answers set. 
				}
				
				webinarPollsBean.setQuestions(webinarPollsQuestionsBeanList1); //combine the questions-answers set with the main poll bean which contain its title and webinar id.

				//"------Updated Polls Stored in Database-----"
				sessionPollsDAO.updateSessionPolls(webinarPollsBean);
			
									
				response.put("success", "true");
				response.put("successMessage", "Successfully Session Poll is updated.");
				return response;
			} else {
				response.put("error", "true");
				response.put("errorMessage", "Failed to update session poll!");
				return response;
			}

		} else {
			response.put("error", "true");
			response.put("errorMessage", "Failed to update session poll!");
		    return response;
		}
	
		
		
	}
	
	
	//To delete Poll
	
	@Override
	public HashMap<String,String> deleteWebinarPoll(String webinarId, String pollId){
		response = new HashMap<String,String>();
		response.put("success", "false");
		response.put("error", "false");
		 
			//-------ZoomManager Delete Webinar Poll Api Called---------
		 HttpStatus httpStatus = zoomManger.deleteWebinarPoll(webinarId, pollId);
			
			if(!(httpStatus.equals(HttpStatus.BAD_REQUEST)))
			{
				//----------Delete session Polls in database----------"
				sessionPollsDAO.deleteSessionPolls(webinarId, pollId);
				response.put("success", "true");
				response.put("successMessage", "Successfully Session Poll is updated.");
			}
		
		return response;
	}

	@Override
	public SessionDayTimeAcadsBean findScheduledSessionById(String sessionId) {
		return Tdao.findScheduledSessionById(sessionId);
	}

	public ArrayList<String> getSubjectCodeLists(String userId,String month,String year) {
		return sessionPollsDAO.getSubjectsCodeInCurrent(userId,month,year);
	}
	
	//For Poll Report
	public PageAcads<SessionPollReportBean> getSessionPollReport(int pageNo, int pageSize, SessionPollReportBean searchBean) {
		return  sessionPollsDAO.getSessionPollReport(1, Integer.MAX_VALUE, searchBean);
	}
	
	public void getTheSapidsFromWebinarId(WebinarPollsResultsBean webinarPollsResultsBean,String webinarID)
	{
		//Get the sapids with respect of WebinarId
		
		HashMap<String,String> sapids = sessionPollsDAO.getTheSapidsFromEmail(webinarID);
		if(sapids.size() > 0) {
			for(WebinarPollsResultsQuestionsBean bean : webinarPollsResultsBean.getQuestions())
			{
			
				bean.setSapid(sapids.get(bean.getEmail()));
			}
		}
		
	}
			
}//end of class
