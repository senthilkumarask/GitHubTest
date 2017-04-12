<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/multisite/Site"/>

	<%-- Page Variables --%>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof id="oldShippingId" param="oldShippingId" />
	<dsp:getvalueof id="id" param="id" />
	<dsp:getvalueof id="newQuantity" param="newQuantity" />
	<dsp:getvalueof id="priceMessageVO" param="priceMessageVO" />
	<dsp:getvalueof id="enableKatoriFlag" param="enableKatoriFlag"/>
	<dsp:getvalueof id="orderHasPersonalizedItem" param="orderHasPersonalizedItem"/>
	
	<c:if test="${enableKatoriFlag eq false && not empty orderHasPersonalizedItem}">
      <div class="genericYellowBox savedItemChangedWrapper clearfix marBottom_10 marTop_10">
	    <h2 class="heading"><bbbl:label key="lbl_personalization_unavailable_cart" language="${pageContext.request.locale.language}" /></h2>
      </div>
    </c:if>
	<c:if test="${(not empty priceMessageVO) && priceMessageVO.priceChange}">
		<div class="row">
			<div class="small-12 columns alert-box yellow radius" data-alert>
				<bbbt:textArea key="txt_item_change" language="${pageContext.request.locale.language}" />
				<a href="#" class="close">&times;</a>
			</div>
		</div>
	</c:if>

	<c:if test="${(not empty priceMessageVO) and ((priceMessageVO.flagOff) or (not priceMessageVO.inStock)) }">
		<div class="row">
			<div class="small-12 columns alert-box yellow radius" data-alert>
				<div class="row">
					<div class="small-12 columns">
						<c:if test="${not priceMessageVO.inStock}">
							<c:set var="isStockAvailability" value="no" scope="request"/>
						</c:if>
						<h2 class="subheader">${priceMessageVO.message}</h2>

						<%-- KP COMMENT START: this still needs to be restructured, but i need a test case (see structure of topLinkCart.jsp) --%>
						<p class="p-secondary">
							<c:if test="${not priceMessageVO.bopus and (defaultCountry eq 'US')}">
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
								<a class="hidden" title="${displayName}" href="${contextPath}/${finalUrl}">${displayName}</a>
								<input type="hidden" value="${priceMessageVO.prodId}" data-dest-class="productId" class="productId" data-change-store-submit="prodId" name="productId">
								<input type="hidden" value="${priceMessageVO.skuId}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
								<input id="quantity" type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${newQuantity}"  name="itemQuantity">
								<c:if test="${not empty priceMessageVO.registryId}">
									<input type="hidden" value="${priceMessageVO.registryId}" data-dest-class="registryId" class="registryId" data-change-store-submit="registryId" name="registryId">
								</c:if>
								<input type="hidden" name="commerceItemId" value="${id}" data-change-store-submit="commerceItemId" />
								<input type="hidden" name="oldShippingId" value="${oldShippingId}" data-change-store-submit="oldShippingId" />
								<input type="hidden" name="newQuantity" value="${newQuantity}" data-change-store-submit="newQuantity" />
								<input type="hidden" name="pageURL" value="${contextPath}/cart/ajax_handler_cart.jsp" data-change-store-submit="pageURL"/>
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
