<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<dsp:importbean
		bean="/com/bbb/common/droplet/ListPriceSalePriceDroplet" />
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet" />
	<dsp:importbean bean="/atg/multisite/SiteContext" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean
		bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean
		bean="/atg/commerce/promotion/ClosenessQualifierDroplet" />
	<dsp:importbean
		bean="/com/bbb/profile/session/BBBSavedItemsSessionBean" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet" />
	 <dsp:importbean bean="/com/bbb/commerce/cart/TopStatusChangeMessageDroplet" />
	<dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona"
		bean="CertonaConfig.siteIdAppIdMap.${applicationId}" />
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof id="movedCommerceItemId"
		bean="CartModifierFormHandler.commerceItemId" />
	<dsp:getvalueof id="fromWishlist"
		bean="CartModifierFormHandler.fromWishlist" />
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet" />
	<dsp:droplet name="RepriceOrderDroplet">
		<dsp:param value="ORDER_SUBTOTAL" name="pricingOp"/>
	</dsp:droplet>
	<c:set var="lblReviewsCount"><bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></c:set>
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="prodImageAttrib" scope="request">class="fl productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
    <c:if test="${disableLazyLoadS7Images eq true}">
        <c:set var="prodImageAttrib" scope="request">class="fl productImage noImageFound" src</c:set>        
    </c:if>
    <c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
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
	<c:set var="enableLTLRegForSite">
		<bbbc:config key="enableLTLRegForSite"
			configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="enableKatoriFlag" scope="request">
		<bbbc:config key="enableKatori" configName="EXIMKeys" />
	</c:set>
	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">wishlist savedItems myAccount</jsp:attribute>
		<jsp:body>
        <div id="content" class="container_12 clearfix">
          <div id="saveForLaterHeader" class="grid_12">
              <h1 class="account fl">My Account</h1>
              <h3 class="subtitle fl">
						<bbbl:label key="lbl_save_item"
							language="${pageContext.request.locale.language}" />
					</h3>
               <c:set var="hidden" value="" />
               <dsp:droplet
						name="/com/bbb/common/droplet/EximCustomizationDroplet">
			       <dsp:oparam name="output">
				      <dsp:getvalueof var="eximCustomizationCodesMap"
								param="eximCustomizationCodesMap" />
			       </dsp:oparam>
		      </dsp:droplet>
              <dsp:droplet
						name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
              	<dsp:param name="addMSG" value="false" />
              	<dsp:oparam name="empty">
			 		 <c:set var="hidden" value="hidden" />
			 	 </dsp:oparam>
			  </dsp:droplet>
			    <ul class="grid_2 share omega ${hidden}">
                  <li><a href="#" class="print"
							title="Print Saved Items"><bbbl:label
									key="lbl_save_print_item"
									language="${pageContext.request.locale.language}" /></a></li>
                  <li><a href="#"
							class="email avoidGlobalEmailHandler" title="Email Saved Items"
							id="openEmailSavedItems"><bbbl:label
									key="lbl_save_email_item"
									language="${pageContext.request.locale.language}" /></a></li>
              </ul>
              <div class="clear"></div>
          </div>

          <div class="grid_2">
              <c:import url="/account/left_nav.jsp">
              <c:param name="currentPage">
							<bbbl:label key="lbl_myaccount_wish_list"
								language="${pageContext.request.locale.language}" />
						</c:param>
			  </c:import>
          </div>
           <div id="saveForLaterBody" class="grid_10 clearfix">
           
   		       <dsp:droplet name="TopStatusChangeMessageDroplet">
   		    <dsp:param name="reqFrom" value="wish" />
				<dsp:oparam name="outputPrice">
					<dsp:getvalueof id="priceMessageVO" param="priceMessageVO" />
					<dsp:getvalueof var="oosItems" param="oosItems" />
					<dsp:getvalueof var="priceMessageVoList" param="priceMessageVoList"/>
					<dsp:getvalueof var="outOfStockItem"  param="oosVOList"/>
					<%-- <c:set var="oosItems" value=${oosItems}/> --%>
					<input type="hidden" id="itemsToRemove" value="oosItems"/>	
					<c:if test="${(not empty priceMessageVO) and ((priceMessageVO.flagOff) or (not priceMessageVO.inStock)) && (not empty oosItems)}">
					<c:if test="${not priceMessageVO.inStock || priceMessageVO.flagOff}">
						<div id="saveForLaterTopMessaging" class="clearfix grid_10 alpha omega">
		          <div class="genericRedBox savedItemChangedWrapper clearfix closeWinWrapper marBottom_10 changeStoreItemWrap">
			           <h2 class="heading">
						<bbbl:label key="lbl_item_oos_unavailable" language="${pageContext.request.locale.language}" />
						<p><bbbl:label key="lbl_cart_top_oos_please_remove" language="${pageContext.request.locale.language}" />
						<a href="#" class='removeItems btnAjaxSubmitCart' data-ajax-frmID="frmCartRemoveOOS"><bbbl:label key="lbl_cart_top_oos_remove_item" language="${pageContext.request.locale.language}" /></a>
						</p>
						</h2>
					
				   </div>
				</div>
						</c:if>
				</c:if>
					</dsp:oparam>
				</dsp:droplet>
           <c:if test="${empty hidden}">
	           <dsp:include otherContext="${contextPath}"
							page="/cart/topLinkCart.jsp">
	           <dsp:param name="priceMessageVoList" value="${priceMessageVoList}"/>
				
	       		</dsp:include>
       		</c:if>
			<dsp:droplet
						name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
              <dsp:oparam name="empty">
              	<dsp:setvalue value="" param="items" />
                    <div id="saveForLaterEmptyMessaging"
								class="clearfix grid_10 alpha omega">
                      <p class="noMarTop">
						<bbbt:textArea key="txtarea_saveitem_empty"
										language="${pageContext.request.locale.language}" />
                      </p>
                    </div>
               </dsp:oparam>
              <dsp:oparam name="output">
              	<dsp:setvalue param="items" paramvalue="giftList" />

            <dsp:form action="wish_list.jsp" method="post">
                <dsp:input
									bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL"
									type="hidden" value="wish_list.jsp" />
                <dsp:input
									bean="GiftlistFormHandler.updateGiftlistItemsErrorURL"
									type="hidden" value="wish_list.jsp" />
                <dsp:input bean="GiftlistFormHandler.giftlistId"
									paramvalue="giftlistId" type="hidden" />
            </dsp:form>

            <%-- <dsp:form action="wish_list.jsp" method="post">
                <dsp:setvalue beanvalue="BBBSavedItemsSessionBean.giftListVO" param="items" />
                <dsp:getvalueof var="items" param="items">
                    items = ${items}
                </dsp:getvalueof>
                <dsp:valueof bean="BBBSavedItemsSessionBean.giftListVO"></dsp:valueof>
            </dsp:form> --%>

            <c:set var="flagOff" value="${false}"/>
               <dsp:droplet name="IsEmpty">
                    <dsp:param name="value" param="items" />
                    <dsp:oparam name="false">


                        <dsp:include
										page="/global/gadgets/errorMessage.jsp">
			                <dsp:param name="formhandler"
											bean="CartModifierFormHandler" />
			            </dsp:include>




                            <div id="saveForLaterContentWrapper"
										class="clearfix">
                                <div id="saveForLaterContent" class="clearfix grid_10 alpha omega productListWrapper">
                                	<dsp:include src="${contextPath}/cart/saveForlaterForms.jsp" />
                                   <dsp:form name="frmSavedItems"
												iclass="frmAjaxSubmit frmSavedItems"
												action="${contextPath}/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp">
                                    <dsp:droplet
													name="/atg/dynamo/droplet/ForEach">
                                        <dsp:param name="array"
														param="items" />
                                        <dsp:param name="reverseOrder"
														value="true" />
                                        <dsp:oparam name="outputStart">
                                            <div
															class="productsListHeader clearfix noBorderBot" aria-hidden="true" style="display:none">
                                                <div
																class="wishlistItemDetails clearfix"><strong><bbbl:label
																		key="lbl_save_items"
																		language="${pageContext.request.locale.language}" /></strong></div>
                                                <div
																class="wishlistQuantityDetails clearfix"><strong><bbbl:label
																		key="lbl_save_quan"
																		language="${pageContext.request.locale.language}" /></strong></div>
                                                <div
																class="wishlistTotalDetails clearfix"><strong><bbbl:label
																		key="lbl_save_total"
																		language="${pageContext.request.locale.language}" /></strong></div>
                                            </div>
                                            <div class="clear"></div>
                                            <ul class="productsListContent clearfix">
                                        
													</dsp:oparam>
                                        <dsp:oparam name="output">
											<c:set var="flagOff" value="${false}" />
											<c:set var="bopusoff" value="${false}" />
											<dsp:getvalueof id="iCount" param="count" />
                                            <dsp:getvalueof
															var="wishListItemId" param="element.wishListItemId" />
                                            <dsp:getvalueof
															var="productId" param="element.prodID" />
                                            <dsp:getvalueof
															var="catalogRefId" param="element.skuID" />
                                            <dsp:getvalueof
															var="registryId" param="element.registryId" />
                                            <dsp:setvalue
															paramvalue="element.giftListId" param="giftlistId" />
                                            <dsp:getvalueof
															var="quantity" param="element.qtyRequested" />
                                            <dsp:getvalueof
															var="itemQuantity" param="element.qtyRequested" />
                                            <dsp:setvalue param="itemId"
															paramvalue="element.wishListItemId" />
                                            <dsp:getvalueof id="id"
															param="element.wishListItemId" />
                                            <dsp:getvalueof
															var="priceMessageVO" param="element.priceMessageVO" />
                                            <dsp:getvalueof
															var="isIntlRestricted"
															param="element.productVO.intlRestricted" />
                                            <dsp:getvalueof
															var="referenceNumber" param="element.referenceNumber" />
					    					<dsp:getvalueof var="personalizePrice"
															param="element.personalizePrice" />
					    					<dsp:getvalueof var="personalizationOptions"
															param="element.personalizationOptions" />
											<dsp:getvalueof var="personalizationOptionsDisplay"
															param="element.personalizationOptionsDisplay" />
					    					<dsp:getvalueof var="personalizationDetails"
															param="element.personalizationDetails" />
					    					<dsp:getvalueof var="ltlShipMethod"
															param="element.ltlShipMethod" />
					    					<dsp:getvalueof var="personalizationStatus"
															param="element.personalizationStatus" />
					    					<dsp:getvalueof var="fullImagePath"
															param="element.fullImagePath" />
											<dsp:getvalueof id="giftlistId" param="giftlistId" />	
											<c:if test="${not empty referenceNumber}">	
												<c:if test="${not empty personalizationOptions}">
												<c:set var="cusDet">${eximCustomizationCodesMap[personalizationOptions]}</c:set>															
												<input type="hidden" value="${cusDet}"
																	id="customizationDetails" />
												</c:if>	
											</c:if>							
					    					<c:choose>
												<c:when
																test="${not empty referenceNumber && personalizationStatus eq 'saved'}">
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
                                            <c:if
															test="${not empty priceMessageVO}">
												<c:set var="bopusoff" value="${not priceMessageVO.bopus}" />
											</c:if>
											<c:choose>
												<c:when
																test="${not empty priceMessageVO and priceMessageVO.flagOff}">
													<c:set var="flagOff" value="${true}" />
												</c:when>
												<c:otherwise>
													<c:set var="flagOff" value="${false}"/>
												</c:otherwise>
											</c:choose>
                                            <dsp:getvalueof
															var="itemCount" param="count" />

                                            <c:set var="totalItems"
															value="${count}" />
                                            <dsp:getvalueof
															var="arraySize" param="size" />
                                            <%-- <dsp:getvalueof var="giftListVO" param="element" />
                                            <dsp:setvalue bean="BBBSavedItemsSessionBean.giftListVO.giftListVO" value="${giftListVO}" />
                                            giftlistVO  : <dsp:getvalueof var="giftListVO" bean="BBBSavedItemsSessionBean.giftListVO" /> :
                                            --%>

                                            <c:set var="displaySize">
                                                <bbbc:config
																key="sfl_display_size" configName='CartAndCheckoutKeys' />
                                            </c:set>
                                            <c:if
															test="${empty displaySize}">
                                                <c:set var="displaySize">2</c:set>
                                            </c:if>

                                            <c:if test="${itemCount == displaySize+1 && arraySize > displaySize}">
                                                <li id="savedItemID_BTN"
																class="savedItemRow showAllBtn">
                                                    <div
																	class="prodItemWrapper clearfix">
																	<input type="hidden" name="ltlFlag" value="${ltlFlag}"/>
                                                        <div
																		class="button">
                                                            <input
																			id="showAllBtn" type="button"
																			value="Show all ${arraySize} items" role="button"
																			aria-pressed="false" aria-labelledby="showAllBtn" />
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
                                          
                                               		<dsp:droplet
																name="SKUWishlistDetailDroplet">
														<dsp:param name="siteId" value="${applicationId}" />
														<dsp:param name="skuId" value="${catalogRefId}" />
														<dsp:param name="personalizedFlag" value="${isPersonalized}" />
														<dsp:param name="personalizedPrice" value="${personalizePrice}"/>
														<dsp:oparam name="output">
															<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
															<dsp:getvalueof var="prdDisplayShipMsg"
																	param="pSKUDetailVO.displayShipMsg" />
															<dsp:getvalueof var="prdShipMsgFlag" 
																	param="pSKUDetailVO.shipMsgFlag" />
															<dsp:getvalueof var="skuDisplayName"
																		param="pSKUDetailVO.displayName" />
															<dsp:getvalueof var="skuImageURL"
																		param="pSKUDetailVO.skuImages.mediumImage" />
															<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color" />
															<dsp:getvalueof var="skuSize" param="pSKUDetailVO.size" />
															<dsp:getvalueof var="ltlFlag"
																		param="pSKUDetailVO.ltlItem" />
															<dsp:getvalueof var="customizableRequired"
																		param="pSKUDetailVO.customizableRequired" />
															<dsp:getvalueof var="customizableCodes"
																		param="pSKUDetailVO.customizableCodes" />
															<dsp:getvalueof var="personalizationType"
																		param="pSKUDetailVO.personalizationType" />
															<dsp:getvalueof var="inCartFlagSKU" param="pSKUDetailVO.inCartFlag" /> 
														</dsp:oparam>
													</dsp:droplet>

											<c:if test="${ltlFlag && empty ltlShipMethod}">
												<li>       
													<div class="clear"></div>
													<div class="ltlMessage ltlMessagePadding">
														<bbbt:textArea key="txt_cart_saved_item_alert"
																	language="<c:out param='${language}'/>" />
													</div>
													<div class="ltlMessage ltlAlertmessage">
															<bbbl:label key='lbl_rlp_ltl_registry_message'
																	language="${pageContext.request.locale.language}" />
															<%--<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>--%>
														</div>												
												</li>
											</c:if>
                                            <li	id="savedItemID_${wishListItemId}"
															class="${lastRow} clearfix changeStoreItemWrap savedItemRow  <c:if test="${not empty registryId}">registeryItem</c:if>   <c:if test="${itemCount > displaySize && arraySize > displaySize}"> hidden </c:if>">
												<c:if test="${not empty personalizationOptions}">
													<c:set var="cusDet">${eximCustomizationCodesMap[personalizationOptions]}</c:set>															
													<input type="hidden" value="${cusDet}"
																	id="customizationDetails" />
												</c:if>	
												<dsp:getvalueof var="productVO" param="element.productVO" />
													 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param param="element.productVO.rollupAttributes"
																	name="array" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="menu" param="key" />
															<c:if test="${menu eq 'FINISH'}">
																<c:set var="rollupAttributesFinish" value="true" />
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
													<dsp:getvalueof var="prodImage" value="${skuImageURL}" />
													<dsp:getvalueof var="prodName" value="${skuDisplayName}" />

													<dsp:getvalueof var="shipMethodUnsupported"
																param="element.shipMethodUnsupported" />
													<%-- LTL Alert  for Ship method unsupported--%>
													<c:if test="${ltlFlag && shipMethodUnsupported}">
														<dsp:getvalueof var="shippingMethodDesc"
																	param="element.ltlShipMethodDesc" />
														<c:if test="${ltlShipMethod == 'LWA'}">
															<c:set var="shippingMethodDesc">
																		<bbbl:label key="lbl_white_glove_assembly"
																			language="<c:out param='${language}'/>" />
																	</c:set>
														</c:if>
														<jsp:useBean id="placeHolderMapServiceLevel"
																	class="java.util.HashMap" scope="request" />
														<c:set target="${placeHolderMapServiceLevel}"
																	property="ShippingMethodDesc"
																	value="${shippingMethodDesc}" />
														<div
																	class="ltlUnavailable ltlUnavailableSFL marBottom_10 marTop_10">
														<p class="padLeft_25">
															<bbbt:textArea key="txt_ship_method_not_supported"
																			placeHolderMap="${placeHolderMapServiceLevel}"
																			language="${pageContext.request.locale.language}" />
														</p>
														</div>
													</c:if>
	                                               <%-- LTL Alert  --%>
													<%-- LTL Alert  --%>
	                                                 <dsp:include
																otherContext="${contextPath}"
																page="/cart/itemLinkWishlist.jsp">
                                                        <dsp:param
																	name="id" value="${wishListItemId}" />
                                                        <dsp:param
																	name="fromwishlist" value="true" />
                                                        <dsp:param
																	name="image" value="${prodImage}" />
														<dsp:param name="displayName" value="${prodName}" />
														<dsp:param name="priceMessageVO" value="${priceMessageVO}" />
														<dsp:param name="isPersonalized" value="${isPersonalized}" />
														<dsp:param name="enableKatoriFlag"
																	value="${enableKatoriFlag}" />
													</dsp:include>


                                                    <%-- duplicate... these fields are already present in JSP ... /cart/itemLinkWishlist.jsp
                                                    <input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="prodId" name="productId">
													<input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
													<c:if test="${flagOff}">
														<input type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="1"  name="itemQuantity">
													</c:if> --%>
                                                   		<c:if
																test="${not empty registryId}">
																
															  <dsp:getvalueof var="registryVO"
																	param="element.registryVO" />
															  <dsp:droplet name="GetRegistryTypeNameDroplet">
																	<dsp:param name="siteId" value="${applicationId}" />
																	<dsp:param name="registryTypeCode"
																		value="${registryVO.registryType.registryTypeName}" />
																	<dsp:oparam name="output">
																		<c:set var="registryTypeName">
																			<dsp:valueof param="registryTypeName" />
																		</c:set>
																	</dsp:oparam>
															  </dsp:droplet>
															  <div class="registeryItemHeader clearfix">
																	<span><bbbl:label
																			key="lbl_cart_registry_from_text"
																			language="<c:out param='${language}'/>" /></span>
																	<strong>${registryVO.primaryRegistrant.firstName}<c:if
																			test="${not empty registryVO.coRegistrant.firstName}">&nbsp;&amp;&nbsp;${registryVO.coRegistrant.firstName}</c:if>
																		<bbbl:label key="lbl_cart_registry_name_suffix"
																			language="<c:out param='${language}'/>" /></strong>
																	<span>${registryTypeName}</span>&nbsp;<span><bbbl:label
																			key="lbl_cart_registry_text"
																			language="<c:out param='${language}'/>" /></span>
																</div>
															<div class="clear"></div>
														</c:if>

                                                    <div class="clear"></div>
                                                    <div class="prodItemWrapper padTop_5 clearfix">
																 <input type="hidden" name="ltlFlag" value="${ltlFlag}"/>
                                                                        <dsp:droplet
																		name="/atg/repository/seo/CanonicalItemLink">
                                                                            <dsp:param
																			name="id" value="${productId}" />
                                                                            <dsp:param
																			name="itemDescriptorName" value="product" />
                                                                            <dsp:param
																			name="repositoryName"
																			value="/atg/commerce/catalog/ProductCatalog" />
                                                                            <dsp:oparam
																			name="output">
                                                                                <c:choose>
                                                                                    <c:when
																					test="${not flagOff}">
                                                                                        <dsp:getvalueof
																						var="finalUrl" vartype="java.lang.String"
																						param="url" />
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <dsp:getvalueof
																						var="finalUrl" vartype="java.lang.String"
																						value="#" />
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </dsp:oparam>
                                                                        </dsp:droplet>


																	<div class="wishlistItemDetails clearfix<c:if test="${inCartFlagSKU}"> inCartContainer</c:if>">
                                                                        <div
																			class="wishlistItemsList">
																			
																		<c:choose>
																		<c:when test="${not flagOff}">
																		<c:choose>
																							      																						
																								  <c:when test="${ltlFlag && not empty ltlShipMethod}">
																								       <c:set var="url">${finalUrl}?skuId=${catalogRefId}&sopt=${ltlShipMethod}</c:set>	
																								 </c:when>
																								 <c:otherwise>
																								   	   <c:set var="url">${finalUrl}?skuId=${catalogRefId}</c:set>		
																								 </c:otherwise>
																								</c:choose>	
																		
																			<dsp:a iclass="prodImg clearfix block"
																						title="${prodName}"
																						page="${url}">
																				<c:choose>
																					<c:when test="${not empty referenceNumber}">
																						<c:choose>
																							<c:when
																										test="${not empty fullImagePath && enableKatoriFlag}">
																								<img class="fl productImage"
																											src="${fullImagePath}"
																											alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																											title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																											height="146" width="146" />
																							</c:when>
																							<c:otherwise>
																								<img class="fl productImage"
																											src="${imagePath}/_assets/global/images/no_image_available.jpg"
																											alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																											title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																											height="146" width="146" />
																							</c:otherwise>
																						</c:choose>
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${empty prodImage}">
																								<img
																											src="${imagePath}/_assets/global/images/no_image_available.jpg"
																											width="146" height="146" alt="${prodName}"
																											title="${prodName}" class="fl productImage" />
																							</c:when>
																							<c:otherwise>
																								<img
																											${prodImageAttrib}="${scene7Path}/${prodImage}" width="146"
																											height="146" alt="${prodName}"
																											title="${prodName}" />
																							</c:otherwise>
																						</c:choose>
																					</c:otherwise>
																				</c:choose>
																			</dsp:a>
																		</c:when>
																		<c:otherwise>
																		
                                                                            <dsp:a
																						iclass="prodImg clearfix block"
																						title="${prodName}"
																						page="${finalUrl}?skuId=${catalogRefId}">
																			<c:choose>
																				<c:when test="${not empty referenceNumber}">
																					<c:choose>
																						<c:when
																										test="${not empty fullImagePath && enableKatoriFlag}">
																							<img class="fl productImage"
																											src="${fullImagePath}"
																											alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																											title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																											height="146" width="146" />
																						</c:when>
																						<c:otherwise>
																							<img class="fl productImage"
																											src="${imagePath}/_assets/global/images/no_image_available.jpg"
																											alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																											title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																											height="146" width="146" />
																						</c:otherwise>
																					</c:choose>
																				</c:when>
																				<c:otherwise>
																					<c:choose>
																						<c:when test="${empty skuImageURL}">
																							<img
																											src="${imagePath}/_assets/global/images/no_image_available.jpg"
																											width="146" height="146" alt="${prodName}"
																											title="${prodName}" class="fl productImage" />
																						</c:when>
																						<c:otherwise>
																							<img
																											${prodImageAttrib}="${scene7Path}/${skuImageURL}" width="146"
																											height="146" alt="${prodName}"
																											title="${prodName}" />
																						</c:otherwise>
																					</c:choose>
																				</c:otherwise>
																			</c:choose>
                                                                           </dsp:a>
																		</c:otherwise>
																		</c:choose>
																		 <c:if test="${isPersonalizationIncomplete eq 'true'}">
                                                                                <div
																					class="PersonalizationProgressText personalizationProgress">
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
                                                                                    <div
																						class="progressHead">
																						${progressTxt}
																					</div>
                                                                                       <span>${completeTxt}</span>
                                                                                    </div>
                                                                          </c:if>
                                                                          </div>
                                                                        <span class="prodInfo block breakWord">
                                                                            <span class="prodName">
																			<c:choose>
																				<c:when test="${not flagOff}">
																				<c:choose>	
																							     																						
																								  <c:when test="${ltlFlag && not empty ltlShipMethod}">
																								       <c:set var="url">${finalUrl}?skuId=${catalogRefId}&sopt=${ltlShipMethod}</c:set>	
																								 </c:when>
																								 <c:otherwise>
																								   	   <c:set var="url">${finalUrl}?skuId=${catalogRefId}</c:set>		
																								 </c:otherwise>
																								</c:choose>	
																					<dsp:a page="${url}"
																							iclass="productName" title="${prodName}">
																						<dsp:valueof value="${prodName}"
																								valueishtml="true" />
																					</dsp:a>
																				</c:when>
																				<c:otherwise>
																					<span class="disableText"><dsp:valueof
																								value="${skuDisplayName}" valueishtml="true" /></span>
																				</c:otherwise>
																			</c:choose>
                                                                            </span>
																			<span class="prodAttribsSavedItem">
																				<c:if test='${not empty skuColor}'>
																					<c:choose>
																						<c:when test="${rollupAttributesFinish == 'true'}">
																							<bbbl:label key="lbl_item_finish"
																								language="${pageContext.request.locale.language}" /> : </c:when>
																						<c:otherwise>
																							<bbbl:label key="lbl_item_color"
																								language="${pageContext.request.locale.language}" /> : </c:otherwise>
																					</c:choose>
																					<dsp:valueof value="${skuColor}" valueishtml="true" />
																				</c:if>
																				<c:if test='${not empty skuSize}'>
																					<c:if test='${not empty skuColor}'>
																						<br />
																					</c:if>
																					<bbbl:label key="lbl_item_size"
																						language="${pageContext.request.locale.language}" /> : <dsp:valueof
																						value="${skuSize}" valueishtml="true" />
																				</c:if>
																				</br>
																				<c:if
																					test='${enableKatoriFlag && not empty personalizationOptions}'>${eximCustomizationCodesMap[personalizationOptions]} : <dsp:valueof
																						value="${personalizationDetails}"
																						valueishtml="true" />
																				</c:if>
																				<c:set var="rollupAttributesFinish" value="false" />
																			</span>
																			
																			<c:if test="${not empty referenceNumber && enableKatoriFlag}">
																				<c:if test="${not empty personalizationOptions}">
																					<span class="personalizationAttr katoriPrice">
																					<%--BBBSL-8154 --%>
																						<%-- <span aria-hidden="true">${personalizationOptionsDisplay} </span> --%>
																						<c:choose>
																							<c:when
																								test='${isPersonalizationIncomplete eq false && not empty personalizationType && personalizationType == "PY"}'>
																							 <dsp:valueof value="${personalizePrice}"
																									number="0.00" converter="currency" />&nbsp;<bbbl:textArea
																									key="txt_sfl_personal_price_msg"
																									language="<c:out param='${language}'/>" />
																						    </c:when>
																						    <c:when
																								test='${isPersonalizationIncomplete eq false && not empty personalizationType && personalizationType == "CR"}'>
																							 <dsp:valueof value="${personalizePrice}"
																									number="0.00" converter="currency" />&nbsp;
																									<c:choose>
																										<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
																											<bbbl:textArea
																												key="txt_pdp_cr_customize_katori_price"
																												language="<c:out param='${language}'/>" />
																										</c:when>
																										<c:otherwise>
																											<bbbl:textArea
																												key="txt_pdp_cr_katori_price"
																												language="<c:out param='${language}'/>" />
																										</c:otherwise>
																									</c:choose>
																									
																						    </c:when>
																							<c:when test='${not empty personalizationType && personalizationType != "PB" && isPersonalizationIncomplete}'>
                                                                                            <bbbl:label key="lbl_sfl_personalization_price" language="${pageContext.request.locale.language}" />
                                                                                            </c:when>																						    
																							<c:when
																								test='${not empty personalizationType && personalizationType == "PB"}'>
																							    <bbbl:label key="lbl_PB_Fee_detail"
																									language="${pageContext.request.locale.language}" />
																							</c:when>
																						</c:choose>
																					</span>
																				</c:if>
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
																	<a href="#"
																		class="editPersonalizationSfl bold <c:if test="${isInternationalCustomer}">disableText</c:if>"
																		title="${editPersonalizeTxt}"
                                                                                  	data-sku="${skuVO.skuId}" data-product="${productId}" data-refnum="${referenceNumber}" data-quantity="${quantity}" data-wishlistId="${giftlistId}" data-wishlistitemId="${wishListItemId}">
																		 			${editPersonalizeTxt}</a>
                                                                           	</div> 
	                                                                     </c:if>
																			</c:if>
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
																		  <dsp:oparam name="false">

																		  </dsp:oparam>
																	  </dsp:droplet>
																	  <dsp:getvalueof var="prodName" value="" />
																	   <dsp:getvalueof var="prodImage" value="" />
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
																			<c:if test="${not flagOff}">
																				<c:choose>
																					<c:when test="${ratings ne null && ratings ne '0' && (reviews eq '1' || reviews gt '1') }">
          																				<c:choose>
																				<c:when
																					test="${ltlFlag && not empty ltlShipMethod}">
																						<span tabindex="0" aria-label=" <fmt:formatNumber value="${rating * .10}"/> out of 5 stars ${lblForThe} ${productVO.name}" class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
			                                                               					<a href="${pageContext.request.contextPath}${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&sopt=${ltlShipMethod}&showRatings=true" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productVO.name}">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></a>
					                                                               		</span>
																					</c:when>
																					<c:otherwise>
																						<span tabindex="0" aria-label="<fmt:formatNumber value="${rating * .10}"/> out of 5 stars ${lblForThe} ${productVO.name}" class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0"/>">
																							<a href="${pageContext.request.contextPath}${finalUrl}?skuId=${catalogRefId}&showRatings=true" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productVO.name} ">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></a>
					                                                               		</span>	
																					</c:otherwise>
																				</c:choose>
																				</c:when>
																		<c:otherwise>
																			<c:choose>
																				<c:when
																					test="${ltlFlag && not empty ltlShipMethod}">
																					<span tabindex="0" aria-label="<fmt:formatNumber value="${rating * .10}"/> out of 5 stars ${lblForThe} ${productVO.name}"
																						class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																					<a
																							href="${pageContext.request.contextPath}${finalUrl}?skuId=${catalogRefId}&sopt=${ltlShipMethod}&showRatings=true" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productVO.name}">(${totalReviewCount}) <bbbl:label
																						key="lbl_sfl_reviews"
																						language="<c:out param='${language}'/>" />
																				</a>
																			</span>
																		</c:when>
																		<c:otherwise>
																			<span tabindex="0" aria-label="<fmt:formatNumber value="${rating * .10}"/> out of 5 stars ${lblForThe} ${productVO.name} "
																				class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																				<a
																					href="${pageContext.request.contextPath}${finalUrl}?skuId=${catalogRefId}&showRatings=true" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productVO.name}">(${totalReviewCount}) <bbbl:label
																						key="lbl_sfl_reviews"
																						language="<c:out param='${language}'/>" />
																				</a>
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
	                                                                            <dsp:param name="productId" value="${productId}" />
	                                                                            <dsp:param name="skuId" value="${catalogRefId}" />
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
																		                                                           	
												                        <span class="freeShipBadge">
																		<c:if test="${prdShipMsgFlag}">
																			${prdDisplayShipMsg}
																		</c:if>
																		</span>                                                 	
                                                                     </span>
                                                                          
                                                                        <div class="clear"></div>
                                                                    </div>
