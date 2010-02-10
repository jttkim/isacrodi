<%@ge contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<html>
<body>
  <html:form action="/hello" method="post">
  Enter Name: <html:text property="name"/>
  <br/>
  <input type="submit" name="submit" value="Go"/>
  </html:form>
</body>
</html>

