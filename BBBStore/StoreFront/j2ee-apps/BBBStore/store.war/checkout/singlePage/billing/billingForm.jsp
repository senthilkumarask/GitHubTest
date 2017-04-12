<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBSPBillingAddressFormHandler" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="useShipAddr" bean="SessionBean.useShipAddr"/>
	
		<jsp:body>	
		 <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
		 <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
        	
				<hr />
                        
                 <h1 class="SPCSectionHeading"><bbbl:label key="lbl_spc_section_heading_billing" language="${pageContext.request.locale.language}" /> <span class="icon icon-checkmark hidden"></span></h1>
               

               <div id="billingErrors">
                
				    	<dsp:include page="/global/gadgets/errorMessage.jsp">
	               	<dsp:param name="formhandler" bean="BBBSPBillingAddressFormHandler"/>
	               </dsp:include>

               </div>
               <c:if test="${param.fromCardinalError eq 'true'}">
                            <div class="error customError"><bbbe:error key='err_cardinal_billing_error' language='${language}'/></div>
               </c:if>
            	
               <%-- TODO change leftCol id --%>
				<%--Passing the SPC parameter |PS-65358  --%>
                <div id="leftCol" class="">
                	<dsp:droplet name="BBBBillingAddressDroplet">
							<dsp:param name="order" bean="ShoppingCart.current" />
							<dsp:param name="profile" bean="Profile" />
							<dsp:param name="billingAddrContainer" bean="BBBAddressContainer" />
							<dsp:param name="fromSPC" value="true" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="selectedAddrKey" param="selectedAddrKey"/>
								<dsp:getvalueof var="addresses" param="addresses"/>				
							</dsp:oparam>
						</dsp:droplet>
						
						<c:set var="size" value="${fn:length(addresses)}"/>
						<%--
                	<c:choose>
		                <c:when test = "${payPalOrder eq true && size ne null}">
		                	<c:choose>
		                		<c:when test = "${size eq 1 }">
		                			<h3 class="sectionHeading"><bbbl:label key="lbl_spc_paypal_one_billing_address_title" language="${pageContext.request.locale.language}" /></h3>
		                		</c:when>
		                		<c:otherwise>
		                			
		                			<h3 class="sectionHeading"><bbbl:label key="lbl_spc_paypal_billing_address_title" language="${pageContext.request.locale.language}" /></h3>
		                			
		                		</c:otherwise>
		                	</c:choose>
		                </c:when>
		                <c:otherwise>
	                		
	                		<h3 class="sectionHeading"><bbbl:label key="lbl_spc_billing_address_match" language="${pageContext.request.locale.language}" /></h3>
	                		
	                	</c:otherwise>
                	</c:choose>
                	--%>
				 		<div>
							<dsp:form name="form" method="post" id="SpcBillingForm" iclass="flatform">
								
								<dsp:include page="/checkout/singlePage/billing/addNewBillingAddress.jsp" >
									<dsp:param name="selectedAddrKey" value="${selectedAddrKey}"/>
									<dsp:param name="addresses" value="${addresses}"/>
								</dsp:include>

								<%--
								<dsp:include page="/checkout/singlePage/billing/couponInfoForBilling.jsp" />
								--%>

									<%--Changes for BBBSL-2662-Start--%>
									<%--
									<c:if test="${transient == 'true'}">
										<c:set var="isChecked">
											<dsp:valueof bean="BBBSPBillingAddressFormHandler.emailChecked" />
										</c:set>
										<div class="checkboxItem input clearfix">
										<div class="checkbox">
										<c:choose>
										<c:when test="${currentSiteId eq 'BedBathCanada'}">
											<c:choose>
											<c:when test="${isChecked eq 'checked'}">
											<dsp:input type="checkbox" checked="true" name="emailSignUp" id="emailSignUp" bean="BBBSPBillingAddressFormHandler.emailSignUp" />
											</c:when>
											<c:otherwise>
											<dsp:input type="checkbox" checked="false" name="emailSignUp" id="emailSignUp" bean="BBBSPBillingAddressFormHandler.emailSignUp" />
											</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:choose>
											<c:when test="${isChecked eq 'unChecked'}">
											<dsp:input type="checkbox" checked="false" name="emailSignUp" id="emailSignUp" bean="BBBSPBillingAddressFormHandler.emailSignUp" />
											</c:when>
											<c:otherwise>
											<dsp:input type="checkbox" checked="true" name="emailSignUp" id="emailSignUp" bean="BBBSPBillingAddressFormHandler.emailSignUp" />
											</c:otherwise>
											</c:choose>
										</c:otherwise>
										</c:choose>
										</div>
										<div class="label">
											<label for="emailSignUp"><bbbl:label key="lbl_spc_subscribe_email_billing" language="<c:out param='${language}'/>" /></label>
										</div>
										</div>
									</c:if> 
									--%>
									<%--Changes for BBBSL-2662-End--%>

									<%--
									<div class="button btnApply button_active button_active_orange">
									
										<c:set var="lbl_button_next" scope="page">
		                               <bbbl:label
											key="lbl_spc_button_next"
											language="${pageContext.request.locale.language}" />
	                                </c:set>								
										<dsp:input bean="BBBSPBillingAddressFormHandler.saveBillingAddress"  
											type="submit" value="${lbl_button_next}" id="billingAddressNextBtn" >
	                                    <dsp:tagAttribute name="aria-checked" value="false"/>
	                                    <dsp:tagAttribute name="aria-labelledby" value="billingAddressNextBtn"/>
	                                    <dsp:tagAttribute name="role" value="button"/>
	                                </dsp:input>
									</div>
									--%>
									<div class="hidden">
										<dsp:input bean="BBBSPBillingAddressFormHandler.saveBillingAddress"  
												type="submit" value="BillingSubmit" id="billingAddressNextBtn" >
	                              <dsp:tagAttribute name="aria-checked" value="false"/>
	                              <dsp:tagAttribute name="aria-labelledby" value="billingAddressNextBtn"/>
	                              <dsp:tagAttribute name="role" value="button"/>
	                          	</dsp:input>
                          	</div>
							</dsp:form>
						</div>
					</div>
		</jsp:body>
</dsp:page>
