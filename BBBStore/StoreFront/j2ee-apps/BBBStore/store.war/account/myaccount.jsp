<dsp:page>
<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/com/bbb/wishlist/BBBWishlistItemCountDroplet" />
<dsp:importbean bean="/com/bbb/payment/giftcard/BBBGiftCardFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
<dsp:importbean	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/com/bbb/social/facebook/FBConnectFormHandler" />
<dsp:importbean bean="/atg/multisite/Site" />

<dsp:getvalueof id="currentSiteId" bean="Site.id" />

<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
		<c:set var="BedBathCanadaSite">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
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
<c:set var="challengeQuestionON"><bbbc:config key="challenge_question_flag" configName="FlagDrivenFunctions" /></c:set>

 <bbb:pageContainer index="false" follow="false">
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">${section}</jsp:attribute>
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
    <jsp:attribute name="PageType">MyAccountDetails</jsp:attribute>
    <jsp:body>
		<div id="content" class="container_12 clearfix" role="main">
		
			<div class="grid_12">
				<div class="grid_4 noMarLeft">
				<h1 class="account fl"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/></h1>
				<h3 class="subtitle"><bbbl:label key="lbl_myaccount_overview" language ="${pageContext.request.locale.language}"/></h3>
				<div class="clear"></div>
				</div>	
				<dsp:importbean bean="/com/bbb/cms/droplet/ChallengeQuestionExistDroplet" />
				<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
				<c:if test="${challengeQuestionON}">
				<dsp:droplet name="ChallengeQuestionExistDroplet">
					<dsp:param name="challengeQuestionProfileID" bean="/atg/userprofiling/Profile.id"/>
					<dsp:oparam name="output">
					<dsp:getvalueof param="ChallengeQuestionNotExist" var="challengeQuestionNotSetup" />
							<%--${challengeQuestionNotSetup} --%>
					<div class="grid_7 noMar accPrivacyMsg"> <bbbl:label key="lbl_setup_challnege_quest" language="${pageContext.request.locale.language}" /> <a href="/store/account/personalinfo.jsp" class='startNowLink'><bbbl:label key="lbl_start_now" language="${pageContext.request.locale.language}" />.</a>
				</div>
					</dsp:oparam>
				</dsp:droplet>
				</c:if>
			</div>
			
			<div class="grid_2">
				<c:import url="/account/left_nav.jsp"/>
			</div>
			
			<div class="grid_9 prefix_1">
				<%-- only show first time after sign in of profile creation --%>
				<dsp:getvalueof var="newRegistration" vartype="boolean" bean="Profile.newRegistration"/>
				<dsp:getvalueof var="fbProfileExtended" vartype="boolean" bean="Profile.fbProfileExtended"/>
				<c:if test="${newRegistration==true}">
					<p class="bold welcomeMsg noMarTop"><bbbt:textArea key="txtarea_overview_acc_created" language ="${pageContext.request.locale.language}"/></p>
					<%--tellApart integration start --%>
					  <c:if test="${TellApartOn}">
                   <bbb:tellApart actionType="login"/>
					</c:if>
					<%--tellApart integration End --%>
					<dsp:setvalue bean="Profile.newRegistration" value="false" />
				</c:if>
				<c:if test="${fbProfileExtended==true}">
					<p class="bold welcomeMsg noMarTop"><bbbt:textArea key="txtarea_overview_fb_acc_extended" language ="${pageContext.request.locale.language}"/></p>
					<dsp:setvalue bean="Profile.fbProfileExtended" value="false" />
				</c:if>
				<div class="grid_6 alpha">
					<div id="myOrders" class="section clearfix">
						<div class="spacer clearfix">
							<h2 class="noMarTop"><bbbl:label key="lbl_overview_my_orders" language ="${pageContext.request.locale.language}"/></h2>
							<bbbt:textArea key="txtarea_overview_my_orders" language ="${pageContext.request.locale.language}"/>
							<div class="button">
								<a href="order_summary.jsp"><bbbl:label key="lbl_overview_view_my_orders" language ="${pageContext.request.locale.language}"/></a>
							</div>
							<div class="clear"></div>
						</div>
					</div>
					<div class="clearfix sections_2">
						<div id="myAddressBook" class="section grid_3 alpha clearfix">
							<%-- only show when user does not have a saved address --%>
							 <div class="spacer clearfix">
								<h2 class="noMarTop"><bbbl:label key="lbl_addressbook_addressbooklabel" language ="${pageContext.request.locale.language}"/></h2>
						 		<dsp:droplet name="MapToArrayDefaultFirst">
						            			<dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId"/>
						            			<dsp:param name="defaultId2" bean="Profile.billingAddress.repositoryId"/>
						            			<dsp:param name="map" bean="Profile.secondaryAddresses"/>
						            			
						            			<dsp:oparam name="output">
						            			  <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
						          			    <c:choose>
						               				 <c:when test="${empty sortedArray}">
														<bbbt:textArea key="txtarea_overview_address_book_address_not_saved" language ="${pageContext.request.locale.language}"/>
														<div class="button">
															<%-- this functionality still under development --%>
															<a href="${contextPath}/account/address_book.jsp?showAddNewAddress=true"><bbbl:label key="lbl_addressbook_addnewaddress" language ="${pageContext.request.locale.language}"/></a>
														</div>
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
														  <c:if test="${isShipDefault && isBillDefault}">
														  	<div class="item">
																	<h3 class="bold"><bbbl:label key="lbl_overview_address_default" language ="${pageContext.request.locale.language}"/></h3>
																	<div class="address">
																		<div class="name">
																			<span class="firstName"><dsp:valueof param="address.firstName" valueishtml="false"/></span> <span class="middleName" valueishtml="false"></span> <span class="lastName"><dsp:valueof param="address.lastName" valueishtml="false"/></span>
																		</div>
																		<div class="street1"><dsp:valueof param="address.address1" valueishtml="false"/></div>
																		<div class="street2"><dsp:valueof param="address.address2" valueishtml="false"/></div>
																		<div class="cityStateZip"> <span class="city"><dsp:valueof param="address.city" valueishtml="false"/></span>, <span class="state"><dsp:valueof param="address.state" valueishtml="false"/></span> <span class="zip"><dsp:valueof param="address.postalCode" valueishtml="false"/></span> </div>
																	</div>
																</div>
														  </c:if>
														  <c:if test="${isShipDefault && !isBillDefault}">
														  	<div class="item">
																	<h3 class="bold"><bbbl:label key="lbl_overview_address_shipping_default" language ="${pageContext.request.locale.language}"/></h3>
																	<div class="address">
																		<div class="name">
																			<span class="firstName"><dsp:valueof param="address.firstName" valueishtml="false"/></span> <span class="middleName" valueishtml="false"></span> <span class="lastName"><dsp:valueof param="address.lastName" valueishtml="false"/></span>
																		</div>
																		<div class="street1"><dsp:valueof param="address.address1" valueishtml="false"/></div>
																		<div class="street2"><dsp:valueof param="address.address2" valueishtml="false"/></div>
																		<div class="cityStateZip"> <span class="city"><dsp:valueof param="address.city" valueishtml="false"/></span>, <span class="state"><dsp:valueof param="address.state" valueishtml="false"/></span> <span class="zip"><dsp:valueof param="address.postalCode" valueishtml="false"/></span> </div>
																	</div>
																</div>
														  </c:if>
														  <c:if test="${!isShipDefault && isBillDefault}">
														  	<div class="item">
																	<h3 class="bold"><bbbl:label key="lbl_overview_address_billing_default" language ="${pageContext.request.locale.language}"/></h3>
																	<div class="address">
																		<div class="name">
																			<span class="firstName"><dsp:valueof param="address.firstName" valueishtml="false"/></span> <span class="middleName" valueishtml="false"></span> <span class="lastName"><dsp:valueof param="address.lastName" valueishtml="false"/></span>
																		</div>
																		<div class="street1"><dsp:valueof param="address.address1" valueishtml="false"/></div>
																		<div class="street2"><dsp:valueof param="address.address2" valueishtml="false"/></div>
																		<div class="cityStateZip"> <span class="city"><dsp:valueof param="address.city" valueishtml="false"/></span>, <span class="state"><dsp:valueof param="address.state" valueishtml="false"/></span> <span class="zip"><dsp:valueof param="address.postalCode" valueishtml="false"/></span> </div>
																	</div>
																</div>
														  </c:if>
														  <c:if test="${!isShipDefault && !isBillDefault}">
														  	<div class="item">
																	<div class="address">
																		<div class="name">
																			<span class="firstName"><dsp:valueof param="address.firstName" valueishtml="false"/></span> <span class="middleName" valueishtml="false"></span> <span class="lastName"><dsp:valueof param="address.lastName" valueishtml="false"/></span>
																		</div>
																		<div class="street1"><dsp:valueof param="address.address1" valueishtml="false"/></div>
																		<div class="street2"><dsp:valueof param="address.address2" valueishtml="false"/></div>
																		<div class="cityStateZip"> <span class="city"><dsp:valueof param="address.city" valueishtml="false"/></span>, <span class="state"><dsp:valueof param="address.state" valueishtml="false"/></span> <span class="zip"><dsp:valueof param="address.postalCode" valueishtml="false"/></span> </div>
																	</div>
																</div>
														  </c:if>
														  
													  	<c:set var="count" value="${count+1}"/>
												    	</c:forEach>
												    		<c:set target="${addressCounter}" property="total"><dsp:valueof param="sortedArraySize"/></c:set>
															<c:set target="${addressCounter}" property="current">${count}</c:set>
															<div class="itemInfo bold"><bbbl:label key="lbl_overview_showing_x_of_y" language ="${pageContext.request.locale.language}" placeHolderMap="${addressCounter}"/></div>
													  		<a href="${contextPath}/account/address_book.jsp" title="${overview_address_manage_title}" class="itemLink"><bbbl:label key="lbl_overview_address_manage" language ="${pageContext.request.locale.language}"/></a>
												    </c:otherwise>
						              			</c:choose>
						           			 </dsp:oparam>
						           			 <dsp:oparam name="empty">
						           			 <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
						           			 	<c:if test="${empty sortedArray}">
														<bbbt:textArea key="txtarea_overview_address_book_address_not_saved" language ="${pageContext.request.locale.language}"/>
														<div class="button">
															<%-- this functionality still under development --%>
															<a href="${contextPath}/account/address_book.jsp?showAddNewAddress=true"><bbbl:label key="lbl_addressbook_addnewaddress" language ="${pageContext.request.locale.language}"/></a>
														</div>
												</c:if>
						           			 </dsp:oparam>
						          		</dsp:droplet>
										<div class="clear"></div>
								</div>
						</div>
						<div id="myCreditCard" class="section grid_3 omega clearfix">
							<div class="spacer clearfix">
								<h2 class="noMarTop"><bbbl:label key="lbl_overview_my_credit_cards" language ="${pageContext.request.locale.language}"/></h2>
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
											<div class="button">
												<%-- this functionality still under development --%>
												<a href="add_credit_card.jsp"><bbbl:label key="lbl_overview_credit_cards_add" language ="${pageContext.request.locale.language}"/></a>
											</div>
										</c:when>
										<c:when test="${not empty sortedArray}">
											<%-- only show when user has a saved credit card --%>
												<div class="item">
													<h3 class="bold"><bbbl:label key="lbl_overview_credit_cards_preferred" language ="${pageContext.request.locale.language}"/></h3>
													<div class="card">
													<c:set var="ending_with" scope="page">
														<bbbl:label key="lbl_creditcardinfo_endingwith" language="${pageContext.request.locale.language}" />
													</c:set>
															<div class="cardType">
																<div class="bold title"><dsp:valueof bean="Profile.defaultCreditCard.creditCardType"/> ${ending_with} </div>
																<dsp:getvalueof var="creditCard" bean="Profile.defaultCreditCard.creditCardNumber"/>
																<div class="value"><c:out value="${fn:substring(creditCard,fn:length(creditCard)-4,fn:length(creditCard))}" /></div>
															</div>
															<div class="cardName">
																<div class="bold title"><bbbl:label key="lbl_overview_credit_cards_name" language ="${pageContext.request.locale.language}"/></div>
																<div class="value"><span class="firstName"><dsp:valueof bean="Profile.defaultCreditCard.nameOnCard" valueishtml="false"/></span></div>
															</div>
															<div class="cardExpiry">
																<div class="bold title"><bbbl:label key="lbl_overview_credit_cards_expires" language ="${pageContext.request.locale.language}"/></div>
																<div class="value"><dsp:valueof bean="Profile.defaultCreditCard.expirationMonth" converter="number" format="00" valueishtml="false"/>/<dsp:valueof bean="Profile.defaultCreditCard.expirationYear" valueishtml="false"/></div>
															</div>
														</div>
												</div>
														<jsp:useBean id="cardCounter" class="java.util.HashMap" scope="request"/>
														<c:set target="${cardCounter}" property="total"><dsp:valueof param="sortedArraySize"/></c:set>
														<c:set target="${cardCounter}" property="current">${1}</c:set>
														<div class="itemInfo bold"><bbbl:label key="lbl_overview_showing_x_of_y" language ="${pageContext.request.locale.language}" placeHolderMap="${cardCounter}"/></div>
														<a href="view_credit_card.jsp" title="${creditcardinfo_manage_title}" class="itemLink"><bbbl:label key="lbl_creditcardinfo_manage" language ="${pageContext.request.locale.language}"/></a>
										</c:when>
									</c:choose>
								</dsp:oparam>
								<dsp:oparam name="empty">
									<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
							           	<c:if test="${empty sortedArray}">
											<bbbt:textArea key="txtarea_overview_credit_card_address_not_saved" language ="${pageContext.request.locale.language}"/>
											<div class="button">
													<%-- this functionality still under development --%>
												<a href="add_credit_card.jsp"><bbbl:label key="lbl_overview_credit_cards_add" language ="${pageContext.request.locale.language}"/></a>
											</div>
							    		</c:if>
								</dsp:oparam>
							</dsp:droplet>
								<div class="clear"></div>
								</div>
						</div>
                        <div class="clear"></div>
					</div>
					
					<div id="myRegistries" class="section clearfix">
						<div class="spacer clearfix">
							<h2 class="noMarTop"><bbbl:label key="lbl_overview_my_registries" language ="${pageContext.request.locale.language}"/></h2>
							<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet">
									<dsp:param name="profile" bean="Profile"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="value" param="userStatus"/>
												<c:choose>
													<c:when test="${value <= '2'}">
													<%-- only show when user does not have any registry --%>
													<div class="item marRight_10">
															<h3 class="bold"><bbbl:label key="lbl_overview_create_registries" language ="${pageContext.request.locale.language}"/></h3>
															<bbbt:textArea key="txtarea_overview_my_registries" language ="${pageContext.request.locale.language}"/>
															<div class="regType marTop_10">
																<dsp:form>
																<dsp:select bean="GiftRegistryFormHandler.registryEventType" iclass="selector_primary selectRegToCreate" id="typeofregselect">		
																	<dsp:droplet name="GiftRegistryTypesDroplet">
																		<dsp:param name="siteId" value="${currentSiteId}"/>
																		<dsp:oparam name="output">
																		<dsp:option value="" selected="selected" iclass="uniform"><bbbl:label key="lbl_overview_select_type" language="${pageContext.request.locale.language}"/></dsp:option>
																			<dsp:droplet name="ForEach">
																				<dsp:param name="array" param="registryTypes" />
																				<dsp:oparam name="output">
																					<dsp:param name="regTypes" param="element" />	
																					<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
																					<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
																					<dsp:option value="${registryCode}">
																							<a href="#"><dsp:valueof param="element.registryName"></dsp:valueof></a>
																					</dsp:option>
																				</dsp:oparam>
																			</dsp:droplet>
																		</dsp:oparam>
																	</dsp:droplet>
                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                                    <dsp:tagAttribute name="aria-label" value="select the registry you would like to create. You will be redirected to the begin the creation process"/>
																	<dsp:tagAttribute name="aria-hidden" value="false"/>
																</dsp:select>
																<%-- Client DOM XSRF | Part -1
																dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
																	value="${contextPath}/giftregistry/registry_type_select.jsp" />
																<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
																	value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
																<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden"
																	value="myAccount" />
																<dsp:input id="submitRegistryClick"
																	bean="GiftRegistryFormHandler.registryTypes" type="submit" value="submit"
																	style="display:none;"></dsp:input>
																</dsp:form>
																<%-- <script type="text/javascript">
																  $('#submitRegistryClick').attr('disabled', 'disabled');
		   														   function updateFormEnabled() {
																  if ($('#typeofregselect').val() == ' ') {
															      $('#submitRegistryClick').attr('disabled', 'disabled');
                                                                  } else {
                                                                  $('#submitRegistryClick').removeAttr('disabled');
	                                                              $('#submitRegistryClick').click();
                                                                  }
                                                                  }
                                                                 $('#typeofregselect').change(updateFormEnabled);
    	                                                         </script> --%>
															</div>
															<a href="${contextPath}/giftregistry/my_registries.jsp" title="${overview_manage_registries_title}" class="itemLink"><bbbl:label key="lbl_overview_manage_registries" language ="${pageContext.request.locale.language}"/></a> 
														</div>
														<div class="item">
															<bbbt:textArea key="txtarea_overview_registries_why_register" language ="${pageContext.request.locale.language}"/>
														</div>
													</c:when>
													<c:otherwise>
														<%-- only show when user has registries --%>
														<div class="item marRight_10">
															<h3 class="bold"><bbbl:label key="lbl_overview_manage_registries" language ="${pageContext.request.locale.language}"/></h3>
															<div class="button">
																<a href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_overview_manage_registries" language ="${pageContext.request.locale.language}"/></a>
															</div>
														</div>
														<div class="item">
															<bbbt:textArea key="txtarea_overview_registries_create_registry" language ="${pageContext.request.locale.language}"/>
															<div class="regType marTop_10">
																<dsp:form>
																<dsp:select bean="GiftRegistryFormHandler.registryEventType" iclass="selector_primary selectRegToCreate" id="typeofregselect">		
																	<dsp:droplet name="GiftRegistryTypesDroplet">
																		<dsp:param name="siteId" value="${currentSiteId}"/>
																		<dsp:oparam name="output">
																		<!--<option value="" selected="selected" iclass="uniform" class="visuallyhidden"> Select the type of registry you would like to create</option>-->
																		<dsp:option value="" selected="selected" iclass="uniform"><bbbl:label key="lbl_overview_select_type" language="${pageContext.request.locale.language}"/></dsp:option>
																			<dsp:droplet name="ForEach">
																				<dsp:param name="array" param="registryTypes" />
																				<dsp:oparam name="output">
																					<dsp:param name="regTypes" param="element" />	
																					<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
																					<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
																					<dsp:option value="${registryCode}">
																							<a href="#"><dsp:valueof param="element.registryName"></dsp:valueof></a>
																					</dsp:option>
																				</dsp:oparam>
																			</dsp:droplet>
																		</dsp:oparam>
																	</dsp:droplet>
                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                                    <dsp:tagAttribute name="aria-label" value="select the registry you would like to create. You will be redirected to the begin the creation process"/>
																	<dsp:tagAttribute name="aria-hidden" value="false"/>
																</dsp:select>
																<%-- Client DOM XSRF | Part -1
																 dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
																	value="${contextPath}/giftregistry/registry_type_select.jsp" />
																<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
																	value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
																<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden"
																	value="myAccount" />
																<dsp:input id="submitRegistryClick"
																	bean="GiftRegistryFormHandler.registryTypes" type="submit" value="submit"
																	style="display:none;"></dsp:input>
																</dsp:form>
																<%-- <script type="text/javascript">
																 $('#submitRegistryClick').attr('disabled', 'disabled');
																 function updateFormEnabled() {
														         if ($('#typeofregselect').val() == ' ') {
													            $('#submitRegistryClick').attr('disabled', 'disabled');
													         	} else {
														        $('#submitRegistryClick').removeAttr('disabled');
													            $('#submitRegistryClick').click();
														        }
														        }
														        $('#typeofregselect').change(updateFormEnabled);
    													        </script> --%>
															</div>
														</div>
													</c:otherwise>
												</c:choose>												
									</dsp:oparam>
									<dsp:oparam name="empty">
						           			 <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
						           			 	<c:if test="${empty sortedArray}">
														<bbbt:textArea key="txtarea_overview_address_book_address_not_saved" language ="${pageContext.request.locale.language}"/>
														<div class="button">
															<%-- this functionality still under development --%>
															<a href="${contextPath}/account/address_book.jsp?showAddNewAddress=true"><bbbl:label key="lbl_addressbook_addnewaddress" language ="${pageContext.request.locale.language}"/></a>
														</div>
												</c:if>
						           			 </dsp:oparam>
								</dsp:droplet>
								<div class="clear"></div>
							</div>
					</div>
						<c:if test="${FBOn}">
						<div id="myFacebook" class="section clearfix fbConnectWrapOverview">
						<dsp:getvalueof var="fbUnlinkVar" param="fbUnlink" />
						<c:if test="${not empty fbUnlinkVar}">
							<dsp:setvalue bean="FBConnectFormHandler.unLinking" />
						</c:if>
						<dsp:include page="facebook_linking.jsp" flush="true"/>
					</div></c:if>
				</div>
				<c:set var="BedBathCanadaSite">
					<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
				</c:set>
				<div class="grid_3 omega">
					<c:if test="${MapQuestOn}">
						<dsp:getvalueof id="currUrl" value="${pageContext.request.requestURI}" scope="session"/>
						<dsp:include src="${contextPath}/account/frags/favstorewidget.jsp"/>
					</c:if>						
					 <c:if test="${ValueLinkOn}">  
					<div id="myGiftCard" class="section clearfix">
						<div class="spacer clearfix">
							<h2 class="noMarTop"><bbbl:label key='lbl_giftcard_checkbal_heading' language='${pageContext.request.locale.language}' /></h2>
							<dsp:form iclass="frmCheckGiftCardBal" action="${contextPath}/account/check_giftcard_balance.jsp" method="post">
								<div class="clearfix">
									<div class="input noMarBot giftCardNumber">
										<div class="label">
											<label id="lblgiftCardNumberMA" for="giftCardNumberMA"><bbbl:label key="lbl_giftcard_number" language ="${pageContext.request.locale.language}"/></label>
										</div>
										<div class="text">
											<dsp:input type="text" id="giftCardNumberMA" bean="BBBGiftCardFormHandler.giftCardNumber" name="giftCardNumber" maxlength="16" value="">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblgiftCardNumberMA errorgiftCardNumberMA"/>
                                            </dsp:input>
										</div>			
									</div>

									<div class="input giftCardPin">
										<div class="label">
											<label id="lblgiftCardPinMA" for="giftCardPinMA"><bbbl:label key="lbl_giftcard_pin" language ="${pageContext.request.locale.language}"/></label>
										</div>
										<div class="text">
											<dsp:input type="text" id="giftCardPinMA" bean="BBBGiftCardFormHandler.giftCardPin" name="giftCardPin" maxlength="8" value="">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblgiftCardPinMA errorgiftCardPinMA"/>
                                            </dsp:input>
										</div>			
									</div>
								</div>						
								
								<div class="clearfix errorWrap hidden">
									<label class="error serverErrors block"></label>
								</div>
								
								<div class="clearfix giftCardBalanceWrap hidden">
									<div>
										<strong><bbbl:label key="lbl_giftcard_checkbal_giftcard" language ="${pageContext.request.locale.language}"/></strong>&nbsp;<span class="giftCardNumberResult giftCardResultItem"></span>
									</div>
									<div class="bal marTop_10">
										<strong><bbbl:label key="lbl_giftcard_checkbal_balance" language ="${pageContext.request.locale.language}"/></strong>&nbsp;<span class="balance giftCardResultItem"></span>
									</div>
								</div>
								<div class="clearfix marTop_10">
									<div class="button fl">
										<dsp:input type="hidden" bean="BBBGiftCardFormHandler.balance" value=""/>
										<dsp:a href="#" iclass="btnCheckBal"><bbbl:label key="lbl_giftcard_checkbal_button" language ="${pageContext.request.locale.language}"/></dsp:a>
									</div>
								</div>
								
								
								 <%-- 
								<div class="clearfix bal hidden marTop_10">
									<div class="bold title fl"><bbbl:label key="lbl_giftcard_checkbal_balance" language ="${pageContext.request.locale.language}"/></div>
									<div class="value fr"><span class="balance"></span></div>
								</div>
								 --%>
							</dsp:form>
                            <div class="clear"></div>
						</div>
					</div>
					</c:if>
					<div id="myWishList" class="section clearfix">
						<div class="spacer clearfix">
							<h2 class="noMarTop"><bbbl:label key="lbl_wishlist_wishlist" language ="${pageContext.request.locale.language}"/></h2>
							<div class="info">
								<div class="clearfix">
									<dsp:droplet name="BBBWishlistItemCountDroplet">
									<dsp:param name="wishlistId" bean="Profile.wishlist.id" />
									<dsp:param name="siteId" value="${currentSiteId}"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="wishlistItemCount" vartype="java.lang.Object" param="wishlistItemCount"/>
										</dsp:oparam>
									</dsp:droplet>
									<div class="count">${wishlistItemCount}</div>
									<div class="msg"><bbbl:label key="lbl_overview_wishlist" language ="${pageContext.request.locale.language}"/></div>
								</div>
							</div>
							<a href="${contextPath}/wishlist/wish_list.jsp" title="${overview_wishlist_manage_title}" class="itemLink"><bbbl:label key="lbl_overview_wishlist_manage" language ="${pageContext.request.locale.language}"/></a> 
							<div class="clear"></div>
						</div>
					</div>
					<c:if test="${CouponOn}">
					 <div id="myCoupons" class="section clearfix"
					    data-ajax-url="${contextPath}/account/coupon_details.jsp">
					    <div class="textCenter">
   							<img width="20" height="20" alt="small loader" class="loader" src="/_assets/global/images/widgets/small_loader.gif" />
						</div>
					</div>
					</c:if>
				</div>
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
				 <c:if test="${currentSiteId == BedBathUSSite && TellApartOn  || currentSiteId == BuyBuyBabySite && TellApartOn_baby  || currentSiteId == BedBathCanadaSite && TellApartOn_ca}">
				<bbb:tellApart actionType="login"/>
				</c:if>
			</c:if>
		</c:if>
		<dsp:getvalueof id="isReturningUser" value="true" scope="session"/>
		<%--tellApart integration End --%>
