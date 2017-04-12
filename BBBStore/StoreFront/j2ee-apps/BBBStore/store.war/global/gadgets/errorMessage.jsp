<dsp:page>
	<%--
      This page displays any form handler exceptions wrapped in a div.

      Parameters:
      -  formhandler - A formhandler object that may have exceptions
  --%>

	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:getvalueof param="formhandler" var="formhandler" />
	<dsp:getvalueof bean="SessionBean.singlePageCheckout" var="singlePageCheckout"/>
	<%-- formhandler Errors --%>
	<dsp:droplet name="ErrorMessageForEach">
		<c:if test="${null ne formhandler}">
			<dsp:param param="formhandler.formExceptions" name="exceptions" />
		</c:if>

		<dsp:oparam name="outputStart">
			<c:if test="${!envoyError}">
				<ul class="error ajaxError">
			</c:if>
		</dsp:oparam>
		<dsp:oparam name="output">
			<c:choose>
				<c:when test="${envoyError}">
					<div class="error">
				</c:when>
				<c:otherwise>
					<li class="error">
				</c:otherwise>
			</c:choose>

			<dsp:getvalueof param="message" var="err_msg_key" />
			
			<c:set var="under_score" value="_" />
			<c:choose>
				<c:when test="${fn:contains(err_msg_key, under_score)}">
					<c:set var="err_msg" scope="page">
						<bbbe:error key="${err_msg_key}"
							language="${pageContext.request.locale.language}" />
					</c:set>
				</c:when>
			</c:choose>
			<%--If Checkout is being done with Same Day Delivery shipping method --%>
			<c:choose>
			<c:when test="${fn:contains(err_msg_key, 'err_sdd_preview_error') || fn:contains(err_msg_key, 'SameDay_Deliv_Error')}">
					<div class="sddErrorCartPage">
					<c:choose>
						<c:when test="${singlePageCheckout}">
						
							${err_msg}<a class="lowercase" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp" title="Go To shipping"><strong>Go to Shipping page</strong></a>
						</c:when>
						<c:otherwise>
							${err_msg}<a class="lowercase" href="${pageContext.request.contextPath}/checkout/shipping/shipping.jsp" title="Go To shipping"><strong>Go to Shipping page</strong></a>
						</c:otherwise>
					</c:choose>
					</div>
				</c:when>
				<c:otherwise>
					<c:choose>
					<c:when test="${empty err_msg}">
						<dsp:getvalueof var="ErrorMessage" param="message" />
						<c:if test="${errMsg ne ErrorMessage}">
							<dsp:valueof param="message" valueishtml="true" />
						</c:if>
						<dsp:getvalueof var="errMsg" param="message" />
					</c:when>

					<c:otherwise>${err_msg}</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			<c:set var="err_msg" scope="page"></c:set>
			<c:choose>
				<c:when test="${envoyError}">
					</div>
				</c:when>
				<c:otherwise>
					</li>
				</c:otherwise>
			</c:choose>

		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			<c:if test="${!envoyError}">
				</ul>
			</c:if>
		</dsp:oparam>

	</dsp:droplet>
</dsp:page>
