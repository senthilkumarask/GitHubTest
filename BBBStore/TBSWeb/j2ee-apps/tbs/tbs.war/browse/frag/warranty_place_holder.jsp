<dsp:page>
	<dsp:getvalueof var="attribs" param="attribs" />
	<c:set var="AttributePDPBelow">PDPB</c:set>
	
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param value="${attribs}" name="array" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="placeHolderBelow" param="key"/>
			<c:if test="${(not empty placeHolderBelow) && (placeHolderBelow eq 'PDPB')}">
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="element" name="array" />
					<dsp:param name="sortProperties" value="+priority"/>
					<dsp:oparam name="outputStart">
					</dsp:oparam>
					<dsp:oparam name="output">
						<dsp:getvalueof var="placeHolderBelow" param="element.placeHolder"/>
						<dsp:getvalueof var="attributeDescripBelow" param="element.attributeDescrip"/>
						<dsp:getvalueof var="imageURLBelow" param="element.imageURL"/>
						<dsp:getvalueof var="actionURLBelow" param="element.actionURL"/>
						<dsp:getvalueof var="attributeName" param="element.attributeName"/>	
						<c:choose>
							<c:when test="${null ne attributeDescripBelow}">
								<div id="warrPlaceHolder">
									<c:set var="replaceAttributeDescrip" value="${attributeDescripBelow}" />
									<dsp:valueof value="${replaceAttributeDescrip}" valueishtml="true"/>
								</div>
							</c:when>
						</c:choose>
					</dsp:oparam>
					<dsp:oparam name="outputEnd">
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>