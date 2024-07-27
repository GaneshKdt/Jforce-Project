package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.ModuleContentStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;

public class LearningResourcesDAO extends BaseDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	@Transactional(readOnly = false)
	public long saveModuleContent(final ModuleContentStudentPortalBean moduleContent) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	GeneratedKeyHolder holder = new GeneratedKeyHolder();
	try {
		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement("INSERT INTO acads.module"
		        		+ " (subject, moduleName, description, createdBy, lastModifiedBy, lastModifiedDate, createdDate, active) "
		        		+ " VALUES(?,?,?,?,?,sysdate(),sysdate(),?) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, moduleContent.getSubject());
		        statement.setString(2, moduleContent.getModuleName());
		        statement.setString(3, moduleContent.getDescription()); 
		        statement.setString(4, moduleContent.getCreatedBy()); 
		        statement.setString(5, moduleContent.getLastModifiedBy()); 
		        statement.setString(6, moduleContent.getActive());      
		        return statement;
		    }
		}, holder);

		long primaryKey = holder.getKey().longValue();

		//System.out.println("key For Added ModuleContent "+primaryKey+ " Name : "+moduleContent.getModuleName());
		return primaryKey;
	} catch (DataAccessException e) {
		//e.printStackTrace();
		return 0;
	}
}
	
	@Transactional(readOnly = false)
public long saveModuleDocument(final ModuleContentStudentPortalBean moduleContent) {
      jdbcTemplate = new JdbcTemplate(dataSource);
      GeneratedKeyHolder holder = new GeneratedKeyHolder();
      try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                //id, moduleId, documentName, folderPath, type, active, noOfPages
                  @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                              PreparedStatement statement = con.prepareStatement("INSERT INTO acads.module_documents"
                              + " (moduleId, documentName, folderPath,documentPath, previewPath, type, noOfPages, active, createdBy, lastModifiedBy, lastModifiedDate, createdDate) "
                              + " VALUES(?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate()) ", Statement.RETURN_GENERATED_KEYS);
                    statement.setInt(1, moduleContent.getModuleId());
                    statement.setString(2, moduleContent.getDocumentName());
                    statement.setString(3, moduleContent.getFolderPath()); 
                    statement.setString(4, moduleContent.getDocumentPath());
                    statement.setString(5, moduleContent.getPreviewPath());
                    statement.setString(6, moduleContent.getType()); 
                    statement.setInt(7, moduleContent.getNoOfPages());  
                    statement.setString(8, moduleContent.getActive());       
                    statement.setString(9, moduleContent.getCreatedBy());      
                    statement.setString(10, moduleContent.getLastModifiedBy());
                    return statement;
                }
            }, holder);

            long primaryKey = holder.getKey().longValue();

            //System.out.println("key For Added ModuleContent "+primaryKey+ " Name : "+moduleContent.getModuleName());
            return primaryKey;
      } catch (DataAccessException e) {
            //e.printStackTrace();
            return 0;
      }
}

