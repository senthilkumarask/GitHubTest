<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RollUpListDroplet"/>
<c:set var="scene7Path">
    <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>

<json:object>

<dsp:droplet name="RollUpListDroplet">
<dsp:param name="id" param="productId" />
<dsp:param name="firstRollUpValue" param="swatchValue"/>
<dsp:param name="firstRollUpType" param="swatchType" />
<dsp:oparam name="output">
<dsp:getvalueof var="type" param="jsonObject"/>
<c:if test="${type eq 'SIZE'}"> 

	<json:object name="sizeSwatches"> 
			<dsp:droplet name="RollUpListDroplet">
			<dsp:param name="id" param="productId" />
			<dsp:param name="firstRollUpValue" param="swatchValue"/>
			<dsp:param name="firstRollUpType" param="swatchType" />
					<dsp:getvalueof var="json" param="jsonObject"/>								
					<dsp:oparam name="output">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="rollupList" name="array" />
						
						<dsp:oparam name="output">
							<dsp:getvalueof var="name" param="element.rollupAttribute"/>
							<c:set var="lowerCaseName" value="${fn:toLowerCase(name)}"></c:set>								
								<json:object name="size${fn:replace(lowerCaseName, ' ', '')}"> 
								<json:property name="name">
									<c:out value="${name}" escapeXml="false"/>
								</json:property>
								<json:property name="code">
									<c:out value="${name}" escapeXml="false"/>
								</json:property>				
								</json:object>
						</dsp:oparam>
						</dsp:droplet>
					
					</dsp:oparam>	
			</dsp:droplet>
	
	
	</json:object>

</c:if>


<c:if test="${type eq 'COLOR'}">

	<json:object name="colorSwatches">  
			<dsp:droplet name="RollUpListDroplet">
			<dsp:param name="id" param="productId" />
			<dsp:param name="firstRollUpValue" param="swatchValue"/>
			<dsp:param name="firstRollUpType" param="swatchType" />
					<dsp:getvalueof var="json" param="jsonObject"/>								
					<dsp:oparam name="output">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="rollupList" name="array" />
						
						<dsp:oparam name="output">
							<dsp:getvalueof var="name" param="element.rollupAttribute"/>	
							<dsp:getvalueof var="imgUrl" param="element.swatchImagePath"/>
							<c:set var="lowerCaseName" value="${fn:toLowerCase(name)}"></c:set>
							<json:object name="color${fn:replace(lowerCaseName, ' ', '')}">							
								<json:property name="url">
									${scene7Path}/${imgUrl}
								</json:property>
								<json:property name="name">
									${name}
								</json:property>
								<json:property name="code">
									${name}
								</json:property>	
							</json:object>
						</dsp:oparam>
						</dsp:droplet>
					
					</dsp:oparam>	
			</dsp:droplet>
	
	
	</json:object>
</c:if>

<c:if test="${type eq 'FINISH'}">

	<json:object name="finishSwatches">  
			<dsp:droplet name="RollUpListDroplet">
			<dsp:param name="id" param="productId" />
			<dsp:param name="firstRollUpValue" param="swatchValue"/>
			<dsp:param name="firstRollUpType" param="swatchType" />
					<dsp:getvalueof var="json" param="jsonObject"/>								
					<dsp:oparam name="output">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="rollupList" name="array" />
						
						<dsp:oparam name="output">
							<dsp:getvalueof var="name" param="element.rollupAttribute"/>	
							<dsp:getvalueof var="imgUrl" param="element.swatchImagePath"/>
							<c:set var="lowerCaseName" value="${fn:toLowerCase(name)}"></c:set>	
							<json:object name="finish${fn:replace(lowerCaseName, ' ', '')}">							
								<json:property name="url">
									${scene7Path}/${imgUrl}
								</json:property>
								<json:property name="name">
									${name}
								</json:property>
								<json:property name="code">
									${name}
								</json:property>	
							</json:object>	
						</dsp:oparam>
						</dsp:droplet>
					
					</dsp:oparam>	
			</dsp:droplet>
	
	
	</json:object>
</c:if>
</dsp:oparam>
</dsp:droplet>
</json:object>
</dsp:page>