<%-- <script type="text/javascript">
	function callMyAccRegistryTypesFormHandler() {
		if (document.getElementById('typeofregselect').value!="")
		{
			document.getElementById("submitRegistryClick").click();
		}

	}
</script> --%>
<%-- YourAmigo code starts  6/18/2013--%>
		<c:if test="${YourAmigoON}">
		<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
		<c:if test="${!isTransient}">
	<%-- ######################################################################### --%>
	<%--  Configuring the javascript for tracking signups (to be placed on the     --%>
	<%--  signup confirmation page, if any).                                       --%>
	<%-- ######################################################################### --%>
<c:choose>
<c:when test="${(currentSiteId eq BuyBuyBabySite)}">
<script src="https://support.youramigo.com/52657396/tracev2.js"></script>
<c:set var="ya_cust" value="52657396"></c:set>
</c:when>
<c:when test="${(currentSiteId eq BedBathUSSite)}">
<script src="https://support.youramigo.com/73053126/trace.js"></script>
<c:set var="ya_cust" value="73053126"></c:set>
</c:when>
<c:when test="${(currentSiteId eq BedBathCanadaSite)}">
<script src="https://support.youramigo.com/73053127/tracev2.js"></script>
<c:set var="ya_cust" value="73053127"></c:set>
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

<c:import url="/selfservice/find_in_store.jsp"/>
<%--  New version of view map/get directions --%>
<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
</jsp:body>
<jsp:attribute name="footerContent">
<dsp:getvalueof var="newRegistration" vartype="boolean" bean="Profile.newRegistration" />
<dsp:getvalueof var="emailOptIn"  bean="ProfileFormHandler.value.receiveEmail" />
<script type="text/javascript">
	var newRegistration ='${newRegistration}';
	var emailOptIn = '${emailOptIn}';
	if((newRegistration == 'true')&& (emailOptIn == 'yes')){
	rkg_micropixel('${currentSiteId}','ATGEmail');
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
