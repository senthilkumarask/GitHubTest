<%--  
This jsp appears as a popup window when user 
clicks on Email (Registry, Friend) link  --%>
<dsp:page>

<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<%-- <dsp:importbean bean="/com/bbb/email/EmailHolder"/> --%>
<dsp:importbean bean="/com/bbb/cms/email/EmailAPageFormHandler"/>

<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof bean="Site.id" id="siteId"/>	
<%-- <dsp:getvalueof var="title" bean="EmailHolder.values.title"/> --%>

<%-- 	<div title="${title}"> --%>
<div>
	<bbbl:textArea key="txt_email_tofriend_instruct" language ="${pageContext.request.locale.language}"/>
	<dsp:form id="frmEmailAFriend" method="post" action="${contextroot}/emailtemplates/emailapage/emailAPage_cms_confirm.jsp">

		<dsp:getvalueof var="templateUrlVar" param="templateUrl" />
		<c:if test="${not empty templateUrlVar}">
			<dsp:input bean="EmailAPageFormHandler.templateUrl" value="${originatingRequest.contextPath}${templateUrlVar}" type="hidden" />
<%-- 			<dsp:input bean="EmailAPageFormHandler.siteId" value="${siteId}" type="hidden" /> --%>
		</c:if>

		<fieldset>

			<c:set var="defaultSubjectTxt"><bbbl:label key='lbl_emailAFriendFormInputs_subject' language='${pageContext.request.locale.language}'/></c:set>
    		<dsp:input bean="EmailAPageFormHandler.subject" value="${defaultSubjectTxt}" type="hidden"/>
			
			<!--<dsp:input bean="EmailAPageFormHandler.successURL" type="hidden" value="/emailtemplates/emailapage/emailAPage_cms_confirm.jsp"/>
			<dsp:input bean="EmailAPageFormHandler.errorURL" type="hidden" value="/emailtemplates/emailapage/emailAPage_cms.jsp"/>-->
			<dsp:input bean="EmailAPageFormHandler.fromPage" type="hidden" value="emailapagecms" />

   			<p class="atg_store_pageDescription"></p>
 

			<div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label id="lblfrndEmail" for="frndEmail"><bbbl:label key="lbl_email_friend" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
				</div>
				<div class="text width_3 fl">
					<dsp:input type="text" bean="EmailAPageFormHandler.recipientEmail" id="frndEmail" name="frndEmail">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblfrndEmail errorfrndEmail"/>
                    </dsp:input>
				</div>			
			</div>

			<div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label id="lblyourEmail" for="yourEmail"><bbbl:label key="lbl_email_sender" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
				</div>
				<div class="text width_3 fl">
					<dsp:input type="text" bean="EmailAPageFormHandler.senderEmail" id="yourEmail" name="yourEmail" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblyourEmail erroryourEmail"/>
                    </dsp:input>
				</div>			
			</div>
					

			<div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label for="comments"><bbbl:label key="lbl_email_comments" language ="${pageContext.request.locale.language}"/></label>
				</div>
				<div class="textarea width_4 fl">
					<dsp:textarea type="textarea" bean="EmailAPageFormHandler.message" id="comments" name="comments"></dsp:textarea>
				</div>			
			</div>
			<%-- <div id="captchaDiv" class="clearfix formRow"></div> --%>
			<div id="captchaDiv" class="clearfix formRow noMarTop">
				<div class="input clearfix">
					<div class="label width_2 fl">
						<label id="lblcaptchaAnswer" for="captchaAnswer">Captcha <span class="required">*</span></label>
					</div>
					<div class="text width_3 fl">
						<img  width="300" height="100" src="<c:url value="/simpleCaptcha.png" />" />
						<dsp:input type="text" name="captchaAnswer" id="captchaAnswer" value="" iclass="marTop_5" bean="EmailAPageFormHandler.captchaAnswer" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcaptchaAnswer errorcaptchaAnswer"/>
                        </dsp:input>
					</div>			
				</div>
			</div>
			
			<%-- Change for BBBSL-3817,  Starts --%>
			<div class="input clearfix formRow noMarTop">
				<div class="checkEmailFriend clearfix">
                    <div class="checkbox fl">
                        <dsp:input id="emailPromotions" type="checkbox"
                                name="emailPromotions" checked="false" iclass="marRight_5"
                                bean="EmailAPageFormHandler.ccFlag">
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblemailPromotions"/>
                        </dsp:input>
                    </div>
                    <div class="label fl">
                        <label id="lblemailPromotions" for="emailPromotions"><bbbl:label key="lbl_email_checkbox_copy_email" language ="${pageContext.request.locale.language}"/></label>
                    </div>
                    <div class="clear"></div>
				</div>
			</div>
			<%-- Change for BBBSL-3817,  Ends --%>
			
			<div class="formRow clearfix">
				<div class="button fl marRight_10">
					<dsp:input type="Submit" bean="EmailAPageFormHandler.send" value="SUBMIT" id="btnSubmit"  title="Submit">
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="btnSubmit"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
				</div>
				<div class="button fl">
					<input type="button" id="btnCancel" value="Cancel" class="close-any-dialog" role="button" aria-pressed="false" aria-labelledby="btnCancel" />
				</div>
			</div>
		</fieldset>
	
	</dsp:form>
	</div>
		
</dsp:page>