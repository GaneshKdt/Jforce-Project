package com.nmims.factory;

import java.util.ArrayList;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.interfaces.MarsheetFactoryInterface;

public abstract class MarksheetFactory implements MarsheetFactoryInterface {
	
	public abstract MarksheetBean studentSelfMarksheet(PassFailExamBean studentMarks);
	
	public abstract ArrayList<EmbaPassFailBean> getClearedSemForStudent(String sapid);
	
}
