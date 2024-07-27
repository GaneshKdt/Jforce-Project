<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
		
		<c:if test="${homepageRank.size() > 0}">
			<div class="ranks mb-0">
				<div class="d-flex align-items-center text-wrap py-1">
					<span class="fw-bold me-3"><small class="fs-5"> RANK</small></span>
						<div class="ms-auto">
							<a href="/studentportal/student/ranks" class="text-dark me-1"><small class="text-nowrap">SEE ALL</small></a>
							<a type="button" data-bs-toggle="collapse" data-bs-target="#collapseRank" role="button" aria-expanded="true" aria-controls="collapseRank" class="text-muted"
								id="collapseCard"> <i class="fa-solid fa-square-minus"></i></a>
						</div>
				</div>
					
				<div id="collapseRank" class="collapse ">
					<div class="card card-body text-center">
						<h6><i class="fa-solid fa-clipboard-list"></i>
						<small > Rank List</small></h6>
					</div>
				</div>
				<div id="collapseRank" class="collapse show">
					<div class="card card-body  pt-0  pb-1 ">
						<c:choose>
							<c:when test="${ empty homepageRank }">
								<h6 class="text-center mt-3" ><i class="fa-solid fa-clipboard-list"></i><small > Rank List</small></h6>																	
							</c:when>
							<c:otherwise>
					
							<div class="row d-flex justify-content-evenly">
							  <c:forEach var="rank" items="${ homepageRank }">		
							        <div class="card mt-1   me-1 ms-1 col-lg-12  d-flex justify-content-center" >
							            <div class="card-body">
							                <div class="row align-items-center">
							                    <p class="fs-6 text-center"><strong>Sem : ${ rank.sem } Cycle : ${ rank.month } ${ rank.year }</strong></p>
							                </div>
							                <p>
							                    <c:choose>
							                        <c:when test="${ empty rank.rank }">
							                            <div class="fs-6">
							                                <strong>Not Applicable</strong>
							                            </div>
							                        </c:when>
							                        <c:otherwise>
							                        <div class="row d-flex justify-content-center">
								                            <div class=" col-md-5 d-flex justify-content-center  align-items-center">
								                               <div class="row d-flex justify-content-center text-center">
									                                <div class=" col-lg-6  col-md-12 col-6 text-center fs-2">
									                                    <i class="fa-sharp fa-solid fa-ranking-star "></i> 
									                                </div>
									                                <div class=" col-lg-6  col-md-12 col-6 text-center ">
									                                    <div class="row  fw-bolder d-flex ">
									                                        <div class="col-12 fs-6 text-center text-nowrap"><span class="text-danger ">${rank.rank}</span></div>
									                                        <div class="col-12 text-nowrap"><small>My Rank</small></div>
									                                    </div>
									                                </div>
								                           	 	</div>
								                            </div>
								                            <br>
								                            <div class=" col-md-5 d-flex justify-content-center  align-items-center">
								                                 <div class="row justify-content-center text-center">
										                                <div class=" col-lg-6 col-md-12 col-6 text-center fs-2 ">
										                                    <i class="fa-solid fa-medal "></i>
										                                </div>
										                                <div class=" col-lg-6  col-md-12 col-6 text-center">
										                                    <div class="row fw-bolder d-flex">
										                                        <div class="col-12 fs-6 text-nowrap text-center"><span class="text-danger ">${ rank.total } / ${ rank.outOfMarks }</span></div>
										                                        <div class="col-12 text-nowrap"><small>Total Score</small></div>
										                                    </div>
										                                </div>
								                          		  </div>
								                            </div>
							                           </div> 
							                        </c:otherwise>
							                    </c:choose>
							                </p>
						            </div>
						        </div>
							</c:forEach>		
						</div>
							</c:otherwise>	
						</c:choose>

					</div>
				</div>
			</div>
	</c:if>
		