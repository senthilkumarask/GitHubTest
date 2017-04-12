<dsp:page>

	<%-- Variables --%>
	<dsp:getvalueof var="requestUrl" bean="/OriginatingRequest.requestURL" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="pageSecured" value="${pageContext.request.secure}" scope="request" />
	<c:set var="startIndex" value="${fn:indexOf(requestUrl, contextPath)}"/>
	<c:set var="url" value="${fn:substring(requestUrl, 0, startIndex)}"/>
	<c:set var="finalUrl" value="${url}"/>
	<c:if test="${!pageSecured}">
		<c:set var="finalUrl" value="${fn:replace(url, 'http', 'https')}"/>
	</c:if>
	<c:set var="finalUrl" value="${finalUrl}${contextPath}/account/check_balance.jsp"/>

	<div title="Check Gift Card Balance">
		<script type="text/javascript">
			function iframeGiftCardBalanceLoaded() {
				$('#iframeGiftCardBalanceLoader').hide();
			}
		</script>
		<style type="text/css">
			#iframeGiftCardBalanceLoader {
				position:absolute;
				left: 0;
				top: 0;
				height: 80%;
				width: 100%;
			}
		</style>
		<div id="iframeGiftCardBalanceLoader" class="loadingGIF">&nbsp;</div>
		<iframe type="some_value_to_prevent_js_error_on_ie7" id="iframeGiftCardBalance" onload="iframeGiftCardBalanceLoaded()" src="${finalUrl}" title="Check Gift Card Balance" width="520" height="170" scrolling="no" frameBorder="0" class="noOverflow"></iframe>
	</div>

</dsp:page>
