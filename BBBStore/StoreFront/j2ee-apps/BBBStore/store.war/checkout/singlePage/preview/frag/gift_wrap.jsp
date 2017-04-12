<dsp:page>
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="isFromPreview" param="isFromPreview"/>
	<ul class="SPCpreviewGiftWrapping">
		<span class="grid_3 SPCpreviewText"><h3 class="subHeading"><bbbl:label key="lbl_gift_wrapping" language="<c:out param='${language}'/>"/></h3></span>
		<span class="grid_3 SPCpreviewText noMar">
        
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
			
			</span>
	
		
	
	</ul>
	<c:if test="${containsGiftWrap && not empty shippingGroup.specialInstructions.giftMessage || (shippingGroup.giftWrapInd && not empty shippingGroup.specialInstructions.giftMessage)}">
	<ul class="SPCpreviewYourMessage">
		<span class="grid_3 SPCpreviewText"><h3 class="subHeading"><bbbl:label key="lbl_checkout_gift_message" language="<c:out param='${language}'/>"/></h3></span>
		<span class="noMar grid_4 breakWord"><c:out value='${shippingGroup.specialInstructions.giftMessage}'/>
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
					<span class="marTop_10 editPreviewLink"><a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp?showGiftOptions=true&isFromPreview=${isFromPreview}#spcGiftOptions" title="<bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/></strong></a></span>			
				</c:when>
				<c:otherwise>
					<span class="marTop_10 editPreviewLink"><a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp?showGiftOptions=true&isFromPreview=${isFromPreview}#spcGiftOptions" title="<bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/></strong></a></span>
				</c:otherwise>
			</c:choose>
		</c:if>
		</span>
	</ul>
		</c:if>
</dsp:page>

