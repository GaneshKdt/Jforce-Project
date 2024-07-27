<%-- <!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">
    
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
    <jsp:include page="../views/common/jscss.jsp">
	<jsp:param value="Learning Resources" name="title"/>
    </jsp:include>
    
    <style type="text/css">
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
 
</style>
    
    <body>
    
    	<%@ include file="../views/common/header.jsp" %>
        <div class="sz-main-content-wrapper">
        	<jsp:include page="../views/common/breadcrum.jsp">
			<jsp:param value="Student Zone;Student Support;Service Request" name="breadcrumItems"/>
			</jsp:include>
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../views/common/left-sidebar.jsp">
								<jsp:param value="Service Request" name="activeMenu"/>
							</jsp:include>
							
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../views/common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Learning Resources</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../views/common/messages.jsp" %>
											
												 <c:forEach var="subjects" items="${getSubjectList}" varStatus="status" >
    <div class="panel-group" id="accordion${status.count}">
        <div class="panel panel-default" id="myDiv" style="border-radius:10px;">
            <div class="panel-heading">
                <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne${status.count}"><span class="glyphicon glyphicon-menu-down"></span> ${subjects}</a>
                </h4>
            </div>
            <div id="collapseOne${status.count}" class="panel-collapse collapse">
                <div class="panel-body">
                 <div class="ui-105-content${status.count }">

            <ul class="nav nav-tabs nav-justified">

                  <li class="link-one" data-tab="tab-1"><a href="#login-block${status.count }" data-toggle="tab"><i class="fa fa-inbox" ></i>DOCS</a></li>

                  <li class="link-two" data-tab="tab-2" id="link2"><a href="#register-block${status.count}" data-toggle="tab"><i class="fa fa-sign-in"></i>VIDEOS</a></li>

            </ul>

                       <div class="tab-content"  >

                  <div class="tab-pane active fade in" id="login-block${status.count }" >

                        <!-- Login Block Form -->

                        <div class="login-block-form" id="tab-1">

                        <form:form cssClass="form" role="form" action=""

                                    method="post" modelAttribute="contentBean" id="">

                                          <div class="table-responsive">

                                    <table id="inboxTable" class="table table-striped table-hover"

                                          style="font-size: 12px">

                                          <thead>
                                                <tr>
                                                      <th>Sr. No.</th>
                                                      <th>Content</th>
                                                      <th>Actions</th>
                                                </tr>

                                          </thead>

                                          <tbody>
	 <c:if test = "${empty getContentList}">
	 
	 <tr> NA </tr>
	 </c:if>
	  <c:if test = "${not empty getContentList}">
                                         <c:forEach var="contentFile" items="${getContentList}" varStatus="status">
                                      
							       <tr>
							            <td ><c:out value="${status.count}"/></td>
							            <td><c:out value="${contentFile.filePath}"></c:out></td>
										 <td width="20%"><c:out value="${contentFile.name}"/></td>
										<td width="20%"><c:out value="${contentFile.description}"/></td>
										<td >
										<c:if test="${not empty contentFile.filePath}">
										    <a href="downloadFile?filePath=${contentFile.filePath}">Download</a>
										</c:if> 
										
										<c:if test="${empty contentFile.filePath }">
										NA
										</c:if>
										<c:if test="${fn:endsWith(contentFile.filePath, '.pdf') || fn:endsWith(contentFile.filePath, '.PDF')
										|| fn:endsWith(contentFile.filePath, '.Pdf')}">
										    / <a href="previewContent?filePath=file:///${contentFile.filePath}&name=${contentFile.name}" target="_blank">View</a>
										</c:if>
										
											
										</td>
										
									        
							        </tr>   
							    
							    </c:forEach>
