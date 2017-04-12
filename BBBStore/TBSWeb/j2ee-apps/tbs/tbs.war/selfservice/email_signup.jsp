<dsp:page>
<%@ page import="com.bbb.constants.BBBCoreConstants" %>
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
    <dsp:importbean var="SubscriptionFormHandler" bean="/com/bbb/selfservice/SubscriptionFormHandler"/>
    <bbb:pageContainer index="false" follow="false">
        <jsp:attribute name="bodyClass">my-account</jsp:attribute>
        <jsp:attribute name="pageWrapper">email-signup</jsp:attribute>
        <jsp:attribute name="section">accounts</jsp:attribute>
        <jsp:body>
        <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
            <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
            <c:set var="subscriptionType" value="<%=BBBCoreConstants.SUBSCRIPTION_TYPE.TYPE_SUBSCRIBE_EMAIL_DIRECTMAIL %>" scope="page" />
            
            <div id="content" class="container_12 clearfix cardView" role="main">
                <div class="grid_10 suffix_2 spacing"> 
                    <h3><bbbl:label key="lbl_subscribe_email_and_deirectemail" language ="${pageContext.request.locale.language}"/></h3>
                    <c:choose>
                        <c:when test="${SubscriptionFormHandler.successMessage == true}" >
                            <bbbt:textArea key="txt_subscription_successful_module" language ="${pageContext.request.locale.language}"/>
                           
                            <c:if test="${empty added}">
								<dsp:getvalueof id="subscribe" value="${sessionScope.subscribe}"/>
								<c:if test="${!(empty subscribe)}">
									<script type="text/javascript">    
										var pageAction = "336";
									</script>
								</c:if>
								<dsp:getvalueof id="added" value="added" scope="session"/>
							</c:if>
							
                        </c:when>
                        <c:otherwise>
                        
                   <p class="txtHighlight"><bbbl:label key="lbl_subscribe_personal_contact_info" language ="${pageContext.request.locale.language}"/></p>
                    <div class="cardView">
                        <dsp:form id="emailInfo" action="email_signup.jsp" method="post">
                        	<dsp:input bean="SubscriptionFormHandler.type" type="hidden" value="${subscriptionType}"/>
                           <!--   <dsp:input bean="SubscriptionFormHandler.subscriptionSuccessURL" type="hidden" value="email_signup.jsp"/>
                            <dsp:input bean="SubscriptionFormHandler.subscriptionErrorURL" type="hidden" value="email_signup.jsp"/>-->
                             <dsp:input bean="SubscriptionFormHandler.fromPage" type="hidden" value="emailsignup" />
                            <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach" >
                                <dsp:oparam name="output"> <b>
                                    <dsp:valueof param="message"/>
                                    </b><br>
                                </dsp:oparam>
                                <dsp:oparam name="outputStart">
                                    <LI class="error"> 
                                </dsp:oparam>
                                <dsp:oparam name="outputEnd">
                                    </LI>
                                </dsp:oparam>
                            </dsp:droplet>
                            <div class="input clearfix labelGrid2">
                                <div class="label">
                                    <label id="lblemail" for="email"><bbbl:label key="lbl_subscribe_email" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
                                </div>
                                <div class="text">
                                    <div>
                                        <dsp:input id="email" bean="SubscriptionFormHandler.emailAddr" name="email" type="text">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </div>
                            <div class="clear"></div>
                            <div class="input clearfix labelGrid2">
                                <div class="label">
                                   <label id="lblemailConfirm" for="emailConfirm"><bbbl:label key="lbl_subscribe_confirmemail" language ="${pageContext.request.locale.language}"/><span class="required">*</span></label>
                                </div>
                                <div class="text">
                                    <div>
                                        <dsp:input id="emailConfirm" bean="SubscriptionFormHandler.confirmEmailAddr" name="emailConfirm" type="text">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblemailConfirm erroremailConfirm"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </div>
                            <div class="clear"></div>
                            <div class="input clearfix">
                                <div class="label">
                                    <label id="lblsalutation" for="salutation"><bbbl:label key="lbl_subscribe_salutation" language ="${pageContext.request.locale.language}"/></label>
                                </div>
                                <div class="select">
                                    <div class="grid_2 alpha suffix_3">
                                        <dsp:select bean="SubscriptionFormHandler.selectedSalutation" id="salutation" name="salutation">
                                            <dsp:option value=""><bbbl:label key="lbl_subscribe_select_salutation" language ="${pageContext.request.locale.language}"/> </dsp:option>
                                            <dsp:droplet name="ForEach">
												<dsp:param name="array" bean="SubscriptionFormHandler.salutation" />
												<dsp:oparam name="output">
													<dsp:getvalueof param="element" id="elementVal">
														<dsp:option value="${elementVal}">
															${elementVal}
														</dsp:option>
													</dsp:getvalueof>
												</dsp:oparam>
											</dsp:droplet>
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsalutation errorsalutation"/>
                                        </dsp:select>
                                      </div>
                                </div>
                            </div>
                            <div class="clear"></div>
                            <div class="input clearfix labelGrid2">
                                <div class="label">
                                   <label id="lblfirstName" for="firstName"><bbbl:label key="lbl_profile_firstname" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                                </div>
                                <div class="text">
                                    <div>
                                        <dsp:input bean="SubscriptionFormHandler.firstName" name="firstName" id="firstName" type="text">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </div>
                            <div class="clear"></div>
                            <div class="input clearfix labelGrid2">
                                <div class="label">
                                    <label id="lbllastName" for="lastName"><bbbl:label key="lbl_profile_lastname" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                                </div>
                                <div class="text">
                                    <div>
                                        <dsp:input bean="SubscriptionFormHandler.lastName" name="lastName" id="lastName" type="text">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </div>
                            <div class="clear"></div>
                           <div class="grid_7 alpha"> 
                                <div class="input clearfix labelGrid2">
		                            <div class="label">
		                                <label id="lbladd1" for="add1"><bbbl:label key="lbl_subscribe_address_line1" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
		                            </div>
		                            <div class="text">
		                                <div>
		                                    <dsp:input bean="SubscriptionFormHandler.addressLine1" name="add1" id="add1" type="text">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbladd1 erroradd1"/>
                                            </dsp:input>
		                                </div>
		                            </div>
		                        </div>
		                        <div class="input clearfix labelGrid2">
		                            <div class="label">
		                                <label id="lbladd2" for="add2"><bbbl:label key="lbl_subscribe_address_line2" language ="${pageContext.request.locale.language}"/></label>
		                            </div>
		                            <div class="text">
		                                <div>
		                                     <dsp:input bean="SubscriptionFormHandler.addressLine2" name="add2" id="add2" type="text">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbladd2 erroradd2"/>
                                            </dsp:input>
		                                </div>
		                            </div>
		                        </div>
		                    </div>
                            <div class="clear"></div>
                            <div class="input clearfix labelGrid2">
                                <div class="label">
                                    <label id="lblcity" for="city"><bbbl:label key="lbl_subscribe_city" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                                </div>
                                <div class="text">
                                    <div>
                                        <dsp:input bean="SubscriptionFormHandler.city" name="city" id="city" type="text">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblcity errorcity"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </div>
                          <div class="input clearfix labelGrid2">
                            <c:choose>
                			<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
                            <label id="lblstate" for="state"><bbbl:label key="lbl_shipping_new_province" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                            </c:when>
                            <c:otherwise>
                            <label id="lblstate" for="state"><bbbl:label key="lbl_subscribe_state" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                            </c:otherwise>
                            </c:choose>
                           
                           
                            <div class="select">
                                <div class="grid_2 alpha suffix_3">
                               
									<dsp:select  bean="SubscriptionFormHandler.state" id="state" name="stateName">
										<dsp:droplet name="StateDroplet">
											<dsp:oparam name="output">
											    <dsp:option value=""><bbbl:label key="lbl_subscribe_select_state" language ="${pageContext.request.locale.language}"/></dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="location" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="element.stateName" id="elementVal">
														<dsp:option value="${elementVal}">
															${elementVal}
														</dsp:option>
														</dsp:getvalueof>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblstate errorstate"/>
									</dsp:select>
                                </div>
                            </div>
                        </div>
                            
                            
                            <div class="clear"></div>
                            <c:set var="TBS_BedBathCanadaSite">
								<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
							</c:set>
                            <div class="input clearfix labelGrid2">
                                <div class="label">
                                    <label id="lblzip" for="zip">
                                   	<c:choose>
                                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
	                                    <bbbl:label key="lbl_subscribe_canadazipcode" language ="${pageContext.request.locale.language}"/>
	                                    <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
	                                    <div class="text">
                                    	<div>
                                        	<dsp:input bean="SubscriptionFormHandler.zipcode" name="zipCA" id="zip" type="text">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
                                            </dsp:input>
                                    	</div>
                                		</div>
                                    </c:when>
                                    <c:otherwise>
                                        <bbbl:label key="lbl_subscribe_zipcode" language ="${pageContext.request.locale.language}"/>
                                        <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
	                                    <div class="text">
                                    	<div>
                                        	<dsp:input bean="SubscriptionFormHandler.zipcode" name="zipUS" id="zip" type="text">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
                                            </dsp:input>
                                    	</div>
                                		</div>
                                    </c:otherwise>
                                    </c:choose>
                                    </label>
                                </div>
                            </div>
                            <div class="clear"></div>
                            <div class="input clearfix phoneFieldWrap">
                                <fieldset class="phoneFields">
									<legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend>
									<div class="label">
										<label id="lblbasePhoneFull" for="basePhoneFull"><bbbl:label key="lbl_subscribe_phone" language ="${pageContext.request.locale.language}"/></label>
									</div>
									<div class="grid_3 alpha phone fl">
										<div class="text fl">
											<input id="basePhoneFull" type="text" name="basePhoneFull" class="phoneField" maxlength="10" aria-required="false" aria-labelledby="lblbasePhoneFull errorbasePhoneFull" />
										</div>
										<dsp:input bean="SubscriptionFormHandler.phoneNumber" name="phone" type="hidden" iclass="fullPhoneNum"/>
										<div class="cb">
											<label id="errorbasePhoneFull" for="basePhoneFull" class="error" generated="true"></label>
										</div>
									</div>
                                </fieldset>
                            </div>
                            <div class="input clearfix phoneFieldWrap">
                                <fieldset class="phoneFields">
									<legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_cellnumber' language='${pageContext.request.locale.language}'/></legend>
                                <div class="label">
                                    <label id="lblcellPhoneFull" for="cellPhoneFull"><bbbl:label key="lbl_subscribe_mobile" language ="${pageContext.request.locale.language}"/></label>
                                </div>
                                <div class="grid_3 alpha phone fl">
                                    <div class="text fl">
                                        <input id="cellPhoneFull" type="text" name="cellPhoneFull" class="mobilePhone phoneField" maxlength="10" aria-required="false" aria-labelledby="lblcellPhoneFull errorcellPhoneFull" />
                                    </div>
                                    <dsp:input bean="SubscriptionFormHandler.mobileNumber" iclass="fullPhoneNum" name="cell" type="hidden"/>
                                    <div class="cb">
                                        <label id="errorcellPhoneFull" for="cellPhoneFull" class="error" generated="true"></label>
                                    </div>
                                </div>
                                </fieldset>
                            </div>
                            <div class="clear"></div>
                            <bbbt:textArea key="txt_subscription_receive" language ="${pageContext.request.locale.language}"/>
                            <div class="clear"></div>
                            <div class="label labelGrid4 text">
                                <dsp:input type="checkbox" checked="true" bean="SubscriptionFormHandler.emailOffer" name="promoOfr">
                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                </dsp:input>
                                 <label><bbbl:label key="lbl_subscribe_news_and_promotionaloffers" language ="${pageContext.request.locale.language}"/></label>
                            </div>
                            <div class="clear"></div>
                            <div class="label input text labelGrid4">
                                <dsp:input type="checkbox" checked="true" bean="SubscriptionFormHandler.directMailOffer" name="directMailOffer">
                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                </dsp:input>
                                <label><bbbl:label key="lbl_subscribe_circulars" language ="${pageContext.request.locale.language}"/></label>
                            </div>
                            <div class="clear"></div>
                            <p class="grid_6 alpha"><bbbt:textArea key="txt_subscription_pleasenote" language ="${pageContext.request.locale.language}"/></p> 
                            <div class="clear"></div>
                            <div class="label input text labelGrid4">
                                <dsp:input type="checkbox" bean="SubscriptionFormHandler.mobileOffer" name="confirmMobile">
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                </dsp:input>
                                <label><bbbl:label key="lbl_subscribe_mobile_offers" language ="${pageContext.request.locale.language}"/></label> 
                            </div>
                            <div class="clear"></div>
                            <bbbt:textArea key="txt_subscription_bbb_ maydeliveroffersviamobile" language ="${pageContext.request.locale.language}"/>
                            <div class="clear"></div>
                            <div class="label labelGrid2 clearfix">
                                <div class="button button_active">
                                    <dsp:input type="submit" bean="SubscriptionFormHandler.subscribe" value="SUBMIT" id="Submit">
                                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                                        <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
                                </div>
                            </div>
                            <div class="clear"></div>
                            <c:if test="${EmailOn}">
                            <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
							<c:set target="${placeHolderMap}" property="linkUnsubscribeEMail" value='<a class="emailFix" href="${pageContext.request.contextPath}/selfservice/unsubscribe_email.jsp" title="email unsubscribe">email unsubscribe page</a>'/>
							<c:set target="${placeHolderMap}" property="linkUnsubscribeDirectMail" value='<a class="emailFix" href="${pageContext.request.contextPath}/selfservice/unsubscribe_direct_mail.jsp" title="direct email unsubscribe">direct email unsubscribe page</a>'/>
                            <bbbt:textArea key="txt_subscription_email_unsubscribe" language="<c:out param='${pageContext.request.locale.language}'/>" placeHolderMap="${placeHolderMap}"/>
                            <bbbt:textArea key="txt_subscription_directmail_unsubscribe" language="<c:out param='${pageContext.request.locale.language}'/>" placeHolderMap="${placeHolderMap}"/>
                            </c:if>
                        </dsp:form>
                    </div>
                    </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <c:if test="${SubscriptionFormHandler.successMessage eq true}" >
            	<script type="text/javascript">
					BBB.tracking = 'email signup';
				</script>
			</c:if>
         </jsp:body>
    </bbb:pageContainer>
	<c:if test="${TellApartOn}">
		<bbb:tellApart actionType="pv" />		
	</c:if>
</dsp:page>
