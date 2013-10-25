<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.jbpm.formModeler.jBPMFormModeler/jBPM.html?message=Login failed: Not Authorized";
  response.sendRedirect(redirectURL);
%>