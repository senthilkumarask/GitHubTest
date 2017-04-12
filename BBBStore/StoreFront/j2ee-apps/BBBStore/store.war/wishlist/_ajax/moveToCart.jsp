<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:getvalueof id="movedCommerceItemId" param="movedCommerceItemId"/>
	<dsp:getvalueof id="fromWishlist" param="fromWishlist"/>
	<dsp:getvalueof id="quantity" param="quantity"/>
	<dsp:getvalueof id="countNo" param="currentCount"/>
	<dsp:getvalueof id="btsValue" param="btsValue"/>
	  <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
	 <dsp:droplet name="/atg/commerce/order/droplet/BBBCartDisplayDroplet">
			<dsp:param name="order" bean="ShoppingCart.current" />
			<dsp:oparam name="output">
				 <dsp:droplet name="ForEach">
					<dsp:param name="array" param="commerceItemList" />
					<dsp:param name="elementName" value="commerceItem" />
					<dsp:oparam name="output">
						<dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id" />
						<c:if test="${(not empty movedCommerceItemId) and (movedCommerceItemId eq commerceItemId) and fromWishlist}">
							<dsp:getvalueof var="commItem" param="commerceItem"/>
								<li class="savedItemRow movedItem movedToCart itemJustMovedToCartUndoLink" id="savedItemID_2">								
										<div class="prodItemWrapper clearfix">
											<p class="width_7 noMar padLeft_10 padTop_10 padBottom_10 fl prodInfo breakWord textLeft">
												<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
													<dsp:param name="id" param="commerceItem.BBBCommerceItem.repositoryItem.productId" />
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
												<a href="${contextPath}${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}" class="prodName">${commItem.skuDetailVO.displayName}</a>
												&nbsp;<bbbl:label key="lbl_save_top_link_moved" language="${pageContext.request.locale.language}"/>
												<c:set var="lbl_save_top_link_undo"><bbbl:label key="lbl_save_top_link_undo" language="${pageContext.request.locale.language}"/></c:set>
												<a class="lnkAction undoMoveToCart btnAjaxSubmitSFL" data-ajax-frmID="frmSaveUndoCart" title="${lbl_save_top_link_undo}" href="#">${lbl_save_top_link_undo}</a>
											</p>
											<dsp:getvalueof id="cartUndoCountNo" value="${countNo}" />
                                            <dsp:getvalueof id="cartUndoCommerceId" value="${movedCommerceItemId}" /> 
                                            <input type="hidden" name="cartUndoQuantity" class="frmAjaxSubmitData" value="${quantity}" />
                                            <input type="hidden" name="cartUndoCount" class="frmAjaxSubmitData" value="${countNo}" />
                                            <input type="hidden" name="cartUndoCommerceId" class="frmAjaxSubmitData" value="${movedCommerceItemId}" />
                                            <input type="hidden" name="cartUndoBts" class="frmAjaxSubmitData" value="${btsValue}" />
											<p class="width_2 noMar padRight_10 padTop_10 padBottom_10 fr textRight">
												<c:set var="lbl_save_top_link_view_cart"><bbbl:label key="lbl_save_top_link_view_cart" language="${pageContext.request.locale.language}"/></c:set>
												<a href="${contextPath}/cart/cart.jsp" title="${lbl_save_top_link_view_cart}" class="lnkAction viewCart">${lbl_save_top_link_view_cart}</a>
											</p>
											<div class="clear"></div>
											
																						
											
										</div>
										<div class="clear"></div>
										<div class="hidden">3</div>
									<div class="clear"></div>
								</li>
						</c:if>	
						
					</dsp:oparam>
				</dsp:droplet>	
			</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>