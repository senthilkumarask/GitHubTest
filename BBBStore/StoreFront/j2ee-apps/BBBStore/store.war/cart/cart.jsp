<dsp:page>
    <%@ page import="com.bbb.constants.BBBCheckoutConstants"%>
	<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
    <dsp:importbean bean="/com/bbb/commerce/cart/TopStatusChangeMessageDroplet" />
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBOrderRKGInfo" />
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>
	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
    <dsp:importbean bean="/atg/commerce/order/droplet/BBBContinueShoppingDroplet"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet"/>
    <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
    <dsp:importbean bean="/atg/commerce/order/droplet/ShipQualifierThreshold" />
     <dsp:importbean bean="/atg/commerce/order/droplet/ValidateClosenessQualifier" />
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
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
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/com/bbb/internationalshipping/formhandler/InternationalShipFormHandler" />
	<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" bean="ShoppingCart.current.priceInfo"/>
	<dsp:getvalueof var="eximWebserviceFailure" bean="ShoppingCart.current.eximWebserviceFailure"/>
	<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
	<dsp:getvalueof id="appliedCouponMap" bean="CartModifierFormHandler.appliedCouponMap.${couponId}" scope="session"/>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblSaveThe"><bbbl:label key="lbl_accessibility_save_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblFuturePurchase"><bbbl:label key="lbl_accessibility_future_purchase" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="countryCode">${valueMap.defaultUserCountryCode}</c:set>
	<c:set var="currencyCode">${valueMap.defaultUserCurrencyCode}</c:set>
	<c:set var="isEximWebServiceErrInResp">true</c:set>
	<dsp:getvalueof var="reqFrom" param="reqFrom" />
<c:if test="${empty reqFrom}">
	<c:set var="reqFrom" value="merge"/>
