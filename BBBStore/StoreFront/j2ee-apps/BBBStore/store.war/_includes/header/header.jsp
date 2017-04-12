<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
    <dsp:importbean bean="/com/bbb/search/handler/NavigationSearchFormHandler"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
    <dsp:importbean bean="/com/bbb/cms/droplet/HeaderWhatsNewDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/com/bbb/cms/droplet/BBBGetRootCategoryDroplet"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <%-- Removing this code as it was somehow interfering with checkout flow --%>
    <%-- <dsp:setvalue value="CART" bean="CheckoutProgressStates.currentLevel"/> --%>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="currentCatId" param="categoryId"/>
    <dsp:getvalueof id="siteURL" bean="Site.productionURL"/>
    <dsp:getvalueof var="homepage" param="homepage"/>
	<dsp:getvalueof var="PageType" param="PageType"/>
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
	<dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
	<dsp:getvalueof id="isStagingServer" bean="/com/bbb/search/endeca/EndecaSearch.stagingServer"/>
	<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />
	<dsp:getvalueof var="couponCount" bean="SessionBean.couponCount" />
	<input type="hidden" name="isInternationalCustomer" value="${isInternationalCustomer}" />
	<span class="visuallyhidden" aria-live="assertive" id="liveAnnounce"></span>
	<input type="hidden" name="showRegistryRibbon" value="${showRegistryRibbon}" />
	<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
	<c:set var="countryCodeLowerCase" >${fn:toLowerCase(valueMap.defaultUserCountryCode)}</c:set>
	<c:set var="defaultUserCountryCode" scope="session">${valueMap.defaultUserCountryCode}</c:set>
	<dsp:getvalueof var="pageName" value="${pageNameFB}"/>
	<dsp:getvalueof var ="profile" bean="/atg/userprofiling/Profile"/>
	<c:set var="securityStatus" value=""/>
	 <c:if test="${!((fn:indexOf(pageWrapper, 'errorPages') > -1) || (fn:indexOf(pageWrapper, 'serverErrorPages') > -1) || (fn:indexOf(pageWrapper, '500') > -1))}">
	 	<c:set var="securityStatus"><dsp:valueof bean="/atg/userprofiling/Profile.securityStatus"/></c:set>
	 </c:if>

	<c:set var="enableLazyLoadCollegeFlyout"><bbbc:config key="enableLazyLoadCollegeFlyout" configName="FlagDrivenFunctions"/></c:set>
	
	<%-- Retrieving Vendor Parameter for Vendor Story --%>
	<c:set var="vendorKey"><bbbc:config key="VendorParam" configName="VendorKeys"/></c:set>
	<c:if test="${!fn:containsIgnoreCase(vendorKey, 'VALUE NOT FOUND FOR KEY') && not empty vendorKey}">
		<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
		<c:if test="${not empty vendorParam}">
		  <c:set var="vendorParam" value="&${vendorKey}=${vendorParam}" />
	    </c:if>  
	</c:if>
	
	<c:if test="${securityStatus eq '2' }">
		<c:set var="recognizedUserFlag" value="true" scope="request" />
		<input id="recognizedUserFlag" class="recognizedUserFlag" type="hidden" value="true"/>
		<c:set var="displayNotYou">true</c:set>
	</c:if>
	<dsp:getvalueof var="isLoggedIn" bean="/atg/userprofiling/Profile.transient"/>
	<c:set var="currentURL">${pageContext.request.servletPath}</c:set>
	<c:if test="${isLoggedIn ne 'true' && fn:containsIgnoreCase(currentURL, 'changePwdAfterResetmodel.jsp')}">
		<dsp:setvalue bean="ProfileFormHandler.refreshUserProfile" />
	</c:if>

	<c:set var="homePageCachingOn">
		<bbbc:config key="HOME_PAGE_CACHING_ON_OFF" configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="personalStore">
	<bbbc:config key="PSP_homepageLink" configName="FlagDrivenFunctions" />
	</c:set>
    <c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="flyoutCacheTimeout">
		<bbbc:config key="FlyoutCacheTimeout" configName="HTMLCacheKeys" />
	</c:set>
	<c:set var="newRelicTagON">
		<bbbc:config key="newRelicTag" configName="ContentCatalogKeys" />
	</c:set>
	
	<c:if test="${empty currentSiteId}">
	  <dsp:getvalueof var="currentSiteId" bean="Site.id" />
	</c:if>
	
    <c:choose>
     <c:when test="${currentSiteId == BedBathCanadaSite}">
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BedBathCanada_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	<c:when test="${currentSiteId == BedBathUSSite}">
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BedBathUS_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	<c:when test="${currentSiteId == BuyBuyBabySite}">
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BuyBuyBaby_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	</c:choose>
	<c:set var="promotionTabId" scope="request">
	  <bbbc:config key="promotionTab" configName="PageThemeKeys" />
    </c:set>
    <c:if test="${!(empty promotionTabId) && fn:contains(promotionTabId, currentSiteId)}">
    <dsp:droplet name="/com/bbb/cms/droplet/PromotionTabFlyoutDroplet">
          <dsp:param name="promoTabId" value="${promotionTabId}"/>
          <dsp:oparam name="output">      
          <dsp:getvalueof var="promoBoxContentVO" param="promoBoxContentVO" />
             <dsp:getvalueof var="promoContent" param="promoBoxContentVO.promoBoxContent" />
             <dsp:getvalueof var="catCSSFile" param="promoBoxContentVO.promoBoxCssFilePath" />
   	      <dsp:getvalueof var="catJSFile" param="promoBoxContentVO.promoBoxJsFilePath" />
          </dsp:oparam>
       </dsp:droplet>
      </c:if>
		<%--BBBSL-7133|Adding Separate config keys for Find A store  --%>	
		<c:set var="FindAStoreOn" scope="request">
			<bbbc:config key="FindAStoreOn" configName="ContentCatalogKeys" />
		</c:set>	
                            
	 <div id="headerWrapper" role="navigation">
	 	 <c:set var="enableFindAStore"><bbbc:config key="enableSlimHeader_FindAStore" configName="FlagDrivenFunctions" /></c:set>
     	 <c:set var="enableSignUpForOffers"><bbbc:config key="enableSlimHeader_SignUpForOffers" configName="FlagDrivenFunctions" /></c:set>
         <c:set var="enableTrackOrder"><bbbc:config key="enableSlimHeader_TrackOrder" configName="FlagDrivenFunctions" /></c:set>
         <c:set var="enableContactUs"><bbbc:config key="enableSlimHeader_ContactUs" configName="FlagDrivenFunctions" /></c:set>
         <c:set var="enableGiftCards"><bbbc:config key="enableSlimHeader_GiftCards" configName="FlagDrivenFunctions" /></c:set>
         <c:set var="enableShipTo"><bbbc:config key="enableSlimHeader_ShipTo" configName="FlagDrivenFunctions" /></c:set>
         <c:set var="enableYouMayLike"><bbbc:config key="enableSlimHeader_YouMayLike" configName="FlagDrivenFunctions" /></c:set>
        <div id="topNavHeader">
        <input type="hidden" name="isAdBlockerDetected" id="isAdBlocker" value="false" /> 
        <c:set var="CookieBlockerFlag"><bbbc:config key="CookieBlockerFlag" configName="FlagDrivenFunctions" /></c:set>					
		<c:if test="${CookieBlockerFlag}">
			<div id="cookie-block-msg-wrapper" style="display:none;">
				<bbbt:textArea key="txt_cookie_blocker_msg"  language ="${pageContext.request.locale.language}"/>
			</div>
		</c:if>	 
        <c:if test="${not empty PageType}">
								<c:set var="AdBlockerFlag"><bbbc:config key="AdBlockerFlag" configName="FlagDrivenFunctions" /></c:set>								
								<c:set var="AdBlocker"><bbbc:config key="${PageType}" configName="AdBlockerPages" /></c:set>
								
								<c:if test="${AdBlockerFlag}">
									<c:choose>
										<c:when test="${fn:containsIgnoreCase(AdBlocker,'false')}">
											<input type="hidden" name="showAdBlockerMesage" id="showAdBlockerMsg" value="true" />
										</c:when>
										<c:otherwise>
											<input type="hidden" name="showAdBlockerMesage" id="showAdBlockerMsg" value="false" />
										</c:otherwise>		
									</c:choose>																					
									<div class="ad-block-msg-wrapper" style="display:none;">
										<bbbt:textArea key="txt_ad_blocker_msg"  language ="${pageContext.request.locale.language}"/>
									</div>
								</c:if>	
								</c:if>
       	 		<div class="container_12 posRel clearfix">
	                <div class="grid_9 topNapWrap omega">
	                    <div id="topNavMenu">
	                        <div id="ssOurBrands-textLinks">
	                        	<c:if test="${currentSiteId ne BedBathCanadaSite}">
	                        	 <bbbt:textArea key="txt_homepage_header"  language ="${pageContext.request.locale.language}"/>
	                        	</c:if>
							 	<c:if test="${currentSiteId eq BedBathCanadaSite }">
							 	<ul>
							 	<c:if test="${enableFindAStore}">
	                            <c:choose>
	                                <c:when test="${currentSiteId == BedBathCanadaSite}">
	                                    <li class="findStore"><dsp:a href="${contextPath}/selfservice/CanadaStoreLocator"><span class="icon icon-store-icon marRight_5" aria-hidden="true"></span><bbbl:label key="lbl_header_canadafindastore" language="${pageContext.request.locale.language}" /></dsp:a></li>                        
	                                </c:when>
	                                <c:otherwise>
			                          <c:if test="${FindAStoreOn}">
	                                    <li class="findStore"><a href="${contextPath}/selfservice/FindStore"><bbbl:label key="lbl_header_findastore" language="${pageContext.request.locale.language}" /></a></li>
	                                  </c:if>
	                                </c:otherwise>
	                            </c:choose>
	                            </c:if> 
	                            <c:if test="${personalStore && enableYouMayLike}">
	                            <li>
	                            <c:choose>
	                            <c:when test="${currentSiteId ne BedBathCanadaSite}">
									<div id="personalStoreLink">
										<a href="${contextPath}/personalstore/"><bbbl:label key="lbl_personal_feed" language="${pageContext.request.locale.language}" /> </a>
									</div>
								</c:when>
								<c:otherwise>
								    <div id="personalStoreLink">
										<a href="${contextPath}/personalstore/"><bbbl:label key="lbl_personal_feed" language="${pageContext.request.locale.language}"/></a>
									</div>
								</c:otherwise>
								</c:choose>
									
								</li>
	                            </c:if>
	                             <c:if test="${enableSignUpForOffers}">
	                            <%-- BBBSL-2355 START --%>
	                           	<bbbt:textArea key="txt_email_sign_up_link"  language ="${pageContext.request.locale.language}"/>
	                          	<%-- BBBSL-2355 END --%>
	                          	</c:if>
	                             <c:if test="${enableTrackOrder}">
	                            <li>
								<c:choose>
	                            	<c:when test="${!isLoggedIn and requestScope.recognizedUserFlag eq false}">
	                            		<a href="${contextPath}/account/order_summary.jsp">
	                            	</c:when>
	                            	<c:otherwise>
										<a href="${contextPath}/account/TrackOrder">
									</c:otherwise>
	                            </c:choose>
								<bbbl:label key="lbl_header_trackorder" language="${pageContext.request.locale.language}" /></a></li>
								</c:if>
	                            <c:if test="${ContactUsOn && enableContactUs}">
	                            <li><a href="${contextPath}/selfservice/ContactUs"><bbbl:label key="lbl_header_contactus" language="${pageContext.request.locale.language}" /></a></li>
	                            </c:if>
								<c:if test="${enableGiftCards}">
	                            <li><a href="${contextPath}/static/GiftCardHomePage"><bbbl:label key="lbl_header_giftcards" language="${pageContext.request.locale.language}" /></a></li>
	                            </c:if>
							 	
							 	</ul>
							 	</c:if>
							</div>
	                        <ul class="clearfix">
		                        <c:set var="internationalShippingOn"><bbbc:config key="international_shipping_switch" configName="International_Shipping"/></c:set>

	                            <c:choose>
		                        <c:when test="${homePageCachingOn eq true}">
						        	 <li class="last posRel clearfix">						        	 	
						        	 </li>
						       	</c:when>
						       	<c:otherwise>
	                            <dsp:droplet name="/atg/dynamo/droplet/Switch">
	                                <dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
	                                <dsp:oparam name="true">
	                                    <li class="last"><dsp:a page="/account/Login">

	                                    <span class="icon icon-user marRight_5" aria-hidden="true"></span>

	                                    <bbbl:label key="lbl_header_myaccount_new_store" language="${pageContext.request.locale.language}" /></dsp:a></li>
	                                </dsp:oparam>
	                                <dsp:oparam name="false">
	                                    <li class="last <c:if test="${displayNotYou eq true}">myAccLogin</c:if>">
	                                        <div id="myAccountLink">
												<dsp:getvalueof bean="/atg/userprofiling/Profile.firstName" var="displayName" />
	                                            <a href="${contextPath}/account/myaccount.jsp" class="recognizedUser">
													<span class="icon icon-user marRight_5" aria-hidden="true"></span>
													<c:choose>
													<c:when test="${currentSiteId eq BuyBuyBabySite}">
													<span class="userT">
													<bbbl:label key="lbl_header_myaccount" language="${pageContext.request.locale.language}" />
														<c:out value="${fn:substring(displayName,0,21)}"/>
													</span>	
	                                        		</c:when>
													<c:otherwise>
													<span class="userT">
													<bbbl:label key="lbl_header_myaccount" language="${pageContext.request.locale.language}" />
														<c:out value="${displayName}"/>
													</span>	
													</c:otherwise>
													</c:choose>																						    
													<c:choose>
													  <c:when test="${not empty couponCount && couponCount > 0}">
													    <span id="accountNotificationsCount" class="accountHeaderNotification">${couponCount}</span>
													  </c:when>
													  <c:otherwise>
													    <span id="accountNotificationsCount">
													     <!-- <span class="textCenter">
   															<img width="20" height="20" alt="small loader" class="loader" src="/_assets/global/images/widgets/small_loader.gif" />
														 </span> -->
													     </span>
													  </c:otherwise>
													</c:choose>
												</a>
	                                        </div>
											<c:if test="${displayNotYou eq true}">
												<div>
													<dsp:a href="javascript:void(0);" iclass="displayNotYou"><bbbl:label key="lbl_display_not_you" language="${pageContext.request.locale.language}" /></dsp:a>
												</div>
											</c:if>
											
											
											
	                                        <div id="myAccountFlyout">
	                                            <ul>
	                                                <li><a href="${contextPath}/account/order_summary.jsp" class="recognizedUser"><bbbl:label key="lbl_header_orders" language="${pageContext.request.locale.language}" /></a></li>
													<c:if test="${MapQuestOn}">
														<li><a href="${contextPath}/account/favoritestore.jsp"><bbbl:label key="lbl_header_favoritestore" language="${pageContext.request.locale.language}" /></a></li>
	                                               	</c:if>
	                                                <li><a href="${contextPath}/account/address_book.jsp" class="recognizedUser"><bbbl:label key="lbl_header_addressbook" language="${pageContext.request.locale.language}" /></a></li>
	                                                <li><a href="${contextPath}/account/view_credit_card.jsp" class="recognizedUser"><bbbl:label key="lbl_header_creditcards" language="${pageContext.request.locale.language}" /></a></li>
	                                                <li><a href="${contextPath}/giftregistry/my_registries.jsp" class="recognizedUser"><bbbl:label key="lbl_header_registries" language="${pageContext.request.locale.language}" /></a></li>
	                                                <li><a href="${contextPath}/wishlist/wish_list.jsp"><bbbl:label key="lbl_header_wishlist" language="${pageContext.request.locale.language}" /></a></li>
	                                                <c:if test="${CouponOn}">
														<li class="couponWallet">
															<a href="${contextPath}/account/coupons.jsp"><bbbl:label key="lbl_header_coupons" language="${pageContext.request.locale.language}" />
															<c:choose>
															  <c:when test="${not empty couponCount && couponCount > 0}">
															     <span id="accountFlyoutCouponCount" class="accountFlyoutCouponCount">${couponCount}</span>															     
															  </c:when>
															  <c:otherwise>	
																<span id="accountFlyoutCouponCount">
														     	<!-- <span class="textCenter">
  																	<img width="20" height="20" alt="small loader" class="loader" src="/_assets/global/images/widgets/small_loader.gif" />
															    </span> -->
														        </span>
															  </c:otherwise>
															</c:choose>
															</a>
														</li>
													</c:if>
	                                                <li><a href="${contextPath}/account/personalinfo.jsp" class="recognizedUser"><bbbl:label key="lbl_header_personalinfo" language="${pageContext.request.locale.language}" /></a></li>
	                                                <c:if test= "${HarteHanksOn}">
	                                                <li><a href="${contextPath}/account/preferences.jsp" class="recognizedUser"><bbbl:label key="lbl_header_preferences" language="${pageContext.request.locale.language}" /></a></li>
	                                                </c:if>
	                                                 <c:if test="${KirschOn}">
		                                                <li><a href="${contextPath}/account/kirsch.jsp"><bbbl:label key="lbl_header_levolorproject" language="${pageContext.request.locale.language}" /></a></li>
	                                                </c:if>
	                                                <bbbt:textArea key="txt_siteSpect_account_flyout"  language ="${pageContext.request.locale.language}"/>
	                                                <li><dsp:a href=""><dsp:property bean="ProfileFormHandler.logout" value="true"/><bbbl:label key="lbl_header_logout" language="${pageContext.request.locale.language}" /></dsp:a></li>
	                                            </ul>
	                                        </div>
	                                    </li>
	                               </dsp:oparam>
	                            </dsp:droplet>
	                            </c:otherwise>
	                            </c:choose>
	                        </ul>
	                    </div>
	                </div>
	                <c:choose>
			         <c:when test="${homePageCachingOn eq true}">
			        	  <div id="shoppingCartTrigger" class="grid_3 alpha">
			        	 	<div class="textCenter"><img width="20" height="20" class="loadingTopNav" alt="loading" src="/_assets/global/images/widgets/small_loader.gif" /></div>
			        	 </div>
			       	 </c:when>
			       	 <c:otherwise>
	                <div id="shoppingCartTrigger" class="grid_3 alpha">
	                    <div id="shoppingCartMenu">
	                        <div id="shoppingCartLinks">
	                            <div id="shoppingCartItems">
	                                <dsp:a rel="nofollow" page="/cart/cart.jsp">

                                    <c:choose>  
	                                    <c:when test="${currentSiteId ne BuyBuyBabySite}">
	                                    	<div class="cart-icon fl">
	                                			<svg version="1.1" id="svg2989" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:inkscape="http://www.inkscape.org/namespaces/inkscape" xmlns:sodipodi="http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd" xmlns:svg="http://www.w3.org/2000/svg" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:cc="http://creativecommons.org/ns#" sodipodi:docname="shopping_cart_font_awesome.svg" inkscape:version="0.48.3.1 r9886" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 612 792" enable-background="new 0 0 612 792" xml:space="preserve">
	                                			<sodipodi:namedview  guidetolerance="10" id="namedview2995" borderopacity="1" inkscape:cy="896" gridtolerance="10" objecttolerance="10" pagecolor="#ffffff" bordercolor="#666666" showgrid="false" inkscape:cx="896" inkscape:zoom="0.13169643" inkscape:current-layer="svg2989" inkscape:window-maximized="0" inkscape:window-y="25" inkscape:window-x="0" inkscape:window-height="480" inkscape:pageshadow="2" inkscape:window-width="640" inkscape:pageopacity="0">
	                                			</sodipodi:namedview>
	                                			<g>
	                                			<path fill="#333333" d="M451.5,477.5c0,12.1,4.3,22.4,12.8,30.9c8.5,8.5,18.8,12.8,30.9,12.8c12.1,0,22.4-4.3,30.9-12.8 c8.5-8.5,12.8-18.8,12.8-30.9s-4.3-22.4-12.8-30.9c-8.5-8.5-18.8-12.8-30.9-12.8c-12.1,0-22.4,4.3-30.9,12.8 C455.8,455.1,451.5,465.4,451.5,477.5z"/>
	                                			<path fill="#333333" d="M145.5,477.5c0,12.1,4.3,22.4,12.8,30.9c8.5,8.5,18.8,12.8,30.9,12.8s22.4-4.3,30.9-12.8 c8.5-8.5,12.8-18.8,12.8-30.9s-4.3-22.4-12.8-30.9c-8.5-8.5-18.8-12.8-30.9-12.8s-22.4,4.3-30.9,12.8 C149.8,455.1,145.5,465.4,145.5,477.5z"/>
	                                			<path fill="#333333" d="M576.1,90.6c-4.3-4.3-9.4-6.5-15.4-6.5H150.6c-0.2-1.6-0.7-4.5-1.5-8.7c-0.8-4.2-1.4-7.6-1.9-10.1 c-0.5-2.5-1.3-5.5-2.6-9.1c-1.3-3.5-2.7-6.3-4.4-8.4c-1.7-2-4-3.8-6.8-5.3c-2.8-1.5-6.1-2.2-9.7-2.2H36.2c-5.9,0-11,2.2-15.4,6.5 c-4.3,4.3-6.5,9.4-6.5,15.4c0,5.9,2.2,11,6.5,15.4s9.4,6.5,15.4,6.5h69.7l60.4,281.1c-0.5,0.9-2.8,5.2-7,13 c-4.2,7.7-7.6,14.5-10.1,20.3c-2.5,5.8-3.8,10.3-3.8,13.5c0,5.9,2.2,11,6.5,15.4c4.3,4.3,9.4,6.5,15.4,6.5h21.9h306h21.9 c5.9,0,11-2.2,15.4-6.5c4.3-4.3,6.5-9.4,6.5-15.4c0-5.9-2.2-11-6.5-15.4c-4.3-4.3-9.4-6.5-15.4-6.5H202.9 c5.5-10.9,8.2-18.2,8.2-21.9c0-2.3-0.3-4.8-0.9-7.5c-0.6-2.7-1.3-5.7-2-9.1c-0.8-3.3-1.3-5.7-1.5-7.3l356.5-41.7 c5.7-0.7,10.4-3.1,14-7.3c3.6-4.2,5.5-9.1,5.5-14.5V105.9C582.6,100,580.5,94.9,576.1,90.6z M546,221.3l-48.3,0l-8.8-44.3l57.1,0 V221.3z M167.8,177l45.3,0l8.8,44.3l-43.9,0L167.8,177z M231.5,176.9l50.1,0l8.8,44.3l-50.1,0L231.5,176.9z M405.8,163.9l-46.3,0 L352,126h46.3L405.8,163.9z M416.6,126h43.8l7.5,37.9l-43.8,0L416.6,126z M362.1,176.9l46.3,0l8.8,44.3l-46.3,0L362.1,176.9z M352.6,221.3l-43.8,0l-8.8-44.3l43.8,0L352.6,221.3z M341.2,163.9l-43.8,0l-7.5-37.9h43.8L341.2,163.9z M279,163.9l-50.1,0 l-7.6-37.9h50.1L279,163.9z M293,234.3l11.5,57.6l-49.1,5.1l-12.5-62.7L293,234.3z M311.4,234.3l43.8,0l10.2,51.4l-42.9,4.4 L311.4,234.3z M373.5,234.3l46.3,0l8.9,44.8l-45.4,4.7L373.5,234.3z M438.2,234.3l43.8,0l7.7,38.5l-42.9,4.4L438.2,234.3z M435.6,221.3l-8.8-44.3l43.8,0l8.8,44.3L435.6,221.3z M546,163.9l-59.7,0l-7.5-37.9H546V163.9z M203,126l7.6,37.9l-45.7,0l-8.8-38 H203z M197,303l-15.9-68.7l43.4,0l12.8,64.6L197,303z M507.6,271l-7.3-36.7l45.7,0V267L507.6,271z"/>
	                                			</g>
	                                			</svg>
	                                		</div>
	                                    	<span id="cartItems" class="block fl"><dsp:include page="/cart/cart_item_count.jsp"/></span> <!-- <bbbl:label key="lbl_orderitems_items" language="${pageContext.request.locale.language}" /> -->
	                                    </c:when>
										<c:otherwise>
	                                    	<div class="cart-icon fl">
	                                			<svg version="1.1" id="svg2989" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:inkscape="http://www.inkscape.org/namespaces/inkscape" xmlns:svg="http://www.w3.org/2000/svg" xmlns:sodipodi="http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd" xmlns:cc="http://creativecommons.org/ns#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" inkscape:version="0.48.3.1 r9886" sodipodi:docname="shopping_cart_font_awesome.svg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 612 792" enable-background="new 0 0 612 792" xml:space="preserve">
	                                			<sodipodi:namedview  bordercolor="#666666" inkscape:zoom="0.13169643" inkscape:cx="896" guidetolerance="10" inkscape:cy="896" id="namedview2995" borderopacity="1" gridtolerance="10" showgrid="false" objecttolerance="10" pagecolor="#ffffff" inkscape:pageopacity="0" inkscape:window-width="640" inkscape:window-maximized="0" inkscape:current-layer="svg2989" inkscape:window-x="0" inkscape:window-height="480" inkscape:window-y="25" inkscape:pageshadow="2"></sodipodi:namedview>
	                                			<g>
	                                			<path fill="#FFFFFF" d="M451.5,477.5c0,12.1,4.3,22.4,12.8,30.9c8.5,8.5,18.8,12.8,30.9,12.8s22.4-4.3,30.9-12.8 s12.8-18.8,12.8-30.9s-4.3-22.4-12.8-30.9s-18.8-12.8-30.9-12.8s-22.4,4.3-30.9,12.8C455.8,455.1,451.5,465.4,451.5,477.5z"/>
	                                			<path fill="#FFFFFF" d="M145.5,477.5c0,12.1,4.3,22.4,12.8,30.9c8.5,8.5,18.8,12.8,30.9,12.8s22.4-4.3,30.9-12.8 s12.8-18.8,12.8-30.9s-4.3-22.4-12.8-30.9s-18.8-12.8-30.9-12.8s-22.4,4.3-30.9,12.8C149.8,455.1,145.5,465.4,145.5,477.5z"/>
	                                			<path fill="#FFFFFF" d="M576.1,90.6c-4.3-4.3-9.4-6.5-15.4-6.5H150.6c-0.2-1.6-0.7-4.5-1.5-8.7s-1.4-7.6-1.9-10.1s-1.3-5.5-2.6-9.1 c-1.3-3.5-2.7-6.3-4.4-8.4c-1.7-2-4-3.8-6.8-5.3s-6.1-2.2-9.7-2.2H36.2c-5.9,0-11,2.2-15.4,6.5c-4.3,4.3-6.5,9.4-6.5,15.4 c0,5.9,2.2,11,6.5,15.4s9.4,6.5,15.4,6.5h69.7l60.4,281.1c-0.5,0.9-2.8,5.2-7,13c-4.2,7.7-7.6,14.5-10.1,20.3s-3.8,10.3-3.8,13.5 c0,5.9,2.2,11,6.5,15.4c4.3,4.3,9.4,6.5,15.4,6.5h21.9h306h21.9c5.9,0,11-2.2,15.4-6.5c4.3-4.3,6.5-9.4,6.5-15.4 c0-5.9-2.2-11-6.5-15.4c-4.3-4.3-9.4-6.5-15.4-6.5H202.9c5.5-10.9,8.2-18.2,8.2-21.9c0-2.3-0.3-4.8-0.9-7.5c-0.6-2.7-1.3-5.7-2-9.1 c-0.8-3.3-1.3-5.7-1.5-7.3l356.5-41.7c5.7-0.7,10.4-3.1,14-7.3c3.6-4.2,5.5-9.1,5.5-14.5V105.9C582.6,100,580.5,94.9,576.1,90.6z M546,221.3h-48.3l-8.8-44.3H546V221.3z M167.8,177h45.3l8.8,44.3H178L167.8,177z M231.5,176.9h50.1l8.8,44.3h-50.1L231.5,176.9z M405.8,163.9h-46.3L352,126h46.3L405.8,163.9z M416.6,126h43.8l7.5,37.9h-43.8L416.6,126z M362.1,176.9h46.3l8.8,44.3h-46.3 L362.1,176.9z M352.6,221.3h-43.8L300,177h43.8L352.6,221.3z M341.2,163.9h-43.8l-7.5-37.9h43.8L341.2,163.9z M279,163.9h-50.1 l-7.6-37.9h50.1L279,163.9z M293,234.3l11.5,57.6l-49.1,5.1l-12.5-62.7H293z M311.4,234.3h43.8l10.2,51.4l-42.9,4.4L311.4,234.3z M373.5,234.3h46.3l8.9,44.8l-45.4,4.7L373.5,234.3z M438.2,234.3H482l7.7,38.5l-42.9,4.4L438.2,234.3z M435.6,221.3l-8.8-44.3 h43.8l8.8,44.3H435.6z M546,163.9h-59.7l-7.5-37.9H546V163.9z M203,126l7.6,37.9h-45.7l-8.8-38H203V126z M197,303l-15.9-68.7h43.4 l12.8,64.6L197,303z M507.6,271l-7.3-36.7H546V267L507.6,271z"/>
	                                			</g>
	                                			</svg>
	                                		</div>
	                                   		<span id="cartItems" class="block fl"><dsp:include page="/cart/cart_item_count.jsp"/></span> <!-- <bbbl:label key="lbl_orderitems_items" language="${pageContext.request.locale.language}" /> -->
	                                    </c:otherwise>
                                     </c:choose>    
	                               </dsp:a>
	                            </div>
	                            <div id="shoppingCartCheckout">
	                                <dsp:a rel="nofollow" page="/cart/cart.jsp"><bbbl:label key="lbl_checkout_cart" language="${pageContext.request.locale.language}" /></dsp:a>
	                            </div>
	                        </div>
	                    </div>
	                    <div id="shoppingCartArrow"></div>
	                </div>                
	                </c:otherwise>
	                </c:choose>
	            </div>
        </div>
		
		<c:if test="${fn:contains(pageWrapper,'myAccount')}">
			<div class="fbPermissionBox hidden">
				<span class="fbDeclinedPermission bold"></span>
				<bbbt:textArea key="txt_fb_permission_content"
							language="${pageContext.request.locale.language}" />
			</div>
		</c:if>
		
        <div class="clear"></div>
        <c:set var="babyCAVar" value=""></c:set>
		<c:if test="${sessionBabyCA eq 'true' && currentSiteId == BedBathCanadaSite}">
			<c:set var="babyCAVar" value="_Baby_CA"/>
		</c:if>
        <div id="mainHeader">
			<div id="psFlyout">
				<c:import url="/personalstore/personalStoreFlyout.jsp" />
			</div>
            <div class="container_12 clearfix">
                <div id="headerInformation" class="grid_12">
                    <c:set var="gridClass" value="grid_3" />
                    <c:set var="gridClassPromo" value="grid_5" />
                   <c:if test="${currentSiteId == BuyBuyBabySite}">
                        <c:set var="gridClass" value="grid_2" />
                        <c:set var="gridClassPromo" value="grid_10" />
                    </c:if> 
                    <c:if test="${currentSiteId == BedBathCanadaSite}">
                        <c:set var="gridClass" value="grid_5" />
                        <c:set var="gridClassPromo" value="grid_8" />
                    </c:if>

                        <div class="${gridClass} alpha">
                        <div id="siteLogo" <c:if test="${currentSiteId == BedBathUSSite}">class="storeLogo" </c:if> itemscope itemtype="http://schema.org/Organization">
                            <c:set var="logoWidth" value="173" />
                            <c:if test="${currentSiteId == BedBathCanadaSite}">
                                <c:set var="logoCountry" value="_ca" />
                                <c:set var="logoWidth" value="200" />
                            </c:if>
                            <c:choose>
                                <c:when test="${currentSiteId == BedBathCanadaSite || currentSiteId == BedBathUSSite}">
                                    <c:choose>
                                        <c:when test="${themeName == 'br'}">
                                            <%-- Start: Added for Scope # 81 H1 tags --%>
                                            <c:if test="${homepage eq 'homepage'}">
                                                <h1 class="txtOffScreen"><bbbl:label key="lbl_findstore_bed_beyond" language="${pageContext.request.locale.language}" /> - <bbbl:label key="lbl_wedding_invites" language="${pageContext.request.locale.language}" /> &amp; <bbbl:label key="lbl_pdp_accessories" language="${pageContext.request.locale.language}" /></h1>
                                            </c:if>
                                            <%-- End: Added for Scope # 81 H1 tags --%>
                                            <a itemprop="url" href="/" title="Bed Bath &amp; Beyond - Wedding Invitations &amp; Accessories">
                                                <img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_br${logoCountry}.png" onload="BBB.addPerfMark('ux-destination-verified');"
                                                	height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond - Wedding Invitations &amp; Accessories" />
                                                <script>BBB.addPerfMark('ux-destination-verified');</script>
                                            </a>
                                            
                                            <%-- Baby Canada --%>
                                           
                                            <c:if test="${currentSiteId == BedBathCanadaSite}">
                                            	<a href="//<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="logoBabyCA">
                                                	<img src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" onload="BBB.addPerfMark('ux-destination-verified');" height="50" width="121" alt="Buy Buy Baby" />
                                                    <script>BBB.addPerfMark('ux-destination-verified');</script>
                                                </a>
                                            </c:if>
                                            <%-- Baby Canada --%>
                                        </c:when>
                                        <c:when test="${themeName == 'bc'}">
                                            <c:choose>
                                                <c:when test="${currentSiteId == BedBathCanadaSite}">
                                                    <%-- Start: Added for Scope # 81 H1 tags --%>
                                                    <c:if test="${homepage eq 'homepage'}">
                                                        <h1 class="txtOffScreen"><bbbl:label key="lbl_findstore_bed_beyond" language="${pageContext.request.locale.language}" /> - <bbbl:label key="lbl_we_love_university" language="${pageContext.request.locale.language}" /></h1>
                                                    </c:if>
                                                    <%-- End: Added for Scope # 81 H1 tags --%>
                                                    <a itemprop="url" href="/" title="Bed Bath &amp; Beyond - We Love University">
                                                        <img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" onload="BBB.addPerfMark('ux-destination-verified');" height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond - We Love University" />
                                                    	<script>BBB.addPerfMark('ux-destination-verified');</script>
                                                    </a>
                                                    
                                                    <%-- Baby Canada --%>
                                                  
		                                            <c:if test="${currentSiteId == BedBathCanadaSite}">
		                                            	<a href="//<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="logoBabyCA">
                                                            <img src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" onload="BBB.addPerfMark('ux-destination-verified');" height="50" width="121" alt="Buy Buy Baby" />
                                                        	<script>BBB.addPerfMark('ux-destination-verified');</script>
                                                        </a>
		                                            </c:if>
		                                            <%-- Baby Canada --%>
                                                </c:when>
                                                <c:otherwise>
                                                    <%-- Start: Added for Scope # 81 H1 tags --%>
                                                    <c:if test="${homepage eq 'homepage'}">
                                                        <h1 class="txtOffScreen"><bbbl:label key="lbl_findstore_bed_beyond" language="${pageContext.request.locale.language}" /> - <bbbl:label key="lbl_we_love_college" language="${pageContext.request.locale.language}" /></h1>
                                                    </c:if>
                                                    <%-- End: Added for Scope # 81 H1 tags --%>
                                                    <a itemprop="url" href="/" title="Bed Bath &amp; Beyond - We Love College">
                                                        <img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" onload="BBB.addPerfMark('ux-destination-verified');" height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond - We Love College" />
                                                    	<script>BBB.addPerfMark('ux-destination-verified');</script>
		                                            </a>
                                                    
                                                    <%-- Baby Canada --%>
                                                    
		                                            <c:if test="${currentSiteId == BedBathCanadaSite}">
		                                            	<a href="//<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="logoBabyCA">
                                                            <img src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" onload="BBB.addPerfMark('ux-destination-verified');" height="50" width="121" alt="Buy Buy Baby" />
                                                        	<script>BBB.addPerfMark('ux-destination-verified');</script>
		                                                </a>
		                                            </c:if>
		                                            <%-- Baby Canada --%>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <%-- Start: Added for Scope # 81 H1 tags --%>
                                            <c:if test="${homepage eq 'homepage'}">
                                                <h1 class="txtOffScreen"><bbbl:label key="lbl_findstore_bed_beyond" language="${pageContext.request.locale.language}" /></h1>
                                            </c:if>
                                            <%-- End: Added for Scope # 81 H1 tags --%>
                                            <a itemprop="url" href="/" title="Bed Bath &amp; Beyond">
                                                <img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" onload="BBB.addPerfMark('ux-destination-verified');" height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond" />
                                            	<script>BBB.addPerfMark('ux-destination-verified');</script>
		                                    </a>
                                          	
                                            <%-- Baby Canada --%>
                                            <c:if test="${currentSiteId == BedBathCanadaSite}">
                                            	<a href="//<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="logoBabyCA">
                                                	<img src="${imagePath}/_assets/global/images/logo/logo_baby${logoCountry}.png" onload="BBB.addPerfMark('ux-destination-verified');" height="50" width="121" alt="Buy Buy Baby" />
                                                	<script>BBB.addPerfMark('ux-destination-verified');</script>
		                                        </a>
                                            </c:if>
                                            <%-- Baby Canada --%>
                                           
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <%-- Start: Added for Scope # 81 H1 tags --%>
                                    <c:if test="${homepage eq 'homepage'}">
                                        <h1 class="txtOffScreen">buybuy BABY</h1>
                                    </c:if>
                                    <%-- End: Added for Scope # 81 H1 tags --%>
                                    <a itemprop="url" href="/" title="buybuy BABY">
                                        <img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_bb_circle.png" onload="BBB.addPerfMark('ux-destination-verified');" height="122" width="127" alt="buybuy BABY" />
                                        <script>BBB.addPerfMark('ux-destination-verified');</script>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <jsp:useBean id="placeHolderMapFreeShip" class="java.util.HashMap" scope="request"/>
                    <c:set target="${placeHolderMapFreeShip}" property="themeName">${themeName}</c:set>
                    <c:set var="txt_freeshipping_header_img"><bbbt:textArea key="txt_freeshipping_header_img" placeHolderMap="${placeHolderMapFreeShip}" language ="${pageContext.request.locale.language}"/></c:set>
                    <input type="hidden" name="akamaiCachingOn" class="akamaiCachingOn" value="${homePageCachingOn}" />
                   <div class="${gridClassPromo} alpha omega intlFreeShip <c:if test='${homePageCachingOn eq true}'>hidden</c:if>">
                   		<div id="promoArea"  <c:if test="${(empty txt_freeshipping_header_img) || (homepage eq 'homepage')}">class="promoMargin"</c:if>>
							<c:if test="${homepage ne 'homepage' }"> ${txt_freeshipping_header_img} </c:if>
                            <div class="clear"></div>
                            <c:choose>
                                <c:when test="${isInternationalCustomer ne true}">
							<bbbt:textArea key="txt_bopus_header_img"  language ="${pageContext.request.locale.language}"/>
							    </c:when>
							     <c:otherwise>
							         <bbbt:textArea key="txt_bopus_header_img_intl"  language ="${pageContext.request.locale.language}"/>
							     </c:otherwise>
                            </c:choose>
	                    </div>
                        <c:if test="${currentSiteId ne BedBathCanadaSite}">
                	    <div id="linkArea" class="fr <c:if test="${isInternationalCustomer}">international</c:if>">
                       		<ul class="subNav2 active">
                                
                                <c:if test="${enableFindAStore}">
	                            <c:choose>
	                                <c:when test="${currentSiteId == BedBathCanadaSite}">
	                                    <li class="findStore"><dsp:a href="${contextPath}/selfservice/CanadaStoreLocator">
	                                    <span class="icon icon-store-icon marRight_5" aria-hidden="true"></span><bbbl:label key="lbl_header_canadafindastore" language="${pageContext.request.locale.language}" /></dsp:a>
	                                    </li>                        
	                                </c:when>
	                                <c:otherwise>
			                          <c:if test="${FindAStoreOn}">
	                                    <li class="findStore"><a href="${contextPath}/selfservice/FindStore">
	                                     <span class="icon icon-store-icon marRight_5" aria-hidden="true"></span>
	                                    <bbbl:label key="lbl_header_findastore" language="${pageContext.request.locale.language}" /></a>
	                                   </li>
	                                  </c:if>
	                                </c:otherwise>
	                            </c:choose>
	                            </c:if>
	                          <c:if test="${personalStore && enableYouMayLike}">
	                            <li>
	                            <c:choose>
	                            <c:when test="${currentSiteId ne BedBathCanadaSite}">
									<div id="personalStoreLink">
										<a href="${contextPath}/personalstore/"><!-- <bbbl:label key="lbl_personal_feed" language="${pageContext.request.locale.language}" /> -->You May Like...</a>
									</div>
								</c:when>
								<c:otherwise>
								    <div id="personalStoreLink">
										<a href="${contextPath}/personalstore/"><bbbl:label key="lbl_personal_feed" language="${pageContext.request.locale.language}"/></a>
									</div>
								</c:otherwise>
								</c:choose>
									
								</li>
	                            </c:if>
	                            <c:if test="${enableSignUpForOffers}">
	                            <%-- BBBSL-2355 START --%>
	                           	<bbbt:textArea key="txt_email_sign_up_link"  language ="${pageContext.request.locale.language}"/>
	                          	<%-- BBBSL-2355 END --%>
	                            </c:if>
	                            
	                            <c:if test="${enableTrackOrder}">
	                            <li>
	                            
								<c:choose>
	                            	<c:when test="${!isLoggedIn and requestScope.recognizedUserFlag eq false}">
	                            		<a href="${contextPath}/account/order_summary.jsp">
	                            	</c:when>
	                            	<c:otherwise>
										<a href="${contextPath}/account/TrackOrder">
									</c:otherwise>
	                            </c:choose>
								<bbbl:label key="lbl_header_trackorder" language="${pageContext.request.locale.language}" /></a></li>
								</c:if>
	                            <c:if test="${ContactUsOn && enableContactUs}">
	                            <li><a href="${contextPath}/selfservice/ContactUs"><bbbl:label key="lbl_header_contactus" language="${pageContext.request.locale.language}" /></a></li>
	                            </c:if>
	                            <c:if test="${enableGiftCards}">
	                            <li><a href="${contextPath}/static/GiftCardHomePage"><bbbl:label key="lbl_header_giftcards" language="${pageContext.request.locale.language}" /></a></li>
	                            </c:if>
	                            <c:if test="${internationalShippingOn && pageName != 'guestLogin' && enableShipTo}">
                       
		                         <li>
	                                <a href='#' id="shipToLink" title="International Shipping"> <bbbl:label key="lbl_intl_shipto_topmenu" language="${language}"/>  <span class="flag flag${countryCodeLowerCase}"></span> </a>
	                            </li>
	                            </c:if>
	                            </ul>
	                            </div>
	                            </c:if>

                    </div>                  
                    <c:set var="gridClass" value="grid_4" />
                    <%-- <c:if test="${currentSiteId == BuyBuyBabySite}">
                        <c:set var="gridClass" value="grid_4 alpha fl" />
                    </c:if> --%>
                    
                    <c:choose>
	                    <c:when test="${currentSiteId == BedBathCanadaSite}">
	                    	<a id="department-navigationCA" name="content"></a>
	                    	<div class="collegeBridalAreaCA<c:if test="${!(empty promoBoxContentVO)}"> enableTrend</c:if>" id="collegeBridalArea">
	                    </c:when>
						<c:when test="${currentSiteId == BedBathUSSite}">
	                     	<div <c:if test="${!(empty promoBoxContentVO)}">class="enableTrend"</c:if> id="collegeBridalArea">
							<style>
                             #headerWrapper #collegeBridalArea .shopLinkPanel ul.menuListDesc li a {font-size:12px; line-height:14px;}
                            </style>
                            <style>
                             #headerWrapper #collegeBridalArea .shopNavHoverPanel .shopNavLinks>li {margin-bottom:0;}
							#headerWrapper #collegeBridalArea .shopNavHoverPanel .shopNavLinks>li>a {text-transform: none; padding: 3px 8px 3px 26px;}
							#headerWrapper #collegeBridalArea .shopNavHoverPanel .shopNavLinks>li>hr {margin:8px;}
							</style>
	                    </c:when>
	                    <c:otherwise>
	                    	<a id="department-navigation" name="content"></a>
	                     	<div <c:if test="${!(empty promoBoxContentVO)}">class="enableTrend"</c:if> id="collegeBridalArea">
							<style>
							#headerWrapper #collegeBridalArea .shopLinkPanel ul.menuListDesc li a {font-size:12px; line-height:14px;}
							</style>
	                     </c:otherwise>
                    </c:choose>
							<div class="shopLink">
                        	<a href="#" title="shop products"><bbbl:label key="lbl_menu_content_header_shopProducts" language="${pageContext.request.locale.language}" /><span class="visuallyhidden">Opens a flyout</span></a>
            <%--<div class="shopLink" style="color: rgb(255, 255, 255);"><bbbl:label key="lbl_menu_content_header_shopProducts" language="${pageContext.request.locale.language}" /></div> --%>
                            <div class="shopNavHoverPanel">
                                <ul class="shopNavLinks">
                                <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
                                <dsp:getvalueof id="catalogId" param="CatalogId" />
                                <c:choose>
                                  <c:when test="${isStagingServer}">
                                    <dsp:include page="/_includes/header/L1_header_top_nav.jsp">
                                      <dsp:param name="currentSiteId" value="${currentSiteId}"/>
                                      <dsp:param name="contextPath" value="${contextPath}"/>
                                    </dsp:include>
                                  </c:when>
                                  <c:otherwise>
                                    <dsp:droplet name="/atg/dynamo/droplet/Cache">
                                      <dsp:param name="key" value="TopNavL1_${currentSiteId}${babyCAVar}" />
                                      <dsp:param name="cacheCheckSeconds" value="${flyoutCacheTimeout}"/>
                                      <dsp:oparam name="output">
                                        <dsp:include page="/_includes/header/L1_header_top_nav.jsp">
                                          <dsp:param name="currentSiteId" value="${currentSiteId}"/>
                                          <dsp:param name="contextPath" value="${contextPath}"/>
                                        </dsp:include>
                                      </dsp:oparam>
                                    </dsp:droplet>
                                  </c:otherwise>
                                </c:choose>
                                <dsp:getvalueof var="key" value="TopNav_${currentSiteId}${babyCAVar}" />
                                <dsp:getvalueof var="cacheCheckSeconds" value="${flyoutCacheTimeout}"/>                                
                                </ul>
                			</div>				
                		</div><!-- .shopLink -->
                		<c:if test="${!(empty promoBoxContentVO)}">
                        
           	               <script type="text/javascript" src="${catJSFile}"></script>
 						     <link rel="stylesheet" type="text/css" href="${catCSSFile}" />
 						      <dsp:valueof value="${promoContent}" valueishtml="true"/>
 						
            	         </c:if>                  
                        <c:if test="${currentSiteId eq BedBathUSSite}">
                        <%-- Start: Added for Scope # 81 H1 tags --%>
                        <c:if test="${PageType eq 'CollegeLandingPage'}">
                        <h1 class="txtOffScreen"><bbbl:label key="lbl_header_shopforcollege" language="${pageContext.request.locale.language}" /></h1>
                        </c:if>
                        <%-- End: Added for Scope # 81 H1 tags --%>
                        
                        
                        <div id="shopForCollegeLink">
                        	<a href="/store/page/College"><bbbl:label key="lbl_header_shopforcollege" language="${pageContext.request.locale.language}" /><span class="visuallyhidden">Opens a flyout</span></a>
                            <div id="shopForCollegelFlyout">
                            	<div class="green-arrow"></div>
                            	<c:choose>
                            	<c:when test="${enableLazyLoadCollegeFlyout eq true}">
                                <div class="textCenter">
				                		<img width="20" height="20" alt="small loader" class="loader" src="/_assets/global/images/widgets/small_loader.gif" />
				                 </div>
                            	</c:when>
                            	<c:otherwise>
                                	 <c:import url="/bbcollege/shop_for_college.jsp" />
				                 </c:otherwise>
				                 </c:choose>
                            </div>
                        </div><!--#shopForCollegeLink-->
                        </c:if>
                        <c:if test="${currentSiteId eq BedBathCanadaSite}">
                        <%-- Start: Added for Scope # 81 H1 tags --%>
                        <c:if test="${PageType eq 'CollegeLandingPage'}">
                        <h1 class="txtOffScreen"><bbbl:label key="lbl_header_shopforcollege" language="${pageContext.request.locale.language}" /></h1>
                        </c:if>
                          <%-- End: Added for Scope # 81 H1 tags --%>
                          <div id="shopForCollegeLink" class="shopForCollegeLinkCA"> <a href="/store/page/College">
                            <bbbl:label key="lbl_header_shopforcollege" language="${pageContext.request.locale.language}" /><span class="visuallyhidden">Opens a flyout</span>
                            </a>
                            <c:choose>
                              <c:when test="${babyCAMode == 'true'}">
                                <div id="shopForCollegelFlyout">
                                  <div class="green-arrow"></div>
                                  <c:choose>
                            			<c:when test="${enableLazyLoadCollegeFlyout eq true}">
                                  <div class="textCenter">
				                		<img width="20" height="20" alt="small loader" class="loader" src="/_assets/global/images/widgets/small_loader.gif" />
				                 </div>
                            		   </c:when>
                            	 <c:otherwise>
                                	 <c:import url="/bbcollege/shop_for_college.jsp" />
				                 </c:otherwise>
				                 </c:choose>
                                </div>
                              </c:when>
                              <c:otherwise>
                                <div id="shopForCollegelFlyout">
                                  <div class="green-arrow"></div>
                                  <c:choose>
                            	<c:when test="${enableLazyLoadCollegeFlyout eq true}">
                                  <div class="textCenter">
				                		<img width="20" height="20" alt="small loader" class="loader" src="/_assets/global/images/widgets/small_loader.gif" />
				                 </div>
                            	</c:when>
                            	<c:otherwise>
                                	 <c:import url="/bbcollege/shop_for_college.jsp" />
				                 </c:otherwise>
				                 </c:choose>
                                </div>
                              </c:otherwise>
                            </c:choose>
                          </div><!--#shopForCollegeLink--> 
                        </c:if>

                            
                            <c:if test="${currentSiteId == BuyBuyBabySite}">
                                <div id="bridalGiftRegistryLink">
                                    <a id="bridalGiftRegistryAnchor" href="/store/page/BabyRegistry"><bbbl:label key="lbl_reg_feature_baby_reg" language="${pageContext.request.locale.language}" /><span class="visuallyhidden">Opens a flyout</span></a>
                                    <div id="bridalGiftRegistryFlyout">
                            <c:choose>
                                 <c:when test="${homePageCachingOn eq true}">
                                      <div class="clearfix loader textCenter"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                                 </c:when>
                                 <c:otherwise>
                                     <c:import url="/giftregistry/reg_flyout.jsp" />
                                 </c:otherwise>
                            </c:choose>
                              </div>
                                </div><!--#bridalGiftRegistryLink-->	                         
                            </c:if>                       
                            <c:if test="${currentSiteId eq BedBathUSSite}">
                                <div id="bridalGiftRegistryLink">
                                    <a id="bridalGiftRegistryAnchor" href="/store/page/Registry"><bbbl:label key="lbl_header_bridalgiftregistry" language="${pageContext.request.locale.language}" /><span class="visuallyhidden">Opens a flyout</span></a>
                                    <div id="bridalGiftRegistryFlyout">
                            <c:choose>
                                 <c:when test="${homePageCachingOn eq true}">
                                      <div class="clearfix loader textCenter"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                                 </c:when>
                                 <c:otherwise>
                                     <c:import url="/giftregistry/reg_flyout.jsp" />
                                 </c:otherwise>
                            </c:choose>
                              </div>
                                </div><!--#bridalGiftRegistryLink-->                        
                                
                            </c:if>
                            <c:if test="${currentSiteId eq BedBathCanadaSite}">
                                <c:choose>
                                    <c:when test="${babyCAMode == 'true'}">
                                        <div id="bridalGiftRegistryLink">
                                            <a id="bridalGiftRegistryAnchor" href="/store/page/BabyCanadaRegistry"><bbbl:label key="lbl_header_bridalgiftregistry_ca" language="${pageContext.request.locale.language}" /><span class="visuallyhidden">Opens a flyout</span></a>
                                        <div id="bridalGiftRegistryFlyout">
                            <c:choose>
                                 <c:when test="${homePageCachingOn eq true}">
                                      <div class="clearfix loader textCenter"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                                 </c:when>
                                 <c:otherwise>
                                     <c:import url="/giftregistry/reg_flyout.jsp" />
                                 </c:otherwise>
                            </c:choose>
                              </div>
                                        </div><!--#bridalGiftRegistryLink-->                        			
                                        
                                    </c:when>
                                <c:otherwise>
                                        <div id="bridalGiftRegistryLink" class="bridalGiftRegistryLinkCA">
                                            <a id="bridalGiftRegistryAnchor" href="/store/page/Registry"><bbbl:label key="lbl_header_bridalgiftregistry_ca" language="${pageContext.request.locale.language}" /><span class="visuallyhidden">Opens a flyout</span></a>
                                            <div id="bridalGiftRegistryFlyout">
                            <c:choose>
                                 <c:when test="${homePageCachingOn eq true}">
                                      <div class="clearfix loader textCenter"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                                 </c:when>
                                 <c:otherwise>
                                     <c:import url="/giftregistry/reg_flyout.jsp" />
                                 </c:otherwise>
                            </c:choose>
                              </div>
                            </div><!--#bridalGiftRegistryLink-->
                                
                                <div id="shopForCollegelFlyout">
                                    <div class="green-arrow"></div>
                                    <c:choose>
                            	<c:when test="${enableLazyLoadCollegeFlyout eq true}">
                                    <div class="textCenter">
				                		<img width="20" height="20" alt="small loader" class="loader" src="/_assets/global/images/widgets/small_loader.gif" />
				                 </div>
                            	</c:when>
                            	<c:otherwise>
                                	 <c:import url="/bbcollege/shop_for_college.jsp" />
				                 </c:otherwise>
				                 </c:choose>
                                </div>
            		</c:otherwise>
        		</c:choose>
    
                            </c:if>                      
                        
                    </div><!--#collegeBridalArea-->
                    
                    <div id="searchWrapper" class="grid_3 alpha omega<c:if test="${!(empty promoBoxContentVO)}"> enableTrend</c:if>">
                        <div id="searchForm" role="search">
							<c:set var="guide" ><bbbl:label key="lbl_header_search_box_guiding_text" language="${pageContext.request.locale.language}" /></c:set>
                            <form method="post" action="#" id="globalSearchForm">
                            	<input id="siteSpectBrand_TypeAHead_Size" type="hidden" value=""  />
								<input id="siteSpectPopular_TypeAHead_Size" type="hidden" value=""/>
								<input id="siteSpectDepartment_TypeAHead_Size" type="hidden" value=""/>
								<input id="siteSpectSearchInDepartment_TypeAHead_Size" type="hidden" value=""/>

                            	<input type="hidden" value="${contextPath}/_includes/header/searchFlyout.jsp" name="ajaxServiceURL" />

                                <%-- Search Within Department begins --%>
								<dsp:droplet name="/atg/dynamo/droplet/Cache">
								   	<dsp:param name="key" value="SearchDropDown_${currentSiteId}${babyCAVar}" />
								   	<dsp:param name="cacheCheckSeconds" value="${flyoutCacheTimeout}"/>
								   	<dsp:oparam name="output">
								<dsp:droplet name="SearchDroplet">
								<dsp:param name="CatalogId" value="0"/>
								<dsp:param name="CatalogRefId" value="10000"/>
								<dsp:param name="isHeader" value="Y"/>
								<dsp:param name ="appendProdDimId" value="Y"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="browseVO" param="browseSearchVO.facets"/>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="browseSearchVO.facets" name="array" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="facetName" param="element.name" />
										<c:if test="${facetName == 'DEPARTMENT'}">
											<c:set var="isFirstOption" value="${true}"/>
											<span id="defaultAllValue" class="hidden"><bbbl:label key="lbl_link_all_categories" language="${pageContext.request.locale.language}" /></span>
											<span id="lblsearchByDept" class="visuallyhidden" aria-hidden='false'><bbbl:label key="lbl_search_by_dept" language="${pageContext.request.locale.language}" /></span>
											<select id="searchByDept" class="hidden">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element.facetRefinement" name="array" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="categoryId" param="element.catalogId"/>
												<c:if test="${isFirstOption}">
													<c:set var="isFirstOption" value="${false}"/>
													<option value="0"><bbbl:label key="lbl_all_departments" language="${pageContext.request.locale.language}" /></option>
												</c:if>
												<option value="${categoryId}"><dsp:valueof param="element.name" /></option>
											</dsp:oparam>
											</dsp:droplet>
											</select>
										</c:if>
									</dsp:oparam>
									</dsp:droplet>
									<dsp:getvalueof var="pageSize" param="pageSize"/>
									<dsp:getvalueof var="dimensionIds" param="dimensionIds"/>
									<input type="hidden" name="pageSize" value="${pageSize}"/>
									<input type="hidden" name="dimensionIds" value="${dimensionIds}"/>
								</dsp:oparam>
								</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
                                <%-- Search Within Department ends --%>
                                
                                <div class="searchDD"><bbbl:label key="lbl_mng_regitem_all" language="${pageContext.request.locale.language}" /></div>
                                
                                <%-- R2.2 SEO Friendly URL changes --%>
                                <input type="hidden" name="searchURL" value="${contextPath}/s/"/>
                                <input type="hidden" name="vendorParam" value="${vendorParam}"/>
           
                                <div class="searchFormInputWrapper">	
                                    <label id="lblsearchFormInput" class="txtOffScreen" for="searchFormInput"><bbbl:label key="lbl_header_search_products" language="${pageContext.request.locale.language}" /></label>
                                	<input id="searchFormInput" role="textbox" aria-labelledby="lblsearchFormInput" aria-owns="searchFormResults" type="text" value="" title="<bbbl:label key="lbl_header_search_products" language="${pageContext.request.locale.language}" />" name="searchBox" placeholder="${guide}" autocomplete="off" autocorrect="off" aria-haspopup="true" aria-autocomplete="none" />
                                </div>
								<div class="searchFormButtonWrapper fr">
                                	<button type="submit" class="searchFormButton" value="submit your search" name="submit your search" role="button" title="Search" style="padding:8px">
  										<span class="icon icon-search block" aria-hidden="true"></span> 
										<span class="icon-text"><bbbl:label key="lbl_submit_search" language="${pageContext.request.locale.language}" /></span>
									</button> 
                                </div>
							</form>
							<style>
								#headerWrapper .searchFormButton:focus
								{
  									border:white dotted thin !important;
								}
							</style>
							<form method="post" action="#" id="hiddenForm">
								<input type="hidden" name="origSearchTerm" value="" />
								<input type="hidden" name="deptValue" value="" />
								<input type="submit" class="hidden" value=" " />
							</form>
						</div>
                        <div id="searchFormResults" role="listbox"></div>
                        
                    </div>
                </div>
                <div id="menuWrapper" class="grid_12 clearfix" aria-haspopup="true">
               
               <dsp:getvalueof var="categoryId" param="categoryId"/>
               <dsp:getvalueof var="CatalogId" param="CatalogId"/>
               <c:choose>
                 <c:when test="${not empty categoryId}">
                 <c:set var="catId" value="${categoryId}"/>
                 </c:when>
                 <c:otherwise>
                 <c:set var="catId" value="${CatalogId}"/>
                 </c:otherwise>
               </c:choose>
               <c:if test="${not empty catId}">
               
               <dsp:droplet name="BBBGetRootCategoryDroplet">
                <dsp:param name="childCategory" value="${catId}"/>
	                <dsp:oparam name="output">
	                  <dsp:getvalueof var="rootCategory" param="rootCategory" scope="request"/>
	                </dsp:oparam>
                <dsp:oparam name="empty">
                 </dsp:oparam>
                 <dsp:oparam name="error">
                 </dsp:oparam>
               </dsp:droplet>
               </c:if>
              <!--[if IE]>
         <style type="text/css">
            #headerWrapper #searchFormInput {
            padding: 8px 16px 16px 16px;
            height: 10px;
            }
         </style>
         <![endif]-->
         <div class="tooltipBBB" style="display:none;">
            <div class="tooltip-innerBBB"></div>
            <div class="tooltip-arrowBBB"></div>
         </div>
		 <c:set var="browseSearchVOFacets" value=""/>
         
				
				
            </div>
        </div>
        
         <div id="miniCartWrapper" class="">
            <div id="shoppingCartContent" class="container_12">
                <div id="miniCart" class="clearfix">
                	<div class="textCenter">
                		<img width="20" height="20" alt="small loader" class="loader" src="/_assets/global/images/widgets/small_loader.gif" />
                	</div>
                </div>
                <%-- <c:import url="/cart/mini_cart.jsp" /> --%>
            </div>
        </div>
		
		<c:if test="${newRelicTagON}">
		<dsp:include page="/_includes/header/headerNewRelic.jsp"/>
		</c:if> 
	                    <c:choose>
                                 <c:when test="${homePageCachingOn eq true}">
                                      <div id="registryRibbonHeader" /></div>
                                 </c:when>
                                 <c:otherwise>
                                     	<dsp:include page="/_includes/header/subheader.jsp"> 
									   		<dsp:param name="currentSiteId" value="${currentSiteId}"/>
									   		<dsp:param name="PageType" value="${PageType}"/>
									  </dsp:include>
                                 </c:otherwise>
                            </c:choose>
	  
    </div>
   </div>
       <c:choose>
	                    <c:when test="${currentSiteId == BedBathUSSite}">
	                    <script> !function(e){"use strict";"function"==typeof define&&define.amd?define(["jquery"],e):jQuery&&!jQuery.fn.hoverIntent&&e(jQuery)}(function(e){"use strict";var t,n,o={interval:100,sensitivity:6,timeout:0},i=0,u=function(e){t=e.pageX,n=e.pageY},r=function(e,o,i,v){return Math.sqrt((i.pX-t)*(i.pX-t)+(i.pY-n)*(i.pY-n))<v.sensitivity?(o.off(i.event,u),delete i.timeoutId,i.isActive=!0,e.pageX=t,e.pageY=n,delete i.pX,delete i.pY,v.over.apply(o[0],[e])):(i.pX=t,i.pY=n,i.timeoutId=setTimeout(function(){r(e,o,i,v)},v.interval),void 0)},v=function(e,t,n,o){return delete t.data("hoverIntent")[n.id],o.apply(t[0],[e])};e.fn.hoverIntent=function(t,n,a){var s=i++,d=e.extend({},o);e.isPlainObject(t)?(d=e.extend(d,t),e.isFunction(d.out)||(d.out=d.over)):d=e.isFunction(n)?e.extend(d,{over:t,out:n,selector:a}):e.extend(d,{over:t,out:t,selector:n});var f=function(t){var n=e.extend({},t),o=e(this),i=o.data("hoverIntent");i||o.data("hoverIntent",i={});var a=i[s];a||(i[s]=a={id:s}),a.timeoutId&&(a.timeoutId=clearTimeout(a.timeoutId));var f=a.event="mousemove.hoverIntent.hoverIntent"+s;if("mouseenter"===t.type){if(a.isActive)return;a.pX=n.pageX,a.pY=n.pageY,o.off(f,u).on(f,u),a.timeoutId=setTimeout(function(){r(n,o,a,d)},d.interval)}else{if(!a.isActive)return;o.off(f,u),a.timeoutId=setTimeout(function(){v(n,o,a,d.out)},d.timeout)}};return this.on({"mouseenter.hoverIntent":f,"mouseleave.hoverIntent":f},d.selector)}});</script>

                        <script id="hoverIntent">
                        $(document).ready(function(){


                        $('.shopLink').on('mouseenter', function(){
						$('#collegeBridalArea .shopNavHoverPanel .shopNavLinks > li').off('mouseenter mouseleave');

						//console.dir($('#collegeBridalArea .shopNavHoverPanel .shopNavLinks > li').data('events'));

						$('#collegeBridalArea .shopNavHoverPanel .shopNavLinks > li > a').hoverIntent({
						interval:20, 
						timeout:200, 
						over: function(){ 
						var $t= $(this); 

						if($t.parent().hasClass('') || $t.parent().hasClass('giftL1Node')){

						$t.parent().addClass('shopNavHover').siblings().removeClass("shopNavHover");
						Hsize = $t.next('.shopLinkPanel').css('height', 'auto').height();
						$t.next('.shopLinkPanel').height(Hsize).parents('.shopNavLinks').height(Hsize); 
						}
						else{
             			$t.parent().addClass('shopNavHover').siblings().removeClass("shopNavHover");
             			$t.parents('.shopNavLinks').css('height', 'auto');
						}

						var shopLinkHeight = $("#collegeBridalArea .shopNavHoverPanel .shopNavLinks").css("height", "auto").height();
                        var flyoutPanelHeight = $t.next(".shopLinkPanel").css("height", "auto").height();
                    if (flyoutPanelHeight > shopLinkHeight) {
                        $t.next(".shopLinkPanel").height(flyoutPanelHeight);
                        $("#collegeBridalArea .shopNavHoverPanel .shopNavLinks").height(flyoutPanelHeight);
                    } else {
                        $t.next(".shopLinkPanel").height(shopLinkHeight);
                        $("#collegeBridalArea .shopNavHoverPanel .shopNavLinks").height(shopLinkHeight);
                    }

						},
						out: function(){
						var $t= $(this); 
						$('.shopLinkPanel').hover(function(){
						},
						function(){
						$t.parent().removeClass('shopNavHover');
						});
						}
						});
						});
						});
						</script>
	                    </c:when>

            <c:otherwise>
           </c:otherwise>
      </c:choose>
	                    <script>
                        var otherCats = ["Jewelry", "Luggage", "Pet Care", "Sports Fan", "Audio"];
                        var displayCat = otherCats[Math.floor(Math.random()*otherCats.length)];

                        $(document).ready(function () {
                        var supportsTouch = 'ontouchstart' in window || navigator.msMaxTouchPoints || navigator.userAgent.toLowerCase().match( /windows phone os 7/i );  //check for touch support

                        if (supportsTouch) {
			            $('.shopLink').on('touchstart', function () {
                        $('#shopLinkPanelCat_10007').parent('li').find('a[data-contentid="tabData_10007"]').text(displayCat + ' & More');
                        });
                        }

                    else {
                        $('.shopLink').mouseenter(function() {
				        $('#shopLinkPanelCat_10007').parent('li').find('a[data-contentid="tabData_10007"]').text(displayCat + ' & More');
			            });
                        }    
                        });
                        </script>  

</dsp:page>
