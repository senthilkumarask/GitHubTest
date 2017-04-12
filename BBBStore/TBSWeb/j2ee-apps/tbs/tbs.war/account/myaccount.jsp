<dsp:page>

	<%-- Imports --%>
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
	<dsp:importbean bean="/com/bbb/wishlist/BBBWishlistItemCountDroplet" />
	<dsp:importbean bean="/com/bbb/payment/giftcard/BBBGiftCardFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	
<%-- 	<dsp:importbean bean="/com/bbb/social/facebook/FBConnectFormHandler" /> --%>
	
	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="section" value="accounts" scope="request" />
	<c:set var="pageNameFB" value="myAccountOverview" scope="request" />
	<c:set var="pageWrapper" value="overviewPage addressBook myAccount useMapQuest useFB useStoreLocator" scope="request" />
	<c:set var="overview_view_my_orders_title"><bbbl:label key="lbl_overview_view_my_orders" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="overview_wishlist_manage_title"><bbbl:label key="lbl_overview_wishlist_manage" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="overview_coupons_view_title"><bbbl:label key="lbl_overview_coupons_view" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="giftcard_checkbal_button_title"><bbbl:label key="lbl_giftcard_checkbal_button" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="overview_credit_cards_add_title"><bbbl:label key="lbl_overview_credit_cards_add" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="overview_address_manage_title"><bbbl:label key="lbl_overview_address_manage" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="creditcardinfo_manage_title"><bbbl:label key="lbl_creditcardinfo_manage" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="overview_manage_registries_title"><bbbl:label key="lbl_overview_manage_registries" language ="${pageContext.request.locale.language}"/></c:set>
	<c:set var="addressbook_addnewaddress_title"><bbbl:label key="lbl_addressbook_addnewaddress" language ="${pageContext.request.locale.language}"/></c:set>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

	<bbb:pageContainer index="false" follow="false">

		<jsp:attribute name="bodyClass">my-account</jsp:attribute>
		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
		<jsp:attribute name="PageType">MyAccountDetails</jsp:attribute>

		<jsp:body>

			<div class="row" id="content">
				<div class="small-12 columns">
					<h1><dsp:getvalueof bean="Profile.firstName" var="displayName" />
						<c:choose>
							<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
								<c:out value="${fn:substring(displayName,0,21)}"/>
							</c:when>
							<c:otherwise>
								<c:out value="${displayName}"/>&#39;s
							</c:otherwise>
						</c:choose>: <span class="subheader"><bbbl:label key="lbl_myaccount_overview" language ="${pageContext.request.locale.language}"/></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage">
							<bbbl:label key="lbl_myaccount_overview" language="${pageContext.request.locale.language}" />
						</c:param>
					</c:import>
				</div>
				<div class="small-12 large-9 columns">

					<%-- only show first time after sign in of profile creation --%>
					<dsp:getvalueof var="newRegistration" vartype="boolean" bean="Profile.newRegistration"/>
					<dsp:getvalueof var="fbProfileExtended" vartype="boolean" bean="Profile.fbProfileExtended"/>
					<c:if test="${newRegistration==true}">
						<div class="row">
							<div class="small-12 columns">
								<p class="p-secondary"><bbbt:textArea key="txtarea_overview_acc_created" language ="${pageContext.request.locale.language}"/></p>
							</div>
						</div>
						<%--tellApart integration start --%>
						<c:if test="${TellApartOn}">
							<bbb:tellApart actionType="login"/>
						</c:if>
						<%--tellApart integration End --%>
						<dsp:setvalue bean="Profile.newRegistration" value="false" />
					</c:if>
					<c:if test="${fbProfileExtended==true}">
						<div class="row">
							<div class="small-12 columns">
								<p class="p-secondary"><bbbt:textArea key="txtarea_overview_fb_acc_extended" language ="${pageContext.request.locale.language}"/></p>
							</div>
						</div>
						<dsp:setvalue bean="Profile.fbProfileExtended" value="false" />
					</c:if>

					<div class="row">
						<div class="small-12 large-8 columns">
							<div class="row">
								<div class="small-12 columns">
									<div class="my-account-section" id="myOrders">
										<h2><bbbl:label key="lbl_overview_my_orders" language ="${pageContext.request.locale.language}"/></h2>
										<p class="p-secondary"><bbbt:textArea key="txtarea_overview_my_orders" language ="${pageContext.request.locale.language}"/></p>
										<a href="order_summary.jsp" class="small button primary"><bbbl:label key="lbl_overview_view_my_orders" language ="${pageContext.request.locale.language}"/></a>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="small-12 large-6 columns">
									<div class="my-account-section same-height" id="myAddressBook">
										<h2><bbbl:label key="lbl_addressbook_addressbooklabel" language ="${pageContext.request.locale.language}"/></h2>
										<dsp:droplet name="MapToArrayDefaultFirst">
											<dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId"/>
											<dsp:param name="defaultId2" bean="Profile.billingAddress.repositoryId"/>
											<dsp:param name="map" bean="Profile.secondaryAddresses"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
												<c:choose>
													<c:when test="${empty sortedArray}">
														<bbbt:textArea key="txtarea_overview_address_book_address_not_saved" language ="${pageContext.request.locale.language}"/>
														<%-- this functionality still under development --%>
														<a href="${contextPath}/account/address_book.jsp?showAddNewAddress=true" class="small button primary"><bbbl:label key="lbl_addressbook_addnewaddress" language ="${pageContext.request.locale.language}"/></a>
													</c:when>
													<c:otherwise>
														<c:set var="count" value="0"/>
														<c:forEach var="address" items="${sortedArray}" end="1">
															<dsp:setvalue param="address" value="${address}"/>
															<jsp:useBean id="addressCounter" class="java.util.HashMap" scope="request"/>
															<dsp:droplet name="Compare">
																<dsp:param name="obj1" bean="Profile.shippingAddress.repositoryId"/>
																<dsp:param name="obj2" param="address.value.id"/>
																<dsp:oparam name="equal">
																	<c:set var="isShipDefault" value="true"/>
																</dsp:oparam>
																<dsp:oparam name="default">
																	<c:set var="isShipDefault" value="false"/>
																</dsp:oparam>
															</dsp:droplet>
															<dsp:droplet name="Compare">
																<dsp:param name="obj1" bean="Profile.billingAddress.repositoryId"/>
																<dsp:param name="obj2" param="address.value.id"/>
																<dsp:oparam name="equal">
																	<c:set var="isBillDefault" value="true"/>
																</dsp:oparam>
																<dsp:oparam name="default">
																	<c:set var="isBillDefault" value="false"/>
																</dsp:oparam>
															</dsp:droplet>
															<dsp:param name="address" param="address.value"/>
															<div class="item">
																<c:if test="${isShipDefault && isBillDefault}">
																	<h3><bbbl:label key="lbl_overview_address_default" language ="${pageContext.request.locale.language}"/></h3>
																</c:if>
																<c:if test="${isShipDefault && !isBillDefault}">
																	<h3><bbbl:label key="lbl_overview_address_shipping_default" language ="${pageContext.request.locale.language}"/></h3>
																</c:if>
																<c:if test="${!isShipDefault && isBillDefault}">
																	<h3><bbbl:label key="lbl_overview_address_billing_default" language ="${pageContext.request.locale.language}"/></h3>
																</c:if>
																<ul class="address">
																	<li><dsp:valueof param="address.firstName" valueishtml="false"/> <dsp:valueof param="address.lastName" valueishtml="false"/></li>
																	<li><dsp:valueof param="address.address1" valueishtml="false"/></li>
																	<li><dsp:valueof param="address.address2" valueishtml="false"/></li>
																	<li><dsp:valueof param="address.city" valueishtml="false"/>, <dsp:valueof param="address.state" valueishtml="false"/> <dsp:valueof param="address.postalCode" valueishtml="false"/></li>
																</ul>
															</div>
															<c:set var="count" value="${count+1}"/>
														</c:forEach>
														<c:set target="${addressCounter}" property="total"><dsp:valueof param="sortedArraySize"/></c:set>
														<c:set target="${addressCounter}" property="current">${count}</c:set>
														<h4><bbbl:label key="lbl_overview_showing_x_of_y" language ="${pageContext.request.locale.language}" placeHolderMap="${addressCounter}"/></h4>
														<a href="${contextPath}/account/address_book.jsp" title="${overview_address_manage_title}"><bbbl:label key="lbl_overview_address_manage" language ="${pageContext.request.locale.language}"/></a>
													</c:otherwise>
												</c:choose>
											</dsp:oparam>
											<dsp:oparam name="empty">
						           			 <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
						           			 	<c:if test="${empty sortedArray}">
														<bbbt:textArea key="txtarea_overview_address_book_address_not_saved" language ="${pageContext.request.locale.language}"/>
														<a href="${contextPath}/account/address_book.jsp?showAddNewAddress=true" class="small button primary"><bbbl:label key="lbl_addressbook_addnewaddress" language ="${pageContext.request.locale.language}"/></a>
												</c:if>
						           			 </dsp:oparam>
										</dsp:droplet>
									</div>
								</div>
								<div class="small-12 large-6 columns">
									<div class="my-account-section same-height" id="myCreditCard">
										<h2><bbbl:label key="lbl_overview_my_credit_cards" language ="${pageContext.request.locale.language}"/></h2>
										<dsp:droplet name="MapToArrayDefaultFirst">
											<dsp:param name="defaultId" bean="Profile.defaultCreditCard.repositoryId"/>
											<dsp:param name="sortByKeys" value="true"/>
											<dsp:param name="map" bean="Profile.creditCards"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
												<c:choose>
													<c:when test="${empty sortedArray}">
														<%-- only show when user does not have a saved credit card --%>
														<bbbt:textArea key="txtarea_overview_credit_card_address_not_saved" language ="${pageContext.request.locale.language}"/>
														<%-- this functionality still under development --%>
														<a href="add_credit_card.jsp" class="small button primary"><bbbl:label key="lbl_overview_credit_cards_add" language ="${pageContext.request.locale.language}"/></a>
													</c:when>
													<c:when test="${not empty sortedArray}">
														<%-- only show when user has a saved credit card --%>
														<div class="item">
															<h3><bbbl:label key="lbl_overview_credit_cards_preferred" language ="${pageContext.request.locale.language}"/></h3>
															<ul class="address">
																<c:set var="ending_with" scope="page">
																	<bbbl:label key="lbl_creditcardinfo_endingwith" language="${pageContext.request.locale.language}" />
																</c:set>
																<li>
																	<dsp:getvalueof var="creditCard" bean="Profile.defaultCreditCard.creditCardNumber"/>
																	<h5><dsp:valueof bean="Profile.defaultCreditCard.creditCardType"/> ${ending_with}</h5>
																	<c:out value="${fn:substring(creditCard,fn:length(creditCard)-4,fn:length(creditCard))}" />
																</li>
																<li>
																	<h5><bbbl:label key="lbl_overview_credit_cards_name" language ="${pageContext.request.locale.language}"/></h5>
																	<dsp:valueof bean="Profile.defaultCreditCard.nameOnCard" valueishtml="false"/>
																</li>
																<li>
																	<h5><bbbl:label key="lbl_overview_credit_cards_expires" language ="${pageContext.request.locale.language}"/></h5>
																	<dsp:valueof bean="Profile.defaultCreditCard.expirationMonth" converter="number" format="00" valueishtml="false"/>/<dsp:valueof bean="Profile.defaultCreditCard.expirationYear" valueishtml="false"/>
																</li>
															</ul>
														</div>
														<jsp:useBean id="cardCounter" class="java.util.HashMap" scope="request"/>
														<c:set target="${cardCounter}" property="total"><dsp:valueof param="sortedArraySize"/></c:set>
														<c:set target="${cardCounter}" property="current">${1}</c:set>
														<h4><bbbl:label key="lbl_overview_showing_x_of_y" language ="${pageContext.request.locale.language}" placeHolderMap="${cardCounter}"/></h4>
														<a href="view_credit_card.jsp" title="${creditcardinfo_manage_title}"><bbbl:label key="lbl_creditcardinfo_manage" language ="${pageContext.request.locale.language}"/></a>
													</c:when>
												</c:choose>
											</dsp:oparam>
											<dsp:oparam name="empty">
						           			 <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
						           			 	<c:if test="${empty sortedArray}">
														<bbbt:textArea key="txtarea_overview_credit_card_address_not_saved" language ="${pageContext.request.locale.language}"/>
														<a href="${contextPath}/account/view_credit_card.jsp?showAddCreditCard=true" class="small button primary"><bbbl:label key="lbl_overview_credit_cards_add" language ="${pageContext.request.locale.language}"/></a>
												</c:if>
						           			 </dsp:oparam>
										</dsp:droplet>
									</div>
								</div>
							</div>
							
							<dsp:droplet name="GiftRegistryFlyoutDroplet">
							<dsp:param name="profile" bean="Profile"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="value" param="userStatus"/>
								<c:if test="${value > '2'}">
									<div class="row">
										<div class="small-12 columns">
											<div class="my-account-section" id="myRegistries">
												<h2><bbbl:label key="lbl_overview_my_registries" language ="${pageContext.request.locale.language}"/></h2>
												<div class="row">
													<div class="small-12 columns">
														<%-- only show when user has registries --%>
														<h3><bbbl:label key="lbl_overview_manage_registries" language ="${pageContext.request.locale.language}"/></h3>
														<a href="${contextPath}/giftregistry/my_registries.jsp" class="small button primary">
															<bbbl:label key="lbl_overview_manage_registries" language="${pageContext.request.locale.language}"/>
														</a>
													</div>
												</div>
											</div>
										</div>
									</div>
								</c:if>
							</dsp:oparam>
							</dsp:droplet>
							
							<%-- Uncomment this to create registry
							<h3><bbbl:label key="lbl_overview_create_registries" language ="${pageContext.request.locale.language}"/></h3>
							<bbbt:textArea key="txtarea_overview_my_registries" language ="${pageContext.request.locale.language}"/>
							<div class="small-12 large-6 columns">
								<dsp:form>
									<dsp:select bean="GiftRegistryFormHandler.registryEventType" id="typeofregselect">
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
									<dsp:tagAttribute name="aria-labelledby" value="typeofregselect"/>
									</dsp:select>
									<dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden" value="${contextPath}/giftregistry/registry_type_select.jsp" />
									<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden" value="${contextPath}/giftregistry/registry_creation_form.jsp" />
									<dsp:input bean="GiftRegistryFormHandler.registryTypes" type="submit" value="" id="submitRegistryClick" iclass="hidden" />
								</dsp:form>
							</div>								
							 --%>
							<%--
							<c:if test="${FBOn}">
								<div class="row">
									<div class="small-12 large-8 columns fbConnectWrapOverview my-account-section" id="myFacebook">
										<dsp:getvalueof var="fbUnlinkVar" param="fbUnlink" />
										<c:if test="${not empty fbUnlinkVar}">
											<dsp:setvalue bean="FBConnectFormHandler.unLinking" />
										</c:if>
										<dsp:include page="facebook_linking.jsp" flush="true"/>
									</div>
								</div>
							</c:if>
							--%>
						</div>
						<div class="small-12 large-4 columns">
							<c:set var="TBS_BedBathCanadaSite">
								<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
							</c:set>
							<c:if test="${MapQuestOn && not currentSiteId eq TBS_BedBathCanadaSite}">
								<dsp:getvalueof id="currUrl" value="${pageContext.request.requestURI}" scope="session"/>
								<div class="row">
									<div class="small-12 columns">
										<div class="my-account-section" id="myFavoriteStore">
											<dsp:include src="${contextPath}/account/frags/favstorewidget.jsp"/>
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${ValueLinkOn}">
								<div class="row">
									<div class="small-12 columns">
										<div class="my-account-section" id="myGiftCard">
											<h2><bbbl:label key='lbl_giftcard_checkbal_heading' language='${pageContext.request.locale.language}' /></h2>
											<dsp:form iclass="frmCheckGiftCardBal" action="${contextPath}/account/check_giftcard_balance.jsp" method="post">
												<div class="giftCardNumber">
													<c:set var="placeholder"><bbbl:label key="lbl_giftcard_number" language ="${pageContext.request.locale.language}"/></c:set>
													<dsp:input type="text" id="giftCardNumberMA" bean="BBBGiftCardFormHandler.giftCardNumber" name="giftCardNumber" maxlength="16" value="">
														<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
														<dsp:tagAttribute name="aria-required" value="false"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblgiftCardNumberMA errorgiftCardNumberMA"/>
													</dsp:input>
													<c:set var="placeholder"><bbbl:label key="lbl_giftcard_pin" language ="${pageContext.request.locale.language}"/></c:set>
													<dsp:input type="text" id="giftCardPinMA" bean="BBBGiftCardFormHandler.giftCardPin" name="giftCardPin" maxlength="8" value="">
														<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
														<dsp:tagAttribute name="aria-required" value="false"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblgiftCardPinMA errorgiftCardPinMA"/>
													</dsp:input>
												</div>
												<div class="errorWrap hidden">
													<label class="error serverErrors block"></label>
												</div>
												<div class="giftCardBalanceWrap hidden">
													<div>
														<strong><bbbl:label key="lbl_giftcard_checkbal_giftcard" language ="${pageContext.request.locale.language}"/></strong>&nbsp;<span class="giftCardNumberResult giftCardResultItem"></span>
													</div>
													<div>
														<strong><bbbl:label key="lbl_giftcard_checkbal_balance" language ="${pageContext.request.locale.language}"/></strong>&nbsp;<span class="balance giftCardResultItem"></span>
													</div>
												</div>
												<dsp:input type="hidden" bean="BBBGiftCardFormHandler.balance" value=""/>
												<dsp:a href="#" iclass="btnCheckBal small button primary"><bbbl:label key="lbl_giftcard_checkbal_button" language ="${pageContext.request.locale.language}"/></dsp:a>
											</dsp:form>
										</div>
									</div>
								</div>
							</c:if>
							<div class="row">
								<div class="small-12 columns">
									<div class="my-account-section" id="myWishList">
										<h2><bbbl:label key="lbl_wishlist_wishlist" language ="${pageContext.request.locale.language}"/></h2>
										<div class="info">
											<dsp:droplet name="BBBWishlistItemCountDroplet">
											<dsp:param name="wishlistId" bean="Profile.wishlist.id" />
											<dsp:param name="siteId" value="${currentSiteId}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="wishlistItemCount" vartype="java.lang.Object" param="wishlistItemCount"/>
												</dsp:oparam>
											</dsp:droplet>
											<span class="count">${wishlistItemCount}</span>
											<span class="msg"><bbbl:label key="lbl_overview_wishlist" language ="${pageContext.request.locale.language}"/></span>
										</div>
										<a href="${contextPath}/wishlist/wish_list.jsp" title="${overview_wishlist_manage_title}" class="itemLink"><bbbl:label key="lbl_overview_wishlist_manage" language ="${pageContext.request.locale.language}"/></a>
									</div>
								</div>
							</div>
							<c:if test="${CouponOn}">
								<div class="row">
									<div class="small-12 columns">
										<div class="my-account-section" id="myCoupons">
											<h2>Coupons</h2>
											<div class="info" id="myCouponsCount">
												<img src="/_assets/tbs_assets/img/ajax-loader.gif" />
											</div>
											<%-- RM# 35525 Coupon Wallet is not in scope for TBS Next --%>
											<%-- <a href="${contextPath}/account/coupons.jsp" title="${overview_coupons_view_title}" class="itemLink"><bbbl:label key="lbl_overview_coupons_view" language ="${pageContext.request.locale.language}"/></a> --%>
										</div>
									</div>
								</div>
							</c:if>
						</div>
					</div>

					<c:if test="${empty added}">
						<dsp:getvalueof id="subscribe" value="${sessionScope.subscribe}"/>
						<c:if test="${!(empty subscribe)}">
							<script type="text/javascript">
								var pageAction = "336";
							</script>
						</c:if>
						<dsp:getvalueof id="added" value="added" scope="session"/>
					</c:if>

					<%--tellApart integration start --%>
					<c:if test="${empty isReturningUser}">
						<dsp:getvalueof id="returningUser" value="${sessionScope.returningUser}"/>
						<c:if test="${!(empty returningUser)}">
							<c:if test="${currentSiteId == TBS_BedBathUSSite && TellApartOn || currentSiteId == TBS_BuyBuyBabySite && TellApartOn_baby || currentSiteId == TBS_BedBathCanadaSite && TellApartOn_ca}">
								<bbb:tellApart actionType="login"/>
							</c:if>
						</c:if>
					</c:if>
					<dsp:getvalueof id="isReturningUser" value="true" scope="session"/>
					<%--tellApart integration End --%>

					<!-- YourAmigo code starts 6/18/2013-->
					<c:if test="${YourAmigoON}">
						<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
						<c:if test="${!isTransient}">
							<!-- ###################################################################### -->
							<!-- Configuring the javascript for tracking signups (to be placed on the	-->
							<!-- signup confirmation page, if any).										-->
							<!-- ###################################################################### -->
							<c:choose>
								<c:when test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
									<script src="https://support.youramigo.com/52657396/tracev2.js"></script>
									<c:set var="ya_cust" value="52657396"></c:set>
								</c:when>
								<c:when test="${(currentSiteId eq TBS_BedBathUSSite)}">
									<script src="https://support.youramigo.com/73053126/trace.js"></script>
									<c:set var="ya_cust" value="73053126"></c:set>
								</c:when>
							</c:choose>
							<script type="text/javascript">
							/* <![CDATA[ */
								/*** YA signup tracking code for Bed Bath & Beyond (www.bedbathandbeyond.com) ***/
								// --- begin customer configurable section ---
								ya_tid = Math.floor(Math.random()*1000000);	// Set XXXXX to the ID counting the signup, or to a random
													// value if you have no such id - eg,
													// ya_tid = Math.random();
								ya_pid = ""; // Set YYYYY to the type of signup - can be blank
													// if you have only one signup type.
								ya_ctype = "REG"; // Indicate that this is a signup and not a purchase.
								// --- end customer configurable section. DO NOT CHANGE CODE BELOW ---
								ya_cust = '${ya_cust}';
								try { yaConvert(); } catch(e) {}
							/* ]]> */
							</script>
						</c:if>
					</c:if>

					<c:import url="/selfservice/find_in_store.jsp" />
					<%-- New version of view map/get directions --%>
					<c:import url="/selfservice/store/find_store_pdp.jsp" />

				</div>
			</div>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<dsp:getvalueof var="newRegistration" vartype="boolean" bean="Profile.newRegistration" />
			<dsp:getvalueof var="emailOptIn" bean="ProfileFormHandler.value.receiveEmail" />
			<script type="text/javascript">
				var newRegistration ='${newRegistration}';
				var emailOptIn = '${emailOptIn}';
				if((newRegistration == 'true')&& (emailOptIn == 'yes')){
				rkg_micropixel('${currentSiteId}','email');
				}

				if (typeof s !== 'undefined') {
					s.pageName = 'My Account';
					<c:if test="${newRegistration eq true}">
						s.events='event30,event36';
					</c:if>
					s.channel = 'My Account';
					s.pageName='My Account>Overview';
					s.prop1='My Account';
					s.prop2='My Account';
					s.prop3='My Account';
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
