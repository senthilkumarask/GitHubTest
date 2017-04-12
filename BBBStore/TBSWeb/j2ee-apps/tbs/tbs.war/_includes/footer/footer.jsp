<dsp:page>

<%-- THIS PAGE NO LONGER USED --%>


    <dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>
    <dsp:importbean var="SubscriptionFormHandler" bean="/com/bbb/selfservice/SubscriptionFormHandler"/>
    <dsp:importbean var="CurrentDate" bean="/atg/dynamo/service/CurrentDate"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:importbean bean="/com/bbb/internationalshipping/droplet/InternationalShippingContextSetterDroplet"/>
    <c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="newRelicTagON">
		<bbbc:config key="newRelicTag" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="isLoggedIn" bean="/atg/userprofiling/Profile.transient"/>
	<dsp:droplet name="InternationalShippingContextSetterDroplet">
		<dsp:oparam name="output">
		<dsp:getvalueof var="countryCode" param="countryCode"/>
		</dsp:oparam>
	</dsp:droplet>
    <div class="clear"></div>
    <div id="footer" class="clearfix  <c:if test="${!YourAmigoON}">yaOff</c:if>" role="contentinfo">
        <div class="topBorder"></div>
        <div class="container_12 padBottom_20 clearfix">
            <div class="fl borderRight divSignupSpecialEmail">
                <div class="box grid_2 alpha clearfix">
               	<c:if test="${EmailOn}">
                    <bbbt:textArea key="txt_footer_signup_specialoffers" language ="${pageContext.request.locale.language}"/>
					<c:choose>
						<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
							<c:set var="emailSignupURL"><bbbc:config key="emailSignupURL_US" configName="ThirdPartyURLs" /></c:set>
						</c:when>
						<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
							<c:set var="emailSignupURL"><bbbc:config key="emailSignupURL_Baby" configName="ThirdPartyURLs" /></c:set>
						</c:when>
						<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
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
								<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
									<c:set var="button_active">button_active</c:set>
								</c:when>
							</c:choose>
							
                            <div class="input noMar clearfix">
                                <div class="button button_prodLight ${button_active}">
                                    <input type="submit" value="${lbl_footersubmit_button}" id="Submit" onclick="javascript:omnitureExternalLinks('Email Sign Up: submit a Email Sign Up Special Offer button');" name="submit" role="button" aria-pressed="false" aria-labelledby="Submit" />
                                </div>
                            </div>
                            <div class="clear"></div>
                        </form>
                    </div>
                </c:if>    
                    <div class="smallText">
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
            <div class="fl borderRight">
                <div class="box grid_2 clearfix">
                    <h3><bbbl:label key="lbl_footer_company_info" language="${pageContext.request.locale.language}" /></h3>
                    <p>
	                    <c:if test="${currentSiteId eq TBS_BuyBuyBabySite}">
	                    	<bbbl:label key="lbl_footer_aboutus" language="${pageContext.request.locale.language}" />
	                    </c:if>
	                    <c:choose>
	                        <c:when test="${currentSiteId ne TBS_BuyBuyBabySite}">
	                            <bbbl:label key="lbl_footer_corporate_responsibility" language="${pageContext.request.locale.language}" />
	                        </c:when>
	                    </c:choose>
                    </p>
                    <p>
                    	<bbbl:label key="lbl_footer_media_relations" language="${pageContext.request.locale.language}" />
                    </p>	
                    <c:if test="${currentSiteId ne TBS_BuyBuyBabySite}">
                        <c:set var="cmsData"><bbbt:textArea key="txtarea_investor_conferencecall" language ="${pageContext.request.locale.language}"/></c:set>
                        <c:choose>
                            <c:when test="${fn:length(cmsData) == 0 || fn:contains(cmsData, 'VALUE NOT FOUND')}">
								<p><a href="//phx.corporate-ir.net/phoenix.zhtml?c=97860&p=irol-irhome" target="_blank">Investor Relations</a></p>
							</c:when>
							<c:otherwise>
								<p><bbbl:label key="lbl_footer_investor_relations" language="${pageContext.request.locale.language}" /></p>
							</c:otherwise>
	                    </c:choose>
                    </c:if>
                    <p><bbbl:label key="lbl_footer_careers" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_terms_of_use" language="${pageContext.request.locale.language}" /></p>
					
					<c:set var="FindAStoreTitle"><bbbl:label key="lbl_header_canadafindastore" language="${pageContext.request.locale.language}" /></c:set>
                   	<c:choose>
                    	<c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
                    		<p><dsp:a title="${FindAStoreTitle}" href="${contextPath}/selfservice/CanadaStoreLocator">${FindAStoreTitle}</dsp:a></p>
                    	</c:when>
                        <c:otherwise>
		                	<c:if test="${MapQuestOn}">
                        		<p><a title="${FindAStoreTitle}" href="${contextPath}/selfservice/FindStore">${FindAStoreTitle}</a></p>
                        	</c:if>
                        </c:otherwise>
                    </c:choose>
                     <c:if test="${currentSiteId ne TBS_BuyBuyBabySite}">
                    <p><bbbl:label key="lbl_footer_corporate_sales" language="${pageContext.request.locale.language}" /></p>
                    </c:if>
                </div>
            </div>
            <div class="fl borderRight">
                <div class="box grid_2 clearfix">
                    <h3><bbbl:label key="lbl_footer_shopping_tools" language="${pageContext.request.locale.language}" /></h3>
					<c:if test="${currentSiteId eq TBS_BuyBuyBabySite}">
						<p><bbbl:label key="lbl_footer_baby_shop_by_catalogs" language="${pageContext.request.locale.language}" /></p>
					</c:if>
                    <p><bbbl:label key="lbl_footer_guides" language="${pageContext.request.locale.language}" /></p>
                    <c:choose>
                        <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                            <p><bbbl:label key="lbl_footer_buybuybaby_videos" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
                        <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                            <p><bbbl:label key="lbl_footer_videos" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
						 <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                            <p><bbbl:label key="lbl_footer_bedbathca_videos" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
                    </c:choose>
                    <p><bbbl:label key="lbl_footer_glossary" language="${pageContext.request.locale.language}" /></p>
                    <c:choose>
                        <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                            <p><bbbl:label key="lbl_footer_registry_checklist" language="${pageContext.request.locale.language}" /></p>
                            <p><bbbl:label key="lbl_footer_adoptionchecklist1" language="${pageContext.request.locale.language}" /></p>
                            <p><bbbl:label key="lbl_footer_adoptionchecklist2" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
                    </c:choose>
                    <p><bbbl:label key="lbl_footer_shop_personalized_invitations" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_shop_gift_cards" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_shop_all_clearance" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_shop_by_brand" language="${pageContext.request.locale.language}" /></p>
                   
                </div>
            </div>
            <div class="fl borderRight <c:if test="${currentSiteId eq TBS_BedBathCanadaSite}">noBorder</c:if>">
                <div class="box grid_2 clearfix">
                    <h3><bbbl:label key="lbl_footer_customer_service" language="${pageContext.request.locale.language}" /></h3>
                    <c:if test="${ContactUsOn}">
                    <p><bbbl:label key="lbl_footer_contactus_feedback" language="${pageContext.request.locale.language}" /></p>
                    </c:if>
                	<p><bbbl:label key="lbl_footer_easy_returns" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_shipping_info" language="${pageContext.request.locale.language}" /></p>
                    
                    <%--Footer link for International shipping --%>
                    <c:if test="${internationalShippingOn}">
						<p><bbbl:label key="lbl_footer_international_shipping" language="${pageContext.request.locale.language}" /></p>  
					</c:if>
                    
                    <p><bbbl:label key="lbl_footer_bopus_link"  language ="${pageContext.request.locale.language}"/></p>
                    <p><bbbl:label key="lbl_footer_gift_packaging" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_faqs" language="${pageContext.request.locale.language}" /></p>
                    <p><bbbl:label key="lbl_footer_create_an_account" language="${pageContext.request.locale.language}" /></p>
                    <c:choose>
                        <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                            <p><bbbl:label key="lbl_footer_create_registry" language="${pageContext.request.locale.language}" /></p>
                            <p><bbbl:label key="lbl_footer_find_registry" language="${pageContext.request.locale.language}" /></p>
                        </c:when>
                    </c:choose>
                     <c:if test="${ValueLinkOn}">  
	                    <p><bbbl:label key="lbl_footer_check_gift_card_balance" language="${pageContext.request.locale.language}" /></p>
                    </c:if>
                    <p>
                    <c:choose>
                    	<c:when test="${!isLoggedIn}">
                        	<a href="${contextPath}/account/order_summary.jsp"><bbbl:label key="lbl_footer_order_inquiry" language="${pageContext.request.locale.language}" /></a>
                        </c:when>
                        <c:otherwise>
							<a href="${contextPath}/account/TrackOrder"><bbbl:label key="lbl_footer_order_inquiry" language="${pageContext.request.locale.language}" /></a>
						</c:otherwise>
                    </c:choose>
                    </p>
                    <p class="safetyRecalls">
                        <bbbl:label key="lbl_footer_safety_recalls" language="${pageContext.request.locale.language}" />
                    </p>
                </div>
            </div>
            <c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
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
        <c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
            <div class="container_12 clearfix">
                <div id="otherSites" class="grid_12">
                    <div class="otherSitesWrapper">
                        <div class="fl" id="otherSitesTitle"><p><bbbl:label key="lbl_footer_visit_other_sites" language="${pageContext.request.locale.language}" /></p></div>
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                <div class="fl" id="logoBy2"><bbbl:label key="lbl_footer_bedbathbeyond" language="${pageContext.request.locale.language}" /></div>
                                <div class="fl" id="logoHarmon"><bbbl:label key="lbl_footer_face_value" language="${pageContext.request.locale.language}" /></div>
                                <div class="fl" id="logoCpwm"><bbbl:label key="lbl_footer_cpwm" language="${pageContext.request.locale.language}" /></div>
                                <div class="fl" id="logoBb2"><bbbl:label key="lbl_footer_buybuybaby" language="${pageContext.request.locale.language}" /></div>
                            </c:when>
                            <c:otherwise>
                                <div class="fl" id="logoBb"><bbbl:label key="lbl_footer_buybuybaby" language="${pageContext.request.locale.language}" /></div>
                                <div class="fl" id="logoHarmon"><bbbl:label key="lbl_footer_face_value" language="${pageContext.request.locale.language}" /></div>
                                <div class="fl" id="logoCpwm"><bbbl:label key="lbl_footer_cpwm" language="${pageContext.request.locale.language}" /></div>
                                <div class="fl" id="logoBy"><bbbl:label key="lbl_footer_bedbathbeyond" language="${pageContext.request.locale.language}" /></div>
                            </c:otherwise>
                        </c:choose>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </c:if>
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
                    <c:when test="${currentSiteId eq TBS_BedBathUSSite }">
                        <bbbt:textArea key="txt_footer_bedbathbeyond_all_rights_reserved" placeHolderMap="${placeHolderMapCopyRight}" language ="${pageContext.request.locale.language}"/>
                    </c:when>
                    <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                        <bbbt:textArea key="txt_footer_buybuybaby_all_rights_reserved" placeHolderMap="${placeHolderMapCopyRight}" language ="${pageContext.request.locale.language}"/>
                    </c:when>
                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
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

	<%-- END Script for International Shipping Welcome Mat --%>

	
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