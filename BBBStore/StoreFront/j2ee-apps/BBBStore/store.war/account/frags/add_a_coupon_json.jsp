<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	<dsp:importbean bean="/com/bbb/account/WalletFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
		
	<dsp:getvalueof var="successCouponid" bean="WalletFormHandler.successCouponid" />

	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="WalletFormHandler.formError"/>
		<dsp:oparam name="true">
		<dsp:getvalueof var="error" bean="WalletFormHandler.errorMap.couponError"/>
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
				<json:property name="successCouponid" value="${successCouponid}"/>
				<json:property name="success" value="true"/>
					
				  <dsp:droplet name="Switch">
			        <dsp:param name="value" bean="Profile.transient"/>
			        <dsp:oparam name="false">
			            <json:property name="url"><dsp:valueof value="${contextPath}/account/coupons.jsp"/></json:property>
			        </dsp:oparam>
			        <dsp:oparam name="true">
			           <json:property name="url"><dsp:valueof value="${contextPath}/couponWallet/walletLanding.jsp"/></json:property>
			        </dsp:oparam>
			     </dsp:droplet>					
			</json:object>									
		</dsp:oparam>					
	</dsp:droplet>	
</dsp:page>