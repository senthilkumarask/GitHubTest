<dsp:page>
<dsp:importbean var="CurrentDate" bean="/atg/dynamo/service/CurrentDate"/>
<dsp:getvalueof var="bodyEndTagContent" param="bodyEndTagContent" />
<dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
<div id="storeNumberModal" class="reveal-modal small" data-reveal>
    <dsp:include page="/selfservice/store_number.jsp" />
    <a id="closeStoreModal" class="close-reveal-modal">&#215;</a>
</div>

<%--     <dsp:include page="/_includes/double_click_general_tag.jsp"></dsp:include>
 --%>   
     <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/bbb_combined_libs.js?v=${buildRevisionNumber}"></script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/jquery-migrate-1.2.1.js"></script>
            <script type="text/javascript" src= '${jsPath}/_assets/tbs_assets/js/src/plugins/jquery.hoverIntent.minified.js'></script>
            <script type="text/javascript" src= '${jsPath}/_assets/tbs_assets/js/src/slick/slick.js'></script>
            <script type="text/javascript" src= '${jsPath}/_assets/tbs_assets/js/src/foundation/foundation-datepicker.js'></script>
            <script type="text/javascript" src= '${jsPath}/_assets/tbs_assets/js/src/foundation/moment.min.js'></script>
            <script type="text/javascript" src= '${jsPath}/_assets/tbs_assets/js/src/foundation/daterangepicker.js'></script>
            <script type="text/javascript" src= '${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.validate.js'></script>
            <script type="text/javascript" src= '${jsPath}/_assets/tbs_assets/js/src/legacy/global.config.js'></script>
            <script type="text/javascript" src= '${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.carouFredSel-5.5.5-min.js'></script>
        </c:otherwise>
    </c:choose>

    <dsp:include page="/includes/pageEndScript.jsp"/>

    <%-- KP COMMENT START: Dropping old footer --%>
        <%-- remove the commented for omniture --%>
        <dsp:getvalueof var="footerContent" param="footerLastContent"/>
        <c:if test="${not empty footerContent}">
            <c:out value="${footerContent}" escapeXml="false"/>
        </c:if>

         <c:if test="${fn:indexOf(pageWrapper, 'useBazaarVoice') > -1 && bExternalJSCSS && bPlugins && BazaarVoiceOn}">
            <c:choose>
                <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                    <script type="text/javascript">
                        var cssLink1=document.createElement('link'),
                            cssLink2=document.createElement('link');
                            cssLink1.type="text/css";   cssLink2.type="text/css";
                            cssLink1.rel="stylesheet";  cssLink2.rel="stylesheet";
                            cssLink1.href="<bbbc:config key='css_BazaarVoice_ratings_us' configName='ThirdPartyURLs'/>";
                            cssLink2.href="<bbbc:config key='css_BazaarVoice_qa_us' configName='ThirdPartyURLs'/>";
                            document.body.appendChild(cssLink1);
                            document.body.appendChild(cssLink2);
                    </script> 
                <%--
                    <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_ratings_us' configName='ThirdPartyURLs' />" />
                    <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_qa_us' configName='ThirdPartyURLs' />" />
                --%>

                </c:when>
                <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                    <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_ratings_baby' configName='ThirdPartyURLs' />" />
                    <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_qa_baby' configName='ThirdPartyURLs' />" />
                </c:when>
                <c:otherwise>
                    <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_ratings_ca' configName='ThirdPartyURLs' />" />
                </c:otherwise>
            </c:choose>
        </c:if>
        
    <%-- KP COMMENT END --%>

                    <%-- KP COMMENT START: New Footer --%>
                    <!-- footer -->
                    
                    
                    
                    