</c:if>
                                          </tbody>

                                    </table>

                              </div>

                              </form:form>

                                    </div>

                                </div>

                                    <!-- Results Panel -->

            <div class="tab-pane fade" id="register-block ${status.count}">

                        <!-- Login Block Form -->

                        <div class="register-block-form" id="tab-2">

                        <form:form cssClass="form" role="form" action=""

                                    method="post" modelAttribute="" id="">

                                          <div class="table-responsive">

                                    <table id="outboxTable" class="table table-striped table-hover"

                                          style="font-size: 12px">

                                          <thead>

                                                <tr>

                                                      <th>Sr. No.</th>

                                                      <th>Actions</th>

                                                </tr>

                                          </thead>

                                          <tbody>

                                            

                                          </tbody>

                                    </table>

                              </div>

                              </form:form>

                                    </div>

                                </div>

                                    </div>

                                </div>
                </div>
            </div>
        </div>
       
   
    </div></c:forEach>
											
										</div>
										
										
										
										
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
        <embed align="middle" src="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PATH')" /><%=request.getParameter("filePath") %>" width = "100%" height="500px">  
       <img src="${destinationFolder}"/>
       <div>
        <input type="button" value="Preview" onclick="PreviewImage();" >
        <embed src = "file://E:/one.pdf" width = "250" height = "100" />
        <div style="clear:both">
           <iframe id="viewer" frameborder="0" scrolling="no" width="400" height="600"></iframe>
        </div>
    </div>
         
        <jsp:include page="../views/common/footer.jsp"/>
            
		
    </body>
    <script>
     function PreviewImage() {
               
               pdffile_url="E:/one.pdf"
               $('#viewer').attr('src',pdffile_url);
           }
    </script>
    <script>
    $(document).ready(function(){
       
        // Add minus icon for collapse element which is open by default
        $(".collapse.in").each(function(){
        	$(this).siblings(".panel-heading").find(".glyphicon").addClass("glyphicon-minus").removeClass("glyphicon-plus");
        });
        
        // Toggle plus minus icon on show hide of collapse element
        $(".collapse").on('show.bs.collapse', function(){
        	$(this).parent().find(".glyphicon").removeClass("glyphicon-plus").addClass("glyphicon-minus");
        }).on('hide.bs.collapse', function(){
        	$(this).parent().find(".glyphicon").removeClass("glyphicon-minus").addClass("glyphicon-plus");
        });
        
        var myColors = [
                        'red', 'blue', 'yellow' , 'green' , 'purple' , 'orange'
                    ];
                    var i = 0;
                    
                    
                    $('div#myDiv').each(function() {
                        $(this).css('border-left-color', myColors[i]);
                        i = (i + 1) % myColors.length;
                    });
                    
                    $('ul.nav li').click(function(){
                        var tab_id = $(this).attr('data-tab');

                        if(tab_id=="tab-2")
                        {
                        console.log('called');
                        $("#link2").click(function(){
                        $("#tab-1").hide();
                      
                    });
                        }    else{
                         $("#tab-1").show();
                        }
                      })
    });
