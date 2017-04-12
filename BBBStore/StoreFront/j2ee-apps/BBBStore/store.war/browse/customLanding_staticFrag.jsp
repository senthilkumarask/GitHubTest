<dsp:page>
<%--Common code of static for all slots is refactored to this jsp --%>
<dsp:getvalueof var="promoSlotTitle" param="promoSlot.promoElementName"/>
<dsp:getvalueof var="promoSlotImgLink" param="promoSlot.promoImageLink"/>
<dsp:getvalueof var="promoSlotPromoOverlayImage" param="promoSlot.overlayImage"/>
<dsp:getvalueof var="promoBoxContent" param="promoSlot.promoBoxContent"/>
<dsp:getvalueof var="promoCssFilePath" param="promoSlot.cssFilePath"/>
<dsp:getvalueof var="promoJsFilePath" param="promoSlot.jsFilePath"/>

<%--BELL-30 changes for configuring overlay flag from BCC changes start --%>
    <c:set var="overlay_flag">
		<bbbc:config key="ClpOverlayFlag" configName="ContentCatalogKeys" />
	</c:set>
<%--BELL-30 changes for configuring overlay flag from BCC changes end --%>


<c:if test="${not empty promoBoxContent }">
		<c:if test="${overlay_flag && not empty promoSlotPromoOverlayImage}">
			<div class="overlayIcon">
				<img alt= "overlay icon" src="${promoSlotPromoOverlayImage}"/>
			</div>
		</c:if>	
			<c:choose>
				<c:when test="${not empty promoSlotImgLink}">
				 <script type="text/javascript" src="${promoJsFilePath}"></script>
					<link rel="stylesheet" type="text/css" href="${promoCssFilePath }" />
					<a title="${promoSlotTitle}" href="${promoSlotImgLink}">
						<span class="sectionTitle">${promoSlotTitle}</span>
							<dsp:valueof value="${promoBoxContent}" valueishtml="true"/>
					</a>
				</c:when>
				<c:otherwise>
					<dsp:valueof value="${promoBoxContent}" valueishtml="true"/>
				</c:otherwise>
			</c:choose>
</c:if>

</dsp:page>