<html>
<style>
	div {
		margin-left:10px;
		padding:10px 20px;
		color:white;
		border-radius:10px;
		min-width:100px;
		float:left;
		text-align: center;
	}
</style>
<body>
	<div style="background-color:#FF5733 !important;">
	  <h4>
	      Portal Started
	  </h4>
	  <h2 id="portal_started">
	    ${ portal_started }
	  </h2>
	</div>
	
	<div style="background-color:#0CBF83 !important;">
	  <h4>
	      Mettl Completed
	  </h4>
	  <h2>
	    ${ mettl_completed }
	  </h2>
	</div>
	
	
	<div style="background-color:#FFBD00 !important;">
	  <h4>
	      No Action
	  </h4>
	  <h2>
	    ${ no_action }
	  </h2>
	</div>
	
	<div style="background-color:rgb(0, 132, 205) !important;">
	  <h4>
	      Mettl started
	  </h4>
	  <h2>
	    ${ mettl_started }
	  </h2>
	</div>
	
	<script>
		setTimeout(function(){
			window.location.reload()
		},30000);
	</script>
</body>
</html>