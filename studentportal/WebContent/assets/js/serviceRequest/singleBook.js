if(isCertificate == true){
			totalCost = totalCost + parseFloat(0.18 * totalCost);
		}
		document.getElementById("amount").value = totalCost;
		document.getElementById("showValue").innerHTML = totalCost;
		
		$(document).ready(function(){
			function isEmptyData(data){
				if(data.trim() == "" || data == null){
					return true;
				}
				return false;
			}
			function ChangeFormAction(action){
				$('#form1').attr('action',action);
				$('#form1').submit();
			}
			$("#backToSR").click(function(){
				ChangeFormAction('selectSRForm');
			});
			
			$("#submit").click(function(){
				
				let sem = $("select[name='subject']").val();
				if(!isEmptyData(sem)){
					if (!confirm('Are you sure you want to submit this Service Request?')) return false;
					ChangeFormAction('saveSingleBook');
					return false;
				}
				alert("please select subject");
				
			});
			
			
/* 			$(document).on('click','.selectPaymentGateway',function(){
				$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
				ChangeFormAction('saveSingleBook');
			}); */
			
			/* $('#selectPaytm').click(function(){
				$("#paymentOption").attr("value","paytm");
				ChangeFormAction('saveSingleBook');
			});
			
			$('#selectHdfc').click(function(){
				$("#paymentOption").attr("value","hdfc");
				ChangeFormAction('saveSingleBook');
			});
			
			$('#selectBilldesk').click(function(){
				$("#paymentOption").attr("value","billdesk");
				ChangeFormAction('saveSingleBook');
			});
			
			$('#selectPayu').click(function(){
				$("#paymentOption").attr("value","payu");
				ChangeFormAction('saveSingleBook');
			}); */
		});