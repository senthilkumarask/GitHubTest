<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <%-- this variable needs to hold how many products are there in the cart --%>
    <%-- scope is set as session because the same variable is used in "order_items.jsp" --%>
    <c:set var="cartItemsCount" scope="session"><dsp:valueof bean="ShoppingCart.current.commerceItemCount"/></c:set>
	<dsp:getvalueof var="isInternationalUser" bean="SessionBean.internationalShippingContext" />
	<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" bean="ShoppingCart.current.priceInfo"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <%-- BBBH-3558  hideOrderSubtotal is set to hide order sub total on mini cart if cart contains an item having in cart price --%>
    <%-- BBBH-6359,6360   Fix to show price on mini cart for in-cart items added--%>
	<dsp:getvalueof var="fromRegOwnerName" param="fromRegOwnerName"/>
	<c:if test="${empty fromRegOwnerName}">
		<dsp:getvalueof var="fromRegOwnerName" value="false"/>	
	</c:if>
	<c:set var="hideOrderSubtotal" value="false" scope="request"/>
    
    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
        <dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
        <dsp:oparam name="false">
        <c:if test="${orderPriceInfo == null}">  
		<dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
			<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
		</dsp:droplet>
		
		</c:if>
            <div id="mini_Cart" class="clearfix" data-cart-items-count="${cartItemsCount}">
                <dsp:include page="/cart/cart_free_shipping_qualifier.jsp" flush="true"></dsp:include>
					<dsp:include page="/cart/order_items.jsp" flush="true">
						<dsp:param name="fromMiniCart" value="true"/>
						<dsp:param name="fromRegOwnerName" value="${fromRegOwnerName}"/>
					</dsp:include>
                <div class="clear"></div>
                <ul id="miniCartBottom" class="noMar noPad">
                    <li class="miniCartRow clearfix">
                        <c:if test="${param.count ne null}">
                            <p class="miniCartMessage">
                                <c:out value="${param.count}" /> <bbbl:label key="lbl_orderitems_itemaddedtocart" language="${pageContext.request.locale.language}" />
                            </p>
                        </c:if>
                        <div class="padTop_10 clearfix" id="<c:if test="${isInternationalUser}">miniInternational</c:if>">
                            <div class="fr<c:if test="${isInternationalUser and !hideOrderSubtotal}"> widthHundred</c:if>">
                                <strong class="miniCartText fl<c:if test="${isInternationalUser}"> internationalmini</c:if>">
								 	<c:choose>
										<c:when test="${isInternationalUser}">											
											<c:choose>
												<c:when test="${!hideOrderSubtotal}">
													<bbbl:label key="lbl_estimated_orderSubtotal" language="${pageContext.request.locale.language}" /> <span class="summaryCount"><dsp:include page="/cart/cart_item_count.jsp"/> <bbbl:label key="lbl_cartdetail_items" language="${pageContext.request.locale.language}" /></span>
													
												</c:when>
												<c:otherwise>
													<span class="summaryCount"><dsp:include page="/cart/cart_item_count.jsp"/><bbbl:label key="lbl_incart_minicart_items" language="${pageContext.request.locale.language}" /><span class="minicartHideOrderTotal">**</span><span class="disPriceString discountEnableAtc"><bbbl:label	key="atc_incart_order_message"	language="${pageContext.request.locale.language}" /></span>
												</span>
												
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<dsp:include page="/cart/cart_item_count.jsp"/> 
											<c:choose>
												<c:when test="${!hideOrderSubtotal}">
													<bbbl:label key="lbl_minicart_items" language="${pageContext.request.locale.language}" />
												</c:when>
												<c:otherwise>
													<bbbl:label key="lbl_incart_minicart_items" language="${pageContext.request.locale.language}" /><span class="minicartHideOrderTotal">**</span><span class="disPriceString discountEnableAtc"><bbbl:label	key="atc_incart_order_message"	language="${pageContext.request.locale.language}" /></span>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>	
										<dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasErrorPersonalizedItemDroplet">
										 	<dsp:param name="order" bean="ShoppingCart.current"/>
										 	<dsp:oparam name="output">
												<dsp:getvalueof var="orderHasErrorPrsnlizedItem" param="orderHasErrorPrsnlizedItem" />
			 								</dsp:oparam>
										</dsp:droplet>
										<c:if test="${!hideOrderSubtotal}">
										<c:choose>
										<c:when test="${orderHasErrorPrsnlizedItem}">
											<span class="<c:if test="${isInternationalUser}">internationalShipMiniCart</c:if>">
		                                            <bbbl:label key="lbl_cart_tbd" language="${pageContext.request.locale.language}" />
											</span>
										</c:when>
										<c:otherwise>
	                                        <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
		                                        <dsp:param name="priceObject" bean="ShoppingCart.current" />
		                                        <dsp:param name="forItemsRawTotal" value="true"/>
		                                        <dsp:oparam name="output">
												<span class="<c:if test="${isInternationalUser}">internationalShipMiniCart</c:if>">
		                                            <dsp:valueof param="cartItemsRawTotal" converter="currency"/>
												</span>
		                                        </dsp:oparam>
	                                    	</dsp:droplet>
                                    	</c:otherwise>
                                    </c:choose>
                                    </c:if>
                                </strong>
                                
                                <div class="miniCartButton button button_active button_active_orange">
                                    <a href="${contextPath}/cart/cart.jsp"><bbbl:label key="lbl_minicart_checkout_cart" language="${pageContext.request.locale.language}" /></a>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                        </div>
						<p class="cb smallText noMar padTop_5 textRight">
						<c:if test="${!hideOrderSubtotal}">
						 <c:choose>
							<c:when test="${isInternationalUser}">
								<bbbl:label key="lbl_international_cart_disclaimer" language="${pageContext.request.locale.language}" /> 
							</c:when>
							<c:otherwise>
								<bbbl:label key="lbl_mini_cart_disclaimer" language="${pageContext.request.locale.language}" />
							</c:otherwise>
						</c:choose>
						</c:if>
                       </p>
					</li>
                </ul>
                <div class="clear"></div>
            </div>
        </dsp:oparam>
        <dsp:oparam name="true">
            <div id="mini_Cart" class="clearfix" data-cart-items-count="0">
                <div class="miniCartEmptyMessage breakWord"><bbbl:label key="lbl_orderitems_noitemsincart" language="${pageContext.request.locale.language}" /></div>
                <div class="clear"></div>
            </div>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>