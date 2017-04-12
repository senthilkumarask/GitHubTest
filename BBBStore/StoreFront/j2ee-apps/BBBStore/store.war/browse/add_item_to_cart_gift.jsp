<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:getvalueof var="jasonObj" param="addItemResults" />
	<dsp:setvalue bean="CartModifierFormHandler.jsonResultString" value="${jasonObj}" />	
	<dsp:setvalue bean="CartModifierFormHandler.addRegistryItemToOrder" />	
	<dsp:getvalueof var="formError" bean="CartModifierFormHandler.formError"/>
	<json:object>
		<json:property name="error" value="${formError}"></json:property>
		<json:property name="commItemCount"><dsp:valueof bean="CartModifierFormHandler.addItemCount"/></json:property>
		<json:property name="count"><dsp:valueof bean="CartModifierFormHandler.qtyAdded"/></json:property>	
		<c:if test="${formError == true}">						
			<json:array name="errorMessages">
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
					<dsp:param bean="CartModifierFormHandler.formExceptions" name="exceptions"/>
					
					<dsp:oparam name="output">						
							<json:property><dsp:valueof param="message"/></json:property>						
					</dsp:oparam>				
				</dsp:droplet>
			</json:array>	
			
		</c:if>
	    
	</json:object> 
</dsp:page>