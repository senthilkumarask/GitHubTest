<dsp:page>
    <dsp:importbean bean="/com/bbb/browse/EmailAFriendFormHandler"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
	<dsp:getvalueof var="currentCatalogURL" param="currentCatalogURL"/>
	<dsp:getvalueof var="templateUrlVar" param="templateUrl" />	
    <dsp:getvalueof id="contextroot" idtype="java.lang.String"
                              bean="/OriginatingRequest.contextPath"/>
    <c:set var="section" value="accounts" scope="request" />
    <bbb:pageContainerNoHeaderFooter index="false" follow="false">
        <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
        <jsp:attribute name="section">${section}</jsp:attribute>
        <jsp:attribute name="pageWrapper">${pageWrapper} emailAFriendCatalog</jsp:attribute>
        <jsp:body>
            <div class="clearfix width_6 marLeft_20">
            <style type="text/css">
            <!-- body, html { min-width: inherit; overflow: hidden; } -->
            </style>
            <dsp:form id="frmEmailAFriend" method="post" action="${contextroot}/browse/gadgets/email_friend_confirm.jsp">
                <c:if test="${not empty currentCatalogURL}">
                  <dsp:input bean="EmailAFriendFormHandler.currentCatalogURL" 
                        value="${currentCatalogURL}" type="hidden"/>
                </c:if>
                <c:if test="${not empty templateUrlVar}">
                  <dsp:input bean="EmailAFriendFormHandler.templateUrl" 
                        value="${originatingRequest.contextPath}${templateUrlVar}" type="hidden"/>
                </c:if>
                <fmt:message key="browse_emailAFriend.defaultSubject" var="defaultSubjectText"/>
                
                <c:set var="defaultSubjectTxt"><bbbl:label key='lbl_emailAFriendFormInputs_subject' language='${pageContext.request.locale.language}'/></c:set>
                <dsp:input bean="EmailAFriendFormHandler.subject" value="${defaultSubjectTxt}" type="hidden"/>

                <!--<c:url var="successUrlVar" value="/browse/gadgets/email_friend_confirm.jsp">    
                </c:url>
                <dsp:input bean="EmailAFriendFormHandler.successURL" type="hidden" value="${successUrlVar}"/>
                
                <c:url var="errorUrlVar" value="/browse/gadgets/emailAFriend_catalog.jsp">      
                </c:url>
                <dsp:input bean="EmailAFriendFormHandler.errorURL" type="hidden" value="${errorUrlVar}"/>-->
                <dsp:input bean="EmailAFriendFormHandler.fromPage" type="hidden" value="emailafriendcatalog" />

                <dsp:getvalueof var="url" vartype="java.lang.String" param="product.template.url"/>
                <c:choose>
                  <c:when test="${not empty url}">
                    <%-- Product Template is set --%>
                    <c:set var="cancelUrlTarget" value="${originatingRequest.contextPath}${url}"/>
                    <dsp:input bean="EmailAFriendFormHandler.cancelURL" type="hidden"
                        value="${cancelUrlTarget}?productId=${param.productId}"/>
                  </c:when>
                  <c:otherwise>
                    <%-- Product Template not set --%>
                    <dsp:input bean="EmailAFriendFormHandler.cancelURL" type="hidden"
                      value="${originatingRequest.requestURI}?productId=${param.productId}"/>
                  </c:otherwise>
                </c:choose>

                <p class="atg_store_pageDescription"></p>                
                <div class="error">
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                    <dsp:param bean="EmailAFriendFormHandler.formError" name="value"/>
                    <dsp:oparam name="true">
                        <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
                        <dsp:param bean="EmailAFriendFormHandler.formExceptions" name="exceptions"/>
                        <dsp:oparam name="output">
                            <dsp:valueof param="message"/>
                        </dsp:oparam>
                        </dsp:droplet>
                    </dsp:oparam>
                </dsp:droplet>
                </div>	                
                <input name="productId" type="hidden" value="${productId}"/>
                    <fieldset>
                        <div class="input clearfix formRow marTop_10">
                            <div class="label width_2 fl">
                                <label id="lblfrndEmail" for="frndEmail"><bbbl:label key='lbl_emailafriend_friend_email' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
                            </div>
                            <div class="text width_2 fl marRight_20">
                                <dsp:input type="text" id="frndEmail" name="frndEmail" bean="EmailAFriendFormHandler.recipientEmail">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblfrndEmail errorfrndEmail"/>
                                </dsp:input>
                            </div>	
                            <div class="fl frndEmailInfoTxt"><bbbl:label key='lbl_emailafriend_placeholder' language="${pageContext.request.locale.language}" /></div>
                        </div>
                        <div class="input clearfix formRow noMarTop">
                            <div class="label width_2 fl">
                                <label id="lblyourEmail" for="yourEmail"><bbbl:label key='lbl_emailafriend_your_email' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
                            </div>
                            <div class="text width_2 fl">
                                <dsp:input type="text" id="yourEmail" name="yourEmail" value="" bean="EmailAFriendFormHandler.senderEmail">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblyourEmail erroryourEmail"/>
                                </dsp:input>
                            </div>			
                        </div>
                        <div class="input clearfix formRow noMarTop">
                            <div class="label width_2 fl">
                                <label for="comments"><bbbl:label key='lbl_emailafriend_comments' language="${pageContext.request.locale.language}" /> </label>
                            </div>
                            <div class="textarea width_3 fl">
                                <dsp:textarea name="comments" id="comments" bean="EmailAFriendFormHandler.message" maxlength="1500" style="padding: 1px 2px;"></dsp:textarea>
                            </div>			
                        </div>
                        <div id="captchaDiv" class="clearfix noMarTop">
                            <div class="input clearfix">
                                <div class="label width_2 fl">
                                    <label id="lblcaptchaAnswer" for="captchaAnswer"><bbbl:label key='lbl_emailafriend_captcha' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
                                </div>
                                <div class="text width_3 fl">
                                    <img width="237" src="<c:url value="/simpleCaptcha.png" />" alt="captcha answer">
                                    <dsp:input type="text" name="captchaAnswer" id="captchaAnswer" value="" iclass="marTop_5" bean="EmailAFriendFormHandler.captchaAnswer" autocomplete="off">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcaptchaAnswer errorcaptchaAnswer"/>
                                    </dsp:input>
                                </div>			
                            </div>
                        </div>
                        <dsp:input type="hidden" id="emailType" name="emailType" value="EmailAFriendCatalog" bean="EmailAFriendFormHandler.emailType"/>
                        
                        <%-- Change for BBBSL-3817,  Starts --%>
                        <div class="input clearfix formRow noMarTop">
							<div class="checkEmailFriend clearfix">
		                    	<div class="checkbox fl">
		                        <dsp:input id="emailPromotions" type="checkbox"
		                                name="emailPromotions" checked="false" iclass="marRight_5"
		                                bean="EmailAFriendFormHandler.ccFlag">
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
                        
                        <div class="marTop_20 buttonpane clearfix">
                            <div class="ui-dialog-buttonset">
                                <div class="button button_active">
                                    <dsp:input type="submit"  bean="EmailAFriendFormHandler.send" value="SEND EMAIL" >
                                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                                        <dsp:tagAttribute name="role" value="button"/>
                                        <dsp:param name="isCatalogOrCircularPage" value="${isCatalogOrCircularPage}"></dsp:param>
                                    </dsp:input>
                                </div>
                            </div>
                        </div>
                    </fieldset>
              </dsp:form>
            </div>
        </jsp:body>
    </bbb:pageContainerNoHeaderFooter>
</dsp:page>