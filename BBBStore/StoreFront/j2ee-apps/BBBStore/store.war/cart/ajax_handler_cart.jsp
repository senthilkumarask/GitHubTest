<dsp:page>
    <%@ page import="com.bbb.constants.BBBCheckoutConstants"%>
    <dsp:importbean bean="/com/bbb/commerce/cart/TopStatusChangeMessageDroplet" />
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:droplet name="RepriceOrderDroplet">
		<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
	</dsp:droplet>
		<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
    <dsp:importbean bean="/atg/commerce/order/droplet/BBBContinueShoppingDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
    <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
   <dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
    <dsp:importbean bean="/atg/commerce/promotion/ClosenessQualifierDroplet" />    
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
    <dsp:importbean bean="/atg/multisite/Site"/>    
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/atg/commerce/order/droplet/ValidateClosenessQualifier" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
    <dsp:importbean bean="/com/bbb/internationalshipping/formhandler/InternationalShipFormHandler" />
    	  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
    	  <dsp:getvalueof var="reqFrom" param="reqFrom" />
	<c:if test="${empty reqFrom}">
		<c:set var="reqFrom" value="merge"/>
	</c:if>
    	  <dsp:getvalueof var="securityStatus" bean="Profile.securityStatus" />
	<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<c:set var="countryCode">${valueMap.defaultUserCountryCode}</c:set>
	<c:set var="currencyCode">${valueMap.defaultUserCurrencyCode}</c:set>
	<dsp:getvalueof var="sopt" bean="/OriginatingRequest.sopt"/>	
    <c:set var="porchCallbackString" value="" />
    <c:set var="porchConfigsString" value="" />
    <c:set var="porchConfigsStringWithPorchLoad" value="" />
    <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
    <dsp:getvalueof var="cartState" value="${0}"/>
    <c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
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
	<c:set var="disableCheckoutBtn" value="false" />
	<dsp:include page="/includes/pageIncludeTags.jsp"/>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
    <c:set var="lbl_cartdetail_movetowishList" scope="page">
        <bbbl:label key="lbl_cartdetail_movetowishList" language="${pageContext.request.locale.language}" />
    </c:set>
    <c:set var="CertonaContext" value="" scope="request"/>
    <c:set var="RegistryContext" value="" scope="request"/>
    <c:set var="registryFlag" value="false"/>
    <c:set var="skuFlag" value="false"/>
    <c:set var="BedBathUSSite">
        <bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="BuyBuyBabySite">
        <bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="BedBathCanadaSite">
        <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <dsp:droplet name="BBBPaymentGroupDroplet">
	     <dsp:param bean="ShoppingCart.current" name="order"/>
	     <dsp:param name="serviceType" value="GetPaymentGroupStatusOnLoad"/>
	     <dsp:oparam name="output">
	       <dsp:getvalueof var="isOrderAmtCoveredVar" vartype="java.lang.String" param="isOrderAmtCovered" scope="request"/>
	     </dsp:oparam>
    </dsp:droplet>
	<%-- # pay pal button dependding upon its ltl flag or item in order   --%>
	  <dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet">
			 <dsp:param name="order" bean="ShoppingCart.current"/>
			 <dsp:oparam name="output">
				<dsp:getvalueof var="orderHasLTLItem" param="orderHasLTLItem" />
			 </dsp:oparam>
		</dsp:droplet>
		<dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasErrorPersonalizedItemDroplet">
			 <dsp:param name="order" bean="ShoppingCart.current"/>
			 <dsp:oparam name="output">
				<dsp:getvalueof var="orderHasErrorPrsnlizedItem" param="orderHasErrorPrsnlizedItem" />
			 </dsp:oparam>
		</dsp:droplet>
		<dsp:droplet name="BBBPackNHoldDroplet">
			<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
				<dsp:oparam name="output">
					<dsp:getvalueof param="hasSingleCollegeItem" var="hasSingleCollegeItem"/>
					<dsp:getvalueof param="hasAllPackNHoldItems" var="hasAllPackNHoldItems"/>
				</dsp:oparam>
		</dsp:droplet>
		<c:if test="${isInternationalCustomer}">
		<dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasRestrictedItemDroplet">
			 <dsp:param name="order" bean="ShoppingCart.current"/>			 
			 <dsp:oparam name="output">
				<dsp:getvalueof var="orderHasIntlResterictedItem" param="orderHasIntlResterictedItem" />
				<dsp:getvalueof var="intlRestrictedItems" param="intlRestrictedItems" />
				<input type="hidden" id="itemsToRemove" value="intlRestrictedItems"/>
			 </dsp:oparam>
		</dsp:droplet>
		</c:if>	
		 <c:if test="${isInternationalCustomer}">
		<dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasPersonalizedItemDroplet">
			 <dsp:param name="order" bean="ShoppingCart.current"/>
			 <dsp:oparam name="output">
				<dsp:getvalueof var="orderHasPersonalizedItem" param="orderHasPersonalizedItem" />
			 </dsp:oparam>
		</dsp:droplet>
		</c:if>
		<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
			 <dsp:oparam name="output">
				<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
			 </dsp:oparam>
		</dsp:droplet>
    <dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_us"/></c:set>
			</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_ca"/></c:set>
		</c:otherwise>
	</c:choose>
    <dsp:droplet name="Switch">
        <dsp:param name="value" bean="Profile.transient"/>
        <dsp:oparam name="false">
            <dsp:getvalueof var="userId" bean="Profile.id"/>
        </dsp:oparam>
        <dsp:oparam name="true">
            <dsp:getvalueof var="userId" value=""/>
        </dsp:oparam>
    </dsp:droplet>
    <c:set var="topCheckoutButtonFlag" value="${false}"/>
    <c:set var="check" value="${false}"/>
	 <c:set var="cart_one_column_layout_enable" scope="request"><bbbc:config key="cart_one_column_layout_enable" configName="FlagDrivenFunctions" /></c:set>
    <div id="response">
        <div id="cartBody">
            <%-- BBBSL-4343 DoubleClick Floodlight START 
            <c:if test="${DoubleClickOn}">
                <c:if test="${currentSiteId eq BedBathUSSite}">
                    <c:set var="cat"><bbbc:config key="cart_page_cat" configName="ICROSSINGTag_Keys" /></c:set>
                    <c:if test="${not empty cat}">
                        <c:set var="type"><bbbc:config key="type_2" configName="ICROSSINGTag_Keys" /></c:set>
                        <c:set var="src"><bbbc:config key="src" configName="ICROSSINGTag_Keys" /></c:set>
                        <dsp:include page="/_includes/double_click_tag.jsp">
                            <dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat}"/>
                        </dsp:include>
                    </c:if>
                </c:if>
            </c:if>
            DoubleClick Floodlight END --%>

            <script type="text/javascript">
                var resx = new Object();
                var productIdsCertona = '';
                var eventTypeCertona = '';
            </script>
			<dsp:getvalueof id="countNo" bean="GiftlistFormHandler.countNo"/>
			<dsp:getvalueof id="itemMoveFromCartID" bean="GiftlistFormHandler.itemMoveFromCartID"/>
			<dsp:getvalueof id="moveOperation" bean="GiftlistFormHandler.moveOperation"/>
			<dsp:getvalueof id="quantityWish" bean="GiftlistFormHandler.quantity" />
			<dsp:getvalueof id="storeId" bean="GiftlistFormHandler.storeId" />
			<dsp:getvalueof id="movedCommerceItemId" bean="CartModifierFormHandler.commerceItemId"/>
			<dsp:getvalueof id="fromWishlist" bean="CartModifierFormHandler.fromWishlist"/>
			<dsp:getvalueof id="quantityCart" bean="CartModifierFormHandler.quantity" />
			<dsp:getvalueof id="btsValue" bean="GiftlistFormHandler.bts" />
			
			<dsp:getvalueof id="buyOffAssociationSkuId" bean="CartModifierFormHandler.buyOffAssociationSkuId"/>
			<dsp:getvalueof var="hasRegistryPorchServiceRemoved" bean="SessionBean.registryPorchServiceRemoved" />
			<dsp:getvalueof id="hasPorchServiceRemoved" bean="SessionBean.registryPorchServiceRemoved" />
			<dsp:setvalue bean="SessionBean.registryPorchServiceRemoved" value="false"/>
			
			

			<dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
			</dsp:droplet>
			<c:if test="${not empty itemMoveFromCartID}">
				<c:set var="movedCommerceItemId" value="${null}"/>
			</c:if>
			<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
			<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
			<c:set var="isStockAvailability" value="yes" scope="request"/>

            <dsp:getvalueof var="transientUser" bean="Profile.transient"/>
            <dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList"/>
				<c:if test="${empty errorMap}">
		            <dsp:include page="/global/gadgets/errorMessage.jsp">
		                <dsp:param name="formhandler" bean="CartModifierFormHandler"/>
		            </dsp:include>
		        </c:if>


			<%-- Undo Error --%>
			<dsp:getvalueof id="undoCheck" bean="CartModifierFormHandler.undoCheck"/>
			<c:if test="${undoCheck}">
	        	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
				<dsp:droplet name="ErrorMessageForEach">
					<dsp:param bean="CartModifierFormHandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="outputStart">
						<span class="hidden undoFailure"></span>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
			
		    <div id="cartTopMessaging" class="clearfix grid_10 alpha omega">
				<dsp:droplet name="TopStatusChangeMessageDroplet">
								<dsp:param name="reqType" value="${reqFrom}" />
								<dsp:oparam name="outputPrice">
									<dsp:getvalueof id="priceMessageVO" param="priceMessageVO" />
									<dsp:getvalueof var="oosItems" param="oosItems" />
									<dsp:getvalueof var="outOfStockItem"  param="oosVOList"/>
									<dsp:getvalueof var="priceMessageVoList" param="priceMessageVoList"/>
									<dsp:getvalueof var="outOfStockItem"  param="oosVOList"/>
									<%-- <c:set var="oosItems" value=${oosItems}/> --%>
									<input type="hidden" id="itemsToRemove" value="oosItems"/>	
									<c:if test="${(not empty priceMessageVO) and ((priceMessageVO.flagOff) or (not priceMessageVO.inStock))&& (not empty oosItems) }">
									
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
									<dsp:oparam name="outputIntlShip">
										<dsp:getvalueof id="intShipSku" param="intShipSku" />
										<c:if test="${intShipSku}">
											<div id="intlShippingMessage" class="marTop_10 marBottom_10">
												<div class="messageWrapper">
													<p class="noMar marLeft_25"><bbbt:textArea key="txt_intlShip_restrict" language="${pageContext.request.locale.language}" /></p>  
												</div>
											</div>
										</c:if>
									</dsp:oparam>
									<dsp:oparam name="outputEnvoy">
											<div id="intlShippingMessage" class="marTop_10 marBottom_10">
												<div class="messageWrapper">
													<p class="noMar marLeft_25"><bbbt:textArea key="txt_intlShip_envoy" language="${pageContext.request.locale.language}" /></p>  
												</div>
											</div>
									</dsp:oparam>
									</dsp:droplet>
				<dsp:include page="topLinkCart.jsp"/>
				<dsp:param name="priceMessageVoList" value="${priceMessageVoList}"/>
				<dsp:param name="outOfStockItem" value="${outOfStockItem}"/>
								
				<div class="clear"></div>
			</div>
            <div id="cartEmptyMessaging" class="clearfix grid_10 alpha omega hidden">
				<c:choose>
					<c:when test="${cart_one_column_layout_enable}">
						<c:set var="bannerWidth" value ="975" />
					</c:when>
					<c:otherwise>
						<c:set var="bannerWidth" value ="810" />
					</c:otherwise>
				</c:choose>
            </div>

            <div id="cartContentWrapper" class="clearfix">
                <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                    <dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
                    <dsp:oparam name="false">
					
                        <div id="cartTopMessaging" class="clearfix grid_10 alpha omega">
                            <dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
                            <dsp:setvalue paramvalue="wishlist.giftlistItems" param="items" />
							
										
								<c:if test="${(isInternationalCustomer && orderHasIntlResterictedItem) || orderHasPersonalizedItem}">
									<div id="saveForLaterTopMessaging" class="clearfix grid_10 alpha omega">
							          <div class="genericRedBox savedItemChangedWrapper clearfix closeWinWrapper marBottom_10 changeStoreItemWrap">
								           <h2 class="heading">
											<bbbl:label key="lbl_cart_top_intl_restricted_message_cart" language="${pageContext.request.locale.language}" />
											<p><bbbl:label key="lbl_cart_top_intl_restricted_please_remove" language="${pageContext.request.locale.language}" />
											<a href="#" class='removeItems btnAjaxSubmitCart' data-ajax-frmID="frmCartRemoveRestrict"><bbbl:label key="lbl_cart_top_intl_restricted_remove_item" language="${pageContext.request.locale.language}" /></a>
											</p>
										</h2>
									   </div>
								</c:if>
								
							<c:if test="${orderHasErrorPrsnlizedItem}">
								<div id="saveForLaterTopMessaging" class="clearfix grid_10 alpha omega">
								  <div class="genericRedBox savedItemChangedWrapper clearfix closeWinWrapper marBottom_10 changeStoreItemWrap">
									  
									 <h2 class="heading"><bbbl:label key="lbl_cart_top_personalized_err_message" language="${pageContext.request.locale.language}" /></h2>
								   </div>
								</div>
							</c:if>
                            <div class="clear"></div>
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
                                        <img class="bannerIMG" alt="${promoName}" src="${promoImage}" width="${bannerWidth}" />
                                    </c:if>
                                </dsp:oparam>
                            </dsp:droplet>
                            </c:if>
                        </div>
						<c:set var="cartAnalyzerFlag"><bbbc:config key="CartAnalyzerFlag" configName='CartAndCheckoutKeys' /></c:set>
						<c:if test="${!sessionScope.Recommanded_Item_Selected && cartAnalyzerFlag eq 'true'}">
						<dsp:droplet name="/com/bbb/commerce/cart/droplet/CartAnalyzerDroplet">
						  <dsp:param name="order" bean="ShoppingCart.current" />
						  <dsp:oparam name="output">
							  <dsp:getvalueof var="recommSkuVO" param="recommSkuVO"/>
							  <c:if test="${isInternationalCustomer && recommSkuVO.recommSKUVO.intlRestricted}">
									 <dsp:getvalueof var="recommSkuVO" value=""/>
							  </c:if>
						  </dsp:oparam>
						</dsp:droplet>
						</c:if>
                        <div id="cartContent" class="clearfix grid_10 alpha omega productListWrapper">
                            <div class="clear"></div>
                            <dsp:include page="/cart/cartForms.jsp"/>


							<dsp:getvalueof var="userActiveRegList" bean="SessionBean.values.userActiveRegistriesList" vartype="java.util.Map"/>
							
                            <dsp:form formid="frmCartRow" iclass="frmAjaxSubmit clearfix frmCartRow" method="post" action="ajax_handler_cart.jsp">
                            <c:set var="isOutOfStock" value="${false}"/>
                            <c:set var="displayDeliverySurMayApply" value="${false}"/>
                            <c:set var="shipmethodAvlForAllLtlItem" value="${true}"/>
                                <ul class="productsListContent clearfix">
                                <dsp:droplet name="/atg/commerce/order/droplet/BBBCartDisplayDroplet">
                                    <dsp:param name="order" bean="ShoppingCart.current" />
									<dsp:param name="fromCart" value="true" />
									<dsp:param name="isInternationalShipping" value="${isInternationalCustomer}" />
                                    <dsp:oparam name="output">
                                    <dsp:getvalueof var="favStoreStockStatus" param="favStoreStockStatus"/>
									<dsp:getvalueof var="storeDetails" param="storeDetails"/>
                                        <dsp:droplet name="ForEach">
                                            <dsp:param name="array" param="commerceItemList" />                                            
                                            <dsp:param name="elementName" value="commerceItem" />
											<dsp:getvalueof var="arraySize" param="size" />
											<dsp:getvalueof var="currentCount" param="count" />
											<c:set var="lastRow" value="" />
											<c:if test="${arraySize eq currentCount}">
												<c:set var="lastRow" value="lastRow" />
											</c:if>
													
										
                                            <dsp:oparam name="output">
											 <c:set var="itemFlagoff" value="${false}"/>
											 <c:set var="disableLink" value="${false}"/>
											 <dsp:getvalueof var="commItem" param="commerceItem"/>
											<c:if test="${commItem.stockAvailability ne 0 && commItem.stockAvailability ne 2}">
													<c:set var="isOutOfStock" value="${true}"/>
											</c:if>
											<dsp:getvalueof id="priceMessageVO" value="${commItem.priceMessageVO}" />
											<c:if test="${not empty priceMessageVO}">
												<c:set var="itemFlagoff" value="${priceMessageVO.flagOff}"/>
												<c:set var="disableLink" value="${priceMessageVO.disableLink}"/>
											</c:if>
											<dsp:getvalueof var="webOffered" value="${commItem.skuDetailVO.webOfferedFlag}"/>
													<c:if test="{itemFlagoff || not webOffered }">
													</c:if>
											<dsp:getvalueof id="isIntlRestricted" value="${commItem.skuDetailVO.intlRestricted}"/>
											<dsp:getvalueof id="iCount" param="count"/>
												<dsp:getvalueof id="size" param="size"/>

												<c:if test="${(size eq iCount) and (size lt countNo)}">
													<c:set var="check" value="${true}"/>
												</c:if>
												<c:if test="${iCount eq countNo}">
													<dsp:include page="cartUndoLink2.jsp">
														<dsp:param name="itemMoveFromCartID" value="${itemMoveFromCartID}"/>
														<dsp:param name="moveOperation" value="${moveOperation}"/>
														<dsp:param name="quantity" value="${quantityWish}"/>
														<dsp:param name="storeId" value="${storeId}"/>
														<dsp:param name="countNo" value="${countNo}"/>
														<dsp:param name="hasPorchServiceRemoved" value="${hasPorchServiceRemoved}"/>
													</dsp:include>
												</c:if>
                                                <dsp:getvalueof var="commItem" param="commerceItem"/>
                                                <dsp:getvalueof var="count" param="count"/>
                                                <dsp:param name="bbbItem" param="commerceItem.BBBCommerceItem.shippingGroupRelationships"/>
                                                <dsp:getvalueof id="newQuantity" param="commerceItem.BBBCommerceItem.quantity"/>
                                                <dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id"/>
                                                <dsp:getvalueof id="productIdsCertona" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
												<dsp:getvalueof var="shipMethodUnsupported" param="commerceItem.BBBCommerceItem.shipMethodUnsupported"/>
												<dsp:getvalueof var="commLtlShipMethod" param="commerceItem.BBBCommerceItem.ltlShipMethod"/>
												<dsp:getvalueof var="ltlShipMethodDesc" param="commerceItem.ltlShipMethodDesc"/>
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
									
												<li>
												
												
												<c:if test="${ not ship_method_avl && commItem.skuDetailVO.ltlItem && empty commLtlShipMethod}" >
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
												
                                                <li id="cartItemID_${commItem.BBBCommerceItem.id}" class="${lastRow} ${itemJustMovedToCart} ${itemMovedBackToCart} <c:if test="${not empty commItem.BBBCommerceItem.registryId}">registeryItem</c:if> clearfix cartRow changeStoreItemWrap <c:if test="${count eq 1}">firstItem</c:if>">
													<c:set var="itemMovedBackToCart" value=""/>
													<c:set var="itemJustMovedToCart" value=""/>
													<%--BBBSL-5887 | passing  registry ID to filter out of stock messages --%>
													<dsp:include page="itemLinkCart.jsp">
														<dsp:param name="id" value="${commerceItemId}"/>
														<dsp:param name="oldShippingId" value="${oldShippingId}"/>
														<dsp:param name="newQuantity" value="${newQuantity}"/>
														<dsp:param name="image" param="commerceItem.BBBCommerceItem.auxiliaryData.catalogRef.mediumImage"/>
														<dsp:param name="displayName" value="${commItem.skuDetailVO.displayName}"/>
														<dsp:param name="priceMessageVO" value="${commItem.priceMessageVO}"/>
														<dsp:param name="isIntlRestricted" value="${commItem.skuDetailVO.intlRestricted}"/>
														<dsp:param name="orderHasPersonalizedItem" value="${commItem.BBBCommerceItem.referenceNumber}"/>
														<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}"/>
													</dsp:include>
													<%-- LTL Alert  --%>
                                                       
														<%-- LTL Alert  --%>
														
														 <c:if test="${shipMethodUnsupported && commItem.skuDetailVO.ltlItem && not empty commLtlShipMethod}" >
														  <c:if test="${commLtlShipMethod == 'LW' && commItem.BBBCommerceItem.whiteGloveAssembly}">
														 <c:set var="ltlShipMethodDesc"><bbbl:label key="lbl_white_glove_assembly" language="<c:out param='${language}'/>"/></c:set>
															</c:if>
															<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request"/>
														<c:set target="${placeHolderMapServiceLevel}" property="ShippingMethodDesc" value="${ltlShipMethodDesc}"/>
														<div class="clear"></div>
														<div class="ltlUnavailable marBottom_10 marTop_10" >
														<p class="padLeft_25">
															<bbbt:textArea key="txt_ship_method_not_supported" placeHolderMap="${placeHolderMapServiceLevel}" language ="${pageContext.request.locale.language}"/>
														</p>
														</div>
														</c:if>
														<%-- LTL Alert  --%>
														<div class="registeryItemHeader clearfix">
														<c:set var="isMyRegistry" value="${true}"/>
                                                        <c:if test="${not empty commItem.BBBCommerceItem.registryId}">                                                         
														<c:set var="registryUrl" value="../giftregistry/view_registry_guest.jsp"/>
														<c:set var="isMyRegistry" value="${false}"/>
														<c:if test='${fn:contains(userActiveRegList, commItem.BBBCommerceItem.registryId)}'>
															<c:set var="registryUrl" value="../giftregistry/view_registry_owner.jsp"/>															
														</c:if>
                                                            <c:set var="registryFlag" value="true"/>
                                                            <c:set var="RegistryContext" scope="request">${RegistryContext}${commItem.BBBCommerceItem.registryId};</c:set>
                                                            <script type="text/javascript">
                                                                eventTypeCertona = eventTypeCertona + 'shopping+cart+' + '${registryType}' + ';';
                                                                productIdsCertona = productIdsCertona + '${productIdsCertona}' + ';';
                                                            </script>
                                                            
                                                                <dsp:getvalueof bean="ShoppingCart.current.registryMap.${commItem.BBBCommerceItem.registryId}" var="registratantVO"/>
                                                                <c:if test="${commItem.BBBCommerceItem.registryInfo ne null}">
                                                                    <span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>
                                                                    <dsp:getvalueof var="registryInfo" param="commerceItem.BBBCommerceItem.registryInfo"/>
																	<dsp:a href="${registryUrl}">
																	<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}"/>
																	<dsp:param name="eventType" value="${registratantVO.registryType.registryTypeDesc}" />
                                                                    <strong>${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong>
																	</dsp:a>
                                                                       <span>${registratantVO.registryType.registryTypeDesc}</span>&nbsp;<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
                                                                </c:if>
                                                            
                                                            <div class="clear"></div>
                                                        </c:if>
														</div>

                                                        <c:if test="${empty commItem.BBBCommerceItem.registryId}">
                                                            <script type="text/javascript">
                                                                productIdsCertona = productIdsCertona + '${productIdsCertona}' + ';';
                                                            </script>
                                                        </c:if>

                                                        <div class="prodItemWrapper clearfix">                                                            
                                                                <div class="cartItemDetails clearfix itemCol">
                                                                    <dsp:getvalueof var="image" param="commerceItem.skuDetailVO.skuImages.mediumImage"/>
																	<dsp:getvalueof var="skuSize"  value="${commItem.skuDetailVO.size}" />
																	<dsp:getvalueof var="skuUpc" value="${commItem.skuDetailVO.upc}" />
																	<dsp:getvalueof var="skuColor" value="${commItem.skuDetailVO.color}" />
																	<dsp:getvalueof var="pOpt" value="${commItem.BBBCommerceItem.personalizationOptions}"/>
                                                                            <c:set var="CertonaContext" scope="request">${CertonaContext}<dsp:valueof param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>;</c:set>
                                                                            <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                                                <dsp:param name="id" param="commerceItem.BBBCommerceItem.repositoryItem.productId" />
                                                                                <dsp:param name="itemDescriptorName" value="product" />
                                                                                <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                                                                                <dsp:oparam name="output">
                                                                                    <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
                                                                                        <dsp:param name="priceObject" value="${commItem.BBBCommerceItem}" />
                                                                                        <dsp:param name="profile" bean="Profile"/>
                                                                                        <dsp:oparam name="output">
                                                                                            <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
                                                                                        </dsp:oparam>
                                                                                    </dsp:droplet>
																					<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
																						<c:if test="${itemFlagoff or disableLink}">
																							<c:set var="finalUrl" value="#"/>
																						</c:if>
                                                                                        <c:choose>
																							<c:when test="${itemFlagoff or disableLink}">
                                                                                                <span class="padLeft_10 block fl">
																								<c:choose>
																									<c:when test="${empty image}">
																										<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																									</c:when>
																									<c:when test="${(not empty commItem.BBBCommerceItem.referenceNumber && commItem.BBBCommerceItem.referenceNumber != null) || (commItem.BBBCommerceItem.eximErrorExists) || !enableKatoriFlag}">
																										<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																									</c:when>
																									<c:otherwise>
																										<img class="fl productImage noImageFound" src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																									</c:otherwise>
																								</c:choose>
																								</span>
                                                                                                <div class="fl padLeft_10">
                                                                                                <span class="prodInfo block cb padTop_5">
																									<span class="prodName productName disableText"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></span>
																								</span>
																								<span class="prodAttribsCart">
																									<c:if test='${not empty skuColor}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" /></c:if>
																									<c:if test='${not empty skuSize}'><c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" /></c:if>
																							        <c:if test='${enableKatoriFlag && not empty commItem.BBBCommerceItem.personalizationOptions && commItem.BBBCommerceItem.personalizationOptions != null}'>
																										<br>${eximCustomizationCodesMap[pOpt]} :  <c:out value="${commItem.BBBCommerceItem.personalizationDetails}" escapeXml="false" />
																									</c:if>
																								</span>
																								<c:if test="${commItem.skuDetailVO.giftWrapEligible  && not isInternationalCustomer}">
																				                     <span class="prodAttribsCart"><bbbl:label key="lbl_cartdetail_elegibleforgiftpackaging" language="<c:out param='${language}'/>"/>&#32;<bbbt:textArea key="txt_giftpackaging_popup_link" language="<c:out param='${language}'/>"/></span>																			                  
																				                </c:if>
																								 <c:if test='${not empty commItem.BBBCommerceItem.referenceNumber && commItem.BBBCommerceItem.referenceNumber != null && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag}'>
																								<div class="personalizationAttr katoriPrice">
																								<%--BBBSL-8154  --%>
		                                                                                            <%-- <span  aria-hidden="true">${commItem.BBBCommerceItem.personalizationOptionDisplay}</span> --%>
		                                                                                            
																									<div class="priceAddText">
																									<c:choose>
			                                                                                            <c:when test='${not empty commItem.BBBCommerceItem.personalizePrice && not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "PY"}'>
																											<dsp:valueof value="${commItem.BBBCommerceItem.personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
																										</c:when>
																										 <c:when test='${not empty commItem.BBBCommerceItem.personalizePrice && not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "CR"}'>
																											<dsp:valueof value="${commItem.BBBCommerceItem.personalizePrice}" converter="currency"/> 
																											<c:choose>
																												<c:when test="${not empty commItem.skuDetailVO.customizableCodes && fn:contains(customizeCTACodes, commItem.skuDetailVO.customizableCodes)}">
																													<bbbl:label key="lbl_exim_cr_added_price_customize" language ="${pageContext.request.locale.language}"/>
																												</c:when>
																												<c:otherwise>
																													<bbbl:label key="lbl_exim_cr_added_price" language ="${pageContext.request.locale.language}"/>
																												</c:otherwise>
																											</c:choose>
																										</c:when>
																										<c:when test='${not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "PB"}'>
																												<bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
																										</c:when>
																									</c:choose>
																									</div>
	                                                                                           	</div>
	                                                                                            <div class="personalizeLinks" data-personalization="${eximCustomizationCodesMap[pOpt]}">
	                                                                                            	<dsp:getvalueof var="prodId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
	                                                                                                <a href="#" class="editPersonalization bold <c:if test="${isInternationalCustomer ||  not empty commItem.BBBCommerceItem.registryId}">disableText</c:if>" title="<bbbl:label key="lbl_cart_personalization_edit" language="<c:out param='${language}'/>"/>" aria-label="<bbbl:label key="lbl_cart_edit_personalization_for" language="<c:out param='${language}'/>"/> ${commItem.skuDetailVO.displayName}" data-custom-vendor="${commItem.vendorInfoVO.vendorName}" data-product="${prodId}" data-sku="${commItem.BBBCommerceItem.catalogRefId}" data-refnum="${commItem.BBBCommerceItem.referenceNumber}" data-quantity="${commItem.BBBCommerceItem.quantity}">
																									<bbbl:label key="lbl_cart_personalization_edit" language="<c:out param='${language}'/>"/></a>
	                                                                                                <span class="seperator"></span>																									
		                                                                                            <a href="javascript:void(0)" class="removePersonalization bold" aria-label="<bbbl:label key="lbl_cart_remove_personalization_for" language="<c:out param='${language}'/>"/> ${commItem.skuDetailVO.displayName}" onclick="omniRemove('${prodId}','${commItem.BBBCommerceItem.catalogRefId}');"><bbbl:label key="lbl_cartdetail_remove" language="<c:out param='${language}'/>"/> ${eximCustomizationCodesMap[pOpt]}</a>
	                                                                                            </div>		
																							</c:if>
                                                                                                </div>
																								<%--156 Cart analyzer savita--%>
																							</c:when>																							
																							<c:otherwise>
																							<%-- Start :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
																								
																								<c:choose>
																								     <c:when test="${commItem.skuDetailVO.ltlItem && not empty shippingMethod && (commItem.BBBCommerceItem.whiteGloveAssembly)}">
																									      <c:set var="url">${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}&sopt=LWA</c:set>
																									</c:when>
																									 <c:when test="${commItem.skuDetailVO.ltlItem && not empty shippingMethod && (!commItem.BBBCommerceItem.whiteGloveAssembly)}">
																									      <c:set var="url">${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}&sopt=${shippingMethod}</c:set>
																									</c:when>
																									<c:otherwise>
																										 <c:set var="url">${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}</c:set>			
																									</c:otherwise>
																								</c:choose>	
																						    <%-- end :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
																								
																								<dsp:a iclass="prodImg padLeft_10 block fl" page="${url}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																									<c:choose>	
																										<c:when test="${not empty commItem.BBBCommerceItem.referenceNumber && commItem.BBBCommerceItem.referenceNumber != null}">
																											<c:choose> 
																												<c:when test="${not empty commItem.BBBCommerceItem.fullImagePath && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag}">
																													  <img class="fl productImage" src="${commItem.BBBCommerceItem.fullImagePath}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																												 </c:when>
																												 <c:otherwise>
																													 <img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																												 </c:otherwise>
																											</c:choose>
																										</c:when>
																										<c:otherwise>
																											<c:choose>
																												<c:when test="${empty image}">
																													<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																												</c:when>
																												<c:otherwise>
																													<img class="fl productImage noImageFound" src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																												</c:otherwise>
																											</c:choose>
																										</c:otherwise>
																									</c:choose>
																								</dsp:a>
																								<div class="fl padLeft_10">	
																								<%-- Start :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
																																																																						
																								<c:choose>
																									 <c:when test="${commItem.skuDetailVO.ltlItem && not empty shippingMethod && commItem.BBBCommerceItem.whiteGloveAssembly}">
		                                                                                                <dsp:a iclass="prodInfo block cb padTop_5" page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}&sopt=LWA" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																										<span class="prodName productName"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></span>
		                                                                                                </dsp:a>
																									 </c:when>
																									 <c:when test="${commItem.skuDetailVO.ltlItem && not empty shippingMethod && (!commItem.BBBCommerceItem.whiteGloveAssembly)}">
		                                                                                                <dsp:a iclass="prodInfo block cb padTop_5" page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}&sopt=${shippingMethod}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																										<span class="prodName productName"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></span>
		                                                                                                </dsp:a>
																									 </c:when>
																								<c:otherwise>
																										<dsp:a iclass="prodInfo block cb padTop_5" page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																										<span class="prodName productName"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></span>
		                                                                                                </dsp:a>
																								</c:otherwise>
																								</c:choose>
																								<%-- end :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
																									
																								<span class="prodAttribsCart">
																									<c:if test='${not empty skuColor}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" /></c:if>
																									<c:if test='${not empty skuSize}'><c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" /></c:if>
																							        <c:if test='${enableKatoriFlag && not empty commItem.BBBCommerceItem.personalizationOptions && commItem.BBBCommerceItem.personalizationOptions != null}'>
																										<br>${eximCustomizationCodesMap[pOpt]} :  <c:out value="${commItem.BBBCommerceItem.personalizationDetails}" escapeXml="false" />
																									</c:if>
																								</span>
																								<c:if test="${commItem.skuDetailVO.giftWrapEligible  && not isInternationalCustomer}">
																				                     <span class="prodAttribsCart"><bbbl:label key="lbl_cartdetail_elegibleforgiftpackaging" language="<c:out param='${language}'/>"/>&#32;<bbbt:textArea key="txt_giftpackaging_popup_link" language="<c:out param='${language}'/>"/></span>																			                  
																				                </c:if>
																							<c:if test='${not empty commItem.BBBCommerceItem.referenceNumber && commItem.BBBCommerceItem.referenceNumber != null  && enableKatoriFlag}'>
																								<div class="personalizationAttr katoriPrice">
		                                                                                            <c:if test='${not empty commItem.BBBCommerceItem.personalizationOptions && commItem.BBBCommerceItem.personalizationOptions != null}'>
																										<%--BBBSL-8154  --%>
																										<%-- <span  aria-hidden="true">${commItem.BBBCommerceItem.personalizationOptionsDisplay}</span> --%>
																									</c:if>
																									<c:choose>
																										<c:when test='${not empty commItem.BBBCommerceItem.personalizePrice && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag && not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "PY"}'>
																										<div class="priceAddText">
																											<dsp:valueof value="${commItem.BBBCommerceItem.personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
																										</div>
																										</c:when>
																										<c:when test='${not empty commItem.BBBCommerceItem.personalizePrice && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag && not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "CR"}'>
																										<div class="priceAddText">
																											<dsp:valueof value="${commItem.BBBCommerceItem.personalizePrice}" converter="currency"/> 
																											<c:choose>
																												<c:when test="${not empty commItem.skuDetailVO.customizableCodes && fn:contains(customizeCTACodes, commItem.skuDetailVO.customizableCodes)}">
																													<bbbl:label key="lbl_exim_cr_added_price_customize" language ="${pageContext.request.locale.language}"/>
																												</c:when>
																												<c:otherwise>
																													<bbbl:label key="lbl_exim_cr_added_price" language ="${pageContext.request.locale.language}"/>
																												</c:otherwise>
																											</c:choose>
																										</div>
																										</c:when>
																										<c:when test='${not empty commItem.skuDetailVO.personalizationType && commItem.skuDetailVO.personalizationType == "PB"}'>
																											<div class="priceAddText">	
																												<bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
																										    </div>
																										</c:when>
																									</c:choose>
	                                                                                           	</div>
																								<c:if test='${!commItem.BBBCommerceItem.eximErrorExists && isMyRegistry}'>
																									<div class="personalizeLinks" data-personalization="${eximCustomizationCodesMap[pOpt]}">
																										<dsp:getvalueof var="prodId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
																										<a href="#" class="editPersonalization bold <c:if test="${isInternationalCustomer ||  not empty commItem.BBBCommerceItem.registryId}">disableText</c:if>" title="<bbbl:label key="lbl_cart_personalization_edit" language="<c:out param='${language}'/>"/>" aria-label="<bbbl:label key="lbl_cart_edit_personalization_for" language="<c:out param='${language}'/>"/> ${commItem.skuDetailVO.displayName}" data-custom-vendor="${commItem.vendorInfoVO.vendorName}" data-product="${prodId}" data-sku="${commItem.BBBCommerceItem.catalogRefId}" data-refnum="${commItem.BBBCommerceItem.referenceNumber}" data-quantity="${commItem.BBBCommerceItem.quantity}">
																										<bbbl:label key="lbl_cart_personalization_edit" language="<c:out param='${language}'/>"/></a>
																										<span class="seperator"></span>
																										<dsp:getvalueof var="prodId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
																										<a href="javascript:void(0)" class="removePersonalization bold" aria-label="<bbbl:label key="lbl_cart_remove_personalization_for" language="<c:out param='${language}'/>"/> ${commItem.skuDetailVO.displayName}" onclick="omniRemove('${prodId}','${commItem.BBBCommerceItem.catalogRefId}');"><bbbl:label key="lbl_cartdetail_remove" language="<c:out param='${language}'/>"/> ${eximCustomizationCodesMap[pOpt]}</a>
																									</div>
																								</c:if>
																							</c:if>
                                                                                                </div>
																								<%--156 Cart analyzer savita--%>
																							</c:otherwise>
																						</c:choose>
                                                                                </dsp:oparam>
                                                                            </dsp:droplet>
                                                                    <c:if test="${null ne commItem.skuDetailVO.displayName}">
                                                                        <c:set var="skuFlag" value="true"/>
                                                                        <c:set var="skuFlag" value="true"/>
                                                                        <script type="text/javascript">
                                                                            eventTypeCertona = eventTypeCertona + 'shopping+cart' + ';';
                                                                        </script>
                                                                    </c:if>

                                                                    <c:if test="${not isInternationalCustomer}">
			                                                            <dsp:droplet name="Switch">
																			<dsp:param name="value" value="${commItem.BBBCommerceItem.porchService}"/>
																			<dsp:oparam name="true">
																				<div class="porchWrapper">
																					<div class="porchServiceAdded">

																						<span class="txtOffScreen">Installation has been added to ${commItem.skuDetailVO.displayName}&nbsp.</span>

																						<%-- this should come from the commerce item data --%>
																						<span class="serviceType">${commItem.BBBCommerceItem.porchServiceType}</span>
														
																						<%-- this should come from the commerce item data --%>
																						
																						<c:choose>
																						<c:when test ="${commItem.BBBCommerceItem.priceEstimation ne null}">
																						<p class="serviceEstimate">
																							<bbbl:label key="lbl_bbby_porch_service_estimated_price" language="<c:out param='${language}'/>"/>
																							${commItem.BBBCommerceItem.priceEstimation}
																						</p>
																						<p class="serviceDisclaimer">
																							<bbbl:label key="lbl_bbby_porch_service_disclaimer_cart" language="<c:out param='${language}'/>"/>
																						</p>
																						</c:when>
																						<c:otherwise>
																						<p class="serviceEstimate">	
																							<bbbl:label key="lbl_porch_service_estimated_by_pro" language="<c:out param='${language}'/>"/>
																						</p>
																						</c:otherwise>
																						</c:choose>																						
																						
																					</div>   		
																					<div class="porchRemoveService">
																						<c:set var="lbl_bbby_porch_service_remove">
																			            	<bbbl:label key='lbl_bbby_porch_service_remove' language="${pageContext.request.locale.language}" />
																			            </c:set>

																						<%--yet to be Implemented for removing Porch Service Attachment!--%>
																					 	<dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
																						<a href="#" onclick="trackPorchRemoval(); return false;" class="btnAjaxSubmitCart removePorchService" aria-label="Remove installation services from ${fn:escapeXml(commItem.skuDetailVO.displayName)}" title="Remove installation services from ${fn:escapeXml(commItem.skuDetailVO.displayName)}" data-ajax-frmID="frmCartServiceRemove">${lbl_bbby_porch_service_remove}</a>
																					</div>
																				</div>
																			</dsp:oparam>
																			<dsp:oparam name="false">
																				<dsp:droplet name="com/bbb/config/BBBPorchConfigDroplet">
																					<dsp:param name="siteId" value="${currentSiteId}"/>
																					<dsp:param name="pageName" value="cartPage"/>
																					<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}"/>
																					<dsp:param name="commerceItem" value="${commItem.BBBCommerceItem}"/>
																					 <dsp:param name="profile" bean="Profile"/>
																				 	<dsp:oparam name="CartPorch">
																				 		<dsp:getvalueof var="porchServiceTypeCode" param="porchServiceTypeCodes[0]"/>
																						<dsp:droplet name="/atg/dynamo/droplet/IsNull">
																							<dsp:param name="value" value="${porchServiceTypeCode}"/>
																							<dsp:oparam name="false">							
																								<c:set var="gotPorchEligibleItem" value="${true}" />

																								<div class="porchWrapper" id="porch${commItem.BBBCommerceItem.catalogRefId}" style="visibility: hidden;">
																					
																									<div id="porch-widget-body${commItem.BBBCommerceItem.catalogRefId}" class="porch-widget" ></div>
																									<div class="clear"></div>								

																									<p class="porchDisclaimer" style="display: none;">
																										<bbbl:label key="lbl_bbby_porch_service_disclaimer_cart" language="<c:out param='${language}'/>"/>
																									</p>
																								</div>
																								<a href="#" class="btnAjaxSubmitCart addPorchSubmit" data-ajax-frmID="frmCartAddService">Add Service</a>
																						
																								<dsp:droplet name="/com/bbb/commerce/browse/droplet/AkamaiZipCodeDroplet">
																									<dsp:param name="profile" bean="Profile"/>
																									<dsp:oparam name="output">
																										<dsp:getvalueof var="preferredShippingZipCode" param="zipCode"/>
																									</dsp:oparam>
																								</dsp:droplet>

																								<c:set var="lbl_bbby_porch_service_learn_more">
																					            	<bbbl:label key='lbl_bbby_porch_service_learn_more' language="${pageContext.request.locale.language}" />
																					            </c:set>

																								<c:set var="porchConfigsString">
																									${porchConfigsString}
																									
																									var params${commItem.BBBCommerceItem.catalogRefId} = {
																										fontFamily: " Arial, Helvetica, sans-serif",
																										fontSize: "14px",
																										fontColor: "#444",
																										fontWeight: "normal",
																										querySelector: '#porch-widget-body${commItem.BBBCommerceItem.catalogRefId}',
																										serviceFamilyCode: "${porchServiceTypeCode}", <%--get from  data --%>
																										partnerSku: "${commItem.BBBCommerceItem.catalogRefId}",
																										productName: '${fn:escapeXml(commItem.skuDetailVO.displayName)}',
																										<%--//productUrl: "${pageURL}",  need current domain --%>
																										postalCode: '${preferredShippingZipCode}',
																										learnMoreText: '${lbl_bbby_porch_service_learn_more}',
																										bindPDPWidgetLoad: false,
																										configureOnLoad: true,
																										interface: 'cart',
																										site: '${currentSiteId}'
																									};																		
																								</c:set>

																								<c:set var="porchConfigsStringWithPorchLoad">
																									${porchConfigsStringWithPorchLoad}
																									
																									var params${commItem.BBBCommerceItem.catalogRefId} = {
																										fontFamily: " Arial, Helvetica, sans-serif",
																										fontSize: "14px",
																										fontColor: "#444",
																										fontWeight: "normal",
																										querySelector: '#porch-widget-body${commItem.BBBCommerceItem.catalogRefId}',
																										serviceFamilyCode: "${porchServiceTypeCode}", <%--get from  data --%>
																										partnerSku: "${commItem.BBBCommerceItem.catalogRefId}",
																										productName: '${fn:escapeXml(commItem.skuDetailVO.displayName)}',
																										<%--//productUrl: "${pageURL}",  need current domain --%>
																										postalCode: '${preferredShippingZipCode}',
																										learnMoreText: '${lbl_bbby_porch_service_learn_more}',																					
																										interface: 'cart',
																										site: '${currentSiteId}'
																									};																		
																								</c:set>


																								<c:set var="porchCallbackString">
																									${porchCallbackString}$('#porch${commItem.BBBCommerceItem.catalogRefId}').BBBPorch(params${commItem.BBBCommerceItem.catalogRefId}); 
																								</c:set>
																							</dsp:oparam>
																						</dsp:droplet>
																					</dsp:oparam>
																				</dsp:droplet> 	
																			</dsp:oparam>
																		</dsp:droplet>
																	</c:if>


                                                                </div>

                                                                <div class="cartQuantityDetails clearfix itemCol">
                                                                    <table class="prodDeliveryInfo">
                                                                    <tbody>
																<c:choose>
																	<c:when test="${not itemFlagoff && !(isInternationalCustomer && commItem.skuDetailVO.intlRestricted) && !commItem.BBBCommerceItem.eximErrorExists}">
																			<tr class="quantityBox">
																			<td>
																				<label class="hidden" id="lblquantity_text_${iCount}" for="quantity_text_${iCount}"><bbbl:label key="lbl_cartdetail_quantity" language="<c:out param='${language}'/>"/> ${commItem.BBBCommerceItem.quantity} of ${commItem.skuDetailVO.displayName}</label>
                                                                                <div class="spinner"> 
                                                                                <span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
																				<input  name="${commItem.BBBCommerceItem.id}" type="text" id="quantity_text_${count}"  value="${commItem.BBBCommerceItem.quantity}" title="${commItem.BBBCommerceItem.quantity}  of ${commItem.skuDetailVO.displayName}&nbsp" class="itemQuantity fl" name="qty" role="textbox" maxlength="2"  aria-required="true" aria-labelledby="lblquantity_text_${iCount}"/>
                                                                                 <div class="clear"></div></div>
																			</td>
																			</tr>
																			<tr class="lnkUpdate">
																			  <td>
																				<a href="javascript:void(0);" class="triggerUpdate" data-submit-button="btnUpdate${commItem.BBBCommerceItem.id}"><span class="txtOffScreen">The quantity has been Updated for ${commItem.skuDetailVO.displayName}&nbsp.</span><strong><bbbl:label key="lbl_cartdetail_update" language="<c:out param='${language}'/>"/></strong></a>
																				<dsp:input type="hidden" bean="CartModifierFormHandler.commerceItemId" value="${commItem.BBBCommerceItem.id}"/>
																				<dsp:input name="btnUpdate" id="btnUpdate${commItem.BBBCommerceItem.id}" type="submit" bean="CartModifierFormHandler.ajaxSetOrderByCommerceId" iclass="hidden" />
																				</td>
																			</tr>
																	</c:when>
																	<c:otherwise>
																				<input name="${commItem.BBBCommerceItem.id}" type="hidden" value="${commItem.BBBCommerceItem.quantity}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" id="quantity_text_${count}" class="itemQuantity" maxlength="2"/>
																		</c:otherwise>
																</c:choose>
                                                                        <tr class="lnkRemove">
                                                                        <td>
                                                                        <dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
                                                                              <a href="#" class="btnAjaxSubmitCart" data-ajax-frmID="frmCartItemRemove" onclick="omniRemove('${productId}','${commItem.BBBCommerceItem.catalogRefId}');"><span class="txtOffScreen">Remove ${commItem.skuDetailVO.displayName}&nbsp from cart &nbsp. </span><strong><bbbl:label key="lbl_cartdetail_remove" language="<c:out param='${language}'/>"/></strong></a>
                                                                              </td>
                                                                        </tr>
																	<c:if test="${not itemFlagoff}">
                                                                        <tr class="lnkSaveForLater">
                                                                        <td>
                                                                            <dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
																			<c:if test='${not empty commItem.BBBCommerceItem.personalizationOptions && commItem.BBBCommerceItem.personalizationOptions != null}'>
																							<c:set var="cusDet">			${eximCustomizationCodesMap[pOpt]} </c:set>
																									</c:if>
																									<c:choose>
																								
																									<c:when test='${not empty cusDet}'>
                                                                             <a href="#" class="btnAjaxSubmitCart" data-ajax-frmID="frmCartSaveForLater" onclick="omniAddToListPers(this, '${productId}', '${commItem.BBBCommerceItem.catalogRefId}', '${commItem.BBBCommerceItem.quantity}','${cusDet}');"><span class="txtOffScreen">Save ${commItem.skuDetailVO.displayName}&nbsp for a later purchase &nbsp. </span><strong>Save for Later</strong></a>
																			 </c:when>
																			 <c:otherwise>
																			 <a href="#" class="btnAjaxSubmitCart" data-ajax-frmID="frmCartSaveForLater" onclick="omniAddToList(this, '${productId}', '${commItem.BBBCommerceItem.catalogRefId}', '${commItem.BBBCommerceItem.quantity}');"><span class="txtOffScreen">Save ${commItem.skuDetailVO.displayName}&nbsp for a later purchase &nbsp. </span><strong>Save for Later</strong></a>
																			 </c:otherwise>
																			 </c:choose>
																			 </td>
                                                                        </tr>
																	</c:if>
																	</tbody>
                                                                    </table>
                                                                </div>

																<%-- Display shipping details --%>
                                                                <div class="cartDeliveryDetails clearfix itemCol" aria-hidden="false">
                                                                    <dsp:include page="/cart/cart_includes/shippingType.jsp">
																		<dsp:param name="commItem" value="${commItem}"/>
																		<dsp:param name="favStoreStockStatus" value="${favStoreStockStatus}"/>
																		<dsp:param name="storeDetails" value="${storeDetails}"/>
																		<c:if test="${commItem.BBBCommerceItem.id == buyOffAssociationSkuId}">
																			<dsp:param name="hasRegistryPorchServiceRemoved" value="${hasRegistryPorchServiceRemoved}"/>
																		</c:if>
																	</dsp:include>
                                                                </div>

																	<%-- Display item price details --%>
                                                                    <dsp:include page="/cart/cart_includes/priceDisplay.jsp">
																		<dsp:param name="commItem" value="${commItem}"/>
																		<dsp:param name="orderObject" bean="ShoppingCart.current" />
																	</dsp:include>
                                                                <div class="clear"></div>
                                                               
                                                                <input type="hidden" name="updateCartInfoSemiColonSeparated" class="frmAjaxSubmitData" value="${intlRestrictedItems}" />
                                                                <input type="hidden" name="updateCartInfoSemiColonSeparatedOOS" class="frmAjaxSubmitData" value="${oosItems}" />
                                                                <input type="hidden" name="saveStoreId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.storeId}" />
                                                                <input type="hidden" name="saveBts" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.bts}" />
                                                            <input type="hidden" name="saveCount" class="frmAjaxSubmitData" value="${iCount}" />
                                                                <input type="hidden" name="saveQuantity" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.quantity}" />
                                                                <input type="hidden" name="saveCurrentItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />

                                                            <input type="hidden" name="removeCommerceItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
                                                            <input type="hidden" name="removeSubmitButton" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
                                                            <input type="hidden" name="buyOffAssociationSkuId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
                                                            <input type="hidden" name="associateRegistryContextButton" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
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

                                                            <div class="clear"></div>
															
											   </div>
                                                <div class="clear"></div>
                                                    <div class="clear"></div>
                                                </li>
												<input type="hidden" name="newHasSingleCollegeItem" id="newHasSingleCollegeItem" value="${hasSingleCollegeItem}" />
												<input type="hidden" name="newHasAllCollegeItems" id="newHasAllCollegeItems" value="${hasAllPackNHoldItems}" />
												<c:if test="${check}">
													<dsp:include page="cartUndoLink2.jsp">
														<dsp:param name="itemMoveFromCartID" value="${itemMoveFromCartID}"/>
														<dsp:param name="moveOperation" value="${moveOperation}"/>
														<dsp:param name="quantity" value="${quantityWish}"/>
														<dsp:param name="storeId" value="${storeId}"/>
														<dsp:param name="countNo" value="${countNo}"/>
														<dsp:param name="hasPorchServiceRemoved" value="${hasPorchServiceRemoved}"/>
													</dsp:include>
												</c:if>
												<c:if test="${not empty recommSkuVO}">
													<dsp:include page="/cart/cart_includes/recommItem.jsp">
														<dsp:param name="recommSkuVO" value="${recommSkuVO}"/>
														<dsp:param name="commItem" value="${commItem}"/>
													</dsp:include>
												</c:if>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </dsp:oparam>
                                </dsp:droplet>

                            </ul>
                            <div class="clear"></div>
                            </dsp:form>
                            <%-- Display coupons and total mount details --%>
                            <div id="cartItemsTotalAndCouponsWrapper">
                               <dsp:getvalueof var="orderType" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
                                <c:if test="${not empty orderType && orderType ne null && orderType ne 'ONLINE_ONLY' }">
                                <div class="clearfix padBottom_20">
                                    <div class="alert alert-alert noMar"> 
                                        <h6><bbbl:label key="lbl_items_for_store_pickup_title" language="<c:out param='${language}'/>"/></h6>
                                        <p><bbbt:textArea key="txt_items_for_store_pickup_cart" language="<c:out param='${language}'/>"/></p>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                                </c:if>
                                 <dsp:include page="coupon_include.jsp">
									<dsp:param name="orderHasErrorPrsnlizedItem" value="${orderHasErrorPrsnlizedItem}"/>
								 </dsp:include>
								 <div id="rightCol" class="summaryNew grid_4 omega clearfix fr">
                                     <h6><bbbl:label key="lbl_order_summary" language="<c:out param='${language}'/>"/></h6>
									 <dl class="checkoutSummary clearfix <c:if test="${isInternationalCustomer}">noBorderBot noPadBot</c:if>">
										  <dt class="fl bold <c:if test="${isInternationalCustomer}">noPadBot internationalOrder</c:if>">
										 <c:choose>
											<c:when test="${isInternationalCustomer}">
												<bbbl:label key="lbl_estimated_orderSubtotal" language="<c:out param='${language}'/>"/> 
										</c:when>
										<c:otherwise>
											<bbbl:label key="lbl_order_subtotal" language="<c:out param='${language}'/>"/> 
										</c:otherwise>
									</c:choose>
									<span class="summaryCount">
											 <dsp:droplet name="/com/bbb/commerce/cart/BBBCartItemCountDroplet">
												 <dsp:param name="shoppingCart" bean="ShoppingCart.current" />
												 <dsp:oparam name="output">
													 <dsp:valueof param="commerceItemCount"/>
												 </dsp:oparam>
											 </dsp:droplet>
											 <bbbl:label key="lbl_cartdetail_items" language="<c:out param='${language}'/>"/>
											 </span>
										 </dt>
										 <c:choose>
											<c:when test="${orderHasErrorPrsnlizedItem}">
											 <dsp:include page="/cart/cart_includes/cartPriceDisplayTBD.jsp">
													<dsp:param name="displayDeliverySurMayApply" value="${displayDeliverySurMayApply}"/>
													<dsp:param name="shipmethodAvlForAllLtlItem" value="${shipmethodAvlForAllLtlItem}"/>
													<dsp:param name="orderHasLTLItem" value="${orderHasLTLItem}"/>
													<dsp:param name="orderHasErrorPrsnlizedItem" value="${orderHasErrorPrsnlizedItem}"/>
												</dsp:include>
												</c:when>
												<c:otherwise>

												 <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
													 <dsp:param name="profile" bean="Profile"/>
													 <dsp:param name="priceObject" bean="ShoppingCart.current" />
													 <dsp:oparam name="output">
														 <dsp:getvalueof var="storeAmount" param="priceInfoVO.storeAmount"/>
														 <dsp:getvalueof var="onlinePurchaseTotal" param="priceInfoVO.onlinePurchaseTotal"/>
														 <dsp:getvalueof var="storeEcoFeeTotal" param="priceInfoVO.storeEcoFeeTotal"/>
														 <dsp:getvalueof var="onlineEcoFeeTotal" param="priceInfoVO.onlineEcoFeeTotal"/>
														 <dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
														 <dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
														 <dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
														 <dsp:getvalueof var="orderPreTaxAmout" param="priceInfoVO.orderPreTaxAmout"/>
														 <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
														 <dsp:getvalueof var="freeShipping" param="priceInfoVO.freeShipping"/>
														 <dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
														 <dsp:getvalueof var="surchargeSavings" param="priceInfoVO.surchargeSavings"/>
														 <dsp:getvalueof var="finalShippingCharge" param="priceInfoVO.finalShippingCharge"/>
														 
														 <%-- LTL Changes  --%>
														 <dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
														<dsp:getvalueof var="maxDeliverySurchargeReached" param="priceInfoVO.maxDeliverySurchargeReached"/>
														<dsp:getvalueof var="totalAssemblyFee" param="priceInfoVO.totalAssemblyFee"/>
														<dsp:getvalueof var="maxDeliverySurcharge" param="priceInfoVO.maxDeliverySurcharge"/>
														 

														 <dd class="fl bold <c:if test="${isInternationalCustomer}">noPadBot internationalOrderPrice</c:if>"><dsp:valueof value="${storeAmount + onlinePurchaseTotal }" converter="currency"/></dd>
														 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
														 <c:if test="${not isInternationalCustomer}">
															 <c:choose>
																 <c:when test="${freeShipping ne true}">
																	 <c:choose>
																		<c:when  test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}"> 
																			  <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a>
																			  </dt>
																			  <dd class="fl bold">TBD</dd>
																		</c:when>
																		<c:otherwise>
																		 <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> 
																		 <c:choose>
																			 <c:when test="${finalShippingCharge eq 0.0}">
																			 <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a>
																		 </dt>
																				<dd class="fl bold"><bbbl:label key="lbl_shipping_free" language="<c:out param='${language}'/>"/></dd>
																			 </c:when>
																			 <c:otherwise>
																			 </dt>
																				<dd class="fl bold"><dsp:valueof value="${finalShippingCharge}" converter="currency" number="0.00"/></dd>
																			 </c:otherwise>
																		 </c:choose>
																		</c:otherwise>
																	</c:choose>
																	
																 </c:when>
																 <c:otherwise>
																	<%-- LTL changes --%>
																	 <c:choose>
																		 <c:when test="${orderHasLTLItem eq true && rawShippingTotal eq 0.0}" >
																			 <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
																			 <dd class="fl">  TBD </dd>
																		 </c:when>
																		 <c:otherwise>
																		 <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> 
																		 <c:choose>
																			 <c:when test="${finalShippingCharge eq 0.0}">
																			 <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
																				<dd class="fl bold"><bbbl:label key="lbl_shipping_free" language="<c:out param='${language}'/>"/></dd>
																			 </c:when>
																			 <c:otherwise>
																			 </dt>
																				<dd class="fl bold"><dsp:valueof value="${finalShippingCharge}" converter="currency" number="0.00"/></dd>
																			 </c:otherwise>
																		 </c:choose>
																		 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
																	 </c:otherwise>
																	</c:choose>
																 </c:otherwise>
															 </c:choose>
															 <c:if test="${totalSurcharge gt 0.0}">
																 <dt class="fl"><span class="bold"><bbbl:label key="lbl_parcel_surcharge" language="<c:out param='${language}'/>"/> </span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
																 <dd class="fl bold"><dsp:valueof value="${totalSurcharge}" converter="currency"/></dd>
																 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
															 </c:if>
															 <c:if test="${surchargeSavings gt 0.0}">
																 <dt class="fl bold"><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></dt>
																 <dd class="fl bold">(<dsp:valueof value="${surchargeSavings}" converter="currency"/>)</dd>
																 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
															 </c:if>
															 <dsp:getvalueof var="ecoFeeTotal" value="${storeEcoFeeTotal + onlineEcoFeeTotal }"/>
															 <c:if test="${ecoFeeTotal gt 0.0}">
																 <dt class="fl bold"><bbbl:label key="lbl_preview_ecofee" language="<c:out param='${language}'/>"/></dt>
																 <dd class="fl bold"><dsp:valueof value="${ecoFeeTotal}" converter="currency"/></dd>
																 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
															 </c:if>
															 <%-- Additional info for LTL items summary --%>
															<c:if  test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}"> 	
															<dt class="fl bold padTop_5"><bbbl:label key="ltl_delivery_surcharge_may_apply" language="<c:out param='${language}'/>"/></dt>
															<dd class="fl bold padTop_5">TBD</dd>
															</c:if>
															<c:if  test ="${totalDeliverySurcharge gt 0.0 && shipmethodAvlForAllLtlItem}"> 
																<dt class="fl padTop_5"><span class="bold"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
																<dd class="fl bold padTop_5"><dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/></dd>
															</c:if>
															<c:if  test ="${maxDeliverySurchargeReached}"> 
																<c:choose>
																	<c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
																		  <dt class="fl padTop_5"><span class="bold"><bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/>:</span> <br>
																		  <a href="${contextPath}/static/whatthismean" class="maxSurcharges popup"><bbbl:label key="lbl_what_this_mean" language="<c:out param='${language}'/>"/></a>
																		  </dt>
																		  <dd class="fl bold padTop_5">(TBD)</dd>
																	</c:when>
																	<c:otherwise>
																		 <dt class="fl padTop_5"><span class="bold">
																		<bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/>:</span> <br>
																		<a href="${contextPath}/static/whatthismean" class="maxSurcharges popup"><bbbl:label key="lbl_what_this_mean" language="<c:out param='${language}'/>"/></a>
																		</dt>
																		<dd class="fl bold highlightRed padTop_5">(-<dsp:valueof value="${totalDeliverySurcharge - maxDeliverySurcharge}" number="0.00" converter="currency"/>)</dd>
																	</c:otherwise>
															   </c:choose>
															</c:if>
															<c:if  test ="${totalAssemblyFee gt 0.0}"> 
																<dt class="fl bold padTop_5"><bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:</dt>
																<dd class="fl bold padTop_5"><dsp:valueof value="${totalAssemblyFee}" number="0.00" converter="currency"/></dd>
															</c:if>
															<%-- Additional info for LTL items summary --%>
															
															 <dsp:getvalueof var="preTaxAmout" value="${orderPreTaxAmout + storeAmount + storeEcoFeeTotal }"/>
															 <dt class="fr total">
															   
																<c:choose>
																	<c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
																		<strong><bbbl:label key="lbl_cartdetail_cart_total" language="<c:out param='${language}'/>"/>:</strong>&nbsp;<span>
																		<dsp:valueof value="${storeAmount + onlinePurchaseTotal + totalSurcharge + totalAssemblyFee}" converter="currency"/></span>
																	</c:when>
																	<c:otherwise>
																		<strong><bbbl:label key="lbl_cartdetail_pretaxtotal" language="<c:out param='${language}'/>"/>:</strong>&nbsp;<span>
																		<dsp:valueof value="${preTaxAmout}" converter="currency"/></span>
																	</c:otherwise>
																</c:choose>
															 </dt>
															 <%--<dd class="fl total"></dd>--%>
															 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
															 <c:if test="${totalSavedAmount gt 0.0}">
																   <c:choose>
																	<c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
																		  <dt class="fr highlight summarySaving bold">
																			 <bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>"/>:
																			 <span class="bold">TBD</span>
																		  </dt>
																		<%--<dd class="fl bold"></dd>--%>
																		 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
																	</c:when>
																	<c:otherwise>
																		 <dt class="fr highlight summarySaving  bold">
																			 <bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>"/>:
																			 <span class="bold"><dsp:valueof value="${totalSavedAmount}" converter="defaultCurrency"/></span>
																		  </dt>
																		 <%--<dd class="fl"></dd>--%>
																		 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
																	</c:otherwise>
																  </c:choose>
															 </c:if>
															 </c:if>
														 </dsp:oparam>
													 </dsp:droplet>
											 </c:otherwise>
										</c:choose>
											 <c:if test="${isInternationalCustomer}">
												<div class="pricingNote"><bbbl:label key="lbl_international_cart_disclaimer" language="<c:out param='${language}'/>"/></div>
											</c:if>
											 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
										 </dl>
										 <div class="clear"></div>
										 <div class="requiredInfo <c:if test="${isInternationalCustomer}">hidden</c:if>">
											 <p class="marTop_10 noMarBot smallText"><bbbl:label key="lbl_cartdetail_shippinginfo" language="<c:out param='${language}'/>"/></p>
											 <bbbl:label key="lbl_cartdetail_viewshippingcostslink" language="<c:out param='${language}'/>"/>
										 </div>
									 
									 <div class="clear"></div>
									 <div class="clearfix marTop_20 checkoutButtons">
										 <dsp:form formid="express_form" method="post" action="${contextPath}/cart/cart.jsp" >
										  <c:if test="${orderHasLTLItem ne true and (not isInternationalCustomer) and not orderHasErrorPrsnlizedItem}">
												 <dsp:droplet name="/com/bbb/commerce/cart/droplet/DisplayExpressCheckout">
													 <dsp:param name="order" bean="ShoppingCart.current"/>
													 <dsp:param name="profile" bean="Profile"/>
													 <dsp:oparam name="false">
															 <div class="clearfix">
																 <div class="checkbox fl">
																	 <dsp:input id="express_checkout" bean="CartModifierFormHandler.expressCheckout" type="checkbox" name="expire_checkout">
																		<dsp:tagAttribute name="aria-checked" value="false"/>
																		<dsp:tagAttribute name="role" value="checkbox"/>
																	 </dsp:input>
																 </div>
																 <div class="label fl">
																	 <label for="express_checkout"><strong><bbbl:label key="lbl_cartdetail_expresscheckout" language="<c:out param='${language}'/>"/></strong></label>
																 </div>
																 <div class="clear"></div>
															 </div>
															 <p class="cb noMarTop marBottom_10 smallText"><bbbl:label key="lbl_cartdetail_gotoorderreview" language="<c:out param='${language}'/>"/></p>
													  </dsp:oparam>
													  <dsp:oparam name="true">
															 <div class="clearfix">
																 <div class="checkbox fl">
																	 <dsp:input id="express_checkout" disabled="disabled" bean="CartModifierFormHandler.expressCheckout" type="checkbox" name="expire_checkout">
																		<dsp:tagAttribute name="aria-checked" value="false"/>
																		<dsp:tagAttribute name="role" value="checkbox"/>
																	 </dsp:input>
																 </div>
																 <div class="label fl">
																	 <label for="express_checkout"><strong><bbbl:label key="lbl_cartdetail_expresscheckout" language="<c:out param='${language}'/>"/></strong></label>
																 </div>
																 <div class="clear"></div>
															 </div>
															 <p class="cb noMarTop marBottom_10 smallText"><bbbl:label key="lbl_cartdetail_gotoorderreview" language="<c:out param='${language}'/>"/></p>
														 </dsp:oparam>
												 </dsp:droplet>
											</c:if>

											<dsp:droplet name="/com/bbb/commerce/cart/droplet/CheckFlagOffItemsDroplet">
												<dsp:param name="checkOOSItem" value="${true}"/>
													<dsp:oparam name="output">
													<dsp:getvalueof var="showCheckOutBtn" param="flagOffChecked"/>
													<dsp:getvalueof var="itemNotOOS" param="itemOutOfStock"/>
													</dsp:oparam>
												</dsp:droplet>



											  <div id="bottomCheckoutButton" class="clearfix">
                                                <c:set var="topCheckoutButtonFlag" value="${true}"/>
												<c:choose>
												 <c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
													<dsp:input bean="CartModifierFormHandler.fromCart" value="${true}" type="hidden"/>
													 <div class="button button_active button_active_orange btnCheckout <c:if test="${orderHasIntlResterictedItem || orderHasPersonalizedItem || orderHasErrorPrsnlizedItem }">button_disabled</c:if>">
													 	<c:choose>
														<c:when test="${not isInternationalCustomer}">
														 	<c:choose>
																<c:when test="${orderHasErrorPrsnlizedItem}">
																	<dsp:input id="botCheckoutBtn" bean="CartModifierFormHandler.checkout" type="submit" value="${lbl_checkout_checkout}">
																		<dsp:tagAttribute name="role" value="button"/>
																		<dsp:tagAttribute name="disabled" value="disabled"/>
																	</dsp:input>
																	<c:set var="disableCheckoutBtn" value="true" />
																</c:when>
																<c:otherwise>
																	<dsp:input id="botCheckoutBtn" bean="CartModifierFormHandler.checkout" type="submit" value="${lbl_checkout_checkout}">
																		<dsp:tagAttribute name="role" value="button"/>
																	</dsp:input>
																</c:otherwise>
															</c:choose>	
                                                         	</c:when>
														<c:otherwise>
															<%-- BBBH-391 | Client DOM XSRF
													 		<dsp:input bean="InternationalShipFormHandler.successURL" type="hidden" value="/store/checkout/envoy_checkout.jsp" />
													 		<dsp:input bean="InternationalShipFormHandler.errorURL" type="hidden" value="/store/cart/cart.jsp" /> --%>
													 		<dsp:input bean="InternationalShipFormHandler.fromPage" type="hidden" value="envoyCheckout" />
													 		<dsp:input bean="InternationalShipFormHandler.userSelectedCountry" type="hidden" value="${countryCode}" />
													 		<dsp:input bean="InternationalShipFormHandler.userSelectedCurrency" type="hidden" value="${currencyCode}" />
													 	
																<input name="internationalOrderType" type="hidden" value="internationalOrder"/>
															 
															 <dsp:getvalueof var="exceptionString" bean="InternationalShipFormHandler.exceptionString" />  
															 <dsp:getvalueof var="exceptionCode" bean="InternationalShipFormHandler.exceptionCode" />  
															  
															  <div id="error" class="hidden">
																  <ul>
																	<li id="tl_atg_err_code">${exceptionCode}
																	<li id="tl_atg_err_value">${exceptionString}
																  </ul>
															  </div>   
																
																<c:choose>
							                                  <c:when test="${orderHasIntlResterictedItem || orderHasPersonalizedItem || orderHasErrorPrsnlizedItem}">
																<dsp:input id="botCheckoutBtn" bean="InternationalShipFormHandler.envoyCheckout" type="submit" value="${lbl_checkout_checkout}" disabled="disabled">
																<%-- <dsp:tagAttribute name="aria-pressed" value="false"/> --%>
                                                            	<dsp:tagAttribute name="role" value="button"/>
																<dsp:tagAttribute name="disabled" value="disabled"/>
                                                        	 </dsp:input>
															 <c:set var="disableCheckoutBtn" value="true" />
															 </c:when>
															 <c:otherwise>
															<dsp:input id="botCheckoutBtn" bean="InternationalShipFormHandler.envoyCheckout" type="submit" value="${lbl_checkout_checkout}">
																<%-- <dsp:tagAttribute name="aria-pressed" value="false"/> --%>
                                                            	<dsp:tagAttribute name="role" value="button"/>
                                                        	 </dsp:input>
															 </c:otherwise>
														</c:choose>
														</c:otherwise>
														</c:choose>
													 </div>
												 </c:when>
												 <c:otherwise>
													 <div class="button button_active button_active_orange button_disabled btnCheckout">
														 <dsp:input id="botCheckoutBtn" bean="CartModifierFormHandler.checkout" type="submit" value="${lbl_checkout_checkout}">
														 <dsp:tagAttribute name="disabled" value="disabled"/>
                                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                            <dsp:tagAttribute name="role" value="button"/>
                                                         </dsp:input>
														 <c:set var="disableCheckoutBtn" value="true" />
													 </div>
												 </c:otherwise>
												</c:choose>
												 <dsp:droplet name="BBBContinueShoppingDroplet">
													 <dsp:oparam name="output">
														 <dsp:getvalueof var="linkURL" param="continue_shopping_url" />
														 <c:set var="continueshoppinglink"> <bbbl:label key="lbl_cartdetail_continueshoppinglink" language="<c:out param='${language}'/>" /></c:set>
                                                         <div class="clear"></div>
														 <dsp:a iclass="buttonTextLink marTop_10 noPadLeft" href="${linkURL}" title="${continueshoppinglink}">
															 <bbbl:label key="lbl_cartdetail_continueshoppinglink" language="<c:out param='${language}'/>"/>
														 </dsp:a>
													 </dsp:oparam>
												 </dsp:droplet>

												 <c:if test="${not (showCheckOutBtn and itemNotOOS and (not isOutOfStock))}">
													 <div class="clearfix padTop_10 padBottom_10 cb">
														 <span class="error"><bbbl:label key="lbl_cart_outofstockmessage" language="<c:out param='${language}'/>"/></span>
													 </div>
												 </c:if>

												 <div class="clear"></div>
											 </div>
                                            <div class="clear"></div>

                                            <%-- #Scope 83 A Start R2.2 pay pal button dependeding upon its flag in bcc   --%>
                                                 <dsp:getvalueof var="orderType" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />                                                
                                                <c:if test="${paypalCartButtonEnable and (not isInternationalCustomer)}">
                                                 	<c:choose>
												        <c:when test="${paypalOn}">
																<c:choose >
																
																	<c:when test="${orderHasLTLItem eq true || (registryFlag eq true) }">
																		<div class="paypalCheckoutContainer paypal_disabled fl"> 
																		<bbbt:textArea key="txt_paypal_disable_image_new" language ="${pageContext.request.locale.language}"/>
																		</div>
																		<div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_10 disableText">
																			<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																		</div>
																		<div class="clear"></div>
																		<div class="marTop_15 smallText bold highlightRed"><bbbl:label key="lbl_paypal_not_available_ltl_or_reg_product" language="${pageContext.request.locale.language}" /></div>
																	</c:when>
																	<c:when test="${(orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID')}">
																		<div class="paypalCheckoutContainer paypal_disabled fl"> 
																		<bbbt:textArea key="txt_paypal_disable_image_cart" language ="${pageContext.request.locale.language}"/>
																		</div>
																		<div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
																			<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																		</div>
																		<div class="clear"></div>
																		<div class="error marTop_15 noPaypalAvlMsg"><bbbl:label key="lbl_paypal_not_avilable_bopus_hybrid" language="${pageContext.request.locale.language}" /></div>
																	</c:when>
																	<c:when test="${orderHasErrorPrsnlizedItem}">
																		<div class="paypalCheckoutContainer paypal_disabled fl"> 
																		<bbbt:textArea key="txt_paypal_disable_image_new" language ="${pageContext.request.locale.language}"/>
																		</div>
																		<div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_10 disableText">
																			<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																		</div>
																	</c:when>
																	
																	<c:otherwise>
																	<c:choose>
																	 <c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock) }">
																		 <c:choose>
																			<c:when test="${isOrderAmtCoveredVar}">
																				<%-- BED-435 & BED 436 | Total amount less than Gift Card  should disable PayPal button on Ajax Call --%>
																				<!-- Remove Paypal Info if whole amount is covered from gift card  -->
																				<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
																				<c:if test="${payPalOrder}">
																					<dsp:droplet name="/com/bbb/commerce/order/paypal/RemovePayPalInfoDroplet">
																					</dsp:droplet>
																				</c:if>
																				<!-- Remove Paypal Info if whole amount is covered from gift card  -->
																				<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
																				<c:if test="${payPalOrder}">
																					<dsp:droplet name="/com/bbb/commerce/order/paypal/RemovePayPalInfoDroplet">
																					</dsp:droplet>
																				</c:if>
																				<div class="paypalCheckoutContainer fl button_disabled"> 
																				<img src="/_assets/global/images/paypal_disabled.png" class="paypalCheckoutButton" role="image" alt="paypal disabled" />
																				</div>
																				<div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
																					<bbbl:label key="lbl_paypal_bill_me_later" 	language="${pageContext.request.locale.language}" />
																				</div>
																				<div class="clear"></div>
																				<div class="error marTop_15 block">
																				<bbbe:error key="err_paypal_zero_balance" language ="${pageContext.request.locale.language}"/>
																				</div>
																			</c:when>
																			<c:otherwise>
																			<div class="paypalCheckoutContainer fl"> 
																				<c:choose>
																					<c:when test="${not empty securityStatus and securityStatus == 2}">
																						<dsp:a href="">
																								<dsp:property bean="ProfileFormHandler.redirectURL" value="${contextPath}/account/frags/pre_paypal.jsp"/>
																								<dsp:property bean="ProfileFormHandler.removeItemsFromOrderAndLogOut" value="true"  />
																								<img alt="Paypal checkout" role="button"
																									src="<bbbt:textArea key="txt_paypal_image_cart" language =	"${pageContext.request.locale.language}"/>"
																									class="paypalCheckoutButton">
																						</dsp:a>
																					</c:when>
																					<c:otherwise>
																						<dsp:a href="">
																							<dsp:property bean="CartModifierFormHandler.fromCart" value="${true}" priority="8" />
																							<dsp:property bean="CartModifierFormHandler.payPalErrorURL"	value="/cart/cart.jsp" priority="9" />
																							<dsp:property bean="CartModifierFormHandler.checkoutWithPaypal"	value="true" priority="0" />
																								<img alt="Paypal checkout" role="button"
																										src="<bbbt:textArea key="txt_paypal_image_cart" language =	"${pageContext.request.locale.language}"/>"
																											class="paypalCheckoutButton">
																						</dsp:a>
																					</c:otherwise>
																				</c:choose>
																			</div>
																			<div class="fl marLeft_10 billMeLaterLink marTop_15">
																				<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																			</div>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	 <c:otherwise>
																		<div class="paypalCheckoutContainer paypal_disabled fl"> 
																			<bbbt:textArea key="txt_paypal_disable_image_cart" language ="${pageContext.request.locale.language}"/>
																	   </div>
																	   <div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
																			<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																		</div>
																		<div class="clear"></div>
																	 </c:otherwise>
																	</c:choose>																	
																	</c:otherwise>
																</c:choose>												        	  											            	
												        </c:when>
												        <c:otherwise>
															<div class="paypalCheckoutContainer paypal_disabled fl"> 
																	<bbbt:textArea key="txt_paypal_disable_image_cart" language ="${pageContext.request.locale.language}"/>
															   </div>
															   <div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
																	<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																</div>
															<div class="clear"></div>
												            <div class="error marTop_15"><bbbl:label key="lbl_paypal_not_available" language="${pageContext.request.locale.language}" /></div>
												        </c:otherwise>
												    </c:choose>    
													</c:if>     
                                           <%--  end R2.2 pay pal button dependeding upon its flag in bcc   --%>
										   
										   <%-- Start R2.2   Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal --%> 
                                           <div class="clear"></div>
										   <c:choose>
										   <c:when test="${visaLogoOn || masterCardLogoOn}">
                                           <div class="visaMasterCardContainer infoMsg"> 
												<bbbl:label key="lbl_vbv_payment_secure" language="${pageContext.request.locale.language}" />
												    
													<c:if test="${visaLogoOn}">
													<bbbt:textArea key="txt_vbv_logo_visa" language="<c:out param='${language}'/>"/>
													</c:if>
													<c:if test="${visaLogoOn && masterCardLogoOn}">		
													<span class="visaLogoSep"></span>
													</c:if>
													<c:if test="${masterCardLogoOn}">
													<bbbt:textArea key="txt_vbv_logo_masterCard" language="<c:out param='${language}'/>"/>
													</c:if>
													
											</div> 
											</c:when>
											</c:choose>
											<%-- End R2.2   Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal --%>    
 
											 <div class="clear"></div>
										 </dsp:form>
									 </div>
									 <div class="clear"></div>
								 </div>
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                        </div>
                        <div class="clear"></div>
                    </dsp:oparam>
					<dsp:oparam name="true">
						<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
                        <dsp:setvalue paramvalue="wishlist.giftlistItems" param="items" />
                             
						<c:choose>
							<c:when test="${moveOperation}">

								<div id="cartContent" class="clearfix grid_10 alpha omega productListWrapper">
									<div class="clear"></div>

									<ul class="productsListContent clearfix">
										<dsp:include page="cartUndoLink.jsp">
											<dsp:param name="itemMoveFromCartID" value="${itemMoveFromCartID}"/>
											<dsp:param name="moveOperation" value="${moveOperation}"/>
											<dsp:param name="quantity" value="${quantityWish}"/>
											<dsp:param name="storeId" value="${storeId}"/>
											<dsp:param name="countNo" value="${countNo}"/>
											<dsp:param name="btsValue" value="${btsValue}"/>
											<dsp:param name="hasPorchServiceRemoved" value="${hasPorchServiceRemoved}"/>
										</dsp:include>
									</ul>
									
								</div>
							</c:when>
							<c:otherwise>
								<div id="cartBody" class="clearfix grid_10 alpha omega">
								<div id="cartTopMessaging" class="clearfix grid_10 alpha omega">
									<c:if test="${(isInternationalCustomer && orderHasIntlResterictedItem) || orderHasPersonalizedItem}">
								 <div id="saveForLaterTopMessaging" class="clearfix grid_10 alpha omega">
							          <div class="genericRedBox savedItemChangedWrapper clearfix closeWinWrapper marBottom_10 changeStoreItemWrap">
								           <h2 class="heading">
											<bbbl:label key="lbl_cart_top_intl_restricted_message_cart" language="${pageContext.request.locale.language}" />
											<p><bbbl:label key="lbl_cart_top_intl_restricted_please_remove" language="${pageContext.request.locale.language}" />
											<a href="#" class='removeItems btnAjaxSubmitCart' data-ajax-frmID="frmCartRemoveRestrict"><bbbl:label key="lbl_cart_top_intl_restricted_remove_item" language="${pageContext.request.locale.language}" /></a>
											</p>
										</h2>
									   </div>
								</c:if>
	                                <div class="clear"></div>
	                            </div>
                                <div id="cartEmptyMessaging" class="clearfix grid_10 alpha omega">
                                    <dsp:droplet name="BBBContinueShoppingDroplet">
                                        <dsp:oparam name="output">
                                            <dsp:getvalueof var="linkURL" param="continue_shopping_url" />
                                            <dsp:droplet name="Switch">
                                                <dsp:param name="value" bean="Profile.transient"/>
                                                <dsp:oparam name="false">
													<c:set var="firstVisit" scope="session" value=""/>
                                                    <p>
                                                        <strong><bbbl:label key="lbl_cartdetail_emptycart" language="<c:out param='${language}'/>"/></strong>&nbsp;&nbsp;
                                                        <c:set var="continueshoppinglink"> <bbbl:label key="lbl_cartdetail_continueshoppinglink" language="<c:out param='${language}'/>" /></c:set>
                                                        <dsp:a href="${linkURL}" title="${continueshoppinglink}" >
                                                            <bbbl:label key="lbl_cartdetail_continueshoppinglink" language="<c:out param='${language}'/>"/>
                                                        </dsp:a>
                                                    </p>
                                                </dsp:oparam>
                                                <dsp:oparam name="true">
													<c:set var="firstVisit" scope="session" value=""/>
                                                    <p>
                                                        <jsp:useBean id="emptyCartMsgLinks" class="java.util.HashMap" scope="request"/>
                                                        <c:set target="${emptyCartMsgLinks}" property="continueShoppingLink">${linkURL}</c:set>
                                                        <c:set target="${emptyCartMsgLinks}" property="loginLink">${contextPath}/account/Login</c:set>
                                                        <bbbl:textArea key="txt_cartdetail_empty_cart_msg" language="<c:out param='${language}'/>" placeHolderMap="${emptyCartMsgLinks}"/>
                                                    </p>
                                                </dsp:oparam>
                                            </dsp:droplet>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </div>
                            </div>
							</c:otherwise>
						</c:choose>
						<c:if test="${cart_one_column_layout_enable}">
							<div id="certonaSlots"> 
								<dsp:include page="/cart/subCart.jsp"> 
								<dsp:param name="CertonaContext" value="${CertonaContext}"/> 
								<dsp:param name="RegistryContext" value="${RegistryContext}"/> 
								<dsp:param name="shippingThreshold" value="${shippingThreshold}"/> 
								</dsp:include> 
							</div> 
						</c:if>
					</dsp:oparam>
                </dsp:droplet>
            </div>
            <div class="clear"></div>
        </div>

							<c:set var="lastMinItemsMax" scope="request">
								<bbbc:config key="LastMinItemsMax" configName="CertonaKeys" />
							</c:set>
							<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.itemId" var="prodIdCertona"/>
							<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.quantity" var="quantityCert"/>
							<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.price" var="prodPriceCertona"  />
							<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.registryName" var="eventType" />
							<dsp:getvalueof bean="GiftRegistryFormHandler.certonaParameter.transactionId" var="registryIdForCertona" />
							<dsp:getvalueof bean="GiftRegistryFormHandler.moveItemFromSaveForLater" var="moveItemFromSaveForLaterChk" />						
							<c:if test="${moveItemFromSaveForLaterChk eq true  && not empty registryIdForCertona }">
								<dsp:droplet name="/com/bbb/certona/droplet/CertonaDroplet">
									<dsp:param name="scheme" value="fc_lmi"/>
									<dsp:param name="context" value="${CertonaContext}"/>
									<dsp:param name="exitemid" value="${RegistryContext}"/>
									<dsp:param name="userid" value="${userId}"/>
									<dsp:param name="siteId" value="${appid}"/>
									<dsp:param name="number" value="${lastMinItemsMax}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId" scope="request"/>
									</dsp:oparam>
								</dsp:droplet>
								<script type="text/javascript">
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
								</script>
							</c:if>
							
				<%-- LTL changes divided files in two jsps and passed required parameters for included file  --%>			
							<dsp:include page="ajax_handler_cart_sfl.jsp">

                                <dsp:param name="movedCommerceItemId" value="${movedCommerceItemId}"/>
								<dsp:param name="applicationId" value="${applicationId}"/>
                                <dsp:param name="fromWishlist" value="${fromWishlist}"/>
                                <dsp:param name="quantityCart" value="${quantityCart}"/>
                                <dsp:param name="btsValue" value="${btsValue}"/>

							</dsp:include>
				<%-- LTL changes end --%>

    <div id="topCheckoutButton">
        <c:if test="${topCheckoutButtonFlag}">
			<dsp:form name="express_form" method="post" action="${contextPath}/cart/cart.jsp">
			<dsp:getvalueof id="shipGrpCount" bean="ShoppingCart.current.shippingGroupCount" />
				<div class="clearfix">
				
				
				<c:choose>
					<c:when test="${disableCheckoutBtn eq 'true'}">
						<div class="button button_active button_active_orange button_disabled">
							<input class="triggerSubmit" data-submit-button="botCheckoutBtn" type="submit" value="${lbl_checkout_checkout}" disabled="disabled" role="button" />
						</div>
					</c:when>
					<c:otherwise>
						<div class="button button_active button_active_orange">
						<input class="triggerSubmit" data-submit-button="botCheckoutBtn" type="submit" value="${lbl_checkout_checkout}" role="button" />
					</div>
					</c:otherwise>
				</c:choose>
					
					<dsp:droplet name="BBBContinueShoppingDroplet">
						<dsp:oparam name="output">
							<dsp:getvalueof var="linkURL" param="continue_shopping_url" />
							<c:set var="continueshoppinglink"> <bbbl:label key="lbl_cartdetail_continueshoppinglink" language="<c:out param='${language}'/>" /></c:set>
							<dsp:a iclass="buttonTextLink" href="${linkURL}" title="${continueshoppinglink}">
								<bbbl:label key="lbl_cartdetail_continueshoppinglink" language="<c:out param='${language}'/>"/>
							</dsp:a>
						</dsp:oparam>
					</dsp:droplet>
					<div class="clear"></div>
				</div>
				<div class="clear"></div>
			</dsp:form>
        </c:if>
    </div>

</div>
<%-- if a porch product is available, init the widget
	if the Porch file has not been loaded yet, need to lazy load it first --%>
 <c:if test="${not empty porchCallbackString}">
    <script type="text/javascript">
    	(function(){
			if(typeof window.Porch == 'undefined'){
				${porchConfigsStringWithPorchLoad}

				bbbLazyLoader.js('/_assets/global/js/porch.js', function(){${porchCallbackString}});
			}
			else {
				${porchConfigsString}

				${porchCallbackString}	
			}		
    	})();
    </script>
</c:if>
</dsp:page>
