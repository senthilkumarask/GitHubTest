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
  <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
  <dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>   
  <dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
  <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet" />
  <dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof id="isAnonymousProfile" bean="Profile.transient" />
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
  <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
	<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO"/>
	<dsp:getvalueof var="isFormException" param="isFormException"/>	
  <dsp:getvalueof param="commerceItemCount" var="totalCartCount"/>      
  <dsp:getvalueof param="registryItemCount" var="registryCount"/>      
  <dsp:getvalueof param="isDefaultShippAddr" var="isDefaultShippAddr"/>
  <dsp:getvalueof id ="order" bean="ShoppingCart.current" />
  <dsp:getvalueof param="isFromPreview" var="isFromPreview"/>
  

  <%-- adding this config value in the case of ajax requested shipping section  --%>
  <c:set var="shipTo_POBoxOn" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions" /></c:set>
  <%-- 
   debugging newAddress.jsp <br />
   shipTo_POBoxOn: ${shipTo_POBoxOn} <br />
  isFromPreview: ${isFromPreview}<br />
  registryCount : ${registryCount}<br />
	totalCartCount: ${totalCartCount}<br />
  selectedAddress: ${selectedAddress}<br />
  shippingAddress: <dsp:valueof  bean="/atg/commerce/ShoppingCart.current.shippingAddress"/><br />
  shippingAddress.id: <dsp:valueof  bean="/atg/commerce/ShoppingCart.current.shippingAddress.id"/><br />
  shippingAddress.phoneNumber: <dsp:valueof  bean="/atg/commerce/ShoppingCart.current.shippingAddress.phoneNumber"/><br />
  Profile.mobileNumber:<dsp:valueof  bean="/atg/userprofiling/Profile.mobileNumber"/><br />
   --%>

    <%-- debugging code       

    <dsp:getvalueof var="cartBillingAddress" bean="/atg/commerce/ShoppingCart.current.billingAddress" />
    ShoppingCart.current.billingAddress : ${cartBillingAddress}
    <br />${cartBillingAddress.id}
    <br />${cartBillingAddress.phoneNumber}
    <br />${cartBillingAddress.email}
    --%>
    
     


   <c:choose>
		 <c:when test = "${beddingShipAddrVO != null }">
		   <c:set var="beddingKit" value="true"/>
		   <c:set var="beddingState" value="${beddingShipAddrVO.state}"/>
		 </c:when>
		 <c:otherwise>
		 	<c:set var="beddingKit" value="false"/>
		 </c:otherwise>
	 </c:choose>
	
    <c:set var="newAddrChecked" value="${false}"/>
    <c:set var="RegistrantAddressOnShippingGroup" value="${false}"/>


    <c:choose >
        <%-- if logged in , loop through profile address and get the default shipping address --%>
        <c:when test="${!isAnonymousProfile}" >
             <dsp:getvalueof var="defaultShippingId" bean="Profile.shippingAddress.repositoryId" />
            <dsp:getvalueof var="defaultBillingID" bean="Profile.billingAddress.repositoryId" />

            <%-- debugging newAddress.jsp <br />
                current order shipping address: <dsp:valueof bean="ShoppingCart.current.shippingAddress"/> <br />
                selectedAddress: ${selectedAddress} <br />
                shipping address: <dsp:valueof bean="Profile.shippingAddress.id"/><br/>    
                <dsp:valueof bean="Profile.secondaryAddresses"/><br/>    
                <dsp:getvalueof param="Profile.shippingAddress" var="currentAddress"/>  
            --%>

            <%-- getting the already entered order address so we can propopulate the shipping inputs --%>
            <%-- if registry item is present, don't fetch the registrant address if  --%>            
            <c:choose >              
              <c:when test="${registryCount > 0}" >
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                    <dsp:param name="array" param="groupAddresses" />
                    <dsp:param name="elementName" value="address" />
                    <dsp:oparam name="output">      
                      <dsp:getvalueof param="address.id" var="currentAddressID"/>  
                      <dsp:getvalueof param="address.identifier" var="currentAddressIdentifier"/>
                      <dsp:getvalueof param="address.registryId" var="shippingRegistryID"/>
                      <%-- dubugging   
                        registryId: <dsp:valueof param="address.registryId" />  <br>
                        <dsp:valueof param="address.id"/><br>
                        <dsp:valueof param="address.identifier"/><br>
                       --%>

                      <c:if test="${not empty shippingRegistryID}">
                         <c:set var="RegistrantAddressOnShippingGroup" value="${true}"/>
                      </c:if>

                      <c:choose>            
                        <c:when test="${selectedAddress == currentAddressID && !fn:contains(selectedAddress, 'registry')}">
                          <dsp:getvalueof param="address" var="currentAddress"/>  
                          <c:set var="newAddrChecked" value="${true}"/>
                          <c:set var="usingRegistrantAddress" value="${false}"/>
                        </c:when>                
                        <c:when test="${fn:contains(selectedAddress, 'PROFILE')}">
                          <dsp:getvalueof param="address" var="currentAddress"/>  
                          <c:set var="newAddrChecked" value="${true}"/>
                          <c:set var="usingRegistrantAddress" value="${false}"/>
                        </c:when>                        
                      </c:choose>      
                      
                    </dsp:oparam>
                </dsp:droplet>  
              </c:when>
              <c:otherwise>
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                    <dsp:param name="array" param="groupAddresses" />
                    <dsp:param name="elementName" value="address" />
                    <dsp:oparam name="output">      
                      <dsp:getvalueof param="address.id" var="currentAddressID"/>
                      <dsp:getvalueof param="address.identifier" var="currentAddressIdentifier"/>
                      <dsp:getvalueof param="address.registryId" var="shippingRegistryID"/>
                      <%-- if customer had registry item in their cart, 
                          then chose to ship to registrant, 
                          then removed the registry item from their cart,
                          need to empty the registrant address --%>
                           
                          <%--dubugging   
                        registryId: <dsp:valueof param="address.registryId" />  <br>
                        <dsp:valueof param="address.id"/><br>
                        <dsp:valueof param="address.identifier"/><br>
                        --%>

                        <c:if test="${not empty shippingRegistryID}">
                          <c:set var="RegistrantAddressOnShippingGroup" value="${true}"/>
                        </c:if> 

                      <c:choose>            
                        <c:when test="${selectedAddress == currentAddressID && !fn:contains(currentAddressIdentifier, 'registry')}">
                          <dsp:getvalueof param="address" var="currentAddress"/>  
                          <c:set var="newAddrChecked" value="${true}"/>
                          <c:set var="usingRegistrantAddress" value="${false}"/>
                        </c:when> 
                        <c:when test="${fn:contains(selectedAddress, 'PROFILE')}">
                          <dsp:getvalueof param="address" var="currentAddress"/>                            
                        </c:when> 
                      </c:choose>      
                      
                    </dsp:oparam>
                </dsp:droplet>  
              </c:otherwise>
            </c:choose>

            

            <dsp:droplet name="ForEach">    
                <dsp:param name="array" param="profileAddresses" />
                <dsp:param name="elementName" value="address" />
                <dsp:param name="sortProperties" value="id"/>
                <dsp:oparam name="output">
                      <dsp:getvalueof param="count" var="profileAddressCount" scope="request" />

                      <dsp:getvalueof param="address.identifier" var="currentAddressIdentifer"/>
                      <dsp:getvalueof param="address.id" var="currentAddressID"/>
                      <%-- debugging 
                      In the loop
                      defaultShippingId ${defaultShippingId}<br>
                      ${fn:indexOf( address.identifier, defaultShippingId)}<br>
                      <dsp:valueof param="address.id"/><br>
                      currentAddressID:${currentAddressID}<br><br>
                      --%>


                      <%-- only check for default shipping address if there is no address on the order, fetched above --%>        
                      <c:if test="${empty currentAddress}">            
                        <c:choose>            
                            <c:when test="${defaultShippingId eq currentAddressID}">
                              <dsp:getvalueof param="address" var="currentAddress"/>
                            </c:when>                
                        </c:choose>                    
                      </c:if>
                                                                                                  
                </dsp:oparam>
            </dsp:droplet>


            
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
        <c:otherwise>
            <%-- getting the already entered address so we can propopulate the shipping inputs --%>
            <%--IF the address was new and was not saved in profile --%>
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="groupAddresses" />
                <dsp:param name="elementName" value="address" />
                <dsp:oparam name="output">      

                  <%-- getting the already entered order address so we can propopulate the shipping inputs --%>
                  <%-- if registry item is present, don't fetch the registrant address if  --%>            
                  <c:choose >              
                    <c:when test="${registryCount > 0}" >
                      <dsp:getvalueof param="address.registryId" var="currentRegistryID"/>  
                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                            <dsp:param name="array" param="groupAddresses" />
                            <dsp:param name="elementName" value="address" />
                            <dsp:oparam name="output">      
                              <dsp:getvalueof param="address.id" var="currentAddressID"/>  
                              <c:if test="${selectedAddress == currentAddressID && empty currentRegistryID}">
                                <c:set var="newAddrChecked" value="${true}"/>
                                <dsp:getvalueof param="address" var="currentAddress"/>
                              </c:if>
                            </dsp:oparam>
                        </dsp:droplet>  
                    </c:when>
                    <c:otherwise>
                      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                          <dsp:param name="array" param="groupAddresses" />
                          <dsp:param name="elementName" value="address" />
                          <dsp:oparam name="output">      
                          <dsp:getvalueof param="address.id" var="currentAddressID"/> 
                          <dsp:getvalueof param="address.identifier" var="currentAddressIdentifer"/>
                          
                           <c:choose>            
                            <c:when test="${selectedAddress == currentAddressID && !fn:contains(currentAddressIdentifer, 'registry')}">
                              <dsp:getvalueof param="address" var="currentAddress"/>  
                              <c:set var="newAddrChecked" value="${true}"/>
                              <c:set var="usingRegistrantAddress" value="${false}"/>
                            </c:when> 
                            <c:when test="${fn:contains(currentAddressIdentifer, 'registry')}">
                              <c:set var="RegistrantAddressOnShippingGroup" value="${true}"/>
                            </c:when> 
                          </c:choose> 
                            
                          </dsp:oparam>
                      </dsp:droplet>    
                    </c:otherwise>
                  </c:choose>

                </dsp:oparam>
            </dsp:droplet>            
        </c:otherwise>
     </c:choose>
