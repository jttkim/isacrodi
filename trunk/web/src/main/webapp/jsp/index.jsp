<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.naming.*" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<body>
  <p><strong>hello message: <s:property value="name"/></strong></p>
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

