        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


     <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title><%=request.getParameter("title") %></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="shortcut icon" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/images/icons/favicon.png">
        <link rel="apple-touch-icon" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/images/icons/apple-touch-icon.png">

        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/bootstrap.css">
        <!--<link rel="stylesheet" href="resources_2015/css/bootstrap.min.css">-->
        <!--<link rel="stylesheet" href="resources_2015/css/bootstrap-theme.min.css">-->
<%--         <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/font-awesome.min.css"> --%>
		<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/jquery-ui.min.css">
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/main.css">
        
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/demo-styles.css" />
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/fileinput.css" />
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/bootstrap-datepicker.css" />
		<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/TimeCircles.css">
        <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script>
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/bootstrap-combobox.css" />
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/typography.css">
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/datatable.css" />
        <!-- Editable element CSS -->
		<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/bootstrap-editable.css" rel="stylesheet">	
		<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/styles.css?id=2">
		<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/dependent-dropdown.css" rel="stylesheet">	
		
		 <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/FontAwesome-6.2.1.js" crossorigin="anonymous"></script>

        <!--[if lt IE 9]>
          <script src="resources_2015/js/vendor/html5shiv.min.js"></script>
          <script src="resources_2015/js/vendor/respond.min.js"></script>
        <![endif]-->

		<style>
		.modal-backdrop {
		  z-index: -1;
		}
		
		#checkLabel{
			 display: block;
   padding-left: 20px; 
  
		}
			#checkme {
			  width: 13px;
			  height: 23px;
			  padding: 0;
			  margin:0;
			  vertical-align: bottom;
			  position: relative;
			  top: -1px;
			 
			}
		
		</style>
	
    </head>