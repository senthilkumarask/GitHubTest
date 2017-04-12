<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/com/bbb/search/handler/TBSOrderSearchFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/com/bbb/cms/droplet/CollegeCategoryDroplet" />
    <dsp:importbean bean="/com/bbb/cms/droplet/LandingTemplateDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/Cache"/>

	<dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="currentCatId" param="categoryId"/>
	<dsp:getvalueof id="siteURL" bean="Site.productionURL"/>
	<dsp:getvalueof var="homepage" param="homepage"/>
	<dsp:getvalueof var="PageType" param="PageType"/>
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
	<dsp:getvalueof var="isTBSOrderSubmitted" bean="CheckoutProgressStates.isTBSOrderSubmitted"/>
	<dsp:getvalueof var="comparisonListItems" bean="ProductComparisonList.items" />
	
	<!-- Below site id get is added for story BBBI-3818. This parameter will be rendered when this 
	jsp gets invoked by InvalidateDropletCacheScheduler.  -->

	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof id="siteId" param="siteId"></dsp:getvalueof>
	<c:if test="${not empty siteId}">
		<dsp:getvalueof id="currentSiteId" param="siteId"/>
	</c:if>
	
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="flyoutCacheTimeout">
		<bbbc:config key="FlyoutCacheTimeout" configName="HTMLCacheKeys" />
	</c:set>
	<c:set var="rootCatValue">
		<bbbc:config key="${currentSiteId}RootCategory" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="newRelicTagON">
		<bbbc:config key="newRelicTag" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="isLoggedIn" bean="Profile.transient"/>
    <c:set var="findACollegeLinks_us">
        <bbbc:config key="findACollegeLinks_BedBathUS" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="findACollegeLinks_ca">
        <bbbc:config key="findACollegeLinks_BedBathCanada" configName="ContentCatalogKeys" />
    </c:set>
	
	<c:choose>
		<c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
			<c:set var="clearanceCategory">
				<bbbc:config key="BedBathCanada_clearanceCategories" configName="ContentCatalogKeys" />
			</c:set>
		</c:when>
		<c:when test="${currentSiteId == TBS_BedBathUSSite}">
			<c:set var="clearanceCategory">
				<bbbc:config key="BedBathUS_clearanceCategories" configName="ContentCatalogKeys" />
			</c:set>
		</c:when>
		<c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
			<c:set var="clearanceCategory">
				<bbbc:config key="BuyBuyBaby_clearanceCategories" configName="ContentCatalogKeys" />
			</c:set>
		</c:when>
	</c:choose>
	<dsp:getvalueof var="associateName" value="${sessionScope.associate1}"/>
	<header style='<c:if test="${fn:contains(header['User-Agent'],'AdobeAIR')}">margin-bottom: 3rem;</c:if>'>
		<!-- off canvas menu trigger -->
		<nav class="tab-bar nav">
			<div class="row">
				<c:set var="associateID">${sessionScope.associate1.associateId}</c:set>

				<c:set var="associateID">${fn:substring(associateID,0,7)}</c:set>
				<div class="small-12 columns headerWrapper">
					<div class="associateLogout show-for-large-up <c:if test="${empty associateName}">hidden</c:if>">
						<p>Hi ${associateID},</p>
						
						<dsp:form action="" method="post">
                            <dsp:input iclass="logOutAssociateLink no-padding" bean="ProfileFormHandler.clearCart" type="submit" value="Log Out"/>
                        </dsp:form>

					</div>
					
					<a class="left-off-canvas-toggle menu-icon" href='javascript:void(0);'><span></span></a>
	
                    <c:choose>
                         <c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
                            <a href="${contextPath}" class="home-btn ca"><span> </span></a>
                         </c:when>
                         <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                             <a href="${contextPath}" class="home-btn baby"><span> </span></a>
                         </c:when>
                         <c:otherwise>
                             <a href="${contextPath}" class="home-btn"><span></span></a>
                         </c:otherwise>
                     </c:choose>
					<div class="search-container <c:if test="${not empty associateName}">search-container-asso</c:if>">
						<dsp:form action="${originatingRequest.requestURI}" method="post" id="searchForm" formid="searchForm">
							<div class="row">
								<div class="small-2 medium-3 large-3 columns no-padding">
									<a href="#" data-dropdown="params" class="small secondary radius button dropdown prefix search-param"><span>&nbsp;</span></a>
									<ul id="params" data-dropdown-content="" class="f-dropdown">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" bean="TBSOrderSearchFormHandler.availableSearchTypes" />
											<dsp:param name="elementName" value="searchTypeName" />
											<dsp:oparam name="output">
												<dsp:getvalueof id="searchTypeName" param="searchTypeName" />
												<dsp:getvalueof id="searchCode" param="key" />
												<dsp:getvalueof id="count" param="count"/>
												<c:choose>
													<c:when test="${count == 1}">
														<li class="selected"><a href="#" data-search-code="${searchCode}" class="searchtype"><c:out value="${searchTypeName}" /></a></li>
													</c:when>
													<c:otherwise>
														<li><a href="#" data-search-code="${searchCode}" class="searchtype"><c:out value="${searchTypeName}" /></a></li>
													</c:otherwise>
												</c:choose>
												
											</dsp:oparam>
										</dsp:droplet>
									</ul>
								</div>
								<div class="small-8 medium-8 large-8 columns no-padding">
									<label class="hide" for="searchBar"><bbbl:label key="lbl_header_search_products" language="${pageContext.request.locale.language}" /></label>
									<dsp:input type="text" bean="TBSOrderSearchFormHandler.searchTerm" iclass="search-bar" id="searchBar" name="searchBar" value="">
										<dsp:tagAttribute name="placeholder" value="Product keywords, brand, or category"/>
										<dsp:tagAttribute name="autocomplete" value="off"/>
									</dsp:input>
								</div>
								<div class="small-2 medium-1 large-1 columns no-padding">
									<a href="#search" class="button postfix search-btn"><span>&nbsp;</span></a>
									<dsp:input bean="TBSOrderSearchFormHandler.searchType" id="searchTypeInput" type="hidden" value="" />
									<dsp:input bean="TBSOrderSearchFormHandler.searchTerm" id="searchTermInput" type="hidden" value="" />
									<dsp:input bean="TBSOrderSearchFormHandler.babyItems" id="babyItems" type="hidden" value="" />
									<dsp:input bean="TBSOrderSearchFormHandler.search" id="searchInput" type="submit" iclass="hidden" value="Submit" />
								</div>
							</div>
						</dsp:form>
					</div>
					
					<div class="main-nav">
						<ul>
							<li class="services">
								<a href="#">services</a>
								<div class="bg-dropdown"></div>
								<div class="row main-dropdown">
									<div class="small-12 columns">
										<h1>Services</h1>
									</div>
									<div class="small-12 columns">
										<ul class="large-block-grid-5">
											<li>
													<a href="${contextPath}/search/track_order.jsp" class="service-tile">
													<div class="service-tile-container">
														<div class="service-tile-content">
															<span class="order"></span>
															<h4><bbbl:label key="lbl_tbs_order_inquiry" language="<c:out param='${pageContext.request.locale.language}'/>" /></h4>
														</div>
													</div>
												</a>
											</li>
											
											<c:set var="enableCustomOrders">
												<bbbc:config key="${currentSiteId}" configName="CustomOrderDisplayConfig" />
											</c:set>
											
											<c:if test="${enableCustomOrders}">
												<li>
													<a href="${contextPath}/account/custom_order.jsp" class="service-tile">
														<div class="service-tile-container">
															<div class="service-tile-content">
																<span class="custom-order"></span>
																<h4><bbbl:label key="tbs_customorderpageheader" language ="${pageContext.request.locale.language}"/></h4>
															</div>
														</div>
													</a>
												</li>
											</c:if>
											<li>
												<c:choose>
													<c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
													<dsp:a href="${contextPath}/selfservice/CanadaStoreLocator" iclass="service-tile">
															<div class="service-tile-container">
																<div class="service-tile-content">
																	<span class="store-locator"></span>
																	<h4><bbbl:label key="lbl_header_canadafindastore" language="${pageContext.request.locale.language}" /></h4>
																</div>
															</div>
														</dsp:a>
													</c:when>
													<c:otherwise>
													<c:if test="${MapQuestOn}">
														<a href="${contextPath}/selfservice/FindStore" class="service-tile">
															<div class="service-tile-container">
																<div class="service-tile-content">
																	<span class="store-locator"></span>
																	<h4><bbbl:label key="lbl_header_findastore" language="${pageContext.request.locale.language}" /></h4>
																</div>
															</div>
														</a>

													</c:if>
													</c:otherwise>
												</c:choose>
											</li>
											<li>
												<a href="${contextPath}/static/GiftCardHomePage" class="service-tile">
													<div class="service-tile-container">
														<div class="service-tile-content">
															<span class="gift-card"></span>
															<h4><bbbl:label key="lbl_header_giftcards" language="${pageContext.request.locale.language}" /></h4>
														</div>
													</div>
												</a>
											</li>

											<li>
												<a href="${contextPath}/account/preferences.jsp" class="service-tile">
													<div class="service-tile-container">
														<div class="service-tile-content">
															<span class="preferences"></span>
															<h4><bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/></h4>
														</div>
													</div>
												</a>
											</li>
										</ul>
									</div>
								</div>
							</li>
							<li class="registry">
                                <c:choose>
                                    <c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
                                       <a href="${contextPath}/page/Registry">registry</a>
                                    </c:when>
                                    <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                                        <a href="${contextPath}/page/BabyRegistry">registry</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${contextPath}/page/Registry">registry</a>
                                    </c:otherwise>
                                </c:choose>
								
									<div class="bg-dropdown"></div>
									<div class="row main-dropdown">
									<dsp:form action="${contextPath}/giftregistry/registry_search_guest.jsp" method="post" id="regSearchViaNumFlyout">
										<div class="small-12 columns">
											<h1>Search for a Gift Registry</h1>
										</div>
										<div class="small-5 columns">
											<div class="row">
												<div class="small-12 columns">
													<label for="regId" class='hide'>Enter Registry ID</label>
													<dsp:input type="text" name="regIdNum" id="regIdNum" bean="GiftRegistryFormHandler.registrySearchVO.registryId" value="">
														<dsp:tagAttribute name="placeholder" value="Registry ID" />
														<dsp:tagAttribute name="aria-required" value="false" />
														<dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryNumber errorbbRegistryNumber" />
													</dsp:input>
												</div>
												<div class="small-5 small-offset-7 columns">
													<label for="regSearch" class='hide'>SEARCH</label>
													<dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearch" iclass="button tiny service right expand" id="regSearch" value="SEARCH" />
												<%-- 	<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchSuccessURL" value="/tbs/giftregistry/registry_search_guest.jsp" />
													<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchErrorURL" value="/tbs/giftregistry/registry_search_guest.jsp" /> --%>
													<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="header" />
													
												</div>
											</div>
										</div>
									</dsp:form>
										<div class="small-1 columns or">or</div>
										<div class="small-6 columns">
											<dsp:form action="${contextPath}/giftregistry/registry_search_guest.jsp" method="post" id="regSearchViaNameFlyout">
											<div class="row">
												<div class="small-12 columns">
													<label for="regFirst" class='hide'>Enter first name</label>
													<dsp:input type="text"  id="regFirstName" name="regFirstName" bean="GiftRegistryFormHandler.registrySearchVO.firstName" maxlength="30" value="">
														<dsp:tagAttribute name="placeholder" value="First Name" />
														<dsp:tagAttribute name="aria-required" value="false" />
														<dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryFirstName errorbbRegistryFirstName" />
													</dsp:input>
												</div>
												<div class="small-12 columns">
													<label for="regLast" class='hide'>Enter last name</label>
													<dsp:input type="text" id="regLastName" name="regLastName" bean="GiftRegistryFormHandler.registrySearchVO.lastName" maxlength="30" value="">
														<dsp:tagAttribute name="placeholder" value="Last Name" />
														<dsp:tagAttribute name="aria-required" value="false" />
														<dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryLastName errorbbRegistryLastName" />
													</dsp:input>
												</div>
												<div class="small-6 columns registry-state">
													<c:choose>
														<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
															<c:set var="stateLabel" value="Province"/>
														</c:when>
														<c:otherwise>
															<c:set var="stateLabel" value="State"/>
														</c:otherwise>
													</c:choose>
													
													<a href="#" data-dropdown="regState" class="small download radius button dropdown thin">${stateLabel}<span>&nbsp;</span></a>
													<ul id="regState" data-dropdown-content="" class="f-dropdown">
														<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
															<dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
															<dsp:oparam name="output">
																<li>
																	<a href="#">
																		<c:choose>
																			<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
																				<bbbl:label key="lbl_bridalbook_select_province" language="${pageContext.request.locale.language}" />
																			</c:when>
																			<c:otherwise>
																				<bbbl:label key="lbl_bridalbook_select_state" language="${pageContext.request.locale.language}" />
																			</c:otherwise>
																		</c:choose>
																	</a>
																</li>
																<dsp:droplet name="ForEach">
																	<dsp:param name="array" param="location" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof param="element.stateName" id="stateName" />
																		<dsp:getvalueof param="element.stateCode" id="stateCode" />
																		<li>
																			<a href="#" data-state="${stateCode}">
																				${stateName}
																			</a>
																		</li>
																	</dsp:oparam>
																</dsp:droplet>
															</dsp:oparam>
														</dsp:droplet>
													</ul>
													<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.state" id="bbRegistryState" name="bbRegistryState" value="" />
												</div>
												<div class="small-6 columns registry-type">
													<a href="#" data-dropdown="regType" class="small download radius button dropdown thin">Event Type<span>&nbsp;</span></a>
													<ul id="regType" data-dropdown-content="" class="f-dropdown">
														<dsp:droplet name="GiftRegistryTypesDroplet">
															<dsp:param name="siteId" value="${currentSiteId}"/>
															<dsp:oparam name="output">
																<li class="selected"><a href="#">Event Type</a></li>
																<dsp:droplet name="ForEach">
																	<dsp:param name="array" param="registryTypes" />
																	<dsp:oparam name="output">
																		<dsp:param name="regTypes" param="element" />
																		<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
																		<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
																		<li><a href="#" data-registry-code="${registryCode}"><dsp:valueof param="element.registryName" /></a></li>
																	</dsp:oparam>
																</dsp:droplet>
															</dsp:oparam>
														</dsp:droplet>
													</ul>
													<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.event" id="bbRegistryEvent" name="bbRegistryEvent" value="" />
												</div>	
											</div>
											<div class="row">
												<div class="small-4 small-offset-8 columns">
													<dsp:input type="hidden" bean="GiftRegistryFormHandler.hidden" value="8" />
													<label for="registrySearch" class='hide'>SEARCH</label>
													<dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearch" iclass="button tiny service right expand" id="regNameIdBtn" value="SEARCH" />
													<%-- <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchSuccessURL" value="/tbs/giftregistry/registry_search_guest.jsp" />
													<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchErrorURL" value="/tbs/giftregistry/registry_search_guest.jsp" /> --%>
												</div>
											</div>
											</dsp:form>
										</div>
										
										<div class="creat-reg-fout large-5">
										<h1><bbbl:label key="lbl_overview_create_registries" language ="${pageContext.request.locale.language}"/></h1>
											<div class="grid_2 clearfix small-7 columns no-padding">
										<dsp:setvalue bean="GiftRegistryFormHandler.registryEventType"  value="" />	
									 		<dsp:form>
													<dsp:select bean="GiftRegistryFormHandler.registryEventType" onchange="callMyAccRegistryTypesFormHandlerInHeader();" id="typeofregselectHdr">
													<dsp:droplet name="GiftRegistryTypesDroplet">
													<dsp:param name="siteId" value="${currentSiteId}"/>
													<dsp:oparam name="output">
														<dsp:option value="" selected="selected"><bbbl:label key="lbl_overview_select_type" language="${pageContext.request.locale.language}"/></dsp:option>
														<dsp:droplet name="ForEach">
														<dsp:param name="array" param="registryTypes" />
														<dsp:oparam name="output">
														<dsp:param name="regTypes" param="element" />
															<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
															<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
															<dsp:option value="${registryCode}">
																<dsp:valueof param="element.registryName" />
															</dsp:option>
														</dsp:oparam>
														</dsp:droplet>
													</dsp:oparam>
													</dsp:droplet>
													<dsp:tagAttribute name="aria-required" value="false"/>
													<dsp:tagAttribute name="aria-labelledby" value="typeofregselectHdr"/>
													<dsp:tagAttribute name="class" value="selector_primary"/>
													<dsp:tagAttribute name="autocomplete" value="off"/>
													</dsp:select>
													<%-- <dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden" value="${contextPath}/giftregistry/registry_type_select.jsp" />
													<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden" value="${contextPath}${simpleRegCreationFormPath}" /> --%>
													<c:choose>
											<c:when test="${currentSiteId == TBS_BedBathUSSite}">
												
												<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="headerSimpleRegBedBath" />
											</c:when>
											<c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
												
												<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="headerSimpleRegBuyBuy" />
											</c:when>
											<c:otherwise>
											
												<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="headerSimpleRegBedBath" />
											</c:otherwise>
										</c:choose>
													
													<dsp:input bean="GiftRegistryFormHandler.registryTypes" type="submit" value="" id="submitRegistryClickHdr" iclass="hidden" />
								</dsp:form>
											 </div>
										</div>
									</div>
							</li>

							<dsp:droplet name="Switch">
								<dsp:param name="value" bean="Profile.transient"/>
								<dsp:oparam name="true">
									<li class="login-name"><dsp:a page="/account/Login"><bbbl:label key="lbl_header_myaccount_new" language="${pageContext.request.locale.language}" /></dsp:a>

									</li>
								</dsp:oparam>
								<dsp:oparam name="false">
								<dsp:droplet name="Switch">
								<dsp:param name="value" value="${isTBSOrderSubmitted}"/>
								<dsp:oparam name="true">
								 <li class="login-name"><dsp:a page="/account/Login"><bbbl:label key="lbl_header_myaccount_new" language="${pageContext.request.locale.language}" /></dsp:a>
									</li>
								</dsp:oparam>
								<dsp:oparam name="default">
									<li class="login-name">
										<dsp:getvalueof bean="Profile.firstName" var="displayName" />
										<a href="${contextPath}/account/myaccount.jsp">Account
										</a>
										<div class="bg-dropdown"></div>
										<div class="row main-dropdown">
											<div class="my-account-subnav">
													<ul id="account-subnav-list">
														<li><a href="${contextPath}/account/order_summary.jsp"><bbbl:label key="lbl_header_orders" language="${pageContext.request.locale.language}" /></a></li>
														<c:if test="${MapQuestOn}">
															<c:if test="${!(currentSiteId eq TBS_BedBathCanadaSite)}">
															<li><a href="${contextPath}/account/favoritestore.jsp"><bbbl:label key="lbl_header_favoritestore" language="${pageContext.request.locale.language}" /></a></li>
															</c:if>
														</c:if>
														<li><a href="${contextPath}/account/address_book.jsp"><bbbl:label key="lbl_header_addressbook" language="${pageContext.request.locale.language}" /></a></li>
														<li><a href="${contextPath}/account/view_credit_card.jsp"><bbbl:label key="lbl_header_creditcards" language="${pageContext.request.locale.language}" /></a></li>
														<li><a href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_header_registries" language="${pageContext.request.locale.language}" /></a></li>
														<li><a href="${contextPath}/wishlist/wish_list.jsp"><bbbl:label key="lbl_header_wishlist" language="${pageContext.request.locale.language}" /></a></li>
														<li><a href="${contextPath}/account/personalinfo.jsp"><bbbl:label key="lbl_header_personalinfo" language="${pageContext.request.locale.language}" /></a></li>
														<c:if test= "${HarteHanksOn}">
															<li><a href="${contextPath}/account/preferences.jsp"><bbbl:label key="lbl_header_preferences" language="${pageContext.request.locale.language}" /></a></li>
														</c:if>
														<c:if test="${KirschOn}">
															<li><a href="${contextPath}/account/kirsch.jsp"><bbbl:label key="lbl_header_levolorproject" language="${pageContext.request.locale.language}" /></a></li>
														</c:if>
														<li><dsp:a href=""><dsp:property bean="ProfileFormHandler.logout" value="true"/><bbbl:label key="lbl_header_logout" language="${pageContext.request.locale.language}" /></dsp:a></li>
													</ul>
												</div>
											</div>
										</li>
										</dsp:oparam>
										</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</ul>
					</div>

					<%-- cart count --%>
					<c:choose>
					<c:when test="${(fn:indexOf(pageWrapper, 'errorPages') > -1) || (fn:indexOf(pageWrapper, 'serverErrorPages') > -1) || (fn:indexOf(pageWrapper, '500') > -1)}">
						<c:set var="numItems">0</c:set>
					</c:when>
					<c:otherwise>
						<c:set var="numItems"><%@ include file="/cart/cart_item_count.jsp" %></c:set>
					</c:otherwise>
					</c:choose>
					<a href="${contextPath}/cart/cart.jsp" class="cart-btn">
						<span class="cart-icon <c:if test="${currentSiteId == TBS_BuyBuyBabySite}">baby</c:if>">
                        </span>
						<c:if test="${numItems > 0}">
							(<span id="cartItems"><c:out value="${numItems}" /></span>)
							<div id="miniCart"></div>
						</c:if>
						<c:if test="${numItems == 0}">
							<div id="miniCart">
								<div class="mini-cart-empty">
									<p><bbbl:label key="lbl_orderitems_noitemsincart" language="${pageContext.request.locale.language}" /></p>
								</div>
							</div>
						</c:if>
					</a>
				</div>

			</div>

		</nav>

		<%-- KP COMMENT START: for use with searchFlyout.jsp --%>
		
		<div class="type-ahead-container hidden">
			<div class="row">
				<div class="small-12 medium-5 columns type-ahead-terms">
					<ul class="terms-list"></ul>
				</div>
				<div class="small-12 medium-7 columns type-ahead-brands-dept">
					<div class="row">
						<div class="small-12 medium-6 columns type-ahead-dept">
							<h4>Top Departments</h4>
							<ul class="terms-list">
							</ul>
						</div>
						<div class="small-12 medium-6 columns type-ahead-brands">
							<h4>Top Brands</h4>
							<ul class="terms-list"></ul>
						</div>
					</div>
					<div class="row">
						<div class="small-12 medium-6 columns typeahead-left-btn hidden"></div>
						<div class="small-12 medium-6 columns typeahead-right-btn hidden"></div>
					</div>
				</div>
			</div>
		</div>
        <c:if test="${!fn:contains(header['User-Agent'],'AdobeAIR')}">
		<dsp:droplet name="Cache">
	   	<dsp:param name="key" value="menu_contents.jsp_${currentSiteId}" />
	   	<dsp:param name="cacheCheckSeconds" value="${flyoutCacheTimeout}"/>	   	
	   	<dsp:oparam name="output">
	   		<dsp:droplet name="SearchDroplet">
			<dsp:param name="CatalogId" value="0"/>
			<dsp:param name="CatalogRefId" value="10000"/>
			<dsp:param name="isHeader" value="Y"/>
			<dsp:param name="type" value="product"/>
			<dsp:param name="comparisonListItems" value="${comparisonListItems}"/>
				<dsp:oparam name="output">
				<c:if test="${NodeStylingON}">
					<c:set var="SpecialL1Nodes">
						<bbbc:config key="SpecialL1Nodes" configName="ContentCatalogKeys" />
					</c:set>
					<c:set var="SpecialL1NodesList" value="${fn:split(SpecialL1Nodes, ';')}" />
				</c:if>                
				<c:set var="babyCAL1Cat"><bbbc:config key="BabyCanada_L1_Category" configName="ContentCatalogKeys" /></c:set>
				<dsp:droplet name="ForEach">
					<dsp:param param="browseSearchVO.facets" name="array" />					 
					<dsp:oparam name="outputStart">
		<nav class="show-for-large-up nav sub-nav">
							<div class="row">
								<div class="small-12 columns">
					</dsp:oparam>
					<dsp:oparam name="outputEnd">
								</div>
							</div>
						</nav>
					</dsp:oparam>					
					<dsp:oparam name="output">
						<dsp:getvalueof var="facetName" param="element.name" />
						<c:if test="${facetName == 'DEPARTMENT'}">
							<c:set var="isFirst" value="${true}" />
							<dsp:getvalueof var="refinementVO" param="element.facetRefinement" scope="page"/>
							<dsp:droplet name="ForEach">
								<dsp:param param="element.facetRefinement" name="array" />
								<dsp:getvalueof var="counter" param="count" scope="page"/>
								<dsp:getvalueof var="size" param="size"/>
								<dsp:oparam name="outputStart">
									<ul class="carouselSlickUL">
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
                                    <c:if test="${currentSiteId != 'TBS_BuyBuyBaby'}">
                                    <li class="college">
                                        <a href="${contextPath}/page/College"><bbbl:label key="lbl_tbs_header_shopforcollege" language="${pageContext.request.locale.language}" /></a>
                                        <div class="bg-dropdown"></div>
                                        <div class="menu-dropdown row">
			<!-- For College dropdown in header--> 
                                            <dsp:include page="/bbcollege/shop_for_college.jsp">
                                            	<dsp:param name="currentSiteId" value="${currentSiteId}" />
                                            </dsp:include>
                                            <!-- For College dropdown -->
                                        </div>
                                    </li>
                                    </c:if>
                                    <li>
                                          <dsp:droplet name="/com/bbb/cms/droplet/HeaderWhatsNewDroplet">
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="categoryId" param="categoryId"/>                                       

                                                    <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                        <dsp:param name="id" value="${categoryId}" />
                                                        <dsp:param name="itemDescriptorName" value="category" />
                                                        <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                                                        <dsp:oparam name="output">
                                                            <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                                                                <a href="${contextPath}${finalUrl}?catFlg=true">
                                                                   <bbbl:label key="lbl_header_whatsnew" language="${pageContext.request.locale.language}" />
                                                                </a>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                            </dsp:oparam>
                                        </dsp:droplet> 
                                        <div class="menu-dropdown row">
                                        </div>
                                    </li>
                                    <li>
                                        <dsp:a href="${contextPath}/page/brands"><bbbl:label key="lbl_header_shopbybrand" language="${pageContext.request.locale.language}" /></dsp:a>
                                        <div class="menu-dropdown row">
                                        </div>
                                    </li>
                                    <li>
                                             <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                <dsp:param name="id" value="${clearanceCategory}" />
                                                <dsp:param name="itemDescriptorName" value="category" />
                                                <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                                                <dsp:oparam name="output">
                                                    <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                                                    <a href="${contextPath}${finalUrl}">
                                                        <bbbl:label key="lbl_header_clearance" language="${pageContext.request.locale.language}" />
                                                    </a>
                                                </dsp:oparam>
                                            </dsp:droplet> 
                                        <div class="menu-dropdown row">
                                        </div>
                                    </li>
									</ul>
								</dsp:oparam>
								<dsp:oparam name="output">
									<li>
										<dsp:getvalueof var="query" param="element.query"/>
										<dsp:getvalueof var="categoryId" param="element.catalogId"/>
										<c:set var="customMenuItem" value="" />
										<c:if test="${NodeStylingON}">
											<%-- Find style class if this is special node --%>
											<c:forEach var="SpecialNode" items="${SpecialL1NodesList}">
													<c:if test="${SpecialNode eq categoryId}">
														<c:set var="customMenuItem">
															<bbbc:config key="${categoryId}StyleClass" configName="ContentCatalogKeys" />
														</c:set>
												</c:if>
											</c:forEach>
										</c:if>
										<c:set var="babyCAL1CatClass" value=""/>
										<c:if test="${fn:contains(babyCAL1Cat, categoryId)}">
											<c:set var="babyCAL1CatClass" value="babyNavMenu"/>
										</c:if>
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="element.catalogId" />
											<dsp:param name="itemDescriptorName" value="category" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
												<a href="#">
													<dsp:valueof param="element.name" />
												</a>
												<div class="bg-dropdown"></div>
												<div class="menu-dropdown row">
													<dsp:include page="/_includes/header/elements/menu_contents.jsp">
														<dsp:param name="CatalogId" value="${categoryId}" />
														<dsp:param name="catalogRefId" value="${categoryId}" />
														<dsp:param name="rootCategory" value="${rootCatValue}"/>
														<dsp:param name="flyoutCacheTimeout" value="${flyoutCacheTimeout}"/>
													</dsp:include>
												</div>
											</dsp:oparam>
										</dsp:droplet>
									</li>
								</dsp:oparam>
							</dsp:droplet>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>                
			</dsp:oparam>			
		</dsp:droplet>		
		</dsp:oparam>		
		</dsp:droplet>	
		</c:if>
	</header>
    <div class="associateLogoutMobile <c:if test="${empty associateName}">hidden</c:if>">
		<p>Hi ${associateID},</p>
		<dsp:form action="" method="post">
            <dsp:input iclass="logOutAssociateLinkMobile no-padding" bean="ProfileFormHandler.clearCart" type="submit" value="Log Out"/>
        </dsp:form>
	</div>
	<!-- off canvas menu -->
	<aside class="left-off-canvas-menu">
		<ul class="off-canvas-list top-nav">
			<li class="collapse-nav">
				<a href="#">services</a>
				<div class="plus-minus">
					<span></span>
				</div>
				<ul class="off-canvas-list">
					<li>
						<c:choose>
							<c:when test="${!isLoggedIn}">
								<a href="${contextPath}/account/order_summary.jsp"><bbbl:label key="lbl_header_trackorder" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:otherwise>
								<a href="${contextPath}/account/TrackOrder"><bbbl:label key="lbl_header_trackorder" language="${pageContext.request.locale.language}" /></a>
							</c:otherwise>
						</c:choose>
					</li>
					<li><a href="${contextPath}/account/custom_order.jsp"><bbbl:label key="tbs_customorderpageheader" language ="${pageContext.request.locale.language}"/></a></li>
					<li>
						<c:choose>
							<c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
							<dsp:a href="${contextPath}/selfservice/CanadaStoreLocator"><bbbl:label key="lbl_header_canadafindastore" language="${pageContext.request.locale.language}" /></dsp:a>
							</c:when>
							<c:otherwise>
							<c:if test="${MapQuestOn}">
								<a href="${contextPath}/selfservice/FindStore" ><bbbl:label key="lbl_header_findastore" language="${pageContext.request.locale.language}" /></a>
							</c:if>
							</c:otherwise>
						</c:choose>
					</li>
					<li>
						<a href="${contextPath}/static/GiftCardHomePage"><bbbl:label key="lbl_header_giftcards" language="${pageContext.request.locale.language}" /></a>
					</li>
					<li>
						<a href="${contextPath}/account/preferences.jsp"><bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/></a>
					</li>
				</ul>
			</li>
			<li><a href="${contextPath}/giftregistry/index.jsp">registry</a></li>
			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="true">
					<li><dsp:a page="/account/Login"><bbbl:label key="lbl_header_myaccount_new" language="${pageContext.request.locale.language}" /></dsp:a></li>
				</dsp:oparam>
				<dsp:oparam name="false">
					<li class="login-name collapse-nav">
						<dsp:getvalueof bean="Profile.firstName" var="displayName" />
						<a href="${contextPath}/account/myaccount.jsp">Account
						</a>
						<div class="plus-minus">
							<span></span>
						</div>
						<ul class="off-canvas-list">
							<li><a href="${contextPath}/account/order_summary.jsp"><bbbl:label key="lbl_header_orders" language="${pageContext.request.locale.language}" /></a></li>
							<c:if test="${MapQuestOn}">
								<c:if test="${!(currentSiteId eq TBS_BedBathCanadaSite)}">
									<li><a href="${contextPath}/account/favoritestore.jsp"><bbbl:label key="lbl_header_favoritestore" language="${pageContext.request.locale.language}" /></a></li>
								</c:if>
							</c:if>
							<li><a href="${contextPath}/account/address_book.jsp"><bbbl:label key="lbl_header_addressbook" language="${pageContext.request.locale.language}" /></a></li>
							<li><a href="${contextPath}/account/view_credit_card.jsp"><bbbl:label key="lbl_header_creditcards" language="${pageContext.request.locale.language}" /></a></li>
							<li><a href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_header_registries" language="${pageContext.request.locale.language}" /></a></li>
							<li><a href="${contextPath}/wishlist/wish_list.jsp"><bbbl:label key="lbl_header_wishlist" language="${pageContext.request.locale.language}" /></a></li>
							<c:if test="${CouponOn}">
								<li><a href="${contextPath}/account/coupons.jsp"><bbbl:label key="lbl_header_coupons" language="${pageContext.request.locale.language}" /></a></li>
							</c:if>
							<li><a href="${contextPath}/account/personalinfo.jsp"><bbbl:label key="lbl_header_personalinfo" language="${pageContext.request.locale.language}" /></a></li>
							<c:if test= "${HarteHanksOn}">
								<li><a href="${contextPath}/account/preferences.jsp"><bbbl:label key="lbl_header_preferences" language="${pageContext.request.locale.language}" /></a></li>
							</c:if>
							<c:if test="${KirschOn}">
								<li><a href="${contextPath}/account/kirsch.jsp"><bbbl:label key="lbl_header_levolorproject" language="${pageContext.request.locale.language}" /></a></li>
							</c:if>
							<li><dsp:a href=""><dsp:property bean="ProfileFormHandler.logout" value="true"/><bbbl:label key="lbl_header_logout" language="${pageContext.request.locale.language}" /></dsp:a></li>
						</ul>
					</li>
				</dsp:oparam>
			</dsp:droplet>
		</ul>
		<dsp:droplet name="Cache">
	   	<dsp:param name="key" value="menu_contents_off-canvas.jsp_${currentSiteId}" />
	   	<dsp:param name="cacheCheckSeconds" value="${flyoutCacheTimeout}"/>
	   		<dsp:oparam name="output">
		<dsp:droplet name="SearchDroplet">
			<dsp:param name="CatalogId" value="0"/>
			<dsp:param name="CatalogRefId" value="10000"/>
			<dsp:param name="isHeader" value="Y"/>
			<dsp:param name="type" value="product"/>
			<dsp:param name="comparisonListItems" value="${comparisonListItems}"/>
			<dsp:oparam name="output">
				<c:if test="${NodeStylingON}">
					<c:set var="SpecialL1Nodes">
						<bbbc:config key="SpecialL1Nodes" configName="ContentCatalogKeys" />
					</c:set>
					<c:set var="SpecialL1NodesList" value="${fn:split(SpecialL1Nodes, ';')}" />
				</c:if>
				<c:set var="babyCAL1Cat"><bbbc:config key="BabyCanada_L1_Category" configName="ContentCatalogKeys" /></c:set>
				<dsp:droplet name="ForEach">
					<dsp:param param="browseSearchVO.facets" name="array" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="facetName" param="element.name" />
						<c:if test="${facetName == 'DEPARTMENT'}">
							<c:set var="isFirst" value="${true}" />
							<dsp:getvalueof var="refinementVO" param="element.facetRefinement" scope="page"/>
							<dsp:droplet name="ForEach">
								<dsp:param param="element.facetRefinement" name="array" />
								<dsp:getvalueof var="counter" param="count" scope="page"/>
								<dsp:getvalueof var="size" param="size"/>
								<dsp:oparam name="outputStart">
									<ul class="off-canvas-list">
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
									<c:if test="${currentSiteId != 'TBS_BuyBuyBaby'}">
					                    <li class="college">
					                        <a href="${contextPath}/page/College"><bbbl:label key="lbl_tbs_header_shopforcollege" language="${pageContext.request.locale.language}" /></a>
					                    </li>
					                 </c:if>
									</ul>
								</dsp:oparam>
								<dsp:oparam name="output">
									<li class="collapse-nav">
										<dsp:getvalueof var="query" param="element.query"/>
										<dsp:getvalueof var="categoryId" param="element.catalogId"/>
										<c:set var="customMenuItem" value="" />

										<c:if test="${NodeStylingON}">
											<c:forEach var="SpecialNode" items="${SpecialL1NodesList}">
												<c:if test="${SpecialNode eq categoryId}">
													<c:set var="customMenuItem">
														<bbbc:config key="${categoryId}StyleClass" configName="ContentCatalogKeys" />
													</c:set>
												</c:if>
											</c:forEach>
										</c:if>

										<c:set var="babyCAL1CatClass" value=""/>
										<c:if test="${fn:contains(babyCAL1Cat, categoryId)}">
											<c:set var="babyCAL1CatClass" value="babyNavMenu"/>
										</c:if>

										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="element.catalogId" />
											<dsp:param name="itemDescriptorName" value="category" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
												<a href="#">
													<dsp:valueof param="element.name" />
												</a>
												<div class="plus-minus">
													<span></span>
												</div>
												<dsp:include page="/_includes/header/elements/menu_contents_off-canvas.jsp">
													<dsp:param name="CatalogId" value="${categoryId}" />
													<dsp:param name="catalogRefId" value="${categoryId}" />
													<dsp:param name="rootCategory" value="${rootCatValue}"/>
													<dsp:param name="flyoutCacheTimeout" value="${flyoutCacheTimeout}"/>
												</dsp:include>
											</dsp:oparam>
										</dsp:droplet>
									</li>
								</dsp:oparam>
							</dsp:droplet>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
		</dsp:oparam>
		</dsp:droplet>        
	</aside>
	<c:if test="${newRelicTagON}">
		<dsp:include page="/_includes/header/headerNewRelic.jsp"/>
	</c:if>
 
     <script type="text/javascript">
		function callMyAccRegistryTypesFormHandlerInHeader() {
			if (document.getElementById('typeofregselectHdr').value!="")
			{
				document.getElementById("submitRegistryClickHdr").click();
			}
		}
	</script>
</dsp:page>