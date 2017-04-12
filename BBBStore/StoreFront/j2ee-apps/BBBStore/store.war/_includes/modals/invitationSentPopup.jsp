<dsp:page>
	<form id="frmInviteConf" method="post" action="">
		<div>
			<p><bbbl:label key="lbl_invitaion_sent" language="${pageContext.request.locale.language}" /></p>
		</div>
		<div class="button button_secondary button_active">
		 	<input id="btnInviteAnother_1" name="btnInviteAnother_1" aria-pressed="false" value="Invite Another" role="button" type="submit">
		 	<input type="hidden" name="inviteAnother" value='${contextPath}/store/_includes/modals/inviteFriendsRegistry.jsp?eventType=<dsp:valueof value="${fn:escapeXml(param.eventType)}"/>&registryId=<dsp:valueof value="${fn:escapeXml(param.registryId)}"/>'>
		</div>
		<a class="buttonTextLink close-any-dialog capitalize" href="#" role="link"><bbbl:label key="lbl_js_close_button" language="${pageContext.request.locale.language}" /></a>
	</form>
</dsp:page>