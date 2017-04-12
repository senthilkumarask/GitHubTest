<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandsDroplet"/>
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	<dsp:getvalueof var="searchTerm" value="${Keyword}"/>
	<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
	<dsp:getvalueof var="frmCollegePage" param="fromCollege"/>
	<dsp:getvalueof var="isRedirect" param="isRedirect"/>
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:getvalueof var="metaVendorParam" bean="SessionBean.vendorParam"/>
	
	<%--BBBI-3804: omniture Tagging :start --%>
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
	
	<%-- 504D implementation --%>
	<c:choose>
		<c:when test="${frmBrandPage eq 'true' || frmCollegePage eq 'true'}">
			<c:set var="follow" value="true"/>
			<c:set var="index" value="true"/>
		</c:when>
		<c:when test="${not empty metaVendorParam}">
    		<c:set var="follow" value="false"/>
			<c:set var="index" value="false"/>
    	</c:when>
		<c:otherwise>
			<c:set var="follow" value="false"/>
			<c:set var="index" value="true"/>
		</c:otherwise>
	</c:choose>
	
	<bbb:pageContainer>
		<jsp:attribute name="section">search</jsp:attribute>
		<jsp:attribute name="pageWrapper">searchList searchResults useScene7 useCertonaJs ${pageGridClass}</jsp:attribute>
		<jsp:attribute name="titleString">Search Results for ${searchTerm} </jsp:attribute>
		<jsp:attribute name="follow">${follow}</jsp:attribute>
		<jsp:attribute name="index">${index}</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute>  
		<jsp:body>
			<script type="text/javascript">
				var resx = new Object();
				var linksCertona='';
			</script>
			<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/search_frag.jsp" >
					<dsp:param name="searchTerm" value="${searchTerm}"/>
				</dsp:include>
			</c:if>
		
			<dsp:importbean bean="/com/bbb/search/droplet/ResultListDroplet"/>
			<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
			<dsp:importbean bean="/atg/multisite/Site"/>
			<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
			<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
			 <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
			<dsp:importbean bean="/atg/userprofiling/Profile" />
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			<%--<dsp:getvalueof var="url" vartype="java.lang.String" bean="/OriginatingRequest.requestURI" />--%>
			
			<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
			<dsp:getvalueof var="linkString" param="linkString"/>
			<c:set var="prodCount"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></c:set>
			<c:set var="videoCount"><dsp:valueof param="browseSearchVO.assetMap.Media.count"/></c:set>
			<c:set var="guideCount"><dsp:valueof param="browseSearchVO.assetMap.Guide_TBS.count"/></c:set>
			<c:set var="otherCount"><dsp:valueof param="browseSearchVO.assetMap.Other.count"/></c:set>
			<c:if test="${empty otherCount }">
			<c:set var="otherCount" value="0"/>
			</c:if>
			<c:if test="${empty videoCount }">
			<c:set var="videoCount" value="0"/>
			</c:if>
			<c:if test="${empty prodCount }">
			<c:set var="prodCount" value="0"/>
			</c:if>
			<c:if test="${empty guideCount }">
			<c:set var="guideCount" value="0"/>
			</c:if>
			
			<script type="text/javascript">
				linksCertona = "${linkString}";
			</script>
		    <dsp:getvalueof id="applicationId" bean="Site.id" />
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}" />
			
			<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRight"><bbbl:label key="lbl_promo_key_right" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
			<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
			<dsp:droplet name="ForEach">
         		<dsp:param name="array" param="browseSearchVO.autoSuggest" />
           		<dsp:oparam name="output">
           			<dsp:getvalueof var="spellCorrection" param="element.spellCorrection"/>
           			<dsp:droplet name="IsNull">
         				<dsp:param name="value" value="${spellCorrection}" />
         				<dsp:oparam name="false">
         					<dsp:getvalueof var="searchTerm" value="${spellCorrection}"/>
         					<dsp:getvalueof var="autoCorrected" value="true"/>
         				</dsp:oparam>
         			</dsp:droplet>
         			<dsp:getvalueof var="didYouMeanTerm" param="element.dymSuggestion"/>
         			<dsp:droplet name="IsNull">
         				<dsp:param name="value" value="${didYouMeanTerm}" />
         				<dsp:oparam name="false">
         					<dsp:getvalueof var="dymSuggestion" value="true"/>
         				</dsp:oparam>
         			</dsp:droplet>
					<dsp:getvalueof var="autoPhrase" value="false"/>
					<dsp:getvalueof var="isAutoPhrase" param="element.autoPhrase"/>
					<dsp:getvalueof var="autoPhrase" value="${isAutoPhrase}"/>
           		</dsp:oparam>
           	</dsp:droplet>
			<div id="content" class="subCategory container_12 clearfix" role="main">
				<div class="breadcrumbs grid_12">
					<a href="${contextPath}" title="<bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" />"><bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" /></a>
					<span class="rightCarrot">&gt;</span>
					<span><bbbl:label key="lbl_header_search_results_page_1" language="${pageContext.request.locale.language}" /> <span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></span>
				</div>
				<div class="pageTitle grid_12">
					<h1 class="mainTitle"><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" /></h1>
					<h2 class="subTitle marLeft_5"><bbbl:label key="lbl_header_search_results_page_3" language="${pageContext.request.locale.language}" />&nbsp;${otherCount+videoCount+prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;<span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></h2>
                    <c:if test="${autoPhrase eq 'false'}">
                    <c:if test="${autoCorrected eq 'true' || dymSuggestion eq 'true'}">
                        <h2 class="subTitle block noMar">
                            <c:if test="${autoCorrected eq 'true'}">
                             <c:set var="keyword"><dsp:valueof param="Keyword"/></c:set>
                                <bbbl:label key="lbl_spell_correction_result" language="${pageContext.request.locale.language}" />
                                <span class="bold">&ldquo;<c:out value="${keyword}" escapeXml="false"/>&rdquo;</span>
                                <bbbl:label key="lbl_spell_correction_2" language="${pageContext.request.locale.language}" /> 
                                <span class="bold">&rdquo;<dsp:valueof value="${spellCorrection}"/>&ldquo;</span>&#46;
                            </c:if>
                            <c:if test="${dymSuggestion eq 'true'}">
                                <dsp:droplet name="ForEach">
                                    <dsp:param name="array" param="browseSearchVO.autoSuggest" />
                                    <dsp:oparam name="outputStart"><bbbl:label key="lbl_did_you_mean_1" language="${pageContext.request.locale.language}" />
                                    </dsp:oparam>
                                    <dsp:oparam name="output">
                                        <dsp:getvalueof var="totalSuggestions" param="size"/>
                                        <dsp:getvalueof var="currentCount" param="count"/>
                                        <%-- R2.2 Story - SEO Friendly URL changes --%>
                                        <a href="${contextPath}${urlPrefixForSuggestion}<dsp:valueof param="element.dymSuggestion"/>${searchQueryParamsForSuggestion}" class="bold" title="<dsp:valueof param="element.dymSuggestion"/>"><dsp:valueof param="element.dymSuggestion"/></a>
                                        <c:if test="${totalSuggestions ne currentCount}"><bbbl:label key="lbl_did_you_mean_2" language="${pageContext.request.locale.language}" /></c:if>
                                    </dsp:oparam>
                                    <dsp:oparam name="outputEnd"><bbbl:label key="lbl_did_you_mean_3" language="${pageContext.request.locale.language}" />
                                    </dsp:oparam>
                                </dsp:droplet>
                            </c:if>
                        </h2>
                    </c:if>
                    </c:if>
				</div>

				   
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="browseSearchVO.promoMap" />
                  	<dsp:oparam name="output">
				  		<dsp:getvalueof var="elementList" param="element"/>
                         	<dsp:getvalueof var="key1" param="key"/>
                         	 <c:if test="${key1 eq promoKeyTop &&  not empty elementList}">
                           		<div class="grid_12 clearfix promo-12col">
	                           		<dsp:droplet name="ForEach">
	                                	<dsp:param name="array" value="${elementList}" />
	                                    <dsp:oparam name="output">
			                            	<dsp:getvalueof var="imageHREF" param="element.imageHref"/>
			                            	<c:if test="${not empty imageHREF}">
								                    <dsp:droplet name="AddContextPathDroplet">
				                            			<dsp:param name="InputLink" value="${imageHREF}" />
				                            			<dsp:oparam name="output">
				                            				<dsp:getvalueof var="imageHREF" param="OutputLink"/>
				                            			</dsp:oparam>
				                            		</dsp:droplet>
				                            </c:if>
						                    <dsp:getvalueof var="imageURL" param="element.imageSrc"/>
						                    <c:choose>
						                    	<c:when test="${not empty imageHREF}">
						                    		<a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>">
	                                          			<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
	                                          		</a>
						                    	</c:when>
						          	          	<c:otherwise>
	                                          			<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
						            	        </c:otherwise>
						                    </c:choose>
	                                  	</dsp:oparam>
	                            	</dsp:droplet>
	                            </div>
                         	</c:if>
								<c:if test="${key1 eq promoKeyCenter &&  not empty elementList}">
						  			<div class="grid_12 clearfix promo-12col1 relatedSearch">                                                   	
	                      	     		<dsp:droplet name="ForEach">
	                                 		<dsp:param name="array" value="${elementList}" />
												<dsp:oparam name="output">	
													<dsp:getvalueof  var="SeperatedValue" param="element.RelatedSeperated" />
														<span class="bold marRight_10">${promoKeyRelated} :</span>
														    <dsp:droplet name="ForEach">
																<dsp:param name="array" value="${SeperatedValue}" />
																	<c:set var="currentCount">
																		<dsp:valueof param="count"/></c:set>
																<dsp:oparam name="output">
																				<dsp:getvalueof  var="keyWords" param="element" />
																					<c:set var="urlPrefixForSuggestion1" value="${contextPath}/s/" scope="request" />
																						<a href="${urlPrefixForSuggestion1}${keyWords}" title="">${keyWords}<c:if test="${fn:length(SeperatedValue) ne currentCount}">,</c:if></a>
																							
																</dsp:oparam>
															</dsp:droplet>
											    </dsp:oparam>
	                                	</dsp:droplet>
                                	</div>
                            	</c:if>
                   </dsp:oparam>
 				</dsp:droplet>
 				
                 <c:set var="pageSize" value="1-${size}"/>
				<div class="<c:out value="${facetClass}"/>">
					<dsp:include page="/_includes/modules/faceted_bar.jsp">
						<dsp:param name="browseSearchVO" param="browseSearchVO" />
						<dsp:param name="url" value="${url}" />
						<dsp:param name="showDepartment" value="true" />
					</dsp:include>
				</div>
				<div id="prodGridContainer" class="<c:out value="${gridClass}"/>">

					<div id="pagTop" class="<c:out value="${gridClass}"/> alpha omega">
						<dsp:include page="/_includes/modules/pagination_top_2bar.jsp">
								<dsp:param name="searchAssetType" value="${searchAssetType}"/>
								<dsp:param name="browseSearchVO" param="browseSearchVO" />	
								<dsp:param name="Keyword" param="Keyword"/>
								<dsp:getvalueof var="view" param="view" />
								<dsp:param name="view" value="${view}"/>
						</dsp:include>
					</div>

					<div class="grid_8 alpha">
						<dsp:droplet name="ResultListDroplet">
							<dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
							<dsp:oparam name="output">
								<dsp:include page="/_includes/modules/product_list_1x10.jsp">
									<dsp:param name="browseSearchVO" param="browseSearchVO" />
									<dsp:param name="BBBProductListVO" param="BBBProductListVO"/>
								</dsp:include>
							</dsp:oparam>
						</dsp:droplet>
					</div>
					
					<c:if test="${plpGridSize == '5'}">
					<div class="<c:out value="${facetClass}"/> omega">
                       	<dsp:droplet name="ForEach">
                     		<dsp:param name="array" param="browseSearchVO.promoMap" />
                        	<dsp:oparam name="output">
                        		<dsp:getvalueof var="elementList" param="element"/>
                            	<dsp:getvalueof var="key2" param="key"/>
	                            <c:if test="${key2 eq promoKeyRight}">
	                            	<dsp:droplet name="ForEach">
	                                   <dsp:param name="array" value="${elementList}" />
	                                    <dsp:oparam name="output">
	                                    	<dsp:getvalueof var="imageHREF" param="element.imageHref"/>
	                                    	<c:if test="${not empty imageHREF}">
								                    <dsp:droplet name="AddContextPathDroplet">
				                            			<dsp:param name="InputLink" value="${imageHREF}" />
				                            			<dsp:oparam name="output">
				                            				<dsp:getvalueof var="imageHREF" param="OutputLink"/>
				                            			</dsp:oparam>
				                            		</dsp:droplet>
				                            </c:if>
						                    <dsp:getvalueof var="imageURL" param="element.imageSrc"/>
											<div class="promo-2col">
											<c:choose>
												<c:when test="${not empty imageHREF}">
			                                	<a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>"><img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" /></a>	
												</c:when>
												<c:otherwise>
			                                		<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />	
												</c:otherwise>
											</c:choose>
                                            </div>
	                                    </dsp:oparam>
	                             	</dsp:droplet>
	                          	</c:if>
                    		</dsp:oparam>
                    	</dsp:droplet>
					</div>
					</c:if>
					<div id="pagBot" class="<c:out value="${gridClass}"/> alpha omega">
						<c:import url="/_includes/modules/pagination_bottom_full.jsp" />
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
			<c:set var="term"><c:out value="${searchTerm}" escapeXml="true"/></c:set>
			<c:set var="cert_scheme" scope="request">search_rr</c:set>
<%-- 			<dsp:droplet name="CertonaDroplet">
			      <dsp:param name="scheme" value="${cert_scheme}"/>
			      <dsp:param name="userid" value="${userId}"/>
			      <dsp:param name="siteId" value="${applicationId}"/>
			      <dsp:oparam name="output">
			          <dsp:getvalueof var="certona_clearenceProductsList" param="certonaResponseVO.resonanceMap.${cert_scheme}.productsVOsList"/>
			          <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
			          <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
			      </dsp:oparam>
			      <dsp:oparam name="error">
			          <c:set var="displayFlag" value="false"/>
			      </dsp:oparam>
			      <dsp:oparam name="empty">
			          <c:set var="displayFlag" value="false"/>
			      </dsp:oparam>
			  </dsp:droplet>  --%>
			<script type="text/javascript">
				resx.appid = "${appIdCertona}";
				resx.top1 = 100000;
				resx.top2 = 100000;
				resx.links = linksCertona;
				resx.customerid = "${userId}";
				resx.pageid = "${pageIdCertona}";
				resx.Keyword = "${term}";
			</script>		
		</jsp:body>

        <dsp:getvalueof var="pagNum" param="pagNum"/>
		<jsp:attribute name="footerContent">
			<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
           <script type="text/javascript">
           var pagNum='${pagNum}';
           var serchTerm = '<dsp:valueof value="${searchTerm}"/>';
			s.pageName='Search';
			s.channel='Search';
           if(typeof s !=='undefined') {
        	<c:if test="${frmBrandPage eq 'true'}">
				s.channel='Brand Search';
			</c:if>  

			s.prop1='Search';
			s.prop2='Search';
			s.prop3='Search';
			s.prop4='';
			s.prop5='';
			s.prop6=''; 
			s.prop7=serchTerm.toLowerCase();
			s.prop8='';
			s.prop25="List View";
			s.eVar47="List View";
			s.eVar61="${OmnitureVariable}";
			if(pagNum=='' || pagNum==1){
			s.events='event1';
           }
			s.eVar2=serchTerm.toLowerCase();
			var s_code=s.t();
			if(s_code)document.write(s_code);		
           }
        </script>
    </jsp:attribute>
		
		
	</bbb:pageContainer>
</dsp:page>