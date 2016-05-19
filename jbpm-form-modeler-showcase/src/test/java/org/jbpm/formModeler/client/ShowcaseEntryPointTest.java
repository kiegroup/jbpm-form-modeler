/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.formModeler.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.jbpm.formModeler.client.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.security.KieWorkbenchSecurityService;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.ConstantsAnswerMock;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ShowcaseEntryPointTest {

    @Mock
    private AppConfigService appConfigService;
    private CallerMock<AppConfigService> appConfigServiceCallerMock;

    @Mock
    private KieWorkbenchSecurityService kieSecurityService;
    private CallerMock<KieWorkbenchSecurityService> kieSecurityServiceCallerMock;

    @Mock
    private PlaceManagerActivityService pmas;
    private CallerMock<PlaceManagerActivityService> pmasCallerMock;

    @Mock
    private KieWorkbenchACL kieACL;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Mock
    private WorkbenchMenuBarPresenter menuBar;

    @Mock
    private PlaceManager placeManager;

    private ShowcaseEntryPoint showcaseEntryPoint;

    @Before
    public void setup() {
        appConfigServiceCallerMock = new CallerMock<>( appConfigService );
        kieSecurityServiceCallerMock = new CallerMock<>( kieSecurityService );
        pmasCallerMock = new CallerMock<>( pmas );

        showcaseEntryPoint = spy( new ShowcaseEntryPoint( appConfigServiceCallerMock,
                                                          kieSecurityServiceCallerMock,
                                                          pmasCallerMock,
                                                          kieACL,
                                                          activityBeansCache,
                                                          menusHelper,
                                                          menuBar,
                                                          placeManager ) );
        mockMenuHelper();
        mockConstants();
    }

    @Test
    public void setupMenuTest() {
        showcaseEntryPoint.setupMenu();

        ArgumentCaptor<Menus> menusCaptor = ArgumentCaptor.forClass( Menus.class );
        verify( menuBar ).addMenus( menusCaptor.capture() );

        Menus menus = menusCaptor.getValue();

        assertEquals( 2, menus.getItems().size() );

        assertEquals( showcaseEntryPoint.constants.home(), menus.getItems().get( 0 ).getCaption() );
        assertEquals( showcaseEntryPoint.constants.authoring(), menus.getItems().get( 1 ).getCaption() );

        verify( menusHelper ).addRolesMenuItems();
        verify( menusHelper ).addWorkbenchConfigurationMenuItem();
        verify( menusHelper ).addUtilitiesMenuItems();
    }

    @Test
    public void getAuthoringViewsTest() {
        List<? extends MenuItem> authoringMenuItems = showcaseEntryPoint.getAuthoringViews();

        assertEquals( 1, authoringMenuItems.size() );
        assertEquals( showcaseEntryPoint.constants.project_authoring(), authoringMenuItems.get( 0 ).getCaption() );
    }

    private void mockMenuHelper() {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add( mock( MenuItem.class ) );
        doReturn( menuItems ).when( menusHelper ).getPerspectivesMenuItems();

        doReturn( mock( AbstractWorkbenchPerspectiveActivity.class ) ).when( menusHelper ).getDefaultPerspectiveActivity();
    }

    private void mockConstants() {
        showcaseEntryPoint.constants = mock( Constants.class, new ConstantsAnswerMock() );
    }

}
