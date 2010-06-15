<%@include file="components/htmlstart.html" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
  <title>Isacrodi: CRUD</title>
  <link type="text/css" rel="stylesheet" href="css/site.css"/>
</head>

<body>

<%@include file="/WEB-INF/views/components/navbar.jsp" %>


<div class="content">

<h1>Welcome to Isacrodi</h1>

<p>Welcome, <s:property value="%{isacrodiUser.firstname}"/> <s:property value="%{isacrodiUser.lastname}"/>

<h2>Crop Disorder Records</h2>

<p><span class="todonote">This list currently shows all CDRs in the
system, should list only the CDRs of the user who is logged in by
default.</span></p>

<ul>
<s:iterator var="cdr" value="%{cropDisorderRecordList}">
<s:url var="cdrurl" action="showcropdisorderrecord">
<s:param name="cropDisorderRecordId" value="%{id}"/>
</s:url>
<li><s:a href="%{cdrurl}">record #<s:property value="%{id}"/></s:a></li>
</s:iterator>
</ul>
</div>

</body>
</html>
