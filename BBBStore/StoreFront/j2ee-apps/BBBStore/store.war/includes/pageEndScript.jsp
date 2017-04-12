<dsp:page>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="language" bean="/OriginatingRequest.requestLocale.locale.language"/>
    <dsp:getvalueof var="section" param="section"/>
    <dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
    <dsp:getvalueof var="themeFolder" param="themeFolder"/>
	<dsp:getvalueof var="homepage" param="homepage"/>
	<dsp:getvalueof var="PageType" param="PageType"/>
    <c:set var="productComparison"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>
    <%-- R2.2 Story - 178-a Product Comparison tool Changes | Include drawer on every page except checkout and product comparison page : Start--%>
    <%-- US:DSK:|Sprint1(2.2.1)| [end] --%>
    <c:if test="${(section eq 'search' || section eq 'browse') && productComparison &&  empty homepage}">
    	<dsp:include src="/browse/compare_drawer.jsp"/>
    </c:if>
     <%-- US: DSK: Product BI |Sprint1(2.2.1)| [Start]--> <!-- US:DSK: --%>
                    <c:if test="${section eq 'compare'}">
                                <dsp:include src="/browse/compare_drawer_sticky.jsp"/>
    </c:if>
    
    <%-- R2.2 Story - 178-a Product Comparison tool Changes | Include drawer on every page except checkout and product comparison page : End--%>
    <c:import url="/_includes/script_loader.jsp" />
    <%--
    <script type="text/javascript"> 
    Modernizr.load(['${jsPath}/_assets/global/js/javadev.js']);
    </script>
    --%>
    
</dsp:page>
<%-- @version $Id:  $$Change:  $--%>
