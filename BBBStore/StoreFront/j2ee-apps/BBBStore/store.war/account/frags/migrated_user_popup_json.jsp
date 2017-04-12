<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	<json:object>
		<json:property name="url"><dsp:valueof value="${contextPath}/account/login.jsp?migratedUserPopup=true"/></json:property>
	</json:object>
</dsp:page>