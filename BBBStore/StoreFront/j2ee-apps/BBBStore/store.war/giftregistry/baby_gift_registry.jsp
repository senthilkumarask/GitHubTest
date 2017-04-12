<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="enableRegSearchById" scope="request"><bbbc:config key="enableRegSearchById" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="pageVariation" value="bb" scope="request" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<div class="pink-arrow"></div>		
<div class="notLoggedIn">
        <div id="findARegistryInfoSelect" class="grid_3 alpha">
		<div class="txtOffScreen">
				<input aria-hidden="true"></input>
			</div>	
            <div id="findARegistryInfo" class="noPadTop">
                <h2><bbbt:textArea key="txt_regflyout_createaregistry" language ="${pageContext.request.locale.language}"/></h2>
               <c:choose> 
		       <c:when test="${(currentSiteId eq 'BedBathCanada') && (babyCAMode =='true')}">		     
                <br>
</c:when>
<c:otherwise>
                <p><strong><bbbl:label key='lbl_find_reg_havent_created' language ="${pageContext.request.locale.language}"/></strong></p>
                <p><bbbl:label key='lbl_find_reg_when_you_reg' language ="${pageContext.request.locale.language}"/></p>
				</c:otherwise>
				</c:choose>

                <div id="findARegistrySelectType">
                    <dsp:include page= "registry_type_select.jsp"/>
                </div>
                <div class="clear"></div>
                <div class="pushDown">
                 <c:choose> 
		       <c:when test="${(currentSiteId eq 'BedBathCanada') && (babyCAMode =='true')}">		     
					<p><strong><bbbl:label key="lbl_bca_regflyout_alreadyregistered" language ="${pageContext.request.locale.language}"/></strong><a class="lnkManageReg" href="${contextPath}/giftregistry/my_registries.jsp?redirectPage=MyRegistries"><bbbl:label key="lbl_regflyout_logintoview" language ="${pageContext.request.locale.language}"/> &raquo;</a></p>			   </c:when>
				<c:otherwise>
                    <p><strong><bbbl:label key="lbl_regflyout_alreadyregistered" language ="${pageContext.request.locale.language}"/></strong><a class="lnkManageReg" href="${contextPath}/giftregistry/my_registries.jsp?redirectPage=MyRegistries"><bbbl:label key="lbl_regflyout_logintoview" language ="${pageContext.request.locale.language}"/> &raquo;</a></p>
                    </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <div class="grid_3">
            <div id="babyGiftRegistryItems"></div>
        </div>
        <c:choose>
			<c:when test="${enableRegSearchById == 'true'}">
				   <div id="findARegistryInfo" class="grid_3 omega babyRegistryWithRegistryId">
			</c:when>
			<c:otherwise>
				   <div id="findARegistryInfo" class="grid_3 omega">
			</c:otherwise>
		</c:choose>	
      
            <div id="findARegistryFormTitle">
                <h2><bbbt:textArea key="txt_regflyout_giveagift" language ="${pageContext.request.locale.language}"/></h2>
                <p><bbbl:label key="lbl_regflyout_information" language ="${pageContext.request.locale.language}"/></p>
            </div>
            <div id="findARegistryFormForm" role="application">			
				<c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<c:set var="findRegistryFormCount" value="0" scope="request"/>
				<dsp:include page="/addgiftregistry/find_registry_widget.jsp">
					<dsp:param name="findRegistryFormId" value="babyFlyOut" />
					<dsp:param name="submitText" value="${findButton}" />
					<dsp:param name="handlerMethod" value="registrySearchFromFlyout" />
					<dsp:param name="bridalException" value="false" />
					<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
					<dsp:param name="flyout" value="true" />
				</dsp:include>				
                <div class="clear"></div>
            </div>
        </div>
        <div class="clear"></div>
         <dsp:include page="find_registry_links.jsp"></dsp:include>
        <div class="clear"></div>
        <div id="findARegistryBottomBorder"></div>
    </div>
</dsp:page>