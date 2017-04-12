<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandsDroplet" />
<dsp:page>
	<bbb:pageContainer>
	<jsp:attribute name="section">browse</jsp:attribute>
		<jsp:attribute name="pageWrapper">brandListing</jsp:attribute>
		<jsp:body>
		<c:set var="allBrandsCacheTimeout"><bbbc:config key="AllBrandsCacheTimeout" configName="HTMLCacheKeys" /></c:set>
		<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" /> 
			 <dsp:droplet name="/atg/dynamo/droplet/Cache">
		        <dsp:param name="key" value="${currentSiteId}_AllBrands" />
		        <dsp:param name="cacheCheckSeconds" value="${allBrandsCacheTimeout}" />
		        <dsp:oparam name="output">
					<div class="row">
						<h1><bbbl:label key="lbl_brandslisting_brandsheading" language="${pageContext.request.locale.language}" /></h1>					</div>
					<div class="row hide-for-medium-down"
						<c:set var="lbl_brandslisting_subheading">
							<bbbl:label key="lbl_brandslisting_subheading" language="${pageContext.request.locale.language}" />
						</c:set>
						<c:if test="${not empty lbl_brandslisting_subheading}">
							<p>
								${lbl_brandslisting_subheading}
							</p>
						</c:if>
						<dsp:droplet name="BrandsDroplet">
							<dsp:oparam name="output">
							<dsp:getvalueof var="kalphabetBrandListMapey" param="alphabetBrandListMap" />
							<dsp:getvalueof var="featuredBrands" param="featuredBrands" />
								<%-- <dsp:include page="FeaturedBrands.jsp"/> --%>
							
								<dsp:include page="BrandListing.jsp"/>
							</dsp:oparam>
						</dsp:droplet>
					</div>
				</dsp:oparam>
			</dsp:droplet>
		</jsp:body>
	<jsp:attribute name="footerContent">
    <script type="text/javascript">
  		if (typeof s !== 'undefined') {
			s.pageName = 'Shop by Brand';
			s.channel = 'Shop by Brand';
			s.prop1 = 'Main Level Page';
			s.prop2 = 'Main Level Page';
			s.prop3 = 'Main Level Page';
			var s_code = s.t();
				if (s_code)
					document.write(s_code);
			}
	</script>
    </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>