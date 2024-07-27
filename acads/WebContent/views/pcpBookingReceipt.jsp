<!DOCTYPE html>

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="PCP/VC Registration Receipt" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;PCP/VC Registration Receipt" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="PCP/VC Registration Receipt" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              					<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              					<div class="sz-content">
									<h2 class="red text-capitalize">PCP/VC Fee Receipt</h2>
									<div class="clearfix"></div>
	            					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
									</div>
              								
              					</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>