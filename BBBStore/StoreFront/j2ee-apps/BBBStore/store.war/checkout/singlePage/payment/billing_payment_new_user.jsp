<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
    <dsp:importbean bean="/com/bbb/payment/giftcard/BBBGiftCardFormHandler"/>
    <dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetCreditCardsForPayment"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/BBBCreditCardContainer"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <c:set var="year" scope="page">
        <bbbl:label key="lbl_spc_addcreditcard_year" language="${pageContext.request.locale.language}" />
    </c:set>
    <c:set var="chooseCardType" scope="page">
       <bbbl:label key="lbl_spc_choose_cardtype" language="${pageContext.request.locale.language}"/>
    </c:set>
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<c:choose>
    	<c:when test="${internationalCCFlag eq 'true'}">
    		<input type="hidden" id="internationalCC" name="internationalCC" value="${internationalCCFlag}" />
    	</c:when>
    	<c:otherwise>
    		<input type="hidden" id="internationalCC" name="internationalCC" value="" />
    	</c:otherwise> 
   	</c:choose>    
    <div class=" marTop_10">
        <div class="clearfix">
            <%-- pay with a credit card grid start --%>
            <div id="frmPayUsingSavedCard" class="newUser">
                <fieldset class="radioGroup">
                    <legend class="hidden"><bbbl:label key="lbl_spc_payment_newCard" language="<c:out param='${language}'/>"/></legend>
                    <ul class="cartProducts payUsingSavedCards">
                        <li class="noBorder">
                            <div id="addNewCardContainer" class="radioItem input clearfix">
                                <div class="grid_3 alpha" id="addNewCardLabel">
                                    <div class="radio">
                                        <dsp:input type="hidden" value="New" bean="PaymentGroupFormHandler.selectedCreditCardId" name="selectedCreditCardId" id="selectedCreditCardId" checked="true">
                                   			 <dsp:tagAttribute name="aria-hidden" value="false"/>
                                        </dsp:input>
                                    </div>
                                    <div class="label">
                                        <label for="selectedCreditCardId"> <span><bbbl:label key="lbl_spc_payment_newCard" language="<c:out param='${language}'/>"/></span> </label>
                                    </div>
                                </div>
                                <div class="grid_3 textCenter">
                                    <div class="label">
                                        <label for="selectedCreditCardId"> <span>&nbsp;</span> </label>
                                    </div>
                                </div>
                                <div class="grid_2 textRight omega">
                                    <div class="label">
                                        <label for="selectedCreditCardId"> <span>&nbsp;</span> </label>
                                    </div>
                                </div>
                                <%--Add credit card form start--%>
                                <div class="clear"></div>
                                <div class="subForm clearfix">
                                    <fieldset>

                                        <dsp:input type="hidden" id="cardType" name="cardType" bean="PaymentGroupFormHandler.creditCardInfo.creditCardType" />

                                        <div class="input_wrap grid_4 alpha omega">
                                            <label id="lblcardType" for="cardNumber" class="popUpLbl">
                                                <bbbl:label key="lbl_spc_payment_cardNumber" language="<c:out param='${language}'/>"/>
                                            </label>
                                            <dsp:input bean="PaymentGroupFormHandler.creditCardInfo.creditCardNumber" type="text" name="cardNumber" id="cardNumber" maxlength="16" autocomplete="off">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblcardNumber errorcardNumber"/>
                                                <dsp:tagAttribute name="class" value="grid_4 noMar omega" />
                                            </dsp:input>
                                        </div>
                                        <div class="cardType grid_3 omega">
                                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                <dsp:param name="array" bean="PaymentGroupFormHandler.creditCardTypes" />
                                                <dsp:param name="elementName" value="cardlist" />
                                                <dsp:oparam name="output">
                                                    <dsp:getvalueof id="cardImage" param="cardlist.imageURL" />
                                                    <dsp:getvalueof id="cardName" param="cardlist.name" />
                                                    <dsp:getvalueof id="cardCode" param="cardlist.code" />
                                                    <img class="creditCardIcon" src="${imagePath}${cardImage}" width="41" height="26" alt="${cardName}" data-cardcode="${cardCode}">
                                                </dsp:oparam>
                                            </dsp:droplet>
                                        </div>
                                        <div class="input_wrap input text grid_7 alpha marTop_20">
                                            <div class="select grid_2 marRight_20">
                                                <label class="popUpLbl"><bbbl:label key="lbl_Exp_Month" language="${pageContext.request.locale.language}"/></label>
                                                <dsp:select bean="PaymentGroupFormHandler.creditCardInfo.expirationMonth" id="creditCardMonth" name="creditCardMonth">
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblcreditCardMonth errorcreditCardMonth"/>
                                                    <dsp:option value=""><bbbl:label key="lbl_addcreditcard_month" language="${pageContext.request.locale.language}"/></dsp:option>
                                                    <dsp:option value="1">01</dsp:option>
                                                    <dsp:option value="2">02</dsp:option>
                                                    <dsp:option value="3">03</dsp:option>
                                                    <dsp:option value="4">04</dsp:option>
                                                    <dsp:option value="5">05</dsp:option>
                                                    <dsp:option value="6">06</dsp:option>
                                                    <dsp:option value="7">07</dsp:option>
                                                    <dsp:option value="8">08</dsp:option>
                                                    <dsp:option value="9">09</dsp:option>
                                                    <dsp:option value="10">10</dsp:option>
                                                    <dsp:option value="11">11</dsp:option>
                                                    <dsp:option value="12">12</dsp:option>
                                                </dsp:select>
                                                <label id="errorcreditCardMonth" for="creditCardMonth" generated="true" class="error"></label>
                                            </div>
                                            <div class="select grid_2 marRight_20">
                                                <jsp:useBean id="now" class="java.util.Date" scope="request" />
                                                <fmt:formatDate var="year_val" value="${now}" pattern="yyyy" />
                                                <label class="popUpLbl"><bbbl:label key="lbl_Exp_Year" language="${pageContext.request.locale.language}"/></label>
                                                <dsp:select bean="PaymentGroupFormHandler.creditCardInfo.expirationYear" id="creditCardYear" name="creditCardYear">
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="creditCardYear lblcreditCardYear errorcreditCardYear"/>
                                                    <dsp:option value="">${year}</dsp:option>
                                                    <dsp:option value="${year_val}">${year_val}</dsp:option>
                                                    <dsp:getvalueof var="cardMaxYear" bean="PaymentGroupFormHandler.creditCardYearMaxLimit"></dsp:getvalueof>
                                                    <c:forEach begin="0" end="${cardMaxYear-1}" step="1" varStatus="count">
                                                        <dsp:option value="${year_val+count.count}">${year_val+count.count}</dsp:option>
                                                    </c:forEach>
                                                </dsp:select>
                                                <label id="errorcreditCardYear" for="creditCardYear" generated="true" class="error"></label>
                                                <label id="lblcreditCardYear" for="creditCardYear" class="offScreen"> <bbbl:label key="lbl_spc_payment_expiresOn" language="<c:out param='${language}'/>"/> </label>
                                            </div>
                                            <div class="input grid_2 omega">
                                                    <label id="lblsecurityCode" for="securityCode" class="popUpLbl"> <bbbl:label key="lbl_spc_payment_securityCode" language="<c:out param='${language}'/>"/> </label>
                                                <div class="text noMarBot">
                                                    <dsp:input bean="PaymentGroupFormHandler.creditCardInfo.cardVerificationNumber" type="text" name="securityCode" id="securityCode" maxlength="4" autocomplete="off" iclass="securityCodeCVV">
                                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                                        <dsp:tagAttribute name="aria-labelledby" value="lblsecurityCode errorsecurityCode"/>
                                                    </dsp:input>                                                    
                                                </div>
                                                <div class="whatsThis">
                                                    <span class="icon-fallback-text"> 
                                                       <a class="info icon icon-question-circle" id="cvvInfo"  aria-hidden="true">
                                                          <!-- <b><bbbl:label key="lbl_spc_payment_txt" language="<c:out param='${language}'/>"/></b> -->
                                                          <span><bbbl:label key="lbl_spc_payment_dynamicText" language="<c:out param='${language}'/>"/></span> 
                                                        </a>
                                                        <span class="icon-text"><bbbl:label key="lbl_spc_payment_dynamicText" language="<c:out param='${language}'/>"/></span>
                                                    </span>
                                                </div>
                                                <%--
                                                <div class="whatsThis">
                                                    <a class="info icon-question-circle">
                                                        <span><bbbl:label key="lbl_spc_payment_dynamicText" language="<c:out param='${language}'/>"/></span>
                                                    </a>
                                                </div>
                                                --%>
                                                <div class="cb">
                                                    <label id="errorsecurityCode" class="error" for="securityCode" generated="true"></label>
                                                    <label class="offScreen" for="securityCode"> <bbbl:label key="lbl_spc_payment_securityCode" language="<c:out param='${language}'/>"/> </label>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="expiryDate input text grid_6 alpha">
                                            
                                        </div>
                                        <div class="input_wrap grid_4 alpha marTop_20">
                                                <label id="lblnameCard" for="nameCard" class="popUpLbl"> <bbbl:label key="lbl_spc_payment_nameOnCard" language="<c:out param='${language}'/>"/> </label>
                                                <dsp:input bean="PaymentGroupFormHandler.creditCardInfo.nameOnCard" type="text" name="nameCard" maxlength="61" id="nameCard" >
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblnameCard errornameCard"/>
                                                    <dsp:tagAttribute name="class" value="grid_4 noMar omega" />
                                                </dsp:input>
                                        </div>
                                         <dsp:droplet name="Switch">
                                            <dsp:param name="value" bean="Profile.transient"/>
                                            <dsp:oparam name="false">
                                                <c:choose>
                                                    <c:when test="${internationalCCFlag ne 'true'}">
                                                        <div class="input grid_6 alpha saveToAccountWrapper">
                                                            <div class="checkbox fl">
                                                                <dsp:input bean="PaymentGroupFormHandler.saveProfileFlag" type="checkbox" name="saveToAccount" id="saveToAccount" checked="true">
                                                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                                                </dsp:input>
                                                            </div>
                                                            <div class="label">
                                                                <label id="lblsaveToAccount" for="saveToAccount"><bbbl:label key="lbl_spc_payment_saveToAct" language="<c:out param='${language}'/>"/></label>
                                                            </div>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="input grid_6 alpha hidden saveToAccountWrapper">
                                                            <div class="checkbox fl">
                                                                <dsp:input bean="PaymentGroupFormHandler.saveProfileFlag" type="checkbox" disabled="true" name="saveToAccount" id="saveToAccount" checked="true">
                                                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                                                </dsp:input>
                                                            </div>
                                                            <div class="label">
                                                                <label id="lblsaveToAccount" for="saveToAccount"><bbbl:label key="lbl_spc_payment_saveToAct" language="<c:out param='${language}'/>"/></label>
                                                            </div>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </fieldset>
                                    <div class="clear"></div>
                                </div>
                                <%--Add credit card form end--%>
                            </div>
                        </li>
                    </ul>
                </fieldset>
            </div>
            <%-- pay with a credit card grid end --%>
        </div>
    </div>
</dsp:page>
