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
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ page import="org.jbpm.formModeler.service.bb.commons.config.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jbpm.formModeler.components.editor.EditFormFormatter">
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStart">
    <form style="margin:0px" action="<factory:formUrl/>" id="<factory:encode name="saveForm"/>">
    <factory:handler action="saveCurrentForm" />
    <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputNameInput">
    <td>
        <b><i18n:message key="name">!!!Nombre</i18n:message></b><br>
        <input name="name" type="text" class="skn-input"
               value="<mvc:fragmentValue name="formName"/>"
               size="20" maxlength="64">
    </td>
    </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStatusInputStart">
    <tr>
    <td>
        <b><i18n:message key="status">!!!Status</i18n:message></b><br>
    <select  name="status" class="skn-input" >
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStatusInputOption">
    <mvc:fragmentValue name="optionValue" id="optionValue">
        <option <mvc:fragmentValue name="selected"/> value="<%=optionValue%>">
            <i18n:message key='<%="formStatus."+optionValue%>'><%=optionValue%></i18n:message>
        </option>
    </mvc:fragmentValue>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStatusInputEnd">
    </select>
    </td>
    </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputDefaultFormInput">
    <tr>
    <td >
        <b><i18n:message key="Purpose">!!!Finalidad</i18n:message></b><br>
    <table cellpadding="0" cellspacing="0" border="0" width="50%">
    <tr>
        <td width="1px">
            <mvc:fragmentValue name="default" id="isDefault">
                <input name="default" type="checkbox" value="true"
                    <%=((Boolean)isDefault).booleanValue()?"checked":""%>>
            </mvc:fragmentValue>
        </td>
        <td title="<i18n:message key="default.alt">!!!Editar</i18n:message>">
            <i18n:message key="default">!!!Editar</i18n:message>
        </td>
</mvc:fragment>
<mvc:fragment name="outputDefaultViewFormInput">
        <td width="1px">
            <mvc:fragmentValue name="defaultView" id="isDefaultView">
                <input name="defaultView" type="checkbox" value="true"
                    <%=((Boolean)isDefaultView).booleanValue()?"checked":""%>>
            </mvc:fragmentValue>
        </td>
        <td title="<i18n:message key="defaultView.alt">!!!Vista por defecto</i18n:message>">
            <i18n:message key="defaultView">!!!Vista por defecto</i18n:message>
        </td>
</mvc:fragment>
<mvc:fragment name="outputShortViewFormInput">
        <td width="1px">
            <mvc:fragmentValue name="shortView" id="isShortView">
                <input name="shortView" type="checkbox" value="true"
                    <%=((Boolean)isShortView).booleanValue()?"checked":""%>>
            </mvc:fragmentValue>
        </td>
        <td title="<i18n:message key="shortView.alt">!!!Vista corta</i18n:message>">
            <i18n:message key="shortView">!!!Vista corta</i18n:message>
        </td>
    </tr>
</mvc:fragment>
<mvc:fragment name="outputCreationViewFormInput">
    <tr>
        <td width="1px">
            <mvc:fragmentValue name="creationView" id="isCreationView">
                <input name="creationView" type="checkbox" value="true"
                    <%=((Boolean)isCreationView).booleanValue()?"checked":""%>>
            </mvc:fragmentValue>
        </td>
        <td title="<i18n:message key="creationView.alt">!!!Vista para la creacion</i18n:message>">
            <i18n:message key="creationView">!!!Vista para la creacion</i18n:message>
        </td>
</mvc:fragment>
<mvc:fragment name="outputSearchViewFormInput">
        <td width="1px">
            <mvc:fragmentValue name="searchView" id="isSearchView">
                <input name="searchView" type="checkbox" value="true"
                    <%=((Boolean)isSearchView).booleanValue()?"checked":""%>>
            </mvc:fragmentValue>
        </td>
        <td title="<i18n:message key="searchView.alt">!!!Vista para la b&uacute;squeda</i18n:message>">
            <i18n:message key="searchView">!!!Vista para la b&uacute;squeda</i18n:message>
        </td>
