/*
 * JS file containing fetch API's to logout from all apps.
 * Promise.all() is used which takes in an array of fetch api requests and returns a Resolve if all are executed successfully.
 * @author: Raynal Dcunha
 */

window.onload = function() {
	const portalLogoutFetchReq = fetch("studentportal/logout");
	const examLogoutFetchReq = fetch("exam/logoutforSSO");
	const acadsLogoutFetchReq = fetch("acads/logoutforSSO");
	const ltiLogoutFetchReq = fetch("ltidemo/logoutforSSO");
	const csLogoutFetchReq = fetch("careerservices/logoutforSSO");
	
//	const portalLogoutFetchReq = fetch(reqObj('studentportal/logout'));
//	const examLogoutFetchReq = fetch(reqObj('exam/logoutforSSO'));
//	const acadsLogoutFetchReq = fetch(reqObj('acads/logoutforSSO'));
//	const ltiLogoutFetchReq = fetch(reqObj('ltidemo/logoutforSSO'));
//	const csLogoutFetchReq = fetch(reqObj('careerservices/logoutforSSO'));

	const allAppsLogoutSSO = Promise.all([portalLogoutFetchReq, examLogoutFetchReq, acadsLogoutFetchReq, ltiLogoutFetchReq, csLogoutFetchReq]);
	
	allAppsLogoutSSO
		.catch(function(err) { console.error("Resources Failed to execute completely, please refresh! Error Message: ", err.message); });
}

//Method to create a Request Object for POST API call
function reqObj(url) {
	return new Request(url, {
								method: 'POST',
								headers: new Headers({
									'Content-Type': 'text/plain; charset=UTF-8'
								})
							});
}
