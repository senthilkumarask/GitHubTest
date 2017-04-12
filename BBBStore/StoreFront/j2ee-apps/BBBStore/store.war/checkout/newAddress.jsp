<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  newAddress.jsp
 *
 *  DESCRIPTION: address fields for new address entry
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
    <dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof id="isAnonymousProfile" bean="Profile.transient" />
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
	 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO"/>
	 <dsp:getvalueof var="isFormException" param="isFormException"/>
	 <c:choose>
		 <c:when test = "${beddingShipAddrVO != null }">
		   <c:set var="beddingKit" value="true"/>
		   <c:set var="beddingState" value="${beddingShipAddrVO.state}"/>
		 </c:when>
		 <c:otherwise>
		 	<c:set var="beddingKit" value="false"/>
		 </c:otherwise>
	 </c:choose>
	
	<dsp:droplet name="BBBPackNHoldDroplet">
            <dsp:param name="order" bean="ShoppingCart.current"/>
            <dsp:oparam name="output">
               <dsp:getvalueof param="isPackHold" var="isPackHold"/>     
           </dsp:oparam>
     </dsp:droplet>

     <c:choose>
		<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn && (currentSiteId ne BedBathCanadaSite)}">
		<c:set var="iclassValue" value=""/>
		</c:when>
		<c:otherwise>
		<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
		</c:otherwise>
	</c:choose>
	
	<dsp:getvalueof var="order" bean="/atg/commerce/ShoppingCart.current"></dsp:getvalueof>
	<c:if test="${beddingShipAddrVO == null && isPackHold eq true}">     
		<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet">
	 	  <dsp:param name="order" value="${order}"/>
	 	  <dsp:param name="shippingGroup" value="${order.shippingGroups}"/>
	 	   <dsp:param name="isPackHold" value="${true}"/>  
	 	   <dsp:oparam name="beddingKit">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="beddingKit" value="true"/>
           		<c:set var="beddingState" value="${beddingShipAddrVO.state}"/>
           </dsp:oparam>
		   <dsp:oparam name="weblinkOrder">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		 <c:set var="beddingState" value="${beddingShipAddrVO.state}"/>
           </dsp:oparam>
           <dsp:oparam name="notBeddingKit">
           		<c:set var="beddingKit" value="false"/>
           </dsp:oparam>
		</dsp:droplet>
     </c:if>
	
	<%-- Added as Part of PayPal Story - 83-N : Start --%>
	<%-- Pre Populate fields in address fields if paypalAddress is true--%>
	<dsp:getvalueof var= "address" bean = "PayPalSessionBean.address"/>
	<c:choose>
		<c:when test = "${address != null}">
			<c:set var="payPalAddress" value="true"/>
			<dsp:getvalueof var= "isInternationalOrPOError" bean = "PayPalSessionBean.internationalOrPOError"/>
		</c:when>
		<c:otherwise>
			<c:set var="payPalAddress" value="false"/>
		</c:otherwise>
	</c:choose>
	<%-- Added as Part of PayPal Story - 83-N : End --%>
    <div class="radioItem input clearfix last">
    	<div class="radio">
	    	<c:choose>
	           <c:when test="${empty preFillValues || preFillValues}">
	          <%-- BBBSL-971 starts --%>
		          <c:choose>
		             <c:when test="${empty selectedAddress && beddingKit eq true}">
		              <dsp:input type="radio" name="addressToShip" id="addressToShip4n" value="college"
	                                           checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName"/>
	                  <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.beddingKitOrder" value="beddingKit"/>
		             </c:when>
					 <c:when test="${isPackHold eq true}">

		              <dsp:input type="radio" name="addressToShip" id="addressToShip4n" value="college"
	                                           checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName"/>
		             </c:when>

					 <c:when test="${( empty selectedAddress) &&  beddingKit eq false}">
		              <dsp:input type="radio" iclass="newAddOpt" name="addressToShip" id="addressToShip4nNormal" value="true"
	                                           checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName"/>
		             </c:when>
		             <c:otherwise>
		             <dsp:input type="radio" name="addressToShip" id="addressToShip4nNormal" value="true"
	                                           checked="${(isAnonymousProfile&&!containsaddressForGuest)||!containsaddressForUser || payPalAddress || isFormException}" iclass="newAddOpt"
	                                           bean="BBBShippingGroupFormhandler.shipToAddressName"/>
		             </c:otherwise>
		           </c:choose>
	           </c:when>
	           <c:otherwise>
	             <dsp:input type="radio" name="addressToShip" id="addressToShip4nNormal" value="true"
                                           checked="true" iclass="newAddOpt"
                                           bean="BBBShippingGroupFormhandler.shipToAddressName">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4n"/>
                </dsp:input>
	           </c:otherwise>
	         </c:choose>
		 </div>

        <div class="label">
            <label id="lbladdressToShip4n" for="addressToShip4n">
                <span>
                <c:choose>
                 <c:when test="${beddingKit eq true || isPackHold eq true}">
                   <bbbl:label key="lbl_college_address" language="${pageContext.request.locale.language}" /></span>
                 </c:when>
                 <c:otherwise>
                   <bbbl:label key="lbl_shipping_new_address" language="${pageContext.request.locale.language}" /></span>
                 </c:otherwise>
                </c:choose>


            </label>
        </div>
        <div class="clear"></div>
        <div class="subForm clearfix hidden">
            <div class="fieldsInlineWrapper clearfix">


				<dsp:input bean="BBBShippingGroupFormhandler.address.country" value="${defaultCountry}" type="hidden"/>

                <div class="input">
                    <div class="label">
                        <label id="lblcheckoutfirstName" for="checkoutfirstName">
                            <bbbl:label key="lbl_shipping_new_first_name" language="${pageContext.request.locale.language}" />
                            <span class="required">*</span>
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
	                            <c:when test="${beddingKit eq true}">
	                        	   <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutfirstName errorcheckoutfirstName"/>
									<dsp:tagAttribute name="aria-describedby" value="errorcheckoutfirstName"/> 
                                  </dsp:input>
	                        	</c:when>
	                        	 <c:when test="${payPalAddress eq 'true'}">
	                        	 <dsp:getvalueof var= "firstName" bean = "PayPalSessionBean.address.firstName"/>
                                  <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="${firstName}">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutfirstName errorcheckoutfirstName"/>
                                    <dsp:tagAttribute name="aria-describedby" value="errorcheckoutfirstName"/> 
                                  </dsp:input>
                                </c:when>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutfirstName errorcheckoutfirstName"/>
									<dsp:tagAttribute name="aria-describedby" value="errorcheckoutfirstName"/> 
                                  </dsp:input>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutfirstName errorcheckoutfirstName"/>
                                       <dsp:tagAttribute name="aria-describedby" value="errorcheckoutfirstName"/> 
                                  </dsp:input>
                                </c:otherwise>
                              </c:choose>
                        </div>
                    </div>
					<div class="clear"></div>
                </div>



                <%-- <div class="input">
                    <div class="label">
                        <label for="checkoutmidleName">
                            <bbbl:label key="lbl_shipping_new_middle_name" language="${pageContext.request.locale.language}" />
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.middleName" maxlength="40" name="checkoutmidleName" id="checkoutmidleName"/>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.middleName" maxlength="40" name="checkoutmidleName" id="checkoutmidleName" value=""/>
                                </c:otherwise>
                              </c:choose>
                        </div>
                    </div>
                </div> --%>

				<div class="clear"></div>
            </div>
			<div class="clear"></div>


            <div class="input clearfix">
                <div class="label">
                    <label id="lblcheckoutlastName" for="checkoutlastName">
                        <bbbl:label key="lbl_shipping_new_last_name" language="${pageContext.request.locale.language}" />
                        <span class="required">*</span>
                    </label>
                </div>
                <div class="text">
                    <div>
                         <c:choose>
                        	<c:when test="${beddingKit eq true || isPackHold eq true}">
                        	    <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName" value="">
                                   <dsp:tagAttribute name="aria-required" value="true"/>
                                   <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutlastName errorcheckoutlastName"/>
								   <dsp:tagAttribute name="aria-describedby" value="errorcheckoutlastName"/> 
                                 </dsp:input>
                        	</c:when>
                        	<c:when test="${payPalAddress eq 'true'}">
                        	 <dsp:getvalueof var= "lastName" bean = "PayPalSessionBean.address.lastName"/>
                                 <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName" value="${lastName}">
                                   <dsp:tagAttribute name="aria-required" value="true"/>
                                   <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutlastName errorcheckoutlastName"/>
								   <dsp:tagAttribute name="aria-describedby" value="errorcheckoutlastName"/> 
                                 </dsp:input>
                            </c:when>
                            <c:when test="${empty preFillValues || preFillValues}">
                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutlastName errorcheckoutlastName"/>
								<dsp:tagAttribute name="aria-describedby" value="errorcheckoutlastName"/> 
                              </dsp:input>
                            </c:when>
                            <c:otherwise>
                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName" value="">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutlastName errorcheckoutlastName"/>
								<dsp:tagAttribute name="aria-describedby" value="errorcheckoutlastName"/> 
                              </dsp:input>
                            </c:otherwise>
                          </c:choose>
                    </div>
                </div>
				<div class="clear"></div>
            </div>
			<div class="clear"></div>



            <div class="input clearfix">
                <div class="label">
                <c:choose>
                <c:when test="${!(empty beddingShipAddrVO.collegeName)}">
                <label id="lblcollege" for="college">
                 <bbbl:label key="lbl_reg_ph_collegeName" language ='${pageContext.request.locale.language}'/>
                 </label>
                 </c:when>
                <c:otherwise>
                 <label id="lblcompany" for="company">
                   <bbbl:label key="lbl_spc_shipping_new_company" language="${pageContext.request.locale.language}" />
                 </label>
                </c:otherwise>
               </c:choose>
                </div>
                <div class="text">
                    <div>
                        <c:choose>
                        	<%-- <c:when test="${beddingKit eq true}">
                        	   <input type="text" maxlength="50" name="college" id="college" value="${beddingShipAddrVO.collegeName}" aria-required="true" aria-labelledby="lblcompany errorcompany" />
                        	   <input type="hidden" maxlength="40" name="company" id="company" value="${beddingShipAddrVO.companyName}" aria-required="true" aria-labelledby="lblcompany errorcompany" />
                        	</c:when>
                        	<c:when test="${isPackHold eq true}">
                        	   <input type="text" maxlength="50" name="college" id="college" value="${beddingShipAddrVO.collegeName}" aria-required="true" aria-labelledby="lblcompany errorcompany" />
                        	   <input type="hidden" disabled="true" maxlength="40" name="company" id="company" value="${beddingShipAddrVO.collegeName}" aria-required="true" aria-labelledby="lblcompany errorcompany" />
                        	</c:when> --%>
                        	<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
                        	 <dsp:getvalueof var= "companyName" bean = "PayPalSessionBean.address.companyName"/>
                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company" value="${companyName}">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                              </dsp:input>
                            </c:when>
                            <c:when test="${empty preFillValues || preFillValues}">
                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company" value="${beddingShipAddrVO.collegeName}">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                              </dsp:input>
                            </c:when>
                            <c:otherwise>
                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company" value="">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                              </dsp:input>
                            </c:otherwise>
                          </c:choose>
                    </div>
                </div>
				<div class="clear"></div>
            </div>
			<div class="clear"></div>

            <div class="fieldsInlineWrapper clearfix">

                <div class="input">
                    <div class="label">
                        <label id="lbladdress1" for="address1">
                            <bbbl:label key="lbl_shipping_new_line1" language="${pageContext.request.locale.language}" />
                            <span class="required">*</span>
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                 <c:when test="${beddingKit eq true || isPackHold eq true}">
                                 	<dsp:getvalueof var= "address1" value= "${beddingShipAddrVO.addrLine1}"/>	
                            	 	<dsp:input type="text" iclass="poBoxAddNotAllowed" bean="BBBShippingGroupFormhandler.address.address1" maxlength="50" name="address1" id="address1" value= "${address1}">
										<dsp:tagAttribute name="autocomplete" value="off"/>
                            	 		<dsp:tagAttribute name="aria-required" value="true"/>
	                                   <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lbladdress1 erroraddress1"/> 
									   <dsp:tagAttribute name="aria-describedby" value="erroraddress1"/> 
	                                 </dsp:input>
                                </c:when>
                                <c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
		                       	 <dsp:getvalueof var= "address1" bean = "PayPalSessionBean.address.address1"/>		                       	 
		                              <dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address1" maxlength="50" name="address1" id="address1" value="${address1}">
	                                   <dsp:tagAttribute name="aria-required" value="true"/>
	                                   <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lbladdress1 erroraddress1"/>
									   <dsp:tagAttribute name="aria-describedby" value="erroraddress1"/> 
	                                 </dsp:input>
		                         </c:when>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address1" maxlength="50" name="address1" id="address1">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lbladdress1 erroraddress1"/>
									<dsp:tagAttribute name="aria-describedby" value="erroraddress1"/> 
                                  </dsp:input>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address1" maxlength="50" name="address1" id="address1" value="">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lbladdress1 erroraddress1"/>
									<dsp:tagAttribute name="aria-describedby" value="erroraddress1"/> 
                                  </dsp:input>
                                </c:otherwise>
                              </c:choose>
                        </div>
                    </div>
					<div class="clear"></div>
                </div>



                <div class="input">
                    <div class="label">
                        <label id="lbladdress2" for="address2">
                            <bbbl:label key="lbl_shipping_new_line2" language="${pageContext.request.locale.language}" />
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                <c:when test="${beddingKit eq true || isPackHold eq true}">
                                	<dsp:getvalueof var= "address2" value= "${beddingShipAddrVO.addrLine2}"/>	
                            	 	<dsp:input type="text" iclass="poBoxAddNotAllowed" bean="BBBShippingGroupFormhandler.address.address2" maxlength="50" name="address2" id="address2" value="${address2}">
                            	 		<dsp:tagAttribute name="aria-required" value="true"/>
	                                   <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/> 
	                                 </dsp:input>
                                </c:when>
                                <c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
		                       	 <dsp:getvalueof var= "address2" bean = "PayPalSessionBean.address.address2"/>
		                              <dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address2" maxlength="50" name="address2" id="address2" value="${address2}">
	                                    <dsp:tagAttribute name="aria-required" value="true"/>
	                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
	                                  </dsp:input>
		                         </c:when>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address2" maxlength="50" name="address2" id="address2">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                                  </dsp:input>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address2" maxlength="50" name="address2" id="address2" value="">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                                  </dsp:input>

                                </c:otherwise>
                              </c:choose>
                        </div>
                    </div>
					<div class="clear"></div>
                </div>

				<div class="clear"></div>
            </div>
			<div class="clear"></div>

            <div class="fieldsInlineWrapper clearfix">

                <div class="input">
                    <div class="label">
                        <label id="lblcityName" for="cityName">
                            <bbbl:label key="lbl_shipping_new_city" language="${pageContext.request.locale.language}" />
                            <span class="required">*</span>
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                <c:when test="${beddingKit eq true || isPackHold eq true}">
                                	<dsp:getvalueof var= "city" value= "${beddingShipAddrVO.city}"/>	
                            	 	<dsp:input type="text" iclass="poBoxAddNotAllowed" bean="BBBShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName" value="${city}">
                            	 		<dsp:tagAttribute name="aria-required" value="true"/>
	                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcityName errorcityName"/> 
										<dsp:tagAttribute name="aria-describedby" value="errorcityName"/> 
	                                 </dsp:input>
                                </c:when>
                                 <c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
		                       	 <dsp:getvalueof var= "city" bean = "PayPalSessionBean.address.city"/>
		                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName" value="${city}">
	                                    <dsp:tagAttribute name="aria-required" value="true"/>
	                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcityName errorcityName"/>
										<dsp:tagAttribute name="aria-describedby" value="errorcityName"/> 
	                                  </dsp:input>
		                         </c:when>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcityName errorcityName"/>
									<dsp:tagAttribute name="aria-describedby" value="errorcityName"/> 
                                  </dsp:input>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName" value="">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcityName errorcityName"/>
									<dsp:tagAttribute name="aria-describedby" value="errorcityName"/> 
                                  </dsp:input>
                                </c:otherwise>
                              </c:choose>
                        </div>
                    </div>
					<div class="clear"></div>
                </div>



                <div class="input">
                    <div class="label">
                        <label id="lblstateName" for="stateName">

						  <c:choose>
							 <c:when test="${currentSiteId eq 'BedBathCanada'}">
								  <bbbl:label key="lbl_addnewbillingaddressprovince" language="${pageContext.request.locale.language}" />
							  </c:when>
							  <c:otherwise>
								  <bbbl:label key="lbl_shipping_new_state" language="${pageContext.request.locale.language}" />
							  </c:otherwise>
						  </c:choose><span class="required">*</span>
						</label>
                    </div>
                    <div class="select noMar cb">
                        <c:choose>
                        <c:when test="${beddingKit eq true || isPackHold eq true}">
							    <dsp:select bean="BBBShippingGroupFormhandler.address.state" name="stateName" id="stateName" iclass="shippingStateName">
								<dsp:droplet name="StateDroplet">
									<dsp:oparam name="output">
										 <c:choose>
											<c:when test="${currentSiteId eq 'BedBathCanada'}">
														  <option value=""><bbbl:label key="lbl_easy_return_form_select_province" language="${pageContext.request.locale.language}"/></option>	
													  </c:when>
													  <c:otherwise>
													      <option value=""><bbbl:label key="lbl_search_store_select_state" language="${pageContext.request.locale.language}"/></option>
													  </c:otherwise>
										   </c:choose>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="location" />
											<dsp:param name="sortProperties" value="+stateName" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="element.stateName" id="elementVal"></dsp:getvalueof>
												<dsp:getvalueof param="element.stateCode" id="elementCode"></dsp:getvalueof>
												<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>
													<c:choose>
														<c:when test="${fn:toLowerCase(beddingState) eq fn:toLowerCase(elementCode)}">
															<option data-saveToProfile="${showOnBilling}" value="${elementCode}" selected="selected">
																${elementVal}
															</option>
														</c:when>
														<c:otherwise>
															<option data-saveToProfile="${showOnBilling}" value="${elementCode}">
																${elementVal}
															</option>
														</c:otherwise>
													</c:choose>


											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:select>
							</c:when>
							<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
								<dsp:getvalueof var= "state" bean = "PayPalSessionBean.address.state"/>
							    <dsp:select bean="BBBShippingGroupFormhandler.address.state" name="stateName" id="stateName" iclass="shippingStateName">
								<dsp:droplet name="StateDroplet">
									<dsp:oparam name="output">
										 <c:choose>
											<c:when test="${currentSiteId eq 'BedBathCanada'}">
														  <option value=""><bbbl:label key="lbl_easy_return_form_select_province" language="${pageContext.request.locale.language}"/></option>	
													  </c:when>
													  <c:otherwise>
													      <option value=""><bbbl:label key="lbl_search_store_select_state" language="${pageContext.request.locale.language}"/></option>
													  </c:otherwise>
  										 </c:choose>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="location" />
											<dsp:param name="sortProperties" value="+stateName" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="element.stateName" id="elementVal"></dsp:getvalueof>
												<dsp:getvalueof param="element.stateCode" id="elementCode"></dsp:getvalueof>
												<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>
													<c:choose>
														<c:when test="${fn:toLowerCase(state) eq fn:toLowerCase(elementCode)}">
															<option data-saveToProfile="${showOnBilling}" value="${elementCode}" selected="selected">
																${elementVal}
															</option>
														</c:when>
														<c:otherwise>
															<option data-saveToProfile="${showOnBilling}" value="${elementCode}">
																${elementVal}
															</option>
														</c:otherwise>
													</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:select>
							</c:when>
                            <c:when test="${empty preFillValues || preFillValues}">
							    <dsp:select bean="BBBShippingGroupFormhandler.address.state" name="stateName" id="stateName" iclass="shippingStateName">
								<dsp:droplet name="StateDroplet">
									<dsp:oparam name="output">
										 <c:choose>
												 <c:when test="${currentSiteId eq 'BedBathCanada'}">
														  <option value=""><bbbl:label key="lbl_easy_return_form_select_province" language="${pageContext.request.locale.language}"/></option>	
													  </c:when>
													  <c:otherwise>
													      <option value=""><bbbl:label key="lbl_search_store_select_state" language="${pageContext.request.locale.language}"/></option>
													  </c:otherwise>
										   </c:choose>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="location" />
											<dsp:param name="sortProperties" value="+stateName" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="element.stateName" id="elementVal"></dsp:getvalueof>
												<dsp:getvalueof param="element.stateCode" id="elementCode"></dsp:getvalueof>
												<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>
													<c:choose>
														<c:when test="${fn:toLowerCase(beddingState) eq fn:toLowerCase(elementCode)}">
															<option data-saveToProfile="${showOnBilling}" value="${elementCode}" selected="selected">
																${elementVal}
															</option>
														</c:when>
														<c:otherwise>
															<option data-saveToProfile="${showOnBilling}" value="${elementCode}">
																${elementVal}
															</option>
														</c:otherwise>
													</c:choose>


											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblstateName errorstateName"/>
								<dsp:tagAttribute name="aria-describedby" value="errorstateName"/> 
							</dsp:select>

                            </c:when>
                            <c:otherwise>
								<dsp:select bean="BBBShippingGroupFormhandler.address.state" name="stateName" id="stateName" nodefault="true" iclass="shippingStateName">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblstateName errorstateName"/>
								<dsp:tagAttribute name="aria-describedby" value="errorstateName"/> 
									<dsp:droplet name="StateDroplet">
										<dsp:oparam name="output">
											 <c:choose>
													<c:when test="${currentSiteId eq 'BedBathCanada'}">
														  <option value=""><bbbl:label key="lbl_easy_return_form_select_province" language="${pageContext.request.locale.language}"/></option>	
													  </c:when>
													  <c:otherwise>
													      <option value=""><bbbl:label key="lbl_search_store_select_state" language="${pageContext.request.locale.language}"/></option>
													  </c:otherwise>
											   </c:choose>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="location" />
												<dsp:param name="sortProperties" value="+stateName" />
												<dsp:oparam name="output">
													<dsp:getvalueof param="element.stateName" id="elementVal"></dsp:getvalueof>
													<dsp:getvalueof param="element.stateCode" id="elementCode"></dsp:getvalueof>
												<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>

													 <c:choose>
														<c:when test="${fn:toLowerCase(beddingState) eq fn:toLowerCase(elementVal)}" >
															<option data-saveToProfile="${showOnBilling}" value="${elementCode}" selected="selected">
																${elementVal}
															</option>
														</c:when>
														<c:otherwise>
															<option data-saveToProfile="${showOnBilling}" value="${elementCode}">
																${elementVal}
															</option>
														</c:otherwise>
													</c:choose>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									</dsp:droplet>
								</dsp:select>

                            </c:otherwise>
                          </c:choose>
                    </div>
					<div class="clear"></div>
                </div>

            </div>
            <div class="clear"></div>

            <div class="input clearfix">
                <div class="label">
                    <label id="lblzip" for="zip">
						<c:choose>
								 <c:when test="${currentSiteId eq 'BedBathCanada'}">
									  <bbbl:label key="lbl_subscribe_canadazipcode" language="${pageContext.request.locale.language}" />
								  </c:when>
								  <c:otherwise>
								      <bbbl:label key="lbl_shipping_new_zip" language="${pageContext.request.locale.language}" />
								  </c:otherwise>
						</c:choose>
                        <span class="required">*</span>
                    </label>
                </div>
                <div class="text">
                    <div>
                        <c:choose>
                            <c:when test="${defaultCountry ne 'US'}">
                              <c:set var="zipCodeClass" value="zipCA" scope="page"/>
                            </c:when>
                            <c:otherwise>
                              <c:set var="zipCodeClass" value="zipUS" scope="page"/>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${beddingKit eq true || isPackHold eq true}">
                            	<dsp:getvalueof var= "zip" value= "${beddingShipAddrVO.zip}"/>	
                           	 	<dsp:input type="text" iclass="poBoxAddNotAllowed" bean="BBBShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip" value="${zip}">
                           	 		<dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblzip errorzip"/> 
									<dsp:tagAttribute name="aria-describedby" value="errorzip"/> 
                                 </dsp:input>
                        	</c:when>
                        	 <c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
		                       	 <dsp:getvalueof var= "postalCode" bean = "PayPalSessionBean.address.postalCode"/>
		                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip" value="${postalCode}">
		                                <dsp:tagAttribute name="aria-required" value="true"/>
		                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblzip errorzip"/>
										<dsp:tagAttribute name="aria-describedby" value="errorzip"/> 
		                              </dsp:input>
		                         </c:when>
                            <c:when test="${empty preFillValues || preFillValues}">
                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblzip errorzip"/>
								<dsp:tagAttribute name="aria-describedby" value="errorzip"/> 
                              </dsp:input>
                            </c:when>
                            <c:otherwise>
                              <dsp:input type="text" bean="BBBShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip" value="">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblzip errorzip"/>
								<dsp:tagAttribute name="aria-describedby" value="errorzip"/> 
                            </dsp:input>
                            </c:otherwise>
                          </c:choose>
                    </div>
                </div>
				<div class="clear"></div>
            </div>
            <div class="sddShippingError error hidden">
				<bbbe:error key="err_sdd_zip_change" language="${pageContext.request.locale.language}"/>
				</div>
			<dsp:input type="hidden"  bean="BBBShippingGroupFormhandler.address.countryName" id="countryName" name="countryName" value=""/>
			<dsp:input type="hidden" name="poBoxFlag" bean="BBBShippingGroupFormhandler.poBoxFlag" id="poBoxFlag" value=""/>
			<dsp:input type="hidden" name="poBoxStatus" bean="BBBShippingGroupFormhandler.poBoxStatus" id="poBoxStatus" value=""/>
			
            <dsp:droplet name="Switch">
                <dsp:param name="value" bean="Profile.transient"/>
                <dsp:oparam name="false">
                    <div class="clear"></div>
                    <div class="checkboxItem input clearfix noPadBot noMarBot">
                        <dsp:getvalueof var="profileAddresses" param="profileAddresses" />
						<div class="checkbox">
							<c:choose>
                            	<c:when test="${beddingKit eq true || isPackHold eq true}">
                            		<dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="false" type="checkbox" name="saveToAccount" id="saveToAccount" checked="false" >
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                               		</dsp:input>
									<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.saveShippingAddress" value="false" />
                           		</c:when>
                            	<c:otherwise>
                           			<c:choose>
										<c:when test="${empty profileAddresses}">
											<dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" disabled="true" name="saveToAccount" id="saveToAccount" checked="true" >
                                        	<dsp:tagAttribute name="aria-checked" value="true"/>
                                        	<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                    		</dsp:input>		
											<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.saveShippingAddress" id="saveShippingAddress" value="true" />
										</c:when>
										<c:otherwise>
											<dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" iclass="Disable" name="saveToAccount" id="saveToAccount" checked="false">
                                        	<dsp:tagAttribute name="aria-checked" value="true"/>
                                        	<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                    		</dsp:input>
										</c:otherwise>
									</c:choose>
                            	</c:otherwise>
                            </c:choose>

                        </div>
                        <div class="label">
                            <label id="lblsaveToAccount" for="saveToAccount"><bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" /></label>
                        </div>
                    </div>
                </dsp:oparam>
                <dsp:oparam name="true">
                    <div class="clear"></div>
                    <div class="checkboxItem input clearfix noPadBot noMarBot" style="display:none">
                        <div class="checkbox">
                            <dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" name="saveToAccount" iclass="enableDisable" id="saveToAccount" checked="true">
                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                            </dsp:input>
                            <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.saveShippingAddress" id="collegeSelected" value="true" />
                        </div>
                        <div class="label">
                            <label id="lblsaveToAccount" for="saveToAccount"><bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" /></label>
                        </div>
                    </div>
                </dsp:oparam>
            </dsp:droplet>
        </div>
    </div>
     <dsp:getvalueof var="sessionBeanNull" bean = "PayPalSessionBean.sessionBeanNull"/>

</dsp:page>