function checkSelectedSubjects() {
		const checkboxes = document.getElementsByClassName("subjectCheck");
		document.getElementById("submit").setAttribute("disabled", "disabled");

		for(const checkbox of checkboxes) {
			if(checkbox.checked)
				document.getElementById("submit").removeAttribute("disabled");
		}
	}