<dsp:page>
    <dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>
    <dsp:importbean var="SubscriptionFormHandler" bean="/com/bbb/selfservice/SubscriptionFormHandler"/>
    <dsp:importbean var="CurrentDate" bean="/atg/dynamo/service/CurrentDate"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <c:set var="footerCacheTimeout"><bbbc:config key="footerCacheTimeout" configName="HTMLCacheKeys" /></c:set>
    <dsp:importbean bean="/com/bbb/internationalshipping/droplet/InternationalShippingContextSetterDroplet"/>
    <dsp:importbean bean="/com/bbb/cms/droplet/WebFooterLinkTemplateDroplet"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:getvalueof var="homepage" param="homepage"/>
    <dsp:getvalueof var="PageType" param="PageType"/>
    
    <c:set var="sameDayDeliveryFlag">
		<bbbc:config key="SameDayDeliveryFlag" configName="SameDayDeliveryKeys" />
	</c:set>
	<c:set var="sddOnHomePage" scope="request">
		<bbbc:config key="sddOnHomePage" configName="SameDayDeliveryKeys" />
	</c:set>
    	<%-- adding this flag for International Shipping switch--%>
	<c:set var="internationalShippingOn" scope="request"><bbbc:config key="international_shipping_switch" configName="International_Shipping"/></c:set>
	<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />
	<c:set var="homePageCachingOn">
		<bbbc:config key="HOME_PAGE_CACHING_ON_OFF" configName="FlagDrivenFunctions" />
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
	<c:set var="newRelicTagON">
		<bbbc:config key="newRelicTag" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="isLoggedIn" bean="/atg/userprofiling/Profile.transient"/>
	<dsp:getvalueof bean="SessionBean" var="bean"/>
     <c:choose>
      <c:when test="${empty bean.reqFromHomepage &&  fn:contains(PageType,'HomePage')}">
      	<dsp:setvalue bean="SessionBean.reqFromHomepage" value="true"/>
      </c:when>
      <c:otherwise>
     	<dsp:setvalue bean="SessionBean.reqFromHomepage" value="false"/>
      </c:otherwise>
     </c:choose>

	<dsp:droplet name="/atg/dynamo/droplet/Cache">
  	<dsp:param name="key" value="footor_${currentSiteId}" />
  	<dsp:param name="cacheCheckSeconds" value="${footerCacheTimeout}"/>
  	<dsp:oparam name="output">
	  	
    <div class="clear"></div>
	<div id="box"><img style="padding:6px;" alt="up-arrow" src="//s7d9.scene7.com/is/image/BedBathandBeyond/arrow_up?$PNG$&fmt=png-alpha" width="13" height="8"></div>
    <div id="footer" class="clearfix  <c:if test="${!YourAmigoON}">yaOff</c:if>" role="contentinfo">
        <bbbt:textArea key="txt_personalization_tooltip" language ="${pageContext.request.locale.language}"/>
        <bbbt:textArea key="txt_reg_dsl_tool_tip" language ="${pageContext.request.locale.language}"/>
        <div class="topBorder"></div>
        <div class="hidden"><a href="${contextPath}/_includes/modals/sameDayDeliveryModal.jsp" id="sddModal">Same Day Delivery</a><a href="${contextPath}/_includes/modals/sameDayDeliveryCongratulationsModal.jsp" id="sddCongratModal">Congrat Modal</a></div> 
        <div class="container_12 padBottom_20 clearfix">
            <div class="fl borderRight divSignupSpecialEmail">
                <div class="box grid_2 alpha clearfix">
	
               	<c:if test="${EmailOn}">
                    <bbbt:textArea key="txt_footer_signup_specialoffers" language ="${pageContext.request.locale.language}"/>
					<c:choose>
						<c:when test="${currentSiteId eq BedBathUSSite}">
							<c:set var="emailSignupURL"><bbbc:config key="emailSignupURL_US" configName="ThirdPartyURLs" /></c:set>
						</c:when>
						<c:when test="${currentSiteId eq BuyBuyBabySite}">
							<c:set var="emailSignupURL"><bbbc:config key="emailSignupURL_Baby" configName="ThirdPartyURLs" /></c:set>
						</c:when>
						<c:when test="${currentSiteId eq BedBathCanadaSite}">
							<c:set var="emailSignupURL"><bbbc:config key="emailSignupURL_CA" configName="ThirdPartyURLs" /></c:set>
						</c:when>
					</c:choose>
                    <div class="clearfix cb">
                        <form id="frmSignupSpecialEmail" class="post form" method="post" action="${emailSignupURL}">
                            <div class="input noMar clearfix">
                                <div class="label">
                                    <label id="lbltxtSignupSpecialEmail" for="txtSignupSpecialEmail"><bbbl:label key="lbl_footer_specialemail" language="${pageContext.request.locale.language}" /></label>
                                </div>
                                <div class="text padRight_10">
									<c:set var="lbl_email_signup_placeholder">
										<bbbl:label key="lbl_email_signup_placeholder" language="${pageContext.request.locale.language}" />
									</c:set>
                                    <input id="txtSignupSpecialEmail" placeholder="${lbl_email_signup_placeholder}" name="email" type="text" aria-required="true" aria-labelledby="lbltxtSignupSpecialEmail errortxtSignupSpecialEmail" />    
                                </div>
                            </div>
                            <c:set var="lbl_footersubmit_button">
                                <bbbl:label key='lbl_footer_emailsubmit_button' language='${pageContext.request.locale.language}' />
                            </c:set>
                            <div class="clear"></div>
							
							<c:choose>
								<c:when test="${currentSiteId eq BuyBuyBabySite}">
									<c:set var="button_active">button_active</c:set>
								</c:when>
							</c:choose>
							
                            <div class="input noMar clearfix">
                                <div class="button button_prodLight ${button_active}">
                                    <input type="submit" value="${lbl_footersubmit_button}" id="Submit" onclick="javascript:omnitureExternalLinks('Email Sign Up: submit a Email Sign Up Special Offer button');" name="submit" aria-pressed="false" aria-labelledby="Submit" />
                                </div>
                            </div>
                            <div class="clear"></div>
                        </form>
                    </div>
                </c:if>                   
                <p class="footeroffers"><bbbl:label key="lbl_footer_myoffers" language="${pageContext.request.locale.language}" /></p>
                    <div class="smallText">
                        <%-- <p><a href="/store/st/PrivacyPolicy">Privacy Policy & Your Privacy</a></p> --%>
                        <p><bbbl:label key="lbl_footer_privacypolicy" language="${pageContext.request.locale.language}" /></p>
                    </div>                    
                </div>
            </div>
            <div class="fl borderRight">
                <div class="box grid_2 clearfix">
                    <h3><bbbl:label key="lbl_footer_satisfactionguarante" language="${pageContext.request.locale.language}" /></h3>
                    <p><bbbt:textArea key="txt_footer_help" language ="${pageContext.request.locale.language}"/></p>
                </div>
            </div>
             <dsp:droplet name="WebFooterLinkTemplateDroplet">
				<dsp:oparam name="output">
				<dsp:getvalueof var="staticTemplateData" param="staticTemplateData"/>
            <div class="fl borderRight">
                <div class="box grid_2 clearfix">
                     <h3>${staticTemplateData[0].footerName}</h3>
                   <c:forEach items="${staticTemplateData[0].links}" var="linkVO">
                   <c:choose>
	                   <c:when test="${linkVO.bannerText eq 'Investor Relations'}">
		                   <c:if test="${currentSiteId ne BuyBuyBabySite}">
		                        <c:set var="cmsData"><bbbt:textArea key="txtarea_investor_conferencecall" language ="${pageContext.request.locale.language}"/></c:set>
		                        <c:choose>
		                            <c:when test="${fn:length(cmsData) == 0 || fn:contains(cmsData, 'VALUE NOT FOUND')}">
										<p><a href="http://phx.corporate-ir.net/phoenix.zhtml?c=97860&p=irol-irhome" target="_blank">Investor Relations</a></p>
									</c:when>
									<c:otherwise>
										<p>${linkVO.bannerLink}</p>
									</c:otherwise>
			                    </c:choose>
		                    </c:if>
	                    </c:when>
                    <c:otherwise>
	                    <c:choose>
		                   <c:when test="${currentSiteId != BedBathCanadaSite  && linkVO.bannerText eq 'Find A Store'}">
		                   	<c:if test="${MapQuestOn}">
		                   		 <p>${linkVO.bannerLink}</p>
		                   	 </c:if>
		                   </c:when>
		                   <c:when test="${linkVO.bannerText eq 'Same Day Delivery'}">
	                       	<c:if test="${ fn:containsIgnoreCase(sameDayDeliveryFlag,'true') && isInternationalCustomer ne true }">
		                   		 <p>${linkVO.bannerLink}</p>
		                   </c:if>
		                   </c:when> 
	                    <c:otherwise> 
	                    	 <p>${linkVO.bannerLink}</p>
	                   </c:otherwise>
		                   </c:choose>
                    </c:otherwise>
                   	</c:choose>	
                   </c:forEach>
                
                  
	                   <%--  <c:if test="${currentSiteId eq BuyBuyBabySite}">
	                    	<bbbl:label key="lbl_footer_aboutus" language="${pageContext.request.locale.language}" />
	                    </c:if>
	                    <c:choose>
	                        <c:when test="${currentSiteId ne BuyBuyBabySite}">
	                            <bbbl:label key="lbl_footer_corporate_responsibility" language="${pageContext.request.locale.language}" />
	                        </c:when>
	                    </c:choose>
                    	<bbbl:label key="lbl_footer_media_relations" language="${pageContext.request.locale.language}" />
                   	
                    <c:if test="${currentSiteId ne BuyBuyBabySite}">
                        <c:set var="cmsData"><bbbt:textArea key="txtarea_investor_conferencecall" language ="${pageContext.request.locale.language}"/></c:set>
                        <c:choose>
                            <c:when test="${fn:length(cmsData) == 0 || fn:contains(cmsData, 'VALUE NOT FOUND')}">
								<p><a href="http://phx.corporate-ir.net/phoenix.zhtml?c=97860&p=irol-irhome" target="_blank">Investor Relations</a></p>
							</c:when>
							<c:otherwise>
								<p><bbbl:label key="lbl_footer_investor_relations" language="${pageContext.request.locale.language}" /></p>
							</c:otherwise>
	                    </c:choose>
                    </c:if>
                    <p><bbbl:label key="lbl_footer_careers" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_terms_of_use" language="${pageContext.request.locale.language}" /></p>
					
					<c:choose>
                    	<c:when test="${currentSiteId == BedBathCanadaSite}">
                   	 		<p><bbbl:label key="lbl_footer_accessibility" language="${pageContext.request.locale.language}" /></p>
                    	</c:when>
                    </c:choose>
                    
					<c:set var="FindAStoreTitle"><bbbl:label key="lbl_header_canadafindastore" language="${pageContext.request.locale.language}" /></c:set>
                   	<c:choose>
                    	<c:when test="${currentSiteId == BedBathCanadaSite}">
                    		<p><dsp:a title="${FindAStoreTitle}" href="${contextPath}/selfservice/CanadaStoreLocator">${FindAStoreTitle}</dsp:a></p>
                    	</c:when>
                        <c:otherwise>
		                	<c:if test="${MapQuestOn}">
                        		<p><a title="${FindAStoreTitle}" href="${contextPath}/selfservice/FindStore">${FindAStoreTitle}</a></p>
                        	</c:if>
                        </c:otherwise>
                    </c:choose>
                     <c:if test="${currentSiteId ne BuyBuyBabySite}">
                    <p><bbbl:label key="lbl_footer_corporate_sales" language="${pageContext.request.locale.language}" /></p>
                    </c:if> --%>

                </div>
            </div>
            <div class="fl borderRight">
                <div class="box grid_2 clearfix">
                 <h3>${staticTemplateData[1].footerName}</h3>
                   <c:forEach items="${staticTemplateData[1].links}" var="linkVO">
                   		<p>${linkVO.bannerLink}</p>
                   </c:forEach>
                    <%-- <h3><bbbl:label key="lbl_footer_shopping_tools" language="${pageContext.request.locale.language}" /></h3>
                    <p><a href="${contextPath}/cms/static/staticpage.jsp?pageName=ShippingPolicies">Guides</a></p>
					<c:if test="${currentSiteId eq BuyBuyBabySite}">
						<p><bbbl:label key="lbl_footer_baby_shop_by_catalogs" language="${pageContext.request.locale.language}" /></p>
					</c:if>
                    <p><bbbl:label key="lbl_footer_guides" language="${pageContext.request.locale.language}" /></p>
                    <c:choose>
                        <c:when test="${currentSiteId eq BuyBuyBabySite}">
                            <p><bbbl:label key="lbl_footer_buybuybaby_videos" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
                        <c:when test="${currentSiteId eq BedBathUSSite}">
                            <p><bbbl:label key="lbl_footer_videos" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
						 <c:when test="${currentSiteId eq BedBathCanadaSite}">
                            <p><bbbl:label key="lbl_footer_bedbathca_videos" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
                    </c:choose>
                    <p><bbbl:label key="lbl_footer_glossary" language="${pageContext.request.locale.language}" /></p>
                    <c:choose>
                        <c:when test="${currentSiteId eq BuyBuyBabySite}">
                            <p><bbbl:label key="lbl_footer_registry_checklist" language="${pageContext.request.locale.language}" /></p>
                            <p><bbbl:label key="lbl_footer_adoptionchecklist1" language="${pageContext.request.locale.language}" /></p>
                            <p><bbbl:label key="lbl_footer_adoptionchecklist2" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
                    </c:choose>
                    <p><bbbl:label key="lbl_footer_shop_personalized_invitations" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_shop_gift_cards" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_shop_all_clearance" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_shop_by_brand" language="${pageContext.request.locale.language}" /></p>
                   --%>
                </div>
            </div>
            <div class="fl borderRight <c:if test="${currentSiteId eq BedBathCanadaSite}">noBorder</c:if>">
                <div class="box grid_2 clearfix">
                 <h3>${staticTemplateData[2].footerName}</h3>
                   <c:forEach items="${staticTemplateData[2].links}" var="linkVO">
                   		<c:if test="${(linkVO.bannerText eq 'Contact Us / Feedback' && ContactUsOn) || (linkVO.bannerText eq 'Check Gift Card Balance' && ValueLinkOn) || (linkVO.bannerText ne 'Contact Us / Feedback' && linkVO.bannerText ne 'Check Gift Card Balance' )}">
                   			<p <c:if test="${linkVO.bannerText eq 'Safety & Recalls'}">class="safetyRecalls"</c:if>>${linkVO.bannerLink}</p>
                  	</c:if>
                   </c:forEach>
                <%--  <h3><bbbl:label key="lbl_footer_customer_service" language="${pageContext.request.locale.language}" /></h3>
                    <c:if test="${ContactUsOn}">
                    <p><bbbl:label key="lbl_footer_contactus_feedback" language="${pageContext.request.locale.language}" /></p>
                    </c:if>
                	<p><bbbl:label key="lbl_footer_easy_returns" language="${pageContext.request.locale.language}" /></p>
					Fix for defect BBBSL-2819
                	<p><bbbl:label key="lbl_footer_price_match" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_shipping_info" language="${pageContext.request.locale.language}" /></p>
                    
                    Footer link for International shipping
                    <c:if test="${internationalShippingOn}">
						<p><bbbl:label key="lbl_footer_international_shipping" language="${pageContext.request.locale.language}" /></p>  
					</c:if>
                    
                    <p><bbbl:label key="lbl_footer_bopus_link"  language ="${pageContext.request.locale.language}"/></p>
                    <p><bbbl:label key="lbl_footer_gift_packaging" language="${pageContext.request.locale.language}" /></p>
                    Added Picture People as per BBBSL-2933
                    <c:if test="${currentSiteId == BuyBuyBabySite}">
                     	<p><bbbl:label key="lbl_footer_picture_people" language="${pageContext.request.locale.language}" /></p>
                    </c:if>
                    <p><bbbl:label key="lbl_footer_faqs" language="${pageContext.request.locale.language}" /></p>
                    <c:choose>
                        <c:when test="${currentSiteId == BuyBuyBabySite}">
                            <p><bbbl:label key="lbl_footer_create_registry" language="${pageContext.request.locale.language}" /></p>
                            <p><bbbl:label key="lbl_footer_find_registry" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
                    </c:choose>
                     <c:if test="${ValueLinkOn}">  
	                    <p><bbbl:label key="lbl_footer_check_gift_card_balance" language="${pageContext.request.locale.language}" /></p>
                    </c:if>
                    Fix for defect BBBSL-2819
                    <p class="safetyRecalls">
                        <bbbl:label key="lbl_footer_safety_recalls" language="${pageContext.request.locale.language}" />
                    </p> --%>
                </div>
            </div>
            </dsp:oparam>
			</dsp:droplet>
            <c:if test="${currentSiteId ne BedBathCanadaSite}">
            <div class="fl borderRight noBorder">
                <div class="box grid_2 omega clearfix">
                    <h3><bbbl:label key="lbl_footer_followus" language="${pageContext.request.locale.language}" /></h3>
                    <c:if test="${FBOn}">
                         <bbbt:textArea key="txt_footer_facebook" language="${pageContext.request.locale.language}" />
                    </c:if>
                    <bbbt:textArea key="txt_footer_twitter" language="${pageContext.request.locale.language}" />

                    <bbbt:textArea key="txt_footer_pinterest" language="${pageContext.request.locale.language}" />
                    
                    <bbbt:textArea key="txt_footer_youtube" language="${pageContext.request.locale.language}" />
                    
                    <bbbt:textArea key="txt_footer_instagram" language="${pageContext.request.locale.language}" />
                    
                    <bbbt:textArea key="txt_footer_googleplus" language="${pageContext.request.locale.language}" />
                    
                    <bbbt:textArea key="txt_footer_bedbathblog" language="${pageContext.request.locale.language}" />
                    
                </div>
            </div>
            </c:if>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
        
            <div class="container_12 clearfix">
                <div id="otherSites" class="grid_12">
                    <div class="otherSitesWrapper">   
                        <bbbl:textArea key="txt_footer_logo_bedbathbeyond" language="${pageContext.request.locale.language}" />
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        
        <%--Your Amigo Footer starts --%>
        <c:if test="${YourAmigoON}">
			<bbbt:textArea key="txt_footer_youramigolinks" language ="${pageContext.request.locale.language}"/>
		</c:if>
        <%--Your Amigo Footer ends --%>
        <div class="clear"></div>
    </div>

   	<div class="clear"></div>
   	<c:if test="${newRelicTagON}">
		<dsp:include page="/_includes/footer/footerNewRelic.jsp"/>
	</c:if>   

    <div id="footerPs">
    	<dsp:getvalueof var="currentYear" bean="CurrentDate.year"/>
    	<jsp:useBean id="placeHolderMapCopyRight" class="java.util.HashMap" scope="request"/>
		<c:set target="${placeHolderMapCopyRight}" property="currentYear" value="${currentYear}"/>
        <div class="container_12 clearfix">
            <p class="grid_12 clearfix">
                <c:choose>
                    <c:when test="${currentSiteId eq BedBathUSSite }">
                        <bbbt:textArea key="txt_footer_bedbathbeyond_all_rights_reserved" placeHolderMap="${placeHolderMapCopyRight}" language ="${pageContext.request.locale.language}"/>
                    </c:when>
                    <c:when test="${currentSiteId eq BuyBuyBabySite}">
                        <bbbt:textArea key="txt_footer_buybuybaby_all_rights_reserved" placeHolderMap="${placeHolderMapCopyRight}" language ="${pageContext.request.locale.language}"/>
                    </c:when>
                    <c:when test="${currentSiteId eq BedBathCanadaSite}">
                        <bbbt:textArea key="txt_footer_bedbathbeyondca_all_rights_reserved" placeHolderMap="${placeHolderMapCopyRight}" language ="${pageContext.request.locale.language}"/>
                    </c:when>
                </c:choose>
                <dsp:include page="build_number.jsp" />
            </p>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>  
    <div class="clear"></div>
    </dsp:oparam>
    </dsp:droplet>

    <script type="text/javascript">
        (function ($, window, document, undefined) {
            $(function () {
                $("#footer .socialIconFollow a").on('click', function () {
                    var followString = $(this).data('omniture-string');

                    if (typeof externalLinks === 'function' && typeof followString === 'string' && followString.trim() !== '') {
                        externalLinks(followString.trim());
                    }
                });
            });
        }(jQuery, this, this.document));

        function omnitureExternalLinks(data) {
            if (typeof s !== 'undefined') {
                externalLinks(data);
            }
        }
    </script>
   <dsp:getvalueof var="errorList" bean="SystemErrorInfo.errorList"/>
	
	<%-- When home page Akamai caching is on, then dynamic_third_party will invoke welcome mat logic--%>
	<c:choose>
      <c:when test="${homepage eq 'homepage' && (homePageCachingOn eq true or homePageCachingOn eq TRUE)}">
			<%-- When home page Akamai caching is on, then dynamic_third_party will invoke welcome mat logic--%>
      </c:when>
      <c:otherwise>
      
			   		 
             <dsp:droplet name="InternationalShippingContextSetterDroplet">
				<dsp:oparam name="output">
				<dsp:getvalueof var="countryCode" param="countryCode"/>
				</dsp:oparam>
			</dsp:droplet>
			
			<%-- START Script for International Shipping Welcome Mat --%>
			<c:if test="${internationalShippingOn and countryCode ne 'US'}">
		             
				<script type="text/javascript">
					function wlcme51func(url) {
						var wlcme51 = document.createElement("script");
						wlcme51.src = url;
						wlcme51.type = "text/javascript";
						document.getElementsByTagName("head")[0]
								.appendChild(wlcme51);
					}
	
					// Drop / Check for cookie to ensure visitor only sees Welcome Mat once per session
					function isWelcome() {
						var c_name = 'wlcme';
						if (document.cookie.length > 0) {
							c_start = document.cookie.indexOf(c_name + "=");
							if (c_start != -1) {
								c_start = c_start + c_name.length + 1;
								c_end = document.cookie.indexOf(";", c_start);
								if (c_end == -1)
									c_end = document.cookie.length;
								return unescape(document.cookie.substring(c_start,
										c_end));
							}
						}
						return "";
					}
	
					if (!isWelcome()) {
	
						wlcme51func("${welcomeMatUrl}?merchId=${internationalMerchantId}&countryId=${countryCode}&setCookie=Y");
					}
				</script>
			</c:if>
      </c:otherwise>
    </c:choose>

	
	

	<%-- END Script for International Shipping Welcome Mat --%>

	<%--FB & Twitter Pixel tracking starts --%>
	<%-- <c:if test="${pixelFbOn}">
		<c:set var="fb_WCA"><bbbc:config key="pixel_fb_WCA" configName="ContentCatalogKeys" /></c:set>
	
		<script>(function() {
		  var _fbq = window._fbq || (window._fbq = []);
		  if (!_fbq.loaded) {
		    var fbds = document.createElement('script');
		    fbds.async = true;
		    fbds.src = '//connect.facebook.net/en_US/fbds.js';
		    var s = document.getElementsByTagName('script')[0];
		    s.parentNode.insertBefore(fbds, s);
		    _fbq.loaded = true;
		  }
		  _fbq.push(['addPixelId', '${fb_WCA}']);
		})();
		window._fbq = window._fbq || [];
		window._fbq.push(['track', 'PixelInitialized', {}]);
		</script>
		<noscript><img height="1" width="1" alt="" style="display:none" src="https://www.facebook.com/tr?id=${fb_WCA}&amp;ev=NoScript" /></noscript>
	</c:if> --%>
	
	<c:if test="${pixelTwtOn}">
		<c:set var="twt_SiteVisit"><bbbc:config key="pixel_twt_SiteVisit" configName="ContentCatalogKeys" /></c:set>
		
		<!-- script src="//platform.twitter.com/oct.js" type="text/javascript"></script -->
		<script type="text/javascript">
		function loadTwitterOctJS() {
			bbbLazyLoader.js('//platform.twitter.com/oct.js', function(){
                twttr.conversion.trackPid('${twt_SiteVisit}');
                BBB.twttrjs = true;
            });
		}
		if (window.attachEvent) {
	        window.attachEvent('onload', loadTwitterOctJS);
	    }
	    else {
	        window.addEventListener('load', loadTwitterOctJS, false);
	    }
		</script>
		<noscript>
			<img height="1" width="1" style="display:none;" alt="" src="https://analytics.twitter.com/i/adsct?txn_id=${twt_SiteVisit}&p_id=Twitter" />
			<img height="1" width="1" style="display:none;" alt="" src="//t.co/i/adsct?txn_id=${twt_SiteVisit}&p_id=Twitter" />
		</noscript>
	</c:if>
	
	<%--FB & Twitter Pixel tracking ends --%>

    <%--
	<div id="sameDayDeliveryModal" class="hidden">
        <dsp:include src="${contextPath}/_includes/modals/sameDayDeliveryModal.jsp" ></dsp:include>
    </div>
   
    <div id="sameDayDeliveryCongratulationsModal" class="hidden">
        <dsp:include src="${contextPath}/_includes/modals/sameDayDeliveryCongratulationsModal.jsp" ></dsp:include>
    </div>
     --%>
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    	<dsp:param bean="SystemErrorInfo.errorList" name="array" />
		<dsp:param name="elementName" value="ErrorInfoVO" />
		<dsp:oparam name="outputStart"><div id="error" class="hidden"><ul></dsp:oparam>
    	<dsp:oparam name="output">
			<li id="tl_atg_err_code"><dsp:valueof param="ErrorInfoVO.errorCode"/></li>
			<li id="tl_atg_err_value"><dsp:valueof param="ErrorInfoVO.errorDescription"/></li>
    	</dsp:oparam>
		<dsp:oparam name="outputEnd"></ul></div></dsp:oparam>
    </dsp:droplet>
</dsp:page>