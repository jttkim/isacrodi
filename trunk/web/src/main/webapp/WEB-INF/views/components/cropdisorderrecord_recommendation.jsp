<s:if test="%{recommendation}">
<table>
<tr><th>score</th><th>procedure</th></tr>
<s:iterator value="%{procedureScoreList}">
<tr><td><s:property value="%{score}"/></td><td><s:push value="%{procedure}"><%@include file="links/procedurelink.jsp" %></s:push></td></tr>
</s:iterator>
</table>
</s:if>
<s:else>
<p>No computed recommendation available</p>
</s:else>
