<dsp:page>
    <%@ page import="com.bbb.constants.BBBCheckoutConstants"%>
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBOrderRKGInfo" />
	<dsp:droplet name="RepriceOrderDroplet">
		<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
	</dsp:droplet>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>
	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
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
			
	<!-- # pay pal button dependding upon its ltl flag or item in order   -->
	  <dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet">
			 <dsp:param name="order" bean="ShoppingCart.current"/>
			 <dsp:oparam name="output">
				<dsp:getvalueof var="orderHasLTLItem" param="orderHasLTLItem" />
			 </dsp:oparam>
		</dsp:droplet>
			
    <bbb:pageContainer>
    
        <%--R2.2 SCOPE #158 START deviceFingerprint JS call to cybersource--%>
        <c:if test="${deviceFingerprintOn}">  
            <c:set var="merchandId"><bbbc:config key="DF_merchandId" configName="ThirdPartyURLs"/></c:set>
            <c:set var="orgId"><bbbc:config key="DF_orgId" configName="ThirdPartyURLs"/></c:set>
            <c:set var="jSessionId">${pageContext.session.id}</c:set>
            <c:set var="jSession_id" value="${fn:split(jSessionId, '!')}" />
            <c:set var="org_jsessionid">${jSession_id[0]}</c:set>

            <div style="background:url(<bbbc:config key="deviceFinger_image_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}&amp;m=1)"></div> 
            <img src="<bbbc:config key="deviceFinger_image_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}&amp;m=2" /> 
 
			<script src="<bbbc:config key="deviceFinger_js" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}"></script> 
 
			<object type="application/x-shockwave-flash" data="<bbbc:config key="deviceFinger_flasf_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}" width="1" height="1"> 
     			<param name="movie" value="<bbbc:config key="deviceFinger_flasf_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}" /> 
     			<param name="wmode" value="transparent" /> 
			<div></div>
			</object>
		</c:if>

         <%--R2.2 SCOPE #158 END deviceFingerprint JS call to cybersource --%>
        <jsp:attribute name="section">cartDetail</jsp:attribute>
        <jsp:attribute name="PageType">CartDetails</jsp:attribute>
        <jsp:attribute name="pageWrapper">wishlist cartDetail useMapQuest useCertonaJs useStoreLocator</jsp:attribute>
        <jsp:body>
            <%--DoubleClick Floodlight START --%>
            <c:if test="${DoubleClickOn}">
                <c:if test="${currentSiteId eq TBS_BedBathUSSite}">
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
            <%--DoubleClick Floodlight END --%>

            <script type="text/javascript">
                var resx = new Object();
                var productIdsCertona = '';
                var eventTypeCertona = '';
            </script>

            <dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
                <dsp:param name="order" bean="ShoppingCart.current"/>
            </dsp:droplet>

            <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
            <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
            <c:set var="isStockAvailability" value="yes" scope="request"/>

            <dsp:getvalueof var="transientUser" bean="Profile.transient"/>
			 <!-- Added for Story -156 |Cart Analyzer |@psin52-->
			
        <c:set var="cart_one_column_layout_enable" scope="request"><bbbc:config key="cart_one_column_layout_enable" configName="FlagDrivenFunctions" /></c:set>
		
		
			<c:choose>
					
				<c:when test="${cart_one_column_layout_enable}">
					<c:set var="divClass" value ="cartDetailFull" />
					<c:set var="bannerWidth" value ="975" />
				</c:when>
				<c:otherwise>
					<c:set var="divClass" value ="" />
					<c:set var="bannerWidth" value ="810" />
				</c:otherwise>
			</c:choose>

            <div class="container_12 clearfix ${divClass}" id="content" role="main">
                <div id="cartHeader" class="clearfix grid_12">
								<h1 class="account fl"><bbbl:label key="lbl_cartdetail_yourcart" language="<c:out param='${language}'/>"/></h1>
                                <ul class="grid_2 alpha share omega clearfix">
                                    <li class="clearfix"><a href="#" class="print avoidGlobalPrintHandler" title="<bbbl:label key="lbl_cartdetail_print" language="<c:out param='${language}'/>"/>" id="printCart"><bbbl:label key="lbl_cartdetail_print" language="<c:out param='${language}'/>"/></a></li>
                                    <li class="clearfix"><a href="#" class="email avoidGlobalEmailHandler" title="<bbbl:label key="lbl_cartdetail_emailcart" language="<c:out param='${language}'/>"/>" id="openEmailCart"><bbbl:label key="lbl_cartdetail_emailcart" language="<c:out param='${language}'/>"/></a></li>
                                </ul>
								<div class="fr">
								<dsp:include page="/common/click2chatlink.jsp">
									<dsp:param name="pageId" value="3" />
								</dsp:include>
								</div>

                                <div class="padRight_20 padTop_5 topCheckoutButton fr">
                                    <dsp:form formid="express_form" method="post" action="${contextPath}/cart/cart.jsp" >
                                        <dsp:droplet name="/com/bbb/commerce/cart/droplet/CheckFlagOffItemsDroplet">
                                            <dsp:param name="checkOOSItem" value="${true}"/>
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="showCheckOutBtn" param="flagOffChecked"/>
                                                <dsp:getvalueof var="itemNotOOS" param="itemOutOfStock"/>
                                            </dsp:oparam>
                                        </dsp:droplet>

                                        <div class="clearfix">
                                            <c:choose>
                                                <c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
                                                    <dsp:input bean="CartModifierFormHandler.fromCart" value="${true}" type="hidden"/>
                                                    <div class="button button_active button_active_orange">
                                                        <dsp:input bean="CartModifierFormHandler.checkout" type="submit" value="${lbl_checkout_checkout}">
                                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                            <dsp:tagAttribute name="role" value="button"/>
                                                        </dsp:input>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="button button_active button_active_orange button_disabled">
                                                        <dsp:input bean="CartModifierFormHandler.checkout" type="submit" value="${lbl_checkout_checkout}" disabled="true">
                                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                            <dsp:tagAttribute name="role" value="button"/>
                                                        </dsp:input>
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
                                </div>

                            </div>
							<!--156 Cart analyzer savita-->
				<div class="grid_10 pageTitle noMarTop">
					<dsp:getvalueof var= "errorList" bean = "PayPalSessionBean.errorList"/>
     				<c:set var="size" value="${fn:length(errorList)}"/>
	 				<c:if test="${size gt 0}">
	 					<div class="clearfix fl">
	 						<dsp:droplet name="ForEach">
							<dsp:param name="array" value="${errorList}" />
								<dsp:oparam name="output">
									<div class="payPalAddressError error marLeft_10">
										<dsp:getvalueof var="error" param="element"/>
										${error}
									</div>
								</dsp:oparam>
							</dsp:droplet>
						</div>	
	 				</c:if>
	 				<dsp:getvalueof var="sessionBeanNull" bean = "PayPalSessionBean.sessionBeanNull"/>
					<dsp:getvalueof param="payPalError" var="payPalError"/>
					<c:if test = "${payPalError}">
						<div class="error"><bbbl:error key="err_paypal_get_service_fail" language="${pageContext.request.locale.language}" /></div>
					</c:if>
					<%--R2.2 PayPal Change: Display error message in case of webservice error : Start--%>
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param bean="CartModifierFormHandler.errorMap" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="errorCode" param="key" />
									<dsp:getvalueof var="errorMsg" bean="CartModifierFormHandler.errorMap.${errorCode}"/>
								</dsp:oparam>
						</dsp:droplet>
					<div class="error">${errorMsg}</div>
					<%--R2.2 PayPal Change: Display error message in case of webservice error : End--%>
					<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList"/>
					<c:if test="${empty errorMap}">
						<c:if test="${empty errorMsg}">
							<dsp:include page="/global/gadgets/errorMessage.jsp">
								<dsp:param name="formhandler" bean="CartModifierFormHandler"/>
							</dsp:include>
						</c:if>
					</c:if>
					
                    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                        <dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
                        <dsp:oparam name="true">
							
                            <div id="cartBody" class="clearfix grid_10 alpha omega">
                            	<div id="cartTopMessaging" class="clearfix grid_10 alpha omega">
								<dsp:include page="topLinkCart.jsp"/>
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
									
									 <c:if test="${cart_one_column_layout_enable}">
									 <div id="certonaSlots">
                                     <dsp:include page="/cart/subCart.jsp">
                                        <dsp:param name="CertonaContext" value="${CertonaContext}"/>
                                        <dsp:param name="RegistryContext" value="${RegistryContext}"/>
                                        <dsp:param name="shippingThreshold" value="${shippingThreshold}"/>
                                    </dsp:include>
									</div>
									</c:if>
                                </div>
                            </div>
                        </dsp:oparam>

                        <dsp:oparam name="false">

							<div id="cartBody" class="clearfix grid_10 alpha omega">
                            <div id="cartTopMessaging" class="clearfix grid_10 alpha omega">
								<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
								<dsp:setvalue paramvalue="wishlist.giftlistItems" param="items" />
								<dsp:include page="topLinkCart.jsp"/>
                                <div class="clear"></div>
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
                            </div>
                            <dsp:droplet name="ShipQualifierThreshold">
                                    <dsp:param name="type" value="shipping" />
                                    <dsp:param name="order" bean="ShoppingCart.current" />
                                    <dsp:oparam name="output">
                                    <dsp:getvalueof var="shippingThreshold" param="shippingThreshold"/>
                                    </dsp:oparam>
                                </dsp:droplet>
                            <dsp:droplet name="/com/bbb/commerce/cart/droplet/CartAnalyzerDroplet">
                              <dsp:param name="order" bean="ShoppingCart.current" />
                              <dsp:oparam name="output">
								  <dsp:getvalueof var="recommSkuVO" param="recommSkuVO"/>
							  </dsp:oparam>
							</dsp:droplet>

                            <div id="cartContent" class="clearfix grid_10 alpha omega productListWrapper">
                                <ul class="productsListHeader noBorderBot clearfix">
                                    <li class="cartItemDetails clearfix"><strong><bbbl:label key="lbl_cartdetail_item" language="<c:out param='${language}'/>"/></strong></li>
                                    <li class="cartQuantityDetails clearfix"><strong><bbbl:label key="lbl_cartdetail_quantity" language="<c:out param='${language}'/>"/></strong></li>
                                    <li class="cartDeliveryDetails clearfix"><strong><bbbl:label key="lbl_cartdetail_deliverydetails" language="<c:out param='${language}'/>"/></strong></li>
                                    <li class="cartTotalDetails clearfix"><strong><bbbl:label key="lbl_cartdetail_totalprice" language="<c:out param='${language}'/>"/></strong></li>
                                </ul>
                                <div class="clear"></div>

                                <dsp:include page="/cart/cartForms.jsp"/>
                                
                                <dsp:form formid="frmCartRow" iclass="frmAjaxSubmit clearfix frmCartRow" method="post" action="ajax_handler_cart.jsp">
                                <c:set var="isOutOfStock" value="${false}"/>
								<c:set var="displayDeliverySurMayApply" value="${false}"/>
								<c:set var="shipmethodAvlForAllLtlItem" value="${true}"/>
								
                                <ul class="productsListContent clearfix">
								
								<dsp:getvalueof var="userActiveRegList" bean="SessionBean.values.userActiveRegistriesList" vartype="java.util.Map"/>
								
                                    <dsp:droplet name="/atg/commerce/order/droplet/BBBCartDisplayDroplet">
                                        <dsp:param name="order" bean="ShoppingCart.current" />
										<dsp:param name="fromCart" value="true" />
                                        <dsp:oparam name="output">
                                            <dsp:droplet name="ForEach">
                                                <dsp:param name="array" param="commerceItemList" />                                                
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
													
													<c:if test="${commItem.stockAvailability ne 0}">
															<c:set var="isOutOfStock" value="${true}"/>
													</c:if>
													<dsp:getvalueof id="priceMessageVO" value="${commItem.priceMessageVO}" />
													<c:if test="${not empty priceMessageVO}">
														<c:set var="itemFlagoff" value="${priceMessageVO.flagOff}"/>
														<c:set var="disableLink" value="${priceMessageVO.disableLink}"/>
													</c:if>
                                                    <dsp:getvalueof var="count" param="count"/>
                                                    <dsp:param name="bbbItem" param="commerceItem.BBBCommerceItem.shippingGroupRelationships"/>
                                                    <dsp:getvalueof id="newQuantity" param="commerceItem.BBBCommerceItem.quantity"/>
                                                    <dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id"/>
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
											
												    <li id="cartItemID_${commItem.BBBCommerceItem.id}" class=" ${lastRow} <c:if test="${not empty commItem.BBBCommerceItem.registryId}">registeryItem</c:if> clearfix cartRow changeStoreItemWrap <c:if test="${count eq 1}">firstItem</c:if>">
														<dsp:include page="itemLinkCart.jsp">
															<dsp:param name="id" value="${commerceItemId}"/>
															<dsp:param name="oldShippingId" value="${oldShippingId}"/>
															<dsp:param name="newQuantity" value="${newQuantity}"/>
															<dsp:param name="image" param="commerceItem.BBBCommerceItem.auxiliaryData.catalogRef.mediumImage"/>
															<dsp:param name="displayName" value="${commItem.skuDetailVO.displayName}"/>
															<dsp:param name="priceMessageVO" value="${commItem.priceMessageVO}"/>
															
															
														</dsp:include>
														<!-- LTL Alert  -->
                                                       <c:if test="${ not ship_method_avl && commItem.skuDetailVO.ltlItem}" >
															<div class="clear"></div>
															<div class="ltlItemAlert alert alert-info">
																<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>
															</div>
														</c:if>
														<!-- LTL Alert  -->
														
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
                                                                <div class="registeryItemHeader clearfix">
                                                                    <dsp:getvalueof bean="ShoppingCart.current.registryMap.${commItem.BBBCommerceItem.registryId}" var="registratantVO"/>
                                                                    <c:if test="${commItem.BBBCommerceItem.registryInfo ne null}">
                                                                        <span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>
                                                                        <dsp:getvalueof var="registryInfo" param="commerceItem.BBBCommerceItem.registryInfo"/>
																		<dsp:a href="${registryUrl}">
																			<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}"/>
																			<dsp:param name="eventType" value="${registratantVO.registryType.registryTypeDesc}" />
																			<strong>${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if>
																			<bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong>
																		</dsp:a>
                                                                        <span>${registratantVO.registryType.registryTypeDesc}</span>&nbsp;<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
                                                                    </c:if>                                                                
																	</div>
                                                                <div class="clear"></div>
                                                            </c:if>
  
                                                            <c:if test="${empty commItem.BBBCommerceItem.registryId}">
                                                                <script type="text/javascript">
                                                                    productIdsCertona = productIdsCertona + '${productIdsCertona}' + ';';
                                                                </script>
                                                            </c:if>

                                                            <div class="prodItemWrapper clearfix">
                                                                <ul class="clearfix">
                                                                    <li class="cartItemDetails clearfix itemCol">
                                                                    	<dsp:getvalueof var="image" param="commerceItem.skuDetailVO.skuImages.mediumImage"/>
																		<dsp:getvalueof var="skuUpc" value="${commItem.skuDetailVO.upc}" />
																		<dsp:getvalueof var="skuColor" value="${commItem.skuDetailVO.color}" />
																		<dsp:getvalueof var="skuSize"  value="${commItem.skuDetailVO.size}" />
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
																								</span>
                                                                                                </div>
																								<!--156 Cart analyzer savita-->
																							</c:when>
																							<c:otherwise>
																								<dsp:a iclass="prodImg padLeft_10 block fl" page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																									<c:choose>
																										<c:when test="${empty image}">
																											<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																										</c:when>
																										<c:otherwise>
																											<img class="fl productImage noImageFound" src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
																										</c:otherwise>
																									</c:choose>
																								</dsp:a>
                                                                                                <div class="fl padLeft_10">
																								<dsp:a iclass="prodInfo block cb padTop_5" page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																										<span class="prodName productName"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></span>
                                                                                                </dsp:a>
																								<span class="prodAttribsCart">
																									<c:if test='${not empty skuColor}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" /></c:if>
																									<c:if test='${not empty skuSize}'><c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" /></c:if>
																								</span>
                                                                                                </div>
																								<!--156 Cart analyzer savita-->
																							</c:otherwise>
																						</c:choose>
                                                                                    </dsp:oparam>
                                                                                </dsp:droplet>
                                                                        <c:if test="${null ne commItem.skuDetailVO.displayName}">
                                                                            <c:set var="skuFlag" value="true"/>
                                                                            <script type="text/javascript">
                                                                                eventTypeCertona = eventTypeCertona + 'shopping+cart' + ';';
                                                                            </script>
                                                                        </c:if>
                                                                    </li>

                                                                    <li class="cartQuantityDetails clearfix itemCol">
                                                                        <ul class="prodDeliveryInfo">
																		<c:choose>
																			<c:when test="${not itemFlagoff}">
																				<li class="quantityBox">
																					<label id="lblquantity_text_${count}" class="hidden" for="quantity_text_${count}"><bbbl:label key="lbl_cartdetail_quantity" language="<c:out param='${language}'/>"/></label>
                                                                                    <div class="spinner"> <a href="#" class="scrollDown down" title="Decrease Quantity">down</a>
																					<input name="${commItem.BBBCommerceItem.id}" type="text" value="${commItem.BBBCommerceItem.quantity}" id="quantity_text_${count}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="itemQuantity fl" maxlength="2" aria-required="true" aria-labelledby="lblquantity_text_${count}" />
                                                                                    <a href="#" class="scrollUp up" title="Increase Quantity">up</a> <div class="clear"></div></div>
																				</li>
																				<li class="lnkUpdate">
																					<a href="#" class="triggerSubmit" data-submit-button="btnUpdate${commItem.BBBCommerceItem.id}" title="<bbbl:label key="lbl_cartdetail_update" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_cartdetail_update" language="<c:out param='${language}'/>"/></strong></a>
																					<dsp:input type="hidden" bean="CartModifierFormHandler.commerceItemId" value="${commItem.BBBCommerceItem.id}"/>
																					<dsp:input type="hidden" bean="CartModifierFormHandler.setOrderSuccessURL" value="/tbs/cart/ajax_handler_cart.jsp"/>
																					<dsp:input type="hidden" bean="CartModifierFormHandler.setOrderErrorURL" value="/tbs/cart/ajax_handler_cart.jsp"/>
																					<dsp:input name="btnUpdate" id="btnUpdate${commItem.BBBCommerceItem.id}" type="submit" bean="CartModifierFormHandler.setOrderByCommerceId" iclass="hidden" />
																				</li>
																			</c:when>
																			<c:otherwise>
																					<input name="${commItem.BBBCommerceItem.id}" type="hidden" value="${commItem.BBBCommerceItem.quantity}" id="quantity_text_${count}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="itemQuantity" maxlength="2"/>
																			</c:otherwise>
																		</c:choose>
                                                                            <li class="lnkRemove">
                                                                            	<dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
                                                                                <a href="#" class="btnAjaxSubmitCart" data-ajax-frmID="frmCartItemRemove" onclick="omniRemove('${productId}','${commItem.BBBCommerceItem.catalogRefId}');" title="<bbbl:label key="lbl_cartdetail_remove" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_cartdetail_remove" language="<c:out param='${language}'/>"/></strong></a>
                                                                            </li>
																		<c:if test="${not itemFlagoff}">
                                                                            <li class="lnkSaveForLater">
																				<dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
                                                                                <a href="#" class="btnAjaxSubmitCart" data-ajax-frmID="frmCartSaveForLater" onclick="omniAddToList(this, '${productId}', '${commItem.BBBCommerceItem.catalogRefId}', '${commItem.BBBCommerceItem.quantity}');" title="Save for Later"><strong>Save for Later</strong></a>
                                                                            </li>
																		</c:if>
                                                                        </ul>
                                                                    </li>

                                                                    <%-- Display shipping details --%>
                                                                    <li class="cartDeliveryDetails clearfix itemCol">
                                                                        <dsp:include page="/cart/cart_includes/shippingType.jsp">
																			<dsp:param name="commItem" value="${commItem}"/>
																		</dsp:include>
                                                                    </li>

																	<%-- Display item price details --%>
                                                                    <dsp:include page="/cart/cart_includes/priceDisplay.jsp">
																		<dsp:param name="commItem" value="${commItem}"/>
																		<dsp:param name="orderObject" bean="ShoppingCart.current" />
																	</dsp:include>
                                                                </ul>
                                                                <div class="clear"></div>
                                                                <input type="hidden" name="saveStoreId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.storeId}" />
                                                                <input type="hidden" name="saveCount" class="frmAjaxSubmitData" value="${count}" />
                                                                <input type="hidden" name="saveQuantity" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.quantity}" />
                                                                <input type="hidden" name="saveCurrentItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />

                                                                <input type="hidden" name="removeCommerceItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
                                                                <input type="hidden" name="removeSubmitButton" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />

                                                                <input type="hidden" name="shipOldShippingId" class="frmAjaxSubmitData" value="${oldShippingId}" />
                                                                <input type="hidden" name="shipCommerceItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
                                                                <input type="hidden" name="shipNewQuantity" class="frmAjaxSubmitData" value="${newQuantity}" />

                                                                <input type="hidden" name="storeId" class="storeId" value="${commItemStoreId}" />
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
													 <dsp:include page="/cart/cart_includes/recommItem.jsp">
														<dsp:param name="recommSkuVO" value="${recommSkuVO}"/>
														<dsp:param name="commItem" value="${commItem}"/>
													</dsp:include>
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
								 <div id="leftCol" class="grid_8 alpha clearfix">
								 
								  <!-- Added for Story -156 |Cart Analyzer |@psin52-->
									
									 <c:if test="${cart_one_column_layout_enable}">
                                     <div id="certonaSlots">
                                     <dsp:include page="/cart/subCart.jsp">
                                        <dsp:param name="CertonaContext" value="${CertonaContext}"/>
                                        <dsp:param name="RegistryContext" value="${RegistryContext}"/>
                                        <dsp:param name="shippingThreshold" value="${shippingThreshold}"/>
                                    </dsp:include>
									</div>
									</c:if>
									
                                    <dsp:droplet name="Switch">
										 <dsp:param name="value" bean="Profile.transient"/>
										 <dsp:oparam name="false">
											 <dsp:getvalueof id="bopusOnly" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
											 <c:choose>
												 <c:when test="${CouponOn}" >
													 <dsp:include page="/cart/couponDisplay.jsp">
														 <dsp:param name="action" value="${contextPath}/cart/cart.jsp"/>
														 <dsp:param name="cartCheck" value="true"/>
													 </dsp:include>
												 </c:when>
												 <c:otherwise>
													 &nbsp;
												 </c:otherwise>
											 </c:choose>
										 </dsp:oparam>
										 <dsp:oparam name="true">
											 &nbsp;
										 </dsp:oparam>
									 </dsp:droplet>
									
								 </div>
								 <div id="rightCol" class="grid_4 omega clearfix fr">
									 <dl class="checkoutSummary clearfix">
										 <dt class="fl bold">
											 <dsp:droplet name="/com/bbb/commerce/cart/BBBCartItemCountDroplet">
												 <dsp:param name="shoppingCart" bean="ShoppingCart.current" />
												 <dsp:oparam name="output">
													 <dsp:valueof param="commerceItemCount"/>
												 </dsp:oparam>
											 </dsp:droplet>
											 <bbbl:label key="lbl_cartdetail_items" language="<c:out param='${language}'/>"/>
										 </dt>
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

												<dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
												<dsp:getvalueof var="maxDeliverySurchargeReached" param="priceInfoVO.maxDeliverySurchargeReached"/>
											    <dsp:getvalueof var="totalAssemblyFee" param="priceInfoVO.totalAssemblyFee"/>
												<dsp:getvalueof var="maxDeliverySurcharge" param="priceInfoVO.maxDeliverySurcharge"/>
												
												 <dd class="fl bold"><dsp:valueof value="${storeAmount + onlinePurchaseTotal }" converter="currency"/></dd>
                                                 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
												 <c:choose>
													 <c:when test="${freeShipping ne true}">
														<c:choose>
															<c:when  test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}"> 
																  <dt class="fl"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>
																	<dsp:include page="/common/shipping_method_description.jsp"/>
																  </dt>
																  <dd class="fl">TBD</dd>
															</c:when>
															<c:otherwise>
																 <dt class="fl"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>
																	 <dsp:include page="/common/shipping_method_description.jsp"/>
																 </dt>
																 <dd class="fl"><dsp:valueof value="${rawShippingTotal}" converter="currency" number="0.00"/>*</dd>
														     </c:otherwise>
														  </c:choose>
														   <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
																 <dsp:droplet name="ForEach">
																	 <dsp:param name="array" param="priceInfoVO.shippingAdjustments" />
																	 <dsp:param name="elementName" value="shippingPromoDiscount" />
																	 <dsp:oparam name="outputStart">
																		 <dt class="fl">
																	 </dsp:oparam>
																	 <dsp:oparam name="outputEnd">
																		 </dt>
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
																 <dd class="fl"><span class="highlight">(<dsp:valueof value="${shippingSavings}" converter="currency" number="0.00"/>)</span></dd>
																 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
																 </c:if>
													 </c:when>
													 <c:otherwise>
														 <%-- LTL changes --%>
														 <c:choose>
															 <c:when test="${orderHasLTLItem eq true && rawShippingTotal eq 0.0}" >
																 <dt class="fl"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/></dt>
																 <dd class="fl">  TBD </dd>
															 </c:when>
															 <c:otherwise>
														 <dt class="fl"><strong><bbbl:label key="lbl_cartdetail_freeshipping" language="<c:out param='${language}'/>"/></strong></dt><dd class="fl">&nbsp;</dd>
                                                         <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
													 </c:otherwise>
												 </c:choose>
													 </c:otherwise>
												 </c:choose>
												 <c:if test="${totalSurcharge gt 0.0}">
													 <dt class="fl"><bbbl:label key="lbl_preview_surcharge" language="<c:out param='${language}'/>"/></dt>
													 <dd class="fl"><dsp:valueof value="${totalSurcharge}" converter="currency"/></dd>
                                                     <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
												 </c:if>
												 <c:if test="${surchargeSavings gt 0.0}">
													 <dt class="fl highlight"><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></dt>
													 <dd class="fl highlight">(<dsp:valueof value="${surchargeSavings}" converter="currency"/>)</dd>
                                                     <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
												 </c:if>
												 <dsp:getvalueof var="ecoFeeTotal" value="${storeEcoFeeTotal + onlineEcoFeeTotal }"/>
												 <c:if test="${ecoFeeTotal gt 0.0}">
													 <dt class="fl"><bbbl:label key="lbl_preview_ecofee" language="<c:out param='${language}'/>"/></dt>
													 <dd class="fl"><dsp:valueof value="${ecoFeeTotal}" converter="currency"/></dd>
                                                     <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
												 </c:if>
												<!-- Additional info for LTL items summary -->
												<c:if  test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}"> 	
                                                <dt class="fl bold highlightBlack padTop_5"><bbbl:label key="ltl_delivery_surcharge_may_apply" language="<c:out param='${language}'/>"/></dt>
                                                <dd class="fl bold highlightBlack padTop_5">TBD</dd>
												</c:if>
												<c:if  test ="${totalDeliverySurcharge gt 0.0 && shipmethodAvlForAllLtlItem}"> 
	                                                <dt class="fl bold highlightBlack padTop_5"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/></dt>
	                                                <dd class="fl bold highlightBlack padTop_5"><dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/></dd>
												</c:if>
                                                <c:if  test ="${maxDeliverySurchargeReached}"> 
	                                                 <c:choose>
														<c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
															  <dt class="fl bold highlightRed padTop_5"><bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/> <br>
															  <a class="highlightBlue newOrPopup maxSurcharges" href="/tbs/cart/static/max_surcharges_info.jsp"><bbbl:label key="lbl_what_this_mean" language="${pageContext.request.locale.language}" /></a>
															  </dt>
															  <dd class="fl bold highlightRed padTop_5">(TBD)</dd>
														</c:when>
														<c:otherwise>
															 <dt class="fl bold highlightRed padTop_5">
															<bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/> <br>
															<a class="highlightBlue newOrPopup maxSurcharges" href="/tbs/cart/static/max_surcharges_info.jsp"><bbbl:label key="lbl_what_this_mean" language="${pageContext.request.locale.language}" /></a>
															</dt>
															<dd class="fl bold highlightRed padTop_5">(<dsp:valueof value="${totalDeliverySurcharge - maxDeliverySurcharge}" number="0.00" converter="currency"/>)</dd>
														</c:otherwise>
												   </c:choose>
												</c:if>
                                                <c:if  test ="${totalAssemblyFee gt 0.0}"> 
													<dt class="fl bold highlightBlack padTop_5"><bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/></dt>
	                                                <dd class="fl bold highlightBlack padTop_5"><dsp:valueof value="${totalAssemblyFee}" number="0.00" converter="currency"/></dd>
												</c:if>
												<!-- Additional info for LTL items summary -->
												 
												 <dsp:getvalueof var="preTaxAmout" value="${orderPreTaxAmout + storeAmount + storeEcoFeeTotal }"/>
												 <dt class="fl total"><strong><bbbl:label key="lbl_cartdetail_pretaxtotal" language="<c:out param='${language}'/>"/></strong></dt>
												 <dd class="fl total"><span class="highlight highlightGreen"><dsp:valueof value="${preTaxAmout}" converter="currency"/></span></dd>
                                                 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
												 <c:if test="${totalSavedAmount gt 0.0}">
													    <c:choose>
														<c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
															 <dt class="fl"><bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>"/></dt>
															 <dd class="fl">TBD</dd>
															 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
														</c:when>
														<c:otherwise>
															 <dt class="fl"><bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>"/></dt>
															 <dd class="fl"><dsp:valueof value="${totalSavedAmount}" converter="currency"/></dd>
															 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
														</c:otherwise>
												  	    </c:choose>
												 </c:if>
											 </dsp:oparam>
										 </dsp:droplet>
										 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
									 </dl>
									 <div class="clear"></div>
									 <div class="requiredInfo">
										 <p class="marTop_10 noMarBot smallText"><bbbl:label key="lbl_cartdetail_shippinginfo" language="<c:out param='${language}'/>"/></p>
										 <bbbl:label key="lbl_cartdetail_viewshippingcostslink" language="<c:out param='${language}'/>"/>
									 </div>
									 <div class="clear"></div>
									 <div class="clearfix marTop_10 checkoutButtons">
										 <dsp:form formid="express_form" method="post" action="${contextPath}/cart/cart.jsp" >
												 <c:if test="${orderHasLTLItem ne true }">
													 <dsp:droplet name="/com/bbb/commerce/cart/droplet/DisplayExpressCheckout">
														 <dsp:param name="order" bean="ShoppingCart.current"/>
														 <dsp:param name="profile" bean="Profile"/>
														 <dsp:oparam name="output">
															 <dsp:getvalueof var="disableExpressCheckout" param="disableExpressCheckout" />
															 <div class="clearfix">
																 <div class="checkbox fl">
																	 <c:choose>
																		 <c:when test="${disableExpressCheckout eq false }">
																			 <dsp:input id="express_checkout" bean="CartModifierFormHandler.expressCheckout" type="checkbox" name="expire_checkout">
		                                                                        <dsp:tagAttribute name="aria-checked" value="false"/>
		                                                                     </dsp:input>
																		 </c:when>
																		 <c:otherwise>
																			 <dsp:input id="express_checkout" disabled="true" bean="CartModifierFormHandler.expressCheckout" type="checkbox" name="expire_checkout">
		                                                                        <dsp:tagAttribute name="aria-checked" value="false"/>
		                                                                     </dsp:input>
																		 </c:otherwise>
																	 </c:choose>
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

											 <div class="clearfix">
											 <c:choose>
												 <c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
													<dsp:input bean="CartModifierFormHandler.fromCart" value="${true}" type="hidden"/>
													 <div class="button button_active button_active_orange">
														 <dsp:input bean="CartModifierFormHandler.checkout" type="submit" value="${lbl_checkout_checkout}">
                                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                            <dsp:tagAttribute name="role" value="button"/>
                                                         </dsp:input>
													 </div>
												 </c:when>
												 <c:otherwise>
													 <div class="button button_active button_active_orange button_disabled">
														 <dsp:input bean="CartModifierFormHandler.checkout" type="submit" value="${lbl_checkout_checkout}" disabled="true">
                                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                            <dsp:tagAttribute name="role" value="button"/>
                                                         </dsp:input>
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

												 <c:if test="${not (showCheckOutBtn and itemNotOOS and (not isOutOfStock))}">
													 <div class="clearfix padTop_10 padBottom_10 cb">
														 <span class="error"><bbbl:label key="lbl_cart_outofstockmessage" language="<c:out param='${language}'/>"/></span>
													 </div>
												 </c:if>

												 <div class="clear"></div>
											 </div>
											<!-- #Scope  : Start International shipping link depending upon its Switch off/on flag in bcc   -->
                                            <div class="clear"></div>
                                            <c:if test="${internationalShippingOn}">
                                        	<c:choose>
															<c:when
																test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
																<c:choose>
																	<c:when test="${isOrderAmtCoveredVar}">
																		<div class="button_disabled">
																			<bbbl:label key="lbl_chkout_international_shipping"
																				language="<c:out param='${language}'/>" />

																			<img
																				src="/_assets/global/images/international_shipping_help_icon.png"
																				alt="International Shipping Help" /> 
																		</div>
																	</c:when>
																	<c:otherwise>
																		<div id="intlShippingLink" class="marTop_10">

																			<dsp:a title="International Shipping"
																				iclass="openIntlShippingLink" href="javascript:">
																				<bbbl:label key="lbl_chkout_international_shipping"
																					language="<c:out param='${language}'/>" />
																			</dsp:a>
																			<a id="intlShippingHelpIcon" class="info"> <img
																				src="/_assets/global/images/international_shipping_help_icon.png"
																				alt="International Shipping Help" /> <span class="textLeft">
																					<bbbt:textArea key="txt_chkout_international_shipping_help"
																						language="<c:out param='${language}'/>" /> </span> </a>
																		</div>
																	</c:otherwise>
																</c:choose>

															</c:when>
															<c:otherwise>
																<div class="button_disabled">
																			<bbbl:label key="lbl_chkout_international_shipping"
																				language="<c:out param='${language}'/>" />

																			<img
																				src="/_assets/global/images/international_shipping_help_icon.png"
																				alt="International Shipping Help" /> 
																</div>
															</c:otherwise>
														</c:choose>
													
										  </c:if>
																																						  
										  <!-- #Scope  : END International shipping link -->
									
																				
                                          <!-- #Scope 83 A : Start R2.2 pay pal button dependeding upon its flag in bcc   -->
                                          <dsp:getvalueof var="orderType" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />                                                
                                                 <c:if test="${paypalCartButtonEnable}">
                                                 	<c:choose>
												        <c:when test="${paypalOn}">
																<c:choose >
																	<c:when test="${orderHasLTLItem eq true}">
																		<div class="paypalCheckoutContainer paypal_disabled fl"> 
																		<bbbt:textArea key="txt_paypal_disable_image_new" language ="${pageContext.request.locale.language}"/>
																		</div>
																		<div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_10 disableText">
																			<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																		</div>
																		<div class="clear"></div>
																		<div class="marTop_15 smallText bold highlightRed"><bbbl:label key="lbl_paypal_not_availab_ltl_product" language="${pageContext.request.locale.language}" /></div>
																	</c:when>
																	<c:when test="${(orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID')}">
																		<div class="paypalCheckoutContainer paypal_disabled fl"> 
																		<bbbt:textArea key="txt_paypal_disable_image_cart" language ="${pageContext.request.locale.language}"/>
																		</div>
																		<div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
																			<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																		</div>
																		<div class="clear"></div>
																		<div class="error marTop_15"><bbbl:label key="lbl_paypal_not_avilable_bopus_hybrid" language="${pageContext.request.locale.language}" /></div>
																	</c:when>
																	<c:otherwise>
																	<c:choose>
																	 <c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
																		<c:choose>
																			<c:when test="${isOrderAmtCoveredVar}">
																				<div class="paypalCheckoutContainer fl button_disabled"> 
																				<img src="/_assets/global/images/paypal_disabled.png" class="paypalCheckoutButton">
																				</div>
																				<div class="fl marLeft_10 billMeLaterLink billMeLaterLink_disabled marTop_15">
																					<bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
																				</div>
																				<div class="clear"></div>
																				<div class="error marTop_15">
																				<bbbe:error key="err_paypal_zero_balance" language ="${pageContext.request.locale.language}"/>
																				</div>
																			</c:when>
																			<c:otherwise>
																			<div class="paypalCheckoutContainer fl"> 
																				<dsp:a href="">
																				<dsp:property bean="CartModifierFormHandler.fromCart" value="${true}" priority="8"/>
																				<dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/cart/cart.jsp" priority="9"/>
																				<dsp:property bean="CartModifierFormHandler.checkoutWithPaypal" value="true" priority="0"/>
																				<img src="<bbbt:textArea key="txt_paypal_image_cart" language =	"${pageContext.request.locale.language}"/>" class="paypalCheckoutButton">
																				</dsp:a>
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
                                                                                                 
                                          <!--  end R2.2 pay pal button dependeding upon its flag in bcc   --> 
                                          
                                          <!-- Start R2.2   Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal --> 
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
											<!-- End R2.2   Scope#258-M;258-N Verified by Visa-Display logo;-Open Modal -->    

											 <div class="clear"></div>
										 </dsp:form>
									 </div>
									 <div class="clear"></div>
								 </div>

                                    <div class="clear"></div>
                                </div>
                                <div class="clear"></div>
                            </div>
							</div>
                            <div class="clear"></div>
                        </dsp:oparam>
                    </dsp:droplet>
                    <div class="clear"></div>

                    <dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
                        <dsp:oparam name="empty">
                           <div id="saveForLaterHeader" class="clearfix grid_10 alpha omega hidden">
                                <h2 class="account fl"><bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>"/></h2>
                                <ul class="grid_2 alpha share omega clearfix">
                                    <li class="clearfix"><a href="#" class="print avoidGlobalPrintHandler" title="Print Saved Items" id="printSavedItems"><bbbl:label key="lbl_sfl_print_saved_items" language="<c:out param='${language}'/>"/></a></li>
                                    <li class="clearfix"><a href="#" class="email avoidGlobalEmailHandler" title="Email Saved Items" id="openEmailSavedItems"><bbbl:label key="lbl_sfl_email_saved_items" language="<c:out param='${language}'/>"/></a></li>
                                </ul>                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                            <div id="saveForLaterBody" class="savedItems clearfix grid_10 alpha omega hidden">
                                <div id="saveForLaterEmptyMessaging" class="clearfix grid_10 alpha omega">
                                </div>
                            </div>
                            <div class="clear"></div>
                        </dsp:oparam>
                        <dsp:oparam name="output">
                            <div id="saveForLaterHeader" class="clearfix grid_10 alpha omega">
                                <h2 class="account fl"><bbbl:label key="lbl_sfl_your_saved_items" language="<c:out param='${language}'/>"/></h2>
                                <ul class="grid_2 alpha share omega clearfix">
                                    <li class="clearfix"><a href="#" class="print avoidGlobalPrintHandler" title="Print Saved Items" id="printSavedItems"><bbbl:label key="lbl_sfl_print_saved_items" language="<c:out param='${language}'/>"/></a></li>
                                    <li class="clearfix"><a href="#" class="email avoidGlobalEmailHandler" title="Email Saved Items" id="openEmailSavedItems"><bbbl:label key="lbl_sfl_email_saved_items" language="<c:out param='${language}'/>"/></a></li>
                                </ul>
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                            <div id="saveForLaterBody" class="savedItems clearfix grid_10 alpha omega">
                                <div id="saveForLaterContent" class="clearfix grid_10 alpha omega productListWrapper">
                                    <ul class="productsListHeader noBorderBot clearfix">
                                        <li class="wishlistItemDetails clearfix"><strong><bbbl:label key="lbl_sfl_item" language="<c:out param='${language}'/>"/></strong></li>
                                        <li class="wishlistQuantityDetails clearfix"><strong><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></strong></li>
                                        <li class="wishlistTotalDetails clearfix"><strong><bbbl:label key="lbl_sfl_total" language="<c:out param='${language}'/>"/></strong></li>
                                    </ul>
                                    <div class="clear"></div>
									<dsp:include page="saveForlaterCartForms.jsp"/>
									<dsp:form formid="frmSavedItems" iclass="frmAjaxSubmit clearfix frmSavedItems" method="post" action="ajax_handler_cart.jsp">
                                    <ul class="productsListContent clearfix">
								
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
                                            <dsp:oparam name="outputStart">
                                            </dsp:oparam>
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
												<c:set var="displaySize" >
													<bbbc:config key="sfl_display_size" configName='CartAndCheckoutKeys' />
												 </c:set>
												 <c:if test="${empty displaySize}">
													<c:set var="displaySize" >
														2
													 </c:set>
												 </c:if>
												<c:if test="${currentCount == displaySize+1 && arraySize > displaySize}">
													<li id="savedItemID_BTN" class="savedItemRow showAllBtn">
														<div class="prodItemWrapper clearfix">
															<div class="button">
																<input id="showAllBtn" type="button" value="Show all ${arraySize} items" />
															</div>
														</div>
													</li>
												</c:if>

												
                                                <li id="  savedItemID_${wishListItemId}" class=" ${lastRow} <c:if test="${not empty registryId}">registeryItem</c:if> savedItemRow changeStoreItemWrap clearfix <c:if test="${currentCount > displaySize && arraySize > displaySize}">hidden</c:if>">
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
																				<span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>
																				<dsp:a href="${registryUrl}">
																				<dsp:param name="registryId" value="${registryId}"/>
																				<dsp:param name="eventType" value="${registryTypeName}" />
																				<strong>${registryVO.primaryRegistrant.firstName}<c:if test="${not empty registryVO.coRegistrant.firstName}">&nbsp;&amp;&nbsp;${registryVO.coRegistrant.firstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong>
																				</dsp:a>
																				<span>${registryTypeName}</span>&nbsp;<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
																			</div>																	

																<div class="clear"></div>
														</c:if>
                                                        <div class="clear"></div>
														<!-- LTL Alert  -->
														<c:if test="${isLtlFlag}" >
															<div class="clear"></div>
															<div class="ltlItemAlert alert alert-info">
																<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>
															</div>
														</c:if>
														<!-- LTL Alert  -->
                                                        <div class="prodItemWrapper clearfix">
                                                            <ul class="clearfix">
                                                                <li class="wishlistItemDetails clearfix">
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


																	<c:choose>
																		<c:when test="${not flagOff}">
																			<dsp:a iclass="prodImg clearfix block" title="${pName}" page="${finalUrl}?skuId=${skuID}">
																				<c:choose>
																					<c:when test="${empty prodImage}">
																						<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage"/>
																					</c:when>
																					<c:otherwise>
																						<img src="${scene7Path}/${prodImage}" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage noImageFound"/>
																					</c:otherwise>
																				</c:choose>
																			</dsp:a>
																		</c:when>
																		<c:otherwise>
                                                                            <span class="fl padLeft_10 block">
																			<c:choose>
																				<c:when test="${empty prodImage}">
																					<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage"/>
																				</c:when>
																				<c:otherwise>
																					<img src="${scene7Path}/${prodImage}" height="146" width="146" alt="${pName}" title="${pName}" class="fl productImage noImageFound"/>
																				</c:otherwise>
																			</c:choose>
                                                                            </span>
																		</c:otherwise>
																	</c:choose>
																	<c:if test="${empty pName}">
																		<dsp:getvalueof var="pName" value="${skuDisplayName}" />
																	</c:if>

																	<span class="prodInfo block breakWord">
																		<span class="prodName">
																		   <c:choose>
																				<c:when test="${not flagOff}">
																					<dsp:a page="${finalUrl}?skuId=${skuID}" iclass="productName" title="${productVO.name}">${pName}</dsp:a>
																				</c:when>
																				<c:otherwise>
																					<span class="productName disableText">${pName}</span>
																				</c:otherwise>
																			</c:choose>
																			<dsp:getvalueof var="pName" value="" />
																			<dsp:getvalueof var="prodImage" value="" />
																		</span>
																		<span class="prodAttribsSavedItem">
																			<c:if test='${not empty skuColor}'><c:choose><c:when test="${rollupAttributesFinish == 'true'}"><bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/> : </c:when><c:otherwise><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : </c:otherwise></c:choose><dsp:valueof value="${skuColor}" valueishtml="true" /></c:if>
																			<c:if test='${not empty skuSize}'><c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" /></c:if>
																			<c:set var="rollupAttributesFinish" value="false" />
																		</span>
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
																						<span class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																						<dsp:a  page="${finalUrl}?skuId=${skuID}&categoryId=${CategoryId}&showRatings=true">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></dsp:a>
																						</span>
																					</c:when>
																					<c:otherwise>
																						<span class="prodReview clearfix marBottom_10 prodReview<fmt:formatNumber value="${rating}" pattern="#0" />">
																						<dsp:a  page="${finalUrl}?skuId=${skuID}&showRatings=true">(${totalReviewCount}) <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></dsp:a>
																						</span>
																					</c:otherwise>
																				</c:choose>
																			</c:if>
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
                                                                        <c:choose>
                                                                            <c:when test="${(not empty salePriceD) && (salePriceD gt 0.0)}">
                                                                                <span class="prodPrice">
																					<dsp:include page="/global/gadgets/formattedPrice.jsp">
																						<dsp:param name="price" value="${salePriceD}"/>
																					</dsp:include>
																				</span>
																				<c:set var="itemPrice">${salePriceD}</c:set>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="prodPrice">
																					<dsp:include page="/global/gadgets/formattedPrice.jsp">
																						<dsp:param name="price" value="${listPriceD}"/>
																					</dsp:include>
																				</span>
																				<c:set var="itemPrice">${listPriceD}</c:set>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    <div class="clear"></div>
                                                                </span>
                                                                </li>

                                                                <li class="wishlistQuantityDetails clearfix">
                                                                    <ul class="prodDeliveryInfo padLeft_10 padTop_10">
																	<c:if test="${not flagOff}">
																			<li class="quantityBox">
																				<label id="lblquantity_text_${skuID}" for="quantity_text_${skuID}" class="hidden"><bbbl:label key="lbl_sfl_quantity" language="<c:out param='${language}'/>"/></label>
                                                                                <div class="spinner"> <a href="#" class="scrollDown down" title="Decrease Quantity">down</a>
																				<input name="${wishListItemId}" type="text" value="${quantity}" id="quantity_text_${skuID}" maxlength="2" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="moveToCartData itemQuantity fl" aria-required="true" aria-labelledby="lblquantity_text_${skuID}" />
                                                                                <a href="#" class="scrollUp up" title="Increase Quantity">up</a> <div class="clear"></div></div>
																			</li>
																	</c:if>																	
																		<dsp:input bean="GiftlistFormHandler.fromCartPage" type="hidden" value="true" />
																		<dsp:input bean="GiftlistFormHandler.productId" type="hidden" value="${prodID}" />
																		<dsp:input bean="GiftlistFormHandler.catalogRefIds" type="hidden" value="${skuID}" />
																		<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL" type="hidden" value="/tbs/cart/ajax_handler_cart.jsp" />
																		<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsErrorURL" type="hidden" value="/tbs/cart/ajax_handler_cart.jsp" />
																		<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
																		<dsp:setvalue paramvalue="wishlist.id" param="giftlistId" />
																		<dsp:input bean="GiftlistFormHandler.giftlistId" value="${giftListId}" type="hidden" />
																		<dsp:input bean="GiftlistFormHandler.currentItemId" value="${wishListItemId}" type="hidden" />
																		<c:if test="${not flagOff}">
																			<li class="lnkUpdate"><a href="#" class="triggerSubmit" data-submit-button="btnUpdateSFL0${currentCount}" title="Update Quantity"><strong><bbbl:label key="lbl_sfl_update" language="<c:out param='${language}'/>"/></strong></a></li>
																		</c:if>
																		<dsp:getvalueof id="giftlistId" param="wishlist.id"/>
																		<input type="hidden" name="removeGiftitemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
																		<input type="hidden" name="giftlistId" class="frmAjaxSubmitData" value="${giftlistId}" />
                                                                        <li class="lnkRemove"><a href="#" data-ajax-frmID="frmSaveItemRemove" class="btnAjaxSubmitSFL" data-submit-button="btnRemoveSFL0${currentCount}" title="Remove Product from Save For Later" onclick="removeWishList('${prodID}','${skuID }');"><strong><bbbl:label key="lbl_sfl_remove" language="<c:out param='${language}'/>"/></strong></a></li>
                                                                    </ul>
                                                                    <div class="clear"></div>
																	<dsp:input bean="GiftlistFormHandler.updateGiftlistItems" name="btnUpdate" id="btnUpdateSFL0${currentCount}" type="submit" value="Update" iclass="hidden" />
                                                                </li>

                                                                <li class="wishlistTotalDetails clearfix">
                                                                    <ul class="clearfix noMar">
                                                                        <li class="prodInfo">
																			<span class="prodPrice">
																				<dsp:include page="/global/gadgets/formattedPrice.jsp">
																					<dsp:param name="price" value="${itemPrice * quantity}"/>
																				</dsp:include>
																			</span>
																		<div class="clear"></div>
																		</li>
																	<dsp:getvalueof id="cartQuantity" param="element.quantity"/>
															<dsp:getvalueof id="wishListItemId" param="element.wishListItemId"/>
															<input type="hidden" name="prodID" class="frmAjaxSubmitData" value="${prodID}" />
															<input type="hidden" name="skuID" class="frmAjaxSubmitData" value="${skuID}" />
															<input type="hidden" name="cartQuantity" class="frmAjaxSubmitData" value="${cartQuantity}" />
															<input type="hidden" name="cartWishListItemId" class="frmAjaxSubmitData" value="${wishListItemId}" />
															<input type="hidden" name="iCount" class="frmAjaxSubmitData" value="${currentCount}" />
															<input type="hidden" name="registryId" class="frmAjaxSubmitData" value="${registryId}" />


                                                            <c:set var="mveToCart" value="${false}"/>
															 <c:if test="${(empty priceMessageVO) or ((not empty priceMessageVO) and (priceMessageVO.inStock))}">
																<c:set var="mveToCart" value="${true}"/>
																<li>
																	<div class="button fr marBottom_10">
                                                                        <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                                                            <dsp:param name="value" bean="/atg/commerce/ShoppingCart.current.commerceItems" />
                                                                            <dsp:oparam name="false">
                                                                                <input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${prodID};;;;eVar30=${skuID}|eVar15=Wish List','scAdd')" aria-pressed="false" role="button" />
                                                                            </dsp:oparam>
                                                                            <dsp:oparam name="true">
                                                                                <input name="addItemToOrder" type="button" data-ajax-frmID="frmSaveForLater" value="MOVE TO CART" class="btnAjaxSubmitSFL moveToCart" onclick="additemtoorder(';${prodID};;;;eVar30=${skuID}|eVar15=Wish List','scOpen,scAdd')" aria-pressed="false" role="button" />
                                                                            </dsp:oparam>
                                                                        </dsp:droplet>
																	</div>
																	<div class="clear"></div>
																</li>
															</c:if>
																		<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
																		<c:if test="${bopusoff and (defaultCountry eq 'US' || defaultCountry eq 'Canada') and (not mveToCart)}">
	                                                                        <li>
	                                                                            <div class="button fr marBottom_10">
																					<dsp:getvalueof id="id" param="element.wishListItemId" />
																					<input type="hidden" value="${prodID}" data-dest-class="productId" class="productId" data-change-store-submit="productId" name="productId">
																					<input type="hidden" value="${skuID}" data-dest-class="skuId" class="changeStoreSkuId" data-change-store-submit="skuId" name="skuId">
																					<input id="quantity" type="hidden" data-dest-class="itemQuantity" class="itemQuantity" data-change-store-submit="itemQuantity" value="${quantity}"  name="itemQuantity">
																					<input type="hidden" name="count" value="${currentCount}" data-dest-class="count" data-change-store-submit="count" />
																					<input type="hidden" name="pageURL" value="${contextPath}/cart/ajax_handler_cart.jsp" data-change-store-submit="pageURL"/>
																					<c:if test="${not empty registryId}">
																						<input type="hidden" value="${registryId}" data-dest-class="registryId" class="registryId" data-change-store-submit="registryId" name="registryId">
																					</c:if>
																					
																					<input type="hidden" name="check" value="check" data-dest-class="check" data-change-store-submit="check" />
																					<input type="hidden" name="wishlistItemId" value="${id}" data-dest-class="wishlistItemId" data-change-store-submit="wishlistItemId" />
	                                                                                <input type="button" class="changeStore" value="Find in Store" role="button" aria-pressed="false" />
	                                                                            </div>
	                                                                            <div class="clear"></div>
	                                                                        </li>
                                                                       </c:if>
																			<li>
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
																										<input class="btnAjaxSubmitSFL moveToReg hidden" id="btnMoveToRegSel${wishListItemId}" type="button" value="Move to Registry" onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}');" data-ajax-frmID="frmRegSaveForLater" role="button" aria-labelledby="btnMoveToRegSel${wishListItemId}" aria-pressed="false" />
																								</div>
																								</div>
																								<div class="clear"></div>

																								<div class="clear"></div>
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
																															<dsp:getvalueof var="regId"
																																param="futureRegList.registryId" />
																																<dsp:getvalueof var="registryName"
																																param="futureRegList.eventType" />
																															<input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
																															<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="${regId}" />
																															<input  type="hidden"value="${registryName}" name="registryName" class="addItemToRegis omniRegName" />
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
																			<div class="clear"></div>
                                                                        </li>

                                                                    </ul>
                                                                    <div class="clear"></div>
                                                                </li>
                                                            </ul>
                                                            <div class="clear"></div>
                                                            <input type="submit" name="btnPickUpInStore" id="btnPickUpInStore" class="hidden pickUpInStoreSFL" value="PickUpInStore" role="button" aria-pressed="false" />
                                                        </div>
                                                        <div class="clear"></div>
                                                    <div class="clear"></div>
                                                </li>
                                            </dsp:oparam>
                                            <dsp:oparam name="outputEnd">
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </ul>
                                    <div class="clear"></div>
									</dsp:form>
                                </div>
                                <div class="clear"></div>
                            </div>
                        </dsp:oparam>
                    </dsp:droplet>
                    <div class="clear"></div>
                </div>
				<!-- Added for Story -156 |Cart Analyzer |@psin52-->
				<c:if test="${not cart_one_column_layout_enable}">
				<div id="certonaSlots">
				<dsp:include page="/cart/subCart.jsp">
					<dsp:param name="CertonaContext" value="${CertonaContext}"/>
					<dsp:param name="RegistryContext" value="${RegistryContext}"/>
					<dsp:param name="shippingThreshold" value="${shippingThreshold}"/>
				</dsp:include>
				</div>
				</c:if>
                <c:import url="/selfservice/find_in_store.jsp" >
                    <c:param name="enableStoreSelection" value="true"/>
                </c:import>

                <c:import url="/_includes/modules/change_store_form.jsp" >
                    <c:param name="action" value="${contextPath}/_includes/modules/cart_store_pickup_form.jsp"/>
                </c:import>
				<%--  New version of view map/get directions --%>
				<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>

                <dsp:getvalueof var="itemInfos" bean="CartModifierFormHandler.items" />
                <script type="text/javascript">
                    function removeHandler(commerceItemId,productId){
                        document.getElementById('remove').value=commerceItemId;
                        document.getElementById('remove').click();
                    }
                    /* function changeToStorePickup(storeId,commerceItemId,oldShippingId,newQuantity){
                        document.getElementById('storeId').value=storeId;
                        document.getElementById('storeComId').value=commerceItemId;
                        document.getElementById('storeShipId').value=oldShippingId;
                        document.getElementById('storequantity').value=newQuantity;
                        document.getElementById('storeSubmit').click();
                    } */
                    function changeToShipOnline(commerceItemId,oldShippingId,newQuantity){
                        document.getElementById('onlineComId').value=commerceItemId;
                        document.getElementById('onlineShipId').value=oldShippingId;
                        document.getElementById('onlineQuantity').value=newQuantity;
                        document.getElementById('onlineSubmit').click();
                    }
                    function omniAddToRegis(elem, prodId, qty, itemPrice, skuId){
                        var wrapper = $(elem).closest('.savedItemRow'),
                            regData = wrapper.find('.omniRegData').find('option:selected'),
                            regId = (regData[0] ? regData.attr('value') : wrapper.find('.omniRegId').val()) || "",
                            regName = (regData[0] ? regData.attr('class') : wrapper.find('.omniRegName').val()) || "",
                            regProdString = ';' + prodId + ';;;event22=' + qty + '|event23=' + itemPrice + ';eVar30=' + skuId;

                        if (typeof addItemWishListToRegistry === "function") { addItemWishListToRegistry(regProdString, regName, regId); }
                    }
                </script>
                <c:if test="${YourAmigoON}">
                    <dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
					<c:if test="${isTransient eq false}">
                        <!-- ######################################################################### -->
                        <!--  Configuring the javascript for tracking signups (to be placed on the     -->
                        <!--  signup confirmation page, if any).                                       -->
                        <!-- ######################################################################### -->

                        <c:choose>
			                <c:when test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
			                    <script src="https://support.youramigo.com/52657396/tracev2.js"></script>
			                    <c:set var="ya_cust" value="52657396"></c:set>
			                </c:when>
			                <c:when test="${(currentSiteId eq TBS_BedBathUSSite)}">
			                    <script src="https://support.youramigo.com/73053126/trace.js"></script>
			                    <c:set var="ya_cust" value="73053126"></c:set>
			                </c:when>
			            </c:choose>
                        <script type="text/javascript">
                        /* <![CDATA[ */

                            /*** YA signup tracking code for Bed Bath & Beyond (www.bedbathandbeyond.com) ***/

                            // --- begin customer configurable section ---

                            ya_tid = Math.floor(Math.random()*1000000); // Set XXXXX to the ID counting the signup, or to a random
                                                  // value if you have no such id - eg,
                                                  // ya_tid = Math.random();
                            ya_pid = ""; // Set YYYYY to the type of signup - can be blank
                                                  // if you have only one signup type.

                            ya_ctype = "REG"; // Indicate that this is a signup and not a purchase.
                            // --- end customer configurable section. DO NOT CHANGE CODE BELOW ---

                            ya_cust = "${ya_cust}";
                            try { yaConvert(); } catch(e) {}

                        /* ]]> */
                        </script>
                    </c:if>
                </c:if>
                <c:if test="${TellApartOn}">
                    <bbb:tellApart actionType="updatecart"/>
                </c:if>
                <script type="text/javascript">
                    resx.appid = "${appIdCertona}";
                    resx.customerid = "${userId}";
                    resx.itemid = productIdsCertona;
                    resx.event = "shopping+cart";
                    resx.links = resx.links + resx.itemid;
                </script>
            </div>
            <%--DoubleClick Floodlight START --%>
		<dsp:droplet name="BBBOrderRKGInfo">
	        <dsp:param name="order" bean="ShoppingCart.current" />
	        <dsp:oparam name="output">
				<dsp:getvalueof var="rkgProductNames" param="rkgProductNames"/>
				<dsp:getvalueof var="rkgProductIds" param="rkgProductIds"/>
				<dsp:getvalueof var="rkgProductCount" param="rkgProductCount"/>
			</dsp:oparam>
			</dsp:droplet>
		<c:if test="${DoubleClickOn}">
    		  <c:if test="${(currentSiteId eq TBS_BedBathUSSite)}">
    		   <c:set var="cat"><bbbc:config key="cat_cart_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_2_bedBathUS" configName="RKGKeys" /></c:set>
    		 </c:if>
    		 <c:if test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
    		   <c:set var="cat"><bbbc:config key="cat_cart_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_2_baby" configName="RKGKeys" /></c:set>
    		 </c:if>
    		 <c:set var="orderPreTaxAmoutRKG"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${orderPreTaxAmout}" /></c:set>
    		 <c:set var="preTaxTotal" value="${fn:replace(orderPreTaxAmoutRKG,',','')}" />
			 		<dsp:include page="/_includes/double_click_tag.jsp">
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};qty=${rkgProductCount};cost=${preTaxTotal};u4=${rkgProductIds};u5=${rkgProductNames}"/>
			 		</dsp:include>
		 	</c:if>
		 	
		 	
	 		 <%--DoubleClick Floodlight END --%>
<script type="text/javascript">
function omniRemove(prodId, skuId){
if(typeof s !=='undefined') {
                        s.events="scRemove";
                        s.products=';'+prodId +";;;;eVar30=" + skuId;
                        var s_code=s.t();
                        if(s_code)document.write(s_code);
                    }
}
function omniAddToList(elem, prodId, skuId, qty){
   var totalPrice = $(elem).closest('.cartRow').find('.omniTotalPrice').text().trim().replace("$","");
   var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId;
   if (typeof additemtolist === "function") { additemtolist(finalOut); }
}
</script>


<%--Jsp body Ends here--%>
        </jsp:body>
        <jsp:attribute name="footerContent">
			<script type="text/javascript">
                if(typeof s !=='undefined') {
                    s.channel='Check Out';
                    s.pageName='Check Out>Full Cart';
                    s.prop1='Check Out';
                    s.prop2='Check Out';
                    s.prop3='Check Out';
                    s.events='scView';
                    s.prop6='${pageContext.request.serverName}';
                    s.eVar9='${pageContext.request.serverName}';
                    var s_code=s.t();
                    if(s_code)document.write(s_code);
                }
            </script>
        </jsp:attribute>
    </bbb:pageContainer>
</dsp:page>
