<%-- 
  Page renders site-specific logo
--%>
<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="event" param="event" />
	<c:if test="${empty currentSiteId}">
		<dsp:getvalueof var="currentSiteId" bean="Site.id" />
	</c:if>
    <c:set var="gridClass" value="grid_12" />
    <c:if test="${currentSiteId == BedBathCanadaSite}">
        <c:set var="logoCountry" value="_ca" />
        <c:set var="gridClass" value="grid_12" />
    </c:if>
    <c:choose>
        <c:when test="${event == 'BA1' }">
            <c:set var="gridClass" value="grid_10" />
        </c:when>
        <c:otherwise>
        </c:otherwise>
    </c:choose>


        <div class="pushDown pushUp">
              <div id="siteLogo">
                            <c:choose>
                                <c:when test="${currentSiteId == BedBathUSSite}">
                                    <c:choose>
                                        <c:when test="${ not empty event && event eq 'BRD'}">
                                            <a href="/" >
                                                <img src="${imagePath}/_assets/global/images/logo/logo_reg_new${logoCountry}.png" alt="logo of the Wedding & Gift Registry at Bed Bath & Beyond"/>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                                    <a href="/">
                                                        <img src="${imagePath}/_assets/global/images/logo/logo_br${logoCountry}.png" alt="logo of the Bed Bath & Beyond"/>
                                                    </a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                 <c:when test="${currentSiteId == BedBathCanadaSite }"> 
                                            <a href="/">
                                                 <img alt="logo of the Bed Bath & Beyond" src="${imagePath}/_assets/global/images/logo/logo_br${logoCountry}.png" />
                                            </a>
                                       
                                            <a href="https://<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" class="logoBabyCA">
                                                <img src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" height="50" width="121" alt="logo of the buy buy baby" />
                                            </a>
                                        
                                    
                                </c:when>
                                <c:otherwise>
                                    <a href="/">
                                        <img src="${imagePath}/_assets/global/images/logo/logo_bbb_bb.png" alt="logo of the buy buy baby" /></a>
                                </c:otherwise>
                            </c:choose>
               </div>
        </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/navigation/gadgets/logo.jsp#2 $$Change: 635969 $ --%>
