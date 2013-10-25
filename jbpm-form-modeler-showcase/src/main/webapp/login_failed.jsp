<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.jbpm.formModeler.jBPMFormModeler/jBPM.html?message=Login failed: Invalid UserName or Password";
  response.sendRedirect(redirectURL);
%>