package org.jbpm.formModeler.editor.client.type;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.formModeler.editor.client.resources.FormModelerEditorResources;
import org.jbpm.formModeler.editor.client.resources.i18n.Constants;
import org.jbpm.formModeler.editor.type.FormResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormDefinitionResourceType extends FormResourceTypeDefinition implements ClientResourceType {

    private static final Image IMAGE = new Image(FormModelerEditorResources.INSTANCE.images().typeForm());

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = Constants.INSTANCE.formResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
