<dsp:page>
	<dsp:getvalueof var="bodyEndTagContent" param="bodyEndTagContent" />
	<%-- BBBSL-4343 <dsp:include page="/_includes/double_click_general_tag.jsp"></dsp:include> --%>
	<c:set var="helixWebChatFlag"><bbbc:config key="helixWebChatFlag" configName="FlagDrivenFunctions" /></c:set>
	<c:if test="${!helixWebChatFlag}">
		<script type="text/javascript">
			function geturlparams( name, url ) {
				if (!url) url = location.href;
				name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				var regexS = "[\\?&]"+name+"=([^&#]*)",
				regex = new RegExp( regexS ),
				results = regex.exec( url );
				return results == null ? null : results[1];
			}
			var isHelixChatOn = geturlparams('helixChatOn');
			if(isHelixChatOn === "true" || isHelixChatOn === true  ) {
				$('body').addClass('helixChatOn');
			}
		</script>
	</c:if>
	
	<dsp:include page="/includes/pageEndScript.jsp" />
	<dsp:getvalueof var="footerContent" param="footerLastContent" />
	<c:if test="${not empty footerContent}">
		<c:out value="${footerContent}" escapeXml="false" />
	</c:if>
	
	<%-- Body EndTag Content of StaticTemplate/RegistryTemplate --%>
		${bodyEndTagContent}
	</body>
	</html>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/includes/pageEnd.jsp#2 $$Change: 635969 $--%>
