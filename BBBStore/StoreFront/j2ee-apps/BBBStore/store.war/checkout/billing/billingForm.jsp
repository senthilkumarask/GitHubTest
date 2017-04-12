<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="useShipAddr" bean="SessionBean.useShipAddr"/>
	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="headerRenderer">
        	<dsp:include page="/checkout/checkout_header.jsp" flush="true">
                <dsp:param name="step" value="billing" />
                <dsp:param name="pageId" value="5"/>
            </dsp:include>
            </jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="pageWrapper">billing checkoutBillingPage shippingWrapper useGoogleAddress</jsp:attribute>
		<jsp:attribute name="PageType">Billing</jsp:attribute>
		<jsp:body>	
		 <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
		 <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
        	<div id="content" class="container_12 clearfix" role="main">
                <div class="clearfix marTop_20">
                
			    <dsp:include page="/global/gadgets/errorMessage.jsp">
                	 <dsp:param name="formhandler" bean="BBBBillingAddressFormHandler"/>
                </dsp:include>
            	
                <div id="leftCol" class="grid_8">
                	<dsp:droplet name="BBBBillingAddressDroplet">
						<dsp:param name="order" bean="ShoppingCart.current" />
						<dsp:param name="profile" bean="Profile" />
						<dsp:param name="billingAddrContainer" bean="BBBAddressContainer" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="selectedAddrKey" param="selectedAddrKey"/>
							<dsp:getvalueof var="addresses" param="addresses"/>				
						</dsp:oparam>
					</dsp:droplet>
				
					<c:set var="size" value="${fn:length(addresses)}"/>
                	<c:choose>
		                <c:when test = "${payPalOrder eq true && size ne null}">
		                	<c:choose>
		                		<c:when test = "${size eq 1 }">
		                			<h3 class="sectionHeading"><bbbl:label key="lbl_paypal_one_billing_address_title" language="${pageContext.request.locale.language}" /></h3>
		                		</c:when>
		                		<c:otherwise>
		                			<h3 class="sectionHeading"><bbbl:label key="lbl_paypal_billing_address_title" language="${pageContext.request.locale.language}" /></h3>
		                		</c:otherwise>
		                	</c:choose>
		                </c:when>
		                <c:otherwise>
	                		<h3 class="sectionHeading"><bbbl:label key="lbl_billing_address_match" language="${pageContext.request.locale.language}" /></h3>
	                	</c:otherwise>
                	</c:choose>
				 	<div>
						<dsp:form name="form" method="post" id="formShippingSingleLocation">
							<dsp:include page="/checkout/billing/addNewBillingAddress.jsp" >
								<dsp:param name="selectedAddrKey" value="${selectedAddrKey}"/>
								<dsp:param name="addresses" value="${addresses}"/>
							</dsp:include>
							<dsp:include page="/checkout/billing/couponInfoForBilling.jsp" />
							
							<%--Changes for BBBSL-2662-Start--%>
							
							<c:if test="${transient == 'true'}">
								<c:set var="isChecked">
									<dsp:valueof bean="BBBBillingAddressFormHandler.emailChecked" />
								</c:set>
								<div class="checkboxItem input clearfix">
								<div class="checkbox">
								<c:choose>
								<c:when test="${currentSiteId eq 'BedBathCanada'}">
									<c:choose>
									<c:when test="${isChecked eq 'checked'}">
									<dsp:input type="checkbox" checked="true" name="emailSignUp" id="emailSignUp" bean="BBBBillingAddressFormHandler.emailSignUp" />
									</c:when>
									<c:otherwise>
									<dsp:input type="checkbox" checked="false" name="emailSignUp" id="emailSignUp" bean="BBBBillingAddressFormHandler.emailSignUp" />
									</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:choose>
									<c:when test="${isChecked eq 'unChecked'}">
									<dsp:input type="checkbox" checked="false" name="emailSignUp" id="emailSignUp" bean="BBBBillingAddressFormHandler.emailSignUp" />
									</c:when>
									<c:otherwise>
									<dsp:input type="checkbox" checked="true" name="emailSignUp" id="emailSignUp" bean="BBBBillingAddressFormHandler.emailSignUp" />
									</c:otherwise>
									</c:choose>
								</c:otherwise>
								</c:choose>
								</div>
								<div class="label">
									<label for="emailSignUp"><bbbl:label key="lbl_subscribe_email_billing" language="<c:out param='${language}'/>" /></label>
								</div>
								</div>
							</c:if> 
							<%--Changes for BBBSL-2662-End--%>
							<div class="button btnApply button_active button_active_orange">
								
								<c:set var="lbl_continue_to_payment" scope="page">
	                               <bbbl:label
										key="lbl_continue_to_payment"
										language="${pageContext.request.locale.language}" />
                                </c:set>								
								<dsp:input bean="BBBBillingAddressFormHandler.saveBillingAddress"  
									type="submit" value="${lbl_continue_to_payment}" id="billingAddressNextBtn" >
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="billingAddressNextBtn"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
							</div>
						</dsp:form>
					</div>
				</div>
			     <dsp:include page="/checkout/order_summary_frag.jsp" />
                 </div>
			</div>
		</jsp:body>
		 	
			<jsp:attribute name="footerContent">
	
				<c:set var="productIds" scope="request"/>
				 <dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
					<dsp:param name="order" bean="ShoppingCart.current"/>
					 <dsp:oparam name="output">
						<dsp:getvalueof var="productIds" param="commerceItemList" />
			        </dsp:oparam>     
			    </dsp:droplet>
	
				<script type="text/javascript">
					if (typeof s !== 'undefined') {
						s.pageName = 'Check Out>Billing';
						s.channel = 'Check Out';
						s.prop1 = 'Check Out';
						s.prop2 = 'Check Out';
						s.prop3 = 'Check Out';
						s.prop6='${pageContext.request.serverName}'; 
						s.eVar9='${pageContext.request.serverName}';
						s.events = "event9";
						s.products = '${productIds}';
						var s_code = s.t();
						if (s_code)
							document.write(s_code);
					}
				</script>
			</jsp:attribute>
		
	</bbb:pageContainer>
</dsp:page>
