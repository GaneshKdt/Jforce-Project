package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.Gson;
import com.nmims.beans.StudentCourseDetailsBean;
import com.nmims.beans.Feature;
import com.nmims.beans.PackageBean;
import com.nmims.beans.PackageFamily;
import com.nmims.beans.PackageRequirements;
import com.nmims.beans.StudentPackageBean;
import com.nmims.beans.UpgradePath;
import com.nmims.beans.SalesForcePackage;
import com.nmims.beans.PacakageAvailabilityBean;
import com.nmims.beans.UpgradePathDetails;
import com.nmims.helpers.DataValidationHelpers;
import com.nmims.helpers.SalesForceHelper_Packages;


public class PackageApplicabilityDAO {
	//initiate jdbc
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(PackageApplicabilityDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	Gson gson = new Gson();
	
	@Autowired
	EntitlementCheckerDAO entitlementCheckerDAO;
	
	@Autowired
	SalesForceHelper_Packages salesForceHelper;
	
	DataValidationHelpers validationHelpers = new DataValidationHelpers();
	
	/*
	 * 	Master variable that stores all requirements for the packages with this consumer program structure
	 */
		List<PackageRequirements> requirementsList = new ArrayList<PackageRequirements>();
		
		/*
		 * 	find all packages applicable to the student
		 */
		 	public List<UpgradePathDetails> studentApplicablePackages(String sapid){
		 		//init return variable
		 		List<UpgradePathDetails> upgradePathDetails = new ArrayList<UpgradePathDetails>();
		 		//Get a bean of course details for the student. includes things like courseId which is used to check package availablility
				StudentCourseDetailsBean studentDetails = getStudentCourseDetails(sapid);
				//List of purchased packages
				List<StudentPackageBean> packagesPurchased = getStudentPurchasedPackages(sapid);
				String consumerProgramStructureId = studentDetails.getConsumerProgramStructureId();
				//initialize allRequirements
				initAllRequirementsForConsumerProgramStructure(consumerProgramStructureId);				
				//Get a list of all upgrade paths for this consumer program structure
				List<UpgradePath> paths = getUpgradePathsForConsumerProgramStructure(consumerProgramStructureId);

				//For each path in this program structure
				for(UpgradePath path : paths) {
					//Init list item 
					UpgradePathDetails pathDetails = new UpgradePathDetails();
					pathDetails.setUpgradePath(path);
					String pathId = path.getPathId();
					
					//set features for this consumer program structure available path
					List<Feature> features = getFeaturesForUpgradePathAndConsumerType(pathId, consumerProgramStructureId);
					pathDetails.setFeaturesAvailableForThisFamily(features);
					List<PacakageAvailabilityBean> availabilityBeans = getAvailabilityBeans(pathId, consumerProgramStructureId, packagesPurchased, studentDetails);
					//add item to list
					pathDetails.setPackages(availabilityBeans);
					if(pathDetails.getPackages() != null && pathDetails.getPackages().size() > 0) {
						upgradePathDetails.add(pathDetails);
					}
				}
				return upgradePathDetails;
			}
			
		 	
		 	public List<UpgradePathDetails> studentApplicablePackagesForSemester(String sapid, int currentSem){
		 		
		 		//init return variable
		 		List<UpgradePathDetails> upgradePathDetails = new ArrayList<UpgradePathDetails>();
		 		//Get a bean of course details for the student. includes things like courseId which is used to check package availablility
				StudentCourseDetailsBean studentDetails = getStudentCourseDetails(sapid);
				studentDetails.setCurrentSem(currentSem);
				//List of purchased packages
				List<StudentPackageBean> packagesPurchased = getStudentPurchasedPackages(sapid);
				String consumerProgramStructureId = studentDetails.getConsumerProgramStructureId();
				//initialize allRequirements
				initAllRequirementsForConsumerProgramStructure(consumerProgramStructureId);				
				//Get a list of all upgrade paths for this consumer program structure
				List<UpgradePath> paths = getUpgradePathsForConsumerProgramStructure(consumerProgramStructureId);

				//For each path in this program structure
				for(UpgradePath path : paths) {
					//Init list item 
					UpgradePathDetails pathDetails = new UpgradePathDetails();
					pathDetails.setUpgradePath(path);
					String pathId = path.getPathId();
					
					//set features for this consumer program structure available path
					List<Feature> features = getFeaturesForUpgradePathAndConsumerType(pathId, consumerProgramStructureId);
					pathDetails.setFeaturesAvailableForThisFamily(features);
					List<PacakageAvailabilityBean> availabilityBeans = getAvailabilityBeans(pathId, consumerProgramStructureId, packagesPurchased, studentDetails);
					//add item to list
					pathDetails.setPackages(availabilityBeans);
					if(pathDetails.getPackages() != null && pathDetails.getPackages().size() > 0) {
						upgradePathDetails.add(pathDetails);
					}
				}
				return upgradePathDetails;
			}
			
		 	
		 	
		 /*
		  * get packageAvailabilityBean for family with packageId
		  */
		 	public PacakageAvailabilityBean getPackageAvailabilityBeanFor(String sapid, String familyId) {
		 		
		 		//get the family of the package; used to fetch the info about applicability and static applicability info
		 		PackageFamily family = getFamilyFromId(familyId);
		 		
		 		//packages in this family
		 		List<PackageBean> packages = getPackagesOfFamily(family.getFamilyId());
		 		
		 		//Get a bean of course details for the student. includes things like courseId which is used to check package availablility
				StudentCourseDetailsBean studentDetails = getStudentCourseDetails(sapid);
				
				//init requirements 
				initAllRequirementsForConsumerProgramStructure(studentDetails.getConsumerProgramStructureId());
				
				//List of purchased packages
				List<StudentPackageBean> studentPackages = getStudentPurchasedPackages(sapid);
				
				//get availability info
				PacakageAvailabilityBean availabilityBean = getFamilyApplicabilityForStudent(sapid, family, packages, studentPackages, studentDetails);
				
				//temporary wrapper list used to fetch price
				List<PacakageAvailabilityBean> beans = new ArrayList<PacakageAvailabilityBean>();
				beans.add(availabilityBean);
				
				//set the prices to this list and then return the bean of this family
				return setPricesFromSF(beans).get(0);
		 	}
		 	
		 /*
		  * get PackageFamily
		  */
		 	
		 	public PackageFamily getFamily(String familyId) {
		 		return getFamilyFromId(familyId);
		 	}
		 	
		 	/*
		 	 * 	Get packages of family
		 	 */
		 		private List<PackageBean> getPackagesOfFamily(String familyId){
		 			String sql = "SELECT * "
			 				+ "FROM "
			 				+ "`products`.`packages` "
			 				+ "WHERE "
			 				+ "`packageFamily` = ? ";
		
			 		try {
			 			List<PackageBean> packages = jdbcTemplate.query(sql, 
			 					new Object[] { familyId },
			 					new BeanPropertyRowMapper<PackageBean>(PackageBean.class));
			 			
			 			if(packages.size() > 0) {
				 			return packages;
			 			}
			 		}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
			 		return null;
		 		}
		 	/*
		 	 * 	Get family
		 	 */
		 		private PackageFamily getFamilyFromId(String familyId) {
		 			String sql = "SELECT "
		 					+ "* "
		 					+ "FROM "
		 					+ "`products`.`package_families` "
		 					+ "WHERE "
		 					+ "`familyId` = ?";
		 			
		 			try {
			 			List<PackageFamily> families = jdbcTemplate.query(sql, 
			 					new Object[] { familyId },
			 					new BeanPropertyRowMapper<PackageFamily>(PackageFamily.class));
			 			
			 			if(families.size() > 0) {
				 			return families.get(0);
			 			}else {
			 				return null;
			 			}
			 		}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
			 		return null;
		 		}
		 	/*
		 	 * 	Get a list of availabilityBeans
		 	 */
				private List<PacakageAvailabilityBean> getAvailabilityBeans(String pathId, String consumerProgramStructureId, List<StudentPackageBean> studentPackages, StudentCourseDetailsBean studentDetails){
		
					//get a list of all families in this upgrade path
					List<PackageFamily> families = getFamiliesForPath(pathId, consumerProgramStructureId);
		
					//Get packages in this path for this consumer program structure
						//all packages are called at first pass to reduce db calls
						List<PackageBean> packages = getPackagesInUpgradePathForConsumerProgramStructure(pathId, consumerProgramStructureId);
					
						
					//Initialize List to return package availability in
					List<PacakageAvailabilityBean> availabilityBeans = new ArrayList<PacakageAvailabilityBean>();
					
					
					//for each family
					for(PackageFamily family : families) {
						availabilityBeans.add(getFamilyApplicabilityForStudent(studentDetails.getSapid(), family, packages, studentPackages, studentDetails));
					}
					
					availabilityBeans = setPricesFromSF(availabilityBeans);
					return availabilityBeans;
				}
				
				private PacakageAvailabilityBean getFamilyApplicabilityForStudent(String sapid, PackageFamily family, List<PackageBean> packages, List<StudentPackageBean> studentPackages, StudentCourseDetailsBean studentDetails) {

					//initiate availability bean
					PacakageAvailabilityBean availabilityBean = new PacakageAvailabilityBean();
					StudentPackageBean activePackage = checkIfThisPackageOfFamilyStillActiveForStudent(family.getFamilyId(), studentPackages);
					
					initAllRequirementsForConsumerProgramStructure(studentDetails.getConsumerProgramStructureId());
					//check if a package of this family is currently active
					if(activePackage == null) {
	
						//init at false
						availabilityBean.setAvailable(false);
						availabilityBean.setPdfURL("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
						availabilityBean.setFamilyId(family.getFamilyId());
						
						for(PackageBean thisPackage: packages) {
							//for each package, check for packages in this family.
							if(thisPackage.getPackageFamily().equals(family.getFamilyId()) && !availabilityBean.isAvailable()) {

								if(thisPackage.isOpenForSale()) {
									availabilityBean.setUpcoming(false);
									//for each package in this family,
										//if an applicable package is not found yet, set the packageId as this:

										availabilityBean.setPackageId(thisPackage.getPackageId());
										availabilityBean.setSalesForceUID(thisPackage.getSalesForceUID());
										availabilityBean.setDurationType(thisPackage.getDurationType());
										
										thisPackage.setPackageRequirements(getRequirementsForPackageId(thisPackage.getPackageId()));
									//check if this package is applicable for the student
									if(checkIfPackageApplicableForStudent(thisPackage, studentDetails)) {
										availabilityBean.setAvailable(true);
									}

								}else {
									availabilityBean.setPackageId(thisPackage.getPackageId());
									availabilityBean.setSalesForceUID(thisPackage.getSalesForceUID());
									availabilityBean.setDurationType(thisPackage.getDurationType());
									availabilityBean.setUpcoming(thisPackage.isUpcoming());
								}
							}
						}
					}else {
						availabilityBean.setPackageId(activePackage.getPurchasedPackage().getPackageId());
						availabilityBean.setSalesForceUID(activePackage.getPurchasedPackage().getSalesForceUID());
						availabilityBean.setDurationType(activePackage.getPurchasedPackage().getDurationType());
						availabilityBean.setPurchased(true);
						availabilityBean.setAvailable(false);
					}
					//Set features for whatever the final packageId this package has
					availabilityBean.setAvailableFeatures(getFeatureIdsForPackage(availabilityBean.getPackageId()));

					availabilityBean.setViewDetailsURL("careerservices/packageDetailsWebView?&productId=" + family.getFamilyId());

					availabilityBean.setFamilyId(family.getFamilyId());
					availabilityBean.setFamilyName(family.getFamilyName());
					availabilityBean.setDescriptionShort(family.getDescriptionShort());
					availabilityBean.setDescription(family.getDescription());
					availabilityBean.setEligibilityCriteria(family.getEligibilityCriteria());
					availabilityBean.setComponentEligibilityCriteria(family.getComponentEligibilityCriteria());
					availabilityBean.setKeyHighlights(family.getKeyHighlights());
					return availabilityBean;
				}

				private PackageRequirements getRequirementsForPackageId(String packageId) {

					for(PackageRequirements reqs :requirementsList) {
						if(reqs.getPackageId().equals(packageId)) {
							return reqs;
						}
					}
					return null;
				}
				
				private PackageRequirements getRequirementsForPackageIdAndConsumerTypeId(String packageId, String consumerTypeId) {

					for(PackageRequirements reqs :requirementsList) {
						if(reqs.getPackageId().equals(packageId) && reqs.getConsumerProgramStructureId().equals(consumerTypeId)) {
							return reqs;
						}
					}
					return null;
				}
				
			 	private boolean initAllRequirementsForConsumerProgramStructure(String consumerProgramStructureId) {

			 		//get all the requirements for all packages that include this consumer program structure id
			 		String sql = "SELECT "
			 				+ "`pr`.`requirementsId`, "
			 				+ "`pr`.`packageId`, "
			 				+ "`prmm`.`consumerProgramStructureId`, "
			 				+ "`pr`.`requiredSemMin`, "
			 				+ "`pr`.`requiredSemMax`, "
			 				+ "`pr`.`minSubjectsClearedTotal`, "
			 				+ "`pr`.`minSubjectsClearedPerSem`, "
			 				+ "`pr`.`availableForAlumni`, "
			 				+ "`pr`.`availableForAlumniOnly`, "
			 				+ "`pr`.`alumniMaxMonthsAfterLastRegistration` "
			 				+ "FROM "
			 				+ "`products`.`package_requirements` `pr` "
			 				+ "LEFT JOIN "
			 				+ "`products`.`package_requirements_master_mapping` `prmm` "
			 				+ "ON "
			 				+ "`prmm`.`requirementsId` = `pr`.`requirementsId` "
			 				+ "WHERE "
			 				+ "`prmm`.`consumerProgramStructureId` = ?";
			 		try {
			 			List<PackageRequirements> requirements = jdbcTemplate.query(sql, 
			 					new Object[] { consumerProgramStructureId},
			 					new BeanPropertyRowMapper<PackageRequirements>(PackageRequirements.class));


			 			if(requirements.size() > 0) {
			 				requirementsList = requirements;
			 				return true;
			 			}
			 		}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
					return false;
			 	}
		
				private List<String> getFeatureIdsForPackage(String packageId) {
					String sql = "SELECT featureId "
							+ "FROM "
							+ "`products`.`package_features` "
							+ "WHERE "
							+ "`packageId` = ?";
					
					try {
			 			List<String> features = jdbcTemplate.query(sql, 
			 					new Object[] { packageId},
			 					new RowMapper<String>(){
				                    public String mapRow(ResultSet rs, int rowNum) 
				                                                 throws SQLException {
				                            return rs.getString(1);
				                    }
			               });
			 			
			 			if(features.size() > 0) {
				 			return features;
			 			}
			 		}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
					return new ArrayList<String>();
				}
					
			 	private List<UpgradePath> getUpgradePathsForConsumerProgramStructure(String consumerProgramStructureId){
			 		
			 		String sql = ""
			 				//What to filter by
			 				+ "SELECT `up`.* "
			 				+ "FROM "
			 				+ "`products`.`packages` `p` "
			 				//Joins to filter by consumer id
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_requirements` `pr` "
				 				+ "ON "
				 				+ "`pr`.`packageId` = `p`.`packageId` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_requirements_master_mapping` `prmm` "
				 				+ "ON "
				 				+ "`pr`.`requirementsId` = `prmm`.`requirementsId` "
			 				// END
				 			// Joins to filter by path id
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_families` `pfam` "
				 				+ "ON "
				 				+ "`pfam`.`familyId` = `p`.`packageFamily` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`upgrade_path_packages` `upp` "
				 				+ "ON "
				 				+ "`upp`.`packageFamilyId` = `p`.`packageFamily` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`upgrade_paths` `up` "
				 				+ "ON "
				 				+ "`upp`.`pathId` = `up`.`pathId` "
			 				//END
			 				+ "WHERE "
			 				+ "`prmm`.`consumerProgramStructureId` = ? "
			 				+ "GROUP BY "
			 				//filter by upgrade path. pathId
			 				+ "`pathId`";
			 		try {
			 			List<UpgradePath> paths = jdbcTemplate.query(sql, 
			 					new Object[] { consumerProgramStructureId},
			 					new BeanPropertyRowMapper<UpgradePath>(UpgradePath.class));
			 			
			 			if(paths.size() > 0) {
				 			return paths;
			 			}
			 		}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
					return new ArrayList<UpgradePath>();
			 	}
		
			 	private List<PackageBean> getPackagesInUpgradePathForConsumerProgramStructure(String pathId, String consumerProgramStructureId){
			 		String sql = "SELECT `p`.* "
			 				+ "FROM "
			 				+ "`products`.`packages` `p` "
			 				//Joins to filter by consumer id
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_requirements` `pr` "
				 				+ "ON "
				 				+ "`pr`.`packageId` = `p`.`packageId` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_requirements_master_mapping` `prmm` "
				 				+ "ON "
				 				+ "`pr`.`requirementsId` = `prmm`.`requirementsId` "
			 				// END
				 			// Joins to filter by path id
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_families` `pfam` "
				 				+ "ON "
				 				+ "`pfam`.`familyId` = `p`.`packageFamily` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`upgrade_path_packages` `upp` "
				 				+ "ON "
				 				+ "`upp`.`packageFamilyId` = `p`.`packageFamily` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`upgrade_paths` `up` "
				 				+ "ON "
				 				+ "`upp`.`pathId` = `up`.`pathId` "
			 				//END
			 				+ "WHERE "
			 				+ "`upp`.`pathId` = ? "
			 				+ "AND "
			 				+ "`prmm`.`consumerProgramStructureId` = ? "
			 				+ "GROUP BY "
			 				+ "`packageId`";
		
			 		try {
			 			List<PackageBean> packages = jdbcTemplate.query(sql, 
			 					new Object[] { pathId , consumerProgramStructureId},
			 					new BeanPropertyRowMapper<PackageBean>(PackageBean.class));
			 			
			 			if(packages.size() > 0) {
				 			return packages;
			 			}
			 		}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
			 		return new ArrayList<PackageBean>();
			 	}
			 	
			 	private List<Feature> getFeaturesForUpgradePathAndConsumerType(String pathId, String consumerProgramStructureId){
			 		String sql = "SELECT `f`.* "
			 				+ "FROM "
			 				+ "`products`.`package_features` `pf` "
			 				+ "LEFT JOIN "
			 				+ "`products`.`features` `f` "
			 				+ "ON "
			 				+ "`f`.`featureId` = `pf`.`featureId` "
			 				+ "LEFT JOIN "
			 				+ "`products`.`packages` `p` "
			 				+ "ON "
			 				+ "`p`.`packageId` = `pf`.`packageId` "
			 				//Joins to filter by consumer id
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_requirements` `pr` "
				 				+ "ON "
				 				+ "`pr`.`packageId` = `p`.`packageId` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_requirements_master_mapping` `prmm` "
				 				+ "ON "
				 				+ "`pr`.`requirementsId` = `prmm`.`requirementsId` "
			 				// END
				 			// Joins to filter by path id
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_families` `pfam` "
				 				+ "ON "
				 				+ "`pfam`.`familyId` = `p`.`packageFamily` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`upgrade_path_packages` `upp` "
				 				+ "ON "
				 				+ "`upp`.`packageFamilyId` = `p`.`packageFamily` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`upgrade_paths` `up` "
				 				+ "ON "
				 				+ "`upp`.`pathId` = `up`.`pathId` "
			 				//END
			 				+ "WHERE "
			 				+ "`upp`.`pathId` = ? "
			 				+ "AND "
			 				+ "`prmm`.`consumerProgramStructureId` = ? "
			 				+ "GROUP BY "
			 				+ "`featureId`";
		
			 		try {
			 			List<Feature> features = jdbcTemplate.query(sql, 
			 					new Object[] { pathId , consumerProgramStructureId},
			 					new BeanPropertyRowMapper<Feature>(Feature.class));
			 			
			 			if(features.size() > 0) {
				 			return features;
			 			}else {
			 				return new ArrayList<Feature>();
			 			}
			 		}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
			 		return new ArrayList<Feature>();
			 	}
			 	
			 	private List<PackageFamily> getFamiliesForPath(String pathId, String consumerProgramStructureId){
			 		String sql = "SELECT `pfam`.* "
			 				+ "FROM "
			 				+ "`products`.`package_features` `pf` "
			 				+ "LEFT JOIN "
			 				+ "`products`.`features` `f` "
			 				+ "ON "
			 				+ "`f`.`featureId` = `pf`.`featureId` "
			 				+ "LEFT JOIN "
			 				+ "`products`.`packages` `p` "
			 				+ "ON "
			 				+ "`p`.`packageId` = `pf`.`packageId` "
			 				//Joins to filter by consumer id
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_requirements` `pr` "
				 				+ "ON "
				 				+ "`pr`.`packageId` = `p`.`packageId` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_requirements_master_mapping` `prmm` "
				 				+ "ON "
				 				+ "`pr`.`requirementsId` = `prmm`.`requirementsId` "
			 				// END
				 			// Joins to filter by path id
				 				+ "LEFT JOIN "
				 				+ "`products`.`package_families` `pfam` "
				 				+ "ON "
				 				+ "`pfam`.`familyId` = `p`.`packageFamily` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`upgrade_path_packages` `upp` "
				 				+ "ON "
				 				+ "`upp`.`packageFamilyId` = `p`.`packageFamily` "
				 				+ "LEFT JOIN "
				 				+ "`products`.`upgrade_paths` `up` "
				 				+ "ON "
				 				+ "`upp`.`pathId` = `up`.`pathId` "
			 				//END
			 				+ "WHERE "
			 				+ "`upp`.`pathId` = ? "
			 				+ "AND "
			 				+ "`prmm`.`consumerProgramStructureId` = ? "
			 				+ "GROUP BY "
			 				+ "`familyId`";
		
			 		try {
			 			List<PackageFamily> families = jdbcTemplate.query(sql, 
			 					new Object[] { pathId , consumerProgramStructureId},
			 					new BeanPropertyRowMapper<PackageFamily>(PackageFamily.class));
			 			
			 			if(families.size() > 0) {
				 			return families;
			 			}else {
			 				return new ArrayList<PackageFamily>();
			 			}
			 		}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
			 		return new ArrayList<PackageFamily>();
			 	}

 	/*
 	 * 	students active/purchased packages
 	 */
		public List<StudentPackageBean> getStudentPurchasedPackages(String sapid){	
			List<StudentPackageBean> packageBeans = jdbcTemplate.query(
													"SELECT "
													+ "`p`.`packageId` AS `packageId`, "
													+ "`p`.`packageName` AS `packageName`, "
													+ "`p`.`durationMax` AS `durationMax`, "
													+ "`p`.`durationType` AS `durationType`, "
													+ "`p`.`packageFamily` AS `packageFamily`, "
													+ "`sp`.`startDate` AS `startDate`, "
													+ "`sp`.`endDate` AS `endDate`, "
													+ "`sp`.`salesForceUID` AS `salesForceUID`"
													+ " FROM " 
													+ "`products`.`student_packages` AS `sp` " 
													+ "LEFT JOIN "
													+ "`products`.`packages` AS `p` " 
													+ "ON "
													+ "`sp`.`salesForceUID`=`p`.`salesForceUID` " 
													+ "WHERE "
													+ "`sp`.`sapid`= ? "
//													Currently no logic appended for laterals
//													+ "OR "
//													+ "`sp`.`sapid` IN "
//													//for lateral students, if their old account has had any purchases, use them
//													+ "( "
//														+ "SELECT "
//														+ "`previousStudentId` AS `sapid` "
//														+ "FROM "
//														+ "`exam`.`students` "
//														+ "WHERE "
//														+ "`sapid` = ?"
//													+ ")"
														, 
													new Object[] { 
//															sapid, 
															sapid }, 
													new RowMapper<StudentPackageBean>() {
			    @Override
			    public StudentPackageBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			        PackageBean newPackageBean = new PackageBean();
	
			        String packageId = (String) rs.getString("packageId");
			        String packageFamily = (String) rs.getString("packageFamily");
			    	String packageName = (String) rs.getObject("packageName");
			    	int durationMax = (int) rs.getInt("durationMax");
			    	String durationType = rs.getString("durationType");
			    	String salesForceUID = rs.getString("salesForceUID");
			    	
			    	newPackageBean.setSalesForceUID(salesForceUID);
			    	newPackageBean.setDurationType(durationType);
			    	newPackageBean.setPackageFamily(packageFamily);;
			    	newPackageBean.setPackageId(packageId);
			    	newPackageBean.setPackageName(packageName);
			    	newPackageBean.setDurationMax(durationMax);
			    	
			    	StudentPackageBean newStudentPackageBean = new StudentPackageBean();
			    	newStudentPackageBean.setPurchasedPackage(newPackageBean);
			    	newStudentPackageBean.setSapid(sapid);
			    	newStudentPackageBean.setStartDate(rs.getDate("startDate"));
			    	newStudentPackageBean.setEndDate(rs.getDate("endDate"));
			    	
			        return newStudentPackageBean;
			    }
			});
			return packageBeans;
		}

	/*
	 * 	get the students course progress details
	 */
		public StudentCourseDetailsBean getStudentCourseDetails(String sapid) {
			//returns StudentCourseDetailsBean 
			String sql = "SELECT "
					+ "`student`.`sapid` AS `sapid`, "
					+ "`programs`.`programType` AS `program`, "
					+ "`student`.`programcleared` AS `programCleared`, "
					+ "`programs`.`noOfSubjectsToClear` AS `noOfSubjectsToClear`, "
					+ "`programs`.`noOfSubjectsToClearLateral` AS `noOfSubjectsToClearLateral`, "
					+ "`programs`.`noOfSubjectsToClearSem` AS `noOfSubjectsToClearSem`, "
					+ "`programs`.`noOfSemesters` AS `noOfSemesters`, "
					+ "`student`.`isLateral` AS `isLateral`, "
					+ "`registrationInfo`.`sem` AS `currentSem`, "
					+ "`student`.`consumerProgramStructureId` AS `consumerProgramStructureId`, "
					+ "`registrationInfo`.`createdDate` AS `lastRegistrationDate`, "
					+ "`registrationInfo`.`sem` AS `sem` "
				+ "FROM `exam`.`students` AS `student` "
				+ "LEFT JOIN `exam`.`consumer_program_structure` AS `cps` "
				+ "ON `student`.`consumerProgramStructureId`=`cps`.`id` "
				+ "LEFT JOIN `exam`.`programs` AS `programs` "
				+ "ON `cps`.`id`=`programs`.`consumerProgramStructureId` "
				+ "LEFT JOIN ( "
					+ "SELECT "
					+ "* "
					+ "FROM  "
					+ "`exam`.`registration`  "
					+ "WHERE (sapid,sem) "
					+ "IN "
					+ "( "
						+ "SELECT "
						+ "sapid, MAX(sem)"
						+ "FROM  "
						+ "`exam`.`registration` "
						+ "GROUP BY "
						+ "`sapid`"
					+ ")"
				+ ") `registrationInfo` "
				+ "ON "
				+ "`registrationInfo`.`sapid` = `student`.`sapid`"
				+ "WHERE "
				+ "`student`.`sapid` = ? ";
			List<StudentCourseDetailsBean> courseDetailsList = jdbcTemplate.query(
					sql, 
					new Object[] { 
							sapid 
						},
					new BeanPropertyRowMapper<StudentCourseDetailsBean>(StudentCourseDetailsBean.class)
					);
			if(courseDetailsList.size() > 0) {
				StudentCourseDetailsBean courseDetails = courseDetailsList.get(0);
				courseDetails.setSemResults(getStudentSemesterViseResults(sapid));
				courseDetails = checkIfStudentIsAlumni(courseDetails);
				
				return courseDetails;
			}
			return null;
		}
		
			/*
		 * 	get the students results
		 */
			private Map<Integer, Integer> getStudentSemesterViseResults(String sapid) {
				Map<Integer, Integer> mapToReturn = new HashMap<Integer, Integer>();
				/*
				 * Automatically check to see if the student is lateral
				 * If the student is lateral, 
				 * 	get their old student id and get the marks for the previous semester/s too
				 */
				String sql = ""
						+ "SELECT "
							+ "`sem`, "
							+ "COUNT(*) as `Papers Passed` "
						+ "FROM "
							+ "`exam`.`passfail` "
						+ "WHERE "
							+ "`sapid` = ? "
						+ "AND "
							+ "`isPass` = 'Y' "
						+ "GROUP BY "
							+ "`sem`";
				List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[] { sapid });
				
				for(Map<String, Object> result: results) {
					int semNo = (int) result.get("sem");
					int numPass = (int) Math.toIntExact((long) result.get("Papers Passed"));
					mapToReturn.put(semNo, numPass);
				}
				return mapToReturn;
			}
	
