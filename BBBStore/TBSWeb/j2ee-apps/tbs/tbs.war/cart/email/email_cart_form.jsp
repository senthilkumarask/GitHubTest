<dsp:page>
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:importbean bean="/com/bbb/commerce/cart/EmailCartFormHandler"/>	
		
		<dsp:form action="${originatingRequest.requestURI}"
        		  method="post">
        		  
        
	    <!--  <dsp:setvalue bean="EmailCartFormHandler.successURL"  value="/tbs/cart/email/response_email_cart_json.jsp"/>
	    <dsp:setvalue bean="EmailCartFormHandler.errorURL" value="/tbs/cart/email/response_email_cart_json.jsp"/>-->
	     <dsp:input bean="EmailCartFormHandler.fromPage" type="hidden" value="emailcartform" />
	    <dsp:setvalue bean="EmailCartFormHandler.cancelURL" value="${originatingRequest.requestURI}"/>
	    <dsp:setvalue bean="EmailCartFormHandler.recipientEmail" paramvalue="email" />						
		<dsp:setvalue bean="EmailCartFormHandler.send" value="submit"/>
			
	    
        </dsp:form>		

</dsp:page>