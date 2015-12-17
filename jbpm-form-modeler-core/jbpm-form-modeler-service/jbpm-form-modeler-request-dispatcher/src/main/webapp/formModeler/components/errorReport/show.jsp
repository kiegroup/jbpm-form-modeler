<%--

    Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.

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
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jbpm.formModeler.service.error.messages" locale="<%= LocaleManager.currentLocale() %>"/>
<mvc:formatter name="ErrorReportFormatter">
    <mvc:fragment name="errorMessage">
    <mvc:fragmentValue name="technicalDetails" id="technicalDetails">
    <mvc:fragmentValue name="closeEnabled" id="closeEnabled">
        <table width="100%" class="skn-error" cellpadding="1" cellspacing="2">
            <tr>
                <td width="48"><img src="<mvc:fragmentValue name="errorIcon"/>" width="32" height="32"/></td>
                <td class="skn-error" align="left" valign="center"><mvc:fragmentValue name="errorMessage"/></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td align="left">
                    <table cellpadding="1" cellspacing="2">
                        <tr>
                            <% if (technicalDetails != null) { %>
                            <td align="left" id="<factory:encode name="viewTechnicalDetails" />">
                                <input type="button" class="skn-button" value="<i18n:message key="viewTechnicalDetails">!!!View technical details</i18n:message>"
                                       onclick="document.getElementById('<factory:encode name="technicalDetails"/>').style.display='block';
                                        document.getElementById('<factory:encode name="viewTechnicalDetails"/>').style.display='none';
                                        document.getElementById('<factory:encode name="hideTechnicalDetails"/>').style.display='block';">&nbsp;
                            </td>
                            <td align="left" id="<factory:encode name="hideTechnicalDetails" />" style="display:none;">
                                <input type="button" class="skn-button" value="<i18n:message key="hideTechnicalDetails">!!!Hide technical details</i18n:message>"
                                       onclick="document.getElementById('<factory:encode name="technicalDetails"/>').style.display='none';
                                        document.getElementById('<factory:encode name="viewTechnicalDetails"/>').style.display='block';
                                        document.getElementById('<factory:encode name="hideTechnicalDetails"/>').style.display='none';">&nbsp;
                            </td>
                            <% } %>
                            <% if (((Boolean) closeEnabled).booleanValue()) { %>
                            <td align="left">
                                <form action="<factory:formUrl/>" method="POST">
                                <factory:handler action="continue"/>
                                    <input type="submit" class="skn-button" value="<i18n:message key="continue">!!!Continuar</i18n:message>">&nbsp;
                                </form>
                            </td>
                            <% } %>
                        </tr>
                    </table>
                </td>
            </tr>
            <% if (technicalDetails != null) { %>
            <tr>
                <td>&nbsp;</td>
                <td align="left">
                    <textarea id="<factory:encode name="technicalDetails"/>" style="font-size:smaller;display:none" rows="15" cols="120"><%= technicalDetails %></textarea>
                </td>
            </tr>
            <% } %>
        </table>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
