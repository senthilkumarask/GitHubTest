<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<div class="compare-alert">
		<div id="compareAlertText">
			<h3 class="marBottom_20"><bbbl:label key="lbl_compare_product_limit" language="${pageContext.request.locale.language}" /></h3>
			<span><bbbl:textArea key="txt_compare_product_alert" language="${pageContext.request.locale.language}"/></span>
		</div>
		<div class="formRow clearfix">
			<div class="button button_active marRight_10">
				<a href="${contextPath}/compare/product_comparison.jsp" class="compare-button">
					<bbbl:label key="lbl_compare_products" language="${pageContext.request.locale.language}" />
				</a>
			</div>
				<a href="#" class="btnCancelCompare close-any-dialog" title="Cancel">Cancel</a>
			</div>
	</div>
</dsp:page>