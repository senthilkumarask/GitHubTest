<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/com/bbb/commerce/checklist/droplet/CheckListDroplet" />
    <dsp:page>
        <dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="sessionRegistry" />
        <dsp:getvalueof bean="SessionBean.activateGuideInRegistryRibbon" var="activateGuideInRegistryRibbon" />
       	<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />  
        <c:if test="${activateGuideInRegistryRibbon}">
        <dsp:getvalueof var="sessionMap" bean="SessionBean.values"/>
        <dsp:getvalueof var="selectedGuideType" value="${sessionMap.selectedGuideType}"/>
        </c:if>
        <c:set var="checkListKey" value="${sessionRegistry.registryType.registryTypeDesc}CheckListProgress" />

        <c:set var="registryCheckListKey">
            <bbbc:config key="${checkListKey}" configName="ContentCatalogKeys" />
        </c:set>


        <c:if test="${fn:containsIgnoreCase(sessionRegistry.registryType.registryTypeDesc, 'Commitment')}">
            <c:set var="registryCheckListKey">
                <bbbc:config key="CommitmentCheckListProgress" configName="ContentCatalogKeys" />
            </c:set>
        </c:if>

        <c:if test="${fn:containsIgnoreCase(sessionRegistry.registryType.registryTypeDesc, 'College')}">
            <c:set var="registryCheckListKey">
                <bbbc:config key="CollegeCheckListProgress" configName="ContentCatalogKeys" />
            </c:set>
        </c:if>
        
        <c:choose>
            <c:when test="${currentSiteId == 'BuyBuyBaby'}">
                <c:set var="parentTheme" value="fullViewBabyTheme" />
            </c:when>
            <c:otherwise>
                <c:set var="parentTheme" value="fullViewDefaultTheme" />
            </c:otherwise>
        </c:choose>
        <c:set var="printLabel"><bbbl:label key='lbl_print_checklist' language="${pageContext.request.locale.language}"/></c:set>
        <dsp:getvalueof bean="SessionBean" var="bean"/>
        <c:if test="${registryCheckListKey ne 'on' or activateGuideInRegistryRibbon}">
         <dsp:droplet name="CheckListDroplet">
                <dsp:param name="registryType" value="${sessionRegistry.registryType.registryTypeDesc}" />
                <dsp:param name="staticChecklist" value="true" />
                <dsp:param name="guideType" value="${selectedGuideType}" />
                <dsp:param name="sessionBean" value="${bean}" />	
                <dsp:oparam name="output">
                    <dsp:getvalueof var="isDisabled" param="isDisabled" />
                    <dsp:getvalueof var="checkListVO" param="staticChecklistVO" />
                </dsp:oparam>
            </dsp:droplet>
        </c:if>

        <c:set var="interactiveChecklistCacheTimeout"><bbbc:config key="interactiveChecklistCacheTimeout" configName="HTMLCacheKeys" /></c:set>
			<c:choose>
		<c:when test="${activateGuideInRegistryRibbon}">
			<c:set var="cacheKey" value="checklistStaticNAFullView_${selectedGuideType}"></c:set>
		</c:when>
		<c:otherwise><c:set var="cacheKey" value="checklistStaticNAFullView_${sessionRegistry.registryType.registryTypeName}"></c:set></c:otherwise>
	</c:choose>
		
		<section id="checklistFullView" class="clearfix <c:if test="${registryCheckListKey ne 'on' or activateGuideInRegistryRibbon}">staticFullView</c:if> ${parentTheme}" style="width:840px">
            <div class="checklistHeaderWrapper <c:if test="${not activateGuideInRegistryRibbon}">guideFullView</c:if>">

                <c:choose>
            	<c:when test="${currentSiteId == 'BedBathCanada'}">
            	<div class="checklistLogoContainer caCkLogo">
								<div id="siteLogo" class="checklistHeaderLogo">
                                            <a href="/" class="logoBbbCa" onclick="s_objectID=&quot;https://www.bedbathandbeyond.ca/_1&quot;;return this.s_oc?this.s_oc(e):true" >
                                                 <img alt="logo of the Bed Bath &amp; Beyond" src="/_assets/global/images/logo/logo_bbb_ca.png" />
                                            </a>
                                            <a href="https://www.buybuybaby.ca" class="logoBabyCA" onclick="s_objectID=&quot;https://www.buybuybaby.ca/_1&quot;;return this.s_oc?this.s_oc(e):true">
                                                <img src="/_assets/global/images/logo/logo_baby_ca.png" alt="logo of the buy buy baby" />
                                            </a>

								</div>

				</div>
            	</c:when>
            	<c:when test="${currentSiteId == 'BedBathUS'}">
            	<div class="checklistLogoContainer">
   						<div id="siteLogo" class="checklistHeaderLogo">
       						 <a href="/"> <img alt="logo of the Bed Bath &amp; Beyond"
       				<c:choose>
           				<c:when test="${not activateGuideInRegistryRibbon}">
             				<c:choose>
       						 <c:when test="${sessionRegistry.registryType.registryTypeDesc eq 'Wedding'}">src="/_assets/global/images/logo/logo_reg_new.png"</c:when>
            				<c:otherwise>src="/_assets/global/images/logo/logo_br.png"</c:otherwise>
        					</c:choose>
            			</c:when>
            			<c:otherwise>src="/_assets/global/images/logo/logo_bbb.png"</c:otherwise>
   					</c:choose> /></a>

    			</div>
				</div>
            	</c:when>
            	<c:otherwise>
            	<div class="checklistLogoContainer">
						<div id="siteLogo" class="checklistHeaderLogo">
       						 <a href="/" > <img alt="logo of the Bed Bath &amp; Beyond" src="/_assets/global/images/logo/babyRegistry.png" /></a>
						</div>
				</div>
            	</c:otherwise>
            	</c:choose>
				<div class="checklistHeader">
                    <c:choose>
                <c:when test="${activateGuideInRegistryRibbon}">
                <div id="fullCheckListViewDialogHeading" class="checklistMainHeading">
                ${checkListVO.displayName}
                            </div>
                </c:when>
                <c:otherwise>
                <div id="fullCheckListViewDialogHeading" class="checklistMainHeading">${sessionRegistry.primaryRegistrantFirstName}<c:if test="${sessionRegistry.coRegistrantFirstName ne null }">
                            <bbbl:label key='lbl_mng_regitem_and' language="${pageContext.request.locale.language}" /> ${sessionRegistry.coRegistrantFirstName}</c:if>'s ${sessionRegistry.registryType.registryTypeDesc} <bbbl:label key='lbl_Checklist_full_view_value' language="${pageContext.request.locale.language}" />
                            </div>

                            <c:choose>
                      <c:when test="${fn:containsIgnoreCase(sessionRegistry.registryType.registryTypeDesc, 'Baby')}">
                      <div class="checklistSubHeading">
							 <span><c:if test="${sessionRegistry.eventDate ne '0' and sessionRegistry.eventDate ne 'null'}"><bbbl:label key='lbl_babybook_event_date' language="${pageContext.request.locale.language}" />:</span> <span class="" style="border-right: 1px solid; padding-right:5px">${sessionRegistry.eventDate}</c:if></span> <span><bbbl:label key='lbl_event_baby_gender' language="${pageContext.request.locale.language}" />:</span>
							<span class="reg-id">
							 <c:choose>
		                        <c:when test="${sessionRegistry.eventVO.babyGender == 'B'}">
			                        		It's a Boy!
			                     </c:when>
		                        <c:when test="${sessionRegistry.eventVO.babyGender == 'G'}">
		                        			It's a Girl!
		                        </c:when>
		                        <c:when test="${sessionRegistry.eventVO.babyGender == 'T'}">
		                        			It's multiples!
		                        </c:when>
		                        <c:otherwise>
		                        			It's a surprise!
		                        </c:otherwise>
		                      </c:choose>
		                    </span>
                       </div>
                      </c:when>
                     <c:otherwise>
                     <div class="checklistSubHeading">
							 <span><c:if test="${sessionRegistry.eventDate ne '0' and sessionRegistry.eventDate ne 'null'}">${sessionRegistry.registryType.registryTypeDesc} <bbbl:label key='lbl_bridalshow_date' language="${pageContext.request.locale.language}" />:</span> <span class="" style="border-right: 1px solid; padding-right:5px">${sessionRegistry.eventDate}</c:if></span> <span><bbbl:label key='lbl_regcreate_search_regid' language="${pageContext.request.locale.language}" /></span> <span class="reg-id">${sessionRegistry.registryId}</span>
                       </div>
                     </c:otherwise>
                      </c:choose>
                </c:otherwise>
                </c:choose>

                </div>
                <div class="printIconContainer">
                    <div class="printIcon">
                        <a aria-label="${printLabel}" id="printFullChecklist" href="/store/_includes/header/elements/checklistStaticFullView.jsp">
                            <span>p</span>
                        </a>
                    </div>
                </div>
            </div>
		
        <c:choose>
            <c:when test="${registryCheckListKey eq 'on' and not activateGuideInRegistryRibbon}">
                <dsp:include page="/_includes/header/elements/checklistFullView.jsp">
                    <dsp:param name="registryCheckListKey" value="${registryCheckListKey}" />
                </dsp:include>
            </c:when>
            <c:otherwise>
                <dsp:droplet name="/atg/dynamo/droplet/Cache">
                    <dsp:param name="key" value="${cacheKey}_${currentSiteId}" />
                    <dsp:param name="cacheCheckSeconds" value="${interactiveChecklistCacheTimeout}" />
                    <dsp:oparam name="output">
                        <dsp:include page="/_includes/header/elements/StaticFullView.jsp">
                        <dsp:param name="checkListVO" value="${checkListVO}" />
                        <dsp:param name="activateGuideInRegistryRibbon" value="${activateGuideInRegistryRibbon}" />
                        </dsp:include>
                    </dsp:oparam>
                </dsp:droplet>
                
            </c:otherwise>
        </c:choose>
        </section>
    </dsp:page>