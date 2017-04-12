<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="isLoggedIn" bean="/atg/userprofiling/Profile.transient"/>
<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
<c:set var="gridClass" value="grid_3" />
<c:if test="${not empty param.gridClass}">
    <c:set var="gridClass" value="${param.gridClass}" />
</c:if>
<div class="leftNav ${gridClass}">
 
<dsp:getvalueof param="pageName" var="pageName"/>
 	<h3 class="noMarTop"><bbbl:label key="lbl_leftnavguide_abtregistry" language ="${pageContext.request.locale.language}"/></h3>
	<div class="clear"></div>
	<ul class="clearfix noMar noPadBot" role="menu">
	<li role="menuitem">
	<c:choose>
                <c:when test="${pageName == 'RegistryFeatures'}">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_regfeatures" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                  <c:set var="lbl_leftnavguide_regfeatures">
					<bbbl:label key="lbl_leftnavguide_regfeatures" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/RegistryFeatures" title="${lbl_leftnavguide_regfeatures}" >
				    <bbbl:label key="lbl_leftnavguide_regfeatures" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
    </c:choose>
	</li>
	
	<dsp:getvalueof bean="SessionBean.babyCA" var="babyCA"/>
	<c:if test="${((currentSiteId eq BedBathCanadaSite) or (babyCA == 'true'))}">
	<li role="menuitem">
	
		    <c:choose>
                <c:when test="${pageName == 'RegistryFAQs'}">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_regfaq" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                 <c:set var="lbl_leftnavguide_regfaq">
					<bbbl:label key="lbl_leftnavguide_regfaq" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/RegistryFAQs" title="${lbl_leftnavguide_regfaq}">
				    <bbbl:label key="lbl_leftnavguide_regfaq" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
            </c:choose>
       
	</li>
	</c:if>
	
	<li role="menuitem">
	<c:set var="pageNameIncentives">RegistryIncentives</c:set>
	<c:if test="${currentSiteId eq BuyBuyBabySite}"><c:set var="pageNameIncentives">FreeGoodyBag</c:set></c:if>
	
			 <c:choose>
                <c:when test="${pageName == pageNameIncentives}">          
                    <span class="active"><bbbl:label key="lbl_leftnavguide_regincentive" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                 <c:set var="lbl_leftnavguide_regincentive">
					<bbbl:label key="lbl_leftnavguide_regincentive" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/${pageNameIncentives}" title="${lbl_leftnavguide_regincentive}">
				    <bbbl:label key="lbl_leftnavguide_regincentive" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
            </c:choose>
     
	</li>
		<c:if test="${((currentSiteId eq BedBathCanadaSite) or (babyCA == 'true'))}">
	<li role="menuitem">
	<c:set var="pageNameIncentives">FreeGoodyBag</c:set>
		
			 <c:choose>
                <c:when test="${pageName == pageNameIncentives}">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_babyreggoodybag" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                 <c:set var="lbl_leftnavguide_babyreggoodybag">
					<bbbl:label key="lbl_leftnavguide_babyreggoodybag" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/${pageNameIncentives}" title="${lbl_leftnavguide_babyreggoodybag}">
				    <bbbl:label key="lbl_leftnavguide_babyreggoodybag" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
            </c:choose>
     
	</li>
	</c:if>
	
	</ul>
	<h3 class="marTop_20"><bbbl:label key="lbl_leftnavguide_regtools" language ="${pageContext.request.locale.language}"/></h3>
	<div class="clear"></div>
	<ul class="clearfix noPadBot noMarTop" role="menu">


