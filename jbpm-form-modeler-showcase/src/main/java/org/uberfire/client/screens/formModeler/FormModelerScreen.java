package org.uberfire.client.screens.formModeler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen(identifier = "FormModelerScreen")
public class FormModelerScreen
        extends Composite {

    interface ViewBinder
            extends
            UiBinder<Widget, FormModelerScreen> {
    }

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Form Modeler Screen";
    }

}
