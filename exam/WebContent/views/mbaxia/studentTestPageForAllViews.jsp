<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page pageEncoding="UTF-8" %>


<html lang="en">



<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />

<script type="text/javascript">
    var slowLoad = window.setTimeout( function() {
        alert( "The page is taking time loading, please wait or close all tabs and retry after joining again. PRESS OK to continue. " );
    }, 10000 ); //10sec

    window.addEventListener( 'load', function() {
    	console.log('window.clearTimeout slowLoad started ');
        window.clearTimeout( slowLoad );
    	console.log('window.clearTimeout slowLoad end ');
    }, false );
</script>

<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Start Assignment" name="title" />
</jsp:include>

<%
	
%>
<!-- Roboto Web Font  fgfg-->

<link
	href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en"
	rel="stylesheet">

<!-- Include CSS for icons. -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css" />

<!-- JQuery & Boostrap related CDNS -->
<!-- 
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
 -->
<!-- JQuery & Boostrap related CDNS end -->

<!-- Include Editor style. -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css"
	rel="stylesheet" type="text/css" />
<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />

<!--
<link rel="stylesheet" href="assets/js/ckeditor/ckeditor_responsive_table/plugin.css">
  -->
<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/css/CustomCalc.css">
<!-- 
<link rel="stylesheet" href="assets/css/CustomScriblePad.css">
  -->
	
<style>
#sectionName{
	text-align: center;
    color: grey;
    font-weight: bold;
    text-transform: uppercase;
    padding-top : 10px
}

body {
	-webkit-user-select: none;
	-khtml-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	-o-user-select: none;
	user-select: none;
	background-color: #ECE9E7 !important;
}

#calculatorDiv{
	position: fixed;
	top: 58px;
	right: 0;
	display : none;
}

#scriblePadDiv{
	position: fixed;
	top: 80px;
	right: 0;
	display : none;
}

#toggleCalculatorButton{
	color : black;
	position: fixed;
	top: 25px;
	right: 10px;

}
#toggleScribblePadButton{
	color : black;
	position: fixed;
	top: 60px;
	right: 5px;

}

 .math-tex{
    overflow-x: auto;
    display: flex;
}


/* CSS FOR CKEDITOR TABLE RESPONSIVE VIEW start */
table {
    display: table;
    border-collapse: separate;
    border-spacing: 2px;
}

/* CSS from main plugin start */
  thead {
  display: none;
}

@media (min-width: 768px) {
    thead {
    display: table-header-group;
    
  }
}

  tbody tr {
  display: block;
  
    margin-bottom: 5px;
}

@media (min-width: 768px) {
    tbody tr {
    display: table-row;
  }
}

  tbody tr td {
  display: block;
}

@media (min-width: 768px) {
    tbody tr td {
    display: table-cell;
  }
}

  tbody tr td:before {
  content: attr(data-label);
}

@media (min-width: 768px) {
    tbody tr td:before {
  content: ' ';
  }
}


/* CSS from main plugin end */


 table {
  border-collapse: collapse;
}

  thead {
  background: #ccc;
}

  thead th {
  font-weight: 600;
  padding: 15px;
  text-align: left;
}

 

  tbody tr {
  /* border-bottom: 3px solid #ccc; */
  
   background-color: lightgray;
}

@media (min-width: 768px) {
	tbody {
  		border: 1px solid #ccc;
	}
    tbody tr {
    border-bottom: none;
    background-color: transparent;
  }
}

  tbody tr td {
    text-align: left;
  padding: 5px;
}

@media (min-width: 768px) {
    tbody tr td {
    border: 1px solid #ccc;
  }
}

  tbody tr td:before {
  float: left;
  font-weight: 600;
  margin-right: 10px;
}

  tbody tr td:last-child {
  /* border-bottom: 0; */
}

  tbody tr:last-child {
  /* border-bottom: 0; */
}


/* CSS FOR CKEDITOR TABLE RESPONSIVE VIEW end */

#redBackgroundDiv {
	background-color: red !important;
	z-index: 9999;
	width: 100%;
	height: 100vh;
	position: fixed;
	display: none;
}


#contactAdminParentDiv {
	background-color: rgba(0,0,0,0.5) !important;
	z-index: 9999999999;
	width: 100%;
	height: 100vh;
	position: fixed;
	display : none; 
	
}

#contactAdminChildDiv {
	background-color: white !important;
	color: black;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	text-align : center;
	padding : 100px 50px;
}

@media only screen and (max-width: 480px) {
  
	#contactAdminChildDiv {
		padding : 5px 5px;
		width : 90%;
	}
}



#reportLostFocusParentDiv {
	background-color: rgba(0,0,0,0.2) !important;
	z-index: 9999999999;
	width: 100%;
	height: 100vh;
	position: fixed;
	display : none; 
	
}

#reportLostFocusChildDiv {
	background-color: white !important;
	color: black;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	text-align : center;
	//padding : 100px 50px;
	width: 70%;
}

/*button for #reportLostFocus */
.btnForRLF {
	display: inline-block;
	font-weight: normal;
	text-align: center;
	white-space: nowrap;
	vertical-align: middle;
	cursor: pointer;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
	border: 1px solid transparent;
	padding: 5px;
	margin: 5px;
	font-size: 1rem;
	line-height: 1.5;
	border-radius: 0;
}


.btnForRLFSubmit { 
   background-color: rgb(48, 133, 214); 
 }
 
 
.btnForRLFSubmit:hover { 
   background-color: darkblue;
   border : 1px solid black;
   color:white;  
 }
 
.btnForRLFSubmit:focus,
.btnForRLFSubmit.focus { 
   background-color: blue;
   border : 1px solid black;
   color:white;  
 }
 
.btnForRLFClose { 
	display:none;
   background-color: red;
 }
 
 
.btnForRLFClose:hover { 
     background-color: rgb(210, 35, 42);
   border : 1px solid black;
   color:white; 
 }
 
.btnForRLFClose:focus,
.btnForRLFClose.focus { 
     background-color: rgb(210, 35, 42);
   border : 1px solid black;
   color:white;
   
 }
 
.inputForRLF{

   color:black;
}

.inputForRLF:focus,
.inputForRLF.focus{
	
	border-top : 0px solid black;
	border-right : 0px solid black;
	border-bottom : 2px solid black;
	border-left : 0px solid black;
	
} 

#reportRLFTitle{
	font-size : 20px;
}
#reportRLFSubTitle{
	font-size : 16px;
}

#reportRLFSelectReasonDiv{
	font-size : 16px;
}


@media only screen and (max-width: 480px) {
  
	#reportLostFocusChildDiv {
		padding : 5px 5px 5px 5px ;
		position: fixed;
		width : 90%;
		height : 100%;
	}
}

#reportRLFPopAlert{
	display:none;
	position: fixed;
	top: 10px;
	left: 50%;
	transform: translateX(-50%);
}

.blinkingButton {
  -webkit-border-radius: 10px;
  border-radius: 10px;
  border: none;
  color: #FFFFFF;
  cursor: pointer;
  font-family: Arial;
  font-size: 16px;
  text-align: center;
  text-decoration: none;
  -webkit-animation: glowing 1500ms infinite;
  -moz-animation: glowing 1500ms infinite;
  -o-animation: glowing 1500ms infinite;
  animation: glowing 1500ms infinite;
}
@-webkit-keyframes glowing {
  0% { background-color: #B20000; -webkit-box-shadow: 0 0 1px #B20000; }
  50% { background-color: #FF0000; -webkit-box-shadow: 0 0 10px #FF0000; }
  100% { background-color: #B20000; -webkit-box-shadow: 0 0 1px #B20000; }
}

@-moz-keyframes glowing {
  0% { background-color: #B20000; -moz-box-shadow: 0 0 1px #B20000; }
  50% { background-color: #FF0000; -moz-box-shadow: 0 0 10px #FF0000; }
  100% { background-color: #B20000; -moz-box-shadow: 0 0 1px #B20000; }
}

@-o-keyframes glowing {
  0% { background-color: #B20000; box-shadow: 0 0 1px #B20000; }
  50% { background-color: #FF0000; box-shadow:  0 0 10px #FF0000; }
  100% { background-color: #B20000; box-shadow: 0 0 1px #B20000; }
}

@keyframes glowing {
  0% { background-color: #B20000; box-shadow: 0 0 1px #B20000; }
  50% { background-color: #FF0000; box-shadow:  0 0 10px #FF0000; }
  100% { background-color: #B20000; box-shadow:0 0 1px #B20000; }
}


#parentSpinnerDiv {
	background-color: transparent !important;
	z-index: 999;
	width: 100%;
	height: 100vh;
	position: fixed;
}

#childSpinnerDiv {
	color: black;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

#submitTest :hover {
	color: white !important;
}

.tooltip {
	font-size: 16px;
}

#questionsPreviewSection {
	overflow: scroll;
	height: 500px !important;
	margin-top: 10px;
}

#questionsPreviewSection::-webkit-scrollbar {
	width: 2px;
	height: 2px;
}

#questionsPreviewSection::-webkit-scrollbar-thumb {
	background-color: grey;
	border-radius: 1px;
}

.questionImage {
	width: 100%;
	margin: 5px 10px;
}

.questionImageContainer {
	margin: 0px 0px 15px 0px;
}

.swal-button--confirm {
	background-color: #d2232a;
}

.swal2-styled.swal2-confirm {
	font-size: 0.85em !important;
}

.swal2-styled.swal2-cancel {
	font-size: 0.85em !important;
}

.fa-bookmark-o, .fa-bookmark {
	color: #708090 !important;
}

.label-success {
	background-color: #66BB6A;
}

.label-success[href]:focus, .label-success[href]:hover {
	background-color: #49a54e;
}

.left_status {
	background-color: #337ab7;
}

.skipped_status {
	background-color: #a94442;
}

.prevnext { //
	float: right;
	text-decoration: none;
	display: inline-block;
	padding: 8px 16px;
}

.prevnext:hover {
	background-color: #ddd;
	color: black;
}

.previous {
	background-color: white;
	color: black;
	margin-right: 15px;
}

.next {
	background-color: #a94442;
	color: white;
}

.round {
	border-radius: 50%;
}

.list-group {
	padding-left: 0;
	margin-bottom: 0;
}

.list-group-item {
	position: relative;
	display: block;
	padding: .75rem 1.25rem;
	margin-bottom: -1px;
	background-color: #fff;
	border: 1px solid #eee;
}

.card-title {
	margin-bottom: 0.75rem;
}

body {
	font-family: Roboto, "Helvetica Neue", Arial, Helvetica, sans-serif;
	font-size: 1rem;
	line-height: 1.5;
	color: #373a3c;
	background-color: #f9f9f9;
}

.btn {
	display: inline-block;
	font-weight: normal;
	text-align: center;
	white-space: nowrap;
	vertical-align: middle;
	cursor: pointer;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
	border: 1px solid transparent;
	padding: 4px 10px;
	font-size: 1rem;
	line-height: 1.5;
	border-radius: 0;
}

.btn:focus, .btn.focus, .btn:active:focus, .btn:active.focus, .btn.active:focus,
	.btn.active.focus {
	outline: thin dotted;
	outline: 5px auto -webkit-focus-ring-color;
	outline-offset: -2px;
}

.btn:focus, .btn:hover {
	text-decoration: none;
}

.btn.focus {
	text-decoration: none;
}

.btn:active, .btn.active {
	background-image: none;
	outline: 0;
}

.btn.disabled, .btn:disabled {
	cursor: not-allowed;
	opacity: .65;
}

.btn-rounded {
	border-radius: 5px;
}

.pull-xs-right {
	float: right !important;
}

.btn-success {
	color: #fff;
	background-color: #66BB6A;
	border-color: #66BB6A;
}

.btn-success:hover {
	color: #fff;
	background-color: #49a54e;
	border-color: #469e4a;
}

.btn-success:focus, .btn-success.focus {
	color: #fff;
	background-color: #49a54e;
	border-color: #469e4a;
}

.btn-success:active, .btn-success.active, .open>.btn-success.dropdown-toggle
	{
	color: #fff;
	background-color: #49a54e;
	border-color: #469e4a;
	background-image: none;
}

.btn-success:active:hover, .btn-success:active:focus, .btn-success:active.focus,
	.btn-success.active:hover, .btn-success.active:focus, .btn-success.active.focus,
	.open>.btn-success.dropdown-toggle:hover, .open>.btn-success.dropdown-toggle:focus,
	.open>.btn-success.dropdown-toggle.focus {
	color: #fff;
	background-color: #3e8c42;
	border-color: #327035;
}

.btn-success.disabled:focus, .btn-success.disabled.focus, .btn-success:disabled:focus,
	.btn-success:disabled.focus {
	background-color: #66BB6A;
	border-color: #66BB6A;
}

.btn-success.disabled:hover, .btn-success:disabled:hover {
	background-color: #66BB6A;
	border-color: #66BB6A;
}

.btn-white, .btn-default {
	color: #818a91;
	background-color: #fff;
	border-color: #eceeef;
}

.card-footer {
	padding: 0.75rem 1.25rem;
	background-color: #f5f5f5;
	border-top: 1px solid #eee;
}

.card .card-header, .card .card-footer {
	padding: .625rem;
	color: rgba(0, 0, 0, 0.84);
}

.card .card-header .media, .card .card-footer .media {
	line-height: inherit;
}

.card-footer:last-child {
	border-radius: 0 0 3px 3px;
}

.card .card-title {
	font-size: 1.25rem;
	margin-bottom: .625rem;
}

.card .card-subtitle {
	color: #818a91;
}

.card .card-title+.card-subtitle {
	margin-top: -.625rem;
}

.card .card-header .card-title, .card .card-header .card-subtitle {
	margin: 0 !important;
}

.card .card-header .media-body {
	line-height: 1.5;
}

.card-red {
	background-color: #F44336;
	border-color: #F44336;
}

.card-success .progress[value]::-webkit-progress-value {
	background-color: #469e4a;
}

.card-success .progress[value]::-moz-progress-bar {
	background-color: #469e4a;
}

.c-input {
	position: relative;
	display: inline;
	padding-left: 1.5rem;
	color: #555;
	cursor: pointer;
	font-size: 16px;
}

.c-input>input {
	position: absolute;
	z-index: -1;
	opacity: 0;
}

.c-input>input:checked ~.c-indicator {
	color: #fff;
	background-color: #d2232a;
}

.c-input>input:focus ~.c-indicator {
	box-shadow: 0 0 0 .075rem #fff, 0 0 0 .2rem #0074d9;
}

.c-input>input:active ~.c-indicator {
	color: #fff;
	background-color: #84c6ff;
}

.c-input+.c-input {
	margin-left: 1rem;
}

.c-indicator {
	position: absolute;
	top: 0;
	left: 0;
	display: block;
	width: 1rem;
	height: 1rem;
	font-size: 65%;
	line-height: 1rem;
	color: #eee;
	text-align: center;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
	background-repeat: no-repeat;
	background-position: center center;
	background-size: 50% 50%;
	background-color: lightgrey;
}

.c-checkbox .c-indicator {
	border-radius: .25rem;
}

.c-checkbox input:checked ~.c-indicator {
	background-image:
		url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxNy4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+DQo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4Ig0KCSB2aWV3Qm94PSIwIDAgOCA4IiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCA4IDgiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZmlsbD0iI0ZGRkZGRiIgZD0iTTYuNCwxTDUuNywxLjdMMi45LDQuNUwyLjEsMy43TDEuNCwzTDAsNC40bDAuNywwLjdsMS41LDEuNWwwLjcsMC43bDAuNy0wLjdsMy41LTMuNWwwLjctMC43TDYuNCwxTDYuNCwxeiINCgkvPg0KPC9zdmc+DQo=);
}

.c-checkbox input:indeterminate ~.c-indicator {
	background-color: #0074d9;
	background-image:
		url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxNy4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+DQo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4Ig0KCSB3aWR0aD0iOHB4IiBoZWlnaHQ9IjhweCIgdmlld0JveD0iMCAwIDggOCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgOCA4IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxwYXRoIGZpbGw9IiNGRkZGRkYiIGQ9Ik0wLDN2Mmg4VjNIMHoiLz4NCjwvc3ZnPg0K);
}

.c-radio .c-indicator {
	border-radius: 50%;
}

.c-radio input:checked ~.c-indicator {
	background-image:
		url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxNy4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+DQo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4Ig0KCSB2aWV3Qm94PSIwIDAgOCA4IiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCA4IDgiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZmlsbD0iI0ZGRkZGRiIgZD0iTTQsMUMyLjMsMSwxLDIuMywxLDRzMS4zLDMsMywzczMtMS4zLDMtM1M1LjcsMSw0LDF6Ii8+DQo8L3N2Zz4NCg==);
}

.c-inputs-stacked .c-input {
	display: inline;
}

.c-inputs-stacked .c-input::after {
	display: block;
	margin-bottom: .25rem;
	content: "";
}

.c-inputs-stacked .c-input+.c-input {
	margin-left: 0;
}

.c-select {
	display: inline-block;
	max-width: 100%;
	padding: .375rem 1.75rem .375rem .75rem;
	padding-right: .75rem \9;
	color: #55595c;
	vertical-align: middle;
	background: #fff
		url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAUCAMAAACzvE1FAAAADFBMVEUzMzMzMzMzMzMzMzMKAG/3AAAAA3RSTlMAf4C/aSLHAAAAPElEQVR42q3NMQ4AIAgEQTn//2cLdRKppSGzBYwzVXvznNWs8C58CiussPJj8h6NwgorrKRdTvuV9v16Afn0AYFOB7aYAAAAAElFTkSuQmCC)
		no-repeat right 0.75rem center;
	background-image: none \9;
	background-size: 8px 10px;
	border: 1px solid #eceeef;
	-moz-appearance: none;
	-webkit-appearance: none;
}

.c-select:focus {
	border-color: #51a7e8;
	outline: none;
}

.c-select::-ms-expand {
	opacity: 0;
}

.c-select-sm {
	padding-top: 3px;
	padding-bottom: 3px;
	font-size: 12px;
}

.c-select-sm


:not

 

(
[
multiple
]

 

)
{
height


:

 

26
px


;
min-height


:

 

26
px


;
}
.form-group {
	margin-bottom: 1rem;
}

.media-left, .media-right, .media-body {
	display: table-cell;
	vertical-align: top;
}

.media-left {
	padding-right: 10px;
}

.media-middle {
	vertical-align: middle;
}

.m-b-0 {
	margin-bottom: 0 !important;
}

.bg-white {
	background-color: #ffffff;
}

.p-a-1 {
	padding: 1rem 1rem !important;
}

.card-header {
	padding: 0.75rem 1.25rem;
	background-color: #f5f5f5;
	border-bottom: 1px solid #eee;
}

.container-fluid {
	margin-left: auto;
	margin-right: auto;
	padding-left: 0.625rem;
	padding-right: 0.625rem;
}

.card {
	position: relative;
	display: block;
	margin-bottom: 0.75rem;
	background-color: #fff;
	border: 1px solid #eee;
	border-radius: 3px;
}

.card-block {
	padding: 1.25rem;
}

.center {
	text-align: center;
}

.questionNoSpan{
}

.testQuestionTextSpan, .testQuestionTextSpan > * {
  font-size: inherit;
  
}

.testQuestionTextSpan{
  overflow : visible;
    white-space: pre-wrap;       /* Since CSS 2.1 */
    white-space: -moz-pre-wrap;  /* Mozilla, since 1999 */
    white-space: -pre-wrap;      /* Opera 4-6 */
    white-space: -o-pre-wrap;    /* Opera 7 */
    word-wrap: break-word;   
  
}

@media ( min-width : 544px) {
	.card-group {
		display: table;
		width: 100%;
		table-layout: fixed;
	}
	.card-group .card {
		display: table-cell;
		vertical-align: top;
	}
	.card-group .card+.card {
		margin-left: 0;
		border-left: 0;
	}
	.card-group .card:first-child {
		border-bottom-right-radius: 0;
		border-top-right-radius: 0;
	}
	.card-group .card:first-child .card-img-top {
		border-top-right-radius: 0;
	}
	.card-group .card:first-child .card-img-bottom {
		border-bottom-right-radius: 0;
	}
	.card-group .card:last-child {
		border-bottom-left-radius: 0;
		border-top-left-radius: 0;
	}
	.card-group .card:last-child .card-img-top {
		border-top-left-radius: 0;
	}
	.card-group .card:last-child .card-img-bottom {
		border-bottom-left-radius: 0;
	}
	.card-group
	
	 
	
	.card
	
	
	:not
	
	 
	
	(
	:first-child
	
	 
	
	)
	:not
	
	 
	
	(
	:last-child
	
	 
	
	)
	{
	border-radius
	
	
	:
	
	 
	
	0;
}

.card-group .card:not (:first-child ):not (:last-child ) .card-img-top,
	.card-group .card:not (:first-child ):not (:last-child )
	.card-img-bottom {
	border-radius: 0;
}

}
.card-group {
	margin-bottom: 1.25rem;
}

.m-b-0 {
	margin-bottom: 0 !important;
}

.text-muted-light {
	color: #b7bdc1;
}

.blackBorder {
	border: 2px solid black;
}

.questionSpan {
	font-size: 20px;
}

.testDiv {
	
}

.textAlignCenter {
	text-align: center;
}

.questionMainDiv {
	display: none;
}

.showQuestionMainDiv {
	display: block;
	webkit-animation: fadein 0.5s;
	animation: fadein 0.5s;
}

.hideQuestionMainDiv {
	display: none;
	webkit-animation: fadeout 0.5s 2.5s;
	animation: fadeout 0.5s 2.5s;
}

.showElement {
	display: block !important;
}

.hideElement {
	display: none !important;
}

.visibilityHidden {
	visibility: hidden;
}

.visibilityVisible {
	visibility: visible;
}

#testTimerDiv {
	
}

.vcenter {
	display: inline-block !important;
	vertical-align: middle !important;
	float: none;
}

.timerNumbers {
	font-family: "Open Sans";
	font-weight: 600;
	color: #000;
}

#subQuestionsContainer1 {
	overflow: scroll;
	height: 350px;
	!
	important;
}

#subQuestionsContainer1::-webkit-scrollbar {
	width: 12px;
}

#subQuestionsContainer1::-webkit-scrollbar-thumb {
	background-color: #d2232a;
	border-radius: 15px;
}

#lostFocusPopupMessage  {
	visibility: hidden;
	/* Hidden by default. Visible on click */
	width: 70%;
	/* Set a default minimum width */
	/* Divide value of min-width by 2 */
	/* Black background color */
	color: #fff;
	/* White text color */
	text-align: center;
	/* Centered text */
	border-radius: 2px;
	/* Rounded borders */
	padding: 16px;
	/* Padding */
	position: fixed;
	/* Sit on top of the screen */
	z-index: 1;
	/* Add a z-index if needed */
	/* Center the snackbar */
	top: 30px;
	/* 30px from the top */
	left: 50%;
  transform: translateX(-50%);
}
#lostFocusPopupMessage.show {
	visibility: visible;
	/* Show the snackbar */
	/* Add animation: Take 0.5 seconds to fade in and out the snackbar. 
However, delay the fade out process for 2.5 seconds */
	-webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
	animation: fadein 0.5s, fadeout 0.5s 2.5s;
}

/* The snackbar - position it at the bottom and in the middle of the screen */
#snackbar {
	visibility: hidden;
	/* Hidden by default. Visible on click */
	min-width: 250px;
	/* Set a default minimum width */
	margin-left: -125px;
	/* Divide value of min-width by 2 */
	/* Black background color */
	color: #fff;
	/* White text color */
	text-align: center;
	/* Centered text */
	border-radius: 2px;
	/* Rounded borders */
	padding: 16px;
	/* Padding */
	position: fixed;
	/* Sit on top of the screen */
	z-index: 1;
	/* Add a z-index if needed */
	left: 50%;
	/* Center the snackbar */
	bottom: 30px;
	/* 30px from the bottom */
}

/* Show the snackbar when clicking on a button (class added with JavaScript) */
#snackbar.show {
	visibility: visible;
	/* Show the snackbar */
	/* Add animation: Take 0.5 seconds to fade in and out the snackbar. 
However, delay the fade out process for 2.5 seconds */
	-webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
	animation: fadein 0.5s, fadeout 0.5s 2.5s;
}

