<dsp:page>    
<%@ page import="com.bbb.constants.BBBCoreConstants" %>


<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean var="SubscriptionFormHandler" bean="/com/bbb/selfservice/SubscriptionFormHandler"/>

<bbb:pageContainer>
  	<jsp:attribute name="bodyClass">my-account</jsp:attribute>
    <jsp:attribute name="pageWrapper">email-signup</jsp:attribute>
    <jsp:attribute name="section">accounts</jsp:attribute>
    <jsp:body>
    
     <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
     <c:set var="subscriptionType" value="<%=BBBCoreConstants.SUBSCRIPTION_TYPE.TYPE_UNSUBSCRIBE_EMAIL %>" scope="page" />
     <c:set var="frequencyTwiceAMonth" value="<%=BBBCoreConstants.FREQUENCY.FREQUENCY_TWICE_A_MONTH %>" scope="page" />
     <c:set var="frequencyOnceAMonth" value="<%=BBBCoreConstants.FREQUENCY.FREQUENCY_ONCE_A_MONTH %>" scope="page" />
     <c:set var="frequencyCoupons" value="<%=BBBCoreConstants.FREQUENCY.FREQUENCY_ONLINE_GIFTCERTIFICATE_COUPONS %>" scope="page" />
     <c:set var="frequencyUnsubscribeAll" value="<%=BBBCoreConstants.FREQUENCY.UNSUBSCRIBE_ALL_EMAILS %>" scope="page" />
    
    <div id="content" class="container_12 clearfix creditCard" role="main">
        <div class="grid_10 suffix_2"> 
            <h1><bbbl:label key="lbl_unsubscribe_from_email" language ="${pageContext.request.locale.language}"/></label></h1>
            <c:choose> 
            	 <c:when test="${SubscriptionFormHandler.successMessage and SubscriptionFormHandler.frequency ne 'UNSUBSCRIBE_ALL_EMAILS'}" >
             		 <bbbt:textArea key="txt_unsubscribe_email_sub_changed_module" language ="${pageContext.request.locale.language}"/>
             		 <dsp:setvalue bean="SubscriptionFormHandler.successMessage" value="false"/> 
             	</c:when>
             	<c:when test="${SubscriptionFormHandler.successMessage and SubscriptionFormHandler.frequency eq 'UNSUBSCRIBE_ALL_EMAILS'}" >
	             	<bbbt:textArea key="txt_unsubscribe_email_unsub_confirmation_module" language ="${pageContext.request.locale.language}"/>
             	</c:when>
             	<c:otherwise>
                <h5 class="textHighlightBig"><bbbl:label key="lbl_unsubscribe_email_changefrequencyinstead" language ="${pageContext.request.locale.language}"/></h5>
            <div class="addCard unsubscribe">
            <dsp:form id="unsubscribeMail" action="unsubscribe_email.jsp" method="post">

			    <dsp:include page="/global/gadgets/errorMessage.jsp">
			      <dsp:param name="formhandler" bean="SubscriptionFormHandler"/>
			    </dsp:include>
		
            	<dsp:input bean="SubscriptionFormHandler.type" type="hidden" value="${subscriptionType}"/>
                <!--  <dsp:input bean="SubscriptionFormHandler.subscriptionSuccessURL" type="hidden" value="unsubscribe_email.jsp"/>
				<dsp:input bean="SubscriptionFormHandler.subscriptionErrorURL" type="hidden" value="unsubscribe_email.jsp"/>-->
				 <dsp:input bean="SubscriptionFormHandler.fromPage" type="hidden" value="unsubscribeemail" />
						
        	    
                    <div class="label">
                        <dsp:input type="radio" name="unsubscribeMail" id="twicemonth" bean="SubscriptionFormHandler.frequency" value="${frequencyTwiceAMonth}">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltwicemonth"/>
                        </dsp:input>
                       <label id="lbltwicemonth" for="twicemonth"><bbbl:label key="lbl_unsubscribe_email_frequency_twicemonth" language ="${pageContext.request.locale.language}"/></label> 
                    </div>
                    <div class="label">
                        <dsp:input type="radio" name="unsubscribeMail" id="oncemonth" bean="SubscriptionFormHandler.frequency" value="${frequencyOnceAMonth}">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbloncemonth"/>
                        </dsp:input>
                        <label id="lbloncemonth" for="oncemonth"><bbbl:label key="lbl_unsubscribe_email_frequency_oncemonth" language ="${pageContext.request.locale.language}"/></label>
                    </div>
                    <div class="label">
                        <dsp:input type="radio" name="unsubscribeMail" id="coupons" bean="SubscriptionFormHandler.frequency" value="${frequencyCoupons}">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcoupons"/>
                        </dsp:input>
                        <label id="lblcoupons" for="coupons"><bbbl:label key="lbl_unsubscribe_email_frequency_certificates_and_coupons" language ="${pageContext.request.locale.language}"/></label>
                    </div>
                    <div class="label spaceBottom_10">
                        <dsp:input type="radio" name="unsubscribeMail" id="no" bean="SubscriptionFormHandler.frequency" value="${frequencyUnsubscribeAll}">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblno errorno"/>
                        </dsp:input>
                        <label id="lblno" for="no"><bbbl:label key="lbl_unsubscribe_email_frequency_nothanks" language ="${pageContext.request.locale.language}"/></label>
                        <label id="errorno" class="error" generated="true" for="unsubscribeMail"> <label>
						<h5 class="offScreen"><bbbl:label key="lbl_unsubscribe_email_changefrequencyinstead" language ="${pageContext.request.locale.language}"/></h5>
                    </div>
                    <div class="input clearfix width_3">
                        <div class="label">
                            <label id="lblemail" for="emailAdd"><bbbl:label key="lbl_unsubscribe_email_email_address" language ="${pageContext.request.locale.language}"/></label>
                        </div>
                        <div class="text">
                            <div>
								<dsp:getvalueof var="email" bean="Profile.email"/>
								<c:if test="${ email eq null }">
									<dsp:getvalueof var="email" param="email"/>
								</c:if>
                                <dsp:input id="email" bean="SubscriptionFormHandler.emailAddr" name="email"  type="text" value="${email}">
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                </dsp:input>
                            </div>
                        </div>
                    </div>
                    
                    <div class="label clearfix">
                        <div class="button button_active">
                            <dsp:input type="submit" bean="SubscriptionFormHandler.unSubscribeEmail" value="SUBMIT" id="Submit">
                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="Submit"/>
                                <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <c:if test="${EmailOn}">
                    <div>
            			<bbbt:textArea key="txt_unsubscribe_email_tounsubdirectmail" language ="${pageContext.request.locale.language}" />
            		</div> 
            		</c:if>
                </dsp:form>
            </div>
            </c:otherwise>
             </c:choose>
         </div>
    </div>
</jsp:body>
</bbb:pageContainer>
</dsp:page>    
    