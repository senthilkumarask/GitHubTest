<dsp:page>
	<bbb:pageContainer index="false" follow="false" >
		<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
		<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
		<dsp:importbean bean="/atg/userprofiling/Profile"/>
		<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
		
		<b><bbbl:label key="lbl_header_creditcards" language="${pageContext.request.locale.language}" />:</b>
				<p align='left'>Lorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem Ipsum<br/>
				Lorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem Ipsum<br/>
				Lorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem IpsumLorem Ipsum<br/><br/><br/>
				<a href='add_credit_card.jsp'><bbbl:label key="lbl_creditcardinfo_addcreditcard" language="${pageContext.request.locale.language}" /></a>
				<br/><br/><br/>
				</p>
		<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
		<dsp:getvalueof id="cancelURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
		
		<%-- Iterate through all this user's credit cards, sorting the array so that the
		     default credit card is first. --%>
		<dsp:droplet name="MapToArrayDefaultFirst">
			<dsp:param name="defaultId" bean="Profile.defaultCreditCard.repositoryId"/>
			<dsp:param name="sortByKeys" value="true"/>
			<%-- Profile <dsp:valueof bean="Profile.creditCards"/> --%>
			<dsp:param name="map" bean="Profile.creditCards"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/> 
				<c:choose>
					<c:when test="${not empty sortedArray}">
						<div id="atg_store_storedCreditCards"><table border='1' width='600'><tr>
							<c:forEach var="element" items="${sortedArray}"><td>
								<dsp:setvalue param="creditCard" value="${element}"/>
								<dsp:getvalueof var="creditCard" param="creditCard"/>
								<c:if test="${not empty creditCard}">
									<c:set var="count" value="0"/>
									<c:set var="numberLinks" value="2"/>
									<dsp:droplet name="Compare">
									<dsp:param name="obj" bean="Profile.defaultCreditCard"/>
										<dsp:param name="obj1" bean="Profile.defaultCreditCard.repositoryId"/>
										<dsp:param name="obj2" param="creditCard.value.id"/>
										<dsp:oparam name="equal">
											<c:set var="isDefault" value="true"/>
										</dsp:oparam>
										<dsp:oparam name="default">
											<c:set var="isDefault" value="false"/>
										</dsp:oparam>
									</dsp:droplet>
									<div class="atg_store_paymentInfoGroup${isDefault ? ' atg_store_paymentInfoGroupDefault' : ''}">
										<c:set var="counter" value="${counter + 1}"/>
										<%-- Show Default Label if it is the default value --%>
										<c:choose>
										<c:when test="${isDefault}">
										<dt><b>Preferred Card</b></dt>
										<%-- <dt class="atg_store_defaultCreditCard">
										  <dsp:valueof param="creditCard.key"/>
										<dsp:a page="/myaccount/profileDefaults.jsp" title="${defaultAddressTitle}">
										<span><fmt:message key="common.default"/></span>
										</dsp:a>
										</dt> --%>
										</c:when>
										<c:otherwise>
											<dsp:form>
													<dsp:param name="creditCardNumber" param="creditCard.key"/>
													<dsp:getvalueof var="ccName" param="creditCardNumber"/>
													<dsp:input bean="ProfileFormHandler.value.defaultCreditCard" type="hidden" value="${ccName}"/>
													<dsp:input type="submit" value="Make Preferred Card" bean="ProfileFormHandler.checkoutDefaults">
                                                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                        <dsp:tagAttribute name="role" value="button"/>
                                                    </dsp:input>
											</dsp:form>
											<%-- 
										<dsp:valueof param="creditCard.key"/>
										<dsp:valueof param="creditCard.value.creditCardType"/>ending in
										<c:out value="${fn:substring(creditCard,fn:length(creditCard)-4,fn:length(creditCard))}"/> --%>
										</c:otherwise>
										</c:choose>
										
										<%-- <dsp:valueof param="creditCard.key"/> --%>
										<b><dsp:valueof param="creditCard.value.creditCardType"/></b> <bbbl:label key="lbl_creditcardinfo_endingwith" language="${pageContext.request.locale.language}" />: 
										<dsp:getvalueof var="creditCard" param="creditCard.value.creditCardNumber"/>
										<b><c:out value="${fn:substring(creditCard,fn:length(creditCard)-4,fn:length(creditCard))}"/></b>
										<%-- Display Credit Card Details --%>
										<%-- <dt>card Number -<dsp:valueof param="creditCard.value.creditCardNumber"/></dt>
										<dt>card Type -<dsp:valueof param="creditCard.value.creditCardType"/></dt> --%>
										<%-- <dt>card Key -<dsp:valueof param="creditCard.key"/></dt> --%> 
										<dt><bbbl:label key="lbl_creditcardinfo_nameoncard" language="${pageContext.request.locale.language}" />: <dsp:valueof param="creditCard.value.nameOnCard"/></dt>
										<dt><bbbl:label key="lbl_creditcardinfo_exp" language="${pageContext.request.locale.language}" />: <dsp:valueof param="creditCard.value.expirationMonth"/>/<dsp:valueof param="creditCard.value.expirationYear"/>
										<%-- Address -<dsp:valueof param="creditCard.value.billingAddress"/> --%>
										
										<%-- Display Edit/Remove/MakeDefault Links --%>
										<c:set var="count" value="${count + 1}"/>
										<dsp:a bean="ProfileFormHandler.editCard" page="../#accountCardEdit.jsp"
										     paramvalue="creditCard.key" title="${editCardTitle}">
											<dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
											<dsp:param name="cancelURL" value="${cancelURL}?preFillValues=false"/>
											<span><dt><bbbl:label key="lbl_registry_store_edit" language="${pageContext.request.locale.language}" /></dt></span>
										</dsp:a>
										<c:set var="count" value="${count + 1}"/>
										<dsp:a bean="ProfileFormHandler.removeCard" href="${requestURL}"
										paramvalue="creditCard.key" title="${removeCardTitle}">
											<span><dt><bbbl:label key="lbl_addressbook_remove" language="${pageContext.request.locale.language}" /></dt></span>
										</dsp:a>
					               </div>
				             </c:if></td>
							</c:forEach>
							</tr></table>
						</div>
					</c:when>
				</c:choose>
			</dsp:oparam>
		</dsp:droplet> <%-- MapToArrayDefaultFirst (sort saved credit cards) --%>
	</bbb:pageContainer>
</dsp:page>
