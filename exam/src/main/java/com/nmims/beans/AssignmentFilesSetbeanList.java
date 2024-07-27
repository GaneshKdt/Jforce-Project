package com.nmims.beans;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AssignmentFilesSetbeanList {   

    List<AssignmentFilesSetbean> fileset;    

    


	public List<AssignmentFilesSetbean> getFileset() {
		return fileset;
	}




	public void setFileset(List<AssignmentFilesSetbean> fileset) {
		this.fileset = fileset;
	}
 



	@Override
	public String toString() {
		return "AssignmentFilesSetbeanList [fileset=" + fileset + "]";
	} 
    

}