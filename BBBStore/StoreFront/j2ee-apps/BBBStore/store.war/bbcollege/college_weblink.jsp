<dsp:page>
  <dsp:importbean bean="/com/bbb/commerce/catalog/droplet/SchoolLookupDroplet" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:droplet name="SchoolLookupDroplet">
	  <dsp:getvalueof var="schoolId" param="schoolId"/>
	  <dsp:param name="schoolId" value="${schoolId}"/>
		    <dsp:oparam name="output"> 
		           
				   
		    <dsp:getvalueof var="schoolContent" param="SchoolVO.schoolContent" />
			<dsp:getvalueof var="schoolName" param="SchoolVO.schoolName" />
            <dsp:getvalueof var="catCSSFile" param="SchoolVO.cssFilePath" />
	    	<dsp:getvalueof var="catJSFile" param="SchoolVO.jsFilePath" />
			<dsp:getvalueof var="largeWelcomeMsg" param="SchoolVO.largeWelcomeMsg" />
			<dsp:getvalueof var="smallLogoURL" param="SchoolVO.smallLogoURL" />
			<dsp:getvalueof var="pdfURL" param="SchoolVO.pdfURL" />
			<dsp:getvalueof id="zip" param="SchoolVO.zip"/>
  			<dsp:getvalueof id="city" param="SchoolVO.city"/>
			<dsp:getvalueof id="state" param="SchoolVO.state.stateCode"/>
			<dsp:getvalueof id="addr" value="${city},${state},${zip}"/>

           

			      <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param name="value" bean="Profile.transient"/>
                  <dsp:oparam name="false">
                     <c:set var="enableFavoriteStores" value="true" />        
                  </dsp:oparam>
                  <dsp:oparam name="true">
                           <c:set var="enableFavoriteStores" value="false" />        
                     </dsp:oparam>
              </dsp:droplet>   

              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                     <dsp:param name="array" bean="Profile.userSiteItems"/>
                     <dsp:param name="elementName" value="sites"/>
                     <dsp:oparam name="output">
                           <dsp:getvalueof id="key" param="key"/>
                           <c:if test="${currentSiteId eq key}">
                                  <dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId"/>
                           </c:if>
                     </dsp:oparam>
              </dsp:droplet>
               
              <c:choose>
				<c:when test="${currentSiteId eq BedBathUSSite}">
					<c:set var="radius"><bbbc:config key="radius_college_store_us" configName="MapQuestStoreType" /></c:set> 
				</c:when>
				<c:otherwise>
					<c:set var="radius"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
				</c:otherwise>
			</c:choose>
                    
			      <div class="grid_12 container_college_specific" id="containerCollegeSpecific">
			        <div class="college_name_logo">
			        	<c:if test="${not empty smallLogoURL}"><img src="${smallLogoURL}" alt="logo of ${schoolName}"></c:if>
			        	${largeWelcomeMsg}
			        </div>
			        <dsp:droplet name="/com/bbb/selfservice/ClosestStoreToCollegeDroplet">
                                <dsp:param name="searchString" value="${addr}"/>
                                <dsp:param name="searchType" value="3"/>
                                <dsp:param name="siteId" value="${currentSiteId}"/>
                                <dsp:param name="radius" value="${radius}" />
                                <dsp:oparam name="output">
                                
                                  <dsp:getvalueof var="closestStoreDetail" param="closestStoreDetails"/>
                                 
                                </dsp:oparam>
                                <dsp:oparam name="error">
                    			<div class="error marTop_20">
                    				<p>
                    					<bbbl:label key="lbl_search_store_error" language="${pageContext.request.locale.language}" />
                    				</p>
                    			</div>
                    			</dsp:oparam>
                                </dsp:droplet>
                                <input type="hidden" id="pdf_url" value="${pdfURL}"/>
                                <div class="pdf_college <c:if test="${empty closestStoreDetail}"> align_without_findstore</c:if>">
        			      		  <a href="javascript:void(0)" class='omnitureCall' target="_blank" aria-hidden='true'>
        			      		  	<img src="/_assets/global/images/pdf_icon.png" alt="">
        			      		  </a>
        			      		  <a href="javascript:void(0)" class='omnitureCall' target="_blank"><bbbl:label key="lbl_college_info_sheet" language="${pageContext.request.locale.language}" /></a>
        			      	    </div>
                                <c:if test="${not empty closestStoreDetail}"> 
								   <div class="closest_store">
			      		           <img src="/_assets/global/images/closest_store.png" alt="">
			      		           <div class="closest_data">
				      		       <span><bbbl:label key="lbl_college_closest_store" language="${pageContext.request.locale.language}" /></span>
							      		<span class="store_name" tabindex="0" style="outline:none"> ${closestStoreDetail.storeName}</span>
							      		<div class="link_container">
							      		  <a href="javascript:void(0)" aria-labelledby="closestStoreInfo" ><bbbl:label key="lbl_college_store_info" language="${pageContext.request.locale.language}" /></a>
							      		  <input type="hidden" value="true" name="frmCollege" aria-hidden='true'>
							      		  <a id="showMapViewMore" href="javascript:void(0)" data-origin="${addr}" data-conceptID="${currentSiteId}" data-enableFavoriteStores="${enableFavoriteStores}" data-favoriteStoreID="${favouriteStoreId}" aria-label="View more stores in your school's area">
							      		<bbbl:label key="lbl_college_view_more" language="${pageContext.request.locale.language}" /></a>
							      		</div>
						      		</div>
			      		
			      		
			      	</div>
                    <%-- INCLUDE JSP FOR FIND STORE --%>
                    <dsp:include page="/selfservice/store/find_store_pdp.jsp"></dsp:include>
			      </div> 
			      <div id="closestStoreInfo">
			      	<div class="closestStoreInfo">
			      		<span class="visuallyhidden"> store info for  ${closestStoreDetail.storeName} store.  </span>
			      		<div class="store_name"> ${closestStoreDetail.storeName} (${closestStoreDetail.distance} ${closestStoreDetail.distanceUnit} )</div>
	                    <div class="address_store"> ${closestStoreDetail.address}</div>
	                    <div class="address_store">
	                    ${closestStoreDetail.city}, ${closestStoreDetail.state}, ${closestStoreDetail.postalCode}
	                   
	                    </div>
	                    <div class="address_store storePhone">
	                    	 ${closestStoreDetail.storePhone}
	                    </div>
	                    <a href="javascript:void(0)" id="getStoreDirection" title="Get Map & Directions" data-submit-storeId="${closestStoreDetail.storeId}"><bbbl:label key="lbl_find_store_get_directions" language="${pageContext.request.locale.language}" /></a>
				        <div class="day_timing">${closestStoreDetail.weekdaysStoreTimings}</div>
				        <div class="day_timing">${closestStoreDetail.satStoreTimings}</div>
				        <div class="day_timing">${closestStoreDetail.sunStoreTimings}</div>
			      	</div>
			      </div>    
			      </c:if>
                  
			   
					 
			    <c:choose>
			     <c:when test="${!(empty schoolContent)}">
					 <script type="text/javascript" src="${catJSFile}"></script>
						<link rel="stylesheet" type="text/css" href="${catCSSFile }" />
						<div id="schoolContent" class="marBottom_10 marLeft_10 clearfix">
						  ${schoolContent}
						</div>
					 </c:when>
					 <c:otherwise>						
						<dsp:include page="hero_image.jsp">
						   <dsp:param name="heroImage" value="${heroImage}"/>
						 </dsp:include>
					 </c:otherwise>
				</c:choose>  
			</dsp:oparam>
	</dsp:droplet>
</dsp:page>