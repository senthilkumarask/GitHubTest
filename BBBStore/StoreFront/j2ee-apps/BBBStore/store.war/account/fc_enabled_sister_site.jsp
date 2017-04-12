<dsp:page>
	<%@ page import="com.bbb.social.facebook.FBConstants"%>
	<dsp:importbean bean="/com/bbb/social/facebook/FBUserSiteAssocDroplet"/>
	<dsp:importbean var="ProfileFormHandler"	bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/com/bbb/social/facebook/FBConnectFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		
	<dsp:droplet name="FBUserSiteAssocDroplet">
	<dsp:param name="profileLookup" value="<%=FBConstants.PROFILE_LOOKUP_BASED_ON_FBACCOUNTID%>"/>
	<dsp:param name="siteId" bean="Site.id"/>
	<dsp:oparam name="output">
	<div title='<bbbl:label key="lbl_fb_extend_profile_title" language ="${pageContext.request.locale.language}"/>'>
		<jsp:useBean id="customKeys" class="java.util.HashMap" scope="request"/>
		<dsp:setvalue bean="FBConnectFormHandler.getBBBProfileEmailId" />
		<dsp:getvalueof bean="FBConnectFormHandler.emailAddress" var="emailAddress" />
		
		<c:set target="${customKeys}" property="emailAddress">${emailAddress}</c:set>
		
		<bbbl:textArea key="txt_fb_enable_sister_site" language ="${pageContext.request.locale.language}" placeHolderMap="${customKeys}"/>
		
		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
			<dsp:oparam name="output">
			<LI>
				<dsp:oparam name="outputStart">					
				</dsp:oparam>
				<b><dsp:valueof param="message" /> </b>
				<br>
			</LI>
			</dsp:oparam>
			<dsp:oparam name="outputEnd">				
			</dsp:oparam>			
		</dsp:droplet>
		<dsp:form id="frmExtendAccountP" action="${contextPath}/account/login.jsp"	method="post">
			<dsp:input bean="ProfileFormHandler.emailAddress" type="hidden" value="${sessionScope.FB_BASIC_INFO.email}"/>
			<div class="formRow clearfix">
				<div class="button fl marRight_10">
					<dsp:input bean="ProfileFormHandler.extendAccount" type="submit"	name="btnExtendAccount" value="Extend Account" >
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="btnExtendAccount"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
				</div>
				<div class="button">
					<input type="button" id="btnCancel" value="Cancel"	class="close-any-dialog" role="button" aria-pressed="false" aria-labelledby="btnCancel" />
				</div>
			</div>
			
		</dsp:form>
	</div>
	</dsp:oparam>
	</dsp:droplet>
</dsp:page>