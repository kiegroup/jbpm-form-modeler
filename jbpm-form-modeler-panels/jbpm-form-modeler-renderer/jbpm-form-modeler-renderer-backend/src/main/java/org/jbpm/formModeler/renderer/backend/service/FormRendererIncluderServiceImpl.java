package org.jbpm.formModeler.renderer.backend.service;

import org.jboss.errai.bus.server.annotations.Service;
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

    @Override
    public FormRenderContextTO launchTest() {

        try {
            InputStreamReader is = new InputStreamReader(this.getClass().getResourceAsStream("test/testInvoice.form"));
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
            formRenderingService.persistContext(ctxUID);
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean clearContext(String ctxUID) {
        try {
            formRenderingService.removeContext(ctxUID);
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
