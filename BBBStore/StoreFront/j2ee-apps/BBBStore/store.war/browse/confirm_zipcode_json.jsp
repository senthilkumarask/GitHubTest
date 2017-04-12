<dsp:page>

<dsp:importbean bean="com/bbb/common/droplet/CheckForSDDDroplet"/> 

<dsp:getvalueof var="fromLandingPage" param="fromLandingPage"/>
  <dsp:droplet name="CheckForSDDDroplet">
		<dsp:param name="inputZip" value="${param.sddZip}" /> 
		<dsp:oparam name="output">
		
		<dsp:getvalueof var="sddEnabled" param="sddEnabled"/>
		</dsp:oparam>
  </dsp:droplet>

	<c:choose>
		<c:when test="${!sddEnabled}">
			<json:object>
			<json:property name="success" value="false"/>
			<json:property name="errorMessage"></json:property>	
			<json:property name="footerMessage"><bbbl:label key="lbl_sdd_validation_footer" language="${pageContext.request.locale.language}"/></json:property>
			<<json:property name="headerMessage">Error Header</json:property>
			</json:object> 
		</c:when>
		<c:otherwise>
			<json:object>
				<json:property name="success" value="true"/>
				<c:choose>
					<c:when test="${fromLandingPage eq 'true'}">
						<json:property name="congratulations" escapeXml="false"><dsp:include page="/_includes/sameDayDeliveryCongratulationsInfo.jsp" ></dsp:include></json:property>
					</c:when>
					<c:otherwise>
						<json:property name="congratulations" escapeXml="false"><dsp:include page="/_includes/modals/sameDayDeliveryCongratulationsModal.jsp"></dsp:include></json:property>
					</c:otherwise>
				</c:choose>
			
			</json:object>	
		</c:otherwise>
	</c:choose>								
</dsp:page>