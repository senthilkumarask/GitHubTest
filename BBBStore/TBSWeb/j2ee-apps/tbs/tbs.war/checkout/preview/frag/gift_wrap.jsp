<dsp:page>

	<%-- Variables --%>
	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
		<dsp:getvalueof param="isMultiShip" var="isMultiShip" />
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="isFromPreview" param="isFromPreview"/>

	<h3 class="checkout-title"><bbbl:label key="lbl_preview_giftoptions" language="<c:out param='${language}'/>"/>
		<c:if test="${not isConfirmation && isMultiShip}">
			<a class="edit-step tiny secondary button" href="#" data-step="shipping">Edit</a>
		</c:if>
		
	</h3>
	<div id="giftoption">
	<ul class="address">
		<c:choose>
			<c:when test="${not shippingGroup.containsGiftWrap and empty shippingGroup.specialInstructions.giftMessage}">
				<li>None</li>
			</c:when>
			<c:otherwise>
				<c:if test="${shippingGroup.containsGiftWrap}">
					<li><strong>Gift Wrap</strong></li>
					<li><bbbl:label key="lbl_preview_gift_wrap_true" language="<c:out param='${language}'/>"/></li>
				</c:if>
				<c:if test="${not empty shippingGroup.specialInstructions.giftMessage}">
					<li><strong><bbbl:label key="lbl_preview_message" language="<c:out param='${language}'/>"/></strong></li>
					<li><c:out value='${shippingGroup.specialInstructions.giftMessage}'/></li>
				</c:if>
			</c:otherwise>
		</c:choose>
	</ul>
</div>
</dsp:page>

