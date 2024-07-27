
<!DOCTYPE html>


<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Lecture Videos" name="title" />
</jsp:include>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;Mock Calls"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Recorded Calls</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
						
														  <div class="panel panel-default faq">
															<div class="panel-heading">
															  <h4 class="panel-title"> 
															  <a class="collapsed" data-toggle="collapse" aria-expanded="false"  data-parent="#193" href="#recordingsDV">
															    Recordings for DV 
															  </a> 
															  </h4>
															</div>
															<!--/.panel-heading -->
															
															<div id="recordingsDV" class="panel-collapse collapse out">
															  <div class="panel-body" style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																<div class="panel-group" id="nested">
														<!-- BY PS academics 2.6.1-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20061">
																Recording 1
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20061" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                                <a href = "/lr/ImportantDocICLC/Recordings/1 -DV.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.1-->
														<!-- BY PS academics 2.6.2-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20062">
																Recording 2
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20062" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                               <a href = "/lr/ImportantDocICLC/Recordings/3 - DV.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.2-->
																</div>
															  </div>
															</div>
														   </div>
														<!-- End of Recordings -->
														
														
														
														 <div class="panel panel-default faq">
															<div class="panel-heading">
															  <h4 class="panel-title"> 
															  <a class="collapsed" data-toggle="collapse" aria-expanded="false"  data-parent="#194" href="#recordings">
															    Recordings for ML 
															  </a> 
															  </h4>
															</div>
															<!--/.panel-heading -->
															
															<div id="recordings" class="panel-collapse collapse out">
															  <div class="panel-body" style="border-bottom: solid 2px grey; border-left: solid 2px grey; border-right: solid 2px grey;">
																<div class="panel-group" id="nested">
														<!-- BY PS academics 2.6.1-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20071">
																Recording 1
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20071" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                                <a href = "/lr/ImportantDocICLC/Recordings/2 - ML.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.1-->
														<!-- BY PS academics 2.6.2-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20072">
																Recording 2
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20072" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                               <a href = "/lr/ImportantDocICLC/Recordings/4 - ML.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.2-->
														<!-- BY PS academics 2.6.2-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20073">
																Recording 3
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20073" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                               <a href = "/lr/ImportantDocICLC/Recordings/5- ML.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.2-->
														<!-- BY PS academics 2.6.2-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20074">
																Recording 4
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20074" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                               <a href = "/lr/ImportantDocICLC/Recordings/6 - ML.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.2-->
														<!-- BY PS academics 2.6.2-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20075">
																Recording 5
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20075" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                               <a href = "/lr/ImportantDocICLC/Recordings/7 -ML.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.2-->
														<!-- BY PS academics 2.6.2-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20076">
																Recording 6
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20076" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                               <a href = "/lr/ImportantDocICLC/Recordings/8 -ML.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.2-->
														<!-- BY PS academics 2.6.2-->
														
								                          <div class="panel panel-default faq">
								                            <div class="panel-heading">
								                              <h4 class="panel-title"> 
															  <a data-toggle="collapse" aria-expanded="false"  data-parent="#recordings" href="#20077">
																Recording 7
																</a> 
															  </h4>
								                            </div>
								                            <!--/.panel-heading -->
								                            <div id="20077" class="panel-collapse collapse out">
								                              <div class="panel-body faqAns">
								                               <a href = "/lr/ImportantDocICLC/Recordings/9 -ML.wav" target="_blank">Click Here</a>
								                              </div>
								                              <!--/.panel-body --> 
								                            </div>
								                            <!--/.panel-collapse --> 
								                          </div>
								                          <!-- /.panel -->
														<!-- End of BY PS academics 2.6.2-->
																</div>
															  </div>
															</div>
														   </div>
														<!-- End of Recordings -->
							
							
						</div>


					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
</html>