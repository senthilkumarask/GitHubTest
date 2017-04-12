<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  addressDisplay.jsp
 *
 *  DESCRIPTION: existin addresses for users are rendered
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
    <dsp:getvalueof param="registriesAddresses" var="registriesAddresses"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:getvalueof id="isAnonymousProfile" bean="Profile.transient" />
    <dsp:importbean bean="/atg/userprofiling/Profile"/>   
    <dsp:getvalueof param="commerceItemCount" var="totalCartCount"/>      
    <dsp:getvalueof param="registryItemCount" var="registryCount"/>      
    <dsp:getvalueof var="identifier" bean="/atg/commerce/ShoppingCart.current.shippingAddress.identifier"/>
    
	
    <dsp:getvalueof var="shippingAddressID" bean="Profile.shippingAddress.repositoryId" />        

    <%--   
   debugging addressDisplay.jsp <br />    
    selectedAddress: ${selectedAddress} <br />
    shipping address: <dsp:valueof bean="Profile.shippingAddress.id"/><br/>    
    shippingAddressID: ${shippingAddressID} <br />
    <dsp:valueof bean="Profile.secondaryAddresses"/>
    <dsp:getvalueof param="Profile.shippingAddress" var="currentAddress"/>  <br/>    
    registryItemCount: ${registryItemCount}
   --%>

    <c:choose >
        <%-- if logged in , loop through profile address and get the default shipping address --%>
        <c:when test="${!isAnonymousProfile}" >
                        
            <%-- debugging
                shipping address: <dsp:valueof bean="Profile.shippingAddress.id"/><br/>    
                <dsp:valueof bean="Profile.secondaryAddresses"/><br/>    
                <dsp:getvalueof param="Profile.shippingAddress" var="currentAddress"/>  
            --%>

            <%-- getting the already entered order address so we can propopulate the shipping inputs --%>
            <%--IF the address was new and was not saved in profile --%>
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="groupAddresses" />
                <dsp:param name="elementName" value="address" />
                <dsp:oparam name="output">      
                    <dsp:getvalueof param="address.id" var="currentAddressID"/> 
                    <dsp:getvalueof param="address.identifier" var="currentAddressIdentifier"/> 
                    
                  
                    <c:choose>            
                        <c:when test="${selectedAddress == currentAddressID && !fn:contains(currentAddressIdentifier, 'registry')}">
                          <dsp:getvalueof param="address" var="currentAddress"/>                            
                        </c:when>     
                        <c:when test="${fn:contains(selectedAddress, 'PROFILE')}">
                          <dsp:getvalueof param="address" var="currentAddress"/>                            
                        </c:when>           
                    </c:choose>      
                      

                      <%--
                      <br />Debugging[addressDisplay.jsp]: <br />
                      Address: <dsp:valueof param="address" />  <br />
                      address.identifier: <dsp:valueof param="address.identifier" /><br />
                       currentAddress : ${currentAddress.id}<br>
                      selectedAddress: ${selectedAddress}
                      --%>
                  
                </dsp:oparam>
            </dsp:droplet>


            <c:if test="${empty currentAddress}">
                <dsp:getvalueof var="defaultShippingId" bean="Profile.shippingAddress.repositoryId" />
                <dsp:droplet name="ForEach">    
                    <dsp:param name="array" param="profileAddresses" />
                    <dsp:param name="elementName" value="address" />
                    <dsp:param name="sortProperties" value="id"/>
                    <dsp:oparam name="output">

                          <dsp:getvalueof param="address.identifier" var="currentAddressIdentifer"/>
                          <dsp:getvalueof param="address.id" var="currentAddressID"/>
                          <%-- debugging
                          defaultShippingId ${defaultShippingId}<br>
                          ${fn:indexOf( address.identifier, defaultShippingId)}<br>
                          <dsp:valueof param="address.id"/><br>
                          currentAddressID:${currentAddressID}<br><br>
                          --%>
                        
                          <c:choose>            
                              <c:when test="${defaultShippingId eq currentAddressID}">
                                <dsp:getvalueof param="address" var="currentAddress"/>
                              </c:when>                
                          </c:choose>                    
                                                                                                      
                    </dsp:oparam>
                </dsp:droplet>

            </c:if>
        </c:when>
        <c:otherwise>
            <%-- getting the already entered address so we can propopulate the shipping inputs --%>
            <%--IF the address was new and was not saved in profile --%>

            <%-- 
            <dsp:getvalueof var="shippingAddress" bean="/atg/commerce/ShoppingCart.current.shippingAddress" />
            <dsp:getvalueof bean="/atg/commerce/ShoppingCart.current.shippingAddress" var="currentAddress"/>
              
            <dsp:getvalueof param="shippingAddress.id" var="currentAddressID"/> 
            <dsp:getvalueof param="shippingAddress.identifier" var="currentAddressIdentifer"/>
            
                
                  Debugging[addressDisplay.jsp - anon]: <br />
                  identifier: ${identifier}<BR />
                  Address: <dsp:valueof param="address" />  <br />
                  address.identifier: ${currentAddressIdentifer}<br />
                  selectedAddress: ${selectedAddress}<br />
                ${selectedAddress} == ${currentAddressID}:${fn:contains(currentAddressIdentifier, 'registry')}<br />
                Group Addresses<dsp:valueof param="groupAddresses" /> 
            --%>

 
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="groupAddresses" />
                <dsp:param name="elementName" value="address" />
                <dsp:oparam name="output">      

                
                <dsp:getvalueof param="address.id" var="currentAddressID"/> 
                <dsp:getvalueof param="address.identifier" var="currentAddressIdentifer"/>
                
                    <%--                        
                    <br />Debugging[addressDisplay.jsp - anon foreach groupAddress]: <br />
                    Address: <dsp:valueof param="address" />  <br />
                    address.identifier: ${currentAddressIdentifer}<br />
                    selectedAddress: ${selectedAddress}
                    ${selectedAddress} == ${currentAddressID}:${fn:contains(currentAddressIdentifier, 'registry')}
                    --%>             
                    <c:choose>            
                        <c:when test="${selectedAddress == currentAddressID && !fn:contains(currentAddressIdentifer, 'registry')}">
                          <dsp:getvalueof param="address" var="currentAddress"/>                            
                          
                        </c:when>                
                    </c:choose> 
                  
                </dsp:oparam>
            </dsp:droplet>    
                  
        </c:otherwise>
     </c:choose>


    <%--     
        debugging    
      defaultShippingId ${defaultShippingId}<br>
      ${fn:indexOf( address.identifier, defaultShippingId)}<br>
      <dsp:valueof param="address.id"/><br>
      currentAddressID:${currentAddressID}<br>
      currentAddress : ${currentAddress.id}<br>

    --%>
    
    <c:if test="${not empty currentAddress}">

        <dsp:getvalueof var="userFirstName" value="${currentAddress.firstName}"/>
        <dsp:getvalueof var="userLastName" value="${currentAddress.lastName}"/>
        <dsp:getvalueof var="userCompanyName" value="${currentAddress.companyName}"/>
        <dsp:getvalueof var="userAddress1" value="${currentAddress.address1}"/>
        <dsp:getvalueof var="userAddress2" value="${currentAddress.address2}"/>
        <dsp:getvalueof var="userCity" value="${currentAddress.city}"/>
        <dsp:getvalueof var="userState" value="${currentAddress.state}"/>
        <dsp:getvalueof var="userPostalCode" value="${currentAddress.postalCode}"/>
        <%-- BBBH- 2385 - setting the zipOnLoad as request param --%>
		<c:set var="zipOnLoad" scope="request" value="${currentAddress.postalCode}"/>
		<input type="hidden" name="zipOnLoad" id="zipOnLoad" value="${zipOnLoad}"/>
		
		
        <dsp:getvalueof var="userEmail" value="${currentAddress.email}"/>
        <dsp:getvalueof var="phoneNumber" value="${currentAddress.phoneNumber}"/>
    
        <div class="radioItem input clearfix preselected">

            <h3><bbbl:label key="lbl_spc_shipping_address_header" language="${pageContext.request.locale.language}" /></h3>

            <div class="label">
                <label id="lbladdressToShip2s${index}" for="addressToShip2s${index}">
                    <span>${userFirstName} ${userLastName}</span>
                    <c:if test="${not empty userCompanyName}">
                        <span>${userCompanyName}</span>
                    </c:if>
                    <span>${userAddress1}</span>
                    <c:if test="${not empty userAddress2}">
                        <span>${userAddress2}</span>
                    </c:if>
                    <span>${userCity}, ${userState} ${userPostalCode}</span>
                    
                    <c:if test="${not empty userEmail}">
                        <span>${userEmail}</span>
                    </c:if>

                    <c:if test="${not empty phoneNumber}">
                        <span>${fn:substring(phoneNumber, 0, 3)}-${fn:substring(phoneNumber, 3, 6)}-${fn:substring(phoneNumber, 6, 10)}</span>
                    </c:if>
                </label>
                <div id="editAddressLinks">
                    <a id="editAddress" href="#"><bbbl:label key="lbl_spc_edit_address" language="${pageContext.request.locale.language}" /></a> |
                    <a id="addNewAddress" href="#"><bbbl:label key="lbl_spc_add_new_address" language="${pageContext.request.locale.language}" /></a>
                </div>
            </div>
        </div>
    </c:if>

