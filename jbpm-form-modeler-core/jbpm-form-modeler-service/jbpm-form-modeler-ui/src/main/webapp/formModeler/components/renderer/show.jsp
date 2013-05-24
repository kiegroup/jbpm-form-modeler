<%@ page import="org.jbpm.formModeler.api.model.Form" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<mvc:formatter name="org.jbpm.formModeler.components.renderer.FormRenderingComponentFormatter">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="ctxUID" id="ctxUID">
        <mvc:fragmentValue name="form" id="form">
        <mvc:fragmentValue name="errors" id="errors">
        <mvc:fragmentValue name="submitted" id="submitted">
<form action="<factory:formUrl/>" method="post" id="formRendering<%=ctxUID%>">
    <factory:handler action="submitForm"/>
    <input type="hidden" name="ctxUID" id="ctxUID" value="<%=ctxUID%>"/>
    <input type="hidden" id="persist_<%=ctxUID%>" name="persistForm" value="false"/>
    <mvc:formatter name="org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter">
        <mvc:formatterParam name="form" value="<%=form%>"/>
        <mvc:formatterParam name="renderMode" value="<%=Form.RENDER_MODE_FORM%>"/>
        <mvc:formatterParam name="namespace" value="<%=ctxUID%>"/>
        <mvc:formatterParam name="reuseStatus" value="true"/>
        <%@ include file="/formModeler/defaultFormRenderingFormatterOptions.jsp" %>
    </mvc:formatter>
</form>
<script type="text/javascript"defer="defer">
    setAjax("formRendering<%=ctxUID%>");
</script>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>