<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
    <dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
    <dsp:importbean bean="/com/bbb/payment/giftcard/BBBGiftCardFormHandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:getvalueof var="orderType" vartype="java.lang.String" bean="ShoppingCart.current.onlineBopusItemsStatusInOrder" scope="page"/>
    <dsp:getvalueof var="giftCardException" bean="BBBGiftCardFormHandler.formExceptions"/>
    <div id="payWithGiftCardContainer" class="checkboxItem input clearfix payWithGiftCard noPad">
        <c:choose>
            <c:when test="${orderType eq 'BOPUS_ONLY'}">
                <h3 class="sectionHeading noBorder"><bbbl:label key="lbl_gc_label2" language="<c:out param='${language}'/>"/></h3>
                <p class="noMar"><bbbl:label key="lbl_gc_label1" language="<c:out param='${language}'/>"/></p>
            </c:when>
            <c:when test="${orderType ne 'BOPUS_ONLY'}">
                <dsp:droplet name="Switch">
                    <dsp:param name="value" value="${isOrderAmtCoveredVar}"/>
                    <dsp:oparam name="false">
                        <%-- NOTE: if there is a gift card applied then checkbox should be disabled and checked. If no gift cards applied then enabled and not checked --%>
                        <dsp:droplet name="Switch">
                            <dsp:param name="value" value="${isGiftcardsVar || not empty giftCardException}"/>
                            <dsp:oparam name="true">
                                <div class="checkbox">
                                    <input type="checkbox" value="" name="payWith" id="payWith" checked="checked" disabled="disabled" aria-hidden='false' aria-labelledby="lblpayWith" <c:if test="${empty giftCardException}">disabled="disabled" </c:if>/>
                                </div>
                             </dsp:oparam>
                             <dsp:oparam name="false">
                                <div class="checkbox">
                                    <input type="checkbox" value="" name="payWith" aria-hidden='false' aria-labelledby="lblpayWith" id="payWith" />
                                </div>
                             </dsp:oparam>
                        </dsp:droplet>
                        <div class="label marBottom_10">
                            <label id="lblpayWith" for="payWith" class="payWithGirftCardLabel"><bbbl:label key="lbl_gc_label2" language="<c:out param='${language}'/>"/></label>
                        </div>
                        <div class="clear"></div>
                    </dsp:oparam>
                    <dsp:oparam name="true">
                        <h3 class="sectionHeading noBorder"><bbbl:label key="lbl_gc_label2" language="<c:out param='${language}'/>"/></h3>
                    </dsp:oparam>
                </dsp:droplet>
                
                <div class="clear"></div>
                <%--Droplet area starts --%>
                <dsp:droplet name="BBBPaymentGroupDroplet">
                    <dsp:param bean="ShoppingCart.current" name="order"/>
                    <dsp:param name="serviceType" value="GiftCardDetailService"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="orderTotal" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.total"></dsp:getvalueof>
                        <dsp:getvalueof vartype="java.lang.String" var="totalGCAmt" param="coveredByGC" scope="request"/>
                        <div id="payWithGiftCard" class="subForm clearfix"> 
                            <div class="grid_5 alpha suffix_3 omega">
                                <dl class="payWithCard clearfix marBottom_10">
                                    <dt class="grid_3 alpha"><bbbl:label key="lbl_gc_amtDue" language="<c:out param='${language}'/>"/></dt>
                                    <dd class="grid_1 textRight">
                                        <dsp:droplet name="CurrencyFormatter">
                                            <dsp:param name="currency" value="${orderTotal - totalGCAmt}"/>
                                            <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
                                            <dsp:oparam name="output">
                                           	<dsp:getvalueof var="formatCurrency" param="formattedCurrency" ></dsp:getvalueof>
                                            <c:if test="${fn:contains(formatCurrency, '($0.00)')}">
                                            	<c:set var="formatCurrency" value="$0.00"></c:set>
                                            </c:if>
                                                ${formatCurrency}
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </dd>
                                    <dt class="grid_3 alpha"><bbbl:label key="lbl_gc_coveredAmt" language="<c:out param='${language}'/>"/></dt>
                                    <dd class="grid_1 textRight">
                                        <dsp:droplet name="CurrencyFormatter">
                                            <dsp:param name="currency" param="coveredByGC"/>
                                            <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
                                            <dsp:oparam name="output">
                                                <dsp:valueof param="formattedCurrency"/>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </dd>
                                </dl>
                                <c:if test="${isOrderAmtCoveredVar == false}">
                                    <p class="combinePayment clearfix"><bbbl:label key="lbl_gc_ccgc" language="<c:out param='${language}'/>"/></p>
                                </c:if>
                            </div>
                            <dsp:droplet name="ErrorMessageForEach">
							<dsp:param bean="BBBGiftCardFormHandler.formExceptions" name="exceptions" />
								<dsp:oparam name="output">
									<p class="error"><dsp:valueof param="message" valueishtml="true"/></p>
								</dsp:oparam>
							</dsp:droplet>
                            <%-- pay with gift card Grid section start--%>
                            <dsp:droplet name="Switch">
                                <dsp:param name="value" value="${isGiftcardsVar}"/>
                                <dsp:oparam name="true">
                                    <div class="grid_8 highlightSection alpha omega">
                                        <div class="clearfix">
                                            <ul class="productsListHeader">
                                                <li class="grid_3"><bbbl:label key="lbl_gc_gcNumber" language="<c:out param='${language}'/>"/></li>
                                                <li class="grid_1 alpha textRight"><bbbl:label key="lbl_gc_used" language="<c:out param='${language}'/>"/></li>
                                                <li class="grid_2 textRight"><bbbl:label key="lbl_gc_remainingBal" language="<c:out param='${language}'/>"/></li>
                                                <li class="grid_2 alpha omega textRight"></li>
                                            </ul>
                                            <ul class="cartProducts noMar">
                                                <dsp:droplet name="ForEach">
                                                    <dsp:param param="giftcards" name="array"/>
                                                    <dsp:oparam name="output">
                                                        <li>
                                                            <div class="grid_3 omega"><dsp:valueof param="element.cardNumber"></dsp:valueof></div>
                                                            <div class="grid_1 textRight">
                                                                <dsp:droplet name="CurrencyFormatter">
                                                                    <dsp:param name="currency" param="element.amount"/>
                                                                    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
                                                                    <dsp:oparam name="output">
                                                                        <dsp:valueof param="formattedCurrency"/>
                                                                    </dsp:oparam>
                                                                </dsp:droplet>
                                                             </div>
                                                             <div class="grid_2 textRight"> 
                                                                <dsp:getvalueof var="cardBalance" param="element.balance">
                                                                    <dsp:getvalueof var="cardAmount" param="element.amount">
                                                                        <c:choose>
                                                                            <c:when test="${cardBalance >= cardAmount}">
                                                                                <dsp:droplet name="CurrencyFormatter">
                                                                                    <dsp:param name="currency" value="${cardBalance - cardAmount}"/>
                                                                                    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
                                                                                    <dsp:oparam name="output">
                                                                                        <dsp:valueof param="formattedCurrency"/>
                                                                                    </dsp:oparam>
                                                                                </dsp:droplet>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <dsp:droplet name="CurrencyFormatter">
                                                                                    <dsp:param name="currency" value="0"/>
                                                                                    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
                                                                                    <dsp:oparam name="output">
                                                                                        <dsp:valueof param="formattedCurrency"/>
                                                                                    </dsp:oparam>
                                                                                </dsp:droplet>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </dsp:getvalueof>
                                                                </dsp:getvalueof> 
                                                             </div>
                                                             <div class="grid_2 alpha omega textRight">
                                                                <dsp:a page="/checkout/payment/billing_payment.jsp" title="Remove" iclass="remove" id="removeGiftCard">
                                                                    <dsp:property bean="BBBGiftCardFormHandler.paymentGroupId" paramvalue="element.id"/>
                                                                    <dsp:property bean="BBBGiftCardFormHandler.removePaymentGroup" value="true"/>
                                                                    <bbbl:label key="lbl_gc_remove" language="<c:out param='${language}'/>"/> <span class="removeIcon"></span>
                                                                    <%--<bbbl:label key="lbl_cartdetail_movetowishList" language="<c:out param='${language}'/>"/>--%>
                                                                </dsp:a>
                                                                <%--<a href="#" title="Remove" class="remove">REMOVE <span class="removeIcon"></span></a>--%> 
                                                            </div>
                                                        </li>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                            </ul>
                                        </div>
                                    </div>
                                </dsp:oparam>
                            </dsp:droplet>
                            <%-- pay with gift card grid section end --%>
                            
                            <%-- Add a/another Girt Card start --%>
                            <dsp:droplet name="Switch">
                                <dsp:param name="value" value="${isMaxGiftcardAddedVar}"/>
                                <dsp:oparam name="false">
                                    <dsp:droplet name="Switch">
                                        <dsp:param name="value" value="${isOrderAmtCoveredVar}"/>
                                        <dsp:oparam name="false">
                                            <div class="grid_8 alpha omega">
                                                <dsp:droplet name="Switch">
                                                    <dsp:param name="value" value="${isGiftcardsVar}"/>
                                                    <dsp:oparam name="true">
                                                        <h3 class="sectionHeading noBorder"><bbbl:label key="lbl_gc_addGC" language="<c:out param='${language}'/>"/></h3>
                                                    </dsp:oparam>
                                                    <dsp:oparam name="false">
                                                        <h3 class="sectionHeading noBorder"><bbbl:label key="lbl_gc_addGC_MCC" language="<c:out param='${language}'/>"/></h3>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                                <dsp:form id="frmPayWithGiftCard" iclass="clearfix" action="${contextPath}/checkout/payment/billing_payment.jsp" method="post">
                                                    <%--<dsp:droplet name="Switch">
                                                        <dsp:param name="value" bean="BBBGiftCardFormHandler.formError"/>
                                                        <dsp:oparam name="true">
                                                            <dsp:droplet name="ForEach">
                                                                <dsp:param name="array" bean="BBBGiftCardFormHandler.formExceptions"/>
                                                                <dsp:oparam name="output">
                                                                    <dsp:droplet name="Switch">
                                                                    <dsp:param name="value" param="element.errorCode"/>
                                                                    <dsp:oparam name="giftcarderror">
                                                                        <div class="error serverErrors"><dsp:valueof param="element.message"/></div>
                                                                    </dsp:oparam>
                                                                    </dsp:droplet>
                                                                </dsp:oparam>
                                                            </dsp:droplet>
                                                        </dsp:oparam>
                                                    </dsp:droplet>--%>
                                                    <div class="grid_2 alpha omega input marTop_5">
                                                        <label id="lblgiftCardNumberPWGC" for="giftCardNumberPWGC"><bbbl:label key="lbl_gc_gcNumber" language="<c:out param='${language}'/>"/></label>
                                                        <div class="text">
                                                            <dsp:input autocomplete="off" type="text" bean="BBBGiftCardFormHandler.giftCardNumber" name="giftCardNumber" id="giftCardNumberPWGC" maxlength="16"  iclass="validateCard" >
                                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblgiftCardNumberPWGC errorgiftCardNumberPWGC"/>
																<dsp:tagAttribute name="aria-describedby" value="errorgiftCardNumberPWGC"/> 
                                                            </dsp:input>
                                                        </div>
                                                    </div>
                                                    <div class="grid_2 input marTop_5 alpha omega padLeft_20">
                                                        <label id="lblgiftCardPinPWGC" for="giftCardPinPWGC"><bbbl:label key="lbl_gc_gcPin" language="<c:out param='${language}'/>"/></label>
                                                        <div class="text">
                                                            <dsp:input autocomplete="off" type="text" bean="BBBGiftCardFormHandler.giftCardPin" name="giftCardPin" id="giftCardPinPWGC" maxlength="8" iclass="validateCard">
                                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblgiftCardPinPWGC errorgiftCardPinPWGC"/>
																<dsp:tagAttribute name="aria-describedby" value="errorgiftCardPinPWGC"/> 
                                                            </dsp:input>
															<bbbl:label key="lbl_payment_find_your_pin" language="<c:out param='${language}'/>"/>
                                                        </div>
                                                    </div>
                                                    <div class="grid_2 input alpha padLeft_20">
                                                        <div class="button addGC">
                                                            <input type="submit" value="CHECK BALANCE" id="checkBalance" onclick="javascript:omnitureExternalLinks('Value Link: Add a Gift Card-Check Balance Button ')"/>
                                                            <%-- <input type="button" value="CHECK BALANCE" title="CHECK BALANCE" id="checkBalance" />--%>
                                                        </div>
                                                    </div>
                                                    <div class="cb clearfix giftCardBalanceWrap hidden marBottom_10">
                                                        <div class="grid_3 alpha">
                                                            <label><strong>Gift Card:</strong> <span class="giftCardNumberResult giftCardResultItem"></span></label>
                                                        </div>
                                                        <div class="grid_2 alpha omega">
                                                            <label class="bal"><strong><bbbl:label key="lbl_gc_bal" language="<c:out param='${language}'/>"/></strong> <span class="balance giftCardResultItem"></span></label>
                                                        </div>
                                                    </div>
                                                    <div class="grid_8 alpha omega marBottom_10">
                                                        <div class="button button_active">                                                            
                                                            <dsp:input type="submit" bean="BBBGiftCardFormHandler.createGiftCard" value="Apply">
                                                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                                <dsp:tagAttribute name="role" value="button"/>
                                                            </dsp:input>                                                            
                                                        </div>
                                                    </div>
                                                    <dsp:input type="hidden" bean="BBBGiftCardFormHandler.giftCardSuccessURL" value="/checkout/payment/billing_payment.jsp"/>
                                                </dsp:form>
                                                <dsp:form action="${contextPath}/checkout/payment/billing_payment.jsp" method="post"  id="frmPayWithGiftCardHiddenForm" iclass="hidden">
                                                    <dsp:input type="hidden" name="giftCardNumber" value="" id="giftCardNumberPWGC" bean="BBBGiftCardFormHandler.giftCardNumber"/>
                                                    <dsp:input type="hidden" name="giftCardPin" value="" id="giftCardPinPWGC"  bean="BBBGiftCardFormHandler.giftCardPin" />
                                                    <dsp:input type="submit" name="submit" value="submit" id="btnSubmitCheckGC" bean="BBBGiftCardFormHandler.balance">
                                                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                        <dsp:tagAttribute name="aria-labelledby" value="btnSubmitCheckGC"/>
                                                        <dsp:tagAttribute name="role" value="button"/>
                                                    </dsp:input>
                                                </dsp:form>
                                            </div>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </dsp:oparam>
                            </dsp:droplet>
                        </div>
                     </dsp:oparam>
                </dsp:droplet>
            </c:when>
        </c:choose>
    </div>
    <script type="text/javascript">
	 function omnitureExternalLinks(data){
       	if (typeof s !== 'undefined') {
       	externalLinks(data);
       	}
       }</script>
</dsp:page>
