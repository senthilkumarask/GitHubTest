<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
	<c:choose>
		<c:when test="${isTransient}">
			<c:set var="customerType" value="Anonymous" />
		</c:when>
		<c:otherwise>
			<c:set var="customerType" value="LoggedIn" />
		</c:otherwise>
	</c:choose>
<!--${customerType}--<dsp:valueof param="registryType" />--<dsp:valueof param="pageName" />--<dsp:valueof param="promoSpot" />--<dsp:valueof param="siteId" />--<dsp:valueof param="channel" />-->
	<dsp:droplet name="/atg/targeting/TargetingFirst">
		<dsp:param name="targeter" bean="/atg/registry/RepositoryTargeters/RegistryPagesPromoImageTargeter" />
		<dsp:param name="pageName" param="pageName" />
		<dsp:param name="promoSpot" param="promoSpot" />
		<dsp:param name="registryType" param="registryType" />
		<dsp:param name="customerType" value="${customerType}" />
		<dsp:param name="siteId" param="siteId" />
		<dsp:param name="channel" param="channel" />
		<dsp:param name="howMany" value="1" />
		<dsp:oparam name="empty">
		<!--Empty-->
		</dsp:oparam>
		<dsp:oparam name="output">
		<!--Got the records-->
			<dsp:droplet name="IsEmpty">
				<dsp:param name="value" param="element.promoBox" />
				<dsp:oparam name="true">
				<!--Promoboxes are null.-->
				</dsp:oparam>
				<dsp:oparam name="false">
				<!--Got the Promoboxes.-->
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="element.promoBox" />
						<dsp:oparam name="output">
						  <div class="small-12 columns">
                            <dsp:getvalueof var="promoContent" param="element.promoBoxContent" />
                            <dsp:getvalueof var="promoContentNew" value="${fn:replace(promoContent, '/store','/tbs')}"/>
                            <dsp:getvalueof var="promoContentNew" value="${fn:replace(promoContentNew, 'fl','small-12 medium-4 columns')}"/>
                            ${promoContentNew}
                          </div>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>