<table>
<tr><th>name</th><th>value</th></tr>
<s:iterator value="%{numericDescriptorList}">
<tr><td><s:property value="%{numericType.typeName}"/></td><td><s:property value="%{numericValue}"/></td></tr>
</s:iterator>
</table>
