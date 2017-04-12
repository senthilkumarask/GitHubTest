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
					<c:set var="localStoreLinkToYext">
						<bbbc:config key="LOCAL_STORE_LINK_TO_YEXT" configName="FlagDrivenFunctions" />
					</c:set>
					<c:if test="${localStoreLinkToYext}">
						<c:set var="hostNameForYEXTLink"><bbbl:label key="lbl_host_name_for_yext" language="${pageContext.request.locale.language}" /></c:set>
						<c:set var="linkDisplayNameForYEXTLink"><bbbl:label key="lbl_link_display_name_for_yext" language="${pageContext.request.locale.language}" /></c:set>
						<dsp:getvalueof var="cityForYEXTLink" param="storeDetails.city" />
						<dsp:getvalueof var="stateForYEXTLink" param="storeDetails.province" />
						<dsp:getvalueof var="zipForYEXTLink" param="storeDetails.postalCode" />
						<dsp:getvalueof var="finalLinkToYext" value="${hostNameForYEXTLink}/${cityForYEXTLink}-${stateForYEXTLink}-${zipForYEXTLink}-${storeId}" />
					</c:if>
					
					<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
                    <div class="grid_3 alpha location">
					<c:choose>
					<c:when test="${localStoreLinkToYext}">
						<h4><a href="${finalLinkToYext}" class="getStoreDetailsLinkOnStoreName" target="_blank"><span class="storeTitle"><dsp:valueof param="storeDetails.storeName" /></span></a>, <dsp:valueof param="storeDetails.province" /></h4>
					</c:when>
					<c:otherwise>
						<h4><span class="storeTitle"><dsp:valueof param="storeDetails.storeName" /></span>, <dsp:valueof param="storeDetails.province" /></h4>
					</c:otherwise>
					</c:choose>
						<div class="address">
							<div class="street"><dsp:valueof param="storeDetails.address" /></div>
							<div> <span class="city"><dsp:valueof param="storeDetails.city" />,</span> <span class="state"><dsp:valueof
						param="storeDetails.province" /></span> <span class="zip"><dsp:valueof param="storeDetails.postalCode" /></span> </div>
						</div>
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
							<div class="grid_3 canStoreButton">
								<c:choose>
									<c:when test="${not empty favStoreId and favStoreId==storeId}">
									<div class="button button_secondary button_disabled canMyStoreButton">
										<input type="button" value="My Store"> 
									</div>
									</c:when>
									<c:otherwise>
										<div class="button button_secondary canFavStoreButton">
											<dsp:a href="/store/selfservice/CanadaStoreLocator?favouriteStoreId=${storeId}" iclass="storeLocatorMakeFavoriteStoreButton">
												<bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" />
												<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
											</dsp:a>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>

                        <div class="actionLink">
						<a class="viewMap" href="#viewMap" title="<bbbl:label key="lbl_find_store_view_map"
						language="${pageContext.request.locale.language}" />"><bbbl:label key="lbl_find_store_view_map"
						language="${pageContext.request.locale.language}" /></a>
						<c:if test="${localStoreLinkToYext}">
							<a href="${finalLinkToYext}" class="storeLocatorLocationItemGetStoreDetailsLink" target="_blank">${linkDisplayNameForYEXTLink}</a>
						</c:if>
						</div>
				        </div>
                        <div class="grid_3">
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
				  				   	
                            	   	<div class="benefitsItem"><img src="${imagePath}${imageLocation}" alt="${imageAlt}" /></div>		
				  				</dsp:oparam>
				  		    </dsp:droplet>
				  		    </div>
                        </div>
			            <div class="grid_3 omega marTop_10 padTop_5">
			            <c:choose>
			            <c:when test="${flag eq 'true'}">
						<img width="84" height="37" src="${imagePath}/_assets/bbbabyca/images/bbbabyLogoStore.png" alt="Bed Bath &amp; Baby" />
			          
			              </c:when>
			              <c:otherwise>
			            <img width="149" height="39" alt="Bed Bath &amp; Beyond" src="${imagePath}/_assets/bbbabyca/images/bbbyondLogoStore.png"/>
			              </c:otherwise>
			              </c:choose>
			            </div>
                        
</dsp:page>