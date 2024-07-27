	function notifyLead() {
	  alert("Please enrole for the complete course...");
	}
 
	 var sapid=$("#studentInSideBarsapid").val(); 
	let data = {
			'sapId':sapid
		};
	$(".re_reg_li").css("display","none"); 

	//Script added for toggle effect on arrow click 
	const toggleNav = document.getElementById('toggle-nav');
	const dashboards = document.querySelectorAll('.toggle-name');

	toggleNav.addEventListener('click', () => {
		  dashboards.forEach((dashboard) => {
			
		    if (dashboard.style.display === 'none') {
		       dashboard.style.display = 'inline-block';
		    } else {
		        dashboard.style.display = 'none';	  
		    }
		  });
	})
		
