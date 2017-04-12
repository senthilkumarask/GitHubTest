<dsp:page>

	<%-- Imports --%>
	<%@ page import="com.bbb.constants.BBBCheckoutConstants"%>

	<%-- Variables --%>
	<dsp:getvalueof var="isWishList" param="isWishList" />
	<c:set var="language" value="<c:out param='${language}' />" />

	<div class="row">
		<div class="small-12">
			<c:if test="${not isWishList}">
				<div class="left page-title">
					<h1 class="inline-title"><bbbl:label key="lbl_sfl_your_saved_items" language="${language}"/></h1>
				</div>
			</c:if>
			<div class="right print-email">
				<a href="#" class="pdp-sprite email email-saved-items" title="<bbbl:label key="lbl_sfl_email_saved_items" language="${language}"/>"><span></span></a>|<a href="#" class="pdp-sprite print print-saved-items" title="<bbbl:label key="lbl_sfl_print_saved_items" language="${language}"/>"><span></span></a>
			</div>
		</div>
	</div>
	<div id="emailModal" class="reveal-modal medium" data-reveal><a class="close-reveal-modal">&#215;</a></div>

</dsp:page>
