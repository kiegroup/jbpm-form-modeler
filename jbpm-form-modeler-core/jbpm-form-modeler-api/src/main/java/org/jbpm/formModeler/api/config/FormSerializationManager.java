package org.jbpm.formModeler.api.config;

import org.jbpm.formModeler.api.model.Form;

import java.io.Serializable;


public interface FormSerializationManager extends Serializable {

    public String generateFormXML(Form form);

    public Form loadFormFromXML(String xml);

}
