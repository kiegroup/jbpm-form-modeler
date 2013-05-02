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

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jbpm.formModeler.components.editor.DataHoldersFormFormatter">
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="formDataHolders"/>">
        <factory:handler action="formDataHolders" />
        <input type="hidden" name="<%=WysiwygFormEditor.ACTION_TO_DO%>" id="<factory:encode name="actionToDo"/>" value="<%=WysiwygFormEditor.ACTION_ADD_DATA_HOLDER%>"/>
        <table cellpadding="0" cellspacing="0" border="0" width="100%" >
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputNameInput">
        <tr>
            <td class="FormProperties" align="center">
                <script type="text/javascript">

                    function show_dataholderInfo(divStr){
                        if (divStr =='val1'){
                            document.getElementById('val1').style.display='block';
                            document.getElementById('val2').style.display='none';
                            document.getElementById('val3').style.display='none';
                        } else if (divStr=='val2'){
                            document.getElementById('val1').style.display='none';
                            document.getElementById('val2').style.display='block';
                            document.getElementById('val3').style.display='none';
                        } else if (divStr=='val3'){
                            document.getElementById('val1').style.display='none';
                            document.getElementById('val2').style.display='none';
                            document.getElementById('val3').style.display='block';
                        } else {
                            document.getElementById('val1').style.display='none';
                            document.getElementById('val2').style.display='none';
                            document.getElementById('val3').style.display='none';
                        }

                    }
                    show_dataholderInfo("none");

                </script>
                <table cellpadding="2" cellspacing="0" border="0">
                    <tr>
                        <td width="25%"><b><i18n:message key="dataHolder_id">!!!dataHolder_id</i18n:message>:</b></td>
                        <td width="25%"><b><i18n:message key="dataHolder_renderColor">!!!dataHolder_renderColor</i18n:message>:</b></td>
                        <td width="20%"><b><i18n:message key="dataHolder_type">!!!dataHolder_type</i18n:message>:</b></td>
                        <td width="25%"><b><i18n:message key="dataHolder_info">!!!dataHolder_info</i18n:message>:</b></td>
                    <tr>
                    <tr>
                        <td valign="top"><input name="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>" type="text" class="skn-input" value="" size="20" maxlength="64"></td>
                        <td valign="top"><select class="skn-input" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_RENDERCOLOR%>">
                            <option value="#FF8881">ROJO </option>
                            <option value="#FBB767">NARANJA</option>
                            <option value="#E9E371">AMARILLO</option>
                            <option value="#A7E690">VERDE</option>
                            <option value="#9BCAFA">AZUL</option>
                            <option value="#B29FE4">VIOLETA</option>
                            <option value="#BBBBBB">GRIS</option>
                        </select></td>

                        <td valign="top">
                            <table cellpadding="0" cellspacing="0" border="0" width="100%" >
                                <tr><td><input type="radio" name="group1" value="val1" onclick="show_dataholderInfo('val1');"><i18n:message key="dataHolder_process">!!!Process </i18n:message></td></tr>
                                <tr><td><input type="radio" name="group1" value="val2" onclick="show_dataholderInfo('val2')"><i18n:message key="dataHolder_datamodel">!!!Data Model source</i18n:message></td></tr>
                                <tr><td><input type="radio" name="group1" value="val2" onclick="show_dataholderInfo('val3')"><i18n:message key="dataHolder_info_javaClass">!!!dataHolder_info_javaClass</i18n:message><br></td></tr>
                            </table>
                        </td>
                        <td valign="top"><table cellpadding="0" cellspacing="0" border="0" width="100%" >
                            <tr><td><select class="skn-input" id="val1" style="display: none">
                                <option value="bindStr11">Process1-Task1 - inputForm</option>
                                <option value="bindStr12">Process1-Task2 - inputForm</option>
                                <option value="bindStr13">Process1-Task3 - inputForm</option>
                                <option value="bindStr14">Process1-Task4 - inputForm</option>
                                <option value="bindStr21">Process2-Task1 - inputForm</option>
                                <option value="bindStr22">Process2-Task2 - inputForm</option>
                            </select></tr>
                            <tr><td><select class="skn-input" id="val2" style="display: none">
                                <option value="bindStr11">Model1 </option>
                                <option value="bindStr12">Model2 </option>
                                <option value="bindStr13">Model3 </option>
                            </select></td></tr>
                            <tr><td> <input id="val3" name="<%=WysiwygFormEditor.PARAMETER_HOLDER_INFO%>" type="text" class="skn-input" value="" size="20" maxlength="64" style="display: none"></td></tr>
                        </table></td>
                    <tr>
                </table>
                <br>
                <div style="text-align: center;">
                    <input type="submit" value="<i18n:message key="dataHolder_addDataHolder">!!! dataHolder_addDataHolder</i18n:message>" class="skn-button">
                </div>

            </td>
        </tr>

    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStartBindings">
        <tr>
        <td style="vertical-align: top">
        <br><br>
        <table cellpadding="4" cellspacing="1" border="0" width="50%" class="skn-table_border" align="center">
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
                        <tr><td align="center"><a title="<i18n:message key="delete">!!!Borrar</i18n:message>"
                                                  href="<factory:url  action="formDataHolders">
                                         <factory:param name="<%=WysiwygFormEditor.PARAMETER_HOLDER_ID%>" value="<%=id%>"/>
                                         <factory:param name="<%=WysiwygFormEditor.ACTION_TO_DO%>" value="<%=WysiwygFormEditor.ACTION_REMOVE_DATA_HOLDER%>"/>
                                      </factory:url>"
                                                  onclick="return confirm('<i18n:message key="dataHolder_delete.confirm">Sure?</i18n:message>');">
                            <img src="<static:image relativePath="actions/delete.png"/>" border="0" title="<i18n:message key="delete">!!!Clear</i18n:message>"/>
                        </a></td>
                            <td><div style="background-color: <%=renderColor%> ">&nbsp;</div></td>
                            <td><%=id%></td>
                            <td><%=type%></td>
                            <td><%=value%></td>



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
