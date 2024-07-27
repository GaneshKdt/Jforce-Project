<%@page import="com.nmims.beans.FaqQuestionAnswerTableBean"%>
<%@page import="com.nmims.beans.FaqSubCategoryBean"%>
<%@page
	import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="com.nmims.daos.FaqDao"%>
<%@page import="com.nmims.beans.FaqCategoryBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>

<html lang="en">




<jsp:include page="../common/jscss.jsp">
	<jsp:param value="FAQ" name="title" />
</jsp:include>



<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Student Support;FAQs"
				name="breadcrumItems" />
		</jsp:include>
		<%
		ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request);
		FaqDao faqdao = (FaqDao) ac.getBean("faqdao");
		String faqGroupTypeId=(String)session.getAttribute("faqGroupTypeId");
		%>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="FAQs" name="activeMenu" />
				</jsp:include>
			</div>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<div class="common-content supportWrap">
							<div class="col-xs-12">
								<div class="row">
									<div class="col-lg-9 col-sm-6 col-xs-12">
										<h3 class="information-title red">Frequently Asked
											Questions</h3>
									</div>
									<div class="col-lg-3 col-sm-6 col-xs-12"></div>

									<div class="col-xs-12 col-md-12">
										<div class="row" style="padding: 15px;">

											<div class="col-md-12" style="margin: 0em 0; padding: 0px;">

												<input type="text" id="search-criteria" class="form-control"
													style="width: 100%" placeholder="Enter Search Text Here">
											</div>


										</div>
									</div>
								</div>
								<div class="supportFaq">
									<div class="panel-group" id="accordion" role="tablist"
										aria-multiselectable="true">




										<%
										ArrayList<FaqCategoryBean> category = (ArrayList<FaqCategoryBean>) request.getAttribute("category");

										for (int i = 0; i < category.size(); i++) {
										%>
										<div class="panel panel-default faq">
											<div class="panel-heading">
												<h4 class="panel-title">
													<a class="collapsed" data-toggle="collapse"
														aria-expanded="false" data-parent="#accordion"
														href="#<%=category.get(i).getCategoryname().replaceAll(" ", "")%>">
														<%=category.get(i).getCategoryname()%>
													</a>
												</h4>
											</div>
											<!--/.panel-heading -->


											<div
												id="<%=category.get(i).getCategoryname().replaceAll(" ", "")%>"
												class="panel-collapse collapse out">
												<div class="panel-body">



													<div class="panel-group" id="nested">

														<div class="panel-group" id="accordion1" role="tablist"
															aria-multiselectable="true">
															
															<!-- BY PS -->
														<% 	
														ArrayList<FaqQuestionAnswerTableBean> questionanswerlist=faqdao.getListOfFaqQuestionAnswer(faqGroupTypeId, String.valueOf(category.get(i).getId()),"");
												
														for(int q=0;q<questionanswerlist.size();q++)
														{
														%>
														<div class="panel panel-default faq">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a data-toggle="collapse" aria-expanded="false"
																		data-parent="#192"
																		href="#<%=category.get(i).getCategoryname().replaceAll(" ","") + questionanswerlist.get(q).getId()%>">
																		 <%=questionanswerlist.get(q).getQuestion() %> </a>
																</h4>
															</div>
															<!--/.panel-heading -->
															<div id="<%=category.get(i).getCategoryname().replaceAll(" ","") + questionanswerlist.get(q).getId()%>"
																class="panel-collapse collapse out">
																<div class="panel-body faqAns">
																<p><%=questionanswerlist.get(q).getAnswer() %></p>
																	<!-- <p>
																	
																		You can enroll for our Distance Learning Programs
																		using the below link and paying the applicable charges<br>

																		<a
																			href="https://ngasce.secure.force.com/nmLogin_New?type=registration"
																			title="Enroll for Distance Learning Programs Here">
																			https://ngasce.secure.force.com/nmLogin_New?type=registration
																		</a> <br> Please note : The registration charges are
																		non-refundable.
																	</p> -->
																</div>
																<!--/.panel-body -->
															</div>
															<!--/.panel-collapse -->
														</div>
														<!-- /.panel -->
                                                    <%} %>
															
															<%
															ArrayList<FaqSubCategoryBean> subcategory = faqdao.getFAQSubCategories(String.valueOf(category.get(i).getId()));
															for (int j = 0; j < subcategory.size(); j++) {
															%>
															<div class="panel panel-default faq">
																<div class="panel-heading">
																	<h4 class="panel-title">
																		<a class="collapsed" data-toggle="collapse"
																			aria-expanded="false" data-parent="#accordion1"
																			href="#<%=subcategory.get(j).getSubCategoryName().replaceAll(" ", "")%>">
																			<%=subcategory.get(j).getSubCategoryName()%>
																		</a>
																	</h4>
																</div>
																<!--/.panel-heading -->


																<div
																	id="<%=subcategory.get(j).getSubCategoryName().replaceAll(" ", "")%>"
																	class="panel-collapse collapse out">
																	<div class="panel-body">



																		<div class="panel-group" id="nested">

																		<%
																		ArrayList<FaqQuestionAnswerTableBean> questionanswerlistforSubcategory=faqdao.getListOfFaqQuestionAnswer(faqGroupTypeId, String.valueOf(category.get(i).getId()),String.valueOf(subcategory.get(j).getId()));
																		for(int s=0;s<questionanswerlistforSubcategory.size();s++)
																		{
																		%>
																			
																			<!-- BY PS -->
																			<div class="panel panel-default faq">
																				<div class="panel-heading">
																					<h4 class="panel-title">
																						<a data-toggle="collapse" aria-expanded="false"
																							data-parent="#192"
																							href="#<%=subcategory.get(j).getSubCategoryName().replaceAll(" ", "")+ questionanswerlistforSubcategory.get(s).getId()%>">
																							 <%=questionanswerlistforSubcategory.get(s).getQuestion() %>
																						</a>
																					</h4>
																				</div>
																				<!--/.panel-heading -->
																				<div
																					id="<%=subcategory.get(j).getSubCategoryName().replaceAll(" ", "") + questionanswerlistforSubcategory.get(s).getId()%>"
																					class="panel-collapse collapse out">
																					<div class="panel-body faqAns">
																					
																					<p><%=questionanswerlistforSubcategory.get(s).getAnswer() %> </p>
																						<!-- <p>
																							You can enroll for our Distance Learning Programs
																							using the below link and paying the applicable
																							charges<br> <a
																								href="https://ngasce.secure.force.com/nmLogin_New?type=registration"
																								title="Enroll for Distance Learning Programs Here">
																								https://ngasce.secure.force.com/nmLogin_New?type=registration
																							</a> <br> Please note : The registration charges
																							are non-refundable.
																						</p> -->
																					</div>
																					<!--/.panel-body -->
																				</div>
																				<!--/.panel-collapse -->
																			</div>
																			<!-- /.panel -->
																			<%} %>


																		</div>
																		<!--/.panel-group achere -->
																	</div>
																	<!--/.panel-body-->
																</div>
																<!--/.panel-collapse-->

															</div>
															<%
															}
															%>
														</div>
														
														


													</div>
													<!--/.panel-group achere -->
												</div>
												<!--/.panel-body-->
											</div>
											<!--/.panel-collapse-->

										</div>
										<!--/.panel-->
										<%
										}
										%>
									</div>
								</div>
							</div>
							<div class="clearfix"></div>
						</div>
						<div class="clearfix"></div>

					</div>
				</div>


			</div>
		</div>
	</div>


	<jsp:include page="../common/footer.jsp" />


	<script>
		$('#search-criteria').on(
				'input',
				function(e) {
					$('#search-criteria').css("background-color", "white");
					$('.panel-body').css("background-color", "white");
					$('.panel-collapse').removeClass('in');
					$('.panel-collapse').addClass('out');

					var txt = $('#search-criteria').val();
					if (txt == '') {
						$('.panel-collapse').removeClass('in');
						$('.panel-collapse').addClass('out');
						$('#search-criteria').css("background-color", "white");

					} else {
						$('.panel-body').each(
								function() {
									if ($(this).text().toUpperCase().indexOf(
											txt.toUpperCase()) != -1) {
										$('#search-criteria').css(
												"background-color", "#B8F1B8");
										$(this).css("background-color",
												"lightgrey");
										$(this).parent('.panel-collapse')
												.removeClass('out');
										$(this).parent('.panel-collapse')
												.addClass('in');
									}
								});
					}

				});
	</script>


</body>
</html>