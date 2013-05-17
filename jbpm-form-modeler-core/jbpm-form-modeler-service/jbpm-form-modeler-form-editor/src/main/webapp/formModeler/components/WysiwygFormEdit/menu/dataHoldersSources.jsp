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
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ page import="org.jbpm.formModeler.components.editor.WysiwygFormEditor" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jbpm.formModeler.components.editor.DataHoldersFormFormatter">
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStart">
    <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="formDataHolders"/>">
    <factory:handler action="formDataHolders"/>
    <input type="hidden" name="<%=WysiwygFormEditor.ACTION_TO_DO%>" id="<factory:encode name="actionToDo"/>"
           value="<%=WysiwygFormEditor.ACTION_ADD_DATA_HOLDER%>"/>
    <table width="100%" >
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputFormAddHolderStart">
    <tr>
    <td class="LeftColumnProperties" align="center">
    <script type="text/javascript">

        function show_dataholderInfo(divStr) {
            if (divStr == '<%=Form.HOLDER_TYPE_CODE_BPM_PROCESS%>') {
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_BPM_PROCESS%>').style.display = 'block';
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL%>').style.display = 'none';
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_POJO_CLASSNAME%>').style.display = 'none';
            } else if (divStr == '<%=Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL%>') {
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_BPM_PROCESS%>').style.display = 'none';
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL%>').style.display = 'block';
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_POJO_CLASSNAME%>').style.display = 'none';
            } else if (divStr == '<%=Form.HOLDER_TYPE_CODE_POJO_CLASSNAME%>') {
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_BPM_PROCESS%>').style.display = 'none';
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL%>').style.display = 'none';
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_POJO_CLASSNAME%>').style.display = 'block';
            } else {
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_BPM_PROCESS%>').style.display = 'none';
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL%>').style.display = 'none';
                document.getElementById('<%=Form.HOLDER_TYPE_CODE_POJO_CLASSNAME%>').style.display = 'none';
            }

        }
        show_dataholderInfo("none");

    </script>
    <table>
    <tr>
        <td><b><i18n:message key="dataHolder_id">!!!dataHolder_id</i18n:message>:</b></td>
    </tr>
    <tr>
        <td><input name="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>" type="text" class="skn-input" value=""
                   size="20" maxlength="64"></td>
    </tr>
    <tr>
        <td><b><i18n:message key="dataHolder_renderColor">!!!dataHolder_renderColor</i18n:message>:</b></td>
    </tr>
    <tr>
        <td>
            <select class="skn-input" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_RENDERCOLOR%>">
                <option value="#FF8881"><i18n:message key="holder_color_red">Red</i18n:message></option>
                <option value="#FBB767"><i18n:message key="holder_color_orange">Orange</i18n:message></option>
                <option value="#E9E371"><i18n:message key="holder_color_yellow">Yellow</i18n:message></option>
                <option value="#A7E690"><i18n:message key="holder_color_green">Green</i18n:message></option>
                <option value="#9BCAFA"><i18n:message key="holder_color_blue">Blue</i18n:message></option>
                <option value="#B29FE4"><i18n:message key="holder_color_violet">Violet</i18n:message></option>
                <option value="#BBBBBB"><i18n:message key="holder_color_grey">Grey</i18n:message></option>
            </select>
        </td>
    </tr>
    <tr>
        <td><b><i18n:message key="dataHolder_type">!!!dataHolder_type</i18n:message>:</b></td>
    </tr>
    <tr>
        <td>
            <!--input type="radio" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_TYPE%>"
                   value="<%=Form.HOLDER_TYPE_CODE_BPM_PROCESS%>"
                   onclick="show_dataholderInfo('<%=Form.HOLDER_TYPE_CODE_BPM_PROCESS%>');">&nbsp;<i18n:message
                key="dataHolder_process">!!!Process </i18n:message><br-->
            <input type="radio" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_TYPE%>"
                   value="<%=Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL%>"
                   onclick="show_dataholderInfo('<%=Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL%>')">&nbsp;<i18n:message
                key="dataHolder_datamodel">!!!Data Model source</i18n:message><br>
            <input type="radio" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_TYPE%>"
                   value="<%=Form.HOLDER_TYPE_CODE_POJO_CLASSNAME%>"
                   onclick="show_dataholderInfo('<%=Form.HOLDER_TYPE_CODE_POJO_CLASSNAME%>')">&nbsp;<i18n:message
                key="dataHolder_info_javaClass">!!!dataHolder_info_javaClass</i18n:message>
        </td>
    </tr>
    <tr>
        <td><b><i18n:message key="dataHolder_info">!!!dataHolder_info</i18n:message>:</b></td>
    </tr>
    <td valign="top">
    <table cellpadding="0" cellspacing="0" border="0" width="100%" >
