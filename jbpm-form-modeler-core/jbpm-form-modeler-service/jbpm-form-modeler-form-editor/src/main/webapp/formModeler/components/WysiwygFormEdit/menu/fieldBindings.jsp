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
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jbpm.formModeler.components.editor.BindingFormFormatter">
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="generateForm"/>">
        <factory:handler action="generateForm" />
        <div class="bindingProperties">
        <table>

    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputNameInput">
        <tr>
        <td>
            <b>Class name</b><br>
            <input name="className" type="text" class="skn-input"
                   value=""
                   size="20" maxlength="64">
        </td>
        </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        <tr>
            <td>
                <table cellpadding="1" cellspacing="0" border="0" width="100%">
                    <tr>
                        <td align="center" style="height:30px" nowrap>
                            <input id="<factory:encode name="generateFormSubmit"/>" type="submit"
                                   class="skn-button" value="Generate Form"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        </table>
        </div>
        </form>
        <script defer>
            setAjax("<factory:encode name="generateForm"/>");
        </script>

    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
</mvc:formatter>
