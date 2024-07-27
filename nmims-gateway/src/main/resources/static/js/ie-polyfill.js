/*
 * JS file which checks if the browser supports the Window object features introduced in ES6 and after.
 * If any of the listed features is not supported by the browser, polyfills for those features are created and loaded (Browser used for reference IE 11).
 * Checked Features are [Promise, Fetch, URLSearchParams, Request, Headers]
 * 
 * All polyfills are loaded even if any single one is missing, to avoid the if else combinations to check the working of each feature.
 * Polyfills are loaded from the Amazon d3 server.
 * A sleep method is created to delay the loadScript method execution, to buy time for the polyfill scripts to load.
 * @author: Raynal Dcunha
 */

if (! browserSupportsAllWindowFeatures()) {
	loadScript();
}

//function which determines if the browser supports these particular features
function browserSupportsAllWindowFeatures() {
	return window.Promise && window.fetch && window.URLSearchParams
			&& window.Request && window.Headers;
}

function loadScript() {
	try {
		// Creating & Loading Promise polyfill script tag
		var promiseScript = document.createElement("script");
		promiseScript.type = "text/javascript";
		promiseScript.src = "https://d3udzp2n88cf0o.cloudfront.net/js/polyfill/promise-polyfill.min.js";
		document.head.appendChild(promiseScript);

		// Creating & Loading Fetch polyfill script tag
		var fetchScript = document.createElement("script");
		fetchScript.type = "text/javascript";
		fetchScript.src = "https://d3udzp2n88cf0o.cloudfront.net/js/polyfill/fetch.umd.min.js";
		document.head.appendChild(fetchScript);

		// Creating & Loading URLSearchParams polyfill script tag
		var urlSearchParamsScript = document.createElement("script");
		urlSearchParamsScript.type = "text/javascript";
		urlSearchParamsScript.src = "https://d3udzp2n88cf0o.cloudfront.net/js/polyfill/url-search-params-polyfill.min.js";
		document.head.appendChild(urlSearchParamsScript);
		
		sleep(600);	//sleep will make function wait 600 milliseconds
	}
	catch(error) {
		alert("Failed to load required resources, please refresh the page for optimal performance!")
	}
}

//function which runs for the specified amount of milliseconds
function sleep(milliseconds) {
	var start = new Date().getTime();
	var end = start;
	start = start + milliseconds;
	
	while(end < start) {
		end = new Date().getTime();
	}
}
