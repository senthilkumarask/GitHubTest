<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryPaginationDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSCommerceItemLookupDroplet"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	
	<dsp:getvalueof param="commerceId" var="commerceId"/>
	
	<c:if test="${empty commerceId}">
		<c:set var="qty" value="1"/>
	</c:if>

	<%-- Page Variables --%>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
			<c:set var="pageVariation" value="bb" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="by" scope="request" />
		</c:otherwise>
	</c:choose>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${appid eq 'TBS_BuyBuyBaby'}">
			<c:set var="gridValue" value="1" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="gridValue" value="2" scope="request" />
		</c:otherwise>
	</c:choose>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

	<dsp:getvalueof var="perPage" param="pagFilterOpt" scope="request" />
	<dsp:getvalueof var="pagNum" param="pagNum" scope="request" />
	<dsp:getvalueof var="sessFirstName" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.firstName" scope="request" />
	<dsp:getvalueof var="sessLastName" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.lastName" scope="request" />
	<dsp:getvalueof var="sessRegistryId" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.registryId" scope="request" />
	<dsp:getvalueof var="sessState" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.state" scope="request" />
	<dsp:getvalueof var="sessEventType" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.event" scope="request" />
	<dsp:getvalueof var="sessSort" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.sort" />
	<dsp:getvalueof var="sessOrder" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.sortSeqOrder"/>
	<dsp:getvalueof var="sessSortOrder" value="${sessSort}${sessOrder}" scope="request" />
	
	
	<c:if test="${empty perPage}">
		<c:set var="perPage" value="10" scope="request" />
	</c:if>
	<c:if test="${empty pagNum}">
		<c:set var="pagNum" value="1" scope="request" />
	</c:if>
	<dsp:getvalueof var="tabNo" param="tabNo" />
	<c:if test="${ empty tabNo}">
		<c:set var="tabNo" value="1" />
	</c:if>
	<dsp:getvalueof var="previousPage" param="previousPage" />
	<dsp:getvalueof var="sortBy" param="sortPassString" scope="request" />
	<dsp:getvalueof var="sortOrder" param="sortOrder" scope="request" />
	
	
	<c:choose>
		<c:when test="${sessSort eq 'NAME'}">
			<c:set var="sortOrderName" value="${sessOrder}" scope="request" />
		</c:when>
		<c:when test="${sessSort eq 'EVENTTYPEDESC'}">
			<c:set var="sortOrderType" value="${sessOrder}" scope="request" />
		</c:when>
		<c:when test="${sessSort eq 'DATE'}">
			<c:set var="sortOrderDate" value="${sessOrder}" scope="request" />
		</c:when>
		<c:when test="${sessSort eq 'STATE'}">
			<c:set var="sortOrderState" value="${sessOrder}" scope="request" />
		</c:when>
		<c:when test="${sessSort eq 'REGISTRYNUM'}">
			<c:set var="sortOrderRegNum" value="${sessOrder}" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="sortOrderName" value="ASCE" scope="request" />
		</c:otherwise>
	</c:choose>

	<%-- Header --%>
	<div class="row">
		<div class="small-12 columns no-padding">
			<h1>Search for a Registry</h1>
		</div>
	</div>

	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" param="search" />
		<dsp:oparam name="false">

			<script type="text/javascript">
				var totalCount = '0';
			</script>

			<%-- Errors --%>
			<dsp:include page="/global/gadgets/errorMessage.jsp">
				<dsp:param name="formhandler" bean="CartModifierFormHandler"/>
			</dsp:include>

			<%-- Registry Search Results --%>
			<dsp:droplet name="GiftRegistryPaginationDroplet">
				<dsp:param name="pageNo" value="${pagNum}" />
				<dsp:param name="perPage" value="${perPage}" />
				<dsp:param name="tabNo" value="${tabNo}" />
				<dsp:param name="previousPage" value="${previousPage}" />
				<dsp:param name="sortOrder" value="${sortOrder}" />
				<dsp:param name="siteId" value="${appid}" />
				<dsp:oparam name="output">

					<%-- Registry Search Form --%>
					<dsp:include page="registry_search_form.jsp">
						<dsp:param name="showOnlySearchFields" value="false"  />
						<dsp:param name="formId" value="1"  />
						<dsp:param name="commerceId" param="commerceId"/>
					</dsp:include>

					<dsp:droplet name="IsEmpty">
						<dsp:param param="registrySummaryResultList" name="value" />
						<dsp:oparam name="false">

							<dsp:getvalueof var="totalCount" param="totalCount" scope="request" />
							<dsp:getvalueof var="registrySummaryResultList" param="registrySummaryResultList" />
							<dsp:getvalueof var="currentResultSize" value="${fn:length(registrySummaryResultList)}" scope="request" />

							<c:set var="totalTabFlot" value="${totalCount/perPage}" />
							<c:set var="totalTab"  value="${totalTabFlot+(1-(totalTabFlot%1))%1}" scope="request" />

							<dsp:form id="registrantSelectForm" method="post" action="registry_search.jsp">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="registrySummaryResultList" />
									<dsp:oparam name="outputStart">

										<div class="row search-results">
											<div class="small-12 columns">

												<%-- Search Pagination --%>
												<dsp:include page="registry_search_pagination.jsp">
													<dsp:param name="currentResultSize" value="${currentResultSize}" />
													<dsp:param name="totalCount" value="${totalCount}" />
													<dsp:param name="perPage" value="${perPage}" />
													<dsp:param name="totalTab" value="${totalTab}" />
												</dsp:include>

												<div class="row column-header hide-for-medium-down">
													<div class="small-11 columns">
														<div class="row">
															<div class="small-12 large-1 columns">
																<h3> </h3>
															</div>
															<div class="small-12 large-3 columns">
																<a href="#" class="sortByFilter ${fn:toLowerCase(sortOrderName)}" data-sort-by-type="NAME" data-sort-order="${sortOrderName}">
																	<h3>
																		<bbbl:label key="lbl_regsrchguest_Registrants" language="${pageContext.request.locale.language}" />
																		<span></span>
																	</h3>
																</a>
															</div>

															<div class="small-12 large-2 columns">
																<a href="#" class="sortByFilter ${fn:toLowerCase(sortOrderType)}" data-sort-by-type="EVENTTYPEDESC" data-sort-order="${sortOrderType}"><h3><bbbl:label key="lbl_regsrchguest_EventType" language="${pageContext.request.locale.language}" />&nbsp;<span></span></h3></a>
															</div>
															<div class="small-12 large-2 columns">
																<a href="#" class="sortByFilter ${fn:toLowerCase(sortOrderDate)}" data-sort-by-type="DATE" data-sort-order="${sortOrderDate}"><h3>Date&nbsp;<span></span></h3></a>
															</div>
															<div class="small-12 large-2 columns">
																<a href="#" class="sortByFilter ${fn:toLowerCase(sortOrderState)}" data-sort-by-type="STATE" data-sort-order="${sortOrderState}">
																	<h3>
																		<c:choose>
																			<c:when test="${appid == 'TBS_BedBathCanada'}">
																				<bbbl:label key="lbl_registrants_statecanada" language="${pageContext.request.locale.language}" />
																			</c:when>
																			<c:otherwise>
																				<bbbl:label key="lbl_regsrchguest_State" language="${pageContext.request.locale.language}" />
																			</c:otherwise>
																		</c:choose>
																		<span></span>
																	</h3>
																</a>
															</div>
															<div class="small-12 large-2 columns">
																<a href="#" class="sortByFilter ${fn:toLowerCase(sortOrderRegNum)}" data-sort-by-type="REGISTRYNUM" data-sort-order="${sortOrderRegNum}">
																	<h3>
																		<%--<bbbl:label key="lbl_regsrchguest_Registry" language="${pageContext.request.locale.language}" />--%>
																		Registry #
																		<span></span>
																	</h3>
																</a>
															</div>
														</div>
													</div>
												</div>
												<div class="row results">
													<div class="small-12 columns">
									</dsp:oparam>
									<dsp:oparam name="output">
										<dsp:getvalueof var="regId" param="element.registryId" />
										<c:set var="currentCount">
											<dsp:valueof param="count" />
										</c:set>
										<dsp:getvalueof var="eventType" param="element.eventType" />
										<dsp:getvalueof var="pRMaidenName" param="element.primaryRegistrantMaidenName" />
										<dsp:getvalueof var="cRMaidenName"param="element.coRegistrantMaidenName" />
										<dsp:getvalueof var="pwsurl" param="element.pwsurl" />
										<dsp:getvalueof var="pwsToken" param="element.personalWebsiteToken" />

										<div class="row">
										<div class="small-1 reg-radio columns">
												<label class="inline-rc radio">
													<dsp:input bean="CartModifierFormHandler.registryId" type="radio" id="regSearchRadio_${regId}" name="regSearchRadio" value="${regId}"/>
													<span></span>
												</label>
											</div>
											<div class="small-11 reg-data columns">
												<div class="row">
													<div class="small-12 large-3 columns no-padding-left">
														<dsp:valueof param="element.primaryRegistrantFirstName" />
														<dsp:getvalueof var="coRegName" param="element.coRegistrantFirstName"/>
														<c:if test="${not empty coRegName}">	
															&amp; <dsp:valueof param="element.coRegistrantFirstName" />										
														</c:if>
														<c:if test="${not empty eventType && eventType eq 'Wedding' && not empty pwsurl && not empty pwsToken}">
															<c:set var="personalSite">
																${contextPath}<bbbc:config key="personal_site_url" configName="ThirdPartyURLs" />${pwsToken}&registryId=${regId}
															</c:set>
															<a title="Personal Wedding Website" href="${personalSite}" id="personalSite" target="personalSite">
																<bbbl:label key='lbl_mng_regitem_pwedsite' language="${pageContext.request.locale.language}" />
															</a>
														</c:if>
														<c:if test="${appid eq 'TBS_BuyBuyBaby'}">
															<dsp:droplet name="IsEmpty">
																<dsp:getvalueof param="element.primaryRegistrantMaidenName" id="pMaidenName">
																	<dsp:param value="${pMaidenName}" name="value" />
																</dsp:getvalueof>
																<dsp:oparam name="false">
																	<c:set var="pRegMaidenName">
																		<dsp:valueof param="element.primaryRegistrantMaidenName" />
																	</c:set>
																	<c:if test="${not empty pRegMaidenName}">
																		<dsp:valueof param="element.primaryRegistrantMaidenName" />
																	</c:if>
																</dsp:oparam>
																<dsp:oparam name="true">-</dsp:oparam>
															</dsp:droplet>
														</c:if> 
													</div>
													<div class="small-12 large-2 columns no-padding-left">
														<dsp:valueof param="element.eventType" />
													</div>
													<div class="small-12 large-2 columns no-padding-left">
														<c:choose>
															<c:when test="${appid eq 'TBS_BedBathCanada'}">
																<dsp:valueof param="element.eventDateCanada" />
															</c:when>
															<c:otherwise>
																<dsp:valueof param="element.eventDate" />
															</c:otherwise>
														</c:choose>
													</div>
													<div class="small-12 large-2 columns no-padding-left">
														<dsp:valueof param="element.state" />
													</div>
													<div class="small-12 large-3 columns no-padding-left">
														<dsp:valueof param="element.registryId" />
													</div>
												</div>
											</div>
											
										</div>
									</dsp:oparam>
									<dsp:oparam name="outputEnd">
														<dsp:getvalueof param="commerceId" var="cId"/>
														<dsp:droplet name="TBSCommerceItemLookupDroplet">
															<dsp:param name="id" value="${cId}"/>
															<dsp:param name="order" bean="ShoppingCart.current"/>
															<dsp:param name="elementName" value="cItem"/>
															<dsp:oparam name="output">
																<dsp:getvalueof param="cItem.quantity" var="qty" scope="request"/>
															</dsp:oparam>
														</dsp:droplet>
													</div>
												</div>
												<div class="row">
													<div class="small-12 columns">
														<div class="row">
														
															<c:choose>
																<c:when test="${qty == 1 && empty commerceId}">
																	 <%-- <div class="small-6 columns">
																		<label class="inline-rc checkbox" for="applyToAllItems">
																			<dsp:input bean="CartModifierFormHandler.allItemsForRegistry" type="checkbox" id="applyToAllItems" />
																			<span></span>
																			Apply to all items in cart.
																		</label>
																	</div>  --%>
																	<div class="small-6 columns">
																		<a class="button tiny service expand apply-to-upcregistry single-item">apply</a>
																	</div>
																	<%-- dsp:input bean="CartModifierFormHandler.quantity" type="hidden" value="1"/>
																	<dsp:input bean="CartModifierFormHandler.commerceItemId" type="hidden" value="${cId}"/>
																	<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistry" iclass="hidden apply-to-registry-submit" value="Assign" type="submit"/>
																	<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistrySuccessUrl" value="${contextPath}/cart/cart.jsp" type="hidden"/>
																	<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistryErrorUrl" value="${contextPath}/cart/cart.jsp" type="hidden"/> --%>
																</c:when>
																<c:when test="${qty == 1 && not empty commerceId}">
																	<div class="small-6 columns left">
																		<label class="inline-rc checkbox" for="applyToAllItems">
																			<dsp:input bean="CartModifierFormHandler.allItemsForRegistry" type="checkbox" id="applyToAllItems" />
																			<span></span>
																			All items in cart are gifts for this Registrant.
																		</label>
																	</div>
																	<div class="small-3 columns right">
																		<a class="button tiny service expand apply-to-registry single-item">Select</a>
																	</div>
																	<dsp:input bean="CartModifierFormHandler.quantity" type="hidden" value="1"/>
																	<dsp:input bean="CartModifierFormHandler.commerceItemId" type="hidden" value="${cId}"/>
																	<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistry" iclass="hidden apply-to-registry-submit" value="Assign" type="submit"/>
																	<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden" value="registrySearch" />
																	<%-- <dsp:input bean="CartModifierFormHandler.splitAndAssignRegistrySuccessUrl" value="${contextPath}/cart/cart.jsp" type="hidden"/>
																	<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistryErrorUrl" value="${contextPath}/cart/cart.jsp" type="hidden"/> --%>
																</c:when>
															</c:choose>
															<c:if test="${qty > 1}">
																<div class="small-6 columns">
																	<label class="inline-rc checkbox" for="applyToAllItems">
																		<dsp:input bean="CartModifierFormHandler.allItemsForRegistry" type="checkbox" id="applyToAllItems" />
																		<span></span>
																		Apply to all items in cart.
																	</label>
																</div>
																<div class="small-6 columns">
																	<a class="button tiny service expand apply-to-registry">apply</a>
																</div>
																<dsp:input bean="CartModifierFormHandler.moveNext" type="submit" iclass="hidden apply-to-registry-submit" value="select"/>
																<dsp:input bean="CartModifierFormHandler.itemToRegister" type="hidden" value="${cId}"/>
																<dsp:input bean="CartModifierFormHandler.successQueryParam"
												type="hidden"
												value="commerceId=${cId}" />
												<dsp:input bean="CartModifierFormHandler.errorQueryParam"
												type="hidden"
												value="commerceId=${cId}" />
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="regSearch" />
																<%-- <dsp:input bean="CartModifierFormHandler.moveNextSuccessUrl" type="hidden" id="moveNextSuccessUrl" value="${contextPath}/giftregistry/registry_qty.jsp?commerceId=${cId}&registryId="/>
																<dsp:input bean="CartModifierFormHandler.moveNextErrorUrl" type="hidden" value="${contextPath}/giftregistry/registry_search.jsp?commerceId=${cId}"/> --%>
															</c:if>
														</div>
													</div>
												</div>
											</div>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:form>
						</dsp:oparam>
						<dsp:oparam name="true">
							<div class="row">
								<div class="small-12 columns">
									<p class="error">
										<bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" />
									</p>
								</div>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
				<dsp:oparam name="error">
					<dsp:getvalueof var="errorMsg" param="errorMsg" />
					<div class="row">
						<div id="registryFilterFormWrap" class="small-12 columns">
							<dsp:include page="registry_search_form.jsp">
								<dsp:param name="showOnlySearchFields" value="true"  />
								<dsp:param name="formId" value="3"  />
							</dsp:include>
						</div>
					</div>
					<div class="row">
						<div id="noSearchResults" class="small-12 columns">
							<p class="error">
								<bbbl:label key="${errorMsg}" language="${pageContext.request.locale.language}" />
							</p>
						</div>
					</div>
				</dsp:oparam>
				<dsp:oparam name="empty">
					<div class="row">
						<div id="registryFilterFormWrap" class="small-12 columns">
							<dsp:include page="registry_search_form.jsp">
								<dsp:param name="showOnlySearchFields" value="true" />
								<dsp:param name="formId" value="2" />
							</dsp:include>
						</div>
					</div>
					<div id="registryFilterFormWrap" class="small-12 row">
						<div class="small-12 columns no-padding" id="noSearchResults">
							<h3><bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" /></h3>
							<p class="p-secondary"><bbbl:label key="lbl_regsrchguest_try_again" language="${pageContext.request.locale.language}" /></p>
						</div>
					</div>
				</dsp:oparam>
			</dsp:droplet>
			<%-- End GiftRegistryPaginationDroplet --%>

			<script type="text/javascript">
				var totalCount = '${totalCount}';
			</script>

		</dsp:oparam>
		<dsp:oparam name="true">
		
			<%-- Registry Search Form --%>
			<dsp:include page="registry_search_form.jsp">
				<dsp:param name="showOnlySearchFields" value="false"  />
				<dsp:param name="formId" value="1"  />
				<dsp:param name="commerceId" param="commerceId"/>
			</dsp:include>
		</dsp:oparam>
	</dsp:droplet>

	<a class="close-reveal-modal">&#215;</a>

	<dsp:form id="registryFilterFormHidden" formid="registryFilterFormHidden" iclass="hidden" action="bbb_search_registry" method="post">
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.firstName" name="bbRegistryFirstName" maxlength="30" value="${sessFirstName}" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.lastName" name="bbRegistryLastName" maxlength="30" value="${sessLastName}" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.event" name="bbRegistryEventType" maxlength="30" value="${sessEventType}" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.state" name="bbRegistryState" maxlength="30" value="${sessState}" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.registryId" name="bbRegistryNumber" value="${sessRegistryId}" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.sort" name="sort" value="${sessSort}" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.sortSeqOrder" name="sortSeqOrder" value="${sortOrder}" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.hidden" value="3" />
		<dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearch" value="submit" id="searchRegistry" iclass="hidden"/>
		<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="registrySearch" />
	    <dsp:input bean="EmailAFriendFormHandler.queryParam"  type="hidden" value="commerceId=${cId }&search=search" />
		<%-- <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchSuccessURL" value="${contextPath}/giftregistry/registry_search.jsp?commerceId=${commerceId}&search=search" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchErrorURL" value="${contextPath}/giftregistry/registry_search.jsp?commerceId=${commerceId}&search=search" /> --%>
	</dsp:form>

</dsp:page>
