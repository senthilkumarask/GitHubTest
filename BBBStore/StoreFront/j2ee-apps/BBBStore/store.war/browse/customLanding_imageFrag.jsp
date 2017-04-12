<dsp:page>
	<%--Common code of image for all slots is refactored to this jsp --%>
<dsp:getvalueof var="promoSlotTitle" param="promoSlot.promoElementName"/>
<dsp:getvalueof var="promoSlotTitle" param="promoSlot.promoElementName"/>
<dsp:getvalueof var="promoSlotPromoAltText" param="promoSlot.promoAltText"/>
<dsp:getvalueof var="promoSlotPromoOverlayImage" param="promoSlot.overlayImage"/>
<dsp:getvalueof var="promoSlotImgLink" param="promoSlot.promoImageLink"/>
<dsp:getvalueof var="promoSlotImgType" param="promoSlot.imageDestinationURLType"/>
<dsp:getvalueof var="fromClpQs" param="fromClpQs"/>
<dsp:getvalueof var="fromClpAmp" param="fromClpAmp"/>

<%--BELL-30 changes for configuring overlay flag from BCC changes start --%>
    <c:set var="overlay_flag">
		<bbbc:config key="ClpOverlayFlag" configName="ContentCatalogKeys" />
	</c:set>
<%--BELL-30 changes for configuring overlay flag from BCC changes end --%>

<%-- Band-503 CLP Link on Search Results Page changes start--%>
	<c:if test="${not empty promoSlotImgLink && promoSlotImgType eq 'SearchPage'}">
		<c:choose>
			<c:when test="${fn:contains(promoSlotImgLink,'?')}">
				<c:set var="promoSlotImgLink" value="${promoSlotImgLink}${fromClpAmp}" />
			</c:when>
			<c:otherwise>
				<c:set var="promoSlotImgLink" value="${promoSlotImgLink}${fromClpQs}" />
			</c:otherwise>
		</c:choose>	
	</c:if>
	
<%-- Band-503 CLP Link on Search Results Page changes end--%>	
	<dsp:getvalueof var="promoSlotPromoImageUrl" param="promoSlot.imageUrl"/>
	<c:if test="${overlay_flag && not empty promoSlotPromoOverlayImage }">
			<%-- Adding Div for overlay --%>
			<div class="overlayIcon">
				<img alt= "overlay icon" src="${promoSlotPromoOverlayImage}"/>
			</div>
	</c:if>
	<c:choose>
		<c:when test="${not empty promoSlotTitle}">																				
			<a title="${promoSlotTitle}" href="${promoSlotImgLink}">
				<span class="sectionTitle">${promoSlotTitle}</span>
				<img alt="${promoSlotPromoAltText}" src="${promoSlotPromoImageUrl}" />
			</a>
		</c:when>
		<c:otherwise>
			<a title="${promoSlotTitle}" href="${promoSlotImgLink}">
				<img alt="${promoSlotPromoAltText}" src="${promoSlotPromoImageUrl}" />
			</a>
		</c:otherwise>
	</c:choose>

</dsp:page>																
											