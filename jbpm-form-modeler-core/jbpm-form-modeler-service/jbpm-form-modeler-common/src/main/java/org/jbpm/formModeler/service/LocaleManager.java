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
package org.jbpm.formModeler.service;

import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SessionScoped
@Named("localeManager")
public class LocaleManager implements Serializable {

    public static LocaleManager lookup() {
        return (LocaleManager) CDIBeanLocator.getBeanByName("localeManager");
    }

    protected Logger log = LoggerFactory.getLogger(LocaleManager.class);

    /**
     * The list of locales supported.
     */
    @Inject @Config("en,es,ca,fr,ja,de,pt,zh")
    protected String[] installedLocaleIds;

    /**
     * The default localeId.
     */
    @Inject @Config("en")
    protected String defaultLocaleId;

    private Locale[] availableLocales;
    private Locale currentLocale;
    private Locale currentEditLocale;
    private Locale defaultLocale;

    @PostConstruct
    public void init() {
        List availableLocalesList = new ArrayList();
        for (int i = 0; i < installedLocaleIds.length; i++) {
            Locale locale = getLocaleById(installedLocaleIds[i]);
            if (locale != null) availableLocalesList.add(locale);
        }
        availableLocales = (Locale[]) availableLocalesList.toArray(new Locale[availableLocalesList.size()]);
        defaultLocale = getLocaleById(defaultLocaleId);
    }

    public String[] getInstalledLocaleIds() {
        return installedLocaleIds;
    }

    public void setInstalledLocaleIds(String[] installedLocaleIds) {
        this.installedLocaleIds = installedLocaleIds;
    }

    public String getDefaultLocaleId() {
        return defaultLocaleId;
    }

    public void setDefaultLocaleId(String defaultLocale) {
        this.defaultLocaleId = defaultLocale;
        this.defaultLocale = getLocaleById(defaultLocale);
    }

    /**
     * Get a Locale by its id.
     *
     * @return a Locale whose toString() equals given localeId, or null if it doesn't exist
     */
    public Locale getLocaleById(String localeId) {
        Locale[] allLocales = getAllLocales();
        for (int i = 0; i < allLocales.length; i++) {
            Locale locale = allLocales[i];
            if (locale.toString().equals(localeId)) return locale;
        }
        return null;
    }

    /**
     * Locales supported by the VM
     */
    public Locale[] getAllLocales() {
        return Locale.getAvailableLocales();
    }

    /**
     * Locales supported by the platform
     */
    public Locale[] getPlatformAvailableLocales() {
        return availableLocales;
    }

    /**
     * Locales supported.
     */
    public Locale[] geLocales() {
        return getPlatformAvailableLocales();
    }

    /**
     * Current locale for viewing contents
     */
    public Locale getCurrentLocale() {
        return currentLocale == null ? defaultLocale : currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    /**
     * Default locale for the application
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    // Language methods

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
     * Langs supported.
     */
    public String[] getPlatformAvailableLangs() {
        return localeToString(getPlatformAvailableLocales());
    }

    /**
     * Langs supported.
     */
    public String[] getLangs() {
        return getPlatformAvailableLangs();
    }

    /**
     * Get the current language for displaying contents
     */
    public String getCurrentLang() {
        return getCurrentLocale().toString();
    }

    /**
     * Set the current language for displaying contents
     */
    public void setCurrentLang(String langId) {
        Locale locale = getLocaleById(langId);
        if (locale != null) setCurrentLocale(locale);
        else log.error("Can't set current lang to {}", langId);
    }

    /**
     * Get the default language for the platform
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
        String lang = getCurrentLocale().getLanguage();

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
     * Static getter for current Locale.
     */
    public static Locale currentLocale() {
        return LocaleManager.lookup().getCurrentLocale();
    }

    /**
     * Static getter for current lang.
     */
    public static String currentLang() {
        return LocaleManager.lookup().getCurrentLang();
    }
}
