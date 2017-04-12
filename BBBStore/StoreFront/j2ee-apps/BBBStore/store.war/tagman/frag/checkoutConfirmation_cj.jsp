<dsp:page>

<dsp:getvalueof id="cjSkuIds" param="cjSkuIds" />
<dsp:getvalueof id="cjSkuPrices" param="cjSkuPrices" />
<dsp:getvalueof id="cjSkuQty" param="cjSkuQty" />
<%-- Begin TagMan --%>
	<script type="text/javascript">	
		window.tmParam.cj_product_sku = '${cjSkuIds}';
		window.tmParam.cj_product_price = '${cjSkuPrices}';
		window.tmParam.cj_product_quantity = '${cjSkuQty}';
	</script>
		
</dsp:page>