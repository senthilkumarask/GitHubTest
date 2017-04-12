<dsp:page>
	<dsp:getvalueof var="reasonCode" param="reasonCode"/>
	<c:set var="reasonCodeMsg"><bbbl:label key='reason_code_${reasonCode}' language="${pageContext.request.locale.language}" /></c:set>
	
<%--The 'when' condition is for "Value not found for reason_code_x" so that default message is displayed otherwise we will display message associated with reasonCode--%>
	<c:choose>
		<c:when test="${fn:contains(reasonCodeMsg, 'reason_code_')}">
			<bbbl:label key='default_reasoncode_message' language="${pageContext.request.locale.language}" />									
		</c:when>
		<c:otherwise>
			${reasonCodeMsg}
		</c:otherwise>
	</c:choose>			
</dsp:page>