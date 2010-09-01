<table>
<tr><th>name</th><th>value</th></tr>
<s:iterator value="%{categoricalDescriptorList}">
<tr><td><s:property value="%{descriptorType.typeName}"/></td>
<td>
<ul>
<s:iterator value="%{categoricalTypeValueSet}">
<li><s:property value="%{valueType}"/></li>
</s:iterator>
</ul>
</td></tr>
</s:iterator>
</table>
