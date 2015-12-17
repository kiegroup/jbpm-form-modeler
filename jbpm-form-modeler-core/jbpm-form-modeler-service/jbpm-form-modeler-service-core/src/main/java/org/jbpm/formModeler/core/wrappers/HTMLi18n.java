/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.formModeler.core.wrappers;

import java.util.Map;
import java.util.Set;

import org.jbpm.formModeler.api.model.wrappers.I18nSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for Multilanguage HTML field type.
 */
public class HTMLi18n extends I18nSet {
    private static transient Logger log = LoggerFactory.getLogger(HTMLi18n.class);

    public HTMLi18n(Map m) {
        super(m);
    }

    public HTMLi18n(Set s) {
        super(s);
    }

    public HTMLi18n() {
        super();
    }
}
