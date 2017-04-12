<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  invalidateOrderError
 *
 *  DESCRIPTION: page for error page
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>

  
    <bbb:pageContainer index="false" follow="false" >
      <jsp:attribute name="SEOTagRenderer">
      <dsp:include page="/global/gadgets/metaDetails.jsp" flush="true" />
    </jsp:attribute>
    
    <jsp:attribute name="section">cms</jsp:attribute>
    <jsp:attribute name="pageWrapper">invalidateOrderError</jsp:attribute>
    <jsp:attribute name="footerContent">
           <script>
           if(typeof s !=='undefined') {
            s.pageName='invalidate order error: ' + document.location.href;
            s.pageType='errorPage';
            var s_code=s.t();
            if(s_code)document.write(s_code);    
           }
        </script>
    </jsp:attribute>
    
    <jsp:body>
         <div id="content" class="container_12 clearfix">

            <div id="cmsPageContent"><div class="grid_12">
                     <span class="error"><bbbt:textArea key="txt_error_invalidOrder" language="${pageContext.request.locale.language}"/></span>
            </div></div>
        </div>
    </jsp:body>
  </bbb:pageContainer>  
</dsp:page>