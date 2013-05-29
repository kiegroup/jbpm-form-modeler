package org.jbpm.formModeler.core.config;

import org.jbpm.formModeler.core.xml.util.XMLNode;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.wrappers.I18nSet;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: nmirasch
 * Date: 3/14/13
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
@ApplicationScoped
public class TestFormSerialization {

    FormSerializationManagerImpl formSerializationManager;

    public TestFormSerialization() {
        this.formSerializationManager = new FormSerializationManagerImpl();
    }

    public boolean saveFormToLocalDrive(Form form) {
        try {

            XMLNode rootNode = new XMLNode(FormSerializationManagerImpl.NODE_FORM, null);

            formSerializationManager.generateFormXML(form, rootNode);

            String fileName = "default";
            if (form != null) fileName = form.getName() + ".form";

            OutputStreamWriter wos = new OutputStreamWriter(new FileOutputStream(fileName));
            wos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            rootNode.writeXML(wos, true);
            wos.close();

            return true;
        } catch (Exception e) {
            System.out.println("saveFormToLocalDrive "+ e);
        }
        return false;
    }


    public Form readFormFromLocalDrive(String formId) {

        String fileName = "default";
        if (formId != null) fileName = "form_" + formId + ".form";

        File f = null;
        FileReader fr = null;
        BufferedReader br = null;
        String xml = "";
        try {
            f = new File("/home/nmirasch/tmpFormFiles/" + fileName);
            fr = new FileReader(f);
            br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) xml += linea;
            Form form = formSerializationManager.loadFormFromXML(xml);

            return form;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;

    }



    public Form createTestForm() {
        try {
            Form form = new Form();
            //form.setCreationView(true);
            form.setId(Long.valueOf("3"));
            form.setName("Form3");
            form.setFormTemplate("templateeeeeeeeeefda df a");

            Field camp = new Field();
            camp.setCssStyle("title-skn1");
            I18nSet name= new I18nSet();
            name.setValue("es","nombre1");
            name.setValue("ca","nom1");
            name.setValue("en","name1");
            camp.setLabel(name);

            camp.setPosition(0);
            camp.setId(Long.valueOf(23));
            camp.setFieldName("Descripcion1");

            Field camp2 = new Field();
            camp2.setCssStyle("title-skn");
            I18nSet name2= new I18nSet();
            name2.setValue("es","nombre2");
            name2.setValue("ca","nom2");
            name2.setValue("en","name2");
            camp2.setLabel(name2);
            I18nSet name3= new I18nSet();
            name3.setValue("es","titulo2");
            name3.setValue("ca","tittol2");
            name3.setValue("en", "title2");
            camp2.setTitle(name3);

            camp2.setPosition(1);
            camp2.setId(Long.valueOf(23));
            camp2.setFieldName("Descripcion2");

            Set campos = new TreeSet();
            campos.add(camp);
            campos.add(camp2);

            form.setFormFields(campos);

            return form;

        } catch (Exception e) {

        }
        return null;
    }

    public static void main(String[] args) {
        TestFormSerialization test = new TestFormSerialization();
        Form form = test.readFormFromLocalDrive("3");
        System.out.println("form" +form.getName());

       //Form form = test.createTestForm();
       // test.saveFormToLocalDrive(form);
    }
}
