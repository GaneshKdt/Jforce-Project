package com.nmims.products;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */

public interface ITimeboundProducts {

	public static final String MBA_WX = "MBA - WX";
	public static final String MBA_X = "MBA - X";
	public static final String MSC_AI_ML_OPS = "M.Sc. (AI & ML Ops)";
	
	//MSC AI Programs
	public static final String MSC_AI = "M.Sc. (AI)";
	public static final String PCDS = "PC-DS";
	public static final String PDDS = "PD-DS";


	//Modular PD-DM Programs
	public static final String CSEM = "C-SEM";
	public static final String CSMM = "C-SMM";
	public static final String CDMA = "C-DMA";
	public static final String CSEM_AND_SMM = "C-SEM & SMM";
	public static final String CSEM_AND_DMA = "C-SEM & DMA";
	public static final String CSMM_AND_DMA = "C-SMM & DMA";
	public static final String PCDM = "PC-DM";
	public static final String PDDM = "PDDM";
	
	
	public static List<String> getTimeboundPrograms(){
		return Arrays.asList(MBA_WX,MSC_AI_ML_OPS,MSC_AI,PCDS,PDDS,CSEM,CSMM,CDMA,CSEM_AND_SMM,CSEM_AND_DMA,CSMM_AND_DMA,PCDM,PDDM);
	}
	
	public static List<String> getMSCAIPrograms(){
		return Arrays.asList(MSC_AI,PCDS,PDDS);
	}
	
	public static List<String> getModularPDDMPrograms(){
		return Arrays.asList(CSEM,CSMM,CDMA,CSEM_AND_SMM,CSEM_AND_DMA,CSMM_AND_DMA,PCDM,PDDM);
	}
}
