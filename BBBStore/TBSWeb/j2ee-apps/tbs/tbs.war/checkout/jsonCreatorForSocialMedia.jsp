<dsp:page>
	<dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
	<dsp:getvalueof var="orderVOListContent" param="orderVoList"/>
	
	<c:set var="scene7PathForSocial" value="${scene7Path}"/>
		
		<c:if test="${fn:startsWith(scene7PathForSocial, '//')}">
			<c:set var="scene7PathForSocial" value="https:${scene7PathForSocial}"/>
		</c:if>
		
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param value="${orderVOListContent}" name="array" />
			
			<dsp:oparam name="empty">
				BBB.userOrderProducts ={};
			</dsp:oparam>
			
			<dsp:oparam name="outputStart">
				BBB.userOrderProducts ={
			</dsp:oparam>		
			<dsp:oparam name="output">
			
				<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" param="element.registry" />
						<dsp:oparam name="true">

				<dsp:getvalueof var="lowerCaseName" param="element.skuId"/>		
					'<c:out value="${lowerCaseName}"/>':			
					<json:object>

				<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
					<dsp:param name="id" param="element.productId" />
					<dsp:param name="itemDescriptorName" value="product" />
					<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
					<dsp:oparam name="output">
							<json:property name="url" escapeXml="false">http://<dsp:valueof value="${serverName}"/><dsp:valueof value="${contextPath}"/><dsp:valueof param="url"/> </json:property>
					</dsp:oparam>
				</dsp:droplet>

			
		
				
			<json:property name="title" escapeXml="false"> <dsp:valueof param="element.productTitle" valueishtml="true" /> </json:property>
			<json:property name="image"><dsp:valueof value="${scene7PathForSocial}"/>/<dsp:valueof param="element.productLargeImageURL"/> </json:property>
			<json:property name="shortDesc" escapeXml="false"> <dsp:valueof param="element.productDescription" valueishtml="true" /> </json:property>
			<json:property name="registry" value="false" />
					
					</json:object>
					
					<dsp:droplet name="/atg/dynamo/droplet/Compare">
						<dsp:param name="obj1" param="count"/>
						<dsp:param name="obj2" param="size"/>
						<dsp:oparam name="lessthan">
					,
					</dsp:oparam>
					</dsp:droplet>
				
						</dsp:oparam>
				</dsp:droplet>
					
			</dsp:oparam>
			
			<dsp:oparam name="outputEnd">
			};
			</dsp:oparam>
		</dsp:droplet>
</dsp:page>
