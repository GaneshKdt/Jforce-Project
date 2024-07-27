<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%> <%@page
import="com.nmims.beans.PageAcads"%>

<html class="no-js">
  <!--<![endif]-->

  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib
  prefix="form" uri="http://www.springframework.org/tags/form" %>
  <link
    rel="stylesheet"
    type="text/css"
    href="https://cdn.datatables.net/v/dt/dt-1.10.24/datatables.min.css"
  />
  <jsp:include page="jscss.jsp">
    <jsp:param value="View Sessions" name="title" />
  </jsp:include>

  <body class="inside">
    <%@ include file="publicHeader.jsp"%>

    <section class="content-container login">
      <div class="container-fluid customTheme">
        <div class="row"><legend>Session Scheduled List</legend></div>
        <%@ include file="messages.jsp"%>
        <div class="clearfix">
          <%if(request.getAttribute("error") == null) { %>
          <div class="container-fluid">
            <div class="row">
              <div class="col-md-6">
              <div style="background-color:white;border-radius:5px;padding:5px;">
                <table class="table table-border">
                  <tbody>
                    <tr>
                      <td>Subject Name:</td>
                      <td>
                        <c:out value="${ subject }"></c:out>
                      </td>
                    </tr>
                    <tr>
                      <td>Track:</td>
                      <td id="js_track_dropdown1">
                      </td>
                    </tr>
                    <tr>
                      <td>Year:</td>
                      <td><c:out value="${ year }"></c:out></td>
                    </tr>
                    <tr>
                      <td>Month:</td>
                      <td><c:out value="${ month }"></c:out></td>
                    </tr>
                  </tbody>
                </table>
                </div>
              </div>
            </div>
            <br/>
            <div class="table-responsive">
              <table
                id="js_datatable"
                class="table table-bordered"
                style="background-color: white !important"
              >
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Day</th>
                    <th>Subject</th>
                    <th>Session Name</th>
                    <th>Track</th>
                  </tr>
                  <tr>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Day</th>
                    <th id="js_subject_name_dropdown2">Subject</th>
                    <th>Session Name</th>
                    <th id="js_track_dropdown2">Track</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach
                    var="sessionDayTimeBean"
                    items="${sessionDayTimeBeanList}"
                  >
                    <tr>
                      <td>
                        <c:out value="${ sessionDayTimeBean.date }"></c:out>
                      </td>
                      <td>
                        <c:out
                          value="${ sessionDayTimeBean.startTime }"
                        ></c:out>
                      </td>
                      <td>
                        <c:out value="${ sessionDayTimeBean.endTime }"></c:out>
                      </td>
                      <td>
                        <c:out value="${ sessionDayTimeBean.day }"></c:out>
                      </td>
                      <td>
                        <c:out value="${ sessionDayTimeBean.subject }"></c:out>
                      </td>
                      <td>
                        <c:out
                          value="${ sessionDayTimeBean.sessionName }"
                        ></c:out>
                      </td>
                      <td>
                        <c:out value="${ sessionDayTimeBean.track }"></c:out>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </div>
          <br /><br />
          <%} %>
        </div>
      </div>
    </section>
    <jsp:include page="footer.jsp" />
    <script
      type="text/javascript"
      src="https://cdn.datatables.net/v/dt/dt-1.10.24/datatables.min.js"
    ></script>
    <script>
      let flag_tmp = true;
      let flag_tmp2 = true;
      $(document).ready(function () {
        $("#js_datatable").DataTable({
        	"ordering": false,
          initComplete: function () {
            this.api()
              .columns()
              .every(function () {
                var column = this;
                var select = $('<select><option value="">All</option></select>')
                  .appendTo($(column.header()).empty())
                  .on("change", function () {
                    var val = $.fn.dataTable.util.escapeRegex($(this).val());

                    column
                      .search(val ? "^" + val + "$" : "", true, false)
                      .draw();
                  });

                column
                  .data()
                  .unique()
                  .sort()
                  .each(function (d, j) {
                    if (column.search() === "^" + d + "$") {
                      select.append(
                        '<option value="' +
                          d +
                          '" selected="selected">' +
                          d +
                          "</option>"
                      );
                    } else {
                      select.append(
                        '<option value="' + d + '">' + d + "</option>"
                      );
                    }
                  });
                /*$("#js_subject_name_dropdown1").html(
                  $("#js_subject_name_dropdown2").html()
                );*/
                $("#js_track_dropdown1").html(
                  $("#js_track_dropdown2").html()
                );
              });
          },
        });
        $("#js_datatable_filter").hide();
        $("#js_datatable_wrapper").css({
          "background-color": "white",
          "padding": "5px 10px",
          "border-radius" : "5px"
        });
        $(document).on(
          "change",
          "#js_subject_name_dropdown1 select",
          function () {
            flag_tmp = false;
            $("#js_subject_name_dropdown2 select").val($(this).val()).change();
          }
        );

        $(document).on(
          "change",
          "#js_track_dropdown1 select",
          function () {
            flag_tmp2 = false;
            $("#js_track_dropdown2 select").val($(this).val()).change();
          }
        );

        $(document).on('change','#js_subject_name_dropdown2 select',function(){
          if (flag_tmp) {
                      $("#js_subject_name_dropdown1 select")
                        .val($(this).val())
                        .change();
                    }
                    flag_tmp = true;
        });

        $(document).on('change','#js_track_dropdown2 select',function(){
          if (flag_tmp2) {
                      $("#js_track_dropdown1 select")
                        .val($(this).val())
                        .change();
                    }
                    flag_tmp2 = true;
        });
      });
    </script>
  </body>
</html>
