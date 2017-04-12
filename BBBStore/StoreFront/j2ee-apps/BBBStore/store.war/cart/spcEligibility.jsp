<dsp:page>												
	<dsp:droplet name="/com/bbb/commerce/cart/droplet/SinglePageCktEligible">
		<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="singlePageEligible" param="singlePageEligible"/>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>