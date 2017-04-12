<dsp:page>

	<%-- Imports --%>
	<%@ page import="com.bbb.constants.BBBCheckoutConstants"%>
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBOrderRKGInfo" />
	<dsp:droplet name="RepriceOrderDroplet">
		<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
	</dsp:droplet>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>
	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBContinueShoppingDroplet"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean bean="/atg/commerce/order/droplet/ShipQualifierThreshold" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	<dsp:importbean bean="/com/bbb/common/droplet/ListPriceSalePriceDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/atg/commerce/promotion/ClosenessQualifierDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/TBSPriceOverrideFormhandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof var="cartState" value="${0}"/>
	<c:if test="${currentState ne cartState}">
		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="CART"/>
	</c:if>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
	<c:set var="lbl_checkout_checkout" scope="page">
		<bbbl:label key="lbl_checkout_checkout" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="bopusOnlyText" value="<%=BBBCheckoutConstants.BOPUS_ONLY %>" scope="page" />
	<c:set var="lbl_cartdetail_movetowishList" scope="page">
		<bbbl:label key="lbl_cartdetail_movetowishList" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="CertonaContext" value="" scope="request"/>
	<c:set var="RegistryContext" value="" scope="request"/>
	<c:set var="registryFlag" value="false"/>
	<c:set var="skuFlag" value="false"/>
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:setvalue value="false" bean = "PayPalSessionBean.fromPayPalPreview"/>

	<dsp:droplet name="BBBPaymentGroupDroplet">
		<dsp:param bean="ShoppingCart.current" name="order"/>
		<dsp:param name="serviceType" value="GetPaymentGroupStatusOnLoad"/>
		<dsp:oparam name="output">
		<dsp:getvalueof var="isOrderAmtCoveredVar" vartype="java.lang.String" param="isOrderAmtCovered" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="Profile.transient"/>
		<dsp:oparam name="false">
			<dsp:getvalueof var="userId" bean="Profile.id"/>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof var="userId" value=""/>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		<dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" bean="ShoppingCart.current.priceInfo"/>
			<c:if test="${orderPriceInfo == null}">
				<dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
					<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
				</dsp:droplet>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

	<%-- paypal button depending upon its ltl flag or item in order --%>
	<dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet">
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="orderHasLTLItem" param="orderHasLTLItem" />
		</dsp:oparam>
	</dsp:droplet>

	<%-- Save For Later --%>
	<dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
		<dsp:oparam name="empty">
			<div id="saveForLaterHeader" class="hidden">
				<dsp:include page="savedItemsHeader.jsp" />
			</div>
			<div id="saveForLaterBody" class="savedItems hidden">
				<div id="saveForLaterEmptyMessaging"></div>
			</div>
		</dsp:oparam>
		<dsp:oparam name="output">
			<div id="saveForLaterHeader">
				<dsp:include page="savedItemsHeader.jsp" />
			</div>
			<div id="saveForLaterBody" class="savedItems">
				<div id="saveForLaterContent">
					<div class="row saved-items-header hide-for-medium-down productsListHeader">
						<div class="medium-6 columns">
							<h3><bbbl:label key="lbl_sfl_item" language="${language}"/></h3>
						</div>
						<div class="medium-3 columns">
							<h3><bbbl:label key="lbl_sfl_quantity" language="${language}"/></h3>
						</div>
						<div class="medium-3 columns no-padding-left">
							<h3><bbbl:label key="lbl_sfl_total" language="${language}"/></h3>
						</div>
					</div>

					<dsp:include page="saveForlaterCartForms.jsp"/>

					<dsp:form formid="savedItemsForm" id="savedItemsForm" method="post" action="ajax_handler_cart.jsp">

						<dsp:droplet name="CertonaDroplet">
							<dsp:param name="scheme" value="fc_lmi"/>
							<dsp:param name="madhur" value="maddy"/>
							<dsp:param name="context" value="${CertonaContext}"/>
							<dsp:param name="exitemid" value="${RegistryContext}"/>
							<dsp:param name="userid" value="${userId}"/>
							<dsp:param name="number" value="${lastMinItemsMax}"/>
							<dsp:param name="siteId" value="${applicationId}"/>
							<dsp:param name="shippingThreshold" value="${shippingThreshold}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId" scope="request"/>
							</dsp:oparam>
						</dsp:droplet>

						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="giftList" />
							<dsp:param name="reverseOrder" value="true" />
							<dsp:oparam name="output">
								<c:set var="flagOff" value="${false}"/>
								<c:set var="bopusoff" value="${false}"/>
								<dsp:getvalueof var="arraySize" param="size" />
								<dsp:getvalueof var="currentCount" param="count" />
								<c:set var="lastRow" value="" />

								<c:if test="${arraySize eq currentCount}">
									<c:set var="lastRow" value="lastRow" />
								</c:if>

								<dsp:getvalueof var="registryId" param="element.registryID" />
								<dsp:getvalueof var="skuID" param="element.skuID" />
								<dsp:getvalueof var="quantity" param="element.quantity" />
								<dsp:getvalueof var="wishListItemId" param="element.wishListItemId" />
								<dsp:getvalueof var="commerceItemId" param="element.commerceItemId" />
								<dsp:getvalueof var="priceMessageVO" param="element.priceMessageVO" />
								<dsp:getvalueof var="prodID" param="element.prodID" />
								<dsp:getvalueof var="giftListId" param="element.giftListId" />
								<dsp:getvalueof var="ltlShipMethod"
								param="element.ltlShipMethod" />

								<c:if test="${not empty priceMessageVO}">
									<c:set var="bopusoff" value="${not priceMessageVO.bopus}"/>
								</c:if>

								<c:choose>
									<c:when test="${not empty priceMessageVO and priceMessageVO.flagOff}">
										<c:set var="flagOff" value="${true}"/>
									</c:when>
									<c:otherwise>
										<c:set var="flagOff" value="${false}"/>
									</c:otherwise>
								</c:choose>

								<c:set var="displaySize">
									<bbbc:config key="sfl_display_size" configName='CartAndCheckoutKeys' />
								</c:set>

								<c:if test="${empty displaySize}">
									<c:set var="displaySize" value="2" />
								</c:if>

								<c:if test="${currentCount == displaySize+1 && arraySize > displaySize}">
