<dsp:getvalueof var="skuVO" param="skuVO" />
<dsp:getvalueof var="pDefaultChildSku" param="pDefaultChildSku" />
<dsp:getvalueof var="productId" param="productId" />
<dsp:getvalueof var="vendorName" param="vendorName"/>
<c:set var="customizeCTACodes">
	<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
</c:set>
<c:choose>
	<c:when test="${itemAlreadyPersonalized }">
	<div class="personalizedPDPWrapper">
			<div class="marTop_10 loaderDiv hidden">
				<img width="20" height="20" src="" alt="small loader">
			</div>
			<div class="productContent clearfix">
				<img src="${personalizedSmallImage}" class="personalizedImg" alt="Personalized Image">
				<ul class="prodInfo grid_4">									
				<li class="personalisedProdDetails">${personalizedSku.eximResponse.description}</li>
					<li class="priceAdditionText">
						<span class="personalizationAttr">
						<%--BBBSL-8154 --%>
							<%-- <span class="eximIcon"  aria-hidden="true">${personalizedSku.personalizedSingleCode }</span> --%>
						</span>
						<c:choose>
						<c:when test="${personalizedSku.personalizationComplete eq true }">
						<c:choose>
							<c:when test="${skuVO.personalizationType eq 'PY' }">
								<span class="addedPrice">${shopperCurrency}${eximPersonalizedPrice }</span>
								<bbbt:textArea key="txt_pdp_katori_price" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${skuVO.personalizationType eq 'CR' }">
								<span class="addedPrice">${shopperCurrency}${eximPersonalizedPrice }</span>
								<c:choose>
								<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
									<bbbt:textArea key="txt_pdp_cr_customize_katori_price" language ="${pageContext.request.locale.language}"/>
								</c:when>
								<c:otherwise>
									<bbbt:textArea key="txt_pdp_cr_katori_price" language ="${pageContext.request.locale.language}"/>
								</c:otherwise>
								</c:choose>
							</c:when>
							<c:when test="${skuVO.personalizationType eq 'PB' }">
								<span class='addPriceText'>
								<bbbl:label key="lbl_PB_Fee_detail" language="${pageContext.request.locale.language}" />
								</span>
							</c:when>
						</c:choose>
							</c:when>
							<c:otherwise>
								<span class="addedPrice" ></span>
								<span class='addPriceText'><bbbl:label key="lbl_price_change" language="${pageContext.request.locale.language}" /></span>
							</c:otherwise>
							</c:choose>

						 <!-- <span class="addedPrice"></span> -->
					</li>
					<li class="updateLinks">
						<c:choose>
						<c:when test="${disableEditRemove eq true }" >
							<a href="" aria-label="<bbbl:label key="lbl_cart_edit_personalization_for" language="<c:out param='${language}'/>"/> ${skuVO.displayName}" class="editLink personalize disabled" title="Edit Link" data-refnum='<c:if test="${itemAlreadyPersonalized }">${personalizedSku.eximResponse.refnum }</c:if>' data-custom-vendor='${vendorName}' data-product="${productId}" data-sku='${pDefaultChildSku}' data-quantity='1'  ><bbbl:label key='lbl_cart_personalization_edit' language="${pageContext.request.locale.language}" /></a>
							<span class="seperator"></span>
							<a href="" aria-label="<bbbl:label key="lbl_cart_remove_personalization_for" language="<c:out param='${language}'/>"/> ${skuVO.displayName}" class="removePersonalization disabled" title="Remove Personalization Link"><bbbl:label key="lbl_remove_personalization" language="${pageContext.request.locale.language}" /> <span class="removePersonalizeType">${personalizedSku.personalizedSingleCodeDescription }</span></a>
						</c:when>
						<c:otherwise>
							<a href="#" aria-label="<bbbl:label key="lbl_cart_edit_personalization_for" language="<c:out param='${language}'/>"/> ${skuVO.displayName}" class="editLink personalize" title="Edit Link" data-refnum='<c:if test="${itemAlreadyPersonalized }">${personalizedSku.eximResponse.refnum }</c:if>' data-custom-vendor='${vendorName}' data-product="${productId}" data-sku='${pDefaultChildSku}' data-quantity='1'><bbbl:label key='lbl_cart_personalization_edit' language="${pageContext.request.locale.language}" /></a>
							<span class="seperator"></span>
							<a href="#" aria-label="<bbbl:label key="lbl_cart_remove_personalization_for" language="<c:out param='${language}'/>"/> ${skuVO.displayName}" class="removePersonalization" title="Remove Personalization Link"><bbbl:label key="lbl_remove_personalization" language="${pageContext.request.locale.language}" /> <span class="removePersonalizeType">${personalizedSku.personalizedSingleCodeDescription }</span></a>
						</c:otherwise>
						</c:choose>
					</li>
				</ul>
			</div>
	</div>
	</c:when>
	<c:otherwise>
	<div class="personalizedPDPWrapper hidden">
		<div class="marTop_10 loaderDiv">
			<img width="20" height="20" src="/_assets/global/images/widgets/small_loader.gif" alt="small loader">
		</div>
		<div class="productContent clearfix hidden">
			<img src="/_assets/global/images/no_image_available.jpg" class="personalizedImg" alt="Personalized Image">
			<ul class="prodInfo grid_4">
			
				<li class="priceAdditionText">
					<span class="personalizationAttr">
					<%--BBBSL-8154 --%>
						<%-- <span class="eximIcon"  aria-hidden="true">M</span>--%>
					</span>
					<span class="addedPrice"></span>
					<span class='addPriceText hidden'>
						<bbbt:textArea key="txt_pdp_katori_price" language ="${pageContext.request.locale.language}"/>
					</span>

					 <!-- <span class="addedPrice"></span> -->
				</li>
				<li class="updateLinks">
					<a href="#" aria-label="<bbbl:label key="lbl_cart_edit_personalization_for" language="<c:out param='${language}'/>"/> ${skuVO.displayName}" class="editLink personalize" title="Edit Link" data-refnum='' data-custom-vendor='${vendorName}' data-product="${productId}" data-sku='${pDefaultChildSku}' data-quantity='1'><bbbl:label key='lbl_cart_personalization_edit' language="${pageContext.request.locale.language}" /></a>
					<span class="seperator"></span>
					<a href="#" aria-label="<bbbl:label key="lbl_cart_remove_personalization_for" language="<c:out param='${language}'/>"/> ${skuVO.displayName}" class="removePersonalization" title="Remove Personalization Link"><bbbl:label key="lbl_remove_personalization" language="${pageContext.request.locale.language}" /> <span class="removePersonalizeType"></span></a>
				</li>
			</ul>
		</div>
	</div>
	</c:otherwise>
</c:choose>