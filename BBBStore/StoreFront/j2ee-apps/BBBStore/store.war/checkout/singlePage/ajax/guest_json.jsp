<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />	

<c:set var="fromReset" value="${param.fromReset}"/>
<%--

	<dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
   <dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>
   <c:if test="${(empty showLegacyPwdPopup) and (empty showMigratedPopup)}">
   		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			<dsp:param name="value" bean="ProfileFormHandler.errorMap.loginPasswordError"/>
			<dsp:oparam name="false">
				<div class="error">
				    	<dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"></dsp:valueof>
				    
			   </div>
			</dsp:oparam>
			<dsp:oparam name="true">
				<dsp:include page="/global/gadgets/errorMessage.jsp">
	      			<dsp:param name="formhandler" bean="ProfileFormHandler"/>
	    		</dsp:include>
			</dsp:oparam>
		</dsp:droplet>
	   
	</c:if>
--%>
	<c:if test="${not empty fromReset && fromReset=='true' }">
	<json:object>
				<json:property name="success" value="true"/>
				<json:property name="fromReset" value="true"/>
			</json:object>
	</c:if>
	<c:if test="${empty fromReset}">
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.formExceptions"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error" value="${true}"/>
				<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
					<dsp:param name="value" bean="ProfileFormHandler.errorMap.loginPasswordError"/>
					<dsp:oparam name="false">
						<json:array name="errorMessages">						
							<json:property >
						   	<dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"></dsp:valueof>
						   </json:property >
						</json:array>					   
					</dsp:oparam>
					<dsp:oparam name="true">
						<json:array name="errorMessages">
							<dsp:droplet name="ErrorMessageForEach">
								<dsp:oparam name="output">
									
									<dsp:getvalueof param="message" var="err_msg_key" />
									<c:set var="under_score" value="_" />
									<c:choose>
										<c:when test="${fn:contains(err_msg_key, under_score)}">
											<c:set var="err_msg" scope="page">
												<bbbe:error key="${err_msg_key}"	language="${pageContext.request.locale.language}" />
											</c:set>
										</c:when>
									</c:choose>
									<json:property >
										<dsp:valueof param="message" valueishtml="true" />
										<%--
										<c:choose>
											<c:when test="${empty err_msg}">
												<dsp:getvalueof var="ErrorMessage" param="message" />
												<c:if test="${errMsg ne ErrorMessage}">
												B:	<dsp:valueof param="message" valueishtml="true" />
												</c:if>
												<dsp:getvalueof var="errMsg" param="message" />	
											</c:when>
											<c:otherwise>C:${err_msg}</c:otherwise>
										</c:choose>
										--%>
									</json:property>
									<c:set var="err_msg" scope="page"></c:set>
									
								</dsp:oparam>
							</dsp:droplet>
						</json:array>	
					</dsp:oparam>
				</dsp:droplet>

				


			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="true">
			<json:object>
				<json:property name="success" value="true"/>
			</json:object>	
		</dsp:oparam>					
	</dsp:droplet>
	</c:if>	
</dsp:page>