<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetCreditCardsForPayment"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/BBBCreditCardContainer"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<%-- Variables --%>
	<dsp:getvalueof var="billingAddress" param="order.billingAddress"/>
	<c:set var="year" scope="page">
		<bbbl:label key="lbl_addcreditcard_year" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="chooseCardType" scope="page">
		<bbbl:label key="lbl_choose_cardtype" language="${pageContext.request.locale.language}"/>
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
	<dsp:getvalueof var="istransient" bean="Profile.transient"/>
	<dsp:droplet name="GetCreditCardsForPayment">
		<dsp:param name="Profile" bean="Profile" />
		<dsp:param name="Order" bean="ShoppingCart.current" />
		<dsp:param name="CreditCardContainer" bean="BBBCreditCardContainer" />
		<dsp:param name="elementName" value="creditCardContainer" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="noSavedCards" bean="BBBCreditCardContainer.creditCardMap.empty" />
		</dsp:oparam>
	</dsp:droplet>

	<label class="inline-rc radio gray-panel credit-card new-credit-card-trigger <c:if test='${noSavedCards}'>hidden</c:if>" for="selectedCreditCardId">
		<dsp:input type="radio" value="New" bean="PaymentGroupFormHandler.selectedCreditCardId" name="selectedCreditCardId" id="selectedCreditCardId" checked="${noSavedCards}" />
		<span></span>
		<bbbl:label key="lbl_payment_newCard" language="<c:out param='${language}'/>"/>
	</label>

	<div class="row new-credit-card <c:if test='${!noSavedCards}'>hidden</c:if>">
		<div class="small-12 columns">
			<dsp:select id="cardType" name="cardType" iclass="CVVRulesFix hidden" bean="PaymentGroupFormHandler.creditCardInfo.creditCardType">
				<dsp:tagAttribute name="aria-required" value="false"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblcardType errorcardType"/>
				<dsp:option value="">${chooseCardType}</dsp:option>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" bean="PaymentGroupFormHandler.creditCardTypes" />
					<dsp:param name="elementName" value="cardlist" />
					<dsp:oparam name="output">
						<dsp:getvalueof id="cardName" param="cardlist.name" />
						<dsp:getvalueof id="cardCode" param="cardlist.code" />
						<dsp:option value="${cardCode}">
							<c:out value="${cardName}" />
						</dsp:option>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:select>
			<label id="errorcardType" for="cardType" generated="true" class="error"></label>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" bean="PaymentGroupFormHandler.creditCardTypes" />
				<dsp:param name="elementName" value="cardlist" />
				<dsp:oparam name="output">
					<dsp:getvalueof id="cardImage" param="cardlist.imageURL" />
					<dsp:getvalueof id="cardName" param="cardlist.name" />
					<img class="creditCardIcon" src="${imagePath}${cardImage}" width="41" height="26" alt="${cardName}">
				</dsp:oparam>
			</dsp:droplet>
		</div>
		<div class="small-12 large-8 columns">
			<c:set var="placeholderText"><bbbl:label key="lbl_payment_cardNumber" language="<c:out param='${language}'/>"/></c:set>
			<dsp:input bean="PaymentGroupFormHandler.creditCardInfo.creditCardNumber" type="text" name="cardNumber" id="cardNumber" maxlength="16" autocomplete="off" value="">
					<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
					<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblcardNumber errorcardNumber"/>
			</dsp:input>
		</div>
		<div class="small-12 large-4 columns">
			<c:set var="placeholderText">
				CVV
				<%--<bbbl:label key="lbl_payment_securityCode" language="<c:out param='${language}'/>"/>--%>
			</c:set>
			<dsp:input bean="PaymentGroupFormHandler.creditCardInfo.cardVerificationNumber" type="text" name="securityCode" id="securityCode" maxlength="4" autocomplete="off" iclass="securityCodeCVV" value="">
				<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
				<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblsecurityCode errorsecurityCode"/>
			</dsp:input>
			<a href="#" data-reveal-id="cvvInfoModal" class="cvc-info">
				<bbbl:label key="lbl_payment_txt" language="<c:out param='${language}'/>"/>
			</a>
			<label id="errorsecurityCode" class="error" for="securityCode" generated="true"></label>
		</div>
		<div class="small-12 large-4 columns">
			<label for="creditCardMonth" class="left inline small-only-no-margin-bottom medium-only-no-margin-bottom"><bbbl:label key="lbl_payment_expiresOn" language="<c:out param='${language}'/>"/></label>
		</div>
		<div class="small-12 large-4 columns">
			<dsp:select bean="PaymentGroupFormHandler.creditCardInfo.expirationMonth" id="creditCardMonth" name="creditCardMonth" iclass="uniform">
				<dsp:tagAttribute name="aria-required" value="false"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblcreditCardMonth errorcreditCardMonth"/>
				<dsp:option value="">Month</dsp:option>
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
		<div class="small-12 large-4 columns">
			<jsp:useBean id="now" class="java.util.Date" scope="request" />
			<fmt:formatDate var="year_val" value="${now}" pattern="yyyy" />
			<dsp:select bean="PaymentGroupFormHandler.creditCardInfo.expirationYear" id="creditCardYear" name="creditCardYear" iclass="uniform">
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
		</div>
		<div class="small-12 columns">
			<c:set var="placeholderText"><bbbl:label key="lbl_payment_nameOnCard" language="<c:out param='${language}'/>"/></c:set>
			<dsp:input bean="PaymentGroupFormHandler.creditCardInfo.nameOnCard" type="text" name="nameCard" maxlength="61" id="nameCard" >
				<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
				<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblnameCard errornameCard"/>
			</dsp:input>
			<a href="#" class="name-on-card"><dsp:valueof param="order.billingAddress.firstName" /> <dsp:valueof param="order.billingAddress.lastName" /></a>
		</div>
		<div class="small-12 columns saveToAccountWrapper">
			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="false">
					<c:choose>
						<c:when test="${internationalCCFlag ne 'true'}">
							<label class="inline-rc checkbox" for="savePayToAccount">
								<dsp:input bean="PaymentGroupFormHandler.saveProfileFlag" type="checkbox" name="savePayToAccount" id="savePayToAccount" checked="true">
									<dsp:tagAttribute name="aria-checked" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
								</dsp:input>
								<span></span>
								<bbbl:label key="lbl_payment_saveToAct" language="<c:out param='${language}'/>"/>
							</label>
						</c:when>
						<c:otherwise>
							<label class="inline-rc checkbox hidden" for="savePayToAccount">
								<dsp:input bean="PaymentGroupFormHandler.saveProfileFlag" type="checkbox" name="savePayToAccount" id="savePayToAccount" checked="true" disabled="true">
									<dsp:tagAttribute name="aria-checked" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
								</dsp:input>
								<span></span>
								<bbbl:label key="lbl_payment_saveToAct" language="<c:out param='${language}'/>"/>
							</label>
						</c:otherwise>
					</c:choose>
				</dsp:oparam>
			</dsp:droplet>
		</div>
	</div>

</dsp:page>
