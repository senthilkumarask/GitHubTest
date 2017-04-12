<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.errorMap"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property escapeXml="false" name="error"><dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"/></json:property>
				<<json:property escapeXml="false" name="login" value="${false}"/>
			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="true">
		<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient" />
		   <dsp:getvalueof var="redirectURL" bean="ProfileFormHandler.redirectURL"/>		   
			   <c:choose>
					<c:when test="${isTransient eq true }">
							 <json:object>
								<json:property escapeXml="false" name="login" value="${false}"/>
								<json:property escapeXml="false" name="redirectURL" value="/store/account/Login"/>
							</json:object>
					</c:when>
					<c:otherwise>
						 <json:object>
							<json:property escapeXml="false" name="login" value="${true}"/>
							<json:property escapeXml="false" name="redirectURL" value="${redirectURL}"/>
							<c:set var="sessionIdForSPC" value="${pageContext.session.id}" scope="session" />
						</json:object>
					</c:otherwise>
			 </c:choose>			 
		</dsp:oparam>					
	</dsp:droplet>	
</dsp:page>