<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/multisite/Site"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof id="registryIdSavedItem" param="registryIdSavedItem"/>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof id="priceMessageVO" param="priceMessageVO" />
	<dsp:getvalueof var="isPersonalized" param="isPersonalized" />
	<dsp:getvalueof var="enableKatoriFlag" param="enableKatoriFlag" />
	 
	<c:if test="${(not empty priceMessageVO) && priceMessageVO.priceChange}">
		<div class="row">
			<div class="small-12 columns alert-box yellow radius" data-alert>
				<bbbt:textArea key="txt_item_change" language="${pageContext.request.locale.language}" />
			</div>
		</div>
	</c:if>
    
	<c:if test="${enableKatoriFlag eq false && isPersonalized eq true}">
      <div class="genericYellowBox savedItemChangedWrapper clearfix marBottom_10 marTop_10">
        Personalization validation failed/Service Unavailable
      </div>
    </c:if>
	<c:if test="${(not empty priceMessageVO) and ((priceMessageVO.flagOff) or (not priceMessageVO.inStock)) }">
		<div class="row">
			<div class="small-12 columns alert-box yellow radius" data-alert>
				<div class="row">
					<div class="small-12 columns">
						<h2 class="subheader">${priceMessageVO.message}</h2>

						<%-- KP COMMENT START: this still needs to be restructured, but i need a test case (see structure of topLinkCart.jsp) --%>
						<p class="p-secondary">
							<c:if test="${not priceMessageVO.bopus  and (defaultCountry eq 'US' || defaultCountry eq 'Canada')}">
								<bbbl:label key="lbl_save_top_link_info" language="${pageContext.request.locale.language}" />
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" value="${priceMessageVO.prodId}" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<c:choose>
											<c:when test="${not flagOff}">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="#" />
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
								</dsp:droplet>
								<dsp:getvalueof var="image" param="image"/>
								<dsp:getvalueof var="displayName" param="displayName"/>
								<c:choose>
									<c:when test="${empty image}">
										<img class="hidden" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${displayName}" title="${displayName}"/>
									</c:when>
									<c:otherwise>
										<img class="hidden" src="${scene7Path}/${image}" alt="${displayName}" title="${displayName}"/>
									</c:otherwise>
								</c:choose>
								<a class="prodName productName hidden" title="${displayName}" href="${contextPath}/${finalUrl}">${displayName}</a>
								<dsp:getvalueof id="fromwishlist" param="fromwishlist"/>
								<c:choose>
									<c:when test="${not empty fromwishlist}">
										<c:set var="pageURL" value="${contextPath}/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp"/>
									</c:when>
									<c:otherwise>
										<c:set var="pageURL" value="${contextPath}/cart/ajax_handler_cart.jsp"/>
									</c:otherwise>
								</c:choose>
								<dsp:getvalueof id="id" param="id" />
								<input type="hidden" value="${priceMessageVO.prodId}" data-dest-class="productId" class="productId" data-change-store-submit="productId" name="productId">
								<input type="hidden" value="${priceMessageVO.skuId}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
								<c:if test="${not empty priceMessageVO.registryId}">
									<input type="hidden" value="${priceMessageVO.registryId}" data-dest-class="registryId" class="registryId" data-change-store-submit="registryId" name="registryId">
								</c:if>
								<input id="quantity" type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${priceMessageVO.quantity}"  name="itemQuantity">
								<input type="hidden" name="pageURL" value="${pageURL}" data-change-store-submit="pageURL"/>
								<input type="hidden" name="check" value="check" data-dest-class="check" data-change-store-submit="check" />
								<input type="hidden" name="wishlistItemId" value="${id}" data-dest-class="wishlistItemId" data-change-store-submit="wishlistItemId" />
								<bbbt:textArea key="txtarea_save_top_link_link" language="${pageContext.request.locale.language}" />
							</c:if>

							<c:if test="${not (defaultCountry eq 'US')}">
								<bbbl:label key="lbl_save_top_link_info_ca" language="${pageContext.request.locale.language}" />
							</c:if>

							<c:if test="${(not priceMessageVO.bopus) && (not empty priceMessageVO.parentCat) and (defaultCountry eq 'US')}">
								or&nbsp;
							</c:if>

							<c:if test="${priceMessageVO.bopus}">
								<bbbl:label key="lbl_save_item_link_oos" language="${pageContext.request.locale.language}" />
								<c:if test="${not empty priceMessageVO.parentCat}">
									<bbbl:label key="lbl_save_item_link_bopus_parent_cat" language="${pageContext.request.locale.language}" />
								</c:if>
							</c:if>

							<c:if test="${not empty priceMessageVO.parentCat}">
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" value="${priceMessageVO.parentCat}" />
									<dsp:param name="itemDescriptorName" value="category" />
									<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="catfinalUrl" vartype="java.lang.String" param="url" />
										<c:set var="lbl_save_top_link_shop"><bbbl:label key="lbl_save_top_link_shop" language="${pageContext.request.locale.language}"/></c:set>
										<a href="${contextPath}${catfinalUrl}" title="${lbl_save_top_link_shop}"><bbbl:label key="lbl_save_top_link_shop" language="${pageContext.request.locale.language}" /></a>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>.
						</p>
						<%-- KP COMMENT END --%>

					</div>
				</div>
			</div>
		</div>
	</c:if>

</dsp:page>