</mvc:fragment>
<mvc:fragment name="rowStart">
    <td>
</mvc:fragment>
<%--------------------------------------------------------------------------------------------------%>
<mvc:fragment name="selectStart">
    <mvc:fragmentValue name="id" id="id">
        <mvc:fragmentValue name="name" id="name">
            <select class="skn-input" id="<%= id %>" name="<%=name%>" style="display: none">
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%--------------------------------------------------------------------------------------------------%>
<mvc:fragment name="selectOption">
    <mvc:fragmentValue name="optionLabel" id="optionLabel">
        <mvc:fragmentValue name="optionValue" id="optionValue">
            <option value="<%=optionValue%>"><%=optionLabel%>
            </option>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%--------------------------------------------------------------------------------------------------%>
<mvc:fragment name="selectEnd">
    </select>
</mvc:fragment>
<mvc:fragment name="rowEnd">
    </td>
</mvc:fragment>
<mvc:fragment name="outputFormAddHolderEnd">
    <tr>
        <td><input id="<%=Form.HOLDER_TYPE_CODE_POJO_CLASSNAME%>"
                   name="<%=WysiwygFormEditor.PARAMETER_HOLDER_INFO%>" type="text" class="skn-input" value=""
                   size="20" maxlength="64" style="display: none"></td>
    </tr>
    </table></td>
    <tr>
    </table>
    <br>

    <div style="text-align: center;">
        <input type="submit"
               value="<i18n:message key="dataHolder_addDataHolder">!!! dataHolder_addDataHolder</i18n:message>"
               class="skn-button">
    </div>

    </td>


</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStartBindings">

    <td class="RightMainColumn">

    <h1>Form Data Holder Management</h1>

    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et
        dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex
        ea commodo consequat.</p>
    <br><br>
    <table width="50%" class="skn-table_border" align="center">
    <tr class="skn-table_header">
        <td>&nbsp;</td>
        <td><i18n:message key="dataHolder_renderColor">!!!!!!dataHolder_renderColor</i18n:message></td>
        <td><i18n:message key="dataHolder_id">!!!dataHolder_id</i18n:message></td>
        <td><i18n:message key="dataHolder_type">!!!dataHolder_type</i18n:message></td>
        <td><i18n:message key="dataHolder_info">!!!!!!dataHolder_info</i18n:message></td>


    </tr>
</mvc:fragment>

<mvc:fragment name="outputBindings">
    <mvc:fragmentValue name="id" id="id">
        <mvc:fragmentValue name="type" id="type">
            <mvc:fragmentValue name="value" id="value">
                <mvc:fragmentValue name="renderColor" id="renderColor">
                    <tr>
                        <td align="center"><a title="<i18n:message key="delete">!!!Borrar</i18n:message>"
                                              href="<factory:url  action="formDataHolders">
                                         <factory:param name="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>" value="<%=id%>"/>
                                         <factory:param name="<%=WysiwygFormEditor.ACTION_TO_DO%>" value="<%=WysiwygFormEditor.ACTION_REMOVE_DATA_HOLDER%>"/>
                                      </factory:url>"
                                              onclick="return confirm('<i18n:message
                                                      key="dataHolder_delete.confirm">Sure?</i18n:message>');">
                            <img src="<static:image relativePath="actions/delete.png"/>" border="0"
                                 title="<i18n:message key="delete">!!!Clear</i18n:message>"/>
                        </a></td>
                        <td>
                            <div style="background-color: <%=renderColor%> ">&nbsp;</div>
                        </td>
                        <td><%=id%>
                        </td>
                        <td><%=type%>
                        </td>
                        <td><%=value%>
                        </td>


                    </tr>
                </mvc:fragmentValue>
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
    </table>

    </form>
    <script type="text/javascript" defer="defer">
        setAjax("<factory:encode name="formDataHolders"/>");
    </script>


</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
</mvc:formatter>
