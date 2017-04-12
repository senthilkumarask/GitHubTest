<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBSPBillingAddressFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBPopulateStatesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBCountriesDroplet"/>	
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof id ="order" bean="ShoppingCart.current" />
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof var="useShipAddr" bean="SessionBean.useShipAddr"/>
	<dsp:getvalueof id="isAnonymousProfile" bean="Profile.transient" />
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<dsp:getvalueof var="addresses" param="addresses"/>	



	<c:if test="${not payPalOrder}">
		<dsp:getvalueof var ="payPalOrder" param="currentBillingAddress.fromPaypal"/>
	</c:if>
	<c:choose>
		<c:when test="${payPalOrder eq true && order.billingAddress.id eq id}">
			<c:set var="isPayPal" value="true"/>
		</c:when>
		<c:otherwise>
			<c:set var="isPayPal" value="false"/>
		</c:otherwise>
	</c:choose>

	<div class="clear"></div>
	<fieldset class="radioGroup">
		<legend class="hidden">
			<label> 
				<bbbl:label key="lbl_spc_billing_address_match"
					language="${pageContext.request.locale.language}" /> 
			</label>
		</legend>


		<dsp:getvalueof var="defaultBillingID"	bean="Profile.billingAddress.repositoryId" />


	<%-- 
		<dsp:droplet name="BBBBillingAddressDroplet"> 
			<dsp:param name="order" bean="ShoppingCart.current" />
			<dsp:param name="profile" bean="Profile" />
			<dsp:param name="billingAddrContainer" bean="BBBAddressContainer" />
			<dsp:param name="useShipAddr" value="${useShipAddr}" />		
			<dsp:oparam name="output">
				<dsp:getvalueof var="addresses" param="addresses"/>								
				
			</dsp:oparam>
		</dsp:droplet> 
		
