<c:set var="section" value="cms" scope="request" />
<c:set var="pageWrapper" value="sizingGuides" scope="request" />

<dsp:page>
	<dsp:importbean bean="/com/bbb/cms/droplet/GuidesLongDescDroplet"/>
	<dsp:getvalueof var="guideId" param="guideId"/>
	<dsp:droplet name="GuidesLongDescDroplet">
		<dsp:param name="guideId" value="${guideId}"/>
		<dsp:oparam name="output">
			<bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" >
				<jsp:body>
					<dsp:getvalueof var="title" param="guidesLongDesc.title"/>
					<dsp:getvalueof var="longDescription" param="guidesLongDesc.longDescription"/>
					<div id="content" class="container_12 clearfix" role="main">
						<div id="cmsPageHead" class="grid_12 clearfix">
							<h1>${title}</h1>
						</div>
						<div id="cmsPageContent">
							${longDescription}	 
						</div>
					</div>
				</jsp:body>
			</bbb:pageContainer>
		</dsp:oparam>
		<dsp:oparam name="empty">
			<dsp:include page="../404.jsp" flush="true"/>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
	
	 