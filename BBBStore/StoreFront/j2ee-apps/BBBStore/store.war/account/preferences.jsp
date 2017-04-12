<dsp:page>
<c:set var="section" value="accounts" scope="request" />
<c:set var="pageWrapper" value="wishlist myAccount hhPreferences" scope="request" />
<bbb:pageContainer index="false" follow="false">
	<jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
	<jsp:attribute name="section">${section}</jsp:attribute>
	<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
	<jsp:body>
		<div id="content" class="container_12 clearfix" role="main">
			<div class="grid_12">
				<h1 class="account fl"><bbbl:label key="lbl_prefrences_myaccount" language ="${pageContext.request.locale.language}"/></h1>
				<h3 class="subtitle"><bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/></h3>
                <div class="clear"></div>
			</div>

			<div class="grid_2">
			    <c:import url="/account/left_nav.jsp">
		    		 <c:param name="currentPage"><bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/></c:param>
				</c:import>
			</div>

			<dsp:getvalueof var="dataCenter" bean="/atg/dynamo/service/IdGenerator.dcPrefix"/>
			
			 <c:forEach items="${cookie}" var="bbbCookie">				 
			     <c:if test="${bbbCookie.value.name == 'HarteHanks'}">  
			     <c:set var="dc">${bbbCookie.value.value}</c:set>
			     </c:if>
     		 </c:forEach>
			 
			 
		 <c:if test="${empty dc}">
				<c:set var="dc">1</c:set> 
			<c:choose>
				<c:when test="${dataCenter=='DC1'}">
					<c:set var="dc">1</c:set>
				</c:when>
				<c:when test="${dataCenter=='DC2'}">
					<c:set var="dc">2</c:set>
				</c:when>
				<c:when test="${dataCenter=='STG1'}">
					<c:set var="dc">s1</c:set>
				</c:when>
				<c:when test="${dataCenter=='STG2'}">
					<c:set var="dc">s2</c:set>
				</c:when>
			</c:choose>
		</c:if>
			
			<div class="grid_10 info">
				<c:if test="${HarteHanksOn}">
            	<dsp:droplet name="/com/bbb/account/BBBStoreSessionDroplet">
            		<dsp:param name="currSite" value="${currentSiteId}"/>
            		<dsp:oparam name="success">
            			<dsp:getvalueof  id="URL" param="URL"/>
            			<div class="width_10">
				            <iframe type="some_value_to_prevent_js_error_on_ie7" id="hhIframe" title='<bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/>' src='${URL}&dc=${dc}' width="1000" height="1450" scrolling="no" frameBorder="0" class="noOverflow"></iframe>
						</div>
            		</dsp:oparam>
            		<dsp:oparam name="fail">
            			<div class="width_10">
            				<bbbl:label key="lbl_prefrences_info" language ="${pageContext.request.locale.language}"/>
            			</div>	
            		</dsp:oparam>
            	</dsp:droplet>
            	</c:if>
			</div>
		</div>
	</jsp:body>	
<jsp:attribute name="footerContent">
<script type="text/javascript">
	if (typeof s !== 'undefined') {
		s.pageName = 'My Account>Preferences';
		s.channel = 'My Account';
		s.prop1='My Account';
		s.prop2='My Account';
		s.prop3='My Account';
		s.prop6='${pageContext.request.serverName}'; 
		s.eVar9='${pageContext.request.serverName}';
		var s_code = s.t();
		if (s_code)
			document.write(s_code);
	}
</script>
</jsp:attribute>	
</bbb:pageContainer>
</dsp:page>
