<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	
	<%-- Validate external parameters --%>
	<dsp:droplet name="ValidateParametersDroplet">
	    <dsp:param value="bookId" name="paramArray" />
	    <dsp:param value="${param.bookID}" name="paramsValuesArray" />
	    <dsp:oparam name="error">
	      <dsp:droplet name="RedirectDroplet">
	        <dsp:param name="url" value="/404.jsp" />
	      </dsp:droplet>
	    </dsp:oparam>
    </dsp:droplet>
     
<bbb:pageContainer>
	<jsp:attribute name="section">browse</jsp:attribute>
	<jsp:attribute name="pageWrapper">babyBook flashBookPage</jsp:attribute>
<jsp:body>
     <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
	 <c:set target="${placeHolderMap}" property="pageName" value="Baby Book"/>
        <div id="content" class="container_12 clearfix noPad" role="main">
			<div class="grid_12 clearfix">
				<h1><bbbl:label key="lbl_babybook_howbook" language="${pageContext.request.locale.language}" /></h1>
			</div>
            <div class="clear"></div>
            <div id="s7CatalogViewerContainer" class="grid_12 clearfix textCenter">
                <%-- add/change required values in the lines/variables below --%>
                <script type="text/javascript" language="javascript">
                    <bbbt:textArea key="txt_circular_s7CatalogOpts" language ="${pageContext.request.locale.language}" />
                    try {
                        /*var circID = '${param.bookID}',
                            hasCompany = !!(circID.indexOf('/') > -1);

                        s7CatalogOpts.companyName = (hasCompany && circID.split('/')[0]) || s7CatalogOpts.companyName;
                        s7CatalogOpts.catalogID_Mobile = (hasCompany && circID.split('/')[1]) || circID;
                        s7CatalogOpts.catalogID_Web = (hasCompany && circID.split('/')[1]) || circID;*/
                        s7CatalogOpts.companyName = '<bbbl:label key="lbl_registrybook_catalog_companyname" language="${pageContext.request.locale.language}" />';
                        s7CatalogOpts.catalogID_Mobile = '<bbbl:label key="lbl_registrybook_catalog_mobilename" language="${pageContext.request.locale.language}" />';
                        s7CatalogOpts.catalogID_Web = '<bbbl:label key="lbl_registrybook_catalog_webname" language="${pageContext.request.locale.language}" />';
                    } catch(e) {}
                </script>

                <bbbt:textArea key="txt_circular_s7CoreCode" language ="${pageContext.request.locale.language}" />

                <noscript>
                    <div class="noScript">
                        <h3><span class="error"><bbbe:error key="err_javascript_disabled" language="${pageContext.request.locale.language}"/></span></h3>
                        <bbbt:textArea key="txt_javascript_disabled_message" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
                    </div>
                </noscript>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
    

       </jsp:body>
	</bbb:pageContainer>
	<script type="text/javascript">
	   	if(typeof s !=='undefined') {
		s.channel = 'Registry';
		s.pageName='Baby Book - Look Inside';// pagename
		s.prop1='Registry';// page title
		s.prop2='Registry';// category level 1 
		s.prop3='Registry';// category level 2
		s.prop6='${pageContext.request.serverName}'; 
		s.eVar9='${pageContext.request.serverName}';
		var s_code=s.t();
			if(s_code)document.write(s_code);		
         }
	</script>
</dsp:page>
