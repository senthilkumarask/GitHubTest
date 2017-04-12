<%-- <dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/RecommendationLandingPageTemplateDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
	
<dsp:droplet name="RecommendationLandingPageTemplateDroplet">
					
					<dsp:oparam name="output">
					<dsp:getvalueof var="siteId" param="RecommendationLandingPageTemplateVO.siteId" />
					<dsp:getvalueof var="channel" param="RecommendationLandingPageTemplateVO.channel" />
					<dsp:getvalueof var="registryType" param="RecommendationLandingPageTemplateVO.registryType" />
						<dsp:param name="registrantTemplateVO" param="RecommendationLandingPageTemplateVO" />
					<!--	<dsp:getvalueof var="pageTitle" param="LandingTemplateVO.pageTitle" />	-->
<div id="findYourStyle" class="grid_6 alpha">
							     <dsp:include page="promo_list_layout_50.jsp">
							    	<dsp:param name="promoBox" param="RecommendationLandingPageTemplateVO.promoBox"/>
							  	</dsp:include>
							   <MAP NAME="${imageMapName}">${imageMapContent}</MAP>
					  		</div>
							 
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="RecommendationLandingPageTemplateVO.promoBoxList" name="array" />
									<dsp:oparam name="output">
                                        <dsp:getvalueof var="count" param="count" />
                                        <dsp:getvalueof var="size" param="size" />
                                        <c:choose>
                                            <c:when test="${count%2==1}">
                                                <div class="circRow clearfix">
                                                <c:set var="circClass" value="alpha circLeft"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="circClass" value="omega circRight"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${count>0}">
                                            <div class="grid_5 ${circClass}">
                                                <div class="circLink">
                                                    <dsp:getvalueof var="imageURL" param="element.imageURL" />
                                                    <dsp:getvalueof var="imageAlt" param="element.imageAltText" />
                                                    <dsp:getvalueof var="promoId" param="element.id" />
                                                    <dsp:getvalueof var="description" param="element.promoBoxContent" />
                                                      <dsp:getvalueof var="imageMapContent" param="element.imageMapContent" />
                                                    <a href="${contextPath}/page/Circular/${promoId}" class="popupIframe" title="${imageAlt}">
                                                        <img class="circImage" alt="${imageAlt}" title="${imageAlt}" src="${imageURL}" width="290" height="325" />
                                                        <div class="clear"></div>
                                                        <span class="circName">${imageAlt}</span> 
                                                    </a>
                                                </div>
                                            </div>
                                        </c:if>
										<c:if test="${count%2==0 || size == count}">
											</div>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								
								<div id="findYourStyle" class="grid_6 alpha">
							     <dsp:include page="promo_list_layout_50.jsp">
							    	<dsp:param name="promoBoxBottom" param="RecommendationLandingPageTemplateVO.promoBoxBottom"/>
							  	</dsp:include>
							   <MAP NAME="${imageMapName}">${imageMapContent}</MAP>
					  		</div>
							 
</dsp:page>	         
	          --%>