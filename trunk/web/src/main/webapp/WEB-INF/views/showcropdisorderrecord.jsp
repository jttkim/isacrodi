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

<p><s:url var="editndurl" action="editnumericdescriptors_input"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{editndurl}">edit</s:a></p>


<h2>Categorical Descriptors</h2>

<%@include file="/WEB-INF/views/components/cropdisorderrecord_categoricaldescriptors.jsp" %>

<p><s:url var="editcdurl" action="editcategoricaldescriptors_input"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{editcdurl}">edit</s:a></p>


<h2>Image Descriptors</h2>

<%@include file="/WEB-INF/views/components/cropdisorderrecord_imagedescriptors.jsp" %>

<p><s:url var="editidurl" action="editimagedescriptors_input"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{editidurl}">edit</s:a></p>


<h2>Diagnosis</h2>

<%@include file="/WEB-INF/views/components/cropdisorderrecord_diagnosis.jsp" %>

<p><s:url var="requestdiagnosisurl" action="requestdiagnosis"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{requestdiagnosisurl}">request a computed diagnosis for this record</s:a></p>

<p><s:url var="requestdiagnosiswithproviderurl" action="requestdiagnosis"><s:param name="cropDisorderRecordId" value="%{id}"/><s:param name="constructNewDiagnosisProvider" value="%{'true'}"/></s:url><s:a href="%{requestdiagnosiswithproviderurl}">request a computed diagnosis by a new diagnosis provider for this record</s:a></p>

<p><s:url var="editexpertdiagnosisurl" action="editexperttdiagnosis"><s:param name="cropDisorderRecordId" value="%{id}"/></s:url><s:a href="%{editexpertdiagnosisurl}">enter / edit the expert diagnosis for this record</s:a></p>


<h2>Recommendation</h2>

<%@include file="/WEB-INF/views/components/cropdisorderrecord_recommendation.jsp" %>

</div>

</body>
</html>