--%>
		<dsp:getvalueof var="orderBillingAddress" bean="/atg/commerce/ShoppingCart.current.billingAddress" />
		<c:if test="${not empty orderBillingAddress}">				
				<dsp:getvalueof var="orderBillingAddressID" bean="/atg/commerce/ShoppingCart.current.billingAddress.id" />
		</c:if>

		<%--
		debugging code:

		ShoppingCart.current.billingAddress : ${orderBillingAddress}<br />
		${orderBillingAddressID}
	   
	   </br />Default Billing: ${defaultBillingID}</br />

		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="addresses" />
			<dsp:oparam name="output">
				<br />				
				Billing Group Addresses:<br />
				element.id: <dsp:valueof param="element.id" /><br />	
				element.firstName<dsp:valueof param="element.firstName"  /> <br />
				element.lastName<dsp:valueof param="element.lastName"  /> <br />
				element.address1<dsp:valueof param="element.address1"  /> <br />
				element.state<dsp:valueof param="element.state"  /> <br />
				<br />				
			</dsp:oparam>
		</dsp:droplet>
		 --%>

		<%-- if logged in, look for default billing address
				otherwise, look for group billing address
		--%>
		<c:set var="preselectedAddressType" value=""/>

	

		<c:choose >
			<c:when test="${!isAnonymousProfile}" >

				<%-- FIRST, try to get the billing address from group --%>
			 	<dsp:droplet name="ForEach">
					<dsp:param name="array" param="addresses" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="billState" param="element.state" />	
						<dsp:getvalueof param="element.id" var="currentAddressID"/>				
						<c:if test="${billState != 'INITIAL' && orderBillingAddressID == currentAddressID}" >							
							<dsp:getvalueof param="element" var="currentBillingAddress"/>						
							<c:set var="preselectedAddressType" value="group"/>
							<c:set var="addressCount" value="${fn:length(addresses)-1}"/>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>

				<%-- debugging code  		
				Using order billing address
			   <br />Current billing:${currentBillingAddress} <br />
				--%>


				<c:if test="${empty currentBillingAddress}">	
					<dsp:droplet
						name="/com/bbb/commerce/shipping/droplet/CheckProfileAddress">
						<dsp:param name="siteId" bean="ShoppingCart.current.siteId" />
						<dsp:param name="profile" bean="/atg/userprofiling/Profile" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="profileAddresses" param="profileAddresses" />

							<dsp:droplet name="ForEach">    
							  <dsp:param name="array" param="profileAddresses" />
							  <dsp:param name="elementName" value="address" />
							  <dsp:param name="sortProperties" value="id"/>
							  <dsp:oparam name="output">
						  		  	<dsp:getvalueof var="addressCount" param="count" />
						        	<dsp:getvalueof param="address.identifier" var="currentAddressIdentifer"/>
						        	<dsp:getvalueof param="address.id" var="currentAddressID"/>
						        	<%-- debugging 
						        	IN THE LOOP
						        	defaultBillingID ${defaultBillingID}<br>
						        	${fn:indexOf( address.identifier, defaultBillingID)}<br>
						        	<dsp:valueof param="address.id"/><br>
						        	currentAddressID:${currentAddressID}<br><br>
						        	--%>
				
									<%--Trying to display default billing address for logged in user in the form --%>
									<c:if test="${empty currentBillingAddress}">
							      
							        	<c:choose>            
							            <c:when test="${defaultBillingID eq currentAddressID}">
							            	<c:set var="preselectedAddressType" value="profile"/>							
							              	<dsp:getvalueof param="address" var="currentBillingAddress"/>


							              		<dsp:droplet name="MapToArrayDefaultFirst">
									      			<dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId" />
									      			<dsp:param name="defaultId2" bean="Profile.billingAddress.repositoryId" />
									      			<dsp:param name="map" bean="Profile.secondaryAddresses" />
									      			<dsp:param name="sortByKeys" value="true" />
									      			<dsp:param name="currentAddressID" value="${currentAddressID}"/>
									      			<dsp:oparam name="output">
									      			  <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray" />
									      			  <dsp:getvalueof var="nickname" vartype="java.lang.Object" param="nickname" />
														</dsp:oparam>
													</dsp:droplet>


							            </c:when>                
							        	</c:choose>
							      </c:if>  
							  </dsp:oparam>
							</dsp:droplet>

						</dsp:oparam>
					</dsp:droplet>
				</c:if> 
					
            

				<%--Trying to display default billing address for logged in user in the form 
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="addresses" />
					<dsp:oparam name="output">
						<dsp:getvalueof var ="id" param="element.id" />			
						
						<c:if test="${defaultBillingID eq id}">
							<dsp:getvalueof param="element" var="currentBillingAddress"/>						
							<c:set var="preselectedAddressType" value="profile"/>							
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
				--%>
    			
    			
			</c:when>
		 	<c:otherwise>
		 		<%-- get the billing address from group --%>
			 	<dsp:droplet name="ForEach">
					<dsp:param name="array" param="addresses" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="billState" param="element.state" />					
						<dsp:getvalueof param="element.id" var="currentAddressID"/>				
						<c:if test="${billState != 'INITIAL'  && orderBillingAddressID == currentAddressID}" >							
							<dsp:getvalueof param="element" var="currentBillingAddress"/>						
							<c:set var="preselectedAddressType" value="group"/>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</c:otherwise>
	 </c:choose>



		
	<c:set var="hideForm" value=""/>
	<c:set var="gotBillingAddress" value="${false}"/>
   <c:if test="${not empty currentBillingAddress || payPalOrder}"> 
      <c:set var="hideForm" value="hidden"/>
      <c:set var="gotBillingAddress" value="${true}"/>
   </c:if>
	   

		<%-- 				
	   debugging code  		
	   <br />
	   Current billing:${currentBillingAddress}
	   ${currentBillingAddress.id}
	   <br />
	   Got billing: ${gotBillingAddress}


		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="addresses" />
			<dsp:oparam name="output">
				<br />				
				<dsp:valueof param="element.id" />	
				<dsp:valueof param="element.address1"  /> <dsp:valueof param="element.state"  /> 
			</dsp:oparam>
		</dsp:droplet>
		--%>


		<c:if test="${gotBillingAddress}">
			<dsp:include page="/checkout/singlePage/billing/allBillingAddresses.jsp" >
				<dsp:param name="currentBillingAddress" value="${currentBillingAddress}"/>
				<dsp:param name="addresses" param="addresses"/>
				<dsp:param name="gotBillingAddress" value="${gotBillingAddress}"/>
				
			</dsp:include>
		</c:if>

		<%--
		<dsp:droplet name="BBBBillingAddressDroplet"> 
			<dsp:param name="order" bean="ShoppingCart.current" />
			<dsp:param name="profile" bean="Profile" />
			<dsp:param name="billingAddrContainer" bean="BBBAddressContainer" />
			<dsp:param name="useShipAddr" value="${useShipAddr}" />		
			<dsp:oparam name="output">
				<dsp:include page="/checkout/singlePage/billing/allBillingAddresses.jsp" >
					<dsp:param name="currentBillingAddress" param="currentBillingAddress"/>
					<dsp:param name="addresses" param="addresses"/>
					<dsp:param name="gotBillingAddress" value="${gotBillingAddress	}"/>
					
				</dsp:include>
				<dsp:getvalueof var="addresses" param="addresses"/>				
			</dsp:oparam>
		</dsp:droplet> 
		 --%>

				

		<%--


		<dsp:droplet
			name="/com/bbb/commerce/shipping/droplet/CheckProfileAddress">
			<dsp:param name="siteId" bean="ShoppingCart.current.siteId" />
			<dsp:param name="profile" bean="/atg/userprofiling/Profile" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="profileAddresses" param="profileAddresses" />
			</dsp:oparam>
		</dsp:droplet>

		<c:if test="${!isAnonymousProfile}">
			<dsp:getvalueof var="defaultShippingId" bean="Profile.shippingAddress.repositoryId" />
			<dsp:getvalueof var="defaultBillingID"	bean="Profile.billingAddress.repositoryId" />

			<dsp:droplet name="ForEach">
				<dsp:param name="array" value="${profileAddresses }" />
				<dsp:param name="elementName" value="address" />
				<dsp:param name="sortProperties" value="id" />
				<dsp:oparam name="output">

					<dsp:getvalueof param="address.identifier" var="currentBillingAddressIdentifer" />
					<dsp:getvalueof param="address.id" var="currentBillingAddressID" />
		
       			<c:choose>
						<c:when test="${defaultBillingID eq currentBillingAddressID}">
							<dsp:getvalueof param="address" var="currentBillingAddress" />
						</c:when>
					</c:choose>                    
                                                                         
        		</dsp:oparam>
			</dsp:droplet>
			
			
		</c:if>
		--%>
		<%--End Trying to display default billing address in the form --%>

		<%--<dsp:include page="allBillingAddresses.jsp" >
			<dsp:param name="selectedAddrKey" param="selectedAddrKey"/>
			<dsp:param name="addresses" param="addresses"/>
		</dsp:include>
		<dsp:getvalueof var="addresses" param="addresses"/>  --%>
		<c:choose>
				<c:when test = "${payPalOrder}">
					<div class="radioItem input clearfix addNewAddress">
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt"
							bean="BBBSPBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" >
	                     <dsp:tagAttribute name="aria-checked" value="false"/>
	                     <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
	               </dsp:input>
	               <div class="label">
							<label id="lbladdressToShip4" for="addressToShip4"> <span><bbbl:label key="lbl_add_new_billing_address" language="${pageContext.request.locale.language}"/></span> </label>
						</div>
					</div>
				</c:when>
				<%--
				<c:otherwise>						
					<dsp:input type="hidden" value="${id}"
					bean="BBBSPBillingAddressFormHandler.userSelectedOption"
						name="addressToShip" id="billingAddressOption${loopIndex}" iclass="">
                          <dsp:tagAttribute name="aria-checked" value="false"/>
                          <dsp:tagAttribute name="aria-labelledby" value="lblbillingAddressOption${loopIndex}"/>
                      </dsp:input>
				</c:otherwise>
				--%>
			</c:choose>
		
		<div id="billingAddressFields" class="radioItem input clearfix last ${hideForm}">

			
			<%--<div class="radio">
				<c:choose>
					<c:when test="${addresses ne null && not empty addresses}">
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt"
							bean="BBBSPBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
                        </dsp:input>
					</c:when>
					<c:otherwise>
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt" checked="true"
							bean="BBBSPBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
                        </dsp:input>
					</c:otherwise>

				</c:choose>
				
			</div> --%>
			
			<h6><bbbl:label key="lbl_spc_enter_below_for_billing_information" language="${pageContext.request.locale.language}" /></h6>
			


			<c:choose>
				<c:when test="${gotBillingAddress}">
					<c:set var="id" value="${currentBillingAddress.id}"/>
				</c:when>
				<c:otherwise>
					<c:set var="id" value="NEW"/>
				</c:otherwise>
			</c:choose>


			<c:choose>
				<c:when test="${preselectedAddressType == 'profile'}">
					<dsp:input  type="hidden" name="updateAddress" id="billingUpdateAddress" value="true" bean="BBBSPBillingAddressFormHandler.updateAddress" /> 											
				</c:when>
				<c:otherwise>
					<dsp:input  type="hidden" name="updateAddress" id="billingUpdateAddress" value="false" bean="BBBSPBillingAddressFormHandler.updateAddress" /> 									
				</c:otherwise>
			</c:choose>


			<dsp:input  type="hidden" name="nickname" id="billingNickname" value="${nickname }" bean="BBBSPBillingAddressFormHandler.nickname" />                   		  

			<dsp:input  type="hidden" name="currentBillingAddressID" id="currentBillingAddressID" value="${currentBillingAddress.id}" bean="BBBSPBillingAddressFormHandler.currentBillingAddressId" /> 


			<c:choose>
				<c:when test="${preselectedAddressType == 'profile'}">
					<dsp:input type="hidden" name="addressToShip" iclass="newAddOpt"
							bean="BBBSPBillingAddressFormHandler.userSelectedOption" value="${currentBillingAddress.id}" id="addressToShip4" />
				</c:when>
				<c:otherwise>
					<dsp:input type="hidden" name="addressToShip" iclass="newAddOpt" checked="true"
							bean="BBBSPBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" />
				</c:otherwise>
			</c:choose>

								

			<div class="subForm clearfix ">
				
				<div class="input_wrap">
					
						<label  class="popUpLbl" id="lblBillingFirstName" for="billingFirstName"> 
							<bbbl:label
								key="lbl_spc_shipping_new_first_name"
								language="${pageContext.request.locale.language}" /> <span
								class="required">*</span> 
						</label>
					
					
						<dsp:input type="text" value="${currentBillingAddress.firstName}" name="firstName" id="billingFirstName"
							bean="BBBSPBillingAddressFormHandler.billingAddress.firstName" iclass="checkoutfirstName" >
                             <dsp:tagAttribute name="aria-required" value="true"/>
                             <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
                  </dsp:input>
				</div>
			
				<div class="clear"></div>
				
				<div class="input_wrap clearfix">
					
					<label class="popUpLbl" id="lblBillingLastName" for="billingLastName">
						<bbbl:label
									key="lbl_spc_shipping_new_last_name"
									language="${pageContext.request.locale.language}" />
						<span class="required">*</span>
					</label>
					
					
					<dsp:input type="text" value="${currentBillingAddress.lastName }" id="billingLastName" name="lastName"
							bean="BBBSPBillingAddressFormHandler.billingAddress.lastName" >
               	<dsp:tagAttribute name="aria-required" value="true"/>
                  <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
               </dsp:input>
					
				</div>
				
				<div class="clear"></div>
				
				<div class="input_wrap clearfix">
					
					<label class="popUpLbl" id="lblBillingCompany" for="billingCompany">
						<bbbl:label
									key="lbl_spc_shipping_new_company"
									language="${pageContext.request.locale.language}" />
					</label>
					
					
					
					<dsp:input type="text" value="${currentBillingAddress.companyName}" id="billingCompany" name="companyName"
								bean="BBBSPBillingAddressFormHandler.billingAddress.companyName" >
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
               </dsp:input>
					
				</div>
	
				<div class="clear"></div>

