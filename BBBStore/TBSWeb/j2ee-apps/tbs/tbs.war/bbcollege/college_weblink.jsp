<dsp:page>
  <dsp:importbean bean="/com/bbb/commerce/catalog/droplet/SchoolLookupDroplet" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:droplet name="SchoolLookupDroplet">
	  <dsp:getvalueof var="schoolId" param="schoolId"/>
	  <dsp:param name="schoolId" value="${schoolId}"/>
		    <dsp:oparam name="output"> 
		           
				   
			            <dsp:getvalueof var="largeWelcomeMsg" param="SchoolVO.largeWelcomeMsg" />
					    <dsp:getvalueof var="schoolName" param="SchoolVO.schoolName" />
			            <dsp:getvalueof var="largeLogoURL" param="SchoolVO.largeLogoURL" />						
			            <dsp:getvalueof var="smallLogoURL" param="SchoolVO.smallLogoURL" />
			            <dsp:getvalueof var="smallWelcomeMsg" param="SchoolVO.smallWelcomeMsg" />
			            
			            
			    <c:choose>
			    
			    <%-- 
			   		 	Show college specific promotion only when logo is there else show standard college lading page -
			    		If Logo is present but there is no valid promotion, then show the college specific landing page without
			    		Promotion module -  CR3 
			    --%>
			    
					 <c:when test="${(not empty largeLogoURL || not empty largeWelcomeMsg || not empty smallLogoURL || not empty smallWelcomeMsg)}">
						<div class="collegePromoContainer contentDivs">
                            <c:if test="${(not empty largeLogoURL || not empty largeWelcomeMsg)}">
                            <c:set var="hidePromoDataClass" value=" hidden " />
							<div class="collegePromo contentDivs noMar clearfix">
								<div class="spacer fl schoolName <c:if test="${not empty largeLogoURL}"> schoolNameFixWidth </c:if>">
									<h2 class="noMarBot"><bbbl:label key="lbl_collegeweblink_welcome" language ="${pageContext.request.locale.language}"/></h2>
									<h1 class="noMarTop">
										<c:choose>
											 <c:when test="${not empty largeWelcomeMsg}">
											 ${largeWelcomeMsg}
										  
											 </c:when>
											 <c:otherwise>
											 
											 ${schoolName}
											 </c:otherwise>
										</c:choose>
									 </h1>
									 
								</div>
                                <c:if test="${not empty largeLogoURL}">
								<div class="spacer fr schoolLogo">
									<img src="${largeLogoURL}" title="${schoolName}" alt="${schoolName}" height="300" width="300" />
								</div>
								</c:if>
                                <div class="clear"></div>
						   </div>
                           </c:if>
							 
                             <c:if test="${(not empty smallLogoURL || not empty smallWelcomeMsg)}">
							<div class="collegePromoData contentDivs noMar ${hidePromoDataClass} clearfix">
								<div class="spacer fl schoolName <c:if test="${not empty smallLogoURL}"> schoolNameFixWidth </c:if>">
									<h2 class="noMarBot"><bbbl:label key="lbl_collegeweblink_welcome" language ="${pageContext.request.locale.language}"/></h2>
									<h1 class="noMarTop">
										<c:choose>
											 <c:when test="${not empty smallWelcomeMsg}">
											 ${smallWelcomeMsg}
											 </c:when>
											 <c:otherwise>
											 ${schoolName}
											 </c:otherwise>
										</c:choose>
									 </h1>
									 
									 <dsp:getvalueof var="schoolPromotions" bean="Profile.schoolPromotions"/>
									 <c:if test="${not empty schoolPromotions}">
											<dsp:getvalueof var="description" param="SchoolVO.promotionRepositoryItem.description" />
											<dsp:getvalueof var="id" param="SchoolVO.promotionRepositoryItem.id" />									 
									 	${description} 
							  		 	<a href="../_includes/modals/college_promo.jsp?promotionId=${id}" class="newOrPopup" title="Click here for details">
							  		 		<bbbl:label key="lbl_collegeweblink_detailink" language ="${pageContext.request.locale.language}"/>
							  		 	</a>
									 </c:if>
									 
									
								</div>
                                <c:if test="${not empty smallLogoURL}">
								<div class="spacer fr schoolLogo">
									<img src="${smallLogoURL}" title="${schoolName}" alt="${schoolName}" height="300" width="300" />
								</div>
                                </c:if>
                                <div class="clear"></div>
							</div>
                            </c:if>
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