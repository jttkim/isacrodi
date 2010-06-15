<%@include file="components/htmlstart.html" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
  <title>Isacrodi: CRUD</title>
  <link type="text/css" rel="stylesheet" href="css/site.css"/>
</head>

<body>

<%@include file="/WEB-INF/views/components/navbar.jsp" %>


<div class="content">

<h1>CRUD</h1>

<s:property escape="false" value="entityHtml"/>

</div>

</body>
</html>
