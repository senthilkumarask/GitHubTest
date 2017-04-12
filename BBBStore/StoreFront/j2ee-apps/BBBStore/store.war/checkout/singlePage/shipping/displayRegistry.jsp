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
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	
	
   <dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
   <dsp:getvalueof param="commerceItemCount" var="totalCartCount"/>      
  	<dsp:getvalueof param="registryItemCount" var="registryCount"/>      
  	<dsp:getvalueof param="isDefaultShippAddr" var="isDefaultShippAddr"/>

   <c:set var="containsaddressForGuest" scope="request" value="false"/>
	<c:set var="containsaddressForUser" scope="request" value="false"/>
	<c:set var="isChecked" scope="request">false</c:set>

<%--	
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
--%>
	<c:if test="${fn:contains(selectedAddress, 'null') || empty selectedAddress}">
		<c:set var="isDefaultShippAddr" value="${true}" />	   
	 </c:if>

<%--
	debugging on displayRegistry.jsp
   selectedAddress: ${selectedAddress} <br/>
   totalCartCount: ${totalCartCount}      <br/>
  	registryCount: ${registryCount}      <br/>
  	isDefaultShippAddr: ${isDefaultShippAddr}<br/>
  	firstVisit : ${firstVisit}<br/>


  	<c:if test="${registryCount > 0}">
		<div class="radioItem input clearfix">
	      <div class="radio">

				<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr" value="reg1234"
		                   checked="true"
		                   bean="BBBSPShippingGroupFormhandler.shipToAddressName">
			   	<dsp:tagAttribute name="aria-checked" value="true"/>
			   	<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
		      </dsp:input>
			  
		   </div>
		   <div class="label">
	           <label id="lbladdressToShipr${index}" for="addressToShipr${index}">
	               <span><bbbl:label key="lbl_spc_shipping_reg_address_by" language="${pageContext.request.locale.language}" />                            
	                 Registrant asdf & qwerty
	               </span>                        
	           </label>
	      </div>
	      <div class="clear"></div>
	   </div>
	</c:if>
	--%>
	




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
										<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
                                           checked="true"
                                           bean="BBBSPShippingGroupFormhandler.shipToAddressName">
									   	<dsp:tagAttribute name="aria-checked" value="true"/>
									   	<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
				                   </dsp:input>
								   <c:set var="isChecked" scope="request">true</c:set>
									</c:when>
									<c:otherwise>
										<c:set var="isChecked" scope="request">false</c:set>
										<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
                                           checked="false"
                                           bean="BBBSPShippingGroupFormhandler.shipToAddressName">
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
												<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
													   checked="true"
													   bean="BBBSPShippingGroupFormhandler.shipToAddressName">
												   <dsp:tagAttribute name="aria-checked" value="true"/>
												   <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
					                     </dsp:input>
										 <c:set var="isChecked" scope="request">true</c:set>
											</c:when>
											<c:otherwise>
												<c:set var="isChecked" scope="request">false</c:set>
												<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
													   checked="false"
													   bean="BBBSPShippingGroupFormhandler.shipToAddressName">
												   <dsp:tagAttribute name="aria-checked" value="false"/>
												   <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
							               </dsp:input>
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
									<dsp:oparam name="true">
										<c:choose>
											<c:when test="${isDefaultShippAddr && not empty firstVisit}">
												<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
											   	checked="true"
											   	bean="BBBSPShippingGroupFormhandler.shipToAddressName">
										   		<dsp:tagAttribute name="aria-checked" value="true"/>
										   		<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShipr${index} erroraddressToShip"/>
												</dsp:input>
												<c:set var="isChecked" scope="request">true</c:set>
											</c:when>
											<c:otherwise>
												<c:set var="isChecked" scope="request">false</c:set>
												<dsp:input type="radio" name="addressToShip" iclass="registryRadio" id="addressToShipr${index}" value="${currentAddress}"
												   checked="false"
												   bean="BBBSPShippingGroupFormhandler.shipToAddressName">
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
               <dsp:getvalueof bean="ShoppingCart.current.registryMap.${currentAddressId}" var="registratantVO"/>				 
					<div class="label">
                    <label id="lbladdressToShipr${index}" for="addressToShipr${index}">
                        <span><bbbl:label key="lbl_spc_shipping_reg_address_by" language="${pageContext.request.locale.language}" />                            
                            
                                <strong>${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName}
                                <c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}
                                </c:if><bbbl:label key="lbl_spc_cart_registry_name_suffix" language="<c:out param='${language}'/>"/>
                                </strong>
                            
                            <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
                            <c:set target="${placeHolderMap}" property="id" value="${currentAddressId}"/>
                            <bbbl:label key="lbl_spc_shipping_registry" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderMap}"/>
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
												   bean="BBBSPShippingGroupFormhandler.sendShippingConfEmail">
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblsendShippingConfEmail"/>
                                    </dsp:input>
								</c:when>
								<c:otherwise>
								   <dsp:input type="checkbox" name="sendShippingConfEmail" id="sendShippingConfEmail" value="true"
												   bean="BBBSPShippingGroupFormhandler.sendShippingConfEmail" checked="${email == useremail}">
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblsendShippingConfEmail"/>
                                    </dsp:input>
								</c:otherwise>
							</c:choose>
							
                            <dsp:input type="hidden" value="${registrantEmail}"
                                           bean="BBBSPShippingGroupFormhandler.sendShippingEmail"/>
										   
                                           
                        </div>
                        <div class="label">
                            <label id="lblsendShippingConfEmail" for="sendShippingConfEmail"><bbbl:label key="lbl_spc_shipping_pack_hold_confirm" language="${pageContext.request.locale.language}" /></label>
                        </div>
                    </div>
                </div> --%>
                <%-- DO NOT DELETE .... closed --%>
            </div>                                                                             
        </dsp:oparam>
    </dsp:droplet>
	<c:set var="zipOnLoadReg" scope="request"></c:set>
<c:if test="${isChecked}">
    <c:set var="zipOnLoadReg" scope="request">registryZip</c:set>
    </c:if>
</dsp:page>
