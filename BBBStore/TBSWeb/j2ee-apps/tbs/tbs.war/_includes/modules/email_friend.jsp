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
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
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

	<div title="${title}" id="emailFriendFormContainer">	
	
	<dsp:form id="frmEmailAFriend" name="frmEmailAFriend" method="post" action="${contextPath}/_includes/modules/email_friend.jsp">

		<fieldset>

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
					<c:when test="${not empty currentSiteId && currentSiteId eq 'TBS_BuyBuyBaby'}">
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
					<c:when test="${not empty currentSiteId && currentSiteId eq 'TBS_BuyBuyBaby'}">
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

			<dsp:input bean="${formHandler}.successURL" type="hidden" value="/tbs/_includes/email_friend_confirm.jsp"/>
			<dsp:input bean="${formHandler}.errorURL" type="hidden"  value="/tbs/_includes/email_friend.jsp"/>

   			<p class="atg_store_pageDescription"></p>

			<div class="input clearfix row">
				<div class="label small-12 large-2 columns">
					<label id="lblfrndEmail" for="frndEmail"><bbbl:label key="lbl_email_friend" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
				</div>
				<div class="text small-12 large-4 columns">
					<dsp:input type="text" bean="EmailHolder.values.recipientEmail" id="frndEmail" name="frndEmail">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblfrndEmail errorfrndEmail"/>
                    </dsp:input>
				</div>
				<div class="small-12 large-6 columns frndEmailInfoTxt">
					<bbbl:textArea key="txt_email_tofriend_instruct" language ="${pageContext.request.locale.language}"/>
				</div>				
			</div>

			<div class="input clearfix row">
				<div class="label small-12 large-2 columns">
					<label id="lblyourEmail" for="yourEmail"><bbbl:label key="lbl_email_sender" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
				</div>
				<div class="text small-12 large-4 columns end">
					<dsp:input type="text" bean="EmailHolder.values.senderEmail" id="yourEmail" name="yourEmail">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblyourEmail erroryourEmail"/>
                    </dsp:input>
				</div>			
			</div>

			<div class="input clearfix row">
				<div class="label small-12 large-2 columns">
					<label for="comments"><bbbl:label key="lbl_email_comments" language ="${pageContext.request.locale.language}"/> </label>
				</div>
				<div class="textarea small-12 large-8 columns end">
					<dsp:textarea type="textarea" bean="EmailHolder.values.message" id="comments" name="comments"></dsp:textarea>
				</div>			
			</div>
			<div class="input clearfix row">
				<div class="checkEmailFriend clearfix">
                    <div class="checkbox small-12 columns">
                        <dsp:input id="emailPromotions" type="checkbox" name="emailPromotions" checked="true" bean="EmailHolder.ccFlag">
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblemailPromotions"/>
                        </dsp:input>
                    	<label id="lblemailPromotions" for="emailPromotions"><bbbl:label key="lbl_email_checkbox_copy_email" language ="${pageContext.request.locale.language}"/></label>
                    </div>
                    <div class="clear"></div>
				</div>
			</div>
			<div class="row">
				<div class="small-6 large-3 columns">
					<input type="hidden" name="eventTypeRegistry" value="${eventTypeRegistry}" />
					<input type="hidden" name="registryId" value="${registryId}" />
	             	<dsp:input type="hidden" value="submit" bean="${formHandler}.${handlerName}" name="sendRegistryEmail" id="sendRegistryEmail"/>
	             	<input type="submit" value="Submit" name="sendEmailButton" id="sendEmailButton" class="button tiny service expand">
	            </div>
				<div class="small-6 large-3 columns end">
                	<a class="button tiny download expand close-modal" href="#" role="link">Cancel</a>
                </div>
			</div>
		</fieldset>
	
	</dsp:form>
	</div>
		
</dsp:page>