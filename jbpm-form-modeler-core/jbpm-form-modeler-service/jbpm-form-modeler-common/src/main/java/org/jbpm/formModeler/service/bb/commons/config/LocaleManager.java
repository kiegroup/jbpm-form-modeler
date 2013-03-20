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
package org.jbpm.formModeler.service.bb.commons.config;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Manager that holds the list of languages supported.
 */
public class LocaleManager extends BasicFactoryElement {

    public static LocaleManager lookup() {
        return (LocaleManager) Factory.lookup("org.jbpm.formModeler.service.LocaleManager");
    }

    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LocaleManager.class.getName());

    private String[] installedLocaleIds = {"es", "en", "ca"};
    private String defaultLocaleId = "es";

    private Locale[] availableLocales;
    private Locale currentLocale;
    private Locale currentEditLocale;
    private Locale defaultLocale;

    /**
     * The list of locales supported.
     * return The list of locales supported.
     */
    public String[] getInstalledLocaleIds() {
        return installedLocaleIds;
    }

    /**
     * The list of locales supported.
     *
     * @param installedLocales The list of locales supported.
     */
    public void setInstalledLocaleIds(String[] installedLocales) {
        this.installedLocaleIds = installedLocales;
        List availableLocalesList = new ArrayList();
        for (int i = 0; i < installedLocales.length; i++) {
            Locale locale = getLocaleById(installedLocales[i]);
            if (locale != null)
                availableLocalesList.add(locale);
        }
        availableLocales = (Locale[]) availableLocalesList.toArray(new Locale[availableLocalesList.size()]);
    }

    /**
     * The default localeId for the platform installation
     *
     * @return The default localeId for the platform installation
     */
    public String getDefaultLocaleId() {
        return defaultLocaleId;
    }

    /**
     * The default localeId for the platform installation
     *
     * @param defaultLocale The default localeId for the platform installation
     */
    public void setDefaultLocaleId(String defaultLocale) {
        this.defaultLocaleId = defaultLocale;
        this.defaultLocale = getLocaleById(defaultLocale);
    }

    /**
     * Get a Locale by its id.
     *
     * @param localeId
     * @return a Locale whose toString() equals given localeId, or null if it doesn't exist
     */
    public Locale getLocaleById(String localeId) {
        Locale[] allLocales = getAllLocales();
        for (int i = 0; i < allLocales.length; i++) {
            Locale locale = allLocales[i];
            if (locale.toString().equals(localeId))
                return locale;
        }
        return null;
    }

    // Bussiness methods

    /**
     * Locales supported by the VM
     *
     * @return Locales supported by the VM
     */
    public Locale[] getAllLocales() {
        return Locale.getAvailableLocales();
    }

    /**
     * Locales supported by the formModeler service
     *
     * @return Locales supported by the formModeler service
     */
    public Locale[] getPlatformAvailableLocales() {
        return availableLocales;
    }

    /**
     * Locales supported by the formModeler service
     *
     * @return Locales supported by the formModeler service
     */
    public Locale[] geLocales() {
        return getPlatformAvailableLocales();
    }

    /**
     * Current locale for editing contents
     *
     * @return Current locale for editing contents
     */
    public Locale getCurrentEditLocale() {
        return currentEditLocale == null ? defaultLocale : currentEditLocale;
    }

    public void setCurrentEditLocale(Locale currentEditLocale) {
        this.currentEditLocale = currentEditLocale;
    }

    /**
     * Current locale for viewing contents
     *
     * @return Current locale for viewing contents
     */
    public Locale getCurrentLocale() {
        return currentLocale == null ? defaultLocale : currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    /**
     * Default locale for the application
     *
     * @return Default locale for the application
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    //Language methods

    protected String[] localeToString(Locale[] locales) {
        List langs = new ArrayList();
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];
            String s = locale.toString();
            langs.add(s);
        }
        return (String[]) langs.toArray(new String[langs.size()]);
    }

    /**
     * Get all language identifiers
     */
    public String[] getAllLanguages() {
        return localeToString(getAllLocales());
    }

    /**
     * Langs supported by the formModeler service
     *
     * @return Langs supported by the formModeler service
     */
    public String[] getPlatformAvailableLangs() {
        return localeToString(getPlatformAvailableLocales());
    }

    /**
     * Langs supported by the formModeler service
     *
     * @return Langs supported by the formModeler service
     */
    public String[] getLangs() {
        return getPlatformAvailableLangs();
    }

    /**
     * Get the language in which the system is editing contents.
     *
     * @return the language in which the system is editing contents.
     */
    public String getCurrentEditLang() {
        return getCurrentEditLocale().toString();
    }

    /**
     * Set the language in which the system is editing contents.
     *
     * @param langId the language in which the system is editing contents.
     */
    public void setCurrentEditLang(String langId) {
        Locale locale = getLocaleById(langId);
        if (locale != null)
            setCurrentEditLocale(locale);
        else
            log.error("Can't set edit lang to " + langId);
    }

    /**
     * Get the current language for displaying contents
     *
     * @return the current language for displaying contents
     */
    public String getCurrentLang() {
        return getCurrentLocale().toString();
    }

    /**
     * Set the current language for displaying contents
     *
     * @param langId the current language for displaying contents
     */
    public void setCurrentLang(String langId) {
        Locale locale = getLocaleById(langId);
        if (locale != null)
            setCurrentLocale(locale);
        else
            log.error("Can't set current lang to " + langId);
    }

    /**
     * Get the default language for the platform
     *
     * @return the default language for the platform
     */
    public String getDefaultLang() {
        return getDefaultLocale().toString();
    }

    /**
     * Given a map of locale->value or language->value, it returns the
     * appropiate value for the current locale. If such value doesn't exist,
     * it uses the default locale.
     *
     * @param localizedData
     * @return appropiate value for given locale.
     */
    public Object localize(Map localizedData) {
        if (localizedData == null) return null;
        String lang = getCurrentLang();

        Object data = localizedData.get(lang);
        if (data != null && (!(data instanceof String) || !"".equals(data)))
            return data;

        Locale locale = getCurrentLocale();
        data = localizedData.get(locale);
        if (null != data && (!(data instanceof String) || !"".equals(data)))
            return data;

        data = localizedData.get(getDefaultLang());
        if (null != data && (!(data instanceof String) || !"".equals(data)))
            return data;

        return localizedData.get(getDefaultLocale());
    }

    /**
     * Static getter for current Locale. Shortcut for
     * <p><code>
     * LocaleManager lManager = (LocaleManager) Factory.lookup("org.jbpm.formModeler.service.LocaleManager");<br>
     * return lManager.getCurrentLocale();
     * </code></p>
     *
     * @return current locale looked up from factory
     */
    public static Locale currentLocale() {
        LocaleManager lManager = (LocaleManager) Factory.lookup("org.jbpm.formModeler.service.LocaleManager");
        return lManager.getCurrentLocale();
    }

    /**
     * Static getter for current lang. Shortcut for
     * <p><code>
     * LocaleManager lManager = (LocaleManager) Factory.lookup("org.jbpm.formModeler.service.LocaleManager");<br>
     * return lManager.getCurrentLang();
     * </code></p>
     *
     * @return current lang looked up from factory
     */
    public static String currentLang() {
        LocaleManager lManager = (LocaleManager) Factory.lookup("org.jbpm.formModeler.service.LocaleManager");
        return lManager.getCurrentLang();
    }
}
