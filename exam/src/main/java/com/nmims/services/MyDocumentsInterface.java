package com.nmims.services;

import com.nmims.beans.DocumentResponseBean;

public interface MyDocumentsInterface {

	public abstract DocumentResponseBean getStudentsDocuments(String sapid) throws Exception;

}
