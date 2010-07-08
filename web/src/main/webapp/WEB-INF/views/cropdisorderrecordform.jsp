<%@include file="components/htmlstart.html" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
  <title>Isacrodi: CDR <s:property value="%{id}"/></title>
  <link type="text/css" rel="stylesheet" href="css/site.css"/>
</head>

<body>

<%@include file="/WEB-INF/views/components/navbar.jsp" %>


<div class="content">

<p><s:actionerror/></p>
<s:form action="editcropdisorderrecord">
<s:if test="cropDisorderRecordId">
<tr><td>Editing crop disorder record</td><td><code>${cropDisorderRecordId}</td></tr>
<s:hidden name="cropDisorderRecordId"/>
<tr><td>Reporting user</td><td><s:property value="%{isacrodiUser.firstname}"/> <s:property value="%{isacrodiUser.lastname}"/></tr>
</s:if>
<s:textfield label="Crop (scientific name)" name="cropScientificName" value="%{crop.scientificName}"/>
<s:textfield label="Diagnosis by expert (scientific name of disorder)" name="expertDiagnosedCropDisorderScientificName" value="%{expertDiagnosedCropDisorder.scientificName}"/>
<s:textarea label="Description" name="description" value="%{description}" rows="8" cols="60"/>
<s:submit/>
</s:form>

<p class="todonote">should show descriptors, diagnosis, recommendation etc. -- write and use components</p>

</div>

</body>
</html>
