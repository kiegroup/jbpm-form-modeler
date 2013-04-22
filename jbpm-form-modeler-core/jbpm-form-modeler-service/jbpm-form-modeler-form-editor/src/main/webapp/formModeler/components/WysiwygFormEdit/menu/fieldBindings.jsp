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
        <input type="hidden" name="<%=WysiwygFormEditor.ACTION_TO_DO%>" id="<factory:encode name="actionToDo"/>" value="<%=WysiwygFormEditor.ACTION_SAVE_FIELD_PROPERTIES%>"/>
        <input type="hidden" name="idToRemove" id="<factory:encode name="bindingId"/>" value=""/>

        <div class="bindingProperties">
        <table >

    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputNameInput">
        <tr>
            <td>
                <b>Id</b><br>
                <input name="bindingId" type="text" class="skn-input"
                       value=""
                       size="20" maxlength="64">
            </td>
        </tr>
        <tr>
            <td>
                <b>Class name</b><br>
                <input name="className" type="text" class="skn-input"
                       value=""
                       size="20" maxlength="64">
            </td>
        </tr>
        <tr>
            <td><input type="submit" value="<i18n:message key="addBinding"> Add </i18n:message>" class="skn-button"
                       onclick="$('#<factory:encode name="actionToDo"/>').val('<%=WysiwygFormEditor.ACTION_ADD_BINDING_VAR%>');"></td>
        </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStartBindings">
        <factory:handler action="generateForm" />
        <tr>
        <td>
        <table cellpadding="1" cellspacing="0" border="0" width="100%">
        <tr>
            <td>Id</td>
            <td>Type</td>
            <td>Value</td>
            <td>Action</td>
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
                               href="<factory:url  action="delete"><factory:param name="bindingId" value="<%=id%>"/></factory:url>"
                               id="<factory:encode name='<%="deleteBtn_"+id%>'/>"
                               onclick="return confirm('<i18n:message key="delete.field.confirm">Sure?</i18n:message>');$('#<factory:encode name="actionToDo"/>').val('<%=WysiwygFormEditor.ACTION_REMOVE_BINDING_VAR%>');$('#<factory:encode name="idToRemove"/>').val('<%=id%>');">
                            <i18n:message key="delete">!!!Borrar</i18n:message>
                        </a></td>
                    </tr>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputEndBindings">
        </table>
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
        <script type="text/javascript" defer="defer">
            setAjax("<factory:encode name="formBindings"/>");
        </script>


    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
</mvc:formatter>
