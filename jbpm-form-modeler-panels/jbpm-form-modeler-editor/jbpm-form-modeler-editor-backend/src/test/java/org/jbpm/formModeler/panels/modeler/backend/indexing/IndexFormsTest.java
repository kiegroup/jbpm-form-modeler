/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.jbpm.formModeler.panels.modeler.backend.indexing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.enterprise.inject.Instance;

import org.apache.lucene.search.Query;
import org.jbpm.examples.purchases.PurchaseOrder;
import org.jbpm.examples.purchases.PurchaseOrderHeader;
import org.jbpm.examples.purchases.PurchaseOrderLine;
import org.jbpm.formModeler.core.config.DataHolderManagerImpl;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.FieldTypeManagerImpl;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.core.config.FormSerializationManagerImpl;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuilder;
import org.jbpm.formModeler.core.config.builders.dataHolder.PojoDataHolderBuilder;
import org.jbpm.formModeler.core.config.builders.fieldType.ComplexFieldTypeBuilder;
import org.jbpm.formModeler.core.config.builders.fieldType.DecoratorFieldTypeBuilder;
import org.jbpm.formModeler.core.config.builders.fieldType.SimpleFieldTypeBuilder;
import org.jbpm.formModeler.core.model.PojoDataHolder;
import org.jbpm.formModeler.dataModeler.integration.DataModelerService;
import org.jbpm.formModeler.editor.type.FormResourceTypeDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.backend.server.DataModelTestUtil;
import org.kie.workbench.common.screens.datamodeller.backend.server.DataModelerServiceImpl;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = PojoDataHolder.class)
public class IndexFormsTest extends BaseIndexingTest<FormResourceTypeDefinition> {

    @SuppressWarnings("serial")
    private static final FieldTypeManager testFieldTypeManager = new FieldTypeManagerImpl() {
        {
            this.builders = new MockEmptyInstance<SimpleFieldTypeBuilder>(new SimpleFieldTypeBuilder());
            this.decoratorBuilders = new MockEmptyInstance<DecoratorFieldTypeBuilder>(new DecoratorFieldTypeBuilder());
            this.complexBuilders = new MockEmptyInstance<ComplexFieldTypeBuilder>(new ComplexFieldTypeBuilder());
        }
    };

    static {
        ((FieldTypeManagerImpl) testFieldTypeManager).init();
    }

    @Before
    public void setupStaticMockAndDataModelService() {
        PowerMockito.mockStatic(PojoDataHolder.class,
                                new Answer<FieldTypeManager>() {
                                    @Override
                                    public FieldTypeManager answer(InvocationOnMock invocation) throws Throwable {
                                        return testFieldTypeManager;
                                    }
                                });
    }

