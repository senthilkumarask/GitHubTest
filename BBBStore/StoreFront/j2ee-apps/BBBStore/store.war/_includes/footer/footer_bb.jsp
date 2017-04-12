<%@ taglib uri="/WEB-INF/tld/BBBLblTxt.tld" prefix="bbb"%>
<div id="footer" class="clearfix">
    <div class="topBorder"></div>
    <div class="container_12">
        <div class="grid_2">
            <div class="box borderRight">
                <h3> <bbbl:label key="lbl_email_sign_up" language="${pageContext.request.locale.language}" /> <span class="subtext"><bbbl:label key="lbl_special_offers" language="${pageContext.request.locale.language}" /></span></h3>
                <form class="post form" action="#" method="post">

                    <div class="input clearfix">
                        <div class="label">
                            <label id="lbltxtSignupSpecialEmail" for="txtSignupSpecialEmail"><bbbl:label key="lbl_special_email" language="${pageContext.request.locale.language}" /></label>
                        </div>
                        <div class="text">
                            <div class="">
                                <input type="text" name="txtSignupSpecialEmailName" value="" id="txtSignupSpecialEmail" aria-required="true" aria-labelledby="lbltxtSignupSpecialEmail errortxtSignupSpecialEmail">
                            </div>
                        </div>
                        <div class="error"></div>
                    </div>
                    
                    <div class="clear"></div>
                    <div class="input submit small">
                        <div class="button">
                            <input type="submit" name="" value="Submit" role="button" aria-pressed="false" />
                        </div>
                    </div>
                    <div class="clear"></div>
                </form>
                <p><a href="#"><bbbl:label key="lbl_subscribe_privacy_policy" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_your_privacy" language="${pageContext.request.locale.language}" /></a></p>
            </div>
        </div>
        <div class="grid_2">
            <div class="box borderRight">
                <h3><bbbl:label key="lbl_satisfaction_gurantee" language="${pageContext.request.locale.language}" /></h3>
                <p>
                    <bbbl:label key="lbl_return_anything" language="${pageContext.request.locale.language}" />
                </p>
            </div>
        </div>
        <div class="grid_2">
            <div class="box borderRight">
                <h3><bbbl:label key="lbl_footer_company_info" language="${pageContext.request.locale.language}" /></h3>
                <p><a href="#"><bbbl:label key="lbl_about_us" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_corporate_responsibilty" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_media_relations" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_investor_relations" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_Careers" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_terms_of_use" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_header_findastore" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_corporate_sales" language="${pageContext.request.locale.language}" /></a></p>
            </div>
        </div>
        <div class="grid_2">
            <div class="box borderRight">
                <h3><bbbl:label key="lbl_footer_shopping_tools" language="${pageContext.request.locale.language}" /></h3>
                <p><a href="#"><bbbl:label key="lbl_guides_breadcrub" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_search_tab_text_videos" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_glossary" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_shop_personalized_inv" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_shop_gift_cards" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_shop_clearance" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_header_shopbybrand" language="${pageContext.request.locale.language}" /></a></p>
            </div>
        </div>
        <div class="grid_2">
            <div class="box borderRight">
                <h3><bbbl:label key="lbl_footer_customer_service" language="${pageContext.request.locale.language}" /></h3>
                <p><a href="#"><bbbl:label key="lbl_contactus_contactus" language="${pageContext.request.locale.language}" />/<bbbl:label key="lbl_feedback" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_easy_return_form_heading_1" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_shipping_info" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_spc_preview_giftpackaging" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_faqs" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_checkoutconfirmation_createaccount" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_giftcard_checkbal_heading" language="${pageContext.request.locale.language}" /></a></p>
                <p><a href="#"><bbbl:label key="lbl_tbs_order_inquiry" language="${pageContext.request.locale.language}" /></a></p>
                <div class="safetyRecalls">
                    <a href="#"><bbbl:label key="lbl_safety_recalls" language="${pageContext.request.locale.language}" /></a>
                </div>
            </div>
        </div>
        <div class="grid_2">
            <div class="box">
                <h3><bbbl:label key="lbl_follow_us" language="${pageContext.request.locale.language}" /></h3>
                <div class="facebookIconFollow socialIconFollow">
                    <a href="www.facebook.com" target="_blank"><bbbl:label key="lbl_facebook" language="${pageContext.request.locale.language}" /></a>
                </div>
                <div class="twitterIconFollow socialIconFollow">
                    <a href="www.twitter.com" target="_blank"><bbbl:label key="lbl_twitter" language="${pageContext.request.locale.language}" /></a>
                </div>
            </div>
        </div>
    </div>
    <div class="clear pushDown"></div>
    <div class="container_12 pushDown">
        <div class="grid_3 alpha">
            <div id="otherSitesTitle"><p><bbbl:label key="lbl_footer_visit_other_sites" language="${pageContext.request.locale.language}" /></p></div>
        </div>
        <div class="grid_2">
            <div id="logoHarmon"><a href="face_value.html"><bbbl:label key="lbl_face_value" language="${pageContext.request.locale.language}" /></a></div>
        </div>
        <div class="grid_2">
            <div id="logoBb"><a href="index.html"><bbbl:label key="lbl_bb_baby" language="${pageContext.request.locale.language}" /></a></div>
        </div>
        <div class="grid_5 omega">
            <div id="logoBy"><a href="bed_bath_beyond.html"><bbbl:label key="lbl_bbb_desktop" language="${pageContext.request.locale.language}" /></a></div>
        </div>
    </div>
    <div class="clear"></div>
</div>
<div id="footerPs">
    <div class="container_12 clearfix">
        <div class="grid_12 clearfix">
            ©1999-2011 <bbbl:label key="lbl_bbb_rights_reserved" language="${pageContext.request.locale.language}" />
        </div>
    </div>
</div>