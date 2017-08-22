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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.formModeler.client.i18n.Constants;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.HomeModelProvider;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

@ApplicationScoped
public class HomeProducer implements HomeModelProvider {

    private Constants constants = Constants.INSTANCE;

    @Inject
    private PlaceManager placeManager;

    public HomeModel get() {
        final HomeModel model = new HomeModel("jBPM Form Modeler",
                                              constants.homeTitle(),
                                              "images/home_bg.jpg");
        model.addShortcut(ModelUtils.makeShortcut("pficon pficon-blueprint",
                                                  constants.design(),
                                                  constants.designTitle(),
                                                  () -> placeManager.goTo(LIBRARY),
                                                  LIBRARY,
                                                  PERSPECTIVE));

        return model;
    }
}
