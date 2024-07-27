	$(document).ready(function() {
			$(".form-select").change(function() {
				var selectedReason = $(this).val();
				console.log("Reason ---------->>>" + selectedReason);
				if (selectedReason == "Other") {
					$('.otherReasonDiv').css('display', 'block');
					$(".otherReason").val("");
				} else {
					$('.otherReasonDiv').css('display', 'none');
					$(".otherReason").val(selectedReason);
				}
			});
			$('.submitform').click(function() {
				var reason = $(".otherReason").val();
				if (reason.length < 5) {
					alert("please provide a reason for withdrawal");
					return false;
				}

				if (!$('#confirmation').is(":checked")) {
					alert("please check confirmation ");
					return false;
				}
			});

		});