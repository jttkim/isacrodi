<s:iterator value="%{imageDescriptorList}">
<h3><s:property value="%{descriptorType.typeName}"/></h3>
<s:url var="imgurl" action="showinlineimage"><s:param name="imageDescriptorId" value="%{id}"/></s:url>
<img src="<s:property escape="false" value="%{imgurl}"/>" alt="image"/>
</s:iterator>
