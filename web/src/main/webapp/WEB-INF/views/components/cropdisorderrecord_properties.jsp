<p>Reporting user: <s:property value="%{isacrodiUser.firstname}"/> <s:property value="%{isacrodiUser.lastname}"/></p>

<p class="todonote">Should link to a dedicated crop jsp based view.</p>

<p><%@include file="links/croplink.jsp" %></p>

<p class="todonote">CDRs should perhaps have a name.</p>

<p>Description</p>

<pre>
<s:property value="%{description}"/>
</pre>
