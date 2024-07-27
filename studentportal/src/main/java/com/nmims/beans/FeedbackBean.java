package com.nmims.beans;

import java.io.Serializable;

public class FeedbackBean extends BaseStudentPortalBean  implements Serializable {
public String comments;
public String category;
public String rating;
public String getRating() {
	return rating;
}

public void setRating(String rating) {
	this.rating = rating;
}
public String getCategory() {
	return category;
}
public void setCategory(String category) {
	this.category = category;
}

public String getComments() {
	return comments;
}

public void setComments(String comments) {
	this.comments = comments;
}
}
