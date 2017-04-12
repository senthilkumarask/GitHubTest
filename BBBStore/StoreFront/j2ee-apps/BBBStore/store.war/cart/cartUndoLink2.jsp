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
	<dsp:getvalueof id="hasPorchServiceRemoved" param="hasPorchServiceRemoved"/>
	
	<dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
		<dsp:oparam name="output">
			<dsp:droplet name="ForEach">
				<dsp:param name="elementName" value="wishlist" />
				<dsp:param name="array" param="giftList"/>
				<dsp:oparam name="output">
				<dsp:getvalueof var="ltlShipMethod" param="wishlist.ltlShipMethod"/>
					<dsp:getvalueof id="wishlist" param="wishlist"/>
					<dsp:getvalueof var="productVO" param="wishlist.productVO" />
					<c:if test="${(not empty itemMoveFromCartID and (itemMoveFromCartID eq wishlist.wishListItemId)) and moveOperation}">
							<li class="cartRow changeStoreItemWrap clearfix savedItem itemJustMovedToSaveUndoLink">
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
											<a class="lnkAction undoSave btnAjaxSubmitCart" data-ajax-frmID="frmCartUndoSave" title="${lbl_save_top_link_undo}" href="#">${lbl_save_top_link_undo}</a>

                                            <dsp:getvalueof id="saveUndoCountNo" param="countNo" />
                                            <dsp:getvalueof id="saveUndoWishListId" param="wishlist.wishListItemId" />
                                            <dsp:getvalueof id="saveUndoRegistryId" param="wishlist.registryID" />
                                            <dsp:getvalueof var="ltlShipMethodToSend" param="wishlist.ltlShipMethod"/>
                                            <dsp:getvalueof var="shipMethodUnsupported" param="wishlist.shipMethodUnsupported"/>
											<c:if test="${ltlShipMethodToSend eq 'LWA'}">
												<dsp:getvalueof var="ltlShipMethodToSend" value="LW"/>
												<input type="hidden" name="saveUndoWhiteGloveAssembly"  class="frmAjaxSubmitData" value="true" />
											</c:if>
											<c:choose>
												<c:when test="${shipMethodUnsupported eq true}">
													<input type="hidden" name="saveUndoPrevLtlShipMethod"  class="frmAjaxSubmitData" value="${ltlShipMethodToSend}" />
												</c:when>
												<c:otherwise>
													<input type="hidden" name="saveUndoLtlShipMethod"  class="frmAjaxSubmitData" value="${ltlShipMethodToSend}" />
												</c:otherwise>
											</c:choose>
											<input type="hidden" name="saveUndoShipMethodUnsupported"  class="frmAjaxSubmitData" value="${shipMethodUnsupported}" />
                                            <input type="hidden" name="saveUndoProductId" class="frmAjaxSubmitData" value="${prodID}" />
                                            <input type="hidden" name="saveUndoCatalogRefId" class="frmAjaxSubmitData" value="${skuID}" />
                                            <input type="hidden" name="saveUndoQuantity" class="frmAjaxSubmitData" value="${quantity}" />
                                            <input type="hidden" name="saveUndoStoreId" class="frmAjaxSubmitData" value="${storeId}" />
                                            <input type="hidden" name="saveUndoWishlistItemId" class="frmAjaxSubmitData" value="${itemMoveFromCartID}" />
                                            <input type="hidden" name="saveUndoCount" class="frmAjaxSubmitData" value="${saveUndoCountNo}" />
                                            <input type="hidden" name="saveUndoWishListId" class="frmAjaxSubmitData" value="${saveUndoWishListId}" />
                                            <input type="hidden" name="saveUndoRegistryId" class="frmAjaxSubmitData" value="${saveUndoRegistryId}" />
                                            <input type="hidden" name="saveUndoBts" class="frmAjaxSubmitData" value="${wishlist.bts}" />
											<input type="hidden" name="saveUndoRefNum" class="frmAjaxSubmitData" value="${wishlist.referenceNumber}" />
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
							</li>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>