		/*
		 * 	Checks if the student is an alumni
		 */
			private StudentCourseDetailsBean checkIfStudentIsAlumni(StudentCourseDetailsBean courseDetails) {
				// Checks if the required number of subjects have been passed. if true then set isAlumni to true
				courseDetails.setAlumni(false);
				int noOfSubjectsToClear = courseDetails.isLateral() ? 
						courseDetails.getNoOfSubjectsToClearLateral() : courseDetails.getNoOfSubjectsToClear();
				
				int totalCleared = 0;
				for (int i = 1; i <= courseDetails.getNoOfSemesters(); i++) {
					if(courseDetails.getSemResults().get(i) != null) {
						totalCleared += courseDetails.getSemResults().get(i);
					}
				}
				
				if(totalCleared >= noOfSubjectsToClear) {
					courseDetails.setAlumni(true);
				}
				return courseDetails;
			}
		
/*
 * 	------------------------ GET Packages ------------------------
 */
		
/*
 * 	--------------------------END---------------------------------
 */
	
	/*
	 * 	returns true if the user exists
	 */
		public boolean checkIfUserExists(String sapid) {
			String sql = "SELECT "
					+ "count(*) "
					+ "FROM "
					+ "`exam`.`students` "
					+ "WHERE "
					+ "sapid=?";
			int result = jdbcTemplate.queryForObject(sql, new Object[] { sapid }, Integer.class);
	
			if(result > 0) {
				return true;
			}
			return false;
		}

