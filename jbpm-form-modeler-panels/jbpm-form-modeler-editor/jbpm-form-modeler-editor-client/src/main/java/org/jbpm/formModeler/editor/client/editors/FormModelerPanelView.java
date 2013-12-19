package org.jbpm.formModeler.editor.client.editors;

import org.uberfire.client.mvp.UberView;

public interface FormModelerPanelView extends UberView<FormModelerPanelPresenter> {
    void hideForm();

    void loadContext(String ctxUID);

    void showCanNotSaveReadOnly();
}