</c:if>
	<jsp:useBean id="currentDate" class="java.util.Date"/>
	<fmt:formatDate value="${currentDate}" var="currentDate" pattern="MM/dd/yyyy"/>
	<c:set var="repriceCart" scope="request">
		<bbbc:config key="reprice_cart" configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
	<c:set var="prodImageAttrib" scope="request">class="fl productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
	<c:if test="${disableLazyLoadS7Images eq true}">
        <c:set var="prodImageAttrib" scope="request">class="fl productImage noImageFound" src</c:set>        
    </c:if>

	<c:if test="${eximWebserviceFailure}"> 
		<dsp:droplet name="/com/bbb/commerce/cart/droplet/GetPersonalizedDetailsDroplet">
			<dsp:param bean="ShoppingCart.current" name="pOrder"/>
			<dsp:oparam name="output">
	       <dsp:getvalueof var="isEximWebServiceErrInResp" param="isEximWebServiceErr" />
	     </dsp:oparam>
		</dsp:droplet>
	</c:if>

	<c:if test="${orderPriceInfo == null or repriceCart or (!isEximWebServiceErrInResp and eximWebserviceFailure)}">  
		<dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
			<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
		</dsp:droplet>
		
	</c:if>
	
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
    <dsp:getvalueof var="cartState" value="${0}"/>
    
    <c:if test="${currentState ne cartState}">
        <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="CART"/>
    </c:if>

	<c:set var="enableLTLRegForSite">
				<bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" />
			</c:set>
    <dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
    <dsp:getvalueof id="applicationId" bean="Site.id" />
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
    <dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
    <c:set var="lbl_checkout_checkout" scope="page">
        <bbbl:label key="lbl_checkout_checkout" language="${pageContext.request.locale.language}" />
    </c:set>
    <c:set var="lbl_cartdetail_movetowishList" scope="page">
        <bbbl:label key="lbl_cartdetail_movetowishList" language="${pageContext.request.locale.language}" />
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
	
	<dsp:droplet name="/com/bbb/commerce/cart/droplet/SinglePageCktEligible">
		<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="singlePageEligible" param="singlePageEligible"/>
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
	 <bbb:pageContainer>
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
		
        <jsp:attribute name="section">cartDetail</jsp:attribute>
        <jsp:attribute name="PageType">CartDetails</jsp:attribute>
        <jsp:attribute name="pageWrapper">wishlist cartDetail useMapQuest useCertonaAjax useStoreLocator</jsp:attribute>
        <jsp:body>
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

            <dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
                <dsp:param name="order" bean="ShoppingCart.current"/>
            </dsp:droplet>

            <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
            <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
            <c:set var="isStockAvailability" value="yes" scope="request"/>

            <dsp:getvalueof var="transientUser" bean="Profile.transient"/>
			 <%-- Added for Story -156 |Cart Analyzer |@psin52--%>
			
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
                                <ul class="hidden grid_2 alpha share omega clearfix">
                                    <li class="clearfix"><a href="#" class="print avoidGlobalPrintHandler" title="<bbbl:label key="lbl_cartdetail_print" language="<c:out param='${language}'/>"/>" id="printCart"><bbbl:label key="lbl_cartdetail_print" language="<c:out param='${language}'/>"/></a></li>
                                    <li class="clearfix"><a href="#" class="email avoidGlobalEmailHandler" title="<bbbl:label key="lbl_cartdetail_emailcart" language="<c:out param='${language}'/>"/>" id="openEmailCart"><bbbl:label key="lbl_cartdetail_emailcart" language="<c:out param='${language}'/>"/></a></li>
                                </ul>
								<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
									<dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
									<dsp:oparam name="true">
										<c:set var="topCheckoutBtnClass" value="hidden"/>
									</dsp:oparam>
									<dsp:oparam name="false">
										<c:set var="topCheckoutBtnClass" value=""/>
									</dsp:oparam>
								</dsp:droplet>
								<dsp:droplet name="/com/bbb/commerce/cart/droplet/CheckFlagOffItemsDroplet">
									<dsp:param name="checkOOSItem" value="${true}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="showCheckOutBtn" param="flagOffChecked"/>
										<dsp:getvalueof var="itemNotOOS" param="itemOutOfStock"/>
									</dsp:oparam>
								</dsp:droplet>
									<div id="topCheckoutButton" class="padTop_5 topCheckoutButton ${topCheckoutBtnClass}">
										<dsp:form name="express_form" method="post" action="${contextPath}/cart/cart.jsp">
											<div class="clearfix">
											<dsp:include page="spcEligibility.jsp"></dsp:include>	
											<dsp:droplet name="/com/bbb/commerce/cart/droplet/SinglePageCktEligible">
												<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="singlePageEligible" param="singlePageEligible"/>
												</dsp:oparam>
											</dsp:droplet>
											<dsp:getvalueof id="shipGrpCount" bean="ShoppingCart.current.shippingGroupCount" />
												<c:choose>
													<c:when test="${showCheckOutBtn and itemNotOOS and not orderHasPersonalizedItem and not orderHasErrorPrsnlizedItem}">													
													<c:choose>
														<c:when test="${not singlePageEligible }">
														<div class="button button_active button_active_orange">
															<input class="triggerSubmit" data-submit-button="botCheckoutBtn" type="submit" value="${lbl_checkout_checkout}" disabled="disabled" role="button" />
														</div>
														</c:when>
														<c:otherwise>
														<div class="button button_active button_active_orange button_disabled singlePageCheckout">
																<input class="triggerSubmit" data-submit-button="botCheckoutBtn" type="submit" value="${lbl_checkout_checkout}"  role="button" />
															</div>
														</c:otherwise>
													</c:choose>
													</c:when>
													<c:otherwise>
														<div class="button button_active button_active_orange button_disabled">
															<input class="triggerSubmit" data-submit-button="botCheckoutBtn" type="submit" value="${lbl_checkout_checkout}" disabled="disabled" role="button" />
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
								<dsp:include page="/common/click2chatlink.jsp">
									<dsp:param name="pageId" value="3" />
                                    <dsp:param name="divApplied" value="${true}"/>
                                    <dsp:param name="divClass" value="padLeft_20 fr"/>
								</dsp:include>
                            </div><!--#cartHeader-->
							<%--156 Cart analyzer savita--%>
						<dsp:droplet name="BBBPackNHoldDroplet">
								<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
								<dsp:oparam name="output">
									<dsp:getvalueof param="hasSingleCollegeItem" var="hasSingleCollegeItem"/>
									<dsp:getvalueof param="hasAllPackNHoldItems" var="hasAllPackNHoldItems"/>
								</dsp:oparam>
						</dsp:droplet>
						<dsp:droplet name="BBBBeddingKitsAddrDroplet">
							<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
							<dsp:param name="isPackHold" value="${true}"/> 
							<dsp:oparam name="beddingKit">
							<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
								<c:set var="beddingKit" value="true"/>
							</dsp:oparam>
							<dsp:oparam name="weblinkOrder">
								<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
								<c:set var="weblinkOrder" value="true"/>
							</dsp:oparam>
							<dsp:oparam name="notBeddingKit">
								<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
								<c:set var="beddingKit" value="false"/>
							</dsp:oparam>
						</dsp:droplet>
						<c:choose>
							<c:when test="${currentSiteId eq 'BedBathCanada'}">
								<fmt:parseDate value="${beddingShipAddrVO.shippingEndDate}" var="endDate" pattern="dd/MM/yyyy" />
								<fmt:formatDate value="${endDate}" var="endDate" type="date" pattern="MM/dd/yyyy"/>
							</c:when>
							<c:otherwise>
								<fmt:formatDate value="${beddingShipAddrVO.shippingEndDate}" var="endDate" pattern="MM/dd/yyyy"/>
							</c:otherwise>
						</c:choose>
						<c:if test="${hasSingleCollegeItem && !hasAllPackNHoldItems && endDate gt currentDate}">
							<div class="shopNowContainer">
								<h3 class="shopNowMsg"><bbbl:label key="lbl_items_for_snsl_eligibility_title" language="<c:out param='${language}'/>"/></h3>
								<span><bbbt:textArea key="txt_cart_for_snsl_eligibility_message_" language="${pageContext.request.locale.language}" /></span>
							</div>
						</c:if>
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
									<dsp:getvalueof var="errorVal" bean="CartModifierFormHandler.errorMap.${errorCode}"/>
									<c:if test="${!fn:containsIgnoreCase(errorVal, 'ERROR_CART_MAX_REACHED')}">
										<dsp:getvalueof var="errorMsg" bean="CartModifierFormHandler.errorMap.${errorCode}"/>
									</c:if>
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
                            	<dsp:droplet name="TopStatusChangeMessageDroplet">
								<dsp:param name="reqType" value="${reqFrom}" />
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
								</div>
								</c:if>
								
								
								<dsp:include page="topLinkCart.jsp">
								  <dsp:param name="priceMessageVoList" value="${priceMessageVoList}"/>
								</dsp:include>
								
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
								</div>
								</c:if>
								<c:if test="${orderHasErrorPrsnlizedItem}">
									<div id="saveForLaterTopMessaging" class="clearfix grid_10 alpha omega">
							          <div class="genericRedBox savedItemChangedWrapper clearfix closeWinWrapper marBottom_10 changeStoreItemWrap">
								         <h2 class="heading"><bbbl:label key="lbl_cart_top_personalized_err_message" language="${pageContext.request.locale.language}" /></h2>
									   </div>
									</div>
								</c:if>
								<dsp:include page="topLinkCart.jsp">
								 <dsp:param name="priceMessageVoList" value="${priceMessageVoList}"/>
								</dsp:include>
								
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
                                            <!-- Handling IE 11 behavior as it takes height according to width if src is missing. -->
                                            <c:if test="${empty promoImage || !(fn:startsWith(promoImage, 'http://') || fn:startsWith(promoImage, '/'))}">
                                            	<c:set var="bannerWidth" value=""/>
                                            </c:if>
                                            <img class="bannerIMG" alt="${promoName}" src="${promoImage}" width="${bannerWidth}"/>
                                        </c:if>
										<dsp:droplet name="ShipQualifierThreshold">
											<dsp:param name="closenessQualifier" value="${closenessQualifier}"/>
											<dsp:param name="order" bean="ShoppingCart.current" />
											<dsp:oparam name="output">
											<dsp:getvalueof var="shippingThreshold" param="shippingThreshold"/>
                                    </dsp:oparam>
                                </dsp:droplet>
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
									<dsp:getvalueof var="recommSkuVO" bean="/Constants.null" vartype="com.bbb.commerce.browse.vo.RecommendedSkuVO" />
								  </c:if>
							  </dsp:oparam>
							</dsp:droplet>
							</c:if>
			          
                            <div id="cartContent" class="clearfix grid_10 alpha omega productListWrapper">
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
                                        <dsp:getvalueof var="favStoreStockStatus" param="favStoreStockStatus"/>
										<dsp:getvalueof var="storeDetails" param="storeDetails"/>
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
													
													<c:if test="${commItem.stockAvailability ne 0 && commItem.stockAvailability ne 2}">
															<c:set var="isOutOfStock" value="${true}"/>
													</c:if>
													<dsp:getvalueof id="priceMessageVO" value="${commItem.priceMessageVO}" />
													<c:if test="${not empty priceMessageVO}">
														<c:set var="itemFlagoff" value="${priceMessageVO.flagOff}"/>
														<c:set var="disableLink" value="${priceMessageVO.disableLink}"/>
													</c:if>
													<dsp:getvalueof var="webOffered" value="${commItem.skuDetailVO.webOfferedFlag}"/>
													
													<dsp:getvalueof id="isIntlRestricted" value="${commItem.skuDetailVO.intlRestricted}"/>
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
													<%-- LTL Alert  --%>
                                                    <c:if test="${ not ship_method_avl && commItem.skuDetailVO.ltlItem && empty commLtlShipMethod}" >
														<li>													
														
																<div class="clear"></div>
																<div class="ltlMessage ltlMessagePadding">
																<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>
															</div>
															
															
															<div  class="ltlMessage ltlAlertmessage" >
																	
																	<bbbl:label key='lbl_rlp_ltl_registry_message' language="${pageContext.request.locale.language}" />
																	<%--<bbbt:textArea key="txt_cart_saved_item_alert" language="<c:out param='${language}'/>"/>--%>
																</div>
															
														</li>	
													</c:if>
													<%-- LTL Alert  --%>
													<%--BBBSL-5887 | passing  registry ID to filter out of stock messages --%>
												    <li id="cartItemID_${commItem.BBBCommerceItem.id}" class=" ${lastRow} <c:if test="${not empty commItem.BBBCommerceItem.registryId}">registeryItem</c:if> clearfix cartRow changeStoreItemWrap <c:if test="${count eq 1}">firstItem</c:if>">
														<dsp:include page="itemLinkCart.jsp">
															<dsp:param name="id" value="${commerceItemId}"/>
															<dsp:param name="oldShippingId" value="${oldShippingId}"/>
															<dsp:param name="newQuantity" value="${newQuantity}"/>
															<dsp:param name="image" param="commerceItem.BBBCommerceItem.auxiliaryData.catalogRef.mediumImage"/>
															<dsp:param name="displayName" value="${commItem.skuDetailVO.displayName}"/>
															<dsp:param name="priceMessageVO" value="${commItem.priceMessageVO}"/>
															<dsp:param name="isIntlRestricted" value="${commItem.skuDetailVO.intlRestricted}"/>
															<dsp:param name="orderHasPersonalizedItem" value="${commItem.BBBCommerceItem.referenceNumber}"/>
															<dsp:param name="enableKatoriFlag" value="${enableKatoriFlag}"/>
															<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}"/>
														</dsp:include>
															
														
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
                                                            <c:set var="isMyRegistry" value="${false}"/>
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
                                                                    <dsp:getvalueof bean="ShoppingCart.current.registryMap.${commItem.BBBCommerceItem.registryId}" var="registratantVO"/>
                                                                    <c:if test="${commItem.BBBCommerceItem.registryInfo ne null}">
                                                                        <span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>
                                                                        <dsp:getvalueof var="registryInfo" param="commerceItem.BBBCommerceItem.registryInfo"/>
																		<dsp:a href="${registryUrl}">
																			<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}"/>
																			<dsp:param name="eventType" value="${registratantVO.registryType.registryTypeDesc}" />
																			<dsp:param name="inventoryCallEnabled" value="${true}" />
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
																		<dsp:getvalueof var="skuUpc" value="${commItem.skuDetailVO.upc}" />
																		<dsp:getvalueof var="skuColor" value="${commItem.skuDetailVO.color}" />
																		<dsp:getvalueof var="skuSize"  value="${commItem.skuDetailVO.size}" />
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
                                                                                                       <c:when test="${not empty commItem.BBBCommerceItem.referenceNumber}">
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
                                                                                                    <img ${prodImageAttrib}="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
                                                                                                         </c:otherwise>
                                                                                                        </c:choose>
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
																										<br> ${eximCustomizationCodesMap[pOpt]} :  <c:out value="${commItem.BBBCommerceItem.personalizationDetails}" escapeXml="false" />
																									</c:if>
																								</span>
																								<c:if test="${commItem.skuDetailVO.giftWrapEligible  && not isInternationalCustomer}">
																				                    <span class="prodAttribsCart"><bbbl:label key="lbl_cartdetail_elegibleforgiftpackaging" language="<c:out param='${language}'/>"/>&#32;<bbbt:textArea key="txt_giftpackaging_popup_link" language="<c:out param='${language}'/>"/></span>																				                  
																				                </c:if>
																								
																								 <c:if test='${not empty commItem.BBBCommerceItem.referenceNumber && commItem.BBBCommerceItem.referenceNumber != null && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag}'>
																								<div class="personalizationAttr katoriPrice">
																								<%--BBBSL-8154 --%>
		                                                                                            <%-- <span  aria-hidden="true">${commItem.BBBCommerceItem.personalizationOptionsDisplay}</span> --%>
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
																											<div class="priceAddText">	
																												<bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
																										    </div>
																										     </c:when>
																									    </c:choose>  
																									</div>
                                                                                                </div>
	                                                                                            <div class="personalizeLinks" data-personalization="${eximCustomizationCodesMap[pOpt]}">
																								<dsp:getvalueof var="prodId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
	                                                                                                <a href="#" class="editPersonalization bold <c:if test="${isInternationalCustomer ||  not empty commItem.BBBCommerceItem.registryId}">disableText</c:if>" aria-label="<bbbl:label key="lbl_cart_edit_personalization_for" language="<c:out param='${language}'/>"/> ${commItem.skuDetailVO.displayName}" title="<bbbl:label key="lbl_cart_personalization_edit" language="<c:out param='${language}'/>"/>" data-custom-vendor="${commItem.vendorInfoVO.vendorName}"	data-product="${prodId}" data-sku="${commItem.BBBCommerceItem.catalogRefId}" data-refnum="${commItem.BBBCommerceItem.referenceNumber}" data-quantity="${commItem.BBBCommerceItem.quantity}">
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
																							      <c:when test="${commItem.skuDetailVO.ltlItem && not empty shippingMethod && commItem.BBBCommerceItem.whiteGloveAssembly}">
																								       <c:set var="url">${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}&sopt=LWA</c:set>	
																								 </c:when>																							
																								  <c:when test="${commItem.skuDetailVO.ltlItem && not empty shippingMethod && (!commItem.BBBCommerceItem.whiteGloveAssembly)}">
																								       <c:set var="url">${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}&sopt=${shippingMethod}</c:set>	
																								 </c:when>
																								 <c:otherwise>
																								   	   <c:set var="url">${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}</c:set>		
																								 </c:otherwise>
																								</c:choose>	
																								<dsp:a iclass="prodImg padLeft_10 block fl" page="${url}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
																							<%-- end :: BPSI-407 | DSK -Remember LTL Service Option if user clicks LTL item from cart page, save for later section--%>
																									<c:choose>
                                                                                                       <c:when test="${not empty commItem.BBBCommerceItem.referenceNumber}">
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
                                                                                                    <img ${prodImageAttrib}="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
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
																								 <c:if test='${enableKatoriFlag && not empty commItem.BBBCommerceItem.referenceNumber && commItem.BBBCommerceItem.referenceNumber != null}'>
																								<div class="personalizationAttr katoriPrice">
																									<c:if test='${not empty commItem.BBBCommerceItem.personalizationOptions && commItem.BBBCommerceItem.personalizationOptions != null}'>
																										<%--BBBSL-8154 --%>
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
																								<c:if test='${!commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag && isMyRegistry}'>
																									<div class="personalizeLinks" data-personalization="${eximCustomizationCodesMap[pOpt]}">
																									<dsp:getvalueof var="prodId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
																										<a href="#" class="editPersonalization bold <c:if test="${isInternationalCustomer ||  not empty commItem.BBBCommerceItem.registryId}">disableText</c:if>" title="<bbbl:label key="lbl_cart_personalization_edit" language="<c:out param='${language}'/>"/>" aria-label="<bbbl:label key="lbl_cart_edit_personalization_for" language="<c:out param='${language}'/>"/> ${commItem.skuDetailVO.displayName}" data-custom-vendor="${commItem.vendorInfoVO.vendorName}" data-product="${prodId}" data-sku="${commItem.BBBCommerceItem.catalogRefId}" data-refnum="${commItem.BBBCommerceItem.referenceNumber}" data-quantity="${commItem.BBBCommerceItem.quantity}">
																										<bbbl:label key="lbl_cart_personalization_edit" language="<c:out param='${language}'/>"/></a>
																										<span class="seperator"></span>
																										
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
                                                                            <script type="text/javascript">
                                                                                eventTypeCertona = eventTypeCertona + 'shopping+cart' + ';';
                                                                            </script>
                                                                        </c:if>

                                                                        <%-- 	PORCH display
																			Conditions to check for:
																			* is global porch config on?
																			* is porch cart config on
																			* is this product service eligible?
																			* does this item have a service attached?
																		--%>
												
										                            	<c:if test="${not isInternationalCustomer}">
																			<dsp:droplet name="Switch">
																				<dsp:param name="value" value="${commItem.BBBCommerceItem.porchService}"/>
																				<dsp:oparam name="true">
																					<c:set var="gotPorchEligibleItem" value="${true}" />	
																					<div class="porchWrapper">									
																						<div class="porchServiceAdded">
																							<%-- this should come from the commerce item data --%>
																							<span class="serviceType"> ${commItem.BBBCommerceItem.porchServiceType}</span>
															
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

																									<script type="text/javascript">														
																										var params${commItem.BBBCommerceItem.catalogRefId} = {
																											fontFamily: " Arial, Helvetica, sans-serif",
																											fontSize: "14px",
																											fontColor: "#444",
																											fontWeight: "normal",
																											querySelector: '#porch-widget-body${commItem.BBBCommerceItem.catalogRefId}',
																											serviceFamilyCode: "${porchServiceTypeCode}", <%--get from  data --%>
																											partnerSku: "${commItem.BBBCommerceItem.catalogRefId}",
																											productName: '${fn:escapeXml(commItem.skuDetailVO.displayName)}',
																											//productUrl: "${pageURL}", <%-- need current domain --%>
																											postalCode: '${preferredShippingZipCode}',
																											learnMoreText: '${lbl_bbby_porch_service_learn_more}',
																											interface: 'cart' ,
																											site: '${currentSiteId}'
																										};											
																									</script>
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
																					<label id="lblquantity_text_${count}" class="hidden" for="quantity_text_${count}"><bbbl:label key="lbl_cartdetail_quantity" language="<c:out param='${language}'/>"/> ${commItem.BBBCommerceItem.quantity} of ${commItem.skuDetailVO.displayName}</label>
                                                                                     <div class="spinner" style="height: 32px;">
								
								<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
								<input  name="${commItem.BBBCommerceItem.id}" type="text" id="quantity_text_${count}"  value="${commItem.BBBCommerceItem.quantity}" title="${commItem.BBBCommerceItem.quantity} of ${commItem.skuDetailVO.displayName}" class="itemQuantity fl" name="qty" role="textbox" maxlength="2"  aria-required="true" aria-labelledby="lblquantity_text_${count}"/>
						 		</div>
																				</td>
																				</tr>
																				
																				<tr class="lnkUpdate">
																				<td>
																					<a href="javascript:void(0);" role="link" aria-label="Update quantity ${lblForThe} ${commItem.skuDetailVO.displayName}" class="triggerUpdate" data-submit-button="btnUpdate${commItem.BBBCommerceItem.id}"><span class="txtOffScreen">Update Quantity of ${commItem.skuDetailVO.displayName}&nbsp.</span><strong><bbbl:label key="lbl_cartdetail_update" language="<c:out param='${language}'/>"/></strong></a>
																					<dsp:input type="hidden" bean="CartModifierFormHandler.commerceItemId" value="${commItem.BBBCommerceItem.id}"/>
																					<dsp:input type="hidden" bean="CartModifierFormHandler.setOrderSuccessURL" value="/store/cart/ajax_handler_cart.jsp"/>
																					<dsp:input type="hidden" bean="CartModifierFormHandler.setOrderErrorURL" value="/store/cart/ajax_handler_cart.jsp"/>
																					<dsp:input name="btnUpdate" id="btnUpdate${commItem.BBBCommerceItem.id}" type="submit" bean="CartModifierFormHandler.setOrderByCommerceId" iclass="hidden" />
																				 </td>
																				</tr>
																			</c:when>
																			<c:otherwise>
																					<input name="${commItem.BBBCommerceItem.id}" type="hidden" value="${commItem.BBBCommerceItem.quantity}" id="quantity_text_${count}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="itemQuantity" maxlength="2"/>
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
                                                                                <a href="#" class="btnAjaxSubmitCart" data-ajax-frmID="frmCartSaveForLater" onclick="omniAddToList(this, '${productId}', '${commItem.BBBCommerceItem.catalogRefId}', '${commItem.BBBCommerceItem.quantity}');"><span class="txtOffScreen">Save ${commItem.skuDetailVO.displayName}&nbsp for a later purchase&nbsp. </span><strong>Save for Later</strong></a>
                                                                                </td>
                                                                            </tr>
																		</c:if>
									  </tbody>									</tbody>
                                                                        </table>
                                                                    </div>

                                                                    <%-- Display shipping details --%>
                                                                    <div class="cartDeliveryDetails clearfix itemCol" aria-hidden="false">
                                                                        <dsp:include page="/cart/cart_includes/shippingType.jsp">
																			<dsp:param name="commItem" value="${commItem}"/>
																			<dsp:param name="favStoreStockStatus" value="${favStoreStockStatus}"/>
																			<dsp:param name="storeDetails" value="${storeDetails}"/>
																		</dsp:include>
                                                                    </div>

																	<%-- Display item price details --%>
                                                                    <dsp:include page="/cart/cart_includes/priceDisplay.jsp">
																		<dsp:param name="commItem" value="${commItem}"/>
																		<dsp:param name="orderObject" bean="ShoppingCart.current" />
																		<dsp:param name="enableKatoriFlag" value="${enableKatoriFlag}"/>
																	</dsp:include>
                                                                
                                                                <div class="clear"></div>
                                                               
                                                                <input type="hidden" name="updateCartInfoSemiColonSeparated" class="frmAjaxSubmitData" value="${intlRestrictedItems}" />
                                                                <input type="hidden" name="updateCartInfoSemiColonSeparatedOOS" class="frmAjaxSubmitData" value="${oosItems}" />
                                                                <input type="hidden" name="saveStoreId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.storeId}" />
                                                                <input type="hidden" name="saveBts" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.bts}" />
                                                                <input type="hidden" name="saveCount" class="frmAjaxSubmitData" value="${count}" />
                                                                <input type="hidden" name="saveQuantity" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.quantity}" />
                                                                <input type="hidden" name="saveCurrentItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />

                                                                <input type="hidden" name="removeCommerceItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
                                                                <input type="hidden" name="removeSubmitButton" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />

                                                                <input type="hidden" name="buyOffAssociationSkuId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
                                                                <input type="hidden" name="associateRegistryContextButton" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
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
                                                   <dsp:getvalueof var="recomSkuId" value="${recommSkuVO.recommSKUVO.parentProdId}"/>
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
								 <div id="leftCol" class="grid_8 alpha clearfix">
								 
								  <%-- Added for Story -156 |Cart Analyzer |@psin52--%>
									
									 <c:if test="${cart_one_column_layout_enable}">
                                     <div id="certonaSlots">
                                     <dsp:include page="/cart/subCart.jsp">
                                        <dsp:param name="CertonaContext" value="${CertonaContext}"/>
                                        <dsp:param name="RegistryContext" value="${RegistryContext}"/>
                                        <dsp:param name="shippingThreshold" value="${shippingThreshold}"/>
                                        <dsp:param name="recomSkuId" value="${recomSkuId}"/>
                                    </dsp:include>
									</div>
									</c:if>
													
									<input type="hidden" id="couponerrors" data-errormap ="<dsp:valueof bean="CartModifierFormHandler.jsonCouponErrors"/>" data-errorexist="<dsp:valueof bean="CartModifierFormHandler.formError"/>" />
				
                                    <dsp:droplet name="Switch">
										 <dsp:param name="value" bean="Profile.transient"/>
										 <dsp:oparam name="false">
											 <dsp:getvalueof id="bopusOnly" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
											 <c:choose>
												 <c:when test="${CouponOn}" >
													 <div id="couponsOnCart" class="clearfix" 
													 data-ajax-url="${contextPath}/cart/asyncCouponDisplay.jsp" 
													 data-ajax-target-divs="#couponsOnCart" 
													 data-ajax-params-count="3" 
													 data-ajax-param1-name="action" 
													 data-ajax-param1-value="${contextPath}/cart/cart.jsp" 
													 data-ajax-param2-name="cartCheck" 
													 data-ajax-param2-value="true" 
													 data-ajax-param3-name="orderHasErrorPrsnlizedItem" 
													 data-ajax-param3-value="${orderHasErrorPrsnlizedItem}" 
													 role="complementary">
                            							<div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                        							</div>
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
												<dsp:include page="/cart/cart_includes/cartPriceDisplay.jsp">
													<dsp:param name="displayDeliverySurMayApply" value="${displayDeliverySurMayApply}"/>
													<dsp:param name="shipmethodAvlForAllLtlItem" value="${shipmethodAvlForAllLtlItem}"/>
													<dsp:param name="orderHasLTLItem" value="${orderHasLTLItem}"/>
													<dsp:param name="orderHasErrorPrsnlizedItem" value="${orderHasErrorPrsnlizedItem}"/>
												</dsp:include>
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
												 <c:if test="${orderHasLTLItem ne true and (not isInternationalCustomer) and not orderHasErrorPrsnlizedItem }">
													 <dsp:droplet name="/com/bbb/commerce/cart/droplet/DisplayExpressCheckout">
														 <dsp:param name="order" bean="ShoppingCart.current"/>
														 <dsp:param name="profile" bean="Profile"/>
														 <dsp:oparam name="false">
															 <dsp:getvalueof var="disableExpressCheckout" param="disableExpressCheckout" />
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
															 <dsp:getvalueof var="disableExpressCheckout" param="disableExpressCheckout" />
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
												
											<dsp:input type="hidden" id="useSPC" bean="CartModifierFormHandler.singlePageCheckout" value="false" />
											<%--<input type="hidden" name="useSPC" id="useSPC" value="${singlePageEligible}" />--%>
												
											<dsp:include page="bottomCheckout_frag.jsp">
												<dsp:param name="showCheckOutBtn" value="${showCheckOutBtn}" />
												<dsp:param name="itemNotOOS" value="${itemNotOOS}" />
												<dsp:param name="isOutOfStock" value="${isOutOfStock}" />
												<dsp:param name="countryCode" value="${countryCode}" />
												<dsp:param name="currencyCode" value="${currencyCode}" />
												<dsp:param name="orderHasPersonalizedItem" value="${orderHasPersonalizedItem}" />
												<dsp:param name="orderHasErrorPrsnlizedItem" value="${orderHasErrorPrsnlizedItem}" />
												<dsp:param name="orderHasIntlResterictedItem" value="${orderHasIntlResterictedItem}" />
												<dsp:param name="singlePageEligible" value="${singlePageEligible}" />
												
												
												<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}" />
												<dsp:param name="paypalCartButtonEnable" value="${paypalCartButtonEnable}" />
												<dsp:param name="orderHasErrorPrsnlizedItem" value="${orderHasErrorPrsnlizedItem}" />
												<dsp:param name="paypalOn" value="${paypalOn}" />
												<dsp:param name="registryFlag" value="${registryFlag}" />
												<dsp:param name="orderHasLTLItem" value="${orderHasLTLItem}" />
												<dsp:param name="orderType" value="${orderType}" />
												<dsp:param name="showCheckOutBtn" value="${showCheckOutBtn}" />
												<dsp:param name="itemNotOOS" value="${itemNotOOS}" />
												<dsp:param name="isOutOfStock" value="${isOutOfStock}" />
												<dsp:param name="isOrderAmtCoveredVar" value="${isOrderAmtCoveredVar}" />
												
											</dsp:include>	

											
                                           
                                          
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
                            </div>
                            <div class="clear"></div>
                        </dsp:oparam>
                    </dsp:droplet>
                    <div class="clear"></div>
                    <dsp:getvalueof var="applicationId" bean="Site.id" />
							<dsp:getvalueof var="certonacon" bean="CertonaConfig.siteIdAppIdMap.${applicationId}" />
						<c:set var="applicationId" scope="request" value="${applicationId}"></c:set>
							<c:set var="appIdCertona" scope="request" value="${certonacon}"></c:set>
                    
                    <dsp:droplet name="/atg/commerce/order/droplet/BBBSaveForLaterDisplayDroplet">
		<dsp:param value="true" name="checkUserSavedItem" />
		<dsp:oparam name="output">
		<dsp:getvalueof var="userWithWishList" param="userWithWishList"/>
		<dsp:getvalueof var="countSavedItems" param="countSavedItems"/>
		<input type="hidden" name="countSavedItemsList" id="countSavedItemsList" value=${countSavedItems} />
		 	<c:choose>
	  <c:when test="${userWithWishList}">
		<div id="sflWrapper" class="clearfix loadlazyContent" data-ajax-url="/store/cart/cart_includes/saveForLater.jsp" data-ajax-params-count="6" data-ajax-param1-name="language" data-ajax-param1-value="${pageContext.request.locale.language}" data-ajax-param2-name="CertonaContext" data-ajax-param2-value="${CertonaContext}" data-ajax-param3-name="RegistryContext" data-ajax-param3-value="${RegistryContext}" data-ajax-param4-name="shippingThreshold" data-ajax-param4-value="${shippingThreshold}" data-ajax-param5-name="applicationId" data-ajax-param5-value="${applicationId}"   data-ajax-param6-name="appIdCertona" data-ajax-param6-value="${appIdCertona}"></div>
		</c:when>
		<c:otherwise>
		 <div id="saveForLaterHeader"
						class="clearfix grid_10 alpha omega hidden">
						<h2 class="account fl">
							<bbbl:label key="lbl_sfl_your_saved_items"
								language="<c:out param='${language}'/>" />
						</h2>
						<ul class="grid_2 alpha share omega clearfix">
							<li class="clearfix"><a href="#"
								class="print avoidGlobalPrintHandler" title="Print Saved Items"
								id="printSavedItems"><bbbl:label
										key="lbl_sfl_print_saved_items"
										language="<c:out param='${language}'/>" /></a></li>
							<li class="clearfix"><a href="#"
								class="email avoidGlobalEmailHandler" title="Email Saved Items"
								id="openEmailSavedItems"><bbbl:label
										key="lbl_sfl_email_saved_items"
										language="<c:out param='${language}'/>" /></a></li>
						</ul>
						<div class="clear"></div>
					</div>
					<div class="clear"></div>
					<div id="saveForLaterBody"
						class="savedItems clearfix grid_10 alpha omega hidden">
						<div id="saveForLaterEmptyMessaging"
							class="clearfix grid_10 alpha omega"></div>
					</div>
			</c:otherwise>		
		</c:choose>
		</dsp:oparam>
			</dsp:droplet>
                    
                    

              
                    <div class="clear"></div>
                </div>
				<%-- Added for Story -156 |Cart Analyzer |@psin52--%>
				<c:if test="${not cart_one_column_layout_enable}">
				<div id="certonaSlots">
				<dsp:include page="/cart/subCart.jsp">
					<dsp:param name="CertonaContext" value="${CertonaContext}"/>
					<dsp:param name="RegistryContext" value="${RegistryContext}"/>
					<dsp:param name="shippingThreshold" value="${shippingThreshold}"/>
					<dsp:param name="recomSkuId" value="${recomSkuId}"/>
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
                    /* function omniAddToRegis(elem, prodId, qty, itemPrice, skuId){
                        var wrapper = $(elem).closest('.savedItemRow'),
                            regData = wrapper.find('.omniRegData').find('option:selected'),
                            regId = (regData[0] ? regData.attr('value') : wrapper.find('.omniRegId').val()) || "",
                            regName = (regData[0] ? regData.attr('class') : wrapper.find('.omniRegName').val()) || "",
                            regProdString = ';' + prodId + ';;;event22=' + qty + '|event23=' + itemPrice + ';eVar30=' + skuId;

                        if (typeof addItemWishListToRegistry === "function") { addItemWishListToRegistry(regProdString, regName, regId); }
                    }
                     	function omniAddToRegisPers(elem, prodId, qty, itemPrice, skuId, cusDet){
                      		var wrapper = $(elem).closest('.savedItemRow'),
                          regData = wrapper.find('.omniRegData').find('option:selected'),
                          regId = (regData[0] ? regData.attr('value') : wrapper.find('.omniRegId').val()) || "",
                          regName = (regData[0] ? regData.attr('class') : wrapper.find('.omniRegName').val()) || "",
                          regProdString = ';' + prodId + ';;;event22=' + qty + '|event23=' + itemPrice + ';eVar30=' + skuId + '|eVar54=' + cusDet;

                      if (typeof addItemWishListToRegistry === "function") { addItemWishListToRegistry(regProdString, regName, regId,true); }
                  }*/
                </script>
                <%-- 
					if we got Porch eligible that DON'T have service attached
                	call the Porch script with a callback that renders the widget 

                	if we got Porch eligible that have service attached
                	lazyload the porch script
                --%> 
                <c:choose>
	                <c:when test="${not empty porchCallbackString}">
	                    <script type="text/javascript">
			                $(function() {				
								bbbLazyLoader.js('/_assets/global/js/porch.js', function(){${porchCallbackString}});
							});
		                </script>
	                </c:when>
	                <c:when test="${gotPorchEligibleItem}">
	                    <script type="text/javascript">
			                $(function() {
								bbbLazyLoader.js('/_assets/global/js/porch.js');
							});
		                </script>
	                </c:when>
	            </c:choose>
                
                <c:if test="${YourAmigoON}">
                    <dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
					<c:if test="${isTransient eq false}">
                        <%-- ######################################################################### --%>
                        <%--  Configuring the javascript for tracking signups (to be placed on the     --%>
                        <%--  signup confirmation page, if any).                                       --%>
                        <%-- ######################################################################### --%>

                        <c:choose>
			                <c:when test="${(currentSiteId eq BuyBuyBabySite)}">
			                    <script src="https://support.youramigo.com/52657396/tracev2.js"></script>
			                    <c:set var="ya_cust" value="52657396"></c:set>
			                </c:when>
			                <c:when test="${(currentSiteId eq BedBathUSSite)}">
			                    <script src="https://support.youramigo.com/73053126/trace.js"></script>
			                    <c:set var="ya_cust" value="73053126"></c:set>
			                </c:when>
							<c:when test="${(currentSiteId eq BedBathCanadaSite)}">
			                    <script src="https://support.youramigo.com/73053127/tracev2.js"></script>
			                    <c:set var="ya_cust" value="73053127"></c:set>
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
                <%--BBBSL-7222 | Adding frequently bought together  item to exclusion list of Certona --%>
                <script type="text/javascript">
               		 var recomSkuId ='${recomSkuId}';
                    resx.appid = "${appIdCertona}";
                    resx.customerid = "${userId}";
                    resx.itemid = productIdsCertona +';' ;
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
				<dsp:getvalueof var="OrderTaxRkg" param="RKG_PRETAX_TOTAL"/>
			</dsp:oparam>
			</dsp:droplet>
		<%--  <c:if test="${DoubleClickOn}">
    		  <c:if test="${(currentSiteId eq BedBathUSSite)}">
    		   <c:set var="cat"><bbbc:config key="cat_cart_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_2_bedBathUS" configName="RKGKeys" /></c:set>
    		 </c:if>
    		 <c:if test="${(currentSiteId eq BuyBuyBabySite)}">
    		   <c:set var="cat"><bbbc:config key="cat_cart_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_2_baby" configName="RKGKeys" /></c:set>
    		 </c:if>
    		  <c:if test="${(currentSiteId eq BedBathCanadaSite)}">
				  <c:set var="cat"><bbbc:config key="cat_cart_bedbathcanada" configName="RKGKeys" /></c:set>
				  <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
				  <c:set var="type"><bbbc:config key="type_2_bedbathcanada" configName="RKGKeys" /></c:set>
			</c:if> 
			 		<dsp:include page="/_includes/double_click_tag.jsp">
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};qty=${rkgProductCount};cost=${OrderTaxRkg};u4=${rkgProductIds};u5=${rkgProductNames}"/>
			 		</dsp:include>
		 	</c:if>
		 	
		 	 --%>
	 		 <%--DoubleClick Floodlight END --%>
			 <dsp:getvalueof var="Id" bean="Profile.id"/> 
			<input type="hidden" name="profileId" value="${Id}"/>  
