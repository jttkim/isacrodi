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

<%@include file="/WEB-INF/views/components/cropdisorderrecord_properties.jsp" %>

<p><s:url var="editurl" action="editcropdisorderrecord_input"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{editurl}">edit</s:a></p>


<h2>Numeric Descriptors</h2>

<%@include file="/WEB-INF/views/components/cropdisorderrecord_numericdescriptors.jsp" %>

<p><s:url var="editndurl" action="editcdrnumericdescriptors_input"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{editndurl}">edit</s:a></p>


<h2>Image Descriptors</h2>

<%@include file="/WEB-INF/views/components/cropdisorderrecord_numericdescriptors.jsp" %>

<p><s:url var="editidurl" action="editcdrimagedescriptors_input"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{editidurl}">edit</s:a></p>


<h2>Diagnosis</h2>

<%@include file="/WEB-INF/views/components/cropdisorderrecord_diagnosis.jsp" %>

<p><s:url var="requestdiagnosisurl" action="requestdiagnosis"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{requestdiagnosisurl}">request a computed diagnosis for this record</s:a></p>

<p><s:url var="editexpertdiagnosisurl" action="editexperttdiagnosis"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{editexpertdiagnosisurl}">enter / edit the expert diagnosis for this record</s:a></p>


</div>

</body>
</html>
