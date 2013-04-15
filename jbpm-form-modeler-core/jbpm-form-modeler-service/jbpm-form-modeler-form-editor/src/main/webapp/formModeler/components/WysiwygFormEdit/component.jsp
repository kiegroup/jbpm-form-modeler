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
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>
<%
    String editorBgColor = "#eaeaea";
%>

<mvc:formatter name="org.jbpm.formModeler.components.editor.WysiwygMenuFormatter">
    <mvc:fragment name="outputStart">
        <table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
    </mvc:fragment>
    <mvc:fragment name="outputHeader">
        <tr>
        <td class="headerComponent" width="100%">
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
    </mvc:fragment>
    <mvc:fragment name="beforeOptions">
        <td valign="top" style="padding:10px; text-align: left !important;">
    </mvc:fragment>

    <mvc:fragment name="optionsOutputStart">

        <form action="<factory:formUrl/>" style="margin:0px;" id="optionsForm">
        <factory:handler action="void"/>
        <input type="hidden" name="<factory:bean property="currentEditionOption"/>">
    </mvc:fragment>
    <mvc:fragment name="outputOption">
        <mvc:fragmentValue name="optionName" id="optionName">
            <mvc:fragmentValue name="optionImage" id="optionImage">
                <input type="image"
                       onclick="setFormInputValue(this.form,'<factory:bean property="currentEditionOption"/>','<%=optionName%>');"
                       style="cursor:hand; margin-right: 15px;" title="Bindings"
                       src="<static:image relativePath="<%=(String)optionImage%>"/>">
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputSelectedOption">
        <mvc:fragmentValue name="optionName" id="optionName">
            <mvc:fragmentValue name="optionImage" id="optionImage">
                <input type="image"
                       onclick="setFormInputValue(this.form,'<factory:bean property="currentEditionOption"/>','<%=optionName%>');"
                       style="cursor:hand; margin-right: 15px;opacity:.5;" title="Bindings"
                       src="<static:image relativePath="<%=(String)optionImage%>"/>">
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="optionsOutputEnd">
        <mvc:fragmentValue name="renderMode" id="renderMode">
            </form>
            <script defer>
                setAjax("optionsForm");
            </script>

            </td>
            <td width="85%">
                <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="switchRenderMode"/>">
                    <factory:handler action="switchRenderMode" />
                    <input type="image"
                           onclick="setFormInputValue(this.form,'renderMode','<%=((renderMode!=null && renderMode.equals(Form.RENDER_MODE_WYSIWYG_FORM)) ? Form.RENDER_MODE_WYSIWYG_DISPLAY : Form.RENDER_MODE_WYSIWYG_FORM )%>');"
                           style="cursor:hand; margin-right: 15px;" title="Bindings"
                           src="<static:image relativePath="<%=((renderMode!=null && renderMode.equals(Form.RENDER_MODE_WYSIWYG_FORM)) ? WysiwygFormEditor.EDITION_OPTION_IMG_FORM_SHOWTMODE : WysiwygFormEditor.EDITION_OPTION_IMG_FORM_INSERTMODE )%>"/>">

                </form>
                <script type="text/javascript" defer="defer">
                    setAjax("<factory:encode name="switchRenderMode"/>");
                </script>
            </td>
            </tr>
            </table>
            </tr>
        </mvc:fragmentValue>
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
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>
</mvc:formatter>
