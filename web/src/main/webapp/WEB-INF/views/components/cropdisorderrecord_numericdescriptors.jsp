<table>
<tr><th>name</th><th>value</th></tr>
<s:iterator value="%{numericDescriptorList}">
<tr><td><s:property value="%{descriptorType.typeName}"/></td><td><s:property value="%{numericValue}"/> <s:property value="%{descriptorType.unit}"/></td></tr>
</s:iterator>
</table>
