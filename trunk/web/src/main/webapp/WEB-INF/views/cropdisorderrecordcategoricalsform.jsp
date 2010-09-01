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
<s:form action="editcategoricaldescriptors">
<tr><td>Editing crop disorder record</td><td><code>${cropDisorderRecordId}</td></tr>
<s:hidden name="cropDisorderRecordId"/>
<tr><td>Reporting user</td><td><s:property value="%{isacrodiUser.firstname}"/> <s:property value="%{isacrodiUser.lastname}"/></tr>
<tr>
<td>Crop:</td>
<td><s:push value="crop"><%@include file="components/links/croplink.jsp" %></s:push></td>
<tr>
<th colspan="2">Edit existing categorical descriptors (blank to delete)</th>
</tr>
</tr>
<s:iterator value="%{categoricalDescriptorList}">
<%-- s:textfield label="%{descriptorType.typeName}" name="categoricaltype_id_%{descriptorType.id}" value="%{categoricalValue}" tooltip="%{descriptorType.description}"/ --%>
<s:select
  name="categoricaltype_id_%{descriptorType.id}"
  label="%{descriptorType.typeName}"
  list="%{descriptorType.descriptorSet}"
  listKey="categoricalValue"
  listValue="categoricalValue"
  emptyOption="true"
  value="%{categoricalValue}"
  tooltip="%{descriptorType.description}"
/>
  <ul>
  <s:iterator value="%{descriptorType.categoricalDescriptorSet}">
  <li><s:property value="%{categoricalValue}"/></li>
  </s:iterator>
  </ul>
</s:iterator>
<tr>
<th colspan="2">Add new categorical descriptor</th>
</tr>
<s:textfield label="categorical type" name="newCategoricalTypeName" value="%{newCategoricalTypeName}"/>
<s:textfield label="value" name="newCategoricalValue" value="%{newCategoricalValue}"/>
<s:submit/>
</s:form>

<p class="todonote">should show descriptors, diagnosis, recommendation etc. -- write and use components</p>

</div>

</body>
</html>
