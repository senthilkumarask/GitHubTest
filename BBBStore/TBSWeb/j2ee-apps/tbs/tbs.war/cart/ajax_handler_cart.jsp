<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:droplet name="RepriceOrderDroplet">
		<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
	</dsp:droplet>
	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBContinueShoppingDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/atg/commerce/promotion/ClosenessQualifierDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GroupCommerceItemsByShiptimeDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/cart/BBBCartItemCountDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBCartDisplayDroplet"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/cart/droplet/DisplayExpressCheckout"/>
	<dsp:importbean bean="/com/bbb/commerce/cart/droplet/CheckFlagOffItemsDroplet"/>
	 <dsp:importbean bean="/atg/commerce/order/droplet/ValidateClosenessQualifier" />
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof var="cartState" value="${0}"/>
	<dsp:getvalueof var="shiptime" value=""/>
	<c:if test="${currentState ne cartState}">
		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="CART"/>
	</c:if>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
	<c:set var="lbl_checkout_checkout" scope="page">
		<bbbl:label key="lbl_checkout_checkout" language="${pageContext.request.locale.language}" />
	</c:set>
	<dsp:include page="/includes/pageIncludeTags.jsp"/>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="lbl_cartdetail_movetowishList" scope="page">
		<bbbl:label key="lbl_cartdetail_movetowishList" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
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

	<c:set var="language" value="<c:out param='${language}' />" />

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

	<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
		 <dsp:oparam name="output">
			<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
		 </dsp:oparam>
	</dsp:droplet>

	<%-- pay pal button dependding upon its ltl flag or item in order --%>
	<dsp:droplet name="OrderHasLTLItemDroplet">
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="orderHasLTLItem" param="orderHasLTLItem" />
		</dsp:oparam>
	</dsp:droplet>

	<%-- cart count --%>
	<dsp:droplet name="BBBCartItemCountDroplet">
		<dsp:param name="shoppingCart" bean="ShoppingCart.current" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="cartCount" param="commerceItemCount"/>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_us"/></c:set>
			</c:when>
		<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_ca"/></c:set>
		</c:otherwise>
	</c:choose>

	<div id="response">

		<%-- KP COMMENT START: mini cart count --%>
		<c:set var="numItems"><%@ include file="/cart/cart_item_count.jsp" %></c:set>
		<dsp:a page="/cart/cart.jsp" iclass="cart-btn">
			<span class="cart-icon"></span>
			<c:if test="${numItems > 0}">
				(<span id="cartItems"><c:out value="${numItems}" /></span>)
			</c:if>
		</dsp:a>
		<%-- KP COMMENT END --%>

		<div id="cartBody">

		<%-- KP COMMENT START: for development only --%>
			<script type="text/javascript">
				var resx = new Object();
				var productIdsCertona = '';
				var eventTypeCertona = '';
			</script>

			<dsp:getvalueof id="countNo" bean="GiftlistFormHandler.countNo"/>
			<dsp:getvalueof id="itemMovedFromCartShipTime" bean="GiftlistFormHandler.shipTime"/>
			<dsp:getvalueof id="itemMoveFromCartID" bean="GiftlistFormHandler.itemMoveFromCartID"/>
			<dsp:getvalueof id="moveOperation" bean="GiftlistFormHandler.moveOperation"/>
			<dsp:getvalueof id="quantityWish" bean="GiftlistFormHandler.quantity" />
			<dsp:getvalueof id="storeId" bean="GiftlistFormHandler.storeId" />
			<dsp:getvalueof id="movedCommerceItemId" bean="CartModifierFormHandler.commerceItemId"/>
			<dsp:getvalueof id="fromWishlist" bean="CartModifierFormHandler.fromWishlist"/>
			<dsp:getvalueof id="quantityCart" bean="CartModifierFormHandler.quantity" />

			<dsp:droplet name="CartRegistryInfoDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
			</dsp:droplet>
			<c:if test="${not empty itemMoveFromCartID}">
				<c:set var="movedCommerceItemId" value="${null}"/>
			</c:if>

			<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
			<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>

			<%-- <dsp:droplet name="CartRegistryInfoDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
			</dsp:droplet> --%>

			<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList"/>

			<c:if test="${empty errorMap}">
				<dsp:include page="/global/gadgets/errorMessage.jsp">
					<dsp:param name="formhandler" bean="CartModifierFormHandler"/>
				</dsp:include>
			</c:if>

			<%-- Undo Error --%>
			<dsp:getvalueof id="undoCheck" bean="CartModifierFormHandler.undoCheck"/>
			<c:if test="${undoCheck}">
				<dsp:droplet name="ErrorMessageForEach">
					<dsp:param bean="CartModifierFormHandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="outputStart">
						<span class="hidden undoFailure"></span>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>

			<div id="cartContentWrapper">
				<dsp:droplet name="IsEmpty">
					<dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
					<dsp:oparam name="true">
						<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
						<dsp:setvalue paramvalue="wishlist.giftlistItems" param="items" />
						<div id="cartBody">

							<!-- Cart Top Message -->
							<div id="cartTopMessaging">
								<dsp:include page="topLinkCart.jsp" />
							</div>

							<!-- Empty Cart Message -->
							<div id="cartEmptyMessaging" class="row">
								<div class="small-12 columns">
									<dsp:droplet name="BBBContinueShoppingDroplet">
										<dsp:oparam name="output">
											<dsp:getvalueof var="linkURL" param="continue_shopping_url" />
											<dsp:droplet name="Switch">
												<dsp:param name="value" bean="Profile.transient"/>
												<dsp:oparam name="false">
													<c:set var="firstVisit" scope="session" value=""/>
													<p>
														<c:if test="${empty cartCount || cartCount <= 0}">
															<strong><bbbl:label key="lbl_cartdetail_emptycart" language="${language}"/></strong>&nbsp;&nbsp;
															</c:if>
														<c:set var="continueshoppinglink"> <bbbl:label key="lbl_cartdetail_continueshoppinglink" language="${language}" /></c:set>
														<dsp:a href="${linkURL}" title="${continueshoppinglink}" iclass="button tiny secondary continue-shopping">
															<bbbl:label key="lbl_cartdetail_continueshoppinglink" language="${language}"/>
														</dsp:a>
													</p>
												</dsp:oparam>
												<dsp:oparam name="true">
													<c:set var="firstVisit" scope="session" value=""/>
													<p>
														<jsp:useBean id="emptyCartMsgLinks" class="java.util.HashMap" scope="request"/>
														<c:set target="${emptyCartMsgLinks}" property="continueShoppingLink">${linkURL}</c:set>
														<c:set target="${emptyCartMsgLinks}" property="loginLink">${contextPath}/account/Login</c:set>
														<c:choose>
															<c:when test="${not empty cartCount && cartCount > 0}">
																<bbbl:textArea key="txt_cartdetail_save_item_msg" language="${language}" placeHolderMap="${emptyCartMsgLinks}"/>
															</c:when>
															<c:otherwise>
																<bbbl:textArea key="txt_cartdetail_empty_cart_msg" language="${language}" placeHolderMap="${emptyCartMsgLinks}"/>
															</c:otherwise>
														</c:choose>
													</p>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									</dsp:droplet>
								</div>
							</div>
						</div>
					</dsp:oparam>
					<dsp:oparam name="false">

						<%-- Cart Top Message --%>
						<div id="cartTopMessaging">
							<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
							<dsp:setvalue paramvalue="wishlist.giftlistItems" param="items" />
							<dsp:include page="topLinkCart.jsp"/>
 							<dsp:droplet name="ValidateClosenessQualifier">
								<dsp:param name="order" bean="ShoppingCart.current" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="result" param="result"/>
	                                </dsp:oparam>
                                </dsp:droplet>
								<c:if test="${result}">
									<dsp:droplet name="ClosenessQualifierDroplet">
										<dsp:param name="type" value="shipping" />
										<dsp:param name="order" bean="ShoppingCart.current" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="closenessQualifier" param="closenessQualifiers"/>
											<dsp:param name="qualifier" value="${closenessQualifier[0]}" />
											<dsp:getvalueof param="qualifier.name" var="promoName" />
											<dsp:getvalueof param="qualifier.upsellMedia.type" var="promoImageType" />
											<c:if test="${promoImageType ne 3 }">
												<dsp:getvalueof param="qualifier.upsellMedia.url" var="promoImage" />
												<img alt="${promoName}" src="${promoImage}" />
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
							   </c:if>
						</div>

						<div id="cartContent" class="productListWrapper">

							<div class="row cart-header hide-for-medium-down productsListHeader">
								<div class="medium-4 columns">
									<h3><bbbl:label key="lbl_cartdetail_item" language="${language}"/></h3>
								</div>
								<div class="medium-2 columns">
									<h3><bbbl:label key="lbl_cartdetail_quantity" language="${language}"/></h3>
								</div>
								<div class="medium-3 columns">
									<h3><bbbl:label key="lbl_cartdetail_deliverydetails" language="${language}"/></h3>
								</div>
								<div class="medium-3 columns no-padding-left">
									<dl class="totals">
										<dt><h3><bbbl:label key="lbl_cartdetail_totalprice" language="${language}"/></h3></dt>
									</dl>
								</div>
							</div>

							<dsp:include page="/cart/cartForms.jsp"/>

							<dsp:form formid="cartForm" id="cartForm" method="post" iclass="frmAjaxSubmit" action="ajax_handler_cart.jsp">
								<c:set var="isOutOfStock" value="${false}"/>
								<c:set var="displayDeliverySurMayApply" value="${false}"/>
								<c:set var="shipmethodAvlForAllLtlItem" value="${true}"/>

									<dsp:getvalueof var="userActiveRegList" bean="SessionBean.values.userActiveRegistriesList" vartype="java.util.Map"/>

									<dsp:droplet name="BBBCartDisplayDroplet">
										<dsp:param name="order" bean="ShoppingCart.current" />
										<dsp:param name="fromCart" value="true" />
										<dsp:oparam name="output">
																				
										  <dsp:droplet name="GroupCommerceItemsByShiptimeDroplet">
		                                    <dsp:param name="commerceItemList" param="commerceItemList"/>
		                                    <dsp:param name="itemMovedFromCartShipTime" value="${itemMovedFromCartShipTime}"/>
		                                    <dsp:oparam name="output">
		                                        <dsp:getvalueof var="movedSFLItemShipTimePresent" param="movedSFLItemShipTimePresent"/>
		                                        <dsp:droplet name="ForEach">
		                                            <dsp:param name="array" param="itemsByShipTime" />
		                                            <dsp:param name="elementName" value="groupedItemsList" />
		                                            <dsp:oparam name="output">
		                                          
		                                                <dsp:getvalueof param="key" var="shiptime"/>                                                    
		                                                 
		                                                <%-- shipping group header --%>
		                                                <div class="row collapse no-padding ship-group" data-ship-group="${shiptime}">
		                                                    <div class="small-12 columns">
		                                                        <c:set var="tbs_ship_time">
		                                                            <c:choose>
		                                                                <c:when test="${shiptime eq '0001'}">
		                                                                    <bbbl:label key="lbl_tbs_ship_time_0001" language="${pageContext.request.locale.language}" />
		                                                                </c:when>
		                                                                <c:when test="${shiptime eq '0002'}">
		                                                                    <bbbl:label key="lbl_tbs_ship_time_0002" language="${pageContext.request.locale.language}" />
		                                                                </c:when>
		                                                                <c:when test="${shiptime eq '0003'}">
		                                                                    <bbbl:label key="lbl_tbs_ship_time_0003" language="${pageContext.request.locale.language}" />
		                                                                </c:when>
		                                                                <c:when test="${shiptime eq '0004'}">
		                                                                    <bbbl:label key="lbl_tbs_ship_time_0004" language="${pageContext.request.locale.language}" />
		                                                                </c:when>
		                                                                <c:when test="${shiptime eq '0005'}">
		                                                                    <bbbl:label key="lbl_tbs_ship_time_0005" language="${pageContext.request.locale.language}" />
		                                                                </c:when>
		                                                                <%-- Added for RM # 33071 Start --%>
		                                                                <c:when test="${shiptime eq '0006'}">
		                                                                    <bbbl:label key="lbl_tbs_kr_ship_time_0006" language="${pageContext.request.locale.language}" />
		                                                                </c:when>
		                                                                <c:when test="${shiptime eq '0007'}">
		                                                                    <bbbl:label key="lbl_tbs_kr_ship_time_0007" language="${pageContext.request.locale.language}" />
		                                                                </c:when>
		                                                                <%-- Added for RM # 33071 End --%>
		                                                                <c:otherwise>
		                                                                    <c:out value="${shiptime}"></c:out>	                                                                    
		                                                                </c:otherwise>
		                                                            </c:choose>
		                                                        </c:set>
		                                                        <p class="divider">
		                                                            ${tbs_ship_time}
		                                                            <c:if test="${HolidayMessagingOn}">
		                                                                <dsp:include src="${contextPath}/common/holidayMessaging.jsp">
		                                                                    <dsp:param name="timeframe" value="${shiptime}"/>
		                                                                        <dsp:param name="tbsShipTime" value="${tbs_ship_time}"/>
		                                                                    <dsp:param name="appendtoLeadTime" value="true"/>
		                                                                    </dsp:include>
		                                                            </c:if>
		                                                        </p>
		                                                    </div>
		                                                </div>								
										
                                            <div id="cart-items">
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="groupedItemsList" />
												<dsp:param name="elementName" value="commerceItem" />
												<dsp:oparam name="output">
													<c:set var="itemFlagoff" value="${false}"/>
													<c:set var="disableLink" value="${false}"/>
													<dsp:getvalueof var="commItem" param="commerceItem"/>
													<dsp:getvalueof var="arraySize" param="size" />
													<dsp:getvalueof var="currentCount" param="count" />
													<c:set var="lastRow" value="" />
													<c:if test="${arraySize eq currentCount}">
														<c:set var="lastRow" value="lastRow" />
													</c:if>
													<dsp:getvalueof id="priceMessageVO" value="${commItem.priceMessageVO}" />
													<c:set var="isOutOfStock" value="${!priceMessageVO.inStock}"/>												
													
													<c:if test="${not empty priceMessageVO}">
														<c:set var="itemFlagoff" value="${priceMessageVO.flagOff}"/>
														<c:set var="disableLink" value="${priceMessageVO.disableLink}"/>
													</c:if>
													<dsp:getvalueof id="iCount" param="count"/>
													<dsp:getvalueof id="size" param="size"/>
																																																						
													<c:choose>
													<%-- For displaying undoLink above the previous product --%>
													<c:when test="${!undoLinkDisplayed and (iCount eq countNo) and (itemMovedFromCartShipTime eq shiptime)}">
													    <dsp:include page="cartUndoLink2.jsp">
															<dsp:param name="itemMoveFromCartID" value="${itemMoveFromCartID}"/>
															<dsp:param name="moveOperation" value="${moveOperation}"/>
															<dsp:param name="quantity" value="${quantityWish}"/>
															<dsp:param name="storeId" value="${storeId}"/>
															<dsp:param name="countNo" value="${countNo}"/>
														</dsp:include>
														<c:set var="undoLinkDisplayed" value="true"/>
													  </c:when>
													  <%-- For displaying undoLink below the previous product --%>
													  <c:when test="${!undoLinkDisplayed and (iCount eq (countNo-1)) and (itemMovedFromCartShipTime eq shiptime)}">
													  	<c:set var="displayUndoLink" value="true"/>
														<c:set var="undoLinkDisplayed" value="true"/>
													  </c:when>
													  <%-- For displaying undoLink if moved product is last in ship group --%>
													  <c:when test="${not empty itemMoveFromCartID and !undoLinkDisplayed and !movedSFLItemShipTimePresent}">
													    <dsp:include page="cartUndoLink2.jsp">
															<dsp:param name="itemMoveFromCartID" value="${itemMoveFromCartID}"/>
															<dsp:param name="moveOperation" value="${moveOperation}"/>
															<dsp:param name="quantity" value="${quantityWish}"/>
															<dsp:param name="storeId" value="${storeId}"/>
															<dsp:param name="countNo" value="${countNo}"/>
														</dsp:include>
														<c:set var="undoLinkDisplayed" value="true"/>
													  </c:when>
													  <c:otherwise>
													  </c:otherwise>
													</c:choose>
													
													<dsp:getvalueof var="commItem" param="commerceItem"/>
													<dsp:getvalueof var="count" param="count"/>
													<dsp:param name="bbbItem" param="commerceItem.BBBCommerceItem.shippingGroupRelationships"/>
													<dsp:getvalueof id="newQuantity" param="commerceItem.BBBCommerceItem.quantity"/>
													<dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id"/>
													<dsp:getvalueof var="commLtlShipMethod" param="commerceItem.BBBCommerceItem.ltlShipMethod"/>
													<dsp:getvalueof id="productIdsCertona" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
													<c:set var="ship_method_avl" value="${true}"/>

													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="bbbItem"/>
														<dsp:param name="elementName" value="shipGrp" />
														<dsp:oparam name="output">
															<dsp:getvalueof id="oldShippingId" param="shipGrp.shippingGroup.id"/>
															<dsp:getvalueof var="shippingMethod" param="shipGrp.shippingGroup.shippingMethod"/>
																<c:if test="${empty shippingMethod}" >
																	<c:set var="ship_method_avl" value="${false}"/>
																	<c:set var="displayDeliverySurMayApply" value="${true}"/>
																	<c:set var="shipmethodAvlForAllLtlItem" value="${false}"/>
																</c:if>
														</dsp:oparam>
													</dsp:droplet>

													<dsp:getvalueof bean="CartModifierFormHandler.undoCheck" id="undoCheck"/>
													<dsp:getvalueof bean="CartModifierFormHandler.itemIdJustMvBack" id="itemIdJustMvBack"/>
													<c:if test="${undoCheck && (itemIdJustMvBack eq commerceItemId)}">
														<c:set var="itemMovedBackToCart" value="itemMovedBackToCart"/>
													</c:if>
													<dsp:getvalueof bean="CartModifierFormHandler.newItemAdded" id="newItemAdded"/>
													<c:if test="${newItemAdded and count == size}">
														<c:set var="itemJustMovedToCart" value="itemJustMovedToCart"/>
													</c:if>

													<div class="row cart-item ${lastRow} <c:if test="${not empty commItem.BBBCommerceItem.registryId}">registeryItem</c:if> cartRow changeStoreItemWrap <c:if test="${count eq 1}">firstItem</c:if>" id="cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}">

														<c:set var="itemMovedBackToCart" value=""/>
														<c:set var="itemJustMovedToCart" value=""/>
														<dsp:include page="itemLinkCart.jsp">
															<dsp:param name="id" value="${commerceItemId}"/>
															<dsp:param name="oldShippingId" value="${oldShippingId}"/>
															<dsp:param name="newQuantity" value="${newQuantity}"/>
															<dsp:param name="image" param="commerceItem.BBBCommerceItem.auxiliaryData.catalogRef.mediumImage"/>
															<dsp:param name="displayName" value="${commItem.skuDetailVO.displayName}"/>
															<dsp:param name="priceMessageVO" value="${commItem.priceMessageVO}"/>
														</dsp:include>

														<!-- LTL Alert  -->
														<c:if test="${ not ship_method_avl && commItem.skuDetailVO.ltlItem && empty commLtlShipMethod}" >
															<div class="small-12 columns           ltlItemAlert alert alert-info">
																<bbbt:textArea key="txt_cart_saved_item_alert" language="${language}"/>
															</div>
														</c:if>
														<!-- LTL Alert  -->

														<%-- TODO: see what this does --%>
														<c:if test="${not empty commItem.BBBCommerceItem.registryId}">
															<c:set var="registryUrl" value="../giftregistry/view_registry_guest.jsp"/>
															<c:if test='${fn:contains(userActiveRegList, commItem.BBBCommerceItem.registryId)}'>
																<c:set var="registryUrl" value="../giftregistry/view_registry_owner.jsp"/>
															</c:if>
															<c:set var="registryFlag" value="true"/>
															<c:set var="RegistryContext" scope="request">${RegistryContext}${commItem.BBBCommerceItem.registryId};</c:set>
															<script type="text/javascript">
																eventTypeCertona = eventTypeCertona + 'shopping+cart+' + '${registryType}' + ';';
																productIdsCertona = productIdsCertona + '${productIdsCertona}' + ';';
															</script>

															<div class="small-12 columns registeryItemHeader">
															<h3>
																	<dsp:getvalueof bean="ShoppingCart.current.registryMap.${commItem.BBBCommerceItem.registryId}" var="registratantVO"/>
																<c:if test="${commItem.BBBCommerceItem.registryInfo ne null}">
																	<span><bbbl:label key="lbl_cart_registry_from_text" language="${language}"/></span>
																	<dsp:getvalueof var="registryInfo" param="commerceItem.BBBCommerceItem.registryInfo"/>
																	<dsp:a href="${registryUrl}">
																		<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}"/>
																		<dsp:param name="eventType" value="${registratantVO.registryType.registryTypeDesc}" />
																		<strong>
																			${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if>
																			<bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/>
																		</strong>
																	</dsp:a>
																	<span>${registratantVO.registryType.registryTypeDesc}</span>&nbsp;<span><bbbl:label key="lbl_cart_registry_text" language="${language}"/></span>
																</c:if>
															</h3>
															</div>
														</c:if>

														<c:if test="${empty commItem.BBBCommerceItem.registryId}">
															<script type="text/javascript">
																productIdsCertona = productIdsCertona + '${productIdsCertona}' + ';';
															</script>
														</c:if>

														<%-- item --%>
														<div class="small-12 large-4 columns">
															<div class="row">
																<dsp:getvalueof var="image" param="commerceItem.skuDetailVO.skuImages.mediumImage"/>
																<dsp:getvalueof var="skuColor" value="${commItem.skuDetailVO.color}" />
																<dsp:getvalueof var="skuSize"  value="${commItem.skuDetailVO.size}" />
																<dsp:getvalueof var="pOpt" value="${commItem.BBBCommerceItem.personalizationOptions}"/>
																<c:set var="CertonaContext" scope="request">${CertonaContext}<dsp:valueof param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>;</c:set>
																<dsp:droplet name="CanonicalItemLink">
																	<dsp:param name="id" param="commerceItem.BBBCommerceItem.repositoryItem.productId" />
																	<dsp:param name="itemDescriptorName" value="product" />
																	<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
																	<dsp:oparam name="output">
																		<dsp:droplet name="BBBPriceDisplayDroplet">
																			<dsp:param name="priceObject" value="${commItem.BBBCommerceItem}" />
																			<dsp:param name="profile" bean="Profile"/>
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
																			</dsp:oparam>
																		</dsp:droplet>
																		<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
																		<c:if test="${itemFlagOff or disableLink}">
																			<c:set var="finalUrl" value="#"/>
																		</c:if>

																		<%-- image --%>
																		<div class="small-4 large-6 columns">
																			<div class="category-prod-img">
																				<c:choose>
																					<c:when test="${itemFlagoff or disableLink}">
																						<c:choose>
																						    <c:when test="${not empty commItem.BBBCommerceItem.referenceNumber && not empty commItem.BBBCommerceItem.fullImagePath && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag}">
                                                                                 		        <img  src="${commItem.BBBCommerceItem.fullImagePath}" alt="${commItem.skuDetailVO.displayName}" title="${commItem.skuDetailVO.displayName}" height="146" width="146" />
                                                                            		        </c:when>
																							<c:when test="${empty image || 'null' == image}">
																								<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																							</c:when>
																							<c:otherwise>
																								<img src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																							</c:otherwise>
																						</c:choose>
																					</c:when>
																					<c:otherwise>
																						<dsp:a iclass="prodImg padLeft_10 block fl" page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																							<c:choose>
																						   <c:when test="${not empty commItem.BBBCommerceItem.referenceNumber && not empty commItem.BBBCommerceItem.fullImagePath && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag}">
                                                                                      		 <img  src="${commItem.BBBCommerceItem.fullImagePath}" alt="${commItem.skuDetailVO.displayName}" title="${commItem.skuDetailVO.displayName}" height="146" width="146" />
                                                                                 		    </c:when>
																								<c:when test="${empty image || 'null' == image}">
																									<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																								</c:when>
																								<c:otherwise>
																									<img class="fl productImage noImageFound" src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
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
																		<div class="small-8 large-6 columns">
																			<c:choose>
																				<c:when test="${itemFlagoff or disableLink}">
																					<div class="product-name"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></div>
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
																							<c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" />
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
																					<div class="facet">
                                                                                    <c:if test="${(commItem.BBBCommerceItem.commerceItemClassType eq 'tbsCommerceItem' 
																									|| commItem.BBBCommerceItem.commerceItemClassType eq 'default')
																									&& commItem.BBBCommerceItem.autoWaiveClassification ne null}">
																							<bbbl:label key="lbl_autowaive_item_code" language="<c:out param='${language}'/>"/> ${commItem.BBBCommerceItem.autoWaiveClassification}
																					</c:if>
																					</div>
																				</c:otherwise>
																			</c:choose>
																			<c:choose>
																				<c:when test="${not empty commItem.BBBCommerceItem.personalizationOptions && fn:contains(customizeCTACodes, commItem.BBBCommerceItem.personalizationOptions)}">
																					<c:set var="customizeTxt" value="true"/>
																				</c:when>
																				<c:otherwise>
																					<c:set var="customizeTxt" value="false"/>
																				</c:otherwise>
																			</c:choose>
																			<c:if test='${not empty commItem.BBBCommerceItem.referenceNumber && commItem.BBBCommerceItem.referenceNumber != null && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag}'>
                                                                                	<div class="personalizationAttributes">
	                                                                                	<c:if test='${enableKatoriFlag && not empty commItem.BBBCommerceItem.personalizationOptions && commItem.BBBCommerceItem.personalizationOptions != null}'>
																							<div> ${eximCustomizationCodesMap[pOpt]} :  ${commItem.BBBCommerceItem.personalizationDetails} </div>
																						</c:if>
																						<div class="pricePersonalization">
																							  <c:choose>
																									<c:when test='${not empty commItem.BBBCommerceItem.personalizePrice && not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "PY"}'>
																										<dsp:valueof value="${commItem.BBBCommerceItem.personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
																									</c:when>
																									<c:when test='${not empty commItem.BBBCommerceItem.personalizePrice && not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "CR"}'>
																										<dsp:valueof value="${commItem.BBBCommerceItem.personalizePrice}" converter="currency"/> <c:choose>
																																															 		<c:when test="${customizeTxt eq true}">
																																															 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																																																						language="${pageContext.request.locale.language}"/>
																																															 		</c:when>
																																															 		<c:otherwise>
																																															 			<bbbl:label key="lbl_exim_cr_added_price"
																																																						language="${pageContext.request.locale.language}"/>
																																															 		</c:otherwise>
																																															 	</c:choose>
																									</c:when>
																									<c:when test='${not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "PB"}'>
																									<div class="priceAddText">
																										<bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
																									</div>
																								     </c:when>
																							  </c:choose>
	                                                                                       </div>
                                                                                       </div>
                                                                                       <c:if test='${empty commItem.BBBCommerceItem.registryId}'>
                                                                                       <div class="personalizeLinks" data-personalization="${eximCustomizationCodesMap[pOpt]}">
                                                                                              <a href="#" class='editPersonalization bold <c:if test="${isInternationalCustomer ||  not empty commItem.BBBCommerceItem.registryId}">disableText</c:if>'
                                                                                               aria-label='<bbbl:label key="lbl_cart_edit_personalization_for" language="${language}"/> ${commItem.skuDetailVO.displayName}' 
                                                                                               title='<bbbl:label key="lbl_cart_personalization_edit" language="${language}"/>' data-custom-vendor="${commItem.vendorInfoVO.vendorName}" data-sku="${commItem.BBBCommerceItem.catalogRefId}" data-refnum="${commItem.BBBCommerceItem.referenceNumber}" data-quantity="${commItem.BBBCommerceItem.quantity}">
																								<bbbl:label key="lbl_cart_personalization_edit" language="<c:out param='${language}'/>"/></a>
                                                                                              <span class="separator"></span>
																							<dsp:getvalueof var="prodId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
                                                                                           <a href="javascript:void(0)" class="removePersonalization bold" aria-label='<bbbl:label key="lbl_cart_remove_personalization_for" language="${language}"/> ${commItem.skuDetailVO.displayName}' onclick="omniRemove('${prodId}','${commItem.BBBCommerceItem.catalogRefId}');"><bbbl:label key="lbl_cartdetail_remove" language="${language}"/> ${eximCustomizationCodesMap[pOpt]}</a>
                                                                                        </div>
                                                                                        </c:if>
																				</c:if>

																		</div>
																	</dsp:oparam>
																</dsp:droplet>
																<c:if test="${null ne commItem.skuDetailVO.displayName}">
																	<c:set var="skuFlag" value="true"/>
																	<script type="text/javascript">
																		eventTypeCertona = eventTypeCertona + 'shopping+cart' + ';';
																	</script>
																</c:if>
															</div>
														</div>

														<%-- quantity --%>
														<div class="small-6 large-5 columns">
															<div class="row">
																<div class="small-12 large-5 columns quantity">

																	<%-- quantity input --%>
																	<div class="qty-spinner quantityBox">
																		<c:choose>
																			<c:when test="${not itemFlagoff}">
																				<a class="button minus secondary" title="Decrease Quantity"><span></span></a>
																				<input data-max-value="99" name="${commItem.BBBCommerceItem.id}" type="tel" value="${commItem.BBBCommerceItem.quantity}" id="quantity_text_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="quantity-input itemQuantity" maxlength="2" aria-required="true" aria-labelledby="lblquantity_text_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" />
																				<a class="button plus secondary" title="Increase Quantity"><span></span></a>
																			</c:when>
																			<c:otherwise>
																				<input data-max-value="99" name="${commItem.BBBCommerceItem.id}" type="hidden" value="${commItem.BBBCommerceItem.quantity}" id="quantity_text_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="quantity-input itemQuantity" maxlength="2"/>
																			</c:otherwise>
																		</c:choose>
																	</div>

																	<%-- quantity actions --%>
																	<div class="qty-actions">
																		<%-- update --%>
																		<c:if test="${not itemFlagoff}">
																			<a href="#" data-parent-row="#cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" class="qty-update" data-submit-button="#btnUpdate${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" title="<bbbl:label key='lbl_cartdetail_update' language='${language}'/>"><bbbl:label key="lbl_cartdetail_update" language="${language}"/></a>
																			<dsp:input type="hidden" bean="CartModifierFormHandler.commerceItemId" value="${commItem.BBBCommerceItem.id}" />
																			<dsp:input type="hidden" bean="CartModifierFormHandler.setOrderSuccessURL" value="${assetDomainName}${contextPath}/cart/ajax_handler_cart.jsp" />
																			<dsp:input type="hidden" bean="CartModifierFormHandler.setOrderErrorURL" value="${contextPath}/cart/ajax_handler_cart.jsp" />
																			<dsp:input name="btnUpdate" id="btnUpdate${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" type="submit" bean="CartModifierFormHandler.setOrderByCommerceId" iclass="hidden" />
																		</c:if>

																		<%-- remove --%>
																		<dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
																		<a href="#" class="remove-item" data-ajax-frmID="frmCartItemRemove" data-parent-row="#cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" onclick="omniRemove('${productId}','${commItem.BBBCommerceItem.catalogRefId}')" title="<bbbl:label key='lbl_cartdetail_remove' language='${language}'/>"><bbbl:label key="lbl_cartdetail_remove" language="${language}"/></a>

																		<%-- save for later --%>
																		<c:if test="${not itemFlagoff}">
																			<dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
																			<c:if test='${not empty commItem.BBBCommerceItem.personalizationOptions && commItem.BBBCommerceItem.personalizationOptions != null}'>
																			    <c:set var="cusDet">${eximCustomizationCodesMap[pOpt]}</c:set>
																			</c:if>
																			<c:choose>
																			  <c:when test='${not empty cusDet}'>
																			    <a href="#" class="save-item btnAjaxSubmitCart" data-ajax-frmID="frmCartSaveForLater" data-parent-row="#cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" onclick="omniAddToListPers(this, '${productId}', '${commItem.BBBCommerceItem.catalogRefId}', '${commItem.BBBCommerceItem.quantity}','${cusDet}');" title="Save for Later">Save for Later</a>
																			   </c:when>
																			   <c:otherwise>
																			<a href="#" class="save-item btnAjaxSubmitCart" data-ajax-frmID="frmCartSaveForLater" data-parent-row="#cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" onclick="omniAddToList(this, '${productId}', '${commItem.BBBCommerceItem.catalogRefId}', '${commItem.BBBCommerceItem.quantity}')" title="Save for Later">Save for Later</a>
																			   </c:otherwise>
																			</c:choose>
																		</c:if>
																	</div>
																</div>

																<div class="small-12 large-7 columns shipping">

																	<%-- shipping radio buttons --%>
																	<dsp:include page="/cart/cart_includes/shippingType.jsp">
																		<dsp:param name="commItem" value="${commItem}"/>
																		<dsp:param name="shipTime" value="${shiptime}"/>
																	</dsp:include>

																	<%-- gift for registrant --%>
																	<dsp:include page="/giftregistry/check_gift_registry.jsp">
																		<dsp:param name="orderObject" bean="ShoppingCart.current" />
																		<dsp:param name="commItem" value="${commItem}"/>
																	</dsp:include>

																</div>
															</div>
														</div>

														<%-- price --%>
														<dsp:include page="/cart/cart_includes/priceDisplay.jsp">
															<dsp:param name="commItem" value="${commItem}"/>
															<dsp:param name="orderObject" bean="ShoppingCart.current" />
														</dsp:include>

														<input type="hidden" name="saveStoreId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.storeId}" />
														<input type="hidden" name="saveCount" class="frmAjaxSubmitData" value="${iCount}" />
														<input type="hidden" name="saveQuantity" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.quantity}" />
														<input type="hidden" name="saveCurrentItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
														<input type="hidden" name="saveCurrentItemShipTime" class="frmAjaxSubmitData" value="${shiptime}" />

														<input type="hidden" name="removeCommerceItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
														<input type="hidden" name="removeSubmitButton" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />

														<input type="hidden" name="shipOldShippingId" class="frmAjaxSubmitData" value="${oldShippingId}" />
														<input type="hidden" name="shipCommerceItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
														<input type="hidden" name="shipNewQuantity" class="frmAjaxSubmitData" value="${newQuantity}" />

														<input type="hidden" name="storeId" class="storeId" value="${commItemStoreId}" data-change-store-storeid="storeId" />
														<input type="hidden" name="productId" class="productId" value="${commItem.BBBCommerceItem.catalogRefId}" data-change-store-submit="prodId" />
														<input type="hidden" name="registryId" class="registryId" value="${commItem.BBBCommerceItem.registryId}" data-change-store-submit="registryId" />
														<c:choose>
														<c:when test="${not empty commItem.BBBCommerceItem.storeId}">
															<input type="hidden" name="changeCurrentStore" class="changeCurrentStore" value="true" data-change-store-submit="changeCurrentStore" />
														</c:when>
														<c:otherwise>
															<input type="hidden" name="changeCurrentStore" class="changeCurrentStore" value="" data-change-store-submit="changeCurrentStore" />
														</c:otherwise>
														</c:choose>
														<input type="hidden" name="skuId" value="${commItem.BBBCommerceItem.catalogRefId}" class="changeStoreSkuId" data-change-store-submit="skuId"/>
														<input type="hidden" name="commerceItemId" value="${commItem.BBBCommerceItem.id}" data-change-store-submit="commerceItemId" />
														<input type="hidden" name="oldShippingId" value="${oldShippingId}" data-change-store-submit="oldShippingId" />
														<input type="hidden" name="newQuantity" value="${newQuantity}" data-change-store-submit="newQuantity" />
														<input type="hidden" name="pageURL" value="${contextPath}/cart/ajax_handler_cart.jsp" data-change-store-submit="pageURL"/>

													</div>

													<%-- cart undo link --%>
													<c:if test="${displayUndoLink}">
														<dsp:include page="cartUndoLink2.jsp">
															<dsp:param name="itemMoveFromCartID" value="${itemMoveFromCartID}"/>
															<dsp:param name="moveOperation" value="${moveOperation}"/>
															<dsp:param name="quantity" value="${quantityWish}"/>
															<dsp:param name="storeId" value="${storeId}"/>
															<dsp:param name="countNo" value="${countNo}"/>
														</dsp:include>
														<c:set var="displayUndoLink" value="false"/>
													</c:if>

												</dsp:oparam>
											</dsp:droplet>
                                            <%-- price override modal inputs --%>
                                            <dsp:input type="hidden" bean="CartModifierFormHandler.overrideId" id="commerceItemId" value="" />
                                            <dsp:input type="hidden" bean="CartModifierFormHandler.overridePrice" id="overridePrice" value="" />
                                            <dsp:input type="hidden" bean="CartModifierFormHandler.overrideQuantity" id="overrideQuantity" value="0" />
                                            <dsp:input type="hidden" bean="CartModifierFormHandler.reasonCode" id="reasonCode" value="" />
                                            <dsp:input type="hidden" bean="CartModifierFormHandler.competitor" id="competitor" value="" />
                                            <dsp:input type="submit" bean="CartModifierFormHandler.itemPriceOverride" id="overrideSubmit" iclass="hidden" value="Override"/>
                                            </div>
										</dsp:oparam>
									</dsp:droplet>
                                        	<%-- End of ForEach Droplet  --%>
							
                                            </dsp:oparam>
                                		   </dsp:droplet>
                                		  <%-- End of GroupCommerceItemsByShiptimeDroplet  --%>
										</dsp:oparam>
									</dsp:droplet>
									<%-- End of BBBCartDisplayDroplet  --%>
							</dsp:form>
							<%-- End of cartForm --%>
						</div>
						<%-- End of cartContent Div --%>
							
					<dsp:include page="/cart/cart_includes/storePromotions.jsp"></dsp:include>

					<%-- Display coupons and total amount details --%>
					<div id="cartItemsTotalAndCouponsWrapper">
						<dsp:getvalueof var="orderType" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
						<c:if test="${not empty orderType && orderType ne null && orderType ne 'ONLINE_ONLY' }">
							<div class="alert small-12 columns">
								<h3><bbbl:label key="lbl_items_for_store_pickup_title" language="${language}"/></h3>
								<p><bbbt:textArea key="txt_items_for_store_pickup_cart" language="${language}"/></p>
							</div>
						</c:if>

						<div class="row cart-total">
							<div class="small-12 large-offset-8 large-4 columns">

								<div class="row">
									<div class="small-12 columns">
										<dl class="totals">
											
											<div class="dl-wrap">
												<dt>
													<h3>
														<dsp:droplet name="BBBCartItemCountDroplet">
															<dsp:param name="shoppingCart" bean="ShoppingCart.current" />
															<dsp:oparam name="output">
																<dsp:valueof param="commerceItemCount"/>
															</dsp:oparam>
														</dsp:droplet>
														<bbbl:label key="lbl_cartdetail_items" language="${language}"/>
													</h3>
												</dt>

												<dsp:droplet name="BBBPriceDisplayDroplet">
													<dsp:param name="profile" bean="Profile"/>
													<dsp:param name="priceObject" bean="ShoppingCart.current" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="storeAmount" param="priceInfoVO.storeAmount"/>
														<dsp:getvalueof var="onlinePurchaseTotal" param="priceInfoVO.onlinePurchaseTotal"/>
														<dsp:getvalueof var="storeEcoFeeTotal" param="priceInfoVO.storeEcoFeeTotal"/>
														<dsp:getvalueof var="onlineEcoFeeTotal" param="priceInfoVO.onlineEcoFeeTotal"/>
														<dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
														<dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
														<dsp:getvalueof var="orderPreTaxAmout" param="priceInfoVO.orderPreTaxAmout"/>
														<dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
														<dsp:getvalueof var="freeShipping" param="priceInfoVO.freeShipping"/>
														<dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
														<dsp:getvalueof var="surchargeSavings" param="priceInfoVO.surchargeSavings"/>
														<dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
														<dsp:getvalueof var="maxDeliverySurchargeReached" param="priceInfoVO.maxDeliverySurchargeReached"/>
														<dsp:getvalueof var="totalAssemblyFee" param="priceInfoVO.totalAssemblyFee"/>
														<dsp:getvalueof var="maxDeliverySurcharge" param="priceInfoVO.maxDeliverySurcharge"/>

														<dd><h3><dsp:valueof value="${storeAmount + onlinePurchaseTotal}" converter="currency"/></h3></dd>


												<c:choose>
													<c:when test="${freeShipping ne true}">
														<div class="dl-wrap">
															<c:choose>
																<c:when test="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
																	<dt><bbbl:label key="lbl_cartdetail_estimatedshipping" language="${language}"/>
																		<dsp:include page="/common/shipping_method_description.jsp"/>
																	</dt>
																	<dd>TBD</dd>
																</c:when>
																<c:otherwise>
																	<dt><bbbl:label key="lbl_cartdetail_estimatedshipping" language="${language}"/>
																		<dsp:include page="/common/shipping_method_description.jsp"/>
																	</dt>
																	<dd><dsp:valueof value="${rawShippingTotal}" converter="currency" number="0.00"/>*</dd>
																</c:otherwise>
															</c:choose>
														</div>
	
														<dsp:getvalueof var="orderAutoWaiveFlag" bean="ShoppingCart.current.autoWaiveFlag"/>
														<dsp:getvalueof var="orderLevelMessage" bean="ShoppingCart.current.autoWaiveClassification"/>
														<c:if test="${orderAutoWaiveFlag && not empty orderLevelMessage}">
                                                        	<div class="dl-wrap">
                                                        		<dt class="savings">${orderLevelMessage}</dt>
																<dd class="savings">(<fmt:formatNumber value="${shippingSavings}"  type="currency"/>)</dd>
															</div>
														</c:if>
														
															<dsp:droplet name="ForEach">
																<dsp:param name="array" param="priceInfoVO.shippingAdjustments" />
																<dsp:param name="elementName" value="shippingPromoDiscount" />
																<dsp:oparam name="outputStart">
																<div class="dl-wrap">
																	<dt class="savings">
																</dsp:oparam>
																<dsp:oparam name="outputEnd">
																	</dt>
																</div>
																</dsp:oparam>
																<dsp:oparam name="output">
																	<c:set var="shipPromo" value="true" />
																	<dsp:getvalueof var="count" param="count" />
																	<dsp:getvalueof var="size" param="size" />
																	<c:choose>
																		<c:when test="${count lt size}" >
																			<dsp:valueof param="key.displayName"/>,
																		</c:when>
																		<c:otherwise>
																			<dsp:valueof param="key.displayName"/>
																		</c:otherwise>
																	</c:choose>
																</dsp:oparam>
															</dsp:droplet>
															<c:if test="${not empty shipPromo}">
																<dd class="savings">(<dsp:valueof value="${shippingSavings}" converter="currency" number="0.00"/>)</dd>
															</c:if>
														
													</c:when>
													<c:otherwise>
														<%-- LTL changes --%>
														<div class="dl-wrap">
														<c:choose>
															<c:when test="${orderHasLTLItem eq true && rawShippingTotal eq 0.0}" >
																<dt><bbbl:label key="lbl_cartdetail_estimatedshipping" language="${language}"/></dt>
																<dd>TBD</dd>
															</c:when>
															<c:otherwise>
																<dt><strong><bbbl:label key="lbl_cartdetail_freeshipping" language="${language}"/></strong></dt>
																<dd></dd>
															</c:otherwise>
														</c:choose>
                                                
                                                        <dsp:getvalueof var="orderAutoWaiveFlag" bean="ShoppingCart.current.autoWaiveFlag"/>
														<dsp:getvalueof var="orderLevelMessage" bean="ShoppingCart.current.autoWaiveClassification"/>
 								                        <c:if test="${orderAutoWaiveFlag && not empty orderLevelMessage}">
                                                        	<div class="dl-wrap">
                                                        		<dt class="savings">${orderLevelMessage}</dt>
																<dd class="savings">(<fmt:formatNumber value="${shippingSavings}"  type="currency"/>)</dd>
															</div>
														</c:if>
														
													</c:otherwise>
												</c:choose>
												<c:if test="${totalSurcharge gt 0.0}">
													<div class="dl-wrap">
														<dt><bbbl:label key="lbl_preview_surcharge" language="${language}"/></dt>
														<dd><dsp:valueof value="${totalSurcharge}" converter="currency"/></dd>
													</div>
												</c:if>
												<c:if test="${surchargeSavings gt 0.0}">
													<div class="dl-wrap">
														<dt class="savings"><bbbl:label key="lbl_surchage_savings" language="${language}"/></dt>
														<dd class="savings">(<dsp:valueof value="${surchargeSavings}" converter="currency"/>)</dd>
													</div>
												</c:if>
												<dsp:getvalueof var="ecoFeeTotal" value="${storeEcoFeeTotal + onlineEcoFeeTotal }"/>
												<c:if test="${ecoFeeTotal gt 0.0}">
													<div class="dl-wrap">
														<dt><bbbl:label key="lbl_preview_ecofee" language="${language}"/></dt>
														<dd><dsp:valueof value="${ecoFeeTotal}" converter="currency"/></dd>
													</div>
												</c:if>

												<%-- Additional info for LTL items summary --%>
												<c:if test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
													<div class="dl-wrap">
														<dt><h3><bbbl:label key="ltl_delivery_surcharge_may_apply" language="${language}"/></h3></dt>
														<dd><h3>TBD</h3></dd>
													</div>
												</c:if>
												<c:if test="${totalDeliverySurcharge gt 0.0 && shipmethodAvlForAllLtlItem}">
													<div class="dl-wrap">
														<dt><h3><bbbl:label key="lbl_cart_delivery_surcharge" language="${language}"/></dt>
														<dd><h3><dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/></dd>
													</div>
												</c:if>
												<c:if test="${maxDeliverySurchargeReached}">
													<div class="dl-wrap">
														<c:choose>
															<c:when test="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
																<dt class="">
																	<bbbl:label key="lbl_cart_max_surcharge_reached" language="${language}"/>
																	<a class="ltlsurcharge" href="/tbs/cart/static/max_surcharges_info.jsp" data-reveal-id="infoModal" data-reveal-ajax="true"><bbbl:label key="lbl_what_this_mean" language="${pageContext.request.locale.language}" /></a>
																</dt>
																<dd class="savings">(TBD)</dd>
															</c:when>
															<c:otherwise>
																<dt class="">
																	<bbbl:label key="lbl_cart_max_surcharge_reached" language="${language}"/> <br>
																	<a class="ltlsurcharge" href="/tbs/cart/static/max_surcharges_info.jsp" data-reveal-id="infoModal" data-reveal-ajax="true"><bbbl:label key="lbl_what_this_mean" language="${pageContext.request.locale.language}" /></a>
																</dt>
																<dd class="savings">(-<dsp:valueof value="${totalDeliverySurcharge - maxDeliverySurcharge}" number="0.00" converter="currency"/>)</dd>
															</c:otherwise>
														</c:choose>
													</div>
												</c:if>
												<c:if test="${totalAssemblyFee gt 0.0}">
													<div class="dl-wrap">
														<dt><h3><bbbl:label key="lbl_cart_assembly_fee" language="${language}"/></h3></dt>
														<dd><h3><dsp:valueof value="${totalAssemblyFee}" number="0.00" converter="currency"/></h3></dd>
													</div>
												</c:if>

												<%-- Additional info for LTL items summary --%>
												<dsp:getvalueof var="preTaxAmout" value="${orderPreTaxAmout + storeAmount + storeEcoFeeTotal }"/>
												<div class="dl-wrap">
													<dt><h1 class="price"><bbbl:label key="lbl_cartdetail_pretaxtotal" language="${language}"/></h1></dt>
													<dd><h1 class="price"><dsp:valueof value="${preTaxAmout}" converter="currency"/></h1></dd>
												</div>
												<c:if test="${totalSavedAmount gt 0.0}">
													<div class="dl-wrap">
													<c:choose>
														<c:when test="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
															<dt><bbbl:label key="lbl_cartdetail_totalsavings" language="${language}"/></dt>
															<dd>TBD</dd>
														</c:when>
														<c:otherwise>
															<dt><bbbl:label key="lbl_cartdetail_totalsavings" language="${language}"/></dt>
															<dd><dsp:valueof value="${totalSavedAmount}" converter="currency"/></dd>
														</c:otherwise>
														</c:choose>
													</div>
												</c:if>
											</dsp:oparam>
										</dsp:droplet>
										</div>
									</dl>
									</div>
									<p class="p-footnote">
										<bbbl:label key="lbl_cartdetail_shippinginfo" language="${language}"/>
										<a href="#" data-reveal-ajax="true" data-reveal-id="infoModal">
										</a>
									</p>

									<dsp:form formid="express_form" method="post" action="${contextPath}/cart/cart.jsp" >

										<div class="row">
											<div class="small-12 columns checkout-type">
												<c:if test="${orderHasLTLItem ne true}">
													<dsp:droplet name="DisplayExpressCheckout">
														<dsp:param name="order" bean="ShoppingCart.current"/>
														<dsp:param name="profile" bean="Profile"/>
														<dsp:oparam name="true">
															<label class="inline-rc checkbox" for="express_checkout">
															<dsp:input id="express_checkout" disabled="true" bean="CartModifierFormHandler.expressCheckout" type="checkbox" name="expire_checkout">
																<dsp:tagAttribute name="aria-checked" value="false"/>
															</dsp:input>
															<span></span>
															<h3><bbbl:label key="lbl_cartdetail_expresscheckout" language="${language}"/></h3>
															</label>
															<p class="p-footnote"><bbbl:label key="lbl_cartdetail_gotoorderreview" language="${language}"/></p>
														</dsp:oparam>
														<dsp:oparam name="false">
															<label class="inline-rc checkbox" for="express_checkout">
																<dsp:input id="express_checkout" bean="CartModifierFormHandler.expressCheckout" type="checkbox" name="expire_checkout">
																	<dsp:tagAttribute name="aria-checked" value="false"/>
																</dsp:input>
																<span></span>
																<h3><bbbl:label key="lbl_cartdetail_expresscheckout" language="${language}"/></h3>
															</label>
															<p class="p-footnote"><bbbl:label key="lbl_cartdetail_gotoorderreview" language="${language}"/></p>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>

												<c:if test="${cartCount > 1}">
													<label class="inline-rc checkbox" for="multi_ship">
			                                           	<c:choose>
															<c:when test="${MapQuestOn}">
																	<dsp:input type="checkbox" bean="CartModifierFormHandler.multiship" value="true" id="multi_ship" name="multi_ship" />
																	<span></span>
																	<h3><bbbl:label key="lbl_tbs_multicheckout_storepickup" language="${language}"/></h3>
															</c:when>
															<c:otherwise>
																	<dsp:input type="checkbox" bean="CartModifierFormHandler.multiship" value="true" id="multi_ship" name="multi_ship" />
																	<span></span>
																	<h3><bbbl:label key="lbl_tbs_multi_check_out" language="${language}"/></h3>
															</c:otherwise>
														</c:choose>
													</label>
												</c:if>

												<dsp:droplet name="CheckFlagOffItemsDroplet">
													<dsp:param name="checkOOSItem" value="${true}"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="showCheckOutBtn" param="flagOffChecked"/>
														<dsp:getvalueof var="itemNotOOS" param="itemOutOfStock"/>
													</dsp:oparam>
												</dsp:droplet>

											</div>
										</div>

										<div class="row">
											<c:choose>
												<c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
													<dsp:input bean="CartModifierFormHandler.fromCart" value="${true}" type="hidden"/>
													<div class="small-12 columns">
														<dsp:input bean="CartModifierFormHandler.tBSCheckout" type="submit" id="tBSCheckout" value="${lbl_checkout_checkout}" iclass="expand button transactional stockAvailable">
															<dsp:tagAttribute name="aria-pressed" value="false"/>
															<dsp:tagAttribute name="role" value="button"/>
															<dsp:tagAttribute name="disabled" value="true"/>
														</dsp:input>
													</div>
												</c:when>
												<c:otherwise>
													<div class="small-12 columns">
														<dsp:input bean="CartModifierFormHandler.tBSCheckout" type="submit" id="tBSCheckout" value="${lbl_checkout_checkout}" disabled="true" iclass="expand button transactional">
															<dsp:tagAttribute name="aria-pressed" value="false"/>
															<dsp:tagAttribute name="role" value="button"/>
														</dsp:input>
													</div>
												</c:otherwise>
											</c:choose>

											<div class="small-6 columns">
												<dsp:droplet name="BBBContinueShoppingDroplet">
													<dsp:oparam name="output">
														<dsp:getvalueof var="linkURL" param="continue_shopping_url" />
														<c:set var="continueshoppinglink"> <bbbl:label key="lbl_cartdetail_continueshoppinglink" language="${language}" /></c:set>
														<dsp:a iclass="button tiny secondary continue-shopping" href="${linkURL}" title="${continueshoppinglink}">
															<bbbl:label key="lbl_cartdetail_continueshoppinglink" language="${language}"/>
														</dsp:a>
													</dsp:oparam>
												</dsp:droplet>
											</div>
										
                                     <div class="small-6 columns">
										<dsp:getvalueof var="orderType" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
										<c:if test="${paypalCartButtonEnable}">
											<c:choose>
												<c:when test="${paypalOn}">
													<c:choose >
														<c:when test="${orderHasLTLItem eq true || (registryFlag eq true)}">
															<div class="paypalCheckoutContainer paypal_disabled ">
																<bbbt:textArea key="txt_paypal_disable_image_new" language ="${pageContext.request.locale.language}"/>
															</div>
															<div class="  billMeLaterLink billMeLaterLink_disabled  disableText">
																<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
															</div>
															<div class=" smallText bold highlightRed"><bbbl:label key="lbl_paypal_not_available_ltl_or_reg_product" language="${pageContext.request.locale.language}" /></div>
														</c:when>
														<c:when test="${(orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID')}">
															<div class="paypalCheckoutContainer paypal_disabled ">
																<bbbt:textArea key="txt_paypal_disable_image_cart" language ="${pageContext.request.locale.language}"/>
															</div>
															<div class="  billMeLaterLink billMeLaterLink_disabled ">
																<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
															</div>
															<div class="error "><bbbl:label key="lbl_paypal_not_avilable_bopus_hybrid" language="${pageContext.request.locale.language}" /></div>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
																<dsp:getvalueof bean="ShoppingCart.current.registryMap" var="registryOrder"/>
																	<c:choose>
																		<c:when test="${isOrderAmtCoveredVar}">
																			<div class="paypalCheckoutContainer  button_disabled">
																				<img src="/_assets/global/images/paypal_disabled.png" class="paypalCheckoutButton">
																			</div>
																			<div class="  billMeLaterLink billMeLaterLink_disabled ">
																				<bbbl:label key="lbl_paypal_bill_me_later" 	language="${pageContext.request.locale.language}" />
																			</div>
																			<div class="error ">
																				<bbbe:error key="err_paypal_zero_balance" language ="${pageContext.request.locale.language}"/>
																			</div>
																		</c:when>
																		  <c:when test="${(registryFlag eq true) }">
																				<div class="paypalCheckoutContainer  button_disabled">
																					<img src="/_assets/global/images/paypal_disabled.png" class="paypalCheckoutButton">
																				</div>
																			</c:when>
																		<c:otherwise>
																			<div class="paypalCheckoutContainer ">
																				<dsp:a href="">
																					<dsp:property bean="CartModifierFormHandler.fromCart" value="${true}" priority="8"/>
																					<dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/cart/cart.jsp" priority="9"/>
																					<dsp:property bean="CartModifierFormHandler.checkoutWithPaypal" value="true" priority="0"/>
																					<img src="<bbbt:textArea key="txt_paypal_image_cart" language =	"${pageContext.request.locale.language}"/>" class="paypalCheckoutButton">
																				</dsp:a>
																			</div>
																			<div class="  billMeLaterLink ">
																				<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																			</div>
																		</c:otherwise>
																	</c:choose>
																</c:when>
																<c:otherwise>
																	<div class="paypalCheckoutContainer paypal_disabled ">
																		<bbbt:textArea key="txt_paypal_disable_image_cart" language ="${pageContext.request.locale.language}"/>
																	</div>
																	<div class="  billMeLaterLink billMeLaterLink_disabled ">
																		<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																	</div>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<div class="paypalCheckoutContainer paypal_disabled ">
														<bbbt:textArea key="txt_paypal_disable_image_cart" language ="${pageContext.request.locale.language}"/>
													</div>
													<div class="  billMeLaterLink billMeLaterLink_disabled ">
														<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
													</div>
													<div class="error "><bbbl:label key="lbl_paypal_not_available" language="${pageContext.request.locale.language}" /></div>
												</c:otherwise>
											</c:choose>
										</c:if>
									</div>
										<!--  end R2.2 pay pal button dependeding upon its flag in bcc   -->
													

										<c:if test="${not (showCheckOutBtn and itemNotOOS and (not isOutOfStock))}">
										  <div class="small-12 columns">
												<span class="error"><bbbl:label key="lbl_cart_outofstockmessage" language="${language}"/></span>
										  </div>
										</c:if>

										<!-- #Scope  : Start International shipping link depending upon its Switch off/on flag in bcc   -->
										<c:if test="${internationalShippingOn}">
										<div class="small-12 columns">
											<c:choose>
												<c:when	test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
													<c:choose>
														<c:when test="${isOrderAmtCoveredVar}">
															<div class="button_disabled">
																<bbbl:label key="lbl_chkout_international_shipping" language="${language}" />
																<img src="/_assets/global/images/international_shipping_help_icon.png" alt="International Shipping Help" />
															</div>
														</c:when>
														<c:otherwise>
															<%-- <div id="intlShippingLink">
																<dsp:a title="International Shipping" iclass="openIntlShippingLink" href="javascript:">
																	<bbbl:label key="lbl_chkout_international_shipping" language="${language}"/>
																</dsp:a>
																<a id="intlShippingHelpIcon" class="info">
																	<img src="/_assets/global/images/international_shipping_help_icon.png" alt="International Shipping Help"/>
																	<span class="textLeft">
																		<bbbt:textArea key="txt_chkout_international_shipping_help" language="${language}"/>
																	</span>
																</a>
															</div> --%>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<div class="expand button secondary small" disabled>
														<bbbl:label key="lbl_chkout_international_shipping" language="${language}" />
														<img src="/_assets/global/images/international_shipping_help_icon.png" alt="International Shipping Help" />
													</div>
												</c:otherwise>
											</c:choose>
											</div>
										</c:if>				

										<!-- Start R2.2   Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal -->
										<c:choose>
											<c:when test="${visaLogoOn || masterCardLogoOn}">
												<div class="small-12 columns visaMasterCardContainer infoMsg">
													<bbbl:label key="lbl_vbv_payment_secure" language="${pageContext.request.locale.language}" />
													<c:if test="${visaLogoOn}">
														<bbbt:textArea key="txt_vbv_logo_visa" language="${language}"/>
													</c:if>
													<c:if test="${visaLogoOn && masterCardLogoOn}">
														<span class="visaLogoSep"></span>
													</c:if>
													<c:if test="${masterCardLogoOn}">
														<bbbt:textArea key="txt_vbv_logo_masterCard" language="${language}"/>
													</c:if>
												</div>
											</c:when>
										</c:choose>
										<!-- End R2.2   Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal -->
										</div>
									</dsp:form>
								</div>
							</div>
						</div>
					</div>
					<%-- End of cartItemsTotalAndCouponsWrapper Div --%>
					
				<%-- quantity for registrant modal --%>
				<div id="qtyForRegistrant" class="reveal-modal medium" data-reveal></div>

				<%-- search for registrant modal --%>
				<div id="searchForRegistrant" class="reveal-modal xlarge reg-search" data-reveal></div>

			</dsp:oparam>
		</dsp:droplet>
		<%-- End of IsEmpty Droplet --%>
		
		<script type="text/javascript">
			$(document).ready(function(){
			if($("#tBSCheckout").hasClass("stockAvailable")){
            	$("#tBSCheckout").removeAttr("disabled");
        	}
		});
		</script>
		</div>
		<%-- End of cartContentWrapper Div --%>
		
		<div id="sflContentWrapper">
		<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.itemId" var="prodIdCertona"/>
		<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.quantity" var="quantityCert"/>
		<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.price" var="prodPriceCertona"  />
		<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.registryName" var="eventType" />
		<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.transactionId" var="registryIdForCertona" />
		<dsp:getvalueof bean="GiftRegistryFormHandler.moveItemFromSaveForLater" var="moveItemFromSaveForLaterChk" />
		<c:if test="${moveItemFromSaveForLaterChk eq true  && not empty registryIdForCertona }">
		<dsp:droplet name="CertonaDroplet">
			<dsp:param name="scheme" value="fc_lmi"/>
			<dsp:param name="context" value="${CertonaContext}"/>
			<dsp:param name="exitemid" value="${RegistryContext}"/>
			<dsp:param name="userid" value="${userId}"/>
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId" scope="request"/>
			</dsp:oparam>
		</dsp:droplet>
		</c:if>
		<dsp:include page="/account/idm/idm_login_checkout.jsp" />

		
		<script type="text/javascript">
			<c:choose>
				<c:when test="${moveItemFromSaveForLaterChk eq true  && not empty registryIdForCertona }">
					var resx = new Object();
					resx.appid = "${appIdCertona}";
					resx.itemid = '${prodIdCertona}';
					resx.pageid = "${pageIdCertona}";
					resx.customerid = "${userId}";
					resx.event = "registry+"+"${eventType}";
					resx.qty = "${quantityCert}";
					resx.price = ${prodPriceCertona};
					resx.transactionid = "${registryIdForCertona}";
					certonaResx.run();
				</c:when>
			</c:choose>
			
			$(document).ready(function(){
				if($("#tBSCheckout").hasClass("stockAvailable")){
	            	$("#tBSCheckout").removeAttr("disabled");
	        	}
			});
		</script>

		<!-- LTL changes divided files in two jsps and passed required parameters for included file  -->
		
		<dsp:include page="ajax_handler_cart_sfl.jsp">
			<dsp:param name="movedCommerceItemId" value="${movedCommerceItemId}"/>
			<dsp:param name="applicationId" value="${applicationId}"/>
			<dsp:param name="fromWishlist" value="${fromWishlist}"/>
			<dsp:param name="quantityCart" value="${quantityCart}"/>
		</dsp:include>
	</div>
	<!-- LTL changes end -->
	</div>
	<%-- End of cartBody Div --%>
	</div>
	<%-- End of Reponse Div --%>
</dsp:page>
