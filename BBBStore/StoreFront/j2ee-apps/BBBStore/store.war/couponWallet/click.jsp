<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

<c:set var="data" value="${param.d}" />
<c:set var="action" value="${param.a}" />

<dsp:droplet name="/com/bbb/account/WalletClickThroughDroplet">
<dsp:param name="a" value="${action}" />
<dsp:param name="d" value="${data}" />
<dsp:oparam name="output">						
			
<dsp:getvalueof var="relativeUrl" param="relativeUrl" />
									
</dsp:oparam>
<dsp:oparam name="error">						
			
 <h2 class=\"error\" style=\"padding:20px;text-align:center;\"><bbbl:label key="lbl_Somethings_wrong_try_again" language="${pageContext.request.locale.language}"/></h2>
									
</dsp:oparam>				
</dsp:droplet>

<c:if test="${not empty relativeUrl }">
<script type="text/javascript">
	window.location = "${relativeUrl}";
</script>
</c:if>
</dsp:page>