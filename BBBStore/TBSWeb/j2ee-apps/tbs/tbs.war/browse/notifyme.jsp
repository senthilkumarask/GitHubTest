<dsp:page>
	<div class="small-12 columns product-other-links">
	<dsp:a iclass="pdp-sprite notify-me" href="/tbs/browse/frag/notifyMeRequest.jsp">
			<dsp:param name="skuId" param="skuId" />
			<dsp:param name="productId" param="productId" />
			<bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" />
		</dsp:a>
		<div id="notifyMeRequest" class="reveal-modal medium" data-reveal>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function(){
			$(".nearby-stores").attr("data-reveal-id","nearbyStore");
			$(".nearby-stores").attr("data-reveal-ajax","true");
			$(".notify-me").attr("data-reveal-id","notifyMeRequest");
			$(".notify-me").attr("data-reveal-ajax","true");
			$( ".bbbS7ContainerTipWrapper" ).wrap( "<a href='#' data-reveal-id='bbbS7ZoomImageDIV'></a>" );
			$(document).foundation('reflow');
		});
	</script>
</dsp:page>