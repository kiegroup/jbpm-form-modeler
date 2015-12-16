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

<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.CreateDynamicObjectFieldFormatter" %>
<%@ page import="org.jbpm.formModeler.core.processing.FormProcessor" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ page import="org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>


<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%try {%>

<mvc:formatter name="CreateDynamicObjectFieldFormatter">
    <mvc:formatterParam name="<%=CreateDynamicObjectFieldFormatter.PARAM_DISPLAYPAGE%>" value="false"/>
    <%----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="styleclass" id="styleclass">
            <mvc:fragmentValue name="cssStyle" id="cssStyle">
                <mvc:fragmentValue name="uid" id="uid">
                    <mvc:fragmentValue name="count" id="count">
                        <mvc:fragmentValue name="tableEnterMode" id="tableEnterMode">
                            <mvc:fragmentValue name="name" id="name">
        <input type="hidden" id='<%=uid + "_index"%>' name='<%=uid + "_index"%>' value="">
        <input type="hidden" id='<%=uid + "_child_uid_value"%>' name="child_uid_value" value="">
        <input type="hidden" id='<%=uid + "_parentFormId"%>' name="<%=uid + "_parentFormId"%>" value="">
        <input type="hidden" id='<%=uid + "_parentNamespace"%>' name="<%=uid + "_parentNamespace"%>" value="">
        <input type="hidden" id='<%=uid + "_field"%>' name='<%=uid + "_field"%>' value="">
        <input type="hidden" id='<%=uid + "_inputName"%>' name='<%=uid + "_inputName"%>' value="">

        <input type="hidden" id="<%=uid%>_tableEnterMode" name='<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "tableEnterMode"%>' value="<%=tableEnterMode%>">
        <input type="hidden" id="<%=uid%>_count" name='<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "count"%>' value="<%=count%>">

        <table cellpadding="0" cellspacing="0" class="dynInputStyle <%=StringUtils.defaultString((String) styleclass)%>" style='width:100%; <%=cssStyle!=null ? cssStyle:""%>'>
                                </mvc:fragmentValue>
                            </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="previewItem">
    <tr>
        <td>
            <jsp:include page="preview.jsp" flush="true"/>
        </td>
    </tr>

</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="editItem">
    <tr>
        <td>
            <jsp:include page="edit.jsp" flush="true"/>
        </td>
    </tr>

</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="tableStart">
    <mvc:fragmentValue name="className" id="className">
        <mvc:fragmentValue name="uid" id="uid">
            <tr>
            <td>
            <table class="<%=className%>" width="100%" cellspacing="1" cellpadding="1">
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="headerStart">
    <mvc:fragmentValue name="colspan" id="colspan">
        <tr class="skn-table_header">
        <%
            if(colspan!=null && ((Integer)colspan).intValue()>0) {
        %>
        <td colspan="<%=colspan%>" width="1px">
            <i18n:message key="actions">Actions!!!!!</i18n:message>
        </td>
        <%
            }
        %>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputColumnName">
    <td style="white-space: nowrap">
        <mvc:fragmentValue name="colLabel"/>
    </td>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="headerEnd">
    </tr>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputSubformActions">
    <mvc:fragmentValue name="modificable" id="modificable">
        <mvc:fragmentValue name="visualizable" id="visualizable">
            <mvc:fragmentValue name="deleteable" id="deleteable">
                <mvc:fragmentValue name="uid" id="uid">
                    <mvc:fragmentValue name="index" id="index">
                        <mvc:fragmentValue name="parentFormId" id="parentFormId">
                            <mvc:fragmentValue name="parentNamespace" id="parentNamespace">
                                <mvc:fragmentValue name="field" id="field">
                                    <mvc:fragmentValue name="namespace" id="namespace">

                        <tr valign="top" class='<%=((Integer) index).intValue() % 2 == 1 ? "skn-even_row" : "skn-odd_row"%>'>
<%
    if (Boolean.TRUE.equals(deleteable)) {
%>
                            <td align="center" style="width:13px">
                                <a title='<i18n:message key="delete">!!!Delete</i18n:message>'
                                   href="#"
                                   onclick="
                                       if (confirm('<i18n:message key="delete.confirm">Sure?</i18n:message>')) {
                                           document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                           document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                           document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                           document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                           document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                           document.getElementById('<%=uid + "_inputName"%>').value='<%=namespace%>';
                                           clearChangeDDMTrigger();
                                           sendFormToHandler(document.getElementById('<%=uid + "_child_uid_value"%>').form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler', 'deleteItem');
                                       }
                                       return false;"
                                   id="<%=uid%>_delete_<%=index%>">
                                    <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0">
                                </a>

                            </td>
<%
    }
    if (Boolean.TRUE.equals(visualizable)) {
%>
                            <td align="center" style="width:13px">
                                <a title='<i18n:message key="preview">!!!Preview</i18n:message>'
                                   href="#"
                                   onclick="
                                       document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                       document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                       document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                       document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                       document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                       document.getElementById('<%=uid + "_inputName"%>').value='<%=namespace%>';
                                       clearChangeDDMTrigger();
                                       sendFormToHandler(document.getElementById('<%=uid + "_child_uid_value"%>').form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler', 'previewItem');
                                       return false;"
                                   id="<%=uid%>_preview_<%=index%>">
                                    <img src="<static:image relativePath="general/16x16/preview.png"/>" border="0">
                                </a>
                            </td>
<%
    }
    if (Boolean.TRUE.equals(modificable)) {
%>
                            <td align="center" style="width:13px">
                                <a title="<i18n:message key="edit">!!!Edit</i18n:message>"
                                   href="#"
                                   onclick="
                                       document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                       document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                       document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                       document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                       document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                       document.getElementById('<%=uid + "_inputName"%>').value='<%=namespace%>';
                                       clearChangeDDMTrigger();
                                       sendFormToHandler(document.getElementById('<%=uid + "_child_uid_value"%>').form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler', 'editItem');
                                       return false;"
                                   id="<%=uid%>_edit_<%=index%>">
                                    <img src="<static:image relativePath="general/16x16/ico-edit.png"/>" border="0">
                                </a>
                            </td>
<%
    }
%>
                                    </mvc:fragmentValue>
                                </mvc:fragmentValue>
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="tableRow">
    <mvc:fragmentValue name="namespace" id="namespace">
        <mvc:fragmentValue name="form" id="form">
            <mvc:fragmentValue name="formValues" id="formValues">
                    <mvc:fragmentValue name="readonly" id="readonly">
                        <mvc:fragmentValue name="renderMode" id="renderMode">
                            <mvc:fragmentValue name="labelMode" id="labelMode">
                                <mvc:formatter
                                        name="FormRenderingFormatter">
                                    <%-- Formatter for table row, cannot use default rendering options --%>
                                    <mvc:formatterParam name="form" value="<%=form%>"/>
                                    <mvc:formatterParam name="renderMode" value="<%=renderMode%>"/>
                                    <mvc:formatterParam name="displayMode" value="default"/>
                                    <mvc:formatterParam name="formValues" value="<%=formValues%>"/>
                                    <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                                    <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
                                    <mvc:formatterParam name="labelMode" value="<%=labelMode%>"/>
                                    <mvc:fragment name="outputStart"></mvc:fragment>
                                    <mvc:fragment name="beforeField"><td valign="top"></mvc:fragment>
                                    <mvc:fragment name="afterField"></td></mvc:fragment>
                                    <mvc:fragment name="outputEnd"></tr></mvc:fragment>
                                </mvc:formatter>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="separator">
    <tr align="center">
        <td colspan="<mvc:fragmentValue name="colspan"/>">
            <mvc:fragmentValue name="separator"/>
        </td>
    </tr>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="tableEnd">
    </table><br>
    </td>
    </tr>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputEnterDataForm">
    <mvc:fragmentValue name="namespace" id="namespace">
    <mvc:fragmentValue name="field" id="field">
    <mvc:fragmentValue name="renderMode" id="renderMode">
    <mvc:fragmentValue name="readOnly" id="readOnly">
    <mvc:fragmentValue name="fieldName" id="fieldName">
        <tr>
            <td>
                <%
                    request.setAttribute(FormRenderingFormatter.ATTR_FIELD, field);
                    request.setAttribute(FormRenderingFormatter.ATTR_FORM_RENDER_MODE, renderMode);
                    request.setAttribute(FormRenderingFormatter.ATTR_NAMESPACE, namespace);
                    request.setAttribute(FormRenderingFormatter.ATTR_NAME, fieldName);
                    request.setAttribute(FormRenderingFormatter.ATTR_FIELD_IS_READONLY, readOnly);
                %>
                <jsp:include page="create.jsp" flush="true"/>
                <%
                    request.removeAttribute(FormRenderingFormatter.ATTR_FIELD);
                    request.removeAttribute(FormRenderingFormatter.ATTR_FORM_RENDER_MODE);
                    request.removeAttribute(FormRenderingFormatter.ATTR_NAMESPACE);
                    request.removeAttribute(FormRenderingFormatter.ATTR_NAME);
                    request.removeAttribute(FormRenderingFormatter.ATTR_FIELD_IS_READONLY);
                %>
            </td>
        </tr>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="renderError">
    <mvc:fragmentValue name="error" id="error">
        <tr>
            <td>
                <span class="skn-error">
                    <i18n:message key="<%=(String)error%>">!!!<%=error%></i18n:message>
                </span>
            </td>
        </tr>
    </mvc:fragmentValue>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
<mvc:fragment name="outputEnd">
    </table>
</mvc:fragment>
<%----------------------------------------------------------------------------------------------------%>
</mvc:formatter>
<%} catch (Throwable t) {
    System.out.println("Error showing CreateDynamicObject input " + t);
    t.printStackTrace();
}%>
