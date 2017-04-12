<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:getvalueof id="PromoCode" param="PromoCode" />
<dsp:getvalueof id="PromoAmount" param="PromoAmount" />
<dsp:getvalueof id="itemSkuIds" param="itemSkuIds" />
<dsp:getvalueof id="itemAmts" param="itemAmts" />
<dsp:getvalueof id="itemSkuNames" param="itemSkuNames" />
<dsp:getvalueof id="itemIds" param="itemIds" />
<dsp:getvalueof id="itemSkuNames" param="itemSkuNames" />
<dsp:getvalueof id="itemQuantities" param="itemQuantities" />
<dsp:getvalueof id="cItemIds" param="cItemIds" />
<dsp:getvalueof var="wcItemUrl" param="wcItemUrl" />
<c:set var="regdata" value=""/>
<dsp:droplet name="ForEach">
	<dsp:param name="array" param="wcItemUrl" />
	<dsp:param name="elementName" value="wcUrlItem" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="size" param="size"/>
		<dsp:getvalueof var="count" param="count"/>
		<dsp:getvalueof var="wcItemValue" param="wcUrlItem" />
		<c:if test="${fn:contains(wcItemValue, '|reg:')}">
		  <c:set var="regdata" value="${regdata}${wcItemValue}"/>
			  <c:if test="${size != count}">
			  <c:set var="regdata" value="${regdata},"/>
			</c:if>
		</c:if>
	</dsp:oparam>
</dsp:droplet>

	<script type="text/javascript">	
		<c:if test="${!empty PromoCode}">
			window.tmParam.promo_code = '${PromoCode}';
		</c:if>
		<c:if test="${!empty PromoAmount}">
			window.tmParam.promot_amt = '${PromoAmount}';
		</c:if>		           										
		window.tmParam.product_sku = '${itemSkuIds}';
		window.tmParam.product_price = '${itemAmts}';
		window.tmParam.product_name = '${itemSkuNames}';
		window.tmParam.product_quantity = '${itemQuantities}';
		window.tmParam.rkg_u4 = '${itemIds}';
		window.tmParam.rkg_u5 = '${itemSkuNames}'
		window.tmParam.item_id = '${cItemIds}';
		window.tmParam.item_reg_id = '${regdata}';
	</script>
</dsp:page>