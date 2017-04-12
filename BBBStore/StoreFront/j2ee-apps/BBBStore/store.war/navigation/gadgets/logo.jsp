<%-- 
  Page renders site-specific logo
--%>
<dsp:page>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="event" param="event" />
	
	<c:set var="gridClass" value="grid_3" />
	<c:if test="${currentSiteId == BedBathCanadaSite}">
    	<c:set var="logoCountry" value="_ca" />
    	<c:set var="gridClass" value="grid_5" />
    </c:if>
	
        <div class="${gridClass} pushDown">
              <div id="siteLogo" itemscope itemtype="http://schema.org/Organization">
                            <c:choose>
                                <c:when test="${currentSiteId == BedBathCanadaSite || currentSiteId == BedBathUSSite}">
                                    <c:choose>
                                        <c:when test="${ not empty event && event eq 'BRD'}">
                                            <a itemprop="url" href="/">
                                                <img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_reg${logoCountry}.png" />
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
												<c:when test="${ not empty event && event ne 'BA1'}">
													<a itemprop="url" href="/">
														<img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_br${logoCountry}.png" />
													</a>
													
													<%-- Baby Canada --%>
			                                        <c:if test="${currentSiteId == BedBathCanadaSite}">
			                                        	<a itemprop="url" href="https://<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="logoBabyCA">
			                                               	<img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" height="50" width="121" alt="Buy Buy Baby" />
			                                            </a>
			                                        </c:if>
			                                        <%-- Baby Canada --%>
												</c:when>
												<c:otherwise>
													<a itemprop="url" href="/">
														<img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" />
													</a>
													
													<%-- Baby Canada --%>
			                                        <c:if test="${currentSiteId == BedBathCanadaSite}">
			                                        	<a itemprop="url" href="https://<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="logoBabyCA">
			                                               	<img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" height="50" width="121" alt="Buy Buy Baby" />
			                                            </a>
			                                        </c:if>
			                                        <%-- Baby Canada --%>
												</c:otherwise>
											</c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <a itemprop="url" href="/">
										<img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_bbb_bb.png" /></a>
                                </c:otherwise>
                            </c:choose>
               </div>
        </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/navigation/gadgets/logo.jsp#2 $$Change: 635969 $ --%>
