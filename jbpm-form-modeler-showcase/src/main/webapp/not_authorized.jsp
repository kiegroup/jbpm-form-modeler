<%
  request.getSession().invalidate();
  String redirectURL = request.getContextPath()  +"/org.jbpm.formModeler.jBPMShowcase/jBPM.html?message=Login failed: Not Authorized";
  response.sendRedirect(redirectURL);
%>