@Transactional(readOnly = false)
public boolean updateModuleContent(ModuleContentStudentPortalBean moduleContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update acads.module set "
						+ " subject=?, "
						+ "moduleName=?, "
						+ "description=?, " 
						+ " lastModifiedBy = ? ,"
						+ " lastModifiedDate = sysdate(), " 
						+ " active = ? " 
							+ "where id=?";
		try {
			jdbcTemplate.update(sql,new Object[] {moduleContent.getSubject(),
												  moduleContent.getModuleName(), 
												  moduleContent.getDescription(), 
												  moduleContent.getLastModifiedBy(),  
												  moduleContent.getActive(), 
												  moduleContent.getId()
												  });
			//System.out.println("Updated ModuleContent with id : "+moduleContent.getId());
			return true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return false;
	}

	@Transactional(readOnly = true)
	public List<ModuleContentStudentPortalBean> getAllModuleContentsList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ModuleContentStudentPortalBean> moduleContentsList=null;
		String sql="select * from acads.module where active='Y'  Order By id ";
		try {
			 moduleContentsList = (List<ModuleContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return moduleContentsList;
	}
	
	
	@Transactional(readOnly = false)
	public int deleteModuleContent(int id) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	int row=0;	
	String sql="delete from acads.module where id=?";
	try {
		 row = jdbcTemplate.update(sql, new Object[] {id});
		 //System.out.println("Deleted "+row+" rows of ModuleContent id " +id);
	} catch (DataAccessException e) {
		//e.printStackTrace();
	}
	return row;
	}
	
	//code for batch update Start
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateModuleContent(final List<ModuleContentStudentPortalBean> moduleContentList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < moduleContentList.size(); i++) {
			try{
				ModuleContentStudentPortalBean bean = moduleContentList.get(i);
				long key= saveModuleContent(bean);
				////System.out.println("Upserted row "+i);
				if(key==0) {
					errorList.add(i+"");
				}
			}catch(Exception e){
				//e.printStackTrace();
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	//code for batch update End
	
	//added on 17/3/2018
	@Transactional(readOnly = true)
	public List<ModuleContentStudentPortalBean> getContentListSubjectWise(String subject){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ModuleContentStudentPortalBean> contentListsubjectWise = null;
		//System.out.println("getModuleDocumentDataById Got  moduleId "+subject);
		String sql1 = "Select distinct m.*  from acads.module m where  m.subject =?";
		//System.out.println("getModuleDocumentDataById SQL "+sql1);
		
		try {
			contentListsubjectWise = (List<ModuleContentStudentPortalBean>) jdbcTemplate.query(sql1, new Object[] {subject}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
			//System.out.println("getModuleDocumentDataById after query exceution");
			return contentListsubjectWise;
			} catch (DataAccessException e) {
			//e.printStackTrace();
		}
	
		return contentListsubjectWise;
	}
	
	//added on 15/2/18
	@Transactional(readOnly = true)
	public List<ModuleContentStudentPortalBean> getContentList(ArrayList<String> allsubjects) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = "''";
		if(allsubjects==null) {
			subjectCommaSeparated = "''";
		}else {
			for (int i = 0; i < allsubjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ allsubjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ allsubjects.get(i).replaceAll("'", "''") + "'";
				}
			}
		}
		String sql = " Select distinct m.* "
					 + "from acads.module m "
					 + " where  m.subject IN ("+subjectCommaSeparated+")";

		////System.out.println("SQL = "+sql);
		List<ModuleContentStudentPortalBean> contentList = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean .class));
		return contentList;
	}
	
	@Transactional(readOnly = true)
	public ModuleContentStudentPortalBean getModuleContentById(int id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ModuleContentStudentPortalBean moduleContent=null;
		String sql="select * from acads.module where id=? and active='Y'";
		try {
			moduleContent = (ModuleContentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return moduleContent;
	}
	
	@Transactional(readOnly = true)
	public ModuleContentStudentPortalBean getModuleVideDataByTopicId(Integer moduleId , Integer topicId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ModuleContentStudentPortalBean moduleVideo = null;
		List<ModuleContentStudentPortalBean> moduleVideoList = null;
		String sql="select * from acads.module_videos where moduleId=? and videoSubtopicId=? and active='Y'  Order By id ";
		try {
			moduleVideo = (ModuleContentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {moduleId,topicId}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
			//moduleVideo= moduleVideoList != null ? moduleVideoList.get(0) : null;
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return moduleVideo;
	}
	
	@Transactional(readOnly = true)
	public ModuleContentStudentPortalBean getModuleVideDataByTopicIdAndType(Integer moduleId , Integer topicId,String type){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ModuleContentStudentPortalBean moduleVideo = null;
		List<ModuleContentStudentPortalBean> moduleVideoList = null;
		String sql="select * from acads.module_videos where moduleId=? and videoSubtopicId=? and type=? and active='Y'  Order By id ";
		try {
			moduleVideo = (ModuleContentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {moduleId,topicId,type}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
			//moduleVideo= moduleVideoList != null ? moduleVideoList.get(0) : null;
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return moduleVideo;
	}
	
	@Transactional(readOnly = true)
	public ModuleContentStudentPortalBean getModuleVideoMapBean(String chapter , String topic, String subject){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ModuleContentStudentPortalBean moduleVideo = null;
		List<ModuleContentStudentPortalBean> moduleVideoList = null;
		String sql="select m.id as id, v.id as videoSubtopicId from acads.module m, acads.video_content_subtopics v"
				+ " where m.moduleName=? and v.fileName=? and m.subject=v.subject Order By id ";
		try {
			moduleVideoList = (List<ModuleContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {chapter,topic}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
			moduleVideo= moduleVideoList != null ? moduleVideoList.get(0) : null;
			//System.out.println("Found match for chapter : "+chapter+" topic : "+topic+" subject : "+subject);
			
		} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println("No match for chapter : "+chapter+" topic : "+topic+" subject : "+subject);
		}
		return moduleVideo;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Select distinct m.subject from exam.students s , exam.program_subject p , acads.module m where "
	+ " p.prgmStructApplicable=s.PrgmStructApplicable and p.program=s.program and p.sem=s.sem and s.sapid = ?  ";
		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return subjectsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsForLeads() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select distinct subject from acads.content_forleads order by subject;";
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		//System.out.println("_________________________Subject in getSubjectList: "+subjectList+"_________________________");
		return subjectList;

	}
	
	
	//CRUD for moduble documents start
	@Transactional(readOnly = true)
	public List<ModuleContentStudentPortalBean> getModuleDocumentDataById(Integer moduleId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ModuleContentStudentPortalBean> moduleDocumnentList = null;
		//System.out.println("getModuleDocumentDataById Got  moduleId "+moduleId);
		String sql1 = "SELECT  md.*  " + 
				"				FROM acads.module_documents md, acads.module m  " + 
				"				where md.moduleId=? and m.id=md.moduleId ";
		//System.out.println("getModuleDocumentDataById SQL "+sql1);
		
		try {
			moduleDocumnentList = (List<ModuleContentStudentPortalBean>) jdbcTemplate.query(sql1, new Object[] {moduleId}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
			//System.out.println("getModuleDocumentDataById after query exceution");
			return moduleDocumnentList;
			} catch (DataAccessException e) {
			//e.printStackTrace();
		}
	
		return moduleDocumnentList;
	}
	
	@Transactional(readOnly = true)
	public int getModuleDocumentPercentage(String sapId,Integer moduleId){
		int modulePercentage=0;
		String sql = "SELECT  ps.percentComplete  " + 
				"				FROM acads.module_documents md   " + 
				"				LEFT JOIN  " + 
				"				 acads.module_progress ps   " + 
				"				 ON md.id = ps.contentId where ps.sapId = ? AND ps.moduleId = ? and md.moduleId=? " ; 
		//System.out.println("getModuleDocumentDataById SQL "+sql);
		
		try {
			modulePercentage = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapId,moduleId,moduleId},Integer.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println();
		}
		//System.out.println("modulePercentage after sql "+modulePercentage);
		return modulePercentage;
	}
	
	@Transactional(readOnly = true)
	public int getModuleProgressCount(String sapId,Integer moduleId){
		int moduleCount=0;
		String sql = "select count(*) from acads.module_progress mp where mp.sapId=? and mp.moduleId=? " ; 
		//System.out.println("getModuleDocumentDataById SQL "+sql);
		
		try {
			moduleCount = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapId,moduleId},Integer.class);
			//System.out.println("inside module progress count");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println();
		}
		//System.out.println("modulePercentage after sql "+moduleCount);
		return moduleCount;
	}
	
	@Transactional(readOnly = true)
	public int getModuleDocumentsCount(Integer moduleId) {
		int noOfModuleDocuments=0;
		String sql="select count(*) from acads.module_documents md , acads.module m where   md.moduleId=m.id and md.moduleId=? ";
		try {
			noOfModuleDocuments=(int)jdbcTemplate.queryForObject(sql, new Object[] {moduleId},Integer.class );
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		return noOfModuleDocuments;
	}
	
	@Transactional(readOnly = true)
	public int getModuleVideosCount(Integer moduleId) {
		int noOfModuleVideos=0;
		String sql="select count(*) from acads.module_videos mv , acads.module m where mv.moduleId=m.id  and m.id=?";
		try {
			noOfModuleVideos=(int)jdbcTemplate.queryForObject(sql, new Object[] {moduleId},Integer.class);
		}
		catch(Exception e) {
			//System.out.println(e);
		}
		return noOfModuleVideos;
	}
	
	@Transactional(readOnly = true)
	public int getModuleVideoPercentage(String sapId,Integer moduleId){
		int videoPercentage=0;
		String sql ="select count(*) from acads.module_progress mp   " + 
						" where mp.sapId=? and mp.moduleId=? and mp.type like '%Video' " ; 
		////System.out.println("getModuleDocumentDataById SQL "+sql);
		
		try {
			videoPercentage = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapId,moduleId},Integer.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println();
		}
		//System.out.println("videoPercentage after sql "+videoPercentage);
		return videoPercentage;
	}
	
	@Transactional(readOnly = true)
	public double getPercentageAvg(Integer moduleId,String sapId){
		double percentage=0;
		String sql = "SELECT  avg(ps.percentComplete) " + 
				"				FROM acads.module_progress ps " + 
				"				LEFT JOIN  acads.module_documents md   " + 
				"				 ON md.id = ps.contentId " + 
				"				 LEFT JOIN acads.module_videos mv " + 
				"				 ON mv.id=md.id " + 
				"				 where ps.moduleId=?  and ps.sapId=? " ; 
		//System.out.println("getModuleDocumentDataById SQL "+sql);
		try {
			percentage = (double) jdbcTemplate.queryForObject(sql, new Object[]{moduleId,sapId},Double.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println();
		}
		//System.out.println("percentage after sql "+percentage);
		return percentage;
	}
	
	
	/*public long saveModuleDocument(final ModuleContentBean moduleContent) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	GeneratedKeyHolder holder = new GeneratedKeyHolder();
	try {
		jdbcTemplate.update(new PreparedStatementCreator() {
		    //id, moduleId, documentName, folderPath, type, active, noOfPages
			@Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement("INSERT INTO acads.module_documents"
		        		+ " (moduleId, documentName, folderPath, type, noOfPages, active, createdBy, lastModifiedBy, lastModifiedDate, createdDate) "
		        		+ " VALUES(?,?,?,?,?,?,?,?,sysdate(),sysdate()) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setInt(1, moduleContent.getModuleId());
		        statement.setString(2, moduleContent.getDocumentName());
		        statement.setString(3, moduleContent.getFolderPath()); 
		        statement.setString(4, moduleContent.getType()); 
		        statement.setInt(5, moduleContent.getNoOfPages());  
		        statement.setString(6, moduleContent.getActive());       
		        statement.setString(7, moduleContent.getCreatedBy());      
		        statement.setString(8, moduleContent.getLastModifiedBy());
		        return statement;
		    }
		}, holder);

		long primaryKey = holder.getKey().longValue();

		//System.out.println("key For Added ModuleContent "+primaryKey+ " Name : "+moduleContent.getModuleName());
		return primaryKey;
	} catch (DataAccessException e) {
		//e.printStackTrace();
		return 0;
	}
}
	*/
	@Transactional(readOnly = false)
	public int deleteModuleDocumentsContent(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from acads.module_documents where id=?";
		try {
			 row = jdbcTemplate.update(sql, new Object[] {id});
			 //System.out.println("Deleted "+row+" rows of ModuleContent id " +id);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return row;
        }
        
		@Transactional(readOnly = false)
        public boolean updateModuleDocuments(ModuleContentStudentPortalBean moduleContentBean) {
            jdbcTemplate = new JdbcTemplate(dataSource);
            String sql="update acads.module_documents set "
                            + " moduleId=?, "
                            + "documentName=?, "
                            + "active=?, " 
                            + "folderPath=?, " 
                            + "type=?, "
                            + "lastModifiedDate=sysdate(), "  
                            + "noOfPages=? "       
                            + "where id=?";
            try {
                jdbcTemplate.update(sql,new Object[] {moduleContentBean.getModuleId(),
                                                      moduleContentBean.getDocumentName(),  
                                                      moduleContentBean.getActive(),
                                                      moduleContentBean.getFolderPath(),
                                                      moduleContentBean.getType(),
                                                       moduleContentBean.getNoOfPages(),
                                                      moduleContentBean.getId()
                                    });
                //System.out.println("Updated Module Topic with id : "+moduleContentBean.getId());
                return true;
            } catch (DataAccessException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
            return false;
        }
		
		@Transactional(readOnly = true)
        public List<ModuleContentStudentPortalBean> getAllModuleDocumentsContentsList(){
            jdbcTemplate = new JdbcTemplate(dataSource);
            List<ModuleContentStudentPortalBean> moduleContentsList=null;
            String sql="select * from acads.module_documents where active='Y'  Order By id ";
            try {
                 moduleContentsList = (List<ModuleContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
            } catch (DataAccessException e) {
                //e.printStackTrace();
            }
            return moduleContentsList;
        }
        
		@Transactional(readOnly = true)
        public ModuleContentStudentPortalBean getModuleDocumentById(Integer id){
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		ModuleContentStudentPortalBean moduleContent=null;
    		String sql="select * from acads.module_documents where id=? and active='Y'";
    		try {
    			moduleContent = (ModuleContentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
    		} catch (DataAccessException e) {
    			//e.printStackTrace();
    		}
    		return moduleContent;
    	}
        
        
        
 
	//CRUD for moduble documents end
	

	//CRUD for moduble videos start
	@Transactional(readOnly = true)
	public List<ModuleContentStudentPortalBean> getModuleVideDataById(Integer moduleId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ModuleContentStudentPortalBean> moduleVideoList = null;
		String sql="select * from acads.module_videos where moduleId=? and active='Y'  Order By id ";
		try {
			moduleVideoList = (List<ModuleContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {moduleId}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return moduleVideoList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentStudentPortalBean> getVideoSubTopicsListByModuleId(Integer moduleId ){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentStudentPortalBean> videoContentsList=null;
		List<VideoContentStudentPortalBean> mainVideosList=null;
		String sql="select vcs.* "
				+ " from acads.video_content_subtopics vcs, acads.module_videos mv "
				+ " where mv.moduleId=? and mv.videoSubtopicId = vcs.id and mv.type='Topic Video' Order By vcs.id ";
//		String sqlMainVideos="select vc.*,s.meetingKey "
//				+ " from acads.video_content vc, acads.module_videos mv "
//				+ " left join acads.sessions s on s.id = vc.sessionId "
//				+ " where mv.moduleId=? and mv.videoSubtopicId = vc.id and mv.type='Main Video' Order By vc.id ";
		
		String sqlMainVideos = "select vc.*,s.meetingKey from acads.video_content vc "
				+ "INNER JOIN acads.module_videos mv ON vc.id = mv.videoSubtopicId "
				+ "INNER JOIN acads.sessions s on vc.sessionId = s.id "
				+ "where mv.moduleId=? and mv.type='Main Video' Order By vc.id";
		
		try {
			mainVideosList = (List<VideoContentStudentPortalBean>) jdbcTemplate.query(sqlMainVideos, new Object[] {moduleId}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
			for(VideoContentStudentPortalBean mainVideo : mainVideosList) {
				mainVideo.setType("Main Video");
			}
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		
		try {
			 videoContentsList = (List<VideoContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {moduleId}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
			 for(VideoContentStudentPortalBean topicVideo : videoContentsList) {
				 topicVideo.setType("Topic Video");
			}
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		////System.out.println("in getVideoSubTopicsListByModuleId \n got mainvideos"+mainVideosList.size()+"\n topicVideos "+videoContentsList.size()); 
		
		mainVideosList.addAll(videoContentsList);
		return mainVideosList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentStudentPortalBean> getVideoSubTopicsListBySubject(String subject ){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentStudentPortalBean> VideoContentsList=null;
		String sql="select vcs.* "
				+ " from acads.video_content_subtopics vcs"
				+ " where subject=? Order By vcs.id ";
		try {
			 VideoContentsList = (List<VideoContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {subject}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return VideoContentsList;
	}

	@Transactional(readOnly = true)
	public List<VideoContentStudentPortalBean> getMainVideoListBySubject(String subject ){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentStudentPortalBean> videosList=null;
//		String sql="select vc.*,s.meetingKey "
//				+ " from acads.video_content vc "
//				+ " left join acads.sessions s on s.id = vc.sessionId "
//				+ " where vc.subject=? Order By vc.id ";
		
		String sql = "select vc.*,s.meetingKey from acads.video_content vc "
				+ "INNER join acads.sessions s on s.id = vc.sessionId "
				+ "where vc.subject=? Order By vc.id";
		try {
			 videosList = (List<VideoContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {subject}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return videosList;
	}
	
	@Transactional(readOnly = false)
	public long mapModuleVideo(final ModuleContentStudentPortalBean moduleContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    //id, moduleId, vidoeSubtopicId, active, createdBy, lastModifiedBy, createdDate, lastModifiedDate
				@Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement("INSERT INTO acads.module_videos"
			        		+ " (moduleId, videoSubtopicId, type, active, createdBy, lastModifiedBy, createdDate, lastModifiedDate) "
			        		+ " VALUES(?,?,?,?,?,?,sysdate(),sysdate()) ", Statement.RETURN_GENERATED_KEYS);
			        statement.setInt(1, moduleContent.getId());
			        statement.setInt(2, moduleContent.getVideoSubtopicId());
			        statement.setString(3, moduleContent.getType());
			        statement.setString(4, "Y"); 
			        statement.setString(5, moduleContent.getCreatedBy()); 
			        statement.setString(6, moduleContent.getLastModifiedBy());  
			        return statement;
			    }
			}, holder);

			long primaryKey = holder.getKey().longValue();
			//System.out.println("key For Added mapping "+primaryKey+ " Name : "+moduleContent.getModuleName());
			return primaryKey;
		} catch (DataAccessException e) {
			//e.printStackTrace();
			return 0;
		}
	}
	
	@Transactional(readOnly = false)
	public int deleteModuleVideoMap(Integer moduleId, Integer videoSubtopicId, String type) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from acads.module_videos where moduleId=? and videoSubtopicId=? and type='"+type+"'";
		try {
			 row = jdbcTemplate.update(sql, new Object[] {moduleId,videoSubtopicId});
			 //System.out.println("Deleted "+row+" rows of ModuleContent id " +moduleId);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return row;
		}
	//CRUD for moduble videos end
	
	// update pageViewedNo. Start
	@Transactional(readOnly = false)
	public boolean updatePageViewedNo(String sapId, String subject, Integer moduleId, Integer documentId, Integer pageNo, int percentComplete) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//System.out.println("SAPID inside : " + sapId);
		
		String sql="select * from acads.module_progress where sapId = "+sapId+" and  moduleId="+moduleId+" and type='document' and contentId="+documentId;
		
		String updateQuery="update acads.module_progress set pageViewed="+pageNo+", percentComplete="+percentComplete+" where moduleId="+moduleId+" and type='document' and contentId="+documentId;
		
		String insertQuery = "INSERT INTO acads.module_progress(sapId, subject, moduleId, type, contentId, percentComplete, pageViewed, year, month) VALUES "
				+ "(?,?,?,'document',?,?,?,?,?)";
		

		try {
			ModuleContentStudentPortalBean pageViewedDetails=null;
			int count=0;
			try {
				pageViewedDetails = (ModuleContentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
				 count= pageViewedDetails!=null ? 1 : 0 ;
				 //System.out.println("count of pageViewDetails : " + count);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//////e.printStackTrace();
			 //System.out.println("pageViewedDetails bean is empty");
				count=0;
			}
    		
			if(count>0) {
				//System.out.println("status count : " + pageViewedDetails.getPageViewed());
				if(pageNo > pageViewedDetails.getPageViewed()) {
				jdbcTemplate.update(updateQuery, new Object[] {}); 
				//System.out.println("Updated row with moduleId : "+moduleId+" and DocumentId : "+documentId+" pageNo : "+pageNo);
				}
			}else {
			jdbcTemplate.update(insertQuery, new Object[] { 
					sapId,
					subject,
					moduleId,
					documentId,
					percentComplete,
					pageNo,
					getLiveAcadConentYear(),
					getLiveAcadConentMonth() 
			});
			//System.out.println("Inserted row with moduleId : "+moduleId+" and DocumentId : "+documentId+" pageNo : "+pageNo);
			
			}
			return true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
	}
	// update pageViewedNo. End

	// update videoViewed Start
	@Transactional(readOnly = false)
	public boolean updateVideoViewed(String sapId, String subject, Integer moduleId, Integer documentId,String type) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select * from acads.module_progress where moduleId=? and type=? and contentId=?";
		
		String insertQuery = "INSERT INTO acads.module_progress(sapId, subject, moduleId, type, contentId, percentComplete, pageViewed, year, month) VALUES "
				+ "(?,?,?,?,?,100,0,?,?)";
		try {
			ModuleContentStudentPortalBean pageViewedDetails=null;
			int count=0;
			try {
				pageViewedDetails = (ModuleContentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {moduleId,type,documentId}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
				 count= pageViewedDetails!=null ? 1 : 0 ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				count=0;
			}
    		
			if(count>0) {
					
				//System.out.println("Already videoViewed with moduleId : "+moduleId+" and DocumentId : "+documentId+" and type : "+type);
				return true;
				}
			else {
			jdbcTemplate.update(insertQuery, new Object[] { 
					sapId,
					subject,
					moduleId,
					type,
					documentId, 
					getLiveAcadConentYear(),
					getLiveAcadConentMonth() 
			});
			//System.out.println("Inserted videoViewed row with moduleId : "+moduleId+" and DocumentId : "+documentId);
			
			}
			return true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
	}
	// update videoViewed End

	
	//NoOfPages
	@Transactional(readOnly = true)
	public int 	getNoOfPages(Integer moduleId,Integer documentId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int noOfPages=0;	
		String sql="select distinct amd.noOfPages from acads.module_documents amd where amd.moduleId=? and amd.id=? and active='Y'";
		try {
			 noOfPages=  (int) jdbcTemplate.queryForObject(sql, new Object[] {moduleId,documentId}, new SingleColumnRowMapper(Integer.class));
			 //System.out.println("NoOfPages "+noOfPages+" rows of ModuleContent id " +documentId);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return noOfPages;
		}
	
	@Transactional(readOnly = true)
	public List<ModuleContentStudentPortalBean> getDownloadCenterLinks(String subject, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select  distinct amd.documentName , amd.folderPath , amd.documentPath from acads.module am , acads.module_documents amd, acads.module_videos amv , acads.module_progress amp " + 
				"where am.subject=? " + 
				"and amp.sapId=? and am.id=amd.moduleId ";
		List<ModuleContentStudentPortalBean> downloadCenterLinks = (List<ModuleContentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{subject,sapId}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
		return downloadCenterLinks;
	}

	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getDownloadCenterContents(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from acads.download_center where subject=? ";
		List<ContentStudentPortalBean> downloadCenterContents=null;
		try {
			downloadCenterContents = (List<ContentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return downloadCenterContents;
	}
	
	@Transactional(readOnly = true)
	public List<ModuleContentStudentPortalBean> getAllDownloadCenterContents() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from acads.download_center";
		List<ModuleContentStudentPortalBean> downloadCenterContents=null;
		try {
			downloadCenterContents = (List<ModuleContentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ModuleContentStudentPortalBean.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return downloadCenterContents;
	}
	
	@Transactional(readOnly = false)
	public long saveDownloadCenterContent(final ModuleContentStudentPortalBean moduleContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement("INSERT INTO acads.download_center"
			        		+ " (subject,  description, name , documentPath, createdBy, lastModifiedBy, lastModifiedDate, createdDate, active,year,month) "
			        		+ " VALUES(?,?,?,?,?,?,sysdate(),sysdate(),?,?,?) ", Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, moduleContent.getSubject());
			        statement.setString(2, moduleContent.getDescription());
			        statement.setString(3, moduleContent.getFileName()); 
			        statement.setString(4, moduleContent.getFilePath()); 
			        statement.setString(5, moduleContent.getCreatedBy()); 
			        statement.setString(6, moduleContent.getLastModifiedBy()); 
			        statement.setString(7, "Y");  
			        statement.setInt(8, moduleContent.getYear());  
			        statement.setString(9, moduleContent.getMonth());  
			        return statement;
			    }
			}, holder);

			long primaryKey = holder.getKey().longValue();

			//System.out.println("key For Added ModuleContent "+primaryKey);
			return primaryKey;
		} catch (DataAccessException e) {
			//e.printStackTrace();
			return 0;
		}
	}
	
	@Transactional(readOnly = false)
	public int deleteDownloadCenterContent(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from acads.download_center where id=?";
		try {
			 row = jdbcTemplate.update(sql, new Object[] {id});
			 //System.out.println("Deleted "+row+" rows of ModuleContent id " +id);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return row;
		}
	
	@Transactional(readOnly = true)
	public List<VideoContentStudentPortalBean> getAllVideoContentList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentStudentPortalBean> VideoContentsList=null;
//		String sql="select v.*,s.meetingKey from acads.video_content v "
//				+ "left join acads.sessions s on s.id = v.sessionId "
//				+ "Order By v.id desc";
		
		String sql = "select v.*,s.meetingKey from acads.video_content v "
				+ "INNER join acads.sessions s on s.id = v.sessionId "
				+ "Order By v.id desc";
		try {
			 VideoContentsList = (List<VideoContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return VideoContentsList;
	}
	
	@Transactional(readOnly = true)
	public String getMobileUrlHd(Long videoId){
		String sql ="select vc.mobileUrlHd from acads.video_content vc where vc.id=? ";
		//System.out.println("inside get mobile url hd query");
		String Ids = (String)jdbcTemplate.queryForObject(sql,new Object[]{videoId}, String.class);
		//System.out.println("Got getMobileUrlHd "+Ids);
		return Ids;
		
	}
	
	@Transactional(readOnly = true)
	public String getSubjectByProgramSemSubjectId(String programSemSubjectId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.program_sem_subject WHERE id = ? ";
		String subject = jdbcTemplate.queryForObject(sql, new Object[]{programSemSubjectId}, String.class);
		return subject;
	}

}
