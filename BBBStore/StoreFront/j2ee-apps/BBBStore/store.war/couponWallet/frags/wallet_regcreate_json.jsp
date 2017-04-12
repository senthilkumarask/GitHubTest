<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	<dsp:importbean bean="/com/bbb/account/WalletFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>			
	<%-- <dsp:getvalueof var="lookupWalletID" bean="WalletFormHandler.lookupWalletID" /> --%>
				
	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="WalletFormHandler.formError"/>
		<dsp:oparam name="true">
		<dsp:getvalueof var="formError" bean="WalletFormHandler.formError"/>
		<json:object>
		<json:property name="error">true</json:property>
		<c:if test="${formError == true}">						
		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
		<dsp:param bean="WalletFormHandler.formExceptions" name="exceptions"/>
		<dsp:oparam name="output">						
				<json:property name="errorMessages"><dsp:valueof param="message"/></json:property>						
		</dsp:oparam>
		</dsp:droplet>
		</c:if>
 </json:object> 
		</dsp:oparam>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="success">true</json:property>				
			  	<json:property name="url"><dsp:valueof value="${contextPath}/couponWallet/walletLanding.jsp"/></json:property>
			</json:object>									
		</dsp:oparam>					
	</dsp:droplet>
</dsp:page>