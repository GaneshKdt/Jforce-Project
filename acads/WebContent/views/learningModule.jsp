

<!DOCTYPE html>
<%@page import="com.nmims.beans.ModuleContentAcadsBean"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
    
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
	
    
    <jsp:include page="../views/common/jscss.jsp">
	<jsp:param value="Learning Resources" name="title"/>
    </jsp:include>
    <link href="https://unpkg.com/nprogress@0.2.0/nprogress.css" rel="stylesheet" />
    <style type="text/css">
     
    .panel-title {
        font-size:18px;
        font-weight:800;
        color:#000;
    }
    
    .bs-example{
    	margin: 20px;
    }
    .panel-title .glyphicon{
        font-size: 14px;
    }
	.panel-heading {
    	padding : 20px 20px 20px 20px;
    }

    .glyphicon {
        float:right;
    }

    .panel-group .panel {
        border-left-width: 4px;
    }


.tile-progress {

color: #fff;
margin-bottom: 20px;
-webkit-border-radius: 5px;
-moz-border-radius: 5px;
border-radius: 5px;
-webkit-background-clip: padding-box;
-moz-background-clip: padding;
background-clip: padding-box;
-webkit-border-radius: 3px;
-moz-border-radius: 3px;
border-radius: 3px;
}
.tile-progress .tile-header {
    padding: 20px 20px 20px 20px;
background-color: #0288D1;
    border-top-left-radius: 7px;
    border-top-right-radius: 7px;
}


.tile-progress .tile-progressbar {
height: 5px;
background: rgba(0,0,0,0.18);
margin: 0;
}
.tile-progress .tile-progressbar span {
background: #fff;
}
.tile-progress .tile-progressbar span {
display: block;
background: #e25050;
width: 0;
height: 100%;
-webkit-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
-moz-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
-o-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
}
.tile-progress .tile-footer {
padding: 20px;
text-align: left;
background:white;
color:black;
-webkit-border-radius: 0 0 3px 3px;
-webkit-background-clip: padding-box;
-moz-border-radius: 0 0 3px 3px;
-moz-background-clip: padding;
border-radius: 0 0 3px 3px;
background-clip: padding-box;
-webkit-border-radius: 0 0 3px 3px;
-moz-border-radius: 0 0 3px 3px;
border-radius: 0 0 3px 3px;
height:200px;
}
.tile-progress.tile-red {
background-color: #f56954;
color: #fff;
}

.tile-progress.tile-blue {
background-color: #0073b7;
color: #fff;
}
.tile-progress.tile-aqua {
background-color: #00c0ef;
color: #fff;
}
.tile-progress.tile-green {
background-color: #00a65a;
color: #fff;
}
.tile-progress.tile-cyan {
background-color: #00b29e;
color: #fff;
}
.tile-progress.tile-purple {
background-color: #ba79cb;
color: #fff;
}
.tile-progress.tile-pink {
background-color: #ec3b83;
color: #fff;
}
.collapse {
    background-color: #eeeeee;

} 

h3 {
    color:#000000;
    float:left;
    font-size:0.9rem;
}

