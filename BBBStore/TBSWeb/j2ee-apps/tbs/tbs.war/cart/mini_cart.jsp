<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler" />

	<%-- this variable needs to hold how many products are there in the cart --%>
	<%-- scope is set as session because the same variable is used in "order_items.jsp" --%>
	<c:set var="cartItemsCount" scope="session"><dsp:valueof bean="ShoppingCart.current.commerceItemCount"/></c:set>

	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		<dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
		<dsp:oparam name="false">
			<div id="miniCart" data-cart-items-count="${cartItemsCount}">
				<div class="row no-margin mini-cart-items">
					<dsp:include page="/cart/cart_free_shipping_qualifier.jsp" flush="true"></dsp:include>
					<dsp:include page="/cart/minicart_order_items.jsp" flush="true">
						<dsp:param name="fromMiniCart" value="true"/>
					</dsp:include>
				</div>
				<div class="row no-margin mini-cart-summary">
					
                    <div class="small-12 columns">
                        <c:if test="${param.count ne null}">
                            <p>
                                <c:out value="${param.count}" /> <bbbl:label key="lbl_orderitems_itemaddedtocart" language="${pageContext.request.locale.language}" />
                            </p>
                          </c:if>
                    </div>
                    <div class="small-12 columns">
						<p><dsp:include page="/cart/cart_item_count.jsp"/> <bbbl:label key="lbl_minicart_items" language="${pageContext.request.locale.language}" />
						<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
							<dsp:param name="priceObject" bean="ShoppingCart.current" />
							<dsp:param name="forItemsRawTotal" value="true"/>
							<dsp:oparam name="output">
								<dsp:getvalueof param="cartItemsRawTotal" var="cartItemsRawTotal"/>
								 <fmt:formatNumber value="${cartItemsRawTotal}"  type="currency"/>
							</dsp:oparam>
						</dsp:droplet></p>
					</div>
					<div class="small-6 columns">
						<dsp:form action="" method="post">
							<dsp:input iclass="button secondary" bean="ProfileFormHandler.clearCart" type="submit" value="Clear Cart"/>
						</dsp:form>
					</div>
					<div class="small-6 columns">
						<a href="${contextPath}/cart/cart.jsp" class="button transactional"><bbbl:label key="lbl_minicart_checkout_itemaddedtocart" language="${pageContext.request.locale.language}" /></a>
					</div>
				</div>
				<div class="row no-margin mini-cart-disclaimer">
					<div class="small-12 columns">
						<h6><bbbl:label key="lbl_mini_cart_disclaimer" language="${pageContext.request.locale.language}" /></h6>
					</div>
				</div>
			</div>
		</dsp:oparam>
		<dsp:oparam name="true">
			<div id="miniCart" data-cart-items-count="0">
				<div class="mini-cart-empty">
					<p><bbbl:label key="lbl_orderitems_noitemsincart" language="${pageContext.request.locale.language}" /></p>
				</div>
			</div>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
