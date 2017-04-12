<dsp:page>
	<dsp:getvalueof var="clear" param="clear" />
	<c:set var="blank" value="   " />
	<c:choose>
		<c:when test="${clear}">
			<c:remove var="status" scope="session"/>
		</c:when>
		<c:otherwise>
			${blank} ${sessionScope.status}
		</c:otherwise>
	</c:choose>
</dsp:page>