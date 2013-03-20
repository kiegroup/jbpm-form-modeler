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
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.jbpm.formModeler.core.wrappers.HTMLi18n"%>
<%@ page import="org.jbpm.formModeler.core.processing.fieldHandlers.HTMLi18nFieldHandler"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.jbpm.formModeler.service.bb.commons.config.LocaleManager"%>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%try{%>
<mvc:formatter name="org.jbpm.formModeler.core.processing.fieldHandlers.SimpleFieldHandlerFormatter">
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
                                    <mvc:fragmentValue name="cssStyle" id="cssStyle">
                                        <mvc:fragmentValue name="disabled" id="disabled">
                                            <mvc:fragmentValue name="height" id="height">
                                              <mvc:fragmentValue name="readonly" id="readonly">
                                                 <mvc:fragmentValue name="lang" id="languageForEditor">
                                        <table border="0" cellpadding="0" cellspacing="0" >
                                            <tr valign="top">
                                                <td>
                                                        <input type="file" style="display:none" name="<%=name%>_fakeFile">
                                                        <mvc:formatter name="org.jbpm.formModeler.service.mvc.formatters.ForFormatter">
                                                            <mvc:formatterParam name="factoryElement" value="org.jbpm.formModeler.service.LocaleManager"/>
                                                            <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                                                            <mvc:fragment name="outputStart">
                                                                <input type="hidden" id="<%=uid+HTMLi18nFieldHandler.LANGUAGE_INPUT_NAME%>" name="<%=name+"_"+HTMLi18nFieldHandler.LANGUAGE_INPUT_NAME%>">
                                                                <select name="selectChangeLanguage"   id="<%=uid%>_selectLang"
                                                                class="dynInputStyle <%=StringUtils.defaultString((String) styleclass, "skn-input")%>"
                                                                <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                onchange="var selectedOption =this.options[this.selectedIndex];
                                                                var textEditorName='<%=uid%>';
                                                                var oEditor;
                                                                // Fixed bug 3482. When subform disabled or readonly FCKEditorAPI does not exist.
                                                                try {
                                                                    oEditor = FCKeditorAPI.GetInstance(textEditorName) ;
                                                                } catch (e) { }
                                                                var inputi18n='<%=uid%>_'+document.getElementById('<%=uid+HTMLi18nFieldHandler.LANGUAGE_INPUT_NAME%>').value;
                                                                var newinput81in='<%=uid%>_'+selectedOption.value;
                                                                var divInput='<%=uid+HTMLi18nFieldHandler.DIV_INPUT_NAME_PREFFIX%>';
                                                                /*alert('Editor = '+document.getElementById(textEditor + '___Frame'));*/
                                                                if (!oEditor)
                                                                    document.getElementById(divInput).innerHTML=document.getElementById(newinput81in).value;
                                                                else {
                                                                    document.getElementById(inputi18n).value = oEditor.GetHTML();
                                                                    oEditor.SetHTML(document.getElementById(newinput81in).value);
                                                                }
                                                                document.getElementById('<%=uid+ HTMLi18nFieldHandler.LANGUAGE_INPUT_NAME%>').value=this.options[this.selectedIndex].value;
                                                                ">

                                                            </mvc:fragment>
                                                            <mvc:fragment name="output">
                                                                <mvc:fragmentValue name="index" id="index">
                                                                    <mvc:fragmentValue name="element" id="locale">
                                                                        <%
                                                                            String selected;
                                                                            //if (((Integer) index).intValue() == 0) selected = "selected";
                                                                            if (((Locale) locale).getLanguage().equals(LocaleManager.currentLang())) selected = "selected";
                                                                            else selected="";
                                                                        %>
                                                                        <option <%=selected%>
                                                                                value="<%=((Locale)locale).toString()%>">
                                                                            <%=StringUtils.capitalize(((Locale)locale).getDisplayName((Locale)locale))%>
                                                                        </option>
                                                                        <% if ("selected".equals(selected)) {%>
                                                                        <script type="text/javascript" defer="true">
                                                                            setTimeout("document.getElementById('<%=uid+HTMLi18nFieldHandler.LANGUAGE_INPUT_NAME%>').value='<mvc:fragmentValue name="element"/>'",10);
                                                                        </script>
                                                                        <% } %>
                                                                    </mvc:fragmentValue>
                                                                </mvc:fragmentValue>
                                                            </mvc:fragment>
                                                            <mvc:fragment name="outputEnd">
                                                                </select>
                                                            </mvc:fragment>
                                                        </mvc:formatter>

                                                        <mvc:formatter name="org.jbpm.formModeler.service.mvc.formatters.ForFormatter">
                                                              <mvc:formatterParam name="factoryElement"
                                                                                value="org.jbpm.formModeler.service.LocaleManager"/>
                                                            <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                                                            <mvc:fragment name="output">
                                                                <mvc:fragmentValue name="index" id="index">
                                                                    <mvc:fragmentValue name="element"
                                                                                       id="locale">
                                                                                 <%
                                                                                   //if (((Integer) index).intValue() == 0 ) {
                                                                                     if (((Locale) locale).getLanguage().equals(LocaleManager.currentLang())) {
                                                                                %>
                                                                                <%
                                                                                  readonly= readonly==null ? Boolean.FALSE : readonly;
                                                                                 disabled= disabled==null ? Boolean.FALSE : disabled;
                                                                                  %>
                                                                                <%if ((readonly != null && !((Boolean) readonly).booleanValue()) && ((disabled != null) && !(((Boolean) disabled).booleanValue()))) { %>
                                                                                    <div style="width:<%=size!=null?size:"250"%>px; height:<%=height!=null?height:"170"%>px; <%=StringUtils.defaultString((String) cssStyle)%>"  id="<%=uid%>_containerdiv"
                                                                                         class="dynInputStyle">
                                                                                        <textarea id="<%=uid%>"  name="<%=name%>"
                                                                                                 rows="4" cols="50"
                                                                                                <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                                                class="skn-input"
                                                                                                <%=maxlength != null ? " maxlength=\"" + maxlength + "\"" : ""%>
                                                                                                <%=tabindex != null ? " tabindex=\"" + tabindex + "\"" : ""%>
                                                                                                <%=accesskey != null ? " accesskey=\"" + accesskey + "\"" : ""%>
                                                                                                <%=altvalue != null ? " alt=\"" + altvalue + "\"" : ""%>
                                                                                                <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                                                <%=readonly != null && ((Boolean) readonly).booleanValue() ? " readonly " : ""%>
                                                                                                <%=disabled != null && ((Boolean) disabled).booleanValue() ? " disabled " : ""%>><%=StringEscapeUtils.escapeHtml(val == null ? "" : StringUtils.defaultString(((HTMLi18n)val).getValue(((Locale)locale).toString())))%></textarea>
                                                                                    </div>
                                                                                <% } else {%>

                                                                                    <div id="<%=uid+HTMLi18nFieldHandler.DIV_INPUT_NAME_PREFFIX%>" style="width:<%=size!=null?size:"250"%>px; height:<%=height!=null?height:"170"%>px"
                                                                                        class="dynInputStyle <%=StringUtils.defaultString((String) styleclass)%>"
                                                                                        <%=cssStyle != null ? " style=\"" + cssStyle + "\"" : ""%>
                                                                                        <%=title != null ? ("title=\"" + title + "\"") : ""%>
                                                                                        >
                                                                                            <%=StringUtils.defaultString(val == null ? "" : ((HTMLi18n)val).getValue(((Locale)locale).toString()))%>
                                                                                    </div>
                                                                                    <input type="hidden" name="<%=name%>" value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(val == null ? "" : ((HTMLi18n)val).getValue(((Locale)locale).toString())))%>"/>
                                                                                <% } %>
                                                                                <%if ((readonly!=null && !((Boolean)readonly).booleanValue()) && ((disabled!=null)&& !(((Boolean)disabled).booleanValue()))) { %>

                                                                        <script language="Javascript" defer="true">
                                                                            setTimeout('replaceDDMTextArea("<%=uid%>", "<%=size!=null?size:"250"%>","<%=height!=null?height:"170"%>", "<%=languageForEditor%>" )',10);
                                                                        </script>

                                                                            <% } %> <% } %>
                                                                            <input id="<%=uid%>_<%=((Locale)locale).toString()%>"  name="<%=name%>_<%=((Locale)locale).toString()%>" style="display:none"
                                                                            onchange="processFormInputChange(this)"
                                                                            value="<%=StringEscapeUtils.escapeHtml(StringUtils.defaultString(
                                                                                  (val==null || "".equals(val))?"":
                                                                               ((HTMLi18n)val).getValue(((Locale)locale).toString()))
                                                                               )%>">
                                                                        </mvc:fragmentValue>
                                                                </mvc:fragmentValue>
                                                            </mvc:fragment>
                                                        </mvc:formatter>
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
</mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){System.out.println("Error showing HTMLi18n input "+t);t.printStackTrace();}%>
