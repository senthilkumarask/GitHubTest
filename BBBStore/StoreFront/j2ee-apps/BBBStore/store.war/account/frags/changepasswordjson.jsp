<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	
	<c:if test="${sessionScope.userCheckingOut eq 'true'}">
		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
	</c:if>
	
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.errorMap"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error">
					<dsp:valueof bean="ProfileFormHandler.errorMap.passwordError"/>
				</json:property>
			</json:object>
		</dsp:oparam>
		<dsp:oparam name="true">
			<json:object>
				<json:property name="success"><bbbl:label key="lbl_password_changed" language="${pageContext.request.locale.language}" /></json:property>
				<dsp:getvalueof bean="SessionBean.RecommenderFlow" var="recommenderFlow"/>
				<dsp:getvalueof bean="SessionBean.RecommenderRedirectUrl" var="recommenderRedirectUrl"/>
				<c:if test="${recommenderFlow eq 'true'}">
					<json:property name="recommenderRedirectUrl" escapeXml="false">${recommenderRedirectUrl}</json:property>
				</c:if>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>