.snackbarSuccess {
	background-color: #388E3C;
}

.snackbarError {
	background-color: #F44336;
}

/* Animations to fade the snackbar in and out */
@
-webkit-keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
-webkit-keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}
@
keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}
.questionPrev {
	color: black !important;
}

.questionPrev:hover {
	background-color: #ccc !important;
}

.orangeLeftBorder {
	padding-left: 10px;
	border-left: 5px solid #337ab7;
	border-radius: 2px;
}

.greenLeftBorder {
	padding-left: 10px;
	border-left: 5px solid #49a54e;
	border-radius: 2px;
}

/* CUSTOM RADIO BUTTON START */

/* The inputContainer */
.inputContainer {
	display: inline;
	position: relative;
	padding-left: 35px;
	margin-bottom: 12px;
	cursor: pointer;
	font-size: 16px;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
}

/* Hide the browser's default radio button */
.inputContainer input {
	position: absolute;
	opacity: 0;
	cursor: pointer;
}

/* Create a custom radio button */
.radioCheckmark {
	position: absolute;
	top: 0;
	left: 0;
	height: 20px;
	width: 20px;
	background-color: #ccc;
	border-radius: 50%;
}

/* On mouse-over, add a grey background color */
.inputContainer:hover input ~.radioCheckmark {
	background-color: #ccc;
}

/* When the radio button is checked, add a custom background */
.inputContainer input:checked ~.radioCheckmark {
	background-color: #d2232a;
}

/* Create the indicator (the dot/circle - hidden when not checked) */
.radioCheckmark:after {
	content: "";
	position: absolute;
	display: none;
}

/* Show the indicator (dot/circle) when checked */
.inputContainer input:checked ~.radioCheckmark:after {
	display: block;
}

/* Style the indicator (dot/circle) */
.inputContainer .radioCheckmark:after {
	top: 7px;
	left: 7px;
	width: 6px;
	height: 6px;
	border-radius: 50%;
	background: white;
}

/* CUSTOMM RADIO BUTTON END */

/* CUSTOM CHECKBOX START */

/* Create a custom checkbox */
.checkmark {
	position: absolute;
	top: 0;
	left: 0;
	height: 20px;
	width: 20px;
	background-color: #ccc !important;
}

/* On mouse-over, add a grey background color */
.inputContainer:hover input ~.checkmark {
	background-color: #ccc !important;
}

/* When the checkbox is checked, add a blue background */
.inputContainer input:checked ~.checkmark {
	background-color: #d2232a;
}

/* Create the checkmark/indicator (hidden when not checked) */
.checkmark:after {
	content: "";
	position: absolute;
	display: none;
}

/* Show the checkmark when checked */
.inputContainer input:checked ~.checkmark:after {
	display: block;
}

/* Style the checkmark/indicator */
.inputContainer .checkmark:after {
	left: 7px;
	top: 3px;
	width: 10px;
	height: 18px;
	border: solid white;
	border-width: 0 3px 3px 0;
	-webkit-transform: rotate(45deg);
	-ms-transform: rotate(45deg);
	transform: rotate(45deg);
}
/
*


 


CUSTOM


 


CHEKCBOX


 


END


 


*




?
</style>
<body id="element" style="background-color: #ECE9E7;">
	<div id="toTop"></div>

	<div id="parentSpinnerDiv">

		<div id="childSpinnerDiv">
			<i class="fa fa-refresh fa-spin"
				style="font-size: 50px; color: grey;"></i>
		</div>

	</div>
	
	<div id="redBackgroundDiv">
	</div>
	
	
				
		<div id="contactAdminParentDiv">
	
			<div id="contactAdminChildDiv">
				<i id="showAlertAndCloseWindowIcon" class="fa-solid fa-triangle-exclamation"
					style="font-size: 50px; color: grey;"></i><br>
				<span id="showAlertAndCloseWindowText"  style="font-size: 16px; color: black;"></span>
				<br>
				<button id="closePageButton" class="btn btn-danger">Ok (<span id="showAlertAndCloseWindowTextTimeLeft"></span>)
				</button>
			</div>
	
		</div>
		
						
		<div id="reportLostFocusParentDiv">
	
			<div id="reportLostFocusChildDiv">
				
			<div class="container-fluid">
	
				<!-- <div class="row" >
					<span title="Close" class="closeReportLostFocusDiv"
						style="float: right; margin:5px 10px; padding: 5px 10px; border: 0px solid black; border-radius: 5px; background-color: white; cursor: pointer;">
						<i class="fa fa-close" style="font-size: 20px;"></i>
					</span>
				</div> -->
				
				
							<div class="row" style="width: 100%;">
								<div class="col-xs-12 col-sm-12 col-md-12  col-lg-12"
									style="padding-right: 5px; padding-left: 5px;">
									<div class="card">
										<div class="card-block center"
											style="padding-right: 10px; padding-left: 10px; padding-bottom: 10px; padding-top: 10px;">
											
											<i id="showAlertAndCloseWindowIcon" class="fa-solid fa-triangle-exclamation"
												style="font-size: 50px; color: grey;"></i><br><br>
												
											<h4 id ="reportRLFTitle" class="m-b-0">
												Submit Reason For Leaving/Switching From Test Page 
											</h4>
											
											<p id ="reportRLFSubTitle" ></p><br>
											
											<div id="reportRLFSelectReasonDiv" class="container-fluid">
											<div class="form-group">
												 <label for="reasonForLostFocus">Select / Type the reason to submit : </label>
   													<input list="reasonsForLostFocus" name="reasonForLostFocus" id="reasonForLostFocus" class="form-control inputForRLF" >
												<datalist id="reasonsForLostFocus">
												  <option value="Lost Focus of the test page due to my internet connection lost">
												  <option value="Lost Focus of the test page due to my System Hanged">
												  <option value="Lost Focus of the test page due to my System Crashed">
												  <option value="Restarted due to my internet  connection lost">
												  <option value="Restarted due to my System Hanged">
												  <option value="Restarted due to my System Crashed">
												</datalist>
												<span id="submitReasonForLostFocusErrorMessage" ></span>
											</div>
											</div>
											
											<div class="row" >
												<span
													style="float: right;">
													<button title="Close" class="btnForRLF btnForRLFClose closeReportLostFocusDiv" >Close, I'll report myself</button>
												</span>
												<span
													style="float: right;">
													<button id="submitReasonForLostFocus" title="Submit" class="btnForRLF btnForRLFSubmit submitReasonForLostFocusBtn" >Submit, Continue Test</button>
												</span>
											</div>
											
										</div>
									</div>
								</div>
								</div>
								
				
				
			</div>
				
			</div>
	
		</div>
	
	<%-- 
	<%@ include file="../common/header.jsp"%>
	 --%>


	<%-- <jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Exam;Online Assignment;Start Assignment" name="breadcrumItems" />
		</jsp:include> --%>


	<%-- <jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Test" name="activeMenu" />
				</jsp:include> --%>



	<%-- 
					<%@ include file="../common/studentInfoBar.jsp"%> --%>

	<div id="pageContainer" class="">


		<div id="element" style="width: 100%;"></div>


		<h2 style="margin-left: 15px; color: grey;" class="text-capitalize">${test.testName}
			- Subject : ${test.subject}</h2>
		<div class="clearfix"></div>
		<div>

			<!-- Code for page goes here start -->

			<div class="row" style="margin: 0px 0px; padding-bottom:70px; ">
				<div class="col-sm-12 " style="margin: 0px 0px;">

					<!-- Content -->
					<div class="layout-content" data-scrollable>
						<div class="container-fluid">

							<div class="row" style="width: 100%;">
								
								<span title="Toggle Fullscreen" id="go-button"
									style="float: right; margin: 0px 15px; padding: 2px 5px 0px 5px; border: 0px solid black; border-radius: 5px; background-color: white; cursor: pointer;">
									<i class="fa-solid fa-maximize" style="font-size: 20px;"></i>
								</span>
								
								<div title="Network Health" id="networkHealth"
									style="float: right; padding : 2px; border: 0px solid black; border-radius: 5px; background-color: white; width: 28px; height:24px;">
									
									
									<div  style=" float: right; background-color:white; margin: 0px 1px;  width: 4px; height:20px; border-radius: 5px;  " >
										<div id="4gSpan" style="   width: 100%; height:20px;" ></div>
									</div>
									
									<div id="3gSpan" style=" float: right; background-color:green; margin: 0px 1px;  width: 4px; height:20px; border-radius: 5px;  " >
										<div  style="background-color:white; width: 100%; height:4px;" ></div>
									</div>
									
									<div id="2gSpan" style=" float: right; background-color:green; margin: 0px 1px;  width: 4px; height:20px; border-radius: 5px;  " >
										<div  style="background-color:white; width: 100%; height:8px;" ></div>
									</div>
									
									<div id="slow-2gSpan" style=" float: right; background-color:green; margin: 0px 1px;  width: 4px; height:20px; border-radius: 5px;  " >
										<div  style="background-color:white; width: 100%; height:12px;" ></div>
									</div>
									
									
								</div>
							</div>

							<div class="row" style="width: 100%;">
								<div class="col-xs-6 col-sm-6 col-md-2  col-lg-2"
									style="padding-right: 5px; padding-left: 5px;">
									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-bottom: 5px; padding-top: 5px;">
											<h4 class="m-b-0">
												<strong id="attemptsCountSpan">${studentsTestDetails.attempt}
													/ ${test.maxAttempt}</strong>
											</h4>
											<small class="text-muted-light">ATTEMPT</small>
										</div>
									</div>
								</div>
								<div class="col-xs-6 col-sm-6 col-md-2  col-lg-2"
									style="padding-right: 5px; padding-left: 5px;">
									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-bottom: 5px; padding-top: 5px;">
											<h4 class="m-b-0">
												<strong id="noOfQuestionsSpan"></strong>
											</h4>
											<small class="text-muted-light">QUESTIONS</small>
										</div>
									</div>
								</div>

								<div class="col-xs-6 col-sm-6 col-md-2  col-lg-2"
									style="padding-right: 5px; padding-left: 5px;">

									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-bottom: 5px; padding-top: 5px;">
											<h4 class="text-success m-b-0">
												<strong id="attemptsSpan"></strong>
											</h4>
											<small class="text-muted-light">ANSWERED</small>
										</div>
									</div>
								</div>

								<div class="col-xs-6 col-sm-6 col-md-2  col-lg-2"
									style="padding-right: 5px; padding-left: 5px;">
									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-bottom: 5px; padding-top: 5px;">
											<h4 class="text-primary m-b-0">
												<strong id="attemptsLeft"></strong>
											</h4>
											<small class="text-muted-light">LEFT</small>
										</div>
									</div>
								</div>
								<div class="col-xs-12 col-sm-12 col-md-4  col-lg-4"
									style="padding-right: 5px; padding-left: 5px;">
									<!-- <div class="card">
													<div class="card-block center">
														<h4 class="text-primary m-b-0 " style="color: black; font-size:18px;">
															<strong id="testTime"> <span id="hoursSpan"
																class="timerNumbers"></span>h : <span id="minutesSpan"
																class="timerNumbers"></span>m : <span id="secondsSpan"
																class="timerNumbers"></span>s
															</strong>
														</h4>
														<small class="text-muted-light">Time left</small>
													</div>
												</div> -->

									<div class="card">
										<div class="card-block center"
											style="padding-right: 5px; padding-left: 5px; padding-top: 5px; padding-bottom: 5px;">
											<h4 class="text-primary m-b-0 "
												style="color: black; font-size: 18px; margin-top: 5px;">
												<strong id="testTime"> <i
													id="bookmarkIcon-${testQuestion.id}" class="fa-regular fa-clock"
													style="font-size: 25px;"></i> <span id="hoursSpan"
													class="timerNumbers"></span>h : <span id="minutesSpan"
													class="timerNumbers"></span>m : <span id="secondsSpan"
													class="timerNumbers"></span>s
												</strong>

												<button id="submitTest" class="btn btn-rounded "
													style="background-color: #d2232a;" disabled>Finish
													Assignment</button>

											</h4>

										</div>
									</div>
								</div>
							</div>



							<div class="row" style="width: 100%;">

								<!-- questions section -->
								<div class="col-md-10">
									<div id="progressDiv"
										style="width: 0px; height: 10px; background-color: #4bb543; border-radius: 5px; padding-left: 10px">
									</div>

									<c:forEach var="testQuestion" items="${testQuestions}"
										varStatus="status">

										<form id="studentTestForm-${status.count}">
											<div id="questionMainDiv-${status.count}"
												class="questionMainDiv">

												<div class="card">
													<div class="card-header bg-white p-a-1"
														style="background-color: #ffffff">
														<div id="prevNextDiv" class="">
															<%--<button type="submit"
																					class="prevnext next  nextQuestionButton"
																					style="text-transform: none; 
																							margin:0px 0px 0px 5px;
																							padding: 8px 16px;
																							font-size: 0.9rem;
																							font-weight: unset;
																							"
																						<c:if test="${status.count==noOfQuestions}">disabled</c:if>
																					>Next &raquo;</button>--%>

															<%-- <a href="#" id="nextQuestionButton-${status.count}"
																					class="prevnext next  nextQuestionButton">
																				Skip &raquo;
																				</a> --%>
															<%-- <button
																					id="previousQuestionButton-${status.count}"
																					style="text-transform: none; 
																							margin:0px;
																							padding: 8px 16px;
																							font-size: 0.9rem;
																							font-weight: unset;
																							"
																					<c:if test="${status.count==1}">disabled</c:if>
																					class="prevnext previous  previousQuestionButton" 
																					>
																				&laquo; Previous
																				</button> --%>
															<a onclick="markForReview(${testQuestion.id})"
																data-toggle="tooltip" class="prevnext previous round"
																style="float: right;" title="Tag For Review">
																<i id="bookmarkIcon-${testQuestion.id}"
																class="fa fa-bookmark-o" style="font-size: 25px;"></i>
															</a>
														</div>
														<br />
														<div class="media" style="width: 100%">
															<div class="media-middle">
																<h4 class="card-title"
																	id="questionMainDiv-${status.count}"
																	class="questionMainDiv">

																	<i id="answerAttemptedTick-${testQuestion.id}" class=""
																		style="color: green;"></i> <span
																		class="questionNoSpan">Q${status.count}.  </span> <br> <span class="testQuestionTextSpan">${testQuestion.question}</span>
																</h4>
																<!-- display name of type of question start -->
																<small style="color: #708090;">Question Type : <c:choose>
																		<c:when test="${testQuestion.type == 1}">
																			Single Select, Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:when test="${testQuestion.type == 2}">
																			Multiple Select  (Can select one or more than one right option), Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:when test="${testQuestion.type == 3}">
																			Case Study, Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:when test="${testQuestion.type == 4}">
																			Descriptive, Weightage : ${testQuestion.marks} 
																		</c:when>
																		<c:when test="${testQuestion.type == 5}">
																			True Or False, Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:when test="${testQuestion.type == 8}">
																			Assignment, Weightage : ${testQuestion.marks}
																		</c:when>
																		<c:otherwise>
																			NA
																		</c:otherwise>
																	</c:choose>
																</small>
																<!-- display name of type of question end -->

															</div>
														</div>
													</div>


													<c:choose>
														<c:when test="${testQuestion.type == 8}">

															<input type="hidden" value="${testQuestion.id}"
																id="hiddenQuestionId-${status.count}">
															<input type="hidden" value="" id="hiddenAssngUrl">
															<input type="hidden" value="${testQuestion.uploadType}" id="hiddenUploadType">
															<div class="card-block p-a-2"
																style="background-color: #ffffff padding-bottom: 0px;">

																	<!--   <div class="col-sm-1" style=" "></div>  -->
																	<%-- <div class="col-sm-12" style=" "> 

																			
													                      	   <iframe 
													                      	   		style="width:100%; min-height: 500px; "
													                      	   		src='${testQuestion.url}' 
													                      	   		frameborder='0'
																			      	webkitAllowFullScreen mozallowfullscreen allowFullScreen
																			   ></iframe>
																			
																			</div> --%>
																	<!--  	<div class="col-sm-1" style=" "></div>   -->
																	<!--  <div class="col-sm-1" style=" "></div>  -->
																	<c:if test="${testQuestion.question ne '' && testQuestion.uploadType == 'mp4' && testQuestion.url  == ''}">
																		<div class="row p-a-1"> 
																			<label for="link-location-text-link">Question:</label>
																				<p id="questionLink1" >${testQuestion.question}</p>
																		</div>
																	</c:if>
																	
																	<c:if test="${testQuestion.url  ne ''}">
																	<div class="row p-a-1"> 
																		<label for="link-location-text-link">Question
																			File: </label>
																			<a id="questionLink2"
																				href="${testQuestion.url}" target="_blank">Download
																				Question File</a>
																	</div>
																	</c:if>
																	
															</div>

															<div class="card-block p-a-2"
																style="background-color: #ffffff padding-bottom: 0px;">
																<div class="row">
																	<div>
																		<label for="link-location-text-link"
																			class="col-xs-3 col-md-1 col-sm-1 control-label">File
																			Upload: </label>
																		<div class="col-md-5 col-sm-10 col-xs-12">

																			<input type="file" class="input-file-btn"
																				id="file-link"
																				style="color: black; background-color: lightgrey" /><span id="uploadingAnswerMessage"></span>

																		</div> 
																	</div>
																</div>
																<br>

																<div class="row">
																	<!-- 	<div class="col-sm-3" style=" "></div> -->
																	<!-- 	<div id="answerFile" style="display:none" > -->
																	<!--  <label for="link-location-text-link" class="col-xs-3 col-md-1 col-sm-1 control-label">Check file: 
																					</label>  -->
																	<div class="col-sm-10" id="answerFile"
																		style="display: none">
																		<!-- 		 <input type="text" name="link-location-text" id="answerLink" class="form-control"
																						 placeholder="Enter File Location URL" readonly> -->
																		<!-- <a id="answerLink" href="">Download Answer File</a>
																						 <div class="col-sm-12" style=" ">  -->
																	<label >Check file uploaded: 
																					</label>
																	<c:if test="${testQuestion.uploadType == 'pdf'}">			
																		<iframe id="answerLink"
																			style="width: 100%; min-height: 500px;" src=''
																			frameborder='0' webkitAllowFullScreen
																			mozallowfullscreen allowFullScreen></iframe>
																		</c:if>
																		<c:if test="${testQuestion.uploadType == 'mp4'}">	
																		
																			  <embed id="answerLink" src="" showcontrols="true" style="width: 100%; min-height: 500px;" type="video/webm" ></embed>
																		</c:if>	
																	</div>
																	<!-- 	</div>  -->
																</div>


															</div>
											<!-- 	</div> -->


												</c:when>
												<c:when test="${testQuestion.type == 7}">

													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-${status.count}">


													<div class="card-block p-a-2"
														style="background-color: #ffffff padding-bottom: 0px;">
														<div class="row">

															<div class="col-sm-3" style=""></div>
															<div class="col-sm-6" style="">
																<iframe style="width: 100%; min-height: 400px;"
																	src='${testQuestion.url}' frameborder='0'
																	webkitAllowFullScreen mozallowfullscreen
																	allowFullScreen></iframe>
															</div>

															<div class="col-sm-3" style=""></div>
														</div>


													</div>

													<div class="card-footer clearfix"
														id="options${testQuestion.id}">
														<div class="form-group">

															<c:forEach var="tempOption"
																items="${testQuestion.optionsList}"
																varStatus="optionStatus">

																<label class="c-input c-checkbox">${tempOption.optionData}

																	<input type="checkbox" id="checkField${status.count}1"
																	value="${tempOption.id}"
																	<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																	onclick="checkFields('${testQuestion.id}',false)">
																	<span class="c-indicator"></span>
																</label>
																<br>
																<br>
															</c:forEach>


														</div>


													</div>
												</c:when>
												<c:when test="${testQuestion.type == 6}">

													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-${status.count}">


													<div class="card-block p-a-2"
														style="background-color: #ffffff padding-bottom: 0px;">
														<div class="col-sm-12 questionImageContainer">
															<img class="questionImage" alt="Question Picture"
																src="${testQuestion.url}">

														</div>


													</div>

													<div class="card-footer clearfix"
														id="options${testQuestion.id}">
														<div class="form-group">

															<c:forEach var="tempOption"
																items="${testQuestion.optionsList}"
																varStatus="optionStatus">


																<label class="c-input c-checkbox">${tempOption.optionData}

																	<input type="checkbox" id="checkField${status.count}1"
																	value="${tempOption.id}"
																	<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																	onclick="checkFields('${testQuestion.id}',false)">
																	<span class="c-indicator"></span>
																</label>
																<br>
																<br>

															</c:forEach>


														</div>


													</div>
												</c:when>
												<c:when test="${testQuestion.type == 5}">

													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-${status.count}">

													<div class="card-block p-a-2" style="padding-bottom: 0px;"
														id="options${testQuestion.id}">
														<div class="form-group">

															<c:forEach var="tempOption"
																items="${testQuestion.optionsList}"
																varStatus="optionStatus">
																<c:choose>
																	<c:when test="${testQuestion.type == 5}">

																		<label class="inputContainer">${tempOption.optionData}


																			<input type="radio"
																			name="selectedOption${testQuestion.id}"
																			id="checkField${status.count}1"
																			value="${tempOption.id}"
																			<c:if test = "${tempOption.selected == 'Y' }">
																							 checked 
																							</c:if>
																			onclick="checkFields('${testQuestion.id}',false)">

																			<span class="radioCheckmark"></span>
																		</label>
																		<br />
																		<br />

																	</c:when>
																</c:choose>
															</c:forEach>


														</div>


													</div>
													<!-- <div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div>-->
												</c:when>
												<c:when test="${testQuestion.type == 4}">
													<div class="">
														<input type="hidden" value="${testQuestion.id}"
															id="hiddenQuestionId-${status.count}">

														<div class="card-block p-a-2" style="padding-bottom: 0px;">

															<span class="text-muted" style=""
																id="characterCount-${testQuestion.id}"></span>

															<span class="text-muted" style=""
																id="saveDescriptiveAlert-${testQuestion.id}"></span>
															<div class="form-group">
																<textarea
																	style="border: 1px solid grey; padding: 5px 10px; width: 100%;"
																	id="textArea-${testQuestion.id}" rows="10" wrap="hard"
																	maxlength="50000" onDrop="blur(); return false;"
																	required 
																	autofocus>${testQuestion.answer}</textarea>
															</div>
														</div>
														<!-- <div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div> -->
													</div>
												</c:when>
												<c:when test="${testQuestion.type == 3}">

													<!-- Display subQuestions if question type is 3 i.e. caseStudy -->
													<div id="subQuestionsContainer">
														<div class="well">
															<p style="font-size: 16px;">
																${testQuestion.description}</p>
														</div>

														<div class="well">
															<h5>Questions :</h5>
															<input type="hidden" value="${testQuestion.id}"
																id="hiddenQuestionId-${status.count}">

															<!-- literate through casestudy questions start -->
															<c:forEach var="subQuestion"
																items="${testQuestion.subQuestionsList }"
																varStatus="sQStatus">


																<div class="card">
																	<div
																		class="card-header bg-white p-a-1 orangeLeftBorder"
																		id="subQuestionH4-${subQuestion.id}"
																		style="background-color: #ffffff">
																		<div class="media">
																			<div class="media-body  media-middle">
																				<h4 class="card-title" class="questionMainDiv">

																					<a href="#demo${subQuestion.id}" class=""
																						data-toggle="collapse"
																						style="color: black; font-size: 18px;">
																						<b>Q${sQStatus.count} : </b>
																						${subQuestion.question }
																					</a><br>

																				</h4>
																			</div>
																		</div>
																	</div>
																</div>

																<div id="demo${subQuestion.id}" class="collapse in">

																	<!-- start -->
																	<div class="card-block p-a-2"
																		style="padding-bottom: 0px;"
																		id="options${testQuestion.id}-${subQuestion.id}">
																		<div class="form-group">

																			<c:forEach var="tempOption"
																				items="${subQuestion.optionsList}"
																				varStatus="optionStatus">
																				<c:choose>
																					<c:when test="${subQuestion.type == 1}">

																						<label class="inputContainer">${tempOption.optionData}


																							<input type="radio"
																							name="selectedOption${subQuestion.id}"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																							onclick="checkFields('#subQuestionH4-${subQuestion.id}',true)">

																							<span class="radioCheckmark"></span>
																						</label>
																						<br />
																						<br />

																					</c:when>
																					<c:when test="${subQuestion.type == 2}">
																						<label class="c-input c-checkbox">${tempOption.optionData}

																							<input type="checkbox"
																							id="checkField${status.count}1"
																							value="${tempOption.id}"
																							<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																							onclick="checkFields('#subQuestionH4-${subQuestion.id}',true)">
																							<span class="c-indicator"></span>
																						</label>
																						<br>
																						<br>

																					</c:when>
																				</c:choose>
																			</c:forEach>


																		</div>


																	</div>
																	<!-- end -->
																</div>
															</c:forEach>

															<!-- literate through casestudy questions end -->

															<!-- <div class="card-footer clearfix "
																		style="background-color: #ffffff">
																		<div class="col-sm-12">

																			<div class="col-sm-6">
																			</div>


																		</div>


																	</div> -->


														</div>


													</div>


												</c:when>
												<c:otherwise>
													<input type="hidden" value="${testQuestion.id}"
														id="hiddenQuestionId-${status.count}">

													<div class="card-block p-a-2" style="padding-bottom: 0px;"
														id="options${testQuestion.id}">
														<div class="form-group">

															<c:forEach var="tempOption"
																items="${testQuestion.optionsList}"
																varStatus="optionStatus">
																<c:choose>
																	<c:when test="${testQuestion.type == 1}">

																		<label class="inputContainer">${tempOption.optionData}


																			<input type="radio"
																			name="selectedOption${testQuestion.id}"
																			id="checkField${status.count}1"
																			value="${tempOption.id}"
																			<c:if test = "${tempOption.selected == 'Y' }">
																							 checked 
																							</c:if>
																			onclick="checkFields('${testQuestion.id}',false)">

																			<span class="radioCheckmark"></span>
																		</label>
																		<br />
																		<br />

																	</c:when>
																	<c:when test="${testQuestion.type == 2}">
																		<label class="c-input c-checkbox">${tempOption.optionData}

																			<input type="checkbox"
																			id="checkField${status.count}1"
																			value="${tempOption.id}"
																			<c:if test = "${tempOption.selected == 'Y'}">
																							 checked 
																							</c:if>
																			onclick="checkFields('${testQuestion.id}',false)">
																			<span class="c-indicator"></span>
																		</label>
																		<br>
																		<br>

																	</c:when>
																</c:choose>
															</c:forEach>


														</div>


													</div>
												</c:otherwise>
												</c:choose>



												<div class="card-footer clearfix "
													style="background-color: #ffffff padding-top: 0px;">
													<div class="col-sm-12">

														<div class="col-sm-6">

															<%-- <button
																							id="previousQuestionButton-${status.count}"
																							style="text-transform: none; 
																									margin:0px;
																									padding: 8px 16px;
																									font-size: 0.9rem;
																									font-weight: unset;
																									"
																							<c:if test="${status.count==1}">disabled</c:if>
																							class="prevnext previous  previousQuestionButton" 
																							>
																						&laquo; Previous
																						</button> --%>

															<!-- <h1 id="LFAT" ></h1> -->
															<button type="submit" id="saveButton-${status.count}" class="btn-success"
																style="text-transform: none; margin: 0px 0px 0px 5px; padding: 8px 16px; font-size: 0.9rem; font-weight: unset;">Save</button>
															<c:if test="${testQuestion.type ne 8}"> 

																<button class="btn-success saveNNextButton"
																	style="text-transform: none; margin: 0px 0px 0px 5px; padding: 8px 16px; font-size: 0.9rem; font-weight: unset;">Save
																	&amp; Next</button>
															</c:if>
															<span class="text-muted offlineAlertSpan" style=""
																></span>
															<span class="text-muted networkHealthSpan" style=""
																></span>
															 
														</div>



													</div>



												</div>



											</div>
								</div>
								</form>

								</c:forEach>

	
							</div>
							<!-- End of questions section -->
							<c:choose>  
							<c:when test="${sectionHashMap.size() > 0}">  
								
								<div class=" col-md-2" style="padding: 5px 2px;">
									<div class="card">
										<div class="">
											<div class="" id="questionsPreviewSection">
											<c:forEach var="section" items="${sectionHashMap}" >	
												
												<p id="sectionName">${section['key']} </p>
												
												<div class="">
												<div class="row ">
													<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"
														style="font-size: 12px;"></div>
													<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5"
														style="font-size: 12px;">Question No.</div>
													<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4"
														style="font-size: 12px;">Status</div>
													<div class="col-xs-1" style="font-size: 12px;"></div>
												</div>
											</div>




											<c:forEach var="testQuestion" items="${section['value']}"
												varStatus="status">

												<div class="col-xs-12"
													style="cursor: pointer; padding-bottom: 5px;; padding-top: 5px;"
													onclick="jumpToQuestion(${testQuestion.srNoForSections})">
													<div class="row">

														<div class="col-xs-1">
															<div class="">
																<i id="bookmarkIconInQuestionStatus-${testQuestion.id}"
																	class="fa fa-bookmark-o"
																	style="display: inline; font-size: 18px;"></i>
															</div>
														</div>
														<div class="col-xs-3">

															<div class="text-muted-light"
																style="color: black; font-size: 16px;">
																<a href="#" onclick="jumpToQuestion(${testQuestion.srNoForSections})">
																	Q${testQuestion.srNoForSections} </a>
															</div>

														</div>

														<div class="col-xs-6">
															<div id="statusQuestion${testQuestion.srNoForSections}"
																class="label left_status">Unattempted</div>

														</div>

														<div class="col-xs-2"></div>



													</div>

												</div>
											</c:forEach>

											</c:forEach>
												
												
											</div>
										</div>
									</div>
								</div>
								
							</c:when> 
							<c:otherwise>
							<div class=" col-md-2" style="padding: 5px 2px;">
								<div class="card">

									<div class="">
										<div class="" id="questionsPreviewSection">

											<div class="">
												<div class="row ">
													<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1"
														style="font-size: 12px;"></div>
													<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5"
														style="font-size: 12px;">Question No.</div>
													<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4"
														style="font-size: 12px;">Status</div>
													<div class="col-xs-1" style="font-size: 12px;"></div>
												</div>
											</div>




											<c:forEach var="testQuestion" items="${testQuestions}"
												varStatus="status">

												<div class="col-xs-12"
													style="cursor: pointer; padding-bottom: 5px;; padding-top: 5px;"
													onclick="jumpToQuestion(${status.count})">
													<div class="row">

														<div class="col-xs-1">
															<div class="">
																<i id="bookmarkIconInQuestionStatus-${testQuestion.id}"
																	class="fa fa-bookmark-o"
																	style="display: inline; font-size: 18px;"></i>
															</div>
														</div>
														<div class="col-xs-3">

															<div class="text-muted-light"
																style="color: black; font-size: 16px;">
																<a href="#" onclick="jumpToQuestion(${status.count})">
																	Q${status.count} </a>
															</div>

														</div>

														<div class="col-xs-6">
															<div id="statusQuestion${status.count}"
																class="label left_status">Unattempted</div>

														</div>

														<div class="col-xs-2"></div>



													</div>

												</div>
											</c:forEach>

											<%-- 	<ul class="list-group m-b-0">

								<li class="list-group-item" style="font-size : 12px !important;">
										<div class="">
											<div class="media-left">Question No.</div>
											<div class="media-body textAlignCenter">
												<span class="textAlignCenter">Status</span>
											</div>
										</div>
									</li>


									<c:forEach var="testQuestion" items="${testQuestions}"
										varStatus="status">

										<li class="list-group-item" style="cursor: pointer"
											onclick="jumpToQuestion(${status.count})">
											<div class="media">
												<div class="media-body">
													<div class="text-muted-light" style = "color:black; font-size : 16px;">
													<a href="#"	
													 onclick="jumpToQuestion(${status.count})">
													Q${status.count}
													</a>
													</div>
												</div>
												<div class="media-body" style = "color:black; font-size : 16px;">${testQuestion.question}</div>
												<div class="media-right">
												<i id="bookmarkIconInQuestionStatus-${testQuestion.id}" class="fa fa-bookmark-o" style = "display: inline ; font-size : 18px;" ></i>
												</div> 
												<div class="media-right">
													<span id="statusQuestion${status.count}"
														class=" left_status">Unattempted</span>
												</div>
											</div>
										</li>
									</c:forEach>
								</ul> --%>
										</div>
									</div>
									<!-- 
									<button class="btnForRLF btnForRLFSubmit showReportLostFocusDiv" > Report Lost Focus </button>
									 -->
								</div>
							</div>
							</c:otherwise>
							</c:choose>  
							
							
							</div>

						</div>


						<!-- <div class="row">
											<div class="col-lg-9 col-md-9  col-sm-12" style="">
												<button id="submitTest" class="btn btn-rounded pull-right"
													style="background-color: #d2232a; ">Finish Assignment</button>
											</div>
											</div> -->

					</div>
				</div>


			</div>
		</div>


		<!-- Code for calculator start -->
		<c:if test="${test.showCalculator == 'Y' }">
		<a href="#" title="Toggle Calculator" id="toggleCalculatorButton"><i id="toggleCalculatorButtonIcon" class="fa-solid fa-calculator" style="font-size:24px"></i></a>
		
		
			
		<div id="calculatorDiv">

				
 <div class="calculator">
        <input type="text" id="screen" maxlength="20">
        <div class="calc-buttons">

          

