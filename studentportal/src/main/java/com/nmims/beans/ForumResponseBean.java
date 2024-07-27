package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForumResponseBean implements Serializable {
	HashMap<Long, String> replyCount = new HashMap<Long, String>();
	List<ForumStudentPortalBean> forumlist = new ArrayList<ForumStudentPortalBean>();

	public HashMap<Long, String> getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(HashMap<Long, String> replyCount) {
		this.replyCount = replyCount;
	}

	public List<ForumStudentPortalBean> getForumlist() {
		return forumlist;
	}

	public void setForumlist(List<ForumStudentPortalBean> forumlist) {
		this.forumlist = forumlist;
	}

	@Override
	public String toString() {
		return "ForumResponseBean [replyCount=" + replyCount + ", forumlist=" + forumlist + "]";
	}

}