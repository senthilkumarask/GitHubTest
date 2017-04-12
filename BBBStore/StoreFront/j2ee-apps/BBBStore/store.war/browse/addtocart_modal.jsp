<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/ValidateRegistryDroplet" />

	<c:set var="hideInCartPrice" value="false" scope="request" />

	<c:set var="imagePath">
		<bbbc:config key="image_host" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="enableKatoriFlag" scope="request">
		<bbbc:config key="enableKatori" configName="EXIMKeys" />
	</c:set>
	<c:set var="showATCItemPrice" scope="request"><bbbc:config key="showATCItemPrice" configName="ContentCatalogKeys" /></c:set>
	<dsp:getvalueof var="appid" bean="Site.id" />
  	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
  	
  	 <dsp:droplet name="Switch">
	   <dsp:param name="value" bean="Profile.transient"/>
	       <dsp:oparam name="false">
		     <dsp:getvalueof var="userId" bean="Profile.id"/>
	       </dsp:oparam>
	       <dsp:oparam name="true">
		     <dsp:getvalueof var="userId" value=""/>
	       </dsp:oparam>
      </dsp:droplet>
	<div id="addtocart_rr">  </div>
	<div id="addModal">
		<div class="addedToCartText">
			<span class="icon-checkmark" aria-hidden='true'></span>
			<span class="modalHead" aria-hidden='false'><bbbl:label key="lbl_added_to_cart"	language="<c:out param='${language}'/>" /></span>
		</div>
		<div class="addCartModalCheckout">
			<div id="cartAdded">
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" bean="ShoppingCart.current.commerceItems" />
					<dsp:param name="sortProperties" value="-lastModifiedDate" />
					<dsp:param name="elementName" value="commerceItem" />
					<dsp:oparam name="output">
					<dsp:getvalueof var="commerceItem"	param="commerceItem" />
					<c:choose>
						<c:when test="${empty linkStringNonRecproduct}">
							<c:set var="linkStringNonRecproduct" value="${commerceItem.auxiliaryData.productId}"></c:set>
						</c:when>
						<c:otherwise>
							<c:set var="linkStringNonRecproduct" value="${linkStringNonRecproduct};${commerceItem.auxiliaryData.productId}"></c:set>
						</c:otherwise>
					</c:choose>
						<c:choose>
							<c:when test="${commerceItem.incartPriceItem}">
								<dsp:droplet name="ValidateRegistryDroplet">
									<dsp:param name="registryId" value="${commerceItem.registryId}" />
									<dsp:param name="profile" bean="Profile" />
									<dsp:oparam name="valid">
										<c:choose>
											<c:when test="${fromRegOwnerName}">
												<c:set var="hideInCartPrice" value="false" scope="request" />
											</c:when>
											<c:otherwise>
												<c:set var="hideInCartPrice" value="true" scope="request" />
												<c:set var="hideOrderSubtotal" value="true" scope="request" />
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
									<dsp:oparam name="inValid">
										<c:set var="hideInCartPrice" value="true" scope="request" />
										<c:set var="hideOrderSubtotal" value="true" scope="request" />
									</dsp:oparam>
								</dsp:droplet>
							</c:when>
							<c:otherwise>
								<c:set var="hideInCartPrice" value="false" scope="request" />
							</c:otherwise>
						</c:choose>
						<dsp:droplet name="/atg/dynamo/droplet/Compare">
							<dsp:param name="obj1" param="commerceItem.repositoryItem.type" />
							<dsp:param name="obj2" value="bbbCommerceItem" />
							<dsp:oparam name="equal">
								<dsp:getvalueof var="freeShippingMethod"
									param="commerceItem.freeShippingMethod" />
								
								<dsp:getvalueof var="commCount" param="count"></dsp:getvalueof>
								<c:if test="${not empty param.commItemCount and commCount le param.commItemCount}">
								 <dsp:getvalueof var="prodId" param="commerceItem.repositoryItem.productId"/>	
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="commerceItem.repositoryItem.productId" />
										<dsp:param name="itemDescriptorName" value="product" />
										<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										</dsp:oparam>
									</dsp:droplet>
									<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
										<dsp:param name="priceObject" param="commerceItem" />
										<dsp:param name="profile" bean="Profile" />
										<dsp:param name="elementName" value="priceInfoVO" />
										<dsp:oparam name="output">
											<%--<dsp:set aram name="commItem" param="commerceItem" />--%>
											<dsp:setvalue param="commItem" paramvalue="commerceItem"/>
											<c:set var="isEximErrorExists">${commerceItem.eximErrorExists}</c:set>
											<c:if test="${commerceItem.storeSKU}">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
													value="" />
											</c:if>


											<dsp:getvalueof var="displayName"
												param="commerceItem.auxiliaryData.catalogRef.displayName" />
											<dsp:getvalueof var="unitSavedAmount"
												param="priceInfoVO.unitSavedAmount" />
											<dsp:getvalueof var="unitListPrice"
												param="priceInfoVO.unitListPrice" />
											<dsp:getvalueof var="unitSalePrice"
												param="priceInfoVO.unitSalePrice" />
											<dsp:getvalueof var="prodImg"
												param="commerceItem.auxiliaryData.catalogRef.mediumImage">
												<c:if test="${!empty commerceItem.referenceNumber}">
													<c:set var="prodImg">${commerceItem.fullImagePath}</c:set>
												</c:if>
												<c:choose>
													<c:when test="${unitSalePrice gt 0.0}">
														<c:choose>
															<c:when test="${!empty commerceItem.referenceNumber}">
																<c:choose>
																	<c:when test="${not empty prodImg && (empty isEximErrorExists or !isEximErrorExists)}">
																		<dsp:img iclass="prodImg noImageFound"
																			src="${prodImg}" height="63" width="63"
																			alt="${displayName}" />
																	</c:when>
																	<c:otherwise>
																		<dsp:img iclass="prodImg"
																			src="${imagePath}/_assets/global/images/no_image_available.jpg"
																			height="63" width="63"
																			alt="${displayName} $${unitSalePrice}" />
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${empty prodImg}">
																		<dsp:img iclass="prodImg"
																			src="${imagePath}/_assets/global/images/no_image_available.jpg"
																			height="63" width="63"
																			alt="${displayName} $${unitSalePrice}" />
																	</c:when>
																	<c:otherwise>
																		<dsp:img iclass="prodImg noImageFound"
																			src="${scene7Path}/${prodImg}" height="63" width="63"
																			alt="${displayName}" />
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${!empty commerceItem.referenceNumber}">
																<c:choose>
																	<c:when
																		test="${not empty prodImg && !isEximErrorExists && enableKatoriFlag}">
																		<dsp:img iclass="prodImg noImageFound"
																			src="${prodImg}" height="63" width="63"
																			alt="${displayName}" />
																	</c:when>
																	<c:otherwise>
																		<dsp:img iclass="prodImg"
																			src="${imagePath}/_assets/global/images/no_image_available.jpg"
																			height="63" width="63"
																			alt="${displayName} $${unitListPrice}" />
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${empty prodImg}">
																		<dsp:img iclass="prodImg"
																			src="${imagePath}/_assets/global/images/no_image_available.jpg"
																			height="63" width="63"
																			alt="${displayName} $${unitListPrice}" />
																	</c:when>
																	<c:otherwise>
																		<dsp:img iclass="prodImg noImageFound"
																			src="${scene7Path}/${prodImg}" height="63" width="63"
																			alt="${displayName}" />
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</dsp:getvalueof>
											<p class="cartAddedTitle">

												<c:out value="${displayName}" escapeXml="false" />
											</p>
											<%-- end :: BPSI-1178 DSK - Remember LTL Service Option if user clicks LTL item from mini cart   --%>
											<%-- BBBH-3558 DSK - Display message on mini cart if in cart flag is true   --%>
											<%-- BBBH-6359,6360   Fix to show price on mini cart for in-cart items added--%>
											
											<c:if test="${showATCItemPrice}">
											<c:choose>
												<c:when test="${hideInCartPrice}">
												
													<div class="disPriceString">
														<bbbl:label key="lbl_see_price_in_cart"
															language="<c:out param='${language}'/>" />
													</div>
												</c:when>
												<c:when test="${not empty commerceItem.referenceNumber && (isEximErrorExists || !enableKatoriFlag)}">
													<div class="prodPrice">TBD</div>
												</c:when>
												<c:otherwise>
													<div class="prodPrice">
														<c:choose>
															<c:when test="${unitSalePrice gt 0.0}">
																<dsp:valueof value="${unitSalePrice}"
																	converter="currency" />
															</c:when>
															<c:otherwise>
																<dsp:valueof value="${unitListPrice}"
																	converter="currency" />
															</c:otherwise>
														</c:choose>
													</div>
													<c:if test="${unitSalePrice gt 0.0}">
														<p class="cartAddedReg">
															<jsp:useBean id="placeHolderMap"
																class="java.util.HashMap" scope="request" />
															<c:set target="${placeHolderMap}"
																property="unitListPrice" value="${unitListPrice}" />
															<bbbl:label key="lbl_checkout_open_bracket"
																language="<c:out param='${language}'/>" />
															<bbbl:label key="lbl_preview_reg"
																language="<c:out param='${language}'/>" />
															&nbsp;
															<dsp:valueof value="${unitListPrice}"
																converter="currency" />
															<bbbl:label key="lbl_checkout_closing_bracket"
																language="<c:out param='${language}'/>" />
														</p>
														<p class="cartAddedSave">
															<bbbl:label key="lbl_cartdetail_yousave"
																language="${pageContext.request.locale.language}" />
															&nbsp;
															<dsp:valueof param="priceInfoVO.unitSavedAmount"
																number="0.00" converter="currency" />
															&nbsp;
															<bbbl:label key="lbl_checkout_open_bracket"
																language="<c:out param='${language}'/>" />
															&nbsp;
															<dsp:valueof param="priceInfoVO.totalSavedPercentage"
																number="0.00" />
															&nbsp;
															<bbbl:label key="lbl_checkout_percent_sign"
																language="<c:out param='${language}'/>" />
															&nbsp;
															<bbbl:label key="lbl_checkout_closing_bracket"
																language="<c:out param='${language}'/>" />
														</p>
													</c:if>
													<c:set var="BedBathCanadaSite">
														<bbbc:config key="BedBathCanadaSiteCode"
															configName="ContentCatalogKeys" />
													</c:set>
													<c:if test="${currentSiteId == BedBathCanadaSite}">
														<dsp:droplet name="/com/bbb/commerce/order/droplet/EcoFeeApplicabilityCheckDroplet">
															<dsp:param name="skuId" param="commerceItem.catalogRefId" />
															<dsp:oparam name="true">
															<p class="cartAddedEcoFee">
																<bbbl:label key="lbl_cartdetail_elegibleforecofee"
																	language="<c:out param='${language}'/>" />
															</p>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
													<c:if test="${fn:length(freeShippingMethod) gt 0  && not isInternationalCustomer}">
														<div class="prodPrimaryAttribute">
															<bbbl:label key="lbl_orderitems_freeshipping"
																language="${pageContext.request.locale.language}" />
														</div>
													</c:if>
												</c:otherwise>
											</c:choose>
											</c:if>
											<p class="cartAddedQuant"><bbbl:label key="lbl_orderitems_quantity"	language="${pageContext.request.locale.language}" />
														&nbsp;
														<span class="cartQty"><dsp:valueof param="quantity"/></span>
											</p>

											<dsp:droplet name="/atg/dynamo/droplet/Switch">
                                                <dsp:param name="value" param="commerceItem.porchService"/>
                                                <dsp:oparam name="true">
                                                    <p class="porchService">
                                                        <%-- This should be a label --%>
                                                		<span class="icon icon-checkmark"></span>                                                            
                                                		<%-- this should come from the commerce item data {static for the time being}--%>
                                                			${commerceItem.porchServiceType}
                                                    </p>
                                            	</dsp:oparam>
				                             </dsp:droplet> 
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
				<input type="hidden" class="linkStringNonRecproduct" var="linkStringNonRecproduct" value="${linkStringNonRecproduct}">
				<input type="hidden" class="userId" var="userId" value="${userId}">
				
			</div>
			<div class="addCartSummary">
				<span><bbbl:label key="lbl_cart_summary"
						language="${pageContext.request.locale.language}" /></span><br>
				<c:if test="${param.count ne null}">
					<p class="miniCartMessage">
						<c:out value="${param.count}" />
						<bbbl:label key="lbl_orderitems_itemaddedtocart"
							language="${pageContext.request.locale.language}" />
					</p>
				</c:if>
				<div class="padTop_10 clearfix" id="<c:if test="${isInternationalUser}">miniInternational</c:if>">
					<div class="<c:if test="${isInternationalUser and !hideOrderSubtotal}"> widthHundred</c:if>">
						<strong class="miniCartText fl<c:if test="${isInternationalUser}"> internationalmini</c:if>">
							<c:choose>
								<c:when test="${isInternationalUser}">
									<c:choose>
										<c:when test="${!hideOrderSubtotal}">
											<bbbl:label key="lbl_estimated_orderSubtotal"
												language="${pageContext.request.locale.language}" />
											<span class="summaryCount"><dsp:include
													page="/cart/cart_item_count.jsp" /> <bbbl:label
													key="lbl_cartdetail_items"
													language="${pageContext.request.locale.language}" /></span>
										</c:when>
										<c:otherwise>
											<span class="summaryCount"><dsp:include
													page="/cart/cart_item_count.jsp" /> <bbbl:label
													key="lbl_incart_minicart_items"
													language="${pageContext.request.locale.language}" /><span class="disPriceString discountEnableAtc"><bbbl:label	key="atc_incart_order_message"	language="${pageContext.request.locale.language}" /></span></span>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<dsp:include page="/cart/cart_item_count.jsp" />
									&nbsp;
									<c:choose>
										<c:when test="${!hideOrderSubtotal}">
											<bbbl:label key="lbl_minicart_items"
												language="${pageContext.request.locale.language}" />
										</c:when>
										<c:otherwise>
											<bbbl:label key="lbl_incart_minicart_items"
												language="${pageContext.request.locale.language}" /><span class="disPriceString discountEnableAtc"><bbbl:label	key="atc_incart_order_message"	language="${pageContext.request.locale.language}" /></span>
												</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose> <dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasErrorPersonalizedItemDroplet">
								<dsp:param name="order" bean="ShoppingCart.current" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="orderHasErrorPrsnlizedItem"
										param="orderHasErrorPrsnlizedItem" />
								</dsp:oparam>
							</dsp:droplet> <c:if test="${!hideOrderSubtotal}">
								<c:choose>
									<c:when test="${orderHasErrorPrsnlizedItem}">
										<span class="<c:if test="${isInternationalUser}">internationalShipMiniCart</c:if>">
											<bbbl:label key="lbl_cart_tbd"
												language="${pageContext.request.locale.language}" />
										</span>
									</c:when>
									<c:otherwise>
										<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
											<dsp:param name="priceObject" bean="ShoppingCart.current" />
											<dsp:param name="forItemsRawTotal" value="true" />
											<dsp:oparam name="output">
												<span class="<c:if test="${isInternationalUser}">internationalShipMiniCart</c:if>">
													<dsp:valueof param="cartItemsRawTotal" converter="currency" />
												</span>
											</dsp:oparam>
										</dsp:droplet>
									</c:otherwise>
								</c:choose>
							</c:if>
						</strong>
						<p class="cb smallText noMar padTop_5 textRight">
							<c:if test="${!hideOrderSubtotal}">
								<c:choose>
									<c:when test="${isInternationalUser}">
										<bbbl:label key="lbl_international_cart_disclaimer"
											language="${pageContext.request.locale.language}" />
									</c:when>
									<c:otherwise>
										<bbbl:label key="lbl_atc_modal_disclaimer"
											language="${pageContext.request.locale.language}" />
									</c:otherwise>
								</c:choose>
							</c:if>
						</p>
						<div class="miniCartButton button button_active button_active_orange">
							<a href="${contextPath}/store/cart/cart.jsp"><bbbl:label
									key="lbl_view_cart1"
									language="${pageContext.request.locale.language}" /></a>
						</div>
						<div class="clear"></div>
					</div>
					<div class="clear"></div>
				</div>
                <div class="continueShoppingLink">
				<input class="continueShop" type="button" role="button" value="KEEP SHOPPING" data-notify-reg="true">
				</div>

			</div>
		</div>
            <div class="modalBottomDataContainer">
            <img class="loaderBottomSection" width="20" height="20" alt="loading" src="/_assets/global/images/widgets/small_loader.gif"/>
			</div>
		</div>
	
		</dsp:page>
