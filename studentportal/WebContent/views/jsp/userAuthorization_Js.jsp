 <script type="text/javascript">
	$(document).ready(function() {
		 
	    $('#btn-add1').click(function(){
	        $('#allLC option:selected').each( function() {
	                $('#authorizedLC').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	            $(this).remove();
	        });
	    });
	    $('#btn-remove1').click(function(){
	        $('#authorizedLC option:selected').each( function() {
	            $('#allLC').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	            $(this).remove();
	        });
	    });
	    
	    
	    $('#btn-add2').click(function(){
	        $('#allCenter option:selected').each( function() {
	                $('#authorizedCenters').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	            $(this).remove();
	        });
	    });
	    $('#btn-remove2').click(function(){
	        $('#authorizedCenters option:selected').each( function() {
	            $('#allCenter').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	            $(this).remove();
	        });
	    });
	    
	    $('#btn-add3').click(function(){
	        $('#allRoles option:selected').each( function() {
	                $('#roles').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	            $(this).remove();
	        });
	    });
	    $('#btn-remove3').click(function(){
	        $('#roles option:selected').each( function() {
	            $('#allRoles').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	            $(this).remove();
	        });
	    });
	 
	});
	
	function selectAllOptions(){
		$('#roles option').prop('selected', true);
		$('#authorizedCenters option').prop('selected', true);
		$('#authorizedLC option').prop('selected', true);
	}
	
	</script>