package org.jbpm.formModeler.integration;

import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.DataModelerDataHolder;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nmirasch
 * Date: 5/16/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */

@ApplicationScoped
public class DataModelerService {

    @Inject
    private org.kie.workbench.common.screens.datamodeller.service.DataModelerService dataModelerService;


    public List getDataModelObjectList(Object path){
        List dataObjectsList = new ArrayList();


        DataModelTO dataModelTO = dataModelerService.loadModel((Path)path);
        HashMap dO;
        if (dataModelTO != null && dataModelTO.getDataObjects() != null) {
            String className = "";
            for (DataObjectTO dataObjectTO : dataModelTO.getDataObjects()) {
                dO = new HashMap();
                className = dataObjectTO.getClassName();
                dO.put("optionLabel", className);
                dO.put("optionValue", className);
                dataObjectsList.add(dO);
            }
        }
        return dataObjectsList;
    }

    public DataHolder createDataHolder (Object path, String id, String className, String renderColor){
        DataModelTO dataModelTO = dataModelerService.loadModel((Path)path);
        DataObjectTO dO = dataModelTO.getDataObjectByClassName(className);

        return new DataModelerDataHolder(id, className, renderColor, dO);
    }


}
