
<!DOCTYPE html>


<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Demo Exam" name="title" />
</jsp:include>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;Demo Exam"
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

						<h2 class="red text-capitalize">Demo Exam</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">


							<div class="table-responsive" id="detail">
								<table class="table table-striped" style="font-size: 12px">
									<thead>
										<tr>
											<!-- <th>Sr No</th> -->
											<th>Subject</th>
											<th>Take Exam</th>
										</tr>
									</thead>
									<tbody>



										<tr>
											<td><c:out value="Business Communication and Etiquette" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTEwNl8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Business Statistics" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyNl8wNjAzMjAxOA== "
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Business Economics" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTEwM18yODAyMjAxOA== "
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Business Law" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIzNF8wNjAzMjAxOA== "
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Corporate Social Responsibility" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTEwNF8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Essentials of HRM" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIxOF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Financial Accounting & Analysis" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIwNl8wMTAzMjAxOA== "
												target="_blank"> Click Here </a></td>
										</tr>



										<tr>
											<td><c:out value="Information Systems for Managers" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTEwNV8wMTAzMjAxOA== "
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Management Theory and Practice" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTEwMF8yMzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>


										<tr>
											<td><c:out value="Marketing Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIwNF8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>


										<tr>
											<td><c:out value="Organisational Behaviour" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTEwMl8yNzAyMjAxOA== "
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Organisational Theory, Structure and Design" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMxMF8yNzAyMjAxOA== "
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Strategic Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfMjE0XzA1MDMyMDE4"
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Introduction to Financial Planning and Investment Planning" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIwMl8wMTAzMjAxOA== "
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Manpower Planning, Recruitment and Selection" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIwM18wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Merchandising Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIwN18wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Consumer Behaviour" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIwOV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Export Import Procedures and Documentation" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIxMF8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Brand Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMwMV8yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Commercial Banking System & Role of RBI" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMwMl8yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Organisation Culture" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMwM18yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Strategic Cost Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMwNF8yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Compensation & Benefits" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMwNV8yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Customer Relationship Management" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMwNl8yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Custom Shipping and Insurance" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMwN18yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="International Marketing" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMxMF8yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Retail Store Design and Location" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMxMV8yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="E COMMERCE AND CYBER LAWS" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTUwMl8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Corporate Finance" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIwMV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Total Quality Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIwNV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Enterprise Resource Planning" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTMwOF8yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Financial Accounting" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTUwM18yNzAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Human Resource Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTUwNF8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Integrated Marketing Communications" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxMF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Mass Communication" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTUwOV8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Public Relations Theory and Practice" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTUxMF8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Social Media Marketing and Web Analytics" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTUxMV8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Written and Oral Communication" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTUxMl8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Introduction to Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTUxM18yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>



										<tr>
											<td><c:out value="Search Engine and Email Marketing" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwMV8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Introduction to Digital Marketing" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwMl8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Search Engine Optimization" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwM18yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Web Analytics" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwNF8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Adwords" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwNV8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Social Media Marketing" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwNl8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="IT Infrastructure Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwN18yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="IT Security and Risk Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwOF8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Managing Business Process Outsourcing" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcwOV8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="IT Project Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcxMF8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Emerging Technologies: IoT, Augmented Reality, Virtual Reality" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcxMV8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Fundamentals of Big Data & Business Analytics" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcxMl8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Cloud Computing" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcxM18yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="E-Business" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcxNF8yODAyMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="E-Governance" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcxNV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Digital Payments" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcxNl8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Operations and Supply Management" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcxOV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Procurement Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcyMF8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Service Operations Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcyMV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Project Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcyNF8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out
													value="Introduction to Financial Planning, Investment Planning, Retirement planning" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcyNV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Financial Institutions and Markets" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfcHJhY3RpY2VfdGVzdF9maW5hbmNpYWxfMDYwNjIwMTk="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Marketing of Financial Services" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTcyOV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="MONITORING AND CONTROLLING PROJECT" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTczMF8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="PROJECT PLANNING & SCHEDULING" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTczMV8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="e- Commerce and Cyber Laws" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTczMl8wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Internet Marketing" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTczN18wMTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

										<tr>
											<td><c:out value="Industrial Relations & Labour Laws" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIxMV8wNTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Logistics Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIxMl8wNTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Tax Insurance and Retirement Planning" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIxNV8wNTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Taxation- Direct and Indirect" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIxNl8wNTAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out
													value="Capital Market and Portfolio Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIxN18wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="India's Foreign Trade" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIxOV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Introduction to Retail" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyMF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Marketing Strategy" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyMV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Project Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyMl8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Retail Banking" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyM18wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Supply Chain Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyNV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Cost & Management Accounting" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyN18wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="International Business" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyOF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Marketing of Financial Services" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIyOV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Operations Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIzMF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Performance Management System" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIzMV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Sales Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTIzM18wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="International Finance" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxMl8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Advanced Supply Chain Management" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwMV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="B2B Marketing" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwMl8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="BUSINESS; ETHICS, GOVERNANCE & RISK" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwM18wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Decision Analysis & Modeling" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwNF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out
													value="Employee Development & Talent Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwNV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="FINANCIAL INSITUTIONS AND MARKETS" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwNl8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Global Retailing" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwN18wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="HR Audit" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwOF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Insurance & Risk Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQwOV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Integrated Marketing Communications" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxMF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out
													value="International Banking & Foreign Exchange Management" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxMV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="International HR Practices" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxM18wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out
													value="International Logistics & Supply Chain Management" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxNF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Marketing Research" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfbWFya2V0aW5nX3Jlc2VhcmNoX3ByYWN0aWNlX3Rlc3RfMjIwNTIwMTk="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out
													value="Retail Store Operation and Inventory Management" />
											</td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxNl8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Services Marketing" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZV80MTdfMDYwMzIwMTg="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Strategic Financial Management" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxOF8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Strategic HRM" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQxOV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="Treasury Management in Banking" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZV80MjBfMDYwMzIwMTg="
												target="_blank"> Click Here </a></td>
										</tr>
										<tr>
											<td><c:out value="World Class Operations" /></td>
											<td><a
												href="http://thepracticetest.in/exam/setup.php?key=bm1pbXNfZTQyMV8wNjAzMjAxOA=="
												target="_blank"> Click Here </a></td>
										</tr>

									</tbody>
								</table>
							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>


	<jsp:include page="../adminCommon/footer.jsp" />


</body>
</html>