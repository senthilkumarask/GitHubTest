<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:getvalueof var="searchTerm" param="enteredSearchTerm"/>
	<dsp:getvalueof var="errortype" value="${param.errortype}" />
	<c:if test="${empty searchTerm}">
		<dsp:getvalueof var="searchTerm" param="keyword"/>
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
		<jsp:attribute name="pageWrapper">searchList searchResults noSearchResults useCertonaJs</jsp:attribute>
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
			<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
				<div class="row">
					<div class="small-12 columns ">
						<h1><bbbl:label key="lbl_header_search_results_page_2" language ="${pageContext.request.locale.language}"/> <span class="subheader"><bbbl:label key="lbl_no_results_header" language ="${pageContext.request.locale.language}"/>
							 &ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></h1>
					</div>
				</div>
				<div class="row">
					<div class="small-12 medium-6 columns">
						
						<c:choose>
						<c:when test ="${errorMsg ne null }"><p><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/><p></c:when>
						<c:when test="${invalidCriteria}"><p><bbbl:label key="lbl_invalid_search_message" language ="${pageContext.request.locale.language}"/></p></c:when>
						<c:otherwise></c:otherwise>
						</c:choose>          
                        
                        <%-- KP COMMENT: HTML and inline styles coming out in the content below, may be a problem --%>
                       <%--  <bbbt:textArea key="txt_content_no_results" language ="${pageContext.request.locale.language}"/> --%>
						<c:set var="pageCopy">
                            <bbbt:textArea key="txt_content_no_results" language ="${pageContext.request.locale.language}"/>
                        </c:set>
                        <c:set var="pageCopyNew" value="${fn:replace(pageCopy, '/store','/tbs')}" />
						${pageCopyNew}
					</div>
					<div class="small-12 medium-6 columns noSearchResult">
							<c:if test="${not empty errortype && errortype eq 'invalidSearchInputForStore'}">
				
									Please enter a valid search term for "store" type selection 
									Example are 
									A valid zip code : 76018
									A valid Store Id : 500
									A Valid Combination of City and State Using comma seperated as : dallas,texas or Dallas,Texas
								
								</c:if>
							<c:if test="${not empty errortype && errortype eq 'invalidAddress'}">
							
								Please enter a valid Combination of City and State Using comma seperated as : "dallas,texas" or "Dallas,Texas" as the search term for "store" type selection
								 
							</c:if>
								<c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
								<c:set var="findRegistryFormCount" value="2" scope="request"/>
								<c:choose>
	                                <c:when test="${appid != 'TBS_BuyBuyBaby' && empty errortype}">
	                                	<%-- KP COMMENT: HTML and inline styles coming out in the content below, may be a problem --%>
	                                    <div class="homePageReg">
	                                    <bbbt:textArea key="txt_header_find_GR_no_Search_results_notbaby" language ="${pageContext.request.locale.language}"/>
	                                           <p class="regFixedContent"><bbbl:label key="lbl_enter_reg_info_search" language ="${pageContext.request.locale.language}"/></p>
	                                        
	                                            <dsp:include page="/addgiftregistry/find_registry_widget.jsp">
													<dsp:param name="findRegistryFormId" value="frmRegInfo" />
													<dsp:param name="submitText" value="${findButton}" />
													<dsp:param name="handlerMethod" value="registrySearchFromNoSearchResultsPage" />
													<%-- R2.2 Story - SEO Friendly URL changes --%>
													<dsp:param name="errorURL" value="s/${searchTerm}" />
													<dsp:param name="bridalException" value="false" />
													<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
												</dsp:include>
	                                	</div>        
	                                </c:when>
	                                <c:otherwise>
	                                	<div class="findARegistryForm">
	                                   		                        <bbbt:textArea key="txt_header_find_GR_no_Search_results_baby" language ="${pageContext.request.locale.language}"/>
	                                            
	                                                <p class="regFixedContent"><bbbl:label key="lbl_bb_fixedContent_reg_search" language ="${pageContext.request.locale.language}"/></p>
	                                            
	                                            <dsp:include page="/addgiftregistry/find_registry_widget.jsp">
													<dsp:param name="findRegistryFormId" value="frmRegInfo" />
													<dsp:param name="submitText" value="${findButton}" />
													<dsp:param name="handlerMethod" value="registrySearchFromNoSearchResultsPage" />
													<%-- R2.2 Story - SEO Friendly URL changes --%>
													<dsp:param name="errorURL" value="s/${searchTerm}" />
													<dsp:param name="bridalException" value="false" />
													<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
												</dsp:include>
	                                     </div>
	                                </c:otherwise>
	                            </c:choose>
	                            
	                  </div>
				</div>
				<c:set var="certona_on"><bbbc:config key="certona_no_search_results_flag" configName="FlagDrivenFunctions" /></c:set> 
				<c:if test="${certona_on eq true}">
						<c:set var="SchemeName" value="nosearch_rr"/>
							<dsp:droplet name="ProdToutDroplet">
								<dsp:param value="lastviewed" name="tabList" />
									<dsp:param param="siteId" name="siteId" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="lastviewedProductsList" param="lastviewedProductsList" />
								<dsp:droplet name="ExitemIdDroplet">
						          <dsp:param value="${lastviewedProductsList}" name="lastviewedProductsList" />
						          <dsp:oparam name="output">
								    <dsp:getvalueof var="productList" param="productList" />
			                               </dsp:oparam>
						        </dsp:droplet>
						        </dsp:oparam>
				  			</dsp:droplet>
				  			<!-- commented the code, there is no certona container -->
							<%-- <dsp:droplet name="CertonaDroplet">
						 		<dsp:param name="scheme" value="${SchemeName}"/>
								 <dsp:param name="userid" value="${userId}"/>
								 <dsp:param name="context" value="${CertonaContext}"/>
								 <dsp:param name="siteId" value="${appid}"/>
								 	 <dsp:param name="exitemid" value="${productList}"/>
								 <dsp:oparam name="output">
										<dsp:getvalueof var="relatedItemsProductsVOsList" param="certonaResponseVO.resonanceMap.${SchemeName}.productsVOsList"/>
										<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
										 <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
								 </dsp:oparam>
								 <dsp:oparam name="error">
								 </dsp:oparam>
								 <dsp:oparam name="empty">
								 </dsp:oparam>
							</dsp:droplet> --%>
		         			<%-- <div id="nosearch_rr" class="categoryProductTabs row">
		         		
		         					<c:if test="${not empty relatedItemsProductsVOsList}">
		         						<jsp:useBean id="customKeys" class="java.util.HashMap" scope="request"/>
		         						<c:set target="${customKeys}" property="searchTerm">${searchTerm}</c:set>
										<div class="arrowSouth"></div><h1><bbbl:textArea key="txt_no_results_cust_also_viewed" language="${pageContext.request.locale.language}" placeHolderMap="${customKeys}" /></h1>
											<c:if test="${not empty relatedItemsProductsVOsList}">
												<div class="categoryProductTabsData small-12 columns no-padding">
												<!--updated for Omniture Story |@psin52 -->
													<dsp:include page="/browse/last_viewed.jsp" >
														<dsp:param name="lastviewedProductsList" value="${relatedItemsProductsVOsList}"/>
														<dsp:param name="desc" value="nullSearchPage_peopleSearch"/>
													</dsp:include>
												</div>
											</c:if>
					  				</c:if>
		         			</div> --%>
		         	</c:if>

				<%-- END: Added as Part of R2.2 Story - 501-a1 --%>
				
				
				<div class="productList row">
					<h1><bbbl:label key="lbl_no_results_promo_header" language ="${pageContext.request.locale.language}"/></h1>
					<div class="catIcons small-12 columns no-padding">
						<div class="catIconsList">									
							<c:set var="promoResults"><bbbt:textArea key="txt_promo_no_results" language ="${pageContext.request.locale.language}"/></c:set>
							<c:set var="promoResultsNew" value="${fn:replace(promoResults, '/store','/tbs')}" />
							${promoResultsNew}
						</div>
					</div>
				</div>
				<dsp:droplet name="Switch">
					<dsp:param name="value" bean="Profile.transient"/>
					<dsp:oparam name="false">
						<dsp:getvalueof var="userId" bean="Profile.id"/>
					</dsp:oparam>
					<dsp:oparam name="true">
						<dsp:getvalueof var="userId" value=""/>
					</dsp:oparam>
				</dsp:droplet>
				<dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />
				<c:set var="term"><c:out value="${searchTerm}" escapeXml="true"/></c:set>
				
				<script type="text/javascript">
					resx.appid = "${appIdCertona}";
					resx.top1 = 100000;
					resx.top2 = 100000;
					resx.links = '${linksCertona}' + '${linkStringNonRecproduct}' + '${productList}';
					resx.customerid = "${userId}";
					resx.Keyword = "${term}";
					resx.pageid = "${pageIdCertona}";
				</script>
			</jsp:body>
				<jsp:attribute name="footerContent">
           <script>
           var serchTerm = '<c:out value="${searchTerm}"/>';
           if(typeof s !=='undefined') {
			s.channel='Search';
			s.pageName='Search';
			s.prop1='Search';
			s.prop2='Search';
			s.prop3='Search';
			s.prop4='';
			s.prop5='';
			s.prop6=''; 
			s.prop7=serchTerm.toLowerCase();
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
