<dsp:page>

	<%-- Page Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="language" bean="/OriginatingRequest.requestLocale.locale.language"/>
	<dsp:getvalueof var="section" param="section"/>
	<dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
	<dsp:getvalueof var="themeFolder" param="themeFolder"/>

	<c:if test="${section eq 'search' || section eq 'browse'}">
		<dsp:include src="/browse/compare_drawer.jsp"/>
	</c:if>

	<%-- KP COMMENT START: jdj - don't think we need this in our version of compare --%>
	<%--
	<!-- US:DSK:|Sprint1(2.2.1)| [end] -->
	<c:if test="${section eq 'search' || section eq 'browse'}">
		<dsp:include src="/browse/compare_drawer.jsp"/>
	</c:if>
	<!-- US: DSK: Product BI |Sprint1(2.2.1)| [Start]--> <!-- US:DSK: -->
	<c:if test="${section eq 'compare'}">
		<dsp:include src="/browse/compare_drawer_sticky.jsp"/>
	</c:if>
	--%>
	<%-- KP COMMENT END --%>

	<%-- R2.2 Story - 178-a Product Comparison tool Changes | Include drawer on every page except checkout and product comparison page : End--%>

	<dsp:include src="/_includes/script_loader.jsp"/>

	<%--
	<script type="text/javascript">
	Modernizr.load(['"${assetDomainName}${contextPath}/resources/js/src/legacy/javadev.js']);
	</script>
	--%>

</dsp:page>
<%-- @version $Id:  $$Change:  $--%>
