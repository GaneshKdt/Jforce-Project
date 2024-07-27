<!DOCTYPE html>

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Hall Ticket" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Hall Ticket" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              					<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              					<div class="sz-content">
									<h2 class="red text-capitalize">Hall Ticket</h2>
									<div class="clearfix"></div>
	            					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
												<input type="button"  class="btn btn-large btn-primary" value="Download Hall Ticket" onclick="window.open('${fileName}')"> 
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
									</div>
              								
              					</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>