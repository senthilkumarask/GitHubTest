<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
		<dsp:importbean bean="/atg/userprofiling/Profile" />
		<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
		<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
		<dsp:importbean bean="/atg/multisite/Site"/>
      <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
		<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />		 
		<dsp:getvalueof var="appid" bean="Site.id" />
		<dsp:getvalueof var="ltlFlag" param="ltlFlag" />
		<dsp:getvalueof var="ltlProductFlag" param="ltlProductFlag" />
		<dsp:getvalueof var="isCustomizationRequired" param="isCustomizationRequired"/>
		<dsp:getvalueof var="disableCTA" param="disableCTA"/>
		<dsp:getvalueof var="transient" bean="Profile.transient" />
		<dsp:getvalueof var="itemAlreadyPersonalized" param="itemAlreadyPersonalized" />
		<c:if test="${empty disableCTA}">
			<c:set var="disableCTA" value="${isCustomizationRequired}" />
		</c:if>
		
		<dsp:droplet name="AddItemToGiftRegistryDroplet">
		<dsp:param name="siteId" value="${appid}"/>
			</dsp:droplet>
			<dsp:getvalueof var="recommendedregistry" bean="SessionBean.registrySummaryVO" />
			<c:set var="enableLTLRegForSite">
				<bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" />
			</c:set>
			<input type="hidden" name="enableLTLRegForSite" value="${enableLTLRegForSite}"/>

			 <c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_add_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
					<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
					<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
					<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
				    <dsp:getvalueof var="sizeValueReccomndReg" value="${fn:length(recommendedregistry)}"/>
                    <c:if test="${sizeValueReccomndReg>0}">
                       <input type="hidden" id="recommendRegPresent" value="true"/>
                     </c:if>
					
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
													         <dsp:getvalueof var="regId"
														                  param="futureRegList.registryId" />
														     <dsp:getvalueof var="registryName"
														                    param="futureRegList.eventType" />
														     <dsp:getvalueof var="registryAltPhoneNumber"
														                    param="futureRegList.alternatePhone" />
												    <c:if test="${empty regIdLst}">
													<input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
													</c:if>
													<c:if test="${empty registryNameLst}">
													<input  type="hidden"value="${registryName}" name="registryName" class="addItemToRegis" />
													</c:if>
						                            <c:if test="${not empty regId}">
                                                               <dsp:setvalue  bean="SessionBean.registryId" value="${regId}"/>
                                                     </c:if>
                                                     <c:if test="${not empty registryName}">
                                                                 <dsp:setvalue  bean="SessionBean.eventType" value="${registryName}"/>
                                                     </c:if>
						                                 </c:when>
						                           </c:choose>
												</dsp:oparam>
											</dsp:droplet>
											<div class="fl addToRegistry">
												<div class="button <c:if test='${ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false") || disableCTA}'>button_disabled</c:if>">
												<input class="btnAddToRegistry" name="btnAddToRegistry"
														type="button" value="${submitAddToRegistryBtn}" role="button" aria-pressed="false"
														data-notify-reg="true" <c:if test='${ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false") || disableCTA}'>disabled="disabled"</c:if> />
												</div>
											</div>
										</c:when>

										<c:when test="${sizeValue>1}">
												<div class="fl addToRegistry addToRegistrySel">
												<div class="select">
												<c:choose>
													<c:when test="${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false'))}">
														<dsp:droplet name = "ForEach">
															<dsp:param name="array" value="${registrySkinnyVOList}" />
															<dsp:oparam name="output">
																<dsp:param name="futureRegList" param="element" />
																<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																<dsp:getvalueof var="registryAltPhoneNumber"
														                    param="futureRegList.alternatePhone" />
																
																<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
																
																<input type="hidden" value="${registryAltPhoneNumber}" class="addItemToList addItemToRegis onPdpAltNumber" id="${regId}" name="altNumber">
																
																
															</dsp:oparam>
														</dsp:droplet>
														<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg selector_primaryAlt" >
															<dsp:option> <bbbl:label key="lbl_add_to_registry"
															language="${pageContext.request.locale.language}" /></dsp:option>
															<dsp:droplet name="ForEach">
																<dsp:param name="array" value="${registrySkinnyVOList}" />
																<dsp:oparam name="output">
																	<dsp:param name="futureRegList" param="element" />

																	<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																	<dsp:getvalueof var="event_type" param="element.eventType" />
																	<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
																	<option value="${regId}"  class="${event_type}" data-poboxflag="${poBoxAddress}" data-notify-reg = "true" >
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
                                    						<dsp:tagAttribute name="aria-hidden" value="false"/>
                                    						<dsp:tagAttribute name="disabled" value="disabled"/>
                                    					</dsp:select>
                                    					 <c:if test="${recommendedregistry != null && not empty recommendedregistry}">
                                    					<input type="hidden" value="true" id="recommendedregistryPresent">
                                    					</c:if>
													</c:when>
													<c:otherwise>
													<dsp:droplet name="ForEach">
															<dsp:param name="array" value="${registrySkinnyVOList}" />
															<dsp:oparam name="output">
																<dsp:param name="futureRegList" param="element" />
																<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																<dsp:getvalueof var="registryAltPhoneNumber"
														                    param="futureRegList.alternatePhone" />
																<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>		
																<input type="hidden" value="${registryAltPhoneNumber}" class="addItemToList addItemToRegis onPdpAltNumber" id="${regId}" name="altNumber">
																
															</dsp:oparam>
														</dsp:droplet>											

														<c:if test='${ltlProductFlag && not enableLTLRegForSite}'> 
															<c:set var="disableRegClass" value="button_disabled registryDisabledLTL"/>
														</c:if>

													<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg selector_primaryAlt ${disableRegClass}" disabled="${not enableLTLRegForSite and ltlProductFlag}">
													<dsp:option><bbbl:label key="lbl_add_to_registry"
													language="${pageContext.request.locale.language}" /></dsp:option>
														<dsp:droplet name="ForEach">
															<dsp:param name="array" value="${registrySkinnyVOList}" />
															<dsp:oparam name="output">
																<dsp:param name="futureRegList" param="element" />

																<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																<dsp:getvalueof var="event_type" param="element.eventType" />
																<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>	
																<option value="${regId}"  class="${event_type}" data-poboxflag="${poBoxAddress}" data-notify-reg="true" >
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
												</div>
											</c:when>
											<c:when test="${sizeValue==1}">

											<c:choose>
											<c:when test="${recommendedregistry != null}">
											<div class="fl addToRegistry addToRegistrySel">
													<dsp:droplet name="ForEach">
															<dsp:param name="array" value="${registrySkinnyVOList}" />
															<dsp:oparam name="output">
																<dsp:param name="futureRegList" param="element" />
																<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																<dsp:getvalueof var="registryAltPhoneNumber"
														                    param="futureRegList.alternatePhone" />
																<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
																<input type="hidden" value="${registryAltPhoneNumber}" class="addItemToList addItemToRegis onPdpAltNumber" id="${regId}" name="altNumber">
															</dsp:oparam>
														</dsp:droplet>
												<c:choose>
												<c:when test="${disableCTA || isInternationalCustomer || (ltlFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false'))}">
												
												<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg selector_primaryAlt">
												<dsp:option> <bbbl:label key="lbl_add_to_registry"
													language="${pageContext.request.locale.language}" /></dsp:option>
														<dsp:droplet name="ForEach">
															<dsp:param name="array" value="${registrySkinnyVOList}" />
															<dsp:oparam name="output">
																<dsp:param name="futureRegList" param="element" />

																<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																<dsp:getvalueof var="event_type" param="element.eventType" />
																<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>	
																<option value="${regId}"  class="${event_type}" data-poboxflag="${poBoxAddress}" data-notify-reg="true"  >
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
												<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg selector_primaryAlt">
												<dsp:option> <bbbl:label key="lbl_add_to_registry"
													language="${pageContext.request.locale.language}" /></dsp:option>
														<dsp:droplet name="ForEach">
															<dsp:param name="array" value="${registrySkinnyVOList}" />
															<dsp:oparam name="output">
																<dsp:param name="futureRegList" param="element" />

																<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
																<dsp:getvalueof var="event_type" param="element.eventType" />
																
																<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
															
																
																<option value="${regId}"  class="${event_type}" data-poboxflag="${poBoxAddress}" data-notify-reg="true" >
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
														<dsp:getvalueof var="registryAltPhoneNumber"
														                    param="futureRegList.alternatePhone" />
														<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
														
																
													<input type="hidden" value="${registryAltPhoneNumber}" name="altNumber" class="addItemToList addItemToRegis onPdpAltNumber" id="${regId}">
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
													<div class="button <c:if test='${disableCTA || isInternationalCustomer ||(ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>button_disabled</c:if> <c:if test='${(ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>registryDisabledLTL</c:if>">
													<input class="btnAddToRegistry" name="btnAddToRegistry" data-notify-reg="true"
															type="button" value="${submitAddToRegistryBtn}" data-poboxflag="${poBoxAddress}" data-altno="${registryAltPhoneNumber}" role="button" aria-pressed="false" <c:if test='${isInternationalCustomer || disableCTA ||( ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>disabled="disabled"</c:if> />
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
								<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg selector_primaryAlt">
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
									<dsp:tagAttribute name="disabled" value="disabled"/>
								</dsp:select>
								</c:when>
								<c:otherwise>
								<dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg selector_primaryAlt">
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
							<div class="button  <c:if test='${disableCTA || isInternationalCustomer ||(ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>button_disabled</c:if> <c:if test='${(ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>registryDisabledLTL</c:if>">
									<input type="button" class="btnAddToRegNoRegistry" value="${submitAddToRegistryBtn}" name="btnAddToRegNoRegistry" <c:if test='${isInternationalCustomer || disableCTA || ( ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>disabled="disabled"</c:if> />
								</div>
								</div>
							</c:otherwise>

						</c:choose>



						</c:otherwise>

					</c:choose>
				</c:when>
				<c:when test="${transient == 'true'}">
					 <div class="fl addToRegistry">
						<div class="button  <c:if test='${disableCTA || isInternationalCustomer ||(ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>button_disabled</c:if> <c:if test='${(ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>registryDisabledLTL</c:if>">
								<input  class="addItemToRegNoLogin" name="addItemToRegNoLogin"
								type="button" role="button" value="${submitAddToRegistryBtn}" <c:if test='${disableCTA || isInternationalCustomer || ( ltlProductFlag && fn:containsIgnoreCase(enableLTLRegForSite,"false"))}'>disabled="disabled"</c:if> data-notify-reg="true"/>
								</div>
								</div>

				</c:when>
			</c:choose>
</dsp:page>