.tile-header span {
    font-size:1rem;
    float:left;
}   
.fond{position:absolute;padding-top:85px;top:0;left:0; right:0;bottom:0;
 background-color:#fbfeff;}

#style_prevu_kit
{
    position: relative;
    -webkit-transition: top 200ms ease-in;
    -webkit-transform: scale(1); 
    -ms-transition: all 200ms ease-in;
    -ms-transform: scale(1); 
    -moz-transition: all 200ms ease-in;
    -moz-transform: scale(1);
    transition: all 200ms ease-in;
    transform: scale(1);   

}
#style_prevu_kit:hover
{
    box-shadow: 0px 0px 50px #ffccbc  ;
    z-index: 2;
    -webkit-transition: all 200ms ease-in;
    -webkit-transform: scale(1);
    -ms-transition: all 200ms ease-in;
    -ms-transform: scale(1);   
    -moz-transition: all 200ms ease-in;
    -moz-transform: scale(1);
    transition: all 200ms ease-in;
    transform: scale(1);
}    


/* new code */
.panel-group .panel {
        border-radius: 0;
        box-shadow: none;
        border-color: #d7ccc8;
    }

    .panel-default > .panel-heading {
        padding: 0;
        border-radius: 0;
        color: #212121;
        background-color: #ffffff ;
        border-color: #EEEEEE;
    }

    .panel-title {
        font-size: 14px;
    }

    .panel-title > a {
        display: block;
        padding: 15px;
        text-decoration: none;
    }

    .more-less {
        float: right;
        color: #212121;
    }

    .panel-default > .panel-heading + .panel-collapse > .panel-body {
        border-top-color: #EEEEEE;
    }

    .button {
    float:right;
 border-radius: 4px;
    background-color: darkgray;
    border: none;
    color: #0e0e0e;
    text-align: center;
    font-size: 16;
    padding: 0px;
    width: 30%;
    transition: all 0.5s;
    cursor: pointer;
    margin: 0px;
    height:24px;
    }

.button span {
  cursor: pointer;
  display: inline-block;
  position: relative;
  transition: 0.5s;
}

.button span:after {
  content: '\00bb';
  position: absolute;
  opacity: 0;
  top: -20px;
  font-size:20px;
  right: 20px;
  transition: 0.5s;
}

.button:hover span {
  padding-right: 25px;
}

.button:hover span:after {
  opacity: 1;
  right: 0;
  color:#000;
}
<
</style>
    
    <body>
    
    	<%@ include file="../views/common/header.jsp" %>
        <div class="sz-main-content-wrapper">
        	<jsp:include page="../views/common/breadcrum.jsp">
			<jsp:param value="Student Zone;Learning Resources" name="breadcrumItems"/>
			</jsp:include>
			
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../views/common/left-sidebar.jsp">
								<jsp:param value="Service Request" name="activeMenu"/>
							</jsp:include>
							
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../views/common/studentInfoBar.jsp" %>
              				  <form:form  action="moduleLibraryList" method="post" modelAttribute="moduleContentBean">	
              				  	
              					<div class="sz-content">
		<div class="panel-content-wrapper" style="background-color:#fff;margin-top:20px;">
			 <c:forEach var="subject" items="${listOfContent}" varStatus="status" >
	 <div class="panel-group" id="accordion${status.count}" role="tablist" aria-multiselectable="true">
        <div class="panel panel-default" id="myDiv" style="border-radius:10px;">
            <div class="panel-heading" role="tab" id="headingOne${status.count}">
                <h4 class="panel-title">
                    <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne${status.count}" aria-expanded="true" aria-controls="collapseOne">
                        <i class="more-less glyphicon glyphicon-menu-down"></i>
                       ${subject.key}
                    </a>
                </h4>
            </div>
            <div id="collapseOne${status.count}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
                <div class="panel-body" style="background-color:#fdf8f8;">
                <div class="container">
                                <div class="row">
                                 <c:forEach var="chapter" items="${subject.value}" varStatus="status" >
								<c:if test="${subject.key == chapter.subject}">
                 <div class="col-sm-3" >
                                        <a href="/acads/student/moduleLibraryList?moduleId=${chapter.id}" >
                                        <div id="style_prevu_kit" class="tile-progress tile-primary" onClick="goToDetails(${chapter.id})" style="cursor:pointer !important;">
                                            <div class="tile-header text-capitalize" id="tileHeader" style="min-height:100px !important; color:#000;">  ${chapter.moduleName} <br>   ${chapter.title}                                  </div>
                                            <div class="tile-footer">
                                                <p style="float:left;margin-bottom:40px;">${chapter.description}</p>
 														
 														<div class="statistics">
 														
 														
 														<div class="col-sm-8" style="padding:0px">
 														
 														<p>Overall Progress :  ${chapter.percentageCombined}%  </p>
 													
 														</div>
 														
 													 <div class="col-sm-12 progress" style="height: 5px;padding:0px;">
   				 <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:${chapter.percentageCombined}% ;
   				 background-color:#3c763d;">
      40%
    </div>
  </div>
 														</div>
 												
 												
 												</div>
                                 </div>    
                            </a>        </div>
</c:if>
                                            
                                                            
                                  </c:forEach>
                                </div>
                                
                            </div>
                </div>
            </div>
        </div>
    </div>
   </c:forEach>
</div>
</div></form:form>
 </div>
					</div>
            </div>
        </div>
       
       <div>
    </div>
        <jsp:include page="../views/common/footer.jsp"/>
    </body>
   <script>
function toggleIcon(e) {
    $(e.target)
        .prev('.panel-heading')
        .find(".more-less")
        .toggleClass('glyphicon glyphicon-menu-down glyphicon glyphicon-menu-up');
}
$('.panel-group').on('hidden.bs.collapse', toggleIcon);
$('.panel-group').on('shown.bs.collapse', toggleIcon);

var myColors = ['red', 'blue', 'yellow' , 'green' , 'purple' , 'orange'];
var i = 0;
$('div#myDiv').each(function() {
     $(this).css('border-left-color', myColors[i]);
      i = (i + 1) % myColors.length;
      });

</script>
    <script>
    $(document).ready(function(){
         var tileHeaderColors = ['#ffcdd2','#bbdefb','#b2dfdb',
        	 '#b2ebf2','#f0f4c3','#b3e5fc','#ffecb3 ','#e1bee7','#d7ccc8'];
         var j=0;
         $('div#tileHeader').each(function(){
        	$(this).css('background-color',tileHeaderColors[j]); 
        	j=(j+1) % tileHeaderColors.length; 
         })
    });
    function goToDetails(id){
    	console.log("test");
     	var colorId =  $('div#tileHeader').css('backgroundColor');
     	console.log('color Id '+colorId);
    	window.location.replace("/acads/student/moduleLibraryList?moduleId="+id+"&colorId="+colorId);
    	
    }
</script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/progress.js"></script> 



</html>