	/*
	 * 	takes packages as input, calls SalesForce for latest prices and returns the prices
	 */
		public List<PacakageAvailabilityBean> setPricesFromSF(List<PacakageAvailabilityBean> inputPackages)  {
			
			List<SalesForcePackage> salesForcePrices = salesForceHelper.getPackagePrices();

			List<PacakageAvailabilityBean> packagesToReturn = new ArrayList<PacakageAvailabilityBean>();
			
			if(salesForcePrices != null) {
				for(SalesForcePackage thisPackage: salesForcePrices) {
					for(PacakageAvailabilityBean onePackage: inputPackages) {

						if(onePackage.getSalesForceUID() != null) {
							if(onePackage.getSalesForceUID().equalsIgnoreCase(thisPackage.getPackageId())) {
								onePackage.setPrice(thisPackage.getUnitPrice());
								packagesToReturn.add(onePackage);
							}
						}
					}
				}
			}
			
			return packagesToReturn;
		}
	/*
	 * 	Check if this student has active packages
	 */
		public boolean checkIfStudentHasActivePackages(String sapid) {
	
			List<StudentPackageBean> purchasedPackageList = getStudentActivePackages(sapid);
			int activePackages = 0;
			for(StudentPackageBean studentPackage: purchasedPackageList) {
				if(checkIfPackageStillActiveForStudent(studentPackage)) {
					activePackages++;
				}
			}
			
			if(activePackages > 0) {
				return true;
			}else {
				return false;
			}
		}	

