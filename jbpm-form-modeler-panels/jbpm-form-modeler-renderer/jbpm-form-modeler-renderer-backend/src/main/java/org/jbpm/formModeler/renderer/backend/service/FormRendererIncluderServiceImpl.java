/**
 * Copyright (C) 2012 JBoss Inc
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
package org.jbpm.formModeler.renderer.backend.service;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.client.FormRenderContextTO;
import org.jbpm.formModeler.core.test.Invoice;
import org.jbpm.formModeler.renderer.service.FormRendererIncluderService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@ApplicationScoped
public class FormRendererIncluderServiceImpl implements FormRendererIncluderService {

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormRenderingServiceImpl formRenderingService;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Override
    public FormRenderContextTO launchTest() {

        try {
            /*
            //InputStreamReader is = new InputStreamReader(this.getClass().getResourceAsStream("test/testInvoice.form"));
            InputStreamReader isSF = new InputStreamReader(this.getClass().getResourceAsStream("test/f1.form"));
            StringBuilder sbSF =new StringBuilder();
            BufferedReader brSF = new BufferedReader(isSF);
            String readSF = brSF.readLine();

            while(readSF != null) {
                sbSF.append(readSF);
                readSF = brSF.readLine();
            }
            Form formSF = formSerializationManager.loadFormFromXML(sbSF.toString());
            FormManager formManager = FormCoreServices.lookup().getFormManager();

            */

            //InputStreamReader is = new InputStreamReader(this.getClass().getResourceAsStream("test/testInvoice.form"));
            InputStreamReader is = new InputStreamReader(this.getClass().getResourceAsStream("test/test1.form"));
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while(read != null) {
                sb.append(read);
                read = br.readLine();
            }

            sb.toString();

            Form form = formSerializationManager.loadFormFromXML(sb.toString());

            Invoice invoice = new Invoice();

            invoice.setName("Ned Stark");
            invoice.setCity("Winterfall");
            invoice.setAddress("Winterfall castle S/N");
            invoice.setZip("08870");
            invoice.setEmail("ned.stark@winteriscoming.com");
            invoice.setCreatedDate(new Date());
            invoice.setUpdatedDate(new Date());

            Map<String, Object> bindingData = new HashMap<String, Object>();
            bindingData.put("invoice", invoice);
            bindingData.put("variable1", "this is a value");
            bindingData.put("variable2", "this is another value");

            return formRenderingService.startRendering(form, bindingData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean persistContext(String ctxUID) {
        try {
            formRenderContextManager.persistContext(ctxUID);
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean clearContext(String ctxUID) {
        try {
            formRenderContextManager.removeContext(ctxUID);
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
