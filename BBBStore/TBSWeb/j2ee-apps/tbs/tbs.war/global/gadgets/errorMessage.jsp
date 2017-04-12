<dsp:page>

<%--
This page displays any form handler exceptions wrapped in a div.

Parameters:
- formhandler - A formhandler object that may have exceptions
--%>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />

	<%-- Variables --%>
	<dsp:getvalueof param="formhandler" var="formhandler" />

	<%-- Formhandler Errors --%>
	<dsp:droplet name="ErrorMessageForEach">
		<c:if test="${null ne formhandler}">
			<dsp:param param="formhandler.formExceptions" name="exceptions" />
		</c:if>
		<dsp:oparam name="outputStart">
			<div class="small-12 columns">
		</dsp:oparam>
		<dsp:oparam name="output">
			<p class="error">
			<dsp:getvalueof param="message" var="err_msg_key" />
			<c:set var="under_score" value="_" />
			<c:choose>
				<c:when test="${fn:contains(err_msg_key, under_score)}">
					<c:set var="err_msg" scope="page">
						<bbbe:error key="${err_msg_key}" language="${pageContext.request.locale.language}" />
					</c:set>
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test="${empty err_msg}">
					<dsp:valueof param="message" valueishtml="true" />
				</c:when>
				<c:otherwise>${err_msg}</c:otherwise>
			</c:choose>
			<c:set var="err_msg" scope="page"></c:set>
			</p>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			</div>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