	/*
	 * 	Get the list of active packages for student. Used externally by webinar scheduler
	 */
		public List<StudentPackageBean> getStudentActivePackages(String sapid) {

			String sql = "SELECT "
					+ "`sp`.`startDate` AS `startDate`, "
					+ "`sp`.`endDate` AS `endDate`, "
					+ "`p`.`durationMax` AS `durationMax`,"
					+ "`p`.`packageFamily` "
					+ "FROM "
					+ "`products`.`student_packages` `sp`"
					+ "LEFT JOIN "
					+ "`products`.`packages` `p` "
					+ "ON `sp`.`salesForceUID`=`p`.`salesForceUID` "
					+ "WHERE sapid=?";
			return jdbcTemplate.query(sql, 
					new Object[] { sapid },
					new RowMapper<StudentPackageBean>() {
				@Override
				public StudentPackageBean mapRow(ResultSet rs, int rowNum) throws SQLException {
					StudentPackageBean studentPackage = new StudentPackageBean();
					studentPackage.setStartDate(rs.getDate("startDate"));
					studentPackage.setEndDate(rs.getDate("endDate"));
					
					PackageBean newPackage = new PackageBean();
					newPackage.setDurationMax(rs.getInt("durationMax"));
					
					studentPackage.setPurchasedPackage(newPackage);
					return studentPackage;
				}
			});
		}

/*
 * 	----------- Helper functions to reduce boilerplate -----------
 */

	
	/*
	 * 	Get all features
	 */
		public List<Feature> getAllFeatures() {
			
			String sql = "SELECT "
					+ "`f`.`featureId` AS `featureId`, "
					+ "`f`.`featureName` AS `featureName`, "
					+ "`f`.`featureDescription` AS `featureDescription`, "
					+ "`f`.`validityFast` AS `validityFast`, "
					+ "`f`.`validityNormal` AS `validityNormal`, "
					+ "`f`.`validitySlow` AS `validitySlow`"
					+ "FROM `products`.`package_features` `pf`"
					+ "LEFT JOIN `products`.`features` `f`"
					+ "ON `f`.`featureId`=`pf`.`featureId`";
			List<Feature> featureList = jdbcTemplate.query(sql, 
					new RowMapper<Feature>() {
				@Override
				public Feature mapRow(ResultSet rs, int rowNum) throws SQLException {
					Feature feature = new Feature();
					feature.setFeatureId(rs.getString("featureId"));
					feature.setFeatureName(rs.getString("featureName"));
					feature.setFeatureDescription(rs.getString("featureDescription"));
					feature.setValidityFast(rs.getInt("validityFast"));
					feature.setValidityNormal(rs.getInt("validityNormal"));
					feature.setValiditySlow(rs.getInt("validitySlow"));
					return feature;
				}
			});
			
			return featureList;
		}

