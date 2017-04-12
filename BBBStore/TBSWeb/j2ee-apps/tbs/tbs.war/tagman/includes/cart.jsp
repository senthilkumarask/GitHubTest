<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	
	<%-- Begin TagMan --%>
	<script type="text/javascript">	 
		// client configurable parameters 
		window.tmParam.action_type = 'updatecart';
		window.tmParam.UpdateCartType = 'Full';		
		window.tmParam.page_type = 'Cart';
			<dsp:droplet name="/atg/commerce/order/droplet/BBBOrderInfoDroplet">
    			<dsp:param name="order" bean="ShoppingCart.current" />
       				<dsp:oparam name="output">
       				window.tmParam.product_sku = '<dsp:valueof param="itemSkuIds"/>';
       				window.tmParam.product_price = '<dsp:valueof param="itemAmts"/>';
       				window.tmParam.currency_code = '<dsp:valueof bean="ShoppingCart.current.priceInfo.currencyCode"/>';
       				window.tmParam.product_quantity = '<dsp:valueof param="itemQuantities"/>';
				</dsp:oparam>
			</dsp:droplet>		
	</script>
	<%--End TagMan --%>
</dsp:page>