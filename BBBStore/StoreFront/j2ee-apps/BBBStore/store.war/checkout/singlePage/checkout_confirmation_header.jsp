<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  checkout_header.jsp
 *
 *  DESCRIPTION: header for checkout pages
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
	<dsp:include page="/_includes/third_party_on_of_tags.jsp"/>
    <dsp:getvalueof var="step" param="step"/>
    <dsp:getvalueof var="link" param="link"/>
     <div class="container_12 clearfix" id="header">
    <dsp:include page="checkoutLogo.jsp" />
	   <dsp:include page="/common/click2chatlink.jsp">
            <dsp:param name="pageId" param="pageId"/>
        </dsp:include>
	 </div>
</dsp:page>