	/*
	 * 	Checks for the package/s expiry
	 */
		public boolean checkIfThisPackageStillActiveForStudent(PackageBean thisPackage, List<StudentPackageBean> studentPackages) {
			//checks if some package is still active for student
			boolean activePackageFound = false;
			for(StudentPackageBean purchasedPackage: studentPackages) {
				if(thisPackage.getPackageFamily().equals(purchasedPackage.getPurchasedPackage().getPackageFamily())) {
					if(checkIfPackageStillActiveForStudent(purchasedPackage)) {
						activePackageFound = true;
					}
				}
			}		
			return activePackageFound;
		}
		
		public StudentPackageBean checkIfThisPackageOfFamilyStillActiveForStudent(String familyId, List<StudentPackageBean> studentPackages) {
			//checks if some package is still active for student
			for(StudentPackageBean purchasedPackage: studentPackages) {
				if(familyId.equals(purchasedPackage.getPurchasedPackage().getPackageFamily())) {
					if(checkIfPackageStillActiveForStudent(purchasedPackage)) {
						return purchasedPackage;
					}
				}
			}		
			return null;
		}
		private boolean checkIfPackageStillActiveForStudent(StudentPackageBean studentPackage) {
			//checks if this package is active for the student
			if(studentPackage.getEndDate() != null) {
				if(validationHelpers.checkIfDateBeforeCurrent(studentPackage.getEndDate())) {
					return false;
				}
			}
			return true;
		}
		