<%-- 				R2.1.1 International Credit Card  --%>
<%-- 				 INPUT SELECT   --%>
				<dsp:getvalueof var="currentCountryName" value="${currentBillingAddress.country}"/>

				<c:choose>
					<c:when test="${!empty currentCountryName && currentCountryName ne 'Canada' }">                   
	                   <%-- do nothing, got the value already --%>
	               </c:when>
	               <c:when test="${!empty currentCountryName && fn:containsIgnoreCase(currentCountryName, 'Canada')}">                 
	                   <c:set var="currentCountryName" value="CA"/>
	               </c:when>
	               <c:when test="${empty currentCountryName && defaultCountry eq 'Canada'}">                   
	                   <c:set var="currentCountryName" value="CA"/>
	               </c:when>
						<c:when test="${empty currentCountryName && defaultCountry eq 'CA'}">
	                   <c:set var="currentCountryName" value="CA"/>
	               </c:when>
	               <c:otherwise>
	               	<c:set var="currentCountryName" value="${defaultCountry}"/>                   
	               </c:otherwise>
	           </c:choose>
				

				<div class="input_wrap countrySelectWrapper" >
					
					<label class="popUpLbl" id="lblcountryName" for="countryName"><bbbl:label key="lbl_Country" language="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
					

					<dsp:droplet name="BBBCountriesDroplet">
						<dsp:oparam name="output">
								<c:choose>
									<c:when test="${empty preFillValues || preFillValues}">

										<dsp:select name="countryName" id="countryName"  bean="BBBSPBillingAddressFormHandler.billingAddress.country">
		                                    <dsp:tagAttribute name="aria-required" value="true"/>
		                                    <dsp:tagAttribute name="aria-labelledby" value="lblcountryName errorcountryName"/>
											<option value=""><bbbl:label key="lbl_intl_shipping_modal_select_country" language="${pageContext.request.locale.language}"/></option>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="country" />
												<dsp:param name="elementName" value="contname" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="countryName" param='key'/>
													<c:choose>
                                          <c:when test="${currentCountryName ne null && currentCountryName eq countryName}">
                                              <option value="<dsp:valueof param='key'/>" selected="selected">
                                                  <dsp:valueof param='contname' />
                                              </option>
                                          </c:when>
                                          <c:when test="${(empty currentCountryName || currentCountryName eq null) && ((defaultCountry eq countryName) ||(defaultCountry eq 'Canada' &&  countryName eq 'CA'))}">
                                              <option value="<dsp:valueof param='key'/>" selected="selected">
                                                  <dsp:valueof param='contname' />
                                              </option>
                                          </c:when>
                                          <c:otherwise>
                                              <option value="<dsp:valueof param='key'/>">
                                                  <dsp:valueof param='contname' />
                                              </option>
                                          </c:otherwise>
                                      </c:choose>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:select>
									</c:when>
									<c:otherwise>
										<dsp:select name="countryName" id="countryName"  bean="BBBSPBillingAddressFormHandler.billingAddress.country" nodefault="true">
		                                    <dsp:tagAttribute name="aria-required" value="true"/>
		                                    <dsp:tagAttribute name="aria-labelledby" value="lblcountryName errorcountryName"/>
											<option value="">Select Country</option>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="country" />
												<dsp:param name="elementName" value="contname" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="countryName" param='key'/>
													<c:choose>
                                                         <c:when test="${currentCountryName ne null && currentCountryName eq countryName}">
                                                             <option value="<dsp:valueof param='key'/>" selected="selected">
                                                                 <dsp:valueof param='contname' />
                                                             </option>
                                                         </c:when>
                                                         <c:when test="${(empty currentCountryName || currentCountryName eq null) && ((defaultCountry eq countryName) ||(defaultCountry eq 'Canada' &&  countryName eq 'CA'))}">
                                                             <option value="<dsp:valueof param='key'/>" selected="selected">
                                                                 <dsp:valueof param='contname' />
                                                             </option>
                                                         </c:when>
                                                         <c:otherwise>
                                                             <option value="<dsp:valueof param='key'/>">
                                                                 <dsp:valueof param='contname' />
                                                             </option>
                                                         </c:otherwise>
                                                     </c:choose>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:select>
									</c:otherwise>
								</c:choose>	
						</dsp:oparam>
					</dsp:droplet>
					<label id="errCountryInvalid" class="error hidden" >Selected/Added International Billing Address is Restricted </label>
					
				</div>

				<div class="clear"></div>
			<%-- END INPUT SELECT --%>
