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
package org.jbpm.formModeler.service.bb.mvc.controller.requestChain;

import org.slf4j.Logger;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.SendErrorResponse;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Analyzes the free memory available.
 */
public class FreeMemoryProcessor implements RequestChainProcessor {

    private static transient Logger log = LoggerFactory.getLogger(FreeMemoryProcessor.class.getName());

    private long minMemorySize = 1000000; // Less than 1MB means memory is running low
    private float minMemoryPercentage = 0.05f; // Less than 5% free memory means memory is running low

    public long getMinMemorySize() {
        return minMemorySize;
    }

    public void setMinMemorySize(long minMemorySize) {
        this.minMemorySize = minMemorySize;
    }

    public float getMinMemoryPercentage() {
        return minMemoryPercentage;
    }

    public void setMinMemoryPercentage(float minMemoryPercentage) {
        this.minMemoryPercentage = minMemoryPercentage;
    }

    public boolean processRequest(CommandRequest request) throws Exception {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        if (isLowMemory(freeMemory, totalMemory)) {
            log.warn("Memory is running low ...");
            freeSomeMemory(freeMemory, totalMemory);
            freeMemory = Runtime.getRuntime().freeMemory();
            totalMemory = Runtime.getRuntime().totalMemory();
            if (isLowMemory(freeMemory, totalMemory)) {
                ControllerStatus.lookup().setResponse(new SendErrorResponse(503));
                ControllerStatus.lookup().consumeURIPart(ControllerStatus.lookup().getURIToBeConsumed());
                log.error("Memory is so low that a user request had to be canceled - 503 sent. Consider increasing memory for current running application.");
                return false;
            }
        }

        return true;
    }

    protected void freeSomeMemory(long freeMemory, long totalMemory) {
        System.gc(); // Simpler memory free algorythm possible
        long newFreeMemory = Runtime.getRuntime().freeMemory();
        long newTotalMemory = Runtime.getRuntime().totalMemory();
        if (isLowMemory(newFreeMemory, newTotalMemory)) {
            freeEvenMoreMemory(newFreeMemory, newTotalMemory);
        } else {
            float percentage = (float) (newFreeMemory * 100.0/ (newTotalMemory * 1.0));
            log.warn("Freed " + (newFreeMemory - freeMemory) + " bytes. Now memory is OK to go on. (" + percentage + "% free)");
        }
    }

    protected void freeEvenMoreMemory(long freeMemory, long totalMemory) {
        // Empty some caches... Drastic measure when memory is low and a System.gc was insufficient to release memory
        System.gc();
        long newFreeMemory = Runtime.getRuntime().freeMemory();
        long newTotalMemory = Runtime.getRuntime().totalMemory();
        if (!isLowMemory(newFreeMemory, newTotalMemory)) {
            float percentage = (float) (newFreeMemory / (newTotalMemory * 1.0));
            log.warn("Hibernate caches had to be deleted to free memory. Consider decreasing cache sizes, or increasing memory for current application. Freed " + (newFreeMemory - freeMemory) + " bytes. Now memory us OK to go on. (" + percentage + "% free)");
        }
    }

    protected boolean isLowMemory(long freeMemory, long totalMemory) {
        if (freeMemory < minMemorySize) return true;
        float percentage = (float) (freeMemory / (totalMemory * 1.0));
        if (percentage < minMemoryPercentage) return true;
        return false;
    }
}