	/*
	 * 	-------- Check if package is applicable for the student --------
	 */
	
		private boolean checkIfPackageApplicableForStudent(PackageBean packageToTest, StudentCourseDetailsBean studentDetails) {
			/*
			 * studentDetails: 
			 * An Map of [student program, subjects to complete per sem, currentSem]
			 * Map program[] = getStudentDetails(sapid);
			 * 
			 * semResults:
			 * An array of results of students(size 4).
			 * int[] semResults = getStudentSemesterViseResults(sapid);
	 		 * 
			 */
			String sapid = studentDetails.getSapid();
			boolean canPurchase = false;

			//List of which paths this family is a part of
			List<String> pathIds = getPathIdsFromFamilyId(packageToTest.getPackageFamily());
			
			for(String pathId: pathIds) {
				//check the students progress in this path
				if(studentCanPurchaseInThisPath(sapid, pathId, packageToTest.getPackageFamily())) {
					//if the student can purchase a package in this path, continue.
					canPurchase = true;
				}
			}
			
			//because a package can be a part of multiple paths
			if(!canPurchase) {
				return false;
			}			
			
			if(studentDetails.isAlumni()) {
			//if this is an alumni student
				//get the parameters
				Date lastRegistrationDate = studentDetails.getLastRegistrationDate();
				int maxMonths = packageToTest.getPackageRequirements().getAlumniMaxMonthsAfterLastRegistration();
				
				//if maxMonths = 0 then it means the months condition isnt configured. Always return true
				if(maxMonths == 0) {
					return true;
				}
				
				//add the months to the last registration date. get 
				Date validTill = validationHelpers.addMonthsToDate(lastRegistrationDate, maxMonths);
				
				//if valid till date is not before current, return true 
				if(!validationHelpers.checkIfDateBeforeCurrent(validTill)) {
					return true;
				}
			}else {
				PackageRequirements requirements = packageToTest.getPackageRequirements();
				
				//only alumni students can see this package. 
				//for example Career Development & Assistance is only visible to Diploma alumni
				if(requirements == null || requirements.isAvailableForAlumniOnly()) {
					return false;
				}
				int currentSem = studentDetails.getCurrentSem();
				
				if(requirements.getRequiredSemMax() == 0) {
					//if there is no max, set max to 10
					requirements.setRequiredSemMax(10);
				}
				//semester is in range( > min and < max | min < semester < max)
				boolean semesterInRangeConsition = validationHelpers.checkIfNumberInRange(studentDetails.getCurrentSem(), requirements.getRequiredSemMin(), requirements.getRequiredSemMax());
				
				
				//MinSubjectsClearedTotalCondition
	
					int minSubjectsClearedPerSem = 0;
					
					//in case the requirement requires more subjects to be cleared per sem than the total subjects per sem in this program
					if(studentDetails.getNoOfSubjectsToClearSem() >= requirements.getMinSubjectsClearedPerSem()) {
						minSubjectsClearedPerSem = requirements.getMinSubjectsClearedPerSem();
					}else {
						minSubjectsClearedPerSem = studentDetails.getNoOfSubjectsToClearSem();
					}
					
					//initialize true and make false if any offenders found;
					boolean minSubjectsClearedPerSemCondition = true;
					Map<Integer, Integer> results = studentDetails.getSemResults();
					
					for(Entry<Integer, Integer> result : results.entrySet()) {
						
						if(result.getValue() < minSubjectsClearedPerSem) {
							//if this isnt the index of the current sem
							if(result.getKey() < currentSem) {
								minSubjectsClearedPerSemCondition = false;
							}
						}
					}

				//MinSubjectsClearedTotalCondition
					//in case the requirement requires more subjects to be cleared than the total subjects in this program
					int minSubjectsClearedTotal = 0;
					
					if(studentDetails.getNoOfSubjectsToClear() >= requirements.getMinSubjectsClearedTotal()) {
						minSubjectsClearedTotal = requirements.getMinSubjectsClearedTotal();
					}else {
						minSubjectsClearedTotal = studentDetails.getNoOfSubjectsToClear();
					}
					
					int totalSubjectsClearedSoFar = 0;
					for(Entry<Integer, Integer> result : results.entrySet()) {
						totalSubjectsClearedSoFar += result.getValue();
					}
					
					boolean minSubjectsClearedTotalCondition = totalSubjectsClearedSoFar >= minSubjectsClearedTotal;

				if(minSubjectsClearedTotalCondition && minSubjectsClearedPerSemCondition && semesterInRangeConsition) {
					return true;
				}
			}
			return false;
		}
		
