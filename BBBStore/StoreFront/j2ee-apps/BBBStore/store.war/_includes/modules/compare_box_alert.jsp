<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<div title="<bbbl:label key="lbl_compare_product_limit" language="${pageContext.request.locale.language}" />">
		<div id="compareAlertText">
			<span><bbbl:textArea key="txt_compare_product_alert" language="${pageContext.request.locale.language}"/></span>
		</div>
		<div class="formRow clearfix noMarBot">
			<div class="button button_active marRight_10">
				<a href="${contextPath}/compare/product_comparison.jsp" class="compare-button">
					<bbbl:label key="lbl_compare_products" language="${pageContext.request.locale.language}" />
				</a>
			</div>
			<a href="#" class="btnCancelCompare close-any-dialog" title="Cancel"><bbbl:label key="lbl_profile_Cancel" language="${pageContext.request.locale.language}" /></a>
		</div>
	</div>
</dsp:page>