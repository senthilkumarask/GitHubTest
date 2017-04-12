<dsp:page>
	
	<bbb:pageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageWrapper">bridalToolkit useAdobeActiveContent</jsp:attribute>

<dsp:getvalueof var="bridaltoolkt1" param="bridaltoolkit"/>
<dsp:getvalueof param="bridalRegistryId" var="bridalRegistryId"/>
<dsp:getvalueof param="bridalEventType" var="bridalEventType"/>
<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
<c:set target="${placeHolderMap}" property="pageName" value="Bridal Toolkit"/>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
<dsp:getvalueof var="bridaltoolkt" param="bridaltoolkit"/>
<dsp:getvalueof var="registryId" param="registryId"/>
<c:if test="${(not empty registryId) && (!isTransient)}">
	<dsp:droplet name="RegistryInfoDisplayDroplet">
		<dsp:param value="${registryId}" name="registryId" />
		<dsp:param name="displayView" value="owner" />		
		<dsp:oparam name="output">
			<dsp:getvalueof var="bridaltoolkt" param="registrySummaryVO.bridalToolkitToken" />
		</dsp:oparam>
	</dsp:droplet>
</c:if>

<div id="content" class="container_12 clearfix">
            <div class="grid_12 clearfix">
    <div class="noFlash hidden">
        <h3><span class="error"><bbbe:error key="err_flash_plugin_disabled" language="${pageContext.request.locale.language}"/></span></h3>
        <bbbt:textArea key="txt_flash_plugin_disabled_message" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
    </div>
    <script type="text/javascript">
        function JSGotoMyRegistry() {
            window.location.href = '${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${bridalRegistryId}&eventType=${bridalEventType}';
            return;
        }
        var domain = '<bbbc:config key="bridaltoolkit_domain1" configName="ThirdPartyURLs" />'; <%-- this value needs to come from BCC and shall be different for each environment like DEV/QA/PROD --%>
        var domain2 = '<bbbc:config key="bridaltoolkit_domain2" configName="ThirdPartyURLs" />'; <%-- not sure if this is required at all, or should it be different form the first one or not, but this was there in the sample provided so used it as is --%>
        var bridalToolkitID = "11" + "${bridaltoolkt}"; <%-- this value needs to be retrievied from wsdl or something for the currently logged in user --%>
        
        if (AC_FL_RunContent == 0 || DetectFlashVer == 0) {
            alert("This page requires AC_RunActiveContent.js.");
        } else {
            // Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
            var hasProductInstall = DetectFlashVer(6, 0, 65);

            // Version check based upon the values defined in globals
            var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);

            if ( hasProductInstall && !hasRequestedVersion ) {
                // DO NOT MODIFY THE FOLLOWING FOUR LINES
                // Location visited after installation is complete if installation is required
                var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
                var MMredirectURL = window.location;
                document.title = document.title.slice(0, 47) + " - Flash Player Installation";
                var MMdoctitle = document.title;

                AC_FL_RunContent(
                    "src", "/store/bbregistry/bridal_toolkit/playerProductInstall",
                    "FlashVars", "MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&MMdoctitle='+MMdoctitle+"",
                    "width", "976",
                    "height", "600",
                    "align", "middle",
                    "id", 'bridalToolkitProductInstall',
                    "quality", "high",
                    "bgcolor", "#ffffff",
                    "name", 'bridalToolkitProductInstall',
                    "allowScriptAccess","sameDomain",
                    "wmode","transparent",
                    "type", "application/x-shockwave-flash",
                    "pluginspage", "//www.adobe.com/go/getflashplayer"
                );
            } else if ( hasRequestedVersion ) {
                AC_FL_RunContent(
                    "src", "/store/bbregistry/bridal_toolkit/BBBShell",
                    "width", "976",
                    "height", "600",
                    "align", "middle",
                    "id", 'bridalToolkit',
                    "quality", "high",
                    "bgcolor", "#ffffff",
                    "name", 'bridalToolkit',
                    "flashvars", "&BS="+bridalToolkitID+"&Domain="+domain+"&Domain2="+domain2,
                    "allowScriptAccess","sameDomain",
                    "wmode","transparent",
                    "type", "application/x-shockwave-flash",
                    "pluginspage", "//www.adobe.com/go/getflashplayer"
                );
            } else {
                // flash is too old or we can't detect the plugin
                $(".noFlash").removeClass('hidden');
            }
        }
    </script>
    <noscript>
        <div class="noScript">
            <h3><span class="error"><bbbe:error key="err_javascript_disabled" language="${pageContext.request.locale.language}"/></span></h3>
            <bbbt:textArea key="txt_javascript_disabled_message" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
        </div>
    </noscript>
  </div>
        </div>

	</bbb:pageContainer>
</dsp:page>