<%-- <c:if test="${EmailOn}">
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
    </c:if> --%>
                    <div class="dslToolTip" style="display: none; top: 300px; left: 385px; position:fixed">
                            <span class="personalizationTypeMthd">Delivery Charge</span>
                            <span class="textTypeMthd">Lorem ipsum dolor sit amet.Lorem ipsum dolor sit amet.</span>
                            <span class="personalizationTypeMthd">Assembly Charge</span>
                            <span class="textTypeMthd">Lorem ipsum dolor sit amet.Lorem ipsum dolor sit amet.</span>
                        </div>
         <c:if test="${!fn:contains(header['User-Agent'],'Adobe_AIR_3.6')}">    
                    <footer>
                        <div class="row links">
                            <div class="small-12 large-4 columns email-signup show-for-large-up">
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
                                    
                                    <form id="frmSignupSpecialEmail" class="post form" method="post" action="${emailSignupURL}">
                                        <label id="lbltxtSignupSpecialEmail" for="txtSignupSpecialEmail"><bbbl:label key="lbl_footer_specialemail" language="${pageContext.request.locale.language}" /></label>
                                            
                                        <c:set var="lbl_email_signup_placeholder">
                                            <bbbl:label key="lbl_email_signup_placeholder" language="${pageContext.request.locale.language}" />
                                        </c:set>
                                        <input id="txtSignupSpecialEmail" class="footer-email" placeholder="${lbl_email_signup_placeholder}" name="email" type="text" aria-required="true" aria-labelledby="lbltxtSignupSpecialEmail errortxtSignupSpecialEmail" />    
                                            
                                        <c:set var="lbl_footersubmit_button">
                                            <bbbl:label key='lbl_footer_emailsubmit_button' language='${pageContext.request.locale.language}' />
                                        </c:set>
                                        
                                        <c:choose>
                                            <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                                <c:set var="button_active">button_active</c:set>
                                            </c:when>
                                        </c:choose>
                                        
                                        <input type="submit" class="button footer-email-submit" value="${lbl_footersubmit_button}" id="Submit" onclick="javascript:omnitureExternalLinks('Email Sign Up: submit a Email Sign Up Special Offer button');" name="submit" role="button" aria-pressed="false" aria-labelledby="Submit" />
                                        
                                    </form>
                                    
                                </c:if>        
                                
                                <ul>                        
                                   
                                   
                                       <c:if test="${currentSiteId ne TBS_BedBathCanadaSite }" >
                                        <li><bbbl:label key="lbl_footer_myoffers_tbs" language="${pageContext.request.locale.language}" /></li>
                                        </c:if> 
                                        
                                        <li><bbbl:label key="lbl_footer_privacypolicy_tbs" language="${pageContext.request.locale.language}" /></li>                                        
                                </ul>
                                
                            </div>
                            
                            <div class="small-12 large-2 columns satisfaction-guarantee">
                                <h3><bbbl:label key="lbl_footer_satisfactionguarante" language="${pageContext.request.locale.language}" /></h3>
                                <p><bbbt:textArea key="txt_footer_help_tbs" language ="${pageContext.request.locale.language}"/></p>
                            </div>
                            
                            <div class="small-12 large-2 columns company-info collapse-list collapse-list-medium-down">
                                <h3><bbbl:label key="lbl_footer_company_info" language="${pageContext.request.locale.language}" /><span></span></h3>
                                <ul>
                                    <li>
                                        <c:if test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                            <bbbl:label key="lbl_footer_aboutus_tbs" language="${pageContext.request.locale.language}" />
                                        </c:if>
                                        <c:if test="${currentSiteId eq TBS_BedBathUSSite }" >    
                                            <bbbl:label key="lbl_footer_corporate_responsibility_tbs" language="${pageContext.request.locale.language}" />
                                        </c:if>
                                    </li>
                                    
                                    <li><bbbl:label key="lbl_footer_media_relations_tbs" language="${pageContext.request.locale.language}" /></li>

                                    <c:if test="${currentSiteId ne TBS_BuyBuyBabySite}">
                                        <c:set var="cmsData"><bbbt:textArea key="txtarea_investor_conferencecall" language ="${pageContext.request.locale.language}"/></c:set>
                                        <c:choose>
                                            <c:when test="${fn:length(cmsData) == 0 || fn:contains(cmsData, 'VALUE NOT FOUND')}">
                                                <li><a href="//phx.corporate-ir.net/phoenix.zhtml?c=97860&p=irol-irhome" target="_blank">Investor Relations</a></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li><bbbl:label key="lbl_footer_investor_relations_tbs" language="${pageContext.request.locale.language}" /></li>    
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                        
                                        
                                     <li><bbbl:label key="lbl_footer_careers_tbs" language="${pageContext.request.locale.language}" /></li>   
                                     <li><bbbl:label key="lbl_footer_terms_of_use_tbs" language="${pageContext.request.locale.language}" /></li>   
                                    
                                    <c:if test="${currentSiteId eq TBS_BedBathCanadaSite}" >
                                    <li><bbbl:label key="lbl_footer_accessibility_tbs" language="${pageContext.request.locale.language}" /></li>
                                    </c:if>
                                    
                                    <c:set var="FindAStoreTitle"><bbbl:label key="lbl_header_canadafindastore" language="${pageContext.request.locale.language}" /></c:set>
                                    <c:choose>
                                        <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                            <li><dsp:a title="${FindAStoreTitle}" href="//www.bedbathandbeyond.ca/store/selfservice/CanadaStoreLocator">${FindAStoreTitle}</dsp:a></li>
                                        </c:when>
                                        <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                            <li><dsp:a title="${FindAStoreTitle}" href="//www.buybuybaby.com/store/selfservice/FindStore">${FindAStoreTitle}</dsp:a></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li><dsp:a title="${FindAStoreTitle}" href="//www.bedbathandbeyond.com/store/selfservice/FindStore">${FindAStoreTitle}</dsp:a></li>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <c:if test="${currentSiteId eq TBS_BedBathUSSite }" >
                                    <li><bbbl:label key="lbl_footer_business_direct_tbs" language="${pageContext.request.locale.language}" /></li>
                                    </c:if>
                                </ul>
                            </div>
                            <div class="small-12 large-2 columns shopping-tools collapse-list collapse-list-medium-down">
                                <h3><bbbl:label key="lbl_footer_shopping_tools" language="${pageContext.request.locale.language}" /><span></span></h3>
                                <ul>
                                    <c:if test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                        <li><bbbl:label key="lbl_footer_baby_shop_by_catalogs" language="${pageContext.request.locale.language}" /></li>
                                    </c:if>
                                    
                                    <c:choose>
                                        <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                            <li><bbbl:label key="lbl_footer_guides_and_advices_tbs" language="${pageContext.request.locale.language}" /></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li><bbbl:label key="lbl_footer_guides_tbs" language="${pageContext.request.locale.language}" /></li>
                                        </c:otherwise>
                                    </c:choose>
                                    <li><bbbl:label key="lbl_footer_bedbathca_videos_tbs" language="${pageContext.request.locale.language}" /></li>
                                    <li><bbbl:label key="lbl_footer_glossary_tbs" language="${pageContext.request.locale.language}" />
                                    
                                    <c:choose>
                                        <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                            <li><bbbl:label key="lbl_footer_registry_checklist_tbs" language="${pageContext.request.locale.language}" /></li>
                                            <li><bbbl:label key="lbl_footer_adoptionchecklist1_tbs" language="${pageContext.request.locale.language}" /></li>
                                            <li><bbbl:label key="lbl_footer_adoptionchecklist2_tbs" language="${pageContext.request.locale.language}" /></li>
                                        </c:when>
                                    </c:choose>
                                    
                                     <c:choose>
                                         <c:when test="${currentSiteId ne TBS_BedBathCanadaSite}">
                                            <li><bbbl:label key="lbl_footer_shop_personalized_invitations_tbs" language="${pageContext.request.locale.language}" /></li>
                                         </c:when>
                                    </c:choose>
                                    
                                    <li><bbbl:label key="lbl_footer_shop_gift_cards_tbs" language="${pageContext.request.locale.language}" /></li>
                                    <li><bbbl:label key="lbl_footer_shop_all_clearance_tbs" language="${pageContext.request.locale.language}" /></li>
                                    <li><bbbl:label key="lbl_footer_shop_by_brand_tbs" language="${pageContext.request.locale.language}" /></li> 
                                        
                                </ul>
                            </div>
                            <div class="small-12 large-2 columns customer-service collapse-list collapse-list-medium-down">
                                <h3><bbbl:label key="lbl_footer_customer_service" language="${pageContext.request.locale.language}" /><span></span></h3>
                                <ul>
                                    <c:if test="${ContactUsOn}">
                                        <li><bbbl:label key="lbl_footer_contactus_feedback_tbs" language="${pageContext.request.locale.language}" /> </li>
                                    </c:if>
                                    
                                    <li><bbbl:label key="lbl_footer_easy_returns_tbs" language="${pageContext.request.locale.language}" /></li>
                                    <li><bbbl:label key="lbl_footer_price_match_tbs" language="${pageContext.request.locale.language}" /></li> 
                                    <li><bbbl:label key="lbl_footer_shipping_info_tbs" language="${pageContext.request.locale.language}" /></li>
                                        
                                    
                                    <%--Footer link for International shipping --%>
                                    <c:if test="${internationalShippingOn && currentSiteId ne TBS_BedBathCanadaSite}">
                                        <li><bbbl:label key="lbl_footer_international_shipping_tbs" language="${pageContext.request.locale.language}" /></li>  
                                    </c:if>
                                    
                                     <li><bbbl:label key="lbl_footer_bopus_link_tbs"  language ="${pageContext.request.locale.language}"/></li> 
                                     <li><bbbl:label key="lbl_footer_gift_packaging_tbs" language="${pageContext.request.locale.language}" /></li>
                                     <c:choose>
                                        <c:when test="${currentSiteId eq TBS_BuyBuyBabySite }" >
                                            <li><bbbl:label key="lbl_footer_picture_people_tbs" language="${pageContext.request.locale.language}" /></li>
                                        </c:when>
                                     </c:choose>                                    
                                     <li><bbbl:label key="lbl_footer_faqs_tbs" language="${pageContext.request.locale.language}" /></li>   
                                        
                                        
                                        
                                        <%--  <c:choose>
                                            <c:when test="${currentSiteId ne TBS_BedBathCanadaSite}">
                                                    <li><bbbl:label key="lbl_footer_create_an_account" language="${pageContext.request.locale.language}" /></li>                              
                                            </c:when>                                            
                                        </c:choose> --%>       
 
                                    <c:choose>
                                        <c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
                                            <li><bbbl:label key="lbl_footer_create_registry_tbs" language="${pageContext.request.locale.language}" /></li>
                                            <li><bbbl:label key="lbl_footer_find_registry_tbs" language="${pageContext.request.locale.language}" /></li>
                                        </c:when>
                                    </c:choose>
                                     
                                     <c:if test="${ValueLinkOn}">  
                                        <li><bbbl:label key="lbl_footer_check_gift_card_balance_tbs" language="${pageContext.request.locale.language}" /></li>                                      
                                     </c:if>
                                    
                                    
                                    
                                    
                                     <%-- <c:choose>
                                            <c:when test="${currentSiteId ne TBS_BedBathCanadaSite}">
                                                  <li>
                                                    <c:choose>
                                                        <c:when test="${!isLoggedIn}">
                                                            <a href="${contextPath}/account/order_summary.jsp"><bbbl:label key="lbl_footer_order_inquiry" language="${pageContext.request.locale.language}" /></a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="${contextPath}/account/TrackOrder"><bbbl:label key="lbl_footer_order_inquiry" language="${pageContext.request.locale.language}" /></a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </li>                            
                                            </c:when>                                           
                                        </c:choose> --%>  
                                        
                                        
                                        
                                    
                                    
                                    
                                    
                                    
                                    
                                    <li><bbbl:label key="lbl_footer_safety_recalls_tbs" language="${pageContext.request.locale.language}" /></li>
                                </ul>
                            </div>
                            
                            <%-- <div class="small-12 large-2 columns social">
                                <c:if test="${currentSiteId ne TBS_BedBathCanadaSite }">
                                <h3 class="show-for-large-up"><bbbl:label key="lbl_footer_followus" language="${pageContext.request.locale.language}" /></h3>
                                <ul>
                                    <c:if test="${FBOn}">
                                         <li class="facebook">
                                            <bbbt:textArea key="txt_footer_facebook_tbs" language="${pageContext.request.locale.language}" />
                                         </li>
                                    </c:if>
                                    <li class="twitter">
                                        <bbbt:textArea key="txt_footer_twitter_tbs" language="${pageContext.request.locale.language}" />
                                    </li>
                                    <li class="pinterest">
                                        <bbbt:textArea key="txt_footer_pinterest_tbs" language="${pageContext.request.locale.language}" />                                    
                                    </li>
                                     <li class="youtube">
                                        <bbbt:textArea key="txt_footer_youtube_tbs" language="${pageContext.request.locale.language}" />
                                    </li>
                                    <li class="instagram">
                                        <bbbt:textArea key="txt_footer_instagram_tbs" language="${pageContext.request.locale.language}" />
                                    </li>
                                    <c:if test="${currentSiteId eq TBS_BedBathUSSite }">
                                        <li class="anbblog"><bbbt:textArea key="txt_footer_bedbathblog_tbs" language="${pageContext.request.locale.language}" /></li>
                                     </c:if>
                                </ul>
                                </c:if> 
                            </div>--%>
                            
                        </div>
                        <div class="row other-sites show-for-large-up">
                            <div class="small-12 columns">
                                <ul>
                                    <c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
                                        <c:choose>
                                            <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                                <li class="bed-bath-beyond"><bbbl:label key="lbl_footer_bedbathbeyond" language="${pageContext.request.locale.language}" /></li>
                                                <li class="face-values"><bbbl:label key="lbl_footer_face_value" language="${pageContext.request.locale.language}" /></li>
                                                <%-- need logo <li><bbbl:label key="lbl_footer_cpwm" language="${pageContext.request.locale.language}" /></li> --%>
                                                <li class="buy-buy-baby"><bbbl:label key="lbl_footer_buybuybaby" language="${pageContext.request.locale.language}" /></li>
                                            </c:when>
                                            <%-- <c:otherwise>
                                                <li class="buy-buy-baby"><bbbl:label key="lbl_footer_buybuybaby" language="${pageContext.request.locale.language}" /></li>
                                                <li class="face-values"><bbbl:label key="lbl_footer_face_value" language="${pageContext.request.locale.language}" /></li>
                                                need logo <li><bbbl:label key="lbl_footer_cpwm" language="${pageContext.request.locale.language}" /></li>
                                                <li class="bed-bath-beyond"><bbbl:label key="lbl_footer_bedbathbeyond" language="${pageContext.request.locale.language}" /></li>
                                            </c:otherwise> --%>
                                        </c:choose>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                    </footer>
                    
                    <div class="legal-footer no-padding">
                        <dsp:getvalueof var="currentYear" bean="CurrentDate.year"/>
                        <jsp:useBean id="placeHolderMapCopyRight" class="java.util.HashMap" scope="request"/>
                        <c:set target="${placeHolderMapCopyRight}" property="currentYear" value="${currentYear}"/>
                        <p>
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
                        </p>
                    </div>
                    <div class="row other-sites hide-for-large-up">
                        <div class="small-12 small-centered columns">
                            <ul>
                                <c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
                                    <c:choose>
                                        <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                            <li class="bed-bath-beyond"><bbbl:label key="lbl_footer_bedbathbeyond" language="${pageContext.request.locale.language}" /></li>
                                            <li class="face-values"><bbbl:label key="lbl_footer_face_value" language="${pageContext.request.locale.language}" /></li>
                                            <%-- need logo <li><bbbl:label key="lbl_footer_cpwm" language="${pageContext.request.locale.language}" /></li> --%>
                                            <li class="buy-buy-baby"><bbbl:label key="lbl_footer_buybuybaby" language="${pageContext.request.locale.language}" /></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="buy-buy-baby"><bbbl:label key="lbl_footer_buybuybaby" language="${pageContext.request.locale.language}" /></li>
                                            <li class="face-values"><bbbl:label key="lbl_footer_face_value" language="${pageContext.request.locale.language}" /></li>
                                            <%-- need logo <li><bbbl:label key="lbl_footer_cpwm" language="${pageContext.request.locale.language}" /></li> --%>
                                            <li class="bed-bath-beyond"><bbbl:label key="lbl_footer_bedbathbeyond" language="${pageContext.request.locale.language}" /></li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </ul>
                        </div>
                    </div>
                    </c:if>
                    <%-- KP COMMENT END --%>

                    <%-- KP COMMENT START: New RWD page structure --%>

                    <!-- close the off-canvas menu -->
                    <a class="exit-off-canvas" href='javascript:void(0);'>&nbsp;</a>

                </div>
            </div>

            <%-- common info modal --%>
            <div id="infoModal" class="reveal-modal tbsRegModal" data-reveal>
                <a class="close-reveal-modal">&#215;</a>
            </div>
            <div id="confirmationModal" class="reveal-modal small" data-reveal>
                <a class="close-reveal-modal">&#215;</a>
            </div>
            <div id="checkoutConfirmationModal" class="reveal-modal small" data-reveal>
                <a class="close-reveal-modal" onclick="$('#storeNumberModal').foundation('reveal', 'open')">&#215;</a>
                <h3>You have entered a Store Number that belongs to a different concept. 
                You will be navigated to the Home Page and all the information entered including items in cart will be discarded.
                <br><br>Do you really want to proceed ?<br></h3>
                <div class="small-12 columns text-center">
                    <input id="storeNumRedirectYesBtn" type="button" class="button" value="yes" />
                    <input id="storeNumRedirectCancelBtn" type="button" class="button secondary" value="cancel" />
                </div>
            </div>
            <div id="bvModal" class="reveal-modal medium bazaarVoice" data-reveal>
                <a class="close-reveal-modal">&#215;</a>
            </div>
            <div id="errorModal" class="reveal-modal small error" data-reveal></div>
            <div id="registryModal" data-reveal-ajax="true" class="reveal-modal small" data-reveal></div>
            <div id="addedToCartModal" class="reveal-modal small" data-reveal></div>
            <div id="genAjaxModal" class="reveal-modal small" data-reveal data-reveal-ajax="true"></div>
            <div id="createRegistryModal" class="reveal-modal small" data-reveal data-reveal-ajax="true"></div>
            <div id="pdpqvModal" class="reveal-modal medium" data-reveal>
                <a class="close-reveal-modal">&#215;</a>
            </div>
            <div id="regUpdateDslModal" data-reveal-ajax="true" class="reveal-modal small" data-reveal>
             <a class="close-reveal-modal">&#215;</a>
             <p><bbbl:label key="lbl_successful_updated" language="${pageContext.request.locale.language}" /></p>
            </div>
            <div id="coRegistrantErrorDialog" class="reveal-modal tbsRegModal" data-reveal>
                <a class="close-reveal-modal">&#215;</a>
                <h2><bbbt:textArea key="txt_reg_coreg_entered_email" language ="${pageContext.request.locale.language}"/></h2>
                <p><bbbt:textArea key="txt_reg_coreg_enter_coreg_email" language ="${pageContext.request.locale.language}"/></p>
                <div>
                    <input class="tbsYesBtn button" type="button" value="ok" />
                </div>
            </div>
             <div id="coRegistrantDialog1" class="reveal-modal tbsRegModal" data-reveal>
                    <a class="close-reveal-modal">&#215;</a>
                    <h2>Co-registrant Access</h2>
                    <bbbt:textArea key="txt_reg_coreg_account_found" language ="${pageContext.request.locale.language}"/>
                    <p id="newCoRegistrantEmail1"></p>
                    <bbbt:textArea key="txt_reg_coreg_manage_msg2" language ="${pageContext.request.locale.language}"/>
                    <div>
                        <input class="tbsYesBtn button" type="button" value="ok" />
                        <input class="tbsCancelBtn button " type="button" value="cancel" />
                    </div>
            </div>
            <div id="coRegistrantDialog3" class="reveal-modal tbsRegModal" data-reveal>
                <a class="close-reveal-modal">&#215;</a>
                <h2>Co-registrant Access</h2>
                <bbbt:textArea key="txt_coregistrant_dialog3" language ="${pageContext.request.locale.language}"/>
                <div>
                    <input class="tbsYesBtn button" type="button" value="ok" />
                </div>
            </div>  
            <div id="coRegistrantDialog2" class="reveal-modal tbsRegModal" data-reveal>
                <a class="close-reveal-modal">&#215;</a>
                <h2>Co-registrant Access</h2>
                    <bbbt:textArea key="txt_reg_coreg_emailsent" language ="${pageContext.request.locale.language}"/>
                    <p id="newCoRegistrantEmail"></p>
                    <bbbt:textArea key="txt_reg_coreg_manage_msg" language ="${pageContext.request.locale.language}"/>
                <div>
                    <input class="tbsNewYesBtn button" type="button" value="ok" />
                    <input class="tbsNewCancelBtn button " type="button" value="cancel" />
                </div>
            </div>
                

            <%-- KP COMMENT START: ajax loader --%>
            <div class="ajax-loader"></div>
            <%-- KP COMMENT END --%>

            <!-- javascript includes -->
            <%-- KP COMMENT START: moving to header...inline js --%>
            <%--
            <script src="${jsPath}/_assets/tbs_assets/js/src/libraries/jquery.js"></script>
            --%>

            <%-- KP COMMENT END 
            <c:set var="minifiedJSFlag" value="false" />--%>
            
            <!-- initialize foundation javascripts -->
            <script>$(document).foundation();</script>
           
            
            

            <%-- KP COMMENT END --%>
            <%-- Body EndTag Content of StaticTemplate/RegistryTemplate --%>
                    ${bodyEndTagContent}
        </body>
    </html>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/includes/pageEnd.jsp#2 $$Change: 635969 $--%>
