<dsp:page>
	<dsp:include page="/checkout/preview/frag/display_address.jsp" flush="true">
		<dsp:param name="shippingGroup" param="shippingGroup"  />
		<dsp:param name="beddingKit" param="beddingKit"  />
		<dsp:param name="collegeName" param="collegeName"  />
		<dsp:param name="weblinkOrder" param ="weblinkOrder"/>
	</dsp:include>
	<dsp:include page="/checkout/preview/frag/shipping_method.jsp" flush="true">
		<dsp:param name="shippingGroup" param="shippingGroup"  />
		<dsp:param name="orderDate" param="orderDate"/>
	</dsp:include>
</dsp:page>
