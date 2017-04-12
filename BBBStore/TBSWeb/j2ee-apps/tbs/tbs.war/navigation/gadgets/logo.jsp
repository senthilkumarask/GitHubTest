<%-- 
  Page renders site-specific logo
--%>
<dsp:page>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="event" param="event" />
	<c:set var="TBS_BedBathUSSite" scope="request">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite" scope="request">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite" scope="request">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	
	<c:set var="gridClass" value="small-4 columns" />
	<c:if test="${currentSiteId == TBS_BedBathCanadaSite}">
    	<c:set var="logoCountry" value="_ca" />
    	<c:set var="gridClass" value="small-4 columns" />
    </c:if>
        <div class="${gridClass} pushDown">
              <div id="siteLogo">
                            <c:choose>
                                <c:when test="${currentSiteId == TBS_BedBathCanadaSite || currentSiteId == TBS_BedBathUSSite}">
                                    <c:choose>
                                        <c:when test="${ not empty event && event eq 'BRD'}">
                                            <a href="${contextPath}">
                                                <img src="${imagePath}/_assets/global/images/logo/logo_reg${logoCountry}.png" />
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
												<c:when test="${ not empty event && event ne 'BA1'}">
													<a href="${contextPath}">
														<img src="${imagePath}/_assets/global/images/logo/logo_br${logoCountry}.png" />
													</a>
													
													<%-- Baby Canada --%>
			                                        <c:if test="${currentSiteId == TBS_BedBathCanadaSite}">
			                                        	<a href="https://<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="logoBabyCA">
			                                               	<img src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" height="50" width="121" alt="Buy Buy Baby" />
			                                            </a>
			                                        </c:if>
			                                        <%-- Baby Canada --%>
												</c:when>
												<c:otherwise>
													<a href="${contextPath}">
														<img src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" />
													</a>
													
													<%-- Baby Canada --%>
			                                        <c:if test="${currentSiteId == TBS_BedBathCanadaSite}">
			                                        	<a href="https://<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="logoBabyCA">
			                                               	<img src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" height="50" width="121" alt="Buy Buy Baby" />
			                                            </a>
			                                        </c:if>
			                                        <%-- Baby Canada --%>
												</c:otherwise>
											</c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <a href="${contextPath}">
										<img src="${imagePath}/_assets/global/images/logo/logo_bbb_bb.png" /></a>
                                </c:otherwise>
                            </c:choose>
               </div>
        </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/navigation/gadgets/logo.jsp#2 $$Change: 635969 $ --%>
