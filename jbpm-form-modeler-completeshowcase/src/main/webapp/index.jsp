<%
    String queryString = request.getQueryString();
    String redirectURL = request.getContextPath()  +"/org.jbpm.formModeler.jBPMShowcase/jBPM.html?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>