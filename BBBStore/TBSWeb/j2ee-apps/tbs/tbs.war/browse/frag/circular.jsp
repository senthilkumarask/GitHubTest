<dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/CircularLandingDroplet" />
    <bbb:pageContainer>
        <jsp:attribute name="section"></jsp:attribute>
        <jsp:attribute name="pageWrapper">circular flashBookPage</jsp:attribute>
        <dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
    	<dsp:getvalueof var="serverName" vartype="java.lang.String" bean="/OriginatingRequest.serverName"/>
        <jsp:body>		
            <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
            <c:set target="${placeHolderMap}" property="pageName" value="Circular"/>
            <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
            <dsp:getvalueof var="circularId" param="cirID"/>
            <c:set var="s7FlashBookID"><bbbc:config key="circular_cat_name_${currentSiteId}" configName="ThirdPartyURLs" /></c:set>
			<c:if test="${circularId != null}">
                <dsp:droplet name="CircularLandingDroplet">
                    <dsp:param name="pageName" value="CircularLandingPage" />
                    <dsp:oparam name="output">
                        <dsp:param name="landingTemplateVO" param="LandingTemplateVO" />
                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                            <dsp:param param="LandingTemplateVO.circularListings" name="array" />
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="promoId" param="element.id" />
                                <c:if test="${circularId eq promoId}">
                                    <dsp:getvalueof var="s7FlashBookID" param="element.imageMapName" />
                                </c:if>
                            </dsp:oparam>
                        </dsp:droplet>
                    </dsp:oparam>
                </dsp:droplet>
            </c:if>

            <div id="content" class="container_12 clearfix" role="main">
                <div id="s7CatalogViewerContainer" class="grid_12 clearfix textCenter">
                    <!-- add/change required values in the lines/variables below -->
                    <script type="text/javascript" language="javascript">
                        <bbbt:textArea key="txt_circular_s7CatalogOpts" language ="${pageContext.request.locale.language}" />
                        try {
                            var circID = '${s7FlashBookID}',
                                hasCompany = !!(circID.indexOf('/') > -1);

                            s7CatalogOpts.companyName = (hasCompany && circID.split('/')[0]) || s7CatalogOpts.companyName;
                            s7CatalogOpts.catalogID_Mobile = (hasCompany && circID.split('/')[1]) || circID;
                            s7CatalogOpts.catalogID_Web = (hasCompany && circID.split('/')[1]) || circID;
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
            s.channel='Circular';
            s.pageName='Circular';
            s.prop1='Circular';
            s.prop2='Circular';
            s.prop3='Circular';
            s.prop4='Circular';
            s.prop5='';
            s.prop6='';
            s.prop7='';
            s.prop8='';
            s.eVar2='';
            var s_code=s.t();
            if(s_code)document.write(s_code);
        }
    </script>
</dsp:page>