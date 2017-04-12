 <dsp:page>
		<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />		
	<c:set var="enableRegSearchById" scope="request"><bbbc:config key="enableRegSearchById" configName="FlagDrivenFunctions" /></c:set>		
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />
            <div class="infoContent grid_3 alpha">
            	<c:choose>
				<c:when test="${enableRegSearchById == 'true'}">
					    <div id="heroInfo" class="RegSearchByIdFlagOn">
				</c:when>
				<c:otherwise>
					   <div id="heroInfo">
				</c:otherwise>
			</c:choose>	
                
                    <div class="guests giveAgiftReg">
                        <p><bbbl:label key='lbl_find_reg_for_guest' language ="${pageContext.request.locale.language}"/></p>
                        <h2><span><bbbl:label key='lbl_find_reg_find' language ="${pageContext.request.locale.language}"/></span>&nbsp;<bbbl:label key='lbl_find_reg_a_reg' language ="${pageContext.request.locale.language}"/></h2>
						<p class="registrantInfo"><bbbl:label key='lbl_find_reg_enter_gift' language ="${pageContext.request.locale.language}"/></p>
						<c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
						<c:set var="findRegistryFormCount" value="1" scope="request"/>
						<dsp:include page="/addgiftregistry/find_registry_widget.jsp">
							<dsp:param name="findRegistryFormId" value="frmFindRegistry" />
							<dsp:param name="submitText" value="${findButton}" />
							<dsp:param name="handlerMethod" value="registrySearchFromBabyLanding" />
							<dsp:param name="bridalException" value="false" />
							<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
						</dsp:include>
                    </div>                    
                </div>                    
            </div>
			
			<div class="grid_6 clearfix giftNum alpha omega">
				<div class="multipleRegistry">
					<p class="welcomeText">&nbsp;<bbbl:label key="lbl_regflyout_welcomeback" language ="${pageContext.request.locale.language}"/> <strong><dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/><dsp:droplet name="IsEmpty"><dsp:getvalueof param="registrySummaryVO.coRegistrantFirstName" id="fName" ><dsp:param value="<%=fName%>" name="value"/></dsp:getvalueof><dsp:oparam name="false"><c:set var="coRegName"><dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/></c:set><c:if test="${not empty coRegName}"> &amp; <dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/></c:if></dsp:oparam></dsp:droplet>!</strong>&nbsp;</p>
					<ul class="clearfix noMarBot sepItemWrap">
				
						<dsp:droplet name="DateCalculationDroplet">
							<dsp:param name="eventDate" param="registrySummaryVO.eventDate" />
							<dsp:param name="convertDateFlag" value="true" />
							<dsp:oparam name="output">
								
								<dsp:getvalueof param="registrySummaryVO.registryType.registryTypeDesc" var="eventType"/>
								<dsp:getvalueof param="check" var="isFutureDate"/>
								
								<li class="grid_2">
				                    <c:if test="${isFutureDate && not empty eventType && eventType eq 'Birthday'}">
				                    	<p> &nbsp;</p>
				                   	</c:if>								
								
									<dsp:valueof param="registrySummaryVO.giftRegistered" />
									<p><bbbl:label key="lbl_regflyout_giftsregistered" language ="${pageContext.request.locale.language}"/></p>
								</li>
								<li class="grid_2 partition">
				                    <c:if test="${isFutureDate && not empty eventType && eventType eq 'Birthday'}">
				                    	<p> &nbsp;</p>
				                   	</c:if>
									<dsp:valueof param="registrySummaryVO.giftPurchased" />
									<p><bbbl:label key="lbl_regflyout_giftspurchased" language ="${pageContext.request.locale.language}"/></p>
								</li>								
								
								<li class="grid_2 omega last">
									<%-- Event messages --%>								
									<dsp:droplet name="Switch">
										<dsp:param name="value" param="check" />
										<dsp:oparam name="true">
											<c:if test="${not empty currentSiteId && currentSiteId eq 'BuyBuyBaby'}">
													<c:choose>
														<c:when test="${not empty eventType && eventType eq 'Baby'}">
															<dsp:valueof param="daysToGo"/>
															<p><bbbl:label key='lbl_regflyout_daystogo_baby_arrives' language="${pageContext.request.locale.language}" /></p>
														</c:when>
														<c:otherwise>
															<c:if test="${not empty eventType && eventType eq 'Birthday'}">
																<p><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></p>
																<dsp:valueof param="daysToGo"/>
																<p><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></p>
															</c:if>
														</c:otherwise>
													</c:choose>
											</c:if>
										</dsp:oparam>
										<dsp:oparam name="false">
											<c:if test="${not empty currentSiteId && currentSiteId eq 'BuyBuyBaby'}">
												<c:choose>
													<c:when test="${not empty eventType && eventType eq 'Baby'}">
														<dsp:valueof param="daysToGo"/>
														<p><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></p>
													</c:when>
													<c:otherwise>
														<c:if test="${not empty eventType && eventType eq 'Birthday'}">
															<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />	
															${daysToNextCeleb}
															<p><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></p>
														</c:if>
													</c:otherwise>
												</c:choose>
											</c:if>
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
				<div class="grid_3 clearfix manageReg alpha">

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
							    		<dsp:param name="fragName" value="babyLanding"/>
							    	</dsp:include>
							    </div>
							    <div class="clear"></div>
							</div>
	               
		                </dsp:oparam>
	                    <dsp:oparam name="false">
							<div class="button button_bridal button_active">
								<dsp:getvalueof var="regId" param="registrySummaryVO.registryId"/>
								<dsp:getvalueof var="eventType" param="registrySummaryVO.registryType.registryTypeDesc"/>
								<dsp:a href="${contextPath}/giftregistry/view_registry_owner.jsp" ><bbbt:textArea key="txt_regflyout_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
									<dsp:param name="registryId" value="${regId}"/>
									<dsp:param name="eventType" value="${eventType}"/>
								</dsp:a>
							</div>                    
	                    </dsp:oparam>
	                </dsp:droplet> 				
					<div class="lnkRegViewAll">
							<a href="${contextPath}/giftregistry/my_registries.jsp" class="viewAllRegistries" title="View All Your Registries">&raquo; <bbbl:label key='lbl_mng_regitem_view_all_reg' language="${pageContext.request.locale.language}" /></a>
					</div>				
					
				</div>
				<div class="selectOption">
					<p class="grid_3"><bbbl:label key='lbl_regflyout_want_to_create' language="${pageContext.request.locale.language}" /></p>
					<div class="grid_3 alpha clearfix createAnotherReg">
						<dsp:form>
							<dsp:droplet name="ErrorMessageForEach">
							  <dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
							  <dsp:oparam name="output">
							  <dsp:valueof param="message"/><br>
							  </dsp:oparam>
							</dsp:droplet>

					  	 <div id="findARegistrySelectType" class="createRegistry selectTypeFOR">
							<label id="selectRegistry"><bbbl:label key="lbl_select_registry_type" language ="${pageContext.request.locale.language}"/></label>
							<dsp:select  bean="GiftRegistryFormHandler.registryEventType"  iclass="selector_primary selectRegToCreate" id="typeofregselect9">	
								<dsp:tagAttribute name="data-submit-button" value="submitClick9"/>	
										<dsp:droplet name="GiftRegistryTypesDroplet">
											<dsp:param name="siteId" value="${currentSiteId}"/>					
											<dsp:oparam name="output">
											<dsp:option value="" selected="selected"><bbbl:label key="lbl_regsearch_select_type" language ="${pageContext.request.locale.language}"/></dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="registryTypes" />
													<dsp:oparam name="output">
													
														<dsp:param name="regTypes" param="element" />	
														<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
														<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
														<dsp:option value="${registryCode}">
															<li>
																<a href="#"><dsp:valueof param="element.registryName"></dsp:valueof></a>
															</li>
														</dsp:option>
													
													</dsp:oparam>
													
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledBy" value="selectRegistry"/>
                                		<dsp:tagAttribute name="aria-hidden" value="false"/>
								</dsp:select>
							
							
							<%-- Client DOM XSRF | Part -1
 							 <dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
								value="${contextPath}/giftregistry/registry_type_select.jsp" />
							<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
								value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
								<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="babyLandingLoggedMulReg" />
							<dsp:input id="submitClick9"
								bean="GiftRegistryFormHandler.registryTypes" type="submit" value="submit"
								style="display:none;"></dsp:input>
							
					
					    </div>
					</dsp:form>
                                        <script type="text/javascript">
                                        $('#submitClick9').attr('disabled', 'disabled');
                                        function updateFormEnabled() {
                                        if ($('#typeofregselect9').val() == ' ') {
                                        $('#submitClick9').prop('disabled', 'disabled');
                                        } else {
                                        $('#submitClick9').removeAttr('disabled');
                                        }
                                        }
                                        $('#typeofregselect9').change(updateFormEnabled);
    	                                </script>
				</div>
			</div>
		</div>
					
		<div class="grid_3 alpha omega babyImageMultiLoggedIn">
			<img src="${imagePath}/_assets/bbbaby/images/prod_hero.png" />
		</div>	
</dsp:page>			