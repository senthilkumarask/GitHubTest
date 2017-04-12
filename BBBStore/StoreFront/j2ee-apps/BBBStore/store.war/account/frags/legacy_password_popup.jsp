<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	<json:object>
		<json:property name="url"><dsp:valueof value="${contextPath}/account/login.jsp?showMigratedPopup=true"/></json:property>
	</json:object>
</dsp:page>