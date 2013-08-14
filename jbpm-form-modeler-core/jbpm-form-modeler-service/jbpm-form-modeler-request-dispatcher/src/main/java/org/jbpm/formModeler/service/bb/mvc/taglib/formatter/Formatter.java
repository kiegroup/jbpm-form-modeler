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
package org.jbpm.formModeler.service.bb.mvc.taglib.formatter;

import org.slf4j.Logger;
import org.jbpm.formModeler.service.LocaleManager;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * This is the base class for all formatters. It provides methods to render output fragments,
 * and to set attributes for the output fragments. Also, input parameters can be read.
 * <p/>
 * ALL classes extending this class MUST provide documentation on the parameters they need,
 * the fragments they render, and the attributes they pass to the fragments. The following
 * template is to be used:
 * <p/>
 * <p/>
 * This class extends Formatter to provide support for [ ... what this class does ... ].
 * <p/>
 * It expects the following input parameters:
 * <ul>
 * <li> param1. [ ... Expected type, optional or not, usage to be given ...]
 * <li> param2. [ ... Expected type, optional or not, usage to be given ...]
 * It serves the following output fragments, with given output attributes:
 * <li> fragment1. [ ... When it is served ...] It receives the following attributes:
 * <ul>
 * <li> attr1. [ ... Description ...]
 * <li> attr2. [ ... Description ...]
 * </ul>
 * <li> fragment2. [ ... When it is served ...] ...
 * </ul>
 */

public abstract class Formatter {

    private Logger log = LoggerFactory.getLogger(Formatter.class);

    private transient FormatterTag tag;
    private transient Locale currentLocale;
    private transient String currentLang;

    /**
     * Sets the parent tag. Called by the framework.
     */
    public void setTag(FormatterTag tag) {
        this.tag = tag;
    }

    protected LocaleManager getLocaleManager() {
        return LocaleManager.lookup();
    }

    protected Locale getLocale() {
        if (currentLocale != null) return currentLocale;
        return currentLocale = getLocaleManager().getCurrentLocale();
    }

    protected String getLang() {
        if (currentLang != null) return currentLang;
        return currentLang = getLocaleManager().getCurrentLang();
    }

    /**
     * Orders the processing of fragment with given name.
     *
     * @param fragmentName Name of the fragment to be rendered.
     */
    protected void renderFragment(String fragmentName) {
        if (log.isDebugEnabled()) log.debug("Rendering of fragment " + fragmentName + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getRenderFragmentInstruction(fragmentName));
    }

    protected void includePage(String pageName) {
        if (log.isDebugEnabled()) log.debug("Including of page " + pageName + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getIncludePageInstruction(pageName));
    }

    protected void writeToOut(String text) {
        if (log.isDebugEnabled()) log.debug("Writting '" + text + "' to output,  scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getWriteToOutInstruction(text));
    }

    protected void setAttributeInterpreter(FormaterTagDynamicAttributesInterpreter interpreter) {
        tag.addProcessingInstruction(ProcessingInstruction.getAddAttributesInterpreterInstruction(interpreter));
    }

    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, Object value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, value));
    }

    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, int value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, new Integer(value)));
    }

    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, byte value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, new Byte(value)));
    }

    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, long value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, new Long(value)));
    }

    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, short value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, new Short(value)));
    }

    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, boolean value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, new Boolean(value)));
    }


    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, char value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, new Character(value)));
    }

    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, float value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, new Float(value)));
    }

    /**
     * Sets a parameter for the fragment. The object set as parameter value
     * must not be changed during all the execution, otherwise the result won't be
     * as expected. That is, if this object has a method that changes its content,
     * this method must not be called by the Formatter during the service() method.
     * Otherwise, all the rendering performed by the fragmentValue tag will use
     * the *last* version of the object, the one existing after the invocation of
     * service method, which is probably not expected.
     * <p>Example, of a iterating formatter:</p>
     * <code>
     * StringBuffer sb = new StringBuffer();<br>
     * for( int i= 0; i<10; i++){<br>
     * sb.delete(0,sb.length())<br>
     * sb.append( i );<br>
     * setAttribute("index",sb);<br>
     * renderFragment("output");<br>
     * }<br>     *
     * </code>
     * will generate an output like :
     * 10 10 10 10 10 10 10 10 10 10
     * while the expected output is:
     * 0 1 2 3 4 5 6 7 8 9
     * <p/>
     * So, use objects and don't change them. This is usually easy to accomplish, by using
     * different instances, in the example above, replace sb.delete(0,sb.length()) with
     * sb = new StringBuffer();
     *
     * @param name  Name of the parameter.
     * @param value It's value. Must not be changed during all the execution.
     */
    protected void setAttribute(String name, double value) {
        if (log.isDebugEnabled()) log.debug("Setting of attribute " + name + " scheduled.");
        tag.addProcessingInstruction(ProcessingInstruction.getSetParameterInstruction(name, new Double(value)));
    }


    /**
     * Return a parameter by its name. If the parameter is not defined, returns null
     *
     * @param name Parameter name to be used.
     * @return a parameter by its name. If the parameter is not defined, returns null.
     */
    protected Object getParameter(String name) {
        return tag.getParam(name);
    }

    /**
     * Perform the required logic for this Formatter. Inside, the methods
     * setAttribute and renderFragment are intended to be used to generate the
     * output and set parameters for this output.
     * Method getParameter is intended to retrieve input parameters by name.
     * <p/>
     * Exceptions are to be catched inside the method, and not to be thrown, normally,
     * formatters could use a error fragment to be displayed when an error happens
     * in displaying. But if the error is unexpected, it can be wrapped inside a
     * FormatterException.
     *
     * @param request  user request
     * @param response response to the user
     * @throws FormatterException in case of an unexpected exception.
     */
    public abstract void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException;

    /**
     * Called after the formatter finished rendering
     *
     * @param request
     * @param response
     * @throws FormatterException
     */
    public void afterRendering(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
    }
}