</mvc:fragment>
<mvc:fragment name="outputResultViewFormInput">
        <td width="1px">
            <mvc:fragmentValue name="resultView" id="isResultView">
                <input name="resultView" type="checkbox" value="true"
                    <%=((Boolean)isResultView).booleanValue()?"checked":""%>>
            </mvc:fragmentValue>
        </td>
        <td title="<i18n:message key="resultView.alt">!!!Resultado </i18n:message>">
            <i18n:message key="resultView">!!!Resultado </i18n:message>
        </td>
    </tr>
    </table>
    </td></tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputDisplayModeStart">
    <tr>
    <td>
        <b><i18n:message key="displayMode">
            !!! Modo de visualizacion:
        </i18n:message></b><br>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputDefaultDisplayMode">
    <input
        <mvc:fragmentValue name="checked"/> type="radio"
                                            name="displayMode" value="default">
    <i18n:message key="displayMode.default">!!!Por defecto</i18n:message>
    <br>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputAlignedDisplayMode">
    <input
        <mvc:fragmentValue name="checked"/> type="radio"
                                            name="displayMode" value="aligned">
    <i18n:message key="displayMode.aligned">!!!Campos alineados</i18n:message>
    <br>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputNoneDisplayMode">
    <input
        <mvc:fragmentValue name="checked"/> type="radio"
                                            name="displayMode" value="none">
    <i18n:message key="displayMode.none">!!!Sin alineaci&oacute;n</i18n:message>
    <br>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputTemplateDisplayMode">
    <input <mvc:fragmentValue name="checked"/> type="radio"  id="<factory:encode name="editTemplateCheckbox"/>"
                                            name="displayMode" value="template">
    <i18n:message key="displayMode.template">!!!Plantilla</i18n:message>
    <%--a href="<panel:link action="startEditTemplate"/>" id="<factory:encode name="editTemplateLnk"/>">
        <i18n:message key="edit">!!!Edit</i18n:message>
    </a--%>
    <input type="hidden" name="editTemplate" value="false">
    <a href="formProperties.jsp#" onclick="var chk = document.getElementById('<factory:encode name="editTemplateCheckbox"/>');
        chk.checked=true; chk.form.editTemplate.value='true' ;submitAjaxForm(chk.form); return false;">
        <img    src="<static:image relativePath="general/16x16/ico-actions_edit.png"/>"
                title="<i18n:message key="edit">!!!Edit</i18n:message>"
                alt="<i18n:message key="edit">!!!Edit</i18n:message>"
                border="0"
                >
    </a>
    <%--script>
        setAjax("<factory:encode name="editTemplateLnk"/>");
    </script--%>
    <br>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputCustomDisplayMode">
    <span>
                    <input <mvc:fragmentValue name="checked"/> type="radio"
                                                            name="displayMode" value="custom">
                    <i18n:message key="displayMode.custom">!!!A medida</i18n:message><br>
                    <%=Form.DISPLAY_MODE_CUSTOM_PATH_PREFIX%>
                    <input name="customJsp" class="skn-input" size="12" maxlength="512"
                           value="<mvc:fragmentValue name="pageValue"/>">
                </span>
    <br>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputDisplayModeEnd">
    </td>
    </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputLabelModeStart">
    <tr>
    <td>
        <b><i18n:message key="labelMode">
            !!! Modo de visualizacion:
        </i18n:message></b><br>
    <select class="skn-input" name="labelMode">
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputLabelMode">
    <mvc:fragmentValue name="labelMode" id="labelMode">
        <option value="<%=labelMode%>">
            <i18n:message key='<%="labelMode."+labelMode%>'><%=labelMode%></i18n:message>
        </option>
    </mvc:fragmentValue>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputLabelModeSelected">
    <mvc:fragmentValue name="labelMode" id="labelMode">
        <option selected class="skn-important" value="<%=labelMode%>">
            <i18n:message key='<%="labelMode."+labelMode%>'><%=labelMode%></i18n:message>
        </option>
    </mvc:fragmentValue>
</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputLabelModeEnd">
    </select>
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
                        <input id="<factory:encode name="saveFormSubmit"/>" type="submit"
                               class="skn-button" value="<i18n:message key="save">!!!Aceptar</i18n:message>"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>

    </table>
    </form>

</mvc:fragment>
<%------------------------------------------------------------------------------------------------------------%>
</mvc:formatter>
<script defer>
    setAjax("<factory:encode name="saveForm"/>");
</script>