<c:set var="newAddrChecked" value="${newAddrChecked}" scope="request"/>
<dsp:getvalueof param="isPackHold" var="isPackHold"/>

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

    <c:set var="gotCurrentAddress" value="${false}"/>

    <c:if test="${not empty currentAddress}"> 
      <c:set var="gotCurrentAddress" value="${true}"/>   
    </c:if>


    <c:choose>
      <c:when test = "${registryCount > 0 }">
        <c:set var="hideShippingForm" value="hidden"/>
        <c:set var="hideContactForm" value=""/>
      </c:when>
      <c:when test="${not empty currentAddress}">
        <c:set var="hideShippingForm" value="hidden"/>
        <c:set var="hideContactForm" value="hidden"/>
      </c:when>
      <c:otherwise>
        <c:set var="hideShippingForm" value=""/>
        <c:set var="hideContactForm" value=""/>
      </c:otherwise>
    </c:choose>
    

    

    <div id="newAddressForm" class="radioItem input clearfix last">    	
	    	
          <c:if test="${registryCount > 0}">        
            <div class="clearfix marTop_10"></div>
          </c:if>

	    	  <c:choose>
	          <c:when test="${empty preFillValues || preFillValues}">
              
	             <%-- BBBSL-971 starts --%>
	            <c:choose>
		            <c:when test="${empty selectedAddress && beddingKit eq true}">
		              <dsp:input type="radio" name="addressToShip" id="addressToShip4n" value="college"
	                                           checked="true" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
	                  <dsp:input type="hidden" bean="BBBSPShippingGroupFormhandler.beddingKitOrder" value="beddingKit"/>
		            </c:when>
					      <c:when test="${isPackHold eq true}">

		               <dsp:input type="radio" name="addressToShip" id="addressToShip4n" value="college"
	                                           checked="true" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
		            </c:when>

					      <c:when test="${( empty selectedAddress) &&  beddingKit eq false}">

                  <c:choose>
                    <c:when test="${registryCount > 0}">
                      <div class="radio">                        
		                    <dsp:input type="radio" iclass="newAddOpt" name="addressToShip" id="addressToShip4nNormal" value="true"
	                         checked="${newAddrChecked}" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
                      </div>                       
                    </c:when>
                    <c:otherwise>
                      <dsp:input type="hidden" iclass="newAddOpt" name="addressToShip" id="addressToShip4nNormal" value="true"
                                             checked="true" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
                    </c:otherwise>
                  </c:choose>   
		            </c:when>
		            <c:otherwise>
                    <c:choose>
                      <c:when test="${registryCount > 0}">
                        <div class="radio">               
                          <c:choose>
                            <c:when test="${newAddrChecked}">           
                              <dsp:input type="radio" iclass="newAddOpt" name="addressToShip" id="addressToShip4nNormal" value="true"
                            checked="true" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
                            </c:when>
                            <c:otherwise>
                              <dsp:input type="radio" iclass="newAddOpt" name="addressToShip" id="addressToShip4nNormal" value="true"
                            checked="false" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
                            </c:otherwise>
                          </c:choose>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <c:choose>
                          <c:when test="${isAnonymousProfile}">
                            <dsp:input 
                              type="hidden" 
                              name="addressToShip" 
                              id="addressToShip4nNormal" 
                              value="true"
                              iclass="newAddOpt"
                              bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
                          <dsp:input  type="hidden" name="updateAddress" id="updateAddress" value="false" bean="BBBSPShippingGroupFormhandler.updateAddress" />
                          </c:when>
                          
                          <c:otherwise>
                            <dsp:input type="hidden" iclass="newAddOpt" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress.identifier}"                                
                              bean="BBBSPShippingGroupFormhandler.shipToAddressName" /> 
                              <dsp:input  type="hidden" name="updateAddress" id="updateAddress" value="true" bean="BBBSPShippingGroupFormhandler.updateAddress" />                            
                              <dsp:input  type="hidden" name="nickname" id="nickname" value="${nickname }" bean="BBBSPShippingGroupFormhandler.nickname" />
                    		  <dsp:input  type="hidden" name="currentAddressID" id="currentAddressID" value="${currentAddress.id}" bean="BBBSPShippingGroupFormhandler.currentAddressID" />                             
                              </c:otherwise>
                        </c:choose>
                      </c:otherwise>
                    </c:choose>
		            </c:otherwise>
		           </c:choose>
	          </c:when>
	          <c:otherwise>
              <c:choose>
                <c:when test="${registryCount > 0}">
                  <div class="radio">
                    <c:choose>
                      <c:when test="${newAddrChecked}">           
                        <dsp:input type="radio" iclass="newAddOpt" name="addressToShip" id="addressToShip4nNormal" value="true"
                      checked="true" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="radio" iclass="newAddOpt" name="addressToShip" id="addressToShip4nNormal" value="true"
                      checked="false" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </c:when>
                <c:otherwise>
                   <c:choose>
                    <c:when test="${isAnonymousProfile}">
                      <dsp:input type="hidden" name="addressToShip" id="addressToShip4nNormal" value="true"
                                           checked="true" iclass="newAddOpt"
                                           bean="BBBSPShippingGroupFormhandler.shipToAddressName">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4n"/>
                      </dsp:input>
                       <dsp:input  type="hidden" name="updateAddress" id="updateAddress" value="false" bean="BBBSPShippingGroupFormhandler.updateAddress" />
                    	<dsp:input  type="hidden" name="nickname" id="nickname" value="${nickname }" bean="BBBSPShippingGroupFormhandler.nickname" />
                    </c:when>
                    <c:otherwise>
                      <dsp:input type="hidden" iclass="newAddOpt" name="addressToShip" id="addressToShip4nNormal" value="${currentAddress.identifier}"                                
                        bean="BBBSPShippingGroupFormhandler.shipToAddressName" />
                        <dsp:input  type="hidden" name="updateAddress" id="updateAddress" value="true" bean="BBBSPShippingGroupFormhandler.updateAddress" />                             
                    	<dsp:input  type="hidden" name="nickname" id="nickname" value="${nickname }" bean="BBBSPShippingGroupFormhandler.nickname" />
                    	<dsp:input  type="hidden" name="currentAddressID" id="currentAddressID" value="${currentAddressID}" bean="BBBSPShippingGroupFormhandler.currentAddressID" />                             
                    </c:otherwise>
                  </c:choose>  
                </c:otherwise>  
              </c:choose>
	            
	          </c:otherwise>
	        </c:choose>
		      

		 

          <c:if test="${registryCount > 0}">


            <div class="label">
              <label id="lbladdressToShip4n" for="addressToShip4n">
                <span>
                <c:choose>
                 <c:when test="${beddingKit eq true || isPackHold eq true}">
                   <bbbl:label key="lbl_spc_college_address" language="${pageContext.request.locale.language}" /></span>
                 </c:when>
                 <c:otherwise>
                   <bbbl:label key="lbl_spc_shipping_new_address" language="${pageContext.request.locale.language}" /></span>
                 </c:otherwise>
                </c:choose>
              </label>
			  <c:if test="${!isAnonymousProfile && profileAddressCount > 0}">
				<div class="clearfix">
					<a id="viewAllAddress" href="#"><bbbl:label key="lbl_spc_view_all_addresses" language="${pageContext.request.locale.language}" /></a>
				</div>
			  </c:if>
            </div>
            
          
            <div class="clearfix marBottom_25"></div>
          

          </c:if>

        
            
          <div id="addressFields" class="subForm  clearfix  ${hideShippingForm}">

				      <dsp:input bean="BBBSPShippingGroupFormhandler.address.country" value="${defaultCountry}" id="defaultShippingCountry" type="hidden"/>
              <%-- this input has no purspose except to accept the country value from the QAS response --%>
              <input  value="" id="QASShippingCountry" type="hidden"/>
  
              <div class="input_wrap">
                  

                  <label class="popUpLbl" id="lblcheckoutfirstName" for="checkoutfirstName">
                      <bbbl:label key="lbl_spc_shipping_new_first_name" language="${pageContext.request.locale.language}" />
                      <span class="required">*</span>
                  </label>                      
                                       
                <c:choose>
                  <c:when test="${beddingKit eq true}">
              	   <input type="text" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="" aria-required="true" aria-labelledby="lblcheckoutfirstName errorcheckoutfirstName" />
              	</c:when>
              	 <c:when test="${payPalAddress eq 'true'}">
              	     <dsp:getvalueof var= "firstName" bean = "PayPalSessionBean.address.firstName"/>
                      <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="${firstName}">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
                      </dsp:input>
                    </c:when>
                    <c:when test="${empty preFillValues || preFillValues}">
                      <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="${currentAddress.firstName}">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
                      </dsp:input>
                    </c:when>
                    <c:otherwise>
                      <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="${currentAddress.firstName}">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
                      </dsp:input>
                    </c:otherwise>
                  </c:choose>


                  
              </div>



                <%-- <div class="input">
                    <div class="label">
                        <label for="checkoutmidleName">
                            <bbbl:label key="lbl_spc_shipping_new_middle_name" language="${pageContext.request.locale.language}" />
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.middleName" maxlength="40" name="checkoutmidleName" id="checkoutmidleName"/>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.middleName" maxlength="40" name="checkoutmidleName" id="checkoutmidleName" value=""/>
                                </c:otherwise>
                              </c:choose>
                        </div>
                    </div>
                </div> --%>

				      <div class="clear"></div>
            
			     
            <div class="clear"></div>


            <div class="input_wrap">
                
              <label class="popUpLbl" id="lblcheckoutlastName" for="checkoutlastName">
                    <bbbl:label key="lbl_spc_shipping_new_last_name" language="${pageContext.request.locale.language}" />
                    <span class="required">*</span>
              </label>
            
               <c:choose>
              	<c:when test="${beddingKit eq true || isPackHold eq true}">
              	   <input type="text" maxlength="40" name="checkoutlastName" id="checkoutlastName" aria-required="true" aria-labelledby="lblcheckoutlastName errorcheckoutlastName" />
              	</c:when>
              	<c:when test="${payPalAddress eq 'true'}">
              	 <dsp:getvalueof var= "lastName" bean = "PayPalSessionBean.address.lastName"/>
                       <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName" value="${lastName}">
                         <dsp:tagAttribute name="aria-required" value="true"/>
                         <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
                       </dsp:input>
                  </c:when>
                  <c:when test="${empty preFillValues || preFillValues}">
                    <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName" value="${currentAddress.lastName}">
                      <dsp:tagAttribute name="aria-required" value="true"/>
                      <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
                    </dsp:input>
                  </c:when>
                  <c:otherwise>
                    <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName" value="${currentAddress.lastName}">
                      <dsp:tagAttribute name="aria-required" value="true"/>
                      <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
                    </dsp:input>
                  </c:otherwise>
                </c:choose>
          
				        
            </div>
			       
             <div class="clear"></div>


            <div class="input_wrap clearfix">
              <c:choose>
                <c:when test="${!(empty beddingShipAddrVO.collegeName)}">
                  <label class="popUpLbl" id="lblcollege" for="college">
                    <bbbl:label key="lbl_reg_ph_collegeName" language ='${pageContext.request.locale.language}'/>
                  </label>
                 </c:when>
                <c:otherwise>
                  <label class="popUpLbl" id="lblcompany" for="company">
                    <bbbl:label key="lbl_spc_shipping_new_company" language="${pageContext.request.locale.language}" />
                  </label>
                </c:otherwise>
              </c:choose>
                
                
                        <c:choose>
	                        <c:when test="${beddingKit eq true}">
	                 	      <input type="text" disabled="true" maxlength="50" name="college" id="college" value="${beddingShipAddrVO.collegeName}" aria-required="true" aria-labelledby="lblcompany errorcompany" />
	                 	      <input type="hidden" disabled="true" maxlength="40" name="company" id="company" value="${beddingShipAddrVO.companyName}" aria-required="true" aria-labelledby="lblcompany errorcompany" />
	                 	    </c:when>
	                 	    <c:when test="${isPackHold eq true}">
	                 	       <input type="text" disabled="true" maxlength="50" name="college" id="college" value="${beddingShipAddrVO.collegeName}" aria-required="true" aria-labelledby="lblcompany errorcompany" />
	                 	       <input type="hidden" disabled="true" maxlength="40" name="company" id="company" value="${beddingShipAddrVO.collegeName}" aria-required="true" aria-labelledby="lblcompany errorcompany" />
	                 	     </c:when>
                        	
                        	<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
                        	 <dsp:getvalueof var= "companyName" bean = "PayPalSessionBean.address.companyName"/>
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company" iclass="optional" value="${companyName}">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                              </dsp:input>
                            </c:when>
                            <c:when test="${empty preFillValues || preFillValues}">
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company" iclass="optional" value="${currentAddress.companyName}">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                              </dsp:input>
                            </c:when>
                            <c:otherwise>
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company" iclass="optional" value="">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                              </dsp:input>
                            </c:otherwise>
                          </c:choose>
                
            </div>
			     
            <div class="clear"></div>

            

                <div class="input_wrap">
                  
                  <label class="popUpLbl" id="lbladdress1" for="address1">
                      <bbbl:label key="lbl_spc_shipping_new_line1" language="${pageContext.request.locale.language}" />
                      <span class="required">*</span>
                  </label>
            
                  <c:choose>
                       <c:when test="${beddingKit eq true || isPackHold eq true}">
                  	 	<input type="text" iclass="poBoxAddNotAllowed" disabled="true" maxlength="50" name="address1" id="address1" value= "${beddingShipAddrVO.addrLine1}" aria-required="true" aria-labelledby="lbladdress1 erroraddress1" />
                      </c:when>
                      <c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
                 	      <dsp:getvalueof var= "address1" bean = "PayPalSessionBean.address.address1"/>		                       	 
                        <dsp:input type="text" iclass="${iclassValue}" bean="BBBSPShippingGroupFormhandler.address.address1" maxlength="50" name="address1" id="address1" value="${address1}">
							<dsp:tagAttribute name="autocomplete" value="off"/>
                           <dsp:tagAttribute name="aria-required" value="true"/>
                           <dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
                         </dsp:input>
                      </c:when>
                      <c:when test="${empty preFillValues || preFillValues}">
                        <dsp:input type="text" iclass="${iclassValue}" bean="BBBSPShippingGroupFormhandler.address.address1" maxlength="50" name="address1" id="address1" value="${currentAddress.address1}">
							<dsp:tagAttribute name="autocomplete" value="off"/>
                          <dsp:tagAttribute name="aria-required" value="true"/>
                          <dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
                        </dsp:input>
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="text" iclass="${iclassValue}" bean="BBBSPShippingGroupFormhandler.address.address1" maxlength="50" name="address1" id="address1"  value="${currentAddress.address1}">
                          <dsp:tagAttribute name="aria-required" value="true"/>
                          <dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
						  <dsp:tagAttribute name="autocomplete" value="off"/>
                        </dsp:input>
                      </c:otherwise>
                    </c:choose>
                    
                </div>

                <div class="clear"></div>

                <div class="input_wrap">
                    
                  <label class="popUpLbl" id="lbladdress2" for="address2">
                      <bbbl:label key="lbl_spc_shipping_new_line2" language="${pageContext.request.locale.language}" />
                  </label>
             
                  <c:choose>
                      <c:when test="${beddingKit eq true || isPackHold eq true}">
                  	 	<input type="text" iclass="${iclassValue}" disabled="true" maxlength="50" name="address2" id="address2" value= "${beddingShipAddrVO.addrLine2}" aria-required="true" aria-labelledby="lbladdress2 erroraddress2" />
                      </c:when>
                      <c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
                     	 <dsp:getvalueof var= "address2" bean = "PayPalSessionBean.address.address2"/>
                        <dsp:input type="text" iclass="${iclassValue}" bean="BBBSPShippingGroupFormhandler.address.address2" maxlength="50" name="address2" id="address2" value="${address2}">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                          </dsp:input>
                       </c:when>
                      <c:when test="${empty preFillValues || preFillValues}">
                        <dsp:input type="text" iclass="${iclassValue} optional" bean="BBBSPShippingGroupFormhandler.address.address2" maxlength="50" name="address2" id="address2" value="${currentAddress.address2}">
                          <dsp:tagAttribute name="aria-required" value="false"/>
                          <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                        </dsp:input>
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="text" iclass="${iclassValue} optional" bean="BBBSPShippingGroupFormhandler.address.address2" maxlength="50" name="address2" id="address2" value="${currentAddress.address2}">
                          <dsp:tagAttribute name="aria-required" value="false"/>
                          <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                        </dsp:input>

                      </c:otherwise>
                    </c:choose>
                   
                </div>

				        
            
			     <div class="clear"></div>

           <div class="input_wrap clearfix">
                
              <label class="popUpLbl" id="lblzip" for="zip">
                <c:choose>
                  <c:when test="${currentSiteId eq 'BedBathCanada'}">
                    <bbbl:label key="lbl_spc_subscribe_canadazipcode" language="${pageContext.request.locale.language}" />
                  </c:when>
                  <c:otherwise>
                      <bbbl:label key="lbl_spc_shipping_new_zip" language="${pageContext.request.locale.language}" />
                  </c:otherwise>
                </c:choose>
                  <span class="required">*</span>
              </label>
          
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
                  <input type="text" disabled="true" maxlength="10" name="${zipCodeClass}" id="zip" value="${beddingShipAddrVO.zip}" aria-required="true" aria-labelledby="lblzip errorzip" />
                </c:when>
                 <c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
                   <dsp:getvalueof var= "postalCode" bean = "PayPalSessionBean.address.postalCode"/>
                        <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip" value="${postalCode}">
                          <dsp:tagAttribute name="aria-required" value="true"/>
                          <dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
                        </dsp:input>
                   </c:when>
                  <c:when test="${empty preFillValues || preFillValues}">
                    <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip" value="${currentAddress.postalCode}">
                      <dsp:tagAttribute name="aria-required" value="true"/>
                      <dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
                    </dsp:input>
					<%-- BBBH- 2385 for Registry flow we disable SDD and set the zipOnLoad as request param --%>
                    <c:choose>
						<c:when test = "${RegistrantAddressOnShippingGroup eq 'true' || (registryCount > 0 && newAddrChecked eq 'false')}">
							<input type="hidden" name="zipOnLoad" id="zipOnLoad" value="registryZip"/>
							<c:set var="zipOnLoad" scope="request" value="registryZip"/>
						</c:when>
						<c:otherwise>
							<c:choose>
							<c:when test = "${not empty currentAddress.postalCode}">
								<c:set var="zipOnLoad" scope="request" value="${currentAddress.postalCode}"/>
								<input type="hidden" name="zipOnLoad" id="zipOnLoad" value="${currentAddress.postalCode}"/>
							</c:when>
							<c:when test = "${not empty postalCode}">
									<c:set var="zipOnLoad" scope="request" value="${postalCode}"/>
									<input type="hidden" name="zipOnLoad" id="zipOnLoad" value="${postalCode}"/>
							</c:when>
							<c:otherwise>
								<input type="hidden" name="zipOnLoad" id="zipOnLoad" value=""/>
							</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
                  </c:when>
                  <c:otherwise>
                    <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip" value="currentAddress.postalCode">
                      <dsp:tagAttribute name="aria-required" value="true"/>
                      <dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
                  </dsp:input>
                  </c:otherwise>
                </c:choose>
                <!-- BBBH-2385 - Defaulted to Standard Shipping -->
                <span class="hidden sddZipChangeMsg">
                	<bbbl:textArea key="txt_shipmthd_defaulted_standard" language ="${pageContext.request.locale.language}"/>
                </span>
            </div>

            <div class="clear"></div>

            <div class="fieldsInlineWrapper clearfix">

                <div class="input_wrap">
                    
                  <label class="popUpLbl" id="lblcityName" for="cityName">
                      <bbbl:label key="lbl_spc_shipping_new_city" language="${pageContext.request.locale.language}" />
                      <span class="required">*</span>
                  </label>
              
                  <c:choose>
                      <c:when test="${beddingKit eq true || isPackHold eq true}">
                        <input type="text" maxlength="25" disabled="true" name="cityName" id="cityName" value="${beddingShipAddrVO.city}" aria-required="true" aria-labelledby="lblcityName errorcityName" />
                      </c:when>
                       <c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
                 	 <dsp:getvalueof var= "city" bean = "PayPalSessionBean.address.city"/>
                        <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName" value="${city}">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
                          </dsp:input>
                   </c:when>
                      <c:when test="${empty preFillValues || preFillValues}">
                        <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName" value="${currentAddress.city}">
                          <dsp:tagAttribute name="aria-required" value="true"/>
                          <dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
                        </dsp:input>
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName" value="${currentAddress.city}">
                          <dsp:tagAttribute name="aria-required" value="true"/>
                          <dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
                        </dsp:input>
                      </c:otherwise>
                    </c:choose>
              
                </div>
    
                <div class="clear"></div>


                <div class="input_wrap">
                    
                    <div class="select noMar cb">

                        <label class="popUpLbl _focus _unfocus" id="lblstateName" for="stateName">

                          <c:choose>
                           <c:when test="${currentSiteId eq 'BedBathCanada'}">
                              <bbbl:label key="lbl_spc_addnewbillingaddressprovince" language="${pageContext.request.locale.language}" />
                            </c:when>
                            <c:otherwise>
                              <bbbl:label key="lbl_spc_shipping_new_state" language="${pageContext.request.locale.language}" />
                            </c:otherwise>
                          </c:choose><span class="required">*</span>
                        </label>

                        <c:choose>
                        <c:when test="${beddingKit eq true || isPackHold eq true}">
							    <dsp:select bean="BBBSPShippingGroupFormhandler.address.state" disabled="true" name="stateName" id="stateName" iclass="shippingStateName">
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
							    <dsp:select bean="BBBSPShippingGroupFormhandler.address.state" name="stateName" id="stateName" iclass="shippingStateName">
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
							  <dsp:select bean="BBBSPShippingGroupFormhandler.address.state" name="stateName" id="stateName" iclass="shippingStateName">
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
    														<c:when test="${fn:toLowerCase(currentAddress.state) eq fn:toLowerCase(elementCode)}">
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
                  <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
						    </dsp:select>

              </c:when>
              <c:otherwise>
								<dsp:select bean="BBBSPShippingGroupFormhandler.address.state" name="stateName" id="stateName" nodefault="true" iclass="shippingStateName">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
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
														<c:when test="${fn:toLowerCase(currentAddress.state) eq fn:toLowerCase(elementVal)}" >
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

            
			<dsp:input type="hidden" bean="BBBSPShippingGroupFormhandler.poBoxFlag" id="poBoxFlag" name="poBoxFlag" value=""/>
			<dsp:input type="hidden"  bean="BBBSPShippingGroupFormhandler.poBoxStatus" id="poBoxStatus" name="poBoxStatus" value=""/>
            
        </div>


        <%-- need logic on when to show contact fields - registry items / college items --%>
        <div id="contactFields" class="subForm ${hideContactForm}">
           

          <h4 class="<c:if test="${registryCount == 0 }">hidden</c:if>">Your Contact Info</h4>
          
          <c:choose>
              <c:when test = "${registryCount > 0 }">                
                <c:if test="${not empty isFromPreview}">                                  
                  <dsp:getvalueof var="phoneNumber" bean="/atg/commerce/ShoppingCart.current.shippingAddress.phoneNumber"/>
                </c:if>
                <c:if test="${empty phoneNumber && !isAnonymousProfile}">                  
                  <dsp:getvalueof var="phoneNumber" bean="/atg/userprofiling/Profile.mobileNumber"/>                  
                </c:if>
              </c:when>              
              <c:otherwise>
                <c:if test="${!empty currentAddress}">
                  <dsp:getvalueof var="phoneNumber" value="${currentAddress.phoneNumber}"/>
                </c:if>
                <c:if test="${empty phoneNumber && !RegistrantAddressOnShippingGroup}">
                  <dsp:getvalueof var="phoneNumber" bean="/atg/commerce/ShoppingCart.current.shippingAddress.phoneNumber"/>
                </c:if>
                <c:if test="${empty phoneNumber}">
                   <dsp:getvalueof var="phoneNumber" bean="/atg/userprofiling/Profile.mobileNumber"/>
                </c:if>
              </c:otherwise>
            </c:choose>
          
           

          
          <div class="input_wrap  clearfix phoneFieldWrap requiredPhone">
              
                <label class="popUpLbl" id="lblbasePhoneFull" for="basePhoneFull">
                  <bbbl:label key="lbl_spc_checkout_phone" language="${pageContext.request.locale.language}" />
                  <span class="required">&nbsp;*</span>
                </label>          
                <%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
                
                    <input id="basePhoneFull" role="textbox" type="text" value="${phoneNumber}" name="basePhoneFull" class="phoneField required" maxlength="10" aria-required="false" aria-labelledby="lblbasePhoneFull errorbasePhoneFull"/>
                
                <dsp:input type="hidden" name="phoneNumber" iclass="fullPhoneNum ignore" bean="BBBSPShippingGroupFormhandler.address.phoneNumber" value="${phoneNumber}" />
                
              
          </div>
          
          <div class="clear"></div>  


            <c:choose>
              <c:when test = "${registryCount > 0 }">        
                <c:if test="${not empty isFromPreview}">
                  <dsp:getvalueof var="userEmail" bean="/atg/commerce/ShoppingCart.current.shippingAddress.email"/>
                </c:if>
                <c:if test="${empty userEmail}">
                  <dsp:getvalueof var="userEmail" bean="/atg/userprofiling/Profile.email"/>
                </c:if>
              </c:when>              
              <c:otherwise>
                <c:if test="${!empty currentAddress}">
                  <dsp:getvalueof var="userEmail" value="${currentAddress.email}"/>
                </c:if>
                <c:if test="${empty userEmail  && !RegistrantAddressOnShippingGroup}">
                  <dsp:getvalueof var="userEmail" bean="/atg/commerce/ShoppingCart.current.shippingAddress.email"/>
                </c:if>
                <c:if test="${empty userEmail}">
                   <dsp:getvalueof var="userEmail" bean="/atg/userprofiling/Profile.email"/>
                </c:if>
              </c:otherwise>
            </c:choose>
            
           <div class="fieldsInlineWrapper clearfix">
        
              <div class="input_wrap">
                
                  <label class="popUpLbl" id="lblemail" for="shippingEmail">
                    <bbbl:label key="lbl_spc_cart_email_email" language="${pageContext.request.locale.language}" /> 
                    <span class="required">*</span>
                  </label>
                
                
                    <%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
                  
                      <dsp:input type="text" name="shippingEmail"
                      bean="BBBSPShippingGroupFormhandler.address.email"
                      value="${userEmail}" id="shippingEmail" required="true">
                                      <dsp:tagAttribute name="aria-required" value="true"/>
                                      <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                    </dsp:input>
                  
              </div>
        
            <%--  <div class="input_wrap">
                
                  <label  class="popUpLbl" id="lblemailConfirm" for="shipEmailConfirm">
                    <bbbl:label key="lbl_spc_checkout_email_confirm" language="${pageContext.request.locale.language}" />
                    <span class="required">*</span> 
                  </label>
                
                  <dsp:getvalueof var="conEmail" bean="BBBSPShippingGroupFormhandler.confirmedEmail"/>
                  <c:if test="${empty conEmail}">                        
                      <c:set var="conEmail" value="${userEmail}" />
                  </c:if>
                
                  <c:choose>
                    <c:when test = "${payPalOrder eq true}">
                      <dsp:input type="text" name="shipEmailConfirm"
                      bean="BBBSPShippingGroupFormhandler.confirmedEmail"
                      value="${conEmail}" id="shipEmailConfirm" required="true" disabled="true">
                                      <dsp:tagAttribute name="aria-required" value="true"/>
                                      <dsp:tagAttribute name="aria-labelledby" value="lblemailConfirm erroremailConfirm"/>
                                      <dsp:tagAttribute name="data-paypalConfEmail" value="${conEmail}"/>
                                    </dsp:input>
                    </c:when>
                    <c:otherwise>
                      <dsp:input type="text" name="shipEmailConfirm"
                      bean="BBBSPShippingGroupFormhandler.confirmedEmail"
                      value="${conEmail}" id="shipEmailConfirm" required="true">
                                      <dsp:tagAttribute name="aria-required" value="true"/>
                                      <dsp:tagAttribute name="aria-labelledby" value="lblemailConfirm erroremailConfirm"/>
                                    </dsp:input>
                    </c:otherwise>
                  </c:choose>
              </div> --%>
          </div>            
        </div>
     

    </div>
    
    <div id="editShippingButtons" class="clearfix hidden">
      <input type="button" id="applyShippingEdit"  aria-label="Apply and Save your shipping address" value="<bbbl:label key='lbl_add_address' language ='${pageContext.request.locale.language}'/>" class="btnPrimary button-Med " />
      <input type="button" id="cancelShippingEdit" aria-label="Cancel editing your shipping address information" value="Cancel" class="btnSecondary button-Med " />
    </div>
	  

      
      <c:if test="${!isAnonymousProfile  && registryCount == 0 && profileAddressCount > 1}">
        <div class="clearfix">
            <a id="viewAllAddress" href="#"><bbbl:label key="lbl_spc_view_all_addresses" language="${pageContext.request.locale.language}" /></a>
        </div>
      </c:if>


      

    		<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
    		
        <%--
        <h3 class="sectionHeading">
    			<bbbl:label key="lbl_spc_contact_info_heading" language="${pageContext.request.locale.language}" />
    		</h3>
        --%>


	   <c:if test="${isAnonymousProfile == 'true'}">
			 <c:set var="isChecked">
			   <dsp:valueof bean="BBBSPShippingGroupFormhandler.emailChecked" />
			 </c:set>
			 <div class="input_wrap checkboxSingle checkboxItem ">
					<div class="checkbox">
						<c:choose>
							<c:when test="${currentSiteId eq 'BedBathCanada'}">
									<c:choose>
									<c:when test="${isChecked eq 'checked'}">
									<dsp:input type="checkbox" iclass="noUniform" checked="true" name="emailSignUp" id="emailSignUp" bean="BBBSPShippingGroupFormhandler.emailSignUp" />
									</c:when>
									<c:otherwise>
									<dsp:input type="checkbox" iclass="noUniform" checked="false" name="emailSignUp" id="emailSignUp" bean="BBBSPShippingGroupFormhandler.emailSignUp" />
									</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:choose>
									<c:when test="${isChecked eq 'unChecked'}">
									<dsp:input type="checkbox" iclass="noUniform" checked="false" name="emailSignUp" id="emailSignUp" bean="BBBSPShippingGroupFormhandler.emailSignUp" />
									</c:when>
									<c:otherwise>
									<dsp:input type="checkbox" iclass="noUniform" checked="true" name="emailSignUp" id="emailSignUp" bean="BBBSPShippingGroupFormhandler.emailSignUp" />
									</c:otherwise>
									</c:choose>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="label">
            <span class="icon-tag icon-font" aria-hidden="true"></span>
						<label for="emailSignUp"><bbbl:label key="lbl_spc_subscribe_email_billing" language="<c:out param='${language}'/>" /></label>
					</div>
				</div>
			</c:if>

      <dsp:droplet name="Switch">
        <dsp:param name="value" bean="Profile.transient"/>
        <dsp:oparam name="false">
            <div class="clear"></div>
            <div class="checkboxItem input clearfix " id="saveToAccountWrap">
              <dsp:getvalueof var="profileAddresses" param="profileAddresses" />
                <div class="checkbox">
                   <c:choose>
                    <c:when test="${beddingKit eq true || isPackHold eq true}">
                        <dsp:input bean="BBBSPShippingGroupFormhandler.saveShippingAddress" value="false"  iclass="noUniform" type="checkbox" disabled="true" name="saveToAccount" id="saveToAccount" checked="false" >
                                  <dsp:tagAttribute name="aria-checked" value="true"/>
                                  <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                        </dsp:input>
                         
                      </c:when>
                      <c:otherwise>
                        <c:choose>
                          <c:when test="${empty profileAddresses}">
                            <dsp:input bean="BBBSPShippingGroupFormhandler.saveShippingAddress" value="true"  iclass="noUniform" type="checkbox" name="saveToAccount" id="saveToAccount" checked="true" >
                                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                              </dsp:input>    
                            
                          </c:when>
                          <c:otherwise>
                             <dsp:input bean="BBBSPShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" iclass="hidden noUniform" name="saveToAccount" id="saveToAccount" checked="false">
                                  <dsp:tagAttribute name="aria-checked" value="true"/>
                                  <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                              </dsp:input>
                          </c:otherwise>
                         </c:choose>
                      </c:otherwise>
                    </c:choose>
                </div>
                <div class="label <c:if test="${!isAnonymousProfile && !empty profileAddresses}">hidden</c:if>">
                    <span class="icon-user icon-font" aria-hidden="true"></span>
                    <label id="lblsaveToAccount" for="saveToAccount"><bbbl:label key="lbl_spc_shipping_new_save_account" language="${pageContext.request.locale.language}" /></label>
                </div>
            </div>
        </dsp:oparam>
        <dsp:oparam name="true">
            <div class="clear"></div>
            <div class="checkboxItem input clearfix " style="display:none">
                <div class="checkbox">
                    <dsp:input bean="BBBSPShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" name="saveToAccount" iclass="enableDisable noUniform" id="saveToAccount" checked="true">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                    </dsp:input>
                </div>
                <div class="label">
                    <span class="icon-user icon-font" aria-hidden="true"></span>
                    <label id="lblsaveToAccount" for="saveToAccount"><bbbl:label key="lbl_spc_shipping_new_save_account" language="${pageContext.request.locale.language}" /></label>
                </div>
            </div>
        </dsp:oparam>
    </dsp:droplet>


    <%-- if logged in, and billing != shipping, don't check 'use as billing' box 
        changing to loggin - selected, not logged in not selected
    --%>
    <c:set var="hideBilling" value=""/>
    <c:if test = "${registryCount > 0 || payPalOrder}">
      <c:set var="hideBilling" value="hidden"/>
    </c:if>
      <c:choose>
        <%-- <c:when test="${not empty currentAddress && not empty defaultShippingId && not empty defaultBillingID && defaultShippingId ne defaultBillingID}"> --%>
        <c:when test="${isAnonymousProfile}"> 

          <c:set var="billingChecked" value="true"/>


          <%--
          if there is already a billing address on the group, dont select this checkbox
          FIRST, try to get the billing address from group --%>
               
          <dsp:getvalueof var="orderBillingAddress" bean="/atg/commerce/ShoppingCart.current.billingAddress" />
          
          <c:if test="${not empty orderBillingAddress}">
            <dsp:getvalueof var="billingAdd1" bean="ShoppingCart.current.billingAddress.address1" />
            <c:set var="billingSubmitted" value="${not empty billingAdd1}" ></c:set>          
            <dsp:getvalueof var="billState" param="/atg/commerce/ShoppingCart.current.billingAddress.state" /> 

            <c:if test="${billingSubmitted && billState != 'INITIAL'}" >             
              <c:set var="billingChecked" value="false"/>
            </c:if>
          </c:if>

            
            
        </c:when>
        <c:otherwise>
            <c:set var="billingChecked" value="false"/>
        </c:otherwise>
      </c:choose>
      

        <div class="checkboxItem input clearfix ${hideBilling}" id="useAsBillingWrapper">
            <div class="checkbox">
               <%--<input type="checkbox" name="useAsBilling" id="useAsBilling" checked="true" /> --%> 
                <dsp:input type="checkbox" checked="${billingChecked}" name="useAsBilling" id="useAsBilling" iclass="" bean="BBBSPShippingGroupFormhandler.useAsBilling" />
  									
            </div>
            <div class="label">
                <span class="icon-location icon-font" aria-hidden="true"></span>
                <label id="lblUseAsBilling" for="useAsBilling"><bbbl:label key="lbl_spc_use_as_billing_address" language="${pageContext.request.locale.language}" /></label>
            </div>
        </div>

    

     <dsp:getvalueof var="sessionBeanNull" bean = "PayPalSessionBean.sessionBeanNull"/>
</dsp:page>