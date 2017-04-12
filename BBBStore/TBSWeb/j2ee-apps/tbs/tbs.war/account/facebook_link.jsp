<dsp:page>

<dsp:importbean bean="/com/bbb/social/facebook/FBConnectFormHandler" />
		
	<%-- <dsp:getvalueof var="fbAccountId" param="fbId" />
	<dsp:getvalueof var="fbEmailAddress" param="fbEmail" />
	
	<dsp:setvalue bean="FBConnectFormHandler.fbAccountId" value="${fbAccountId}" />
	<dsp:setvalue bean="FBConnectFormHandler.emailAddress" value="${fbEmailAddress}" /> --%>
	<dsp:setvalue bean="FBConnectFormHandler.linkingWithoutLogin" />
	
	<dsp:getvalueof bean="FBConnectFormHandler.event" var="event" />
	<dsp:getvalueof bean="FBConnectFormHandler.formError" var="formError"/>

	<json:object>

		<c:choose>

			<c:when test="${formError == true}">
				<json:property name="error">Unexpected Error occurred, Please contact system administrator.</json:property>
			</c:when>
			
			<c:otherwise>
				<json:property name="event">${event}</json:property>
			</c:otherwise>
			
		</c:choose>
		
	</json:object>
	
</dsp:page>