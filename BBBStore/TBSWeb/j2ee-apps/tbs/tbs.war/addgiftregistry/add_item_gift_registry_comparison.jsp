<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="ltlFlag" param="ltlFlag" />
	<dsp:getvalueof var="isCustomizationRequired" param="isCustomizationRequired" />
	<dsp:getvalueof var="disableCTA" param="disableCTA" />
	<dsp:getvalueof var="isItemExcluded" param="isItemExcluded" />
	<dsp:getvalueof var="caDisabled" param="caDisabled" />
	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<dsp:getvalueof var="recommendedregistry"
		bean="SessionBean.registrySummaryVO" />
	<dsp:getvalueof var="itemAlreadyPersonalized"
		param="itemAlreadyPersonalized" />

	<c:set var="enableLTLRegForSite">
		<bbbc:config key="enableLTLRegForSite"
			configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="disableRegistry" value="${false}" />
		 <c:if test="${(ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false'))}">
			<c:set var="disableRegistry" value="${true}" />
		</c:if>
	<input type="hidden" name="enableLTLRegForSite"
		value="${enableLTLRegForSite}" />

	<c:set var="submitAddToRegistryBtn">
		<bbbl:label key='lbl_add_to_registry'
			language="${pageContext.request.locale.language}"></bbbl:label>
	</c:set>
	<dsp:droplet name="AddItemToGiftRegistryDroplet">
                <dsp:param name="siteId" value="${appid}"/>
            </dsp:droplet>
	<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues" />
	<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}"
		var="registrySkinnyVOList" />
	<dsp:getvalueof var="sizeValue"
		value="${fn:length(registrySkinnyVOList)}" />
	<c:if test="${empty disableCTA}">
		<c:set var="disableCTA" value="${isCustomizationRequired}" />
	</c:if>
	
	
	<dsp:droplet name="ForEach">
			<dsp:param name="array" value="${registrySkinnyVOList}" />
		<dsp:oparam name="output">
			<dsp:param name="futureRegList" param="element" />
			<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
			<dsp:getvalueof var="alternateNumber"
				param="futureRegList.alternatePhone" />
			<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
			 
				<%-- <input type="hidden" id="${regId}" value="${alternateNumber}" name="alternateNum" class="onCompareAltNumber"/> --%>
				</dsp:oparam>
				</dsp:droplet>
	<c:choose>
		<c:when test="${transient == 'false'}">
		<dsp:getvalueof var="kickStarterPage" param="kickStarterPage" />
			<c:choose>
				<c:when test="${kickStarterPage=='yes' && sizeValue>1 }">
						<dsp:droplet name="ForEach">
							<dsp:param name="array" value="${registrySkinnyVOList}" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="index" param="index" />
									<c:choose>
										<c:when test="${index==1}">									 
											<dsp:param name="futureRegList" param="element" />
										 	<dsp:getvalueof var="regId" param="futureRegList.registryId" />
										 	<dsp:getvalueof var="registryName" param="futureRegList.eventType" />
											<c:if test="${empty regIdLst}">	
												<input type="hidden" value="${regId}" name="registryId" class="addItemToRegis" />
											</c:if>
											<c:if test="${empty registryNameLst}">	
												<input type="hidden" value="${registryName}" name="registryName" class="addItemToRegis" />
											</c:if>
									 	</c:when>
								   	</c:choose>
								</dsp:oparam>
							</dsp:droplet>
						
					<div class="addToRegistry cb noPad">
					<div class="<c:if test='${disableRegistry || isItemExcluded || caDisabled || disableCTA}'>button_disabled</c:if>">	
						<a class="btnAddToRegistry <c:if test='${disableRegistry || isItemExcluded || disableCTA || caDisabled }'>disabled</c:if>" href="javascript:void(0);"><bbbl:label key='lbl_compare_add_to_registry' language="${pageContext.request.locale.language}" /></a>
					</div>
					</div>
				</c:when>
				<c:when
					test="${sizeValue>1}">
					<c:choose>
						<c:when
							test="${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false')) || isItemExcluded || caDisabled}">
							<c:choose>
								<c:when test="${currentSiteId eq 'BuyBuyBaby'}">
									<div class="fr btnAddToRegistrySel">
									<div class="upperCase addToRegistry addToRegistrySel">
										<div class="select button_select">
											<dsp:setvalue bean="GiftRegistryFormHandler.registryId"
												value="" />
											<dsp:select bean="GiftRegistryFormHandler.registryId"
												name="reqRegistryId"
												iclass="triggerSubmit frmAjaxSubmitData addItemToRegis addItemToRegMultipleReg selector_primaryAlt omniRegData">
												<dsp:tagAttribute name="data-submit-button"
													value="btnMoveToRegSel${wishListItemId}" />
												<dsp:tagAttribute name="aria-required" value="false" />
												<dsp:option>
													<bbbl:label key="lbl_compare_add_to_registry"
														language="${pageContext.request.locale.language}" />
												</dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" value="${registrySkinnyVOList}" />
													<dsp:oparam name="output">
														<dsp:param name="futureRegList" param="element" />
														<dsp:getvalueof var="regId"
															param="futureRegList.registryId" />
														<dsp:getvalueof var="event_type" param="element.eventType" />
														<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
														<option value="${regId}" class="${event_type}" data-poboxflag="${poBoxAddress}">
															<dsp:valueof param="element.eventType"></dsp:valueof>
															<dsp:valueof param="element.eventDate"></dsp:valueof>
														</option>
													</dsp:oparam>
												</dsp:droplet>
												<c:if
													test="${recommendedregistry != null && not empty recommendedregistry}">
													<dsp:option disabled="disabled">
														<bbbl:label key="lbl_reg_friends_registries"
															language="${pageContext.request.locale.language}"></bbbl:label>
													</dsp:option>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" value="${recommendedregistry}" />
														<dsp:oparam name="output">
															<dsp:param name="regDetails" param="element" />
															<dsp:getvalueof var="regNum" param="regDetails.registryId" />
															<dsp:getvalueof var="eventType"
																param="regDetails.eventType" />
															<dsp:getvalueof var="profileName"
																param="regDetails.primaryRegistrantFirstName" />
															<c:choose>
																<c:when test="${itemAlreadyPersonalized }">
																	<dsp:option iclass="recommendationPopup"
																		disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
																</c:when>
																<c:otherwise>
																	<dsp:option iclass="recommendationPopup"
																		value="${regNum}">${profileName}'s ${eventType}</dsp:option>
																</c:otherwise>
															</c:choose>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>
												<dsp:tagAttribute name="disabled" value="disabled" />
											</dsp:select>
											<input class="btnAjaxSubmitSFL moveToReg hidden"
												id="btnMoveToRegSel${wishListItemId}" type="button"
												value="Move to Registry"
												onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}');"
												data-ajax-frmID="frmRegSaveForLater" role="button"
												aria-labelledby="btnMoveToRegSel${wishListItemId}"
												aria-pressed="false" />
										</div>
									</div>
									<div class="clear"></div>
									<div class="clear"></div>
								</div>
								</c:when>
								
								
								<c:otherwise>
								<div class="prodGridRegistry addToRegistry addItemToRegNoLogin marBottom_5 fr cb">
								<dsp:droplet name="ForEach">
																					<dsp:param name="array" value="${registrySkinnyVOList}" />
																				<dsp:oparam name="output">
																					<dsp:param name="futureRegList" param="element" />
																					<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																					<dsp:getvalueof var="alternateNumber"
																						param="futureRegList.alternatePhone" />
																					<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
																						<input type="hidden" id="${regId}" value="${alternateNumber}" name="alternateNum" class="onCompareAltNumber"/>
																						</dsp:oparam>
																						</dsp:droplet>
									<dsp:select bean="GiftRegistryFormHandler.registryId"
										id="addItemToRegMultipleRegID" name="registryId"
										iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection disabled ">
										<dsp:option>
											<bbbl:label key="lbl_add_to_registry"
												language="${pageContext.request.locale.language}" />
										</dsp:option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" value="${registrySkinnyVOList}" />
											<dsp:oparam name="output">
												<dsp:param name="futureRegList" param="element" />
												<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
												<dsp:getvalueof var="regId" param="futureRegList.registryId" />
												<dsp:getvalueof var="event_type" param="element.eventType" />
												<dsp:getvalueof var="alternateNumber" param="futureRegList.alternatePhone" />
												
												<option value="${regId}" class="${event_type}" data-poboxflag="${poBoxAddress}">
													<dsp:valueof param="element.eventType"></dsp:valueof>
													<dsp:valueof param="element.eventDate"></dsp:valueof>
												</option>
											</dsp:oparam>
										</dsp:droplet>
										<c:if
											test="${recommendedregistry != null && not empty recommendedregistry}">
											<dsp:option disabled="disabled">
												<bbbl:label key="lbl_reg_friends_registries"
													language="${pageContext.request.locale.language}"></bbbl:label>
											</dsp:option>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" value="${recommendedregistry}" />
												<dsp:oparam name="output">
													<dsp:param name="regDetails" param="element" />
													<dsp:getvalueof var="regNum" param="regDetails.registryId" />
													<dsp:getvalueof var="eventType" param="regDetails.eventType" />
													<dsp:getvalueof var="profileName"
														param="regDetails.primaryRegistrantFirstName" />
													<c:choose>
														<c:when test="${itemAlreadyPersonalized }">
															<dsp:option iclass="recommendationPopup"
																disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
														</c:when>
														<c:otherwise>
															<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
														</c:otherwise>
													</c:choose>
												</dsp:oparam>
											</dsp:droplet>
										</c:if>
										<dsp:tagAttribute name="aria-required" value="false" />
										<dsp:tagAttribute name="aria-hidden" value="false" />
										<dsp:tagAttribute name="disabled" value="disabled"/>
									</dsp:select>
								</div>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test="${sizeValue>1 && not isInternationalCustomer && !isCustomizationRequired && !isItemExcluded && !caDisabled }">
								<div class="prodGridRegistry addToRegistry addItemToRegNoLogin marBottom_5 fr cb">
								<dsp:select bean="GiftRegistryFormHandler.registryId"
									id="addItemToRegMultipleRegID" name="registryId"
									iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
									<dsp:option>
										<bbbl:label key="lbl_add_to_registry"
											language="${pageContext.request.locale.language}" />
									</dsp:option>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" value="${registrySkinnyVOList}" />
										<dsp:oparam name="output">
											<dsp:param name="futureRegList" param="element" />

											<dsp:getvalueof var="regId" param="futureRegList.registryId" />
											<dsp:getvalueof var="event_type" param="element.eventType" />
											<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
											<option value="${regId}" class="${event_type}" data-poboxflag="${poBoxAddress}" >
												<dsp:valueof param="element.eventType"></dsp:valueof>
												<dsp:valueof param="element.eventDate"></dsp:valueof>
											</option>
										</dsp:oparam>
									</dsp:droplet>
									<c:if
										test="${recommendedregistry != null && not empty recommendedregistry}">
										<dsp:option disabled="disabled">
											<bbbl:label key="lbl_reg_friends_registries"
												language="${pageContext.request.locale.language}"></bbbl:label>
										</dsp:option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" value="${recommendedregistry}" />
											<dsp:oparam name="output">
												<dsp:param name="regDetails" param="element" />
												<dsp:getvalueof var="regNum" param="regDetails.registryId" />
												<dsp:getvalueof var="eventType" param="regDetails.eventType" />
												<dsp:getvalueof var="profileName"
													param="regDetails.primaryRegistrantFirstName" />
												<c:choose>
													<c:when test="${itemAlreadyPersonalized }">
														<dsp:option iclass="recommendationPopup"
															disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
													</c:when>
													<c:otherwise>
														<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
													</c:otherwise>
												</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
									<dsp:tagAttribute name="aria-required" value="false" />
									<dsp:tagAttribute name="aria-hidden" value="false" />
								</dsp:select>
							</div>
						</c:when>
					</c:choose>
				</c:when>
				<c:when test="${sizeValue==1}">

					<c:choose>
						<c:when test="${recommendedregistry != null}">
						<div class="fl addToRegistry addToRegistrySel">
						<c:choose>						
							<c:when test="${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false'))  || isItemExcluded || caDisabled}">
							
								<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
								<dsp:option> <bbbl:label key="lbl_add_to_registry" language="${pageContext.request.locale.language}" /></dsp:option>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" value="${registrySkinnyVOList}" />
										<dsp:oparam name="output">
											<dsp:param name="futureRegList" param="element" />
											<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
											<dsp:getvalueof var="event_type" param="element.eventType" />
											<dsp:getvalueof var="alternateNumber" param="futureRegList.alternatePhone" />
											<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
											<%-- <input type="hidden" value="${regId}" name="registryId" class="addItemToRegis onCompareAltNumber" /> --%>																					
											<option value="${regId}"  class="${event_type}" data-poboxflag="${poBoxAddress}">
												<dsp:valueof param="element.eventType"/> <dsp:valueof param="element.eventDate"/>
											</option>
										</dsp:oparam>
									</dsp:droplet>
									<c:if test="${recommendedregistry != null && not empty recommendedregistry}">
									<dsp:option disabled="disabled"><bbbl:label key="lbl_reg_friends_registries" language="${pageContext.request.locale.language}"></bbbl:label> </dsp:option>														
									<dsp:droplet name="ForEach">
										<dsp:param name="array" value="${recommendedregistry}"/>
											<dsp:oparam name="output">
												<dsp:param name="regDetails" param="element"/>
												<dsp:getvalueof var="regNum" param="regDetails.registryId"/>
												<dsp:getvalueof var="eventType" param="regDetails.eventType"/>
												<dsp:getvalueof var="profileName" param="regDetails.primaryRegistrantFirstName"/>
												<c:choose>
													<c:when test="${itemAlreadyPersonalized }">
														<dsp:option iclass="recommendationPopup" disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
													</c:when>
													<c:otherwise>
														<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
													</c:otherwise>																		
												</c:choose>
											</dsp:oparam>
									</dsp:droplet>
									</c:if>
									<dsp:tagAttribute name="aria-required" value="false"/>
									<dsp:tagAttribute name="aria-hidden" value="false"/>  
									<dsp:tagAttribute name="disabled" value="disabled"/>
								</dsp:select>
							</c:when>
						<c:otherwise>
						
							<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
								<dsp:option> <bbbl:label key="lbl_add_to_registry"
								language="${pageContext.request.locale.language}" /></dsp:option>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" value="${registrySkinnyVOList}" />
										<dsp:oparam name="output">
											<dsp:param name="futureRegList" param="element" />

											<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
											<dsp:getvalueof var="event_type" param="element.eventType" />
											<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
											<option value="${regId}" class="${event_type}" data-poboxflag="${poBoxAddress}" >
												<dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
											</option>
										</dsp:oparam>
									</dsp:droplet>
									<c:if test="${recommendedregistry != null && not empty recommendedregistry}">
									<dsp:option disabled="disabled"><bbbl:label key="lbl_reg_friends_registries" language="${pageContext.request.locale.language}"></bbbl:label> </dsp:option>														
									<dsp:droplet name="ForEach">
										<dsp:param name="array" value="${recommendedregistry}"/>
											<dsp:oparam name="output">
												<dsp:param name="regDetails" param="element"/>
												<dsp:getvalueof var="regNum" param="regDetails.registryId"/>
												<dsp:getvalueof var="eventType" param="regDetails.eventType"/>
												<dsp:getvalueof var="profileName" param="regDetails.primaryRegistrantFirstName"/>
												<c:choose>
													<c:when test="${itemAlreadyPersonalized }">
														<dsp:option iclass="recommendationPopup" disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
													</c:when>
													<c:otherwise>
														<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
													</c:otherwise>																		
												</c:choose>
											</dsp:oparam>
									</dsp:droplet>
									</c:if>
									<dsp:tagAttribute name="aria-required" value="false"/>
									<dsp:tagAttribute name="aria-hidden" value="false"/>
								</dsp:select>
						</c:otherwise>
						</c:choose>
							</div>

						</c:when>
						<c:otherwise>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${registrySkinnyVOList}" />
								<dsp:oparam name="output">
									<dsp:param name="futureRegList" param="element" />
									<dsp:getvalueof var="regId"
										param="futureRegList.registryId" />
										<dsp:getvalueof var="registryName"
										param="futureRegList.eventType" />
										<dsp:getvalueof var="alternateNumber" param="futureRegList.alternatePhone" />	
										<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
									<input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis onCompareAltNumber" />
									<input  type="hidden"value="${registryName}" name="registryName" class="addItemToRegis" />
									 <c:if test="${not empty regId}">
                                                           <dsp:setvalue  bean="SessionBean.registryId" value="${regId}"/>
                                              </c:if>
                                             <c:if test="${not empty registryName}">
                                                             <dsp:setvalue  bean="SessionBean.eventType" value="${registryName}"/>
                                              </c:if>
								</dsp:oparam>
							</dsp:droplet>
							<div class="fl addToRegistry">
								<div class=" <c:if test='${disableCTA || isInternationalCustomer ||(ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))  || isItemExcluded ||  caDisabled}'>button_disabled</c:if>">
								<input type="hidden" id="${regId}" value="${alternateNumber}" name="alternateNum" class="onCompareAltNumber"/>								
								<input class="tiny button expand secondary btnAddToRegistry" name="btnAddToRegistry"
										type="button" value="${submitAddToRegistryBtn}" role="button" data-poboxflag="${poBoxAddress}" aria-pressed="false" <c:if test='${isInternationalCustomer || disableCTA ||( ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false")) || isItemExcluded || caDisabled}'>disabled="disabled"</c:if>/>
								</div>
							</div>

						</c:otherwise>

						</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when
							test="${recommendedregistry != null  && not empty recommendedregistry}">
							<div class="fl addToRegistry addToRegistrySel">
								<c:choose>
									<c:when test="${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false')) || isItemExcluded || caDisabled}">
										<dsp:select bean="GiftRegistryFormHandler.registryId"
											id="addItemToRegMultipleRegID" name="registryId"
											iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
											<dsp:option>
												<bbbl:label key="lbl_add_to_registry"
													language="${pageContext.request.locale.language}" />
											</dsp:option>
											<dsp:option iclass="createNewRegistry"
												value="${submitAddToRegistryBtn}">
												<bbbl:label key="lbl_create_your_registry"
													language="${pageContext.request.locale.language}" />
											</dsp:option>
											<c:if test="${recommendedregistry != null}">
												<dsp:option disabled="true">
													<bbbl:label key="lbl_reg_friends_registries"
														language="${pageContext.request.locale.language}"></bbbl:label>
												</dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" value="${recommendedregistry}" />
													<dsp:oparam name="output">
														<dsp:param name="regDetails" param="element" />
														<dsp:getvalueof var="regNum" param="regDetails.registryId" />
														<dsp:getvalueof var="eventType" param="regDetails.eventType" />
														<dsp:getvalueof var="profileName"
															param="regDetails.primaryRegistrantFirstName" />
														<c:choose>
															<c:when test="${itemAlreadyPersonalized }">
																<dsp:option iclass="recommendationPopup"
																	disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
															</c:when>
															<c:otherwise>
																<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
															</c:otherwise>
														</c:choose>
													</dsp:oparam>
												</dsp:droplet>
											</c:if>
											<dsp:tagAttribute name="aria-hidden" value="false" />
											<dsp:tagAttribute name="disabled" value="disabled" />
										</dsp:select>
									</c:when>
									<c:otherwise>
										<dsp:select bean="GiftRegistryFormHandler.registryId"
											id="addItemToRegMultipleRegID" name="registryId"
											iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
											<dsp:option>
												<bbbl:label key="lbl_add_to_registry"
													language="${pageContext.request.locale.language}" />
											</dsp:option>
											<dsp:option iclass="createNewRegistry"
												value="${submitAddToRegistryBtn}">
												<bbbl:label key="lbl_create_your_registry"
													language="${pageContext.request.locale.language}" />
											</dsp:option>
											<c:if test="${recommendedregistry != null}">
												<dsp:option disabled="true">
													<bbbl:label key="lbl_reg_friends_registries"
														language="${pageContext.request.locale.language}"></bbbl:label>
												</dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" value="${recommendedregistry}" />
													<dsp:oparam name="output">
														<dsp:param name="regDetails" param="element" />
														<dsp:getvalueof var="regNum" param="regDetails.registryId" />
														<dsp:getvalueof var="eventType" param="regDetails.eventType" />
														<dsp:getvalueof var="profileName"
															param="regDetails.primaryRegistrantFirstName" />
														<c:choose>
															<c:when test="${itemAlreadyPersonalized }">
																<dsp:option iclass="recommendationPopup"
																	disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
															</c:when>
															<c:otherwise>
																<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
															</c:otherwise>
														</c:choose>
													</dsp:oparam>
												</dsp:droplet>
											</c:if>
											<dsp:tagAttribute name="aria-hidden" value="false" />
										</dsp:select>
									</c:otherwise>
								</c:choose>
								
							</div>
						</c:when>
						<c:otherwise>
							<div class="addToRegistry marBottom_5 fr cb">
								<div class="addToRegistrybtn <c:if test='${isCustomizationRequired || isInternationalCustomer|| (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))  || isItemExcluded || caDisabled}'>button_disabled</c:if>">
									<input class="tiny button expand secondary btnAddToRegNoRegistry" name="btnAddToRegNoRegistry" type="button"
										<c:if test='${isCustomizationRequired || isInternationalCustomer|| (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false")) || isItemExcluded  || caDisabled}'>disabled="disabled"</c:if>
										value="${submitAddToRegistryBtn}" />
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test="${transient == 'true'}">
			<div class="addToRegistry clearfix">
				<div class="<c:if test='${isCustomizationRequired || isInternationalCustomer|| (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false")) || isItemExcluded ||  caDisabled}'>button_disabled</c:if>">
					<input class="addItemToRegNoLogin tiny button expand secondary" name="addItemToRegNoLogin" type="button" <c:if test='${isCustomizationRequired || isInternationalCustomer|| (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))  || isItemExcluded || caDisabled}'>disabled="disabled"</c:if>  value="${submitAddToRegistryBtn}" />
				</div>
			</div>
		</c:when>
	</c:choose>
</dsp:page>