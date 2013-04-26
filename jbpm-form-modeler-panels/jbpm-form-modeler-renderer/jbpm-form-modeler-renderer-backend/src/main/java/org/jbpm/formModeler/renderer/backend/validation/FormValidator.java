package org.jbpm.formModeler.renderer.backend.validation;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.renderer.validation.FormValidationResult;

import javax.inject.Named;
import java.io.Serializable;
import java.util.Map;

@Named("formRendererValidator")
public class FormValidator implements Serializable {

    public FormValidationResult validate(Map parameterMap) {

        FormValidationResult result = new FormValidationResult();

        if (parameterMap != null) {
            validateString((String[])parameterMap.get("nom"), "Nom", result);
            validateString((String[])parameterMap.get("cognom"), "Cognom", result);
            validateInt((String[]) parameterMap.get("edat"), "Edat", result);
        }


        return result;
    }

    public boolean validateString(String value[], String name, FormValidationResult result) {
        if (ArrayUtils.isEmpty(value) || StringUtils.isEmpty(value[0])) {
            result.addError("Error: '" + name + "' buit!");
            return false;
        }
        System.out.println(name + ": " + value[0]);
        return true;
    }

    public boolean validateInt(String value[], String name, FormValidationResult result)  {
        if (validateString(value, name, result)) {
            if (!StringUtils.isNumeric(value[0])) {
                result.addError("Error: '" + name + "' ha de ser un nombre enter!");
                return false;
            }
            return true;
        }

        return false;
    }
}
