<dsp:page>
    <dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
  	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapCheckDroplet"/>
  	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapGreetingsDroplet"/>
  	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="formExceptionFlag" param="formExceptionFlag" />
	<dsp:getvalueof param="registryItemCount" var="registryCount"/>      

	<dsp:setvalue bean="BBBShippingGroupFormhandler.removeEmptyShippingGroup" />
	<legend class="hidden">Shipping Options</legend>
	<dsp:input bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.shippingGroupId" type="hidden" beanvalue="ShoppingCart.current.shippingGroups[0].id"/>  	
	<dsp:input bean="BBBShippingGroupFormhandler.siteId" value="${currentSiteId}" type="hidden"/>
  						<h3 class="sectionHeading">Shipping Options</h3>
										<dsp:include page="packnhold.jsp">
												<dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
										</dsp:include>
							<%-- GiftWrapCheckDroplet starts --%>
							<dsp:droplet name="GiftWrapCheckDroplet">
										<dsp:param name="shippingGroup" bean="ShoppingCart.current.shippingGroups[0]" />
										<dsp:param name="siteId" value="${currentSiteId}" />
										<dsp:oparam name="output">
										<dsp:getvalueof var="nonGiftWrapSkus" param="nonGiftWrapSkus" />
								<div class="checkboxItem input clearfix">
									<div id="giftCheckboxWrapper" class="checkbox"> 
										<dsp:getvalueof id="checkboxSelection" bean="ShoppingCart.current.shippingGroups[0].specialInstructions.giftMessage"/>
										<dsp:getvalueof id="giftWrapIndicator" bean="ShoppingCart.current.shippingGroups[0].giftWrapInd"/>
										<dsp:getvalueof id="giftWrapItemIndicator" bean="ShoppingCart.current.shippingGroups[0].containsGiftWrap"/>
										
										<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftWrap" value="${giftWrapItemIndicator}" />
										<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftingFlag" value="${giftWrapIndicator}" />
										<c:choose>
										      <c:when test="${(registryCount > 0 && IsRegistryAddress) || not empty checkboxSelection || giftWrapItemIndicator || showGiftOptions=='true'}">
										          <c:set var="giftChecked" value="true"/>
										      </c:when>
										      <c:otherwise>
										          <c:set var="giftChecked" value="false"/>
										      </c:otherwise>
										    </c:choose>
										    
										<dsp:input type="checkbox" checked="${giftChecked}" value="true" name="shippingOption2" id="shippingOption2" bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftingFlag"/>
									</div>
									<div class="label">
										<jsp:useBean id="GiftPriceCounter" class="java.util.HashMap" scope="request"/>
										<label for="shippingOption2"><strong><bbbl:label key="lbl_gift_order_include_gifts" language="<c:out param='${language}'/>"/></strong>
										  <dsp:droplet name="CurrencyFormatter">
													<dsp:param name="currency" param="giftWrapPrice"/>
													<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
													<dsp:oparam name="output">
													   	<c:set target="${GiftPriceCounter}" property="giftPrice"><dsp:valueof param="formattedCurrency"/></c:set>
													</dsp:oparam>
										 </dsp:droplet>
										<bbbl:label key="lbl_gift_packing_slip_msg" language="<c:out param='${language}'/>" placeHolderMap="${GiftPriceCounter}"/></label>
									</div>
									<div class="clear"></div>
									<div class="subForm hidden clearfix" id="giftMsgBox">
										
										<div class="clearfix ">
											<div class="fl">
												<div class="giftPackaging width_5">
													<div class="label">
														<label for="shippingGiftMessage">
															<bbbl:label key="lbl_multi_gift_add_message" language="<c:out param='${language}'/>"/>
														</label>
													</div>
													<div class="text">
														<div class="width_5">
															<dsp:textarea name="shippingGiftMessage" bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftMessage" id="shippingGiftMessage" rows="4" cols="28" maxlength="200">${checkboxSelection}</dsp:textarea>
														</div>
													</div>
													<%--GiftWrapGreetingsDroplet start --%>
													<dsp:droplet name="GiftWrapGreetingsDroplet">
														<dsp:param name="siteId" value="${currentSiteId}" />
														<dsp:oparam name="output">
														<div class="addCommonGreetings">
															<c:set var="commonGreeting"> <bbbl:label key="lbl_multi_gift_common_greeting" language="<c:out param='${language}'/>" /></c:set>
															<a href="#" class="bold" id="shippingAddCommonGreetMsgs" title="${commonGreeting}"> <bbbl:label key="lbl_multi_gift_symbol_plus" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_multi_gift_common_greeting" language="<c:out param='${language}'/>"/></a>
															<select size="10" id="shippingCommonGreetingMsgs" name="shippingCommonGreetingMsgs" class="hidden">
                                                                <option value='0' selected='selected'><bbbl:label key="lbl_gift_default_greeting_option" language="<c:out param='${language}'/>"/></option>
																<dsp:droplet name="ForEach">
																<dsp:param name="array" param="giftWrapMessages"/>
																<dsp:oparam name="output">
																	<option value='<dsp:valueof param="count"/>'><dsp:valueof param="element"/></option>
																</dsp:oparam>
																</dsp:droplet>	
															</select>
														</div>
														</dsp:oparam>
													</dsp:droplet>
													<%--GiftWrapGreetingsDroplet ends --%>
												</div>
											</div>
										</div>
										
										
										<dsp:droplet name="Switch">
										<dsp:param name="value" param="giftWrapFlag"/>
										<dsp:oparam name="true">
										<div class="checkboxItem input clearfix includeGiftPackagingContainer checkBoxGiftPacking">
												<div class="checkbox">
													<dsp:input type="checkbox" name="includeFigtPackaging" id="includeFigtPackaging" bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftWrap"/>
												</div>
												<div class="label">
													<label for="includeFigtPackaging">
													<bbbl:label key="lbl_gift_include_packaging" language="<c:out param='${language}'/>"/>
													<span class="bold">
														 <dsp:droplet name="CurrencyFormatter">
														   <dsp:param name="currency" param="giftWrapPrice"/>
														   <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														   <dsp:oparam name="output">
														     <dsp:valueof param="formattedCurrency"/>
														   </dsp:oparam>
														 </dsp:droplet>
 													</span>
													<bbbl:label key="lbl_gift_include_packaging_end" language="<c:out param='${language}'/>"/>
													<a href="/store/static/GiftPackagingPopUp" class="giftWrappingDetails wrappinggnewOrPopup"   onclick=""> details</a>
													</label>
													<c:if test="${currentSiteId eq 'BedBathCanada'}">
													   <bbbt:textArea key="txt_disclaimer_gift_message" language="<c:out param='${language}'/>"/>
													</c:if>
												</div>
												<c:if test="${nonGiftWrapSkus ne ''}">
													 <div class="giftPackagingMessage"><bbbl:label key="lbl_gift_msg_some_items" language="<c:out param='${language}'/>"/> <dsp:valueof param="nonGiftWrapSkus" valueishtml="true" /> <bbbl:label key="lbl_gift_msg_not_eligible" language="<c:out param='${language}'/>"/></div>
												</c:if>
											</div>
										</dsp:oparam>
										<dsp:oparam name="false">
										<div class="checkboxItem input clearfix"><bbbl:label key="lbl_gift_no_item_available" language="<c:out param='${language}'/>"/></div></dsp:oparam>
										</dsp:droplet>
										</dsp:oparam>
										</dsp:droplet>
									</div>
								</div>
							
							<%-- GiftWrapCheckDroplet ends --%>
</dsp:page>