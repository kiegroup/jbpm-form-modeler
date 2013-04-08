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
<%@ page import="org.jbpm.formModeler.service.bb.commons.config.LocaleManager"%>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.components.editor.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<mvc:formatter name="org.jbpm.formModeler.components.editor.WysiwygFieldsToAddFormatter">
    <mvc:fragment name="fieldsToAddStart">
        <table width="90%">
    </mvc:fragment>
    <mvc:fragment name="empty">
        <div class="skn-error" style="padding:4px; padding-right:15px;">
            <i18n:message key="allPropertiesInUse">
                !!!Todos los campos de la entidad ya se est&aacute;n empleando en el formulario.
            </i18n:message>
        </div>
    </mvc:fragment>
    <mvc:fragment name="outputFieldsToAddStart">
        <tr><td>
        <table valign="top" border="0" width="100%"><tr>
    </mvc:fragment>
    <mvc:fragment name="outputPropertyStart">
        <mvc:fragmentValue name="position" id="position">
            <td width="33%" >
            <script defer="true">
                setAjax("<factory:encode name='<%="addFieldForm"+position%>'/>");
            </script>
            <form method="POST" style="margin:0px;" action="<factory:formUrl/>" id="<factory:encode name='<%="addFieldForm"+position%>'/>">
            <factory:handler  action="addFieldToFormulary"/>
            <table cellspacing="0" width="100%" cellpadding="2"  onmouseover="className='skn-even_row_alt'" onmouseout="className='skn-odd_row'"  class="skn-odd_row">
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputProperty">
        <tr>
            <td>
                <b style="cursor:text;"
                   onclick="this.innerHTML='<input name=\'label\' style=\'width:100px\'  maxlength=\'200\' class=\'skn-input\' value=\'<mvc:fragmentValue name="name"/>\'>'; this.onclick=''; "
                        >
                    <mvc:fragmentValue name="name"/></b>
                <input type="hidden" name="name" value="<mvc:fragmentValue name="name"/>">
            </td><td rowspan="2" align="right">
            <input type="image" onclick="this.onclick=function(){return false;}" style="cursor:hand" src="<static:image relativePath="actions/triang_right.png"/>">
        </td></tr>
    </mvc:fragment>

    <mvc:fragment name="startFieldTypes">
        <tr>
        <td>
        <select name="fieldType" class="skn-input" style="width:150px">
    </mvc:fragment>
    <mvc:fragment name="outputFieldType">
        <mvc:fragmentValue name="id" id="id">
            <option title="<i18n:message key="<%="fieldType." + id%>"/>" value="<%=id%>">
                <i18n:message key="<%="fieldType." + id%>"/>
            </option>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="endFieldTypes">
        </select>
        </td></tr>
    </mvc:fragment>

    <mvc:fragment name="outputPropertyEnd">
        </table></form>
        </td>
        </tr>
        <tr>
    </mvc:fragment>
    <mvc:fragment name="outputFieldsToAddEnd">
        </tr>
        </table>
        </td></tr>
    </mvc:fragment>
    <mvc:fragment name="fieldsToAddEnd">
        </table>

    </mvc:fragment>

</mvc:formatter>
