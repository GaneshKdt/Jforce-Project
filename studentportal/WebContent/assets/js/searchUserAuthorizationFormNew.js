
	$(document).ready(function() {
	    $('#btn-add1').click(function(){
	        $('#allRoles option:selected').each( function() {
	                $('#roles').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	            $(this).remove();
	        });
	    });
	    $('#btn-remove1').click(function(){
	        $('#roles option:selected').each( function() {
	            $('#allRoles').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	            $(this).remove();
	        });
	    });
	 
	});
		
	function selectAllOptions(){
		$('#roles option').prop('selected', true);
	}