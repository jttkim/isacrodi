<div class="navbar">
<p>user: ${isacrodiUser.firstname} ${isacrodiUser.lastname}</p>
<ul>
<li><a href="<%= response.encodeURL("home") %>">home</a></li>
<li><a href="<%= response.encodeURL("newuserform_input") %>">User signup</a></li>
<li><a href="<%= response.encodeURL("newcdrform_input") %>">Request diagnosis</a></li>
<li><a href="<s:url action="logout"/>">logout</a></li>
</ul>
<%
  if (true)
  {
%>
<div class="adminstuff">
<p>CRUD</p>
<ul>
</ul>
<li><a href="crud?entityClassName=Crop">Crops</a></li>
<li><a href="crud?entityClassName=CropDisorder">Disorders</a></li>
<li><a href="crud?entityClassName=CropDisorderRecord">CDRs</a></li>
</div>
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
