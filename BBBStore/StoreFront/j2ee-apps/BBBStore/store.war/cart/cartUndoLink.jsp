<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
    <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet"/>

    <dsp:getvalueof id="applicationId" bean="Site.id" />
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
    <dsp:getvalueof id="quantity" param="quantity"/>
    <dsp:getvalueof id="countNo" param="countNo"/>
	<dsp:getvalueof id="itemMoveFromCartID" param="itemMoveFromCartID"/>
	<dsp:getvalueof id="moveOperation" param="moveOperation"/>
	<dsp:getvalueof id="storeId" param="storeId"/>
	<dsp:getvalueof id="count" param="count"/>
	<dsp:getvalueof id="btsValue" param="btsValue"/>
	<dsp:getvalueof id="hasPorchServiceRemoved" param="hasPorchServiceRemoved"/>
	<dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
		<dsp:oparam name="output">
			<dsp:droplet name="ForEach">
				<dsp:param name="elementName" value="wishlist" />
				<dsp:param name="array" param="giftList"/>
				<dsp:oparam name="output">
				<dsp:getvalueof var="referenceNumber" param="wishlist.referenceNumber"/>
				<dsp:getvalueof var="ltlShipMethod" param="wishlist.ltlShipMethod"/>
					<dsp:getvalueof id="wishlist" param="wishlist"/>
					<dsp:getvalueof var="productVO" param="wishlist.productVO" />
					<c:if test="${(not empty itemMoveFromCartID and (itemMoveFromCartID eq wishlist.wishListItemId)) and moveOperation}">
							<li class="cartRow changeStoreItemWrap clearfix savedItem itemJustMovedToSaveUndoLink">
								<dsp:form action="ajax_handler_cart.jsp" iclass="frmAjaxSubmit clearfix frmCartRow" name="frmCartRow">
									<div class="prodItemWrapper clearfix">
										<p class="width_7 noMar padLeft_10 padTop_10 padBottom_10 fl prodInfo breakWord textLeft">
											<dsp:getvalueof var="skuID" param="wishlist.skuID" />
											<dsp:getvalueof var="prodID" param="wishlist.prodID" />
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
											<dsp:droplet name="SKUWishlistDetailDroplet">
												<dsp:param name="siteId" value="${appid}"/>
												<dsp:param name="skuId" value="${skuID}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="skuName" param="pSKUDetailVO.displayName" />
													<dsp:getvalueof var="isLtlFlag" param="pSKUDetailVO.ltlItem" />
												</dsp:oparam>
											</dsp:droplet>
												<c:choose>																		
													<c:when test="${isLtlFlag && not empty ltlShipMethod}">
													  <c:set var="url">${finalUrl}?skuId=${skuID}&sopt=${ltlShipMethod}</c:set>
													</c:when>
													<c:otherwise>
													  <c:set var="url">${finalUrl}?skuId=${skuID}</c:set>
													</c:otherwise>
												</c:choose>	
											<a href="${contextPath}${url}" title="${skuName}" class="prodName">
												${skuName}
											</a>
											<bbbl:label key="lbl_save_top_link_view_saved2" language="${pageContext.request.locale.language}"/>
											<c:set var="lbl_save_top_link_undo"><bbbl:label key="lbl_save_top_link_undo" language="${pageContext.request.locale.language}"/></c:set>
											<a class="lnkAction undoSave triggerSubmit" data-submit-button="btnUndoSave" title="${lbl_save_top_link_undo}" href="#">${lbl_save_top_link_undo}</a>

											 <dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1" />
											<dsp:getvalueof var="ltlShipMethodToSend" param="wishlist.ltlShipMethod"/>
											<dsp:getvalueof var="shipMethodUnsupported" param="wishlist.shipMethodUnsupported"/>
											
											<dsp:input bean="CartModifierFormHandler.addItemCount" type="hidden" value="1" />
											<dsp:input bean="CartModifierFormHandler.undoCheck" type="hidden" value="true" />
											<dsp:input bean="CartModifierFormHandler.countNo" type="hidden" paramvalue="countNo" />
											<dsp:input bean="CartModifierFormHandler.items[0].productId" value="${prodID}" type="hidden" />
											<dsp:input bean="CartModifierFormHandler.items[0].catalogRefId" value="${skuID}" type="hidden" />
											<dsp:input bean="CartModifierFormHandler.items[0].quantity" type="hidden" value="${quantity}" />
											<dsp:input id="storeId" bean="CartModifierFormHandler.items[0].value.storeId" type="hidden" value="${storeId}" />
											<dsp:input bean="CartModifierFormHandler.fromWishlist" type="hidden" value="true" />
											<dsp:input id="undoCheck" bean="CartModifierFormHandler.undoCheck" type="hidden" value="true" />
											<dsp:input bean="CartModifierFormHandler.fromCart" value="true" iclass="undoSaveData" type="hidden" />
											<dsp:input bean="CartModifierFormHandler.wishListId" type="hidden" paramvalue="wishlist.wishListItemId" />
											<dsp:input bean="CartModifierFormHandler.wishlistItemId" type="hidden" value="${itemMoveFromCartID}" />
											<dsp:input bean="CartModifierFormHandler.value.registryId" type="hidden" paramvalue="wishlist.registryID" />
											<dsp:getvalueof var="itemQuantity" value="${quantity}" />
											<dsp:input bean="CartModifierFormHandler.items[0].value.bts" value="${btsValue}" type="hidden" />
											<dsp:input bean="CartModifierFormHandler.items[0].value.referenceNumber" value="${referenceNumber}" type="hidden" />
											<c:if test="${ltlShipMethodToSend eq 'LWA'}">
												<dsp:getvalueof var="ltlShipMethodToSend" value="LW"/>
												<dsp:input bean="CartModifierFormHandler.items[0].value.whiteGloveAssembly" type="hidden" value="true" />
											</c:if>
											<c:choose>
												<c:when test="${shipMethodUnsupported eq true}">
													<dsp:input bean="CartModifierFormHandler.items[0].value.prevLtlShipMethod" type="hidden" value="${ltlShipMethodToSend}" />
												</c:when>
												<c:otherwise>
													<dsp:input bean="CartModifierFormHandler.items[0].value.ltlShipMethod" type="hidden" value="${ltlShipMethodToSend}" />
												</c:otherwise>
											</c:choose>
											<dsp:input bean="CartModifierFormHandler.items[0].value.shipMethodUnsupported" type="hidden" value="${shipMethodUnsupported}" />
											<dsp:input  bean="CartModifierFormHandler.moveWishListItemToOrder" type="submit"  iclass="undoSaveData hidden" name="btnUndoSave" id="btnUndoSave" value="MOVE TO CART" />
											<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden"
												value="cartUndo" />
											<dsp:input bean="CartModifierFormHandler.successQueryParam"
												type="hidden"
												value="showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
												<%-- Client DOM XSRF
											<dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" iclass="undoSaveData"  type="hidden" value="/store/cart/ajax_handler_cart.jsp?showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
											<dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" iclass="undoSaveData"  type="hidden" value="/store/cart/ajax_handler_cart.jsp?showMoveToCartError=true" />
											 --%>
										</p>
										<p class="grid_2 marRight_10 marTop_10 marBottom_10 alpha fr textRight">
											<c:set var="lbl_save_top_link_view_saved"><bbbl:label key="lbl_save_top_link_view_saved" language="${pageContext.request.locale.language}"/></c:set>
											<a href="#" title="View your saved items" class="lnkAction viewSavedItems">${lbl_save_top_link_view_saved}</a>
										</p>
										<c:if test="${hasPorchServiceRemoved}">
											<p class="width_7 red noMar padLeft_10 padBottom_10 fl breakWord textLeft">
												<bbbl:label key="lbl_bbby_porch_cart_save_for_later" language="${pageContext.request.locale.language}"/>
											</p>
										</c:if>
									</div>
								</dsp:form>
							</li>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>