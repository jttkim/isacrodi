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

<p>Expert diagnosed disorder: <s:property value="%{expertDiagnosedCropDisorder.scientificName}"/></p>

<p class="todonote">link below not yet functional</p>

<s:url var="diagnosisurl" action="requestdiagnosis"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url>
<p><s:a href="%{diagnosisurl}">request diagnosis for this record</s:a></p>

<p class="todonote">This should be a link to the crop.</p>

<p>Affected crop: <s:property value="%{crop.name}"/> (<span class="scientificname"><s:property value="%{crop.scientificName}"/></span>).</p>

<p class="todonote">CDRs should perhaps have a name.</p>

<p>Description</p>

<pre>
<s:property value="%{description}"/>
</pre>


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

<h2>Diagnosis</h2>

<s:if test="%{diagnosis}">
<table>
<tr><th>score</th><th>disorder</th></tr>
<s:iterator value="%{diagnosis.disorderScoreSet}">
<s:url var="disorderurl" action="showdisorder"><s:param name="cropDisorderId" value="%{cropDisorder.id}"/></s:url>
<tr><td><s:property value="%{score}"/></td><td><s:a href="%{disorderurl}"><span class="scientificname"><s:property value="%{cropDisorder.scientificName}"/></span></s:a></td></tr>
</s:iterator>
</table>
</s:if>
<s:else>
<p>No diagnosis available</p>
</s:else>

</div>

</body>
</html>
