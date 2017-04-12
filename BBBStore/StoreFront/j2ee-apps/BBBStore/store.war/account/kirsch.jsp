<dsp:page>
<dsp:importbean bean="/com/bbb/account/droplet/BBBSetCookieDroplet"/>
<dsp:importbean bean="/com/bbb/account/droplet/BBBEncryptionDroplet"/>
<dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet"/>
<dsp:droplet name="BBBEncryptionDroplet">
<dsp:param name="inputvalue" bean="/atg/userprofiling/Profile.id" />
<dsp:param name="operation" value="encrypt" />
<dsp:oparam name="output">
	<dsp:getvalueof var="encryptedProfileId" param="outputvalue"/>
</dsp:oparam>
</dsp:droplet>
<jsp:useBean id="cookies" class="java.util.HashMap" scope="request"/>
<c:set target="${cookies}" property="profileId">${encryptedProfileId}</c:set>
<c:set target="${cookies}" property="siteId"><dsp:valueof bean="/atg/multisite/Site.id"/></c:set>
<dsp:setvalue bean="BBBSetCookieDroplet.cookies" value="${cookies}" />
	<dsp:droplet name="BBBSetCookieDroplet">
<dsp:oparam name="output"></dsp:oparam>
</dsp:droplet>

<c:set var="section" value="accounts" scope="request" />
<c:set var="pageWrapper" value="wishlist myAccount levolorProjects" scope="request" />
<bbb:pageContainer index="false" follow="false">
 	<jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">${section}</jsp:attribute>
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
<jsp:body>

<dsp:droplet name="BBBConfigKeysDroplet">
<dsp:param name="configKey" value="KirschKeys"/>
<dsp:oparam name="output">
	<dsp:getvalueof var="configMap" param="configMap"/>
</dsp:oparam>
</dsp:droplet>
		<div id="content" class="container_12 clearfix" role="main">
			<div class="grid_12 marBottom_20">
				<h1 class="account fl"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/></h1>
				<h3 class="subtitle"><bbbl:label key="lbl_myaccount_levolor_projects" language ="${pageContext.request.locale.language}"/></h3>
                <div class="clear"></div>
			</div>

			<div class="grid_2">
				<c:import url="/account/left_nav.jsp">
					<c:param name="currentPage"><bbbl:label key="lbl_myaccount_levolor_projects" language ="${pageContext.request.locale.language}"/></c:param>
				</c:import>
			</div>
			<c:set var="title_project"><bbbl:label key="lbl_kirsch_view_project_title" language ="${pageContext.request.locale.language}"/></c:set>
			<c:set var="title_order"><bbbl:label key="lbl_kirsch_view_order_title" language ="${pageContext.request.locale.language}"/></c:set>
			<div class="grid_9 prefix_1 info">
				<p class="noMar"><a href="${configMap.projectUrl}" title="${title_project}" class="bold" onclick="javascript:omnitureExternalLinks('kirsch: Navigation for View Your Levolor projects')" ><bbbl:label key="lbl_kirsch_view_project" language ="${pageContext.request.locale.language}"/></a> <span class="smallText"><bbbl:label key="lbl_kirsch_view_project_description" language ="${pageContext.request.locale.language}"/></span></p>
				<p class="noMar"><a href="${configMap.orderUrl}" title="${title_order}" class="bold" onclick="javascript:omnitureExternalLinks('kirsch: Navigation for View Your Levolor orders')"><bbbl:label key="lbl_kirsch_view_order" language ="${pageContext.request.locale.language}"/></a> <span class="smallText"><bbbl:label key="lbl_kirsch_view_order_description" language ="${pageContext.request.locale.language}"/></span></p>
				<p class="noMar"><bbbt:textArea key="textarea_kirsch_promo_content" language ="${pageContext.request.locale.language}"/></p>
			</div>
		</div>
</jsp:body>
 <jsp:attribute name="footerContent">
           <script type="text/javascript">
          if(typeof s !=='undefined') 
           {
           	s.channel = 'My Account';
			s.pageName='My Account>Levolor';// pagename
			s.prop1='My Account';// page title
			s.prop2='My Account';// category level 1 
			s.prop3='My Account';// category level 2
			s.prop6='${pageContext.request.serverName}'; 
			s.eVar9='${pageContext.request.serverName}';
			var s_code=s.t();
			if(s_code)document.write(s_code);		
           }
          function omnitureExternalLinks(data){
          	if (typeof s !== 'undefined') {
          	externalLinks(data);
          	}
          }
        </script>
    </jsp:attribute>
</bbb:pageContainer>
</dsp:page>