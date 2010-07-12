<s:if test="%{expertDiagnosedCropDisorder}">
<p>Disorder diagnosed by expert: <s:property value="%{expertDiagnosedCropDisorder.scientificName}"/></p>
</s:if>
<s:else>
<p>No diagnosis by expert</p>
</s:else>

<s:if test="%{diagnosis}">
<table>
<tr><th>score</th><th>disorder</th></tr>
<s:iterator value="%{diagnosis.disorderScoreSet}">
<s:url var="disorderurl" action="showdisorder"><s:param name="cropDisorderId" value="%{cropDisorder.id}"/></s:url>
<tr><td><s:property value="%{score}"/></td><td><s:a href="%{disorderurl}"><span class="scientificname"><s:property value="%{cropDisorder.scientificName}"/></span></s:a></td></tr>
</s:iterator>
</table>
</s:if>
<s:else>
<p>No computed diagnosis available</p>
</s:else>
