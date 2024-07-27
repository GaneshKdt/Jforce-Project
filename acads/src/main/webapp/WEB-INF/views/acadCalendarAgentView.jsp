
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<html class="no-js"> <!--<![endif]-->
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeBean"%>

<%
ArrayList<SessionDayTimeBean> scheduledSessionList = (ArrayList<SessionDayTimeBean>)request.getAttribute("scheduledSessionList");
%>

<jsp:include page="jscss.jsp">
<jsp:param value="Sessions Calendar" name="title" />
</jsp:include>
	   

<style>
	#calendar{
		width: 100%;
		margin: 0 auto;
	}

</style>

<body class="inside">

	<%@ include file="header.jsp"%>
	<section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Academic Calendar </legend></div>
        <h3>Click on Subject name to view details and attend session</h3>
        <div id="acadLegend"></div>
        <%@ include file="messages.jsp"%>
        
        <div class="row">
        
        <div class="column col-md-10">
        <div id='calendar' align="left"></div><br>
        </div>
        
        <div class="column col-md-8">
        <iframe id="sessionFrame" src="" width="100%" seamless="seamless" height="600" frameborder="0"></iframe>
        </div>
        
        </div>
        
		
	
		<br>
	</div>
	</section>
     
      
      <script>


function detectIE() {
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
       // IE 12 => return version number
       return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }

    // other browser
    return false;
}


flag = detectIE();

if(flag != false){
	alert('You can not view Calendar using Internet Explorer. Please switch to Chrome/Firefox/Safari');
	document.getElementById("acadLegend").innerHTML = "Academic Calendar not visible on IE. Please switch to Chrome/Firefox/Safari to view Academic Calendar";
	
}



</script>
      
 <jsp:include page="footer.jsp" />
 
 <%
 Date dt = new Date();
 
 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
 String today = sdf.format(dt);
 %>
 <script>

	$(document).ready(function() {
		
		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			defaultDate: '<%=today%>',
			editable: false,
			eventLimit: true, // allow "more" link when too many events
			events: [
			     <% for(int i = 0 ; i < scheduledSessionList.size(); i++){
			    	 SessionDayTimeBean bean = scheduledSessionList.get(i);
			     %>    
			         
			         
				{
					title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
					start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
					url: 'viewScheduledSession?id=<%=bean.getId()%>',
				},
				
				<%}%>
			],
			eventClick: function(event) {
		        if (event.url) {
		        	document.getElementById("sessionFrame").src=event.url;
		            //window.open(event.url);
		            return false;
		        }
		    }
		});
		
	});

	
	
</script>
   
</body>
</html>
