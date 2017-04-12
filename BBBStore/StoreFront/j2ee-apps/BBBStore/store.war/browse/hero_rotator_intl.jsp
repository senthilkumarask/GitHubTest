
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="currentSiteId" bean="Site.id" />
<dsp:importbean bean="/com/bbb/cms/droplet/HeroImageIntlDroplet" />
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />
<input type="hidden" class="intlCustomer" name="isInternationalCustomer" value="${isInternationalCustomer}" />
<div id="hero" role="banner" <c:if test="${subHeaderType eq 'showPersistentHeader'}"> class="" </c:if>>
  <dsp:getvalueof var="regType" param="regType" />
     
			<div class="carousel clearfix">
				<div class="carouselBody">
					<div class="carouselContent">
						<div class="heroImageContainer">
						<%--calling HeroImageIntlDroplet to fetch hero images for intl customer story id BPS-1414 start --%>	
						<dsp:droplet name="HeroImageIntlDroplet">
						<dsp:param name="siteId" value="${currentSiteId}" />
							<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}" />
					         <dsp:oparam name="output">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="homePageTemplateVO.heroImages" name="array" />
								<dsp:oparam name="output">
							 	<dsp:getvalueof var="promoBox" param="element.promoBoxContent" />
							 		<%--checking if promoBox is null or not  --%>	
								<c:choose>
									<c:when test="${(not empty promoBox) && (promoBox ne 'null' )  }">
										<dsp:valueof param="element.promoBoxContent" valueishtml="true"/>
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="count" param="count" />
										<c:if test="${count<=5}">
											<dsp:getvalueof var="imgSrc" param="element.imageURL" />
											<dsp:getvalueof var="imageAltText" param="element.imageAltText" />
											<dsp:getvalueof var="imgLink" param="element.imageLink" />
											
											<dsp:getvalueof var="imageMapName" vartype="java.lang.String" param="element.imageMapName"/>
	                                         <dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="element.imageMapContent"/>
	                                         <c:set var="iMapName">#${imageMapName}</c:set>
	                                         <div style="background: url('${imgSrc}') no-repeat center top;">
	                                         <c:choose>
	                                          <c:when test="${(not empty imageMapName) && (imageMapName ne 'null' )  }">	                                                                                    
	                                           <img class="heroImages" style="width: 1280px; margin-right: 0px;" height="405" width="976" src="/_assets/global/images/spacer.gif" title="${imageAltText}" alt="${imageAltText}" usemap="${iMapName}"/>
									          </c:when>
	                                          <c:otherwise>
	                                            <img class="heroImages" style="width: 1280px; margin-right: 0px;" height="405" width="976" src="/_assets/global/images/spacer.gif" title="${imageAltText}" alt="${imageAltText}"/>
									          </c:otherwise>												 
	                                         </c:choose>
										 	  </div>
										</c:if>
									</c:otherwise>
								</c:choose>
					 			</dsp:oparam>
							</dsp:droplet>
							</dsp:oparam>
							</dsp:droplet>
							<%--calling HeroImageIntlDroplet to fetch hero images for intl customer story id BPS-1414 End --%>
						</div>
					</div>
				</div>
				
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="homePageTemplateVO.heroImages" name="array" />
					<dsp:oparam name="output">
		 	 		<dsp:getvalueof var="count" param="count" />
					<c:if test="${count<=5}">
				 	  <dsp:getvalueof var="imageMapName" vartype="java.lang.String" param="element.imageMapName"/>
                      <dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="element.imageMapContent"/>
                       <c:if test="${(not empty imageMapName) && (imageMapName ne 'null' )  }">
   					     <MAP NAME="${imageMapName}">${imageMapContent}</MAP>
                       </c:if>
                   	</c:if>
				 	</dsp:oparam>
				</dsp:droplet>
				<div class="carouselPages">
					<div class="carouselPageLinks clearfix">
						<a title="Page 1" class="selected" href="#">1</a>
						<a title="Page 2" href="#">2</a>
						<a title="Page 3" href="#">3</a>
						<a title="Page 4" href="#">4</a>
					</div>
				</div>
			</div>
		
</div>
</dsp:page>