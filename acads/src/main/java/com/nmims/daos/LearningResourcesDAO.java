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
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ModuleContentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;

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
	public long saveModuleContent(final ModuleContentAcadsBean moduleContent) {
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

		return primaryKey;
	} catch (DataAccessException e) {
		  
		return 0;
	}
}
	
	@Transactional(readOnly = false)
public long saveModuleDocument(final ModuleContentAcadsBean moduleContent) {
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

            return primaryKey;
      } catch (DataAccessException e) {
              
            return 0;
      }
}

	@Transactional(readOnly = false)
public boolean updateModuleContent(ModuleContentAcadsBean moduleContent) {
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
			return true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}
		return false;
	}
	
	@Transactional(readOnly = true)
	public List<ModuleContentAcadsBean> getAllModuleContentsList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ModuleContentAcadsBean> moduleContentsList=null;
		String sql="select * from acads.module where active='Y'  Order By id ";
		try {
			 moduleContentsList = (List<ModuleContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
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
	} catch (DataAccessException e) {
		  
	}
	return row;
	}
	
	//code for batch update Start
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateModuleContent(final List<ModuleContentAcadsBean> moduleContentList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < moduleContentList.size(); i++) {
			try{
				ModuleContentAcadsBean bean = moduleContentList.get(i);
				long key= saveModuleContent(bean);

				if(key==0) {
					errorList.add(i+"");
				}
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	//code for batch update End

	
	//added on 15/2/18
	@Transactional(readOnly = true)
	public List<ModuleContentAcadsBean> getContentList(ArrayList<String> allsubjects) {
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


		List<ModuleContentAcadsBean> contentList = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ModuleContentAcadsBean .class));
		return contentList;
	}
	
	@Transactional(readOnly = true)
	public ModuleContentAcadsBean getModuleContentById(int id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ModuleContentAcadsBean moduleContent=null;
		String sql="select * from acads.module where id=? and active='Y'";
		try {
			moduleContent = (ModuleContentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return moduleContent;
	}
	
	@Transactional(readOnly = true)
	public ModuleContentAcadsBean getModuleVideDataByTopicId(Integer moduleId , Integer topicId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ModuleContentAcadsBean moduleVideo = null;
		List<ModuleContentAcadsBean> moduleVideoList = null;
		String sql="select * from acads.module_videos where moduleId=? and videoSubtopicId=? and active='Y'  Order By id ";
		try {
			moduleVideo = (ModuleContentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] {moduleId,topicId}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));
			//moduleVideo= moduleVideoList != null ? moduleVideoList.get(0) : null;
		} catch (DataAccessException e) {
			  
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
	
	//CRUD for moduble documents start
	@Transactional(readOnly = true)
	public List<ModuleContentAcadsBean> getModuleDocumentDataById(Integer moduleId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ModuleContentAcadsBean> moduleDocumnentList = null;

		String sql1 = "SELECT  md.*  " + 
				"				FROM acads.module_documents md, acads.module m  " + 
				"				where md.moduleId=? and m.id=md.moduleId ";

		
		try {
			moduleDocumnentList = (List<ModuleContentAcadsBean>) jdbcTemplate.query(sql1, new Object[] {moduleId}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));

			return moduleDocumnentList;
			} catch (DataAccessException e) {
			  
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

		
		try {
			modulePercentage = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapId,moduleId,moduleId},Integer.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}

		return modulePercentage;
	}
	
	@Transactional(readOnly = true)
	public int getModuleProgressCount(String sapId,Integer moduleId){
		int moduleCount=0;
		String sql = "select count(*) from acads.module_progress mp where mp.sapId=? and mp.moduleId=? " ; 

		
		try {
			moduleCount = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapId,moduleId},Integer.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block

		}

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
			  
//			System.out.print(e);
		}
		return noOfModuleDocuments;
	}
	
	@Transactional(readOnly = true)
	public int getModuleVideosCount(Integer moduleId) {
		int noOfModuleVideos=0;
		String sql="select count(*) from acads.module_videos mv , acads.module m where mv.moduleId=m.id  and md.moduleId=?";
		try {
			noOfModuleVideos=(int)jdbcTemplate.queryForObject(sql, new Object[] {moduleId},Integer.class);
		}
		catch(Exception e) {

		}
		return noOfModuleVideos;
	}
	
	
	@Transactional(readOnly = true)
	public int getModuleVideoPercentage(String sapId,Integer moduleId){
		int videoPercentage=0;
		String sql ="select mp.percentComplete from acads.module_videos mv   " + 
				"				left join acads.module_progress mp   " + 
				"				on mv.videoSubtopicId=mp.contentId where mp.sapId=? and mp.moduleId=?	  " ; 
		
		try {
			videoPercentage = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapId,moduleId},Integer.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
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

		try {
			percentage = (double) jdbcTemplate.queryForObject(sql, new Object[]{moduleId,sapId},Double.class);
		} catch (Exception e) {
			  
		}

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

		return primaryKey;
	} catch (DataAccessException e) {
		  
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
		} catch (DataAccessException e) {
			  
		}
		return row;
        }
        
	@Transactional(readOnly = false)
        public boolean updateModuleDocuments(ModuleContentAcadsBean moduleContentBean) {
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
                return true;
            } catch (DataAccessException e) {
                // TODO Auto-generated catch block
                  
            }
            return false;
        }

	@Transactional(readOnly = true)
        public List<ModuleContentAcadsBean> getAllModuleDocumentsContentsList(){
            jdbcTemplate = new JdbcTemplate(dataSource);
            List<ModuleContentAcadsBean> moduleContentsList=null;
            String sql="select * from acads.module_documents where active='Y'  Order By id ";
            try {
                 moduleContentsList = (List<ModuleContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));
            } catch (DataAccessException e) {
                  
            }
            return moduleContentsList;
        }
        
	@Transactional(readOnly = true)
        public ModuleContentAcadsBean getModuleDocumentById(Integer id){
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		ModuleContentAcadsBean moduleContent=null;
    		String sql="select * from acads.module_documents where id=? and active='Y'";
    		try {
    			moduleContent = (ModuleContentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));
    		} catch (DataAccessException e) {
    			  
    		}
    		return moduleContent;
    	}
        
        
        
 
	//CRUD for moduble documents end
	

	//CRUD for moduble videos start
	@Transactional(readOnly = true)
	public List<ModuleContentAcadsBean> getModuleVideDataById(Integer moduleId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ModuleContentAcadsBean> moduleVideoList = null;
		String sql="select * from acads.module_videos where moduleId=? and active='Y'  Order By id ";
		try {
			moduleVideoList = (List<ModuleContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {moduleId}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return moduleVideoList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoSubTopicsListByModuleId(Integer moduleId ){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList=null;
		String sql="select vcs.* "
				+ " from acads.video_content_subtopics vcs, acads.module_videos mv "
				+ " where mv.moduleId=? and mv.videoSubtopicId = vcs.id Order By vcs.id ";
		try {
			 VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {moduleId}, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoSubTopicsListBySubject(String subject ){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList=null;
		String sql="select vcs.* "
				+ " from acads.video_content_subtopics vcs"
				+ " where subject=? Order By vcs.id ";
		try {
			 VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {subject}, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}
	
	@Transactional(readOnly = false)
	public long mapModuleVideo(final ModuleContentAcadsBean moduleContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    //id, moduleId, vidoeSubtopicId, active, createdBy, lastModifiedBy, createdDate, lastModifiedDate
				@Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement("INSERT INTO acads.module_videos"
			        		+ " (moduleId, videoSubtopicId, active, createdBy, lastModifiedBy, createdDate, lastModifiedDate) "
			        		+ " VALUES(?,?,?,?,?,sysdate(),sysdate()) ", Statement.RETURN_GENERATED_KEYS);
			        statement.setInt(1, moduleContent.getId());
			        statement.setInt(2, moduleContent.getVideoSubtopicId());
			        statement.setString(3, "Y"); 
			        statement.setString(4, moduleContent.getCreatedBy()); 
			        statement.setString(5, moduleContent.getLastModifiedBy());  
			        return statement;
			    }
			}, holder);

			long primaryKey = holder.getKey().longValue();

			return primaryKey;
		} catch (DataAccessException e) {
			  
			return 0;
		}
	}
	
	@Transactional(readOnly = false)
	public int deleteModuleVideoMap(Integer moduleId, Integer videoSubtopicId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from acads.module_videos where moduleId=? and videoSubtopicId=?";
		try {
			 row = jdbcTemplate.update(sql, new Object[] {moduleId,videoSubtopicId});
		} catch (DataAccessException e) {
			  
		}
		return row;
		}
	//CRUD for moduble videos end
	
	// update pageViewedNo. Start
	@Transactional(readOnly = false)
	public boolean updatePageViewedNo(String sapId, String subject, Integer moduleId, Integer documentId, Integer pageNo, int percentComplete) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select * from acads.module_progress where moduleId="+moduleId+" and type='document' and contentId="+documentId;
		
		String updateQuery="update acads.module_progress set pageViewed="+pageNo+", percentComplete="+percentComplete+" where moduleId="+moduleId+" and type='document' and contentId="+documentId;
		
		String insertQuery = "INSERT INTO acads.module_progress(sapId, subject, moduleId, type, contentId, percentComplete, pageViewed, year, month) VALUES "
				+ "(?,?,?,'document',?,?,?,?,?)";
		

		try {
			ModuleContentAcadsBean pageViewedDetails=null;
			int count=0;
			try {
				pageViewedDetails = (ModuleContentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] {}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));
				 count= pageViewedDetails!=null ? 1 : 0 ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
				count=0;
			}
    		
			if(count>0) {
				if(pageNo > pageViewedDetails.getPageViewed()) {
				jdbcTemplate.update(updateQuery, new Object[] {}); 
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
			
			}
			return true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
			return false;
		}
		
	}
	// update pageViewedNo. End

	// update videoViewed Start
	@Transactional(readOnly = false)
	public boolean updateVideoViewed(String sapId, String subject, Integer moduleId, Integer documentId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select * from acads.module_progress where moduleId=? and type='video' and contentId=?";
		
		String insertQuery = "INSERT INTO acads.module_progress(sapId, subject, moduleId, type, contentId, percentComplete, pageViewed, year, month) VALUES "
				+ "(?,?,?,'video',?,100,0,?,?)";
		try {
			ModuleContentAcadsBean pageViewedDetails=null;
			int count=0;
			try {
				pageViewedDetails = (ModuleContentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] {moduleId,documentId}, new BeanPropertyRowMapper(ModuleContentAcadsBean.class));
				 count= pageViewedDetails!=null ? 1 : 0 ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
				count=0;
			}
    		
			if(count>0) {
					
				return true;
				}
			else {
			jdbcTemplate.update(insertQuery, new Object[] { 
					sapId,
					subject,
					moduleId,
					documentId, 
					getLiveAcadConentYear(),
					getLiveAcadConentMonth() 
			});
			
			}
			return true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
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
		} catch (DataAccessException e) {
			  
		}
		return noOfPages;
		}
	
	
}
