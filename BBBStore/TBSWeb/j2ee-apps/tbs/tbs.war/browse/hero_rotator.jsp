<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:page>
<div id="hero" role="banner" <c:if test="${subHeaderType eq 'showPersistentHeader'}"> class="persistentHeaderFix" </c:if>>
  <dsp:getvalueof var="regType" param="regType" />
     <%--  <c:choose>
	  <c:when test="${regType eq 'COL' }">
		<div id="cmsCarousel" class="grid_9">
		       <bbbt:textArea key="txt_${regType}_registry_landing" language ="${pageContext.request.locale.language}"/>
		        <bbbt:textArea key="txt_${regType}_registry_FPO_landing" language ="${pageContext.request.locale.language}"/>
		</div>
	  </c:when>
	  <c:otherwise> --%> 
			 <div class="carousel clearfix">
				<%-- <div class=" carouselBody">
					<div class="carouselContent">
						<div class="heroImageContainer">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="landingTemplateVO.heroImages" name="array" />
								<dsp:oparam name="output">
							 	<dsp:getvalueof var="promoBox" param="element.promoBoxContent" />
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
						</div>
					</div>
				</div> --%>
				
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="landingTemplateVO.heroImages" name="array" />
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
				<%--<div class="carouselPages">
					<div class="carouselPageLinks clearfix">
						<a title="Page 1" class="selected" href="#">1</a>
						<a title="Page 2" href="#">2</a>
						<a title="Page 3" href="#">3</a>
						<a title="Page 4" href="#">4</a>
					</div>
				</div>--%>
			</div> 
		<%-- </c:otherwise>
		</c:choose> --%>
</div>
</dsp:page>