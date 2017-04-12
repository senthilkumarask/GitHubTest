<dsp:page>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:droplet name="/atg/dynamo/transaction/droplet/Transaction">
	<dsp:param name="transAttribute" value="required"/>
	<dsp:oparam name="output">
		<dsp:droplet name="/com/bbb/commerce/cart/droplet/EmptyCartDroplet">
		<dsp:param name="order" bean="ShoppingCart.current" />
		</dsp:droplet>
	</dsp:oparam>
</dsp:droplet>
</dsp:page>