package org.jbpm.formModeler.editor.client.editors;

import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface FormModelerPanelView extends KieEditorView {
    void loadContext(String ctxUID);

    void showCanNotSaveReadOnly();
}
