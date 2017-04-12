 
 <dsp:page>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
  	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapGreetingsDroplet"/>
  	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="giftWrapPrice" param="giftWrapPrice" />
	<dsp:getvalueof var="shipGroupId" param="shipGroupId" />
	<dsp:getvalueof var="shipGroupParam" param="shipGroupParam" />
	<dsp:getvalueof var="count" param="count" />
	<dsp:getvalueof var="shipGroupGiftMessage" param="shipGroupGiftMessage" />
	<dsp:getvalueof var="shipGroupGiftInd" param="shipGroupGiftInd" />
	<dsp:getvalueof var="nonGiftWrapSkus" param="nonGiftWrapSkus" />
	<dsp:getvalueof var="giftWrapFlag" param="giftWrapFlag" />

<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftMessage" value="${shipGroupGiftMessage}" />
<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftWrap" paramvalue="shipGroupParam.containsGiftWrap" />

<dsp:getvalueof var="giftingFlag" value="${false}" scope="page" vartype="java.lang.Boolean"/>
<c:if test="${shipGroupParam.containsGiftWrap}">
	<dsp:getvalueof var="giftingFlag" value="${shipGroupParam.containsGiftWrap}" scope="page" vartype="java.lang.Boolean"/>											
</c:if>
<c:if test="${shipGroupGiftInd}">
	<dsp:getvalueof var="giftingFlag" value="${shipGroupGiftInd}" scope="page" vartype="java.lang.Boolean"/>											
</c:if>

<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftingFlag" value="${giftingFlag}"/>

<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>	
	
<dsp:input bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].shippingGroupId" type="hidden" value="${shipGroupId}"/>

                    <div class="checkboxItem input clearfix">
                        <div class="checkbox">
          	<dsp:input type="checkbox" name="shippingOption2_${count}" id="shippingOption3_${count}" bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftingFlag" checked="true">
                <dsp:tagAttribute name="aria-checked" value="true"/>
                <dsp:tagAttribute name="aria-labelledby" value="lblshippingOption3_${count}"/>
            </dsp:input>
						                            
                        </div>
                        <div class="label">
                            <label id="lblshippingOption3_${count}" for="shippingOption3_${count}"><strong><bbbl:label key="lbl_spc_multi_gift_include_gifts" language="<c:out param='${language}'/>"/> </strong> <bbbl:label key="lbl_spc_multi_gift_packing_slip_info" language="<c:out param='${language}'/>"/></label>
                        </div>
                        <div class="clear"></div>
                        <div class="subForm clearfix hidden">
                            
                            <div class="clearfix ">
                                <div class="fl">
                                    <div class="giftPackaging clearfix">
                                        <div class="label">
                                            <label for="shippingGiftMessage_${count}"> <bbbl:label key="lbl_spc_multi_gift_add_message" language="<c:out param='${language}'/>"/> </label>
                                        </div>
                                        <div class="text">
                                            <div class="width_5">
                                               <dsp:textarea name="shippingGiftMessage_${count}" bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftMessage" id="shippingGiftMessage_${count}" rows="4" cols="28" iclass="shippingGiftMessage giftMessage"
															maxlength="200"></dsp:textarea>

                                            </div>
                                        </div>

										<dsp:droplet name="GiftWrapGreetingsDroplet">
											<dsp:param name="siteId" value="${currentSiteId}" />
											<dsp:oparam name="output">
											 <div class="fl marLeft_20 marTop_20 addCommonGreetings"> <a href="#" class="bold shippingAddCommonGreetMsgs" id="shippingAddCommonGreetMsgs_${count}"> <bbbl:label key="lbl_spc_multi_gift_symbol_plus" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_spc_multi_gift_common_greeting" language="<c:out param='${language}'/>"/></a>
												<select size="10" id="shippingCommonGreetingMsgs_${count}" name="shippingCommonGreetingMsgs" class="hidden" aria-required="false" aria-labelledby="shippingCommonGreetingMsgs_${count}" >
                                                    <option value='0' selected='selected'><bbbl:label key="lbl_spc_gift_default_greeting_option" language="<c:out param='${language}'/>"/></option>
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


                                    </div>
                                </div>
                            </div>
                            

							<dsp:droplet name="Switch">
										<dsp:param name="value" param="giftWrapFlag"/>
										<dsp:oparam name="true">
											 <div class="checkboxItem input clearfix includeGiftPackagingContainer noPadBot">
												   <div class="checkbox">
														<dsp:input type="checkbox" name="includeFigtPackaging_[${count}]" id="includeFigtPackaging_${count}"  bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftWrap">
                                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                                            <dsp:tagAttribute name="aria-labelledby" value="lblincludeFigtPackaging_${count}"/>
                                                        </dsp:input>
												   </div>

													 <div class="label">
														<label id="lblincludeFigtPackaging_${count}" for="includeFigtPackaging_${count}"><bbbl:label key="lbl_spc_multi_gift_include_packaging" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_spc_multigift_include_wrap_start" language="<c:out param='${language}'/>"/><span class="bold"><dsp:droplet name="CurrencyFormatter">
																			   <dsp:param name="currency" param="giftWrapPrice"/>
																			   <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
																			   <dsp:oparam name="output">
																				 <dsp:valueof param="formattedCurrency"/>
																			   </dsp:oparam>
																			 </dsp:droplet></span><bbbl:label key="lbl_spc_multigift_include_wrap_end" language="<c:out param='${language}'/>"/><a href="/store/static/GiftPackagingPopUp" class="giftWrappingDetails wrappinggnewOrPopup"   onclick=""> details</a></label>
														<c:if test="${currentSiteId eq 'BedBathCanada'}">
															<bbbt:textArea key="txt_spc_disclaimer_gift_message" language="<c:out param='${language}'/>"/>
														</c:if>
													 </div>
													 <c:if test="${nonGiftWrapSkus ne ''}">
													 <div class="giftPackagingMessage"><bbbl:label key="lbl_spc_gift_msg_some_items" language="<c:out param='${language}'/>"/> <c:out value="${nonGiftWrapSkus}" /> <bbbl:label key="lbl_spc_gift_msg_not_eligible" language="<c:out param='${language}'/>"/></div>
													 </c:if>

											 </div>
										</dsp:oparam>
										<dsp:oparam name="false"><div class="giftPackagingMessage"><bbbl:label key="lbl_spc_gift_no_item_available" language="<c:out param='${language}'/>"/></div>
										</dsp:oparam>

							</dsp:droplet>

                           


                        </div>
                    </div>
	
	</dsp:page>
                    