</script>
</html> --%>
<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">
    
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
	
    
    <jsp:include page="../views/common/jscss.jsp">
	<jsp:param value="Learning Resources" name="title"/>
    </jsp:include>
    
    <style type="text/css">
       .panel-default>.panel-heading {
    background: linear-gradient(to top right, #ecaeae  0%, #ff4534  100%);
    border-radius:10px;
}
    .panel-title {
        font-size:18px;
        font-weight:800;
        color:#fff;
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
background-color: #fff;
color: #fff;
}
.tile-progress {
/* background: #00a65b; */
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
    padding: 30px 20px 80px 20px;
background-color: cornflowerblue;
background-color: #e25050;
   
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
    background-color: whitesmoke;

} 

h3 {
    color:#b1acac;
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
    
    box-shadow: 0px 0px 150px #000000;
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
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Learning Resources</h2>
										
		              					<div class="panel-content-wrapper">
											<%@ include file="../views/common/messages.jsp" %>
											
												 <c:forEach var="subjects" items="${getSubjectList}" varStatus="status" >
    <div class="panel-group" id="accordion${status.count}">
        <div class="panel panel-default" id="myDiv" style="border-radius:10px;">
            <div class="panel-heading">
                <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne${status.count}"><span class="glyphicon glyphicon-menu-down"></span> ${subjects}</a>
                </h4>
            </div>
            <div id="collapseOne${status.count}" class="panel-collapse collapse">
                <div class="panel-body">
                                                        
                            
                        <div class="container">
                                <div class="row">
                                    <div class="col-sm-3" id="style_prevu_kit">
                                        <a href="/acads/tabs">
                                        <div class="tile-progress tile-primary">
                                            <div class="tile-header" style="background-image: linear-gradient(rgb(152, 139, 213), rgb(104, 91, 175));">
                                                <h3>Chapter 1</h3>
                                                <span>Defining Professionalism</span>
                                            </div>
                                            
                                            <div class="tile-footer">
                                               
                                                <p style="float:left;margin-bottom:40px;">Gain insights from industry leaders about professionalism.</p>

                                                <div class="date">
                                                    <p style="font-weight:700;margin-bottom:2px;">Due Date</p>
                                                    <p>6th Jan 2018</p>
                                                </div>
                                                <p style="font-weight:700;">Progress</p>
                                                <div class="tile-progressbar">
                                                    
                                                        <span data-fill="65.5%" style="width: 65.5%;"></span> 65.5%
                                                    </div>
                                            </div>
                                        </div></a>
                                    </div>

                                    <div class="col-sm-3"  id="style_prevu_kit">
                                            <div class="tile-progress tile-primary">
                                                <div class="tile-header" style=" background-image: linear-gradient(rgb(168, 116, 111), rgb(119, 67, 63));">
                                                    <h3>Chapter 1</h3>
                                                    <span>Defining Professionalism</span>
                                                </div>
                                                
                                                <div class="tile-footer">     
                                                    <p style="float:left;margin-bottom:40px;">Gain insights from industry leaders about professionalism.</p>
                                                    <div class="date">
                                                        <p style="font-weight:700;margin-bottom:2px;">Due Date</p>
                                                        <p>6th Jan 2018</p>
                                                    </div>
                                                    <p style="font-weight:700;">Progress</p>
                                                    <div class="tile-progressbar">
                                                        
                                                            <span data-fill="65.5%" style="width: 65.5%;"></span> 65.5%
                                                        </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-sm-3"  id="style_prevu_kit">
                                                <div class="tile-progress tile-primary">
                                                    <div class="tile-header" style="background-image: linear-gradient(rgb(149, 200, 160), rgb(90, 161, 102));">
                                                        <h3>Chapter 1</h3>
                                                        <span>Defining Professionalism</span>
                                                    </div>
                                                    
                                                    <div class="tile-footer">
                                                       
                                                        <p style="float:left;margin-bottom:40px;">Gain insights from industry leaders about professionalism.</p>
        
                                                        <div class="date">
                                                            <p style="font-weight:700;margin-bottom:2px;">Due Date</p>
                                                            <p>6th Jan 2018</p>
                                                        </div>
                                                        <p style="font-weight:700;">Progress</p>
                                                        <div class="tile-progressbar">
                                                            
                                                                <span data-fill="65.5%" style="width: 65.5%;"></span> 65.5%
                                                            </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="col-sm-3">
                                                    <div class="tile-progress tile-primary">
                                                        <div class="tile-header" style="    background-image: linear-gradient(rgb(215, 113, 143), rgb(179, 68, 92));">
                                                            <h3>Chapter 1</h3>
                                                            <span>Defining Professionalism</span>
                                                        </div>
                                                        
                                                        <div class="tile-footer">
                                                           
                                                            <p style="float:left;margin-bottom:40px;">Gain insights from industry leaders about professionalism.</p>
            
                                                            <div class="date">
                                                                <p style="font-weight:700;margin-bottom:2px;">Due Date</p>
                                                                <p>6th Jan 2018</p>
                                                            </div>
                                                            <p style="font-weight:700;">Progress</p>
                                                            <div class="tile-progressbar">
                                                                
                                                                    <span data-fill="65.5%" style="width: 65.5%;"></span> 65.5%
                                                                </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="col-sm-3">
                                                        <div class="tile-progress tile-primary">
                                                            <div class="tile-header" style=" background-image: linear-gradient(rgb(251, 183, 131), rgb(228, 126, 74));">
                                                                <h3>Chapter 1</h3>
                                                                <span>Defining Professionalism</span>
                                                            </div>
                                                            
                                                            <div class="tile-footer">
                                                               
                                                                <p style="float:left;margin-bottom:40px;">Gain insights from industry leaders about professionalism.</p>
                
                                                                <div class="date">
                                                                    <p style="font-weight:700;margin-bottom:2px;">Due Date</p>
                                                                    <p>6th Jan 2018</p>
                                                                </div>
                                                                <p style="font-weight:700;">Progress</p>
                                                                <div class="tile-progressbar">
                                                                    
                                                                        <span data-fill="65.5%" style="width: 65.5%;"></span> 65.5%
                                                                    </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="col-sm-3">
                                                            <div class="tile-progress tile-primary">
                                                                <div class="tile-header" style="background-image: linear-gradient(rgb(140, 202, 209), rgb(84, 170, 183));">
                                                                    <h3>Chapter 1</h3>
                                                                    <span>Defining Professionalism</span>
                                                                </div>
                                                                
                                                                <div class="tile-footer">
                                                                   
                                                                    <p style="float:left;margin-bottom:40px;">Gain insights from industry leaders about professionalism.</p>
                    
                                                                    <div class="date">
                                                                        <p style="font-weight:700;margin-bottom:2px;">Due Date</p>
                                                                        <p>6th Jan 2018</p>
                                                                    </div>
                                                                    <p style="font-weight:700;">Progress</p>
                                                                    <div class="tile-progressbar">
                                                                        
                                                                            <span data-fill="65.5%" style="width: 65.5%;"></span> 65.5%
                                                                        </div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="col-sm-3">
                                                                <div class="tile-progress tile-primary">
                                                                    <div class="tile-header" style="background-image: linear-gradient(rgb(125, 185, 228), rgb(71, 131, 196));">
                                                                        <h3>Chapter 1</h3>
                                                                        <span>Defining Professionalism</span>
                                                                    </div>
                                                                    
                                                                    <div class="tile-footer">
                                                                       
                                                                        <p style="float:left;margin-bottom:40px;">Gain insights from industry leaders about professionalism.</p>
                        
                                                                        <div class="date">
                                                                            <p style="font-weight:700;margin-bottom:2px;">Due Date</p>
                                                                            <p>6th Jan 2018</p>
                                                                        </div>
                                                                        <p style="font-weight:700;">Progress</p>
                                                                        <div class="tile-progressbar">
                                                                            
                                                                                <span data-fill="65.5%" style="width: 65.5%;"></span> 65.5%
                                                                            </div>
                                                                    </div>
                                                                </div>
                                                            </div>

                                                            <div class="col-sm-3">
                                                                    <div class="tile-progress tile-primary">
                                                                        <div class="tile-header" style=" background-image: linear-gradient(rgb(95, 178, 172), rgb(49, 122, 116));">
                                                                            <h3>Chapter 1</h3>
                                                                            <span>Defining Professionalism</span>
                                                                        </div>
                                                                        
                                                                        <div class="tile-footer">
                                                                           
                                                                            <p style="float:left;margin-bottom:40px;">Gain insights from industry leaders about professionalism.</p>
                            
                                                                            <div class="date">
                                                                                <p style="font-weight:700;margin-bottom:2px;">Due Date</p>
                                                                                <p>6th Jan 2018</p>
                                                                            </div>
                                                                            <p style="font-weight:700;">Progress</p>
                                                                            <div class="tile-progressbar">
                                                                                
                                                                                    <span data-fill="65.5%" style="width: 65.5%;"></span> 65.5%
                                                                                </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                  
                                </div>
                            </div>
                               
                                                    
                        
                 
                </div>
            </div>
        </div>
       
   
    </div></c:forEach>
											
										</div>
										
										
										
										
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
       
       <div>
      
    </div>
         
        <jsp:include page="../views/common/footer.jsp"/>
            
		
    </body>
    <script>
     function PreviewImage() {
               
               pdffile_url="E:/one.pdf"
               $('#viewer').attr('src',pdffile_url);
           }
    </script>
    <script>
    $(document).ready(function(){
       
        // Add minus icon for collapse element which is open by default
        $(".collapse.in").each(function(){
        	$(this).siblings(".panel-heading").find(".glyphicon").addClass("glyphicon-minus").removeClass("glyphicon-plus");
        });
        
        // Toggle plus minus icon on show hide of collapse element
        $(".collapse").on('show.bs.collapse', function(){
        	$(this).parent().find(".glyphicon").removeClass("glyphicon-plus").addClass("glyphicon-minus");
        }).on('hide.bs.collapse', function(){
        	$(this).parent().find(".glyphicon").removeClass("glyphicon-minus").addClass("glyphicon-plus");
        });
        
        var myColors = [
                        'red', 'blue', 'yellow' , 'green' , 'purple' , 'orange'
                    ];
                    var i = 0;
                    
                    
                    $('div#myDiv').each(function() {
                        $(this).css('border-left-color', myColors[i]);
                        i = (i + 1) % myColors.length;
                    });
                    
                    $('ul.nav li').click(function(){
                        var tab_id = $(this).attr('data-tab');

                        if(tab_id=="tab-2")
                        {
                        console.log('called');
                        $("#link2").click(function(){
                        $("#tab-1").hide();
                      
                    });
                        }    else{
                         $("#tab-1").show();
                        }
                      })
    });
</script>
</html>