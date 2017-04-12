<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<dsp:importbean bean="/com/bbb/common/droplet/ListPriceSalePriceDroplet" />
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
	<dsp:importbean bean="/atg/multisite/SiteContext" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/commerce/promotion/ClosenessQualifierDroplet" />
	<dsp:importbean bean="/com/bbb/profile/session/BBBSavedItemsSessionBean" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>

	<%-- Variables --%>
	<dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof id="movedCommerceItemId" bean="CartModifierFormHandler.commerceItemId"/>
	<dsp:getvalueof id="fromWishlist" bean="CartModifierFormHandler.fromWishlist"/>
	<dsp:droplet name="RepriceOrderDroplet">
		<dsp:param value="ORDER_SUBTOTAL" name="pricingOp"/>
	</dsp:droplet>
	<c:set var="customizeCTACodes" scope="request">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<bbb:pageContainer index="false" follow="false">

		<jsp:attribute name="bodyClass">my-account wish-list</jsp:attribute>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">wishlist savedItems myAccount</jsp:attribute>

		<jsp:body>

			<div class="row" id="content">
				<div class="small-12 columns">
					<h1><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/>: <span class="subheader"><bbbl:label key="lbl_save_item" language ="${pageContext.request.locale.language}"/></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage"><bbbl:label key="lbl_myaccount_wish_list" language ="${pageContext.request.locale.language}"/></c:param>
					</c:import>
				</div>
				<div class="small-12 large-9 columns">
					<c:set var="hidden" value=""/>
					<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
		                <dsp:oparam name="output">
			                <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
		            </dsp:oparam>
	                </dsp:droplet>
					<dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
						<dsp:param name="addMSG" value="false"/>
						<dsp:oparam name="empty">
							<c:set var="hidden" value="hidden"/>
						</dsp:oparam>
					</dsp:droplet>
					<c:set var="enableKatoriFlag" scope="request">
					<bbbc:config key="enableKatori" configName="EXIMKeys" />
				</c:set>
					<dsp:include page="/cart/savedItemsHeader.jsp">
						<dsp:param name="isWishList" value="true"/>
					</dsp:include>

					<c:if test="${empty hidden}">
						<dsp:include otherContext="${contextPath}" page="/cart/topLinkCart.jsp">
							<dsp:param name="reqFrom" value="wish"/>
						</dsp:include>
					</c:if>

					<dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
						<dsp:oparam name="empty">
							<dsp:setvalue value="" param="items" />
							<p><bbbt:textArea key="txtarea_saveitem_empty" language ="${pageContext.request.locale.language}"/></p>
						</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:setvalue param="items" paramvalue="giftList"/>
							<dsp:form action="wish_list.jsp" method="post">
								<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL" type="hidden" value="wish_list.jsp" />
								<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsErrorURL" type="hidden" value="wish_list.jsp" />
								<dsp:input bean="GiftlistFormHandler.giftlistId" paramvalue="giftlistId" type="hidden" />
							</dsp:form>

							<div class="row">

								<c:set var="flagOff" value="${false}"/>
								<dsp:droplet name="IsEmpty">
									<dsp:param name="value" param="items" />
									<dsp:oparam name="false">

										<div class="small-12 columns">
											<dsp:include page="/global/gadgets/errorMessage.jsp">
												<dsp:param name="formhandler" bean="CartModifierFormHandler"/>
											</dsp:include>
										</div>

										<div id="saveForLaterContentWrapper" class="small-12 columns">
											<div id="saveForLaterContent" class=" grid_10 alpha omega productListWrapper">
												<dsp:include src="${contextPath}/cart/saveForlaterForms.jsp"/>
												<dsp:form name="frmSavedItems" iclass="frmAjaxSubmit frmSavedItems savedItemRow" action="${contextPath}/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param name="array" param="items" />
														<dsp:param name="reverseOrder" value="true" />
														<dsp:oparam name="outputStart">
															<ul class="row cart-header hide-for-medium-down productsListHeader">
																<li class="wishlistItemDetails medium-6 columns"><strong><bbbl:label key="lbl_save_items" language ="${pageContext.request.locale.language}"/></strong></li>
																<li class="wishlistQuantityDetails medium-3 columns"><strong><bbbl:label key="lbl_save_quan" language ="${pageContext.request.locale.language}"/></strong></li>
																<li class="wishlistTotalDetails medium-3 columns no-padding-left"><strong><bbbl:label key="lbl_save_total" language ="${pageContext.request.locale.language}"/></strong></li>
															</ul>
															<ul class="productsListContent no-margin">
														</dsp:oparam>
														<dsp:oparam name="output">
															<c:set var="flagOff" value="${false}"/>
															<c:set var="bopusoff" value="${false}"/>
															<dsp:getvalueof id="iCount" param="count"/>
															<dsp:getvalueof var="wishListItemId" param="element.wishListItemId" />
															<dsp:getvalueof var="productId" param="element.prodID" />
															<dsp:getvalueof var="catalogRefId" param="element.skuID" />
															<dsp:getvalueof var="registryId" param="element.registryId" />
															<dsp:setvalue paramvalue="element.giftListId" param="giftlistId" />
															<dsp:getvalueof var="quantity" param ="element.qtyRequested"/>
															<dsp:getvalueof var="itemQuantity" param="element.qtyRequested" />
															<dsp:setvalue param="itemId" paramvalue="element.wishListItemId" />
															<dsp:getvalueof id="id" param="element.wishListItemId" />
															<dsp:getvalueof var="priceMessageVO" param="element.priceMessageVO" />
															<dsp:getvalueof var="referenceNumber" param="element.referenceNumber" />
					    					                <dsp:getvalueof var="personalizePrice" param="element.personalizePrice" />
					    					                <dsp:getvalueof var="personalizationOptions" param="element.personalizationOptions" />
											                <dsp:getvalueof var="personalizationOptionsDisplay" param="element.personalizationOptionsDisplay" />
					    					                <dsp:getvalueof var="personalizationDetails" param="element.personalizationDetails" />
					    					                <dsp:getvalueof var="personalizationStatus" param="element.personalizationStatus" />
					    					                <dsp:getvalueof var="fullImagePath" param="element.fullImagePath" />
					    					                <dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath" />
															<dsp:getvalueof id="giftlistId" param="giftlistId" />
															<c:if test="${not empty referenceNumber}">
																<c:if test="${not empty personalizationOptions}">
																<c:set var="cusDet">${eximCustomizationCodesMap[personalizationOptions]}</c:set>
																<input type="hidden" value="${cusDet}" id="customizationDetails" />
																</c:if>
															</c:if>

									    					<c:choose>
																<c:when test="${not empty referenceNumber && personalizationStatus eq 'saved'}">
																	<c:set var="isPersonalizationIncomplete" value="true" />
																</c:when>
																<c:otherwise>
																	<c:set var="isPersonalizationIncomplete" value="false" />
																</c:otherwise>
															</c:choose>
															<c:choose>
																<c:when test="${not empty referenceNumber}">
																	<c:set var="isPersonalized" value="true" />
																</c:when>

																<c:otherwise>
																	<c:set var="isPersonalized" value="false" />
																</c:otherwise>
															</c:choose>
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
															<dsp:getvalueof var="itemCount" param="count"/>
															<c:set var="totalItems" value="${count}"/>
															<dsp:getvalueof var="arraySize" param="size" />
															<c:set var="displaySize">
																<bbbc:config key="sfl_display_size" configName='CartAndCheckoutKeys' />
															</c:set>
															<c:if test="${empty displaySize}">
																<c:set var="displaySize">2</c:set>
															</c:if>
															<c:if test="${itemCount == displaySize+1 && arraySize > displaySize}">
																<li id="savedItemID_BTN" class="savedItemRow showAllBtn">
																	<div class="prodItemWrapper row">
																		<div class="button">
																			<input id="showAllBtn" type="button" value="Show all ${arraySize} items" role="button" aria-pressed="false" aria-labelledby="showAllBtn" />
																		</div>
																	</div>
																</li>
															</c:if>

															<dsp:getvalueof var="arraySize" param="size" />
															<dsp:getvalueof var="currentCount" param="count" />
															<c:set var="lastRow" value="" />

															<c:if test="${arraySize eq currentCount}">
																<c:set var="lastRow" value="lastRow" />
															</c:if>

															<li id="savedItemID_${wishListItemId}" class="${lastRow}  changeStoreItemWrap savedItemRow saved-item <c:if test="${not empty registryId}">registeryItem</c:if>   <c:if test="${itemCount > displaySize && arraySize > displaySize}"> hidden </c:if>">

																<dsp:getvalueof var="productVO" param="element.productVO"/>
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
																		<dsp:param name="skuId" value="${catalogRefId}"/>
																		<dsp:oparam name="output">
																			<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
																			<dsp:getvalueof var="skuDisplayName" param="pSKUDetailVO.displayName" />
																			<dsp:getvalueof var="skuImageURL" param="pSKUDetailVO.skuImages.mediumImage" />
																			<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color" />
																			<dsp:getvalueof var="skuSize" param="pSKUDetailVO.size" />
																			<dsp:getvalueof var="isLtlFlag" param="pSKUDetailVO.ltlItem" />
																			<dsp:getvalueof var="inCartFlag" param="pSKUDetailVO.inCartFlag" />
																			<dsp:getvalueof var="customizableRequired" param="pSKUDetailVO.customizableRequired" />
																            <dsp:getvalueof var="personalizationType" param="pSKUDetailVO.personalizationType" />
																            <dsp:getvalueof var="customizableCodes" param="pSKUDetailVO.customizableCodes" />
																		</dsp:oparam>
																	</dsp:droplet>

																	<dsp:getvalueof var="prodImage" value="${skuImageURL}" />
																	<dsp:getvalueof var="prodName" value="${skuDisplayName}" />

																	<!-- LTL Alert  -->
																	<c:if test="${isLtlFlag && empty ltlShipMethod}" >
																		<div class="ltlItemAlert alert alert-info">
																			<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>
																		</div>
																	</c:if>

																	<!-- LTL Alert  -->
																	<dsp:include otherContext="${contextPath}" page="/cart/itemLinkWishlist.jsp">
																		<dsp:param name="id" value="${wishListItemId}"/>
																		<dsp:param name="fromwishlist" value="true"/>
																		<dsp:param name="image" value="${prodImage}"/>
																		<dsp:param name="displayName" value="${prodName}"/>
																		<dsp:param name="priceMessageVO" value="${priceMessageVO}"/>
																		<dsp:param name="isPersonalized" value="${isPersonalized}" />
																		<dsp:param name="enableKatoriFlag" value="${enableKatoriFlag}" />
																	</dsp:include>

																	<dsp:droplet name="TBSItemExclusionDroplet">
																		<dsp:param name="siteId" value="${applicationId}"/>
																		<dsp:param name="skuId" value="${catalogRefId}"/>
																		<dsp:oparam name="output">
																		<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																		<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																		<dsp:getvalueof var="reasonCode" param="reasonCode"/>
																		</dsp:oparam>
																	</dsp:droplet>

																	<c:if test="${not empty registryId}">
																		<dsp:getvalueof var="registryVO" param="element.registryVO"/>
																		<dsp:droplet name="GetRegistryTypeNameDroplet">
																			<dsp:param name="siteId" value="${applicationId}"/>
																			<dsp:param name="registryTypeCode" value="${registryVO.registryType.registryTypeName}"/>
																			<dsp:oparam name="output">
																				<c:set var="registryTypeName"><dsp:valueof param="registryTypeName"/></c:set>
																			</dsp:oparam>
																		</dsp:droplet>
																		<div class="registeryItemHeader ">
																			<span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>
																			<strong>${registryVO.primaryRegistrant.firstName}<c:if test="${not empty registryVO.coRegistrant.firstName}">&nbsp;&amp;&nbsp;${registryVO.coRegistrant.firstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong>
																			<span>${registryTypeName}</span>&nbsp;<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
																		</div>
																	</c:if>

																	<div class="prodItemWrapper row">
																		<ul class="no-margin">
																			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
																				<dsp:param name="id" value="${productId}" />
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

																			<li class="wishlistItemDetails small-12 large-6 columns no-padding">
																			 <div class="wishlistItemsList">
																				<c:choose>
																					<c:when test="${not flagOff}">
																						<dsp:a iclass="small-4 large-4 columns prodImg no-padding block" title="${prodName}" page="${finalUrl}?skuId=${catalogRefId}">
																						<c:choose>
																						 <c:when test="${not empty referenceNumber}">
																							<c:choose>
																								<c:when test="${not empty thumbnailImagePath && enableKatoriFlag}">
																								  <img class="fl productImage" src="${thumbnailImagePath}" alt="prodName" title="prodName" height="146" width="146" />
																				                </c:when>
																				                <c:otherwise>
																					              <img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${prodName}" title="${prodName}" height="146" width="146" />
																				                </c:otherwise>
																			                </c:choose>
																		                 </c:when>
																		                 <c:otherwise>
																						    <c:choose>
																								<c:when test="${empty prodImage}">
																									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" title="${prodName}" class="fl productImage" />
																								</c:when>
																								<c:otherwise>
																									<img src="${scene7Path}/${prodImage}" width="146" height="146" alt="${prodName}" title="${prodName}" class="fl productImage noImageFound" />
																								</c:otherwise>
																							</c:choose>
																						</c:otherwise>
																					</c:choose>
																						</dsp:a>
																					</c:when>
																					<c:otherwise>
																						<span class="fl padLeft_10 block">
																						<c:choose>
																							<c:when test="${empty skuImageURL}">
																								<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" title="${prodName}"  class="fl productImage" />
																							</c:when>
																							<c:otherwise>
																								<img src="${scene7Path}/${skuImageURL}" width="146" height="146" alt="${prodName}" title="${prodName}" class="fl productImage noImageFound" />
																							</c:otherwise>
																						</c:choose>
																						</span>
																					</c:otherwise>
																				</c:choose>

                                                                          </div>
																				<div class="small-8 large-8 columns">
																					<span class="prodInfo block breakWord">
																						<span class="prodName">
																							<c:choose>
																								<c:when test="${not flagOff}">
																									<dsp:a page="${finalUrl}?skuId=${catalogRefId}" iclass="productName" title="${prodName}">
																										<dsp:valueof value="${prodName}" valueishtml="true" />
																									</dsp:a>
																								</c:when>
																								<c:otherwise>
																									<span class="disableText"><dsp:valueof value="${skuDisplayName}" valueishtml="true" /></span>
																								</c:otherwise>
																							</c:choose>
																						</span>
																						<span class="prodAttribsSavedItem">
																							<c:if test='${not empty skuColor}'><c:choose><c:when test="${rollupAttributesFinish == 'true'}"><bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/> : </c:when><c:otherwise><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : </c:otherwise></c:choose><dsp:valueof value="${skuColor}" valueishtml="true" /></c:if>
																							<c:if test='${not empty skuSize}'><c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" /></c:if>
																							<c:set var="rollupAttributesFinish" value="false" />
																						</span>
																						<span class="skuNum"><bbbl:label key="lbl_sfl_sku" language="<c:out param='${language}'/>"/> #${catalogRefId}</span>

																						<ul class="prodDeliveryInfo squareBulattedList">
																							<dsp:droplet name="IsProductSKUShippingDroplet">
																								<dsp:param name="siteId" value="${applicationId}"/>
																								<dsp:param name="skuId" value="${catalogRefId}"/>
																								<dsp:param name="prodId" value="${productId}"/>
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
																							</dsp:droplet>
																							<dsp:getvalueof var="prodName" value="" />
																							<dsp:getvalueof var="prodImage" value="" />
																						</ul>
																						<c:if test='${enableKatoriFlag && not empty personalizationOptions}'>
																						<div class="personalizationAttributes">
																						<span class="exim-code">${eximCustomizationCodesMap[personalizationOptions]} : </span>
																						<span class="exim-detail"><dsp:valueof value="${personalizationDetails}" valueishtml="true" />
																						</span>
																						<c:choose>
																							<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
																								<c:set var="editPersonalizeTxt">
																									<bbbl:label key="lbl_sfl_customization_edit" language="<c:out param='${language}'/>"/>
																								</c:set>
																							</c:when>
																							<c:otherwise>
																								<c:set var="editPersonalizeTxt">
																									<bbbl:label key="lbl_sfl_personalization_edit" language="<c:out param='${language}'/>"/>
																								</c:set>
																							</c:otherwise>
																						</c:choose>
																						<c:if test="${isPersonalizationIncomplete}">
																		                    <div class="personalizeLinks">
																			                  <a href="#" class="editPersonalizationSfl bold"
																				                 title="${editPersonalizeTxt}"
		                                                                                  	data-sku="${skuVO.skuId}" data-custom-vendor="${productVO.vendorInfoVO.vendorName}" data-refnum="${referenceNumber}" data-quantity="${quantity}" data-wishlistId="${giftlistId}" data-wishlistitemId="${wishListItemId}">
																				 			${editPersonalizeTxt}</a>
		                                                                           	        </div>

			                                                                                </c:if>
			                                                                                </div>
																						</c:if>


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
																										<div class="small-12 columns product-reviews no-padding-left">
																											<div class="prodReviews ratingsReviews clearfix writeReview">
																												<a title="Write a review" href="/tbs/product/anthology-trade-jolie-window-treatment/212239?skuId=&amp;categoryId=12198&amp;writeReview=true">Write a review</a>
																											</div>
																										</div>
																									</c:when>
																									<c:otherwise>
																										<div class="prodReview  marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																											<dsp:a  page="${finalUrl}?skuId=${catalogRefId}&showRatings=true">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></dsp:a>
																										</div>
																									</c:otherwise>
																								</c:choose>
																							</c:if>
																						</c:if>

																					<c:set var="itemPrice" />
			                                                                        <dsp:droplet name="ListPriceSalePriceDroplet">
			                                                                            <dsp:param name="productId" value="${productId}" />
			                                                                            <dsp:param name="skuId" value="${catalogRefId}" />
			                                                                            <dsp:oparam name="output">
			                                                                                <dsp:getvalueof var="listPriceD" param="listPrice" />
			                                                                                <dsp:getvalueof var="salePriceD" param="salePrice" />
			                                            									 <dsp:getvalueof var="inCartPriceD" param="inCartPrice" />
			                                                                            </dsp:oparam>
			                                                                        </dsp:droplet>

		                                                                 			<c:set var="itemPrice">${listPriceD}</c:set>
			                                                                        <c:choose>
			                                                                            <c:when test="${(not empty salePriceD) && (salePriceD gt 0.0)}">
																							<dsp:getvalueof var="itemPriceD" value="${salePriceD}" vartype="java.lang.Double"/>
																							<c:set var="itemPrice" value="${salePriceD}"/>
			                                                                            </c:when>
			                                                                            <c:otherwise>
																							<dsp:getvalueof var="itemPriceD" value="${listPriceD}" vartype="java.lang.Double"/>
																							<c:set var="itemPrice" value="${listPriceD}"/>
			                                                                            </c:otherwise>
			                                                                        </c:choose>
			                                                                        <%-- BBBH-2890:start --%>
																					<c:if test="${inCartFlag}">
																						<c:set var="itemPrice" value="${inCartPriceD}"/>
																					</c:if>
																					<%-- end --%>
			                                                                        <dsp:getvalueof var="itemPriceD" value="${itemPrice}" vartype="java.lang.Double"/>
			                                                                        <c:choose>
																					   <c:when test="${personalizationType eq 'PY'}">
																						 <dsp:getvalueof var="PersonalPrice" value="${personalizePrice + itemPriceD}" vartype="java.lang.Double"/>
																					   </c:when>
																					   <c:when test="${personalizationType eq 'CR'}">
																						  <dsp:getvalueof var="PersonalPrice" value="${personalizePrice}" vartype="java.lang.Double"/>
																					   </c:when>
																					   <c:otherwise>
																						  <dsp:getvalueof var="PersonalPrice" value="${itemPriceD}" vartype="java.lang.Double"/>
																					   </c:otherwise>
																				   </c:choose>

																				 <c:set var="PersonalizedPrice" value="${PersonalPrice}"/>
																				 <c:choose>
				        															<c:when test="${not empty referenceNumber}">
																					 <c:choose>
																					 		<c:when test="${!enableKatoriFlag}">
																					 		<span class="prodPrice">
																					 			<bbbl:label key="lbl_cart_tbd" language ="${pageContext.request.locale.language}"/>
																					 		</span>
																					 		</c:when>
																					 		<c:when test="${isPersonalizationIncomplete eq 'true' && not empty personalizationType &&  personalizationType eq 'CR' && PersonalizedPrice <= 0.01}">
																							<span class="prodPrice">
																								<c:choose>
																									<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
																										<bbbl:label key='lbl_price_is_tbd_customize' language="${pageContext.request.locale.language}"/>
																									</c:when>
																									<c:otherwise>
																										<bbbl:label key='lbl_price_is_tbd_tbs' language="${pageContext.request.locale.language}"/>
																									</c:otherwise>
																								</c:choose>
																							</span>
																							</c:when>
																							<c:otherwise>
																							<div class="personalizationAttributes">
																								<span class="prodPrice"><dsp:valueof value="${PersonalizedPrice}" number="0.00" converter="currency"/></span>

																								<c:if test="${isPersonalizationIncomplete eq 'true' &&  personalizationType != 'PB'}">
																									<span class= "pricePersonalization"><bbbl:label key="lbl_sfl_personalization_price" language="<c:out param='${language}'/>"/></span>
																								</c:if>
																							</div>
																							</c:otherwise>
																					 	</c:choose>
																			           </c:when>
																			         <c:otherwise>
																				        <span class="prodPrice"><dsp:valueof value="${PersonalizedPrice}" number="0.00" converter="currency"/></span>
																			         </c:otherwise>
																		      </c:choose>

				        													<c:if test="${not empty referenceNumber && enableKatoriFlag}">
																			  <c:if test="${not empty personalizationOptions}">
																				<div class="personalizationAttributes">
																				  <div class="pricePersonalization">
																					<c:choose>
																						<c:when test='${isPersonalizationIncomplete eq false && not empty personalizationType && personalizationType == "PY"}'>
																						 <dsp:valueof value="${personalizePrice}" number="0.00" converter="currency" />&nbsp;<bbbl:textArea key="txt_sfl_personal_price_msg"
																								language="<c:out param='${language}'/>" />
																					    </c:when>
																					    <c:when test='${isPersonalizationIncomplete eq false && not empty personalizationType && personalizationType == "CR"}'>
																						 <dsp:valueof value="${personalizePrice}" number="0.00" converter="currency" />&nbsp;<c:choose>
																																												<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
																																													<bbbl:textArea key="txt_pdp_cr_customize_katori_price"
																																															language="<c:out param='${language}'/>" />
																																												</c:when>
																																												<c:otherwise>
																																													<bbbl:textArea key="txt_pdp_cr_katori_price"
																																															language="<c:out param='${language}'/>" />
																																												</c:otherwise>
																																											</c:choose>
																					    </c:when>
																						<c:when
																							test='${not empty personalizationType && personalizationType == "PB"}'>
																						    <bbbl:label key="lbl_PB_Fee_detail"
																								language="${pageContext.request.locale.language}" />
																						</c:when>
																					</c:choose>
																					</div>
																				</div>
																			</c:if>

																		</c:if>
																		</div>
																			  </li>


																			<!--See if we need this quantity class somewhere in JS <li class="small-12 large-6 columns quantity wishlistQuantityDetails "> -->
																			<li class="small-12 large-6 columns wishlistQuantityDetails">
																				<ul class="prodDeliveryInfo no-margin">
																					<c:if test="${not flagOff}">
																						<li class="quantityBox small-4 large-5 columns quantity no-padding">
																							<label id="lblquantity_text_${catalogRefId}" for="quantity_text_${catalogRefId}" class="hidden">Quantity</label>
																							<div class="qty-spinner">
																								<a class="button minus secondary" title="Decrease Quantity">
																									<span></span>
																								</a>
																									<input name="${wishListItemId}" size="2" type="text" value="${quantity}" id="quantity_text_${catalogRefId}" maxlength="2" title="<bbbl:label key='lbl_quantity_input_box'
																									language="${pageContext.request.locale.language}" />" class="quantity-input moveToCartData itemQuantity fl" aria-required="false" aria-labelledby="lblquantity_text_${catalogRefId}" data-max-value="99" />
																								<a class="button plus secondary" title="Increase Quantity">
																									<span></span>
																								</a>
																							</div>
																							<div class="qty-actions large-7 columns no-margin no-padding">
																								<a class="qty-update small-12 columns no-padding" title="Update" data-parent-row="#savedItemID_${wishListItemId}" data-submit-button="#btnUpdate${wishListItemId}" href="#" onclick="updateWishList('${productId}','${skuId }');">Update</a>
																								<dsp:setvalue param="itemId" paramvalue="element.wishListItemId" />
					                                                                            <dsp:setvalue param="skuId" paramvalue="element.skuId" />
					                                                                            <input type="hidden" value="/tbs/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" name="/atg/commerce/gifts/GiftlistFormHandler.removeItemsFromGiftlistSuccessURL"/>
					                                                                            <input type="hidden" value="/tbs/wishlist/wish_list.jsp" name="/atg/commerce/gifts/GiftlistFormHandler.removeItemsFromGiftlistErrorURL"/>
					                                                                            <dsp:input bean="GiftlistFormHandler.removeGiftitemIds" paramvalue="itemId" type="hidden" />
					                                                                            <dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="itemJustMovedToSaveAjax" />
					                                                                            <%-- Client DOM XSRF | Part -2
					                                                                            <dsp:input bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL" type="hidden" value="/tbs/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" />
					                                                                            <dsp:input bean="GiftlistFormHandler.updateGiftlistItemsErrorURL" type="hidden" value="/tbs/wishlist/wish_list.jsp" /> --%>
					                                                                            <dsp:input bean="GiftlistFormHandler.giftlistId" paramvalue="giftlistId" type="hidden" />
					                                                                            <%-- <dsp:input bean="GiftlistFormHandler.currentItemId" paramvalue="itemId" type="hidden" /> --%>
					                                                                            <!-- <input type="hidden" value=" " name="_D:btnUpdate"> -->
                                                                                    			<dsp:input bean="GiftlistFormHandler.updateGiftlistItems" id="btnUpdate${wishListItemId}" type="submit" value="Update" iclass="hidden " priority="-9999" />
					                                                                            <dsp:getvalueof param="itemId" id="itemId" />
					                                                                            <dsp:getvalueof param="skuId" id="skuId" />
					                                                                            <dsp:getvalueof id="giftlistId" param="giftlistId"/>
																		                        <input type="hidden" name="removeGiftitemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
																		                        <input type="hidden" name="giftlistId" class="frmAjaxSubmitData" value="${giftlistId}" />
																								<a class="remove-item small-12 columns no-padding" title="Remove" data-parent-row="#savedItemID_${wishListItemId}" data-ajax-frmid="frmSaveItemRemove" href="#" onclick="removeWishList('${productId}','${skuId }');">Remove</a>
																								<!--ki-243 <li class="save-item" title="Save for Later" onclick="omniAddToList(this, '1041297643', '41297643', '1')" data-parent-row="#cartItemID_${catalogRefId}" data-ajax-frmid="frmCartSaveForLater" href="#">Save for Later</li> -->
																							</div>
																						</li>
																					</c:if>
																				</li>

																				<li class="wishlistTotalDetails small-8 large-6 columns no-padding">
																					<ul>
																						<li class="prodInfo">
																						  <c:set var="totalPrice" value="${quantity * PersonalPrice}"/>
		                                                                                    <span class="prodPrice">
		                                                                                     <c:choose>
		                                                                                        <c:when test="${not empty referenceNumber && !enableKatoriFlag}">
		                                                                                         <bbbl:label key="lbl_cart_tbd" language ="${pageContext.request.locale.language}"/>
		                                                                                        </c:when>
		                                                                                        <c:when test="${isPersonalizationIncomplete eq 'true' && not empty personalizationType &&  personalizationType eq 'CR' && totalPrice <= 0.01}">
		                                                                                           <bbbl:label key="lbl_dsk_pdp_price_is_tbd" language ="${pageContext.request.locale.language}"/>
		                                                                                        </c:when>
		                                                                                        <c:otherwise>
		                                                                                        <c:set var="totalPriceOmni"> <dsp:valueof value="${totalPrice}" number="0.00" converter="unformattedCurrency"/></c:set>
		                                                                                         <dsp:valueof value="${totalPrice}" number="0.00" converter="currency" />
																						         <input type="hidden" name="totalPriceOmni" class="frmAjaxSubmitData" value="${totalPriceOmni}" />
		                                                                                     </c:otherwise>
		                                                                                   </c:choose>

		                                                                                </span>
		                                                                                <div class="clear"></div>

																						</li>

																						 <c:if test="${isLtlFlag}">
																				  <dsp:getvalueof var="ltlShipMethodDesc" param="element.ltlShipMethodDesc"/>
																					<dsp:getvalueof var="deliverySurcharge" param="element.deliverySurcharge"/>
																					<dsp:getvalueof var="assemblyFee" param="element.assemblyFees"/>
																					<dsp:getvalueof var="shipMethodUnsupported" param="element.shipMethodUnsupported"/>
																					<span class="ltlPriceInfo">+
																					<c:choose>
																		              <c:when test="${empty ltlShipMethod or shipMethodUnsupported}">
																					<span class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/></span>
																					<span class="deliverypriceLtl">
																						<span class="deliverypriceLtlClass"> TBD  </span>
																					</span>

																						</c:when>
																						 <c:otherwise>

																						 <span class="deliveryLtl">${ltlShipMethodDesc}</span>
																						 <span class="deliverypriceLtl">
																						 <span class="deliverypriceLtlClass">
																						 <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
						                                                                    <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
						                                                                    </span>
																						</span>

																						</c:otherwise>
																						  </c:choose>
																						  <c:if test="${assemblyFee gt 0.0 }">

																						  <span class="deliveryLtl">  <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/></span>
																						  <span class="deliverypriceLtl"><span class="deliverypriceLtlClass">  <dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/> </span>
																						   </span>

							                                                                </c:if>
							                                                               </span>
							                                                                </c:if>
																						<c:set var="mveToCart" value="${false}"/>
																						<c:if test="${(empty priceMessageVO) or ((not empty priceMessageVO) and (priceMessageVO.inStock))}">
																							<dsp:getvalueof id="cartQuantity" param="element.qtyRequested"/>
																							<dsp:getvalueof id="wishListItemId" param="element.wishListItemId"/>
																							<dsp:getvalueof var="ltlShipMethodToSend" param="element.ltlShipMethod"/>
																							<input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${productId}" />
																							<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${catalogRefId}" />
																							<input type="hidden" name="cartQuantity" class="frmAjaxSubmitData" value="${cartQuantity}" />
																							<input type="hidden" name="cartWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
																							<input type="hidden" name="cartRegistryId" class="frmAjaxSubmitData" value="${registryId}" />
																							<input type="hidden" name="iCount" class="frmAjaxSubmitData" value="${iCount}" />
																							<input type="hidden" name="refNum"  class="frmAjaxSubmitData" value="${referenceNumber}" />
																							<input type="hidden" name="registryId" class="frmAjaxSubmitData" value="${registryId}" />
																							<input type="hidden" name="personalizationType"  class="frmAjaxSubmitData" value="${personalizationType}" />
																							<input type="hidden" name="customizableCodes"  class="frmAjaxSubmitData" value="${customizableCodes}" />
																							<c:if test="${ltlShipMethodToSend eq 'LWA'}">
																							<dsp:getvalueof var="ltlShipMethodToSend" value="LW"/>
																							<input type="hidden" name="whiteGloveAssembly"  class="frmAjaxSubmitData" value="true" />
																						</c:if>
																						<c:if test="${ltlShipMethodToSend eq 'LW'}">
																							<input type="hidden" name="whiteGloveAssembly" class="frmAjaxSubmitData" value="false" />
																						</c:if>

																								<input type="hidden" name="prevLtlShipMethod"  class="frmAjaxSubmitData" value="${ltlShipMethodToSend}" />

																							<input type="hidden" name="ltlShipMethod"  class="frmAjaxSubmitData" value="${ltlShipMethodToSend}" />
																							<input type="hidden" name="ltlDeliveryServices"  class="frmAjaxSubmitData" value="" />

																							<c:set var="mveToCart" value="${true}"/>
																							<li>
																								<c:choose>
																									<c:when test="${isItemExcluded || caDisabled || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}">
																										<div class="button_disabled">
																											<input name="addItemToOrder" type="button" disabled="disabled" value="MOVE TO CART" class="tiny button service expand btnAjaxSubmitSFL moveToCart "  role="button" aria-pressed="false" />
																										</div>
																										<div style="color:red;font-weight:bold;">
																		
                                    																		<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
																												<dsp:param name="reasonCode" value="${reasonCode}"/>
																											</dsp:include>
                                																		</div>
																									</c:when>
																									<c:otherwise>
																										<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
																											<dsp:param name="value" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
																											<dsp:oparam name="false">
																												<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="tiny button service expand btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${productId};;;;event72=${quantity}|event73=${totalPriceOmni};eVar30=${catalogRefId}|eVar15=Wish List','scAdd,event72,event73',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}')" role="button" aria-pressed="false" />
																											</dsp:oparam>
																											<dsp:oparam name="true">
																												<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="tiny button service expand btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${productId};;;;event72=${quantity}|event73=${totalPriceOmni};eVar30=${catalogRefId}|eVar15=Wish List','scOpen,scAdd,event72,event73',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}')" role="button" aria-pressed="false" />
																											</dsp:oparam>
																										</dsp:droplet>
																									</c:otherwise>
																								</c:choose>
																							</li>
																						</c:if>

																						<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
																						<c:if test="${bopusoff and (defaultCountry eq 'US') and (not mveToCart)}">
																							<li class="wishlistTotalDetails ">
																								<c:choose>
																									<c:when test="${empty prodImage}">
																										<img src="${imagePath}/_assets/global/images/no_image_available.jpg" class="productImage hidden" width="146" height="146" alt="${prodName}" title="${prodName}" />
																									</c:when>
																									<c:otherwise>
																										<img src="${scene7Path}/${prodImage}" width="146" height="146" alt="${prodName}" title="${prodName}" class="fl productImage noImageFound hidden" />
																									</c:otherwise>
																								</c:choose>
																								<a class="prodName productName hidden" title="${prodName}" href="${contextPath}/${finalUrl}">${prodName}</a>
																								<input type="hidden" value="${productId}" data-dest-class="productId" class="productId" data-change-store-submit="productId" name="productId">
																								<input type="hidden" value="${catalogRefId}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
																								<input type="hidden" name="count" value="${iCount}" data-dest-class="count" data-change-store-submit="count" />
																								<c:if test="${not empty registryId}">
																									<input type="hidden" value="${registryId}" data-dest-class="registryId" class="registryId" data-change-store-submit="registryId" name="registryId">
																								</c:if>
																								<input id="quantity" type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity">
																								<input type="hidden" name="pageURL" value="${contextPath}/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" data-change-store-submit="pageURL"/>
																								<input type="hidden" name="check" value="check" data-dest-class="check" data-change-store-submit="check" />
																								<input type="hidden" name="wishlistItemId" value="${id}" data-dest-class="wishlistItemId" data-change-store-submit="wishlistItemId" />
																								<input type="button" class="changeStore" value="Find in Store" role="button" aria-pressed="false" >
																							</li>
																						</c:if>

																						<%-- Move to Registry STARTS --%>
																						<c:if test="${not flagOff}">
                                                                                <li>
                                                                                    <dsp:getvalueof var="registryId" param="registryId" />
                                                                                    <dsp:getvalueof var="regId" param="registryId" />
                                                                                  <%--   <dsp:input bean="GiftRegistryFormHandler.skuId" value="${catalogRefId}" type="hidden" />
		                                                                            <dsp:input bean="GiftRegistryFormHandler.productId" value="${productId}" type="hidden" />
		                                                                            <dsp:input bean="GiftRegistryFormHandler.quantity" type="hidden" paramvalue="element.qtyRequested" />
		                                                                            <dsp:input bean="GiftRegistryFormHandler.movedItemIndex" value="${itemCount}" type="hidden" />
                                                                                    <dsp:input id="moveItemFromSaveForLater" bean="GiftRegistryFormHandler.moveItemFromSaveForLater" type="hidden" value="true" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.wishListItemId" paramvalue="element.wishListItemId" type="hidden" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.fromWishListPage" value="${true}" type="hidden" />
                                                                                    <dsp:input bean="GiftRegistryFormHandler.moveToRegistryResponseURL" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" type="hidden" /> --%>

                                                                                    <dsp:getvalueof var="quantity" param="element.qtyRequested" />
                                                                                    <dsp:getvalueof var="wishListItemId" param="element.wishListItemId" />
                                                                                    <input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${productId}" />
																					<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${catalogRefId}" />
																					<input type="hidden" name="regQuantity" class="frmAjaxSubmitData" value="${quantity}" />
																					<input type="hidden" name="reqWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
																					<input type="hidden" name="currentCount" class="frmAjaxSubmitData" value="${itemCount}" />
                                                                                    <dsp:getvalueof var="applicationId" bean="Site.id" />
                                                                                    <dsp:getvalueof var="transient" bean="Profile.transient" />
                                                                                    <dsp:getvalueof var="userId" bean="Profile.id"/>
                                                                                    <c:choose>
                                                                                        <c:when test="${transient == 'false'}">
                                                                                        <dsp:droplet name="AddItemToGiftRegistryDroplet">
	                                                                                        <dsp:param name="siteId" value="${applicationId}"/>
	                                                                                    </dsp:droplet>
	                                                                                        <c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_move_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
	                                                                                        <dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
																							<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
																							<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
	                                                                                            <c:choose>
                                                                                                <c:when test="${sizeValue>1}">
                                                                                                    <div class="fr btnAddToRegistrySel">
                                                                                                        <div class="upperCase addToRegistry addToRegistrySel">
                                                                                                            <div class="select button_select">
                                                                                                                <dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
                                                                                                                <select id="selRegistryId" name="reqRegistryId" class="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData" data-submit-button="btnMoveToRegSel${wishListItemId}"
                                                                                                                				<c:if test='${isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}'> disabled="disabled"</c:if> aria-required="false">
                                                                                                                    <%-- <dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
                                                                                                                    <dsp:tagAttribute name="aria-required" value="false"/> --%>
                                                                                                                    <option><bbbl:label key="lbl_move_to_registry" language="${pageContext.request.locale.language}" /></option>
                                                                                                                    <dsp:droplet name="ForEach">
                                                                                                                    	<dsp:param name="array" value="${registrySkinnyVOList}" />
                                                                                                                        <dsp:oparam name="output">
                                                                                                                        	<dsp:param name="futureRegList" param="element" />
                                                                                                                            <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                                                                            <dsp:getvalueof var="event_type" param="element.eventType" />
                                                                                                                            <option value="${regId}"  class="${event_type}">
                                                                                                                            	<dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
                                                                                                                           	</option>
                                                                                                                    	</dsp:oparam>
                                                                                                                 	</dsp:droplet>
                                                                                                                </select>
                                                                                                                <input type="hidden" name="reqRegistryId" class="frmAjaxSubmitData" value="${regId}" />
                                                                                                                 </div>
                                                                                                            <div class="clear"></div>
                                                                                                        </div>
                                                                                                        <div class="clear"></div>
                                                                                                        <input class="btnAjaxSubmitSFL hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" onclick="omniAddToRegis(this, '${productId}', '${quantity}', '${itemPrice}', '${catalogRefId}',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}');" role="button" aria-pressed="false" aria-describedby="btnMoveToRegSel${wishListItemId}" />
                                                                                                    </div>
                                                                                                    <div class="clear"></div>
                                                                                                </c:when>
                                                                                                <c:when test="${sizeValue==1}">
                                                                                                    <dsp:droplet name="ForEach">
                                                                                                    	<dsp:param name="array" value="${registrySkinnyVOList}" />
                                                                                                        	<dsp:oparam name="output">
                                                                                                            	<dsp:param name="futureRegList" param="element" />
                                                                                                                <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                                                                <dsp:getvalueof var="registryName" param="futureRegList.eventType" />
                                                                                                                <input  type="hidden" value="${regId}" name="registryId" class="addItemToRegis" />
                                                                                                                <input  type="hidden" value="${registryName}" name="registryName" class="addItemToRegis omniRegName" />
                                                                                                                <dsp:setvalue bean="GiftRegistryFormHandler.registryName" value="${registryName}" />
                                                                                                         	</dsp:oparam>
                                                                                                   	</dsp:droplet>
                                                                                                    <div class="fr addToRegistry">
                                                                                                        <div class="<c:if test='${isItemExcluded || caDisabled || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}'>button_disabled</c:if>">
	                                                                                                        <input type="hidden" name="reqRegistryId" class="frmAjaxSubmitData omniRegId" value="${regId}" />
	    	                                                                                                <%-- <input class="moveToReg" name="btnAddToRegistry" type="button" value="Move to Registry"/> --%>
		                                                                                                    <input class="btnAjaxSubmitSFL moveToReg tiny button expand transactional" name="123reqRegistryId" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" onclick="omniAddToRegis(this, '${productId}', '${quantity}', '${itemPrice}', '${catalogRefId}',${isPersonalized},'${eximCustomizationCodesMap[personalizationOptions]}');" role="button" aria-pressed="false" <c:if test='${isItemExcluded || caDisabled || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}'>disabled="disabled"</c:if>/>
                                                                                                        </div>
                                                                                                    </div>
                                                                                                </c:when>
                                                                                            </c:choose>
                                                                                        <dsp:getvalueof var="certonaQuantity" param="element.qtyRequested" />
                                                                            			<dsp:input bean="GiftRegistryFormHandler.certonaParameter.certonaAppId" value="${appIdCertona}" type="hidden" />
																						<dsp:input bean="GiftRegistryFormHandler.certonaParameter.itemId" value="${productId}" type="hidden" />
																						<dsp:input bean="GiftRegistryFormHandler.certonaParameter.quantity" paramvalue="element.qtyRequested" type="hidden" />
																						<dsp:input bean="GiftRegistryFormHandler.certonaParameter.customerId" value="${userId}" type="hidden" />
																						<dsp:input bean="GiftRegistryFormHandler.certonaParameter.pageId" value="" type="hidden" />
																						<dsp:input bean="GiftRegistryFormHandler.certonaParameter.price" value="${itemPrice * certonaQuantity}" type="hidden" />
																						<dsp:input bean="GiftRegistryFormHandler.certonaParameter.registryName" value="${registryName}" type="hidden" />
                                                                                        </c:when>
                                                                                    </c:choose>
                                                                                    <div class="clear"></div>
                                                                                </li>
																				</c:if>
																						<%-- Move to Registry ENDS --%>
																					</ul>
																				</li>
																			</ul>
																		</div>
																	</li>
																</dsp:oparam>
																<dsp:oparam name="outputEnd">
																	</ul>
																</dsp:oparam>
															</dsp:droplet>
														</dsp:form>
													</div>
												</div>
											</dsp:oparam>
											<dsp:oparam name="true">
												<div id="saveForLaterBody">
													<div id="saveForLaterEmptyMessaging">
														<p class="p-secondary">
															<bbbt:textArea key="txtarea_saveitem_empty" language ="${pageContext.request.locale.language}"/>
														</p>
													</div>
												</div>
											</dsp:oparam>
										</dsp:droplet>
									</div>
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</div>

			<c:import url="/selfservice/find_in_store.jsp" >
				<c:param name="enableStoreSelection" value="true"/>
			</c:import>

			<c:import url="/_includes/modules/change_store_form.jsp" >
				<c:param name="action" value="${contextPath}/_includes/modules/cart_store_pickup_form.jsp"/>
			</c:import>

			<script type="text/javascript">
				BBB.accountManagement = true;
			</script>
			<dsp:getvalueof bean="GiftlistFormHandler.omnitureStatus" id="omnitureStatus"/>
			<c:if test="${omnitureStatus eq 'updated'}">
				<script type="text/javascript">
					BBB.tracking = 'updatedWishList';
				</script>
			</c:if>
			<c:if test="${omnitureStatus eq 'removed'}">
				<script type="text/javascript">
					BBB.tracking = 'removedWishList';
				</script>
			</c:if>

			</div>

			<script type="text/javascript">
				function omniAddToRegis(elem, prodId, qty, itemPrice, skuId, isPersonalized, personalizationType){
					var wrapper = $(elem).closest('.savedItemRow'),
						regData = wrapper.find('.omniRegData').find('option:selected'),
						regId = (regData[0] ? regData.attr('value') : wrapper.find('.omniRegId').val()) || "",
						regName = (regData[0] ? regData.attr('class') : wrapper.find('.omniRegName').val()) || "",
						regProdString = ';' + prodId + ';;;event22=' + qty + '|event23=' + itemPrice + ';eVar30=' + skuId;

					if (typeof addItemWishListToRegistry === "function") { addItemWishListToRegistry(regProdString, regName, regId, isPersonalized, personalizationType); }
				}
			</script>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
					s.pageName = 'My Account>Wish List';
					if (BBB.tracking && BBB.tracking === 'updatedWishList') {
						s.events='event35';
					} else if (BBB.tracking && BBB.tracking === 'removedWishList') {
						s.events='event34';
					} else {
						s.events='event37';
					}
					s.channel = 'My Account';
					s.prop1='My Account';
					s.prop2='My Account';
					s.prop3='My Account';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					var s_code = s.t();
					if (s_code)
						document.write(s_code);
				}

			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
