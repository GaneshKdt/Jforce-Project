<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ContentStudentPortalBean"%>
<%@page import="com.nmims.beans.ModuleContentStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<%
	String isLead = session.getAttribute("isLoginAsLead").toString();
%>
<style type="text/css">

	.disableClick{
	pointer-events: none;
	cursor: default;

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
	padding: 10px;
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
	min-height:100px;
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
	
	.tile-header span {
	    font-size:1rem;
	    float:left;
	}  
	
	.modal-footer {
	     padding: 0; 
	     border-top:none; 
	     }
	
	#downloadVideo_Notused {
	  pointer-events: none;
	  cursor: default;
	}
	
	.nodata { 
    vertical-align: middle;
    color: #a6a8ab;
    font: 1.00em "Open Sans";
    text-align: center;
    margin: 0;
	}
	
	.fa{
		cursor:pointer;
	}
		
		.actionModal {
		  display: none;
		  position: fixed;
		  z-index: 4;
		  padding-top: 150px; 
		  left: 0;
		  top: 0;
		  width: 100%; 
		  height: 100%; 
		  overflow: auto;
		  background-color: rgba(0,0,0,0.4); 
		}

		.actionModal-content {
			font-family: "Open Sans";
			font-weight: 400;
			background-color: #fefefe;
			margin: auto;
			padding: 20px;
			border: 1px solid #888;
			max-height: calc(100vh - 250px);
			overflow: auto; 
			border-radius: 4px;
			font-size: 1.2em;
			text-align: center;
		}
		
		.actionModal-content b{
			font-weight: 700;
		}
		
</style>


<%
	ArrayList<ModuleContentStudentPortalBean> moduleDocumentList =  (ArrayList<ModuleContentStudentPortalBean>)session.getAttribute("moduleDocumentList");
	int noOfLearningResources = moduleDocumentList != null ? moduleDocumentList.size() : 0;
	StudentStudentPortalBean studentSemCheck = (StudentStudentPortalBean)session.getAttribute("semCheck");
	ArrayList<VideoContentStudentPortalBean> videoContentList =  (ArrayList<VideoContentStudentPortalBean>)session.getAttribute("videoContentList");
	int noOfVideoContents = videoContentList != null ? videoContentList.size() : 0;
	ArrayList<ContentStudentPortalBean> contentList =  (ArrayList<ContentStudentPortalBean>)session.getAttribute("contentList");
	int noOfContents = contentList != null ? contentList.size() : 0;
	ArrayList<ContentStudentPortalBean> finalContentLastCycleList = (ArrayList<ContentStudentPortalBean>)session.getAttribute("contentLastCycleList");
	int noOflastCycleContent = finalContentLastCycleList != null ? finalContentLastCycleList.size() : 0;
	System.out.println("IN JSP got contentList size : "+contentList !=null ? contentList.size() : 0);
	ArrayList<ContentStudentPortalBean> course = (ArrayList<ContentStudentPortalBean>)session.getAttribute("contentLastCycleList");
	String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
	int courseSize = course.size();
	int count=1;

%>

<div class="course-learning-resources-m-wrapper" id="learningResources">
	<div class=" panel-courses-page">
		<div class="panel-heading" role="tab" id="">
		
			<%if(!earlyAccess.equalsIgnoreCase("Yes")) { %>
				<div class=" col-md-offset-10 col-md-2 ">
					<div class="custom">
						<select class="form-control switchContent" >
							<option selected>Current Cycle Content</option> 
							<option >Last Cycle Content</option>
						</select>
					</div>
				</div>
			<% } %>
			
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li><a class="panel-toggler collapsed"  role="button" data-toggle="collapse" href="#collapseThree" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>
		<div id="collapseThree" class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
		
			<div class="panel-body" style="padding: 20px;"> 
				<%if(noOfLearningResources == 0 && noOfVideoContents==0 && noOfContents==0 && noOflastCycleContent==0){ %>
					<div class="no-data-wrapper">
						<h6 class="no-data nodata"> <span class="icon-icon-pdf"></span>No new Learning Resources</h6>
					</div>
				<%}else{ %>

					<div class="data-content current-cycle">
						<div>
							<jsp:include page="currentCycleContent.jsp" />
						</div> 
					</div>
					<div class="data-content last-cycle">
						<div>
							<jsp:include page="lastCycleContent.jsp" />
						</div>
					</div>
					<%
						}
					%>
				
			</div>
		</div>
	</div>