<c:if test="${((currentSiteId eq BedBathCanadaSite) or (babyCA == 'true'))}">

		<header>
		<b><bbbl:label key="lbl_leftnavwedding_registry" language ="${pageContext.request.locale.language}"/></b>
		</header>
		
		<li role="menuitem">
		           <c:choose>
		                <c:when test="${pageName eq 'RegistryChecklist' }">
		                    <span class="active"><bbbl:label key="lbl_leftnavguide_regchklist" language ="${pageContext.request.locale.language}"/></span>
		                </c:when>
		                <c:otherwise>
		                <c:set var="lbl_leftnavguide_regchklist">
							<bbbl:label key="lbl_leftnavguide_regchklist" language="${pageContext.request.locale.language}" />
						</c:set>
		                    <dsp:a href="${contextPath}/registry/RegistryChecklist"  title="${lbl_leftnavguide_regchklist}">
						    <bbbl:label key="lbl_leftnavguide_regchklist" language ="${pageContext.request.locale.language}"/>
					        </dsp:a>
		                </c:otherwise>
		            </c:choose>
		
		</li>

		<li role="menuitem">
			
					 <c:choose>
		                <c:when test="${pageName == 'GuidesAndAdviceLandingPage'}">
		                    <span class="active"><bbbl:label key="lbl_guides_title" language ="${pageContext.request.locale.language}"/></span>
		                </c:when>
		                <c:otherwise>
		                 <c:set var="lbl_guides_title">
							<bbbl:label key="lbl_guides_title" language="${pageContext.request.locale.language}" />
						</c:set>
		                    <dsp:a href="${contextPath}/registry/GuidesAndAdviceLandingPage"  title="${lbl_guides_title}">
						    <bbbl:label key="lbl_guides_title" language ="${pageContext.request.locale.language}"/>
					        </dsp:a>
		                </c:otherwise>
		            </c:choose>
				
		</li>

       <c:if test="${BridalBookOn}">
            <li role="menuitem">
            <c:choose>
                <c:when test="${pageName == 'BridalBook'}">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_bridalbook" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                <c:set var="lbl_leftnavguide_bridalbook">
					<bbbl:label key="lbl_leftnavguide_bridalbook" language="${pageContext.request.locale.language}" />
				</c:set>
                    <a href="${contextPath}/bbregistry/BridalBook" title="${lbl_leftnavguide_bridalbook}"><bbbl:label key="lbl_leftnavguide_bridalbook" language ="${pageContext.request.locale.language}"/></a>
                </c:otherwise>
            </c:choose>
            </li>
        </c:if>
         	<li role="menuitem">
    <c:choose>
        <c:when test="${pageName == 'RegistryAnnouncement'}">
            <span class="active"><bbbl:label key="lbl_leftnavguide_registryannouncement" language ="${pageContext.request.locale.language}"/></span>
        </c:when>
        <c:otherwise>
         <c:set var="lbl_leftnavguide_registryannouncement">
					<bbbl:label key="lbl_leftnavguide_registryannouncement" language="${pageContext.request.locale.language}" />
				</c:set>
				<c:set var="printAtHomeLink">
					<bbbl:label key="bbb_printathome_url" language="${pageContext.request.locale.language}" />
				</c:set>
            	<a href="${contextPath}/${printAtHomeLink}" title="${lbl_leftnavguide_registryannouncement}"><bbbl:label key="lbl_leftnavguide_registryannouncement" language ="${pageContext.request.locale.language}"/></a>
            </c:otherwise>
            </c:choose>	
    </li>
		<br>
        <header>
		<b><bbbl:label key="lbl_leftnavbaby_registry" language ="${pageContext.request.locale.language}"/></b>		
		</header>
		
		<li role="menuitem">
           <c:choose>
                <c:when test="${pageName eq 'BabyRegistryChecklist' }">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_babyregchklist" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                <c:set var="lbl_leftnavguide_babyregchklist">
					<bbbl:label key="lbl_leftnavguide_babyregchklist" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/BabyRegistryChecklist"  title="${lbl_leftnavguide_babyregchklist}">
				    <bbbl:label key="lbl_leftnavguide_babyregchklist" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
            </c:choose>

	  </li>
	  
	  	<li role="menuitem">
           <c:choose>
                <c:when test="${pageName eq 'BabyGuidesAndAdviceLandingPage' }">
                    <span class="active"><bbbl:label key="lbl_babyguides_title" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                <c:set var="lbl_babyguides_title">
					<bbbl:label key="lbl_babyguides_title" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/BabyGuidesAndAdviceLandingPage"  title="${lbl_babyguides_title}">
				    <bbbl:label key="lbl_babyguides_title" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
            </c:choose>

	  </li>
	  
	   <li role="menuitem">
		 <c:choose>
             <c:when test="${pageName == 'BabyBook'}">
                 <span class="active"><bbbl:label key="lbl_leftnavguide_babybook" language ="${pageContext.request.locale.language}"/></span>
             </c:when>
             <c:otherwise>
               <c:set var="lbl_leftnavguide_babybook">
					<bbbl:label key="lbl_leftnavguide_babybook" language="${pageContext.request.locale.language}" />
				</c:set>
                 <a href="${contextPath}/registry/BrowseTheBabyBook" title="${lbl_leftnavguide_babybook}"><bbbl:label key="lbl_leftnavguide_babybook" language ="${pageContext.request.locale.language}"/></a>
             </c:otherwise>
         </c:choose>
       </li>
     	<li role="menuitem">
    <c:choose>
        <c:when test="${pageName == 'RegistryAnnouncement'}">
            <span class="active"><bbbl:label key="lbl_leftnavguide_registryannouncement" language ="${pageContext.request.locale.language}"/></span>
        </c:when>
        <c:otherwise>
         <c:set var="lbl_leftnavguide_registryannouncement">
					<bbbl:label key="lbl_leftnavguide_registryannouncement" language="${pageContext.request.locale.language}" />
				</c:set>
				<c:set var="printAtHomeLink">
					<bbbl:label key="bbb_printathome_url" language="${pageContext.request.locale.language}" />
				</c:set>
            	<a href="${contextPath}/${printAtHomeLink}" title="${lbl_leftnavguide_registryannouncement}"><bbbl:label key="lbl_leftnavguide_registryannouncement" language ="${pageContext.request.locale.language}"/></a>
            </c:otherwise>
            </c:choose>	
    </li>	          
