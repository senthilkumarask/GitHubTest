<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  newAddressCollege.jsp
 *
 *  DESCRIPTION: address fields for new address entry
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof id="isAnonymousProfile" bean="Profile.transient" />
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:getvalueof id="siteId" bean="/atg/multisite/Site.id" />
    <div class="radioItem input clearfix last">
    	<div class="radio">
 
<dsp:input type="radio" name="addressToShip" iclass="newAddOpt" id="addressToShip4nColg" value="userAddress"
	                                           checked="false" bean="BBBSPShippingGroupFormhandler.shipToAddressName"/>	           
		 </div>
        
        <div class="label">
            <label for="addressToShip4n">
                <span><bbbl:label key="lbl_spc_shipping_new_address" language="${pageContext.request.locale.language}" /></span>
            </label>
        </div>
        <div class="clear"></div>
        <div class="subForm clearfix hidden">
            <div class="fieldsInlineWrapper clearfix">
                            

				<dsp:input bean="BBBSPShippingGroupFormhandler.address.country" value="${defaultCountry}" type="hidden"/>

                <div class="input">
                    <div class="label">
                        <label id="lblcheckoutfirstNameColg" for="checkoutfirstNameColg">
                            <bbbl:label key="lbl_spc_shipping_new_first_name" language="${pageContext.request.locale.language}" />
                            <span class="required">*</span>
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstNameColg" id="checkoutfirstNameColg">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstNameColg errorcheckoutfirstNameColg"/>
                                </dsp:input>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstNameColg" id="checkoutfirstNameColg" value="">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstNameColg errorcheckoutfirstNameColg"/>
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
            </div>
			<div class="clear"></div>
    
            
            <div class="input clearfix">
                <div class="label">
                    <label id="lblcheckoutlastNameColg" for="checkoutlastNameColg">
                        <bbbl:label key="lbl_spc_shipping_new_last_name" language="${pageContext.request.locale.language}" />
                        <span class="required">*</span>
                    </label>
                </div>
                <div class="text">
                    <div>
                         <c:choose>
                            <c:when test="${empty preFillValues || preFillValues}">
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastNameColg" id="checkoutlastNameColg">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastNameColg errorcheckoutlastNameColg"/>
                              </dsp:input>
                            </c:when>
                            <c:otherwise>
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastNameColg" id="checkoutlastNameColg" value="">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastNameColg errorcheckoutlastNameColg"/>
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
                    <label id="lblcompanyColg" for="companyColg">
                       <bbbl:label key="lbl_reg_ph_collegeName" language ='${pageContext.request.locale.language}'/>
                    </label>
                </div>
                <div class="text">
                    <div>
                        <c:choose>
                            <c:when test="${empty preFillValues || preFillValues}">
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.companyName" maxlength="40" name="college" id="college">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompanyColg errorcompanyColg"/>
                              </dsp:input>
                            </c:when>
                            <c:otherwise>
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.companyName" maxlength="40" name="college" id="college" value="">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompanyColg errorcompanyColg"/>
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
                        <label id="lbladdress1Colg" for="address1Colg">
                            <bbbl:label key="lbl_spc_shipping_new_line1" language="${pageContext.request.locale.language}" />
                            <span class="required">*</span>
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" iclass="poBoxAddNotAllowed" bean="BBBSPShippingGroupFormhandler.address.address1" maxlength="50" name="address1Colg" id="address1Colg">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress1Colg erroraddress1Colg"/>
                                  </dsp:input>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" iclass="poBoxAddNotAllowed" bean="BBBSPShippingGroupFormhandler.address.address1" maxlength="50" name="address1Colg" id="address1Colg" value="">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress1Colg erroraddress1Colg"/>
                                  </dsp:input>
                                </c:otherwise>
                              </c:choose>
                        </div>
                    </div>
					<div class="clear"></div>
                </div>
                
    
                
                <div class="input">
                    <div class="label">
                        <label id="lbladdress2Colg" for="address2Colg">
                            <bbbl:label key="lbl_spc_shipping_new_line2" language="${pageContext.request.locale.language}" />
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" iclass="poBoxAddNotAllowed" bean="BBBSPShippingGroupFormhandler.address.address2" maxlength="50" name="address2Colg" id="address2Colg">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress2Colg erroraddress2Colg"/>
                                  </dsp:input>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" iclass="poBoxAddNotAllowed" bean="BBBSPShippingGroupFormhandler.address.address2" maxlength="50" name="address2Colg" id="address2Colg" value="">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress2Colg erroraddress2Colg"/>
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
                        <label id="lblcityNameColg" for="cityNameColg">
                            <bbbl:label key="lbl_spc_shipping_new_city" language="${pageContext.request.locale.language}" />
                            <span class="required">*</span>
                        </label>
                    </div>
                    <div class="text">
                        <div>
                            <c:choose>
                                <c:when test="${empty preFillValues || preFillValues}">
                                  <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.city" maxlength="25" name="cityNameColg" id="cityNameColg">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblcityNameColg errorcityNameColg"/>
                                  </dsp:input>
                                </c:when>
                                <c:otherwise>
                                  <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.city" maxlength="25" name="cityNameColg" id="cityNameColg" value="">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblcityNameColg errorcityNameColg"/>
                                  </dsp:input>
                                </c:otherwise>
                              </c:choose>                            
                        </div>
                    </div>
					<div class="clear"></div>
                </div>
                
    
                
                <div class="input">
                    <div class="label">
                        <label id="lblstateNameColg" for="stateNameColg">

						  <c:choose>
							 <c:when test="${currentSiteId eq 'BedBathCanada'}">
								  <bbbl:label key="lbl_spc_addnewbillingaddressprovince" language="${pageContext.request.locale.language}" />	
							  </c:when>
							  <c:otherwise>
								  <bbbl:label key="lbl_spc_shipping_new_state" language="${pageContext.request.locale.language}" /> 
							  </c:otherwise>
						  </c:choose><span class="required">*</span>
						</label>
                    </div>
                    <div class="select noMar cb">
                        <c:choose>
                            <c:when test="${empty preFillValues || preFillValues}">

								<dsp:select bean="BBBSPShippingGroupFormhandler.address.state" name="stateNameColg" id="stateNameColg">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblstateNameColg errorstateNameColg"/>
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
												<dsp:getvalueof param="element.stateName" id="elementVal">
												<dsp:getvalueof param="element.stateCode" id="elementCode">
													<dsp:option value="${elementCode}">
														${elementVal}
													</dsp:option>
												</dsp:getvalueof>
												</dsp:getvalueof>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:select>

                            </c:when>
                            <c:otherwise>
								<dsp:select bean="BBBSPShippingGroupFormhandler.address.state" name="stateNameColg" id="stateNameColg" nodefault="true">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblstateNameColg errorstateNameColg"/>
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
													<dsp:getvalueof param="element.stateName" id="elementVal">
													<dsp:getvalueof param="element.stateCode" id="elementCode">
														<dsp:option value="${elementCode}">
															${elementVal}
														</dsp:option>
													</dsp:getvalueof>
													</dsp:getvalueof>
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
            <c:choose>
                 <c:when test="${defaultCountry ne 'US'}">
                   <c:set var="zipCodeClass" value="zipCA" scope="page"/>
                 </c:when>
                 <c:otherwise>
                   <c:set var="zipCodeClass" value="zipUS" scope="page"/>
                 </c:otherwise>
             </c:choose>
            <div class="input clearfix">
                <div class="label">
                    <label id="lbl${zipCodeClass}colg" for="${zipCodeClass}colg">
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
                </div>
                <div class="text">
                    <div>
                        
                        <c:choose>
                            <c:when test="${empty preFillValues || preFillValues}">
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.postalCode"   maxlength="10" name="${zipCodeClass}colg" id="${zipCodeClass}colg">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbl${zipCodeClass}colg error${zipCodeClass}colg"/>
                              </dsp:input>
                            </c:when>
                            <c:otherwise>
                              <dsp:input type="text" bean="BBBSPShippingGroupFormhandler.address.postalCode"   maxlength="10" name="${zipCodeClass}colg" id="${zipCodeClass}colg" value="">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbl${zipCodeClass}colg error${zipCodeClass}colg"/>
                              </dsp:input>
                            </c:otherwise>
                          </c:choose>
                    </div>
                </div>
				<div class="clear"></div>
            </div>
            
            <dsp:droplet name="Switch">
                <dsp:param name="value" bean="Profile.transient"/>
                <dsp:oparam name="false">
                    <div class="clear"></div>
                    <div class="checkboxItem input clearfix noPadBot noMarBot">
                         
                        <dsp:droplet name="/com/bbb/commerce/shipping/droplet/CheckProfileAddress">
							<dsp:param name="siteId" bean="ShoppingCart.current.siteId" />
							<dsp:param name="profile" bean="/atg/userprofiling/Profile" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="profileAddresses" param="profileAddresses"/>		
							</dsp:oparam>
						</dsp:droplet>
						<div class="checkbox">
                            <c:choose>
								<c:when test="${empty profileAddresses}">
									<dsp:input bean="BBBSPShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" disabled="true" name="saveToAccount" id="saveToAccount" checked="true" />
									<dsp:input type="hidden" bean="BBBSPShippingGroupFormhandler.saveShippingAddress" value="true" />
								</c:when>
								<c:otherwise>
									<dsp:input bean="BBBSPShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" iclass="enableDisable" name="saveToAccount" id="saveToAccount" checked="true"/>									
								</c:otherwise>
							</c:choose>
							
                        </div>                       
                        <div class="label">
                            <label for="saveToAccount"><bbbl:label key="lbl_spc_shipping_new_save_account" language="${pageContext.request.locale.language}" /></label>
                        </div>
                    </div>
                </dsp:oparam>
                <dsp:oparam name="true">
                    <div class="clear"></div>
                    <div class="checkboxItem input clearfix noPadBot noMarBot" style="display:none">
                        <div class="checkbox">
                            <dsp:input bean="BBBSPShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" name="saveToAccount" iclass="enableDisable" id="saveToAccount" checked="true"/>
                        </div>
                        <div class="label">
                            <label for="saveToAccount"><bbbl:label key="lbl_spc_shipping_new_save_account" language="${pageContext.request.locale.language}" /></label>
                        </div>
                    </div>
                </dsp:oparam>
            </dsp:droplet>
            
        </div>
    </div>
 
</dsp:page>