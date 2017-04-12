<dsp:page>
 <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler"/>
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />

<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
<dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
<dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>
<dsp:getvalueof var="registryId" param="registryId"/>
<dsp:getvalueof var="eventType" param="eventType"/>
<dsp:getvalueof var="regFirstName" param="regFirstName"/>
<dsp:getvalueof var="regEventDate" param="regEventDate"/>
<dsp:getvalueof var="emptyRegistrant" param="emptyRegistrant"/>

<c:set var="lbl_Invite_Frnd_Email">
 <bbbl:label key="lbl_Invite_Frnd_Email" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
<dsp:form id="frmInviteFriendsReg" method="post" action="">

	<div class="input clearfix formRow marTop_10">
		<div class="label">
			<label id="lblInviteFrndEmail" for="inviteFrndEmail"><bbbl:label key="lblInviteFrndEmail" language="${pageContext.request.locale.language}"/></label>
		</div>

        <dsp:input bean="EmailHolder.values.recipientEmail" id="inviteFrndEmail" name="inviteFrndEmail" value="" type="text">
                         <dsp:tagAttribute name="aria-required" value="true"/>
                         <dsp:tagAttribute name="aria-labelledby" value="lblInviteFrndEmail errorfrndEmail"/>
                         <dsp:tagAttribute name="placeholder" value="${lbl_Invite_Frnd_Email}"/>
        </dsp:input>
	</div>
	<div class="input clearfix formRow marTop_10">
		<div class="label">
			<label id="lblInviteFrndMsg" for="comments"><bbbl:label key="lblInviteFrndMsg" language="${pageContext.request.locale.language}"/></label>
		</div>
		<dsp:textarea id="emailMessage" name="emailMessage" bean="EmailHolder.values.message">
		<dsp:getvalueof var="eventType" param="eventType"/>
		<c:choose>
			<c:when test="${not empty eventType && eventType eq 'Wedding'}">
				<bbbt:textArea key="txt_Invite_Frnd_Msg_Wedding" language="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Retirement'}">
				<bbbt:textArea key="txt_Invite_Frnd_Msg_Retirement" language="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
				<bbbt:textArea key="txt_Invite_Frnd_Msg_Housewarming" language="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
				<bbbt:textArea key="txt_Invite_Frnd_Msg_Commitment" language="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Baby' }">
				<bbbt:textArea key="txt_Invite_Frnd_Msg_Baby" language="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Anniversary' }">
				<bbbt:textArea key="txt_Invite_Frnd_Msg_Anniversery" language="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq'College/University' }">
				<bbbt:textArea key="txt_Invite_Frnd_Msg_College" language="${pageContext.request.locale.language}"/>
			</c:when>
			<c:when test="${not empty eventType && eventType eq 'Birthday' }">
				<bbbt:textArea key="txt_Invite_Frnd_Msg_Birthday" language="${pageContext.request.locale.language}"/>
			</c:when>
			<c:otherwise>
			<bbbt:textArea key="txt_Invite_Frnd_Msg_Other" language="${pageContext.request.locale.language}"/>
			</c:otherwise>
		</c:choose>
		</dsp:textarea>
	</div>

	<dsp:input bean="EmailHolder.values.registryURL" type="hidden"  value="${scheme}://${serverName}${contextPath}/giftregistry/recommender_landing_page.jsp?eventType=${eventType}&registryId=${registryId}"/>
	<dsp:input bean="GiftRegistryFormHandler.emailRecommendationErrorURL" type="hidden" value="${scheme}://${serverName}${contextPath}/_includes/modals/inviteFriendsRegistry_json.jsp?eventType=${eventType}&registryId=${registryId}&emptyRegistrant=${emptyRegistrant}"/>
	<dsp:input bean="GiftRegistryFormHandler.emailRecommendationSuccessURL" type="hidden" value="${scheme}://${serverName}${contextPath}/_includes/modals/inviteFriendsRegistry_json.jsp?eventType=${eventType}&registryId=${registryId}&emptyRegistrant=${emptyRegistrant}"/>
	<!-- BPS-1112 Persist registry Details in GiftRegistryRepository -->
	<dsp:input bean="GiftRegistryFormHandler.registryEventDate" type="hidden" value="${regEventDate}"/>
	<dsp:input bean="GiftRegistryFormHandler.regFirstName" type="hidden" value="${regFirstName}"/>
	<dsp:input bean="GiftRegistryFormHandler.eventType" type="hidden" value="${eventType}"/>
	<!-- BPS-1112 Persist registry Details in GiftRegistryRepository -->

    <c:set var="lbl_send_invite"><bbbl:label key='lbl_send_invite' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
	<div class="marTop_20 buttonpane clearfix">
		<div>
			<dsp:input type="submit" bean="GiftRegistryFormHandler.emailRegistryRecommendation" name="sendInvite" value="${lbl_send_invite}
			" iclass="recommendationButton button-Med" >
                        <dsp:tagAttribute name="role" value="button"/>
             </dsp:input>

		</div>
		<c:set var="cancelBtn"><bbbl:label key='lbl_cancel' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
		<a class="buttonTextLink close-any-dialog capitalize" href="#" role="link">${cancelBtn}</a>
	</div>

</dsp:form>
</dsp:page>