<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  	
		<div class="row">
			<p><bbbl:label key="lbl_brandslisting_guidingtext" language="${pageContext.request.locale.language}" /></p>
			<ul class="inline-list brand-list-nav">
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param param="alphabetBrandListMap" name="array" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="key" param="key" />
					<dsp:getvalueof var="count" param="count" />
					<dsp:getvalueof var="size" param="size" />
					<li><a href="#brandChar${key}" class="last smoothScrollTo" data-smoothscroll-topoffset="55"><dsp:valueof param="key" /></a></li>
					<%-- <c:choose>
						<c:when test="${count eq 1 }">
						<li><a href="#brandChar${key}" class="first smoothScrollTo" data-smoothscroll-topoffset="55"><dsp:valueof param="key" /></a><span>|</span></li>
						</c:when>			
						<c:when test="${count eq (size) }">
						<li><a href="#brandChar${key}" class="last smoothScrollTo" data-smoothscroll-topoffset="55"><dsp:valueof param="key" /></a></li>
						</c:when>
						<c:otherwise>
						<li><a href="#brandChar${key}" class="smoothScrollTo" data-smoothscroll-topoffset="55"><dsp:valueof param="key" /></a><span>|</span>
						</c:otherwise>
					</c:choose> --%>
	
				</dsp:oparam>
			</dsp:droplet>
			</ul>
		</div>	
	
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param param="alphabetBrandListMap" name="array" />
			<dsp:oparam name="output">
				<div class="row">
					<dsp:getvalueof var="key" param="key" />
					<h2 name="brandChar${key}" id="brandChar${key}"><dsp:valueof param="key" /></h2>
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="element" name="array" />
						<dsp:param value="+brandName" name="sortProperties" />
						<dsp:oparam name="outputStart">
							<dsp:getvalueof var="count" param="count" />
							<%-- a total of 5 columns need to be shown and all the brand names in the list should be distributed evenly in the columns --%>
							<c:set var="brandNamesPerColumn" value="${count/5}" />
							<c:set var="linkCounter" value="${0}" />
							<ul class="small-block-grid-2 medium-block-grid-3 large-block-grid-6 brands-group-grid">
						</dsp:oparam>
						<dsp:oparam name="outputEnd">
							</ul>
						</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof var="count" param="count" />
							<c:if test="${linkCounter == 0}">
								<li>
								<ul class="brands-group-list">
								<%-- <c:choose>
									<c:when test="${count eq 1}">
										<ul class="grid_2 alpha">
									</c:when>
									<c:otherwise>
										<ul class="grid_2">
									</c:otherwise>
								</c:choose> --%>
							</c:if>
							<dsp:getvalueof var="brand" param="element.brandName" />
							<%-- R2.2 Story - SEO Friendly URL changes --%>
							<dsp:getvalueof var="seoURL" param="element.seoURL" />
							<c:url var="url" value="${seoURL}" />
							<c:set var="brandName"><dsp:valueof param="element.brandName" valueishtml="true"/></c:set>
							<li><dsp:a href="${url}" title="${brandName}"><dsp:valueof param="element.brandName" valueishtml="true"/></dsp:a></li>
							<c:set var="linkCounter" value="${linkCounter + 1}" />
							<c:if test="${linkCounter > brandNamesPerColumn}">
								</ul>
								</li>
								<c:set var="linkCounter" value="${0}" />
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</dsp:oparam>
		</dsp:droplet>
</dsp:page>