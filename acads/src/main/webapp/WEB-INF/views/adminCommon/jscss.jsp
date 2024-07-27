<%--      <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title><%=request.getParameter("title") %></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="shortcut icon" href="resources_2015/images/icons/favicon.png">
        <link rel="apple-touch-icon" href="resources_2015/images/icons/apple-touch-icon.png">

        <link rel="stylesheet" href="resources_2015/css/bootstrap.css">
        <!--<link rel="stylesheet" href="resources_2015/css/bootstrap.min.css">-->
        <!--<link rel="stylesheet" href="resources_2015/css/bootstrap-theme.min.css">-->
        <link rel="stylesheet" href="resources_2015/css/font-awesome.min.css">
		<link rel="stylesheet" href="resources_2015/css/jquery-ui.min.css">
        <link rel="stylesheet" href="resources_2015/css/main.css">
        <link rel="stylesheet" href="resources_2015/css/styles.css">
        <link rel="stylesheet" href="resources_2015/css/demo-styles.css?id=10" />
        <link rel="stylesheet" href="resources_2015/css/fileinput.css" />
        <link rel="stylesheet" href="resources_2015/css/bootstrap-datepicker.css" />
        <link rel="stylesheet" href="resources_2015/css/slick.css" />
        <link rel="stylesheet" href="resources_2015/css/slick-theme.css" />
        
		<!-- Editable element CSS -->
		<link rel="stylesheet" href="resources_2015/css/bootstrap-editable.css" />
        <script src="resources_2015/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script>
        
        <!--[if lt IE 9]>
          <script src="resources_2015/js/vendor/html5shiv.min.js"></script>
          <script src="resources_2015/js/vendor/respond.min.js"></script>
        <![endif]-->

    </head> --%>
    
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
<head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title><%=request.getParameter("title") %></title>
        <!-- Bootstrap -->
        <link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/css/bootstrap.min.css" rel="stylesheet">
        <link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/css/font-awesome.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/css/fullcalendar.min.css?version=3.9.0">
        <link rel="stylesheet" type="text/css" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/css/fonts.css">
        <link rel="stylesheet" type="text/css" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/css/style.css?v=3">
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css">
        <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/bootstrap-combobox.css" />
        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
          <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
        
        <script>
		  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
		  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
		  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
		
		  ga('create', 'UA-51056462-2', 'auto');
		  ga('send', 'pageview');
		
		</script>
    </head>