<c:if test="${not empty commItem.BBBCommerceItem.personalizationOptions}">
<c:set var="personalizationMessage">${eximCustomizationCodesMap[pOpt]}</c:set>
<input type="hidden" name="personalizationMessageCart" id="personalizationMessageCart" value="${personalizationMessage}"/> 
</c:if>			
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
   var totalPrice = $(elem).closest('.cartRow').find('.omniTotalPriceHidden').val();
   var persDone = '';
    var personalizationMessage = $('input[name=personalizationMessageCart]').val();
   if(typeof personalizationMessage !== 'undefined'){
	var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId+'|eVar54='+personalizationMessage;
	persDone = true;
   }
   else{
	var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId;
   }
   
   if (typeof additemtolist === "function") { additemtolist(finalOut,persDone); }
}
function omniAddToListPers(elem, prodId, skuId, qty, personalizationMessageCart){
   var totalPrice = $(elem).closest('.cartRow').find('.omniTotalPriceHidden').val();
   var persDone = '';
   if(personalizationMessageCart){
	var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId+'|eVar54='+personalizationMessageCart;
	persDone = true;
   }
   else{
	var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId;
   }
   
   if (typeof additemtolist === "function") { additemtolist(finalOut,persDone); }
}
</script>
	<c:set var="sessionIdForSPC" value="${pageContext.session.id}" scope="session" />

<%--Jsp body Ends here--%>
        </jsp:body>
        <jsp:attribute name="footerContent">
        
        <c:set var="productIds" scope="request"/>
		<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
			<dsp:param name="order" bean="ShoppingCart.current"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="productIds" param="commerceItemList" />
			</dsp:oparam>     
		</dsp:droplet>
			    
			<script type="text/javascript">
                if(typeof s !=='undefined') {
                    s.channel='Check Out';
                    s.pageName='Check Out>Full Cart';
                    s.prop1='Check Out';
                    s.prop2='Check Out';
                    s.prop3='Check Out';
					//if('${countryCode}'  == 'undefined' || '${countryCode}'  == '' || '${countryCode}' === 'US')
						s.events='scView';
					//	else
					//	s.events='scCheckout,event8';
                    s.products = '${productIds}';
                    s.prop6='${pageContext.request.serverName}';
                    s.eVar9='${pageContext.request.serverName}';
                    var s_code=s.t();
                    if(s_code)document.write(s_code);
                }
            </script>
        </jsp:attribute>
    </bbb:pageContainer>
</dsp:page>
