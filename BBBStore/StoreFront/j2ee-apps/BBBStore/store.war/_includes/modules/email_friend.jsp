<%--  
This jsp appears as a popup window when user 
clicks on Email (Registry, Friend) link  --%>
<dsp:page>

<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:importbean bean="/com/bbb/email/EmailHolder"/> 
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />

<dsp:getvalueof id="currentSiteId" bean="Site.id" scope="session"/>

<dsp:getvalueof var="title" bean="EmailHolder.values.title"/>

		<c:set var="eventTypeRegistry"><dsp:valueof value="${fn:escapeXml(param.eventTypeRegistry)}"/></c:set>
		    <c:set var="eventTypeRegistry"><dsp:valueof value="${fn:escapeXml(param.eventTypeRegistry)}"/></c:set>
		    <c:set var="formComponentName"><dsp:valueof value="${fn:escapeXml(param.formComponentName)}"/></c:set>
		    <c:set var="handlerName"><dsp:valueof value="${fn:escapeXml(param.handlerName)}"/></c:set>
		    <c:set var="registryId"><dsp:valueof value="${fn:escapeXml(param.registryId)}"/></c:set>
		    <c:set var="eventType"><dsp:valueof value="${fn:escapeXml(param.eventType)}"/></c:set>
		    <c:set var="dateCheck"><dsp:valueof value="${fn:escapeXml(param.dateCheck)}"/></c:set>
		    <c:set var="daysToNextCeleb"><dsp:valueof value="${fn:escapeXml(param.daysToNextCeleb)}"/></c:set>
		    <c:set var="daysToGo"><dsp:valueof value="${fn:escapeXml(param.daysToGo)}"/></c:set>
		    <c:set var="registryURL"><dsp:valueof value="${param.registryURL}"/></c:set>
		    <c:set var="senderEmail"><dsp:valueof value="${fn:escapeXml(param.senderEmail)}"/></c:set>
		    <c:set var="title"><dsp:valueof value="${fn:escapeXml(param.title)}"/></c:set>
			<c:set var="pRegFirstName"><dsp:valueof value="${fn:escapeXml(param.pRegFirstName)}"/></c:set>
			<c:set var="pRegLastName"><dsp:valueof value="${fn:escapeXml(param.pRegLastName)}"/></c:set>
			<c:set var="coRegFirstName"><dsp:valueof value="${fn:escapeXml(param.coRegFirstName)}"/></c:set>
			<c:set var="coRegLastName"><dsp:valueof value="${fn:escapeXml(param.coRegLastName)}"/></c:set>
			<c:set var="eventDate"><dsp:valueof value="${fn:escapeXml(param.eventDate)}"/></c:set>
			<c:set var="subject"><dsp:valueof value="${fn:escapeXml(param.subject)}"/></c:set>

		   	<%-- Validate external parameters --%>
			<dsp:droplet name="ValidateParametersDroplet">
			    <dsp:param value="registryId;eventTypeRegistry;eventType" name="paramArray" />
			    <dsp:param value="${registryId};${eventTypeRegistry};${eventType}" name="paramsValuesArray" />
			    <dsp:oparam name="error">
			      <dsp:droplet name="RedirectDroplet">
			        <dsp:param name="url" value="/404.jsp" />
			      </dsp:droplet>
			    </dsp:oparam>
		    </dsp:droplet> 

	<div title="${title}">	
	
	<dsp:form id="frmEmailAFriend" name="frmEmailAFriend" method="post">

		<fieldset>
		<legend class="hidden"><bbbl:label key="lbl_email_friend" language="${pageContext.request.locale.language}" /></legend>
	

			<dsp:setvalue  bean="EmailHolder.values.daysToGo" value="${daysToGo}" />
			<dsp:setvalue  bean="EmailHolder.values.eventType" value="${eventType}"/>  
			<dsp:setvalue  bean="EmailHolder.values.registryURL" value="${registryURL}"/>
			<dsp:setvalue  bean="EmailHolder.values.senderEmail" value="${senderEmail}"/>
			<dsp:setvalue  bean="EmailHolder.values.registryId" value="${registryId}" />
			<dsp:setvalue  bean="EmailHolder.formComponentName" value="${formComponentName}" />
			<dsp:setvalue  bean="EmailHolder.handlerName" value="${handlerName}" />
			<dsp:setvalue  bean="EmailHolder.values.title" value="${title}" />                                                  
			<dsp:setvalue  bean="EmailHolder.values.pRegFirstName" value="${pRegFirstName}" />
			<dsp:setvalue  bean="EmailHolder.values.pRegLastName" value="${pRegLastName}" />
			<dsp:setvalue  bean="EmailHolder.values.coRegFirstName" value="${coRegFirstName}" />
			<dsp:setvalue  bean="EmailHolder.values.coRegLastName" value="${coRegLastName}" />
			<dsp:setvalue  bean="EmailHolder.values.eventDate" value="${eventDate}" />
			<dsp:setvalue  bean="EmailHolder.values.eventTypeRegistry" value="${eventTypeRegistry}" />
			<dsp:setvalue  bean="EmailHolder.values.subject" value="${subject}" />                           	          	    
			
			<dsp:droplet name="Switch">
				<dsp:param name="value" param="dateCheck" />
				<dsp:oparam name="true">
				<c:choose>
					<c:when test="${not empty currentSiteId && currentSiteId eq 'BuyBuyBaby'}">
						<c:choose>
							<c:when test="${not empty eventType && eventType eq 'BA1'}">
								<c:set var="dateLabel">
									<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;<bbbl:label key='lbl_regflyout_daystogo_day_until_baby' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'BR1'}">
								<c:set var="dateLabel">
									<bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" />&nbsp;
									<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;<bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:otherwise>
								<c:set var="dateLabel"></c:set>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${not empty eventType && eventType eq 'BRD'}">
								<c:set var="dateLabel">
									<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;
									<bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'BA1'}">
								<c:set var="dateLabel">
									<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;<bbbl:label key='lbl_regflyout_daystogo_day_until_baby' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'COM'}">
								<c:set var="dateLabel">
									<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;
									<bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'OTH'}">
								<c:set var="dateLabel"></c:set>
							</c:when>
							<c:otherwise>
								<c:set var="dateLabel">
									<bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" />&nbsp;<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;
									<bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				</dsp:oparam>
				<dsp:oparam name="false">
				<c:choose>
					<c:when test="${not empty currentSiteId && currentSiteId eq 'BuyBuyBaby'}">
						<c:choose>
							<c:when test="${not empty eventType && eventType eq 'BA1'}">
								<c:set var="dateLabel">
									<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;<bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'BR1'}">
								<c:set var="dateLabel">
									<strong>${daysToNextCeleb}</strong>&nbsp;<bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:otherwise>
								<c:set var="dateLabel"></c:set>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${not empty eventType && (eventType eq 'BRD' || eventType eq 'COM')}">
								<c:set var="dateLabel">
									<bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" />&nbsp;<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;
									<bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'BA1'}">
								<c:set var="dateLabel">
									<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;<bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'BIR'}">
								<c:set var="dateLabel">
									<strong>${daysToNextCeleb}</strong>&nbsp;<bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'RET'}">
								<c:set var="dateLabel">
									<bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" />&nbsp;<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;
									<bbbl:label key='lbl_regflyout_daystogo_rest_of_life' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'ANN'}">
								<c:set var="dateLabel">
									<strong>${daysToNextCeleb}</strong>&nbsp;<bbbl:label key='lbl_regflyout_daystogo_another_year' language="${pageContext.request.locale.language}" />*
								</c:set>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'HSW'}">
								<c:set var="dateLabel">
									<bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" />&nbsp;<strong><dsp:valueof param="daysToGo"/></strong>&nbsp;
									<bbbl:label key='lbl_regflyout_daystogo_new_place' language="${pageContext.request.locale.language}" />
								</c:set>
							</c:when>
							<c:otherwise>
								<c:set var="dateLabel"></c:set>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				</dsp:oparam>
			</dsp:droplet>

			<dsp:setvalue  bean="EmailHolder.values.dateLabel" value="${dateLabel}"/>
			<dsp:setvalue  bean="EmailHolder.values.daysToGo" value="${daysToGo}" />
			<dsp:getvalueof var="formHandler" bean="EmailHolder.formComponentName"/>
			<dsp:getvalueof var="handlerName" bean="EmailHolder.handlerName"/>

			<%-- <dsp:input bean="${formHandler}.successURL" type="hidden" beanvalue="EmailHolder.values.successURL"/>
			<dsp:input bean="${formHandler}.errorURL" type="hidden"  beanvalue="EmailHolder.values.errorURL"/> --%>

			<dsp:input bean="${formHandler}.successURL" type="hidden" value="/store/_includes/email_friend_confirm.jsp"/>
			<dsp:input bean="${formHandler}.errorURL" type="hidden"  value="/store/_includes/email_friend.jsp"/>

   			<p class="atg_store_pageDescription"></p>
 

			<div class="input clearfix formRow marTop_10">
				<div class="label width_2 fl">
					<label id="lblfrndEmail" for="frndEmail"><bbbl:label key="lbl_email_friend" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
				</div>
				<div class="text width_2 fl marRight_20">
					<dsp:input type="email" bean="EmailHolder.values.recipientEmail" id="frndEmail" name="frndEmail">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblfrndEmail errorfrndEmail"/>
                    </dsp:input>
				</div>
				<div class="fl frndEmailInfoTxt">
					<bbbl:textArea key="txt_email_tofriend_instruct" language ="${pageContext.request.locale.language}"/>
				</div>				
			</div>

			<div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label id="lblyourEmail" for="yourEmail"><bbbl:label key="lbl_email_sender" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
				</div>
				<div class="text width_2 fl">
					<dsp:input type="email" bean="EmailHolder.values.senderEmail" id="yourEmail" name="yourEmail">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblyourEmail erroryourEmail"/>
                    </dsp:input>
				</div>			
			</div>
					

			<div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label for="comments"><bbbl:label key="lbl_email_comments" language ="${pageContext.request.locale.language}"/> </label>
				</div>
				
				<dsp:setvalue  bean="EmailHolder.values.message" value =""/>
				<div class="textarea width_4 fl">
					<dsp:textarea type="textarea" bean="EmailHolder.values.message" id="comments" name="comments"></dsp:textarea>
				</div>			
			</div>
			<div id="captchaDiv" class="clearfix noMarTop">
				<div class="input clearfix">
					<div class="label width_2 fl">
						<label id="lblcaptchaAnswer" for="captchaAnswer"><bbbl:label key="lbl_emailafriend_captcha" language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
					</div>
					<div class="text width_3 fl">
						<img width="300" height="100" src="" alt="captcha answer"/>
						<dsp:input type="text" name="captchaAnswer" id="captchaAnswer" value="" iclass="marTop_5" bean="${formHandler}.captchaAnswer" autocomplete="off">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcaptchaAnswer errorcaptchaAnswer"/>
                        </dsp:input>
					</div>			
				</div>
			</div>
			<div class="input clearfix formRow noMarTop">
				<div class="checkEmailFriend clearfix">
                    <div class="checkbox fl">
                        <dsp:input id="emailPromotions" type="checkbox"
                                name="emailPromotions" checked="false" iclass="marRight_5"
                                bean="EmailHolder.ccFlag">
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
			<div class="marTop_20 buttonpane clearfix">
			<div class="ui-dialog-buttonset">
				<div class="button button_active">
					<input type="hidden" name="eventTypeRegistry" value="${eventTypeRegistry}" />
					<input type="hidden" name="registryId" value="${registryId}" />
					<dsp:input type="Submit" bean="${formHandler}.${handlerName}" value="Submit" id="btnSubmit">
					<dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="btnSubmit"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
				</div>
                <a class="buttonTextLink close-any-dialog" href="#" role="link"><bbbl:label key="lbl_profile_Cancel" language="${pageContext.request.locale.language}" /></a>
			</div>
		</div>
		</fieldset>
	
	</dsp:form>
	</div>
		
</dsp:page>