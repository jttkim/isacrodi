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
<s:form action="editnumericdescriptors">
<tr><td>Editing crop disorder record</td><td><code>${cropDisorderRecordId}</td></tr>
<s:hidden name="cropDisorderRecordId"/>
<tr><td>Reporting user</td><td><s:property value="%{isacrodiUser.firstname}"/> <s:property value="%{isacrodiUser.lastname}"/></tr>
<tr>
<td>Crop:</td>
<td><s:push value="crop"><%@include file="components/links/croplink.jsp" %></s:push></td>
<tr>
<th colspan="2">Edit existing numeric descriptors (blank to delete)</th>
</tr>
</tr>
<s:iterator value="%{numericDescriptorList}">
<s:textfield label="%{numericType.typeName}" name="numerictype_id_%{numericType.id}" value="%{numericValue}"/>
</s:iterator>
<tr>
<th colspan="2">Add new numeric descriptor</th>
</tr>
<s:textfield label="numeric type" name="newNumericTypeName" value=""/>
<s:textfield label="value" name="newNumericValue" value=""/>
<s:submit/>
</s:form>

<p class="todonote">should show descriptors, diagnosis, recommendation etc. -- write and use components</p>

</div>

</body>
</html>