<%--
    <c:if test="${not empty currentAddress}">
    
        <div class="radioItem input clearfix preselected">

            <h3><bbbl:label key="lbl_spc_shipping_address_header" language="${pageContext.request.locale.language}" /></h3>

            <div class="label">
                <label id="lbladdressToShip2s${index}" for="addressToShip2s${index}">
                    <span><dsp:valueof param="currentAddress.firstName" /> <dsp:valueof param="currentAddress.lastName" /></span>
                    <dsp:getvalueof var="tempCompanyName" param="currentAddress.companyName" />
                    <c:if test="${tempCompanyName != ''}">
                             <span><dsp:valueof param="currentAddress.companyName" /></span>
                    </c:if>
                    <span><dsp:valueof param="currentAddress.address1" /> <dsp:valueof param="currentAddress.address2" /></span>
                    <span><dsp:valueof param="currentAddress.city" />, <dsp:valueof param="currentAddress.state"/> <dsp:valueof param="currentAddress.postalCode" /></span>
                    
                    <c:if test="${not empty userEmail}">
                        <span>${userEmail}</span>
                    </c:if>

                    <c:if test="${not empty phoneNumber}">
                        <span>${phoneNumber}</span>
                    </c:if>
                </label>
            </div>


            <div id="editAddressLinks">
                <a id="editAddress" href="#"><bbbl:label key="lbl_spc_edit_address" language="${pageContext.request.locale.language}" /></a> |
                <a id="addNewAddress" href="#"><bbbl:label key="lbl_spc_add_new_address" language="${pageContext.request.locale.language}" /></a>
            </div>
          
        </div>
    </c:if>
