<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<dsp:page>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<json:object> 
        <json:property name="error" value="${true}"/> 
        <json:array name="errors">
		 	<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
		       <dsp:param name="exceptions" bean="CartModifierFormHandler.formExceptions"/>
		       <dsp:oparam name="output">
		          <json:property>
		          	<dsp:valueof param="message"/>
		          </json:property>
		      </dsp:oparam>
		    </dsp:droplet>
	     </json:array>
    </json:object>
</dsp:page>