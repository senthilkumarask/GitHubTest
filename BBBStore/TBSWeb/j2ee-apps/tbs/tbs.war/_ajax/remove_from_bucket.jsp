<dsp:page>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:getvalueof var="productIdToRemove" param="productId" />
	<dsp:setvalue bean="ProductListHandler.productID" value="${productIdToRemove}" />
	<dsp:setvalue bean="ProductListHandler.removeProduct" />
	
	<dsp:getvalueof var="formError" bean="ProductListHandler.formError"/>
	
	<json:object>
		<json:property name="error" value="${formError}"></json:property>
		<c:if test="${formError == true}">						
			<json:array name="errorMessages">
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
					<dsp:param bean="ProductListHandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="output">						
							<json:property><dsp:valueof param="message"/></json:property>						
					</dsp:oparam>				
				</dsp:droplet>
			</json:array>	
		</c:if>
	</json:object> 
	
</dsp:page>