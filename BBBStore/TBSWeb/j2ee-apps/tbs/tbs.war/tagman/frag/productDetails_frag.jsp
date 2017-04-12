<dsp:page>
	<dsp:getvalueof id="categoryPath" param="categoryPath" />
	<dsp:getvalueof id="pDefaultChildSku" param="pDefaultChildSku" />
	<!-- Begin TagMan -->		
	<script type="text/javascript">
		<c:if test="${!empty pDefaultChildSku}">
			window.tmParam.product_sku = '${pDefaultChildSku}';
		</c:if>
		window.tmParam.page_breadcrumb = '${categoryPath}';
	</script>
	<!-- End TagMan -->
</dsp:page>