    @Test
    public void testIndexForm() throws Exception {

        String[] formFiles = {
                "CreateOrder-taskform.form",
                "FixOrder-taskform.form",
                "PurchaseHeader.form",
                "PurchaseLine.form",
                "Purchases.Purchases-taskform.form",
                "ReviewAdministration-taskform.form",
                "ReviewCFO-taskform.form",
                "ReviewController-taskform.form",
                "ReviewManager-taskform.form"
        };

        Path[] path = new Path[formFiles.length];
        for (int i = 0; i < formFiles.length; ++i) {
            path[i] = basePath.resolve(formFiles[i]);
            final String formStr = loadText(formFiles[i]);

            ioService().write(path[i],
                              formStr);
        }

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = getConfig().getIndexManager().get(org.uberfire.ext.metadata.io.KObjectUtil.toKCluster(basePath.getFileSystem()));

        // find the "PurchaseHeader.form" resource
        {
            final Query query = new SingleTermQueryBuilder(
                    new ValueResourceIndexTerm("PurchaseHeader.form",
                                               ResourceType.FORM))
                    .build();
            searchFor(index,
                      query,
                      1,
                      path[2]);
        }

        // find resources that refer to PurchaseOrderHeader (Java class)
        {
            final Query query = new SingleTermQueryBuilder(
                    new ValueReferenceIndexTerm(PurchaseOrderHeader.class.getCanonicalName(),
                                                ResourceType.JAVA))
                    .build();
            searchFor(index,
                      query,
                      7,
                      path[0],
                      // "CreateOrder-taskform.form",
                      path[1],
                      // "FixOrder-taskform.form"
                      path[2],
                      // "PurchaseHeader.form"
                      path[5],
                      // "ReviewAdministration-taskform.form"
                      path[6],
                      // "ReviewCFO-taskform.form"
                      path[7],
                      // "ReviewController-taskform.form"
                      path[8]); // "ReviewManager-taskform.form"
        }

        // find resources that refer to PurchaseOrderHeader.customer (Java class field)
        {
            final Query query = new SingleTermQueryBuilder(
                    new ValuePartReferenceIndexTerm(PurchaseOrderHeader.class.getCanonicalName(),
                                                    "customer",
                                                    PartType.FIELD))
                    .build();
            searchFor(index,
                      query,
                      1,
                      path[2]);
        }

        // find resources that refer to PurchaseOrderHeader.id (Java class field)
        {
            final Query query = new SingleTermQueryBuilder(
                    new ValuePartReferenceIndexTerm(PurchaseOrderHeader.class.getCanonicalName(),
                                                    "id",
                                                    PartType.FIELD))
                    .build();
            searchFor(index,
                      query,
                      0);
        }

        // find resources that refer to the "PurchaseHeader.form (form)
        {
            final Query query = new SingleTermQueryBuilder(
                    new ValueReferenceIndexTerm("PurchaseHeader.form",
                                                ResourceType.FORM))
                    .build();
            searchFor(index,
                      query,
                      6,
                      path[0],
                      // "CreateOrder-taskform.form",
                      path[1],
                      // "FixOrder-taskform.form"
                      path[5],
                      // "ReviewAdministration-taskform.form"
                      path[6],
                      // "ReviewCFO-taskform.form"
                      path[7],
                      // "ReviewController-taskform.form"
                      path[8]); // "ReviewManager-taskform.form"
        }
    }

    @Override
    protected TestIndexer<FormResourceTypeDefinition> getIndexer() {
        return new TestFormIndexer(new TestFormSerializationManagerImpl());
    }

    @Override
    protected FormResourceTypeDefinition getResourceTypeDefinition() {
        return new FormResourceTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

    @SuppressWarnings("serial")
    public class TestFormSerializationManagerImpl extends FormSerializationManagerImpl {

        public TestFormSerializationManagerImpl() {
            this.formManager = new FormManagerImpl();

            this.fieldTypeManager = testFieldTypeManager;

            final DataModelerService dataModelerService = spy(new DataModelerService() {
                {
                    this.ioService = ioService();
                    this.projectService = getProjectService();
                    this.dataModelerService = mock(org.kie.workbench.common.screens.datamodeller.service.DataModelerService.class);
                    when(dataModelerService.loadModel(any())).thenAnswer(new Answer<DataModel>() {

                        @Override
                        public DataModel answer(InvocationOnMock invocation) throws Throwable {
                            return new DataModelTestUtil(new DataModelerServiceImpl().getAnnotationDefinitions())
                                    .createModel(
                                            PurchaseOrder.class,
                                            PurchaseOrderHeader.class,
                                            PurchaseOrderLine.class);
                        }
                    });
                }

                @Override
                protected Class findHolderClass(String className,
                                                String path) {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        log.warn("Unable to load class '{}': {}",
                                 className,
                                 e);
                    }
                    return null;
                }
            });

            this.dataHolderManager = new DataHolderManagerImpl() {
                {
                    this.holderBuilders = new MockEmptyInstance<DataHolderBuilder>(
                            new PojoDataHolderBuilder(),
                            dataModelerService);
                }
            };
            ((DataHolderManagerImpl) this.dataHolderManager).initializeHolders();
        }
    }

    private static class MockEmptyInstance<T> implements Instance<T> {

        private ArrayList<T> instances = new ArrayList<>(1);

        @SafeVarargs
        public MockEmptyInstance(T... addInstances) {
            instances.addAll(Arrays.asList(addInstances));
        }

        @Override
        public Iterator<T> iterator() {
            return instances.iterator();
        }

        @Override
        public T get() {
            return instances.get(0);
        }

        @Override
        public Instance<T> select(Annotation... qualifiers) {
            return null;
        }

        @Override
        public <U extends T> Instance<U> select(Class<U> subtype,
                                                Annotation... qualifiers) {
            return null;
        }

        @Override
        public boolean isUnsatisfied() {
            return false;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public void destroy(T instance) {
        }
    }
}
