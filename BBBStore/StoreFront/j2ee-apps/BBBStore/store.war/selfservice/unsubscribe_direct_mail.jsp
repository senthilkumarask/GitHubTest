<dsp:page>    
<%@ page import="com.bbb.constants.BBBCoreConstants" %>
<dsp:importbean bean="/com/bbb/selfservice/StateDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean var="SubscriptionFormHandler" bean="/com/bbb/selfservice/SubscriptionFormHandler"/>

<bbb:pageContainer>
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="pageWrapper">email-signup</jsp:attribute>
    <jsp:attribute name="section">accounts</jsp:attribute>
    <jsp:body>
    <dsp:getvalueof id="currentSiteId" bean="Site.id" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <c:set var="subscriptionType" value="<%=BBBCoreConstants.SUBSCRIPTION_TYPE.TYPE_UNSUBSCRIBE_DIRECTMAIL %>" scope="page" />
    
    <div id="content" class="container_12 clearfix cardView" role="main">
        <div class="grid_10 suffix_2"> 
            <h1><bbbl:label key="lbl_unsubscribe_directmail_list" language ="${pageContext.request.locale.language}"/></h1>
            <c:choose> 
            	 <c:when test="${SubscriptionFormHandler.successMessage == true}" >
             		 
            		<bbbt:textArea key="txt_unsubscribe_dm_confirmation_module" language ="${pageContext.request.locale.language}"/>
             		 <dsp:setvalue bean="SubscriptionFormHandler.successMessage" value="false"/> 
             	</c:when>
             	<c:otherwise>
             <bbbt:textArea key="txt_unsubscribe_dm_from_our_direct_mail_list" language ="${pageContext.request.locale.language}"/>
        </div>
        <dsp:form id="directEmailInfo" action="unsubscribe_direct_mail.jsp" method="post">

			    <dsp:include page="/global/gadgets/errorMessage.jsp">
			      <dsp:param name="formhandler" bean="SubscriptionFormHandler"/>
			    </dsp:include>
		
            <div class="grid_10 suffix_2">
                <div class="grid_3 suffix_2 alpha">
                    <div class="cardView"> 
                <dsp:input bean="SubscriptionFormHandler.type" type="hidden" value="${subscriptionType}"/>    
                <!--<dsp:input bean="SubscriptionFormHandler.subscriptionSuccessURL" type="hidden" value="unsubscribe_direct_mail.jsp"/>
				<dsp:input bean="SubscriptionFormHandler.subscriptionErrorURL" type="hidden" value="unsubscribe_direct_mail.jsp"/>-->
				 <dsp:input bean="SubscriptionFormHandler.fromPage" type="hidden" value="unsubscribedirectmail" />
				 
                        <dsp:getvalueof id="error" bean="SubscriptionFormHandler.errorMap"/>
                        <div class="input clearfix labelGrid2">
                            <div class="label">
                               <label id="lblemailaddress" for="emailaddress"><span class="required">&dagger;</span> <bbbl:label key="lbl_subscribe_email" language ="${pageContext.request.locale.language}"/></label>
                            </div>
                            <div class="text">
                                <div>
                                    <c:choose>
		                            		<c:when test="${!(empty error)}">
		                            			<dsp:input id="emailaddress" bean="SubscriptionFormHandler.emailAddr" name="emailaddress" type="text" iclass="error">
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblemailaddress erroremailaddress"/>
                                                </dsp:input>
		                            		</c:when>
		                            		<c:otherwise>
		                            			<dsp:input id="emailaddress" bean="SubscriptionFormHandler.emailAddr" name="emailaddress" type="text">
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblemailaddress erroremailaddress"/>
                                                </dsp:input>
		                            		</c:otherwise>
		                            	</c:choose>
										
                                </div>
                            </div>
                        </div>
                        <div class="input clearfix labelGrid2">
                            <div class="label">
                               <label id="lblfirstName" for="firstName"><bbbl:label key="lbl_profile_firstname" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                            </div>
                            <div class="text">
                                <div>
                                   <dsp:input id="firstName" bean="SubscriptionFormHandler.firstName" name="firstName" type="text">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
                                    </dsp:input>
                                </div>
                            </div>
                        </div>
                        <div class="input clearfix labelGrid2">
                            <div class="label">
                                <label id="lbllastName" for="lastName"><bbbl:label key="lbl_profile_lastname" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                            </div>
                            <div class="text">
                                <div>
                                    <dsp:input id="lastName" bean="SubscriptionFormHandler.lastName" name="lastName" type="text">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
                                    </dsp:input>
                                </div>
                            </div>
                        </div>
                         <div class="clear"></div>
                        <div class="input clearfix">
                        <div class="label">
                            <label id="lblcellPhone1" for="cellPhone1"><bbbl:label key="lbl_subscribe_mobile" language ="${pageContext.request.locale.language}"/></label>
                            <label id="lblcellPhone2" for="cellPhone2" class="cellFirst">First</label>
                            <label id="lblcellPhone3" for="cellPhone3" class="cellFirst">Last</label>
                        </div>
                        <div class="grid_3 alpha phone fl">
                            <div class="text fl">
                                <input id="cellPhone1" type="text" name="cellPhone1" maxlength="3" class="mobilePhone" aria-required="false" aria-labelledby="lblcellPhone1 errorcellPhone1" />
                                <span class="dateSeperator">-</span> </div>
                            <div class="text fl">
                                <input id="cellPhone2" type="text" name="cellPhone2" maxlength="3" class="mobilePhone" aria-required="false" aria-labelledby="lblcellPhone2 errorcellPhone2" />
                                <span class="dateSeperator">-</span> </div>
                            <div class="text fl">
                                <input id="cellPhone3" type="text" name="cellPhone3" maxlength="4" class="mobilePhone" aria-required="false" aria-labelledby="lblcellPhone3 errorcellPhone3" />
                            </div>
                            <dsp:input bean="SubscriptionFormHandler.mobileNumber" name="cell" type="hidden"/>
                            <div class="clearfix">
                                <label id="errorcellPhone1" for="cellPhone1" class="error" generated="true"></label>
                                <label id="errorcellPhone2" for="cellPhone2" class="error" generated="true"></label>
                                <label id="errorcellPhone3" for="cellPhone3" class="error" generated="true"></label>
                            </div>
                        </div>
                    </div>
                        <div class="grid_7 alpha input add text"> 
                            
                            <div class="input clearfix labelGrid2">
                                <div class="label">
                                   <label id="lbladd1" for="add1"><bbbl:label key="lbl_subscribe_address_line1" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                                </div>
                                <div class="text">
                                    <div>
                                        <dsp:input id="add1" bean="SubscriptionFormHandler.addressLine1" name="add1" type="text">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbladd1 erroradd1"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="input clearfix labelGrid2">
                                <div class="label">
                                    <label id="lbladd2" for="add2"><bbbl:label key="lbl_subscribe_address_line1" language ="${pageContext.request.locale.language}"/></label>
                                </div>
                                <div class="text">
                                    <div>
                                        <dsp:input id="add2" bean="SubscriptionFormHandler.addressLine2" name="add2" type="text">
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbladd2 erroradd2"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </div>
                            
                        </div>
                        
                        <div class="grid_7 alpha">
                        <div class="input clearfix labelGrid2">
                            <div class="label">
                                <label id="lblcity" for="city"><bbbl:label key="lbl_subscribe_city" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                            </div>
                            <div class="text">
                                <div>
                                    <dsp:input id="city" bean="SubscriptionFormHandler.city" name="city" type="text">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcity errorcity"/>
                                    </dsp:input>
                                </div>
                            </div>
                        </div>
                        </div>
                        
                        <div class="input clearfix labelGrid2">
							<c:choose>
							<c:when test="${currentSiteId eq 'BedBathCanada'}">
                            <label id="lblstate" for="state"><bbbl:label key="lbl_shipping_new_province" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                            </c:when>
                            <c:otherwise>
                            <label id="lblstate" for="state"><bbbl:label key="lbl_subscribe_state" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                            </c:otherwise>
                            </c:choose>
                           
                            <div class="select">
                                <div class="grid_2 alpha suffix_3">
                               
							<dsp:select bean="SubscriptionFormHandler.state" id="state" name="stateName" iclass="required">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblstate errorstate"/>
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
									</dsp:select>
                                </div>
                            </div>
                        </div>
                        
                        <div class="input clearfix labelGrid2">
							<c:choose>
								<c:when test="${currentSiteId eq 'BedBathCanada'}">
									<div class="label">
									   <label id="lblzipCA" for="zipCA"><bbbl:label key="lbl_addcreditcard_postal_code" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
									</div>
									<div class="text">
										<div>
											<dsp:input id="zipCA" bean="SubscriptionFormHandler.zipcode" iclass="copy2Field" name="zipCA" type="text">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblzipCA errorzipCA"/>
                                            </dsp:input>
										</div>
									</div>
								</c:when>
								<c:otherwise>
									<div class="label">
									   <label id="lblzipUS" for="zipUS"><bbbl:label key="lbl_subscribe_zipcode" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
									</div>
									<div class="text">
										<div>
											<dsp:input id="zipUS" bean="SubscriptionFormHandler.zipcode" name="zipUS" type="text">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblzipUS errorzipUS"/>
                                            </dsp:input>
										</div>
									</div>
								</c:otherwise>
							</c:choose>	
                            
                        </div>
                        
                    </div>
                </div>
                
                <bbbt:textArea key="txt_unsubscribe_dm_not_a_required_field_message" language ="${pageContext.request.locale.language}" />
            </div>
            
                <div class="button button_active clearfix">
                    <dsp:input type="submit" bean="SubscriptionFormHandler.unSubscribeDirectMail" value="SUBMIT" id="Submit">
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="Submit"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
                </div>
            <div class="clear"></div>
            <c:if test="${EmailOn}">
            <div class="marTop_20">
            	<bbbt:textArea key="txt_unsubscribe_dm_tounsubdirectmail" language ="${pageContext.request.locale.language}" />
            </div>
            </c:if>
        </dsp:form>
        </c:otherwise>
        </c:choose>
    </div>
    
 </jsp:body>
</bbb:pageContainer>
</dsp:page>    
      