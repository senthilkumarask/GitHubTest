<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/commerce/checklist/droplet/CheckListDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AcceptableGiftRegistryInfoDroplet" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="registrySummaryVO" />
 <dsp:getvalueof var="disableRegistryDropdown" param="disableRegistryDropdown"/>
  <dsp:getvalueof var="registryListViewUri" param="registryListViewUri"/>
   <dsp:getvalueof var="categorypages" param="categorypages"/>
   <dsp:getvalueof var="showStaticHeader" param="showStaticHeader"/>
   <dsp:getvalueof var="checklistExpandedState" param="checklistExpandedState"/>
<dsp:page>

<c:set var="inItSkinnyVO" value="${sessionScope.inItSkinnyVO}" />
<c:if test="${'true' eq inItSkinnyVO }">
	<dsp:droplet name="AcceptableGiftRegistryInfoDroplet">
	</dsp:droplet>
</c:if>
<c:remove var="inItSkinnyVO" scope="session" />

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
                <c:set var="currentSelected"><bbbl:label key='lbl_currently_selected' language="${pageContext.request.locale.language}"/></c:set>
				<dsp:param name="PageType" param="PageType"/>
				<dsp:getvalueof var="catId" param="catId"/>
                <c:set var="clickToView" scope="request"><bbbl:label key='lbl_click_tap_open' language="${pageContext.request.locale.language}"/></c:set>
		<c:set var="interactiveCheckListFlag">
			<bbbc:config key="interactive_checklist_key" configName="FlagDrivenFunctions" />
		</c:set>

				<%--variable babyCAMode is setup in pagestart, but sometimes this page is ajax loaded, so need to reset the value here  --%>
                <c:if test="${empty babyCAMode}">
                    <%-- get active registry in session - registry type can override SessionBean.babyCA value--%>
                    <dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA"/>
					<c:if test="${sessionBabyCA eq 'true' && currentSiteId eq 'BedBathCanada'}">
						<c:set var="babyCAMode" scope="request" value="true"/>
					</c:if>
					<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="sessionRegistry"/>
					<c:if test="${not empty sessionRegistry}">
						<c:set var="regType" value="${sessionRegistry.registryType.registryTypeDesc}" />
						<%-- if viewing baby registry on Bedbath Canada show baby style --%>
						<c:if test="${(currentSiteId eq 'BedBathCanada') and (regType eq 'Baby')}" >
							<c:set var="babyCAMode" scope="request" value="true"/>
						</c:if>
						<c:if test="${(currentSiteId eq 'BedBathCanada') and (regType ne 'Baby')}" >
							<c:set var="babyCAMode" scope="request" value="false"/>
						</c:if>
					</c:if>
				</c:if>

            	<dsp:getvalueof var="prevNextCategoriesVO" bean="SessionBean.checkListPrevNextCategoriesVO"/>
               
                 <c:set var="seeAllGuidesEnabled" scope="request">
                	<bbbc:config key="seeAllGuidesEnabled" configName="FlagDrivenFunctions" />
                </c:set>
                
              <dsp:getvalueof bean="SessionBean.values" var="sessionMapValues" />
              <dsp:getvalueof bean="SessionBean.activateGuideInRegistryRibbon" var="activateGuideInRegistryRibbon" />

              <dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList" />
              <dsp:getvalueof value="${sessionMapValues.guideVoList}" var="guideVOList" />
              <dsp:getvalueof value="${sessionMapValues.selectedGuideType}" var="selectedGuideType" />
               <c:if test="${fn:length(guideVOList) > 0 and activateGuideInRegistryRibbon eq 'false' and fn:length(registrySkinnyVOList) == 0}">
 					<dsp:getvalueof var="activateGuideInRegistryRibbon" value="true" />
 					
 			   </c:if>
              <c:if test="${not empty guideVOList and activateGuideInRegistryRibbon}">
              	<c:forEach items="${guideVOList}" var="guideVO" varStatus="loop">
					<c:if test="${guideVO.guideTypeCode eq selectedGuideType}">
						<c:set var="selectedGuideVO" value="${guideVO}"/>
					</c:if>
				</c:forEach>
              	
