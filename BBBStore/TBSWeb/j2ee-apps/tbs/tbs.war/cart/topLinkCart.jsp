<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/cart/TopStatusChangeMessageDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="reqFrom" param="reqFrom" />
	<c:if test="${empty reqFrom}">
		<c:set var="reqFrom" value="merge"/>
	</c:if>

	<dsp:droplet name="TopStatusChangeMessageDroplet">
		<dsp:param name="reqType" value="${reqFrom}" />
		<dsp:param name="skipOOSMessage" value="${true}" />
		<dsp:oparam name="outputPrice">
			<dsp:getvalueof id="priceMessageVO" param="priceMessageVO" />
			<c:if test="${(not empty priceMessageVO)}">
				<dsp:droplet name="SKUWishlistDetailDroplet">
					<dsp:param name="siteId" bean="Site.id"/>
					<dsp:param name="skuId" value="${priceMessageVO.skuId}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="skuDisplayName" param="pSKUDetailVO.displayName" />
						<dsp:getvalueof var="skuImageURL" param="pSKUDetailVO.skuImages.smallImage" />
					</dsp:oparam>
				</dsp:droplet>

				<dsp:getvalueof var="outOfStockItem"  value="${outOfStockItemList}"/>
				<dsp:droplet name="CanonicalItemLink">
					<dsp:param name="id" value="${priceMessageVO.prodId}" />
					<dsp:param name="itemDescriptorName" value="product" />
					<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
					</dsp:oparam>
				</dsp:droplet>

				<c:if test="${not fn:contains(outOfStockItem, priceMessageVO.prodId)}">

					<div class="row">
						<div class="small-12 columns alert-box yellow radius" data-alert>
							<div class="row">
								<div class="small-12 columns">
									<c:choose>
										<c:when test="${priceMessageVO.priceChange && !priceMessageVO.flagOff}">
											<bbbt:textArea key="txt_item_change" language="${pageContext.request.locale.language}" />
										</c:when>
										<c:when test="${(not empty priceMessageVO) and ((priceMessageVO.flagOff) or (not priceMessageVO.inStock)) }">
											<h2 class="subheader">${priceMessageVO.message}</h2>
											<p class="p-secondary">
												<c:if test="${not priceMessageVO.inStock || priceMessageVO.flagOff}">
													<bbbl:label key="lbl_save_item_link_oos" language="${pageContext.request.locale.language}" />
													<c:if test="${not empty priceMessageVO.parentCat}">
														<dsp:droplet name="CanonicalItemLink">
															<dsp:param name="id" value="${priceMessageVO.parentCat}" />
															<dsp:param name="itemDescriptorName" value="category" />
															<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
															<dsp:oparam name="output">
																<bbbl:label key="lbl_save_item_link_bopus_parent_cat" language="${pageContext.request.locale.language}" />
																<dsp:getvalueof var="catfinalUrl" vartype="java.lang.String" param="url" />
																<c:set var="lbl_save_top_link_shop"><bbbl:label key="lbl_save_top_link_shop" language="${pageContext.request.locale.language}"/></c:set>
																<a href="${contextPath}${catfinalUrl}" title="${lbl_save_top_link_shop}"><bbbl:label key="lbl_save_top_link_shop" language="${pageContext.request.locale.language}" /></a>.
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
												</c:if>
											</p>
										</c:when>
									</c:choose>
								</div>
							</div>
							<div class="row">
								<div class="small-4 large-2 columns">
									<c:choose>
										<c:when test="${empty skuImageURL}">
											<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${skuDisplayName}" title="${skuDisplayName}" />
										</c:when>
										<c:otherwise>
											<img src="${scene7Path}/${skuImageURL}" alt="${skuDisplayName}" title="${skuDisplayName}" />
										</c:otherwise>
									</c:choose>
								</div>
								<div class="small-8 large-10 columns">
									<div class="product-name">
										<c:choose>
											<c:when test="${priceMessageVO.flagOff or priceMessageVO.disableLink}">
												${skuDisplayName}
											</c:when>
											<c:otherwise>
												<a title="${skuDisplayName}" href="${contextPath}${finalUrl}?skuId=${priceMessageVO.skuId}">${skuDisplayName}</a>
											</c:otherwise>
										</c:choose>
									</div>
									<p class="upc-sku">
										<bbbl:label key="lbl_sfl_sku" language="<c:out param='${language}'/>"/> #${priceMessageVO.skuId}
									</p>
									<div class="price">
										<c:choose>
											<c:when test="${!priceMessageVO.flagOff && priceMessageVO.priceChange}">
												<span class="price-sale"><dsp:valueof value="${priceMessageVO.currentPrice}" converter="currency"/></span>&nbsp;<span class="price-original">Was <dsp:valueof value="${priceMessageVO.prevPrice}" converter="currency"/></span>
											</c:when>
											<c:otherwise>
												<dsp:valueof value="${priceMessageVO.currentPrice}" converter="currency"/>
											</c:otherwise>
										</c:choose>
									</div>
								</div>

								<%-- KP COMMENT START: TODO - what is this input for? --%>
								<input type="hidden" name="productId" class="productId" value="${priceMessageVO.prodId}" data-change-store-submit="prodId" />
								<%-- KP COMMENT END --%>

							</div>
							<a href="#" class="close">&times;</a>
						</div>
					</div>
				</c:if>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
