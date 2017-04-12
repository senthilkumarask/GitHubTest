<dsp:page>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:droplet name="GiftRegistryFlyoutDroplet">
            <dsp:param name="profile" bean="Profile"/>
                <dsp:oparam name="output">
                    <dsp:droplet name="/atg/dynamo/droplet/Switch">
                        <dsp:param name="value" param="userStatus"/>
                                <dsp:oparam name="4">
                                    <dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO" />
                                    <c:set var="manageReg" value="true"/>
                                </dsp:oparam>
                                <dsp:oparam name="2">
                                    <c:set var="manageReg" value="true"/>
                                    <c:set var="noReg" value="true"/>
                                </dsp:oparam>
                                <dsp:oparam name="3">
                                    <dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO" />
                                    <c:set var="manageReg" value="true"/>
                                </dsp:oparam>
                                <dsp:oparam name="1">
                                    <c:set var="loggedOut" value="true"/>
                                </dsp:oparam>
                        </dsp:droplet>
            </dsp:oparam>
    </dsp:droplet>
    <c:set var="TBS_BedBathUSSite">
        <bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="TBS_BuyBuyBabySite">
        <bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <c:set var="TBS_BedBathCanadaSite">
        <bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <dsp:getvalueof id="currentSiteId" bean="Site.id" />
    <c:choose>
        <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
            <c:set var="simpleRegCreationFormPath" value="/giftregistry/simpleReg_creation_form_mobile.jsp" />
            <c:set var="siteSpecFragName" value="babyLanding" />
            <c:set var="siteSpecHandlerMethod" value="registrySearchFromBabyLanding" />
        </c:when>
        <c:otherwise>
            <c:set var="simpleRegCreationFormPath" value="/giftregistry/simpleReg_creation_form_bbus_mobile.jsp" />
            <c:set var="siteSpecFragName" value="bridalLanding" />
            <c:set var="siteSpecHandlerMethod" value="registrySearchFromBridalLanding" />
        </c:otherwise>
    </c:choose>
    <bbb:pageContainer>
        <jsp:body>
            <c:if test="${manageReg eq 'true'}">
                <div class="small-12 large-4 columns padding-10 on-mob mob-and-index">
                    <!-- RM# 33676 display View and manage registry link-->
                    <div class="widget-cover">
                        <c:choose>
                            <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                                <h1 class="purple"><bbbl:label key="lbl_registry_manage_reg" language ="${pageContext.request.locale.language}"/></h1>
                            </c:when>
                            <c:otherwise>
                                <h3 class="purple"><bbbl:label key="lbl_registry_manage_reg" language ="${pageContext.request.locale.language}"/></h3>
                            </c:otherwise>
                        </c:choose>
                        <div class="grid_2 clearfix manageReg alpha small-12 columns frmFindARegistry no-padding">  
                              <%-- Dropdown for registries --%>
                              <div id="findARegistryInfo" class="grid_3 alpha omega">
                                <div id="findARegistrySelectType">
                                    <dsp:include page= "../giftregistry/frags/view_manage_registry.jsp">
                                        <dsp:param name="fragName" value="${siteSpecFragName}"/>
                                    </dsp:include>
                                </div>
                                <div class="clear"></div>
                              </div>
                            <div class="clear"></div>
                        </div>
                        <div class="txt"><bbbt:textArea key="txt_manage_reg" language ="${pageContext.request.locale.language}"/></div>
                    </div>
                </div>
            </c:if>
                <c:choose>
                    <c:when test="${loggedOut eq 'true'}">                  
                        <c:choose>
                            <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                                <div class="small-12 large-4 columns padding-10 on-mob mob-and-index crg-on-baby">
                            </c:when>
                            <c:otherwise>
                                <div class="small-12 large-4 columns padding-10 on-mob mob-and-index">
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <div class="small-12 large-4 columns padding-10 mob-and-index">
                    </c:otherwise>
                </c:choose>
            
                <div class="widget-cover">
                    <c:choose>
                        <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                            <h1 class="create-reg">
                        </c:when>
                        <c:otherwise>
                            <h3 class="create-reg">
                        </c:otherwise>
                    </c:choose>
                    
                        <c:choose>
                            <c:when test="${loggedOut eq 'true' || noReg eq 'true'}">
                                <bbbl:label key="lbl_overview_create_registries" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_overview_create_another_registry" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                    <c:choose>
                        <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                            </h1/>
                        </c:when>
                        <c:otherwise>
                            </h3>
                        </c:otherwise>
                    </c:choose>
                    <div class="grid_2 clearfix small-12 columns no-padding">                   
                        <dsp:form>
                            <dsp:select bean="GiftRegistryFormHandler.registryEventType" onchange="callMyAccRegistryTypesFormHandler();" id="typeofregselectMobile">
                            <dsp:droplet name="GiftRegistryTypesDroplet">
                            <dsp:param name="siteId" value="${currentSiteId}"/>
                            <dsp:oparam name="output">
                                <dsp:option value="" selected="selected"><bbbl:label key="lbl_overview_select_type" language="${pageContext.request.locale.language}"/></dsp:option>
                                <dsp:droplet name="ForEach">
                                <dsp:param name="array" param="registryTypes" />
                                <dsp:oparam name="output">
                                <dsp:param name="regTypes" param="element" />
                                    <dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
                                    <dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
                                    <dsp:option value="${registryCode}">
                                        <dsp:valueof param="element.registryName" />
                                    </dsp:option>
                                    <dsp:getvalueof var="regName" param="element.registryName" />
                                </dsp:oparam>
                                </dsp:droplet>
                            </dsp:oparam>
                            </dsp:droplet>
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="typeofregselectMobile"/>
                            <dsp:tagAttribute name="class" value="selector_primary"/>
                            <dsp:tagAttribute name="autocomplete" value="off"/>
                            </dsp:select>
                            <c:choose>
        <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
           <%--  <c:set var="simpleRegCreationFormPath" value="/giftregistry/simpleReg_creation_form_mobile.jsp" /> --%>
             <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="indexBuyBuyBaby" />
        </c:when>
        <c:otherwise>
           <%--  <c:set var="simpleRegCreationFormPath" value="/giftregistry/simpleReg_creation_form_bbus_mobile.jsp" /> --%>
             <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="indexBedBath" />
        </c:otherwise>
    </c:choose>
                            <%-- <dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden" value="${contextPath}/giftregistry/registry_type_select.jsp" />
                            <dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden" value="${contextPath}${simpleRegCreationFormPath}" /> --%>
                           
                            <dsp:input bean="GiftRegistryFormHandler.registryTypes" type="submit" value="" id="submitRegistryClickMobile" iclass="hidden" />
                        </dsp:form>
                     </div>
                    <!-- <c:if test="${loggedOut eq 'true'}">
                     <div class="oregistery-list">
                        <strong><bbbl:label key="lbl_registry_reasons_to_register" language ="${pageContext.request.locale.language}"/></strong>
                        <bbbt:textArea key="txt_reasons_to_register_tbs" language ="${pageContext.request.locale.language}"/>
                    </div>
                    </c:if> -->
                     <div class="oregistery-list">
                        <strong><bbbl:label key="lbl_registry_reasons_to_register" language ="${pageContext.request.locale.language}"/></strong>
                        <bbbt:textArea key="txt_reasons_to_register_tbs" language ="${pageContext.request.locale.language}"/>
                    </div>
                </div>
            </div>
            <c:choose>
                <c:when test="${loggedOut eq 'true'}">
                    <dsp:form action="${contextPath}/giftregistry/registry_search_guest.jsp" method="post">
                        <c:choose>
                            <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                                <div class="row sgr-on-baby">
                            </c:when>
                            <c:otherwise>
                                <div class="row">
                            </c:otherwise>
                        </c:choose>
                        
                            <div class="small-12 columns">
                                <h1><bbbl:label key="lbl_registry_search_for" language="${pageContext.request.locale.language}" /></h1>
                            </div>
                            <div class="small-12 columns">
                                <dsp:input type="text" id="regId" bean="GiftRegistryFormHandler.registrySearchVO.registryId" value="">
                                    <dsp:tagAttribute name="placeholder" value="Registry ID" />
                                    <dsp:tagAttribute name="aria-required" value="false" />
                                    <dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryNumber errorbbRegistryNumber" />
                                </dsp:input>
                            </div>
                            <div class="small-12 columns">
                                <dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearch" iclass="button tiny service right expand" id="regId" value="SEARCH" />
                                <%-- <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchSuccessURL" value="/tbs/giftregistry/registry_search_guest.jsp" />
                                <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchErrorURL" value="/tbs/giftregistry/registry_search_guest.jsp" /> --%>
                                <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="indexRegistrySearch" />
                            </div>
                        <div class="small-1 small-offset-5 large-1 large-offset-0 columns or"><p>or</p></div>
                            <div class="small-12 columns">
                                <div class="row">
                                    <div class="small-12 columns">
                                        <dsp:input type="text" id="regFirst" bean="GiftRegistryFormHandler.registrySearchVO.firstName" maxlength="30" value="">
                                            <dsp:tagAttribute name="placeholder" value="First Name" />
                                            <dsp:tagAttribute name="aria-required" value="false" />
                                            <dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryFirstName errorbbRegistryFirstName" />
                                        </dsp:input>
                                    </div>
                                    <div class="small-12 columns">
                                        <dsp:input type="text" id="regLast" bean="GiftRegistryFormHandler.registrySearchVO.lastName" maxlength="30" value="">
                                            <dsp:tagAttribute name="placeholder" value="Last Name" />
                                            <dsp:tagAttribute name="aria-required" value="false" />
                                            <dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryLastName errorbbRegistryLastName" />
                                        </dsp:input>
                                    </div>
                                    <div class="small-12 columns registry-state">
                                        <c:choose>
                                            <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                                <c:set var="stateLabel" value="Province"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="stateLabel" value="State"/>
                                            </c:otherwise>
                                        </c:choose>
                                        
                                        <a href="#" data-dropdown="regState" class="small download radius button dropdown thin">${stateLabel}<span>&nbsp;</span></a>
                                        <ul id="regState" data-dropdown-content="" class="f-dropdown">
                                            <dsp:droplet name="/com/bbb/selfservice/StateDroplet">
                                                <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
                                                <dsp:oparam name="output">
                                                    <li>
                                                        <a href="#">
                                                            <c:choose>
                                                                <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                                                    <bbbl:label key="lbl_bridalbook_select_province" language="${pageContext.request.locale.language}" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <bbbl:label key="lbl_bridalbook_select_state" language="${pageContext.request.locale.language}" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </a>
                                                    </li>
                                                    <dsp:droplet name="ForEach">
                                                        <dsp:param name="array" param="location" />
                                                        <dsp:oparam name="output">
                                                            <dsp:getvalueof param="element.stateName" id="stateName" />
                                                            <dsp:getvalueof param="element.stateCode" id="stateCode" />
                                                            <li>
                                                                <a href="#" data-state="${stateCode}">
                                                                    ${stateName}
                                                                </a>
                                                            </li>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                                </dsp:oparam>
                                            </dsp:droplet>
                                        </ul>
                                        <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.state" id="bbRegistryState" name="bbRegistryState" value="" />
                                    </div>
                                    <div class="small-12 columns registry-type">
                                        <a href="#" data-dropdown="regType" class="small download radius button dropdown thin">Event Type<span>&nbsp;</span></a>
                                        <ul id="regType" data-dropdown-content="" class="f-dropdown">
                                            <dsp:droplet name="GiftRegistryTypesDroplet">
                                                <dsp:param name="siteId" value="${currentSiteId}"/>
                                                <dsp:oparam name="output">
                                                    <li class="selected"><a href="#">Event Type</a></li>
                                                    <dsp:droplet name="ForEach">
                                                        <dsp:param name="array" param="registryTypes" />
                                                        <dsp:oparam name="output">
                                                            <dsp:param name="regTypes" param="element" />
                                                            <dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
                                                            <dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
                                                            <li><a href="#" data-registry-code="${registryCode}"><dsp:valueof param="element.registryName" /></a></li>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                                </dsp:oparam>
                                            </dsp:droplet>
                                        </ul>
                                        <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.event" id="bbRegistryEvent" name="bbRegistryEvent" value="" />
                                    </div>  
                                </div>
                                <div class="row">
                                    <div class="small-12 columns">
                                        <dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearch" iclass="button tiny service right expand" id="regId" value="SEARCH" />
                                        <%-- <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchSuccessURL" value="/tbs/giftregistry/registry_search_guest.jsp" />
                                        <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchErrorURL" value="/tbs/giftregistry/registry_search_guest.jsp" /> --%>
                                        <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="indexRegistrySearch" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </dsp:form>
                </c:when>
                <c:otherwise>
                    <div class="small-12 large-4 columns padding-10 mob-baby-login">
                        <div class="widget-cover">
                            <c:choose>
                                <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                                    <h1><bbbl:label key='lbl_landing_giveguests' language ="${pageContext.request.locale.language}"/></h1>
                                </c:when>
                                <c:otherwise>
                                    <h3><bbbl:label key='lbl_landing_giveguests' language ="${pageContext.request.locale.language}"/></h3>
                                </c:otherwise>
                            </c:choose>
                            <c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
                            <c:set var="findRegistryFormCount" value="1" scope="request"/>
                            <dsp:include page="/addgiftregistry/find_registry_widget.jsp">
                                <dsp:param name="findRegistryFormId" value="frmFindRegistry" />
                                <dsp:param name="submitText" value="${findButton}" />
                                <dsp:param name="handlerMethod" value="${siteSpecHandlerMethod}" />
                                <dsp:param name="bridalException" value="false" />
                                <dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
                            </dsp:include>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
            
        </jsp:body>
    </bbb:pageContainer>  
    
    <script type="text/javascript">
    function callMyAccRegistryTypesFormHandler() {
        if (document.getElementById('typeofregselectMobile').value!="")
        {
            document.getElementById("submitRegistryClickMobile").click();
        }

    }
    </script>
</dsp:page>


