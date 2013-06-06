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
package org.jbpm.formModeler.api.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.formModeler.api.client.FormRenderContextTO;

import java.io.Serializable;

@Portable
public class FormRenderEvent implements Serializable {
    private FormRenderContextTO context;

    public FormRenderEvent() {
    }

    public FormRenderEvent(FormRenderContextTO context) {
        this.context = context;
    }

    public FormRenderContextTO getContext() {
        return context;
    }

    public void setContext(FormRenderContextTO context) {
        this.context = context;
    }

    public boolean isMine(FormRenderContextTO myContext) {
        if (myContext == null) return false;
        return isMine(myContext.getCtxUID());
    }

    public boolean isMine(String ctxUID) {
        return context.getCtxUID().equals(ctxUID);
    }
}
