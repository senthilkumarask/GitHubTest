<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="searchTerm" param="enteredSearchTerm"/>
	<dsp:getvalueof var="currentCategory" param="currentSelectedCat"/>
	<dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown"/>
	<dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
	<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>
	<c:set var="enableRegSearchById" scope="request"><bbbc:config key="enableRegSearchById" configName="FlagDrivenFunctions" /></c:set>
    ${PageType }
    <c:if test="${not empty enteredNarrowDown}" >
    
    <c:set var="searchTerm" value="${enteredNarrowDown}" scope="page" ></c:set>
    
    </c:if>
	<!--BBBI-3804: omniture Tagging :start -->
			<c:set var="keywordBoostFlag"><bbbc:config key="KeywordBoostFlag" configName="FlagDrivenFunctions"/></c:set>
			<c:set var="defaultOmnitureVar"><bbbc:config key="DefaultEndecaOmnitureVariable" configName="OmnitureBoosting"/></c:set>
			<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/> 
			<c:choose>
				<c:when test="${(keywordBoostFlag eq true) || (not empty vendorParam) }">
					<dsp:droplet name="OmnitureVariableDroplet">
				  		<dsp:param name="pageName" value="SEARCH"/>
				  			<dsp:oparam name="output">
				    		<dsp:getvalueof var="OmnitureVariable" param="OmnitureVariable"/>
				 		</dsp:oparam>
			  		</dsp:droplet>
				</c:when>
				<c:otherwise>
					<c:set var="OmnitureVariable" value="${defaultOmnitureVar}"/>
				</c:otherwise>
			
			</c:choose>
			<!--BBBI-3804: omniture Tagging :end -->
	<bbb:pageContainer>
		<jsp:attribute name="section">search</jsp:attribute>
		<jsp:attribute name="pageWrapper">searchList searchResults noSearchResults useCertonaAjax</jsp:attribute>
		<jsp:attribute name="titleString">No Search Results for ${searchTerm}</jsp:attribute>
		<jsp:attribute name="follow">false</jsp:attribute>
		<jsp:attribute name="index">false</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute> 
		
		<jsp:body>
		
			<script type="text/javascript">
				var resx = new Object();
			</script>
			<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/search_frag.jsp" >
					<dsp:param name="searchTerm" value="${searchTerm}"/>
				</dsp:include>
			</c:if>
				
			<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
			<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
            <dsp:importbean bean="/atg/userprofiling/Profile" />
            <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			<dsp:importbean bean="/atg/multisite/Site"/>
			<dsp:getvalueof var="appid" bean="Site.id" />
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
			
			
			
			<dsp:getvalueof var="invalidCriteria" param="invalidCriteria"/>
			<div id="content" class="subCategory container_12 clearfix" role="main">
				<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
				<div class="breadcrumbs grid_12">
					<a href="${scheme}://${servername}" title="<bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" />"><bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" /></a>
					<c:if test ="${errorMsg eq null }">
						<span class="rightCarrot">&gt;</span>
						<span><bbbl:label key="lbl_header_search_results_page_1" language ="${pageContext.request.locale.language}"/><span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></span>
					</c:if>
				</div>
				<div class="pageTitle grid_12">
					<h1 class="mainTitle"><bbbl:label key="lbl_header_search_results_page_2" language ="${pageContext.request.locale.language}"/>   <span class="searchTextDesc">
							 <bbbl:label key="lbl_no_results_header" language ="${pageContext.request.locale.language}"/>
							 &ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;
						</span>
					</h1>
				</div>
				
				<div class="grid_12">
					<div class="clearfix">
						<div class="hero clearfix noBorderBot">
							<div class="grid_6 alpha">
							     <c:choose>
							     	<c:when test ="${errorMsg ne null }"><span class="error">/ <bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></span></c:when>
                                  	<c:when test="${invalidCriteria}"><h2 class="subTitleError test2"><bbbl:label key="lbl_invalid_search_message" language ="${pageContext.request.locale.language}"/></h2></c:when>
                                 	<c:otherwise>
                                 	<%-- REMOVE BELOW LINE AS BUSINESS DOES NOT WANT IT UAT-108 --%>
									<%-- <h2 class="searchSubTitleTextWrap"><bbbl:label key="lbl_no_results_message" language ="${pageContext.request.locale.language}"/>&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</h2>--%>
									</c:otherwise>
                                  </c:choose>
								<bbbt:textArea key="txt_content_no_results" language ="${pageContext.request.locale.language}"/>
								<c:set var="searchContentToCheck"><bbbt:textArea key="txt_no_results_popular_keywords" language ="${pageContext.request.locale.language}"/></c:set>
								<c:if test="${not empty searchContentToCheck && searchContentToCheck ne null}">
									<div class="or">
									<p class="grid_2 alpha omega ruler" >&nbsp;</p>
		               				<p class="grid_1 alpha omega txt"><bbbl:label key="lbl_no_results_sep_or" language ="${pageContext.request.locale.language}"/></p>
		                			<p class="grid_2 alpha omega ruler" >&nbsp;</p>
								</div>
								</c:if>
								
								<div class="clear"></div>
								<p class="bold marBottom_5"><bbbl:label key="lbl_no_results_popular_keywords" language ="${pageContext.request.locale.language}"/></p>
								<ul class="searchWords">
									<bbbt:textArea key="txt_no_results_popular_keywords" language ="${pageContext.request.locale.language}"/>
								</ul>
							</div>
							<div class="grid_6 omega clearfix">
								<c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
								<c:set var="findRegistryFormCount" value="2" scope="request"/>
								<c:choose>
	                                <c:when test="${appid != 'BuyBuyBaby'}">
	                                <c:choose>
	                                	<c:when test="${enableRegSearchById == 'true'}">
										 <div class="findARegistryForm enableRegSearchById clearfix">
										 </c:when>
										 <c:otherwise>
	                                    <div class="findARegistryForm clearfix">
										 </c:otherwise>
										 </c:choose>
	                                        <div class="findARegistryFormTitle">
	                                           <bbbt:textArea key="txt_header_find_GR_no_Search_results_notbaby" language ="${pageContext.request.locale.language}"/>
	                                           <p class="regFixedContent"><bbbl:label key="lbl_enter_reg_info_search" language ="${pageContext.request.locale.language}"/></p>
	                                        </div>
	                                        <div class="findARegistryFormForm">
	                                            <dsp:include page="/addgiftregistry/find_registry_widget.jsp">
													<dsp:param name="findRegistryFormId" value="frmRegInfo" />
													<dsp:param name="submitText" value="${findButton}" />
													<dsp:param name="handlerMethod" value="registrySearchFromNoSearchResultsPage" />
													<%-- R2.2 Story - SEO Friendly URL changes --%>
													<dsp:param name="errorURL" value="s/${searchTerm}" />
													<dsp:param name="bridalException" value="false" />
													<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
												</dsp:include>
	                                            <div class="clear"></div>
	                                        </div>
	                                    </div>
	                                </c:when>
	                                <c:otherwise>
	                                    <c:choose>
	                                	<c:when test="${enableRegSearchById == 'true'}">
										 <div class="findARegistryForm enableRegSearchById clearfix">
										 </c:when>
										 <c:otherwise>
	                                    <div class="findARegistryForm clearfix">
										 </c:otherwise>
										 </c:choose>
	                                        <div class="findARegistryFormTitle">
	                                            <bbbt:textArea key="txt_header_find_GR_no_Search_results_baby" language ="${pageContext.request.locale.language}"/>
	                                            <div class="grid_3">
	                                                <p class="regFixedContent"><bbbl:label key="lbl_bb_fixedContent_reg_search" language ="${pageContext.request.locale.language}"/></p>
	                                            </div>
	                                            <div class="clear"></div>
	                                        </div>
	                                        <div class="findARegistryFormForm">
	                                            
	                                            <dsp:include page="/addgiftregistry/find_registry_widget.jsp">
													<dsp:param name="findRegistryFormId" value="frmRegInfo" />
													<dsp:param name="submitText" value="${findButton}" />
													<dsp:param name="handlerMethod" value="registrySearchFromNoSearchResultsPage" />
													<%-- R2.2 Story - SEO Friendly URL changes --%>
													<dsp:param name="errorURL" value="s/${searchTerm}" />
													<dsp:param name="bridalException" value="false" />
													<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
												</dsp:include>
	                                            <div class="clear"></div>
	                                        </div>
	                                    </div>
	                                </c:otherwise>
	                            </c:choose>
							</div>
						</div>
	                    <c:if test="${empty searchTerm}" >
                     		<c:set var="searchTerm" value="${enteredNarrowDown}" ></c:set>
                     	</c:if>
						<%-- START: Added as Part of R2.2 Story - 501-a1 Items will come from Certona --%>
						<c:set var="certona_on"><bbbc:config key="certona_no_search_results_flag" configName="FlagDrivenFunctions" /></c:set> 
                        <c:set var="cert_scheme" scope="request">nosearch_rr</c:set>
                        <c:set var="cert_searchTerm" scope="request"><c:out value="${searchTerm}" escapeXml="true"/></c:set>
                        <c:set var="cert_pageName" scope="request">NoSearchResult Page</c:set>
                        <dsp:getvalueof var="cert_linkStringNonRecproduct" param="linkStringNonRecproduct" />
						<c:set var="cert_term"><c:out value="${searchTerm}" escapeXml="true"/></c:set>
						<c:set var="cert_BazarVoiceOn" scope="request" value="${BazaarVoiceOn}"></c:set>
						
						<%-- AJAX Call to get the response from Certona --%>
                        <div id="certonaRectangularSlot" class="clearfix loadAjaxContent" 
                            data-ajax-url="${contextPath}/search/no_search_result_certona.jsp" 
                            data-ajax-target-divs="#certonaRectangularSlot" 
                            data-ajax-params-count="7" 
                            data-ajax-param1-name="scheme" data-ajax-param1-value="${cert_scheme}" 
                            data-ajax-param2-name="searchTerm" data-ajax-param2-value="${cert_searchTerm}" 
                            data-ajax-param3-name="certonaSwitch" data-ajax-param3-value="${certona_on}" 
                            data-ajax-param4-name="certonaPageName" data-ajax-param4-value="${cert_pageName}" 
                            data-ajax-param5-name="linkStringNonRecproduct" data-ajax-param5-value="${cert_linkStringNonRecproduct}"
                            data-ajax-param6-name="term" data-ajax-param6-value="${cert_term}"
                            data-ajax-param7-name="BazaarVoiceOn" data-ajax-param7-value="${cert_BazarVoiceOn}"
                        role="complementary">
                            <div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                        </div>
                        
