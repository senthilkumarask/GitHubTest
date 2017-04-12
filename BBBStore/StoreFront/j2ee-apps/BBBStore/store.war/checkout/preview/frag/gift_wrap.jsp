<dsp:page>
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="isFromPreview" param="isFromPreview"/>
	<div class="previewGiftOption">
		<p class="previewText"><bbbl:label key="lbl_preview_giftoptions" language="<c:out param='${language}'/>"/></p>
		<c:if test="${not empty showLinks}">
			<c:set var="SGCount" value="0" scope="page"/>
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="order.shippingGroups" />
			<dsp:param name="elementName" value="shippingGroup" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="shippingGroupVar" param="shippingGroup"/>
						<dsp:droplet name="/atg/dynamo/droplet/Compare">
							<dsp:param name="obj1" param="shippingGroupVar.repositoryItem.type"/>
							<dsp:param name="obj2" value="hardgoodShippingGroup"/>
							<dsp:oparam name="equal">
								<c:set var="SGCount" value="${SGCount+1}" scope="page"/>
							</dsp:oparam>
						</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
			<c:choose>
				<c:when test="${SGCount gt 1}">
					<p class="marTop_10 editPreviewLink"><a class="upperCase" href="${pageContext.request.contextPath}/checkout/gifting/gifting.jsp?isFromPreview=${isFromPreview}" title="<bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/></strong></a></p>			
				</c:when>
				<c:otherwise>
					<p class="marTop_10 editPreviewLink"><a class="upperCase" href="${pageContext.request.contextPath}/checkout/shipping/shipping.jsp?isFromPreview=${isFromPreview}" title="<bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/>"><span class="txtOffScreen">Edit Gift Option</span><strong><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/></strong></a></p>
				</c:otherwise>
			</c:choose>
		</c:if>
		<div class="clearfix marTop_5 previewGiftText">
			<p class="fl"><bbbl:label key="lbl_gift_wrapping" language="<c:out param='${language}'/>"/></p>
			<p class="fr">
				<c:choose>
					<c:when test="${shippingGroup.containsGiftWrap}">
					<c:set var="containsGiftWrap" value="true"/>
						<bbbl:label key="lbl_preview_gift_wrap_true" language="<c:out param='${language}'/>"/>
					</c:when>
					<c:otherwise>
					<c:set var="containsGiftWrap" value="false"/>
						<bbbl:label key="lbl_preview_gift_wrap_false" language="<c:out param='${language}'/>"/>
					</c:otherwise>
				</c:choose>
			</p>
		</div>
		<c:if test="${containsGiftWrap && not empty shippingGroup.specialInstructions.giftMessage || (shippingGroup.giftWrapInd && not empty shippingGroup.specialInstructions.giftMessage)}">
			<div class="clearfix giftOptionMsg">
				<p class="marTop_5 previewGiftText "><bbbl:label key="lbl_checkout_gift_message" language="<c:out param='${language}'/>"/></p>
				<p class="marTop_5 smallText breakWord previewGiftText msgData"><c:out value='${shippingGroup.specialInstructions.giftMessage}'/></p>
			</div>
		</c:if>
		
	</div>
</dsp:page>

