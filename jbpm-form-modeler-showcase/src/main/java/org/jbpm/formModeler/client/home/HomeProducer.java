/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.formModeler.client.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.common.screens.home.model.SectionEntry;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        final String url = GWT.getModuleBaseURL();
        model = new HomeModel( "Welcome to the jBPM Form Modeler" );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Model",
                                                              "Create your persistable models",
                                                              url + "/images/HandHome.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Desing",
                                                              "Design your forms",
                                                              url + "/images/HandHome.jpg" ) );

        final SectionEntry s1 = ModelUtils.makeSectionEntry( "Discover and Author:" );
        s1.addChild( ModelUtils.makeSectionEntry( "Author",
                () -> placeManager.goTo( PerspectiveIds.AUTHORING ),
                PerspectiveIds.AUTHORING, ActivityResourceType.PERSPECTIVE) );

        model.addSection( s1 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }
}
