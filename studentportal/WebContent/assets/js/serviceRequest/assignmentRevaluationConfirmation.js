	function ChangeFormAction(action){
			$('#form1').attr('action',action);
			$('#form1').submit();
		}
		$(document).ready(function(){
			$('#saveAssignmentRevaluationAndPay').click(function(){
				if (!confirm('Are you sure you want to submit this Service Request?')) return false;
				ChangeFormAction('saveAssignmentRevaluation');
			});
			
			/* $(document).on('click','.selectPaymentGateway',function(){
				$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
				ChangeFormAction('saveAssignmentRevaluation');
			}); */
		});