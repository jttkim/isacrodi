<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.naming.*" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<body>
  <fieldset>
  <legend>test form</legend>
  <s:form action="formtest">
    <s:textfield label="name" name="name" tooltip="valid names are 'hello' in English and Spanish"/>
    <s:submit/>
  </s:form>
  </fieldset>
  <p><strong>name: <s:property value="name"/></strong></p>
  <p><strong>EJB hello message: <s:property value="ejbHello"/></strong></p>
  <p>Welcome Isacrodi user
  <s:property value="%{isacrodiUser.firstname}"/>
  <s:property value="%{isacrodiUser.lastname}"/>,
  email: <s:property value="%{isacrodiUser.email}"/></p>
  <p><s:property value="isacrodiUser"/></p>
  <p>ognl test <s:property value="%{'this is a literal test'}"/></p>
  <h2>root context</h2>
  <ul>
  <%
  InitialContext context = new InitialContext();
  NamingEnumeration<NameClassPair> e = context.list("/");
  while (e.hasMore())
  {
    NameClassPair p = e.next();
    out.println(String.format("<li>%s</li>", p.getName()));
  }
  %>
  </ul>
  <h2>isacrodi context</h2>
  <ul>
  <%
  e = context.list("isacrodi");
  while (e.hasMore())
  {
    NameClassPair p = e.next();
    out.println(String.format("<li>%s</li>", p.getName()));
  }
  %>
  </ul>
  <h2>isacrodi/HelloBean context</h2>
  <ul>
  <%
  e = context.list("isacrodi/HelloBean");
  while (e.hasMore())
  {
    NameClassPair p = e.next();
    out.println(String.format("<li>%s</li>", p.getName()));
  }
  %>
  </ul>
</body>
</html>
