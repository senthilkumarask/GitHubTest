<dsp:page>
<dsp:importbean bean="atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/bbb/commerce/cart/EmailCartFormHandler"/>
<dsp:setvalue bean="EmailCartFormHandler.senderEmail" paramvalue="yourEmail"/>
<dsp:setvalue bean="EmailCartFormHandler.recipientEmail" paramvalue="email"/>
<dsp:setvalue bean="EmailCartFormHandler.message" paramvalue="message"/>
<dsp:setvalue bean="EmailCartFormHandler.send" paramvalue="SUBMIT"/>




	<json:object>
		<json:property name="error"><dsp:valueof bean="EmailCartFormHandler.formError"/></json:property>
		<json:property name="email"><dsp:valueof bean="EmailCartFormHandler.recipientEmail"/></json:property>			    
	</json:object> 


</dsp:page>