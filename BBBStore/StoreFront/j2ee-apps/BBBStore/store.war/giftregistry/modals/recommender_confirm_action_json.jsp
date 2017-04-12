<%@page contentType="application/json"%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
	<dsp:getvalueof var="registryId" param="registryId"/>
    <dsp:getvalueof var="requestedFlag" param="requestedFlag"/>
    <dsp:getvalueof var="recommenderName" param="recommenderName"/>
     <c:choose>
    	<c:when test="${requestedFlag == 'block' }">
    		<c:set var="lbl_toggle_success"><bbbl:label key="lbl_recommender_persist_block" language="${pageContext.request.locale.language}" /></c:set>
    		<c:set var="buttonText"><bbbl:label key="lbl_recommenders_tab_unblock" language="${pageContext.request.locale.language}" /></c:set>
    		<c:set var="blockedText"><bbbl:label key="lbl_recommenders_tab_blocked" language="${pageContext.request.locale.language}" /></c:set>
    	</c:when>
    	<c:when test="${requestedFlag == 'unblock' }">
    		<c:set var="lbl_toggle_success"><bbbl:label key="lbl_recommender_persist_unblock" language="${pageContext.request.locale.language}" /></c:set>
    		<c:set var="buttonText"><bbbl:label key="lbl_recommenders_tab_block" language="${pageContext.request.locale.language}" /></c:set>
    	</c:when>
    </c:choose>
	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		<dsp:param name="value" bean="GiftRegistryFormHandler.errorMap" />
		<dsp:oparam name="true">
		<json:object escapeXml="false">
				<json:property name="success" value="true"/>
				<json:property name="message" value="${recommenderName}${lbl_toggle_success}"/>
				<json:property name="registryId" value="${registryId}"/>
				<json:property name="buttonText" value="${buttonText }"></json:property>
				<json:property name="blockedText" value="${blockedText }"></json:property>
		</json:object>
		</dsp:oparam>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error"><dsp:valueof bean="GiftRegistryFormHandler.errorMap.TogglePersistError"/></json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>