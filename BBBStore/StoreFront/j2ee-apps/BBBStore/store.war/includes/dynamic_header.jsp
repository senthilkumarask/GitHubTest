<%--
    This page will render header when Home page is cached
--%>
<%@ page contentType="text/html; charset=UTF-8" %>

<%-- 
  JSP 2.1 parameter. With trimDirectiveWhitespaces enabled,
  template text containing only blank lines, or white space,
  is removed from the response output.
  
  trimDirectiveWhitespaces doesn't remove all white spaces in a
  HTML page, it is only supposed to remove the blank lines left behind
  by JSP directives (as described here 
  http://java.sun.com/developer/technicalArticles/J2EE/jsp_21/ ) 
  when the HTML is rendered. 
 --%>
<dsp:page>
<%@page trimDirectiveWhitespaces="true"%>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />
<input type="hidden" class="intlCustomer" name="isInternationalCustomer" value="${isInternationalCustomer}" />
<dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
<dsp:getvalueof var ="securityStatus" bean="/atg/userprofiling/Profile.securityStatus"/>
<dsp:getvalueof bean="SessionBean" var="bean"/>
<dsp:droplet name="/com/bbb/commerce/browse/droplet/BBBCreateSDDCookieDroplet">
 <dsp:param name="sessionBean" value="${bean}" />
</dsp:droplet>
<c:if test="${securityStatus eq '2' }">
	<c:set var="recognizedUserFlag" value="true" scope="request" />
	<input id="recognizedUserFlag" class="recognizedUserFlag" type="hidden" value="true"/>
</c:if>

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="pageName" value="${pageNameFB}"/>

<%--PS-63264 | Adding transient profile check to avoid priming of transient users --%>
<dsp:getvalueof var="primeRegistryCompleted" bean="SessionBean.primeRegistryCompleted" />
<dsp:getvalueof var="anonymousUser" bean="/atg/userprofiling/Profile.transient" />
<input type="hidden" id="primeRegistryToBeDone" value="${not anonymousUser && not primeRegistryCompleted}" />

	<dsp:getvalueof var ="securityStatus" bean="/atg/userprofiling/Profile.securityStatus"/>
	<c:if test="${securityStatus eq '2' }">
		<input id="recognizedUserFlag" class="recognizedUserFlag" type="hidden" value="true"/>
		<c:set var="displayNotYou">true</c:set>
	</c:if>
<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
<c:set var="PageType" value="${pageContext.request.getParameter('pageName')}"/>
<c:set var="countryCodeLowerCase">${fn:toLowerCase(valueMap.defaultUserCountryCode)}</c:set>

<c:set var="personalStore">
	<bbbc:config key="PSP_homepageLink" configName="FlagDrivenFunctions" />
</c:set>

<dsp:droplet name="/com/bbb/logging/NetworkInfoDroplet">
    <dsp:oparam name="output">
		<!--googleoff: all-->
		<%--BBBSL-1822 Start added usernetworkinfo inside html comments to avoid being read by search engines--%>
		<!--
        <div id="userNetworkInfo" class="hidden"> 
		<ul> 
			<li id="session_id"><dsp:valueof param="SESSION_ID" /></li> 
			<li id="jvm_name"><dsp:valueof bean="/atg/dynamo/service/IdGenerator.dcPrefix"/>-<dsp:valueof param="JVM_NAME" /></li>
			<li id="time"><dsp:valueof param="TIME" /></li> 
			</ul> 
		</div>
		-->
		<!--googleon: all-->
    </dsp:oparam>
</dsp:droplet>
        <c:set var="BedBathUSSite" scope="request">
			<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BuyBuyBabySite" scope="request">
			<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BedBathCanadaSite" scope="request">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="scene7Path" scope="request">
			<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
		</c:set>
		
		<%--BBBSL-7133|Adding Separate config keys for Find A store  --%>	
		<c:set var="FindAStoreOn" scope="request">
			<bbbc:config key="FindAStoreOn" configName="ContentCatalogKeys" />
		</c:set>
		
		    <c:choose>
			    <c:when test="${currentSiteId eq BedBathUSSite}">
			    	<c:set var="ContactUsOn" scope="request"><tpsw:switch tagName="ContactUsTag_us"/></c:set>
			    	<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_us"/></c:set>
					<c:set var="CouponOn" scope="request"><tpsw:switch tagName="CouponTag_us"/></c:set>
			    	<c:set var="HarteHanksOn" scope="request"><tpsw:switch tagName="HarteHanksTag_us"/></c:set>
			    	<c:set var="KirschOn" scope="request"><tpsw:switch tagName="KirschTag_us"/></c:set>
			    </c:when>
			    <c:when  test="${currentSiteId eq BuyBuyBabySite}">
			    	<c:set var="ContactUsOn" scope="request"><tpsw:switch tagName="ContactUsTag_baby"/></c:set>
			    	<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_baby"/></c:set>
					<c:set var="CouponOn" scope="request"><tpsw:switch tagName="CouponTag_baby"/></c:set>
			    	<c:set var="HarteHanksOn" scope="request"><tpsw:switch tagName="HarteHanksTag_baby"/></c:set>
			    	<c:set var="KirschOn" scope="request"><tpsw:switch tagName="KirschTag_baby"/></c:set>
			    </c:when>
			    <c:otherwise>
			    	<c:set var="ContactUsOn" scope="request"><tpsw:switch tagName="ContactUsTag_ca"/></c:set>
			    	<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_ca"/></c:set>
					<c:set var="CouponOn" scope="request"><tpsw:switch tagName="CouponTag_ca"/></c:set>
			    	<c:set var="HarteHanksOn" scope="request"><tpsw:switch tagName="HarteHanksTag_ca"/></c:set>
			    	<c:set var="KirschOn" scope="request"><tpsw:switch tagName="KirschTag_ca"/></c:set>
			    </c:otherwise>
		    </c:choose>
                        <c:set var="internationalShippingOn"><bbbc:config key="international_shipping_switch" configName="International_Shipping"/></c:set>
                         <c:if test="${internationalShippingOn && pageName != 'guestLogin'}">
                             <li>
                                <a href='#' id="shipToLink" title="International Shipping"> <bbbl:label key="lbl_intl_shipto_topmenu" language="${language}"/> <span class="flag flag${countryCodeLowerCase}"></span> </a>
                            </li>                           
						</c:if>
                            <c:set var="fetchCouponCount" value="${sessionScope.fetchCouponCount}" />
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
											<c:if test="${empty fetchCouponCount}">
												<dsp:droplet name="GetCouponsDroplet">
													<dsp:param name="EMAIL_ADDR" bean="Profile.email"/>
													<dsp:param name="MOBILE_NUMBER" bean="Profile.mobileNumber"/>
													<dsp:oparam name="output">
													</dsp:oparam>
												</dsp:droplet>
												<c:set var="fetchCouponCount" value="false" scope="session"></c:set>
											</c:if>
											<a href="${contextPath}/account/myaccount.jsp" class="recognizedUser">
											
											<span class="icon icon-user marRight_5" aria-hidden="true"></span>
											
											<!--  <bbbl:label key="lbl_header_myaccount" language="${pageContext.request.locale.language}" /> -->
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
												<dsp:getvalueof var="couponCount" bean="SessionBean.couponCount" />	
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
													<a href="${contextPath}/account/coupons.jsp">
													<bbbl:label key="lbl_header_coupons" language="${pageContext.request.locale.language}" />
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
                                                <li>
	                                                <dsp:a href="${contextPath}/account/frags/logOutHeader.jsp">
	                                                	<bbbl:label key="lbl_header_logout" language="${pageContext.request.locale.language}" />
	                                                </dsp:a>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>
                               </dsp:oparam>
                            </dsp:droplet>
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
                <div class="${gridClassPromo} alpha omega intlFreeShip hidden">
                <dsp:getvalueof bean="SessionBean" var="bean"/>
				 <dsp:getvalueof value="${bean.showSDD}" var="sddFlag"/>
					 
				 <input id="showSDD" type="hidden" value="${sddFlag}" />
				 <c:set var="sddOnHomePage" scope="request">
				<bbbc:config key="sddOnHomePage" configName="SameDayDeliveryKeys" />
				</c:set>
	
				<input type="hidden" value="${sddOnHomePage}" id="onOffSddFlag"/>
              		<div id="promoArea"  <c:if test="${(empty txt_freeshipping_header_img) || (homepage eq 'homepage')}">class="promoMargin"</c:if>>
						<c:if test="${homepage ne 'homepage' }"> ${txt_freeshipping_header_img} </c:if>
                     	<!-- <div class="clear"></div>-->
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
                	  <c:set var="enableFindAStore"><bbbc:config key="enableSlimHeader_FindAStore" configName="FlagDrivenFunctions" /></c:set>
                        <c:set var="enableSignUpForOffers"><bbbc:config key="enableSlimHeader_SignUpForOffers" configName="FlagDrivenFunctions" /></c:set>
                        <c:set var="enableTrackOrder"><bbbc:config key="enableSlimHeader_TrackOrder" configName="FlagDrivenFunctions" /></c:set>
                        <c:set var="enableContactUs"><bbbc:config key="enableSlimHeader_ContactUs" configName="FlagDrivenFunctions" /></c:set>
                        <c:set var="enableGiftCards"><bbbc:config key="enableSlimHeader_GiftCards" configName="FlagDrivenFunctions" /></c:set>
                        <c:set var="enableShipTo"><bbbc:config key="enableSlimHeader_ShipTo" configName="FlagDrivenFunctions" /></c:set>
                        <c:set var="enableYouMayLike"><bbbc:config key="enableSlimHeader_YouMayLike" configName="FlagDrivenFunctions" /></c:set>
                        
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
                <c:if test="${not empty PageType}">
								<c:set var="AdBlockerFlag"><bbbc:config key="AdBlockerFlag" configName="FlagDrivenFunctions" /></c:set>
								<c:if test="${AdBlockerFlag}">																	
									<div class="ad-block-msg-wrapper" style="display:none;">
									<bbbt:textArea key="txt_ad_blocker_msg"  language ="${pageContext.request.locale.language}"/>
									</div>
								</c:if>															
				</c:if>

				  <dsp:include page="../_includes/header/subheader.jsp"> 
				   		<dsp:param name="currentSiteId" value="${currentSiteId}"/>
				   		<dsp:param name="PageType" value="${PageType}"/>
				  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/includes/pageStart.jsp#2 $$Change: 635969 $--%>