<%-- 		R2.1.1 International Credit Card ends --%>
				
			
				<div class="input_wrap">
					
					<label class="popUpLbl" id="lblBillingAddress1" for="BillingAddress1">
							<bbbl:label key="lbl_spc_shipping_new_line1" language="${pageContext.request.locale.language}" />
							<span class="required">*</span>
					</label>
					
					
					<dsp:input type="text" value="${currentBillingAddress.address1 }" id="BillingAddress1" name="address1"
							bean="BBBSPBillingAddressFormHandler.billingAddress.address1" >
						<dsp:tagAttribute name="aria-required" value="true"/>
              		<dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
               </dsp:input>
					
				</div>
				
				<div class="clear"></div>

				<div class="input_wrap">
						<label class="popUpLbl" id="lblBillingAddress2" for="BillingAddress2">
							<bbbl:label key="lbl_spc_shipping_new_line2" language="${pageContext.request.locale.language}" />
						</label>
						
						<dsp:input type="text" value="${currentBillingAddress.address2 }" id="BillingAddress2" name="address2"
								bean="BBBSPBillingAddressFormHandler.billingAddress.address2" iclass="optional">
                                 <dsp:tagAttribute name="aria-required" value="false"/>
                                 <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                  </dsp:input>
				</div>

				<div class="clear"></div>
					
				<div class="input_wrap clearfix">
					
					<label for="BillingZip" id="lblBillingZipCode"  class="popUpLbl" >
							<c:choose>
								 <c:when test="${currentSiteId eq 'BedBathCanada'}">
									 <span class="zipCodeLabelText"><bbbl:label key="lbl_spc_subscribe_canadazipcode" language="${pageContext.request.locale.language}" /></span>
								  </c:when>
								  <c:otherwise>
								      <span class="zipCodeLabelText"><bbbl:label key="lbl_spc_shipping_new_zip" language="${pageContext.request.locale.language}" /></span>
								  </c:otherwise>
						   </c:choose>
						
							
						<span class="required">*</span>
					</label>
					
					<c:choose>
					   <c:when test="${currentCountryName eq 'US'}">
					     <c:set var="zipCodeClass" value="zipUS" scope="page"/>					   
					   </c:when>	  
					   <c:when test="${currentCountryName eq 'CA'}">					   
					   	<c:set var="zipCodeClass" value="zipCA" scope="page"/>
					   </c:when>	
					   <c:otherwise>  	
					   	<c:set var="zipCodeClass" value="zipCodeOthers" scope="page"/>
					   </c:otherwise>
					</c:choose>							
					

					<input type="text" name="${zipCodeClass}" value="${currentBillingAddress.postalCode }" id="BillingZip" aria-required="true" aria-labelledby="lblZipCode errorzip" /> 
               <dsp:input type="hidden" id="billingZipAll" bean="BBBSPBillingAddressFormHandler.billingAddress.postalCode" value="" />
					
				</div>

				<div class="clear"></div>

				<div class="fieldsInlineWrapper clearfix">
					<div class="input_wrap cityWrapper">
						
							<label  class="popUpLbl"  id="lblBillingCityName" for="BillingCityName">
								<bbbl:label key="lbl_spc_shipping_new_city" language="${pageContext.request.locale.language}" />
								<span class="required">*</span>
							</label>
						
						
							<dsp:input type="text" value="${currentBillingAddress.city}" id="BillingCityName" name="cityName"
								bean="BBBSPBillingAddressFormHandler.billingAddress.city" maxlength="25">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
                            </dsp:input>
						
					</div>
					
					<c:if test="${currentCountryName != 'US' && currentCountryName != 'CA'}">
						<c:set var="hideStateSelect" value="hidden"/>	
					</c:if>
					<input type="hidden" id="billStateName" value="" />
					<div class="input_wrap  stateSelectWrapper ${hideStateSelect}" aria-live="assertive">							
							<label for="billingStateName"   class="popUpLbl" id="billingStateNameLbl">
								 <c:choose>
									 <c:when test="${currentSiteId eq 'BedBathCanada'}">
										  <span class="stateLabelText"><bbbl:label key="lbl_spc_addnewbillingaddressprovince" language="${pageContext.request.locale.language}" /></span>
									  </c:when>
									  <c:otherwise>
									      <span class="stateLabelText"><bbbl:label key="lbl_spc_shipping_new_state" language="${pageContext.request.locale.language}" /></span>
									  </c:otherwise>
								  </c:choose>
								<span class="required">*</span>
							</label>
						
						<dsp:droplet name="BBBPopulateStatesDroplet">
							<dsp:param name="NoShowUSTerr" value="noShowOnBilling" />
							<c:if test="${currentCountryName == 'CA'}">
								<dsp:param name="cc" value="ca"/>
							</c:if>
							<dsp:oparam name="output">
								
									<c:choose>
										<c:when test="${empty preFillValues || preFillValues}">
											<dsp:select name="stateName" id="billingStateName"
												bean="BBBSPBillingAddressFormHandler.billingAddress.state">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="stateLbl errorstateName"/>
												<c:choose>
													 <c:when test="${currentSiteId eq 'BedBathCanada'}">
														  <option value=""><bbbl:label key="lbl_easy_return_form_select_province" language="${pageContext.request.locale.language}"/></option>	
													  </c:when>
													  <c:otherwise>
													      <option value=""><bbbl:label key="lbl_search_store_select_state" language="${pageContext.request.locale.language}"/></option>
													  </c:otherwise>
											   </c:choose>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="states" />
													<dsp:param name="elementName" value="state" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="state" param='state.stateCode'/>
														
														<c:choose >
															<c:when test="${state eq currentBillingAddress.state}">
														  		<c:set var="selected" value="selected"/>	
															</c:when>
															<c:otherwise>
															   <c:set var="selected" value=""/>	
															</c:otherwise>
														</c:choose>


														<option ${selected} value="<dsp:valueof param='state.stateCode'/>">
															<dsp:valueof param='state.stateName' />
														</option>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:select>
										</c:when>
										<c:otherwise>
											<dsp:select name="stateName" id="billingStateName"
												bean="BBBSPBillingAddressFormHandler.billingAddress.state">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="stateLbl errorstateName"/>
												<c:choose>
													 <c:when test="${currentSiteId eq 'BedBathCanada'}">
														  <option value=""><bbbl:label key="lbl_easy_return_form_select_province" language="${pageContext.request.locale.language}"/></option>	
													  </c:when>
													  <c:otherwise>
													      <option value=""><bbbl:label key="lbl_search_store_select_state" language="${pageContext.request.locale.language}"/></option>
													  </c:otherwise>
											   </c:choose>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="states" />
													<dsp:param name="elementName" value="state" />
													<dsp:oparam name="output">

														<dsp:getvalueof var="state" param='state.stateCode'/>
														<c:choose >
															<c:when test="${state.stateCode eq currentBillingAddress.state}">
														  		<c:set var="selected" value="selected"/>	
															</c:when>
															<c:otherwise>
															   <c:set var="selected" value=""/>	
															</c:otherwise>
														</c:choose>


														<option ${selected} value="<dsp:valueof param='state.stateCode'/>">
															<dsp:valueof param='state.stateName' />
														</option>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:select>
										</c:otherwise>
									</c:choose>
								
							</dsp:oparam>
						</dsp:droplet>
					</div>
					
					 <%-- INPUT SELECT --%>

					<c:if test="${currentCountryName == 'US' || currentCountryName == 'CA'}">
						<c:set var="hideStateText" value="hidden"/>	
					</c:if>
					<div class="input_wrap stateTextWrapper ${hideStateText}">
						
						<label for="stateText" id="stateLbl" class="popUpLbl"><span class="stateLabelText">State</span> <span class="required">*</span></label>
						
                  
                  <input type="text" value="${currentBillingAddress.state }" id="stateText" name="stateText" aria-required="true" aria-labelledby="stateLbl errorstateText" maxlength="40"/>
                      
                  <dsp:input type="hidden" value="${currentBillingAddress.state }" id="stateTextInt" bean="BBBSPBillingAddressFormHandler.billingAddress.state">
                     	</dsp:input>
                  
					</div>
					<%-- END INPUT SELECT --%>
				</div>
					

				<div class="clear"></div>
				
				


				<%-- <dsp:getvalueof var="isUserAuthenticated"
				<%-- <dsp:test var="isUserAuthenticated" value="TRUE"/> --%>
				<%--
				<dsp:droplet name="/com/bbb/commerce/shipping/droplet/CheckProfileAddress">
					<dsp:param name="siteId" bean="ShoppingCart.current.siteId" />
					<dsp:param name="profile" bean="/atg/userprofiling/Profile" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="profileAddresses" param="profileAddresses"/>		
					</dsp:oparam>
				</dsp:droplet>
				--%>
				<c:choose>
					<c:when test="${!isAnonymousProfile}">
                  <div class="clear"></div>
						<div class="checkboxItem input clearfix saveToAccountWrapper">
							<div class="checkbox">
								
										<dsp:input type="checkbox" name="saveToAccount" id="billingSaveToAccount"
											bean="BBBSPBillingAddressFormHandler.saveToAccount" value="TRUE" checked="false">
	                                <dsp:tagAttribute name="aria-checked" value="true"/>
	                                <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
	                           </dsp:input>

							</div>
							<div class="label">
								<label id="lblsaveToAccount" for="billingSaveToAccount">
									<bbbl:label key="lbl_spc_shipping_new_save_account" language="${pageContext.request.locale.language}" />
								</label>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<dsp:input type="hidden" bean="BBBSPBillingAddressFormHandler.newAddress" value="true" />
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</fieldset>

	<div id="editBillingButtons" class="clearfix ${hideForm}">
      <input type="button" id="applyBillingEdit" aria-label="Apply and Save your billing address" value="Apply" class="btnPrimary button-Med " />
      <input type="button" id="cancelBillingEdit" aria-label="Cancel editing your billing address information" value="Cancel" class="btnSecondary button-Med hidden" />
    </div>
  

  
   <c:if test="${!isAnonymousProfile && addressCount>1}">
     <div class="clearfix">
         <a id="viewAllBillingAddress" href="#"><bbbl:label key="lbl_spc_view_all_billing_addresses" language="${pageContext.request.locale.language}" /></a>
     </div>
   </c:if>
</dsp:page>