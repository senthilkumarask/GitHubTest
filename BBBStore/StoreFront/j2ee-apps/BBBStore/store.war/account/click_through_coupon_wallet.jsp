<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="queryString" value="${pageContext.request.queryString}" />


<c:set var="data" value="${param.d}" />
<c:set var="action" value="${param.a}" />

<dsp:droplet name="/com/bbb/account/WalletClickThroughDroplet">
<dsp:param name="a" value="${action}" />
<dsp:param name="d" value="${data}" />
	<dsp:oparam name="output">						
			<dsp:getvalueof var="walletCreated" param="walletCreated" />
			<dsp:getvalueof var="walletId" param="jsonWalledId" />
			<dsp:getvalueof var="offerId" param="jsonOfferId" />
			<dsp:getvalueof var="action" param="jsonAction" />						
	</dsp:oparam>				
</dsp:droplet>

<c:choose>
	<c:when test="${walletCreated}">
		<c:set var="pageLocation" value="/couponWallet/walletLanding.jsp?action=${action}&walletId=${walletId}&offerId=${offerId}" /> 
	</c:when>
	<c:otherwise>
		<c:set var="pageLocation" value="/couponWallet/walletLanding.jsp" />
	</c:otherwise>
</c:choose>
<%--Write review Parameters ${pageLocation}-- --%>
<script type="text/javascript">
	window.location = "${contextPath}/${pageLocation}";
</script>
</dsp:page>