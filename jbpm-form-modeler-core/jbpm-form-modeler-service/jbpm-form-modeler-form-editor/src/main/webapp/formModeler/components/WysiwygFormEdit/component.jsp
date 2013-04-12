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
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

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
        <td class="headerComponent" width="100%" >
            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    </mvc:fragment>
                    <mvc:fragment name="beforeOptions">
                    <td valign="top" style="padding:10px; text-align: left !important;">
                        </mvc:fragment>

                        <mvc:fragment name="optionsOutputStart">

                        <form action="<factory:formUrl/>" style="margin:0px;" id="optionsForm">
                            <factory:handler action="void"/>
                            <input type="hidden" name="<factory:bean property="currentEditionOption"/>" >
                            <input type="image" onclick="setFormInputValue(this.form,'<factory:bean property="currentEditionOption"/>','<%=WysiwygFormEditor.EDITION_OPTION_FIELDTYPES%>');"
                                   style="cursor:hand; margin-right: 15px;" title="Add Fields By Type" src="<static:image relativePath="general/AddFieldsByType.png"/>">
                            <input type="image" onclick="setFormInputValue(this.form,'<factory:bean property="currentEditionOption"/>','<%=WysiwygFormEditor.EDITION_OPTION_FORM_PROPERTIES%>');"
                                   style="cursor:hand; margin-right: 15px;" title="Form Properties" src="<static:image relativePath="general/FormProperties.png"/>">
                            <input type="image" onclick="setFormInputValue(this.form,'<factory:bean property="currentEditionOption"/>','<%=WysiwygFormEditor.EDITION_OPTION_FORM_EDITION_PROPERTIES%>');"
                                   style="cursor:hand; margin-right: 15px;" title="Bindings" src="<static:image relativePath="general/Bindings.png"/>">
                            <input type="image" style="cursor:hand; margin-right: 15px;" title="Insert Data Mode" src="<static:image relativePath="general/InsertDataMode.png"/>">
                            <input type="image" style="cursor:hand; margin-right: 15px;" title="Show Data Mode" src="<static:image relativePath="general/ShowDataMode.png"/>">

                            </mvc:fragment>
                            <mvc:fragment name="outputOption">
                                <mvc:fragmentValue name="optionName" id="optionName">
                                </mvc:fragmentValue>
                            </mvc:fragment>
                            <mvc:fragment name="outputSelectedOption">
                                <mvc:fragmentValue name="optionName" id="optionName">
                                </mvc:fragmentValue>
                            </mvc:fragment>
                            <mvc:fragment name="optionsOutputEnd">

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
                    <td style="vertical-align: top;">
                        <jsp:include page="editFieldProperties.jsp"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>