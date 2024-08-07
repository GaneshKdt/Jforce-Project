<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script>
try {
	// Page views analytics
	$( document ).ready(function() {
		prepareData();

		logPageView();
	})
	
	window.addEventListener('beforeunload', function (e) {
		e.preventDefault();
		
		// add the time spent field and values ---
		
		if(!window.analyticsData) {
			prepareData()
		}
		
		var data = window.analyticsData
		data.lastTimestamp = new Date().getTime()
		data.timeSpent = data.lastTimestamp - data.initialTimeStamp
		window.analyticsData = data
		
		logPageView();
	});
} catch (e) {
	console.log(e);
}	
</script>

<script>
	// API analytics
	try{
		$.ajaxSetup({
			beforeSend: function() {
			},
			complete: function() {
			}
		});
	}catch(e){
		console.log(e);
	}
</script>


<script>
	// ----------- FUNCTIONS ----------
try{	
	function logPageView() {
		// Log only on Production.
		if(getEnvironment() == 'PROD') {
			$.ajax({
				url: getAnalyticsBaseAPIPath() + 'ltidemo/registerUpdate',
				type: 'post',
				dataType: 'application/json',
				contentType: 'application/json',
				data: JSON.stringify(window.analyticsData)
			});
		} else {
			console.log(window.analyticsData)
		}
	}

	function prepareData() {
		var data = {
			page : getPage(),
			sapid : getUserId(),
			initialTimeStamp : new Date().getTime(),
			timeSpent : 0,
			client : getClientDetails(),
			type : 'Web - PG',
		}
		
		window.analyticsData = data
	}
	
	function getUserId() {
		return "${userId}"
	}
	
	
	function getAnalyticsBaseAPIPath() {
		if(getEnvironment() == 'PROD') {
			return 'https://ngasce-content.nmims.edu/'
		} else {
			return getServer();
		}
	}
	
	
	function getClientDetails() {
		try{
			var client = {
				applicationVersion : '1',
				applicationType : 'Web - PG',
				deviceType : 'Browser',
				deviceName : window.browserInfo.os,
				deviceOS : window.browserInfo.os,
				deviceSystemVersion : window.browserInfo.osVersion,
				browserName : window.browserInfo.browser,
				browserVersion : window.browserInfo.browserVersion, 
			}
			if('geolocation' in navigator) {
			/* geolocation is available */
			navigator.geolocation.getCurrentPosition(function(position){
				client.latitude = position.coords.latitude;
				client.longitude = position.coords.longitude;
			});
			} else {
			/* geolocation IS NOT available */
			client.latitude = 0;
			client.longitude = 0;
			}
			return client
		}catch(e){
			return {}
		}
	}
	
	function getServer() {
		return `<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />`
	}
	
	function getEnvironment() {
		return `<spring:eval expression="@propertyConfigurer.getProperty('ENVIRONMENT')" />`
	}
	
	function getPage() {
		return window.location.pathname;
	}
// This script generates browser version string
(function (window) {
    {
    /* test cases
        alert(
            'browserInfo result: OS: ' + browserInfo.os +' '+ browserInfo.osVersion + '\n'+
                'Browser: ' + browserInfo.browser +' '+ browserInfo.browserVersion + '\n' +
                'Mobile: ' + browserInfo.mobile + '\n' +
                'Cookies: ' + browserInfo.cookies + '\n' +
                'Screen Size: ' + browserInfo.screen
        );
    */
        var unknown = 'Unknown';
    
        //browser
        var nVer = navigator.appVersion;
        var nAgt = navigator.userAgent;
        var browser = navigator.appName;
        var version = '' + parseFloat(navigator.appVersion);
        var majorVersion = parseInt(navigator.appVersion, 10);
        var nameOffset, verOffset, ix;

        // Opera
        if ((verOffset = nAgt.indexOf('Opera')) != -1) {
            browser = 'Opera';
            version = nAgt.substring(verOffset + 6);
            if ((verOffset = nAgt.indexOf('Version')) != -1) {
                version = nAgt.substring(verOffset + 8);
            }
        }
        // MSIE
        else if ((verOffset = nAgt.indexOf('MSIE')) != -1) {
            browser = 'Microsoft Internet Explorer';
            version = nAgt.substring(verOffset + 5);
        }

        //IE 11 no longer identifies itself as MS IE, so trap it
        //http://stackoverflow.com/questions/17907445/how-to-detect-ie11
        else if ((browser == 'Netscape') && (nAgt.indexOf('Trident/') != -1)) {

            browser = 'Microsoft Internet Explorer';
            version = nAgt.substring(verOffset + 5);
            if ((verOffset = nAgt.indexOf('rv:')) != -1) {
                version = nAgt.substring(verOffset + 3);
            }

        }

        // Chrome
        else if ((verOffset = nAgt.indexOf('Chrome')) != -1) {
            browser = 'Chrome';
            version = nAgt.substring(verOffset + 7);
        }
        // Safari
        else if ((verOffset = nAgt.indexOf('Safari')) != -1) {
            browser = 'Safari';
            version = nAgt.substring(verOffset + 7);
            if ((verOffset = nAgt.indexOf('Version')) != -1) {
                version = nAgt.substring(verOffset + 8);
            }

            // Chrome on iPad identifies itself as Safari. Actual results do not match what Google claims
            //  at: https://developers.google.com/chrome/mobile/docs/user-agent?hl=ja
            //  No mention of chrome in the user agent string. However it does mention CriOS, which presumably
            //  can be keyed on to detect it.
            if (nAgt.indexOf('CriOS') != -1) {
                //Chrome on iPad spoofing Safari...correct it.
                browser = 'Chrome';
                //Don't believe there is a way to grab the accurate version number, so leaving that for now.
            }
        }
        // Firefox
        else if ((verOffset = nAgt.indexOf('Firefox')) != -1) {
            browser = 'Firefox';
            version = nAgt.substring(verOffset + 8);
        }
        // Other browsers
        else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) < (verOffset = nAgt.lastIndexOf('/'))) {
            browser = nAgt.substring(nameOffset, verOffset);
            version = nAgt.substring(verOffset + 1);
            if (browser.toLowerCase() == browser.toUpperCase()) {
                browser = navigator.appName;
            }
        }
        // trim the version string
        if ((ix = version.indexOf(';')) != -1) version = version.substring(0, ix);
        if ((ix = version.indexOf(' ')) != -1) version = version.substring(0, ix);
        if ((ix = version.indexOf(')')) != -1) version = version.substring(0, ix);

        majorVersion = parseInt('' + version, 10);
        if (isNaN(majorVersion)) {
            version = '' + parseFloat(navigator.appVersion);
            majorVersion = parseInt(navigator.appVersion, 10);
        }

        // mobile version
        var mobile = /Mobile|mini|Fennec|Android|iP(ad|od|hone)/.test(nVer);

        // cookie
        var cookieEnabled = (navigator.cookieEnabled) ? true : false;

        if (typeof navigator.cookieEnabled == 'undefined' && !cookieEnabled) {
            document.cookie = 'testcookie';
            cookieEnabled = (document.cookie.indexOf('testcookie') != -1) ? true : false;
        }

        // system
        var os = unknown;
        var clientStrings = [
            {s:'Windows 3.11', r:/Win16/},
            {s:'Windows 95', r:/(Windows 95|Win95|Windows_95)/},
            {s:'Windows ME', r:/(Win 9x 4.90|Windows ME)/},
            {s:'Windows 98', r:/(Windows 98|Win98)/},
            {s:'Windows CE', r:/Windows CE/},
            {s:'Windows 2000', r:/(Windows NT 5.0|Windows 2000)/},
            {s:'Windows XP', r:/(Windows NT 5.1|Windows XP)/},
            {s:'Windows Server 2003', r:/Windows NT 5.2/},
            {s:'Windows Vista', r:/Windows NT 6.0/},
            {s:'Windows 7', r:/(Windows 7|Windows NT 6.1)/},
            {s:'Windows 8.1', r:/(Windows 8.1|Windows NT 6.3)/},
            {s:'Windows 8', r:/(Windows 8|Windows NT 6.2)/},
            {s:'Windows 10', r:/(Windows 10|Windows NT 10.0)/},
            {s:'Windows NT 4.0', r:/(Windows NT 4.0|WinNT4.0|WinNT|Windows NT)/},
            {s:'Windows ME', r:/Windows ME/},
            {s:'Android', r:/Android/},
            {s:'Open BSD', r:/OpenBSD/},
            {s:'Sun OS', r:/SunOS/},
            {s:'Linux', r:/(Linux|X11)/},
            {s:'iOS', r:/(iPhone|iPad|iPod)/},
            {s:'Mac OS X', r:/Mac OS X/},
            {s:'Mac OS', r:/(MacPPC|MacIntel|Mac_PowerPC|Macintosh)/},
            {s:'QNX', r:/QNX/},
            {s:'UNIX', r:/UNIX/},
            {s:'BeOS', r:/BeOS/},
            {s:'OS/2', r:/OS\/2/},
            {s:'Search Bot', r:/(nuhk|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask Jeeves\/Teoma|ia_archiver)/}
        ];
        for (var id in clientStrings) {
            var cs = clientStrings[id];
            if (cs.r.test(nAgt)) {
                os = cs.s;
                break;
            }
        }

        var osVersion = unknown;

        if (/Windows/.test(os)) {
            osVersion = /Windows (.*)/.exec(os)[1];
            os = 'Windows';
        }

        switch (os) {
            case 'Mac OS X':
                osVersion = /Mac OS X (10[\.\_\d]+)/.exec(nAgt)[1];
                break;

            case 'Android':
                osVersion = /Android ([\.\_\d]+)/.exec(nAgt)[1];
                break;

            case 'iOS':
                osVersion = /OS (\d+)_(\d+)_?(\d+)?/.exec(nVer);
                osVersion = osVersion[1] + '.' + osVersion[2] + '.' + (osVersion[3] | 0);
                break;

        }
    }

    window.browserInfo = {
        browser: browser,
        browserVersion: version,
        mobile: mobile,
        os: os,
        osVersion: osVersion,
        cookies: cookieEnabled
    };
}(this));
} catch (e) {
	console.log(e);      	
  }
</script>