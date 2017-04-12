<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<c:set var="enableRegSearchById" scope="request"><bbbc:config key="enableRegSearchById" configName="FlagDrivenFunctions" /></c:set>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />		
		
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />
	<div class="grid_2 clearfix gift marTop_20">
	   	 <c:choose>
			<c:when test="${enableRegSearchById == 'true'}">
				 <div class="guests">
					<h3>find <span>a registry</span></h3>
				</div>
			</c:when>
			<c:otherwise>
				<div class="guests">
					<p>
						<span><bbbl:label key="lbl_for_small" language="${pageContext.request.locale.language}" /></span> <bbbl:label key="lbl_guests" language="${pageContext.request.locale.language}" />
					</p>
					<h3><bbbl:label key="lbl_landing_giveguests" language="${pageContext.request.locale.language}" /></h3>
				</div>
			</c:otherwise>
		</c:choose>	
		
				<c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<c:set var="findRegistryFormCount" value="1" scope="request"/>
				<dsp:include page="/addgiftregistry/find_registry_widget.jsp">
					<dsp:param name="findRegistryFormId" value="frmFindRegistry" />
					<dsp:param name="submitText" value="${findButton}" />
					<dsp:param name="handlerMethod" value="registrySearchFromBridalLanding" />
					<dsp:param name="bridalException" value="false" />
					<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
				</dsp:include>
	</div>
	<div class="grid_1 clearfix alpha omega giftOfr">
		<img src="${imagePath}/_assets/bbregistry/images/gift_offr.jpg"
			width="63" height="64" alt="Gift Offer" />
	</div>
	<div class="grid_6 clearfix giftNum alpha omega">
		<div class="multipleRegistry">
			<div class="multipleRegistryContentWrapper">
				<p class="welcomeText">
					<span>&nbsp;</span><bbbl:label key="lbl_regflyout_welcomeback" language ="${pageContext.request.locale.language}"/> <strong><dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/><dsp:droplet name="IsEmpty"><dsp:getvalueof param="registrySummaryVO.coRegistrantFirstName" id="fName" ><dsp:param value="<%=fName%>" name="value"/></dsp:getvalueof><dsp:oparam name="false"><c:set var="coRegName"><dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/></c:set><c:if test="${not empty coRegName}"> &amp; <dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/></c:if></dsp:oparam></dsp:droplet>!</strong><span>&nbsp;</span>
				</p>
				<ul class="clearfix noMarBot">

					<dsp:droplet name="DateCalculationDroplet">
						<dsp:param name="eventDate" param="registrySummaryVO.eventDate" />
						<dsp:param name="convertDateFlag" value="true" />
						<dsp:oparam name="output">

							<dsp:getvalueof param="registrySummaryVO.registryType.registryTypeDesc" var="eventType"/>
		                	<dsp:getvalueof param="check" var="isFutureDate"/>

							<li class="grid_2">
								<c:choose>
			             			<c:when test="${isFutureDate}">
				                    	<c:if test="${not empty eventType && eventType ne 'Wedding' && eventType ne 'Baby' && eventType ne 'Other' &&  
				                    		eventType ne 'Commitment Ceremony' &&  eventType ne 'Others'}">				                    	
				                    		<p> &nbsp;</p>
				                    	</c:if>
				                   	</c:when>
				               		<c:otherwise>
				               			<c:choose>
											<c:when test="${not empty eventType && eventType eq 'Wedding'}">
												<p> &nbsp;</p>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Retirement'}">
												<p> &nbsp;</p>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
												<p> &nbsp;</p>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
												<p> &nbsp;</p>
											</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>
								<dsp:valueof param="registrySummaryVO.giftRegistered" />
								<p><bbbl:label key="lbl_regflyout_giftsregistered" language ="${pageContext.request.locale.language}"/></p>
							</li>
							<li class="grid_2 partition">
								<c:choose>
			             			<c:when test="${isFutureDate}">
				              			<c:if test="${not empty eventType && eventType ne 'Wedding' && eventType ne 'Baby' && eventType ne 'Other' &&  
				                    		eventType ne 'Commitment Ceremony' &&  eventType ne 'Others'}">
				                    		<p> &nbsp;</p>
				                    	</c:if>
				                   	</c:when>
				               		<c:otherwise>
				               			<c:choose>
											<c:when test="${not empty eventType && eventType eq 'Wedding'}">
												<p> &nbsp;</p>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Retirement'}">
												<p> &nbsp;</p>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
												<p> &nbsp;</p>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
												<p> &nbsp;</p>
											</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>							
								<dsp:valueof param="registrySummaryVO.giftPurchased" />
								<p><bbbl:label key="lbl_regflyout_giftspurchased" language ="${pageContext.request.locale.language}"/></p>
							</li>
							<li class="grid_2 last">
								                	
								<%-- Event messages --%>								
								<dsp:droplet name="Switch">
										<dsp:param name="value" param="check" />
										<dsp:oparam name="true">
												<c:choose>
													<c:when test="${not empty eventType && eventType eq 'Wedding'}">
														<dsp:valueof param="daysToGo"/>
														<p><bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" /></p>
													</c:when>
													<c:when test="${not empty eventType && eventType eq 'Baby'}">
														<dsp:valueof param="daysToGo"/>
														<p><bbbl:label key='lbl_regflyout_daystogo_day_until_baby' language="${pageContext.request.locale.language}" /></p>
													</c:when>
													<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
														<dsp:valueof param="daysToGo"/>
														<p><bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" /></p>
													</c:when>
													<c:otherwise>
														<c:if test="${not empty eventType && eventType ne 'Other' &&  eventType ne 'Others'}">
															<p><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></p> 
															<dsp:valueof param="daysToGo"/>
															<p><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></p>
														</c:if>
													</c:otherwise>
												</c:choose>
										</dsp:oparam>
										<dsp:oparam name="false">
											<c:choose>
												<c:when test="${not empty eventType && eventType eq 'Wedding'}">
													<p><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></p>
													<dsp:valueof param="daysToGo"/>
													<p><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></p>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Baby'}">
													<dsp:valueof param="daysToGo"/>
													<p><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></p>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Birthday'}">
													<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />	
													${daysToNextCeleb}
													<p><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></p>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Retirement'}">
													<p><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></p>
													<dsp:valueof param="daysToGo"/>
													<p><bbbl:label key='lbl_regflyout_daystogo_rest_of_life' language="${pageContext.request.locale.language}" /></p>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
													<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />	
													${daysToNextCeleb}
													<p><bbbl:label key='lbl_regflyout_daystogo_another_year' language="${pageContext.request.locale.language}" /></p>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
													<p><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></p>
													<dsp:valueof param="daysToGo"/>
													<p><bbbl:label key='lbl_regflyout_daystogo_new_place' language="${pageContext.request.locale.language}" /></p>
												</c:when>
												<c:otherwise>
													<c:if test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
														<p><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></p>
														<dsp:valueof param="daysToGo"/>
														<p><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></p>
													</c:if>
												</c:otherwise>
											</c:choose>
										</dsp:oparam>
									</dsp:droplet>									
							</li>								
						</dsp:oparam>
						<dsp:oparam name="error">
							<dsp:valueof param="errorMsg"></dsp:valueof>
						</dsp:oparam>
					</dsp:droplet>
				</ul>
			</div>	
			<div class="grid_2 clearfix manageReg alpha">
				
				<dsp:droplet name="/atg/dynamo/droplet/Switch">
                    <dsp:param name="value" param="multiReg" />
                    <dsp:oparam name="true">
		                    <%-- Dropdown for registries --%>
							<div class="txtOffScreen">
								<input aria-hidden="true"></input>
							</div>	
		                    <div id="findARegistryInfo" class="grid_3 alpha omega">
		
							    <div id="findARegistrySelectType">
							    	<dsp:include page= "../giftregistry/frags/view_manage_registry.jsp">
							    		<dsp:param name="fragName" value="bridalLanding"/>
							    	</dsp:include>
							    </div>
							    <div class="clear"></div>
							</div>
	                </dsp:oparam>
                    <dsp:oparam name="false">
						<div class="button button_active">
							<dsp:getvalueof var="regId" param="registrySummaryVO.registryId"/>
							<dsp:getvalueof var="eventType" param="registrySummaryVO.registryType.registryTypeDesc"/>
							<dsp:a href="${contextPath}/giftregistry/view_registry_owner.jsp"><bbbt:textArea key="txt_regflyout_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
								<dsp:param name="registryId" value="${regId}"/>
								<dsp:param name="eventType" value="${eventType}"/>
							</dsp:a>
						</div>                    
                    </dsp:oparam>
                </dsp:droplet> 				
				<div class="clear"></div>
			</div>
			
			<dsp:form>
				<dsp:droplet name="ErrorMessageForEach">
					<dsp:param bean="GiftRegistryFormHandler.formExceptions"
						name="exceptions" />
					<dsp:oparam name="output">
						<dsp:valueof param="message" />
						<br>
					</dsp:oparam>
				</dsp:droplet>
				<div class="selectOption">
					<p class="grid_3 alpha clearfix"><bbbl:label key='lbl_regflyout_want_to_create' language="${pageContext.request.locale.language}" /></p>
					<div class="grid_2 alpha clearfix createAnotherReg">
						<div id="selectRegistryType">
							<ul>
							<label id="selectRegistry"><bbbl:label key="lbl_select_registry_type" language ="${pageContext.request.locale.language}"/></label>
								<dsp:select bean="GiftRegistryFormHandler.registryEventType"
									iclass="selector_primaryAlt selectRegToCreate" id="typeofregselect1">
									<dsp:tagAttribute name="data-submit-button" value="submitClick" />
									<dsp:droplet name="GiftRegistryTypesDroplet">
										<dsp:param name="siteId" value="${currentSiteId}"/>									
										<dsp:oparam name="output">
											<dsp:option value="" selected="selected">
												<bbbl:label key="lbl_regcreate_select_type"
													language="${pageContext.request.locale.language}" />
											</dsp:option>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="registryTypes" />
												<dsp:oparam name="output">
													<dsp:param name="regTypes" param="element" />
													<dsp:getvalueof var="regTypesId"
														param="regTypes.registryCode" />
													<dsp:option value="${regTypesId}">
														<li><a href="#"><dsp:valueof
																	param="element.registryName"></dsp:valueof> </a>
														</li>
													</dsp:option>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									</dsp:droplet>
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-label" value="select the registry you would like to create. You will be redirected to the begin the creation process"/>
                                	<dsp:tagAttribute name="aria-hidden" value="false"/>
								</dsp:select>
							</ul>
						</div>
					</div>
					<dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
						value="${contextPath}/giftregistry/registry_type_select.jsp" />
					<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
						value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" />
					<dsp:input id="submitClick"
						bean="GiftRegistryFormHandler.registryTypes" type="submit"
						value="submit" style="display:none;"></dsp:input>
				</div>
			</dsp:form>
			<script type="text/javascript">
                        $('#submitClick').attr('disabled', 'disabled');
                        function updateFormEnabled() {
                        if ($('#typeofregselect1').val() == ' ') {
                        $('#submitClick').prop('disabled', 'disabled');
                        } else {
                        $('#submitClick').removeAttr('disabled');
                        }
                        }
                        $('#typeofregselect1').change(updateFormEnabled);
    	     </script>
		</div>
	</div>
	<div class="grid_3 clearfix marTop_20">
		<img src="${imagePath}/_assets/bbregistry/images/prod_hero.jpg"
			alt="" width="229" height="239" border="0" usemap="#whyRegister" />
	</div>
	
</dsp:page>
