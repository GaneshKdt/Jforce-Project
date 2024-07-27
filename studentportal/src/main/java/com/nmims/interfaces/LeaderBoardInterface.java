package com.nmims.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentRankBean;

public interface LeaderBoardInterface {

    public ResponseEntity<StudentRankBean> getCycleWiseRank(@RequestBody StudentStudentPortalBean bean);
    public ResponseEntity<StudentRankBean> getRankSubjectWiseBySapId(@RequestBody StudentStudentPortalBean bean);

}
	