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
<%@ page import="org.jbpm.formModeler.service.bb.commons.config.LocaleManager" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>

<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jbpm.formModeler.components.editor.BindingFormFormatter">
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="formBindings"/>">
        <factory:handler action="formBindings" />
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputNameInput">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStartBindings">
        <factory:handler action="generateForm" />
        <table cellpadding="1" cellspacing="0" border="0" width="100%">
        <tr>
            <td><i18n:message key="binding_id">!!!Binding Id</i18n:message></td>
            <td><i18n:message key="binding_type">!!!Binding Type</i18n:message></td>
            <td><i18n:message key="binding_str">!!!!!!Binding String</i18n:message></td>
            <td>&nbsp;</td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputBindings">
        <mvc:fragmentValue name="id" id="id">
            <mvc:fragmentValue name="type" id="type">
                <mvc:fragmentValue name="value" id="value">
                    <tr>
                        <td><%=id%></td>
                        <td><%=type%></td>
                        <td><%=value%></td>
                        <td><a title="<i18n:message key="delete">!!!Borrar</i18n:message>"
                               href="<factory:url  action="formBindings"><factory:param name="bindingId" value="<%=id%>"/><factory:param name="<%=WysiwygFormEditor.ACTION_TO_DO%>" value="<%=WysiwygFormEditor.ACTION_ADD_BINDING_FIELDS%>"/></factory:url>"
                               onclick="return confirm('<i18n:message key="binding_addFields.confirm">Sure!!</i18n:message>');" >
                            <i18n:message key="bindings_addFields">!!!addBindingFields</i18n:message>
                        </a></td>
                    </tr>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputEndBindings">
        </table>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </form>
        <script type="text/javascript" defer="defer">
            setAjax("<factory:encode name="formBindings"/>");
        </script>


    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
</mvc:formatter>