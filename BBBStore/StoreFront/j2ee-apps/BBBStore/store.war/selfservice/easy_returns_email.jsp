<%-- ====================== Description===================
/**
* This page is used to display easy return form in which customer inputs his address, city and state or zip code 
* and other details to generate the labels.
* @author Seema
**/
--%>

<dsp:page>
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>  
	<dsp:importbean var="EasyReturnsFormHandler" bean="/com/bbb/selfservice/EasyReturnsFormHandler"/>
	<c:set var="section" value="selfService" scope="request" />
	<c:set var="pageWrapper" value="easyReturnsForm" scope="request" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<bbb:pageContainer titleKey="lbl_easy_return_form_title">
		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

		<jsp:body>		
<%--Jsp body starts from here--%>
		<dsp:getvalueof id="currentSiteId" bean="Site.id" />
		<c:set var="BedBathCanadaSite">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<div id="content" class="container_12 clearfix" role="main">
						
						<dsp:valueof bean="EasyReturnsFormHandler.message" valueishtml="true"/>	
	                           
		</div>		
<%--Jsp body Ends here--%>	
	</jsp:body>
	<jsp:attribute name="footerContent">
		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