<div class="functions-one">
        <button class="button triggers">C</button>
        <button class="button basic-stuff">(</button>
        <button class="button basic-stuff">)</button>
        <button class="button numbers">7</button>
        <button class="button numbers">8</button>
        <button class="button numbers">9</button>
        <button class="button numbers">4</button>
        <button class="button numbers">5</button>
        <button class="button numbers">6</button>
        <button class="button numbers">1</button>
        <button class="button numbers">2</button>
        <button class="button numbers">3</button>
        <button class="button basic-stuff">+/-</button>
        <button class="button numbers">0</button>
        <button class="button basic-stuff">.</button>
</div>
    



        <div class="functions-two">
            <button class="button basic-stuff">+</button>
            <button class="button basic-stuff">-</button>
            <button class="button basic-stuff">*</button>
            <button class="button basic-stuff">/</button>
            <button class="button triggers">&#60;=</button>
            <button class="button complex-stuff">%</button>
            <button class="button complex-stuff">x!</button>
            <button class="button complex-stuff">x^</button>
            <button class="button complex-stuff">ln</button>
            <button class="button complex-stuff">e</button>
            <button class="button complex-stuff">sq</button>
            <button class="button complex-stuff">sqrt</button>
            <button class="button complex-stuff">sin</button>
            <button class="button complex-stuff">cos</button>
            <button class="button complex-stuff">tan</button>
            <button class="button complex-stuff">log</button>
            <button class="button triggers">=</button>
            <button class="button complex-stuff">pi</button>
            
            <button class="button complex-stuff">rad</button> 
            <button class="button complex-stuff">o</button>
        </div>
   
      </div>
    </div>
	
		</div>
		</c:if>
		<!-- Code for calculator end -->
		
		
		<!-- Code for scrible pad start
		
		<a href="#" id="toggleScribblePadButton"><i id="toggleScribblePadButtonIcon" class="fa fa-pencil-square-o" style="font-size:24px"></i></a>
		
		<div id="scriblePadDiv">
		
				<div style="float:left;" id="buttons">
      <input type="button" id="clear" value="Clear">
    </div>

<div style="float:left;" id="buttonss">
      <input type="button" id="bgcolor-button" value="Change Color">
    </div>
<label for="fontSize" class="brush-size">Brush Size:</label> </br>
  <input class="range" type="range" name="fontSize" min="1" max="200" value="16" data-sizing="px">

<canvas id="draw" width="800" height="800"></canvas>
		
		</div>
		 Code for scrible pad end -->

		<!-- Code for page goes here end -->
	</div>
	</div>

	<div id="snackbar">Some text some message..</div>
	<div id="lostFocusPopupMessage">You have lost focus of the page. Please Click on the page to get back. You will be marked for Copy Case. </div>
	
	<button id="reportRLFPopAlert" class="blinkingButton" >Report Lost Focus</button>
	
	
	<%-- 
	<jsp:include page="../common/footer.jsp" />
 --%>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-3.4.1.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>

	<!-- Commented sweetalert1 js 
<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
-->

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/sweatalert/sweetalert2_v9.js"></script>
	<!--
	<script src="assets/js/ckeditor/ckeditor.js"></script>
	-->
	<!-- 	
	<script src="assets/js/ckeditor/MathJax.js"></script>
	 -->
	<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML"></script>
	
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/CustomCalc.js"></script>
	<!-- 
	<script src="assets/js/CustomScriblePad.js"></script>
	 -->
	<script type="text/javascript">

