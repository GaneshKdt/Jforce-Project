<%
String breadcrumItems = request.getParameter("breadcrumItems");
String[] breadcrumList = new String[]{};
if(breadcrumItems != null){
	breadcrumList = breadcrumItems.split(";");
}


%>

<div class="sz-breadcrumb-wrapper">
    <div class="container-fluid">
        <ul class="sz-breadcrumbs">
        	<%for(String item :breadcrumList ){ %>
        		<li><a href=""><%=item %></a></li>
        	<%} %>
        </ul>
      <!--   <ul class="sz-social-icons">
            <li><a href="https://www.facebook.com/NMIMSSCE" class="fa-brands fa-facebook-f" target="_blank"></a></li>
            <li><a href="https://twitter.com/NMIMS_SCE" class="fa-brands fa-twitter" target="_blank"></a></li>
            <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li>

        </ul> -->
    </div>
</div>