
Admin Side Development:

UI Part -

Step1. Input form for Admin
Admin will select year , month and etc  then he will click the generate report button.




Step2.Show Result in Data table View  and click on download in excel button



Step2.Excel Report downloaded 

 Won Date, Student No, Student Name, Program Name, Semester, and Subject Name will be the columns in the downloaded Excel report







































Coding Part -

1> create new api to process the user request

	1.create API to load a form page
		API = /admin/getWaivedInSubjectsReportForm, mode=GET  
modelAttribute = StudentStudentPortalBean bean
Acad year drop down = ACAD_YEAR_LIST 
Acad month drop down = ACAD_MONTH_LIST 
Submit button on click = /admin/getWaivedInSubjectsReport
	
	2.API to show extra subject data on the ui.
api=/admin/getWaivedInSubjectsReport, mode=POST , 

Method call  = getWaiwedinSubjectsReport() 
param= bean,getAcadMonth(), bean.getYear()
Result = List<StudentStudentPortalBean> extraStudentSubjectList

Return = ModelAndView (“waivedinSubjectsReport”)

3.API to download extra subject excel report.
api=/admin/getWaivedInSubjectsInExcelReport, mode=POST , 

Method call  = getWaiwedinSubjectsReport() 
param= bean,getAcadMonth(), bean.getYear()
Result = List<StudentStudentPortalBean> extraStudentSubjectList

Return = ModelAndView (StudentWaivedinExcelReportView,” StudentPortalBean “,extraStudentSubjectList)

		










	

1> Create a method in StudentService>>getWaivedinSubjectsReport ():
Parameters:
Final String acadMonth
Final String acadYear 

Perform following tasks:
We find a list of students which we want to generate a waivedin report based on user input acadyear and acadmonth.For that we will do dao call in that dao call query will be,
select * from exam.students where isLateral="Y" and enrollmentMonth=? and enrollmentYear=?; 

Iterate all sapids from the above list and process them using below steps.
Call the existing method for each sapid of student “ArrayList<String> mgetWaivedInSubjects(StudentStudentPortalBean student)” to get the waivedin subjects for each student and save that in list of StudentStudentPortalBean.
And return the saved list.

Return:
List<StudentStudentPortalBean> it will return a list of StudentStudentPortalBean in that each bean contains a list of waivedInSubjects , student sapid,student name,program name,semester.
2> Create a class StudentWaivedinExcelReportView>>bulidExcelDocunemt ():
Perform following tasks:
Generate excel sheet  using List<StudentStudentPortalBean> StudentPortalBean list
We will discuss on this for which format we need to print data in excel.
Parameters:
 Map model
 HSSFWorkbook workbook
 HttpServletRequest request
 HttpServletResponse response
Return:
Void
    



















