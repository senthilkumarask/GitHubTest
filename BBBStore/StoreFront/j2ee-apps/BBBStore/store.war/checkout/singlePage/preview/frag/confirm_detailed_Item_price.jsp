<dsp:page>
 <dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
         <dsp:param name="priceObject" param="commerceItemRelationship.commerceItem" />
         <dsp:param name="orderObject" param="order"/>
         <dsp:param name="profile" bean="Profile"/>
         <dsp:oparam name="output">
				<dsp:include page="/checkout/singlePage/preview/frag/confirm_Item_price.jsp" flush="true">
					<dsp:param name="priceInfoVO" param="priceInfoVO"/>
					<dsp:param name="commerceItemRelationship" param="commerceItemRelationship"/>
					<dsp:param name="shippingGroup" param="shippingGroup"/>
					<dsp:param name="promoExclusionMap" param="promoExclusionMap"/>
					<dsp:param name="order" param="order"/>
				</dsp:include>
				<%--<dsp:include page="/checkout/preview/frag/display_item_promotions.jsp" flush="true">
					<dsp:param name="priceInfoVO" param="priceInfoVO"/>
				</dsp:include>--%>			
			<%--<dsp:include page="/checkout/preview/frag/formatted_price.jsp" flush="true">
				<dsp:param name="commerceItemRelationship" param="commerceItemRelationship"/>
				<dsp:param name="priceInfoVO" param="priceInfoVO"/>
			</dsp:include>--%>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>