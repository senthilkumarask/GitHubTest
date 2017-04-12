<dsp:page>
	<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/SelectHeaderDroplet" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />	
		<dsp:importbean bean="/atg/userprofiling/Profile" />
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
        <dsp:getvalueof var="transient" bean="Profile.transient" />
        
		<dsp:param name="currentSiteId" param="currentSiteId" />
		<dsp:param name="PageType" param="PageType" />
		<dsp:droplet name="SelectHeaderDroplet">
			<dsp:param name="siteId" value="${currentSiteId}" />
			<dsp:oparam name="output">
			<dsp:getvalueof id="subheader" param="subheader"/>
            <c:set var="subHeaderType" scope="request">${subheader}</c:set>
            <c:if test="${subheader == 'showGenericHeader'}">
				<div id="headerWrap">
			        <div class="subNavWrapper">
					  <div class="container_12 clearfix">
			            <div class="subnav grid_12">
			                <div class="grid_6 omega">
			                
								<%--variable babyCAMode is setup in pagestart  --%>
			                	<c:choose>
									<c:when test="${currentSiteId == 'TBS_BuyBuyBaby' || (currentSiteId  == 'TBS_BedBathCanada' && babyCAMode == 'true')}">
										<%-- Start: Added for Scope # 81 H1 tags --%>
										<c:if test="${PageType == 'RegistryLandingBaby'}">
											<h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
										</c:if>
										<%-- End: Added for Scope # 81 H1 tags --%>
										<div class="fl subNavBb marRight_20"><img src="${imagePath}/_assets/bbbaby/images/baby_registry_logo.png" alt="baby registry" /></div>
									</c:when>
									<c:otherwise>
										<%-- Start: Added for Scope # 81 H1 tags --%>
										<c:if test="${PageType == 'RegistryLanding'}">
											<h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
										</c:if>
										<%-- End: Added for Scope # 81 H1 tags --%>
				                		<div class="fl subNavBrBlock"><a class="cf inlineBlock" href="${contextPath}/page/Registry"> <bbbl:label key='lbl_mng_regitem_the_Bridal' language="${pageContext.request.locale.language}" /> <img src="${imagePath}/_assets/bbregistry/images/icons/and.png" alt="and"> 
					                		<c:choose>
	                                    		<c:when test="${currentSiteId  == 'TBS_BedBathCanada'}">
	                                    			<bbbl:label key='lbl_mng_regitem_gift_registry_ca' language="${pageContext.request.locale.language}" />
	                                    		</c:when>
	                                    		<c:otherwise>
	                                    			<bbbl:label key='lbl_mng_regitem_gift_registry' language="${pageContext.request.locale.language}" />                                    	
	                                    		</c:otherwise>
	                                    	</c:choose></a>
	                                    </div>
									</c:otherwise>
				               	</c:choose>
				               	<c:if test="${transient == 'false' && !(fn:contains(pageContext.request.requestURL, 'my_registries.jsp'))}">
				               	<a href="${contextPath}/giftregistry/my_registries.jsp" class="viewAllRegistries fl" title="View All Your Registries">
									<bbbl:label key='lbl_mng_regitem_view_all_reg' language="${pageContext.request.locale.language}" />
			               		</a>  
			               		</c:if>             		
			                </div>
			                <c:choose>
								<c:when test="${currentSiteId == 'TBS_BedBathCanada'}">
									<div class="grid_6 omega activity"> <a href="${contextPath}/selfservice/CanadaStoreLocator" title='<bbbl:label key="lbl_regflyout_viewregistries" language ="${pageContext.request.locale.language}"/>' class="store"><bbbl:label key="lbl_regflyout_viewregistries" language ="${pageContext.request.locale.language}"/></a>
								</c:when>
								<c:otherwise>
			                		<div class="grid_6 omega activity"> <a href="${contextPath}/selfservice/FindStore" title='<bbbl:label key="lbl_regflyout_viewregistries" language ="${pageContext.request.locale.language}"/>' class="store"><bbbl:label key="lbl_regflyout_viewregistries" language ="${pageContext.request.locale.language}"/></a>
								</c:otherwise>
				           </c:choose>

			                <span class="ques"><bbbl:label key='lbl_mng_regitem_questions' language="${pageContext.request.locale.language}" /><br> <span><bbbl:label key='lbl_mng_regitem_come_in_free' language="${pageContext.request.locale.language}" /></span></span> </div>
			            </div>
			          </div>
					</div>
			    </div>
			</c:if>   

			 <c:if test="${subheader == 'showPersistentHeader'}">
			    	<div class="container_12 clearfix subHeadRegistryDetail" id="subHeadRegistryDetail">
						<dsp:getvalueof param="registrySummaryVO" var="registrySummaryVO" scope="request"></dsp:getvalueof>
			    		<dsp:include page="/_includes/header/persistent_subheader.jsp"> 
						   <dsp:param name="currentSiteId" value="${currentSiteId}"/>
						   <dsp:param name="registrySummaryVO" value="${registrySummaryVO}"/>
						   <dsp:param name="PageType" value="${PageType}"/>
						</dsp:include>
				  </div>
			</c:if>

			 <c:if test="${subheader == 'noHeader'}">
			 	<c:choose>
					<c:when test="${currentSiteId == 'TBS_BuyBuyBaby'}">
						<div id="navBottomNotes">
							<div class="container_12 clearfix">
								<div id="navBottomInfo" class="grid_12 clearfix">
									<div id="navBottomInfoContent"></div>
								</div>
							</div>
						</div>		
					</c:when>
					<c:otherwise>
						<bbbt:textArea key="txt_college_bar" language ="${pageContext.request.locale.language}"/>	
					</c:otherwise>
				</c:choose>
			 </c:if>
		
		</dsp:oparam>
		</dsp:droplet>

</dsp:page>