<%@include file="components/htmlstart.html" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

  <head>
    <title>Isacrodi Login</title>
    <link rel="stylesheet" type="text/css" href="<s:url value="/css/site.css"/>"/>
  </head>
  <body>


    <h1>Isacrodi Login</h1>
    <h2>Login for Registered Users</h2>

    <p><s:actionerror/></p>
    <s:form action="loginform">
      <s:textfield label="Username" name="username" value="%{username}"/>
      <s:password label="Password" name="password"/>
      <s:submit/>
    </s:form>

    <hr/>

    <h2>Lost Password</h2>

    <p class="todonote">not yet functional</p>

    <p>If you require a new password (e.g. because you forgot your password),
    type your email address below and a new password will be generated and
    sent to you.</p>
    <s:form action="newpasswordform">
      <s:textfield label="Email address" name="email"/>
      <s:submit/>
    </s:form>

    <hr/>

    <h2>New User Signup</h2>

    <p>Please email Anyela Camargo-Rodriguez
    <a href="mailto:A.Camargo-Rodriguez@uea.ac.uk"><code>A.Camargo-Rodriguez@uea.ac.uk</code></a>
    or Jan T Kim <a href="mailto:j.kim@uea.ac.uk"><code>j.kim@uea.ac.uk</code></a>
    if you wish to get an account for the Isacrodi system. Please state your first and last name,
    and your preferred username. A comment on how you became aware of Isacrodi and on your
    background or interest in Isacrodi will be appreciated.</p>

  </body>
</html>
