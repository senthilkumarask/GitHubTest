<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
<dsp:importbean bean="/com/bbb/common/droplet/ListPriceSalePriceDroplet"/>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet"/>

       <%-- LTL changes divided ajax_cart in two jsp's and passed required parameters for included file  --%>
       <dsp:getvalueof id="movedCommerceItemId" param="movedCommerceItemId"/>
       <dsp:getvalueof id="fromWishlist" param="fromWishlist"/>
       <dsp:getvalueof id="quantityCart" param="quantityCart"/>
	   <dsp:getvalueof id="applicationId" param="applicationId"/>
	   <dsp:getvalueof id="btsValue" param="btsValue"/>
	    <c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
	    <c:set var="customizeCTACodes">
			<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
		</c:set>
	   <c:set var="enableLTLRegForSite">
				<bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" />
       </c:set>
	   <c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lblReviewsCount"><bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></c:set>
       <dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
			<dsp:oparam name="output">
				 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
			</dsp:oparam>
	   </dsp:droplet>
	   <c:choose>
		<c:when test="${applicationId eq 'BedBathUS'}">
			<c:set var="mapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_us" />
			</c:set>
		</c:when>
		  <c:when test="${applicationId eq 'BuyBuyBaby'}">
			<c:set var="mapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_baby" />
			</c:set>
		  </c:when>
		  <c:otherwise>
			<c:set var="mapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_ca" />
			</c:set>
		  </c:otherwise>
	    </c:choose>
       <dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
					<dsp:oparam name="empty">
							<dsp:getvalueof var="movedMap" bean="GiftRegistryFormHandler.movedItemMap" />
							<dsp:getvalueof id="storeIdCart" bean="CartModifierFormHandler.storeId" />
							<dsp:getvalueof id="wishlistItemIdCart" bean="CartModifierFormHandler.wishlistItemId" />
							<div  class="clearfix grid_10 alpha omega">
                                <h2 class="account fl"><bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>"/></h2>
                                 <ul class="grid_2 alpha share omega clearfix">
                                    <li class="clearfix"><a href="#" class="print avoidGlobalPrintHandler" title="<bbbl:label key="lbl_cartdetail_print" language="${language}"/>" id="printCart"><bbbl:label key="lbl_cartdetail_print" language="${language}"/></a></li>
                                  <li class="clearfix"><a href="#" class="email avoidGlobalEmailHandler" title="<bbbl:label key="lbl_cartdetail_emailcart" language="${language}"/>" id="openEmailCart"><bbbl:label key="lbl_cartdetail_emailcart" language="${language}"/></a></li>
                                </ul>
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
							<c:choose>
								<c:when test="${(not empty movedCommerceItemId) and fromWishlist}">

									<div id="saveForLaterHeader" class="clearfix grid_10 alpha omega">
										<h2 class="account fl"><bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>"/></h2>
										<ul class="grid_2 alpha share omega clearfix">
											<li class="clearfix"><a href="#" class="print avoidGlobalPrintHandler" title="Print Saved Items" id="printSavedItems"><bbbl:label key="lbl_sfl_print_saved_items" language="<c:out param='${language}'/>"/></a></li>
											<li class="clearfix"><a href="#" class="email avoidGlobalEmailHandler" title="Email Saved Items" id="openEmailSavedItems"><bbbl:label key="lbl_sfl_email_saved_items" language="<c:out param='${language}'/>"/></a></li>
										</ul>
										<div class="clear"></div>
									</div>
									<div id="saveForLaterBody">
										<div id="saveForLaterContentWrapper" class="clearfix">
											<div class="clearfix grid_10 alpha omega productListWrapper" id="saveForLaterContent">
												<div class="productsListHeader noBorderBot clearfix">
													<div class="wishlistItemDetails clearfix"><strong><bbbl:label key="lbl_sfl_item" language="<c:out param='${language}'/>"/></strong>
													</div>
													<div class="wishlistQuantityDetails clearfix"><strong><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></strong>
													</div>
													<div class="wishlistTotalDetails clearfix"><strong><bbbl:label key="lbl_sfl_total" language="<c:out param='${language}'/>"/></strong>
													</div>
												</div>
												<div class="clear"></div>
												<ul class="productsListContent clearfix">
												<dsp:form formid="frmSaveUndoCart" id="frmSaveUndoCart" iclass="frmAjaxSubmit clearfix frmSaveUndoCart" method="post" action="ajax_handler_cart.jsp">
													<dsp:input type="hidden" bean="GiftlistFormHandler.fromCartPage" iclass="undoMoveToCartData" value="true" />
													<dsp:input type="hidden" bean="GiftlistFormHandler.undoComItemId" iclass="frmAjaxSubmitData">
														<dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoCommerceId" />
													</dsp:input>
													<dsp:input bean="GiftlistFormHandler.countNo" type="hidden" iclass="frmAjaxSubmitData">
														<dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoCount" />
													</dsp:input>
													<dsp:input bean="GiftlistFormHandler.undoOpt" type="hidden" value="true" iclass="frmAjaxSubmitData" />
													<dsp:input bean="GiftlistFormHandler.quantity" iclass="frmAjaxSubmitData" type="hidden">
														<dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoQuantity" />
													</dsp:input>
													<dsp:input iclass="frmAjaxSubmitData" bean="GiftlistFormHandler.bts" type="hidden">
														<dsp:tagAttribute name="data-ajax-fieldName" value="cartUndoBts" />
													</dsp:input>
													<dsp:input bean="GiftlistFormHandler.successQueryParam"  type="hidden" value="showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
													<%-- Client DOM XSRF
													<dsp:input bean="GiftlistFormHandler.moveItemsFromCartLoginURL" iclass="frmAjaxSubmitData" type="hidden" value="/cart/ajax_handler_cart.jsp?showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
													<dsp:input bean="GiftlistFormHandler.moveItemsFromCartLoginURL" iclass="frmAjaxSubmitData" type="hidden" value="/cart/ajax_handler_cart.jsp?showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
													<dsp:input bean="GiftlistFormHandler.moveItemsFromCartErrorURL" iclass="frmAjaxSubmitData" type="hidden" value="/cart/ajax_handler_cart.jsp?showMoveToCartError=true" / --%>
													  <dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="moveItemsFromCart" />
													<dsp:input bean="GiftlistFormHandler.moveItemsFromCart" type="submit" value="MOVE TO CART" id="btnUndoMoveToCart2" iclass="hidden" name="btnUndoMoveToCart" />
												</dsp:form>
												<dsp:include page="saveUndoLink.jsp">
													<dsp:param name="movedCommerceItemId" value="${movedCommerceItemId}"/>
													<dsp:param name="fromWishlist" value="${fromWishlist}"/>
													<dsp:param name="quantity" value="${quantityCart}"/>
													<dsp:param name="currentCount" value="1"/>
													<dsp:param name="btsValue" value="${btsValue}"/>
												</dsp:include>
												</ul>
											</div>
										</div>
									</div>
								</c:when>
								<c:when test="${not empty movedMap}">
									<div id="saveForLaterBody">
										<div id="saveForLaterContentWrapper" class="clearfix">
										<div class="clearfix grid_10 alpha omega productListWrapper" id="saveForLaterContent">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" bean="GiftRegistryFormHandler.movedItemMap" />
													<dsp:oparam name = "outputStart">
														<div class="productsListHeader noBorderBot clearfix">
															<div class="wishlistItemDetails clearfix"><strong><bbbl:label key="lbl_sfl_item" language="<c:out param='${language}'/>"/></strong>
															</div>
															<div class="wishlistQuantityDetails clearfix"><strong><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></strong>
															</div>
															<div class="wishlistTotalDetails clearfix"><strong><bbbl:label key="lbl_sfl_total" language="<c:out param='${language}'/>"/></strong>
															</div>
														</div>
														<div class="clear"></div>
														<ul class="productsListContent clearfix">
													</dsp:oparam>

													<dsp:oparam name="output">
														<dsp:getvalueof var="movedItemCount" param="key" />
														<dsp:getvalueof var="productID" param="element" />
														<dsp:getvalueof bean="GiftRegistryFormHandler.skuId" var="skuId" />



														<li id="savedItemID_${skuID}" class="savedItemRow movedItem clearfix movedToReg itemMovedToRegUndoLink">
															<dsp:form name="frmSavedItems" iclass="frmAjaxSubmit clearfix frmSavedItems" action="ajax_handler_cart.jsp">
																<div class="prodItemWrapper clearfix">
																<input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
																	<p class="width_7 noMar padLeft_10 padTop_10 padBottom_10 fl prodInfo breakWord textLeft">
																		<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
																			<dsp:param name="id" value="${productID}" />
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
																			<dsp:param name="skuId" value="${skuId}"/>
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="prodName" param="pSKUDetailVO.displayName" />
																				<dsp:getvalueof var="isLtlFlag" param="pSKUDetailVO.ltlItem" />
																			</dsp:oparam>
																		</dsp:droplet>
																		<input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
																		<dsp:a iclass="prodName" title="${prodName}" page="${finalUrl}?skuId=${skuId}">${prodName}</dsp:a> <bbbl:label key="lbl_moved_to_registry" language="${pageContext.request.locale.language}" /> #<dsp:valueof bean="GiftRegistryFormHandler.movedItemRegistryId" />.
																	</p>
																	<p class="width_2 noMar padRight_10 padTop_10 padBottom_10 fr textRight">

																				<dsp:droplet name="GetRegistryVODroplet">
																					<dsp:param name="siteId" value="${applicationId}"/>
																					<dsp:param bean="GiftRegistryFormHandler.movedItemRegistryId" name="registryId" />
																					<dsp:param value="true" name="isRegTypeNameReq" />
																					<dsp:oparam name="output">
																						<dsp:getvalueof var="registryVO" param="registryVO"/>
																						<dsp:getvalueof var="eventType" param="registryTypeName"/>
																						<c:if test="${not empty eventType}">
																							<dsp:a iclass="lnkAction viewRegistry" href="/store/giftregistry/view_registry_owner.jsp" title="View your registry">View your registry
																								<dsp:param name="registryId" bean="GiftRegistryFormHandler.movedItemRegistryId"/>
																								<dsp:param name="eventType" param="registryTypeName"/>
																							</dsp:a>
																						</c:if>
																					</dsp:oparam>
																				</dsp:droplet>

																	</p>
																	<div class="clear"></div>
																</div>
																<div class="clear"></div>
															</dsp:form>
															<div class="clear"></div>
														</li>
													</dsp:oparam>

													<dsp:oparam name = "outputEnd">
														</ul>
													</dsp:oparam>
											</dsp:droplet>
										</div>
									  </div>
									</div>
								</c:when>
								<c:when test="${not empty storeIdCart and not empty wishlistItemIdCart}">
									<div id="saveForLaterBody">
										<div id="saveForLaterContentWrapper" class="clearfix">
											<div id="saveForLaterContent" class="clearfix grid_10 alpha omega productListWrapper">
												<li class="savedItemRow itemFindInStoreUndoLink"></li>
											</div>
										</div>
									</div>
								</c:when>

							</c:choose>
                        </dsp:oparam>
                    <dsp:oparam name="output">

					 <div id="saveForLaterBody">

						<div id="saveForLaterContentWrapper" class="clearfix">

                        <div id="saveForLaterContent" class="clearfix grid_10 alpha omega productListWrapper">
                            <div class="productsListHeader noBorderBot clearfix">
                                <div class="wishlistItemDetails clearfix"><strong><bbbl:label key="lbl_sfl_item" language="<c:out param='${language}'/>"/></strong></div>
                                <div class="wishlistQuantityDetails clearfix"><strong><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></strong></div>
                                <div class="wishlistTotalDetails clearfix"><strong><bbbl:label key="lbl_sfl_total" language="<c:out param='${language}'/>"/></strong></div>
                            </div>
                            <div class="clear"></div>
							<dsp:include page="saveForlaterCartForms.jsp"/>
							<dsp:form name="frmSavedItems" iclass="frmAjaxSubmit clearfix frmSavedItems" action="ajax_handler_cart.jsp">
								<ul class="productsListContent clearfix">
								<c:set var="check" value="${false}"/>
								<c:set var="showMsg" value="${true}"/>
								<dsp:getvalueof var="appid" bean="Site.id" />
								<dsp:droplet name="AddItemToGiftRegistryDroplet">
									<dsp:param name="siteId" value="${appid}"/>
                                    <c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_move_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
								</dsp:droplet>
								<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
								<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
								<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
                                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                    <dsp:param name="array" param="giftList" />
									<dsp:param name="reverseOrder" value="true" />
                                    <dsp:oparam name="outputStart">
                                    </dsp:oparam>
                                    <dsp:oparam name="output">
										<c:set var="flagOff" value="${false}"/>
										<c:set var="bopusoff" value="${false}"/>
									    <dsp:getvalueof var="arraySize" param="size" />
                                        <dsp:getvalueof var="currentCount" param="count" />
                                        <dsp:getvalueof var="registryId" param="element.registryID" />
                                        <dsp:getvalueof var="skuID" param="element.skuID" />
                                        <dsp:getvalueof var="quantity" param="element.quantity" />
                                        <dsp:getvalueof var="wishListItemId" param="element.wishListItemId" />
                                        <dsp:getvalueof var="commerceItemId" param="element.commerceItemId" />
                                        <dsp:getvalueof var="prodID" param="element.prodID" />
                                        <dsp:getvalueof var="giftListId" param="element.giftListId" />
                                      	<dsp:getvalueof var="referenceNumber" param="element.referenceNumber" />
										<dsp:getvalueof var="personalizePrice" param="element.personalizePrice" />
										<dsp:getvalueof var="personalizationOptions" param="element.personalizationOptions" />
										<dsp:getvalueof var="personalizationOptionsDisplay" param="element.personalizationOptionsDisplay" />
										<dsp:getvalueof var="personalizationDetails" param="element.personalizationDetails" />
										<dsp:getvalueof var="priceMessageVO" param="element.priceMessageVO" />
										<dsp:getvalueof id="iCount" param="count"/>
										<dsp:getvalueof id="size" param="size"/>
										<dsp:getvalueof id="countNo" bean="CartModifierFormHandler.countNo"/>
										<dsp:getvalueof var="saveBts" param="element.bts" />
										<dsp:getvalueof var="personalizationStatus" param="element.personalizationStatus" />
										<dsp:getvalueof var="fullImagePath" param="element.fullImagePath" />
                                        <dsp:getvalueof var="eximErrorExists" param="element.eximErrorExists"/>
                                        

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
										<c:if test="${arraySize eq currentCount}">
													<c:set var="lastRow" value="lastRow" />
										</c:if>



										<c:if test="${(size eq iCount) and (size lt countNo)}">
											<c:set var="check" value="${true}"/>
										</c:if>

										<c:if test="${iCount eq countNo}">
											<dsp:getvalueof id="storeIdCart" bean="CartModifierFormHandler.storeId" />
											<dsp:getvalueof id="wishlistItemIdCart" bean="CartModifierFormHandler.wishlistItemId" />
											<c:if test="${not empty storeIdCart and not empty wishlistItemIdCart}">
													<li class="savedItemRow itemFindInStoreUndoLink"></li>
											</c:if>
											<dsp:include page="saveUndoLink.jsp">
												<dsp:param name="movedCommerceItemId" value="${movedCommerceItemId}"/>
												<dsp:param name="fromWishlist" value="${fromWishlist}"/>
												<dsp:param name="quantity" value="${quantityCart}"/>
												<dsp:param name="currentCount" value="${countNo}"/>
												<dsp:param name="btsValue" value="${btsValue}"/>
											</dsp:include>
										</c:if>

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

                                        <dsp:getvalueof var="movedItemIndex" bean="GiftRegistryFormHandler.movedItemIndex"/>
                                        <c:set var="displaySize" >
                                            <bbbc:config key="sfl_display_size" configName='CartAndCheckoutKeys' />
                                         </c:set>
                                         <c:if test="${empty displaySize}">
                                            <c:set var="displaySize" >
                                                2
                                             </c:set>
                                         </c:if>


										<c:choose>
											<c:when test="${movedItemIndex <= displaySize && currentCount == displaySize+1 && arraySize > displaySize}">

													<li id="savedItemID_BTN" class="savedItemRow showAllBtn">
														<div class="prodItemWrapper clearfix">
														<input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
															<div class="button">
																<input id="showAllBtn" type="button" value="Show all ${arraySize} items" role="button" aria-pressed="false" aria-labelledby="showAllBtn" />
															</div>
														</div>
													</li>

											</c:when>
											<c:when test="${currentCount == displaySize+1 && arraySize > displaySize}">

													<li id="savedItemID_BTN" class="savedItemRow showAllBtn">
														<div class="prodItemWrapper clearfix">
														<input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
															<div class="button">
																<input id="showAllBtn" type="button" value="Show all ${arraySize} items" role="button" aria-pressed="false" aria-labelledby="showAllBtn" />
															</div>
														</div>
													</li>

											</c:when>
										</c:choose>
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" bean="GiftRegistryFormHandler.movedItemMap" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="movedItemCount" param="key" />
														<dsp:getvalueof var="productID" param="element" />
														<dsp:getvalueof bean="GiftRegistryFormHandler.skuId" var="skuId" />
														<c:if test="${movedItemCount eq currentCount}">




														<li id="savedItemID_${skuID}" class="${lastRow} savedItemRow movedItem clearfix movedToReg itemMovedToRegUndoLink">

																<div class="prodItemWrapper clearfix">
																<input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
																	<p class="width_7 noMar padLeft_10 padTop_10 padBottom_10 fl prodInfo breakWord textLeft">
																		<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
																			<dsp:param name="id" value="${productID}" />
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
																			<dsp:param name="skuId" value="${skuId}"/>
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="prodName" param="pSKUDetailVO.displayName" />
																				<dsp:getvalueof var="isLtlFlag" param="pSKUDetailVO.ltlItem" />
																			</dsp:oparam>
																		</dsp:droplet>
																		<input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
																		<dsp:a iclass="prodName" title="${prodName}" page="${finalUrl}?skuId=${skuId}">${prodName}</dsp:a> has been moved to registry #<dsp:valueof bean="GiftRegistryFormHandler.movedItemRegistryId" />.
																	</p>
																	<p class="width_2 noMar padRight_10 padTop_10 padBottom_10 fr textRight">

																				<dsp:droplet name="GetRegistryVODroplet">
																					<dsp:param name="siteId" value="${applicationId}"/>
																					<dsp:param bean="GiftRegistryFormHandler.movedItemRegistryId" name="registryId" />
																					<dsp:param value="true" name="isRegTypeNameReq" />
																					<dsp:oparam name="output">
																						<dsp:getvalueof var="registryVO" param="registryVO"/>
																						<dsp:getvalueof var="eventType" param="registryTypeName"/>
																						<c:if test="${not empty eventType}">
																							<dsp:a iclass="lnkAction viewRegistry" href="/store/giftregistry/view_registry_owner.jsp" title="View your registry"><bbbl:label key="lbl_view_registry" language="${pageContext.request.locale.language}" />
																								<dsp:param name="registryId" bean="GiftRegistryFormHandler.movedItemRegistryId"/>
																								<dsp:param name="eventType" param="registryTypeName"/>
																							</dsp:a>
																						</c:if>
																					</dsp:oparam>
																				</dsp:droplet>

																	</p>
																	<div class="clear"></div>
																</div>
																<div class="clear"></div>

															<div class="clear"></div>
														</li>
														</c:if>
													</dsp:oparam>
												</dsp:droplet>
												<c:if test="${currentCount > displaySize && arraySize > displaySize}">
													<c:set var="hidden" value="hidden"></c:set>
												</c:if>
												<c:if test="${movedItemIndex <= displaySize && currentCount > displaySize && arraySize > displaySize}">
													<c:set var="hidden" value="hidden"></c:set>
												</c:if>

												<dsp:getvalueof bean="GiftlistFormHandler.itemIdJustMvBack" id="itemIdJustMvBackWish"/>
												<dsp:getvalueof bean="GiftlistFormHandler.undoOpt" id="undoCheckwish"/>
												<c:if test="${undoCheckwish and (itemIdJustMvBackWish eq wishListItemId)}">
													<c:set var="itemMovedBackToSave" value="itemMovedBackToSave"/>
												</c:if>
												<dsp:getvalueof bean="GiftlistFormHandler.newItemAdded" id="newItemAddedwish"/>
												<c:if test="${newItemAddedwish and currentCount == 1}">
													<c:set var="itemJustMovedToSave" value="itemJustMovedToSave"/>
												</c:if>
												<dsp:droplet name="SKUWishlistDetailDroplet">
												<dsp:param name="siteId" value="${applicationId}"/>
												<dsp:param name="skuId" value="${skuID}"/>
												<dsp:param name="personalizedFlag" value="${isPersonalized}" />
												<dsp:param name="personalizedPrice" value="${personalizePrice}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
													<dsp:getvalueof var="prdShipMsgFlag" param="pSKUDetailVO.shipMsgFlag"/>
													<dsp:getvalueof var="prdDisplayShipMsg" param="pSKUDetailVO.displayShipMsg"/>
													<dsp:getvalueof var="skuImage" param="pSKUDetailVO.skuImages.mediumImage"/>
													<dsp:getvalueof var="skuDisplayName" param="pSKUDetailVO.displayName" />
													<dsp:getvalueof var="skuImageURL" param="pSKUDetailVO.skuImages.mediumImage" />
													<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color" />
													<dsp:getvalueof var="skuSize" param="pSKUDetailVO.size" />
													<dsp:getvalueof var="isLtlFlag" param="pSKUDetailVO.ltlItem" />
													<dsp:getvalueof var="isIntlRestricted" param="pSKUDetailVO.intlRestricted" />
													<dsp:getvalueof var="customizableRequired" param="pSKUDetailVO.customizableRequired" />
													<dsp:getvalueof var="personalizationType" param="pSKUDetailVO.personalizationType" />
													<dsp:getvalueof var="customizableCodes"
															param="pSKUDetailVO.customizableCodes" />
													<dsp:getvalueof var="inCartFlagSKU" param="pSKUDetailVO.inCartFlag" /> 
												</dsp:oparam>
											</dsp:droplet>
											
											<c:set var="ship_method_avl" value="${true}"/>
												<dsp:droplet name="ForEach">
                                                    <dsp:param name="array" param="skuVO.eligibleShipMethods"/>
                                                    <dsp:oparam name="output">
                                                        <dsp:getvalueof id="shipMethodId" param="element.shipMethodId"/>
															<c:if test="${empty shipMethodId}" >
																<c:set var="ship_method_avl" value="${false}"/>
															</c:if>
                                                    </dsp:oparam>
                                                </dsp:droplet>

												<dsp:getvalueof var="ltlShipMethod" param="element.ltlShipMethod"/>

												<li>
												<c:if test="${ship_method_avl && isLtlFlag && empty ltlShipMethod}" >
															<div class="clear"></div>
															<div class="ltlMessage ltlMessagePadding">
																<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>
															</div>
															<div  class="ltlMessage ltlAlertmessage" >

																<bbbl:label key='lbl_rlp_ltl_registry_message' language="${pageContext.request.locale.language}" />
																<%--<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>--%>
															</div>


														</c:if>



												</li>
                                        <li id="savedItemID_${wishListItemId}" class="${itemJustMovedToSave} ${itemMovedBackToSave} ${lastRow} <c:if test="${not empty registryId}">registeryItem</c:if> savedItemRow changeStoreItemWrap clearfix ${hidden}">
										<c:set var="itemMovedBackToSave" value=""/>
										<c:set var="itemJustMovedToSave" value=""/>
										<input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="prodId" name="productId">
										<input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
										<c:if test="${flagOff}">
											<input type="hidden" data-dest-class="itemQuantity" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity" role="textbox" aria-required="true">
										</c:if>

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


											<dsp:getvalueof var="prodImage" value="${skuImageURL}" />
											<dsp:getvalueof var="pName" value="${skuDisplayName}" />

											<dsp:include page="itemLinkWishlist.jsp?id=${wishListItemId}&registryIdSavedItem=${registryId}&image=${prodImage}">
												<dsp:param name="priceMessageVO" value="${priceMessageVO}"/>
												<dsp:param name="displayName" value="${pName}"/>
												<dsp:param name="isPersonalized" value="${isPersonalized}" />
												<dsp:param name="enableKatoriFlag" value="${enableKatoriFlag}" />
                                           </dsp:include>

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
														  <div class="registeryItemHeader clearfix">
																<span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>&nbsp;
																<dsp:a href="${registryUrl}">
																<dsp:param name="registryId" value="${registryId}"/>
																<dsp:param name="eventType" value="${registryTypeName}" />
																<dsp:param name="inventoryCallEnabled" value="${true}" />
																<strong>${registryVO.primaryRegistrant.firstName}<c:if test="${not empty registryVO.coRegistrant.firstName}">&nbsp;&amp;&nbsp;${registryVO.coRegistrant.firstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong>
																</dsp:a>
																&nbsp;<span>${registryTypeName}</span>&nbsp;<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
															</div>

                                                            <div class="clear"></div>
                                                    </c:if>


												<dsp:getvalueof var="shipMethodUnsupported" param="element.shipMethodUnsupported"/>
													<%-- LTL Alert  for Ship method unsupported--%>
													<c:if test="${isLtlFlag && shipMethodUnsupported}" >
														<dsp:getvalueof var="shippingMethodDesc" param="element.ltlShipMethodDesc"/>
														<c:if test="${ltlShipMethod == 'LWA'}">
															<c:set var="shippingMethodDesc"><bbbl:label key="lbl_white_glove_assembly" language="<c:out param='${language}'/>" />
															</c:set>
														</c:if>
														<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request"/>
														<c:set target="${placeHolderMapServiceLevel}" property="ShippingMethodDesc" value="${shippingMethodDesc}"/>
															<div class="clear"></div>
														<div class="ltlUnavailable marBottom_10 marTop_10" >
														<p class="padLeft_25">
															<bbbt:textArea key="txt_ship_method_not_supported" placeHolderMap="${placeHolderMapServiceLevel}" language ="${pageContext.request.locale.language}"/>
														</p>
															</div>
														</c:if>
                                                <div class="clear"></div>

														<%-- LTL Alert  --%>
                                                <div class="prodItemWrapper clearfix">
                                                <input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
                                                        <div class="wishlistItemDetails clearfix<c:if test="${inCartFlagSKU}"> inCartContainer</c:if>">
                                                        <div class = "wishlistItemsList">
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
													<%-- end :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
														<c:choose>
															<c:when test="${not flagOff}">
															          <c:choose>
																			<c:when test="${isLtlFlag && not empty ltlShipMethod}">
																			  <c:set var="url">${finalUrl}?skuId=${skuID}&sopt=${ltlShipMethod}</c:set>
																			</c:when>
																			<c:otherwise>
																			  <c:set var="url">${finalUrl}?skuId=${skuID}</c:set>
																			</c:otherwise>
																		</c:choose>
																		<dsp:a iclass="prodImg clearfix block" title="${pName}" page="${url}">
													<%-- end :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
																	<c:choose>
																		<c:when test="${not empty referenceNumber}">
																			<c:choose>
																				<c:when test="${not empty fullImagePath && !eximErrorExists && enableKatoriFlag}">
																					  <img class="fl productImage" src="${fullImagePath}" alt="${pName}" title="${pName}" height="146" width="146" />
																				 </c:when>
																				 <c:otherwise>
																					 <img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${pName}" title="${pName}" height="146" width="146" />
																				 </c:otherwise>
																			</c:choose>
																		</c:when>
																		<c:otherwise>
																			<c:choose>
																				<c:when test="${empty prodImage}">
																					<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage"/>
																				</c:when>
																				<c:otherwise>
																					<img src="${scene7Path}/${prodImage}" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage noImageFound"/>
																				</c:otherwise>
																			</c:choose>
																		</c:otherwise>
																	</c:choose>
																</dsp:a>
															</c:when>
															<c:otherwise>
                                                                <dsp:a iclass="prodImg clearfix block" title="${prodName}" page="${finalUrl}?skuId=${catalogRefId}">
																<c:choose>
																	<c:when test="${not empty referenceNumber}">
																		<c:choose>
																			<c:when test="${not empty fullImagePath && !eximErrorExists && enableKatoriFlag}">
																				  <img class="fl productImage" src="${fullImagePath}" alt="${pName}" title="${pName}" height="146" width="146" />
																			 </c:when>
																			 <c:otherwise>
																				 <img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${pName}" title="${pName}" height="146" width="146" />
																			 </c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${empty prodImage}">
																				<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage"/>
																			</c:when>
																			<c:otherwise>
																				<img src="${scene7Path}/${prodImage}" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage noImageFound"/>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
                                                                </dsp:a>
															</c:otherwise>
														</c:choose>
														<c:if test="${isPersonalizationIncomplete eq 'true'}">
                                                            <div class="PersonalizationProgressText personalizationProgress" >
                                                             <c:choose>
																<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
																	<c:set var="progressTxt">
																	<bbbl:label key="lbl_sfl_customization_progress" language="${pageContext.request.locale.language}" />
																	</c:set>
																	<c:set var="completeTxt">
																	<bbbl:label key="lbl_sfl_customization_complete" language="${pageContext.request.locale.language}" />
																	</c:set>
																</c:when>
																<c:otherwise>
																	<c:set var="progressTxt">
																	<bbbl:label key="lbl_sfl_personalization_progress" language="${pageContext.request.locale.language}" />
																	</c:set>
																	<c:set var="completeTxt">
																	<bbbl:label key="lbl_sfl_personalization_complete" language="${pageContext.request.locale.language}" />
																	</c:set>
																</c:otherwise>
															</c:choose>
                                                                <div class= "progressHead">${progressTxt}</div>
                                                                    <span>${completeTxt}</span>
                                                                </div>
                                                        </c:if>
                                                       </div>
															<c:if test="${empty pName}">
																	<dsp:getvalueof var="pName" value="${skuDisplayName}" />
															</c:if>
                                                            <span class="prodInfo block breakWord">
															<span class="prodName">
                                                               <c:choose>
																	<c:when test="${not flagOff}">
																	<%-- Start :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
																	<c:choose>
																		<c:when test="${isLtlFlag && not empty ltlShipMethod}">
																		     <c:set var="url">${finalUrl}?skuId=${skuID}&sopt=${ltlShipMethod}</c:set>
																		</c:when>
																		<c:otherwise>
																			 <c:set var="url">${finalUrl}?skuId=${skuID}</c:set>
																		</c:otherwise>
																	</c:choose>
																		<dsp:a page="${url}" iclass="productName" title="${productVO.name}">${pName}</dsp:a>
																	</c:when>
																	<%-- end :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
																	<c:otherwise>
																		<span class="productName disableText">${skuDisplayName}</span>
																	</c:otherwise>
																</c:choose>
																<dsp:getvalueof var="pName" value="" />
																<dsp:getvalueof var="prodImage" value="" />
															</span>
															<span class="prodAttribsSavedItem">
																<c:if test='${not empty skuColor}'><c:choose><c:when test="${rollupAttributesFinish == 'true'}"><bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/> : </c:when><c:otherwise><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : </c:otherwise></c:choose>&nbsp;<dsp:valueof value="${skuColor}" valueishtml="true" /></c:if>
																<c:if test='${not empty skuSize}'><c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" /></c:if>
																<c:set var="rollupAttributesFinish" value="false" />
															</span>
																	<c:if test="${enableKatoriFlag && not empty referenceNumber}">
																		<c:if test="${not empty personalizationOptions}">
																				${eximCustomizationCodesMap[personalizationOptions]} :  ${personalizationDetails}
																			<span class="personalizationAttr katoriPrice">
																			<%--BBBSL-8154  --%>
																				<%-- <span  aria-hidden="true">${personalizationOptionsDisplay} </span> --%>
																				<c:if test="${isPersonalizationIncomplete eq 'false'}">
																				  <input type="hidden" class="editPersonalization"  data-custom-vendor="${productVO.vendorInfoVO.vendorName}" data-sku="${skuVO.skuId}" data-refnum="${referenceNumber}" data-quantity="${quantity}" aria-hidden="true"></input>
																				</c:if>
																				  <div class="priceAddText">
																				<c:choose>
																					<c:when test='${isPersonalizationIncomplete eq false && not empty personalizePrice && not empty skuVO.personalizationType && skuVO.personalizationType == "PY"}'>
																						<dsp:valueof value="${personalizePrice}" number="0.00" converter="currency"/>&nbsp;<bbbl:textArea key="txt_sfl_personal_price_msg" language="<c:out param='${language}'/>"/>
																					</c:when>
																					<c:when test='${isPersonalizationIncomplete eq false && not empty personalizePrice && not empty skuVO.personalizationType && skuVO.personalizationType == "CR"}'>
																						<dsp:valueof value="${personalizePrice}" number="0.00" converter="currency"/>&nbsp;
																						<c:choose>
																							<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
																								<bbbl:textArea key="txt_pdp_cr_customize_katori_price" language="<c:out param='${language}'/>"/>
																							</c:when>
																							<c:otherwise>
																								<bbbl:textArea key="txt_pdp_cr_katori_price" language="<c:out param='${language}'/>"/>
																							</c:otherwise>
																						</c:choose>
																						
																					</c:when>
																					<c:when
																				test='${not empty skuVO.personalizationType && skuVO.personalizationType != "PB"
																								&&  isPersonalizationIncomplete}'>
																								<bbbl:label key="lbl_sfl_personalization_price" language="${pageContext.request.locale.language}" />
																							</c:when>
																					<c:when test='${not empty skuVO.personalizationType && skuVO.personalizationType == "PB"}'>
																						<bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
																					</c:when>
																				</c:choose>
																				</div>
																				</span>
																				<dsp:setvalue beanvalue="Profile.wishlist"
																			param="wishlist" />
																			<dsp:getvalueof id="wishlistId" param="wishlist.id" />
																			<c:if test="${isPersonalizationIncomplete}">
																					<div class="personalizeLinks">
																					<c:choose>
																						<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
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
                                                                                     	<a href="#" class="editPersonalizationSfl bold <c:if test="${isInternationalCustomer}">disableText</c:if>" title="${editPersonalizeTxt}"
                                                                                     	data-sku="${skuVO.skuId}" data-product="${prodID}" data-refnum="${referenceNumber}" data-quantity="${quantity}" data-wishlistId="${wishlistId}" data-wishlistitemId="${wishListItemId}">
																					 	${editPersonalizeTxt}</a>
	                                                                             	</div>
	                                                                         </c:if>
																		</c:if>
																	</c:if>
                                                                <span class="skuNum"><bbbl:label key="lbl_sfl_sku" language="<c:out param='${language}'/>"/> #${skuID}</span>
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

																		<c:if test = "${not flagOff}">
																			<c:choose>
																				<c:when test="${ratings ne null && ratings ne '0' && (reviews eq '1' || reviews gt '1') }">
																				<c:choose>
																							<c:when test="${isLtlFlag && not empty ltlShipMethod}">
																					<span class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																									<a href="${contextPath}${finalUrl}?skuId=${skuID}&categoryId=${CategoryId}&sopt=${ltlShipMethod}&showRatings=true" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productVO.name}">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></a>
																								</span>
																							</c:when>
																							<c:otherwise>
																								<span class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																					<a href="${contextPath}${finalUrl}?skuId=${skuID}&categoryId=${CategoryId}&showRatings=true" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productVO.name}">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></a>
																					</span>
																							</c:otherwise>
																				</c:choose>
																				</c:when>
																				<c:otherwise>
																					<c:choose>
																						<c:when test="${isLtlFlag && not empty ltlShipMethod}">
																					<span class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																								<a href="${contextPath}${finalUrl}?skuId=${skuID}&sopt=${ltlShipMethod}&showRatings=true" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productVO.name}">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></a>
																							</span>
																						</c:when>
																						<c:otherwise>
																							<span class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																					<a href="${contextPath}${finalUrl}?skuId=${skuID}&showRatings=true" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productVO.name}">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></a>
																					</span>
																						</c:otherwise>
																					</c:choose>
																				</c:otherwise>
																			</c:choose>
																		</c:if>
																	</c:if>
																<c:if test="${inCartFlagSKU}">
																<div class="disPriceString"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </div>
																 </c:if>	
                                                                <c:set var="itemPrice" />
                                                                <dsp:droplet name="ListPriceSalePriceDroplet">
                                                                    <dsp:param name="productId" param="element.prodID" />
                                                                    <dsp:param name="skuId" param="element.skuID" />
                                                                    <dsp:oparam name="output">
                                                                        <dsp:getvalueof var="listPriceD" param="listPrice" />
                                                                        <dsp:getvalueof var="salePriceD" param="salePrice" />

                                                                    </dsp:oparam>
                                                                </dsp:droplet>
															<dsp:getvalueof var="itemPriceD" value="${listPriceD}" vartype="java.lang.Double"/>
                                                                 <c:set var="itemPrice">${listPriceD}</c:set>
                                                                <c:choose>
                                                                    <c:when test="${(not empty salePriceD) && (salePriceD gt 0.0)}">

																		<dsp:getvalueof var="itemPriceD" value="${salePriceD}" vartype="java.lang.Double"/>
                                                                        <c:set var="itemPrice">${salePriceD}</c:set>
                                                                    </c:when>
                                                                    <c:otherwise>
																		<dsp:getvalueof var="itemPriceD" value="${listPriceD}" vartype="java.lang.Double"/>
                                                                        <c:set var="itemPrice">${listPriceD}</c:set>
                                                                    </c:otherwise>
                                                                </c:choose>
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
																								<bbbl:label key="lbl_dsk_pdp_price_is_tbd" language ="${pageContext.request.locale.language}"/>
																							</span>
																							</c:when>
																							<c:otherwise>
																								<span class="prodPrice"><dsp:valueof value="${PersonalizedPrice}" number="0.00" converter="currency"/></span>
																								
																								<c:if test="${isPersonalizationIncomplete eq 'true' &&  personalizationType != 'PB'}">
																									<span class= "personalizationProgress"><bbbl:label key="lbl_sfl_personalization_price" language="<c:out param='${language}'/>"/></span>
																								</c:if>
																							</c:otherwise>
																					 	</c:choose>
																			</c:when>
																			<c:otherwise>
																				<span class="prodPrice"><dsp:valueof value="${PersonalizedPrice}" number="0.00" converter="currency"/></span>
																			</c:otherwise>
																		</c:choose>                                   
														<c:set var="omnitemPrice"><dsp:valueof value="${itemPriceD}" converter="unformattedCurrency"/></c:set>
                                                            
																<c:if test="${prdShipMsgFlag}">
																<div class="freeShipBadge">
																	${prdDisplayShipMsg}
																</div>
																</c:if>  
															
                                                            <div class="clear"></div>
                                                        </span>
                                                        </div>
                                                         <div class="wishlistQuantityDetails clearfix">
															<table class="prodDeliveryInfo padLeft_10 padTop_10">
															<tbody>
															<c:if test="${not flagOff}">
																	<tr class="quantityBox">
																	    <td>
																		<label for="quantity_text_${skuID}" id="lblquantity_text_${skuID}" class="hidden"><bbbl:label key="lbl_cartdetail_quantity" language="<c:out param='${language}'/>"/> ${quantity} of ${skuDisplayName}</label>
                                                                        <div class="spinner"> 
																		<input name="${wishListItemId}" aria-required="true" aria-labelledby="quantity_text_${skuID}" type="text" value="${quantity}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" id="quantity_text_${skuID}" maxlength="2" class="moveToCartData itemQuantity fl"/>
                                                                       <div class="clear"></div></div>
																	</td>
																	</tr>
															</c:if>
																<dsp:input bean="GiftlistFormHandler.fromCartPage" type="hidden" value="true" />
																<dsp:input bean="GiftlistFormHandler.productId" type="hidden" value="${prodID}" />
																<dsp:input bean="GiftlistFormHandler.catalogRefIds" type="hidden" value="${skuID}" />
																<%-- Client DOM XSRF | Part -2
																<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL" type="hidden" value="/store/cart/ajax_handler_cart.jsp" />
																<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsErrorURL" type="hidden" value="/store/cart/ajax_handler_cart.jsp" /> --%>
																<dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="updateGiftlist" />
																<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
																<dsp:setvalue paramvalue="wishlist.id" param="giftlistId" />
																<dsp:input bean="GiftlistFormHandler.giftlistId" value="${giftListId}" type="hidden" />
																<dsp:input bean="GiftlistFormHandler.currentItemId" value="${wishListItemId}" type="hidden" />
																<dsp:input bean="GiftlistFormHandler.bts" value="${saveBts}" type="hidden" />
																<c:if test="${not flagOff}">
																	<tr class="lnkUpdate"><td><a href="javascript:void(0);" class="triggerUpdate" role="link" aria-label="Update quantity ${lblForThe} ${productVO.name}" data-submit-button="btnUpdateSFL0${currentCount}" title="Update Quantity" onclick="updateWishList('${prodID}','${skuID}');"><strong><bbbl:label key="lbl_sfl_update" language="<c:out param='${language}'/>"/></strong></a></td></tr>
																</c:if>
																<dsp:getvalueof id="giftlistId" param="wishlist.id"/>
																<input type="hidden" name="removeGiftitemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
																<input type="hidden" name="giftlistId" class="frmAjaxSubmitData" value="${giftlistId}" />
																<tr class="lnkRemove"><td><a href="#" data-ajax-frmID="frmSaveItemRemove" role="link" aria-label="Update quantity ${lblForThe} ${productVO.name}" class="btnAjaxSubmitSFL" data-submit-button="btnRemoveSFL0${currentCount}" title="Remove Product from Save For Later" onclick="removeWishList('${prodID}','${skuID }');"><strong><bbbl:label key="lbl_sfl_remove" language="<c:out param='${language}'/>"/></strong></a></td></tr>
																</tbody>
															</table>
															<div class="clear"></div>
															<dsp:input bean="GiftlistFormHandler.updateGiftlistItems" name="btnUpdate" id="btnUpdateSFL0${currentCount}" type="submit" value="Update" iclass="hidden" />
														</div>

                                                        <div class="wishlistTotalDetails clearfix">
                                                            <div class="clearfix noMar">
                                                                <div class="prodInfo">
                                                                <span class="padLeft_20 fl prodPrice">
																				<bbbl:label key="lbl_save_total" language ="${pageContext.request.locale.language}"/> 
																			</span>
																		<c:set var="Price" ><dsp:valueof  value="${PersonalPrice}" number="0.00" /></c:set>
                                                                    <span class="prodPrice">
                                                                    <c:choose>
                                                                         <c:when test="${(not empty referenceNumber && !eximErrorExists && enableKatoriFlag) || empty referenceNumber}">
                                                                               <dsp:include page="/global/gadgets/formattedPrice.jsp">
                                                                           <dsp:param name="price" value="${Price * quantity}"/>
                                                                        </dsp:include>
                                                                          </c:when>
                                                               			<c:otherwise>
                                                                     			<bbbl:label key="lbl_cart_tbd" language ="${pageContext.request.locale.language}"/> 
                                                               			</c:otherwise>
                                                          			</c:choose>

                                                                    </span>
                                                                <div class="clear"></div>
                                                                <c:if test="${isPersonalizationIncomplete  && (personalizationType eq 'PY' || personalizationType eq 'CR') && Price > 0.01}">
                                                                        <p class= "floatRight personalizationProgressMsg personalizationProgress">
                                                             <bbbl:label key="lbl_sfl_personalization_price" language="<c:out param='${language}'/>" /></p>
                                                                </c:if>
                                                                </div>
                                                                 <c:if test="${isLtlFlag}">
                                                                       <div class="cartShipMethodInfo padLeft_10 padTop_10 padBottom_20">
																			    <dsp:getvalueof var="ltlShipMethodDesc" param="element.ltlShipMethodDesc"/>
																				<dsp:getvalueof var="deliverySurcharge" param="element.deliverySurcharge"/>
																				<dsp:getvalueof var="assemblyFee" param="element.assemblyFees"/>
																				<c:choose>
																	              <c:when test="${ltlShipMethod == null || shipMethodUnsupported}">
				                                                                <dt class="padLeft_10 fl noPadBot">+ <bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</dt>
				                                                                  <dd class="fr noPadBot padRight_10">TBD</dd>
			                                                                     </c:when>
																				 <c:otherwise>
																				<dt class="padLeft_10 fl noPadBot">+ ${ltlShipMethodDesc}</dt>
				                                                                 <dd class="fr noPadBot padRight_10">
				                                                                   <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
				                                                                    <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
				                                                                   </dd>
																				  </c:otherwise>
																				  </c:choose>
																			<c:if test="${assemblyFee gt 0.0 && !shipMethodUnsupported}">
																			<div class="cartShipMethodInfo padBottom_35">
																				  <dt class="padLeft_20 fl noPadBot">+ <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:</dt>
																				 <dd class="fr noPadBot padRight_10"><dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/> </dd>
																			</div>
			                                                                </c:if>
																			</div>
			                                                                </c:if>
			                                                                
															<dsp:getvalueof id="cartQuantity" param="element.quantity"/>
															<dsp:getvalueof id="wishListItemId" param="element.wishListItemId"/>
															<dsp:getvalueof var="ltlShipMethodToSend" param="element.ltlShipMethod"/>
															<input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />
															<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
															<input type="hidden" name="cartQuantity" class="frmAjaxSubmitData" value="${cartQuantity}" />
															<input type="hidden" name="cartWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
															<input type="hidden" name="iCount" class="frmAjaxSubmitData" value="${currentCount}" />
															<input type="hidden" name="registryId" class="frmAjaxSubmitData" value="${registryId}" />
															<input type="hidden" name="saveBts" class="frmAjaxSubmitData" value="${saveBts}" />
															<input type="hidden" name="shipMethodUnsupported"  class="frmAjaxSubmitData" value="${shipMethodUnsupported}" />
															<input type="hidden" name="refNum"  class="frmAjaxSubmitData" value="${referenceNumber}" />
															<c:if test="${ltlShipMethodToSend eq 'LWA'}">
																<dsp:getvalueof var="ltlShipMethodToSend" value="LW"/>
																<input type="hidden" name="whiteGloveAssembly"  class="frmAjaxSubmitData" value="true" />
															</c:if>
															<c:if test="${ltlShipMethodToSend eq 'LW'}">
																<input type="hidden" name="whiteGloveAssembly"  class="frmAjaxSubmitData" value="false" />
															</c:if>
															<c:choose>
																<c:when test="${shipMethodUnsupported eq true}">
																		<input type="hidden" name="prevLtlShipMethod"  class="frmAjaxSubmitData" value="${ltlShipMethodToSend}" />
													        	</c:when>
																<c:otherwise>
																		<input type="hidden" name="ltlShipMethod"  class="frmAjaxSubmitData" value="${ltlShipMethodToSend}" />
																		<input type="hidden" name="ltlDeliveryServices"  class="frmAjaxSubmitData" value="" />
																</c:otherwise>
															</c:choose>
															<c:choose>
																<c:when test="${not empty referenceNumber}">
																	<c:if test="${not empty personalizationOptions}">
																		<c:set var="cusDet">${eximCustomizationCodesMap[personalizationOptions]}</c:set>
																		<input type="hidden" name="cusDet"  class="frmAjaxSubmitData" value="${cusDet}" id="customizationDetails"/>
																	</c:if>
																</c:when>
																<c:otherwise>
																	<c:set var="cusDet"></c:set>
																</c:otherwise>
															</c:choose>
                                                            <c:set var="mveToCart" value="${false}"/>
                                                            
                                                            <c:set var="OmniItemPrice"><dsp:valueof value="${Price*quantity}" number="0.00"  converter="unformattedCurrency"/></c:set>
															<input type="hidden" value="${OmniItemPrice}" name="OmniItemPrice">
															 <c:if test="${(empty priceMessageVO) or ((not empty priceMessageVO) and (priceMessageVO.inStock))}">
																<c:set var="mveToCart" value="${true}"/>
																<div>
																	<div class="button fr marBottom_10 padRight_10 <c:if test="${(isInternationalCustomer && isIntlRestricted) or (isPersonalizationIncomplete) or eximErrorExists or  
																	(isInternationalCustomer && isPersonalized) or (not empty referenceNumber && !enableKatoriFlag)}">button_disabled</c:if>">
                                                                        <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                                                            <dsp:param name="value" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
                                                                            <dsp:oparam name="false">
																			<c:choose>
																				<c:when test="${not empty cusDet}">
																				
																				<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="btnAjaxSubmitSFL moveToCart" aria-pressed="false" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) or isPersonalizationIncomplete or  (isInternationalCustomer && isPersonalized) or eximErrorExists
																					or (not empty referenceNumber && !enableKatoriFlag)}">disabled="disabled"</c:if> />
																				</c:when>
																				<c:otherwise>
																					<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="btnAjaxSubmitSFL moveToCart" aria-pressed="false" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) or isPersonalizationIncomplete or  (isInternationalCustomer && isPersonalized) or eximErrorExists
																					or (not empty referenceNumber && !enableKatoriFlag)}">disabled="disabled"</c:if> />
																				</c:otherwise>
																			</c:choose>
																			</dsp:oparam>
                                                                            <dsp:oparam name="true">
																				<c:choose>
																					<c:when test="${not empty cusDet}">
																						<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="btnAjaxSubmitSFL moveToCart" aria-pressed="false" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) or isPersonalizationIncomplete or (isInternationalCustomer && isPersonalized) or eximErrorExists
																						or (not empty referenceNumber && !enableKatoriFlag)}">disabled="disabled"</c:if> />
																					</c:when>
																					<c:otherwise>
																						<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="btnAjaxSubmitSFL moveToCart" aria-pressed="false" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) or isPersonalizationIncomplete or  (isInternationalCustomer && isPersonalized) or eximErrorExists
																						or (not empty referenceNumber && !enableKatoriFlag)}">disabled="disabled"</c:if> />
																					</c:otherwise>
																				</c:choose>
                                                                            </dsp:oparam>
                                                                        </dsp:droplet>

																	</div>
																	<c:if test="${isInternationalCustomer && isIntlRestricted or (isInternationalCustomer && isPersonalized)}">
																<div class="notAvailableIntShipMsg saveForLaterIntShipMsg fr padBottom_10 cb clearfix"><bbbl:label key="lbl_cart_intl_restricted_message" language="${pageContext.request.locale.language}" /></div>
																	</c:if>
																	<div class="clear"></div>
																</div>
															</c:if>
																		<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />

																		<c:if test="${bopusoff and mapQuestOn and (not mveToCart)}">
		                                                                	<div>
			                                                                    <div class="button fr marBottom_10 <c:if test="${isInternationalCustomer eq true  or  not empty referenceNumber && referenceNumber != null }">disabled ptrEvtNone</c:if>">
																					<dsp:getvalueof id="id" param="element.wishListItemId" />
																					<input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="productId" name="productId">
																					<input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
																					<input type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity">
																					<c:if test="${not empty registryId}">
																						<input type="hidden" value="${registryId}" data-dest-class="registryId" class="registryId" data-change-store-submit="registryId" name="registryId">
																					</c:if>
																					<input type="hidden" name="pageURL" value="${contextPath}/cart/ajax_handler_cart.jsp" data-change-store-submit="pageURL"/>
																					<input type="hidden" name="check" value="check" data-dest-class="check" data-change-store-submit="check" />
																					<input type="hidden" name="count" value="${currentCount}" data-dest-class="count" data-change-store-submit="count" />
																					<input type="hidden" name="wishlistItemId" value="${id}" data-dest-class="wishlistItemId" data-change-store-submit="wishlistItemId" />

																						<input type="button" class="changeStore" value="Find in Store" role="button" aria-pressed="false"  <c:if test="${isInternationalCustomer eq true|| not empty referenceNumber && referenceNumber != null}">disabled="disabled"</c:if>"/>


			                                                                    </div>
			                                                                    <div class="clear"></div>
		                                                                	</div>
		                                                               </c:if>

															<div>
                                                                            <dsp:input bean="GiftRegistryFormHandler.skuId" value="${skuID}" type="hidden" />
                                                                            <dsp:input bean="GiftRegistryFormHandler.productId" value="${prodID}" type="hidden" />
                                                                            <dsp:input bean="GiftRegistryFormHandler.quantity" type="hidden" value="${commItem.BBBCommerceItem.quantity}" />
                                                                                    <c:if test="${not flagOff}">
			                                                                            <dsp:getvalueof var="transient" bean="Profile.transient" />
																						<input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />
																						<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
																						<input type="hidden" name="regQuantity" class="frmAjaxSubmitData" value="${quantity}" />
																						<input type="hidden" name="reqWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
																						<input type="hidden" name="currentCount" class="frmAjaxSubmitData" value="${currentCount}" />
																						<input type="hidden" name="refNum"  class="frmAjaxSubmitData" value="${referenceNumber}" />
                                          												<input type="hidden" name="personalizationType"  class="frmAjaxSubmitData" value="${personalizationType}" />
                                          												<input type="hidden" name="customizableCodes"  class="frmAjaxSubmitData" value="${customizableCodes}" />
																						<c:if test="${not empty referenceNumber}">
																						<c:if test="${not empty personalizationOptions}">
																							<c:set var="persDet">${eximCustomizationCodesMap[personalizationOptions]}</c:set>
																							<input type="hidden" name="cusDet"  class="frmAjaxSubmitData" value="${persDet}" id="customizationDetails"/>
																						</c:if>
																						</c:if>


			                                                                            <c:choose>
			                                                                                <c:when test="${transient == 'false'}">
																							<c:choose>
                                                                                        <c:when test="${isLtlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false')}">
                                                                                         </c:when>
                                                                                         <c:otherwise>
			                                                                                    <c:choose>
			                                                                                        <c:when test="${sizeValue>1 && (isInternationalCustomer || isPersonalizationIncomplete) or (not empty referenceNumber && !enableKatoriFlag)}">
			                                                                                        <div class="fr btnAddToRegistrySel padRight_10">
			                                                                                        <div class="upperCase addToRegistry addToRegistrySel">
			                                                                                        <dsp:droplet name="ForEach">
																													<dsp:param name="array" value="${registrySkinnyVOList}" />
																													<dsp:oparam name="output">
																														<dsp:param name="futureRegList" param="element" />
																														<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																														<dsp:getvalueof var="alternateNumber"
																															param="futureRegList.alternatePhone" />
																														<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
																															<input type="hidden" id="${regId}" value="${alternateNumber}" name="altNum" class="frmAjaxSubmitData"/>
																														</dsp:oparam>
																														</dsp:droplet>
																														<input type="hidden" value="" name="alternateNum" class="frmAjaxSubmitData"/>
			                                                                                        <div class="select button_select">
																											<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
			                                                                                        		<dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
																												<dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
			                                                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
			                                                                                                        <dsp:option> <bbbl:label key="lbl_move_to_registry"
			                                                                                                                    language="${pageContext.request.locale.language}" /></dsp:option>
			                                                                                                            <dsp:droplet name="ForEach">
			                                                                                                                <dsp:param name="array" value="${registrySkinnyVOList}" />
			                                                                                                                <dsp:oparam name="output">
			                                                                                                                    <dsp:param name="futureRegList" param="element" />
			                                                                                                                    <dsp:getvalueof var="regId"	param="futureRegList.registryId" />
			                                                                                                                    <dsp:getvalueof var="event_type" param="element.eventType" />
			                                                                                                                    <dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
			                                                                                                                    <option value="${regId}"  class="${event_type}" data-poboxflag="${poBoxAddress}" data-notify-reg="true" >
			                                                                                                                        <dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
			                                                                                                                    </option>
			                                                                                                                </dsp:oparam>
			                                                                                                            </dsp:droplet>
																														<dsp:tagAttribute name="disabled" value="disabled"/>
			                                                                                                </dsp:select>

																										<c:choose>
																											<c:when test="${not empty referenceNumber && personalizationOptions!=null && not empty personalizationOptions}">
																												<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" role="button" aria-pressed="false" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
																											</c:when>
																											<c:otherwise>
																												<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" role="button" aria-pressed="false" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
																											</c:otherwise>
																										</c:choose>
			                                                                                        </div>
			                                                                                        </div>
			                                                                                        <div class="clear"></div>
			                                                                                        <div class="clear"></div>
			                                                                                        </div>
			                                                                                        </c:when>
																									<c:when test="${sizeValue>1 && not isInternationalCustomer}">
			                                                                                        <div class="fr btnAddToRegistrySel padRight_10">
			                                                                                        <div class="upperCase addToRegistry addToRegistrySel">
			                                                                                        <dsp:droplet name="ForEach">
																													<dsp:param name="array" value="${registrySkinnyVOList}" />
																													<dsp:oparam name="output">
																														<dsp:param name="futureRegList" param="element" />
																														<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																														<dsp:getvalueof var="alternateNumber"
																															param="futureRegList.alternatePhone" />
																														<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
																															<input type="hidden" id="${regId}" value="${alternateNumber}" name="altNum" class="frmAjaxSubmitData"/>
																														</dsp:oparam>
																														</dsp:droplet>
																														<input type="hidden" value="" name="alternateNum" class="frmAjaxSubmitData"/>
			                                                                                        <div class="select button_select">
																											<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
			                                                                                        		<dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
																												<dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
			                                                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
			                                                                                                        <dsp:option> <bbbl:label key="lbl_move_to_registry"
			                                                                                                                    language="${pageContext.request.locale.language}" /></dsp:option>
			                                                                                                            <dsp:droplet name="ForEach">
			                                                                                                                <dsp:param name="array" value="${registrySkinnyVOList}" />
			                                                                                                                <dsp:oparam name="output">
			                                                                                                                    <dsp:param name="futureRegList" param="element" />
			                                                                                                                    <dsp:getvalueof var="regId"	param="futureRegList.registryId" />
			                                                                                                                    <dsp:getvalueof var="event_type" param="element.eventType" />
			                                                                                                                    <dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
			                                                                                                                    <option value="${regId}"  class="${event_type}" data-poboxflag="${poBoxAddress}" data-notify-reg="true" >
			                                                                                                                        <dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
			                                                                                                                    </option>
			                                                                                                                </dsp:oparam>
			                                                                                                            </dsp:droplet>
			                                                                                                </dsp:select>
																									<c:choose>
																										<c:when test="${not empty referenceNumber && personalizationOptions!=null && not empty personalizationOptions }">
																											<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" role="button" aria-pressed="false" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
																										</c:when>
																										<c:otherwise>
																											<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" role="button" aria-pressed="false" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
																										</c:otherwise>
																									</c:choose>
			                                                                                        </div>
			                                                                                        </div>
			                                                                                        <div class="clear"></div>
			                                                                                        <div class="clear"></div>
			                                                                                        </div>
			                                                                                        </c:when>
			                                                                                        <c:when test="${sizeValue==1}">
			                                                                                        	<dsp:droplet name="ForEach">
			                                                                                            	<dsp:param name="array" value="${registrySkinnyVOList}" />
			                                                                                                	<dsp:oparam name="output">
			                                                                                                     	<dsp:param name="futureRegList" param="element" />
			                                                                                                        <dsp:getvalueof var="regId"
			                                                                                                        	param="futureRegList.registryId" />
			                                                                                                        <dsp:getvalueof var="registryName"
			                                                                                                        	param="futureRegList.eventType" />
			                                                                                                        <dsp:getvalueof var="alternateNumber" param="futureRegList.alternatePhone" />
			                                                                                                        <dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
                                                                                                               		<input type="hidden" id="${regId}" value="${alternateNumber}" name="altNum" class="addItemToRegis"/>
                                                                                                               		<input type="hidden" value="" name="alternateNum" class="frmAjaxSubmitData"/>
			                                                                                                        <input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
			                                                                                                        <dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="${regId}" />
			                                                                                                        <input  type="hidden"value="${registryName}" name="registryName" class="addItemToRegis omniRegName" />
			                                                                                                        <dsp:setvalue bean="GiftRegistryFormHandler.registryName" value="${registryName}" />
			                                                                                                   	</dsp:oparam>
			                                                                                           	</dsp:droplet>
			                                                                                        	<div class="fr addToRegistry">
				                                                                                            <div class="button <c:if test="${isInternationalCustomer or isPersonalizationIncomplete or (not empty referenceNumber && !enableKatoriFlag)}">button_disabled</c:if>">
																											<input type="hidden" name="reqRegistryId" class="frmAjaxSubmitData omniRegId" value="${regId}" <c:if test='${isInternationalCustomer || isPersonalizationIncomplete}'>disabled="disabled"</c:if>/>
				                                                                                            <%-- <input class="moveToReg" name="btnAddToRegistry" type="button" value="Move to Registry"/> --%>

				                                                                                      <c:choose>
																										<c:when test="${not empty referenceNumber && enableKatoriFlag && personalizationOptions!=null && not empty personalizationOptions}">
																											<input class="btnAjaxSubmitSFL moveToReg" name="123reqRegistryId" type="button" value="Move to Registry" data-poboxflag="${poBoxAddress}" data-notify-reg="true" data-ajax-frmID="frmRegSaveForLater" role="button" <c:if test='${isInternationalCustomer || isPersonalizationIncomplete}'>disabled="disabled"</c:if>  aria-pressed="false" />
																										</c:when>
																										<c:otherwise>
																											<input class="btnAjaxSubmitSFL moveToReg" name="123reqRegistryId" type="button" value="Move to Registry" data-poboxflag="${poBoxAddress}" data-notify-reg="true" data-ajax-frmID="frmRegSaveForLater" role="button" <c:if test='${isInternationalCustomer || isPersonalizationIncomplete}'>disabled="disabled"</c:if>  aria-pressed="false" />
																										</c:otherwise>
																									</c:choose>
				                                                                                            </div>
				                                                                                        </div>
				                                                                        			</c:when>
				                                                                    			</c:choose>
																								</c:otherwise>
				                                                                    			</c:choose>
			                                                                    				</c:when>
			                                                                    			</c:choose>
																						</c:if>
                                                                    <div class="clear"></div>
                                                                </div>
                                                        </div>
                                                    

													<div class="clear"></div>

                                                    <input type="submit" name="btnPickUpInStore" id="btnPickUpInStoreSFL${skuID}" class="hidden pickUpInStoreSFL" value="PickUpInStore" role="button" aria-pressed="false" aria-labelledby="btnPickUpInStoreSFL${skuID}" />
                                                </div>
                                                <div class="clear"></div>

                                            <div class="clear"></div>
                                        </li>

										<c:if test="${check}">
											<dsp:getvalueof id="storeIdCart" bean="CartModifierFormHandler.storeId" />
											<dsp:getvalueof id="wishlistItemIdCart" bean="CartModifierFormHandler.wishlistItemId" />
											<c:if test="${not empty storeIdCart and not empty wishlistItemIdCart}">
													<li class="savedItemRow itemFindInStoreUndoLink"></li>
											</c:if>
											<dsp:include page="saveUndoLink.jsp">
												<dsp:param name="movedCommerceItemId" value="${movedCommerceItemId}"/>
												<dsp:param name="fromWishlist" value="${fromWishlist}"/>
												<dsp:param name="quantity" value="${quantityCart}"/>
												<dsp:param name="currentCount" value="${countNo}"/>
												<dsp:param name="btsValue" value="${btsValue}"/>
											</dsp:include>
										</c:if>
                                    </dsp:oparam>
                                    <dsp:oparam name="outputEnd">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" bean="GiftRegistryFormHandler.movedItemMap" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="movedItemCount" param="key" />
												<dsp:getvalueof var="productID" param="element" />
												<dsp:getvalueof bean="GiftRegistryFormHandler.skuId" var="skuId" />
												<c:if test="${movedItemCount eq currentCount+1}">
												<li id="savedItemID_${skuID}" class="savedItemRow movedItem clearfix movedToReg itemMovedToRegUndoLink">
														<div class="prodItemWrapper clearfix">
														<input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
															<p class="width_7 noMar padLeft_10 padTop_10 padBottom_10 fl prodInfo breakWord textLeft">
																<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
																	<dsp:param name="id" value="${productID}" />
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
																	<dsp:param name="skuId" value="${skuId}"/>
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="prodName" param="pSKUDetailVO.displayName" />
																		<dsp:getvalueof var="isLtlFlag" param="pSKUDetailVO.ltlItem" />
																	</dsp:oparam>
																</dsp:droplet>
																<input type="hidden" name="ltlFlag" value="${isLtlFlag}"/>
																<dsp:a iclass="prodName" title="${prodName}" page="${finalUrl}?skuId=${skuId}">${prodName}</dsp:a> has been moved to registry #<dsp:valueof bean="GiftRegistryFormHandler.movedItemRegistryId" />.
															</p>
															<p class="width_2 noMar padRight_10 padTop_10 padBottom_10 fr textRight">

																		<dsp:droplet name="GetRegistryVODroplet">
																			<dsp:param name="siteId" value="${applicationId}"/>
																			<dsp:param bean="GiftRegistryFormHandler.movedItemRegistryId" name="registryId" />
																			<dsp:param value="true" name="isRegTypeNameReq" />
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="registryVO" param="registryVO"/>
																				<dsp:getvalueof var="eventType" param="registryTypeName"/>
																				<c:if test="${not empty eventType}">
																					<dsp:a iclass="lnkAction viewRegistry" href="/store/giftregistry/view_registry_owner.jsp" title="View your registry">View your registry
																						<dsp:param name="registryId" bean="GiftRegistryFormHandler.movedItemRegistryId"/>
																						<dsp:param name="eventType" param="registryTypeName"/>
																					</dsp:a>
																				</c:if>
																			</dsp:oparam>
																		</dsp:droplet>
															</p>
															<div class="clear"></div>
														</div>
														<div class="clear"></div>
													<div class="clear"></div>
												</li>
												</c:if>
											</dsp:oparam>
										</dsp:droplet>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </ul>
                            <div class="clear"></div>
							</dsp:form>
                        </div>
						</div>
						</div>
                        <div class="clear"></div>
                    </dsp:oparam>
                </dsp:droplet>
</dsp:page>