<dsp:page>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<div class="grid_12 clearfix" id="subHeader">
		<h2 class="section fl"><bbbl:label key="lbl_preview_preview" language="<c:out param='${language}'/>"/></h2>
		<div class="suffix_2 fr">
			<dsp:getvalueof var="cartEmpty" bean="ShoppingCart.empty"/>
			<c:if test="${not cartEmpty}">
			<div class="button button_prod button_active fr btnPlaceHolder buttonCheckout button_disabled">
				<input type="button" onclick="document.getElementById('shippingPlaceOrder1').click()" value="Place Order" disabled="disabled" class="btnOrderSubmit enableOnDOMReady"  role="button" aria-pressed="false" />
			</div>
			</c:if>
		</div>
	</div>
</dsp:page>