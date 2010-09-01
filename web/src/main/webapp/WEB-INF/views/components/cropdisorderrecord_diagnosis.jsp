<s:if test="%{expertDiagnosedCropDisorder}">
<p>Disorder diagnosed by expert: <s:push value="%{expertDiagnosedCropDisorder}"><%@include file="links/cropdisorderlink.jsp" %></s:push></p>
</s:if>
<s:else>
<p>No diagnosis by expert</p>
</s:else>

<s:if test="%{diagnosis}">
<table>
<tr><th>score</th><th>disorder</th></tr>
<s:iterator value="%{disorderScoreList}">
<tr><td><s:property value="%{score}"/></td><td><s:push value="%{cropDisorder}"><%@include file="links/cropdisorderlink.jsp" %></s:push></td></tr>
</s:iterator>
</table>
</s:if>
<s:else>
<p>No computed diagnosis available</p>
</s:else>
