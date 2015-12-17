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
<%@ page import="org.jbpm.formModeler.api.model.wrappers.I18nSet" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jbpm.formModeler.service.LocaleManager"%>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.HTMLTextAreaFieldHandler" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%try {
%>
<mvc:formatter name="SimpleFieldHandlerFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="name" id="name">
            <mvc:fragmentValue name="title" id="title">
                <mvc:fragmentValue name="uid" id="uid">
                    <mvc:fragmentValue name="styleclass" id="styleclass">
                        <mvc:fragmentValue name="size" id="size">
                            <mvc:fragmentValue name="maxlength" id="maxlength">
                                <mvc:fragmentValue name="tabindex" id="tabindex">
                                    <mvc:fragmentValue name="value" id="val">
                                        <mvc:fragmentValue name="accesskey" id="accesskey">
                                            <mvc:fragmentValue name="alt" id="altvalue">
                                                <mvc:fragmentValue name="localeManager" id="localeManager">
                                                    <mvc:fragmentValue name="wrong" id="wrong">
                                                        <mvc:fragmentValue name="cssStyle" id="cssStyle">
                                                            <mvc:fragmentValue name="height" id="height">
                                                                <mvc:fragmentValue name="readonly" id="readonly">
                                                                        <!--<table border="0" cellpadding="0" cellspacing="0" >
                                                                        <tr valign="top">
                                                                        <td>
                                                                        <mvc:formatter name="org.jbpm.formModeler.service.mvc.formatters.ForFormatter">
                                                                            <mvc:formatterParam name="bean" value="org.jbpm.formModeler.service.LocaleManager"/>
                                                                            <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                                                                            <mvc:fragment name="outputStart">
                                                                                <select id="<mvc:fragmentValue name="uid"/>"
                                                                                <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                                class='dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>'
                                                                                style="<%=StringUtils.defaultString((String) cssStyle)%>"
                                                                                onchange="
                                                                                <mvc:formatter name="org.jbpm.formModeler.service.mvc.formatters.ForFormatter">
                                                                                    <mvc:formatterParam name="bean" value="org.jbpm.formModeler.service.LocaleManager"/>
                                                                                    <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                                                                                    <mvc:fragment name="output">
                                                                                        <mvc:fragmentValue name="index" id="index">
                                                                                            <mvc:fragmentValue name="element" id="locale">
                                                                                                var elements = this.parentNode.parentNode.parentNode.getElementsByTagName('DIV');
                                                                                                var selectedOption = this.options[this.selectedIndex];
                                                                                                for(i =0 ; i<elements.length; i++) {
                                                                                                var element = elements[i];
                                                                                                if( element.id == '<%=uid+((Locale) locale).toString()%>')
                                                                                                if(element.id == '<%=uid%>'+selectedOption.value ) {
                                                                                                element.style.display= 'inline';
                                                                                                }
                                                                                                else{
                                                                                                element.style.display= 'none';
                                                                                                }
                                                                                                }
                                                                                            </mvc:fragmentValue>
                                                                                        </mvc:fragmentValue>
                                                                                    </mvc:fragment>
                                                                                </mvc:formatter>
                                                                                ">
                                                                            </mvc:fragment>
                                                                            <mvc:fragment name="output">
                                                                                <mvc:fragmentValue name="index" id="index">
                                                                                    <mvc:fragmentValue name="element" id="locale">
                                                                                        <option <%=((Locale) locale).getLanguage().equals(LocaleManager.currentLocale().getLanguage()) ? "selected" : ""%>
                                                                                        value="<%=((Locale)locale).toString()%>">
                                                                                        <%=StringUtils.capitalize(((Locale) locale).getDisplayName((Locale) locale))%>
                                                                                        </option>
                                                                                    </mvc:fragmentValue>
                                                                                </mvc:fragmentValue>
                                                                            </mvc:fragment>
                                                                            <mvc:fragment name="outputEnd">
                                                                                </select>
                                                                            </mvc:fragment>
                                                                        </mvc:formatter>
                                                                        </td>
                                                                        </tr>
                                                                        </table>-->



                                                                        <table border="0" cellpadding="0" cellspacing="0" >
                                                                            <tr valign="top">
                                                                                <td style="padding-bottom: 320px;">
                                                                                    <div style=" width:<%=size!=null?size:"250"%>px; height:<%=height!=null?height:"170"%>px;<%=StringUtils.defaultString((String) cssStyle)%>"
                                                                                         id="<%=uid%>_divcontainer" class="dynInputStyle">
                                                                                        <textarea id="<%=uid%>_<%=LocaleManager.currentLang()%>" name="<%=name%>_<%=LocaleManager.currentLang()%>"
                                                                                                  onchange="processFormInputChange(this)"
                                                                                                  onkeyup="return ismaxlength(this)"
                                                                                                  rows="4" cols="50"
                                                                                                <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                                                  class="skn-input"
                                                                                                <%=maxlength != null ? " maxlength=\"" + maxlength + "\"" : ""%>
                                                                                                <%=tabindex != null ? " tabindex=\"" + tabindex + "\"" : ""%>
                                                                                                <%=accesskey != null ? " accesskey=\"" + accesskey + "\"" : ""%>
                                                                                                <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                                                <%=readonly != null && ((Boolean) readonly).booleanValue() ? " readonly disabled " : ""%>><%=StringEscapeUtils.escapeHtml4(StringUtils.defaultString((val == null || "".equals(val)) ? "" : ((I18nSet) val).getValue(LocaleManager.currentLang())))%></textarea>
                                                                                    </div>
                                                                                </td>
                                                                            </tr>
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
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
<%} catch (Throwable t) {
    System.out.println("Error showing I18nSet input " + t);
    t.printStackTrace();
}%>
