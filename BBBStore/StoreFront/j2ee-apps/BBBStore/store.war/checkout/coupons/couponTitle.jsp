<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  couponTitle.jsp
 *
 *  DESCRIPTION: coupon page title is rendered
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>    
    <div id="subHeader" class="grid_12 clearfix">
            <h2 class="section"><bbbl:label key="lbl_bread_crumb_billing" language="${pageContext.request.locale.language}" />:&nbsp;<span class="subSection"><bbbl:label key="lbl_coupon_pagetitle" language="${pageContext.request.locale.language}" /></span></h2>
    </div>
</dsp:page>