--%>
    <%--IF the address was new and was not saved in profile --%>

    <%--
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" param="groupAddresses" />
        <dsp:param name="elementName" value="address" />
        <dsp:oparam name="output">
            <c:set var="containsaddressForUser" scope="request" value="true"/>
            <dsp:getvalueof param="address.identifier" var="currentAddress"/>
            <dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
            <c:set var="containsaddressForGuest" scope="request" value="true"/>
            <c:if test="${fn:contains(currentAddress, 'PROFILE') && !fn:contains(selectedAddress, 'PROFILE')}">
            	<c:set var="currentAddress"><c:out value="${fn:replace(currentAddress,'PROFILE', '')}"/></c:set>
            </c:if>
            
            
            currentAddress: ${currentAddress}, selectedAddress: ${selectedAddress}, shippingAddressID: ${shippingAddressID}
            
            <c:choose>            
                <c:when test="${currentAddress == selectedAddress && empty shippingAddressID}">
                    <div class="radioItem input clearfix preselected">

                        <h3><bbbl:label key="lbl_spc_shipping_address_header" language="${pageContext.request.locale.language}" /></h3>

                        <div class="">
                            <c:choose>            
								<c:when test="${isChecked eq 'true'}">
									<dsp:input type="hidden" name="addressToShip" id="addressToShip2s${index}" value="${currentAddress}"
												   bean="BBBSPShippingGroupFormhandler.shipToAddressName">                                
                                        <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2s${index} erroraddressToShip"/>
		                            </dsp:input>
								
								</c:when>
								<c:otherwise>
									<dsp:input type="hidden" name="addressToShip" id="addressToShip2s${index}" value="${currentAddress}"
												   bean="BBBSPShippingGroupFormhandler.shipToAddressName">                                
                                        <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2s${index} erroraddressToShip"/>
		                            </dsp:input>
								</c:otherwise>
							</c:choose>     
                        </div>
                        <div class="label">
                            <label id="lbladdressToShip2s${index}" for="addressToShip2s${index}">
                                <span><dsp:valueof param="address.firstName" /> <dsp:valueof param="address.lastName" /></span>
								<dsp:getvalueof var="tempCompanyName" param="address.companyName" />
								<c:if test="${tempCompanyName != ''}">
										 <span><dsp:valueof param="address.companyName" /></span>
								</c:if>
                                <span><dsp:valueof param="address.address1" /> <dsp:valueof param="address.address2" /></span>
                                <span><dsp:valueof param="address.city" />, <dsp:valueof param="address.state"/> <dsp:valueof param="address.postalCode" /></span>
                                
                                <c:if test="${not empty userEmail}">
                                    <span>${userEmail}</span>
                                </c:if>

                                <c:if test="${not empty phoneNumber}">
                                    <span>${phoneNumber}</span>
                                </c:if>
                            </label>
                        </div>
                    </div>
                </c:when>
            </c:choose>   
                                                                  
        </dsp:oparam>
    </dsp:droplet>
    --%>          
</dsp:page>
