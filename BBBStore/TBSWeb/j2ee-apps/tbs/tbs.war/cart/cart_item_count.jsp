<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<dsp:droplet name="/com/bbb/commerce/cart/BBBCartItemCountDroplet">
		<dsp:param name="shoppingCart" bean="ShoppingCart.current" />
		<dsp:oparam name="output">
			<dsp:valueof param="commerceItemCount"/>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
