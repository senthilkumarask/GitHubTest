<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<%-- Page Variables --%>
	<dsp:getvalueof bean="ShoppingCart.current.registryMap" var="registryMap"/>
	<dsp:getvalueof var="ltlCount" param="ltlCount" />
	<dsp:getvalueof var="count" param="count" />
	<c:if test="${upcGiftRegistryEnabled}">
	<div class="small-12 large-3 columns">
		<input type="checkbox" class="upc-gift-for-reg" <c:choose> <c:when test="${count eq ltlCount}"> disabled </c:when> <c:otherwise> enabled </c:otherwise> </c:choose> >
		<span></span>
		This is a gift for a Registrant
	</div>
	<div class="upc-dropdown-container hidden small-12 large-3 columns">
		<a href="#" data-dropdown="giftForReg_1" class="small secondary radius button dropdown expand">Select A Registry<span></span></a>
		<ul id="giftForReg_1" data-dropdown-content class="f-dropdown">
			<li><a href="#">SELECT A REGISTRY</a></li>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" bean="ShoppingCart.current.registryMap"/>
				<dsp:param name="elementName" value="registrymap"/>
				<dsp:oparam name="output">
					<dsp:getvalueof param="registrymap" var="registratantVO"/>
					<dsp:getvalueof param="key" var="currentRegistryId"/>
					<li<c:if test="${registryId eq currentRegistryId}"> class="selected"</c:if>>
						<a href="#" data-registry-id="${currentRegistryId}" data-cid="1" data-qty="1" class="upc-registrant-select">
							<c:out value="${registratantVO.primaryRegistrantFirstName}" /> <c:out value="${registratantVO.primaryRegistrantLastName}" />
						</a>
					</li>
				</dsp:oparam>
			</dsp:droplet>
			<li><a class="upc-search-for-reg" data-cid="1">SEARCH FOR A REGISTRY</a></li>
		</ul>
	</div> 

	<c:if test="${empty registryMap}">
		<c:set var="regMapEmpty" value="true" scope="request" />
	</c:if>
	<input type="hidden" id="regMapEmpty" value="<c:out value='${regMapEmpty}'/>" />
	</c:if>
</dsp:page>
