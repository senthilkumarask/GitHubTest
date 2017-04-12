<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<div class="clear"></div>
<div id="socialShareDialog" class="width_7 clearfix offScreen">
    <div class="borders borderHoriz borderTop">&nbsp;</div>
    <div class="borders borderHoriz borderBottom">&nbsp;</div>
    <div class="borders borderVert borderLeft">&nbsp;</div>
    <div class="borders borderVert borderRight">&nbsp;</div>
    <div class="corners cornersOne cornerTopLeft">&nbsp;</div>
    <div class="corners cornersTwo cornerTopRight tipTopRight">&nbsp;</div>
    <div class="corners cornersTwo cornerBottomLeft">&nbsp;</div>
    <div class="corners cornersOne cornerBottomRight">&nbsp;</div>
    <div class="spacer" data-socialshare-ui-role="dialogContent">
        <div class="ui-dialog titleWrapper" data-socialshare-ui-role="headingWrapper">
            <div class="ui-dialog-titlebar noMar noPad">
                <p id="shareDialogHeading" data-socialshare-ui-role="heading">&nbsp;</p>
                <a href="#close" data-socialshare-role="button" data-socialshare-action="closeDialog" class="btnCloseWin ui-dialog-titlebar-close posRight posTop" title="Close"><span class="ui-icon ui-icon-closethick">Close</span></a>
            </div>
        </div>
        <div class="clear"></div>
        <div id="shareProdCarousel" class="width_2 marLeft_5 marRight_20 fl clearfix" data-socialshare-ui-role="imageCarouselWrapper">
            <div class="carousel clearfix"> 
                <div class="carouselBody width_2 fl clearfix noMar"> 
                    <div class="clear"></div>
                    <div class="carouselContent width_2 fl clearfix noMar">
                        <ul id="shareProdCarouselImages" class="width_2 fl clearfix noMar" data-socialshare-ui-role="imageCarouselImagesContainer"></ul>
                    </div>
                    <div class="clear"></div>
                    <div class="carouselArrows width_2 fl clearfix noMar" data-socialshare-ui-role="imageCarouselArrows"> 
                        <a class="carouselScrollPrevious noMar fl" title="Previous" href="#">Previous</a>
                        <span class="fl arrowLabel">Change Item</span>
                        <a class="carouselScrollNext noMar fl" title="Next" href="#">Next</a>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
        <div id="sharingOptions" class="width_5 fl omega clearfix">
            <div id="facebookShare" data-socialshare-role="dialog" data-socialshare-network="facebook" class="width_5 clearfix hidden sharingOption">
                <form class="clearfix" name="frmSocialShare" action="#socialShare">
                    <input type="hidden" name="url" value="" data-socialshare-type="param" data-socialshare-paramname="u" data-socialshare-datafield="sharedPageUrl"/>
                    <input type="hidden" name="p_title" value="" data-socialshare-type="param" data-socialshare-paramname="p[title]" data-socialshare-datafield="sharedPageTitle"/>
                    <input type="hidden" name="p_url" value="" data-socialshare-type="param" data-socialshare-paramname="p[url]" data-socialshare-datafield="sharedPageUrl"/>
                    <input type="hidden" name="p_images" value="" data-socialshare-type="param" data-socialshare-paramname="p[images][0]" data-socialshare-datafield="sharedThumbImage"/>
                    <input type="hidden" name="p_summary" value="" data-socialshare-type="param" data-socialshare-paramname="p[summary]" data-socialshare-datafield="sharedDescriptionText"/>
                    <p class="prodTitle bold noMarTop" data-socialshare-ui-role="text" data-socialshare-datafield="sharedPageTitle">&nbsp;</p>
                    <p class="shareMessage"><a href="${contextPath}" target="_blank"><bbbl:label key='lbl_pinterest_Share_Text' language="${pageContext.request.locale.language}" /></a></p>
                    <p class="prodDesc bulletsReset" data-socialshare-ui-role="text" data-socialshare-datafield="sharedDescriptionText">&nbsp;</p>
                    <div class="cb buttonset clearfix">
                        <div class="button button_active" data-socialshare-role="shareButtonWrapper">
                            <input type="button" value="POST" name="btnShareFacebook" class="btnShareIt" data-socialshare-role="button" data-socialshare-action="share" role="button" aria-pressed="false" />
                        </div>
                        <a href="#socialShareDialog" class="buttonTextLink btnCancelShare" data-socialshare-role="button" data-socialshare-action="closeDialog">Cancel</a>
                        <div class="clear"></div>
                    </div>
                </form>
            </div>
            <div id="twitterShare" data-socialshare-role="dialog" data-socialshare-network="twitter" class="width_5 clearfix hidden sharingOption">
                <form class="clearfix" name="frmSocialShare" action="#socialShare">
                    <input type="hidden" name="url" value="" data-socialshare-type="param" data-socialshare-paramname="url" data-socialshare-datafield="sharedPageBitlyUrl"/>
                    <!-- <input type="hidden" name="original_referer" value="" data-socialshare-type="param" data-socialshare-paramname="original_referer" data-socialshare-datafield="sharedPageUrl"/> -->
                    <input type="hidden" name="related" value="" data-socialshare-type="param" data-socialshare-paramname="related" data-socialshare-datafield="sharedPageRelated"/>
                    <!-- <input type="hidden" name="via" value="" data-socialshare-type="param" data-socialshare-paramname="via" data-socialshare-datafieldcustom="twitterHandle"/> -->
                    <textarea name="txtShareMessage" class="txtShareIt" data-socialshare-limitedtextinput="true" data-socialshare-type="param" data-socialshare-paramname="text" data-socialshare-datafieldcustom="twitterTweet"></textarea>
                    <p class="prodURL offScreen" data-socialshare-ui-role="text" data-socialshare-datafield="sharedPageBitlyUrl">&nbsp;</p>
                    <div class="cb buttonset clearfix">
                        <div class="button button_active" data-socialshare-role="shareButtonWrapper">
                            <input type="button" value="TWEET" name="btnShareTwitter" class="btnShareIt" data-socialshare-role="button" data-socialshare-action="share" role="button" aria-pressed="false"/>
                        </div>
                        <a href="#socialShareDialog" class="buttonTextLink btnCancelShare" data-socialshare-role="button" data-socialshare-action="closeDialog">Cancel</a>
                        <div class="clear"></div>
                    </div>
                    <p class="shareMessage">Product Link has been included in the tweet, and character count has been accounted for.</p>
                </form>
            </div>
            <div id="pinterestShare" data-socialshare-role="dialog" data-socialshare-network="pinterest" class="width_5 clearfix hidden sharingOption">
                <form class="clearfix" name="frmSocialShare" action="#socialShare">
                    <input type="hidden" name="title" value="" data-socialshare-type="param" data-socialshare-paramname="title" data-socialshare-datafield="sharedPageTitle"/>
                    <input type="hidden" name="url" value="" data-socialshare-type="param" data-socialshare-paramname="url" data-socialshare-datafield="sharedPageUrl"/>
                    <input type="hidden" name="media" value="" data-socialshare-type="param" data-socialshare-paramname="media" data-socialshare-datafield="sharedThumbImage"/>
                    <textarea name="txtShareMessage" class="txtShareIt" data-socialshare-limitedtextinput="true" data-socialshare-type="param" data-socialshare-paramname="description" data-socialshare-datafieldcustom="pinterestText"></textarea>
                    <div class="cb buttonset clearfix">
                        <div class="button button_active" data-socialshare-role="shareButtonWrapper">
                            <input type="button" value="PIN THIS" name="btnSharePinterest" class="btnShareIt" data-socialshare-role="button" data-socialshare-action="share" role="button" aria-pressed="false"/>
                        </div>
                        <a href="#socialShareDialog" class="buttonTextLink btnCancelShare" data-socialshare-role="button" data-socialshare-action="closeDialog">Cancel</a>
                        <div class="clear"></div>
                    </div>
                </form>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
</div>
<div class="clear"></div>