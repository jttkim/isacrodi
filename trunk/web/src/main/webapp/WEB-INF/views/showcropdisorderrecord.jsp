<%@include file="components/htmlstart.html" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
  <title>Isacrodi: CDR <s:property value="%{id}"/></title>
  <link type="text/css" rel="stylesheet" href="css/site.css"/>
</head>

<body>

<%@include file="/WEB-INF/views/components/navbar.jsp" %>


<div class="content">

<h1>Crop Disorder Record <s:property value="%{id}"/></h1>

<p>Reporting user: <s:property value="%{isacrodiUser.firstname}"/> <s:property value="%{isacrodiUser.lastname}"/></p>

<p class="todonote">This should be a link to the crop.</p>

<p class="todonote">link below not yet functional</p>

<s:url var="diagnosisurl" action="requestdiagnosis"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url>
<p><s:a href="%{diagnosisurl}">request diagnosis for this record</s:a></p>

<p>Affected crop: <s:property value="%{crop.name}"/>.</p>

<p class="todonote">CDRs should have some description here, and perhaps a name.</p>


<h2>Numeric Descriptors</h2>

<table>
<tr><th>name</th><th>value</th></tr>
<s:iterator value="%{numericDescriptorList}">
<tr><td><s:property value="%{numericType.typeName}"/></td><td><s:property value="%{numericValue}"/></td></tr>
</s:iterator>
</table>


<h2>Image Descriptors</h2>

<s:iterator value="%{imageDescriptorList}">
<h3><s:property value="%{imageType.typeName}"/></h3>
<s:url var="imgurl" action="showinlineimage"><s:param name="imageDescriptorId" value="%{id}"/></s:url>
<img src="<s:property escape="false" value="%{imgurl}"/>" alt="image"/>
</s:iterator>

</div>

</body>
</html>
