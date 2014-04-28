package org.jbpm.formModeler.core.processing.fieldHandlers.plugable;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.fieldHandlers.FieldHandlerParametersReader;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Dependent
@Named("PlugableFieldHandlerFormatter")
public class PlugableFieldHandlerFormatter extends Formatter {

    private Logger log = LoggerFactory.getLogger(PlugableFieldHandlerFormatter.class);

    public static final String PARAM_MODE = "mode";
    public static final String MODE_INPUT = "input";
    public static final String MODE_SHOW = "show";

    @Inject
    private FieldHandlersManager fieldHandlersManager;

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(request);
        Field field = paramsReader.getCurrentField();

        PlugableFieldHandler fieldHandler = (PlugableFieldHandler) fieldHandlersManager.getHandler(field.getFieldType());

        if (fieldHandler == null) return;

        String inputName = paramsReader.getCurrentFieldName();
        String namespace = paramsReader.getCurrentNamespace();
        Object value = paramsReader.getCurrentFieldValue();

        String mode = (String) getParameter(PARAM_MODE);

        String htmlCode = null;


        if (MODE_INPUT.equals(mode)) {
            htmlCode =  fieldHandler.getInputHTML(value, field, inputName, namespace, paramsReader.isFieldReadonly());
        } else if (MODE_SHOW.equals(mode)) {
            htmlCode =  fieldHandler.getShowHTML(value, field, inputName, namespace);
        }

        if (!StringUtils.isEmpty(htmlCode)) {
            setAttribute("htmlCode", htmlCode);
            renderFragment("output");
        }
    }
}
