<dsp:page>
<c:set var="pageWrapper" value="creditCard myAccount" scope="request" />
<bbb:pageContainer section="accounts" bodyClass="${themeName}" index="false" follow="false">
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
	<c:set var="view_credit_card_title" scope="page">
		<bbbl:label key="lbl_creditcardinfo_creditcardstitle" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="add_credit_card" scope="page">
		<bbbl:label key="lbl_creditcardinfo_addcreditcard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="make_preferred" scope="page">
		<bbbl:label key="lbl_creditcardinfo_makepreferredcreditcard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="preferred_card" scope="page">
		<bbbl:label key="lbl_creditcardinfo_preferredcreditcard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="name_on_card" scope="page">
		<bbbl:label key="lbl_creditcardinfo_nameoncard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="edit_card_title" scope="page">
		<bbbl:label key="lbl_creditcardinfo_edit" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="remove_card_title" scope="page">
		<bbbl:label key="lbl_creditcardinfo_remove" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="exp" scope="page">
		<bbbl:label key="lbl_creditcardinfo_exp" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="ending_with" scope="page">
		<bbbl:label key="lbl_creditcardinfo_endingwith" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="remove_card_heading" scope="page">
		<bbbl:label key="lbl_creditcardinfo_remove_heading" language="${pageContext.request.locale.language}" />
	</c:set>
		
	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<dsp:getvalueof id="cancelURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	
	<div id="content" class="container_12 clearfix creditCard" role="main">
		<div class="grid_12">
			<h1 class="account fl"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/></h1>
			<h3 class="subtitle"><bbbl:label key="lbl_header_creditcards" language="${pageContext.request.locale.language}" /></h3>
            <div class="clear"></div>
		</div>
		<div class="grid_2">
			<c:import url="/account/left_nav.jsp">
				 <c:param name="currentPage"><bbbl:label key="lbl_myaccount_credit_cards" language ="${pageContext.request.locale.language}"/></c:param>
			</c:import>
		</div>
	<div class="grid_8 prefix_1 suffix_1">
		<div class="suffix_2 descWishList"><p>${view_credit_card_title}</p>
		<p class="txtHighlight" ><a href='add_credit_card.jsp' title="${add_credit_card}">+ ${add_credit_card}</a></p>
		</div>
        <c:set var="hasCards" value="${false}"/>

		<%-- Iterate through all this user's credit cards, sorting the array so that the  default credit card is first. --%>
		<dsp:droplet name="MapToArrayDefaultFirst">
			<dsp:param name="defaultId" bean="Profile.defaultCreditCard.repositoryId"/>
			<dsp:param name="sortByKeys" value="true"/>
			<%-- Profile <dsp:valueof bean="Profile.creditCards"/> --%>
			<dsp:param name="map" bean="Profile.creditCards"/>
            <dsp:oparam name="output">
                <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/> 
                <c:choose>
                    <c:when test="${not empty sortedArray}">
                        <div class="cardView clearfix">
                            <c:set var="count" value="0"/>
                            <c:forEach var="element" items="${sortedArray}" varStatus="cardIndexCount">
                                <dsp:setvalue param="creditCard" value="${element}"/>
                                <dsp:getvalueof var="creditCard" param="creditCard"/>
                                <c:if test="${not empty creditCard}">
                                    <c:set var="hasCards" value="${true}"/>
                                    <c:set var="divClass" value="alpha" />
                                    <c:if test="${count % 2 != 0}"><c:set var="divClass" value="omega" /></c:if>
                                    <div class="creditCardWrapper grid_4 ${divClass}">
                                        <c:set var="numberLinks" value="2"/>
                                        <%-- compares whether the credit-card id and the default profile credit-card id are EQUAL or not. if equal sets isDefault to true else to false--%>
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
                                        
                                        <%-- Show Default Label if it is the default value --%>
                                        
                                            <c:choose>
                                                <c:when test="${isDefault}">
                                                    <div class="cardEntry isPreferredShipping">
                                                        <div class="item cardTitle"><strong>${preferred_card}</strong></div>
                                                        <hr/>
                                                        <dsp:form method="post" action="edit_credit_cards.jsp">
                                                                <dsp:input type="hidden" value="/store/account/edit_credit_cards.jsp" bean="ProfileFormHandler.updateCardSuccessURL" />
                                                                <dsp:input type="hidden" beanvalue="/OriginatingRequest.requestURI" bean="ProfileFormHandler.updateCardErrorURL" />
                                                                <dsp:input type="submit" id="editCardSubmit${cardIndexCount.count}" iclass="hidden" paramvalue="creditCard.key" bean="ProfileFormHandler.editCard" />
                                                        </dsp:form>
                                                        <dsp:form method="post" action="view_credit_card.jsp">
                                                            <div class="item"><dsp:valueof param="creditCard.value.creditCardType" valueishtml="false"/> ${ending_with}: <dsp:getvalueof var="creditCard" param="creditCard.value.creditCardNumber"/><c:out value="${fn:substring(creditCard,fn:length(creditCard)-4,fn:length(creditCard))}"/><br/>
                                                                <c:out value="${name_on_card}" />: <dsp:valueof param="creditCard.value.nameOnCard" valueishtml="false"/><br/>
                                                                ${exp}: <dsp:valueof param="creditCard.value.expirationMonth" converter="number" format="00" valueishtml="false"/>/<dsp:valueof param="creditCard.value.expirationYear" valueishtml="false"/><br/>
                                                                
                                                                </div>
                                                                <div class="item"><a href="#" class="triggerSubmit" data-submit-button="editCardSubmit${cardIndexCount.count}" alt="${edit_card_title}">${edit_card_title}</a>
                                                            </div>		
                                                            <div class="item">
                                                                <dsp:a bean="ProfileFormHandler.removeCard" onclick="javascript:acctUpdate('removed saved card');" iclass="removeCardEntry hidden" href="remove_card_json.jsp" paramvalue="creditCard.key" title="${remove_card_title}">
                                                                ${remove_card_title}
                                                                </dsp:a>
                                                                <a href="#" class="removeCardEntry2" title="${remove_card_title}">${remove_card_title}</a>
                                                                <%-- <dsp:input bean="ProfileFormHandler.pResponse.getCreateCardSuccessURL" type="hidden" value="${contextPath}/account/remove_card_json.jsp"/> --%>
                                                            </div>
                                                        </dsp:form>
                                                    </div>
                                                    
                                                </c:when>
                                                <c:otherwise>
                                                     <%-- <a href="#" onclick="document.getElementById('<c:out value="${make_preferred}"/>').click()">${make_preferred}</a>
                                                    <dsp:input type="submit" style="display:none" value="${make_preferred}" id="${make_preferred}"  bean="ProfileFormHandler.checkoutDefaults"/> --%>
                                                    <div class="cardEntry">
                                                        <div class="item cardTitle">
                                                            <dsp:form method="post" action="view_credit_card.jsp">
                                                                <dsp:param name="creditCardNumber" param="creditCard.key"/>
                                                                <dsp:getvalueof var="ccName" param="creditCardNumber"/>
                                                                <dsp:input bean="ProfileFormHandler.value.defaultCreditCard" type="hidden" value="${ccName}"/>
                                                                <dsp:input type="submit" id="cardSubmit${cardIndexCount.count}" iclass="hidden" value="${make_preferred}" bean="ProfileFormHandler.checkoutDefaults" />
                                                            </dsp:form>
                                                            <a href="#" class="triggerSubmit" onclick="javascript:acctUpdate('preferred card updated');" data-submit-button="cardSubmit${cardIndexCount.count}" alt="${make_preferred}">${make_preferred}</a>
                                                        </div><hr/>
                                                        <dsp:form method="post" action="edit_credit_cards.jsp">
                                                                <dsp:input type="hidden" value="/store/account/edit_credit_cards.jsp" bean="ProfileFormHandler.updateCardSuccessURL" />
                                                                <dsp:input type="hidden" beanvalue="/OriginatingRequest.requestURI" bean="ProfileFormHandler.updateCardErrorURL" />
                                                                <dsp:input type="submit" id="editCardSubmit${cardIndexCount.count}" iclass="hidden" paramvalue="creditCard.key" bean="ProfileFormHandler.editCard" />
                                                        </dsp:form>
                                                        <dsp:form method="post" action="view_credit_card.jsp">
                                                            <div class="item"><dsp:valueof param="creditCard.value.creditCardType"/> ${ending_with}: <dsp:getvalueof var="creditCard" param="creditCard.value.creditCardNumber"/><c:out value="${fn:substring(creditCard,fn:length(creditCard)-4,fn:length(creditCard))}"/><br/>
                                                                <c:out value="${name_on_card}" />: <dsp:valueof param="creditCard.value.nameOnCard" valueishtml="false"/><br/>
                                                                ${exp}: <dsp:valueof param="creditCard.value.expirationMonth" converter="number" format="00" valueishtml="false"/>/<dsp:valueof param="creditCard.value.expirationYear" valueishtml="false"/><br/>
                                                                
                                                                </div>
                                                                <div class="item"><a href="#" class="triggerSubmit" data-submit-button="editCardSubmit${cardIndexCount.count}" alt="${edit_card_title}">${edit_card_title}</a>
                                                            </div>		
                                                            <div class="item">
                                                                <dsp:a bean="ProfileFormHandler.removeCard" onclick="javascript:acctUpdate('removed saved card');"  iclass="removeCardEntry hidden" href="remove_card_json.jsp" paramvalue="creditCard.key" title="${remove_card_title}">
                                                                ${remove_card_title}
                                                                </dsp:a>
                                                                <a href="#" class="removeCardEntry2" title="${remove_card_title}">${remove_card_title}</a>
                                                                <%-- <dsp:input bean="ProfileFormHandler.pResponse.getCreateCardSuccessURL" type="hidden" value="${contextPath}/account/remove_card_json.jsp"/> --%>
                                                            </div>
                                                        </dsp:form>
                                                    </div>	
                                                </c:otherwise>
                                            </c:choose>
                                    
                                        <%-- <dsp:valueof param="creditCard.key"/> --%>
                                    </div>
                                    <c:if test="${count % 2 != 0}"><div class="clear"></div></c:if>
                                    <c:set var="count" value="${count + 1}"/>
                                </c:if>
                            </c:forEach>
                        </div>
                        <c:if test="${hasCards}">
                            <div class="suffix_2">
                                <p class="txtHighlight"><a class="addCardEntry" title="${add_credit_card}" href="add_credit_card.jsp">+ ${add_credit_card}</a></p>
                            </div>
                        </c:if>
					</c:when>
				</c:choose>
			</dsp:oparam>
		</dsp:droplet> 

	<%-- MapToArrayDefaultFirst (sort saved credit cards) ends--%>

</div>


<div id="removeCardWarningDialog" title="${remove_card_heading}" class="hidden">
<bbbl:label key="lbl_creditcardinfo_remove_description" language="${pageContext.request.locale.language}" />
</div>
</div>
<div class="clear"></div>
<jsp:attribute name="footerContent">
<script type="text/javascript">
	if (typeof s !== 'undefined') {
	s.pageName ='My Account>My Credit Cards';
	s.channel = 'My Account';
	s.prop1 = 'My Account';
	s.prop2 = 'My Account';
	s.prop3 = 'My Account';
	s.prop6='${pageContext.request.serverName}'; 
	s.eVar9='${pageContext.request.serverName}';
	var s_code = s.t();
	if (s_code)
		document.write(s_code);
	}
</script>	
</jsp:attribute>
</bbb:pageContainer>
</dsp:page>