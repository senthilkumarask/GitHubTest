<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<dsp:page>
  	<json:object> 
        <json:property name="error" value="${false}"/> 
        <json:property name="successURL">
         	<dsp:valueof bean="/atg/commerce/order/purchase/CartModifierFormHandler.sucessURL"/>
         </json:property> 
    </json:object>
</dsp:page>