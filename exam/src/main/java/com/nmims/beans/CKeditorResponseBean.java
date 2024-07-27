package com.nmims.beans;

import java.io.Serializable;

public class CKeditorResponseBean  implements Serializable {
	/*
	   {
    "uploaded": 1,
    "fileName": "foo.jpg",
    "url": "/files/foo.jpg"
}

{
    "uploaded": 0,
    "error": {
        "message": "The file is too big."
    }
}
	*/
	
	private Integer uploaded = 0;
	private String fileName;
	private String url;
	
	private CKeditorErrorBean error;

	public Integer getUploaded() {
		return uploaded;
	}

	public void setUploaded(Integer uploaded) {
		this.uploaded = uploaded;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public CKeditorErrorBean getError() {
		return error;
	}

	public void setError(CKeditorErrorBean error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "CKeditorResponseBean [uploaded=" + uploaded + ", fileName=" + fileName + ", url=" + url + ", error="
				+ (error!=null ? error.toString() : "") + "]";
	}
	
	
	
	  
	    	
}
