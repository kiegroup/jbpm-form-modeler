package org.jbpm.formModeler.api.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ResizeFormcontainerEvent extends FormRenderEvent {
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
