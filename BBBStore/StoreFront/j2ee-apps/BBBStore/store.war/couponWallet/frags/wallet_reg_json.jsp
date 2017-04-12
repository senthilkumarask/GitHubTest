<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	<dsp:importbean bean="/com/bbb/account/WalletFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>		
	<dsp:getvalueof var="lookupWalletID" bean="WalletFormHandler.lookupWalletID" />
	
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="WalletFormHandler.errorMap"/>
		<dsp:oparam name="false">
			<json:object>				
				<json:property name="error">true</json:property>	
				<json:property name="errorMessages"><dsp:valueof bean="WalletFormHandler.errorMap.emailError"/></json:property>	
				
			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="true">			
			<json:object>
				<json:property name="lookupWalletID" value="${lookupWalletID}"/>
				<json:property name="success" value="true"/>			
			           <json:property name="url"><dsp:valueof value="${contextPath}/couponWallet/walletLanding.jsp"/></json:property>
			</json:object>
		</dsp:oparam>					
	</dsp:droplet>	
	


        <dsp:droplet name="IsEmpty">
		<dsp:oparam name="true">
		
		
		</dsp:oparam>						
        </dsp:droplet>

</dsp:page>