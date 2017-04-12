<dsp:page>
<dsp:droplet name="/atg/dynamo/droplet/ForEach">
	<dsp:param param="attributesList" name="array" />
	<dsp:param name="elementName" value="attributeVOList"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="placeholder" param="key"/>
		<c:if test="${placeholder eq 'CRSL'}">
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param param="attributeVOList" name="array" />
				<dsp:param name="elementName" value="attributeVO"/>
				<dsp:param name="sortProperties" value="+priority"/>
				<dsp:oparam name="output">
				<li class="productAttributes prodAttribWrapper">
					<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
				</li>
				</dsp:oparam>
			</dsp:droplet>
		</c:if>
	</dsp:oparam>
</dsp:droplet>
</dsp:page>