</div>

	
	<div id="alertLead" class="actionModal">
	
		<div class="actionModal-content" style="max-width: 500px; padding: 20px;">
			 <i class='fa fa-exclamation-triangle'></i> <b>Confirm Action</b> <br> 
			<div style="padding: 20px;"> Please enroll for the complete course to avail this feature. </div>
			<div style='width: 100%; margin: auto;'>
				<button type='button' class='btn btn-primary' style="background-color: #d2232a" onClick="hideShow('hide')"
					 id="attendClose" style='margin: 10px;'>Close</button>
			</div>
		</div>
		
	</div> 

<script>
function toggleIcon(e) {
    $(e.target)
        .prev('.panel-heading')
        .find(".more-less")
        .toggleClass('glyphicon glyphicon-menu-down glyphicon glyphicon-menu-up');
}
$('.panel-group').on('hidden.bs.collapse', toggleIcon);
$('.panel-group').on('shown.bs.collapse', toggleIcon);

</script>
    <script>
    $(document).ready(function(){
         var tileHeaderColors = ['#e53935 red darken-1','#3949ab indigo darken-1','#00897b teal darken-1','#fb8c00 orange darken-1',
        	 '#757575 grey darken-1','#5e35b1 deep-purple darken-1 ','#43a047 green darken-1','#ff8f00 amber darken-3'];
         var j=0;
         $('div#tileHeader').each(function(){
        	$(this).css('background-color',tileHeaderColors[j]); 
        	j=(j+1) % tileHeaderColors.length; 
         })
         
         

         function get_filesize(url, callback) {
             var xhr = new XMLHttpRequest();
             xhr.open("HEAD", url, true); // Notice "HEAD" instead of "GET",
                                          //  to get only the header
             xhr.onreadystatechange = function() {
                 if (this.readyState == this.DONE) {
                 	console.log("xhr >>>");
                     console.log(xhr);
                     callback(parseInt(xhr.getResponseHeader("Content-Length")));
                 }
             };
             xhr.send();
         }
		
    });
    
    
    function goToDetails(id){
    	console.log("test");
     	var colorId =  $('div#tileHeader').css('backgroundColor');
     	console.log('color Id '+colorId);
    	window.location.replace("/acads/moduleLibraryList?moduleId="+id+"&colorId="+colorId);
    	
    }
</script>

<script>
for(var i =  0; i< <%=noOfVideoContents %>; i++){

	try {
		document.getElementById("downloadVideo"+i).addEventListener("click", function(event){
			console.log('Clicked');
			event.preventDefault()
		});
	}
	catch(err) {
	    console.log("Catch error : "+err.message);
	}
	
}
$(".current-cycle").show();   
$(".last-cycle").hide(); 
$('.switchContent').on('change', function() { 
	var selected = $(this).find('option:selected').text();
	if(selected=="Last Cycle Content"){
		$(".current-cycle").hide();   
		$(".last-cycle").show(); 
	} 
	else {
		$(".current-cycle").show();   
		$(".last-cycle").hide();   
	}    
});
</script> 

<script>

	$(".bookmark").click(function() {
		//added to not let leads bookmark content 
		let isLead = <%= isLead%>;
		if(isLead == "true"){
			return;
		}
		
		let ref=this;
		if ($(ref).data("selected") == "no") {

			$(ref).data("selected", "yes");
			let data = {
				'id' : $(ref).attr("id"),
				'bookmarked' : "Y",
				'url' : $(ref).attr("class").split(" ")[1],
			};
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/studentportal/setBookmark",
				data : JSON.stringify(data),
				success : function(data) {
					$(ref).find("i").removeClass("fa-bookmark-o");
					$(ref).find("i").addClass("fa-bookmark");
					$(ref).find("i").attr("style", "color:#fabc3d;")
				},
				error : function(e) {
					console.log("error", e);
				}
			});
		} else {

			let data = {
				'id' : $(ref).attr("id"),
				'bookmarked' : "N",
				'url' : $(ref).attr("class").split(" ")[1],
			};
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "setBookmark",
				data : JSON.stringify(data),
				success : function(data) {
					$(ref).find("i").addClass("fa-bookmark-o");
					$(ref).find("i").removeClass("fa-bookmark");
					$(ref).find("i").attr("style", "")
					$(ref).data("selected", "no");
				},
				error : function(e) {
					console.log("error", e);
				}
			});

		}
	});

	function hideShow(action){

		console.log("action: "+action)
		if(action == "show"){
			$('#alertLead').show();
		}else{
			$('#alertLead').hide();
		}
		
	}
	
</script>

<!-- DataTable plug-in for search  -->
	 	
	

	
