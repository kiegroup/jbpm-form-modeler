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
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    String editorBgColor = "#eaeaea";
%>
<table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
    <mvc:formatter name="org.jbpm.formModeler.components.editor.WysiwygMenuFormatter">
    <mvc:fragment name="outputStart">
    </mvc:fragment>
    <mvc:fragment name="outputHeader">
    <tr>
        <td class="skn-table_header" width="100%" >
            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    </mvc:fragment>
                    <mvc:fragment name="beforeOptions">
                    <td valign="top" style="padding:10px; text-align: left !important;">
                        </mvc:fragment>

                        <mvc:fragment name="optionsOutputStart">
                        <form action="<factory:formUrl/>" style="margin:0px;" id="optionsForm">
                            <factory:handler action="void"/>
                            <select class="skn-input" name="<factory:bean property="currentEditionOption"/>" onchange="submitAjaxForm(this.form)">
                                </mvc:fragment>
                                <mvc:fragment name="outputOption">
                                    <mvc:fragmentValue name="optionName" id="optionName">
                                        <option value="<%=optionName%>"><i18n:message key="<%=(String)optionName%>">!!!<%=optionName%></i18n:message></option>
                                    </mvc:fragmentValue>
                                </mvc:fragment>
                                <mvc:fragment name="outputSelectedOption">
                                    <mvc:fragmentValue name="optionName" id="optionName">
                                        <option class="skn-important" selected value="<%=optionName%>"><i18n:message key="<%=(String)optionName%>">!!!<%=optionName%></i18n:message></option>
                                    </mvc:fragmentValue>
                                </mvc:fragment>
                                <mvc:fragment name="optionsOutputEnd">
                            </select>
                        </form>
                        <script defer>
                            setAjax("optionsForm");
                        </script>
                    </td>
                </tr>
            </table>
    </tr>
    </mvc:fragment>

    <mvc:fragment name="outputEditionPage">
    <mvc:fragmentValue name="editionPage" id="editionPage">
    <tr>
        <td>
            <table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
                <tr>
                    <td style="vertical-align: top;" width="250px">
                        <jsp:include page="<%=(String)editionPage%>" flush="true"/>
                    </td>
                    </mvc:fragmentValue>
                    </mvc:fragment>
                    <mvc:fragment name="outputEnd">
                    </mvc:fragment>
                    </mvc:formatter>
                    <td style="vertical-align: top;">
                        <jsp:include page="formPreview.jsp"/>
                    </td>
                    <td>
                        <jsp:include page="editFieldProperties.jsp"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>