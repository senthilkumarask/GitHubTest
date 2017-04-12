<%@page contentType="application/json"%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
	<c:set var="lbl_email_invite_sent"><bbbl:label key="lbl_email_invite_sent" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lbl_invitaion_sent"><bbbl:label key="lbl_invitaion_sent" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lbl_manage_email_notifications"><bbbl:label key="lbl_manage_email_notifications" language="${pageContext.request.locale.language}" /></c:set>
	<dsp:getvalueof var="registryId" param="registryId"/>
    <dsp:getvalueof var="eventType" param="eventType"/>
    <dsp:getvalueof var="emptyRegistrant" param="emptyRegistrant"/>
  <c:choose>
	                         <c:when test="${emptyRegistrant eq 0}">
	                      		<c:set var="manageNotifications" value="false"/>
	                     	</c:when>
	                     	<c:otherwise>
	                     			<c:set var="manageNotifications" value="true"/>
	                     	</c:otherwise>
	                     </c:choose>
	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		<dsp:param name="value" bean="GiftRegistryFormHandler.errorMap" />
		<dsp:oparam name="true">
		<c:choose>
			<c:when test="${manageNotifications eq 'true'}">
				<json:object escapeXml="false">
				<json:property name="success" value="${true}"/>
				<json:property name="dialogInvitationSent">${lbl_email_invite_sent}</json:property>
				<json:property name="dialog"><div>${lbl_invitaion_sent} <a href='javascript:void(0)' class='recommendersRecommendationLnk'>${lbl_manage_email_notifications}</a></div></json:property>
				<json:property name="registryId" value="${registryId}"/>
				<json:property name="eventType" value="${eventType}"/>
				<json:property name="eventType" value="${eventType}"/>			
				</json:object>
			</c:when>
			<c:otherwise>
				<json:object escapeXml="false">
				<json:property name="success" value="${true}"/>
				<json:property name="dialogInvitationSent">${lbl_email_invite_sent}</json:property>
				<json:property name="dialog"><div>${lbl_invitaion_sent}</div></json:property>
				<json:property name="registryId" value="${registryId}"/>
				<json:property name="eventType" value="${eventType}"/>
				<json:property name="eventType" value="${eventType}"/>						
				</json:object>
			</c:otherwise>
			</c:choose>
		</dsp:oparam>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error"><dsp:valueof bean="GiftRegistryFormHandler.errorMap.emailError"/></json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>