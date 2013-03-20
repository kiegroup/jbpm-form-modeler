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
package org.jbpm.formModeler.core.wrappers;

/**
 * Wrapper class for Link field types
 */
public class Link implements Comparable {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Link.class.getName());

    private String link;
    private String name;

    public Link() {
    }

    public Link(String link, String name) {
        this.link = link;
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Object object) {
        if (object == null) return 1;
        String oname = ((Link) object).getName();
        String olink = ((Link) object).getLink();

        if (oname == null || this.name == null) {
            if (olink == null)
                return this.link == null ? 0 : 1;
            else
                return this.link == null ? -1 : this.link.compareTo(olink);


        } else {

            if (this.name == null)
                return -1;
            if (oname == null)
                return 1;

            if (!oname.equals(this.name))
                return this.name.compareTo(oname);

            if (olink == null) {
                return this.link == null ? 0 : 1;
            } else {
                return this.link == null ? -1 : this.link.compareTo(olink);
            }


        }


    }


}