<%-- 					END: Added as Part of R2.2 Story - 501-a1 --%>
						
						<div class="clear"></div>
						<div class="productList marTop_20">
							<h3><bbbl:label key="lbl_no_results_promo_header" language ="${pageContext.request.locale.language}"/></h3>
							<div class="catIcons grid_12 clearfix marBottom_20">
								<div class="catIconsList">
									<bbbt:textArea key="txt_promo_no_results" language ="${pageContext.request.locale.language}"/>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			
			</jsp:body>
				<jsp:attribute name="footerContent">
				
           <script>
           var serchTerm = '<c:out value="${searchTerm}"/>';
           var currentCat = '${currentCategory}';
           var colon = currentCat == '' ? '' : ':';
           if(typeof s !=='undefined') {
			
			
			s.channel='Search';
			s.pageName='Search';
			s.prop1='Search';
			s.prop2='Search';
			s.prop3='Search';
			s.prop4='';
			s.prop5='';
			s.prop6=''; 
			s.prop7=serchTerm.toLowerCase() + colon + currentCat;
			s.prop8='zero';
			s.eVar2=serchTerm.toLowerCase();
			s.eVar61="${OmnitureVariable}";
			var s_code=s.t();
			if(s_code)document.write(s_code);		
           }
        </script>
    </jsp:attribute>
		
	</bbb:pageContainer>
</dsp:page>