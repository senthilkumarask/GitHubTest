<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  singleShipping.jsp
 *
 *  DESCRIPTION: page for collection single shipping address for an order
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<%@ page import="atg.servlet.ServletUtil" %>
<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableShippingMethodsDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/> 
    <dsp:importbean bean="/atg/userprofiling/Profile"/>   
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:getvalueof id="isAnonymousProfile" bean="Profile.transient" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="isFromPreview" param="isFromPreview" />
	<dsp:getvalueof var="payPalOrder" param="payPalOrder" />


    <%-- need to get value for mapqueston for ajax requests --%>
    <c:set var="BedBathUSSite" scope="request">
        <bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="BuyBuyBabySite" scope="request">
        <bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="BedBathCanadaSite" scope="request">
        <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:choose>
        <c:when test="${currentSiteId eq BedBathUSSite}">
            <c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_us"/></c:set> 
        </c:when>
        <c:when test="${currentSiteId eq BuyBuyBabySite}">
            <c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_baby"/></c:set>
        </c:when>
        <c:otherwise>
            <c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_ca"/></c:set>
        </c:otherwise>
    </c:choose>
    
    <dsp:getvalueof var="shippingSubmitted" param="shippingSubmitted" />
    <c:if test="${empty shippingSubmitted}">
        <c:set var="shippingSubmitted" value="${false}" />
    </c:if>
    
        <a name="startCheckout"></a>
        
        <%-- adding anchor here --%>
			<a name="spcShipping"></a>
			
        <div id="singleShippingSection" class="clearfix" role="main" data-anonymous="${isAnonymousProfile}" >
            <h1 class="SPCSectionHeading"><bbbl:label key="lbl_spc_section_heading_shipping" language="${pageContext.request.locale.language}" /> <span class="icon icon-checkmark hidden"></span></h1>
			
            <div id="checkoutOptions">
                <span class="single"><bbbl:label key="lbl_spc_one_shipping_location" language="${pageContext.request.locale.language}" /></span>
                <span class="or"><bbbl:label key="lbl_spc_or" language="${pageContext.request.locale.language}" /></span>
                
                <span class="multi not-active">
                	<span class="disable-me"></span>
                    <dsp:a page="/checkout/shipping/shipping.jsp">
                        <dsp:param name="shippingGr" value="multi"/>
                         <c:if test = "${payPalOrder eq true && isFromPreview}">
                            <dsp:param name="isFromPreview" value="${isFromPreview}"/>
                        </c:if> 
                    
                        <bbbl:label key="lbl_spc_shipping_multiple_page_link" language="${pageContext.request.locale.language}" />
                        
                        <c:if test="${MapQuestOn}"  >
                            <bbbl:label key="lbl_spc_or_store_pickup" language="${pageContext.request.locale.language}" />
                        </c:if>
                    </dsp:a>
                </span>
            
            </div>

            <dsp:getvalueof var="formExceptions" bean="BBBSPShippingGroupFormhandler.formExceptions"/>
            <c:choose>
                <c:when test="${empty formExceptions}">
                    <c:set var="isFormException" value="false"/>
                </c:when>
                <c:otherwise>
                    <c:set var="isFormException" value="true"/>
                </c:otherwise>
            </c:choose>
            
            <c:choose>
                        <c:when test="${param.fromCardinalError eq 'true'}">
                            <div class="alert alert-error redirectVerifyVisa"><bbbe:error key='err_cardinal_error' language='${language}'/> </div>
                        </c:when>
                        <c:otherwise>
                            <div id="shippingErrors">
                                <dsp:include page="/global/gadgets/errorMessage.jsp">
                                    <dsp:param name="formhandler" bean="BBBSPShippingGroupFormhandler"/>
                                </dsp:include>
                            </div>
                        </c:otherwise>
            </c:choose>

           <!--  <div id="shippingErrors">
    			<dsp:include page="/global/gadgets/errorMessage.jsp">
    				<dsp:param name="formhandler" bean="BBBSPShippingGroupFormhandler"/>
    			</dsp:include>
            </div> -->

			<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SP_SHIPPING_SINGLE"/>

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

                <%-- debugging 

                <dsp:getvalueof param="registryDebug" var="registryDebug"/>       
                <c:if test="${registryDebug == 'true'}">
                    <c:set var="registryCount" value="1" />
                </c:if>
                
                //commenting out anchor
            <a name="spcShipping"></a>--%>
            <div class="clearfix">

                <div>
                    <dsp:form id="formShippingSingleLocation"  formid="com_bbb_checkoutShippingAddress"
                        action="${pageContext.request.requestURI}" method="post" iclass='flatform' >
                        
                        <dsp:input type="hidden" id="removePorchServiceInput" bean="BBBSPShippingGroupFormhandler.removePorchService" value="false" />

                        <dsp:include page="/checkout/singlePage/shipping/shippingAddressForm.jsp">
                        	<dsp:param name="isFormException" value="${isFormException}"/>
                            <dsp:param name="commerceItemCount" value="${totalCartCount}"/>      
                            <dsp:param name="registryItemCount" value="${registryCount}"/>      
                            <dsp:param name="isDefaultShippAddr" value="${isDefaultShippAddr}"/>
                            <dsp:param name="shippingSubmitted" value="${shippingSubmitted}"/>
                        </dsp:include>
                        

                        <%-- gift msg include --%>
                        <fieldset class="">
                          <dsp:include page="frag/singleShippingOptionsFrag.jsp">
                            <dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
                            <dsp:param name="commerceItemCount" value="${totalCartCount}"/>      
                            <dsp:param name="registryItemCount" value="${registryCount}"/>      
                            <dsp:param name="isDefaultShippAddr" value="${isDefaultShippAddr}"/>
                          </dsp:include>
                        </fieldset>

						

						<%-- Moving the anchor up Defect 35415 --%>
						<a name="spcShippingMethods"></a>	
                        <fieldset id="shippingMethodFields">
	                        <div class="sddMethodError hidden">
								<span class='error'>
									<bbbl:textArea key="txt_shipmthd_defaulted_standard" language ="${pageContext.request.locale.language}"/>
								</span>
	                        </div>
                            <legend class="hidden"><bbbl:label key="lbl_spc_shipping_shipping_method" language="${pageContext.request.locale.language}" /></legend>
							<h3 ><bbbl:label key="lbl_spc_shipping_shipping_method" language="${pageContext.request.locale.language}" /></h3>


                            <%-- have moved the entire contents of shippingMethods.jsp here to reduce a droplet call
                            shippingMethods.jsp will stay in tact for the ajax request. --%>
							<dsp:getvalueof var="customerZip" param="customerZip" />
							
							<!-- BBBH-2385 GetApplicableShippingMethodsDroplet gets the Eligibility status for SDD and
							the shipping methods are displayed accordingly. -->
                            <dsp:droplet name="GetApplicableShippingMethodsDroplet">
                            
							<dsp:param name="currentZip" value="${zipOnLoad}" />
							<c:if test="${not empty zipOnLoad}">
								<dsp:param name="checkForInventory" value="true" />
							</c:if>
                                <dsp:param name="operation" value="perOrder" />
                                <dsp:param name="order" bean="ShoppingCart.current" />
                               <dsp:getvalueof var="sddEligiblityStatus" param="sddEligiblityStatus"/>
								<dsp:getvalueof var="sddOptionEnabled" param="sddOptionEnabled"/>
								<c:if test="${sddEligiblityStatus eq 'marketEligible'}">
								<c:set var="displayMessage"><bbbl:label key="lbl_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
								<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
								</c:if>
                                
                                <dsp:oparam name="output">
                                 <div id="shippingMethods">
								 
								
                                    <dsp:getvalueof param="preSelectedShipMethod" var="preSelectedShipMethod" />  
									<c:choose>
									<c:when test="${empty zipOnLoad}">
										<dsp:param name="currentZip" value="${customerZip}" />
									</c:when>
									<c:otherwise>
										<dsp:param name="currentZip" value="${zipOnLoad}" />
										</c:otherwise>
									</c:choose>
									 
                                    <c:choose>
                                        <c:when test="${formExceptionFlag eq 'true'}">
                                            <dsp:setvalue bean="BBBSPShippingGroupFormhandler.shippingOption" value="${shippingMethodSelected}" />
                                        </c:when>
                                        <c:otherwise>
                                            <dsp:setvalue bean="BBBSPShippingGroupFormhandler.shippingOption" paramvalue="preSelectedShipMethod" />
                                        </c:otherwise>
                                    </c:choose>

                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" param="shipMethodVOList" />
                                        <dsp:param name="sortProperties" value="sortShippingCharge,shipMethodId"/>
                                        <dsp:oparam name="outputStart">
											<dsp:getvalueof var="sddEligiblityStatus" param="sddEligiblityStatus"/>
											<dsp:getvalueof var="sddOptionEnabled" param="sddOptionEnabled"/>


                                            <dsp:getvalueof bean="BBBSPShippingGroupFormhandler.shippingOption" var="shippingOptionVal"  /> 
                                            

                                        
                                            
                                        </dsp:oparam>
                                        <dsp:oparam name="output">
											
                                            <dsp:getvalueof param="element.shipMethodDescription" id="elementVal" />
                                            <dsp:getvalueof param="element.shipMethodId" id="elementId" />              
                                            <dsp:getvalueof param="element.estdShipDurationInDaysLowerLimit" var="estdShipDurationInDaysLowerLimit" />
                                            <dsp:getvalueof param="element.estdShipDurationInDaysHigherLimit" var="estdShipDurationInDaysHigherLimit" />
											
											<c:choose>
												<c:when test="${elementId eq 'SDD'}">
													<c:set var="sddClass">sddClass</c:set>
													<c:set var="displaySDDAlways">
															<%=ServletUtil.getCurrentRequest().getSession().getAttribute("displaySDDAlways")%>
													</c:set>
													<c:if test="${empty sddEligiblityStatus || sddEligiblityStatus eq 'marketIneligible'  || sddEligiblityStatus eq 'addressIneligible'}">
														<c:choose>
															<c:when test="${displaySDDAlways eq 'true' }">
																<c:set var="disableSdd">disabled</c:set>
																<c:set var="hideSdd"></c:set>
																
															</c:when>
															<c:otherwise>
																<c:set var="hideSdd">hidden</c:set>
															</c:otherwise>
														</c:choose>
														
													</c:if>
													
													<c:choose>
														<c:when test="${sddEligiblityStatus eq 'addressIneligible'}">
															<c:set var="displayMessage"><bbbl:label key="lbl_sdd_address_ineligible" language="${pageContext.request.locale.language}" /></c:set>
															<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_address_ineligible" language="${pageContext.request.locale.language}" /></c:set>
															
														</c:when>
														<c:when test="${sddEligiblityStatus eq 'marketIneligible'}">
															<c:set var="displayMessage"><bbbl:label key="lbl_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
															<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
														</c:when>
														<c:when test="${sddEligiblityStatus eq 'itemIneligible'}">
															<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_ineligible" language="${pageContext.request.locale.language}" /></c:set>
															<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_item_ineligible" language="${pageContext.request.locale.language}" /></c:set>
															<c:set var="disableSdd">disabled</c:set>
														</c:when>
															<c:when test="${sddEligiblityStatus eq 'itemEligible'}">
															<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_eligible" language="${pageContext.request.locale.language}" /></c:set>
														</c:when>
														<c:when test="${sddEligiblityStatus eq 'itemUnavailable'}">
															<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_unavailable" language="${pageContext.request.locale.language}" /></c:set>
															<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_item_unavailable" language="${pageContext.request.locale.language}" /></c:set>
														</c:when>
													</c:choose>
													
													
												</c:when>
												<c:otherwise>
													<c:set var="sddClass"></c:set>
													<c:set var="hideSdd"></c:set>
													<c:set var="disableSdd">false</c:set>
												</c:otherwise>
												
											</c:choose>
											 
											

											<div class="shipMethodWrapper clearfix ${hideSdd}">
                                            <div class="shipMethod <c:if test="${elementId eq 'SDD'}">noMarRight </c:if><c:if test="${disableSdd eq 'disabled'}">sddShipDisable</c:if>
												<c:if test="${shippingOptionVal eq elementId && !ignoreDefaultApplied}">active</c:if>">                 
                                                <div class="label">
		                                              <c:choose>
														<c:when test="${elementId eq 'SDD'}">
		                                                <label id="lblshippingMethod${elementId}" for="shippingMethod${elementId}">
		                                                  </c:when>
														<c:otherwise>
														<label id="lblshippingMethod${elementVal}" for="shippingMethod${elementVal}">
														</c:otherwise>
														</c:choose>
                                                        <span class="shippingDesc"><c:out value="${elementVal}" /></span>

                                                        <c:if test="${not empty estdShipDurationInDaysLowerLimit && not empty estdShipDurationInDaysHigherLimit }" >
                                                            <span class="shippingDaysToShip">
                                                                ${estdShipDurationInDaysLowerLimit} - ${estdShipDurationInDaysHigherLimit}
                                                            </span>                         
                                                        </c:if>
                                                        <span class="shippingCharge">
	                                                        <c:if test="${elementId ne 'SDD' || (!empty sddEligiblityStatus && sddEligiblityStatus ne 'marketIneligible')}">
																<dsp:valueof param="element.shippingCharge" converter="currency" number="000.00"/>
															</c:if>
                                                        </span>
                                                    </label>
                                                </div>


                                                <c:choose>
                                                    <c:when test="${shippingOptionVal eq elementId && !ignoreDefaultApplied}"> 
                                                    <!-- SDD condition added for UE - chk -->             
                                                       <c:choose>
														<c:when test="${elementId eq 'SDD'}">
															  <button 
		                                                            class="btnPrimary button-Med shipMethodBtn applied" 
		                                                            id="shippingMethod${elementId}"                        
		                                                            data-methodvalue="${elementId}" >
		                                                            <span class="icon-checkmark icon-font" aria-hidden="true"></span>
		                                                            <bbbl:label key="lbl_spc_shipping_methods_selected" language="${pageContext.request.locale.language}" />
		                                                        </button>
														</c:when><c:otherwise>
															<button 
	                                                            class="btnPrimary button-Med shipMethodBtn applied" 
	                                                            id="shippingMethod${elementVal}"                        
	                                                            data-methodvalue="${elementId}" >
	                                                            <span class="icon-checkmark icon-font" aria-hidden="true"></span>
	                                                            <bbbl:label key="lbl_spc_shipping_methods_selected" language="${pageContext.request.locale.language}" />
	                                                        </button>
														
														</c:otherwise>
														</c:choose>
                                                      <%--   <button 
                                                            class="btnPrimary button-Med shipMethodBtn applied" 
                                                            id="shippingMethod${elementVal}"                        
                                                            data-methodvalue="${elementId}" >
                                                            <span class="icon-checkmark icon-font" aria-hidden="true"></span>
                                                            <bbbl:label key="lbl_spc_shipping_methods_selected" language="${pageContext.request.locale.language}" />
                                                        </button> --%>
                                                        
                                                    </c:when>
                                                    <c:otherwise>
														
														<c:choose>
														<c:when test="${elementId eq 'SDD'}">
                                                        <input 
                                                            type="button" <c:if test="${disableSdd eq 'disabled'}">disabled='disabled'</c:if>                      
                                                            id="shippingMethod${elementId}"                        
                                                            data-methodvalue="${elementId}"
                                                            value="<bbbl:label key='lbl_spc_apply_shipping' language='${pageContext.request.locale.language}' />"
                                                            class="btnPrimary button-Med shipMethodBtn shippingMethod${elementId}"     
                                                        /></c:when><c:otherwise>
															<input 
                                                            type="button"                       
                                                            id="shippingMethod${elementVal}"                        
                                                            data-methodvalue="${elementId}"
                                                            value="<bbbl:label key='lbl_spc_apply_shipping' language='${pageContext.request.locale.language}' />"
                                                            class="btnPrimary button-Med shipMethodBtn"     
                                                        /></c:otherwise>
														</c:choose>
														
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        <%--    <!-- BBBH-2385 if SDD the display messge for various eligibilty status is displayed below -->
                                           <c:choose>
						 						<c:when test="${elementId eq 'SDD' && disableSdd ne 'disabled'}">
													 <div class="sddMessage ${hideSddInvMsg}">
														<small> ${displayMessage}</small> 
													 </div>
												</c:when>
												<c:otherwise>
												 <div class="sddMessage hidden">
														<small> ${displayMessage}</small> 
													 </div>
												</c:otherwise>
											</c:choose> --%>

										</div>         
                              
                                        </dsp:oparam>    
                                        <dsp:oparam name="outputEnd">
                                       <c:choose>
							                 <c:when test="${disableSdd eq 'disabled'}">
							  						<span class='sddNotAvailable clearfix'><span>${displayMessage} </span>
																<c:if test="${not empty displayTooltip}">
																<a href="javascript:;" class="shippingPageSDDLink" >Why Not<span class="questionIcon">2</span></a>
														</c:if>
											 	</span>
							  				</c:when>
							  				<c:when test="${elementId eq 'SDD' && disableSdd ne 'disabled'}">
															<c:if test="${hideSdd eq 'hidden'}">
																<c:set var="hideSddInvMsg">hidden</c:set>
															</c:if>
															<span class='sddNotAvailable clearfix ${hideSddInvMsg}'><span>${displayMessage} </span>
															</span>
											</c:when>
							  				<c:otherwise>
							  					<span class='sddNotAvailable clearfix hidden'><span>${displayMessage} </span>
														<c:if test="${not empty displayTooltip}">
														<a href="javascript:;" class="shippingPageSDDLink" >Why Not<span class="questionIcon">2</span></a>
													</c:if>
												</span>
							  				</c:otherwise>
							         	</c:choose>
							         	<span id="sddTooltip" class="hidden">
											${displayTooltip}
										</span>
                                        <div class="clear"></div>
                                      </dsp:oparam>

                                    </dsp:droplet>
								</div>
                                </dsp:oparam>
                                
                            </dsp:droplet>
                            

                            <%--
                            This input is on the singleShipping JSP because this file will be ajaxed in, 
                            and it does not have a dsp:form tag, so cannot have dsp:input tags on it
                            the hidden input that will be populated by clicking shipping method button                            
                            --%>
                            
                      <%-- <dsp:input type="hidden" bean="BBBSPShippingGroupFormhandler.shippingOption" name="shippingMethodHidden" id="shippingMethod" value="${preSelectedShipMethod}" /> --%> 
                            
                            <%--
                            debugging
                            preSelectedShipMethod: ${preSelectedShipMethod}    
                            shippingOptionVal : ${shippingOptionVal}
                            --%>

                            <%--
                            <dsp:include page="/checkout/singlePage/shipping/shippingMethods.jsp">
								<dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
                                <dsp:param name="ignoreDefaultApplied" value="${true}" />
							</dsp:include>
                            --%>


								
							<div class="marTop_5 clearfix"><bbbl:label key="lbl_spc_shipping_option_disclaimer" language="${pageContext.request.locale.language}" /> 
							
							<bbbl:label key="lbl_spc_cartdetail_viewshippingcostslink" language="${pageContext.request.locale.language}" /></div>
                        
                        <dsp:input type="hidden" bean="BBBSPShippingGroupFormhandler.shippingOption" name="shippingMethodHidden" id="shippingMethod" value="${preSelectedShipMethod}" />
                        <dsp:input type="hidden" bean="BBBSPShippingGroupFormhandler.fromPaypalEdit" name="fromPaypalEditHidden" id="fromPaypalEdit" value="false" />
                       
                        <%--  <dsp:input type="hidden" bean="BBBSPShippingGroupFormhandler.shippingOption" name="shippingMethodHidden" id="shippingMethod" value="${preSelectedShipMethod}" />	 --%>
                        </fieldset>
                       
						
                        <div class="button button_active  hidden">
                            <c:set var="lbl_button_next" scope="page">
	                               <bbbl:label
										key="lbl_spc_shipping_button_next"
										language="${pageContext.request.locale.language}" />
                            </c:set>
                            <dsp:input type="submit" value="${lbl_button_next}"  iclass="enableOnDOMReady" id="submitShippingSingleLocationBtn" 
                            	 bean="BBBSPShippingGroupFormhandler.addShipping">
                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="submitShippingSingleLocationBtn"/>
                                <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
                        </div>

                        
                    </dsp:form>
                </div>
            </div>
        </div>
        
    
</dsp:page>


