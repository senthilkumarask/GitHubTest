<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  displayRegistry.jsp
 *
 *  DESCRIPTION: Shipping address for registry owner
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	
    <dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
     <c:set var="containsaddressForGuest" scope="request" value="false"/>
	<c:set var="containsaddressForUser" scope="request" value="false"/>
	<c:set var="isChecked" scope="request">false</c:set>
	<c:set var="IsRegistryAddress" value="false"/>	
	<dsp:droplet name="/com/bbb/commerce/cart/BBBItemCountDroplet">
		<dsp:param name="shoppingCart" bean="ShoppingCart.current" />
		<dsp:param name="selectedAddress" param="selectedAddress"/>
		<dsp:param name="registriesAddresses" param="registriesAddresses"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="commerceItemCount" var="totalCartCount"/>
			<dsp:getvalueof param="registryItemCount" var="registryCount"/>
			<dsp:getvalueof param="isDefaultShippAddr" var="isDefaultShippAddr"/>
		</dsp:oparam>
	</dsp:droplet>

    <dsp:droplet name="ForEach">
        <dsp:param name="array" param="registriesAddresses" />
        <dsp:param name="elementName" value="address" />
        <dsp:oparam name="output">
        	<c:set var="containsaddressForGuest" scope="request" value="true"/>
			<c:set var="containsaddressForUser" scope="request" value="true"/>
            <dsp:getvalueof param="address.identifier" var="currentAddress"/>
			<dsp:getvalueof param="address.id" var="currentAddressId"/>
			<dsp:getvalueof param="address.registryId" var="registryId"/>
            <dsp:getvalueof param="index" var="index"/>
            <div class="radioItem input clearfix">
                <div class="radio">
                    <c:choose>
                        <c:when test="${totalCartCount-registryCount == 0}">
							 <c:choose> 
								<c:when test="${isDefaultShippAddr && not empty firstVisit}">
									<c:set var="isChecked" scope="request">true</c:set>
									<c:set var="IsRegistryAddress" value="true"/>
									<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
                                           checked="true"
                                           bean="BBBShippingGroupFormhandler.shipToAddressName">
									   <dsp:tagAttribute name="aria-checked" value="true"/>
									   <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
				                     </dsp:input>
								</c:when>
								<c:otherwise>
									<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
                                           checked="false"
                                           bean="BBBShippingGroupFormhandler.shipToAddressName">
									   <dsp:tagAttribute name="aria-checked" value="false"/>
									   <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
                           			</dsp:input>
								</c:otherwise>
							</c:choose>
                        </c:when>
                        <c:otherwise>
							<dsp:droplet name="Switch">
								<dsp:param name="value" bean="Profile.transient"/>
								<dsp:oparam name="false">
									<c:choose>
										<c:when test="${isDefaultShippAddr && not empty firstVisit}">
											<c:set var="isChecked" scope="request">true</c:set>
											<c:set var="IsRegistryAddress" value="true"/>
											<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
												   checked="true"
												   bean="BBBShippingGroupFormhandler.shipToAddressName">
											   <dsp:tagAttribute name="aria-checked" value="true"/>
											   <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
				                            </dsp:input>
										</c:when>
										<c:otherwise>
											<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
												   checked="false"
												   bean="BBBShippingGroupFormhandler.shipToAddressName">
											   <dsp:tagAttribute name="aria-checked" value="false"/>
											   <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
						                    </dsp:input>
										</c:otherwise>
									</c:choose>
								</dsp:oparam>
								<dsp:oparam name="true">
										<c:choose>
										<c:when test="${isDefaultShippAddr && not empty firstVisit}">
										<c:set var="isChecked" scope="request">true</c:set>
										<c:set var="IsRegistryAddress" value="true"/>
										<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
											   checked="true"
											   bean="BBBShippingGroupFormhandler.shipToAddressName">
										   <dsp:tagAttribute name="aria-checked" value="true"/>
										   <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
										</dsp:input>
									</c:when>
									<c:otherwise>
										<c:set var="isChecked" scope="request">true</c:set>
										<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
											   checked="false"
											   bean="BBBShippingGroupFormhandler.shipToAddressName">
										   <dsp:tagAttribute name="aria-checked" value="true"/>
										   <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
										</dsp:input>
									</c:otherwise>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:set var="IsRegistryAddress" value="${IsRegistryAddress}" scope="request"/>
                <dsp:getvalueof bean="ShoppingCart.current.registryMap.${currentAddressId}" var="registratantVO"/>				 
				<div class="label addressDetails">
                    <label id="lbladdressToShipr${index}" for="addressToShipr${index}">
                        <span><bbbl:label key="lbl_shipping_reg_address_by" language="${pageContext.request.locale.language}" />                            
                            
                                <strong>${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName}
                                <c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}
                                </c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/>
                                </strong>
                            
                            <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
                            <c:set target="${placeHolderMap}" property="id" value="${currentAddressId}"/>
                            <bbbl:label key="lbl_shipping_registry" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderMap}"/>
                        </span>                        
                    </label>
                     <div class="sddShippingError error hidden">
						<bbbe:error key="err_sdd_zip_change" language="${pageContext.request.locale.language}"/>
					</div>
                </div>
                <div class="clear"></div>
               
                <%-- DO NOT DELETE ...... the shipping confirmation is commented since DS is not ready to send confirmation emails--%>
                
                <%-- <div class="subForm clearfix">
                    <div class="checkboxItem input shippingConf clearfix">
                        <div class="checkbox">
                            <dsp:getvalueof param="shippingGroup.shippingConfirmationEmail" var="email"/>
                            <dsp:getvalueof param="address.email" var="useremail"/>
							<dsp:getvalueof var="registrantEmail" value="${registratantVO.registrantEmail}"/>
							<c:choose>
								<c:when test="${email == '' || email == null || email != useremail}">
								   <dsp:input type="checkbox" name="sendShippingConfEmail" id="sendShippingConfEmail" value="true"
												   bean="BBBShippingGroupFormhandler.sendShippingConfEmail">
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblsendShippingConfEmail"/>
                                    </dsp:input>
								</c:when>
								<c:otherwise>
								   <dsp:input type="checkbox" name="sendShippingConfEmail" id="sendShippingConfEmail" value="true"
												   bean="BBBShippingGroupFormhandler.sendShippingConfEmail" checked="${email == useremail}">
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblsendShippingConfEmail"/>
                                    </dsp:input>
								</c:otherwise>
							</c:choose>
							
                            <dsp:input type="hidden" value="${registrantEmail}"
                                           bean="BBBShippingGroupFormhandler.sendShippingEmail"/>
										   
                                           
                        </div>
                        <div class="label">
                            <label id="lblsendShippingConfEmail" for="sendShippingConfEmail"><bbbl:label key="lbl_shipping_pack_hold_confirm" language="${pageContext.request.locale.language}" /></label>
                        </div>
                    </div>
                </div> --%>
                <%-- DO NOT DELETE .... closed --%>
            </div>                                                                             
        </dsp:oparam>
    </dsp:droplet>
	 
     <c:if test="${isChecked && not empty registryId}">
    <c:set var="zipOnLoad" scope="request">registryZip</c:set>
    </c:if>
</dsp:page>