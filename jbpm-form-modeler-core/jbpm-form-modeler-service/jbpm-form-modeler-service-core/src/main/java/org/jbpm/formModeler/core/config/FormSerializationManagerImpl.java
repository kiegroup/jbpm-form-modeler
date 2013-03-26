package org.jbpm.formModeler.core.config;


import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xerces.parsers.DOMParser;
import org.jbpm.datamodeler.xml.util.XMLNode;
import org.jbpm.formModeler.api.config.FieldTypeManager;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.config.FormSerializationManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.i18n.I18nEntry;
import org.jbpm.formModeler.api.model.i18n.I18nSet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.util.*;


@ApplicationScoped
public class FormSerializationManagerImpl implements FormSerializationManager {


    public static final String NODE_FORM = "form";

    public static final String NODE_FIELD = "field";
    public static final String NODE_PROPERTY = "property";

    public static final String ATTR_ID = "id";
    public static final String ATTR_POSITION = "position";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";

    @Inject
    FormManager formManager;

    @Inject
    FieldTypeManager fieldTypeManager;


    @Override
    public String generateFormXML(Form form) {
        XMLNode rootNode = new XMLNode(NODE_FORM, null);
        return generateFormXML(form, rootNode);
    }

    @Override
    public Form loadFormFromXML(String xml) {
        try {
            if (xml == null || xml.trim().equals("")) return null;

            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new StringReader(xml)));
            Document doc = parser.getDocument();
            NodeList nodes = doc.getElementsByTagName(NODE_FORM);
            Node rootNode = nodes.item(0); // only comes a form
            return deserializeForm(rootNode);

        } catch (Exception e) {

        }
        return null;
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public Form deserializeForm(Node nodeForm) {

        try {
            if (nodeForm.getNodeName().equals(NODE_FORM)) {
                Form form = formManager.createForm("");
                NodeList childNodes = nodeForm.getChildNodes();

                form.setId(Long.valueOf(StringEscapeUtils.unescapeXml(nodeForm.getAttributes().getNamedItem(ATTR_ID).getNodeValue())));

                HashSet campos = new HashSet();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeName().equals(NODE_PROPERTY)) {
                        String propName = node.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
                        String value = StringEscapeUtils.unescapeXml(node.getAttributes().getNamedItem(ATTR_VALUE).getNodeValue());
                        if ("subject".equals(propName)) {
                            form.setSubject(value);
                        } else if ("name".equals(propName)) {
                            form.setName(value);
                        } else if ("displayMode".equals(propName)) {
                            form.setDisplayMode(value);
                        } else if ("labelMode".equals(propName)) {
                            form.setLabelMode(value);
                        } else if ("showMode".equals(propName)) {
                            form.setShowMode(value);
                        } else if ("status".equals(propName)) {
                            form.setStatus(Long.valueOf(value));
                        } else if ("formTemplate".equals(propName)) {
                            form.setFormTemplate(value);
                        }
                    } else if (node.getNodeName().equals(NODE_FIELD)) {
                        Field field = deserializeField(node);
                        field.setForm(form);
                        campos.add(field);
                    }
                }
                if (campos != null)
                    form.setFormFields(campos);
                return form;
            }
        } catch (Exception e) {
            System.out.println("error " + e);
        }
        return null;

    }


    private void addXMLNode(String propName, String value, XMLNode parent) {
        if (value != null) {
            XMLNode propertyNode = new XMLNode(NODE_PROPERTY, parent);
            propertyNode.addAttribute(ATTR_NAME, propName);
            propertyNode.addAttribute(ATTR_VALUE, value);
            parent.addChild(propertyNode);
        }
    }


    /*
    Generates the xml representation and mount in rootNode the structure to be included
    Fills the XMLNode structure with the form representation and returns the string
    */
    public String generateFormXML(Form form, XMLNode rootNode) {
        try {
            rootNode.addAttribute(ATTR_ID, form.getId().toString());

            addXMLNode("subject", form.getSubject(), rootNode);
            addXMLNode("name", form.getName(), rootNode);
            addXMLNode("displayMode", form.getDisplayMode(), rootNode);
            addXMLNode("labelMode", form.getLabelMode(), rootNode);
            addXMLNode("showMode", form.getShowMode(), rootNode);
            addXMLNode("status", (form.getStatus() != null ? String.valueOf(form.getStatus()) : null), rootNode);
            addXMLNode("formTemplate", form.getFormTemplate(), rootNode);

            Set fields = form.getFormFields();
            if (fields.size() > 0) {
                for (Iterator it = fields.iterator(); it.hasNext(); ) {
                    Field field = (Field) it.next();
                    generateFieldXML(field, rootNode);
                }
            }
            StringWriter sw = new StringWriter();
            rootNode.writeXML(sw, true);
            return sw.toString();
        } catch (Exception e) {

        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Field deserializeField(Node nodeField) {

        try {
            if (nodeField.getNodeName().equals(NODE_FIELD)) {

                Field field = new Field();
                field.setId(Long.valueOf(nodeField.getAttributes().getNamedItem(ATTR_ID).getNodeValue()));
                field.setFieldName(nodeField.getAttributes().getNamedItem(ATTR_NAME).getNodeValue());
                field.setPosition(Integer.parseInt(nodeField.getAttributes().getNamedItem(ATTR_POSITION).getNodeValue()));
                field.setFieldType(fieldTypeManager.getTypeByCode(nodeField.getAttributes().getNamedItem(ATTR_TYPE).getNodeValue()));

                NodeList fieldPropsNodes = nodeField.getChildNodes();
                for (int j = 0; j < fieldPropsNodes.getLength(); j++) {
                    Node nodeFieldProp = fieldPropsNodes.item(j);
                    if (nodeFieldProp.getNodeName().equals(NODE_PROPERTY)) {
                        String propName = nodeFieldProp.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
                        String value = StringEscapeUtils.unescapeXml(nodeFieldProp.getAttributes().getNamedItem(ATTR_VALUE).getNodeValue());
                        if (propName != null && value != null) {
                            if ("fieldName".equals(propName)) {
                                field.setFieldName(value);
                            } else if ("fieldRequired".equals(propName)) {
                                field.setFieldRequired(Boolean.valueOf(value));
                            } else if ("groupWithPrevious".equals(propName)) {
                                field.setGroupWithPrevious(Boolean.valueOf(value));
                            } else if ("height".equals(propName)) {
                                field.setHeight(value);
                            } else if ("labelCSSClass".equals(propName)) {
                                field.setLabelCSSClass(value);
                            } else if ("labelCSSStyle".equals(propName)) {
                                field.setLabelCSSStyle(value);
                            } else if ("label".equals(propName)) {
                                field.setLabel(deserializeI18nEntrySet(value));
                            } else if ("errorMessage".equals(propName)) {
                                field.setErrorMessage(deserializeI18nEntrySet(value));
                            } else if ("title".equals(propName)) {
                                field.setTitle(deserializeI18nEntrySet(value));
                            } else if ("disabled".equals(propName)) {
                                field.setDisabled(Boolean.valueOf(value));
                            } else if ("readonly".equals(propName)) {
                                field.setReadonly(Boolean.valueOf(value));
                            } else if ("size".equals(propName)) {
                                field.setSize(value);
                            } else if ("formula".equals(propName)) {
                                field.setFormula(value);
                            } else if ("rangeFormula".equals(propName)) {
                                field.setRangeFormula(value);
                            } else if ("pattern".equals(propName)) {
                                field.setPattern(value);
                            } else if ("maxlength".equals(propName)) {
                                field.setMaxlength(Long.valueOf(value));
                            } else if ("styleclass".equals(propName)) {
                                field.setStyleclass(value);
                            } else if ("cssStyle".equals(propName)) {
                                field.setCssStyle(value);
                            } else if ("tabindex".equals(propName)) {
                                field.setTabindex(Long.valueOf(value));
                            } else if ("accesskey".equals(propName)) {
                                field.setAccesskey(value);
                            }  else if ("htmlContainer".equals(propName)) {
                                field.setHtmlContainer(value);
                            } else if ("isHTML".equals(propName)) {
                                field.setIsHTML(Boolean.valueOf(value));
                            } else if ("hideContent".equals(propName)) {
                                field.setHideContent(Boolean.valueOf(value));
                            }  else if ("defaultValueFormula".equals(propName)) {
                                field.setDefaultValueFormula(value);
                            }
                        }
                    }
                }
                return field;
            }
        } catch (Exception e) {
                System.out.println("excepcion"+ e);
        }
        return null;
    }


    public String generateFieldXML(Field field, XMLNode parent) {
        try {
            XMLNode rootNode = new XMLNode(NODE_FIELD, parent);
            rootNode.addAttribute(ATTR_ID, String.valueOf(field.getId()));
            rootNode.addAttribute(ATTR_POSITION, String.valueOf(field.getPosition()));
            rootNode.addAttribute(ATTR_NAME, field.getFieldName());
            if (field.getFieldType() != null)
                rootNode.addAttribute(ATTR_TYPE, field.getFieldType().getCode());

            addXMLNode("fieldName", field.getFieldName(), rootNode);
            addXMLNode("fieldRequired", (field.getFieldRequired() != null ? String.valueOf(field.getFieldRequired()) : null), rootNode);
            addXMLNode("groupWithPrevious", (field.getGroupWithPrevious() != null ? String.valueOf(field.getGroupWithPrevious()) : null), rootNode);
            addXMLNode("height", field.getHeight(), rootNode);
            addXMLNode("labelCSSClass", field.getLabelCSSClass(), rootNode);
            addXMLNode("labelCSSStyle", field.getLabelCSSStyle(), rootNode);
            addXMLNode("label", (field.getLabel() != null ? serializeI18nSet(field.getLabel()) : null), rootNode);
            addXMLNode("errorMessage", (field.getErrorMessage() != null ? serializeI18nSet(field.getErrorMessage()) : null), rootNode);
            addXMLNode("title", (field.getTitle() != null ? serializeI18nSet(field.getTitle()) : null), rootNode);
            addXMLNode("disabled", (field.getDisabled() != null ? String.valueOf(field.getDisabled()) : null), rootNode);
            addXMLNode("readonly", (field.getReadonly() != null ? String.valueOf(field.getReadonly()) : null), rootNode);
            addXMLNode("size", field.getSize(), rootNode);
            addXMLNode("formula", field.getFormula(), rootNode);
            addXMLNode("rangeFormula", field.getRangeFormula(), rootNode);
            addXMLNode("pattern", field.getPattern(), rootNode);
            addXMLNode("maxlength", (field.getMaxlength() != null ? String.valueOf(field.getMaxlength()) : null), rootNode);
            addXMLNode("styleclass", field.getStyleclass(), rootNode);
            addXMLNode("cssStyle", field.getCssStyle(), rootNode);
            addXMLNode("tabindex", (field.getTabindex() != null ? String.valueOf(field.getTabindex()) : null), rootNode);
            addXMLNode("accesskey", field.getAccesskey(), rootNode);
            addXMLNode("isHTML", (field.getIsHTML() != null ? String.valueOf(field.getIsHTML()) : null), rootNode);
            addXMLNode("hideContent", (field.getHideContent() != null ? String.valueOf(field.getHideContent()) : null), rootNode);
            addXMLNode("htmlContainer", field.getHtmlContainer(), rootNode);
            addXMLNode("defaultValueFormula", field.getDefaultValueFormula(), rootNode);

            parent.addChild(rootNode);

            StringWriter sw = new StringWriter();
            rootNode.writeXML(sw, true);
            return sw.toString();
        } catch (Exception e) {

        }
        return null;
    }

    protected String[] decodeStringArray(String textValue) {
        if (textValue == null || textValue.trim().length() == 0) return new String[0];
        String[] lista;
        lista = textValue.split("quot;");
        return lista;
    }

    protected String encodeStringArray(String[] value) {
        String cad = "";
        for (int i = 0; i < value.length; i++) {
            cad += "quot;" + value[i] + "quot;";
            i++;
            cad += ",quot;" + value[i] + "quot;";
        }
        return cad;
    }

    public String serializeI18nSet(I18nSet i18nSet) {
        String[] values = new String[i18nSet.entrySet().size() * 2];
        int i = 0;
        for (Iterator it = i18nSet.entrySet().iterator(); it.hasNext(); ) {
            I18nEntry entry = (I18nEntry) it.next();
            values[i] = entry.getLang();
            i++;
            values[i] = (String) entry.getValue();
            i++;
        }
        return encodeStringArray(values);
    }

    public I18nSet deserializeI18nEntrySet(String cadena) {
        String[] values = decodeStringArray(cadena);
        Map mapValues = new HashMap();
        for (int i = 0; i < values.length;i=i+4) {
            String key = values[i + 1];
            String value="";
            if( i+3 < values.length){
                value = values[i + 3];
            }
            if(key.length()==2){
                mapValues.put(key, value);
            }

        }
        return new I18nSet(mapValues);

    }

}
