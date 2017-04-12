<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<div class="compare-alert">
		<div id="compareAlertText">
			<h3 class="marBottom_20"><bbbl:label key="lbl_compare_samesku_header" language="${pageContext.request.locale.language}" /></h3>
			<span><bbbl:label key="lbl_compare_samesku_subheader" language="${pageContext.request.locale.language}"/></span>
		</div>
		<div class="formRow clearfix">
			<div class="button button_active marRight_10">
				<a href="#" class="compare-button close-any-dialog">
					Ok
				</a>
			</div>
				<a href="#" class="btnCancelCompare close-any-dialog" title="Cancel">Cancel</a>
			</div>
	</div>
</dsp:page>
