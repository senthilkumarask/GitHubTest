<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  	<div class="grid_11 suffix_1">
		<dl class="brandChars">
			<dt><bbbl:label key="lbl_brandslisting_guidingtext" language="${pageContext.request.locale.language}" /></dt>
			<dd class="clearfix">
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param param="alphabetBrandListMap" name="array" />
			<dsp:oparam name="output">
			<dsp:getvalueof var="key" param="key" />
			<dsp:getvalueof var="count" param="count" />
			<dsp:getvalueof var="size" param="size" />
			<c:choose>
			<c:when test="${count eq 1 }">
			<a href="#brandChar${key}" class="first smoothScrollTo" data-smoothscroll-topoffset="55"><dsp:valueof param="key" /></a><span>|</span>
			</c:when>			
			<c:when test="${count eq (size) }">
			<a href="#brandChar${key}" class="last smoothScrollTo" data-smoothscroll-topoffset="55"><dsp:valueof param="key" /></a>
			</c:when>
			<c:otherwise>
			<a href="#brandChar${key}" class="smoothScrollTo" data-smoothscroll-topoffset="55"><dsp:valueof param="key" /></a><span>|</span>
			</c:otherwise>
			</c:choose>
				<%-- <a href=#brandCharA class="first">A</a><span>|</span> 
				<a href="#brandCharB">B</a> <span>|</span> <a href="#brandCharC">C</a><span>|</span>
				<a href="#brandCharD">D</a> <span>|</span> <a href="#brandCharE">E</a><span>|</span>
				<a href="#brandCharF">F</a> <span>|</span> <a href="#brandCharG">G</a><span>|</span>
				<a href="#brandCharH">H</a> <span>|</span> <a href="#brandCharI">I</a><span>|</span>
				<a href="#brandCharJ">J</a> <span>|</span> <a href="#brandCharK">K</a><span>|</span>
				<a href="#brandCharL">L</a> <span>|</span> <a href="#brandCharM">M</a><span>|</span>
				<a href="#brandCharN">N</a> <span>|</span> <a href="#brandCharO">O</a><span>|</span>
				<a href="#brandCharP">P</a> <span>|</span> <a href="#brandCharQ">Q</a><span>|</span>
				<a href="#brandCharR">R</a> <span>|</span> <a href="#brandCharS">S</a><span>|</span>
				<a href="#brandCharT">T</a> <span>|</span> <a href="#brandCharU">U</a><span>|</span>
				<a href="#brandCharV">V</a> <span>|</span> <a href="#brandCharW">W</a><span>|</span>
				<a href="#brandCharX">X</a> <span>|</span> <a href="#brandCharY">Y</a><span>|</span>
				<a href="#brandCharZ" class="last">Z</a> --%>
				</dsp:oparam>
				</dsp:droplet>
			</dd>
		</dl>
	
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param param="alphabetBrandListMap" name="array" />
			<dsp:oparam name="output">
				<div class="brandLinks clearfix">
					<dsp:getvalueof var="key" param="key" />
					<a name="brandChar${key}" class="grid_1 brandLinksHeader alpha" href="javascript:void(0)"> <dsp:valueof param="key" /> </a>
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="element" name="array" />
						<dsp:param value="+brandName" name="sortProperties" />
						<dsp:oparam name="outputStart">
							<dsp:getvalueof var="count" param="count" />
							<%-- a total of 5 columns need to be shown and all the brand names in the list should be distributed evenly in the columns --%>
							<c:set var="brandNamesPerColumn" value="${count/5}" />
							<c:set var="linkCounter" value="${0}" />
						</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof var="count" param="count" />
							<c:if test="${linkCounter == 0}">
								<c:choose>
									<c:when test="${count eq 1}">
										<ul class="grid_2 alpha">
									</c:when>
									<c:otherwise>
										<ul class="grid_2">
									</c:otherwise>
								</c:choose>
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
								<c:set var="linkCounter" value="${0}" />
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</dsp:oparam>
		</dsp:droplet>
	</div>
</dsp:page>