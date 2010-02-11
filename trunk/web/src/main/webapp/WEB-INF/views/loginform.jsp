<%@include file="components/htmlstart.html" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

  <head>
    <title>Isacrodi Login</title>
    <link rel="stylesheet" type="text/css" href="<s:url value="/css/site.css"/>"/>
  </head>
  <body>
    <!-- %@include file="/WEB-INF/views/components/navbar.jsp" % -->

    <div class="content">

    <h1>Login</h1>

    <p><s:actionerror/></p>
    <s:form action="loginform">
      <s:textfield label="Email address" name="email" value="%{email}"/>
      <s:password label="Password" name="password"/>
      <s:submit/>
    </s:form>

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
