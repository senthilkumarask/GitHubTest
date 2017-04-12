<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  serverError.jsp
 *
 *  DESCRIPTION: page for Internal server error page
 *
 *  HISTORY:
 *  Aug 3, 2012  Initial version

--%>
<dsp:page>

  
    <bbb:pageContainer index="false" follow="false" >
          
    <jsp:attribute name="section">cms</jsp:attribute>
    <jsp:attribute name="pageWrapper">errorPages serverErrorPages 500</jsp:attribute>
    <jsp:attribute name="footerContent">
           <script type="text/javascript">
           if(typeof s !=='undefined') {
            s.pageName='error code 500: ' + document.location.href; 
            s.pageType='errorPage';
            var s_code=s.t();
            if(s_code)document.write(s_code);    
           }
        </script>
    </jsp:attribute>
    
    <jsp:body>
    	<div class="row">
	         <div id="content" class="container_12 clearfix" role="main">
	            <div id="cmsPageContent">
	                <bbbl:textArea key="txt_500_text" language="${language}"/>                   
		            <div id="error" class="hidden">
						<ul>
							<li id="exception"> <c:out value="${pageContext.exception}" /> </li>
							<li id="stacktrace_top_three">
							<c:forEach var="trace" 
			         			items="${pageContext.exception.stackTrace}" end="2">
									<c:out value="${trace}" />
							</c:forEach>		
							<li>
						</ul>
					</div>                    
	        	</div>
	        </div>
        </div>
       
    </jsp:body>
  </bbb:pageContainer>  
</dsp:page>
<%--@version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/global/serverError.jsp#1 $$Change: 633540 $ --%>