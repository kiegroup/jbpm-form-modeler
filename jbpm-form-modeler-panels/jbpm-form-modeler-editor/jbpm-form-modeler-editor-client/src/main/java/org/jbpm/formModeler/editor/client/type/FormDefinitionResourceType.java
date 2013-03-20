package org.jbpm.formModeler.editor.client.type;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.formModeler.editor.type.FormResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormDefinitionResourceType extends FormResourceTypeDefinition implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }

}
