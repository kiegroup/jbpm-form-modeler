package org.jbpm.formModeler.renderer.backend.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.formModeler.api.Invoice;
import org.jbpm.formModeler.api.config.FormSerializationManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;
import org.jbpm.formModeler.renderer.service.FormRendererIncluderService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
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

            return formRenderingService.startRendering(form, bindingData, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
