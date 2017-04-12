<dsp:page>

	<%-- Imports --%>
	<%@ page import="com.bbb.constants.BBBCheckoutConstants"%>

	<c:set var="language" value="<c:out param='${language}' />" />

	<div id="cartHeader">
		<div class="row">
			<div class="left page-title">
				<h1 class="inline-title"><bbbl:label key="lbl_cartdetail_yourcart" language="${language}"/></h1>
			</div>
			<div class="right print-email">
				<a href="#" class="pdp-sprite email email-trigger" title="<bbbl:label key="lbl_cartdetail_emailcart" language="${language}"/>"><span></span></a>|<a href="#" class="pdp-sprite print print-trigger" title="<bbbl:label key="lbl_cartdetail_print" language="${language}"/>"><span></span></a>
			</div>
		</div>
		<div id="emailModal" class="reveal-modal medium" data-reveal><a class="close-reveal-modal">&#215;</a></div>
	</div>

</dsp:page>
