<dsp:importbean bean="/com/bbb/commerce/shipping/droplet/HolidayMessagingDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:getvalueof param="nearbyStoreLink" var="nearbyStoreLink"/>
<dsp:getvalueof param="showNearbyStorelink" var="showNearbyStorelink"/>
<dsp:getvalueof param="timeframe" var="timeframe"/>
<dsp:getvalueof param="tbsShipTime" var="tbsShipTime"/>
<dsp:getvalueof param="shipMethodDesc" var="shipMethodDesc"/>
<dsp:getvalueof param="shipMethod" var="shipMethod"/>
<dsp:getvalueof param="shippingCharge" var="shippingCharge"/>
<dsp:getvalueof param="currentStep" var="currentStep"/>
<dsp:getvalueof param="appendtoLeadTime" var="appendtoLeadTime"/>
<c:if test="${empty shipMethod}">
	<c:set var="shipMethod" value="3g"/>
</c:if>
<c:if test="${empty timeframe}">
	<c:set var="timeframe" value="0001"/>
</c:if>
	<dsp:droplet name="OrderHasLTLItemDroplet">
        	<dsp:param name="order" bean="ShoppingCart.current"/>
        	<dsp:oparam name="output">
            		<dsp:getvalueof var="orderHasVDCItem" param="orderHasVDCItem" />
		 </dsp:oparam>
	</dsp:droplet>
<dsp:droplet name="HolidayMessagingDroplet">
    <dsp:param name="shipTime" value="${timeframe}"/>
   	<dsp:oparam name="output">
      	<dsp:getvalueof var="HolidayMessagingVO" param="HolidayMessagingVO"/>
      	<c:if test="${not empty HolidayMessagingVO.expeditedLabel}">
      		<c:set var="expeditedLabel">
				<bbbt:textArea key="${HolidayMessagingVO.expeditedLabel}" language="${pageContext.request.locale.language}"></bbbt:textArea>
			</c:set>
		</c:if>
		<c:if test="${not empty HolidayMessagingVO.standardLabel}">
			<c:set var="standardLabel">
				<bbbt:textArea key="${HolidayMessagingVO.standardLabel}" language="${pageContext.request.locale.language}"></bbbt:textArea>
			</c:set>
		</c:if>
<%-- 		Standard Label ${standardLabel} --%>
		<c:set var="holidayMessage">
			<c:choose>
				<c:when test="${shipMethod eq '2a'&& not empty expeditedLabel}">
					${expeditedLabel}
				</c:when>
				<c:when test="${shipMethod eq '3g' && not empty standardLabel}">
					${standardLabel}
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
		</c:set>
		
		
			<c:choose>
				<c:when test="${not empty holidayMessage && not empty tbsShipTime && appendtoLeadTime}">
					${holidayMessage}
				</c:when>
				<c:when test="${not empty holidayMessage && not empty showNearbyStorelink and showNearbyStorelink eq true}">
					<a id="holiday-sprite">
						<span class="holiday-sprite" data-tooltip aria-haspopup="true" class="has-tip tip-bottom holiday_message" title="${holidayMessage}"></span>
					</a>
				</c:when>
				<c:when test="${not empty holidayMessage && currentStep eq 'singleShipReview' && !orderHasVDCItem}">
					<div class="" data-tooltip aria-haspopup="true"  class="has-tip tip-bottom holiday_message" title="${holidayMessage}"> ${holidayMessage}</div>
				</c:when>
				
				<c:when test="${not empty shipMethodDesc}">
					<c:choose>
						<c:when test="${not empty holidayMessage && (shipMethod eq '2a' or shipMethod eq '3g')}">
							<div class="" aria-haspopup="true"  class="has-tip tip-bottom holiday_message" >
						</c:when>
						<c:otherwise>
							<div class="">
						</c:otherwise>
					</c:choose>
						<c:if test="${currentStep ne 'singleShipReview' }">
							<c:out value="${shipMethodDesc}" />
						</c:if>
						<c:if test="${not empty shippingCharge}"> 
							<getvalueof var="shippingCharge" param="shippingCharge"/>
							(<fmt:formatNumber value="${shippingCharge}"  type="currency"/>)
						</c:if>
					
						<c:if test='${!orderHasVDCItem}'><br>${holidayMessage}</c:if>
						</div>
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
		
     </dsp:oparam>
  </dsp:droplet>
