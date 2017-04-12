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
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
    <dsp:getvalueof param="registriesAddresses" var="registriesAddresses"/>
	
    <dsp:droplet name="ForEach">    
        <dsp:param name="array" param="profileAddresses" />
        <dsp:param name="elementName" value="address" />
        <dsp:param name="sortProperties" value="id"/>
        <dsp:oparam name="output">
            <c:set var="containsaddressForUser" scope="request" value="true"/>
            <dsp:getvalueof param="address.identifier" var="currentAddress"/>
            <dsp:getvalueof param="index" var="index"/>
            <div class="radioItem input clearfix noBorder">
                <div class="radio">
                    <c:choose>            
                        <c:when test="${(fn:indexOf(selectedAddress, currentAddress) > -2) && isChecked eq 'true'}">
							<dsp:input type="radio" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress}"
                                           checked="false"
                                           bean="BBBShippingGroupFormhandler.shipToAddressName">
                               <dsp:tagAttribute name="aria-checked" value="false"/>
                               <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2p${index} erroraddressToShip"/>
                            </dsp:input>
                        </c:when>
                        <c:otherwise>
							<c:choose>            
								<c:when test="${empty firstVisit && not empty registriesAddresses}">
									<dsp:input type="radio" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress}"
                                           checked="false"
                                           bean="BBBShippingGroupFormhandler.shipToAddressName">
		                               <dsp:tagAttribute name="aria-checked" value="true"/>
                                       <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2p${index} erroraddressToShip"/>
		                            </dsp:input>
								</c:when>
								<c:when test="${selectedAddress eq currentAddress}">
									<dsp:input type="radio" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress}"
                                           checked="true"
                                           bean="BBBShippingGroupFormhandler.shipToAddressName">
		                               <dsp:tagAttribute name="aria-checked" value="true"/>
                                       <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2p${index} erroraddressToShip"/>
		                            </dsp:input>
                        			<c:set var="zipOnLoad" scope="request"><dsp:valueof param="address.postalCode"/></c:set>
								</c:when>
								<c:otherwise>
									<dsp:input type="radio" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress}"
                                           checked="false"
                                           bean="BBBShippingGroupFormhandler.shipToAddressName">
		                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2p${index} erroraddressToShip"/>
		                            </dsp:input>
								</c:otherwise>
							</c:choose>
                        </c:otherwise>
                    </c:choose>                    
                </div>
                <div class="label addressDetails">
                    <label id="lbladdressToShip2p${index}" for="addressToShip2p${index}">
                        <span><dsp:valueof param="address.firstName" /> <dsp:valueof param="address.lastName" /></span>
                        <dsp:getvalueof var="tempCompanyName" param="address.companyName" />
						<c:if test="${tempCompanyName != ''}">
								 <span><dsp:valueof param="address.companyName" /></span>
						</c:if>
                        <span>
	                        <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
								<dsp:param name="value" param="address.address2"/>
								<dsp:oparam name="true">
									<dsp:valueof param="address.address1" />
								</dsp:oparam>
								<dsp:oparam name="false">
									<dsp:valueof param="address.address1" />, <dsp:valueof param="address.address2" />
								</dsp:oparam>
							</dsp:droplet>
                        </span>
                        <span><dsp:valueof param="address.city" />, <dsp:valueof param="address.state"/><span class="sddShipZip">
                         <dsp:valueof param="address.postalCode"/> </span> </span>
                    </label>
                     <div class="sddShippingError error hidden">
						<bbbe:error key="err_sdd_zip_change" language="${pageContext.request.locale.language}"/>
					</div>
                </div>
            </div>                                                                              
        </dsp:oparam>
    </dsp:droplet>
    <dsp:droplet name="/atg/dynamo/droplet/ForEach"><%--IF the address was new and was not saved in profile --%>
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
            <c:choose>            
                <c:when test="${currentAddress == selectedAddress}">
                    <div class="radioItem input clearfix">
                        <div class="radio">
                            <c:choose>            
								<c:when test="${isChecked eq 'true'}">
									<dsp:input type="radio" name="addressToShip" id="addressToShip2s${index}" value="${currentAddress}"
												   checked="false"
												   bean="BBBShippingGroupFormhandler.shipToAddressName">
		                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2s${index} erroraddressToShip"/>
		                            </dsp:input>
								
								</c:when>
								<c:otherwise>
									<dsp:input type="radio" name="addressToShip" id="addressToShip2s${index}" value="${currentAddress}"
												   checked="true"
												   bean="BBBShippingGroupFormhandler.shipToAddressName">
		                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2s${index} erroraddressToShip"/>
		                            </dsp:input>
		                            <c:set var="zipOnLoad" scope="request"><dsp:valueof param="address.postalCode"/></c:set>
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
                                <span><dsp:valueof param="address.city" />, <dsp:valueof param="address.state"/> <span class="sddShipZip"> 
                                <dsp:valueof param="address.postalCode" /></span> </span>
                            </label>
                            <div class="sddShippingError error hidden">
								<bbbe:error key="err_sdd_zip_change" language="${pageContext.request.locale.language}"/>
							</div>
                        </div>
                    </div>
                </c:when>
            </c:choose>                                                                   
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>