<%--               	<dsp:getvalueof var="selectedGuideVO" value="${guideVOList[0]}" /> --%>
              </c:if>
              <c:if test="${showStaticHeader == 'true'}">
                 <input id='staticChecklistPage' value="static" type="hidden" />
              <div class="subnav grid_12 registryRibbon">
              <c:choose>
                    <c:when test="${siteId eq 'BuyBuyBaby'}">
                        <c:set var="eventType" value="BA1"/>                                                  
                    </c:when>
                    <c:otherwise>
                        <c:set var="eventType" value="BRD"/>                                                	
                   </c:otherwise>
               </c:choose>	
                <dsp:getvalueof var="notloggedIn" bean="Profile.transient"/>   
               <c:choose>
               <c:when test="${!notloggedIn}">
                  	
             <div class="grid_11 omega">
             </c:when>
             <c:otherwise>
           
              <div class="grid_9 omega">
             </c:otherwise>
             </c:choose>
             		<!-- <span class="<c:if test="${currentSiteId ne 'BuyBuyBaby'}">fl staticCLLogo</c:if>"> --> 
                <c:if test="${currentSiteId ne 'BuyBuyBaby'}">  
                  <span class="fl staticCLLogo"> wedding <img src="/_assets/bbregistry/images/icons/and.png" alt="and"> 
                  gift registry    </span>
                </c:if>
             		<c:if test="${currentSiteId eq 'BuyBuyBaby'}">  
                  <span class="fl"> <img src="/_assets/bbbaby/images/babyRegLogo.png" alt="baby registry logo"> </span>
                </c:if>
             		<span class="ribbonCreateReg">
                         <a href="/store/giftregistry/simpleReg_creation_form.jsp?regType=${eventType}"><bbbl:label key='lbl_create_registry' language="${pageContext.request.locale.language}" /></a>
                    </span>
			</div>	
			
			 <c:if test="${notloggedIn}">
			 <div class="grid_3 omega "> 
                  <span class='orRegStatic'><bbbl:label key='lbl_tbs_ord_or' language="${pageContext.request.locale.language}" /> </span>
                 <span  class="manageRegStatic"> <a href="/store/giftregistry/my_registries.jsp" title="<bbbl:label key='lbl_registry_manage_reg' language="${pageContext.request.locale.language}" />"><bbbl:label key='lbl_registry_manage_reg' language="${pageContext.request.locale.language}" /></a></span>
                                                
                 </div>   
                 </c:if>      
             </div>
              </c:if>
               <c:set var="registryCount" scope="request" value="${fn:length(registrySkinnyVOList)}" />
               <c:set var="guideCount" scope="request" value="${fn:length(guideVOList)}" />
               <c:set var="showRegistryGuideHeading" value="${false}"/>
               <input type="hidden" value="${activateGuideInRegistryRibbon}" name="guideFlag"/>
               <c:if test="${( registryCount gt 0 or not empty registrySummaryVO ) and guideCount gt 0}"><c:set var="showRegistryGuideHeading" value="${true}"/></c:if>
              <c:if test="${not empty registrySummaryVO or not empty guideVOList}">

                <div class="subnav grid_12 registryRibbon <c:if test="${not empty selectedGuideVO}"> fromGuide</c:if>">
                    <div class="grid_9 omega settingsMenu">
                        <c:if test="${not empty registrySummaryVO}" >
                        	<c:set var="regFirstName" scope="request" value="${registrySummaryVO.primaryRegistrantFirstName}" />
                        	<c:set var="coregFirstName" scope="request" value="${registrySummaryVO.coRegistrantFirstName}" />
                        	<c:set var="regName" scope="request" value="${registrySummaryVO.registryType.registryTypeDesc}" />
                        </c:if>
                        <c:choose>
                        	<c:when test="${not empty selectedGuideVO}">
	                        			<dsp:include page="/_includes/header/elements/manage_checklist_guide_ribbon.jsp" >
 												<dsp:param name="guideType" value="${selectedGuideVO.guideTypeCode}"/>
 												<dsp:param name="selectedGuideVO" value="${selectedGuideVO}"/>
										</dsp:include>
	                       </c:when>
	                        <c:when test="${not empty registrySummaryVO}">
	                        			<dsp:getvalueof var="registryId" value="${registrySummaryVO.registryId}"/>
	                        			<dsp:include page="/_includes/header/elements/manage_checklist_ribbon.jsp" >
											<dsp:param name="registryId" value="${registryId}"/>
											<dsp:param name="primaryRegistrantFirstName" value="${registrySummaryVO.primaryRegistrantFirstName}"/>
											<dsp:param name="coRegistrantFirstName" value="${registrySummaryVO.coRegistrantFirstName}"/>
										</dsp:include>
	                        </c:when>
	                        
                       </c:choose>

						<%-- code changes for BBBI-364 starts --%>
                        <div class="activeRegistry">
                        	<c:choose>
                        		<c:when test="${not empty selectedGuideVO && !disableRegistryDropdown}"> 
                        			<a aria-hidden='false' href="javascript:void(0);" class="linkedRegistries flyout-menulink" data-placement="bottom" data-hasdropdownrole="true" data-menu-content="#linkedRegistriesContent" data-guideId="${selectedGuideVO.guideTypeCode}" aria-label="<c:if test="${guideCount >1 || (guideCount == 1 and registryCount >=1)}">${clickToView} guide dropdown.</c:if> ${currentSelected} ${selectedGuideVO.guideDisplayName}">
		                                <span aria-hidden='true' class="linkedRegistryName">${selectedGuideVO.guideDisplayName}</span>
		                               <span aria-hidden='true' class="icon-downarrow <c:if test="${not seeAllGuidesEnabled and guideCount < 2 and registryCount <1}">hidden</c:if>"></span>
                            		</a>
                        		</c:when>
                        		<c:when test="${empty selectedGuideVO and not empty registrySummaryVO}">
                        			<a aria-hidden='false' href="javascript:void(0);" class="linkedRegistries flyout-menulink <c:if test="${registryCount < 2 and guideCount < 1 || disableRegistryDropdown}"> singleregistry </c:if> <c:if test="${disableRegistryDropdown}">ajax-check</c:if>" data-placement="bottom" data-hasdropdownrole="true" data-menu-content="#linkedRegistriesContent" data-regTypeDesc="${registrySummaryVO.registryType.registryTypeDesc}"  data-registryid="${registrySummaryVO.registryId}" data-registrytype="${registrySummaryVO.registryType.registryTypeName}" aria-label="${clickToView} registry dropdown. ${currentSelected} ${registrySummaryVO.primaryRegistrantFirstName}<c:if test="${registrySummaryVO.coRegistrantFirstName ne null }"> <bbbl:label key='lbl_mng_regitem_and' language="${pageContext.request.locale.language}" /> ${registrySummaryVO.coRegistrantFirstName}</c:if>'s ${registrySummaryVO.registryType.registryTypeDesc}">
		                                <span aria-hidden='true' class="linkedRegistryName">${registrySummaryVO.primaryRegistrantFirstName}<c:if test="${registrySummaryVO.coRegistrantFirstName ne null }"> <bbbl:label key='lbl_mng_regitem_and' language="${pageContext.request.locale.language}" /> ${registrySummaryVO.coRegistrantFirstName}</c:if>'s ${registrySummaryVO.registryType.registryTypeDesc}</span>
		                                <span aria-hidden='true' class="icon-downarrow"></span>
                            		</a>
                        		</c:when>
                        	</c:choose>
                        
                            <div id="linkedRegistriesContent" class="flyout-menu-content hidden">
                            <div class="">
	                            <c:if test="${showRegistryGuideHeading}">
	                            	<h2 class="flyout-registry"><bbbl:label key="lbl_checklist_your_registry" language="${pageContext.request.locale.language}"/></h2>
	                            </c:if>
                                <c:if test="${not empty registrySummaryVO}">
                                	<ul class="flyout-menu flyout-dropdown-items registry-detail-content">

                                    <li class='dropdown-item <c:if test="${empty selectedGuideVO}"> dropdown-item-selected </c:if> ' data-dropdown-value="${registrySummaryVO.primaryRegistrantFirstName}<c:if test="${registrySummaryVO.coRegistrantFirstName ne null }"> <bbbl:label key='lbl_mng_regitem_and' language="${pageContext.request.locale.language}" /> ${registrySummaryVO.coRegistrantFirstName}</c:if>'s ${registrySummaryVO.registryType.registryTypeDesc}" data-registrytype-text="${registrySummaryVO.registryType.registryTypeDesc}" <c:if test="${not activateGuideInRegistryRibbon}">data-selected="true"</c:if> data-registryid="${registrySummaryVO.registryId}" data-registrytype="${registrySummaryVO.registryType.registryTypeName}">
                                                <a href="javascript:void(0);">
                                                	<span class="tick-registry <c:if test="${not empty selectedGuideVO}"> visibilityHidden</c:if>"></span>
                                                    <span>${registrySummaryVO.primaryRegistrantFirstName}<c:if test="${registrySummaryVO.coRegistrantFirstName ne null }"> and ${registrySummaryVO.coRegistrantFirstName}</c:if>'s ${registrySummaryVO.registryType.registryTypeDesc}</span>
                                                    <span class="visuallyhidden"> <bbbl:label key='lbl_checklist_registry_text' language="${pageContext.request.locale.language}"/></span>
                                                    <span><c:if test="${registrySummaryVO.eventDate ne '0' and registrySummaryVO.eventDate ne 'null'}">${registrySummaryVO.eventDate}</c:if></span>
                                                </a>
                                            </li>

                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" value="${registrySkinnyVOList}" />
                                        <dsp:oparam name="output">
                                            <dsp:getvalueof param="element.eventType" var="eventType" />
                                            <dsp:getvalueof param="element.coRegistrantFirstName" var="coRegistrantFirstName" />
                                            <dsp:getvalueof param="element.registryId" var="registryId" />
                                             <dsp:getvalueof param="element.eventCode" var="eventCode" />

											<c:if test="${registrySummaryVO.registryId ne registryId}">
                                             <li class="dropdown-item" data-dropdown-value="<dsp:valueof param="element.primaryRegistrantFirstName" /><c:if test="${coRegistrantFirstName ne null }"> and <dsp:valueof param="element.coRegistrantFirstName" /></c:if>'s <dsp:valueof param="element.eventType" />" data-registrytype-text='<dsp:valueof param="element.eventType" />' data-registryid="${registryId}" data-registrytype="${eventCode}">
                                                <a href="javascript:void(0);">
                                                	<span class="tick-registry visibilityHidden"></span>
                                                    <span><dsp:valueof param="element.primaryRegistrantFirstName" /><c:if test="${coRegistrantFirstName ne null }"> <bbbl:label key='lbl_mng_regitem_and' language="${pageContext.request.locale.language}" /> <dsp:valueof param="element.coRegistrantFirstName" /></c:if>'s <dsp:valueof param="element.eventType" /></span>
                                                    <span class="visuallyhidden"> <bbbl:label key='lbl_checklist_registry_text' language="${pageContext.request.locale.language}"/></span>
                                                    <span><dsp:valueof param="element.eventDate" /></span>
                                                </a>
                                            </li>
                                            </c:if>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </ul>
                                </c:if>
                                <c:if test="${showRegistryGuideHeading}">
                                	<h2 class="flyout-guide"><bbbl:label key="lbl_checklist_shopping_guides" language="${pageContext.request.locale.language}"/></h2>
                                </c:if>
	                                <ul class="flyout-menu flyout-dropdown-items guide-detail-content">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" value="${guideVOList}" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="element.guideTypeCode" var="guideType" />
												<dsp:getvalueof param="element.guideDisplayName" var="guideDisplayName" />
												<c:choose>
													<c:when test="${guideType eq selectedGuideType}">
														<li class="dropdown-item <c:if test="${not empty selectedGuideVO}"> dropdown-item-selected </c:if> guide" data-dropdown-value="${guideDisplayName}" <c:if test="${activateGuideInRegistryRibbon}">data-selected="true"</c:if> data-guideid="${guideType}">
															<dsp:a href="#">
																<span class="tick-registry <c:if test="${empty selectedGuideVO}"> visibilityHidden </c:if> "></span>
																<span> ${guideDisplayName}</span>
															</dsp:a>
														</li>
													</c:when>
													<c:otherwise>
														<c:if test="${guideCount gt 1}">
															<li class="dropdown-item guide" data-dropdown-value="${guideDisplayName}" data-guideid="${guideType}">
																<dsp:a href="#">
																	<span class="tick-registry visibilityHidden"></span>
																	<span> ${guideDisplayName}</span>
																</dsp:a>
															</li>
														</c:if>
													</c:otherwise>
												</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</ul>
									<c:if test="${guideCount gt 0 and seeAllGuidesEnabled}">
										<div class="see-all-guides">
												<dsp:a href="/store/static/GuidesPage">
													<span><bbbl:label key="lbl_checklist_see_all_guides" language="${pageContext.request.locale.language}"/></span>
												</dsp:a>
										</div>
									</c:if>
                                </div>
                            </div>
                            <c:if test="${not empty registrySummaryVO}">
	                            <input id='setActiveReg' value="${registrySummaryVO.registryId}" type="hidden" />
	                            <input id='setActiveRegEvent' value="${registrySummaryVO.registryType.registryTypeName}" type="hidden" />
                            </c:if>
                        </div>
                    </div>
                    <%-- code changes for BBBI-364 ends --%>

                    <div class="grid_5 omega clearfix categoryMenu hidden">
						&nbsp;
					</div>
                    <div class="grid_3 omega checklistMenu">
						<dsp:include page="/_includes/header/elements/showRegistryChecklist.jsp" >
							<dsp:param name="guideType" value="${selectedGuideType}"/>
						</dsp:include>
                    </div>
                </div>
                 <dsp:setvalue param="checklistVO" beanvalue="SessionBean.checklistVO"/>
				 <dsp:getvalueof var="checklistVO" param="checklistVO"/>
                 
                
                 
                 <div id="C1-C2-C3-CheckListData" class="c1c2c3Container <c:if test="${!isDisabled}"> c1c2c3ContainerHeight </c:if> <c:if test="${!checklistExpandedState || isDisabled}">hiddenInteractiveDiv</c:if>"> 
                 	<c:if test="${!isDisabled}">
	                <c:choose>
						<c:when test="${ not empty registrySummaryVO and (not empty checklistVO && checklistVO.registryId eq registrySummaryVO.registryId) && !(categorypages eq 'true') and not activateGuideInRegistryRibbon}">
								<dsp:include page="/_includes/header/elements/dynamicRegistryOverlay.jsp">
								</dsp:include>
							<input type="hidden" id="fireInteractiveChecklistAjax" value="false"/>
						</c:when>
					<c:otherwise>
	                      <div class="loading-checklist"><bbbl:label key="lbl_interactive_checklist_loading" language="${pageContext.request.locale.language}"/></div>
	                              <img alt='Loader Image' src='/_assets/global/images/widgets/small_loader.gif' class="loading-image">
	                              <div class="checklist-error hidden" style="text-align: center;color: red;"><span class = "checklist-error-icon"></span> <span style="display: inline-block;"><bbbl:label key='checklist_system_error_occurred' language="${pageContext.request.locale.language}"/></span></div>
					</c:otherwise>
					</c:choose>
				  	</c:if>
                   </div>
                   </c:if>
                   <dsp:include page="../modals/hideGuideConfirmationModal.jsp" />

</dsp:page>
