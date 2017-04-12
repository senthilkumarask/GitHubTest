<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="com/bbb/selfservice/ScheduleAppointmentManager" />
<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
<dsp:getvalueof var="errMessageShown" param="errMessageShown" />	
<dsp:getvalueof var="defaultAppointmentCode" value="${param.defaultAppointmentCode}" />	
<dsp:getvalueof var="errorOnModal" value="${param.errorOnModal}" />
<dsp:setvalue value="${param.defaultAppointmentCode}" bean="ScheduleAppointmentManager.defaultAppointment" />
<dsp:setvalue value="${param.defaultAppointmentCode}" bean="SearchStoreFormHandler.defaultAppointment" />

<c:set var="lbl_find_available_stores"> <bbbl:label key="lbl_find_available_stores" language="${pageContext.request.locale.language}" />
			</c:set>

<c:if test="${empty sessionScope.currentSiteId }">
	<c:set var="currentSiteId" scope="session"><dsp:valueof bean="Site.id" /></c:set>
</c:if>

<div id="changeStoreDialogWrapper" class="clearfix hidden" title="${lbl_find_available_stores}">
	<div id="changeStoreStaticWrap" class="changeStoreStaticWrap clearfix">
	<c:if test="${errorOnModal eq true}">
		<div class="error"><bbbl:label key="lbl_favourite_store_message" language="${pageContext.request.locale.language}" /></div>
	</c:if>
			<div class="showWithResult itemDetails clearfix hidden">
				<div class="fl">
					<img class="fl productImage" height="83" width="83" <%-- onerror="this.src='${imagePath}/_assets/global/images/no_image_available.jpg'" --%> src="/_assets/global/images/placeholder/productimage_smallb_63x63.jpg" alt="SKU NAME and PRICE" />
				</div>
				<div class="fl padTop_5 width_7 alpha omega">
					<p class="padBottom_10 productName">Product name goes here.</p>
					<p class="modalSKUName"></p>
				</div>
				<div class="clear"></div>
			</div> 
			<div id="changeStoreFormWrapper" class="marTop_20 hidden">
				<dsp:form method="post" action="/store/scheduleappointment/search_store_appointment.jsp"
								name="changeStoreForm" id="changeStoreFormCollage"  iclass="clearfix">
				<dsp:input bean="SearchStoreFormHandler.siteContext" type="hidden" beanvalue="/atg/multisite/Site.id"/>
					<c:if test="${errMessageShown ne 'true'}">
						<div class="error">
							<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
								<dsp:param bean="SearchStoreFormHandler.formExceptions" name="exceptions"/>
								<dsp:oparam name="output">
									<dsp:valueof param="message" />
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</c:if>
					
					<div class="input clearfix">
								<div class="label">
									<label id="lblappCode" for="appCode">								
												<bbbl:label key="lbl_appointment_type" language="${pageContext.request.locale.language}" />
														<span class="required"> * </span>
									</label>
								</div>
								<div class="select">
								<div class="schdAppOptions alpha suffix_3">
									<c:set var="appCode" value="appCode" scope="request"/>
							
										<dsp:select name="${appCode}" id="appCode"
														bean="SearchStoreFormHandler.defaultAppointment" iclass="uniform">
														
											<dsp:option value="">
										
												<bbbl:label key="lbl_select_appointment_dropdown"
																language="${pageContext.request.locale.language}" />
															<!-- 	<c:choose>
																<c:when test="${!(empty defaultAppointmentType)}">
																	${defaultAppointmentType}
																</c:when>
																<c:otherwise>
																<p>Select an appointment type option
																</p>
																</c:otherwise>
																</c:choose>-->
																
											</dsp:option>
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array"
																bean="SearchStoreFormHandler.appointmentList" />
												<dsp:param name="elementName" value="findAppointments" />
												<dsp:oparam name="output">
													<dsp:getvalueof id="appointmentCode" param="findAppointments.appointmentCode" />
													<dsp:getvalueof id="appointmentName" param="findAppointments.appointmentName" />
													<c:choose>
												<c:when test="${(not empty defaultAppointmentCode) and (appointmentCode eq defaultAppointmentCode)}">
													<dsp:option value="${appointmentCode}" iclass="default" selected="true">${appointmentName}</dsp:option>
												</c:when>
												<c:otherwise>
												<dsp:option value="${appointmentCode}">${appointmentName} </dsp:option>
												</c:otherwise>
												</c:choose>					
												</dsp:oparam>
											</dsp:droplet>
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblAppointmentCode errortxtAppointmentState"/>
											<dsp:tagAttribute name="aria-hidden" value="false"/>
										</dsp:select>
									</div>
									<label id="errortxtAppointmentState" for="appCode" generated="true" class="error"></label>
								<div class="clear"></div>
								</div>
							</div>
							
					<div id="searchStoreBox" class="width_6 alpha omega clearfix">
					
						<div class="width_2 fl">
							
							<div class="input clearfix">
								<div class="label">
								<c:choose>
									<c:when test="${currentSiteId eq 'BedBathCanada'}">
										<label id="lbltxtStoreZip" for="txtStoreZipCollage"><bbbl:label
															key="lbl_search_store_Postal"
															language="${pageContext.request.locale.language}" /><span
														class="required"> * </span>
										</label>				
									</c:when>
									<c:otherwise>
										<label id="lbltxtStoreZip" for="txtStoreZipCollage"><bbbl:label
															key="lbl_search_store_zip"
															language="${pageContext.request.locale.language}" /><span
														class="required"> * </span>
										</label>				
									</c:otherwise>
								</c:choose>
								</div>
								<div class="text">
									<div class="width_1 alpha omega">
										<c:choose>
											<c:when test="${currentSiteId eq 'BedBathCanada'}">
												<dsp:input type="text" id="txtStoreZipCollage" name="storeZipCACollage"
													maxlength="7" bean="SearchStoreFormHandler.storeLocator.postalCode" >
													<dsp:tagAttribute name="aria-required" value="true"/>
													<dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreZip errortxtStoreZip"/>
												</dsp:input>
											</c:when>
											<c:otherwise>
												<dsp:input type="tel" id="txtStoreZipCollage" name="storeZipCollage"
													maxlength="5" bean="SearchStoreFormHandler.storeLocator.postalCode" >
													<dsp:tagAttribute name="aria-required" value="true"/>
													<dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreZip errortxtStoreZip"/>
												</dsp:input>
											</c:otherwise>
										</c:choose>						
									</div>
									<label id="errortxtStoreZip" for="txtStoreZip" generated="true" class="error"></label>
								</div>
							</div>
						</div>

						<div class="width_1 fl marRight_20 marTop_25">
							<div id="orText"><bbbl:label
														key="lbl_OR"
														language="${pageContext.request.locale.language}}" />
							</div>
						</div>

						<div class="width_2 fl marLeft_10">
							
							<div class="input clearfix">
								<div class="label">
									<label id="lbltxtStoreAddress" for="txtStoreAddress"><bbbl:label
														key="lbl_search_store_address"
														language="${pageContext.request.locale.language}}" />
									</label>
								</div>
								<div class="text">
									<div class="width_2 alpha omega">
										<dsp:input type="text" id="txtStoreAddressCollage" name="add2"
														bean="SearchStoreFormHandler.storeLocator.address" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreAddress errortxtStoreAddress"/>
                                        </dsp:input>
									</div>
								</div>
							</div>

							
							<div class="input clearfix">
								<div class="label">
									<label id="lbltxtStoreCity" for="txtStoreCityCollage"><bbbl:label
														key="lbl_search_store_city"
														language="${pageContext.request.locale.language}" /> <span
													class="required">*</span>
									</label>
								</div>
								<div class="text">
									<div class="width_2 alpha omega">
										<dsp:input type="text" id="txtStoreCityCollage" name="storeCityCollage"
														maxlength="25"
														bean="SearchStoreFormHandler.storeLocator.city" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreCity errortxtStoreCity"/>
                                        </dsp:input>
									</div>
								</div>
							</div>

							
							<div class="input clearfix">
								<div class="label">
									<label id="lbltxtStoreState" for="txtStoreStateCollage">
												<bbbl:label key="lbl_search_store_state" language="${pageContext.request.locale.language}" />
														<span class="required"> * </span>
									</label>
								</div>
								<div class="select">
									<div class="width_2 alpha suffix_3">
								<c:choose>
								<c:when test="${currentSiteId eq 'BedBathCanada'}">
								<c:set var="storeState" value="storeStateCACollage" scope="request"/>
								</c:when>
								<c:otherwise>
								<c:set var="storeState" value="storeStateCollage" scope="request"/>
								</c:otherwise>
								</c:choose>
										<dsp:select name="${storeState}" id="txtStoreStateCollage"
														bean="SearchStoreFormHandler.storeLocator.state" iclass="uniform">
											<dsp:option value="">
												<bbbl:label key="lbl_bridalbook_select_state"
																language="${pageContext.request.locale.language}" />
											</dsp:option>
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array"
																bean="SearchStoreFormHandler.stateList" />
												<dsp:param name="elementName" value="statelist" />
												<dsp:oparam name="output">
													<dsp:getvalueof id="stateName" param="statelist.stateName" />
													<dsp:getvalueof id="stateCode" param="statelist.stateCode" />
													
													<dsp:option value="${stateCode}">
														<c:out value="${stateName}" />
													</dsp:option>
												</dsp:oparam>
											</dsp:droplet>
                                            <dsp:tagAttribute name="aria-required" value="true"/>
											<dsp:tagAttribute name="aria-hidden" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblStoreState errortxtStoreState"/>
										</dsp:select>
									</div>
									<label id="errortxtStoreState" for="txtStoreStateCollage" generated="true" class="error block"></label>
								</div>
							</div>
						</div>
					</div>
					<div class="grid_5 alpha omega clearfix marTop_10">
						<div class="grid_1 alpha">
							
							<div class="clearfix">
								<div class="label width_1 fl marTop_5">
									<label id="lblselRadius" for="selRadius"><bbbl:label key="lbl_search_store_radius" language="${pageContext.request.locale.language}"/></label>
								</div>
								<div class="select fl">
									<div class="width_1">
										<c:set var="radius_default_us"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
										<c:set var="radius_default_baby"><bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set> 
										<c:set var="radius_default_ca"><bbbc:config key="radius_default_ca" configName="MapQuestStoreType" /></c:set>
										<c:set var="radius_range_us"><bbbc:config key="radius_range_us" configName="MapQuestStoreType" /></c:set> 
										<c:set var="radius_range_baby"><bbbc:config key="radius_range_baby" configName="MapQuestStoreType" /></c:set>
										<c:set var="radius_range_ca"><bbbc:config key="radius_range_ca" configName="MapQuestStoreType" /></c:set> 
										<c:set var="radius_range_type"><bbbc:config key="radius_range_type" configName="MapQuestStoreType" /></c:set> 										
										<c:choose>
											<c:when test="${currentSiteId eq BedBathUSSite}">
												<c:set var="radius_default_selected">${radius_default_us}</c:set> 
												<c:set var="radius_range">${radius_range_us}</c:set> 
											</c:when>
											<c:when test="${currentSiteId eq BuyBuyBabySite}">
												<c:set var="radius_default_selected">${radius_default_baby}</c:set> 
												<c:set var="radius_range">${radius_range_baby}</c:set> 
											</c:when>
											<c:when test="${currentSiteId eq BedBathCanadaSite}">
												<c:set var="radius_default_selected">${radius_default_ca}</c:set> 
												<c:set var="radius_range">${radius_range_ca}</c:set> 
											</c:when>											
											<c:otherwise>
												<c:set var="radius_default_selected">${radius_default_us}</c:set> 
												<c:set var="radius_range">${radius_range_us}</c:set> 
											</c:otherwise>
										</c:choose>
								
										<dsp:select name="selRadiusName" id="selRadius"
														bean="SearchStoreFormHandler.storeLocator.radius" iclass="uniform">
                                            <dsp:tagAttribute name="aria-required" value="false"/>
											<dsp:tagAttribute name="aria-hidden" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblselRadius errorselRadius"/>
											<c:forTokens items="${radius_range}" delims="," var="item">											
											<c:choose>
												<c:when test="${item == radius_default_selected}">
													<dsp:option value="${item}" iclass="default" selected="true">${item} ${radius_range_type}</dsp:option>
												</c:when>
												<c:otherwise>
													<dsp:option value="${item}">${item} ${radius_range_type} </dsp:option>
												</c:otherwise>
											</c:choose>																					
											</c:forTokens>	
										</dsp:select>
									</div>
								</div>
							</div>
						
						</div>
					</div>
					<!--<dsp:input bean="SearchStoreFormHandler.searchStore" type="submit"
									value="LOOK UP STORES" /> -->
					<!--<dsp:setvalue bean="SearchStoreFormHandler.searchStoreSuccessURL"
									value="find_in_store_appointment.jsp" />
					<dsp:setvalue bean="SearchStoreFormHandler.searchStoreErrorURL"
									value="find_in_store_appointment.jsp" />-->
									<dsp:input bean="SearchStoreFormHandler.fromPage" type="hidden" value="" />
					<input type="hidden" name="registryId" data-dest-class="registryId" value="" />
					<input type="hidden" name="changeCurrentStore" data-dest-class="changeCurrentStore" value="" />		
							
				</dsp:form>
			</div>
	</div>
	
	<div id="changeStoreResultWrapper"></div>
		<div id="sheduleAppBtn" class="hidden">
               <div class="button button_secondary">
					<input id="go" type="button" aria-pressed="false" role="button" value="LOOK UP STORES" name="sheduleAppBtnGo">
				</div>   
				<div class="button button_secondary">                                                                                                                
					<input id="cancel" type="button" aria-pressed="false" role="button" value="Cancel" name="sheduleAppBtnCancel">                                                                                                
				</div>
		</div>
	</div>
<dsp:include page="/selfservice/store/p2p_directions_input.jsp"/>
</dsp:page>		