/*
 * JS file which uses the vanilla JS approach to handle html includes.
 * This file is not in use.
 * @author: Raynal Dcunha
 */

//currentScript is used to get the data-value attribute of the script currently being processed
const breadcrumText = document.currentScript.getAttribute('data-breadcrum-args');
let breadcrumListItem = '<li class="breadcrumb-item text-light">' + breadcrumText + '</li>';

fetch("./common/navbar.html")
  .then(function(response) {
    return response.text();
  })
  .then(function(data) {
    document.querySelector("header").innerHTML = data.replace("#", breadcrumListItem);
  });


fetch("./common/footer.html")
  .then(function(response) {
    return response.text();
  })
  .then(function(data) {
    document.querySelector("footer").innerHTML = data;
  });
/* FILE IS NOT IN USE ~ Using thymeleaf to include html files */