<!-- 									<li id="savedItemID_BTN" class="savedItemRow showAllBtn"> -->
<!-- 										<div class="prodItemWrapper clearfix"> -->
<!-- 											<div class="button"> -->
												<input id="showAllBtn" class="small secondary radius button" type="button" value="Show all ${arraySize} items" />
<!-- 											</div> -->
<!-- 										</div> -->
<!-- 									</li> -->
								</c:if>

								<div class="row saved-item" id="savedItemID_${wishListItemId}">

									<input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="prodId" name="productId">
									<input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
									<c:if test="${flagOff}">
										<input id="quantity" type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity">
									</c:if>

									<dsp:getvalueof var="productVO" param="element.productVO" />
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param param="element.productVO.rollupAttributes" name="array" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="menu" param="key"/>
											<c:if test="${menu eq 'FINISH'}">
												<c:set var="rollupAttributesFinish" value="true" />
											</c:if>
										</dsp:oparam>
									</dsp:droplet>

									<dsp:droplet name="SKUWishlistDetailDroplet">
										<dsp:param name="siteId" value="${applicationId}"/>
										<dsp:param name="skuId" value="${skuID}"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
											<dsp:getvalueof var="skuImage" param="pSKUDetailVO.skuImages.mediumImage"/>
											<dsp:getvalueof var="skuDisplayName" param="pSKUDetailVO.displayName" />
											<dsp:getvalueof var="skuImageURL" param="pSKUDetailVO.skuImages.mediumImage" />
											<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color" />
											<dsp:getvalueof var="skuSize" param="pSKUDetailVO.size" />
											<dsp:getvalueof var="isLtlFlag" param="pSKUDetailVO.ltlItem" />
										</dsp:oparam>
									</dsp:droplet>

									<dsp:getvalueof var="prodImage" value="${skuImageURL}" />
									<dsp:getvalueof var="pName" value="${skuDisplayName}" />

									<dsp:include page="itemLinkWishlist.jsp">
										<dsp:param name="id" value="${wishListItemId}"/>
										<dsp:param name="registryIdSavedItem" value="${registryId}"/>
										<dsp:param name="image" value="${prodImage}"/>
										<dsp:param name="displayName" value="${pName}"/>
										<dsp:param name="priceMessageVO" value="${priceMessageVO}"/>
									</dsp:include>

									<%-- TODO: see what this does --%>
									<%-- LTL Alert --%>
									<c:if test="${isLtlFlag && empty ltlShipMethod}" >
										<div class="small-12 columns           ltlItemAlert alert alert-info">
											<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>
										</div>
									</c:if>
									<%-- LTL Alert --%>

									<c:if test="${not empty registryId}">
										<c:set var="registryUrl" value="../giftregistry/view_registry_guest.jsp"/>
										<c:if test='${fn:contains(userActiveRegList, registryId)}'>
											<c:set var="registryUrl" value="../giftregistry/view_registry_owner.jsp"/>
										</c:if>
										<dsp:getvalueof var="registryVO" param="element.registryVO"/>
										<dsp:droplet name="GetRegistryTypeNameDroplet">
											<dsp:param name="siteId" value="${applicationId}"/>
											<dsp:param name="registryTypeCode" value="${registryVO.registryType.registryTypeName}"/>
											<dsp:oparam name="output">
												<c:set var="registryTypeName"><dsp:valueof param="registryTypeName"/></c:set>
											</dsp:oparam>
										</dsp:droplet>
										<div class="small-12 columns registry-item-heading">
											<bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/>
											<dsp:a href="${registryUrl}">
												<dsp:param name="registryId" value="${registryId}"/>
												<dsp:param name="eventType" value="${registryTypeName}" />
												${registryVO.primaryRegistrant.firstName}<c:if test="${not empty registryVO.coRegistrant.firstName}">&nbsp;&amp;&nbsp;${registryVO.coRegistrant.firstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/>
											</dsp:a>
											${registryTypeName}&nbsp;<bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/>
										</div>
									</c:if>

									<%-- item --%>
									<div class="small-12 large-6 columns">
										<div class="row">

											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" value="${prodID}" />
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

											<%-- image --%>
											<div class="small-4 columns">
												<div class="category-prod-img">
													<c:choose>
														<c:when test="${itemFlagoff or disableLink}">
															<c:choose>
																<c:when test="${empty prodImage || 'null' == prodImage}">
																	<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																</c:when>
																<c:otherwise>
																	<img src="${scene7Path}/${prodImage}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<dsp:a page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																<c:choose>
																	<c:when test="${empty prodImage || 'null' == prodImage}">
																		<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																	</c:when>
																	<c:otherwise>
																		<img src="${scene7Path}/${prodImage}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																	</c:otherwise>
																</c:choose>
															</dsp:a>
														</c:otherwise>
													</c:choose>
												</div>
											</div>

											<%-- capitalize facet labels --%>
											<c:set var="labelColor"><bbbl:label key='lbl_item_color' language='${pageContext.request.locale.language}'/></c:set>
											<c:set var="labelSize"><bbbl:label key='lbl_item_size' language='${pageContext.request.locale.language}'/></c:set>
											<c:set var="labelColorCapitalize" value="${fn:toUpperCase(fn:substring(labelColor, 0, 1))}${fn:toLowerCase(fn:substring(labelColor, 1,fn:length(labelColor)))}" />
											<c:set var="labelSizeCapitalize" value="${fn:toUpperCase(fn:substring(labelSize, 0, 1))}${fn:toLowerCase(fn:substring(labelSize, 1,fn:length(labelSize)))}" />

											<%-- description --%>
											<div class="small-8 columns">
												<c:choose>
													<c:when test="${itemFlagoff or disableLink}">
														<div class="product-name"><c:out value="${pName}" escapeXml="false" /></div>
														<c:if test='${not empty skuColor}'>
															<div class="facet">
																<c:out value="${labelColorCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
															</div>
														</c:if>
														<c:if test='${not empty skuSize}'>
															<div class="facet">
																<c:out value="${labelSizeCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuSize)}" valueishtml="true" />
															</div>
														</c:if>
													</c:when>
													<c:otherwise>
														<div class="product-name">
															<dsp:a page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																<c:out value="${pName}" escapeXml="false" />
															</dsp:a>
														</div>
														<c:if test='${not empty skuColor}'>
															<div class="facet">
																<c:out value="${labelColorCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
															</div>
														</c:if>
														<c:if test='${not empty skuSize}'>
															<div class="facet">
																<c:out value="${labelSizeCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuSize)}" valueishtml="true" />
															</div>
														</c:if>
													</c:otherwise>
												</c:choose>
												<div class="facet">
													<bbbl:label key="lbl_sfl_sku" language="<c:out param='${language}'/>"/> #${skuID}
												</div>
												<dsp:droplet name="ListPriceSalePriceDroplet">
													<dsp:param name="productId" param="element.prodID" />
													<dsp:param name="skuId" param="element.skuID" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="listPriceD" param="listPrice" />
														<dsp:getvalueof var="salePriceD" param="salePrice" />
													</dsp:oparam>
												</dsp:droplet>
												<c:choose>
													<c:when test="${(not empty salePriceD) && (salePriceD gt 0.0)}">
														<h1 class="price">
															<dsp:include page="/global/gadgets/formattedPrice.jsp">
																<dsp:param name="price" value="${salePriceD}"/>
															</dsp:include>
														</h1>
														<c:set var="itemPrice" scope="request">${salePriceD}</c:set>
													</c:when>
													<c:otherwise>
														<h1 class="price">
															<dsp:include page="/global/gadgets/formattedPrice.jsp">
																<dsp:param name="price" value="${listPriceD}"/>
															</dsp:include>
														</h1>
														<c:set var="itemPrice" scope="request">${listPriceD}</c:set>
													</c:otherwise>
												</c:choose>





												<%-- not sure we need this...can't find it actually working in production --%>
											<ul class="prodDeliveryInfo squareBulattedList">
												<dsp:droplet name="IsProductSKUShippingDroplet">
													<dsp:param name="siteId" value="${applicationId}"/>
													<dsp:param name="skuId" value="${skuID}"/>
													<dsp:param name="prodId" value="${prodID}"/>
													<dsp:oparam name="true">
														<dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>
														<c:forEach var="item" items="${restrictedAttributes}">
															<li>
																<c:choose>
																	<c:when test="${null ne item.actionURL}">
																		<a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
																	</c:when>
																	<c:otherwise>
																		${item.attributeDescrip}
																	</c:otherwise>
																</c:choose>
															</li>
														</c:forEach>
													</dsp:oparam>
													<dsp:oparam name="false">
													</dsp:oparam>
												</dsp:droplet>
											</ul>

<%-- need to format this --%>
											<c:if test="${BazaarVoiceOn}">
												<dsp:getvalueof var="reviews" value="${productVO.bvReviews.totalReviewCount}"/>
												<dsp:getvalueof var="ratings" value="${productVO.bvReviews.averageOverallRating}" vartype="java.lang.Integer"/>
												<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>

												<c:choose>
													<c:when test="${empty productVO}">
														<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
													</c:when>
													<c:otherwise>
														<dsp:getvalueof var="totalReviewCount" value="${productVO.bvReviews.totalReviewCount}"></dsp:getvalueof>
													</c:otherwise>
												</c:choose>
												<c:if test="${not flagOff}">
													<c:choose>
														<c:when test="${ratings ne null && ratings ne '0' && (reviews eq '1' || reviews gt '1') }">
															<span class="prodReview prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																<dsp:a  page="${finalUrl}?skuId=${skuID}&categoryId=${CategoryId}&showRatings=true">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></dsp:a>
															</span>
														</c:when>
														<c:otherwise>
															<span class="prodReview prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																<dsp:a  page="${finalUrl}?skuId=${skuID}&showRatings=true">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></dsp:a>
															</span>
														</c:otherwise>
													</c:choose>
												</c:if>
											</c:if>





											</div>
										</div>
									</div>

									<%-- quantity --%>
									<div class="small-6 large-3 columns quantity">

										<%-- quantity input --%>
										<div class="qty-spinner">
											<c:choose>
												<c:when test="${not itemFlagoff}">
													<a class="button minus secondary" title="Decrease Quantity"><span></span></a>
													<input name="${wishListItemId}" type="text" value="${quantity}" id="quantity_text_${skuID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="quantity-input" maxlength="2" data-max-value="99" aria-required="true" aria-labelledby="lblquantity_text_${skuID}" />
													<a class="button plus secondary" title="Increase Quantity"><span></span></a>
												</c:when>
												<c:otherwise>
													<input name="${wishListItemId}" type="hidden" value="${quantity}" id="quantity_text_${skuID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="quantity-input" maxlength="2" data-max-value="99" />
												</c:otherwise>
											</c:choose>
										</div>

										<%-- quantity actions --%>
										<div class="qty-actions">
											<%-- update --%>
											<c:if test="${not itemFlagoff}">
												<a href="#" class="qty-update" data-submit-button="#btnUpdateSFL${wishListItemId}" data-parent-row="#savedItemID_${wishListItemId}" title="<bbbl:label key='lbl_cartdetail_update' language='${language}'/>"><bbbl:label key="lbl_cartdetail_update" language="${language}"/></a>
												<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
												<dsp:setvalue paramvalue="wishlist.id" param="giftlistId" />
												<dsp:input bean="GiftlistFormHandler.giftlistId" value="${giftListId}" type="hidden" />
												<dsp:input bean="GiftlistFormHandler.currentItemId" value="${wishListItemId}" type="hidden" />
												<dsp:input bean="GiftlistFormHandler.fromCartPage" type="hidden" value="true" />
												<dsp:input bean="GiftlistFormHandler.productId" type="hidden" value="${prodID}" />
												<dsp:input bean="GiftlistFormHandler.catalogRefIds" type="hidden" value="${skuID}" />
												<%-- Client DOM XSRF | Part -2
												<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL" type="hidden" value="${contextPath}/cart/ajax_handler_cart.jsp" />
												<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsErrorURL" type="hidden" value="${contextPath}/cart/ajax_handler_cart.jsp" /> --%>
												<dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="updateGiftlist" />
												<dsp:input bean="GiftlistFormHandler.updateGiftlistItems" name="btnUpdateSFL" id="btnUpdateSFL${wishListItemId}" type="submit" value="Update" iclass="hidden" />
											</c:if>

											<%-- remove --%>
											<dsp:getvalueof id="giftlistId" param="wishlist.id"/>
											<input type="hidden" name="removeGiftitemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
											<input type="hidden" name="giftlistId" class="frmAjaxSubmitData" value="${giftlistId}" />
											<a href="#" class="remove-item" data-ajax-frmID="frmSaveItemRemove" data-parent-row="#savedItemID_${wishListItemId}" onclick="removeWishList('${prodID}','${skuID }');" title="<bbbl:label key='lbl_sfl_remove' language='${language}'/>"><bbbl:label key="lbl_sfl_remove" language="${language}"/></a>
										</div>

									</div>

									<div class="small-6 large-3 columns no-padding-left">
										<h1 class="price">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${itemPrice * quantity}"/>
											</dsp:include>
										</h1>

										<dsp:getvalueof id="cartQuantity" param="element.quantity"/>
										<dsp:getvalueof id="wishListItemId" param="element.wishListItemId"/>
										<input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />
										<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
										<input type="hidden" name="cartQuantity" class="frmAjaxSubmitData" value="${cartQuantity}" />
										<input type="hidden" name="cartWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
										<input type="hidden" name="iCount" class="frmAjaxSubmitData" value="${currentCount}" />
										<input type="hidden" name="registryId" class="frmAjaxSubmitData" value="${registryId}" />

										<%-- move to cart button --%>
										<c:set var="mveToCart" value="${false}"/>
										<c:if test="${(empty priceMessageVO) or ((not empty priceMessageVO) and (priceMessageVO.inStock))}">
											<c:set var="mveToCart" value="${true}"/>
											<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
												<dsp:param name="value" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
												<dsp:oparam name="false">
													<input name="addItemToOrder" type="button" class="small secondary radius button moveToCart" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${prodID};;;;eVar30=${skuID}|eVar15=Wish List','scAdd')" aria-pressed="false" role="button" />
												</dsp:oparam>
												<dsp:oparam name="true">
													<input name="addItemToOrder" type="button" class="small secondary radius button moveToCart" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${prodID};;;;eVar30=${skuID}|eVar15=Wish List','scOpen,scAdd')" aria-pressed="false" role="button" />
												</dsp:oparam>
											</dsp:droplet>
										</c:if>

										<%-- change store button --%>
										<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
										<c:if test="${bopusoff and (defaultCountry eq 'US' || defaultCountry eq 'Canada') and (not mveToCart)}">
											<dsp:getvalueof id="id" param="element.wishListItemId" />
											<input type="hidden" value="${prodID}" data-dest-class="productId" data-change-store-submit="productId" name="productId" class="productId">
											<input type="hidden" value="${skuID}" data-dest-class="skuId" data-change-store-submit="skuId" name="skuId" class="changeStoreSkuId">
											<input type="hidden" value="${quantity}" data-dest-class="itemQuantity" data-change-store-submit="itemQuantity" name="itemQuantity" class="itemQuantity" id="quantity">
											<input type="hidden" value="${currentCount}" data-dest-class="count" data-change-store-submit="count" name="count" />
											<input type="hidden" value="${contextPath}/cart/ajax_handler_cart.jsp" data-change-store-submit="pageURL" name="pageURL" />
											<c:if test="${not empty registryId}">
												<input type="hidden" value="${registryId}" data-dest-class="registryId" data-change-store-submit="registryId" name="registryId"class="registryId" >
											</c:if>
											<input type="hidden" name="check" value="check" data-dest-class="check" data-change-store-submit="check" />
											<input type="hidden" name="wishlistItemId" value="${id}" data-dest-class="wishlistItemId" data-change-store-submit="wishlistItemId" />
											<!-- <input type="button" class="changeStore small secondary radius button" value="Find in Store" role="button" aria-pressed="false" /> -->
										 
										 <dsp:a iclass="nearbyStores nearby-stores in-store tiny button secondary" href="/tbs/selfservice/find_tbs_store.jsp">
											<dsp:param name="id" value="${prodID}" />
											<dsp:param name="siteId" value="${currentSiteId}" />
											<dsp:param name="registryId" value="${registryId}" />
											<dsp:param name="skuid" value="${skuID}"/>
											<dsp:param name="itemQuantity" value="${quantity}"/>
											<dsp:param name="wishlistItemId" value="${id}"/>
											<bbbl:label key='lbl_pdp_product_find_store' language="${pageContext.request.locale.language}" />
										</dsp:a>
										 </c:if>

										<input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />
										<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
										<input type="hidden" name="regQuantity" class="frmAjaxSubmitData" value="${quantity}" />
										<input type="hidden" name="reqWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
										<input type="hidden" name="currentCount" class="frmAjaxSubmitData" value="${currentCount}" />
										<dsp:getvalueof var="appid" bean="Site.id" />

										<dsp:droplet name="AddItemToGiftRegistryDroplet">
											<dsp:param name="siteId" value="${appid}"/>
											<c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_move_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
											<dsp:oparam name="output">
												<c:set var="sizeValue">
													<dsp:valueof param="size" />
												</c:set>
											</dsp:oparam>
										</dsp:droplet>

<%-- STILL NEED TO UPDATE THIS --%>
										<c:if test="${not flagOff}">
											<dsp:getvalueof var="transient" bean="Profile.transient" />
											<c:choose>
												<c:when test="${transient == 'false' && not isLtlFlag}">
													<c:choose>
														<c:when test="${sizeValue>1}">
															<div class="fr btnAddToRegistrySel">
																<div class="upperCase addToRegistry addToRegistrySel">
																	<div class="select button_select">
																		<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
																		<dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
																			<dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
																			<dsp:tagAttribute name="aria-required" value="false"/>
																			<dsp:droplet name="AddItemToGiftRegistryDroplet">
																				<dsp:param name="siteId" value="${appid}"/>
																				<dsp:oparam name="output">
																				<dsp:option> <bbbl:label key="lbl_move_to_registry"	language="${pageContext.request.locale.language}" /></dsp:option>
																					<dsp:droplet name="ForEach">
																						<dsp:param name="array" param="registrySkinnyVOList" />
																						<dsp:oparam name="output">
																							<dsp:param name="futureRegList" param="element" />
																							<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																							<dsp:getvalueof var="event_type" param="element.eventType" />
																							<dsp:option value="${regId}"  iclass="${event_type}">
																								<dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
																							</dsp:option>
																						</dsp:oparam>
																					</dsp:droplet>
																				</dsp:oparam>
																			</dsp:droplet>
																		</dsp:select>
																		<input class="btnAjaxSubmitSFL hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}');" data-ajax-frmID="frmRegSaveForLater" role="button" aria-labelledby="btnMoveToRegSel${wishListItemId}" aria-pressed="false" />
																	</div>
																</div>
															</div>
														</c:when>
														<c:when test="${sizeValue==1}">
															<dsp:droplet name="AddItemToGiftRegistryDroplet">
																<dsp:param name="siteId" value="${appid}"/>
																<dsp:oparam name="output">
																	<dsp:droplet name="ForEach">
																		<dsp:param name="array" param="registrySkinnyVOList" />
																		<dsp:oparam name="output">
																			<dsp:param name="futureRegList" param="element" />
																			<dsp:getvalueof var="regId" param="futureRegList.registryId" />
																			<dsp:getvalueof var="registryName" param="futureRegList.eventType" />
																			<input type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
																			<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="${regId}" />
																			<input type="hidden"value="${registryName}" name="registryName" class="addItemToRegis omniRegName" />
																			<dsp:setvalue bean="GiftRegistryFormHandler.registryName" value="${registryName}" />
																		</dsp:oparam>
																	</dsp:droplet>
																</dsp:oparam>
															</dsp:droplet>
															<div class="fr addToRegistry">
																<div class="button">
																<input type="hidden" name="reqRegistryId" class="frmAjaxSubmitData omniRegId" value="${regId}" />
																<%-- <input class="moveToReg" name="btnAddToRegistry" type="button" value="Move to Registry"/> --%>
																<input class="btnAjaxSubmitSFL moveToReg" name="123reqRegistryId" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}');" data-ajax-frmID="frmRegSaveForLater" aria-pressed="false" role="button" />
																</div>
															</div>
														</c:when>
													</c:choose>
													<dsp:input bean="GiftRegistryFormHandler.certonaParameter.certonaAppId" value="${appIdCertona}" type="hidden" />
													<dsp:input bean="GiftRegistryFormHandler.certonaParameter.itemId" value="${prodID}" type="hidden" />
													<dsp:input bean="GiftRegistryFormHandler.certonaParameter.quantity" value="${quantity}" type="hidden" />
													<dsp:input bean="GiftRegistryFormHandler.certonaParameter.customerId" value="${userId}" type="hidden" />
													<dsp:input bean="GiftRegistryFormHandler.certonaParameter.pageId" value="${pageIdCertona}" type="hidden" />
													<dsp:input bean="GiftRegistryFormHandler.certonaParameter.price" value="${itemPrice * quantity}" type="hidden" />
													<dsp:input bean="GiftRegistryFormHandler.certonaParameter.registryName" value="${registryName}" type="hidden" />
												</c:when>
											</c:choose>
										</c:if>
<%-- /STILL NEED TO UPDATE THIS --%>
										<input type="submit" name="btnPickUpInStore" id="btnPickUpInStore" class="hidden pickUpInStoreSFL" value="PickUpInStore" role="button" aria-pressed="false" />
									</div>
								</div>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:form>
				</div>
			</div>
		</dsp:oparam>
	</dsp:droplet>


</dsp:page>
