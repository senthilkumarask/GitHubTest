<dsp:page>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof var="productIdToAdd" param="productId" />
	<dsp:setvalue bean="ProductListHandler.productID" value="${productIdToAdd}" />
	<dsp:setvalue bean="ProductListHandler.addProduct" />
	
	<dsp:getvalueof var="items" bean="ProductComparisonList.items"/>
	<dsp:getvalueof var="formError" bean="ProductListHandler.formError"/>
	<c:choose>
		<c:when test="${formError == true}">
			<json:object>
				<json:property name="error" value="${formError}"></json:property>
				<json:array name="errorMessages">
					<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
						<dsp:param bean="ProductListHandler.formExceptions" name="exceptions"/>
						<dsp:oparam name="output">						
								<json:property><dsp:valueof param="message"/></json:property>						
						</dsp:oparam>				
					</dsp:droplet>
				</json:array>	
			</json:object> 
		</c:when>
		<c:otherwise>
			<json:object>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${items}" />
						<dsp:oparam name="output">
						<dsp:getvalueof var="productId" param="element.productId"/>
						<c:if test = "${productIdToAdd eq productId}">
						<dsp:getvalueof var="image" param="element.imagePath"/>
						<dsp:getvalueof var="imagePath" value="${image}"/>
							<json:property name="productId" value="${productId}"></json:property>
							<json:property name="imagePath" value="${imagePath}"></json:property>
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
						</c:if>
						</dsp:oparam>
				</dsp:droplet>
			</json:object> 
		</c:otherwise>
	</c:choose>
	
</dsp:page>