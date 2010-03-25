<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
  <head>
    <title>Isacrodi Login</title>
    <link rel="stylesheet" type="text/css" href="<s:url value="/css/site.css"/>"/>
  </head>
  <body>
    <%@include file="/WEB-INF/views/components/navbar.jsp" %>

    <div class="content">

    <h2>Login</h2>
    <h4>For already registered users</h4>

    <p><s:actionerror/></p>
    <s:form action="loginform">
      <s:textfield label="Username" name="username" value="%{username}"/>
      <s:password label="Password" name="password"/>
      <s:submit/>
    </s:form>

    <h4>Click on user signup to register</h4>
    <hr/>

    <p>If you require a password (e.g. because you forgot your password),
    type your email address below and a new password will be generated and
    sent to you.</p>
    <s:form action="newpasswordform">
      <s:textfield label="Email address" name="email"/>
      <s:submit/>
    </s:form>
    </div>
  </body>
</html>
