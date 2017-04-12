<dsp:page>

    <%-- Imports --%>
    <%@ page import="com.bbb.constants.BBBCheckoutConstants"%>
    <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
<%--     <dsp:droplet name="RepriceOrderDroplet"> --%>
<%--         <dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/> --%>
<%--     </dsp:droplet> --%>
    <dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>
    <dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
    <dsp:importbean bean="/atg/commerce/order/droplet/BBBContinueShoppingDroplet"/>
    <dsp:importbean bean="/atg/commerce/order/droplet/ShipQualifierThreshold" />
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
    <dsp:importbean bean="/atg/commerce/promotion/ClosenessQualifierDroplet" />
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
    <dsp:importbean bean="/com/bbb/commerce/droplet/TBSCouponsNotAppliedDroplet"/>
    <dsp:importbean bean="/atg/commerce/order/droplet/ValidateClosenessQualifier" />
    <dsp:importbean bean="/com/bbb/commerce/cart/BBBCartItemCountDroplet"/>
    <dsp:importbean bean="/atg/commerce/order/droplet/BBBCartDisplayDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/GroupCommerceItemsByShiptimeDroplet"/>
    <dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
    <dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/cart/droplet/DisplayExpressCheckout"/>
    <dsp:importbean bean="/com/bbb/commerce/cart/droplet/CheckFlagOffItemsDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSCheckCustomOrderDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/droplet/TBSAutoWaiveRestrictedStores"/>

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
    <c:set var="lbl_checkout_checkout" scope="page">
        <bbbl:label key="lbl_checkout_checkout" language="${pageContext.request.locale.language}" />
    </c:set>
    <c:set var="CertonaContext" value="" scope="request"/>
    <c:set var="RegistryContext" value="" scope="request"/>
    <c:set var="registryFlag" value="false"/>
    <c:set var="skuFlag" value="false"/>

    <dsp:setvalue value="false" bean = "PayPalSessionBean.fromPayPalPreview"/>

    <c:set var="language" value="<c:out param='${language}' />" />
    <c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
    <dsp:droplet name="TBSAutoWaiveRestrictedStores">
        <dsp:param name="order" bean="ShoppingCart.current"/>
    </dsp:droplet>

    <dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasErrorPersonalizedItemDroplet">
     <dsp:param name="order" bean="ShoppingCart.current"/>
     <dsp:oparam name="output">
        <dsp:getvalueof var="orderHasErrorPrsnlizedItem" param="orderHasErrorPrsnlizedItem" />
     </dsp:oparam>
    </dsp:droplet>

    <dsp:droplet name="BBBPaymentGroupDroplet">
        <dsp:param bean="ShoppingCart.current" name="order"/>
        <dsp:param name="serviceType" value="GetPaymentGroupStatusOnLoad"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="isOrderAmtCoveredVar" vartype="java.lang.String" param="isOrderAmtCovered" scope="request"/>
        </dsp:oparam>
    </dsp:droplet>

    <%-- paypal button depending upon its ltl flag or item in order --%>
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

    <%-- ERRORS --%>
    <dsp:getvalueof var="errorList" bean="PayPalSessionBean.errorList" />
    <c:set var="size" value="${fn:length(errorList)}"/>
    <c:if test="${size gt 0}">
        <div class="errorList">
            <dsp:droplet name="ForEach">
            <dsp:param name="array" value="${errorList}" />
                <dsp:oparam name="output">
                    <div class="payPalAddressError error">
                        <dsp:getvalueof var="error" param="element"/>
                        ${error}
                    </div>
                </dsp:oparam>
            </dsp:droplet>
        </div>
    </c:if>

    <dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
         <dsp:oparam name="output">
            <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
         </dsp:oparam>
    </dsp:droplet>

    <dsp:getvalueof param="payPalError" var="payPalError"/>
    <c:if test="${payPalError}">
        <div class="error"><bbbl:error key="err_paypal_get_service_fail" language="${pageContext.request.locale.language}" /></div>
    </c:if>

    <%--R2.2 PayPal Change: Display error message in case of webservice error : Start--%>
    <dsp:droplet name="ForEach">
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
    <p class="error">
        <dsp:droplet name="TBSCouponsNotAppliedDroplet">
            <dsp:param name="order" bean="ShoppingCart.current"/>
            <dsp:oparam name="output">
                <dsp:valueof param="promoName"/></br>
                <dsp:getvalueof param="error" var="err_msg_key"/>
                <bbbe:error key="${err_msg_key}" language="${pageContext.request.locale.language}" /></br>
            </dsp:oparam>
        </dsp:droplet>
    </p>
    
    <div id="cartBody">
		<div id="cartContentWrapper">
        <dsp:droplet name="IsEmpty">
            <dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
            <dsp:oparam name="true">

                <%-- Cart Top Message --%>
                <div id="cartTopMessaging">
                    <dsp:include page="topLinkCart.jsp" />
                </div>

                <%-- Empty Cart Message --%>
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
                                         <strong><bbbl:label key="lbl_cartdetail_emptycart" language="${language}"/></strong>&nbsp;&nbsp;
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
                                            <bbbl:textArea key="txt_cartdetail_empty_cart_msg" language="${language}" placeHolderMap="${emptyCartMsgLinks}"/>
                                        </p>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </dsp:oparam>
                        </dsp:droplet>

                    </div>
                </div>
            </dsp:oparam>
            <dsp:oparam name="false">

                <%-- Cart Top Message --%>
                <div id="cartTopMessaging">
                    <dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
                    <dsp:setvalue paramvalue="wishlist.giftlistItems" param="items" />
                    <dsp:include page="topLinkCart.jsp"/>
                    <c:if test="${orderHasErrorPrsnlizedItem}">
                      <div class="genericYellowBox savedItemChangedWrapper clearfix marBottom_10 marTop_10">
                         <h2 class="heading"><bbbl:label key="lbl_cart_top_personalized_err_message" language="${pageContext.request.locale.language}" /></h2>
                      </div>
                    </c:if>
                <div class="clear"></div>
					<dsp:droplet name="ValidateClosenessQualifier">
						<dsp:param name="order" bean="ShoppingCart.current" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="result" param="result" />
						</dsp:oparam>
					</dsp:droplet>
					<c:if test="${result}">
						<dsp:droplet name="ClosenessQualifierDroplet">
							<dsp:param name="type" value="shipping" />
							<dsp:param name="order" bean="ShoppingCart.current" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="closenessQualifier"
									param="closenessQualifiers" />
								<dsp:param name="qualifier" value="${closenessQualifier[0]}" />
								<dsp:getvalueof param="qualifier.name" var="promoName" />
								<dsp:getvalueof param="qualifier.upsellMedia.type"
									var="promoImageType" />
								<c:if test="${promoImageType ne 3 }">
									<dsp:getvalueof param="qualifier.upsellMedia.url"
										var="promoImage" />
									<div class="row">
										<div class="small-12 columns">
											<img class="stretch" alt="${promoName}" src="${promoImage}" />
										</div>
									</div>
								</c:if>
							</dsp:oparam>
						</dsp:droplet>
					</c:if>
				</div>

                <dsp:droplet name="ShipQualifierThreshold">
                    <dsp:param name="type" value="shipping" />
                    <dsp:param name="order" bean="ShoppingCart.current" />
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="shippingThreshold" param="shippingThreshold"/>
                    </dsp:oparam>
                </dsp:droplet>

                <div id="cartContent">

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

                    <dsp:form formid="cartForm" id="cartForm" method="post" action="ajax_handler_cart.jsp">

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
                                    <dsp:oparam name="output">
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

                                                <%-- this will determine if we display 'gift for registrant' dropdown or display search modal immediately --%>
                                                <c:set var="regMapEmpty" value="false" scope="request" />
                                                <div id="cart-items">
                                                <%-- display each item in the shipping group --%>
                                                <dsp:droplet name="ForEach">
                                                    <dsp:param name="array" param="groupedItemsList" />
                                                    <dsp:param name="elementName" value="commerceItem" />
                                                    <dsp:oparam name="output">

                                                        <c:set var="itemFlagoff" value="${false}"/>
                                                        <c:set var="disableLink" value="${false}"/>
                                                        <dsp:getvalueof var="commItem" param="commerceItem"/>
                                                        <dsp:getvalueof var="arraySize" param="size" />
                                                        <dsp:getvalueof var="currentCount" param="count" />

                                                        <c:if test="${commItem.BBBCommerceItem.commerceItemClassType eq 'tbsCommerceItem'}">
                                                            <dsp:getvalueof var="kirsch" value="${commItem.BBBCommerceItem.kirsch}"/>
                                                            <dsp:getvalueof var="cmo" value="${commItem.BBBCommerceItem.CMO}"/>
                                                            <dsp:getvalueof var="tbsItemInfo" value="${commItem.BBBCommerceItem.TBSItemInfo }"/>
                                                        </c:if>

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

                                                        <%-- do not show product link back to pdp for kirsch, cmo, upc --%>
                                                        <dsp:getvalueof var="count" param="count"/>
                                                        <dsp:param name="bbbItem" param="commerceItem.BBBCommerceItem.shippingGroupRelationships"/>
                                                        <dsp:getvalueof id="newQuantity" param="commerceItem.BBBCommerceItem.quantity"/>
                                                        <dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id"/>
                                                        <dsp:getvalueof id="productIdsCertona" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
                                                        <dsp:getvalueof var="commLtlShipMethod" param="commerceItem.BBBCommerceItem.ltlShipMethod"/>
                                                        <c:set var="ship_method_avl" value="${true}"/>

                                                        <dsp:droplet name="ForEach">
                                                            <dsp:param name="array" param="bbbItem"/>
                                                            <dsp:param name="elementName" value="shipGrp" />
                                                            <dsp:oparam name="output">
                                                                <dsp:getvalueof id="oldShippingId" param="shipGrp.shippingGroup.id"/>
                                                                <dsp:getvalueof var="shippingMethod" param="shipGrp.shippingGroup.shippingMethod"/>
                                                                <c:if test="${empty shippingMethod}">
                                                                    <c:set var="ship_method_avl" value="${false}"/>
                                                                    <c:set var="displayDeliverySurMayApply" value="${true}"/>
                                                                    <c:set var="shipmethodAvlForAllLtlItem" value="${false}"/>
                                                                </c:if>
                                                            </dsp:oparam>
                                                        </dsp:droplet>

                                                        <%-- cart item --%>
                                                        <div class="row cart-item ${lastRow} <c:if test="${not empty commItem.BBBCommerceItem.registryId}">registeryItem</c:if> cartRow changeStoreItemWrap <c:if test="${count eq 1}">firstItem</c:if>" id="cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" data-ship-group="${shiptime}">
                                                            <input type="hidden" class="overridQuantity" value="" />
                                                            <dsp:include page="itemLinkCart.jsp">
                                                                <dsp:param name="id" value="${commerceItemId}"/>
                                                                <dsp:param name="oldShippingId" value="${oldShippingId}"/>
                                                                <dsp:param name="newQuantity" value="${newQuantity}"/>
                                                                <dsp:param name="image" param="commerceItem.BBBCommerceItem.auxiliaryData.catalogRef.mediumImage"/>
                                                                <dsp:param name="displayName" value="${commItem.skuDetailVO.displayName}"/>
                                                                <dsp:param name="priceMessageVO" value="${commItem.priceMessageVO}"/>
                                                                <dsp:param name="enableKatoriFlag" value="${enableKatoriFlag}"/>
                                                                <dsp:param name="orderHasPersonalizedItem" value="${commItem.BBBCommerceItem.referenceNumber}"/>
                                                            </dsp:include>

                                                            <%-- LTL Alert --%>
                                                            <c:if test="${ not ship_method_avl && commItem.skuDetailVO.ltlItem && empty commLtlShipMethod}" >
                                                                <div class="small-12 columns           ltlItemAlert alert alert-info">
                                                                    <bbbt:textArea key="txt_cart_saved_item_alert" language="${language}"/>
                                                                </div>
                                                            </c:if>
                                                            <%-- LTL Alert --%>

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
                                                                <div class="small-12 columns registry-item-heading">
                                                                    <h3>
                                                                        <dsp:getvalueof bean="ShoppingCart.current.registryMap.${commItem.BBBCommerceItem.registryId}" var="registratantVO"/>
                                                                        <c:if test="${commItem.BBBCommerceItem.registryInfo ne null}">
                                                                            <bbbl:label key="lbl_cart_registry_from_text" language="${language}"/>
                                                                            <dsp:a href="${registryUrl}">
                                                                                <dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}"/>
                                                                                <dsp:param name="eventType" value="${registratantVO.registryType.registryTypeDesc}" />
                                                                                ${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/>
                                                                            </dsp:a>
                                                                            ${registratantVO.registryType.registryTypeDesc}&nbsp;<bbbl:label key="lbl_cart_registry_text" language="${language}"/>
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
                                                                            <c:if test="${itemFlagOff or disableLink or PdpUrlflag}">
                                                                                <c:set var="finalUrl" value="#"/>
                                                                            </c:if>

                                                                            <%-- image --%>
                                                                            <div class="small-6 columns">
                                                                                <div class="category-prod-img">
                                                                                    <c:choose>
                                                                                        <c:when test="${itemFlagoff or disableLink}">
                                                                                            <c:choose>
                                                                                                <c:when test="${empty image || 'null' eq image}">
                                                                                                    <img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <img src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </c:when>
                                                                                        <c:otherwise>

                                                                                            <c:choose>
                                                                                                <c:when test="${kirsch or cmo}">
                                                                                                     <img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <dsp:a iclass="prodImg" page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
                                                                                                    <c:choose>
                                                                                                        <c:when test="${not empty commItem.BBBCommerceItem.referenceNumber}">
                                                                                                            <c:choose>
                                                                                                                <c:when test="${not empty commItem.BBBCommerceItem.fullImagePath && !commItem.BBBCommerceItem.eximErrorExists && enableKatoriFlag}">
                                                                                                                    <img  src="${commItem.BBBCommerceItem.fullImagePath}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" height="146" width="146" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>
                                                                                                        </c:when>
                                                                                                        <c:otherwise>
                                                                                                            <c:choose>
                                                                                                                <c:when test="${empty image || 'null' eq image}">
                                                                                                                    <img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                    <img src="${scene7Path}/${image}" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
                                                                                                                </c:otherwise>
                                                                                                            </c:choose>
                                                                                                        </c:otherwise>

                                                                                                    </c:choose>
                                                                                                    </dsp:a>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
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
                                                                            <div class="small-6 columns">
                                                                                <c:choose>
                                                                                    <c:when test="${itemFlagoff or disableLink}">
                                                                                        <div class="product-name"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></div>
                                                                                        <c:choose>
                                                                                            <c:when test="${kirsch or cmo}">
                                                                                                <c:if test="${tbsItemInfo ne null}">
                                                                                                    <div class="facet">
                                                                                                        <dsp:valueof value="${tbsItemInfo.productDesc}" valueishtml="true"/>
                                                                                                    </div>
                                                                                                </c:if>
                                                                                            </c:when>
                                                                                            <c:otherwise>
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
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <c:choose>
                                                                                            <c:when test="${kirsch or cmo}">
                                                                                                <div class="product-name"><c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" /></div>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                 <div class="product-name">
                                                                                                        <dsp:a page="${finalUrl}?skuId=${commItem.BBBCommerceItem.catalogRefId}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}">
                                                                                                            <c:out value="${commItem.skuDetailVO.displayName}" escapeXml="false" />
                                                                                                        </dsp:a>
                                                                                                    </div>
                                                                                            </c:otherwise>
                                                                                        </c:choose>

                                                                                        <c:choose>
                                                                                            <c:when test="${kirsch or cmo}">
                                                                                                <c:if test="${tbsItemInfo  ne null}">
                                                                                                    <div class="facet">
                                                                                                        <dsp:valueof value="${tbsItemInfo.productDesc}" valueishtml="true"/>

                                                                                                    </div>
                                                                                                    <a href="#" id="collapsedFacet">Hide Info</a>
                                                                                                    <c:if test="${kirsch}">
                                                                                                    <div class="facet">
                                                                                                    ***This order is for Kirsch Custom Windows. Custom orders are not eligible for return.
                                                                                                    </div>
                                                                                                    </c:if>
                                                                                                </c:if>
                                                                                            </c:when>
                                                                                            <c:otherwise>
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
                                                                                <c:if test='${not empty commItem.BBBCommerceItem.personalizationOptions && commItem.BBBCommerceItem.personalizationOptions != null}'>
                                                                                   <c:set var="cusDet">${eximCustomizationCodesMap[pOpt]}</c:set>
                                                                                </c:if>
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
                                                                                              <a href="#" class='editPersonalization bold <c:if test="${isInternationalCustomer ||  not empty commItem.BBBCommerceItem.registryId}">disableText</c:if>' aria-label='<bbbl:label key="lbl_cart_edit_personalization_for" language="${language}"/> ${commItem.skuDetailVO.displayName}' title='<bbbl:label key="lbl_cart_personalization_edit" language="${language}"/>' data-custom-vendor="${commItem.vendorInfoVO.vendorName}" data-sku="${commItem.BBBCommerceItem.catalogRefId}" data-refnum="${commItem.BBBCommerceItem.referenceNumber}" data-quantity="${commItem.BBBCommerceItem.quantity}">
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
                                                            <div class="small-12 large-5 columns">
                                                                <div class="row">
                                                                    <div class="small-6 large-5 columns quantity">

                                                                        <c:choose>
                                                                            <c:when test="${kirsch}">
                                                                                <c:set var="qtyTitle">
                                                                                    <bbbl:label key='lbl_kirsch_qty_title' language='${pageContext.request.locale.language}'/>
                                                                                </c:set>
                                                                            </c:when>
                                                                            <c:when test="${cmo}">
                                                                                <c:set var="qtyTitle">
                                                                                    <bbbl:label key='lbl_cmo_qty_title' language='${pageContext.request.locale.language}'/>
                                                                                </c:set>
                                                                            </c:when>
                                                                        </c:choose>

                                                                        <%-- quantity input --%>
                                                                        <div class="qty-spinner quantityBox">
                                                                            <c:choose>
                                                                              <c:when test="${not (itemFlagoff)}">
                                                                                <c:choose>
                                                                                    <c:when test="${kirsch or cmo}">
                                                                                        <a class="button minus_disabled secondary" title="${qtyTitle}"><span></span></a>
                                                                                        <input data-max-value="99" name="${commItem.BBBCommerceItem.id}" type="text" value="${commItem.BBBCommerceItem.quantity}" id="quantity_text_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="quantity-input itemQuantity" maxlength="2" aria-required="true" aria-labelledby="lblquantity_text_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" disabled="disabled" />
                                                                                        <a class="button plus_disabled secondary" title="${qtyTitle}"><span></span></a>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <a class="button minus secondary" title="Decrease Quantity"><span></span></a>
                                                                                        <input data-max-value="99" name="${commItem.BBBCommerceItem.id}" type="text" value="${commItem.BBBCommerceItem.quantity}" id="quantity_text_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="quantity-input itemQuantity" maxlength="2" aria-required="true" aria-labelledby="lblquantity_text_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" />
                                                                                        <a class="button plus secondary" title="Increase Quantity"><span></span></a>
                                                                                    </c:otherwise>
                                                                                 </c:choose>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <input data-max-value="99" name="${commItem.BBBCommerceItem.id}" type="hidden" value="${commItem.BBBCommerceItem.quantity}" id="quantity_text_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="quantity-input itemQuantity" maxlength="2"/>
                                                                                </c:otherwise>
                                                                            </c:choose>                                                          
                                                                        </div>
                                                                        <dsp:getvalueof id="outOfStockCartLinks" value="${priceMessageVO.inStock}" />
                                                                        <%-- quantity actions --%>
                                                                        <div class="qty-actions">
                                                                            <%-- update --%>
                                                                            <c:if test="${not (itemFlagoff )}">
                                                                                <c:choose>
                                                                                    <c:when test="${(outOfStockCartLinks) && (kirsch or cmo)}">
                                                                                        <a href="#" class="qty-update disabled" title="<bbbl:label key='lbl_cartdetail_update' language='${language}'/>"><bbbl:label key="lbl_cartdetail_update" language="${language}"/></a>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <a href="#" class="qty-update" data-submit-button="#btnUpdate${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" data-parent-row="#cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" title="<bbbl:label key='lbl_cartdetail_update' language='${language}'/>"><bbbl:label key="lbl_cartdetail_update" language="${language}"/></a>
                                                                                        <dsp:input type="hidden" bean="CartModifierFormHandler.commerceItemId" value="${commItem.BBBCommerceItem.id}" />
                                                                               <dsp:input type="hidden" bean="CartModifierFormHandler.setOrderSuccessURL" value="${contextPath}/cart/ajax_handler_cart.jsp" />
                                                                                        <dsp:input type="hidden" bean="CartModifierFormHandler.setOrderErrorURL" value="${contextPath}/cart/ajax_handler_cart.jsp" />

                                                                                        <dsp:input name="btnUpdate" id="btnUpdate${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" type="submit" bean="CartModifierFormHandler.setOrderByCommerceId" iclass="hidden" />
                                                                                    </c:otherwise>
                                                                                </c:choose>

                                                                            </c:if>
                                                                            <%--</c:if>--%>
                                                                            <%-- remove --%>
                                                                            <dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
                                                                            <a href="#" class="remove-item" data-ajax-frmID="frmCartItemRemove" data-parent-row="#cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" onclick="omniRemove('${productId}','${commItem.BBBCommerceItem.catalogRefId}')" title="<bbbl:label key='lbl_cartdetail_remove' language='${language}'/>"><bbbl:label key="lbl_cartdetail_remove" language="${language}"/></a>

                                                                            <%-- save for later --%>
                                                                            <c:if test="${not (itemFlagoff)}">
                                                                                <c:choose>
                                                                                    <c:when test="${(outOfStockCartLinks) && (kirsch or cmo)}">
                                                                                        <a href="#" class="save-item-disabled disabled" title="Save for Later">Save for Later</a>
                                                                                    </c:when>
                                                                                  
                                                                                  <c:when test='${not empty cusDet}'>
                                                                                    <a href="#" class="save-item btnAjaxSubmitCart" data-ajax-frmID="frmCartSaveForLater" data-parent-row="#cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" onclick="omniAddToListPers(this, '${productId}', '${commItem.BBBCommerceItem.catalogRefId}', '${commItem.BBBCommerceItem.quantity}','${cusDet}');" title="Save for Later">Save for Later</a>
                                                                                  </c:when>
                                                                                   <c:otherwise>
                                                                                       <a href="#" class="save-item btnAjaxSubmitCart" data-ajax-frmID="frmCartSaveForLater" data-parent-row="#cartItemID_${commItem.BBBCommerceItem.id}${commItem.BBBCommerceItem.registryId}" onclick="omniAddToList(this, '${productId}', '${commItem.BBBCommerceItem.catalogRefId}', '${commItem.BBBCommerceItem.quantity}')" title="Save for Later">Save for Later</a>
                                                                                    </c:otherwise>
                                                                                </c:choose>

                                                                            </c:if>
                                                                           <%-- </c:if>--%>
                                                                        </div>

                                                                    </div>

                                                                    <div class="small-6 large-7 columns shipping">

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
                                                            <input type="hidden" name="saveCount" class="frmAjaxSubmitData" value="${count}" />
                                                            <input type="hidden" name="saveQuantity" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.quantity}" />
                                                            <input type="hidden" name="saveCurrentItemId" class="frmAjaxSubmitData" value="${commItem.BBBCommerceItem.id}" />
															<input type="hidden" name="saveCurrentItemShipTime" class="frmAjaxSubmitData" value="${shiptime}" />
																
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

                                                        </div>

                                                    </dsp:oparam>
                                                </dsp:droplet>
                                                <%-- for each cart item --%>
                                                </div>
                                               
                                            </dsp:oparam>
                                        </dsp:droplet>
                                        <%-- End of ForEach Droplet --%>
                                    </dsp:oparam>
                                </dsp:droplet>
                                <%-- End of GroupCommerceItemsByShiptimeDroplet --%>
                            </dsp:oparam>
                        </dsp:droplet>
						<%-- End of BBBCartDisplayDroplet --%>
						
                        <%-- price override modal inputs --%>
                        <dsp:input type="hidden" bean="CartModifierFormHandler.overrideId" id="commerceItemId" value="" />
                        <dsp:input type="hidden" bean="CartModifierFormHandler.overridePrice" id="overridePrice" value="" />
                        <dsp:input type="hidden" bean="CartModifierFormHandler.overrideQuantity" id="overrideQuantity" value="0" />
                        <dsp:input type="hidden" bean="CartModifierFormHandler.reasonCode" id="reasonCode" value="" />
                        <dsp:input type="hidden" bean="CartModifierFormHandler.competitor" id="competitor" value="" />
                        <dsp:input type="submit" bean="CartModifierFormHandler.itemPriceOverride" id="overrideSubmit" iclass="hidden" value="Override"/>

                    </dsp:form>
                    <%-- End of cartForm --%>
                    
                    <%-- Registry un assign form --%>
                    <dsp:form action="${contextPath}/cart/cart.jsp" method="post" id="unselect" formid="unselect">
                        <dsp:input bean="CartModifierFormHandler.commerceItemId" type="hidden" value="${commerceId}" name="registryCommerceId" />
                        <dsp:input bean="CartModifierFormHandler.unassignRegistry" id="unassignRegistry" type="submit" value="true" iclass="hidden" />
                         <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="cartBodyUnassignReg" />

                    </dsp:form>
                    <dsp:form action="${contextPath}/cart/cart.jsp" method="post" id="assigntoRegistry">
                        <dsp:input bean="CartModifierFormHandler.commerceItemId" type="hidden" value="${commerceId}" name="registryCommerceId"/>
                        <dsp:input bean="CartModifierFormHandler.registryId" type="hidden" value="${registryId}" name="assignRegistryId"/>
                        <dsp:input bean="CartModifierFormHandler.quantity" type="hidden" value="1"/>
                        <dsp:input bean="CartModifierFormHandler.splitAndAssignRegistry" id="keepInRegistry" type="submit" value="Assign" iclass="hidden" />
                        <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="cartBodyUnassignReg" />

                    </dsp:form>

                </div>
                <%-- End of  cartContent Div --%>    
                    
                    <%-- delivery charge override override modal inputs --%>
                    <dsp:include page="/checkout/overrides/deliveryChargeOverride.jsp" flush="true">
                    </dsp:include>

                    <%-- Assembly fee override override modal inputs --%>
                    <dsp:include page="/checkout/overrides/assemblyFeeOverride.jsp" flush="true">
                    </dsp:include>

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
                        <div class="small-12 large-8 columns">
                            <%-- recommendations --%>
                        </div>
                        <div class="small-12 large-4 columns">

                            <div class="row">
                                <div class="small-12 columns">
                                    <dl class="totals">
										
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

                                                <div class="dl-wrap">
                                                    <dt>
                                                        <h3>
                                                            <c:out value="${cartCount}" />
                                                            <bbbl:label key="lbl_cartdetail_items" language="${language}"/>
                                                        </h3>
                                                    </dt>
                                                    <dd><h3><fmt:formatNumber value="${storeAmount + onlinePurchaseTotal}"  type="currency"/></h3></dd>
                                                </div>

                                                <c:choose>
                                                    <c:when test="${freeShipping ne true}">
                                                        <div class="dl-wrap">
                                                            <c:choose>
                                                                <c:when  test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
                                                                    <dt><bbbl:label key="lbl_cartdetail_estimatedshipping" language="${language}"/>
                                                                        <dsp:include page="/common/shipping_method_description.jsp"/>
                                                                        <a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping">
                                                                            (<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                                                                        </a>
                                                                    </dt>
                                                                    <dd>TBD</dd>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:choose>
                                                                        <c:when test="${kirsch or cmo}">
                                                                            <dt><bbbl:label key='lbl_shipping_charges' language='${pageContext.request.locale.language}'/>
                                                                            </dt>
                                                                            <dd><fmt:formatNumber value="0"  type="currency"/>*</dd>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                    <dt><bbbl:label key="lbl_cartdetail_estimatedshipping" language="${language}"/>
                                                                        <dsp:include page="/common/shipping_method_description.jsp"/>
                                                                        <a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping">
                                                                            (<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                                                                        </a>
                                                                    </dt>
                                                                            <dd><fmt:formatNumber value="${rawShippingTotal}"  type="currency"/>*</dd>
                                                                </c:otherwise>
                                                            </c:choose>
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

                                                        <c:choose>
                                                            <c:when test="${kirsch or cmo}">
                                                            </c:when>
                                                            <c:otherwise>
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
                                                                        <dd class="savings">(<fmt:formatNumber value="${shippingSavings}"  type="currency"/>)</dd>
                                                            </c:if>
                                                         </c:otherwise>
                                                        </c:choose>

                                                    </c:when>
                                                    <c:otherwise>
                                                        <%-- LTL changes --%>
                                                        <div class="dl-wrap">
                                                            <c:choose>
                                                                <c:when test="${orderHasLTLItem eq true && rawShippingTotal eq 0.0}" >
                                                                    <dt><bbbl:label key="lbl_cartdetail_estimatedshipping" language="${language}"/>
                                                                        <a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping">
                                                                            (<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                                                                        </a>
                                                                    </dt>
                                                                    <dd>TBD</dd>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <dt class="savings"><bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/>
																	</dt>
                                                                    <dd class="savings">
                                                                        <fmt:formatNumber value="${shippingSavings}"  type="currency"/>
                                                                    </dd>
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
                                                        <dd>
                                                            <a href="#" class="shipSurchargeOverrideModal" data-reveal-id="shipSurchargeOverrideModal_1">
                                                                <dsp:valueof value="${totalSurcharge}" converter="currency"/>
                                                            </a>
                                                        </dd>
                                                    </div>
                                                </c:if>

                                                <c:if test="${surchargeSavings gt 0.0}">
                                                    <div class="dl-wrap">
                                                        <dt class="savings"><bbbl:label key="lbl_surchage_savings" language="${language}"/></dt>
                                                        <dd class="savings">(<fmt:formatNumber value="${surchargeSavings}"  type="currency"/>)</dd>
                                                    </div>
                                                </c:if>

                                                <dsp:getvalueof var="ecoFeeTotal" value="${storeEcoFeeTotal + onlineEcoFeeTotal}"/>
                                                <c:if test="${ecoFeeTotal gt 0.0}">
                                                    <div class="dl-wrap">
                                                        <dt><bbbl:label key="lbl_preview_ecofee" language="${language}"/></dt>
                                                        <dd><fmt:formatNumber value="${ecoFeeTotal}"  type="currency"/></dd>
                                                    </div>
                                                </c:if>

                                                <%-- Additional info for LTL items summary --%>
                                                <c:if test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
                                                    <div class="dl-wrap">
                                                        <dt class="surcharge">
                                                            <bbbl:label key="ltl_delivery_surcharge_may_apply" language="${language}"/>
                                                            <a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping">
                                                                (<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                                                            </a>
                                                        </dt>
                                                        <dd class="surcharge">TBD</dd>
                                                    </div>
                                                </c:if>

                                                <c:if test ="${totalDeliverySurcharge gt 0.0 && shipmethodAvlForAllLtlItem}">
                                                    <div class="dl-wrap">
                                                        <dt class="surcharge">
                                                            <bbbl:label key="lbl_cart_delivery_surcharge" language="${language}"/>:
                                                            <a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping">
                                                                (<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                                                            </a>
                                                        </dt>
                                                        <dd class="surcharge">
                                                            <fmt:formatNumber value="${totalDeliverySurcharge}"  type="currency"/>
                                                        </dd>
                                                    </div>
                                                </c:if>

                                                <c:if test ="${maxDeliverySurchargeReached}">
                                                    <div class="dl-wrap">
                                                        <c:choose>
                                                            <c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
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
                                                                <dd class="savings">(-<fmt:formatNumber value="${totalDeliverySurcharge - maxDeliverySurcharge}"  type="currency"/>)</dd>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </c:if>

                                                <c:if  test ="${totalAssemblyFee gt 0.0}">
                                                    <div class="dl-wrap">
                                                        <dt><h3><bbbl:label key="lbl_cart_assembly_fee" language="${language}"/></h3></dt>
                                                        <dd><h3><fmt:formatNumber value="${totalAssemblyFee}"  type="currency"/></h3></dd>
                                                    </div>
                                                </c:if>

                                                <%-- Additional info for LTL items summary --%>
                                                <dsp:getvalueof var="preTaxAmout" value="${orderPreTaxAmout + storeAmount + storeEcoFeeTotal }"/>
                                                <div class="dl-wrap">
                                                    <dt><h1 class="price"><bbbl:label key="lbl_cartdetail_pretaxtotal" language="${language}"/></h1></dt>
                                                    <dd><h1 class="price"><fmt:formatNumber value="${preTaxAmout}"  type="currency"/></h1></dd>
                                                </div>

                                                <c:if test="${totalSavedAmount gt 0.0}">
                                                    <c:choose>
                                                        <c:when test="${kirsch or cmo}">
                                                            <c:set var="totalSavedAmount" value="${totalSavedAmount - shippingSavings}"></c:set>
                                                            <c:if test="${totalSavedAmount gt 0.0}">
                                                    <div class="dl-wrap">
                                                                    <dt class="savings"><bbbl:label key="lbl_cartdetail_totalsavings" language="${language}"/></dt>
                                                                    <dd class="savings"><fmt:formatNumber value="${totalSavedAmount}"  type="currency"/></dd>
                                                                </div>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="dl-wrap">
                                                        <c:choose>
                                                            <c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
                                                                <dt class="savings"><bbbl:label key="lbl_cartdetail_totalsavings" language="${language}"/></dt>
                                                                <dd class="savings">TBD</dd>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <dt class="savings"><bbbl:label key="lbl_cartdetail_totalsavings" language="${language}"/></dt>
                                                                        <dd class="savings"><fmt:formatNumber value="${totalSavedAmount}"  type="currency"/></dd>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                                <dsp:include page="cart_includes/surchargeOverride.jsp">
                                                    <dsp:param name="fromCart" value="true"/>
                                                    <dsp:param name="order" bean="ShoppingCart.current" />
                                                </dsp:include>

                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </dl>

                                    <p class="p-footnote">
                                        <bbbl:label key="lbl_cartdetail_shippinginfo" language="${language}"/>
                                        <%-- TODO: update this in the BCC to point to /tbs instead of /store --%>
                                        <bbbl:label key="lbl_cartdetail_viewshippingcostslink" language="${language}"/>
                                    </p>

                                </div>
                            </div>
                            <dsp:form formid="express_form" id="express_form" method="post" action="${contextPath}/cart/cart.jsp">

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
                                                <c:choose>
                                                    <c:when test="${cmo}">
                                                        <c:set var="disable">true</c:set>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="disable">false</c:set>
                                                    </c:otherwise>
                                                </c:choose>
                                                <label class="inline-rc checkbox <c:if test="${cmo}">disabled</c:if>" for="express_checkout">
                                                    <dsp:input id="express_checkout" disabled="${disable}" bean="CartModifierFormHandler.expressCheckout" type="checkbox" name="expire_checkout">
                                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                                    </dsp:input>
                                                    <span></span>
                                                    <h3><bbbl:label key="lbl_cartdetail_expresscheckout" language="${language}"/></h3>
                                                </label>
                                                <p class="p-footnote"><bbbl:label key="lbl_cartdetail_gotoorderreview" language="${language}"/></p>
                                                <c:if test="${cmo}">
                                                    <p class="p-footnote">Express Check Out is not available for Custom Made Orders. Please use regular Check Out.</p>
                                                </c:if>
                                                </dsp:oparam>
                                            </dsp:droplet>

                                        </c:if>
                                        <c:if test="${cartCount > 1 && not cmo && not kirsch}">
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
                                        <span class="<c:if test='${isMultishipCheckout}'>multishipcheckout</c:if>"> </span>
                                        
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

                                <dsp:droplet name="TBSCheckCustomOrderDroplet">
                                    <dsp:param name="order" bean="ShoppingCart.current"/>
                                    <dsp:oparam name="output">
                                        <dsp:getvalueof param="kirsch" var="kirsch"/>

                                    </dsp:oparam>
                                </dsp:droplet>

                                    <c:choose>
                                        <c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock) and not orderHasErrorPrsnlizedItem}">
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

                                    <%-- #Scope 83 A : Start R2.2 pay pal button dependeding upon its flag in bcc --%>
                                    <div class="small-6 columns">
                                        <dsp:getvalueof var="orderType" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
                                        <c:if test="${paypalCartButtonEnable}">
                                            <c:choose>
                                                <c:when test="${paypalOn}">
                                                    <c:choose>
                                                        <c:when test="${orderHasLTLItem eq true || (registryFlag eq true)}">
                                                            <div class="paypalCheckoutContainer paypal_disabled ">
                                                                <bbbt:textArea key="txt_paypal_disable_image_new" language ="${pageContext.request.locale.language}"/>
                                                            </div>
                                                           
                                                            <div class=" smallText bold highlightRed"><bbbl:label key="lbl_paypal_not_available_ltl_or_reg_product" language="${pageContext.request.locale.language}" /></div>

                                                        </c:when>
                                                        <c:when test="${cmo}">
                                                            <div class="paypalCheckoutContainer paypal_disabled ">
                                                                <bbbt:textArea key="txt_paypal_disable_image_new" language ="${pageContext.request.locale.language}"/>
                                                            </div>
                                                           
                                                        </c:when>
                                                        <c:when test="${(orderType eq 'BOPUS_ONLY') or (orderType eq 'HYBRID')  }">
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
                                                                                <bbbl:label key="lbl_paypal_bill_me_later" language="${pageContext.request.locale.language}" />
                                                                            </div>
                                                                            <div class="error ">
                                                                                <bbbe:error key="err_paypal_zero_balance" language ="${pageContext.request.locale.language}"/>
                                                                            </div>
                                                                        </c:when>
                                                                     
                                                                        <c:otherwise>
                                                                        <div class="paypalCheckoutContainer">
                                                                            <dsp:a href="" >
                                                                                <dsp:property bean="CartModifierFormHandler.fromCart" value="${true}" priority="8"/>
                                                                                <dsp:property bean="CartModifierFormHandler.payPalErrorURL" value="/cart/cart.jsp" priority="9"/>
                                                                                <dsp:property bean="CartModifierFormHandler.checkoutWithPaypal" value="true" priority="0"/>

                                                                            </dsp:a>
                                                                            <img src="<bbbt:textArea key="txt_paypal_image_cart" language = "${pageContext.request.locale.language}"/>" class="paypalCheckoutButton">
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

                                    <c:if test="${not (showCheckOutBtn and itemNotOOS and (not isOutOfStock))}">
                                        <div class="small-12 columns">
                                            <span class="error"><bbbl:label key="lbl_cart_outofstockmessage" language="${language}"/></span>
                                        </div>
                                    </c:if>
                                <c:if test="${internationalShippingOn}">
                                    <div class="small-12 columns">
                                        <c:choose>
                                            <c:when test="${showCheckOutBtn and itemNotOOS and (not isOutOfStock)}">
                                                <c:choose>
                                                    <c:when test="${isOrderAmtCoveredVar}">
                                                        <div class="expand button secondary small" disabled>
                                                            <bbbl:label key="lbl_chkout_international_shipping" language="${language}" />
                                                            <img src="/_assets/global/images/international_shipping_help_icon.png" alt="International Shipping Help" />
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
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
                                                <c:set var="pageCopy">
                                                <bbbt:textArea key="txt_vbv_logo_visa" language="${language}"/>
                                                </c:set>
                                                <c:set var="pageCopyNew" value="${fn:replace(pageCopy, '/store','/tbs')}" />
                                                ${pageCopyNew}
                                            </c:if>
                                            <c:if test="${visaLogoOn && masterCardLogoOn}">
                                                <span class="visaLogoSep"></span>
                                            </c:if>
                                            <c:if test="${masterCardLogoOn}">
                                                <c:set var="pageCopy">
                                                <bbbt:textArea key="txt_vbv_logo_masterCard" language="${language}"/>
                                                </c:set>
                                                <c:set var="pageCopyNew" value="${fn:replace(pageCopy, '/store','/tbs')}" />
                                                ${pageCopyNew}
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
                <%-- End of  cartItemsTotalAndCouponsWrapper Div --%>
                
                	</dsp:oparam>
        		</dsp:droplet>
       </div>
        		
        <dsp:getvalueof var="applicationId" bean="Site.id" />
        <dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
		<%-- Save For Later Section --%>
		<dsp:include page="/account/idm/idm_login_checkout.jsp" />
		<div id="sflContentWrapper" class="loadlazyContent" data-ajax-url="/tbs/cart/cartBodysfl.jsp" data-ajax-params-count="6" data-ajax-param1-name="language" data-ajax-param1-value="${pageContext.request.locale.language}" data-ajax-param2-name="CertonaContext" data-ajax-param2-value="${CertonaContext}" data-ajax-param3-name="RegistryContext" data-ajax-param3-value="${RegistryContext}" data-ajax-param4-name="shippingThreshold" data-ajax-param4-value="${shippingThreshold}" data-ajax-param5-name="applicationId" data-ajax-param5-value="${applicationId}"   data-ajax-param6-name="appIdCertona" data-ajax-param6-value="${appIdCertona}">
		</div>
		      <%-- regMapEmpty true means display search for registry immediately --%>
              <input type="hidden" id="regMapEmpty" value="<c:out value='${regMapEmpty}'/>" />
       </div>
       <%-- End of CartBody div --%>

       <%-- quantity for registrant modal --%>
       <div id="qtyForRegistrant" class="reveal-modal medium" data-reveal></div>

       <%-- search for registrant modal --%>
       <div id="searchForRegistrant" class="reveal-modal xlarge reg-search" data-reveal></div>

     <div id="nearbyStore" class="reveal-modal" data-reveal>
    </div>
    <dsp:include page="/cart/cart_includes/store_pickup_form.jsp"/>

<script type="text/javascript">
        $(document).ready(function(){
            $(".nearby-stores").attr("data-reveal-id","nearbyStore");
            $(".nearby-stores").attr("data-reveal-ajax","true");
            $(document).foundation('reflow');
        	if($("#tBSCheckout").hasClass("stockAvailable")){
                $("#tBSCheckout").removeAttr("disabled")
            };
        
        });
    </script>
</dsp:page>
