<dsp:page>
<dsp:importbean bean="atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/bbb/commerce/cart/EmailSavedItemsFormHandler"/>
<dsp:setvalue bean="EmailSavedItemsFormHandler.senderEmail" paramvalue="yourEmail"/>
<dsp:setvalue bean="EmailSavedItemsFormHandler.recipientEmail" paramvalue="email"/>
<dsp:setvalue bean="EmailSavedItemsFormHandler.message" paramvalue="message"/>
<dsp:setvalue bean="EmailSavedItemsFormHandler.send" paramvalue="SUBMIT"/>
	<json:object>
		<json:property name="error"><dsp:valueof bean="EmailSavedItemsFormHandler.formError"/></json:property>
		<json:property name="email"><dsp:valueof bean="EmailSavedItemsFormHandler.recipientEmail"/></json:property>			    
	</json:object> 


</dsp:page>