		/*
		 * 	Check if the student can purchase a package from this family in this path
		 */
			private boolean studentCanPurchaseInThisPath(String sapid, String pathId, String familyId) {	
				//get users total level in this path
				int totalLevelInPath = getTotalLevelInPath(sapid, pathId);
				//check if student has enough progress to purchase this one package
				return checkIfTotalLevelIsEnough(pathId, familyId, totalLevelInPath);
			}
		
		/*
		 * 	Check if the progress made in this path is enough.
		 */
			private boolean checkIfTotalLevelIsEnough(String pathId, String familyId, int totalLevelInPath) {

				//check if student has enough progress to purchase this one package
				String sql = "SELECT "
						+ "`minLevelToPurchase`, "
						+ "`maxLevelToPurchase` "
						+ "FROM "
						+ "products.upgrade_path_packages "
						+ "where "
						+ "`pathId`=? "
						+ "AND "
						+ "`packageFamilyId`=?";

				List<Map<String, Integer>> levelReqs = jdbcTemplate.query(sql, new Object[] { pathId, familyId }, new RowMapper<Map<String, Integer>>() {
				    @Override
				    public Map<String, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
				    	Map<String, Integer> levelReq = new HashMap<String, Integer>();
				    	levelReq.put("min", rs.getInt("minLevelToPurchase"));
				    	levelReq.put("max", rs.getInt("maxLevelToPurchase"));
				        return levelReq;
				    }
				});

