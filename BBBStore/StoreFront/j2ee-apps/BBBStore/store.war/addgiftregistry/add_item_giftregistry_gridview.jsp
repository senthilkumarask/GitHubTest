<dsp:page>

	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="ltlFlag" param="ltlFlag" />
	<dsp:getvalueof var="isCustomizationRequired"
		param="isCustomizationRequired" />
	<dsp:getvalueof var="disableCTA" param="disableCTA" />
	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<dsp:getvalueof var="recommendedregistry"
		bean="SessionBean.registrySummaryVO" />
	<dsp:getvalueof var="itemAlreadyPersonalized"
		param="itemAlreadyPersonalized" />
	<c:set var="enableLTLRegForSite">
		<bbbc:config key="enableLTLRegForSite"
			configName="FlagDrivenFunctions" />
	</c:set>
	<input type="hidden" name="enableLTLRegForSite"
		value="${enableLTLRegForSite}" />

	<c:set var="submitAddToRegistryBtn">
		<bbbl:label key='lbl_add_to_registry'
			language="${pageContext.request.locale.language}"></bbbl:label>
	</c:set>
	<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues" />
	<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}"
		var="registrySkinnyVOList" />
	<dsp:getvalueof var="sizeValue"
		value="${fn:length(registrySkinnyVOList)}" />
	<c:if test="${empty disableCTA}">
		<c:set var="disableCTA" value="${isCustomizationRequired}" />
	</c:if>
	<c:choose>
		<c:when test="${transient == 'false'}">
			<c:choose>
				<c:when
					test="${sizeValue>1}">
					<c:choose>
						<c:when
							test="${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false'))}">
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
														<option value="${regId}" class="${event_type}" data-notify-reg="true">
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
															<dsp:getvalueof var="regPublic" param="regDetails.isPublic"/>
															<c:if test="${regPublic eq '1'}">
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
															</c:if>
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
												<option value="${regId}" class="${event_type}" data-notify-reg="true">
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
													<dsp:getvalueof var="regPublic" param="regDetails.isPublic"/>
													<c:if test="${regPublic eq '1'}">	
														<c:choose>
															<c:when test="${itemAlreadyPersonalized }">
																<dsp:option iclass="recommendationPopup"
																	disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
															</c:when>
															<c:otherwise>
																<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
															</c:otherwise>
														</c:choose>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										</c:if>
										<dsp:tagAttribute name="aria-required" value="false" />
										<dsp:tagAttribute name="aria-hidden" value="false" />
									</dsp:select>
									<input class="btnAjaxSubmitSFL moveToReg hidden"
												id="btnMoveToRegSel${wishListItemId}" type="button"
												value="Move to Registry"
												onclick="omniAddToRegis(this, '${prodID}', '${quantity}', '${itemPrice}', '${skuID}');"
												data-ajax-frmID="frmRegSaveForLater" role="button"
												aria-labelledby="btnMoveToRegSel${wishListItemId}"
												aria-pressed="false" />
								</div>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
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
											<option value="${regId}" class="${event_type}" data-notify-reg="true">
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
												<dsp:getvalueof var="regPublic" param="regDetails.isPublic"/>
													<c:if test="${regPublic eq '1'}">
													<c:choose>
														<c:when test="${itemAlreadyPersonalized }">
															<dsp:option iclass="recommendationPopup"
																disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
														</c:when>
														<c:otherwise>
															<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
														</c:otherwise>
													</c:choose>
												</c:if>
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
									<dsp:tagAttribute name="aria-required" value="false" />
									<dsp:tagAttribute name="aria-hidden" value="false" />
								</dsp:select>
							</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="${sizeValue==1}">
					<c:choose>
						<c:when test="${recommendedregistry != null}">
						<div class="fl addToRegistry addToRegistrySel">
						<c:choose>
							<c:when test="${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false'))}">
								<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
								<dsp:option> <bbbl:label key="lbl_add_to_registry"
								language="${pageContext.request.locale.language}" /></dsp:option>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" value="${registrySkinnyVOList}" />
										<dsp:oparam name="output">
											<dsp:param name="futureRegList" param="element" />

											<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
											<dsp:getvalueof var="event_type" param="element.eventType" />
											<option value="${regId}"  iclass="${event_type}" data-notify-reg="true">
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
												<dsp:getvalueof var="regPublic" param="regDetails.isPublic"/>
												<c:if test="${regPublic eq '1'}">
													<c:choose>
														<c:when test="${itemAlreadyPersonalized }">
															<dsp:option iclass="recommendationPopup" disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
														</c:when>
														<c:otherwise>
															<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
														</c:otherwise>																		
													</c:choose>
												</c:if>	
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
											<option value="${regId}"  class="${event_type}" data-notify-reg="true">
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
												<dsp:getvalueof var="regPublic" param="regDetails.isPublic"/>
												<c:if test="${regPublic eq '1'}">
													<c:choose>
														<c:when test="${itemAlreadyPersonalized }">
															<dsp:option iclass="recommendationPopup" disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
														</c:when>
														<c:otherwise>
															<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
														</c:otherwise>																		
													</c:choose>
												</c:if>
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
									<input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
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
								<div class="button <c:if test='${disableCTA || isInternationalCustomer ||(ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>button_disabled</c:if>">
								<input class="btnAddToRegistry" name="btnAddToRegistry"
										type="button" data-notify-reg="true" value="${submitAddToRegistryBtn}" role="button" aria-pressed="false" <c:if test='${isInternationalCustomer || disableCTA ||( ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>disabled="disabled"</c:if>/>
								</div>
							</div>

						</c:otherwise>

					</c:choose>

				</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${recommendedregistry != null  && not empty recommendedregistry}">
				<div class="fl addToRegistry addToRegistrySel">
					<c:choose>
						<c:when test="${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false'))}">
							<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
								<dsp:option> <bbbl:label key="lbl_add_to_registry"
												language="${pageContext.request.locale.language}" /></dsp:option>
								<dsp:option iclass="createNewRegistry" value="${submitAddToRegistryBtn}" ><bbbl:label key="lbl_create_your_registry" language="${pageContext.request.locale.language}"/></dsp:option>
								<c:if test="${recommendedregistry != null}">
								<dsp:option disabled="true"><bbbl:label key="lbl_reg_friends_registries" language="${pageContext.request.locale.language}"></bbbl:label> </dsp:option>									
								<dsp:droplet name="ForEach">
									<dsp:param name="array" value="${recommendedregistry}"/>
									<dsp:oparam name="output">
										<dsp:param name="regDetails" param="element"/>
										<dsp:getvalueof var="regNum" param="regDetails.registryId"/>
										<dsp:getvalueof var="eventType" param="regDetails.eventType"/>
										<dsp:getvalueof var="profileName" param="regDetails.primaryRegistrantFirstName"/>
										<dsp:getvalueof var="regPublic" param="regDetails.isPublic"/>
											<c:if test="${regPublic eq '1'}">
											<c:choose>
												<c:when test="${itemAlreadyPersonalized }">
													<dsp:option iclass="recommendationPopup" disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
												</c:when>
												<c:otherwise>
													<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
												</c:otherwise>																		
											</c:choose>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								</c:if>
								<dsp:tagAttribute name="aria-hidden" value="false"/>
								<dsp:tagAttribute name="disabled" value="disabled" />
							</dsp:select>	
						</c:when>
						<c:otherwise>
							<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
								<dsp:option> <bbbl:label key="lbl_add_to_registry"
												language="${pageContext.request.locale.language}" /></dsp:option>
								<dsp:option iclass="createNewRegistry" value="${submitAddToRegistryBtn}" ><bbbl:label key="lbl_create_your_registry" language="${pageContext.request.locale.language}"/></dsp:option>
								<c:if test="${recommendedregistry != null}">
								<dsp:option disabled="true"><bbbl:label key="lbl_reg_friends_registries" language="${pageContext.request.locale.language}"></bbbl:label> </dsp:option>									
								<dsp:droplet name="ForEach">
									<dsp:param name="array" value="${recommendedregistry}"/>
									<dsp:oparam name="output">
										<dsp:param name="regDetails" param="element"/>
										<dsp:getvalueof var="regNum" param="regDetails.registryId"/>
										<dsp:getvalueof var="eventType" param="regDetails.eventType"/>
										<dsp:getvalueof var="profileName" param="regDetails.primaryRegistrantFirstName"/>
										<dsp:getvalueof var="regPublic" param="regDetails.isPublic"/>
											<c:if test="${regPublic eq '1'}">
											<c:choose>
												<c:when test="${itemAlreadyPersonalized }">
													<dsp:option iclass="recommendationPopup" disabled="disabled" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
												</c:when>
												<c:otherwise>
													<dsp:option iclass="recommendationPopup" value="${regNum}">${profileName}'s ${eventType}</dsp:option>
												</c:otherwise>																		
											</c:choose>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								</c:if>
								<dsp:tagAttribute name="aria-hidden" value="false"/>
							</dsp:select>
						</c:otherwise>
					</c:choose>
				</div>
				</c:when>
				<c:otherwise>
					<div class="fl addToRegistry"> <%-- addToRegistryCreate ... this class should only be used if the value/label of the button is "Create Registry" --%>
					<div class="button  <c:if test='${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>button_disabled</c:if>">
						<input type="button" class="btnAddToRegNoRegistry" value="${submitAddToRegistryBtn}" name="btnAddToRegNoRegistry" <c:if test='${isInternationalCustomer || disableCTA || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>disabled="disabled"</c:if> />
					</div>
					</div>
				</c:otherwise>
	
			</c:choose>
		</c:otherwise>
		</c:choose>
		</c:when>
		<c:when test="${transient == 'true'}">
			<div class="addToRegistry clearfix">
				<div class="button <c:if test='${isCustomizationRequired || isInternationalCustomer|| (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>button_disabled</c:if>">
					<input class="addItemToRegNoLogin" name="addItemToRegNoLogin" type="button" <c:if test='${isCustomizationRequired || isInternationalCustomer|| (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>disabled="disabled"</c:if>  value="${submitAddToRegistryBtn}" data-notify-reg="true"/>
				</div>
			</div>
		</c:when>
	</c:choose>
</dsp:page>