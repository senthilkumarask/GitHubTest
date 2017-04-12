<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.errorMap"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error"><dsp:valueof bean="ProfileFormHandler.errorMap.error"/></json:property>
				<<json:property name="logOut" value="${false}"/>
			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="true">
		<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient" />
			<c:choose>
					<c:when test="${isTransient eq true }">
							 <json:object>
								<json:property name="logOut" value="${true}"/>
							</json:object>
					</c:when>
					<c:otherwise>
						 <json:object>
							<json:property name="logOut" value="${false}"/>
							<json:property name="error">Please retry</json:property>
						</json:object>
					</c:otherwise>
			 </c:choose>
	 </dsp:oparam>					
	</dsp:droplet>	
</dsp:page>