				//because a package can be a part of multiple paths
				if(validationHelpers.checkIfNumberInRange(totalLevelInPath, levelReqs.get(0).get("min"), levelReqs.get(0).get("max"))) {
					return true;
				}else {
					return false;
				}
			}
		
		/*
		 * 	Get users total level in this path
		 */
			private int getTotalLevelInPath(String sapid, String pathId) {
				String sql = "SELECT "
						+ "`upp`.`pathId` AS `pathId`, "
						+ "`upp`.`packageFamilyId` AS `packageFamilyId`, "
						+ "`upp`.`validityAfterEndDate` AS `validityAfterEndDate`, "
						+ "`studentpackages`.`startDate` AS `startDate`, "
						+ "`studentpackages`.`endDate` AS `endDate`, "
						+ "`upp`.`levelValue`, "
						+ "`packs`.`durationMax` "
						+ "FROM "
						+ "`products`.`upgrade_path_packages` `upp` "
						+ "LEFT JOIN "
						+ "`products`.`packages` `packs` "
						+ "ON `packs`.`packageFamily`=`upp`.`packageFamilyId` "
						+ "LEFT JOIN "
						+ "`products`.`student_packages` `studentpackages` "
						+ "ON  `packs`.`salesForceUID`=`studentpackages`.`salesForceUID`"
						+ "WHERE "
						+ "`upp`.`pathId`=? AND "
						+ "`upp`.`packageFamilyId` IN"
							+ "( "
								+ "SELECT "
								+ "`p`.`packageFamily` "
								+ "FROM "
								+ "`products`.`packages` `p` "
								+ "LEFT JOIN "
								+ "`products`.`student_packages` `sp` "
								+ "ON `p`.`salesForceUID`=`sp`.`salesForceUID`"
								+ "WHERE `sp`.`sapid`=?"
							+ ")"
						+ "GROUP BY "
							//make sure no two same purchases increase the level +1
						+ "`packageFamilyId`";
	
				//get which paths this package is a part of
				List<String> progressList = jdbcTemplate.query(sql, new Object[] { pathId, sapid }, new RowMapper<String>() {
				    @Override
				    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				    	
				    	//each path also has a "valid for months after end date of feature" for the progress to count
				    	Date startDate = rs.getDate("startDate");
				    	Date endDate = rs.getDate("endDate");
				    	
				    	int validityAfterEndDate = rs.getInt("validityAfterEndDate");
				    	int durationMax = rs.getInt("durationMax");
			    		//Logic only used in test
			    		if(endDate == null) {
			    			Date startDateAddedMonths = validationHelpers.addMonthsToDate(startDate, durationMax);
			    			if(!validationHelpers.checkIfDateBeforeCurrent(validationHelpers.addMonthsToDate(startDateAddedMonths, validityAfterEndDate))) {
				    			return rs.getString("levelValue");
				    		}
			    		}else {
			    			if(!validationHelpers.checkIfDateBeforeCurrent(validationHelpers.addMonthsToDate(endDate, validityAfterEndDate))) {
				    			return rs.getString("levelValue");
				    		}
			    		}
			    		//
			    		
			    		return null;
				    }
				});
				
				int totalLevel = 0;
				
				for(String progress: progressList) {
					if(!validationHelpers.checkIfStringEmptyOrNull(progress)) {
						totalLevel += Integer.parseInt(progress);
					}
				}
				return totalLevel;
			}
		
		/*
		 * 	Get a list of the paths this package family is a part of
		 */
			private List<String> getPathIdsFromFamilyId(String packageFamily) {
				String sql = 
						"SELECT `pathId` FROM "
						+ "`products`.`upgrade_path_packages` "
						+ "WHERE "
						+ "packageFamilyId=?"
						+ " GROUP BY "
						+ "`pathId`";
				//get which paths this package is a part of
				List<String> pathIds = jdbcTemplate.query(sql, new Object[] { packageFamily }, new RowMapper<String>() {
				    @Override
				    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				        return rs.getString("pathId");
				    }
				});
				return pathIds;
			}
			
		/*
		 * 	overloads
		 */
			public boolean checkIfPackageApplicableForStudent(PackageBean packageToTest, String sapid) {
				//overloaded to require the sapid and package to test against. 
				return checkIfPackageApplicableForStudent(packageToTest, getStudentCourseDetails(sapid));
			}
			public boolean checkIfPackageApplicableForStudent(String packageId, String sapid) {
				//overloaded to require the sapid and package to test against. 
				PackageBean packageBean = getPackageFromId(packageId);
				if(packageBean == null) {
					return false;
				}
				StudentCourseDetailsBean studentDetails = getStudentCourseDetails(sapid);
				if(studentDetails == null) {
					return false;
				}
				PackageRequirements packageRequirements = getRequirementsForPackageIdAndConsumerTypeId(packageId, studentDetails.getConsumerProgramStructureId());
				if(packageRequirements == null) {
					return false;
				}
				packageBean.setPackageRequirements(packageRequirements);
				return checkIfPackageApplicableForStudent(packageBean, studentDetails);
			}
			public PackageBean getPackageFromId(String packageId) {

				String sql = "SELECT "
						+ "* "
						+ "FROM "
						+ "`products`.`packages` `p`"
						+ "LEFT JOIN "
						+ "`products`.`package_families` `pf`"
						+ "ON "
						+ "`pf`.`familyId` = `p`.`packageFamily` "
						+ "WHERE "
						+ "`packageId` = ?";
				
				try {
					PackageBean packageBean = jdbcTemplate.queryForObject(
							sql, 
							new Object[] { packageId },
							new BeanPropertyRowMapper<PackageBean>(PackageBean.class));

					return packageBean;
				}catch (Exception e) {
					logger.info("exception : "+e.getMessage());
				}
				return null;
			}
	
	/*
	 * ---------------------------- END ---------------------------------
	 */		
/*
 * 	------------------------ END --------------------------------
 */
		
}
