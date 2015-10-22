<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ page import="org.jbpm.formModeler.core.processing.FormProcessor" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="EditMultipleSubformItemFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="form" id="form">
            <mvc:fragmentValue name="value" id="value">
                <mvc:fragmentValue name="index" id="index">
                    <mvc:fragmentValue name="parentFormId" id="parentFormId">
                        <mvc:fragmentValue name="parentNamespace" id="parentNamespace">
                            <mvc:fragmentValue name="namespace" id="namespace">
                                <mvc:fragmentValue name="name" id="name">
                                    <mvc:fragmentValue name="field" id="field">
                                        <mvc:fragmentValue name="uid" id="uid">
                                            <mvc:fragmentValue name="readonly" id="readonly">
                                                        <table width="100%" cellspacing="1" cellpadding="1">
                                                            <tr>
                                                                <td>
                                                                    <mvc:formatter name="FormRenderingFormatter">
                                                                        <mvc:formatterParam name="form" value="<%=form%>"/>
                                                                        <mvc:formatterParam name="formValues" value="<%=value%>"/>
                                                                        <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                                                                        <mvc:formatterParam name="isMultipleSubForm" value="true"/>
                                                                        <mvc:formatterParam name="isSubForm" value="true"/>
                                                                        <mvc:formatterParam name="isMultiple" value="true"/>
                                                                        <mvc:formatterParam name="renderMode" value="<%=Form.RENDER_MODE_FORM%>"/>
                                                                        <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
                                                                        <%@ include file="/formModeler/components/WysiwygFormEdit/menu/defaultFormRenderingFormatterOptions.jsp" %>
                                                                    </mvc:formatter>
                                                                </td>
                                                            </tr>
                                                            <%
                                                                if(!Boolean.TRUE.equals(readonly)) {
                                                            %>

                                                            <tr>
                                                                <td align="center" style="padding-top:10px">
                                                                    <input type="hidden" name="<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "saveEdited"%>" value="false">
                                                                    <input type="button" class="skn-button" value="<i18n:message key="save">!!!Save</i18n:message>"
                                                                           onclick="this.form.elements['<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "saveEdited"%>'].value=true;clearChangeDDMTrigger();sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler', 'saveEditedItem');"
                                                                            >
                                                                    <input type="button" class="skn-button_alt" value='<i18n:message key="return">!!!Return</i18n:message>'
                                                                           onclick="
                                                                                   document.getElementById('<%=uid + "_child_uid_value"%>').value='<%=uid%>';
                                                                                   document.getElementById('<%=uid + "_index"%>').value='<%=index%>';
                                                                                   document.getElementById('<%=uid + "_parentFormId"%>').value='<%=parentFormId%>';
                                                                                   document.getElementById('<%=uid + "_parentNamespace"%>').value='<%=parentNamespace%>';
                                                                                   document.getElementById('<%=uid + "_field"%>').value='<%=field%>';
                                                                                   document.getElementById('<%=uid + "_inputName"%>').value='<%=namespace%>';
                                                                                   clearChangeDDMTrigger();
                                                                                   sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler', 'cancelEditItem');">
                                                                </td>
                                                            </tr>
                                                            <%
                                                                }
                                                            %>
                                                        </table>
                                            </mvc:fragmentValue>
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
    <mvc:fragment name="noShowDataForm">
        <span class="skn-error">
            <i18n:message key="noShowForm">
                !!Undefined form to show!
            </i18n:message>
        </span>
    </mvc:fragment>
</mvc:formatter>
