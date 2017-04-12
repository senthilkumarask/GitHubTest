<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
	<c:choose>
		<c:when test="${isTransient}">
			<c:set var="customerType" value="Anonymous" />
		</c:when>
		<c:otherwise>
			<c:set var="customerType" value="LoggedIn" />
		</c:otherwise>
	</c:choose>
	<dsp:getvalueof var="regId" param="regId" />
	<dsp:getvalueof var="eventType" param="eventType" />
<%--${customerType}--<dsp:valueof param="registryType" />--<dsp:valueof param="pageName" />--<dsp:valueof param="promoSpot" />--<dsp:valueof param="siteId" />--<dsp:valueof param="channel" />--%>
	<dsp:droplet name="/atg/targeting/TargetingFirst">
		<dsp:param name="targeter" bean="/atg/registry/RepositoryTargeters/RegistryPagesPromoImageTargeter" />
		<dsp:param name="pageName" value="registryCheckList" />
		<dsp:param name="promoSpot" value="Top" />
		<dsp:param name="registryType" value="${eventType}" />
		<dsp:param name="customerType" value="${customerType}" />
		<dsp:param name="siteId" value="${appid}" />
		<dsp:param name="channel" value="Desktop Web" />
		<dsp:param name="howMany" value="1" />
		<dsp:oparam name="empty">
		<%--Empty--%>
		</dsp:oparam>
		<dsp:oparam name="output">
		<%--Got the records--%>
			<dsp:droplet name="IsEmpty">
				<dsp:param name="value" param="element.promoBox" />
				<dsp:oparam name="true">
				<%--Promoboxes are null.--%>
				</dsp:oparam>
				<dsp:oparam name="false">
				<%--Got the Promoboxes.--%>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="element.promoBox" />
						<dsp:oparam name="output">				
						<dsp:getvalueof var="promoContent" param="element.promoBoxContent" />	
						  ${fn:replace(promoContent, "${registryId}", regId)}						
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>