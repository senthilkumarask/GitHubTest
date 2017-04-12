<%-- ====================== Description===================
/**
* This page is used to display store search results which includes store Name, address, distance, phone number, hours , images and various link
like view map.  
* @author Sandeep
**/
--%>

                    <dsp:page>
 				    <dsp:getvalueof var="counter" param="counter"/>
					<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
 				    <dsp:getvalueof var="firstIteration" param="firstIteration"/>  
					<dsp:getvalueof var="flag" param="storeDetails.babyCanadaFlag"/> 
					<dsp:getvalueof var="storeId" param="storeDetails.storeId"/>
					<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
					
					<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
                    <div class="large-4 columns alpha location no-padding-left">
						<h4><span class="storeTitle"><dsp:valueof param="storeDetails.storeName" /></span>, <dsp:valueof param="storeDetails.province" /></h4>
						<p class="address">
							<div class="street"><dsp:valueof param="storeDetails.address" /></div>
							<div> <span class="city"><dsp:valueof param="storeDetails.city" />,</span> <span class="state"><dsp:valueof
						param="storeDetails.province" /></span> <span class="zip"><dsp:valueof param="storeDetails.postalCode" /></span> </div>
						</p>
						
						<c:if test="${not isTransient}">
							<dsp:getvalueof var="favStoreId" bean="Profile.userSiteItems.favouriteStoreId"/>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" bean="Profile.userSiteItems"/>
								<dsp:param name="elementName" value="sites"/>
								<dsp:oparam name="output">
									<dsp:getvalueof id="key" param="key"/>
									<c:if test="${currentSiteId eq key}">
										<dsp:getvalueof id="favStoreId" param="sites.favouriteStoreId"/>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
							<div class="canStoreButton">
								<c:choose>
									<c:when test="${not empty favStoreId and favStoreId==storeId}">
									<div class="canMyStoreButton">
										<input type="button" value="My Store" class="tiny button secondary disabled"> 
									</div>
									
									</c:when>
									<c:otherwise>
										<div class="canFavStoreButton">
											<dsp:a href="/tbs/selfservice/CanadaStoreLocator?favouriteStoreId=${storeId}" iclass="storeLocatorMakeFavoriteStoreButton tiny button secondary">
												<bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" />
												<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
											</dsp:a>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>

                        <div class="actionLink hidden"> <a class="viewMap" href="#viewMap" title="<bbbl:label key="lbl_find_store_view_map" language="${pageContext.request.locale.language}" />">
                        	<bbbl:label key="lbl_find_store_view_map" language="${pageContext.request.locale.language}" /></a>
                        </div>
				        </div>
                        <div class="large-4 columms inline-title">
                            <p> 
                               <dsp:getvalueof var="hours" param="storeDetails.hours"></dsp:getvalueof>
                                <c:forTokens items="${hours}" delims="," var="tokenName">
									<c:out value="${tokenName}" /> <br />
								</c:forTokens>
                                <strong><dsp:valueof param="storeDetails.phone" /></strong> </p>
                            <div class="clearfix">                            
                            	<dsp:droplet name="/atg/dynamo/droplet/ForEach">   
				  					<dsp:param name="array" param="storeDetails.storeSpecialityVO"/>  
				  					<dsp:oparam name="output">
				  				   	<dsp:param name="storeImageVO" param="element" />
				  				   
				  				  	<dsp:getvalueof var="imageLocation" param="storeImageVO.codeImage"></dsp:getvalueof>
				  				  	<dsp:getvalueof var="imageAlt" param="storeImageVO.storeListAltTxt"></dsp:getvalueof>
				  				  	<dsp:getvalueof var="imageTitleText" param="storeImageVO.storeListTitleTxt"></dsp:getvalueof>
				  				   	<dsp:valueof param="storeDetails.storeSpecialityVO.codeImage" />
                            	   	<div class="benefitsItem"><img src="${imagePath}${imageLocation}" alt="${imageAlt}" /></div>		
				  				</dsp:oparam>
				  		    </dsp:droplet>
				  		    </div>
                        </div>
			            <div class="large-3 columms inline-title">
			            <c:choose>
			            <c:when test="${flag eq 'true'}">
						<img width="84" height="37" src="${imagePath}/_assets/bbbabyca/images/bbbabyLogoStore.png" alt="Bed Bath &amp; Baby" />
			          
			              </c:when>
			              <c:otherwise>
			            <img width="149" height="39" alt="Bed Bath &amp; Beyond" src="${imagePath}/_assets/bbbabyca/images/bbbyondLogoStore.png"/>
			              </c:otherwise>
			              </c:choose>
			            </div>
			 
			            <hr>
                        
</dsp:page>