function testHide(){
//////////////console.log('call')
	$("#questionMainDiv-1").removeClass("showQuestionMainDiv");
	$("#questionMainDiv-1").addClass("hideQuestionMainDiv");	
}
function testShwo(){
	$("#questionMainDiv-1").removeClass("hideQuestionMainDiv");
	$("#questionMainDiv-1").addClass("showQuestionMainDiv");
}



	$(document).ready(function() {

		 try{

	  	   ////////////console.log("testSubmitted");
	  	  
   	   ////////////console.log(localStorage.getItem("testSubmitted"));

	  	   //////////////console.log("testSubmitted");

   	   //////////////console.log(localStorage.getItem("testSubmitted"));


	  	   //////////////console.log("testSubmitted");

   	   //////////////console.log(localStorage.getItem("testSubmitted"));

  	    if(localStorage.getItem("testSubmitted") === "Yes"){

  	   	//alert(localStorage.getItem("testSubmitted"));
       	//window.location.href = "/exam/testOverPage";
       	window.location.href = "/exam/mbax/ia/s/viewTestDetailsForStudentsForAllViews?userId=${userId}&id=${test.id}&message=''";
       	
       	
  	   } 

  	  var canStartTest =  ${canRefreshTheTestPage};
  	  var secsToShowAlert = 15;
	  var isReasonForRestartPopUpActive = false;
  	  /*	
  	  if(!canStartTest){
			//showAlertAndCloseWindow("You are not allowed to restart your test, Please contact Course Coordinator on chat for any query.");
  	  		showPageLostFocusAlert_WithSubmitReason("Reason for Restart ","You are not allowed to restart your test, Please submit a valid reason for doing so or your test attempt will be marked for copy case .",false);
		}
  	  	*/
  	  	

		var closeWindowIntervalAdded = false;
		function showAlertAndCloseWindow(messageToShow){


			try{
				 window.onbeforeunload = null;
			}catch(err){
				console.log("Error in unbind beforeunload : ",err);
			}
			
			//console.log("IN showAlertAndCloseWindow  messageToShow : "+messageToShow);
			$("#showAlertAndCloseWindowText").text(messageToShow);

			if(isTestOver){
				
				$("#showAlertAndCloseWindowIcon").removeClass("fa-exclamation-circle");
				$("#showAlertAndCloseWindowIcon").addClass("fa-check-circle");
			}
			
			$("#contactAdminParentDiv").show();
			//console.log("IN showAlertAndCloseWindow  #contactAdminParentDiv : ",$("#contactAdminParentDiv"));
			
			if(!closeWindowIntervalAdded){
				
				closeWindowIntervalAdded = true;
				var closeWindowInterval = setInterval( function(){
					//console.log("IN closeWindowInterval secsToShowAlert : "+secsToShowAlert);
					if(secsToShowAlert < 1){
						closeTestWindow();
					}
					if(secsToShowAlert >= 0){
						$("#showAlertAndCloseWindowTextTimeLeft").text(secsToShowAlert);
					}else if(secsToShowAlert == -1){
						$("#showAlertAndCloseWindowTextTimeLeft").text("Please close the window manually.");
					}else{}
	
					secsToShowAlert-- ;
				} , 1000 );

			}
			
			
			
		}
		
		$("#closePageButton").click(function(e){
			event.preventDefault();
			closeTestWindow();
		});

		function closeTestWindow(){
			if(!isMobileCheck){
				try{
					window.open('','_self').close();
				}catch(err){
					window.location.href = "/exam/mbax/ia/s/viewTestDetailsForStudentsForAllViews?userId=${userId}&id=${test.id}&message=''";
				}
			}else{
				window.location.href = "/exam/mbax/ia/s/viewTestDetailsForStudentsForAllViews?userId=${userId}&id=${test.id}&message=''";
		    }
		}
		
  	    //make table responsive start
  	    try{
  	    var tableHeaderCells = [];
  	    //iterate each table
  	    $('.testQuestionTextSpan table').each(function(){
  	  		////////console.log("Table : ",$(this));
  	  		tableHeaderCells = [];
  	    	
  	  		//get header columns for the table
  	    	$(this).find('thead tr th').each(function(){
		        //do your stuff, you can use $(this) to get current cell
		    	////////console.log("Tables head cell : ",$(this));
		    	$(this).each(function(){
			        ////////console.log("th cell : ",$(this));
			          tableHeaderCells.push($(this)[0].innerHTML);
				})  
		    })
		    ////////console.log("tableHeaderCells : ",tableHeaderCells);
  	  		
  	  		//iterate through all tbody tr cells and set data-label attr
  	  		////////console.log("$(this).find('tbody tr td').length and tableHeaderCells.length : "+$(this).find('tbody tr td').length +" and "+ tableHeaderCells.length);
  	    	
  	    	
  	  		$(this).find('tbody tr').each(function(index){
  	  			////////console.log("Tables body tr row : ",$(this));
	    		
  	  			////////console.log("$(this).find(' td').length and tableHeaderCells.length : "+$(this).find('td').length +" and "+ tableHeaderCells.length);
  	    	
	  	  		if( $(this).find('td').length != tableHeaderCells.length ){
	  	    		//continue;
	  	    	}
  	  			
  	  			$(this).find('td').each(function(index){
			        ////////console.log("Tables body cell : ",$(this));
			        ////////console.log("Tables body cell index : ",index);
			    	let headerName = tableHeaderCells[index] ? tableHeaderCells[index] : "";
			    	////////console.log("headerName : ",headerName);
			        $(this).attr("data-label",headerName);
			        ////////console.log("after setting headerName : ",headerName);
				       
	  	  		})
  	  		})
		
  	    	
		})
  	    }catch(err){
  	    	////////console.log("Error in make table responsive : ",err);
		    
  	    }
  	    //make table responsive end
		

  	    //make image responsive start
  	    try{
  	    $('.testQuestionTextSpan img').each(function(){
  	    	$(this).removeAttr('height').removeAttr('width');
  	    	$(this).addClass('img-responsive');	
  	    	$(this).css('width','100%');
		})
  	    }catch(err){
  	    	////////console.log("Error in make image responsive : ",err);
		}
  	    //make image responsive end
		
		
		
		var noOfQuestions = ${noOfQuestions};
		var currentQuestion = 1;
		var noOfQuestionsAttemptted = 0;
		var attemptedQuestions= Array(noOfQuestions).fill();
	   	var duration = ${test.duration};
		var noOfQuestionsSkipped = 0;
		var isTestOver = false;
		var totalAttempt = ${studentsTestDetails.attempt};
		var questionUpdated = false;
		var lastQuestionUpdated = false;
		var jumpToQuestionPlaceholder = 1;
		
		var facultyIdForPostRequest = '${test.facultyId}';
		
		var isProcessing= false;
		
		var autoSaveCurrentDescriptiveAnswer = false;
		
		var showFiveMinsLeftReminder = true;

		var isLastQuestionVisited= false;
		
		var saveAnswerAjaxRetryCounter = 0;
		var submitTestRetryCounter = 0;
		var updateRemainingTimeCounter = 0;
		
		var saveDescriptiveAlertCountdown = 5 * 60 * 1000; // 5min
		var isSaveDescriptiveAlertLabelSet = false;
		/* var serverPath = '${serverPath}';
		alert('serverPath' +serverPath); */
		
		
		var submitTestApiCallMade = false;
		var submitTestApiCallRetryMade = false;
		var submitTestApiCallCounter = 0;

		var allowedTimeAway = ${test.allowedTimeAway};


		var testStartedOn = '${studentsTestDetails.testStartedOn}';

		var resetTimer = 0;

		var testEndedStatus = "Manually Submitted";
		
		var isMobileCheck = false;
		
		var testTypeForCheck = '${test.testType}';

		var proctoringEnabled = '${test.proctoringEnabled}';


		var resquest_time_logPageLoadedEvent = new Date().getTime();
		var response_time_logPageLoadedEvent = new Date().getTime();
		
		try{

			 var isMobile = {
					    Android: function() {
					        return navigator.userAgent.match(/Android/i);
					    },
					    BlackBerry: function() {
					        return navigator.userAgent.match(/BlackBerry/i);
					    },
					    iOS: function() {
					        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
					    },
					    Opera: function() {
					        return navigator.userAgent.match(/Opera Mini/i);
					    },
					    Windows: function() {
					        return navigator.userAgent.match(/IEMobile/i) || navigator.userAgent.match(/WPDesktop/i);
					    },
					    any: function() {
					        return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
					    }
					};

			 isMobileCheck = isMobile.any();
		}catch(err){
		}

		if(noOfQuestions < 2){
			$('#submitTest').attr('disabled', false);
		}
		
		<c:if test = "${continueAttempt != 'Y'}">
		////////////console.log("test question not continueAttempt");
			<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
				<c:if test="${testQuestion.type == 8}" >
					////////////console.log("test question type 8");
					$('.btn-success').attr("disabled",true);
				</c:if>
			</c:forEach> 
		</c:if>
		<c:if test = "${continueAttempt == 'Y'}">
			noOfQuestionsAttemptted= ${studentsTestDetails.noOfQuestionsAttempted};
			////////////console.log("test question continueAttempt");
			
			<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
			
			<c:if test="${testQuestion.type == 8}" >
				////////////console.log("test question type 8");
				$('.btn-success').attr("disabled",true);
			</c:if>

			//populate attemptedQuestions start
			indexPA${status.index} = ${status.index};
			itemPA${status.index} = 
					{
						qId : ${status.index},
						<c:if test = "${testQuestion.isAttempted == 'Y'}">
							answered : true
						</c:if>
						<c:if test = "${testQuestion.isAttempted != 'Y'}">
							answered : false
						</c:if>
					}		
				
			attemptedQuestions.splice(indexPA${status.index}, 1, itemPA${status.index});
			//populate attemptedQuestions end
			
			<c:if test = "${testQuestion.isAttempted == 'Y'}">
			////////////console.log("test question attempted");
			showTickForAnswerSaved(${testQuestion.id});
			$("#statusQuestion${status.count}").text("Answered");
			$('#statusQuestion${status.count}').addClass('btn-success');
			<c:if test = "${testQuestion.type == 8}">
					$('#answerFile').show();
					$('#answerLink').attr("src","${testQuestion.answer}");
					//$('#answerLink').attr("src",serverPath+"/exam/${testQuestion.answer}");
					$('#hiddenAssngUrl').val('${testQuestion.answer}');
					$('.btn-success').attr('disabled', false);
					//$('#submitTest').attr('disabled', false);
			</c:if>
			</c:if>
			</c:forEach>
		</c:if>
		//////////////console.log("attemptedQuestions : ");
		//////////////console.log(attemptedQuestions);
		//////////////console.log("totalAttempt: "+totalAttempt+" noOfQuestions: "+noOfQuestions+". testId: "+testId+". sapid: "+sapid+" noOfQuestionsAttemptted: "+noOfQuestionsAttemptted+" duration:"+duration);
		
		var questionsDetails = new Array();
		

		<c:forEach var="testQuestion" items="${testQuestions}"	varStatus="status">
				
				var setIntervalVar${testQuestion.id} ;
				
				questionsDetails.push( {
								"id": ${testQuestion.id},
								"type": ${testQuestion.type},
								"divId": ${status.count},
								"subQuestions": [
												<c:forEach var="subQuestion" items="${testQuestion.subQuestionsList}"	varStatus="status">
													{
														"id":${subQuestion.id},
														"type":${subQuestion.type}
													},
												</c:forEach>
												]
								<c:if test = "${testQuestion.isAttempted == 'Y'}">
									,"answerSavedStatus":"saved"
								</c:if>
								<c:if test = "${testQuestion.isAttempted != 'Y'}">
									,"answerSavedStatus":"not saved"
								</c:if>
								
									}
								);
		</c:forEach>
		
		//////////////console.log("Got questionsDetails =====>");
		//////////////console.log(questionsDetails);
		//////////////console.log("Got questionsDetails <=====");
		
		//show first question on page load
		$("#questionMainDiv-1").addClass("showQuestionMainDiv");
		$("#attemptsSpan").text((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted);
		$("#noOfQuestionsSpan").text(noOfQuestions);
		$("#attemptsLeft").text(( (noOfQuestions-noOfQuestionsAttemptted) < 0 ) ? 0 : (noOfQuestions-noOfQuestionsAttemptted) );
		$("#skippedSpan").text(noOfQuestionsSkipped);
		
		calcProgress()
		
		<c:forEach var="testQuestion" items= "${testQuestions}"	varStatus="status">
		////////////console.log('Entered testQuestion forEach')
			try {
				$("#studentTestForm-${status.count}").submit(function(e){
					//////////////console.log('Called Submit')
				    e.preventDefault();
					//////////////console.log("isProcessing: "+isProcessing);
					checkIsTestOver("");
					//////////////console.log('questionUpdated = '+questionUpdated);
					
					if(isProcessing){
						return;
					}else{

						isProcessing = true;
					}

					
					questionId=$("#hiddenQuestionId-${status.count}").val();
					submitTestQuestionForm(questionId,'stayHere');
				});
			}
			catch(err) {
			    //////////////console.log("Catch error : "+err.message);
			}
			
			<c:if test="${testQuestion.type == 4}" >
			
		    $("#textArea-${testQuestion.id}").on('change keydown input', function(){
		    	async function asyncCallToChangedText(){
					let saved = await  changedText(${testQuestion.id})
						.then(success, failure)
				 		function success(data){
							//////////////console.log("In SUCCESS of changedText() =====> ");
							return true;
					 		}
				 		function failure(data){
							showSnackBar("Error in saving answer, Please try again . ", "error");
							return false;
						 	} 
				}
		    	asyncCallToChangedText();
				
		    	//changedText(${testQuestion.id});
			});
			
		    $('#textArea-${testQuestion.id}').bind('copy paste cut',function(e) { 
		    	 e.preventDefault(); //disable cut,copy,paste
		    	 Swal.fire('Cut, Copy & Paste options are disabled !!','');
		    	 }); 
			</c:if>
		</c:forEach>
		
		function submitTestQuestionForm(qId,actionTo){
			

			$('#parentSpinnerDiv').show();
			
	    	saveAnswerAjaxRetryCounter = 0;
			
			/* if(!questionUpdated){
				if('nextQuestion' === actionTo || 'saveNNext' === actionTo ){
					
					async function asyncCallToNextQuestion(){
						let toNextQuestion = await  nextQuestion()
							.then(success, failure)
					 		function success(data){
								isProcessing = false;
								
								//////////////console.log("In SUCCESS nextQuestion() =====> ");
							return true;
						 		}
					 		function failure(data){
								showSnackBar("Error , Please try again . ", "error");
								return false;
							 	} 
					}
					asyncCallToNextQuestion();
					
					
					//nextQuestion();
				}
				else if('previousQuestion' === actionTo){

					async function asyncCallToPreviousQuestion(){
						let toPreviousQuestion = await  previousQuestion()
							.then(success, failure)
					 		function success(data){
								isProcessing = false;
								//////////////console.log("In SUCCESS previousQuestion() =====> ");
							return true;
						 		}
					 		function failure(data){
								showSnackBar("Error , Please try again . ", "error");
								return false;
							 	} 
					}
					asyncCallToPreviousQuestion();
					
					//previousQuestion();
				}
				else if('submitTest' === actionTo || 'stayHere' === actionTo){

					isProcessing = false;

					$('#parentSpinnerDiv').hide();
					return;
				}
				else{

					async function asyncCallToJumpToQuestoinSubFunction(){
						let jumpToQuestoinSubFunction = await  jumpToQuestoinSubFunction(currentQuestion)
							.then(success, failure)
					 		function success(data){
								//////////////console.log("In SUCCESS jumpToQuestoinSubFunction() =====> ");
								isProcessing = false;

							return true;
						 		}
					 		function failure(data){
								showSnackBar("Error , Please try again . ", "error");
								return false;
							 	} 
					}
					asyncCallToJumpToQuestoinSubFunction();
					
					//jumpToQuestoinSubFunction(currentQuestion);
				}

			}
			else{
		    let saved = validateAndSaveAnswer(qId,actionTo);
		    if(saved){
		    	//////////////console.log("Saved Answer.");
		    	questionUpdated= false;
				isProcessing = false;
	 	    }else{
		    	//////////////console.log("Error in saving answer.");
				isProcessing = false;
	 	    }
			} */
			

		    let saved = validateAndSaveAnswer(qId,actionTo);
		    if(saved){
		    	//////////////console.log("Saved Answer.");
		    	questionUpdated= false;
				isProcessing = false;
	 	    }else{
		    	//////////////console.log("Error in saving answer.");
				isProcessing = false;
	 	    }
		}
		
		//validateAndSaveAnswer Start
		function validateAndSaveAnswer(questionId,actionTo){
			//////////////console.log("In validateAndSaveAnswer() questionId : "+questionId+" actionTo : "+actionTo);
			

			
			questionDetails = getQuestionDetails(questionId);
		 	//////////////console.log("In validateAndSaveAnswer() got questionDetails ====> ");
		 	//////////////console.log(questionDetails);
		 	//////////////console.log("In validateAndSaveAnswer() got questionDetails <==== ");
			
			if(questionDetails==null){
				showSnackBar("Error in getting QuestionDetails, Please try again", "error");
			}else{
			//logic to get given answer start
			var inputArraySelector;
			if(questionDetails.type==4){
				return answuerOftype4(questionDetails,actionTo);
			}else if(questionDetails.type==3){
				return answuerOftype3(questionDetails,actionTo);
			}else if(questionDetails.type==8){
				return answuerOftype8(questionDetails,actionTo);
			}else{
				inputArraySelector = "#options"+questionDetails.id+" :input";
				let saved= answuerOftype1n2(questionDetails,inputArraySelector,false,actionTo);
				if(saved){
		 	    	return true;
		 	    }else{
		 	    	return false;
		 	    }
			}
			
			//logic to get given answer end
			}
		}
		//validateAndSaveAnswer end
		
		 function answuerOftype1n2(questionDetails,inputArraySelector,isSubQuestion,actionTo){
				 return new Promise(resolve => {
					      var answer;
							tempOptionCount=1;
						    //////////////console.log("IN answerOftype1n2() inputArraySelector : "+inputArraySelector);
						    inputArray = $(inputArraySelector);
						    //////////////console.log(inputArray);

						    //////////////console.log("inputArray: "+inputArray.length);
						 	for(let i = 0; i < inputArray.length; i++){
						    	//////////////console.log("Option button Option: "+i+" . Value: "+$(inputArray[i]).val());
						    	optionInput =inputArray[i];
						    	if($(optionInput).is(":checked")){
						    		//////////////console.log("Selected Option: "+$(optionInput).attr("name")+" . Value: "+$(optionInput).val()+" . Sapid: "+sapid);
						    		if(tempOptionCount==1){
						    			answer= ""+$(optionInput).val();
						    		}else{
						    			answer= answer+"~"+$(optionInput).val();
						    		}
							    	tempOptionCount++;
						    	}
							}
						 	if(answer!=null){
						 		var saved = saveDQAnswerInRedisAjax(questionDetails.id,answer,sapid,testId,questionDetails.type,isSubQuestion,actionTo)
						 						.then(success, failure)
											 		function success(data){
												 	    	resolve(true);
											 			}
											 		function failure(data){
													      resolve(false);
											 			}
						 			
						 	}else{
						 		hideTickForAnswerSaved(questionDetails.id);
						 		if(!isSubQuestion){


							 		async function asyncCallToDeleteAnswerBySapIdQuestionId(){
										let deleteAns = await  deleteAnswerBySapIdQuestionId(questionDetails.id,sapid)
											.then(success, failure)
									 		function success(data){
												//////////////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");

										 		unAttemptAttemptedQuestion(currentQuestion);
										 		}
									 		function failure(data){
												showSnackBar("Error in saving answer, Please try again . ", "error");
												
											 	} 
									}
							 		asyncCallToDeleteAnswerBySapIdQuestionId();
							 		

						 		}
						 		
						 		//deleteAnswerBySapIdQuestionId
						 		
						 		
								questionUpdated=false;
								$('#parentSpinnerDiv').hide();
						 			//return false;
								    resolve(true);
						 		
						 		
						 	}
			});
			
		}
		
		function answuerOftype3(questionDetails,actionTo){ // if question is of type 3 i.e. Case Study
			var csId = questionDetails.id;
			var csSubQuestions = questionDetails.subQuestions;
			var tempQuestion;
			var inputArraySelector;
			var noOfCSQuestionNotAnswered=0;

		    	var tempNoOfSubQuestionsSaved = 0;
		    	var noOfSelectedCSSubQuestions=0;
				async function asyncCall() {
					 for(var i = 0; i< csSubQuestions.length;i++){
						 tempQuestion=csSubQuestions[i];
						
						//check if inputs are select to show unattempted if none are Start
						 		let inputArraySelectorForCheck = "#options"+csId+"-"+tempQuestion.id+" :input";
						        let inputArrayForCheck = $(inputArraySelectorForCheck);
							    //////////////console.log(inputArrayForCheck);
								let noOfSubQuestionsOptionsSelected = 0;
							    //////////////console.log("inputArrayForCheck: "+inputArrayForCheck.length);
							 	for(let i = 0; i < inputArrayForCheck.length; i++){
							    	//////////////console.log("Option button Option: "+i+" . Value: "+$(inputArrayForCheck[i]).val());
							    	optionInputForCheck =inputArrayForCheck[i];
							    	if($(optionInputForCheck).is(":checked")){
							    		//////////////console.log("Selected Option: "+$(optionInputForCheck).attr("name")+" . Value: "+$(optionInputForCheck).val()+" . Sapid: "+sapid);
							    		noOfSelectedCSSubQuestions++;
							    		noOfSubQuestionsOptionsSelected++;
							    	}
								}
							 	//////////////console.log(" noOfSubQuestionsOptionsSelected : "+noOfSubQuestionsOptionsSelected);
							 	if(noOfSubQuestionsOptionsSelected == 0){
							 		let h4SelectorForSubQuestion = "#subQuestionH4-"+tempQuestion.id;
							 		//////////////console.log(" h4SelectorForSubQuestion : "+h4SelectorForSubQuestion);
							 		$(h4SelectorForSubQuestion).removeClass("greenLeftBorder");
							 	}
						//check if inputs are select to show unattempted if none are End
						 
						if(tempQuestion.type==1 || tempQuestion.type==2){
							inputArraySelector = "#options"+csId+"-"+tempQuestion.id+" :input";
							let saved = await answuerOftype1n2(tempQuestion,inputArraySelector,true).then(function(){
								//////////////console.log("------------------------>CAlled saved")
									tempNoOfSubQuestionsSaved++;			
							});
								
						}
						
						
						
					}
				}
				asyncCall().then(function(){
					//////////////console.log("------------------------>CAlled Then")
					if(tempNoOfSubQuestionsSaved == csSubQuestions.length){
						
						// asyncCallToSavedCSQuestionAnswer start
						async function asyncCallToSavedCSQuestionAnswer(){
							let saved = await saveAnswerAjax(csId,"attempted",sapid,testId,questionDetails.type,true)
	 						.then(success, failure)
						 		function success(data){
	 							//////////////console.log("In SUCCESS of AJAX CALL of Case Study Question =====> ");
								postAjaxSuccessOperations(actionTo);
								


								//check if inputs are select to show unattempted if none are Start
								if(noOfSelectedCSSubQuestions == 0){
									
									async function asyncCallToDeleteAnswerBySapIdQuestionId(){
										let deleteAns = await  deleteAnswerBySapIdQuestionId(questionDetails.id,sapid)
											.then(success, failure)
									 		function success(data){
												//////////////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
											
										 		}
									 		function failure(data){
												showSnackBar("Error in saving answer, Please try again . ", "error");
												
											 	} 
									}
							 		asyncCallToDeleteAnswerBySapIdQuestionId();
									
									questionUpdated = false;

							 		hideTickForAnswerSaved(questionDetails.id);
							 		unAttemptAttemptedQuestion(currentQuestion);
								}else{

							 		showTickForAnswerSaved(questionDetails.id);
								}
								//check if inputs are select to show unattempted if none are End
								
								return true;
							 		}
						 		function failure(data){
									showSnackBar("Error in saving all sub questions, Please try again . ", "error");
									return false;
								 	}
						}
						//asyncCallToSavedCSQuestionAnswer end
						asyncCallToSavedCSQuestionAnswer();
 			
 	
					}else{
						showSnackBar("Error in saving all sub questions, Please try again . ", "error");
						return false;
					}
					
				});
				//////////////console.log("tempNoOfSubQuestionsSaved" + tempNoOfSubQuestionsSaved)
				
			
			return false;
		}
		
		//answuerOftype4 start
		function answuerOftype4(questionDetails,actionTo){ //type 4 is of Descriptive
			var dQuestionId = questionDetails.id;
			var textAreaSelector = "#textArea-"+dQuestionId;
			var dAnswuer = $(textAreaSelector).val();
			//////////////console.log("In answuerOftype4 got dQuestionId: "+dQuestionId+" textAreaSelector: "+textAreaSelector+" dAnswuer: "+dAnswuer);
			
			if(dAnswuer){
			if(dAnswuer.length == 0 ){

				goToQuestionAsPerAction(actionTo);
			}else{
				async function asyncCallToSaveDescriptiveAnswer(){
					let saved = await  saveDQAnswerInRedisAjax(dQuestionId,dAnswuer,sapid,testId,questionDetails.type,false,actionTo)
						.then(success, failure)
				 		function success(data){
							//////////////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
							$('#parentSpinnerDiv').hide();
					    	questionUpdated= false;
						return true;
					 		}
				 		function failure(data){
							showSnackBar("Error in saving answer, Please try again . ", "error");
							$('#parentSpinnerDiv').hide();
							return false;
						 	} 
				}
				asyncCallToSaveDescriptiveAnswer();
			}
			}else{
				goToQuestionAsPerAction(actionTo);
			}
			
		}
		//answuerOftype4 end
		
		function goToQuestionAsPerAction(actionTo){
			if('nextQuestion' === actionTo){
				nextQuestion();
			}
			else if('previousQuestion' === actionTo){
				previousQuestion();
			}
			else if('submitTest' === actionTo || 'stayHere' === actionTo){
			}
			else{
				jumpToQuestoinSubFunction(jumpToQuestionPlaceholder);
			}
        	calcProgress();
			
		}
		

		
		//answuerOftype8 start
		function answuerOftype8(questionDetails,actionTo){ //type 8 is of pdf
			var dQuestionId = questionDetails.id;
			var dAnswuer = $('#hiddenAssngUrl').val();
			////////////console.log("In answuerOftype8 got dQuestionId: "+dQuestionId+" dAnswuer: "+dAnswuer+" actionTo: "+actionTo);
			
			if(dAnswuer){
				if(dAnswuer.length == 0 ){
					showSnackBar("Error in saving answer, Invalid file type uploaded ", "error");
					goToQuestionAsPerAction(actionTo);
					$('#parentSpinnerDiv').hide();
				}else{
					async function asyncCallToSaveAnswer(){
						let saved = await  saveAnswerAjax(dQuestionId,dAnswuer,sapid,testId,questionDetails.type,false,actionTo)
							.then(success, failure)
					 		function success(data){
								////////////console.log("In SUCCESS of AJAX CALL of Link =====> ");
						    	questionUpdated= false;
						    	 $('.btn-success').attr('disabled',false);
						    	 $('#parentSpinnerDiv').hide();
						    	 $('#answerFile').show();
								 $('#answerLink').attr("src",dAnswuer);
									$('#submitTest').attr('disabled', false);
									$('#uploadingAnswerMessage').text("");
							return true;
						 		}
					 		function failure(data){
					 			////////////console.log("data =====> ");
					 			////////////console.log(data);
					 			$('#uploadingAnswerMessage').text("Unable to verify your file. Please select file again using choose file.");
								showSnackBar("Error in saving answer, Please try again . ", "error");
								
								return false;
							 	} 
					}
					asyncCallToSaveAnswer();
				}
				 }else{
					goToQuestionAsPerAction(actionTo);
				} 
		}
		//answuerOftype8 end

	
		

		//getQuestionDetails(questionId) start
		function getQuestionDetails(questionId){
			for(var i=0;i<questionsDetails.length;i++){
				tempDetails = questionsDetails[i];
				if(tempDetails.id == questionId){
					return tempDetails;
				}
			}
			return null;
		}
		//getQuestionDetails(questionId) end
		
		//getQuestionDetailsByDivId(questionId) start
		function getQuestionDetailsByDivId(divId){
			for(var i=0;i<questionsDetails.length;i++){
				tempDetails = questionsDetails[i];
				if(tempDetails.divId == divId){
					return tempDetails;
				}
			}
			return null;
		}
		//getQuestionDetails(questionId) end
		
		//updateAnswerSavedStatusByQuestionId start
		function updateAnswerSavedStatusByQuestionId(questionId,savedStatus){
			try{
				for(var i=0;i<questionsDetails.length;i++){
					if(questionsDetails[i].id == questionId){
						questionsDetails[i].answerSavedStatus = savedStatus;
						break;
					}
				}
			}catch(err){
				console.log("Error in updateAnswerSavedStatusByQuestionId : ",err);
			}
		}
		//updateAnswerSavedStatusByQuestionId end
		
		//getAnswerSavedStatusByQuestionId start
		function getAnswerSavedStatusByQuestionId(questionId){
			try{
				for(var i=0;i<questionsDetails.length;i++){
					if(questionsDetails[i].id == questionId){
						return questionsDetails[i].answerSavedStatus;
						
					}
				}
			}catch(err){
				console.log("Error in getAnswerSavedStatusByQuestionId : ",err);
			}
			return "";
		}
		//getAnswerSavedStatusByQuestionId end
		
		//saveAnswerAjax start
		 function saveAnswerAjax(questionId,answer,sapid,testId,type,isSubQuestion,actionTo){
			 var promiseObj = new Promise(function(resolve, reject){
			//////////////console.log("In saveAnswerAjax() ENTERED...");
			var methodRetruns = false;
			//ajax to save question reponse start
    		var body = {
    			'questionId' : questionId,
    			'answer' : answer,
    			'sapid' : sapid,
    			'testId' : testId,
    			'type' : type,
    			'attempt' : totalAttempt,
    			'answerSavedStatus':getAnswerSavedStatusByQuestionId(questionId)
    			
    		};
    		//////////////console.log(body);
    		
    		let resquest_time = new Date().getTime();
    		let response_time = new Date().getTime();
  			let apiUrl = window.location.origin+"/exam/mbax/ia/sm/m/addStudentsQuestionResponse";
    		
  			$.ajax({
    			type : 'POST',
    			url : '/exam/mbax/ia/sm/m/addStudentsQuestionResponse',
    			data: JSON.stringify(body),
                contentType: "application/json",
                dataType : "json",
                timeout : 10000,
                
    		}).done(function(data) {
				  //////////////console.log("iN AJAX SUCCESS");
              	//////////////console.log(data);
              	updateAnswerSavedStatusByQuestionId(questionId,"saved");
              	if(type == 4){
              		hideAlertLabelToSaveDescriptive();
              	}
              	
              	if(!isSubQuestion){ //Do below operations only if Main question and not of type 3
              		showTickForAnswerSaved(questionId);	
              		
              		postAjaxSuccessOperations(actionTo);
              	}
              	
              	
              	methodRetruns= true;
      			//////////////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
      			
      			//apiLogCall start
      			
	        			try{
    			response_time = new Date().getTime();
    			response_payload_size = data ? data.length : 0 ;
      			//asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,status,error_message,response_payload_size)
      			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Success","",response_payload_size)
				}catch(err){ 
					//////////console.log("Error in apiLogCall :"); 
					//////////console.log(err); 
				}
      			//apiLogCall end
      			
      			resolve(methodRetruns);
    		}).fail(function(xhr) {
    			//////////////console.log("iN AJAX eRROR");
    			
    			//apiLogCall start
      			
    			try{
	    			response_time = new Date().getTime();
	    			response_payload_size = xhr ? xhr.length : 0 ;
	      			//asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,status,error_message,response_payload_size)
	      			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Error","Error in saving Answer : "+JSON.stringify(body)+JSON.stringify(xhr),response_payload_size)
      			}catch(err){

					//////////console.log("Error in apiLogCall :"); 
					//////////console.log(err); 
				}
				
      			//apiLogCall end
      			
    			
    			//Retry once if got error start
    			if(saveAnswerAjaxRetryCounter === 0 ){
    				showSnackBar("Retrying to save answer, Please wait. ", "error");

			    	saveAnswerAjaxRetryCounter = 1;
    				async function asyncCallToSaveAnswerAjaxRetry(){
    					let saved = await  saveAnswerAjax(questionId,answer,sapid,testId,type,isSubQuestion,actionTo)
    						.then(success, failure)
    				 		function success(data){
    							//////////////console.log("In SUCCESS of AJAX CALL of asyncCallToSaveAnswerAjaxRetry =====> ");
    					    	questionUpdated= false;
    							}
    				 		function failure(data){
    							} 
    				}
    				asyncCallToSaveAnswerAjaxRetry();
    			}else{
					try{
	    				let responseStatusMessage = xhr ? (xhr.responseJSON ? (xhr.responseJSON.Status) : "") : "";

	    				let responseInString = JSON.stringify(xhr);
	    				console.log('responseStatusMessage : ',responseStatusMessage);
	    				console.log('responseInString : ',responseInString);
						if(responseInString){
							if(responseInString.includes("TimeOver")){
	
								 try{
								  //Start sweet alert
								  
								  Swal.fire("TimeOver, Kindly close the window.", responseStatusMessage);
									  
								  //end sweet alert
								 }catch(err){
									  alert("TimeOver,  Kindly close the window. "+ responseStatusMessage);
								 }
							} else if(responseInString.includes(":0")){
								
								 try{
								  //Start sweet alert
								  
								  Swal.fire("Internet Connection Issue, Unable to save answer.", "Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
									  
								  //end sweet alert
								 }catch(err){
									  alert("Internet Connection Issue, Unable to save answer. Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
								 }
							} else if(responseInString.includes("404")){
								
								 try{
								  //Start sweet alert
								  
								  Swal.fire("Internet Connection Issue, Unable to save answer.", "Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
									  
								  //end sweet alert
								 }catch(err){
									 alert("Internet Connection Issue, Unable to save answer. Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
								 }
							} else if(responseInString.includes("500")){
								
								 try{
								  //Start sweet alert
								  
								  Swal.fire("Internet Connection Issue, Unable to save answer.", "Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
									  
								  //end sweet alert
								 }catch(err){
									 alert("Internet Connection Issue, Unable to save answer. Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
								 }
							} else if(responseInString.includes("timeout")){
								
								 try{
								  //Start sweet alert
								  
								  Swal.fire("Internet Connection Issue, Unable to save answer.", "Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
									  
								  //end sweet alert
								 }catch(err){
									 alert("Internet Connection Issue, Unable to save answer. Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
								 }
							}else{
								showSnackBar("Error in saving answer. Try Again.", "error");
			    			}
						}else{
							showSnackBar("Error in saving answer. Try Again.", "error");
		    			}
					}catch(err){
						showSnackBar("Error in saving answer. Try Again.", "error");
					}
    			    methodRetruns= false;
    				//////////////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
    			    //////////////console.log('error', xhr);
    			   	//////////console.log( xhr);
    			    
    			    logErrorAjax(xhr,body);
    				//questionUpdated=false;
    				$('#parentSpinnerDiv').hide();
    			    reject(methodRetruns);	
    			}
    			//Retry once if got error end
    			
			  });
			//ajax to save question reponse end
			////////////////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
			 })
			 
			 return promiseObj;

    	}
		//saveAnswerAjaxr end
		
		//saveDQInRedis start
		 function saveDQAnswerInRedisAjax(questionId,answer,sapid,testId,type,isSubQuestion,actionTo){
			 var promiseObj = new Promise(function(resolve, reject){
			console.log("In saveDQAnswerInRedisAjax() ENTERED...");
			var methodRetruns = false;
			//ajax to save question reponse start
    		var body = {
    			'questionId' : questionId,
    			'answer' : answer,
    			'sapid' : sapid,
    			'testId' : testId,
    			'type' : type,
    			'attempt' : totalAttempt,
    			'answerSavedStatus':getAnswerSavedStatusByQuestionId(questionId)
    			
    		};
    		console.log(body);
    		
    		let resquest_time = new Date().getTime();
    		let response_time = new Date().getTime();
  			let apiUrl = window.location.origin+"/timeline/api/mbax/ia/studentTest/saveDQAnswerInCache";
    		
  			$.ajax({
    			type : 'POST',
    			url : '/timeline/api/mbax/ia/studentTest/saveDQAnswerInCache',
    			data: JSON.stringify(body),
                contentType: "application/json",
                dataType : "json",
                timeout : 10000,
                
    		}).done(function(data) {
				  console.log("iN AJAX SUCCESS");
              	console.log(data);

              	updateAnswerSavedStatusByQuestionId(questionId,"saved");
              	
              	if(type == 4){
              		hideAlertLabelToSaveDescriptive();
              	}
              	
              	if(!isSubQuestion){ //Do below operations only if Main question and not of type 3
              		showTickForAnswerSaved(questionId);	
              		
              		postAjaxSuccessOperations(actionTo);
              	}
              	
              	
              	methodRetruns= true;
      			//////////////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
      			
      			//apiLogCall start
      			
	        			try{
    			response_time = new Date().getTime();
    			response_payload_size = data ? data.length : 0 ;
      			//asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,status,error_message,response_payload_size)
      			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Success","",response_payload_size)
				}catch(err){ 
					//////////console.log("Error in apiLogCall :"); 
					//////////console.log(err); 
				}
      			//apiLogCall end
      			
      			resolve(methodRetruns);
    		}).fail(function(xhr) {
    			//////////////console.log("iN AJAX eRROR");
    			console.log("iN saveDQAnswer error : ",xhr);
    			
    			
    			//apiLogCall start
      			
    			try{
	    			response_time = new Date().getTime();
	    			response_payload_size = xhr ? xhr.length : 0 ;
	      			//asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,status,error_message,response_payload_size)
	      			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Error","Error in saving Answer : "+JSON.stringify(body)+JSON.stringify(xhr),response_payload_size)
      			}catch(err){

					//////////console.log("Error in apiLogCall :"); 
					//////////console.log(err); 
				}
				
      			//apiLogCall end
      			
    			
    			//Retry once if got error start
    			if(saveAnswerAjaxRetryCounter === 0 ){
    				showSnackBar("Retrying to save answer, Please wait. ", "error");

			    	saveAnswerAjaxRetryCounter = 1;
    				async function asyncCallToSaveAnswerAjaxRetry(){
    					let saved = await  saveDQAnswerInRedisAjax(questionId,answer,sapid,testId,type,isSubQuestion,actionTo)
    						.then(success, failure)
    				 		function success(data){
    							//////////////console.log("In SUCCESS of AJAX CALL of asyncCallToSaveAnswerAjaxRetry =====> ");
    					    	questionUpdated= false;
    							}
    				 		function failure(data){
    							} 
    				}
    				asyncCallToSaveAnswerAjaxRetry();
    			}else{
					try{
	    				let responseStatusMessage = xhr ? (xhr.responseJSON ? (xhr.responseJSON.Status) : "") : "";
						
	    				let responseInString = JSON.stringify(xhr);
	    				console.log('responseStatusMessage : ',responseStatusMessage);
	    				console.log('responseInString : ',responseInString);
						if(responseInString){
							if(responseInString.includes("TimeOver")){
	
								 try{
								  //Start sweet alert
								  
								  Swal.fire("TimeOver, Kindly close the window.", responseStatusMessage);
									  
								  //end sweet alert
								 }catch(err){
									  alert("TimeOver,  Kindly close the window. "+ responseStatusMessage);
								 }
							} else if(responseInString.includes(":0")){
								
								 try{
								  //Start sweet alert
								  
								  Swal.fire("Internet Connection Issue, Unable to save answer.", "Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
									  
								  //end sweet alert
								 }catch(err){
									  alert("Internet Connection Issue, Unable to save answer. Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
								 }
							} else if(responseInString.includes("404")){
								
								 try{
								  //Start sweet alert
								  
								  Swal.fire("Internet Connection Issue, Unable to save answer.", "Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
									  
								  //end sweet alert
								 }catch(err){
									 alert("Internet Connection Issue, Unable to save answer. Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
								 }
							} else if(responseInString.includes("500")){
								
								 try{
								  //Start sweet alert
								  
								  Swal.fire("Internet Connection Issue, Unable to save answer.", "Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
									  
								  //end sweet alert
								 }catch(err){
									 alert("Internet Connection Issue, Unable to save answer. Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
								 }
							} else if(responseInString.includes("timeout")){
								
								 try{
								  //Start sweet alert
								  
								  Swal.fire("Internet Connection Issue, Unable to save answer.", "Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
									  
								  //end sweet alert
								 }catch(err){
									 alert("Internet Connection Issue, Unable to save answer. Please retry saving the answer, if the issue persists then close all open tabs and retry after joining again. ");
								 }
							}else{
								showSnackBar("Error in saving answer. Try Again.", "error");
			    			}
						}else{
							showSnackBar("Error in saving answer. Try Again.", "error");
		    			}
					}catch(err){
						showSnackBar("Error in saving answer. Try Again.", "error");
					}
    			    methodRetruns= false;
    				//////////////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
    			    //////////////console.log('error', xhr);
    			   	//////////console.log( xhr);
    			    
    			    logErrorAjax(xhr,body);
    				//questionUpdated=false;
    				$('#parentSpinnerDiv').hide();
    			    reject(methodRetruns);	
    			}
    			//Retry once if got error end
    			
			  });
			//ajax to save question reponse end
			////////////////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
			 })
			 
			 return promiseObj;

    	}
		//saveDQInRedis end
		
			
			//deleteAnswerBySapIdQuestionId start
			 function deleteAnswerBySapIdQuestionId(questionId,sapid){
				var promiseObj = new Promise(function(resolve, reject){
				//////////////console.log("In deleteAnswerBySapIdQuestionId() ENTERED...");
				var methodRetruns = false;
				var sQTSelector = "#statusQuestion"+currentQuestion;
				//////////////console.log("sQTSelector : "+sQTSelector);
				var statusQuestionText = $(sQTSelector).text();
				//////////////console.log("statusQuestionText : "+statusQuestionText );
				
				if(statusQuestionText == 'Answered'){
				
				//ajax to deleteAnswerBySapIdQuestionId start
	    		var body = {
	    			'questionId' : questionId,
	    			'sapid' : sapid,
	    			'testId' : testId,
	    			'attempt' : totalAttempt
	    			
	    		};
	    		//////////////console.log(body);
	    		$.ajax({
	    			type : 'POST',
	    			url : '/exam/mbax/ia/s/deleteAnswerBySapIdQuestionId',
	    			data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	                timeout : 10000,
	                
	    		}).done(function(data) {
					//////////////console.log("iN deleteAnswerBySapIdQuestionId AJAX SUCCESS");
	              	//////////////console.log(data);
	              	
	              	methodRetruns= true;
					questionUpdated=false;
					$('#parentSpinnerDiv').hide();
	      			//////////////console.log("In deleteAnswerBySapIdQuestionId() EXIT... got methodRetruns: "+methodRetruns);
	      			resolve(methodRetruns);
	    		}).fail(function(xhr) {
	    			//////////////console.log("iN deleteAnswerBySapIdQuestionId AJAX eRROR");
					showSnackBar("Error in saving answer. Try Again.", "error");
					methodRetruns= false;
					//////////////console.log("In deleteAnswerBySapIdQuestionId() EXIT... got methodRetruns: "+methodRetruns);
				    //////////////console.log('error', xhr);
				    logErrorAjax(xhr,body);

					questionUpdated=false;
					$('#parentSpinnerDiv').hide();
				    reject(methodRetruns);
				  });
				//ajax to  end
				}else{
					questionUpdated=false;
					$('#parentSpinnerDiv').hide();
	      			//////////////console.log("In deleteAnswerBySapIdQuestionId() exit...");
	      			resolve(true);
	    		}
				////////////////console.log("In deleteAnswerBySapIdQuestionId() EXIT... got methodRetruns: "+methodRetruns);
				
				})
				 return promiseObj;

	    	}
			//deleteAnswerBySapIdQuestionId end
		
		//logAutoSaveApiHit Start
				
				//logAutoSaveApiHit start
				 function logAutoSaveApiHit(questionId,sapid){
					var promiseObj = new Promise(function(resolve, reject){
					//////////////console.log("In logAutoSaveApiHit() ENTERED...");
					
					
					//ajax to logAutoSaveApiHit start
		    		var body = {
		    			'questionId' : questionId,
		    			'sapid' : sapid,
		    			'testId' : testId,
		    			'attempt' : totalAttempt
		    			
		    		};
		    		//////////////console.log(body);
		    		$.ajax({
		    			type : 'POST',
		    			url : '/exam/mbax/ia/sm/m/logAutoSaveApiHit',
		    			data: JSON.stringify(body),
		                contentType: "application/json",
		                dataType : "json",
		                timeout : 10000,
		                
		    		}).done(function(data) {
						//////////////console.log("iN deleteAnswerBySapIdQuestionId AJAX SUCCESS");
		              	//////////////console.log(data);
		              	
		    		}).fail(function(xhr) {
					  });
					//ajax to  end
					
					})
					 return promiseObj;

		    	}
				//logAutoSaveApiHit end
			
		//logAutoSaveApiHit end
			
			
		//postAjaxSuccessOperations start
		function postAjaxSuccessOperations(actionTo){
			 var promiseObj = new Promise(function(resolve, reject){
			
			//////////////console.log("Entered postAjaxSuccessOperations() ");
			
			updateAttemptedQuestions(currentQuestion);

			if('nextQuestion' === actionTo || 'saveNNext' === actionTo ){
				
				async function asyncCallToNextQuestion(){
					let toNextQuestion = await  nextQuestion()
						.then(success, failure)
				 		function success(data){
							isProcessing = false;
							//////////////console.log("In SUCCESS nextQuestion() =====> ");
				        	showSnackBar("Answer Saved!", "success");
				        	calcProgress();

							//////////////console.log("Exiting postAjaxSuccessOperations() ");
							resolve(true);
					 		}
				 		function failure(data){
							showSnackBar("Error , Please try again . ", "error");
							resolve(true);
							} 
				}
				asyncCallToNextQuestion();
				
				
				//nextQuestion();
			}
			else if('previousQuestion' === actionTo){

				async function asyncCallToPreviousQuestion(){
					let toPreviousQuestion = await  previousQuestion()
						.then(success, failure)
				 		function success(data){
							isProcessing = false;
							//////////////console.log("In SUCCESS previousQuestion() =====> ");
				        	showSnackBar("Answer Saved!", "success");
				        	calcProgress();

							//////////////console.log("Exiting postAjaxSuccessOperations() ");
							resolve(true);
					 		}
				 		function failure(data){
							showSnackBar("Error , Please try again . ", "error");
							resolve(true);
							} 
				}
				asyncCallToPreviousQuestion();
				
				//previousQuestion();
			}
			else if('submitTest' === actionTo || 'stayHere' === actionTo){

				isProcessing = false;

				$('#parentSpinnerDiv').hide();
	        	showSnackBar("Answer Saved!", "success");
	        	calcProgress();

				//////////////console.log("Exiting postAjaxSuccessOperations() ");
				resolve(true);
			}
			else{
				//alert(actionTo);
				async function asyncCallToJumpToQuestoinSubFunction(){
					
					var jumpToQuestoinSubFunction2 = await  jumpToQuestoinSubFunction(jumpToQuestionPlaceholder)
						.then(success, failure)
				 		function success(data){
							//////////////console.log("In SUCCESS jumpToQuestoinSubFunction() =====> ");
							isProcessing = false;
							
				        	showSnackBar("Answer Saved!", "success");
				        	calcProgress();

							//////////////console.log("Exiting postAjaxSuccessOperations() ");
							resolve(true);
							
					 		}
				 		function failure(data){
							showSnackBar("Error , Please try again . ", "error");
							resolve(true);
							} 
				}
				asyncCallToJumpToQuestoinSubFunction();
				
				//jumpToQuestoinSubFunction(jumpToQuestionPlaceholder);
			}

		
			
			/* if('nextQuestion' === actionTo){
				nextQuestion();
			}
			else if('previousQuestion' === actionTo){
				previousQuestion();
			}
			else if('submitTest' === actionTo || 'stayHere' === actionTo){
			}
			else{
				jumpToQuestoinSubFunction(jumpToQuestionPlaceholder);
			} */
			

    		
			 })
			 return promiseObj;
        }
		//postAjaxSuccessOperations end
		
		//submitTest Start
		$("#submitTest").click(function(e){

            //////////////console.log("Clicked submit Test +++++++++> ");
			event.preventDefault();
			
			/* if(lastQuestionUpdated){
				questionData = getQuestionDetailsByDivId(noOfQuestions);
				if(questionData !=null){
		            //////////////console.log("Saving Last Question. ");
					submitTestQuestionForm(questionData.id,'submitTest'); 
				}else{
					showSnackBar('Error in going to question try again.', "error");
					return;
				}
			} */
			

				   if(questionUpdated){
						questionData = getQuestionDetailsByDivId(currentQuestion);
						if(questionData !=null && questionData.type == 4 ){
							//alert("autosavng...");
							submitTestQuestionForm(questionData.id,'stayHere')
						}else{
							//showSnackBar('Error in going to question try again.', "error");
						}
					} 
			
				if(currentQuestion !== noOfQuestions){

					if(isLastQuestionVisited){
					}else{
						return false;
					}				
				}else{ 
					isLastQuestionVisited = true;
				}
			
            /* Commented sweetalert1 code start
            swal({
                title: "Are you sure?",
                text: "  Test would be submitted and attempt will be exhausted! \n Questions Attempted: "+noOfQuestionsAttemptted+" \n  Questions Left: "+(noOfQuestions-noOfQuestionsAttemptted)+" ",
                icon: "warning",
                buttons: [
                  'No, cancel it!',
                  'Yes, I am sure!'
                ],
                dangerMode: true,
              }).then(function(isConfirm) {
                if (isConfirm) {
    	            event.preventDefault();
			    	submitTestRetryCounter = 0;
    	        	submitTest("testEnded");
                } else {
    	            event.preventDefault();
                }
              }); 
             Commented sweetalert1 code end
            */
            
            Swal.fire({
		                title: "Are you sure?",
		                text: " ",
		                html: "  Test would be submitted and attempt will be exhausted! <br>"+
		                " Questions Attempted: <b>"+((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted)+"</b>.<br> "+
		                " Questions Left: <b>"+(( (noOfQuestions-noOfQuestionsAttemptted) < 0 ) ? 0 : (noOfQuestions-noOfQuestionsAttemptted))+"</b> ",
		                icon: "warning",
		                showCancelButton: true,
		                confirmButtonColor: '#3085d6',
		                cancelButtonColor: '#d33',
		                cancelButtonText: 'No, cancel!',
		                confirmButtonText: 'Yes, I am sure!',
		                 
		              }).then(function(isConfirm) {
		            	   
		                if (isConfirm.value && isConfirm.value === true ) {
		    	            event.preventDefault();
					    	submitTestRetryCounter = 0;
		    	        	submitTest("testEnded");
		                } else {
		    	            event.preventDefault();
		                }
		              }); 
            
		});
		
		function submitTestSubFunction(){
			
		}
		
		$(".nextQuestionButtonNOTUSED").click(function(){

			if(isProcessing){
				return;
			}
			//////////////console.log("Clicked nextQuestionButton ");
			nextQuestion();
		});
		

		$(".previousQuestionButton").click(function(e){
			//////////////console.log("Clicked previousQuestionButton ");

		    e.preventDefault();

			if(isProcessing){
				return;
			}
			
		    /* if(questionUpdated){
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					submitTestQuestionForm(questionData.id,'previousQuestion')
				}else{
					showSnackBar('Error in going to question try again.', "error");
				}
		    
		    	return;	
			} */
			previousQuestion();
		});
		

		$(".saveNNextButton").click(function(e){
			//////////////console.log("Clicked saveNNextButton ");

		    e.preventDefault();

			if(isProcessing){
				return;
			}
			
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					submitTestQuestionForm(questionData.id,'nextQuestion')
				}else{
					showSnackBar('Error in going to question try again.', "error");
				}
		    
		    	return;	
			 
		});
		
		function showTickForAnswerSaved(qId){
			var selector = "#answerAttemptedTick-"+qId;
			//////////////console.log("In showTickForAnswerSaved() got selector: "+selector);
			$(selector).addClass("fa");
			$(selector).addClass("fa-check-circle");
		}
		
		function hideTickForAnswerSaved(qId){
			var selector = "#answerAttemptedTick-"+qId;
			//////////////console.log("In hideTickForAnswerSaved() got selector: "+selector);
			$(selector).removeClass("fa-check-circle");
			$(selector).removeClass("fa");
		}
		
		function updateAttemptedQuestions(question){
			//////////////console.log("In updateAttemptedQuestions() ");
			var arrayLength =attemptedQuestions.length;
			var temp=0;
			if(arrayLength==0){
				index = question - 1;
				item = 
						{
							qId : question,
							answered : true
						}		
					
				attemptedQuestions.splice(index, 1, item);
				noOfQuestionsAttemptted++;
				statusQuestion${status.count}
				$("#statusQuestion" + question).text("Answered");
				$('#statusQuestion' + question).addClass('btn-success');

			}else{
				if(attemptedQuestions[question-1] != null){
					//////////////console.log("Question "+question+" is Attempted");
					if(!attemptedQuestions[question-1].answered){
						//////////////console.log("Question "+question+" is Skipped");
						attemptedQuestions[question-1].answered = true;
						noOfQuestionsAttemptted++;
						noOfQuestionsSkipped--;
						$("#statusQuestion" + question).text("Answered");
						$('#statusQuestion' + question).addClass('btn-success');
					}
					
				}else{
					//////////////console.log("Question "+question+" is NOT Attempted");
					index = question - 1;
					item = 
							{
								qId : question,
								answered : true
							}		
						
					attemptedQuestions.splice(index, 1, item);
					//////////////console.log("Pushed "+question+" in attemptedQuestions[]: "+attemptedQuestions);
					noOfQuestionsAttemptted++;
					$("#statusQuestion" + question).text("Answered");
					$('#statusQuestion' + question).addClass('btn-success');
					}
				}
			
			//////////////console.log("setting noOfQuestionsAttemptted "+noOfQuestionsAttemptted+" to div ");
			$("#attemptsSpan").text((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted);
			
			$("#attemptsLeft").text(((noOfQuestions - noOfQuestionsAttemptted) < 0) ? 0 : (noOfQuestions-noOfQuestionsAttemptted));
			$("#skippedSpan").text(noOfQuestionsSkipped);

		}
		
		function unAttemptAttemptedQuestion(question){
			//////////////console.log("In unAttemptAttemptedQuestion() ");
			var arrayLength =attemptedQuestions.length;
			var temp=0;
			if(arrayLength==0){
				

			}else{
				if(attemptedQuestions[question-1] != null){
					//////////////console.log("Question "+question+" is Attempted");
					if(attemptedQuestions[question-1].answered){
						//////////////console.log("Question "+question+" is Skipped");
						attemptedQuestions[question-1].answered = false;
						noOfQuestionsAttemptted--;
						$("#statusQuestion" + question).text("Unattempted");
						$('#statusQuestion' + question).removeClass('btn-success');
					}
					
				}else{
					//////////////console.log("Question "+question+" is NOT Attempted");
					}
				}
			
			//////////////console.log("setting noOfQuestionsAttemptted "+noOfQuestionsAttemptted+" to div ");
			$("#attemptsSpan").text((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted);
			
			$("#attemptsLeft").text(((noOfQuestions - noOfQuestionsAttemptted) < 0) ? 0 : (noOfQuestions-noOfQuestionsAttemptted) );
			calcProgress();

		}
		
		function nextQuestion(){
			 var promiseObj = new Promise(function(resolve, reject){
					
			//////////////console.log("Entering nextQuestion() currentQuestion: "+currentQuestion);
			//updateSkippedQuestions(currentQuestion);
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=currentQuestion+1;
			if(currentQuestion>noOfQuestions){
				currentQuestion=1;
			}
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			
			isProcessing = false;

			//////////////console.log("Exiting nextQuestion() currentQuestion: "+currentQuestion);
			
			if(currentQuestion === noOfQuestions){
				$('#submitTest').attr('disabled',false);

				isLastQuestionVisited = true;
			}

			
			resolve(true);
			questionUpdated= false;
			$('#parentSpinnerDiv').hide();
			
			 })
			return promiseObj;
		}
		
		
		
		
			 
		//NOT USED KEPT FOR REFERENCE
		function updateSkippedQuestions(question){
			//////////////console.log("Question Skipped" + question )
			if(attemptedQuestions.length == 0){
				index = question - 1;
				item = 
						{
							qId : question,
							answered : false
						}		
					
				attemptedQuestions.splice(index, 1, item);
//////////////console.log(attemptedQuestions)

					noOfQuestionsSkipped++;
$("#statusQuestion" + question).text("Skipped");
$('#statusQuestion' + question).addClass('skipped_status');
			}else{
				if(attemptedQuestions[question - 1] != null){
					//////////////console.log(attemptedQuestions)

				} else{
					index = question - 1;
					item = 
							{
								qId : question,
								answered : false
							}		
						
					attemptedQuestions.splice(index, 1, item);
					//////////////console.log(attemptedQuestions)

					noOfQuestionsSkipped++;
					$("#statusQuestion" + question).text("Skipped");
					$('#statusQuestion' + question).addClass('skipped_status');

				}
			}
			$("#skippedSpan").text(noOfQuestionsSkipped);
			

		}
		
		function previousQuestion(){
			var promiseObj = new Promise(function(resolve, reject){
				
			//////////////console.log("Entering previousQuestion() currentQuestion: "+currentQuestion);
			
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=currentQuestion-1;
			if(currentQuestion < 1){
				currentQuestion=noOfQuestions;
			}
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			
			isProcessing = false;
			
			//////////////console.log("Exiting previousQuestion() currentQuestion: "+currentQuestion);
			
			resolve(true);
			

			if(currentQuestion === noOfQuestions){
				$('#submitTest').attr('disabled',false);

				isLastQuestionVisited = true;
			}
			
			questionUpdated= false;
			$('#parentSpinnerDiv').hide();
    		
			 })
			return promiseObj;
		}
		
		window.jumpToQuestion = function jumpToQuestion(question){
			

			if(isProcessing){
				return;
			}
			
			if(questionUpdated){
			/* 	
            swal({
                title: "Answer updated",
                text: " You have not saved current answer. ",
                icon: "warning",
                buttons: [
                  'Do Not Proceed!',
                  'Proceed, Without Saving'
                ],
                dangerMode: true,
              }).then(function(isConfirm) {
                if (isConfirm) {
    	            event.preventDefault();
    				jumpToQuestoinSubFunction(question);
                } else {
    	            event.preventDefault();
    	            return false;
                }
              });
             */
             

	            Swal.fire({
	                		title: "Answer updated",
	                		text: " You have not saved current answer. ",
			                icon: "warning",
			                showCancelButton: true,
			                confirmButtonColor: '#3085d6',
			                cancelButtonColor: '#d33',
			                cancelButtonText: 'Do Not Proceed!',
			                confirmButtonText: 'Proceed, Without Saving',
			                 
			              }).then(function(isConfirm) {
			            	   //////////////console.log(isConfirm);
			                if (isConfirm.value && isConfirm.value === true ) {
			    	            event.preventDefault();
			    				jumpToQuestoinSubFunction(question);
			                } else {
			    	            event.preventDefault();
			    	            return false;
			                }
			              });
            
				}else{

					jumpToQuestoinSubFunction(question);
				}
			
			
			/* if(questionUpdated){
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					jumpToQuestionPlaceholder = question;
					submitTestQuestionForm(questionData.id,'jumpToQuestion')
				}else{
					showSnackBar('Error in going to question try again.', "error");
				}
				return;
			} */
			
		}
		function jumpToQuestoinSubFunction(question){
			var promiseObj = new Promise(function(resolve, reject){
				
			//////////////console.log("Entering jumpToQuestion() Jumping to: "+question);
			$("#questionMainDiv-"+currentQuestion).addClass("hideQuestionMainDiv");
			currentQuestion=question;
			
			$("#questionMainDiv-"+currentQuestion).removeClass("hideQuestionMainDiv");
			$("#questionMainDiv-"+currentQuestion).addClass("showQuestionMainDiv");
			//Jump to top
			$("html,body").animate({
		        scrollTop: $("#toTop").offset().top},
		        'slow');
			
			
			isProcessing = false;
			//////////////console.log("Exiting jumpToQuestion() currentQuestion: "+currentQuestion);
			

			resolve(true);
			

			if(currentQuestion === noOfQuestions){
				$('#submitTest').attr('disabled',false);

				isLastQuestionVisited = true;
			}
			

			questionUpdated= false;
			$('#parentSpinnerDiv').hide();
			 })
			return promiseObj;
		}
		
		window.markForReview = function markForReview(question){
			//////////////console.log("Entering markForReview() got question "+question);
			//bookmarkIconInQuestionStatus
			
			if($("#bookmarkIcon-"+question).hasClass("fa-bookmark-o")){

				$("#bookmarkIcon-"+question).removeClass("fa-bookmark-o");
				$("#bookmarkIcon-"+question).addClass("fa-bookmark");
				$("#bookmarkIconInQuestionStatus-"+question).removeClass("fa-bookmark-o");
				$("#bookmarkIconInQuestionStatus-"+question).addClass("fa-bookmark");
			
			}else{
				$("#bookmarkIcon-"+question).removeClass("fa-bookmark");
				$("#bookmarkIcon-"+question).addClass("fa-bookmark-o");
				if($("#bookmarkIconInQuestionStatus-"+question).hasClass("fa-bookmark")){
					$("#bookmarkIconInQuestionStatus-"+question).removeClass("fa-bookmark");
					$("#bookmarkIconInQuestionStatus-"+question).addClass("fa-bookmark-o");
				}
				
			}
			
			
			//////////////console.log("Exiting markForReview() question "+question);
		}
		
		function calcProgress(){
			var progressPercentage = ""+ ((noOfQuestionsAttemptted /noOfQuestions)*100) + "%";
			if(((noOfQuestionsAttemptted /noOfQuestions)*100) > 100){
				progressPercentage = "100%";
			}
			//////////////console.log("In calcProgress progressPercentage: "+progressPercentage);
			$("#progressDiv").animate({
				width: progressPercentage},
		        'slow');
		}
		
		//NOT USED KEPT FOR FUTURE USE
		function shouldShowPreviousQuestionButton(){
			if(currentQuestion == 1){
				$("#previousQuestionButton-"+currentQuestion).hide();
				//////////////console.log("Hiding previousQuestionButton");
			}else{
				$("#previousQuestionButton-"+currentQuestion).shwo();
				//////////////console.log("Showing previousQuestionButton");
			}
		}

		//NOT USED KEPT FOR FUTURE USE
		function shouldShowNextQuestionButton(){
			if(currentQuestion == noOfQuestions){
				$("#nextQuestionButton-"+currentQuestion).hide();
				//////////////console.log("Hiding nextQuestionButton");
			}else{
				$("#nextQuestionButton-"+currentQuestion).shwo();nextQuestionButton
			}
		}
		
		function showSnackBar(message,messageType) {
		    // Get the snackbar DIV
		    var x = document.getElementById("snackbar");
			//////////////console.log("In showSnackBar() got message "+message);
		    x.innerHTML = message;
		    
		if(messageType == 'success'){
			// Add the "show snackbarSuccess" class to DIV
		    x.className = "show snackbarSuccess";
			
		}else if(messageType == 'error'){
			// Add the "show snackbarError" class to DIV
		    x.className = "show snackbarError";
			
		}else {
			// Add the "show snackbarError" class to DIV
		    x.className = "show snackbarError";
			
		}
			
		    
			

		    // After 1 seconds, remove the show class from DIV
		    setTimeout(function(){ x.className = ""; }, 1000);
			//////////////console.log("Exiting showSnackBar()");
		}

		//lostFocusPopupMessage start
				
		function showLostFocusPopupMessage() {
			
			if(proctoringEnabled !== 'Y'){
				return;
			}
			
		    var x = document.getElementById("lostFocusPopupMessage");
			if( ( (' ' + x.className + ' ').indexOf(' ' + 'show'+ ' ') < 0 ) ){
				x.className = "show snackbarError";
			}	
		}
		
		function hideLostFocusPopupMessage() {
		    var x = document.getElementById("lostFocusPopupMessage");
		    if( ( (' ' + x.className + ' ').indexOf(' ' + 'show'+ ' ') > -1 ) ){
			    x.className = "";
			}
			
		}
		
		//lostFocusPopupMessage end
		

		//submitTest end
		
		function submitTest(message){

			$('#parentSpinnerDiv').show();
			
			//apiLogCall start
			try{
	    		let apiUrl = window.location.origin+"/exam/mbax/ia/s/startStudentTestPage_finishAssignmentButtonClicked";
				response_time_logPageLoadedEvent = new Date().getTime();
				response_payload_size_logPageLoadedEvent = 0 ;
	  			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time_logPageLoadedEvent,response_time_logPageLoadedEvent,"Success","",response_payload_size_logPageLoadedEvent)
				
	    	}catch(err){ 
				//////////console.log("Error in apiLogCall :"); 
				//////////console.log(err); 
			}
				//apiLogCall end
				
			if( submitTestApiCallMade && submitTestApiCallRetryMade && (submitTestApiCallCounter > 0) ){
				//alert('redirect 1');
				//redirect start
				showSnackBar("Redirecting to Test Details!", "success");
	            // Store
	            localStorage.setItem("testSubmitted", "Yes");
	            isTestOver=true;

				questionUpdated=false;
				$('#parentSpinnerDiv').hide();
               	checkIsTestOver(message);	
				//redirect end
				
			}else {
				if( !submitTestApiCallMade && !submitTestApiCallRetryMade && (submitTestApiCallCounter == 0) ){
					//call submitTestApiCall
					

					async function asyncCallToSubmitTestApiCall(){
						let saved = await  submitTestApiCall(message)
							.then(success, failure)
					 		function success(data){
							//alert('redirect 2');
							
							//redirect start
							showSnackBar("Redirecting to Test Details!", "success");
				            // Store
				            localStorage.setItem("testSubmitted", "Yes");
				            isTestOver=true;

							questionUpdated=false;
							$('#parentSpinnerDiv').hide();
			               	checkIsTestOver(message);	
							//redirect end
							return true;
						 		}
					 		function failure(data){
								//redirect start
								//alert('redirect 3');
				
								showSnackBar("Redirecting to Test Details!", "success");
					            // Store
					            localStorage.setItem("testSubmitted", "Yes");
					            isTestOver=true;

								questionUpdated=false;
								$('#parentSpinnerDiv').hide();
				               	checkIsTestOver(message);	
								//redirect end
								return false;
							 	} 
					}
					asyncCallToSubmitTestApiCall();
					
					
				}
				//else do nothing as submitTestApiCallMade is not true
			}
			
			
			
		}
		function submitTestApiCall(message){
			 var promiseObj = new Promise(function(resolve, reject){
				try{
					submitTestApiCallCounter=submitTestApiCallCounter+1;		
			//////////////console.log("SUBMITING TEST got message: "+message);
			 //ajax to save test start
			 let submitTestApiCallReturnFlag=false;
			 submitTestApiCallMade = true;
	    		var body = {
	    			'testId' : testId,
	    			'sapid' : sapid,
	    			'attempt' : totalAttempt,
	    			'testEndedStatus' : testEndedStatus
	    		};
	    		//////////////console.log(body);
	    		
	    		let resquest_time = new Date().getTime();
    			let response_time = new Date().getTime();
  				let apiUrl = window.location.origin+"/exam/mbax/ia/sm/m/saveStudentsTestDetails";
    		
	    		$.ajax({
	    			type : 'POST',
	    			url : '/exam/mbax/ia/sm/m/saveStudentsTestDetails',
	    			data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	                timeout : 10000,
	                success : function(data) {
	                	//alert('redirect 4');
	    				//console.log("saveStudentsTestDetails success : ",data);

	        			//apiLogCall start
	          			
	        			try{
	    	    			response_time = new Date().getTime();
	    	    			response_payload_size = data ? data.length : 0 ;
	    	      			//asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,status,error_message,response_payload_size)
	    	      			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Success","",response_payload_size)
	          			}catch(err){
							console.log("Error in apiLogCall :"); 
	    					console.log(err); 
	    				}
	    				
	          			//apiLogCall end
	                	
	                	//////////////console.log("iN AJAX SUCCESS");
	                	//////////////console.log(data);
	                	showSnackBar("Test  Submitted!", "success");
	                	// Store
	                	  localStorage.setItem("testSubmitted", "Yes");
	                	 
	             			
	             		
	             
	                	// Retrieve
	                	isTestOver=true;

						questionUpdated=false;
						$('#parentSpinnerDiv').hide();
	                	checkIsTestOver(message);
	                	
	                	submitTestApiCallReturnFlag= true;
	                	resolve(submitTestApiCallReturnFlag);
			    	},
	    			error : function(result) {
	                	//////////////console.log("iN AJAX eRROR");
	    				//////////////console.log(result);
	    			    

	        			//apiLogCall start
	          			
	        			try{
	    	    			response_time = new Date().getTime();
	    	    			response_payload_size = result ? result.length : 0 ;
	    	      			//asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,status,error_message,response_payload_size)
	    	      			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Error","Error in saving Answer : "+JSON.stringify(body)+JSON.stringify(xhr),response_payload_size)
	          			}catch(err){

	    					//////////console.log("Error in apiLogCall :"); 
	    					//////////console.log(err); 
	    				}
	    				
	          			//apiLogCall end
	    				
	    			    //submitTestRetry Start
	    			    if(submitTestRetryCounter === 0){
	    			    	submitTestApiCallRetryMade =true;
	    			    	submitTestRetryCounter = 1;

	    					async function asyncCallToSubmitTestApiCallRetry(){
	    						let saved = await  submitTestApiCall(message)
	    							.then(success, failure)
	    					 		function success(data){

	    		                	submitTestApiCallReturnFlag= true;
	    		                	resolve(submitTestApiCallReturnFlag);
	    							return true;
	    						 		}
	    					 		function failure(data){
		    		                	submitTestApiCallReturnFlag= false;
		    		                	reject(submitTestApiCallReturnFlag);
	    					 			
	    								return false;
	    							 	} 
	    					}
	    					asyncCallToSubmitTestApiCallRetry();
	    			    	
	    			    	
	    			    }else{

		    			    logErrorAjax(result,body);
							questionUpdated=false;
							$('#parentSpinnerDiv').hide();
		    				showSnackBar("Error in submitting Test. Try Again.", "error");

		                	submitTestApiCallReturnFlag= false;
		                	reject(submitTestApiCallReturnFlag);
	    			    }
	    			    //submitTestRetry End
	    			    
		    		}
	    		});
	    		//ajax to save question reponse end
			 //////////////console.log("SUBMITED TEST");
				}catch(err){
					submitTestApiCallReturnFlag= false;
                	reject(submitTestApiCallReturnFlag);
				}
			 })
			 return promiseObj;
		 
		}
		
		function checkIsTestOver(message){
			//////////////console.log("IN checkIsTestOver() got message: "+message);
			if(isTestOver){

				
		  	   	//alert(localStorage.getItem("testSubmitted"));
				//window.location.href = "/exam/testOverPage";
				//window.location.href = "/exam/viewTestDetailsForStudentsForAllViews?userId=${userId}&id=${test.id}&message=testEnded";
				showAlertAndCloseWindow("Your Test Was Submitted Successfully!");
			}
		}
		
		function saveStudentsQuestionResponse(testId,questionId){
			//////////////console.log("In saveStudentsQuestionResponse got testId: "+testId+" questionId: "+questionId);
			//alert("In saveStudentsQuestionResponse got testId: "+testId+" questionId: "+questionId);
			
		} 
		
		window.checkFields = function checkFields(h4Id,isCaseStudy) {
			if(isCaseStudy){
				$(h4Id).addClass("greenLeftBorder");
				//////////////console.log("In Checkfields() setting greenBottomBorder to h4Id:"+h4Id);
			}
			questionUpdated = true;
			if(currentQuestion == noOfQuestions){
	            //////////////console.log("Updating lastQuestionUpdated var. ");
				lastQuestionUpdated= true;
			}
			console.group("checkFields");
			/* if($("input[type=radio]").is(":checked") || $("input[type=checkbox]").is(":checked")){
				postAjaxSuccessOperations('submitTest');
				console.debug("field checked");	
			}else{
				console.debug("field not checked");
			} */
			
			/* if(questionUpdated){
				questionData = getQuestionDetailsByDivId(currentQuestion);
				if(questionData !=null){
					submitTestQuestionForm(questionData.id,'stayHere')
				}else{
					showSnackBar('Error in going to question try agin.', "error");
				}
					    
					    	return;	      
			} */
			console.groupEnd();
		}
		

		window.changedText = function changedText(textAreaId) {
			 var promiseObj = new Promise(function(resolve, reject){
					
			//////////////console.log(" IN changedText() got textAreaId :"+textAreaId);
			let textSelector = '#textArea-'+textAreaId;
			let characterCountSelector = '#characterCount-'+textAreaId;
			
			//////////////console.log(" IN changedText() got textSelector :"+textSelector);
			//////////////console.log(" IN changedText() got characterCountSelector : "+characterCountSelector);
			let textInput = $(textSelector).val();
			//////////////console.log(" IN changedText() got textInput :"+textInput);
			
			if(textInput){
				
				/*//commenting word count on 11/03/2022 by pranit : as causing browser typing issues 
				//code to set characterCount start
				 var words = textInput.match(/\S+/g).length;
					if (words > 1000) {
						// Split the string on first 200 words and rejoin on spaces
						//var trimmed = textInput.split(/\s+/, 5).join(" ");
						// Add a space at the end to keep new typing making new words
						//$(textSelector).val(trimmed + " ");
						
						$(characterCountSelector).html("<b> "+words+" / 1000 Words. (Number of words beyond recommended limit) </b>");
					}
					else {
						$(characterCountSelector).html("<b> "+words+" / 1000 Words </b>");
					}
				//code to set characterCount end
				 */
				 
				if(currentQuestion == noOfQuestions){
		            //////////////console.log("Updating lastQuestionUpdated var. ");
					lastQuestionUpdated= true;
				}
				
				questionUpdated = true;
				
				//////////////console.log(" IN changedText() got textInput.length :"+textInput.length);
				/* if(textInput.length % 50 == 0 ){
					//checkFields(textAreaId,false);
					questionData = getQuestionDetailsByDivId(currentQuestion);
					if(questionData !=null){
						submitTestQuestionForm(questionData.id,'stayHere')
					}else{
						showSnackBar('Error in going to question try agin.', "error");
					}
				} */
				
				if(textInput.length != 0 ){
					showTickForAnswerSaved(textAreaId);	
	          	}else if(textInput.length == 0 ){
	          		
	          		/*
	          		async function asyncCallToDeleteAnswerBySapIdQuestionId(){
						let deleteAns = await  deleteAnswerBySapIdQuestionId(textAreaId,sapid)
							.then(success, failure)
					 		function success(data){
								//////////////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
							
						 		}
					 		function failure(data){
								showSnackBar("Error in saving answer, Please try again . ", "error");
								
							 	} 
					}
			 		//asyncCallToDeleteAnswerBySapIdQuestionId();
			 		 */
			 		 
	          		hideTickForAnswerSaved(textAreaId);	
			 		unAttemptAttemptedQuestion(currentQuestion);
	          	}
				
			}else if(textInput == '' ){
				//$(characterCountSelector).html("<b> 0 / 1000 Words </b>");
				/*
				async function asyncCallToDeleteAnswerBySapIdQuestionId(){
					let deleteAns = await  deleteAnswerBySapIdQuestionId(textAreaId,sapid)
						.then(success, failure)
				 		function success(data){
							//////////////console.log("In SUCCESS of AJAX CALL of Descriptive =====> ");
						
					 		}
				 		function failure(data){
							//showSnackBar("Error in saving answer, Please try again . ", "error");
							
						 	} 
				}
		 		//asyncCallToDeleteAnswerBySapIdQuestionId();
		 		*/
		 		
				hideTickForAnswerSaved(textAreaId);	
		 		unAttemptAttemptedQuestion(currentQuestion);
          	}else {
				hideTickForAnswerSaved(textAreaId);	
		 		unAttemptAttemptedQuestion(currentQuestion);
          	}
			
			resolve(true);

			$('#parentSpinnerDiv').hide();
			 })
			return promiseObj;
			
		} // changedText end 

		try{


			//if( (testTypeForCheck !== 'Assignment') && (proctoringEnabled === 'Y') &&  (testId !== 626) && (testId !== 630) ){ //for not showing popup for PDF/mp4 upload tests
			if( (testTypeForCheck !== 'Assignment') &&  (testId !== 626) && (testId !== 630) ){ //for not showing popup for PDF/mp4 upload tests
			
			var pageLostFocusTime = new Date().getTime();
			var pageBackInFocusTime = new Date().getTime();
			
			var documentFocusCheck = setInterval( checkDocumentFocus, 500 );
			var lostFocusCount = 0;
			var lostFocusApiHitCount = 0;
			var oldDataForLostFocusLogs = {};
			
			var timeAwayCountForMobile = 0;
			var timeAwayCountForWeb = 0;

			var totalTimeAway = 0;
			var allowedTimeAwayInMSecs = allowedTimeAway*1000;
			
			

			var isMouseenterListening = 0;
			var timeMouseWasAway = 0;
			var isMouseOnPage = false;

			var  isMouseOnPageChecker = false;

			var navigatedPopupToBeShown = false;
			
			function checkDocumentFocus() {


			 try{

				if(!isMobileCheck){
				 if(!(IsFullScreenCurrently())){
					 showTestDetais();
				 }
				}
			}catch(err){
			}

			if(allowedTimeAwayInMSecs === (totalTimeAway)){
				//console.log("IN checkDocumentFocus  allowedTimeAwayInMSecs : "+allowedTimeAwayInMSecs+". totalTimeAway : "+totalTimeAway);
				
				showAlertAndCloseWindow("Test Window will close as you navigated away.");
			}
			 

			 if(!isMouseOnPage){
				isMouseOnPage = document.hasFocus() ? true : false;	
			 }
			 
			if ( document.hasFocus() || isMouseOnPage) {

				hideLostFocusPopupMessage();
			
					
					////console.log("document.hasFocus()__start__");
					////console.log("checkDocumentFocus()");
					////console.log("isMouseOnPage : "+isMouseOnPage);
					////console.log("isMouseOnPageChecker : "+isMouseOnPageChecker);
					////console.log("document.hasFocus() : "+document.hasFocus());
					////console.log("isMouseenterListening : "+isMouseenterListening);
					////console.log("lostFocusCount : "+lostFocusCount);
					////console.log("timeMouseWasAway : "+timeMouseWasAway);
					////console.log("timeAwayCountForWeb : "+timeAwayCountForWeb);
					////console.log("checkDocumentFocus()");
					////console.log("document.hasFocus()__end___");
					
					if(document.hasFocus()){
						/*var saveButtonSelector = "#saveButton-"+currentQuestion;
						$(saveButtonSelector).css("border","2px solid red");
						$(saveButtonSelector).text("Save focus in");*/

						$("#pageContainer").unbind("mouseenter mouseleave");
					 	isMouseOnPage = false;

						if(lostFocusCount > 0){
							
							if(isMobileCheck){
								if( timeAwayCountForMobile > 5000){ //to show alert on mobile only if away for 10 sec
									//showPageLostFocusAlert();
									navigatedPopupToBeShown = true;
								}

								timeAwayCountForMobile = 0;
							}else{
								if( timeAwayCountForWeb > 2500 ){ //to show alert on web only if away for 5 sec
									//showPageLostFocusAlert();
									navigatedPopupToBeShown = true;
								}

								timeAwayCountForWeb = 0;
							}
							
							//logic to calculate lostfocus time by system time start
							try{
							
							pageBackInFocusTime = new Date().getTime();

							//console.log("*****");
							//console.log("pageBackInFocusTime : ",pageBackInFocusTime);
							//console.log("pageBackInFocusTime.toString() : ",new Date(pageBackInFocusTime).toString());
							//console.log("pageLostFocusTime : ",pageLostFocusTime);
							//console.log("pageLostFocusTime.toString() : ",new Date(pageLostFocusTime).toString());
							//console.log("(pageBackInFocusTime - pageLostFocusTime) : ",(pageBackInFocusTime - pageLostFocusTime));
							
							if( (pageBackInFocusTime > pageLostFocusTime) &&
								((pageBackInFocusTime - pageLostFocusTime) >= 10000 )		
							){

								//console.log("IN IF");
								//showPageLostFocusAlert();
								navigatedPopupToBeShown = true;

							}

							//console.log("***** end");

							pageLostFocusTime = new Date().getTime();
							
							}catch(lfError){
								console.log("lfError : ",lfError);
							}
							//logic to calculate lostfocus time by system time end
							
							
						}

					} //if(document.hasFocus()){
					
					if(navigatedPopupToBeShown){
						showPageLostFocusAlert();
						navigatedPopupToBeShown = false;
					}


					if(lostFocusCount > 0){
						lostFocusCount = 0;
						var dataForLostFocusLogs = oldDataForLostFocusLogs;
						dataForLostFocusLogs.lastTimestamp = new Date().getTime()
						dataForLostFocusLogs.timeAway = dataForLostFocusLogs.lastTimestamp - dataForLostFocusLogs.initialTimeStamp

						asyncLostFocusAjaxCall(dataForLostFocusLogs);
						lostFocusApiHitCount = 0;

					}
					
					isMouseenterListening = 0;
					timeMouseWasAway = 0;
					timeAwayCountForMobile = 0;
					timeAwayCountForWeb = 0;

					

			 } else {
				 showLostFocusPopupMessage();
					////console.log("else__start__");
					////console.log("checkDocumentFocus()");
					////console.log("isMouseOnPage : "+isMouseOnPage);
					////console.log("isMouseOnPageChecker : "+isMouseOnPageChecker);
					////console.log("document.hasFocus() : "+document.hasFocus());
					////console.log("isMouseenterListening : "+isMouseenterListening);
					////console.log("lostFocusCount : "+lostFocusCount);
					////console.log("timeMouseWasAway : "+timeMouseWasAway);
					////console.log("timeAwayCountForWeb : "+timeAwayCountForWeb);
					////console.log("checkDocumentFocus()");
					////console.log("else__end___");
					
				 	isMouseOnPage = false;
					timeMouseWasAway = timeMouseWasAway+500;


					if(isMouseenterListening === 0 ){
						
						$("#pageContainer").mouseenter(function() {

							/*var saveButtonSelector = "#saveButton-"+currentQuestion;
							$(saveButtonSelector).focus();
							$(saveButtonSelector).css("border","2px solid black");
							$(saveButtonSelector).text("Save Mouse in");*/

							//if( (timeMouseWasAway <= 1500 )){

								
							//}
							timeMouseWasAway =0;
							isMouseenterListening = 1;
							isMouseOnPage = true;
							isMouseOnPageChecker = true;


							////console.log("\nisMouseOnPage : "+isMouseOnPage);
							////console.log("isMouseOnPageChecker : "+isMouseOnPageChecker);
							////console.log("document.hasFocus() : "+document.hasFocus());
							////console.log("isMouseenterListening : "+isMouseenterListening);
							//alert("backonpage");
										
						}); 
						
						$("#pageContainer").mouseleave(function(){
							/*var saveButtonSelector = "#saveButton-"+currentQuestion;
							$(saveButtonSelector).focus();
							$(saveButtonSelector).css("border","5px solid yellow ");
							$(saveButtonSelector).text("Save Mouse left ");*/
							isMouseenterListening = 1;
							isMouseOnPage = false;


							////console.log("\n ******* left mouse start ");
							////console.log("isMouseOnPage : "+isMouseOnPage);
							////console.log("isMouseOnPageChecker : "+isMouseOnPageChecker);
							////console.log("document.hasFocus() : "+document.hasFocus());
							////console.log("isMouseenterListening : "+isMouseenterListening);
							//alert("backonpage");
							////console.log("\n ******* left mouse end ");
							
						});

						isMouseenterListening = 1;
					}

					totalTimeAway = totalTimeAway+500;
					
				 	
					//if( (timeMouseWasAway > 1500 )){

						if(isMobileCheck){
							timeAwayCountForMobile = timeAwayCountForMobile+500;

						}else{
							timeAwayCountForWeb = timeAwayCountForWeb+500;

						}
						
						
						if((timeAwayCountForMobile > 5000) || (timeAwayCountForWeb > 5000) ){
							if(!navigatedPopupToBeShown){
								navigatedPopupToBeShown = true;
							}
						}
						

					if(lostFocusApiHitCount === 0){

							var dataForLostFocusLogs= {
								sapid : sapid,
								testId : testId,
								initialTimeStamp : new Date().getTime(),
								timeAway : 0,
							};
						asyncLostFocusAjaxCall(dataForLostFocusLogs);
						oldDataForLostFocusLogs = dataForLostFocusLogs;
						lostFocusApiHitCount = 1;

						pageLostFocusTime = new Date().getTime();

						}
					lostFocusCount = 1;
					

					//}
			}

			  
			}//checkDocumentFocus() ends


			//asyncLostFocusAjaxCall start
			 function asyncLostFocusAjaxCall(dataToSend){
				try{
				//////console.log("IN asyncLostFocusAjaxCall got dataToSend : ");
				//////console.log(dataToSend);

				var ENVIRONMENT = "${ENVIRONMENT}";
				var serverPathForTest = "${SERVER_PATH}";
				var serverPathForProd = "https://ngasce-content.nmims.edu";
				var urlForLostFocus = "";
				var apiNameForLostFocus = "/ltidemo/api/saveIATestLostFocusLogs";
				if(ENVIRONMENT === 'PROD'){
					urlForLostFocus = serverPathForProd + apiNameForLostFocus;
				}else{
					urlForLostFocus = serverPathForTest + apiNameForLostFocus;

				}
				//////console.log("urlForLostFocus: ",urlForLostFocus);
				
	    		$.ajax({
	    			type : 'POST',
	    			url : urlForLostFocus,
	    			data: JSON.stringify(dataToSend),
	                contentType: "application/json",
	                dataType : "json",
	                timeout : 10000,
	                
	    		}).done(function(data) {
					//////console.log("iN asyncLostFocusAjaxCall AJAX SUCCESS");
	              	//////console.log(data);
	              	
	    		}).fail(function(xhr) {
	    			//////console.log("iN asyncLostFocusAjaxCall AJAX eRROR");
	    		});
				
				}catch(err){
					//////console.log("IN asyncLostFocusAjaxCall got Error : ");
					//////console.log(err);
				}
				
	    	}
			//asyncApiLogAjaxCall end
			
			}//end of if testTypeCheck
		}catch(err){
			//////console.log("Error IN documentFocusCheck : ",err)
		}

		var isCalcVisible=false;
		$('#toggleCalculatorButton').click(function(e) {
			e.preventDefault();
			if(isCalcVisible){

			     $('#calculatorDiv').hide();
					$('#toggleScribblePadButton').show();

				 isCalcVisible=false;


				$("#toggleCalculatorButton i").removeClass("fa-close");
				$("#toggleCalculatorButton i").addClass("fa-calculator");
				
			}else{
				$('#toggleScribblePadButton').hide();
				     $('#calculatorDiv').show();
					 isCalcVisible=true;

					$("#toggleCalculatorButton i").removeClass("fa-calculator");
					$("#toggleCalculatorButton i").addClass("fa-close");
			}
		});

		

		var isScriblePadVisible=false;
		$('#toggleScribblePadButton').click(function(e) {
			e.preventDefault();
			if(isScriblePadVisible){

			     $('#scriblePadDiv').hide();
			     $('#toggleCalculatorButton').show();

			     isScriblePadVisible=false;


				$("#toggleScribblePadButton i").removeClass("fa-close");
				$("#toggleScribblePadButton i").addClass("fa-pencil-square-o");
				
			}else{

				     $('#scriblePadDiv').show();
				     isScriblePadVisible=true;
				     $('#toggleCalculatorButton').hide();

					$("#toggleScribblePadButton i").removeClass("fa-pencil-square-o");
					$("#toggleScribblePadButton i").addClass("fa-close");
			}
		});


		/* Get into full screen */
		function GoInFullscreen(element) {
			try{

				if(!isMobileCheck){	

					if(element.requestFullscreen)
						element.requestFullscreen();
					else if(element.mozRequestFullScreen)
						element.mozRequestFullScreen();
					else if(element.webkitRequestFullscreen)
						element.webkitRequestFullscreen();
					else if(element.msRequestFullscreen)
						element.msRequestFullscreen();
			
				}
			}catch(err){
				//console.log("Erron in GoInFullscreen() ",err);
			  showTestDetaisCount =1;	
			}
		}


		/* Is currently in full screen or not */
		function IsFullScreenCurrently() {
			var full_screen_element = document.fullscreenElement || document.webkitFullscreenElement || document.mozFullScreenElement || document.msFullscreenElement || null;
			
			// If no element is in full-screen
			if(full_screen_element === null)
				return false;
			else
				return true;
		}

		$("#go-button").on('click', function() {
			toggleFullScreen();
		});

		function toggleFullScreen(){
			if(IsFullScreenCurrently())
				GoOutFullscreen();
			else
				GoInFullscreen($("#element").get(0));
		}

		$(document).on('fullscreenchange webkitfullscreenchange mozfullscreenchange MSFullscreenChange', function() {
			if(IsFullScreenCurrently()) {
				$("#element #spanDiv").text('Full Screen Mode Enabled');
				
				$("#go-button i").removeClass("fa-arrows-alt");
				
				$("#go-button i").addClass("fa-compress");	
				
			}
			else {
				$("#element #spanDiv").text('Full Screen Mode Disabled');

				$("#go-button i").removeClass("fa-compress");
				
				$("#go-button i").addClass("fa-arrows-alt");
			}
		});
					
		
		//////////////console.log("Page Ready!");
		
		// Get todays date and time
		  var now = new Date().getTime();
		  
		//Add test's duration to current time
		var countDownDate = now + duration*60*1000;
		var remainingTimeCounter = 0; 
		
		var countdown = duration * 60 * 1000;
		
		
		//New Timer
		var timerId = setInterval(function(){
			  countdown -= 1000; 
				
			
			  var days = Math.floor(countdown / (1000 * 60 * 60 * 24));
			  var hours = Math.floor((countdown % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
			  var min = Math.floor((countdown % (1000 * 60 * 60)) / (1000 * 60));
			  var sec = Math.floor((countdown % (1000 * 60)) / 1000);
			  
			  
			  //var min = Math.floor(countdown / (60 * 1000));
			//  var hours = Math.floor(min / 60);
			  //var sec = Math.floor(countdown - (min * 60 * 1000));  // wrong
			  //var sec = Math.floor((countdown - (min * 60 * 1000)) / 1000);  //correct
			  
			  
			  // If the count down is finished, write some text
			  if (countdown <= 0) {
				  
				  hours=0;
				  min=0;
				  sec=0;
				  days=0;
				  
				  $('#parentSpinnerDiv').show();
				  //////////////console.log("BEFORE TIMEOVER");
				  
				   if(questionUpdated){
						questionData = getQuestionDetailsByDivId(currentQuestion);
						if(questionData !=null && questionData.type == 4 ){
							//alert("autosavng...");
							
							submitTestQuestionForm(questionData.id,'stayHere')
						}else{
							//showSnackBar('Error in going to question try again.', "error");
						}
					}
					$('#parentSpinnerDiv').show();

					 testEndedStatus = "Auto Submitted";
			    	submitTestRetryCounter = 0;
				  submitTest("testTimeOut");
				  //////////////console.log("AFTER TIMEOVER");
				  


			  }
			
			  //Shifted below 3 lines here to set 0 if timeover.
			  if(days>0){
				  $("#hoursSpan").text(days+"d "+hours);
			  }else{
			  	  $("#hoursSpan").text(hours);
			  }
			  $("#minutesSpan").text(min);
			  $("#secondsSpan").text(sec);
			  

			   if(questionUpdated){
					questionData = getQuestionDetailsByDivId(currentQuestion);
					if(questionData !=null && questionData.type == 4 ){
						saveDescriptiveAlertCountdown -= 1000; 
						
						
						if(saveDescriptiveAlertCountdown < 1 && !isSaveDescriptiveAlertLabelSet) {
							setAlertLabelToSaveDescriptive();
						}
					}
				}else{
					saveDescriptiveAlertCountdown = 5 * 60 * 1000  ;
					isSaveDescriptiveAlertLabelSet = false;
				}
			  
			  	/*commented below code and update remaining time is not in use.
			   if(min % 5 ==0){
				  if(remainingTimeCounter == 0){
				  	//////////////console.log('Updating remaining time');
				  	updateRemainingTimeCounter = 0;
				  	updateRemainingTime();
				  	remainingTimeCounter = 1;
				  }
			  }else{
				  remainingTimeCounter = 0;
			  }
			  	*/
			  
			  
	 		  if(min === 4 && hours === 0 && days === 0){
				  if(showFiveMinsLeftReminder){

					 try{
					  //Start sweet alert
					  /* 
					  swal("Last 5 Minutes Left!", "  Kindly Verify and Save all your answers. \n Questions Attempted: "+noOfQuestionsAttemptted+" \n  Questions Left: "+(noOfQuestions-noOfQuestionsAttemptted)+" ");
					   */
					  Swal.fire("Last 5 Minutes Left!", "  Kindly Verify and Save all your answers. \n Questions Attempted: "+((noOfQuestionsAttemptted > noOfQuestions) ? noOfQuestions : noOfQuestionsAttemptted)+" \n  Questions Left: "+(( (noOfQuestions-noOfQuestionsAttemptted) < 0 ) ? 0 : (noOfQuestions-noOfQuestionsAttemptted))+" ");
						  
					  //end sweet alert
					 }catch(err){
						 alert("Last 5 Minutes Left! Kindly Verify and Save all your answers.  Questions Attempted: "+noOfQuestionsAttemptted+".  Questions Left: "+(noOfQuestions-noOfQuestionsAttemptted)+". ");
					 }
						showFiveMinsLeftReminder = false;
					} 				  
				  }


			/*   if (countdown <= 0) {
			     alert("30 min!");
			     clearInterval(timerId);
			     //doSomething();
			  } else {
			     $("#countTime").html(min + " : " + sec);
			  } */
				
			}, 1000); //1000ms. = 1sec.

			
			//AutoSave Descriptive Start
					
			var autoSavetimerId = setInterval(function(){
			
			   if(questionUpdated){
					questionData = getQuestionDetailsByDivId(currentQuestion);
					if(questionData !=null && questionData.type == 4 ){
						//alert("autosavng...");
						
						//submitTestQuestionForm(questionData.id,'stayHere')
						//logAutoSaveApiHit(questionData.id,sapid)

						answuerOftype4(questionData,'stayHere');
						
					}else{
						//showSnackBar('Error in going to question try again.', "error");
					}
				}
			
			}, 120000); //2mins.
			//Autosave descritive end
			
			var showTestDetaisCount =0;
			function showTestDetais(){
				try{
					if(showTestDetaisCount === 0){
						Swal.fire({
			                title: 'Start Test',
			                text: ' ',
			                html: 'Started On : <b>'+testStartedOn+' IST</b>.<br>'
			                + 'Duration : <b>'+duration+' Minutes </b>.<br>'
			                + ' No Of Questions : <b>'+noOfQuestions+' </b>.<br>'
			                + ' <br><b>Do not Refresh Or Close the page before Submission, this will finish your test and you will not be Allowed to Rejoin.</b><br><br>'
			                + ' (Test will Start in 10 seconds, if not clicked on OK button)',
			                icon: "warning",
			                showCancelButton: false,
			                confirmButtonColor: '#3085d6',
			                confirmButtonText: 'Ok',
			                timer: 10000
			              }).then(function(result) {

							  if(resetTimer == 0){
								countdown = duration * 60 * 1000;
								resetTimer=1;
							  }
			            	  
			          		  //GoInFullscreen($("#element").get(0));
							  //showTestDetaisCount =0;
			              });
					}
					showTestDetaisCount =1;	
				}catch(err){
					showTestDetaisCount =1;
				}
			}
			showTestDetais();
			
			function setAlertLabelToSaveDescriptive(){

				qtnData = getQuestionDetailsByDivId(currentQuestion);
				
				if(qtnData !=null && qtnData.type == 4 ){
					let qtnId = qtnData.id;
					let alertSpanSelector = "#saveDescriptiveAlert-"+qtnId;
					let alertMessage = '<span style="padding: 5px 5px;" class="label label-danger"> Save Your Answer! </span>';
					
					$(alertSpanSelector).html(alertMessage);

					isSaveDescriptiveAlertLabelSet = true;
					
					function blinker() {
						//////////////console.log("****** blinker() called..."+window["setIntervalVar-"+qtnId]);
						$(alertSpanSelector).fadeOut(500);
						$(alertSpanSelector).fadeIn(500);
					}
					window["setIntervalVar"+qtnId] =  setInterval(blinker, 1000);
					
					isSaveDescriptiveAlertLabelSet = true;
				}
				
			}
			
			function hideAlertLabelToSaveDescriptive(){

				qtnData = getQuestionDetailsByDivId(currentQuestion);
				
				if(qtnData !=null && qtnData.type == 4 ){
					let qtnId = qtnData.id;
					let alertSpanSelector = "#saveDescriptiveAlert-"+qtnId;
					let alertMessage = '';
					$(alertSpanSelector).html(alertMessage);
					isSaveDescriptiveAlertLabelSet = false;
					clearInterval(window["setIntervalVar"+qtnId]);
				}
				
			}
		
		//ajax call to update remaining time start
		function updateRemainingTime(){
	    		var body = {
	    			'testId' : testId,
	    			'sapid' : sapid,
	    			'attempt' : totalAttempt
	    		};
	    		//////////////console.log(body);
	    		$.ajax({
	    			type : 'POST',
	    			url : '/exam/mbax/ia/s/updateRemainingTime',
	    			data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	                timeout : 10000,
	                success : function(data) {
	                	//////////////console.log("iN AJAX SUCCESS");
	                	//////////////console.log(data);
	                	
			    	},
	    			error : function(result) {
	                	//////////////console.log("iN AJAX eRROR");
	    				//////////////console.log(result);
	    				
	    				// retryUpdateRemainingTime start
	    				if(updateRemainingTimeCounter === 0){
	    					updateRemainingTimeCounter = 1;
	    					updateRemainingTime();
	    				}else{

		    				logErrorAjax(result,body);
		    			    questionUpdated=false;
							$('#parentSpinnerDiv').hide();
		    				showSnackBar("Error in updating remaining time.", "error");	
	    				}
	    				// retryUpdateRemainingTime end
	    				
		    		}
	    		});
		}
		
		//ajax call to update ramining time end
		

 		//code to add alert on page leave start
 		try{
		 window.onbeforeunload = confirmExit;
		   
		  function confirmExit()
		  {		
			  return "You have attempted to leave this page.  If you have made any changes without clicking the Save button, your changes will be lost.  Are you sure you want to exit this page?";
		   }
		  
 		 }catch(err){ 
			console.log("Error IN onbeforeunload :  ",err);
 		 }
	//code to add alert on page leave end
 		
		//code to check for offline and send error when online start
		try{
			window.addEventListener("online",function(){

				let offlineAlertSpanSelector = ".offlineAlertSpan";
				let alertMessage = '';
				
				$(offlineAlertSpanSelector).html(alertMessage);
				
				if(networkOfflineErrorRecords.length > 0){

					 var errorBody = {
				    			'errorAt' : 'Student back online. ',
				    		};
					 let errorRecords = networkOfflineErrorRecords;
					 logErrorAjax(errorRecords,errorBody);
					 $('#parentSpinnerDiv').hide();
					 networkOfflineErrorRecords =[];
				}
					
				Swal.fire("You are now online.", " ");
				
		  		updateConnectionStatus();
					
			});
			
			window.addEventListener("offline",function(){

				let offlineAlertSpanSelector = ".offlineAlertSpan";
				let alertMessage = '<span style="padding: 5px 5px; font-size:16px;" class="label label-danger"> You are Offline! </span>';
				
				$(offlineAlertSpanSelector).html(alertMessage);
			
				
				let d = currentDateTime()
				
				networkOfflineErrorRecords.push({
					'wasOfflineAt' : d,
					'message' : 'Student went offline'
				});
				

	  			document.getElementById("4gSpan").style.backgroundColor  = "lightgrey";
	  			document.getElementById("3gSpan").style.backgroundColor  = "lightgrey";
	  			document.getElementById("2gSpan").style.backgroundColor  = "lightgrey";
	  			document.getElementById("slow-2gSpan").style.backgroundColor  = "lightgrey";
				
				Swal.fire("You are offline.", " ");
			});
			
			
			
		 }catch(docError){
			 var docErrorBody = {
		    			'errorAtNetworkOfflineCheck' : 'Error in check for offline and send error when online',
		    		};
			 logErrorAjax(docError,docErrorBody);
			 $('#parentSpinnerDiv').hide();
		}
		//code to check for offline and send error when online end
			
		//Move functions from private to here start
			
			

	 		

				$(".closeReportLostFocusDiv").click(function(e){
					event.preventDefault();
					closeReportLostFocusDiv();
				});


				function closeReportLostFocusDiv(){
					try{	
						
						$("#submitReasonForLostFocusErrorMessage").text("");
						$("#reasonForLostFocus").val("");
						$(".closeReportLostFocusDiv").hide();

						
						$("#reportLostFocusParentDiv").hide();
						$("#redBackgroundDiv").hide();
					}catch(err){
						
					}
					
				}	
				

				$("#reportRLFPopAlert").click(function(e){
					event.preventDefault();
					showReportLostFocusDiv();
				});
				
				$(".showReportLostFocusDiv").click(function(e){
					event.preventDefault();
					showPageLostFocusAlert_WithSubmitReason("NA","Reason for Restart/Navigated Away ","",true);
					
				});


				
				

				$("#submitReasonForLostFocus").click(function(e){
					event.preventDefault();
					submitReasonForLostFocus();
				});
				function submitReasonForLostFocus(){
					try{	
						var reasonForLostFocus = $("#reasonForLostFocus").val();
			    		
						if(reasonForLostFocus || reasonForLostFocus.trim() !='' ){
							
						}else{
							$("#submitReasonForLostFocusErrorMessage").text("Please select/type a reason before submitting.");
							
							return;
						}
						
						/*data for reason for lost focus*/
						var dataForRLF =  {
								sapid : sapid,
								testId : testId,
								reason : reasonForLostFocus,
								contactedSupport : "Y",
								type : "STUDENT_REASON_FOR_LOST_FOCUS",
								userId : sapid
							};
			    		
			    		console.log("dataForRLF : ",dataForRLF);
			    		$("#submitReasonForLostFocusErrorMessage").text("Saving, Please Wait...");
						

			    		let resquest_time = new Date().getTime();
			    		let response_time = new Date().getTime();
			  			let apiUrl = window.location.origin+"/exam/mbax/ia/admin/saveReasonForLostFocus";
			    		
			    		
			    		$.ajax({
			    			type : 'POST',
			    			url : "/exam/mbax/ia/admin/saveReasonForLostFocus",
			    			data: JSON.stringify(dataForRLF),
			                contentType: "application/json",
			                dataType : "json",
			                timeout : 10000,
			                
			    		}).done(function(data) {
							console.log("iN submitReasonForLostFocus AJAX SUCCESS");
			              	console.log(data);
			              	
			              	isReasonForRestartPopUpActive = false;
			        	  	
				    		$("#submitReasonForLostFocusErrorMessage").text("");
			              	showSnackBar("Reason Saved!", "success");
				        	
							closeReportLostFocusDiv();
			              	
							try{
				    			response_time = new Date().getTime();
				    			response_payload_size = data ? data.length : 0 ;
				      			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Success","",response_payload_size)
							}catch(err){ 
									//////////console.log("Error in apiLogCall :"); 
									//////////console.log(err); 
								}
							
			    		}).fail(function(xhr) {
			    			console.log("iN submitReasonForLostFocus AJAX eRROR",xhr);
							showSnackBar("Error in saving reason, Please try again . ", "error");

				    		$("#submitReasonForLostFocusErrorMessage").text("Error in saving reason, Please try again . ");

							$(".closeReportLostFocusDiv").show();
							
			    			//apiLogCall start
			      			
			    			try{
				    			response_time = new Date().getTime();
				    			response_payload_size = xhr ? xhr.length : 0 ;
				      			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Error","Error in saving reason of lost focus : "+JSON.stringify(body)+JSON.stringify(xhr),response_payload_size)

			    			    logErrorAjax(xhr,body);
			    			}catch(err){

								//////////console.log("Error in apiLogCall :"); 
								//////////console.log(err); 
							}
							
			      			//apiLogCall end
				    		
			    		});
						
					}catch(err){
						
					}
					
				}
				
				
				function showPageLostFocusAlert(){
					
					showPageLostFocusAlert_WithSubmitReason("LostFocus","Navigated Away ","You had navigated away from the test window, This will be reported to the faculty. Do not repeat this behaviour, Otherwise you will be marked for plagiarism. Please submit a valid reason.",false);
					
					//showPageLostFocusAlert_OfSweatAlert(); //commented for any usecase of old way
				}

				function showPageLostFocusAlert_OfSweatAlert(){

					if("Y" != proctoringEnabled){
						return;
					}
					
					try{
						//alert("before alert");
						$("#redBackgroundDiv").show();
			            Swal.fire({
					                //title: "Navigated Away : "+".timeAwayCountForWeb : "+timeAwayCountForWeb+".timeMouseWasAway : "+timeMouseWasAway+".lostFocusCount : "+lostFocusCount+".document.hasFocus() : "+document.hasFocus()+". isMouseOnPage : "+isMouseOnPage,
					                title: "Navigated Away ",
					                text: "  You had navigated away from the test window, This will be reported to the faculty. Do not repeat this behaviour, Otherwise you will be marked for plagiarism.",
					                icon: "warning",
					                showCancelButton: false,
					                confirmButtonColor: '#d33',
					                confirmButtonText: 'Ok, Continue Test',
					                 
					              }).then(function(isConfirm) {
					            	  event.preventDefault();
									  $("#redBackgroundDiv").hide();

									  //GoInFullscreen($("#element").get(0));

									  showTestDetaisCount =1;
					              });	
					}catch(err){
						  $("#redBackgroundDiv").hide();

						  //GoInFullscreen($("#element").get(0));
						
					}
				} // showPageLostFocusAlert_OfSweatAlert
					
		
		 function showReportLostFocusDiv(){
				try{
					$("#redBackgroundDiv").show();
					$("#reportLostFocusParentDiv").show();
					
					logNavigatedAwayPopupShown();
					
				}catch(err){
					
				}
				
			}//showReportLostFocusDiv()	
				
		
			function showPageLostFocusAlert_WithSubmitReason(popUpType,title,subTitle,showCancelButtonForRLF){
				
				if("Y" != proctoringEnabled){
					return;
				}
				
				try{
					if("LostFocus" == popUpType){
						if(isReasonForRestartPopUpActive){
							title = "Reason For Restart and Navigating Away";
							subTitle = "You are not allowed to restart and navigating away from your test, Please submit a valid reason for doing so or your test attempt will be marked for copy case .";
						}
					}
					
					$("#reportRLFTitle").text(title);
					$("#reportRLFSubTitle").text(subTitle);
					$("#redBackgroundDiv").show();
					
					if(showCancelButtonForRLF){
						$(".closeReportLostFocusDiv").show();
					}else{
						$(".closeReportLostFocusDiv").hide();
					}
					
					showReportLostFocusDiv();
					
					
					
				}catch(err){
					  $("#redBackgroundDiv").hide();
					  closeReportLostFocusDiv();
				}	
			} // showPageLostFocusAlert_WithSubmitReason
			
			function logNavigatedAwayPopupShown(){
  		 		
				//apiLogCall start
				
	    	try{
	    		//console.log("In logNavigatedAwayPopupShown :"); 
			
	    		let apiUrl = window.location.origin+"/exam/mbax/ia/s/iaStarted/navigatedAwayPopupShown";
				asyncApiLogAjaxCall(sapid,apiUrl,(new Date().getTime()),(new Date().getTime()),"Success","",0)
				//console.log("after logNavigatedAwayPopupShown :"); 
				
	    	}catch(err){ 
				console.log("Error in logNavigatedAwayPopupShown :"); 
				console.log(err); 
			}
				//apiLogCall end
	  	}
			
			
		//Move functions from private to here end
		
		  	  <c:if test = "${continueAttempt == 'Y'}">
		  		isReasonForRestartPopUpActive = true;
			  	showPageLostFocusAlert_WithSubmitReason("Restarted","Reason for Restart ","You are not allowed to restart your test, Please submit a valid reason for doing so or your test attempt will be marked for copy case .",false);
			  	</c:if>

			  	//Code for checkIfTestAlreadyOpen start
			  	try{

			  		 $(window).on('storage', message_receive_checkIfTestAlreadyOpen);
			  	}catch(storageListenerError){
					 console.log('IN catch() > storageListenerError : ',storageListenerError);
				}
			  		// use local storage for messaging. Set message in local storage and clear it right away
			  		// This is a safe way how to communicate with other tabs while not leaving any traces
			  		//
			  		function message_broadcast_notifyTestDetailsPageOfIAPageLoaded(message)
			  		{	
			  			 console.log('IN message_broadcast_notifyTestDetailsPageOfIAPageLoaded > message : ',message);
							
				  		try{
				  		    localStorage.setItem('messageOfIAPageLoaded',JSON.stringify(message));
				  		    localStorage.removeItem('messageOfIAPageLoaded');
				  		}catch(messageOfIAPageLoadedError){
							 console.log('IN catch() > messageOfIAPageLoadedError : ',messageOfIAPageLoadedError);
						}
				  	}


			  		// receive message
			  		//
			  		function message_receive_checkIfTestAlreadyOpen(ev)
			  		{	
			  			 console.log('IN message_receive_checkIfTestAlreadyOpen > ev : ',ev);
						try{
							
						
						if (ev.originalEvent.key!='messageIsIAPageAlreadyOpen') return; // ignore other keys
			  		    var message=JSON.parse(ev.originalEvent.newValue);
			  		    if (!message) return; // ignore empty msg or msg reset

			  		    // here you act on messages.
			  		    // you can send objects like { 'command': 'doit', 'data': 'abcd' }
			  		    if (message.command == 'isIAPageOpen'){
			  		    	message_broadcast_notifyTestDetailsPageOfIAPageLoaded({'command':'IAPageIsLoaded', 'uid': (new Date).getTime()+Math.random()})
						}
						// etc.
			  		    
						}catch(checkIfTestAlreadyOpenError){
							 console.log('IN catch() > checkIfTestAlreadyOpenError : ',checkIfTestAlreadyOpenError);
						}
			  		}
					//Code for checkIfTestAlreadyOpen end
			  		
			  		function logPageLoadedEvent(){
		  		 		
						//apiLogCall start
						
			    	try{
			    		let apiUrl = window.location.origin+"/exam/mbax/ia/s/startStudentTestPage_pageLoaded";
						response_time_logPageLoadedEvent = new Date().getTime();
						response_payload_size_logPageLoadedEvent = 0 ;
			  			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time_logPageLoadedEvent,response_time_logPageLoadedEvent,"Success","",response_payload_size_logPageLoadedEvent)
						
			    	}catch(err){ 
						//////////console.log("Error in apiLogCall :"); 
						//////////console.log(err); 
					}
						//apiLogCall end
			  	}
				
			  	logPageLoadedEvent();
			
			  	function checkIfQuestionsDivIsVisible(){
			  		try{
				  	    var style = window.getComputedStyle(document.getElementById("questionMainDiv-1"));
				  	    console.log(' style : ',style);
				  	    if (style.display === 'none'){
				  	    	alert('If questions are not visible, Please close all tabs and retry after joining again. PRESS OK to continue.');
				  	    }
			  			
			  		}catch(err){ 
			  			alert('If question is not visible, Please close all tabs and retry after joining again. PRESS OK to continue.');
				  	}

			  	}
			  	checkIfQuestionsDivIsVisible();
			  	
			  	function checkNetworkHealth(){
			  		try{  
			  			
			  		let downlink = '';
			  		let rtt = '';
			  		let downlinkMax = '';
			  		let effectiveType = '';
			  		let type = '';
			  		
			  		try{ downlink = navigator.connection.downlink; }catch(err){}
			  		try{ rtt = navigator.connection.rtt; }catch(err){}
			  		try{ downlinkMax = navigator.connection.downlinkMax; }catch(err){}
			  		try{ effectiveType = navigator.connection.effectiveType; }catch(err){}
			  		try{ type = navigator.connection.type; }catch(err){}
			  		
			  		console.log('downlink : '+downlink+'. rtt : '+rtt+'. downlinkMax : '+downlinkMax+'. effectiveType : '+effectiveType+'. type : '+type+'');
				  	
			  		let networkStrength = '';
			  		
			  		
			  		
			  		}catch(err){
			  			console.log('checkNetworkHealth err : ',err);
				  		
			  		}
			        
			  	}
			  	
			  	try{
			  		var networkConnection = navigator.connection || navigator.mozConnection || navigator.webkitConnection;
			  		var networkType = networkConnection.effectiveType;
					var networkTypePriorityMap = new Map();
					networkTypePriorityMap.set("4g",4);
					networkTypePriorityMap.set("3g",3);
					networkTypePriorityMap.set("2g",2);
					networkTypePriorityMap.set("slow-2g",1);
					
			  		function updateConnectionStatus() {
			  			try{
			  				
			  		  console.log("Connection type changed from " + networkType + " to " + networkConnection.effectiveType);
			  		  changedNetworkType = networkConnection.effectiveType;
			  		  
			  		  let networkTypePriority = networkTypePriorityMap.get(networkType);
			  		  let changedNetworkTypePriority = networkTypePriorityMap.get(changedNetworkType);

			  		  console.log('networkType : '+networkType+'. ');
			  		  console.log('changedNetworkType : '+changedNetworkType+'. ');
			  		  
			  		  console.log('networkTypePriority : '+networkTypePriority+'. ');
			  		  console.log('changedNetworkTypePriority : '+changedNetworkTypePriority+'. ');
			  		  
			  		let networkHealthSpanSelector = ".networkHealthSpan";
					let networkHealthAlertMessage = '';
					  
			  		  if((changedNetworkTypePriority < networkTypePriority) && (changedNetworkTypePriority < 3 )){
			  			networkHealthAlertMessage = '<span style="padding: 5px 5px; font-size:16px;" class="label label-warning">  Internet Connection is unstable. </span>';
					  }
			  		  
			  		  $(networkHealthSpanSelector).html(networkHealthAlertMessage);
			  		  
			  		try{
			  			let apiUrl = window.location.origin+"/exam/mbax/ia/s/startStudentTestPage_CurrentNetworkHealth_From_"+networkType+"_To_"+changedNetworkType;
					response_time_logPageLoadedEvent = new Date().getTime();
					response_payload_size_logPageLoadedEvent = 0 ;
		  			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time_logPageLoadedEvent,response_time_logPageLoadedEvent,"Success","",response_payload_size_logPageLoadedEvent)
					
			  		}catch(cnhErr){console.log('cnhErr',cnhErr);}
			  		
			  		  
			  		  
			  		  networkType = networkConnection.effectiveType;
						

			  		  if(changedNetworkTypePriority == 4){
			  			document.getElementById("4gSpan").style.backgroundColor  = "green";
			  			document.getElementById("3gSpan").style.backgroundColor  = "green";
			  			document.getElementById("2gSpan").style.backgroundColor  = "green";
			  			document.getElementById("slow-2gSpan").style.backgroundColor  = "green";
					 }else if(changedNetworkTypePriority == 3){
				  			document.getElementById("4gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("3gSpan").style.backgroundColor  = "green";
				  			document.getElementById("2gSpan").style.backgroundColor  = "green";
				  			document.getElementById("slow-2gSpan").style.backgroundColor  = "green";
					 }else if(changedNetworkTypePriority == 2){
				  			document.getElementById("4gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("3gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("2gSpan").style.backgroundColor  = "green";
				  			document.getElementById("slow-2gSpan").style.backgroundColor  = "green";
					 }else if(changedNetworkTypePriority == 1){
				  			document.getElementById("4gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("3gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("2gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("slow-2gSpan").style.backgroundColor  = "green";
					 }else {
				  			document.getElementById("4gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("3gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("2gSpan").style.backgroundColor  = "lightgrey";
				  			document.getElementById("slow-2gSpan").style.backgroundColor  = "lightgrey";
					 }
			  		  
			  		  
			  			}catch(err){
			  				console.log('updateConnectionStatus error : ',err);
			  			
			  				document.getElementById("networkHealth").style.display  = "none";
			  				$(".networkHealthSpan").css("display", "none");

			  			}
			  		
			  		}//updateConnectionStatus
			  		updateConnectionStatus();
			  		
			  		networkConnection.addEventListener('change', updateConnectionStatus);
			  	}catch(err){
			  		console.log('Change connection error : ',err);
			  	}
			  	
		 }catch(docError){
			 console.log('IN catch() > docError : ',docError);
			 var docErrorBody = {
		    			'errorAt' : 'Error in doc.ready() : '+JSON.stringify(docError),
		    		};
			 
			 try{
				 logErrorAjax(docError,docErrorBody);
			}catch(docErrorLogErrorAjax){
				
			}
			 
			 let d = currentDateTime()
			 if(networkOfflineErrorRecords){
				 networkOfflineErrorRecords.push({
						'wasOfflineAt' : d,
		    			'errorAt' : 'Error in doc.ready()'
					});
			}else{
				networkOfflineErrorRecords = [];
				 networkOfflineErrorRecords.push({
						'wasOfflineAt' : d,
		    			'errorAt' : docErrorBody,
					});
				
			}
			 $('#parentSpinnerDiv').hide();
			 
			 alert("Something went wrong. Please close the window, logout, login and continue the test again.");
		 }
		
	}); //end of doc ready()
	
	//Shifting this here to make is accessible in pdf/video upload ajax
	var testId = ${test.id};
	var sapid = ${userId};
	var networkOfflineErrorRecords = [];	
	
	//code to save error log start

	//logErrorAjax start
	 function logErrorAjax(stackTrace,body){
		//////////////console.log("In logErrorAjax() ENTERED...");
		var methodRetruns = false;
		//ajax to save logErrorAjax reponse start
		
		var networkInfo = {}
		try {
		    networkInfo = {
		        downlink : navigator.connection.downlink,
		        rtt : navigator.connection.rtt,
		        downlinkMax : navigator.connection.downlinkMax,
		        effectiveType : navigator.connection.effectiveType,
		        type : navigator.connection.type,
		        saveData : navigator.connection.saveData,
		    }
		} catch(error) {
		    networkInfo = { errorMessage : 'Not Available' }
		}
		                            
		var body = {
			'sapid' : sapid,
			'module' : 'test',
			'stackTrace' : JSON.stringify(body)+' - '+JSON.stringify(stackTrace)+'. Network Info :  '+JSON.stringify(networkInfo),
			
		};
		//////////////console.log(body);
		$.ajax({
			type : 'POST',
			url : '/studentportal/m/logError',
			data: JSON.stringify(body),
            contentType: "application/json",
            dataType : "json",
            
		}).done(function(data) {
			  //////////////console.log("iN logError AJAX SUCCESS");
          	//////////////console.log(data);
          	
		}).fail(function(xhr) {
			//////////////console.log("iN logError AJAX eRROR");
			//////////////console.log('error', xhr);
			let d = currentDateTime()
			networkOfflineErrorRecords.push({
				'errorAt' : d,
    			'message' : 'Error in logError'
			});
			
			networkOfflineErrorRecords.push(body);
			
			
		  });
		//ajax to save question reponse end
		////////////////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
		 

	}
	
	//code to save error log end
	
	//asyncApiLogAjaxCall start
	 function asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,status,error_message,response_payload_size){
		try{
			
			let networkInfoForAsyncApiLogAjaxCall = {}
			try {
				let deviceMemoryInfo ="";
				let platformInfo ="";
				let hardwareConcurrencyInfo ="";
				
				
				try {
						
					deviceMemoryInfo = "This device has at least "+ (navigator.deviceMemory ? navigator.deviceMemory+"":"") + " GiB of RAM.";
				} catch(error) {
					deviceMemoryInfo = "Not Available";
				}
				try {
					
					platformInfo = ""+ (navigator.platform ? navigator.platform:"") + "";
				} catch(error) {
					platformInfo = "Not Available";
				}
				try {
					
					hardwareConcurrencyInfo = ""+ (window.navigator.hardwareConcurrency ? window.navigator.hardwareConcurrency+"":"") + "";
				} catch(error) {
					hardwareConcurrencyInfo = "Not Available";
				}
				
			    networkInfoForAsyncApiLogAjaxCall = {
			        downlink : navigator.connection.downlink,
			        rtt : navigator.connection.rtt,
			        downlinkMax : navigator.connection.downlinkMax,
			        effectiveType : navigator.connection.effectiveType,
			        type : navigator.connection.type,
			        saveData : navigator.connection.saveData,
			        deviceMemoryInfo : deviceMemoryInfo,
			        platformInfo : platformInfo,
			        hardwareConcurrencyInfo : hardwareConcurrencyInfo,
			    }
			} catch(error) {
				networkInfoForAsyncApiLogAjaxCall = { errorMessage : 'Not Available' }
			}	
		let bodyForAsyncApiLogAjaxCall = {
			
			"sapid": sapid ? sapid.toString() : "",
            "api": apiUrl,
            "resquest_time": resquest_time ,
            "response_time": response_time ,
            "response_payload_size": response_payload_size ? response_payload_size.toString() : "0" ,
            "status": status,
            "error_message": error_message,
            "platform": "Web",
            "networkInfo" :JSON.stringify(networkInfoForAsyncApiLogAjaxCall),
			
		};
		//console.log("IN asyncApiLogAjaxCall got bodyForAsyncApiLogAjaxCall : ");
		//console.log(bodyForAsyncApiLogAjaxCall);
		$.ajax({
			type : 'POST',
			url : 'https://ngasce-content.nmims.edu/ltidemo/saveNetworkLogs',
			data: JSON.stringify(bodyForAsyncApiLogAjaxCall),
            contentType: "application/json",
            dataType : "json",
            timeout : 10000,
            
		}).done(function(data) {
			//console.log("iN asyncApiLogAjaxCall AJAX SUCCESS");
          	//console.log(data);
          	
		}).fail(function(xhr) {
			console.log("iN asyncApiLogAjaxCall AJAX eRROR",xhr);
			
			
		  });
		
		}catch(err){
			console.log("IN asyncApiLogAjaxCall got Error : ");
			console.log(err);
		}
		
	}
	//asyncApiLogAjaxCall end
	
</script>

<script>
function appendLeadingZeroes(n){
	  if(n <= 9){
	    return "0" + n;
	  }
	  return n
	}	
	
function currentDateTime(){		
	let current_datetime  = new Date()			
	let formatted_date = current_datetime.getFullYear() + "-" + appendLeadingZeroes(current_datetime.getMonth() + 1) + "-" + appendLeadingZeroes(current_datetime.getDate()) + " " + appendLeadingZeroes(current_datetime.getHours()) + ":" + appendLeadingZeroes(current_datetime.getMinutes()) + ":" + appendLeadingZeroes(current_datetime.getSeconds())
	return formatted_date
	
	}	
</script>
	<!-- FOR Custome text area -->

	<!-- Include jQuery lib.
	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	 -->
	<!-- Include Editor JS files. 
	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1//js/froala_editor.pkgd.min.js"></script>
	-->
	<!-- Initialize the editor. 
	<script>
  $(function() {
    $('textareaNU').froalaEditor();
  });
  
  $('textareaNU').on('froalaEditor.initialized', function (e, editor) {
	  editor.events.on('drop', function () { return false; }, true);
	});
</script>
	 end -->


	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); 
});

</script>

	<script>




// Showing next button on click of previous button
/* function showNextButtonOnClickOfPreviousButton(){
	//////////////console.log('IN showNextButtonOnClickOfPreviousButton()');
	$('.nextQuestionButton').show();
} */

$('#parentSpinnerDiv').hide();

</script>

	<script>
try{
parent && parent.window.setHideShowHeaderSidebarBreadcrumbs ? parent.window.setHideShowHeaderSidebarBreadcrumbs(true) : null
}catch(err){
	//////////////console.log(err);
}
</script>


	<!-- script to disable back button -->
	<script>
try{
	history.pushState(null, null, location.href);
    window.onpopstate = function () {
        history.go(1);
    };
}catch(err){
	//////////////console.log(err);
}
</script>


	<!-- script to go full screen 
<script>
try{

	//////////////console.log("Calling GoInFullscreen() ");
	GoInFullscreen($("#pageContainer"));
	
/* Get into full screen */
function GoInFullscreen(element) {
	//////////////console.log("Calling GoInFullscreen() element ");
	//////////////console.log(element);
	if(element.requestFullscreen)
		element.requestFullscreen();
	else if(element.mozRequestFullScreen)
		element.mozRequestFullScreen();
	else if(element.webkitRequestFullscreen)
		element.webkitRequestFullscreen();
	else if(element.msRequestFullscreen)
		element.msRequestFullscreen();
}


	
}catch(err){
	//////////////console.log(err);
}
</script>
-->


	<script>

/* Get out of full screen */
function GoOutFullscreen() {
	if(document.exitFullscreen)
		document.exitFullscreen();
	else if(document.mozCancelFullScreen)
		document.mozCancelFullScreen();
	else if(document.webkitExitFullscreen)
		document.webkitExitFullscreen();
	else if(document.msExitFullscreen)
		document.msExitFullscreen();
}





//Image, Video size and type validation
$('.input-file-btn').change(function (e) {
	//$('#answerFile').hide();
	
	$('.btn-success').attr('disabled', true);
	$('#parentSpinnerDiv').show();
	$('#uploadingAnswerMessage').text("Verifying File. Please wait...");
	//////////////console.log("e.target.files[0]");
	//////////////console.log(e.target.files[0]);
	if($('#file-link').get(0).files.length === 0){
		$('#parentSpinnerDiv').hide();
		$('#uploadingAnswerMessage').text("Please select file again using choose file.");
		alert("FILE NOT UPLOADED . PLEASE CHECK !");
		////////////console.log($('#hiddenAssngUrl').length);
		if($('#hiddenAssngUrl').length === 0){
			
		}else{
			////////////console.log("button is enabled");
			$('.btn-success').attr('disabled', false);
		}
		return false;
	}
	var filename = e.target.files[0].name;
	$('#file-name').val(filename);
	var extension = filename.split('.')[1];
	var file = e.target.files[0];
	var fileType = file["type"];

	var ValidTypes = [];
	var filesize = this.files[0].size;
	var acceptedSize;
	flag = false;
	////////////console.log("filename : "+filename);
	////////////console.log(extension);
	////////////console.log(file);
	////////////console.log(fileType);
	if($('#hiddenUploadType').val()=="pdf"){
		acceptedSize =  70000000;
		ValidTypes = ["application/pdf"];
	}else{
		acceptedSize =  70000000;
		ValidTypes = ["video/mp4"];
	};
	

	if ($.inArray(fileType, ValidTypes) < 0) {
		// invalid file type code goes here
			$('#parentSpinnerDiv').hide();
			$('#uploadingAnswerMessage').text("Invalid file. Please select file again using choose file.");
			try{
				logErrorAjax('INVALID FILE TYPE ERROR. Valid types are : ' + ValidTypes + '  Given file type : ' + fileType,'');
			}catch(err){ console.log('log INVALID FILE TYPE ERROR. ',err); }
			//alert('Invalid File Type Error \n Valid types are : ' + ValidTypes + '\n  Given file type : ' + fileType);
		alert('INVALID FILE TYPE ERROR. \n Valid types are : ' + ValidTypes + '\n  Given file type : ' + fileType);
		return false;
	}else{
		flag = true;
	}
	if (filesize > acceptedSize) {
		$('#parentSpinnerDiv').hide();
		$('#uploadingAnswerMessage').text("Invalid file. Please select file again using choose file.");
		try{
			logErrorAjax('file size '+(filesize/1000000)+' MB exceeded the ' + acceptedSize / 1000000 + ' MB limit','');
		}catch(err){ console.log('log file size error : ',err); }
		alert('file size '+(filesize/1000000)+' MB exceeded the ' + acceptedSize / 1000000 + 'MB limit');
		return false;
	}else{
		flag = true;
	}
	
	if(flag){
		saveMedia(file);	
	}
	

});



function saveMedia(myFile) {
	try{
		let resquest_time = new Date().getTime();
		let response_time = new Date().getTime();
		let apiUrl = window.location.origin+"/exam/mbax/ia/sm/m/uploadTestAnswerFile";

		
		var myFileInput = $('#file-link').prop('files')[0];
		////////////console.log("myFileInput : ");
		////////////console.log(myFileInput);
		
		var formData = new FormData();
		formData.append('image', myFileInput); 
		 $.ajax({
			url : '/exam/mbax/ia/sm/m/uploadTestAnswerFile?testId=${test.id}&userId=${userId}',
			data : formData,
			processData : false,
			contentType : false,
			type : 'POST',
			success : function(data) {
				
				try{
	    			response_time = new Date().getTime();
	    			response_payload_size = data ? data.length : 0 ;
	      			asyncApiLogAjaxCall("${userId}",apiUrl,resquest_time,response_time,"Success","",response_payload_size)
					}catch(err){ 
						console.log("Error in saveMedia apiLogCall :"); 
						console.log(err); 
					}
				
				////////////console.log("File uploaded : IN uploadTestQuestionImage got data : ");
				////////////console.log(data);
				//$('#link-location-text-link').val(data["imageUrl"]);
				//$('#link-location-text-link').attr('readonly', true);
				$('#hiddenAssngUrl').val(data["imageUrl"]);
				$('.btn-success').attr('disabled', false);
				$('#parentSpinnerDiv').hide();
				$('#uploadingAnswerMessage').text("Please click on save button to submit your answer.");
				$('#submitTest').attr('disabled', false);
				setTimeout(function(){ alert("YOUR FILE IS UPDATED. PLEASE CLICK ON SAVE TO SUBMIT IT !"); }, 1000);
			},
			error : function(err) {
				
				try{
	    			response_time = new Date().getTime();
	    			response_payload_size = data ? data.length : 0 ;
	      			asyncApiLogAjaxCall("${userId}",apiUrl,resquest_time,response_time,"Error",JSON.stringify(err),response_payload_size)
					}catch(err){ 
						console.log("Error in saveMedia apiLogCall :"); 
						console.log(err); 
					}
				
				////////////console.log("File uploaded : IN uploadTestQuestionImage got error : ");
				$('#parentSpinnerDiv').hide();
				$('#uploadingAnswerMessage').text("Unable to verify your file. Please select file again using choose file.");
				//alert('File size should be less than 70Mb');
			}
		}); 
	}catch(error){
		alert('Following Error Occurred:'+error); 
	}
}

</script>

	<!-- Comment below script to enable right click

	-->  
  	<script>
		document.oncontextmenu = new Function("return false;");
	</script>  
	
</body>
</html>