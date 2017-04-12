<dsp:page>
<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/${themeFolder}/css/registry.css?v=${buildRevisionNumber}" />
    <%-- Imports --%>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="com/bbb/commerce/giftregistry/droplet/GiftRegistryPaginationDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />
    <dsp:importbean bean="/atg/multisite/Site"/>

    <%-- Variables --%>
    <dsp:getvalueof var="appid" bean="Site.id" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

    <bbb:pageContainer>

        <jsp:attribute name="bodyClass">my-account manage-registry</jsp:attribute>
        <jsp:attribute name="section">accounts</jsp:attribute>
        <jsp:attribute name="pageWrapper">manageRegistry useCertonaJs myAccount</jsp:attribute>
        <%-- <jsp:attribute name="headerRenderer">
        <dsp:include page="/giftregistry/gadgets/header.jsp" flush="true" />
     </jsp:attribute> --%> 
        <jsp:body>
        
            <div class="row">
                <div class="small-12 columns">
                    <h1><bbbl:label key="lbl_regsearch_my_account" language="${pageContext.request.locale.language}" />: <span class="subheader"><bbbl:label key="lbl_regsearch_registeries" language="${pageContext.request.locale.language}" /></span></h1>
                </div>
                <div class="show-for-medium-down small-12">
                    <a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
                </div>
                <div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
                    <c:import url="/account/left_nav.jsp">
                        <c:param name="currentPage">
                        <bbbl:label key="lbl_myaccount_registries" language="${pageContext.request.locale.language}" />
                    </c:param>
                    </c:import>
                </div>
                <div class="small-12 large-9 columns">
                    <div class="row">
                        <div class="small-12 columns error">
                            <dsp:droplet name="ErrorMessageForEach">
                                <dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions" />
                                <dsp:oparam name="output">
                                    <dsp:valueof param="message" /><br>
                                </dsp:oparam>
                            </dsp:droplet>
                        </div>
                        <!-- Create registry is not a TBS function -->
                        <%-- <div class="small-12 large-6 columns">
                            <c:choose>
                                <c:when test="${registryCount == 0}">
                                    <bbbt:textArea key="txt_create_a_registry" language="${pageContext.request.locale.language}" />
                                </c:when>
                                <c:otherwise>
                                    <bbbt:textArea key="txt_another_registry" language="${pageContext.request.locale.language}" />
                                </c:otherwise>
                            </c:choose>
                            <dsp:include page="/giftregistry/my_registry_type_select.jsp" />
                        </div> --%>
                        <div class="small-12 columns">
                            <bbbt:textArea key="txt_why_registry" language="${pageContext.request.locale.language}" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="small-12 columns">
                            <h3><bbbl:label key="lbl_regflyout_looking_old_reg" language="${pageContext.request.locale.language}" /></h3>
                            <p class="p-secondary"><bbbl:label key="lbl_regflyout_if_created" language="${pageContext.request.locale.language}" /></p>
                            <dsp:form action="#" id="findOldRegistry" method="post">
                                <h3 class="findOldRegistry"><bbbl:label key='lbl_regsearch_lnktxt' language="${pageContext.request.locale.language}" /></h3>
                                <div class="row">
                                    <div class="small-12 large-3 columns">
                                        <div class="row">
                                            <div class="small-12 columns">
                                                <c:set var="placeholder"><bbbl:label key='lbl_reg_firstname' language="${pageContext.request.locale.language}" /></c:set>
                                                <dsp:input type="text" name="registryFirstName" id="registryFirstName" bean="GiftRegistryFormHandler.registrySearchVO.firstName" beanvalue="GiftRegSessionBean.requestVO.firstName">
                                                    <dsp:tagAttribute name="placeholder" value="${placeholder}"/>
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblregistryFirstName errorregistryFirstName"/>
                                                </dsp:input>
                                            </div>
                                            <div class="small-12 columns">
                                                <c:set var="placeholder"><bbbl:label key='lbl_reg_lastname' language="${pageContext.request.locale.language}" /></c:set>
                                                <dsp:input type="text" name="registryLastName" id="registryLastName" bean="GiftRegistryFormHandler.registrySearchVO.lastName" beanvalue="GiftRegSessionBean.requestVO.lastName">
                                                    <dsp:tagAttribute name="placeholder" value="${placeholder}"/>
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblregistryLastName errorregistryLastName"/>
                                                </dsp:input>
                                            </div>
                                            <div class="small-12 columns">
                                                <c:choose>
                                                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                                        <c:set var="placeholder"><bbbl:label key="lbl_bridalbook_select_province" language="${pageContext.request.locale.language}" /></c:set>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="placeholder"><bbbl:label key="lbl_bridalbook_select_state" language="${pageContext.request.locale.language}" /></c:set>
                                                    </c:otherwise>
                                                </c:choose>
                                                <dsp:select bean="GiftRegistryFormHandler.registrySearchVO.state" name="bbRegistryState" id="stateName">
                                                    <dsp:option>${placeholder}</dsp:option>
                                                    <dsp:droplet name="/com/bbb/selfservice/StateDroplet">
                                                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
                                                        <dsp:oparam name="output">
                                                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                            <dsp:param name="array" param="location" />
                                                                <dsp:oparam name="output">
                                                                    <dsp:getvalueof param="element.stateName" id="stateName" />
                                                                    <dsp:getvalueof param="element.stateCode" id="stateCode" />
                                                                    <dsp:option value="${stateCode}">
                                                                        ${stateName}
                                                                    </dsp:option>
                                                                </dsp:oparam>
                                                            </dsp:droplet>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
                                                </dsp:select>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="small-12 large-1 columns or">
                                        <bbbl:label key='lbl_regsearch_or' language="${pageContext.request.locale.language}" />
                                    </div>
                                    <div class="small-12 large-3 columns">
                                        <c:set var="placeholder"><bbbl:label key='lbl_regsearch_email' language="${pageContext.request.locale.language}" /></c:set>
                                        <dsp:input type="text" name="registryEmail" id="registryEmail" bean="GiftRegistryFormHandler.registrySearchVO.email" beanvalue="GiftRegSessionBean.requestVO.email">
                                            <dsp:tagAttribute name="placeholder" value="${placeholder}"/>
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryEmail errorregistryEmail"/>
                                        </dsp:input>
                                    </div>
                                    <div class="small-12 large-1 columns or">
                                        <bbbl:label key='lbl_regsearch_or' language="${pageContext.request.locale.language}" />
                                    </div>
                                    <div class="small-12 large-3 columns">
                                        <c:set var="placeholder"><bbbl:label key='lbl_regsearch_registry_num' language="${pageContext.request.locale.language}" /></c:set>
                                        <dsp:input type="text" name="registryNumber" id="registryNumber" bean="GiftRegistryFormHandler.registrySearchVO.registryId" beanvalue="GiftRegSessionBean.requestVO.registryId">
                                            <dsp:tagAttribute name="placeholder" value="${placeholder}"/>
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryNumber errorregistryNumber"/>
                                        </dsp:input>
                                    </div>
                                    <div class="small-12 columns">
                                        <c:set var="findRegistryBtn"><bbbl:label key='lbl_regsearch_find_registry' language="${pageContext.request.locale.language}"></bbbl:label></c:set>
                                        <dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="2"/>
                                        <dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearchFromImportRegistryPage" value="${findRegistryBtn}" id="findRegistryBtn" iclass="small button primary"/>
                                    </div>
                                </div>
                            </dsp:form>
                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="small-12 columns">
                            <div class="searchResults findOldRegistryResults">

                                <%-- <input type="hidden" name="pageNumber" value="${pagNum}" /> --%>
                                <dsp:getvalueof var="pagNum" param="pagNum" scope="request" />
                                <dsp:getvalueof var="perPage" param="pagFilterOpt"  scope="request" />
                                <dsp:getvalueof var="sortBy" param="sortPassString" scope="request" />
                                <dsp:getvalueof var="sortOrder" param="sortOrder"   scope="request" />

                                <%-- Sorting Logic --%>
                                <c:choose>
                                    <c:when test="${sortBy eq 'NAME'}">
                                        <c:set var="sortOrderName" value="ASCE" scope="request" />
                                        <c:if test="${sortOrder=='ASCE'}">
                                            <c:set var="sortOrderName" value="DESC" scope="request" />
                                        </c:if>
                                        <c:set var="sortOrderType" value="ASCE" scope="request" />
                                        <c:set var="sortOrderDate" value="ASCE" scope="request" />
                                        <c:set var="sortOrderState" value="ASCE" scope="request" />
                                    </c:when>
                                    <c:when test="${sortBy eq 'EVENTTYPEDESC'}">
                                        <c:set var="sortOrderName" value="ASCE" scope="request" />
                                        <c:set var="sortOrderType" value="ASCE" scope="request" />
                                        <c:if test="${sortOrder=='ASCE'}">
                                            <c:set var="sortOrderType" value="DESC" scope="request" />
                                        </c:if>
                                        <c:set var="sortOrderDate" value="ASCE" scope="request" />
                                        <c:set var="sortOrderState" value="ASCE" scope="request" />
                                    </c:when>
                                    <c:when test="${sortBy eq 'DATE'}">
                                        <c:set var="sortOrderName" value="ASCE" scope="request" />
                                        <c:set var="sortOrderType" value="ASCE" scope="request" />
                                        <c:set var="sortOrderDate" value="ASCE" scope="request" />
                                        <c:if test="${sortOrder=='ASCE'}">
                                            <c:set var="sortOrderDate" value="DESC" scope="request" />
                                        </c:if>
                                        <c:set var="sortOrderState" value="ASCE" scope="request" />
                                    </c:when>
                                    <c:when test="${sortBy eq 'STATE'}">
                                        <c:set var="sortOrderName" value="ASCE" scope="request" />
                                        <c:set var="sortOrderType" value="ASCE" scope="request" />
                                        <c:set var="sortOrderDate" value="ASCE" scope="request" />
                                        <c:set var="sortOrderState" value="ASCE" scope="request" />
                                        <c:if test="${sortOrder=='ASCE'}">
                                            <c:set var="sortOrderState" value="DESC" scope="request" />
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="sortOrderName" value="DESC" scope="request" />
                                        <c:set var="sortOrderType" value="ASCE" scope="request" />
                                        <c:set var="sortOrderDate" value="ASCE" scope="request" />
                                        <c:set var="sortOrderState" value="ASCE" scope="request" />
                                    </c:otherwise>
                                </c:choose>

                                <c:if test="${empty perPage}">
                                    <c:set var="perPage" value="24" scope="request" />
                                </c:if>
                                <c:if test="${empty pagNum}">
                                    <c:set var="pagNum" value="1" scope="request" />
                                </c:if>

                                <dsp:getvalueof var="previousPage" param="previousPage" />
                                <c:set var="countButton" value="0" scope="request" />
                                <dsp:droplet name="GiftRegistryPaginationDroplet">
                                    <dsp:param name="pageNo" value="${pagNum}" />
                                    <dsp:param name="perPage" value="${perPage}" />
                                    <dsp:param name="siteId" value="${appid}"/>
                                    <dsp:param name="sortOrder" value="${sortOrder}" />
                                    <dsp:oparam name="output">
                                        <dsp:droplet name="IsEmpty">
                                            <dsp:param param="registrySummaryResultList" name="value" />
                                            <dsp:oparam name="false">
                                                <dsp:getvalueof var="totalCount" param="totalCount" scope="request" />
                                                <dsp:getvalueof var="registrySummaryResultList" param="registrySummaryResultList" />
                                                <dsp:getvalueof var="currentResultSize" value="${fn:length(registrySummaryResultList)}" scope="request" />
                                                <c:set var="totalTabFlot"   value="${totalCount/perPage}" />
                                                <c:set var="totalTab" value="${totalTabFlot+(1-(totalTabFlot%1))%1}"    scope="request" />
                                                <dsp:droplet name="ForEach">
                                                    <dsp:param name="array" param="registrySummaryResultList" />
                                                    <dsp:oparam name="outputStart">
                                                        <dsp:include page="registry_pagination.jsp">
                                                            <dsp:param name="currentResultSize" value="${currentResultSize}" />
                                                            <dsp:param name="totalCount"  value="${totalCount}" />
                                                            <dsp:param name="perPage" value="${perPage}" />
                                                            <dsp:param name="totalTab" value="${totalTab}" />
                                                        </dsp:include>
                                                        
                                                        <form id="importRegistryDialogForm" action="#" method="post" class="small-12 columns no-padding">
                                                                        
                                                                        <div class="small-12 large-2 columns no-padding-left">
                                                                            <a class="sortContent" href="reg_import_msg_display.jsp?sortPassString=NAME&pagFilterOpt=${perPage}&sortOrder=${sortOrderName}" title="<bbbl:label key='lbl_reg_name' language='${pageContext.request.locale.language}' />">
                                                                                <bbbl:label key='lbl_reg_name' language="${pageContext.request.locale.language}" />
                                                                            </a>
                                                                        </div>
                                                                        <div class="small-12 large-2 columns no-padding-left">
                                                                            <a href="reg_import_msg_display.jsp?sortPassString=EVENTTYPEDESC&pagFilterOpt=${perPage}&sortOrder=${sortOrderType}" title="Event Type" class="blue sortContent">
                                                                                <bbbl:label key='lbl_reg_event_type' language="${pageContext.request.locale.language}" />
                                                                            </a>
                                                                        </div>
                                                                        <div class="small-12 large-2 columns no-padding-left">
                                                                            <a href="reg_import_msg_display.jsp?sortPassString=DATE&pagFilterOpt=${perPage}&sortOrder=${sortOrderDate}" title="Date" class="blue sortContent">
                                                                                <bbbl:label key='lbl_reg_date' language="${pageContext.request.locale.language}" />
                                                                            </a>
                                                                        </div>
                                                                        <div class="small-12 large-1 columns"><a href="reg_import_msg_display.jsp?sortPassString=STATE&pagFilterOpt=${perPage}&sortOrder=${sortOrderState}" title="State" class="blue sortContent">
                                                                                <bbbl:label key='lbl_reg_state' language="${pageContext.request.locale.language}" />
                                                                            </a>
                                                                        </div>
                                                                        <div class="small-12 large-4 columns">
                                                                            <bbbl:label key='lbl_reg_password' language="${pageContext.request.locale.language}" />
                                                                        </div>
                                                                        <br/><hr/>
                                                    </dsp:oparam>
                                                    <dsp:oparam name="output">
                                                            <c:set var="countButton" value="${countButton + 1}" scope="request" />
                                                                <c:set var="currentCount">
                                                                    <dsp:valueof param="count" />
                                                                </c:set>
                                                            <div class="detail registryInformation">
                                                                <div class="small-12 large-2 columns no-padding-left">
                                                                    <dsp:valueof param="element.primaryRegistrantFirstName" />
                                                                    <dsp:droplet name="IsEmpty">
                                                                        <dsp:getvalueof param="element.coRegistrantFirstName" id="fName">
                                                                            <dsp:param value="${fName}" name="value" />
                                                                        </dsp:getvalueof>
                                                                        <dsp:oparam name="false">
                                                                            <c:set var="coRegName"><dsp:valueof param="element.coRegistrantFirstName"/></c:set>
                                                                            <c:if test="${not empty coRegName}">& <dsp:valueof param="element.coRegistrantFirstName" /></c:if>
                                                                        </dsp:oparam>
                                                                    </dsp:droplet>
                                                                </div>
                                                                <div class="small-12 large-2 columns no-padding-left">
                                                                    <dsp:droplet name="IsEmpty">
                                                                        <dsp:param param="element.eventType" name="value"/>
                                                                        <dsp:oparam name="false">
                                                                            <dsp:valueof param="element.eventType"/>
                                                                        </dsp:oparam>
                                                                        <dsp:oparam name="true">&nbsp;</dsp:oparam>
                                                                    </dsp:droplet>
                                                                </div>
                                                                <div class="small-12 large-2 columns no-padding-left">
                                                                    <dsp:droplet name="IsEmpty">
                                                                        <dsp:param param="element.eventDate" name="value"/>
                                                                        <dsp:oparam name="false">
                                                                            <dsp:valueof param="element.eventDate"/>
                                                                        </dsp:oparam>
                                                                        <dsp:oparam name="true">&nbsp;</dsp:oparam>
                                                                    </dsp:droplet>
                                                                </div>
                                                                <div class="small-12 large-1 columns">
                                                                    <dsp:droplet name="IsEmpty">
                                                                        <dsp:param param="element.state" name="value"/>
                                                                        <dsp:oparam name="false">
                                                                            <dsp:valueof param="element.state"/>
                                                                        </dsp:oparam>
                                                                        <dsp:oparam name="true">&nbsp;</dsp:oparam>
                                                                    </dsp:droplet>
                                                                </div>
                                                                <c:set var="registryId"><dsp:valueof param="element.registryId"/></c:set>
                                                                <div class="small-12 large-4 columns">
                                                                    <label id="lblcurrentpassword" class="hidden" for="currentpassword">Password</label>
                                                                    <input id="currentpassword" type="password" autocomplete="off" name="currentpassword" class="showpassRegImport${registryId}" aria-required="true" aria-labelledby="lblcurrentpassword errorcurrentpassword" />
                                                                    <div class="row">
                                                                    <input name=" showPassword" type="checkbox" value="" data-toggle-class="showpassRegImport${registryId}" class="small-1 columns no-margin showPassword" id="showPassword${registryId}" />
                                                                    <label for="showPassword${registryId}" class="small-5 columns no-margin lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                                                                    <dsp:a iclass="small-6 columns no-margin small forgot-password popupShipping" title="Forgot Password" href="${contextPath}/giftregistry/modals/reg_forgot_password.jsp">
                                                                        <dsp:param name="registryId" value="${registryId}" />
                                                                        <bbbl:label key='lbl_reg_forgot_password' language="${pageContext.request.locale.language}" />
                                                                    </dsp:a>
                                                                    </div>
                                                                </div>
                                                                <div class="small-12 large-1 columns">
                                                                    <input type="hidden" name="registry" value="<dsp:valueof param="element.registryId"/>" />
                                                                    <input type="hidden" name="registryTypeName" value="<dsp:valueof param="element.eventCode"/>" />
                                                                    <input type="hidden" name="registryEventDate" value="<dsp:valueof param="element.eventDate"/>" />
                                                                    <c:set var="importRegistryBtn">
                                                                        <bbbl:label key='lbl_reg_import' language="${pageContext.request.locale.language}"></bbbl:label>
                                                                    </c:set>
                                                                    <input type="button" id="import${countButton}" value=${importRegistryBtn} class="hidden" role="button" aria-pressed="false" aria-labelledby="import${countButton}" />
                                                                    <a href="#" role="link" class="tiny button secondary allCaps triggerSubmit" data-submit-button="import${countButton}">
                                                                        <strong><bbbl:label key='lbl_reg_import' language="${pageContext.request.locale.language}"></bbbl:label></strong>
                                                                    </a>
                                                                </div>
                                                                </div>
                                                    </dsp:oparam>
                                                    <dsp:oparam name="outputEnd">
                                                            
                                                        </form>
                                                        <form class="hidden" id="importRegistryDialogMainForm" action="${contextPath}/giftregistry/find_old_reg_response.jsp" method="post">
                                                            <input type="hidden" name="registryID" value="" />
                                                            <input type="hidden" name="registryPassword" value="" />
                                                            <input type="hidden" name="eventDate" value="" />
                                                            <input type="hidden" name="eventType" value="" />
                                                        </form>
                                                        <dsp:include page="registry_pagination.jsp">
                                                            <dsp:param name="currentResultSize" value="${currentResultSize}" />
                                                            <dsp:param name="totalCount"  value="${totalCount}" />
                                                            <dsp:param name="perPage" value="${perPage}" />
                                                            <dsp:param name="totalTab" value="${totalTab}" />
                                                        </dsp:include>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                            </dsp:oparam>
                                            <dsp:oparam name="true">
                                                <span class="error"><bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" /></span>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </dsp:oparam>
                                    <dsp:oparam name="error">
                                        <dsp:getvalueof var="errorMsg" param="errorMsg" />
                                        <span class="error"><bbbl:label key="${errorMsg}" language="${pageContext.request.locale.language}" /></span>
                                    </dsp:oparam>
                                    <dsp:oparam name="empty">
                                        <span class="error"><bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" /></span>
                                    </dsp:oparam>
                                </dsp:droplet>

                                <div id="importRegistryDialog" class="hidden" title="Import Registry">
                                    <p><bbbl:label key='lbl_import_success_msg' language="${pageContext.request.locale.language}" /></p>
                                    <dsp:a id="btnCloseimportRegistryDialog" href="${contextPath}/giftregistry/my_registries.jsp" iclass="small button primary">
                                        <bbbl:label key='lbl_reg_ok' language="${pageContext.request.locale.language}" />
                                    </dsp:a>
                                </div>
                                <div id="importRegistryDialogError" class="hidden" title="Import Registry Error">
                                    <p><label id="errLabel"></label></p>
                                    <dsp:a href="#" id="btnCloseimportRegistryDialogError" iclass="close-any-dialog small button secondary">
                                        <bbbl:label key='lbl_reg_ok' language="${pageContext.request.locale.language}" />
                                    </dsp:a>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="regFindOldRegistry" class="reveal-modal medium" data-reveal-ajax="true" data-reveal>
                <bbbe:error key='err_js_find_reg_multi_input' language='${language}'/>
                <a class="close-reveal-modal">&#215;</a>
            </div>
        </jsp:body>

    </bbb:pageContainer>

</dsp:page>
