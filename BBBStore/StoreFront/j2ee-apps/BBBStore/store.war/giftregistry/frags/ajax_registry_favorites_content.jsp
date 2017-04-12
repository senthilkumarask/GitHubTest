<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
	<dsp:getvalueof var="regId" param="regId" />
	<dsp:getvalueof var="eventType" param="eventType" />

	<c:set var="registryFavoritesContent">
		<c:choose>
			<c:when test="${not empty eventType && eventType eq 'Wedding'}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_wedding" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Baby'}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_baby" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Birthday'}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_birthday" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Retirement'}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_retirement" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_anniversary" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_housewarming" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_commitment" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && fn:contains(eventType, 'University')}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_college" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Other'}">
				<bbbt:textArea key="txt_registry_favorites_tab_content_other" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:otherwise>
				<bbbt:textArea key="txt_registry_favorites_tab_content_wedding" language ="${pageContext.request.locale.language}"/>
			</c:otherwise>
		</c:choose>
	</c:set>		
	<div id="registryFavoritesContent">
		${registryFavoritesContent}		
	</div>	
</dsp:page>