</c:if>

    <c:if test="${currentSiteId ne BedBathCanadaSite}">
	<li role="menuitem">
           <c:choose>
                <c:when test="${pageName eq 'RegistryChecklist' }">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_regchklist" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                <c:set var="lbl_leftnavguide_regchklist">
					<bbbl:label key="lbl_leftnavguide_regchklist" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/RegistryChecklist"  title="${lbl_leftnavguide_regchklist}">
				    <bbbl:label key="lbl_leftnavguide_regchklist" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
            </c:choose>

	</li>
	</c:if>
	
	<c:if test="${currentSiteId eq BuyBuyBabySite}">
		<li role="menuitem">
			<c:choose>
	                <c:when test="${pageName == 'adoptionchecklist1'}">
	                    <span class="active"><bbbl:label key="lbl_leftnavguide_adoptionchecklist1" language ="${pageContext.request.locale.language}"/></span>
	                </c:when>
	                <c:otherwise>
	                  	<c:set var="lbl_leftnavguide_adoptionchecklist1">
							<bbbl:label key="lbl_leftnavguide_adoptionchecklist1" language="${pageContext.request.locale.language}" />
						</c:set>
						<c:set var="adoptionChecklist1URL">
							<bbbc:config key="adoptionChecklist1URL" configName="ThirdPartyURLs" />
						</c:set>
	                    <dsp:a href="${adoptionChecklist1URL}" title="${lbl_leftnavguide_adoptionchecklist1}" >
					    	<bbbl:label key="lbl_leftnavguide_adoptionchecklist1" language ="${pageContext.request.locale.language}"/>
				        </dsp:a>
	                </c:otherwise>
	    	</c:choose>
		</li>
		<li role="menuitem">
			<c:choose>
	                <c:when test="${pageName == 'adoptionchecklist2'}">
	                    <span class="active"><bbbl:label key="lbl_leftnavguide_adoptionchecklist2" language ="${pageContext.request.locale.language}"/></span>
	                </c:when>
	                <c:otherwise>
	                  	<c:set var="lbl_leftnavguide_adoptionchecklist2">
							<bbbl:label key="lbl_leftnavguide_adoptionchecklist2" language="${pageContext.request.locale.language}" />
						</c:set>
						<c:set var="adoptionChecklist2URL">
							<bbbc:config key="adoptionChecklist2URL" configName="ThirdPartyURLs" />
						</c:set>
	                    <dsp:a href="${adoptionChecklist2URL}" title="${lbl_leftnavguide_adoptionchecklist2}" >
					    	<bbbl:label key="lbl_leftnavguide_adoptionchecklist2" language ="${pageContext.request.locale.language}"/>
				        </dsp:a>
	                </c:otherwise>
	    	</c:choose>
		</li>
	     <li role="menuitem">
		 <c:choose>
             <c:when test="${pageName == 'BabyBook'}">
                 <span class="active"><bbbl:label key="lbl_leftnavguide_babybook" language ="${pageContext.request.locale.language}"/></span>
             </c:when>
             <c:otherwise>
               <c:set var="lbl_leftnavguide_babybook">
					<bbbl:label key="lbl_leftnavguide_babybook" language="${pageContext.request.locale.language}" />
				</c:set>
                 <a href="${contextPath}/registry/BrowseTheBabyBook" title="${lbl_leftnavguide_babybook}"><bbbl:label key="lbl_leftnavguide_babybook" language ="${pageContext.request.locale.language}"/></a>
             </c:otherwise>
         </c:choose>
         </li>
        
      </c:if>
      
      
	<c:set var="bbcy" value="${((currentSiteId eq BedBathCanadaSite) or (babyCA == 'true'))}"/>
	<c:if test="${currentSiteId ne BedBathCanadaSite}">
	<li role="menuitem">
	
			 <c:choose>
                <c:when test="${pageName == 'GuidesAndAdviceLandingPage'}">
                    <span class="active"><bbbl:label key="lbl_guides_title" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                 <c:set var="lbl_guides_title">
					<bbbl:label key="lbl_guides_title" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/GuidesAndAdviceLandingPage"  title="${lbl_guides_title}">
				    <bbbl:label key="lbl_guides_title" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
            </c:choose>
		
	</li>
	</c:if>
	
	 <c:if test="${currentSiteId eq BuyBuyBabySite}">
 	<li role="menuitem">
		<c:choose>
                <c:when test="${pageName == 'KickStarter'}">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_kickstarters" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                <c:set var="lbl_leftnavguide_bridaltool">
					<bbbl:label key="lbl_leftnavguide_kickstarters" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/kickstarters/" title="${lbl_leftnavguide_kickstarters}">
				    <bbbl:label key="lbl_leftnavguide_kickstarters" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
               </c:choose>

		</li>
		
 	<li role="menuitem">
    <c:choose>
        <c:when test="${pageName == 'RegistryAnnouncement'}">
            <span class="active"><bbbl:label key="lbl_leftnavguide_registryannouncement" language ="${pageContext.request.locale.language}"/></span>
        </c:when>
        <c:otherwise>
         <c:set var="lbl_leftnavguide_registryannouncement">
					<bbbl:label key="lbl_leftnavguide_registryannouncement" language="${pageContext.request.locale.language}" />
				</c:set>
				<c:set var="printAtHomeLink">
					<bbbl:label key="bbb_printathome_url" language="${pageContext.request.locale.language}" />
				</c:set>
            	<a href="${contextPath}/${printAtHomeLink}" title="${lbl_leftnavguide_registryannouncement}"><bbbl:label key="lbl_leftnavguide_registryannouncement" language ="${pageContext.request.locale.language}"/></a>
            </c:otherwise>
            </c:choose>	
    </li>
    </c:if>
	
	<c:choose>
	 <c:when test="${currentSiteId eq BuyBuyBabySite}">
	     <li role="menuitem">
	     <c:choose>
             <c:when test="${pageName == 'BabyEvents'}">
                 <span class="active"><bbbl:label key="lbl_leftnavguide_babyshows" language ="${pageContext.request.locale.language}"/></span>
             </c:when>
             <c:otherwise>
             <c:set var="lbl_leftnavguide_babyshows">
					<bbbl:label key="lbl_leftnavguide_babyshows" language="${pageContext.request.locale.language}" />
				</c:set>
                 <a href="${contextPath}/page/BabyEvents" title="${lbl_leftnavguide_babyshows}"><bbbl:label key="lbl_leftnavguide_babyshows" language ="${pageContext.request.locale.language}"/></a>
             </c:otherwise>
         </c:choose>
         </li>
      </c:when>
	 
	 <c:otherwise>
	   	 
    <c:if test="${not bbcy}">
     <li role="menuitem">
	   
			    <c:choose>
                <c:when test="${pageName == 'KickStarter'}">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_kickstarters" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                <c:set var="lbl_leftnavguide_bridaltool">
					<bbbl:label key="lbl_leftnavguide_kickstarters" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/kickstarters/" title="${lbl_leftnavguide_kickstarters}">
				    <bbbl:label key="lbl_leftnavguide_kickstarters" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
               </c:choose>
          </li>
     </c:if>
  
		
		<c:if test="${(BridalBookOn) and (not bbcy)}">
            <li role="menuitem">
            <c:choose>
                <c:when test="${pageName == 'BridalBook'}">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_bridalbook" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                <c:set var="lbl_leftnavguide_bridalbook">
					<bbbl:label key="lbl_leftnavguide_bridalbook" language="${pageContext.request.locale.language}" />
				</c:set>
                    <a href="${contextPath}/bbregistry/BridalBook" title="${lbl_leftnavguide_bridalbook}"><bbbl:label key="lbl_leftnavguide_bridalbook" language ="${pageContext.request.locale.language}"/></a>
                </c:otherwise>
            </c:choose>
            </li>
       </c:if> 
		<li role="menuitem">
	    <c:choose>
            <c:when test="${pageName == 'BridalShow'}">
			    <%-- COMMENTED TO HIDE BRIDAL SHOWS OPTIONS FOR USER --%>
                <%--<span class="active"><bbbl:label key="lbl_leftnavguide_bridalshows" language ="${pageContext.request.locale.language}"/></span>--%>
            </c:when>
            <c:otherwise>
            <c:set var="lbl_leftnavguide_bridalshows">
					<bbbl:label key="lbl_leftnavguide_bridalshows" language="${pageContext.request.locale.language}" />
				</c:set>
				 <c:choose>
		    <c:when test="${siteId ne BuyBuyBabySite}">
		   
                <%--<a href="${contextPath}/page/BridalShows" title="${lbl_leftnavguide_bridalshows}"><bbbl:label key="lbl_leftnavguide_bridalshows" language ="${pageContext.request.locale.language}"/></a>--%>
                </c:when>
                <c:otherwise>
                 <a href="${contextPath}/page/BabyShows" title="${lbl_leftnavguide_bridalshows}"><bbbl:label key="lbl_leftnavguide_bridalshows" language ="${pageContext.request.locale.language}"/></a>
                </c:otherwise>
                </c:choose>
            </c:otherwise>
         </c:choose>
		 </li>
	 </c:otherwise>
   </c:choose>
   	<c:if test="${currentSiteId eq BedBathUSSite}">
   	<li role="menuitem">
   	<c:choose>
        <c:when test="${pageName == 'RegistryAnnouncement'}">
            <span class="active"><bbbl:label key="lbl_leftnavguide_registryannouncement" language ="${pageContext.request.locale.language}"/></span>
        </c:when>
        <c:otherwise>
         <c:set var="lbl_leftnavguide_registryannouncement">
					<bbbl:label key="lbl_leftnavguide_registryannouncement" language="${pageContext.request.locale.language}" />
			</c:set>
			<c:set var="printAtHomeLink">
					<bbbl:label key="bbb_printathome_url" language="${pageContext.request.locale.language}" />
			</c:set>	 
            <a href="${contextPath}/${printAtHomeLink}"  title="${lbl_leftnavguide_registryannouncement}"><bbbl:label key="lbl_leftnavguide_registryannouncement" language ="${pageContext.request.locale.language}"/></a>
        </c:otherwise>
    </c:choose> 	
    </li>
    </c:if>
		<c:if test="${currentSiteId ne BedBathCanadaSite}">
   
	<li role="menuitem">

		 	<c:choose>
                <c:when test="${pageName == 'PersonalizedInvitations'}">
                    <span class="active"><bbbl:label key="lbl_leftnavguide_personalin" language ="${pageContext.request.locale.language}"/></span>
                </c:when>
                <c:otherwise>
                <c:set var="lbl_leftnavguide_personalin">
					<bbbl:label key="lbl_leftnavguide_personalin" language="${pageContext.request.locale.language}" />
				</c:set>
                    <dsp:a href="${contextPath}/registry/PersonalizedInvitations" title="${lbl_leftnavguide_personalin}">
				    <bbbl:label key="lbl_leftnavguide_personalin" language ="${pageContext.request.locale.language}"/>
			        </dsp:a>
                </c:otherwise>
            </c:choose>

	</li>
	</c:if>
    
       </ul>
</div>



