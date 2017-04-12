<dsp:page>
<%--Common code of video for all slots is refactored to this jsp --%>
<dsp:getvalueof var="promoSlotTitle" param="promoSlot.promoElementName"/>
<dsp:getvalueof var="counter" param="counter"/>
<c:choose>
	<c:when test="${not empty promoSlotTitle}">
		<a title="${promoSlotTitle}" href="#">
			<span class="sectionTitle">${promoSlotTitle}</span>
		</a>
	</c:when>
	<c:otherwise>
		<a title="${promoSlotTitle}" href="#">
		</a>
	</c:otherwise>	
</c:choose>	
<dsp:getvalueof var="promoSlotPromoVideoId" param="promoSlot.videoId"/>
<div class="videoThumb" id="prodVideo${counter}" data-prodId="${promoSlotPromoVideoId}">
	<img width="20" height="20" src="/_assets/global/images/widgets/small_loader.gif" class="loading" alt="">
</div>

</dsp:page>