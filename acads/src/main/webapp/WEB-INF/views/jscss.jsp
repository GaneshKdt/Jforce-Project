<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

     <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title><%=request.getParameter("title") %></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="shortcut icon" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/images/icons/favicon.png">
        <link rel="apple-touch-icon" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/images/icons/apple-touch-icon.png">

        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/bootstrap.css">
        <!--<link rel="stylesheet" href="resources_2015/css/bootstrap.min.css">-->
        <!--<link rel="stylesheet" href="resources_2015/css/bootstrap-theme.min.css">-->
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/font-awesome.min.css">
		<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/jquery-ui.min.css">
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/main.css">
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/styles.css?id=1">
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/demo-styles.css" />
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/fileinput.css" />
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/bootstrap-datepicker.css" />
		<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/bootstrap-combobox.css" />
		<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/fullcalendar.css">
		<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/fullcalendar.print.css" rel='stylesheet' media='print'>
        <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script>
        <link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/bootstrap-editable.css" rel="stylesheet">
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/typography.css">
		<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/css/custom-admin.css" >   
        <!--[if lt IE 9]>
          <script src="resources_2015/js/vendor/html5shiv.min.js"></script>
          <script src="resources_2015/js/vendor/respond.min.js"></script>
        <![endif]-->

    </head>