<c:set var="omnitemPrice"><dsp:valueof value="${itemPriceD}" converter="unformattedCurrency"/></c:set>
                                                                    <div class="wishlistQuantityDetails clearfix">
                                                                        <div class="prodDeliveryInfo padLeft_10 padTop_10">
                                                                            <c:if test="${not flagOff}">
                                                                                <div class="quantityBox">
                                                                                    <label id="lblquantity_text_${catalogRefId}" for="quantity_text_${catalogRefId}" class="hidden">Quantity</label>
                                                                                     
                                                                                    <div class="spinner">  <a href="#" class="scrollDown down" aria-label=" <bbbl:label key='lbl_decrease_quantity' language='${pageContext.request.locale.language}'/> of ${productVO.name} &nbsp;.Please select update once you have finalized your quantity">
                                                                                       <span class="icon-fallback-text">
																							<span class="icon-minus" aria-hidden="true"></span>
																							</span></a>
																					<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
                                                                                    
                                                                                    <input name="${wishListItemId}" size="2" type="text" value="${quantity}"  id="quantity_text_${catalogRefId}" maxlength="2" aria-label="<bbbl:label key='lbl_quantity_input_box'
                                                                                      language='${pageContext.request.locale.language}'/> of ${productVO.name}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="moveToCartData itemQuantity fl" aria-required="false"  />

                                                                                    <a href="#" class="scrollUp up" aria-label=" <bbbl:label key='lbl_increase_quantity'
                                                                                      language='${pageContext.request.locale.language}'/> of ${productVO.name} &nbsp;.Please select update once you have finalized your quantity">
                                                                                        <span class="icon-fallback-text">
																							<span class="icon-plus" aria-hidden="true">
																							</span>
																						</span>
                                                                                    </a> <div class="clear"></div>
                                                                                  </div>
                                                                                </div>
                                                                            </c:if>
                                                                            <dsp:setvalue param="itemId" paramvalue="element.wishListItemId" />
                                                                            <dsp:setvalue param="skuId" paramvalue="element.skuId" />
                                                                            <input type="hidden" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" name="/atg/commerce/gifts/GiftlistFormHandler.removeItemsFromGiftlistSuccessURL"/>
                                                                            <input type="hidden" value="/store/wishlist/wish_list.jsp" name="/atg/commerce/gifts/GiftlistFormHandler.removeItemsFromGiftlistErrorURL"/>
                                                                            <dsp:input bean="GiftlistFormHandler.removeGiftitemIds" paramvalue="itemId" type="hidden" />
                                                                              <dsp:input bean="GiftlistFormHandler.fromPage" type="hidden" value="itemJustMovedToSaveAjax" />
		                                                                     <%-- Client DOM XSRF
		                                                                    <dsp:input bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL" type="hidden" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" />
		                                                                    <dsp:input bean="GiftlistFormHandler.updateGiftlistItemsErrorURL" type="hidden" value="/store/wishlist/wish_list.jsp" />
		                                                                    --%>
                                                                            <dsp:input bean="GiftlistFormHandler.giftlistId" paramvalue="giftlistId" type="hidden" />
                                                                            <dsp:input bean="GiftlistFormHandler.currentItemId" paramvalue="itemId" type="hidden" />
                                                                            <dsp:getvalueof param="itemId" id="itemId" />
                                                                            <dsp:getvalueof param="skuId" id="skuId" />
                                                                            <%-- <div class="lnkUpdate"><a href="#" class="triggerSubmit" data-submit-button="btnUpdateSFL0" title="Update Quantity"><strong>Update</strong></a></div>
                                                                            <div class="lnkRemove"><a href="#" class="triggerSubmit" data-submit-button="btnRemoveSFL0" title="Remove Product from Save For Later"><strong>Remove</strong></a></div> --%>
                                                                             <c:if test="${not flagOff}">
                                                                                <div class="lnkUpdate">
                                                                                    <a href="#" class="triggerSubmit" role="link" aria-label="Update quantity ${lblForThe} ${productVO.name}" data-submit-button="btnUpdateSFL0${itemCount}" title="Update Quantity" onclick="updateWishList('${productId}','${skuId }');"><strong>Update</strong></a>
                                                                                    <dsp:input bean="GiftlistFormHandler.updateGiftlistItems" id="btnUpdateSFL0${itemCount}" type="submit" value="Update" iclass="hidden " priority="-9999" />
                                                                                </div>
                                                                            </c:if>
                                                                            
																			<input type="hidden" name="removeGiftitemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
																			<input type="hidden" name="giftlistId" class="frmAjaxSubmitData" value="${giftlistId}" />
                                                                            <div class="lnkRemove">
                                                                                <a href="#"  role="link" aria-label="Remove quantity ${productVO.name} from saved items" data-ajax-frmID="frmSaveItemRemove" class="btnAjaxSubmitSFL" data-submit-button="btnRemoveSFL0${itemCount}" title="Remove Product from Save For Later" onclick="removeWishList('${productId}','${skuId }');"><strong>Remove</strong></a>
                                                                            </div>
                                                                        </div>
                                                                        <div class="clear"></div>
                                                                        <%-- <input type="submit" name="btnUpdate" id="btnUpdateSFL0" class="hidden" value="Update" />--%>
                                                                        <%-- <input type="submit" name="btnRemove" id="btnRemoveSFL0" class="hidden" value="Remove" /> --%>
                                                                        <%-- <dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1" />
                                                                        <dsp:input bean="CartModifierFormHandler.addItemCount" type="hidden" value="1" />
                                                                        <dsp:input bean="CartModifierFormHandler.items[0].productId" value="${productId}" type="hidden" />
                                                                        <dsp:input bean="CartModifierFormHandler.items[0].catalogRefId" value="${catalogRefId}" type="hidden" />
                                                                        <dsp:input bean="CartModifierFormHandler.items[0].quantity" type="hidden" paramvalue="element.qtyRequested" />
                                                                        <dsp:input bean="CartModifierFormHandler.fromWishlist" type="hidden" value="true" />
                                                                        <dsp:input bean="CartModifierFormHandler.wishListId" type="hidden" paramvalue="giftlistId" />
                                                                        <dsp:input bean="CartModifierFormHandler.wishlistItemId" type="hidden" paramvalue="element.wishListItemId" />
                                                                        <dsp:input bean="CartModifierFormHandler.items[0].value.registryId" type="hidden" value="${registryId}" />
                                                                        <dsp:input id="fromWishList" bean="CartModifierFormHandler.fromWishList" type="hidden" value="true" />
																		<dsp:input bean="CartModifierFormHandler.countNo" value="${iCount}" type="hidden" />
                                                                        <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp?showMiniCartFlyout=true&count=${itemQuantity}&commItemCount=1" />
                                                                        <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden" value="/store/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp?showMoveToCartError=true" /> --%>
                                                                    </div>

                                                                    <div class="wishlistTotalDetails clearfix">
                                                                        
                                                                            <div class="prodInfo">
                                                                            <span class="fl prodPrice padLeft_20">
																				<bbbl:label key="lbl_save_total" language ="${pageContext.request.locale.language}"/> 
																			  </span>
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
                                                                                 <c:if test="${isPersonalizationIncomplete && (personalizationType eq 'PY' || personalizationType eq 'CR') && totalPrice > 0.01}">
                                                                                   <p class= "floatRight personalizationProgressMsg personalizationProgress">
                                                                                   <bbbl:label key="lbl_sfl_personalization_price" language="<c:out param='${language}'/>"/></p>
                                                                                 </c:if>
                                                                            </div>
                                                                            <c:if test="${ltlFlag}">
                                                                         <div class="cartShipMethodInfo padLeft_10 padTop_10 padBottom_20">
																		 
																			    <dsp:getvalueof var="ltlShipMethodDesc" param="element.ltlShipMethodDesc"/>
																				<dsp:getvalueof var="deliverySurcharge" param="element.deliverySurcharge"/>
																				<dsp:getvalueof var="assemblyFee" param="element.assemblyFees"/>
																				<c:choose>
																	              <c:when test="${ltlShipMethod == null or shipMethodUnsupported}">
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
																				<input type="hidden" name="registryId" class="frmAjaxSubmitData" value="${registryId}" />
																				<input type="hidden" name="shipMethodUnsupported"  class="frmAjaxSubmitData" value="${shipMethodUnsupported}" />
																				<input type="hidden" name="refNum"  class="frmAjaxSubmitData" value="${referenceNumber}" />
																				<c:if test="${ltlShipMethodToSend eq 'LWA'}">
																					<dsp:getvalueof var="ltlShipMethodToSend" value="LW"/>
																					<input type="hidden" name="whiteGloveAssembly"  class="frmAjaxSubmitData" value="true" />
																				</c:if>
																				<c:if test="${ltlShipMethodToSend eq 'LW'}">
																					<input type="hidden" name="whiteGloveAssembly" class="frmAjaxSubmitData" value="false" />
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
																				<c:if test="${not empty referenceNumber}">
																					<c:if test="${not empty personalizationOptions}">
																						<c:set var="cusDet">${eximCustomizationCodesMap[personalizationOptions]}</c:set>
																						<input type="hidden" value="${cusDet}" id="customizationDetails" />
																					</c:if>
																				</c:if>
                                                                           		<c:set var="mveToCart" value="${true}"/>
                                                                                <div>
                                                                                    <div class="button fr marBottom_10 marTop_5 padRight_10<c:if test="${(isInternationalCustomer && isIntlRestricted) or (isPersonalizationIncomplete) or (isInternationalCustomer && isPersonalized) or (not empty referenceNumber && !enableKatoriFlag)}"> button_disabled</c:if>">
                                                                                        <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                                                                            <dsp:param name="value" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
                                                                                           <dsp:oparam name="false">
																								<input type="hidden" name="isCartEmpty" class="frmAjaxSubmitData" value="false" />
		                                                                                    	<c:choose>
																									<c:when test="${not empty referenceNumber && enableKatoriFlag && personalizationOptions!=null && not empty personalizationOptions}">
																										<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" aria-label="Move ${productVO.name} To Cart" class="btnAjaxSubmitSFL moveToCart" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) or isPersonalizationIncomplete or (isInternationalCustomer && isPersonalized)}">disabled="disabled"</c:if> />
																									</c:when>
																									<c:otherwise>
																										<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" aria-label="Move ${productVO.name} To Cart" class="btnAjaxSubmitSFL moveToCart" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) or isPersonalizationIncomplete or (isInternationalCustomer && isPersonalized) or (not empty referenceNumber && !enableKatoriFlag)}">disabled="disabled"</c:if> />
																									</c:otherwise>
																								</c:choose>
		                                                                                    </dsp:oparam>
		                                                                                    <dsp:oparam name="true">
																								<input type="hidden" name="isCartEmpty" class="frmAjaxSubmitData" value="true" />
		                                                                                    	<c:choose>
																									<c:when test="${not empty referenceNumber && enableKatoriFlag && personalizationOptions!=null && not empty personalizationOptions}">
																										<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" aria-label="Move ${productVO.name} To Cart" class="btnAjaxSubmitSFL moveToCart" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) or isPersonalizationIncomplete or (isInternationalCustomer && isPersonalized)}">disabled="disabled"</c:if> />
																									</c:when>
																									<c:otherwise>
																										<input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" aria-label="Move ${productVO.name} To Cart" class="btnAjaxSubmitSFL moveToCart" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) or isPersonalizationIncomplete or (isInternationalCustomer && isPersonalized) or (not empty referenceNumber && !enableKatoriFlag)}">disabled="disabled"</c:if> />
																									</c:otherwise>
																								</c:choose>	
		                                                                                    </dsp:oparam>
                                                                                        </dsp:droplet>
                                                                                        <%-- <input class="moveToCart" name="btnMoveToCart" type="submit" value="Move to Cart"> --%>
                                                                                    </div>
                                                                                    <c:if test="${isInternationalCustomer && isIntlRestricted or (isInternationalCustomer && isPersonalized)}">
                                                                                    	<div class="notAvailableIntShipMsg padBottom_10 fr wishIntlShipMsg cb clearfix padRight_10"><bbbl:label key='lbl_cart_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
                                                                                    </c:if>
                                                                                   
                                                                                    
                                                                                    <div class="clear"></div>
                                                                                </div>
																			</c:if>
																			<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
																			<c:if test="${bopusoff and mapQuestOn and (not mveToCart)}">
		                                                                        <div  class="wishlistTotalDetails clearfix">
		                                                                            <div class="button fr marBottom_10 padRight_10<c:if test='${isInternationalCustomer eq true || (not empty referenceNumber && referenceNumber != null)}'> button_disabled</c:if>">
		                                                                            	<c:choose>
																							<c:when test="${empty prodImage}">
																								<img src="${imagePath}/_assets/global/images/no_image_available.jpg" class="productImage hidden" width="146" height="146" alt="${prodName}" title="${prodName}" />
																							</c:when>
																							<c:otherwise>
																								<img ${prodImageAttrib}="${scene7Path}/${prodImage}" width="146" height="146" alt="${prodName}" title="${prodName}" class="fl productImage noImageFound hidden" />
																							</c:otherwise>
																						</c:choose>
																						 <a class="prodName productName hidden" title="${prodName}" href="${contextPath}/${finalUrl}">${prodName}</a>
																						<input type="hidden" value="${productId}" data-dest-class="productId" class="productId" data-change-store-submit="productId" name="productId">
																						<input type="hidden" value="${catalogRefId}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
																						<input type="hidden" name="count" value="${iCount}" data-dest-class="count" data-change-store-submit="count" />
																						<c:if test="${not empty registryId}">
																							<input type="hidden" value="${registryId}" data-dest-class="registryId" class="registryId" data-change-store-submit="registryId" name="registryId">
																						</c:if>
																						<input type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity">
																						<input type="hidden" name="pageURL" value="${contextPath}/wishlist/_ajax/itemJustMovedToSave_ajax_handler.jsp" data-change-store-submit="pageURL"/>
																						<input type="hidden" name="check" value="check" data-dest-class="check" data-change-store-submit="check" />
																						<input type="hidden" name="wishlistItemId" value="${id}" data-dest-class="wishlistItemId" data-change-store-submit="wishlistItemId" />
		                                                                                <input type="button" class="changeStore" value="Find in Store" role="button" aria-pressed="false"  <c:if test='${isInternationalCustomer eq true || not empty referenceNumber && referenceNumber != null}'>disabled</c:if>>
																						
		                                                                            </div>
		                                                                            <div class="clear"></div>
		                                                                        </div>
	                                                                       </c:if>
                                                                                <%-- Move to Registry STARTS --%>
																				 <c:if test="${not flagOff}">
                                                                                <div>
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
																					<input type="hidden" name="refNum"  class="frmAjaxSubmitData" value="${referenceNumber}" />
                                          											<input type="hidden" name="personalizationType"  class="frmAjaxSubmitData" value="${personalizationType}" />
                                          											<input type="hidden" name="customizableCodes"  class="frmAjaxSubmitData" value="${customizableCodes}" />
																					
                                                                                    <dsp:getvalueof var="appid" bean="Site.id" />
                                                                                    <dsp:getvalueof var="transient" bean="Profile.transient" />
                                                                                    <dsp:getvalueof var="userId" bean="Profile.id"/>
                                                                                    <c:choose>
                                                                                        <c:when test="${transient == 'false'}">
                                                                                        <c:choose>
                                                                                        <c:when test="${ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false')}">
                                                                                         </c:when>
                                                                                         <c:otherwise>
                                                                                        <dsp:droplet name="AddItemToGiftRegistryDroplet">
	                                                                                        <dsp:param name="siteId" value="${appid}"/>
	                                                                                    </dsp:droplet>
	                                                                                        <c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_move_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
	                                                                                        <dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
																							<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
																							<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
	                                                                                            <c:choose>
																							
																								<c:when test="${sizeValue>1 && (isInternationalCustomer or isPersonalizationIncomplete) or (not empty referenceNumber && !enableKatoriFlag)}">
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
																								    <input type="hidden" name="shipMethodUnsupported"  class="frmAjaxSubmitData" value="${shipMethodUnsupported}" />
																	                				<input type="hidden" value="" name="alternateNum" class="frmAjaxSubmitData"/>
																								<div class="select button_select">
																										<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
																										<dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
																										<dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
                                                                                                        <dsp:tagAttribute name="aria-required" value="false"/>
																												<dsp:option> <bbbl:label key="lbl_move_to_registry"	language="${pageContext.request.locale.language}" /></dsp:option>
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
																												<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" role="button" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
																											</c:when>
																											<c:otherwise>
																												<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" role="button" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
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
														<input type="hidden" name="shipMethodUnsupported"  class="frmAjaxSubmitData" value="${shipMethodUnsupported}" />
														<input type="hidden" value="" name="alternateNum" class="frmAjaxSubmitData"/>
                                                                                                            <div class="select button_select">
                                                                                                                <dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="" />
                                                                                                                <dsp:select bean="GiftRegistryFormHandler.registryId" name="reqRegistryId" iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
                                                                                                                    <dsp:tagAttribute name="data-submit-button" value="btnMoveToRegSel${wishListItemId}"/>
                                                                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
										                                    <dsp:tagAttribute name="aria-label" value="move ${productVO.name} to registry"/>
                                                                                                                    <dsp:option><bbbl:label key="lbl_move_to_registry" language="${pageContext.request.locale.language}" /></dsp:option>
                                                                                                                    <dsp:droplet name="ForEach">
                                                                                                                    	<dsp:param name="array" value="${registrySkinnyVOList}" />
                                                                                                                        <dsp:oparam name="output">
                                                                                                                        	<dsp:param name="futureRegList" param="element" />
                                                                                                                            <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                                                                            <dsp:getvalueof var="event_type" param="element.eventType" />
                                                                                                                            <dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
                                                                                                                            <option value="${regId}"  class="${event_type}" data-poboxflag="${poBoxAddress}" data-notify-reg="true" >
                                                                                                                            	<dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
                                                                                                                           	</option>
                                                                                                                    	</dsp:oparam>
                                                                                                                 	</dsp:droplet>
                                                                                                                </dsp:select>
                                                                                                            </div>
                                                                                                            <div class="clear"></div>
                                                                                                        </div>
																									
                                                                                                        <div class="clear"></div>
                                                                                                        <c:choose>													
																											<c:when test="${not empty referenceNumber && personalizationOptions!=null && not empty personalizationOptions}">
																											
																												<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" role="button" aria-labelledby="btnMoveToRegSel${wishListItemId}" />
																											</c:when>
																											<c:otherwise>
																											
																												<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" role="button" aria-describedby="btnMoveToRegSel${wishListItemId}" />
																											</c:otherwise>
																										</c:choose>	                                                                                                        
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
                                                                                                                <dsp:getvalueof var="alternateNumber" param="futureRegList.alternatePhone" />
                                                                                                                <dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
                                                                                                                <input  type="hidden" value="${regId}" name="registryId" class="addItemToRegis" />
                                                                                                                <input  type="hidden" value="${registryName}" name="registryName" class="addItemToRegis omniRegName" />
                                                                                                                <input type="hidden" id="${regId}" value="${alternateNumber}" name="altNum" class="addItemToRegis"/>
                                                                                                                <input type="hidden" value="" name="alternateNum" class="frmAjaxSubmitData"/>
                                                                                                                <input type="hidden" name="shipMethodUnsupported"  class="frmAjaxSubmitData" value="${shipMethodUnsupported}" />
														                                                       <dsp:setvalue bean="GiftRegistryFormHandler.registryName" value="${registryName}" />
                                                                                                         	</dsp:oparam>
                                                                                                   	</dsp:droplet>
                                                                                                    <div class="fr addToRegistry padRight_10">
                                                                                                      <div class="button <c:if test="${isInternationalCustomer || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}">button_disabled</c:if>">
	                                                                                                        <input type="hidden" name="reqRegistryId"  <c:if test='${ltlFlag || isInternationalCustomer || isPersonalizationIncomplete}'>disabled="disabled"</c:if> class="frmAjaxSubmitData omniRegId" value="${regId}" />
	    	                                                                                                <%-- <input class="moveToReg" name="btnAddToRegistry" type="button" value="Move to Registry"/> --%>
																											<c:choose>													
																											<c:when test="${not empty referenceNumber && personalizationOptions!=null && not empty personalizationOptions}">
																											<input class="btnAjaxSubmitSFL moveToReg" name="123reqRegistryId" aria-label="move ${productVO.name} to registry" type="button"  data-poboxflag="${poBoxAddress}" data-notify-reg="true" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" <c:if test="${isInternationalCustomer || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}">disabled ="disabled"</c:if> role="button" />
		                                                                                               </c:when>
																											<c:otherwise>
																									   <input class="btnAjaxSubmitSFL moveToReg" name="123reqRegistryId" aria-label="move ${productVO.name} to registry" type="button" data-poboxflag="${poBoxAddress}" data-notify-reg="true" value="Move to Registry" data-ajax-frmID="frmRegSaveForLater" <c:if test="${isInternationalCustomer || isPersonalizationIncomplete || (not empty referenceNumber && !enableKatoriFlag)}">disabled ="disabled"</c:if> role="button" />
                                                                                                      </c:otherwise>
																										</c:choose>	
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
                                                                                       </c:otherwise>
                                                                                       </c:choose>
                                                                                        </c:when>
                                                                                    </c:choose>
                                                                                    
                                                                                </div>
																				</c:if>
                                                                                <%-- Move to Registry ENDS --%>                                                                        
                                                                    </div>
                                                    </div>                                                                                                    
                                            </li>
                                        </dsp:oparam>
                                        <dsp:oparam name="outputEnd">
                                            </ul>

                                            <div class="clear"></div>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                    </dsp:form>
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>

                        </div>
                        <%-- End:ForEach Droplet --%>
                    </dsp:oparam>
                    <dsp:oparam name="true">
                    <div id="saveForLaterBody" class="grid_10 clearfix">
                          <div id="saveForLaterEmptyMessaging" class="clearfix grid_10 alpha omega">
                            <p class="noMarTop">
                                <bbbt:textArea key="txtarea_saveitem_empty" language ="${pageContext.request.locale.language}"/>
                            </p>
                          </div>
                    </div>
                    </dsp:oparam>
                </dsp:droplet>
                <div class="clear"></div>

            </dsp:oparam>
            </dsp:droplet>
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
              		
        </jsp:body>
        <jsp:attribute name="footerContent">
		 <script>
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
