/**
 * 
 */
	/* 	var arrays=[]; 
		$(document).ready(function() {
			$('#someButton').click(function() {
			arrays=[];
			var checkboxes = document.querySelectorAll('input[name=checkbox]:checked');
			for (var i = 0; i < checkboxes.length; i++) {
				arrays.push(checkboxes[i].value)  
			}
			});
		}); 
	*/

	// for select all	
	$('#selectAll').click(function (e) {
	    $(this).closest('table').find('td input:checkbox').prop('checked', this.checked);
	});
	
	$('.checkBox').click(function (e) {
		var ischecked= $(this).is(':checked');
		if(!ischecked){
			$("#selectAll").prop("checked", false);
		}
	});
	
	function setCheckAll() {
  		document.querySelector('input.checkAll').checked =
     	document.querySelectorAll('.checkBox').length ==
     	document.querySelectorAll('.checkBox:checked').length;
	}