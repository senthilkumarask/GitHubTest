<dsp:page>

	<%-- Variables --%>
	<dsp:getvalueof var="paypal" param="paypal" />
	<dsp:getvalueof var="addcheck" param="addcheck" />
	<c:set var="checkboxHiddenClass" value="hidden" />
	<c:if test="${sessionScope.giftsAreIncludedInOrder}">
		<c:set var="checkboxHiddenClass" value="" />
	</c:if>
	
	<ul class="checkout-steps hide-for-small-only medium-only-no-margin-bottom">
		<li id="shipping" class="<c:if test='${not paypal and not addcheck}'>active</c:if>"><a class="edit-step <c:if test='${not paypal}'>disabled</c:if>" href="#" data-step="shipping"><h1><bbbl:label key="lbl_bread_crumb_shipping" language="${pageContext.request.locale.language}" /></h1></a></li>
		<li id="gifting" class="${checkboxHiddenClass}"><a class="edit-step <c:if test='${not paypal and not addcheck}'>disabled</c:if>" href="#" data-step="gifting"><h1>Gift Options</h1></a></li>
		<li id="billing"><a class="edit-step <c:if test='${not paypal and not addcheck}'>disabled</c:if>" href="#" data-step="billing"><h1><bbbl:label key="lbl_bread_crumb_billing" language="${pageContext.request.locale.language}" /></h1></a></li>
		<li id="payment"><a class="edit-step <c:if test='${not paypal and addcheck}'>disabled</c:if>" href="#" data-step="payment"><h1><bbbl:label key="lbl_bread_crumb_payment" language="${pageContext.request.locale.language}" /></h1></a></li>
		<li id="review" class="<c:if test='${paypal and addcheck}'>active</c:if>"><h1><bbbl:label key="lbl_bread_crumb_preview" language="${pageContext.request.locale.language}" /></h1></li>
	</ul>

</dsp:page>
