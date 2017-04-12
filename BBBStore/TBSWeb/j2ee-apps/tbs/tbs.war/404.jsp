<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  404.jsp
 *
 *  DESCRIPTION: page for error page
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>

  <%response.setStatus(404);%>
    <bbb:pageContainer index="false" follow="false" >
      <jsp:attribute name="SEOTagRenderer">
      <dsp:include page="/global/gadgets/metaDetails.jsp" flush="true" />
    </jsp:attribute>
    
    <jsp:attribute name="section">cms</jsp:attribute>
    <jsp:attribute name="pageWrapper">errorPages 404</jsp:attribute>
    <jsp:attribute name="footerContent">
           <script type="text/javascript">
           if(typeof s !=='undefined') {
            s.pageName='error code 404: ' + document.location.href; 
            s.pageType='errorPage';
            s.channel = s.prop1 = s.prop2 = s.prop3 = "Error Page";
            var s_code=s.t();
            if(s_code)document.write(s_code);    
           }
        </script>
    </jsp:attribute>
    
    <jsp:body>
    	<div class="row">
	         <div id="content" class="container_12 clearfix" role="main">
	
	            <div id="cmsPageContent">
	                <bbbl:textArea key="txt_404_text" language="${language}"/>                    
	            </div>
	        </div>
    	</div>
       
    </jsp:body>
  </bbb:pageContainer>  
</dsp:page>


