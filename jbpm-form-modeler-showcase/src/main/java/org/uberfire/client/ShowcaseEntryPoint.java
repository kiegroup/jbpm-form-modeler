/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.*;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

import javax.inject.Inject;
import java.util.*;

import static org.uberfire.client.workbench.widgets.menu.MenuFactory.newTopLevelMenu;

/**
 * GWT's Entry-point for Uberfire-showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private IOCBeanManager manager;

    @Inject
    private WorkbenchMenuBar menubar;

    /*
    @Inject
    private WorkbenchMenuBarPresenter menubar;
    */

//    @Inject
//    private Caller<FileExplorerRootService> rootService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private IOCBeanManager iocManager;

    @Inject
    private ActivityManager activityManager;

    @AfterInitialization
    public void startApp() {
        setupMenu();
        hideLoadingPopup();
    }

    private void setupMenu() {

        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();

        final Menus menus =
                newTopLevelMenu( "Project navigation" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                if ( defaultPerspective != null ) {
                                    placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                                } else {
                                    Window.alert("Default perspective not found.");
                                }
                            }
                        } )
                        .endMenu().newTopLevelMenu( "File navigation" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo( new DefaultPlaceRequest( "FileExplorerPerspective" ) );

                            }
                        } )
                        .endMenu()
                        .build();

        menubar.aggregateWorkbenchMenus( menus );
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractWorkbenchPerspectiveActivity.class );
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop:
        while ( perspectivesIterator.hasNext() ) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

    private List<AbstractWorkbenchPerspectiveActivity> getPerspectiveActivities() {

        //Get Perspective Providers
        final Set<AbstractWorkbenchPerspectiveActivity> activities = activityManager.getActivities( AbstractWorkbenchPerspectiveActivity.class );

        //Sort Perspective Providers so they're always in the same sequence!
        List<AbstractWorkbenchPerspectiveActivity> sortedActivities = new ArrayList<AbstractWorkbenchPerspectiveActivity>( activities );
        Collections.sort( sortedActivities,
                new Comparator<AbstractWorkbenchPerspectiveActivity>() {

                    @Override
                    public int compare( AbstractWorkbenchPerspectiveActivity o1,
                                        AbstractWorkbenchPerspectiveActivity o2 ) {
                        return o1.getPerspective().getName().compareTo( o2.getPerspective().getName() );
                    }

                } );

        return sortedActivities;
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

}