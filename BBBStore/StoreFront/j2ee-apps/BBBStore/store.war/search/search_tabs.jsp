<%-- R2.2 SEO Friendly URL changes --%>
<dsp:page>
    
	<dsp:getvalueof id="searchTerm" value="${searchTerm}"/>
	<dsp:getvalueof var="defaultView" param="view"/>
	<c:set var="url" value="${contextPath}${seoUrl}/s/${searchTerm}" scope="request" />
	<%--Start | BPS-1384:Search Within Search - Part II--%>
	  <c:if test="${not empty narrowDown}">		
     <c:set var="url" value="${url}/${narrowDown}"  scope="request"></c:set>
     <dsp:getvalueof var="swsterms" value="${fn:escapeXml(param.swsterms)}"/>
     </c:if>
     <%--End | BPS-1384:Search Within Search - Part II--%>
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
	<dsp:getvalueof var="searchMode" param="browseSearchVO.urlSearchMode"/>
	<c:set var="searchMode" value="&searchMode=${searchMode}"/>
	<dsp:getvalueof var="partialFlag" param="browseSearchVO.partialFlag"/>
	<c:set var="partialFlag" value="&partialFlag=${partialFlag}"/>
	<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
	
	<%-- Retrieving Vendor Parameter for Vendor Story --%>
	<%@ include file="/_includes/modules/getVendorParam.jsp"%>
	
	<div class="searchGroupResults" id="pagGroupResult">
		<c:set var="product"><bbbl:label key="lbl_search_tab_value_product" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="guide"><bbbl:label key="lbl_search_tab_value_guides" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="media"><bbbl:label key="lbl_search_tab_value_videos" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="other"><bbbl:label key="lbl_search_tab_value_others" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="bracOpen"><bbbl:label key="lbl_regsrchguest_bracOpen" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="bracClose"><bbbl:label key="lbl_regsrchguest_bracClose" language="${pageContext.request.locale.language}" /></c:set>
		<c:choose>
			<c:when test="${searchAssetType eq product}">
				<div class="button_pill button_pill_active">
					<a href="#"  class="redirPage" ><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> 
						<div>
							<span class="prodCount search_tab"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></span>
							<span class="prodCount cornerCount_prod_test">&nbsp;</span>
						</div>
					</a>
				</div>			
			</c:when>
			<c:when test="${searchAssetType ne product}">
				<%-- If Product is in assetMap, show it is inactive --%>	
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="browseSearchVO.assetMap.Product"/>
					<dsp:oparam name="false">
						<div class="button_pill">
							<c:set var="productLabel"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> </c:set>
							<c:set var="productCount"><dsp:valueof param="browseSearchVO.assetMap.Product.count"/></c:set>
							<a data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"  class="redirPage dynFormSubmit" href="${url}/1-${size}?<dsp:valueof param="browseSearchVO.assetMap.Product.query"/>${searchQueryParams}${partialFlag}${searchMode}${vendorParam}" onclick="javascript:searchTabTrack('${productLabel}','${productCount}')">
								<bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> 
								<div>
								    <span class="prodCount search_tab"><dsp:valueof param="browseSearchVO.assetMap.Product.count"/></span>
									<span class="prodCount cornerCount_prod_test">&nbsp;</span>
								</div>
							</a>
						</div>									
					</dsp:oparam>	
					<dsp:oparam name="true">
						<div class="button_pill">
							<c:set var="productLabel"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> </c:set>
							<c:set var="productCount">0</c:set>
							<a href="#"  class="redirPage" onclick="javascript:searchTabTrack('${productLabel}','${productCount}')"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> 
								<span class="prodCount search_tab">0</span>
								<span class="cornerCount_prod_test">&nbsp;</span>
							</a>
						</div>
					</dsp:oparam>							
				</dsp:droplet>
			</c:when>
		 </c:choose>
		 	 <c:choose>
		 	<c:when test="${searchAssetType eq media}">
				<div class="button_pill button_pill_active">
					<a href="#"  class="redirPage"><bbbl:label key="lbl_search_tab_text_videos" language="${pageContext.request.locale.language}" /> 
						<div><span class="prodCount search_tab"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></span>
						<span class="prodCount cornerCount_prod_test"></span></div>
					</a>
				</div>			
			</c:when>
			<c:when test="${searchAssetType ne media}">
				<%-- If Media is in assetMap, show it is inactive --%>
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="browseSearchVO.assetMap.Media"/>
					<dsp:oparam name="false">
						<div class="button_pill">
							<c:set var="mediaLabel"><bbbl:label key="lbl_search_tab_text_videos" language="${pageContext.request.locale.language}" /> </c:set>
                            <c:set var="mediaCount"><dsp:valueof param="browseSearchVO.assetMap.Media.count"/></c:set>
                            <dsp:getvalueof var="assetFilter" param="browseSearchVO.assetMap.Media.assetFilter"/>
                            <a data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"  class="redirPage dynFormSubmit" href="${url}/${assetFilter}/1-${size}?<dsp:valueof param="browseSearchVO.assetMap.Media.query"/>${searchQueryParams}&media=true&pagFilterOpt=${mediaCount}${partialFlag}${searchMode}${vendorParam}" onclick="javascript:searchTabTrack('${mediaLabel}','${mediaCount}')"><bbbl:label key="lbl_search_tab_text_videos" language="${pageContext.request.locale.language}" /> 
                                 <div> <span class="prodCount search_tab">${mediaCount}</span>
                                 <span class="prodCount cornerCount_prod_test"></span></div>
                            </a>
                        </div>									
					</dsp:oparam>				
				</dsp:droplet>		
			</c:when>
  		 </c:choose>
		 <c:choose>
			<c:when test="${searchAssetType eq guide}">
				<div class="button_pill button_pill_active">
					<a href="#"  class="redirPage"><bbbl:label key="lbl_search_tab_text_guides" language="${pageContext.request.locale.language}" /> 
						<div><span class="prodCount search_tab"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></span>
						<span class="prodCount cornerCount_prod_test"></span></div>
					</a>
				</div>			
			</c:when>
			<c:when test="${searchAssetType ne guide}">
				<%-- If Guides is in assetMap, show it is inactive --%>		
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="browseSearchVO.assetMap.Guide"/>
					<dsp:oparam name="false">
						<div class="button_pill">
							<c:set var="guideLabel"><bbbl:label key="lbl_search_tab_text_guides" language="${pageContext.request.locale.language}" /> </c:set>
							<c:set var="guideCount"><dsp:valueof param="browseSearchVO.assetMap.Guide.count"/></c:set>
							<dsp:getvalueof var="assetFilter" param="browseSearchVO.assetMap.Guide.assetFilter"/>
							<a data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" class="redirPage dynFormSubmit" href="${url}/${assetFilter}/1-${size}?<dsp:valueof param="browseSearchVO.assetMap.Guide.query"/>${searchQueryParams}${partialFlag}${searchMode}${vendorParam}" onclick="javascript:searchTabTrack('${guideLabel}','${guideCount}')"><bbbl:label key="lbl_search_tab_text_guides" language="${pageContext.request.locale.language}" /> 
								<div><span class="prodCount search_tab"><dsp:valueof param="browseSearchVO.assetMap.Guide.count"/></span>
								<span class="prodCount cornerCount_prod_test"></span></div>
							</a>
						</div>									
					</dsp:oparam>				
				</dsp:droplet>			
			</c:when>				 		
		</c:choose>
		<%-- <c:choose>
			<c:when test="${searchAssetType eq other}">
				<div class="button_pill button_pill_active">
					<a href="#" rel="nofollow" class="redirPage"><bbbl:label key="lbl_search_tab_text_others" language="${pageContext.request.locale.language}" /> 
					<div> <span class="prodCount prodCount search_tab"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></span>
					<span class="prodCount cornerCount_prod_test"></span></div>
					</a>
				</div>			
			</c:when>
			<c:when test="${searchAssetType ne other}">
				If Other is in assetMap, show it is inactive		
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="browseSearchVO.assetMap.Other"/>
					<dsp:oparam name="false">
						<div class="button_pill">
							<c:set var="othersLabel"><bbbl:label key="lbl_search_tab_text_others" language="${pageContext.request.locale.language}" /> </c:set>
                            <c:set var="othersCount"><dsp:valueof param="browseSearchVO.assetMap.Other.count"/></c:set>
                            <dsp:getvalueof var="assetFilter" param="browseSearchVO.assetMap.Other.assetFilter"/>
							<a data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" rel="nofollow" class="redirPage dynFormSubmit" href="${url}/${assetFilter}/1-${size}?<dsp:valueof param="browseSearchVO.assetMap.Other.query"/>${searchQueryParams}${partialFlag}${searchMode}" onclick="javascript:searchTabTrack('${othersLabel}','${othersCount}')"><bbbl:label key="lbl_search_tab_text_others" language="${pageContext.request.locale.language}" /> 
								<div><span class="prodCount search_tab"><dsp:valueof param="browseSearchVO.assetMap.Other.count"/></span>
								<span class="prodCount cornerCount_prod_test"></span></div>
							</a>
						</div>
					</dsp:oparam>				
				</dsp:droplet>	
			</c:when>
		 </c:choose>	 --%>			 		
	</div>
</dsp:page>