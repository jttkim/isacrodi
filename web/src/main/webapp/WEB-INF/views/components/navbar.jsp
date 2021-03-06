<div class="navbar">
<p>user: <s:property value="%{#session.isacrodiUser.firstname}"/>
<s:property value="%{#session.isacrodiUser.lastname}"/></p>
<ul>
<li><a href="<%= response.encodeURL("home") %>">home</a></li>
<li><a href="<%= response.encodeURL("cdrform_input") %>">New CDR</a></li>
<li><a href="<s:url action="logout"/>">logout</a></li>
</ul>
<s:if test="%{installationType in {'development'}}">
<p>Browse</p>
<ul>
<li><a href="crud?entityClassName=Crop">Crops</a></li>
<li><a href="crud?entityClassName=CropDisorder">Disorders</a></li>
<li><a href="crud?entityClassName=CropDisorderRecord">CDRs</a></li>
<li><a href="crud?entityClassName=NumericType">Numeric Types</a></li>
<li><a href="crud?entityClassName=CategoricalType">Categorical Types</a></li>
<li><a href="crud?entityClassName=ImageType">Image Types</a></li>
<li><a href="crud?entityClassName=Procedure">Procedures</a></li>
<li><a href="crud?entityClassName=Recommendation">Recommendations</a></li>
</ul>
</s:if>
</div>
<%
  if (true)
  {
%>
<div class="adminstuff">
<%
  }
%>
</div>


<%
  if ((request.getAttribute("errormessage") != null) || (request.getAttribute("infomessage") != null))
  {
%>
<div class="messages">

<%
    if (request.getAttribute("errormessage") != null)
    {
%>
<p><span class="errormessage">${errormessage}</span></p>
<%
    }
%>

<%
    if (request.getAttribute("infomessage") != null)
    {
%>
<p><span class="infomessage">${infomessage}</span></p>
<%
    }
%>

</div>
<%
  }
%>
