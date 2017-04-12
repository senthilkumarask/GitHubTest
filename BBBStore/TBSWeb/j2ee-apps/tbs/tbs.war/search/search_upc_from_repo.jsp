<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandsDroplet"/>
	<dsp:getvalueof var="searchTerm" value="${Keyword}"/>
	<%-- 504D implementation --%>

            <c:set var="prodCount"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></c:set>
			
			<c:if test="${empty prodCount }">
			<c:set var="prodCount" value="0"/>
			</c:if>
            <dsp:getvalueof var="resultCount" value="${otherCount+videoCount+prodCount+guideCount}"></dsp:getvalueof>
            <dsp:getvalueof var="pagNum" param="pagNum"/>
	<bbb:pageContainer>
		<jsp:attribute name="section">search</jsp:attribute>
		<jsp:attribute name="pageWrapper">searchGrid searchResults useScene7 useCertonaJs ${pageGridClass}</jsp:attribute>
		<jsp:attribute name="titleString">Search Results for ${searchTerm} </jsp:attribute>
		<jsp:attribute name="follow">${follow}</jsp:attribute>
		<jsp:attribute name="index">${index}</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute> 
		<jsp:attribute name="bodyClass">search-grid</jsp:attribute> 
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
            <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
            <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
            <dsp:importbean bean="/atg/multisite/Site"/>
            <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
            <dsp:importbean bean="/atg/userprofiling/Profile" />
			
           <dsp:getvalueof var="linkString" param="linkString"/>
			<script type="text/javascript">
				linksCertona = "${linkString}";
			</script>
			
			<dsp:getvalueof id="applicationId" bean="Site.id" />
		    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>			
			<dsp:getvalueof var="view" param="view"/>           	
            <dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
        	<dsp:getvalueof var="origRequestURI" vartype="java.lang.String" bean="/OriginatingRequest.requestURI" />
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			
		<div class="row">
			<div class="small-12 columns no-padding">
				<c:choose>
					<c:when test="${not empty searchType && searchType eq 'upc'}">
						<br/>
						<h2 class="subheader"><strong><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" /></strong>:
						 			<dsp:valueof param="resultsList"/><br/>
						 			<dsp:getvalueof param="noResultsList" var="noResultsList"/>
						 			<dsp:getvalueof param="invalidSkuUpcCount" var="invalidSkuUpcCount"/>
						</h2>
						<c:if test="${invalidSkuUpcCount gt 0}">
							<span style="color: red">
			 				 	<img width="15" height="10" src="/tbs/resources/img/icons/Warning.jpg"/> No results for <c:out value="${noResultsList}"/>
			 				</span>
			 			</c:if>
					</c:when>
				</c:choose>				
			</div>
		</div>
      <div class="row">
		        <c:choose>
					<c:when test="${not empty searchType && searchType eq 'upc'}">
						<div id="left-nav" class="hide-for-large-up small-medium-right-off-canvas-menu left-nav">
					</c:when>
				</c:choose>
		        
			        <div class="row show-for-medium-down">
			        	<dsp:include page="/search/search_types_from_repo.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="bbbProductListVO" param="bbbProductListVO" />	
							<dsp:param name="Keyword" param="Keyword"/>
						</dsp:include>
					</div>
					
					<div class="row show-for-medium-down">
						<h3><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></h3>
						<ul class="category-list">
							<!--Removed Sorting Options-->
                       </ul>
					</div>
					<!--Removed Facets code-->
		        </div>
		        
		        <c:choose>
					<c:when test="${not empty searchType && searchType eq 'upc'}">
						<div class="small-12 large-12 columns">
					</c:when>
				</c:choose>
		        
		        	<div class="row grid-control show-for-medium-down">
						<div class="small-2 medium-1 large-1 right columns">
							<div class="sortIcons fr row">
								<c:choose>
									<c:when test="${view == 'list'}">
										<div class="small-6 columns">
											<a class="active layout-icon-list" data-view="list" title="List View" id="plpListView"><span> </span></a>
										</div>
										<div class="small-6 columns ">
											<a class="layout-icon-grid" data-view="grid" title="Grid View" id="plpGridView"><span> </span></a>
										</div>
									</c:when>
									<c:otherwise>
										<div class="small-6 columns">
											<a class="layout-icon-list" data-view="list" title="List View" id="plpListView"><span> </span></a>
										</div>
										<div class="small-6 columns ">
											<a class="active layout-icon-grid" data-view="grid" title="Grid View" id="plpGridView"><span> </span></a>
										</div>
									</c:otherwise>
								</c:choose>		
							</div>
						</div>
					</div>
				
					<div class="row">
						
						
	           	 
                               	
	                             <dsp:include page="/_includes/modules/product_list_upc_from_repo-rwd.jsp">
	                                 <dsp:param name="BBBProductListVO" param="bbbProductListVO"/>
									 <dsp:param name="validSkuUpcCount" param="validSkuUpcCount"/>
									 <c:if test="${not empty searchType && searchType eq 'upc'}">
									 	<dsp:param name="searchType" value="${searchType}"/>
									 </c:if>
	                             </dsp:include>
               		 
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
			<script type="text/javascript">
				resx.appid = "${appIdCertona}";
				resx.links = linksCertona;
				resx.customerid = "${userId}";
				resx.Keyword = "${term}";
			</script>
			
		</jsp:body>
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
			
			<c:choose>
				<c:when test="${searchView eq 'list'}">
					s.prop25="List View";
					s.eVar47="List View";
				</c:when>
				<c:when test="${searchView eq 'grid4'}">
					s.prop25="Grid View-4";
					s.eVar47="Grid View-4";
				</c:when>
				<c:otherwise>
					s.prop25="Grid View-3";
					s.eVar47="Grid View-3";
				</c:otherwise>
			</c:choose>
			s.prop1='Search';
			s.prop2='Search';
			s.prop3='Search';
			s.prop4='';
			s.prop5='';
			s.prop6=''; 
			s.prop7=serchTerm.toLowerCase();
			s.prop8='<dsp:valueof value="${resultCount}"/>';
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