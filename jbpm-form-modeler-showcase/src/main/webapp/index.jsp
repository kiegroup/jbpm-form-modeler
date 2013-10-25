<%
    String queryString = request.getQueryString();
    String redirectURL = "org.jbpm.formModeler.jBPMFormModeler/jBPM.html?" + ( queryString == null ? "" : queryString );
    response.sendRedirect( redirectURL );
%>
