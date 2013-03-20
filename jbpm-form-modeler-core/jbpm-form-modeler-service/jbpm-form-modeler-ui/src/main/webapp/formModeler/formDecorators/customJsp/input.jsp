<%--

    Copyright (C) 2012 JBoss Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%try {%>
<mvc:formatter name="org.jbpm.formModeler.core.processing.fieldHandlers.SimpleFieldHandlerFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="formula" id="pageToInclude">
            <mvc:fragmentValue name="fieldName" id="fieldName">
                <mvc:fragmentValue name="formNamespace" id="formNamespace">
                    <mvc:fragmentValue name="formId" id="formId">
                        <mvc:fragmentValue name="htmlContainer" id="properties">

                <%
                    if(!StringUtils.isEmpty((String)pageToInclude)) {
                        Boolean b = (Boolean) request.getAttribute( formId + "." + fieldName + ".reset." + formNamespace);
                        b = b == null ? Boolean.FALSE : b;
                        request.setAttribute("reset", b);
                        request.setAttribute("properties", properties);
                %>
                <jsp:include page="<%=(String)pageToInclude%>" flush="true">
                    <jsp:param name="fieldName" value="<%=fieldName%>"/>
                </jsp:include>
                <%
                        request.removeAttribute("reset");
                        request.removeAttribute("properties");
                    }
                %>

                          </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
<%} catch (Throwable t) {
    System.out.println("Error showing Separator input " + t);
    t.printStackTrace();
}%>
