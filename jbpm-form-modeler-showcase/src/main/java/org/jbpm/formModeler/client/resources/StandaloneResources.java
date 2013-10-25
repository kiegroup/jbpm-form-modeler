package org.jbpm.formModeler.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.jbpm.formModeler.client.resources.css.StandaloneCss;
import org.jbpm.formModeler.client.resources.images.StandaloneImages;

public interface StandaloneResources
        extends
        ClientBundle {

    StandaloneResources INSTANCE = GWT.create( StandaloneResources.class );

    @Source("css/Standalone.css")
    StandaloneCss CSS();

    StandaloneImages images();

}
