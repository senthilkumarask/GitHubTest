<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:getvalueof var="jasonObj" param="addItemResults" />
	<dsp:setvalue bean="CartModifierFormHandler.jsonResultString" value="${jasonObj}" />
	<dsp:setvalue bean="CartModifierFormHandler.addItemToOrder" />
	
	<dsp:getvalueof var="formError" bean="CartModifierFormHandler.formError"/>
	<json:object>
		<json:property name="error" value="${formError}"></json:property>
		<json:property name="commItemCount"><dsp:valueof bean="CartModifierFormHandler.addItemCount"/></json:property>
		<json:property name="count"><dsp:valueof bean="CartModifierFormHandler.qtyAdded"/></json:property>	
		<c:if test="${formError == true}">
			<json:array name="errorMessages">
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
					<dsp:param bean="CartModifierFormHandler.formExceptions" name="exceptions"/>
					<dsp:getvalueof var="errorMsg" param="message"/>
					<dsp:oparam name="output">						
							<json:property><c:out value="${errorMsg}" escapeXml="false" /></json:property>
							<c:set var="maxLimitError">ERROR_CART_MAX_REACHED</c:set>
							<dsp:getvalueof var="errorMsg" bean="CartModifierFormHandler.errorMap.${maxLimitError}"/>
							<c:if test="${fn:containsIgnoreCase(errorMsg, 'ERROR_CART_MAX_REACHED')}">
								<c:set var="maxCartCount">true</c:set>
							</c:if>	
					</dsp:oparam>				
				</dsp:droplet>
			</json:array>	
			
		</c:if>
		<c:if test="${maxCartCount eq true}">
			<json:property name="errMaxCartCount" value="${maxCartCount}"></json:property>
		</c:if>
	    
	</json:object> 
</dsp:page>