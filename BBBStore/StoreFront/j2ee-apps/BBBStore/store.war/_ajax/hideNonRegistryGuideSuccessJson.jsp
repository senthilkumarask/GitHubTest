<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/interactive/InteractiveChecklistFormHandler"/>
		<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/SelectHeaderDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="guideType" param="guideType"/>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
		<dsp:setvalue bean="InteractiveChecklistFormHandler.addOrHideGuideType" value="${guideType}" />
		<dsp:setvalue bean="InteractiveChecklistFormHandler.hideGuideFlow" value="${true}" />
		<dsp:setvalue bean="InteractiveChecklistFormHandler.addOrHideShoppingGuide" value="true" />
		<dsp:droplet name="SelectHeaderDroplet">
			<dsp:param name="siteId" value="${currentSiteId}" />
			<dsp:oparam name="output">
			<dsp:getvalueof id="showStaticHeader" param="showStaticHeader"/>
			<c:set var="showStaticHeader" scope="request">${showStaticHeader}</c:set>
			</dsp:oparam>
			</dsp:droplet>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="InteractiveChecklistFormHandler.nextActivatedGuideType"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property escapeXml="false" name="regOrGuideType">
					<dsp:valueof bean="InteractiveChecklistFormHandler.nextActivatedGuideType"/>
				</json:property>
			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="true">
			<json:object>				
				<json:property escapeXml="false" name="showStaticHeader">
					${showStaticHeader}
				</json:property>
			</json:object>	
		</dsp:oparam>
	</dsp:droplet>	
</dsp:page>