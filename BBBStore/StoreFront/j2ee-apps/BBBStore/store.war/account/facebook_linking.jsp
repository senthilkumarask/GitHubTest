<dsp:page>
	<dsp:importbean bean="/com/bbb/social/facebook/FBConnectFormHandler" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
	<c:set var="overview_facebook_link_title"><bbbl:label key="lbl_overview_facebook_link" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="overview_facebook_unlink_title"><bbbl:label key="lbl_overview_facebook_unlink" language ="${pageContext.request.locale.language}"/></c:set>
	
	<dsp:setvalue bean="FBConnectFormHandler.fbBasicInfo" value="" />
	<dsp:setvalue bean="FBConnectFormHandler.event" value="" />
	
	<dsp:setvalue bean="FBConnectFormHandler.pageSection" value="${pageNameFB}" />
	<dsp:setvalue bean="FBConnectFormHandler.checkFBConnect" />
	
	<dsp:getvalueof bean="FBConnectFormHandler.event" var="event" />
	<dsp:getvalueof bean="FBConnectFormHandler.formError" var="formError"/>
	<dsp:form name="fbLinkedForm" method="post" action="${contextPath}/account/myaccount.jsp?fbUnlink=true">			
		<c:choose>
			<c:when test="${event == 'BBB_PROFILE_EXIST_IN_SAME_SITE' || event == 'BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT'}">
				<div class="spacer clearfix">
					<bbbt:textArea key="txtarea_overview_facebook_not_linked" language ="${pageContext.request.locale.language}"/>
					<div class="clearfix">
						<div class="fr">
							<a id="showFBUnlinkModal" title="${overview_facebook_link_title}" class="fbDisConnectBtn marRight_20" href="#">
								<img title="Unlink with Facebook" alt="Unlink with Facebook" src="/_assets/global/images/f_disconnect.png" />
							</a>
							<dsp:setvalue bean="FBConnectFormHandler.getFacebookId" />
							<dsp:getvalueof bean="FBConnectFormHandler.fbAccountId" var="accountId" />
							<dsp:getvalueof bean="FBConnectFormHandler.fbFullName" var="fbFullName" />
							
							<c:if test="${not empty accountId}">
								<img class="fbUserPic" title="${fbFullName}" alt="${fbFullName}" src="//graph.facebook.com/${accountId}/picture?type=small" />
							</c:if>	
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="spacer clearfix">
					<p class="error cb fcConnectErrorMsg hidden"></p>
					<bbbt:textArea key="txtarea_overview_facebook_linked" language ="${pageContext.request.locale.language}"/>
					<div class="renderFBConnect" data-fb-connect-section="overview">
						<img src="/_assets/global/images/fb-loader.gif" alt="Facebook User" class="fbConnectLoader" />
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</dsp:form>
	<form name="frmUnlinkFB" action="${contextPath}/account/myaccount.jsp" class="hidden" method="post">
		<input name="fbUnlink" type="hidden" id="fbUnlink" value="true" />
		<input name="fbUnlinkSubmit" type="submit" id="fbUnlinkSubmit" value="unlink" aria-pressed="false" aria-labelledby="fbUnlinkSubmit" />
	</form>
	<div id="fbUnlinkModal" title="Are you sure?" class="hidden">
		<p class="bold marBottom_20"><bbbl:label key="lbl_confirm_facebook_unlink" language ="${pageContext.request.locale.language}"/></p>
		<div class="noMarBot formRow clearfix">
			<div class="fl width_1 marRight_10">
				<div class="button">
					<input type="button" value='<bbbl:label key="lbl_confirm_facebook_unlink_yes" language ="${pageContext.request.locale.language}"/>' name="fbYes" class="triggerSubmit" data-submit-button="fbUnlinkSubmit" aria-pressed="false" />
				</div>
			</div>
			<div class="fl width_1">
				<div class="button">
					<input type="button" value='<bbbl:label key="lbl_confirm_facebook_unlink_no" language ="${pageContext.request.locale.language}"/>' name="fbNo" class="close-any-dialog" aria-pressed="false" />
				</div>
			</div>
		</div>
	</div>
</dsp:page>			
				