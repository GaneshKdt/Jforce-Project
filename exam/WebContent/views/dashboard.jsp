<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
   
    <jsp:include page="jscss.jsp">
	<jsp:param value="Welcome to Exam Data Management" name="title" />
	</jsp:include>
	
    <body class="inside">
	
    <%@ include file="header.jsp"%>
    <%
    
    Person p = (Person)session.getAttribute("user");
    
    String firstName = "";
	String lastName = "";
	String displayName = "";
	String email = "";
	String lastLogon = "";
    
    if(p != null){
    	displayName = p.getDisplayName();
    	lastLogon = p.getLastLogon();
    }
    %>
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row"><legend>Dashboard</legend></div> 
           
          <%@ include file="messages.jsp" %>
          <div class="panel-body"> 
          
          	<div class="row">
          	<form:form action="dashboard" method="post" modelAttribute="student" name="dashboardForm">
          	
	          	<div class="col-md-2">
	          		<div class="form-group">
	          			<label class="control-label" for="year">Year</label>
						<form:select id="year" path="year" class="form-control"   itemValue="${student.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				</div>		
				
				<div class="col-md-2">
	          		<div class="form-group">
	          			<label class="control-label" for="month">Month</label>
						<form:select id="month" path="month" class="form-control"   itemValue="${student.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
				</div>	
          	
          	
          		<div class="col-md-2">
          			<label class="control-label" for="lc">LC</label>
		          	<form:select id="lc" path="lc" >
						<form:option value="">All LCs</form:option>          	
						<form:option value="Mumbai">Mumbai</form:option>
						<form:option value="Delhi">Delhi</form:option>
						<form:option value="Bangalore">Bangalore</form:option>
						<form:option value="Pune">Pune</form:option>
						<form:option value="Kolkata">Kolkata</form:option>
						<form:option value="Hyderabad">Hyderabad</form:option>
						<form:option value="Ahmedabad">Ahmedabad</form:option>
		          	</form:select>
	          	</div>
	          	
	          	<div class="col-md-3">
					<label class="control-label" for="centerCode">IC</label>
					<form:select id="centerCode" path="centerCode"  class="form-control"   >
						<form:option value="">All ICs</form:option>
						<form:options items="${centerList}" />
					</form:select>
				</div>
				
				
				<div class="col-md-2">
					<label class="control-label" for="centerCode">Program</label>
					<form:select id="program" path="program"  class="form-control" >
						<form:option value="">Select Program</form:option>
						<form:options items="${programList}" />
					</form:select>
				</div>
				
				<div class="col-md-2">
					<label class="control-label" for="sem">Sem</label>
						<form:select id="sem" path="sem" class="form-control"  >
						<form:option value="">Select Semester</form:option>
						<form:option value="1">1</form:option>
						<form:option value="2">2</form:option>
						<form:option value="3">3</form:option>
						<form:option value="4">4</form:option>
					</form:select>
				</div>
				
				<div class="col-md-4" style="overflow:visible;">
					<label class="control-label" for="subject">Subject</label>
					<form:select id="subject" path="subject" class="combobox form-control"  >
						<form:option value="">Type OR Select Subject</form:option>
						<form:options items="${subjectList}" />
					</form:select>
				</div>
	          	
	          	
	          	<div class="col-md-1 form-group">
	          		<a href="#" onclick="document.dashboardForm.submit();"><i class="fa-solid fa-rotate" aria-hidden="true"></i></a>
				</div>
          	</form:form>
          </div>
          
          
          <hr/>
          
          	<div class="row">
          		<div class="col-md-6">
			 		<div id="marksWiseStudents"></div> 
			 	</div>
			 	
			 	<div class="col-md-6">
			 		<div id="passFailStudents"></div> 
			 	</div>
        	</div>
          </div>
    </section>
    
    
    <jsp:include page="footer.jsp" />
    
    
    <script type="text/javascript" src="http://static.fusioncharts.com/code/latest/fusioncharts.js"></script>
<script type="text/javascript" src="http://static.fusioncharts.com/code/latest/themes/fusioncharts.theme.fint.js?cacheBust=56"></script>
<script type="text/javascript">
  FusionCharts.ready(function(){
	 var marksWiseStudentsChart = new FusionCharts({
		    type: 'column2d',
		    renderAt: 'marksWiseStudents',
		    width: '450',
		    height: '300',
		    dataFormat: 'json',
		    dataSource: {
		        "chart": {
		            "caption": "No of student-subjects in marks bucket",
		            //"paletteColors": "#DD2726",
		            "yaxisname" : "# of Student-Subjects",
		            "bgColor": "#ffffff",
		            "borderAlpha": "50",
		            "canvasBorderAlpha": "0",
		            "usePlotGradientColor": "0",
		            "plotBorderAlpha": "0",
		            "showBorder" : "0",
		            "placevaluesInside": "1",
		            "rotatevalues": "1",
		            "valueFontColor": "#FFFFFF",     
		            "baseFont": "Arial, Sans-Serif",       
		            "valueFontSize": "12",
		            "showXAxisLine": "1",
		            "xAxisLineColor": "#999999",
		            "divlineColor": "#999999",               
		            "divLineIsDashed": "1",
		            "showAlternateHGridColor": "0",
		            "toolTipColor": "#ffffff",
		            "toolTipBorderThickness": "0",
		            "toolTipBgColor": "#424243",
		            "toolTipBgAlpha": "100",
		            "toolTipBorderRadius": "2",
		            "toolTipPadding": "5",
		            "legendBgColor": "#ffffff",
		            "legendBorderAlpha": '0',
		            "legendShadow": '0',
		            "legendItemFontSize": '12',
		            "legendItemFontColor": '#666666'
		        },
		        "data": <%=(String)request.getAttribute("NoOfStudentsByMarksData")%>
		    }
		}
	);
    marksWiseStudentsChart.render();
    
    var passFailStudentsChart = new FusionCharts({
		    type: 'pie2d',
		    renderAt: 'passFailStudents',
		    width: '450',
		    height: '300',
		    dataFormat: 'json',
		    dataSource: {
		        "chart": {
		            "caption": "Pass/Fail Subjects",
		           // "subCaption": "Last year",
		            "showPercentInTooltip": "1",
		            "decimals": "1",
		            "useDataPlotColorForLabels": "1",
		            "showLegend": "1",
		            "paletteColors": "#009933,#ff3300",
		            //Theme
		            "theme": "fint"
		        },
		        "data": <%=(String)request.getAttribute("passFailPercentData")%>
		    }
		}
	);
    passFailStudentsChart.render();
    
    
    
    
    
    
    
});
</script>
  </body>
</html>
