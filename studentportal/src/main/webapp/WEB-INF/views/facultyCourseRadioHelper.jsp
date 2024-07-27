
<%
String nameAttribute = (String)request.getParameter("nameAttribute");
%>

<div class="form-group">

	<label for="one">1</label> <input type="radio" data-value="1"
		name="<%=nameAttribute%>" class="ratingRadio" /> <label for="two">2</label>
	<input type="radio" data-value="2" name="<%=nameAttribute%>"
		class="ratingRadio" /> <label for="three">3</label> <input
		type="radio" data-value="3" name="<%=nameAttribute%>"
		class="ratingRadio" /> <label for="four">4</label> <input
		type="radio" data-value="4" name="<%=nameAttribute%>"
		class="ratingRadio" /> <label for="five">5</label> <input
		type="radio" data-value="5" name="<%=nameAttribute%>"
		class="ratingRadio" /> <label for="six">6</label> <input type="radio"
		data-value="6" name="<%=nameAttribute%>" class="ratingRadio" /> <label
		for="seven">7</label> <input type="radio" data-value="7"
		name="<%=nameAttribute%>" class="ratingRadio" />

</div>



