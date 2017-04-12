<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />
<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="appid" bean="Site.id" />

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
	<c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_ca"/></c:set>
		</c:otherwise>
	</c:choose>
<div class="loggedInMultiple">
    <div class="grid_3 alpha omega">
        <div id="findARegistryFormTitleLogin">
            <h2><bbbt:textArea key="txt_regflyout_givegift" language ="${pageContext.request.locale.language}"/></h2>
            <p><bbbt:textArea key="txt_regflyout_enterinfo" language ="${pageContext.request.locale.language}"/></p>
        </div>
        <div id="findARegistryFormFormLogin" class="clearfix" role="application">
                <c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<c:set var="findRegistryFormCount" value="0" scope="request"/>
				<dsp:include page="/addgiftregistry/find_registry_widget.jsp">
					<dsp:param name="findRegistryFormId" value="bridalFlyOut" />
					<dsp:param name="submitText" value="${findButton}" />
					<dsp:param name="handlerMethod" value="registrySearchFromFlyout" />
					<dsp:param name="bridalException" value="false" />
					<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
					<dsp:param name="flyout" value="true" />
				</dsp:include>           
				<div class="clear"></div>
				<%-- <div id="findARegistryFormMessage">
					<bbbl:label key="lbl_regflyout_information" language ="${pageContext.request.locale.language}"/>
				</div> --%>
               
        </div>
    </div>
    <%-- fly out registry item count--%>
	<dsp:getvalueof var="registryId" param="registryId"/>
	
	<dsp:droplet name="RegistryInfoDisplayDroplet">
		<dsp:param value="${registrySummaryVO.registryId}" name="registryId" />
		<dsp:param name="displayView" value="owner" />		
		<dsp:oparam name="output">
			<dsp:getvalueof var="count" param="registrySummaryVO.giftRemaining"/>
	  </dsp:oparam>
	</dsp:droplet>
	<%--End fly out registry item count --%>
    <div id="findARegistryFormLogin" class="grid_6 omega alpha">
        <div id="findACurrentRegistry">
            <div id="currentyRegistryNumbers" class="clearfix">
                <h3><bbbl:label key="lbl_regflyout_welcomeback" language ="${pageContext.request.locale.language}"/> <strong><dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/><dsp:droplet name="IsEmpty"><dsp:getvalueof param="registrySummaryVO.coRegistrantFirstName" id="fName" ><dsp:param value="<%=fName%>" name="value"/></dsp:getvalueof><dsp:oparam name="false"><c:set var="coRegName"><dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/></c:set><c:if test="${not empty coRegName}"> &amp; <dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/></c:if></dsp:oparam></dsp:droplet>!</strong></h3>
				<div class="countBoxWraper countBox clearfix">               

                    <%-- Days to go / Days past messages --%>
                    <dsp:droplet name="DateCalculationDroplet">
                        <dsp:param name="eventDate" param="registrySummaryVO.eventDate"/>
                        <dsp:param name="convertDateFlag" value="true" />
						<dsp:oparam name="output">
	                		<dsp:getvalueof param="registrySummaryVO.registryType.registryTypeDesc" var="eventType"/>
			                <dsp:getvalueof param="check" var="isFutureDate"/>
			     			<div class="grid_2 noMar">
			               		<c:choose>
			             			<c:when test="${isFutureDate}">
				                    	<c:if test="${not empty eventType && eventType ne 'Wedding' && eventType ne 'Baby' && eventType ne 'Other' &&  
				                    		eventType ne 'Commitment Ceremony' &&  eventType ne 'Others'}">
				                    		<div class="countLabel"> &nbsp;</div>
				                    	</c:if>
				                   	</c:when>
				               		<c:otherwise>
				               			<c:choose>
											<c:when test="${not empty eventType && eventType eq 'Wedding'}">
												<div class="countLabel"> &nbsp;</div>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Retirement'}">
												<div class="countLabel"> &nbsp;</div>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
												<div class="countLabel"> &nbsp;</div>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
												<div class="countLabel"> &nbsp;</div>
											</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>
															
			                    <div class="count">
			                    <dsp:valueof param="registrySummaryVO.giftRegistered" />
								</div>
			                    <div class="countLabel"><bbbl:label key="lbl_regflyout_giftsregistered" language ="${pageContext.request.locale.language}"/></div>
			                </div>
			                <div class="grid_2">
			                	<c:choose>
			             			<c:when test="${isFutureDate}">
				                    	<c:if test="${not empty eventType && eventType ne 'Wedding' && eventType ne 'Baby' && eventType ne 'Other' &&
				                    		eventType ne 'Commitment Ceremony' &&  eventType ne 'Others'}">
				                    		<div class="countLabel"> &nbsp;</div>
				                    	</c:if>
				                   	</c:when>
				               		<c:otherwise>
				               			<c:choose>
											<c:when test="${not empty eventType && eventType eq 'Wedding'}">
												<div class="countLabel"> &nbsp;</div>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Retirement'}">
												<div class="countLabel"> &nbsp;</div>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
												<div class="countLabel"> &nbsp;</div>
											</c:when>
											<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
												<div class="countLabel"> &nbsp;</div>
											</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>
			                    <div class="count"><dsp:valueof param="registrySummaryVO.giftPurchased"/></div>
			                    <div class="countLabel"><bbbl:label key="lbl_regflyout_giftspurchased" language ="${pageContext.request.locale.language}"/></div>
			                </div>
	                
			                <div class="last">                            
			                       <div class="count">
                           	                            
									<dsp:droplet name="Switch">
										<dsp:param name="value" param="check" />
										<dsp:oparam name="true">
												<c:choose>
													<c:when test="${not empty eventType && eventType eq 'Wedding'}">
														<dsp:valueof param="daysToGo"/>
														<div class="countLabel">
															<bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" />
														</div>
													</c:when>
													<c:when test="${not empty eventType && eventType eq 'Baby'}">
														<dsp:valueof param="daysToGo"/>
														<div class="countLabel">
															<bbbl:label key='lbl_regflyout_daystogo_day_until_baby' language="${pageContext.request.locale.language}" />
														</div>
													</c:when>
													<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
														<dsp:valueof param="daysToGo"/>
														<div class="countLabel">
															<bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" />
														</div>
													</c:when>
													<c:otherwise>
														<c:if test="${not empty eventType && eventType ne 'Other' &&  eventType ne 'Others'}">
															<div class="countLabel">
															<bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" />
															</div> <dsp:valueof param="daysToGo"/>
															<div class="countLabel">
																<bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" />
															</div>
														</c:if>
													</c:otherwise>
												</c:choose>
										</dsp:oparam>
										<dsp:oparam name="false">
											<c:choose>
												<c:when test="${not empty eventType && eventType eq 'Wedding'}">
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" />
													</div>
													<dsp:valueof param="daysToGo"/>
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" />
													</div>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Baby'}">
													<dsp:valueof param="daysToGo"/>
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" />
													</div>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Birthday'}">
													<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />	
													${daysToNextCeleb}
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" />
													</div>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Retirement'}">
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" />
													</div>
													<dsp:valueof param="daysToGo"/>
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_rest_of_life' language="${pageContext.request.locale.language}" />
													</div>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
													<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />	
													${daysToNextCeleb}
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_another_year' language="${pageContext.request.locale.language}" />*
													</div>
												</c:when>
												<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" />
													</div>
													<dsp:valueof param="daysToGo"/>
													<div class="countLabel">
														<bbbl:label key='lbl_regflyout_daystogo_new_place' language="${pageContext.request.locale.language}" />
													</div>
												</c:when>
												<c:otherwise>
													<c:if test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
														<div class="countLabel">
															<bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" />
														</div>
														<dsp:valueof param="daysToGo"/>
														<div class="countLabel">
															<bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" />
														</div>
													</c:if>
												</c:otherwise>
											</c:choose>
										</dsp:oparam>
									</dsp:droplet>	
		                        </div>
			                </div>	                        
                           </dsp:oparam>
                           
                           <dsp:oparam name="error">
							<dsp:valueof param="errorMsg"></dsp:valueof>
						</dsp:oparam>
                   </dsp:droplet>
                   <%-- Days to go / Days past messages --%>

                </div>
            </div>
            <dsp:getvalueof var="regId" param="registrySummaryVO.registryId"/>
            <dsp:getvalueof var="eventType" param="registrySummaryVO.registryType.registryTypeDesc"/>
            <div id="currentyRegistryLinks" class="clearfix">

                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                    <dsp:param name="value" param="multiReg" />
                    <dsp:oparam name="true">
                    
		                <div class="input fl">

		                    <%-- Dropdown for registries --%>
		                    <div id="findARegistryInfo" class="grid_3 alpha omega">
		
							    <div id="findARegistrySelectType">
							    	<dsp:include page= "frags/view_manage_registry.jsp">
							    		<dsp:param name="fragName" value="bridalFlyout"/>
							    	</dsp:include>
							    </div>
							    <div class="clear"></div>
							</div>
		                </div>                    
	                    
	                     <div class="lnkRegViewAll">
                            <c:choose>
                                <c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
                                    <a href="${contextPath}/selfservice/CanadaStoreLocator" title='<bbbl:label key="lbl_regflyout_viewregistries" language ="${pageContext.request.locale.language}"/>'>&raquo; <bbbl:label key="lbl_regflyout_viewregistries" language ="${pageContext.request.locale.language}"/></a>
                                </c:when>
                                <c:otherwise>
		                          <c:if test="${MapQuestOn}">
                                    <a href="${contextPath}/selfservice/FindStore" title='<bbbl:label key="lbl_regflyout_viewregistries" language ="${pageContext.request.locale.language}"/>'>&raquo; <bbbl:label key="lbl_regflyout_viewregistries" language ="${pageContext.request.locale.language}"/></a>
                                  </c:if>
                                </c:otherwise>
                            </c:choose>
						</div>
	                    </dsp:oparam>
                    <dsp:oparam name="false">
		                <div class="input fl">
		                    <div class="button button_secondary button_active">
		                        <dsp:a href="${contextPath}/giftregistry/view_registry_owner.jsp" ><bbbt:textArea key="txt_regflyout_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
		                        <dsp:param name="registryId" value="${regId}"/>
								<dsp:param name="eventType" value="${eventType}"/>
		                        </dsp:a>
		                    </div>
		                </div>                      
                    </dsp:oparam>
                </dsp:droplet> 

            </div>
        </div>
    </div>

    <div class="clear"></div>
 <dsp:include page="find_registry_links.jsp"></dsp:include>
</div>
</dsp:page>