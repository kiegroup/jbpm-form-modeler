<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ page import="org.jbpm.formModeler.core.processing.FormProcessor" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle id="bundle" baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="MultipleSubformCreateItemFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="form" id="form">
            <mvc:fragmentValue name="namespace" id="namespace">
                <mvc:fragmentValue name="uid" id="uid">
                    <mvc:fragmentValue name="name" id="name">
                        <mvc:fragmentValue name="fieldName" id="fieldName">
                            <mvc:fragmentValue name="entityName" id="entityName">
                                <mvc:fragmentValue name="expanded" id="expanded">
                                    <mvc:fragmentValue name="noCancelButton" id="noCancelButton">
                                        <mvc:fragmentValue name="readonly" id="readonly">
                                            <mvc:fragmentValue name="renderMode" id="renderMode">
                                                        <input type="hidden" id="<%=uid%>_expand" name="<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "expand"%>" value="leaveItAlone">
                                                        <input type="hidden" id="<%=uid%>_create" name="<%=name + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create"%>" value="leaveItAlone">
                                                        <%
                                                            if (Boolean.TRUE.equals(expanded)) {
                                                        %>
                                                        <table align="left" border="0"  width="100%" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td>
                                                                    <mvc:formatter name="FormRenderingFormatter">
                                                                        <mvc:formatterParam name="form" value="<%=form%>"/>
                                                                        <mvc:formatterParam name="namespace" value="<%=namespace%>"/>
                                                                        <mvc:formatterParam name="isMultiple" value="true"/>
                                                                        <mvc:formatterParam name="isSubForm" value="true"/>
                                                                        <mvc:formatterParam name="isReadonly" value="<%=readonly%>"/>
                                                                        <mvc:formatterParam name="renderMode" value="<%=renderMode%>"/>
                                                                        <%@ include file="/formModeler/components/WysiwygFormEdit/menu/defaultFormRenderingFormatterOptions.jsp" %>
                                                                    </mvc:formatter>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>
                                                                    <table align="left" border="0"  width="100%">
                                                                        <tr>
                                                                            <td align="center" nowrap="nowrap" style="padding-top:10px">
                                                                                <input type="button" class="skn-button"
                                                                                       value="<mvc:fragmentValue name="newItemButtonText"/>"
                                                                                       onclick="this.disabled=true; document.getElementById('<%=uid%>_create').value=true;clearChangeDDMTrigger();sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler', 'addItem');">
                                                                                <% if (!Boolean.TRUE.equals(noCancelButton)) { %>
                                                                                <input type="button" class="skn-button_alt"
                                                                                       value="<mvc:fragmentValue name="cancelButtonText"/>"
                                                                                       onclick="document.getElementById('<%=uid%>_create').value=false;document.getElementById('<%=uid%>_expand').value=false;clearChangeDDMTrigger();sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler', 'expandSubform');"/>
                                                                                <% } %>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>

                                                        <%
                                                            }
                                                            if (!Boolean.TRUE.equals(expanded) && !Boolean.TRUE.equals(readonly)) {
                                                        %>

                                                        <div style="text-align:center; padding-top:0px; width:100%;">
                                                            <input type="button" class="skn-button" value="<mvc:fragmentValue name="addItemButtonText"/>"
                                                                   onclick="this.disabled=true; document.getElementById('<%=uid%>_expand').value=true;clearChangeDDMTrigger();sendFormToHandler(this.form, 'org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler', 'expandSubform');"/>
                                                        </div>
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
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
