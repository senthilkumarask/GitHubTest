<dsp:page>

	<dsp:getvalueof var="skuVO" param="skuVO" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="isInternationalCustomer"
		param="isInternationalCustomer" />
	<dsp:getvalueof var="enableKatoriFlag" param="enableKatoriFlag" />
	<dsp:getvalueof var="pDefaultChildSku" param="pDefaultChildSku" />
	<dsp:getvalueof var="itemAlreadyPersonalized" param="itemAlreadyPersonalized"/>
	<dsp:getvalueof var="vendorName" param="vendorName"/>
	
	<c:set var="omniPersonalizeButtonClick">
		<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="customizableCodes" value="${skuVO.customizableCodes}" scope="request"/>
	<c:set var="customizeCTACodes" scope="request">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	
	<c:choose>
		<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
			<c:set var="personalizationTxt">
				<bbbl:label key="lbl_customize_this" language="${pageContext.request.locale.language}"/>
			</c:set>
			<c:set var="customizeAttr" value="customize"/>
			<input id="customizeCTAFlag" type="hidden" value="true"/>
		</c:when>
		<c:otherwise>
			<c:set var="personalizationTxt">
				<bbbl:label key="lbl_personalize_this" language="${pageContext.request.locale.language}"/>
			</c:set>
			<c:set var="customizeAttr" value=""/>
			<input id="customizeCTAFlag" type="hidden" value="false"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when
			test="${ (skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes && !isInternationalCustomer}">
			<div class="personalizationOffered clearfix cb fl">
				<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose>
					class="personalize 	${customizeAttr} <c:if test="${not enableKatoriFlag}">disabled</c:if>"
					role="button" data-custom-vendor="${vendorName}" data-product="${productId}" data-sku="${pDefaultChildSku}" data-refNum="<c:if test='${itemAlreadyPersonalized }'>${requestScope.personalizedSku.refnum }</c:if>"
					data-quantity=""
					<c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
					${personalizationTxt}
				</a>
				<c:choose>
					<c:when test="${enableKatoriFlag}">
							<span class='feeAppliedPB <c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType ne 'PB'}">hidden</c:if>'><bbbl:label
									key='lbl_PB_Fee_detail'
									language="${pageContext.request.locale.language}" /></span>
							<span class='feeAppliedPY <c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType ne 'PY'}">hidden</c:if>'> <bbbl:label
									key='lbl_PY_Fee_detail'
									language="${pageContext.request.locale.language}" /></span>
							<span class='feeAppliedCR <c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType ne 'CR'}">hidden</c:if>'> <bbbl:label
									key='lbl_CR_Fee_detail'
									language="${pageContext.request.locale.language}" /></span>
						<span class="personalizationDetail"> <bbbl:label
								key='lbl_cart_personalization_detail'
								language="${pageContext.request.locale.language}" />
						</span>
					</c:when>
					<c:otherwise>
						<span><bbbl:label key='lbl_pdp_personalization_unavailable'
								language="${pageContext.request.locale.language}" /></span>
					</c:otherwise>
				</c:choose>
			</div>
		</c:when>
		<c:otherwise>
			<%--If user selects international shipping on PDP then personalization will be disabled --%>
			<c:choose>
				<c:when test="${isInternationalCustomer}">
					<c:choose>
						<c:when
							test="${(skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">
							<div class="personalizationOffered clearfix cb fl">
								<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button"
								    data-custom-vendor="${vendorName}" data-product="${productId}"	data-sku="${pDefaultChildSku}" data-refNum="<c:if test='${itemAlreadyPersonalized }'>${requestScope.personalizedSku.refnum }</c:if>" data-quantity=""
									<c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
									${personalizationTxt}
								</a> <span class="personalizationIntlShippingMsg"><bbbt:textArea
										key="txt_pdp_per_unavailable_intl"
										language="${pageContext.request.locale.language}" /></span>
							</div>

						</c:when>
						<c:otherwise>
							<div class="personalizationOffered clearfix cb fl hidden ">
								<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose>
									class="personalize ${customizeAttr} <c:if test="${not enableKatoriFlag}">disabled</c:if>"
									role="button" data-custom-vendor="${vendorName}" data-product="${productId}" data-sku="${pDefaultChildSku}" data-refNum="<c:if test='${itemAlreadyPersonalized }'>${requestScope.personalizedSku.refnum }</c:if>"
									data-quantity=""
									<c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
									${personalizationTxt}
								</a>
								<c:choose>
									<c:when
										test="${enableKatoriFlag && not isInternationalCustomer}">
										<c:if
											test="${not empty skuVO.personalizationType && skuVO.personalizationType =='PB'}">
											<span class="feeApplied"><bbbl:label
													key='lbl_PB_Fee_detail'
													language="${pageContext.request.locale.language}" /></span>
										</c:if>
										<c:if
											test="${not empty skuVO.personalizationType && skuVO.personalizationType =='PY'}">
											<span class="feeApplied"> <bbbl:label
													key='lbl_PY_Fee_detail'
													language="${pageContext.request.locale.language}" /></span>
										</c:if>
										<c:if
											test="${not empty skuVO.personalizationType && skuVO.personalizationType =='CR'}">
											<span class="feeApplied"> <bbbl:label
													key='lbl_CR_Fee_detail'
													language="${pageContext.request.locale.language}" /></span>
										</c:if>
									&nbsp;<bbbl:label key='lbl_cart_personalization_detail'
											language="${pageContext.request.locale.language}" />
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${isInternationalCustomer}">
												<span class="personalizationIntlShippingMsg"><bbbt:textArea
														key="txt_pdp_per_unavailable_intl"
														language="${pageContext.request.locale.language}" /></span>
											</c:when>
											<c:otherwise>
											&nbsp;<bbbl:label key='lbl_pdp_personalization_unavailable'
													language="${pageContext.request.locale.language}" />
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<div class="personalizationOffered clearfix cb fl hidden">
						<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose>
							class="personalize ${customizeAttr} <c:if test="${not enableKatoriFlag}">disabled</c:if>"
							role="button" data-custom-vendor="${vendorName}" data-product="${productId}" data-sku="${pDefaultChildSku}" data-refNum="<c:if test='${itemAlreadyPersonalized }'>${requestScope.personalizedSku.refnum }</c:if>"
							data-quantity=""
							<c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
							${personalizationTxt}
						</a>
						<c:choose>
							<c:when test="${enableKatoriFlag}">
								<span class="feeAppliedPB hidden"><bbbl:label
										key='lbl_PB_Fee_detail'
										language="${pageContext.request.locale.language}" /></span>
								<span class="feeAppliedPY hidden"> <bbbl:label
										key='lbl_PY_Fee_detail'
										language="${pageContext.request.locale.language}" /></span>
								<span class="feeAppliedCR hidden"><bbbl:label
										key='lbl_CR_Fee_detail'
										language="${pageContext.request.locale.language}" /></span>
								<span class="personalizationDetail"> <bbbl:label
										key='lbl_cart_personalization_detail'
										language="${pageContext.request.locale.language}" />
								</span>
							</c:when>
							<c:otherwise>

								<span> <bbbl:label
										key='lbl_pdp_personalization_unavailable'
										language="${pageContext.request.locale.language}" />
								</span>
							</c:otherwise>
						</c:choose>
					</div>

				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</dsp:page>