package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.Post;
import com.nmims.timeline.model.PostFacultyDTO;

@Repository
public interface PostRepository extends CrudRepository<Post, String>   {
	
	@Query(nativeQuery=true,value="SELECT * FROM lti.post GROUP BY subject_config_id")
	List<Post> getAllTimeboundIds();
	
	//@Query(nativeQuery=true,value="SELECT  IFNULL(f.firstName, 'System') as firstName,IFNULL(f.lastName, ' ') as lastName, f.imgUrl as profilePicFilePath, p.*  FROM lti.post as p LEFT JOIN acads.faculty f  ON p.userId = f.facultyId  where p.scheduleFlag = ?1 and p.scheduledDate < ?2  order by p.scheduledDate desc")
	@Query(nativeQuery=true,value="SELECT  'System' firstName,' ' as lastName, 'img' as profilePicFilePath, p.*  FROM lti.post as p LEFT JOIN acads.faculty f  ON p.userId = f.facultyId  where p.scheduleFlag = ?1 and p.scheduledDate < ?2  order by p.scheduledDate desc")
	List<Post> getAllByScheduleFlagAndScheduledDateBeforeOrderByScheduledDateDesc(String scheduleFlag,String currentDate);
	
	List<Post> findByScheduleFlagAndScheduledDateBeforeOrderByScheduledDateDescPostIdDesc(String scheduleFlag,String currentDate);
	
	//Added For Resit students
	List<Post> findByScheduleFlagAndScheduledDateBeforeAndTypeNotInOrderByScheduledDateDescPostIdDesc(String scheduleFlag,String currentDate, List<String> postTypes);
	
	//@Query(nativeQuery=true,value="SELECT   IFNULL(f.firstName, 'System') as firstName,IFNULL(f.lastName, ' ') as lastName, f.imgUrl as profilePicFilePath, p.* FROM lti.post as p LEFT JOIN acads.faculty f  ON p.userId = f.facultyId   where p.subject_config_id = ?1 and p.scheduleFlag = ?2 and p.scheduledDate < ?3  order by scheduledDate desc")
	@Query(nativeQuery=true,value="SELECT 'System' firstName,' ' as lastName, 'img' as profilePicFilePath, p.* FROM lti.post as p LEFT JOIN acads.faculty f  ON p.userId = f.facultyId   where p.subject_config_id = ?1 and p.scheduleFlag = ?2 and p.scheduledDate < ?3  order by scheduledDate desc")
	List<Post> getAllByTimeboundIdAndScheduleFlagAndScheduledDateBeforeOrderByScheduledDateDesc(Integer timeboundId,
			String string, String currentDate);
	
	List<Post>findBySubjectConfigIdAndScheduleFlagAndScheduledDateBeforeOrderByScheduledDateDescPostIdDesc(Integer timeboundId, String string, String currentDate);
	
	//Added For Resit students
	List<Post>findBySubjectConfigIdAndScheduleFlagAndScheduledDateBeforeAndTypeNotInOrderByScheduledDateDescPostIdDesc(Integer timeboundId, String string, String currentDate, List<String> postTypes);
	
	 
	//	@Query(nativeQuery=true,value="SELECT  'System' firstName,' ' as lastName, 'img' as profilePicFilePath, p.*  FROM lti.post as p LEFT JOIN acads.faculty f  ON p.userId = f.facultyId  where p.scheduleFlag = ?1 and p.scheduledDate < ?2  order by p.scheduledDate desc")
	@Query(nativeQuery=true,value="SELECT  IFNULL(f.firstName, 'System') as firstName,IFNULL(f.lastName, ' ') as lastName, f.imgUrl as profilePicFilePath, p.*  FROM lti.post as p LEFT JOIN acads.faculty f  ON p.userId = f.facultyId  where p.scheduleFlag = ?1 and p.scheduledDate < ?2  order by p.scheduledDate desc")
	List getPostsFacultysByScheduleFlagAndScheduledDateBeforeOrderByScheduledDateDesc(String scheduleFlag,String currentDate);
		
	
	
}
