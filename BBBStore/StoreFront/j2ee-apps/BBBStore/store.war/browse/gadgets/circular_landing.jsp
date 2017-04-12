<dsp:page>
<dsp:importbean bean="/atg/multisite/Site" />
<dsp:importbean bean="/com/bbb/cms/droplet/CircularLandingDroplet" />
<dsp:getvalueof var="siteId" bean="Site.id" />
<bbb:pageContainer>
	<jsp:attribute name="section">browse</jsp:attribute>
	<jsp:attribute name="pageWrapper">circular circularLanding</jsp:attribute>
<jsp:body>
				<dsp:droplet name="CircularLandingDroplet">
					<dsp:param name="pageName" value="CircularLandingPage" />
					<dsp:oparam name="output">
						<dsp:param name="landingTemplateVO" param="LandingTemplateVO" />
						<dsp:getvalueof var="pageTitle" param="LandingTemplateVO.pageTitle" />
						<%-- content --%>
						<div id="content" class="container_12 clearfix" role="main">
							<div id="circHeader" class="grid_12 clearfix">
								<h1>${pageTitle}</h1>
								<p><bbbl:label key="lbl_circularlanding_header" language="${pageContext.request.locale.language}" /></p>
								<hr />
							</div>

							<div id="circLinks" class="grid_12 clearfix">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="LandingTemplateVO.circularListings" name="array" />
									<dsp:oparam name="output">
                                        <dsp:getvalueof var="count" param="count" />
                                        <dsp:getvalueof var="size" param="size" />
                                        <c:choose>
                                            <c:when test="${count%2==1}">
                                                <div class="circRow clearfix">
                                                <c:set var="circClass" value="alpha circLeft"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="circClass" value="omega circRight"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${count>0}">
                                            <div class="grid_5 ${circClass}">
                                                <div class="circLink">
                                                    <dsp:getvalueof var="imageURL" param="element.imageURL" />
                                                    <dsp:getvalueof var="imageAlt" param="element.imageAltText" />
                                                    <dsp:getvalueof var="promoId" param="element.id" />
                                                    <dsp:getvalueof var="description" param="element.promoBoxContent" />
                                                    <a href="${contextPath}/page/Circular/${promoId}" class="popupIframe" title="${imageAlt}">
                                                        <img class="circImage" alt="${imageAlt}" title="${imageAlt}" src="${imageURL}" width="290" height="325" />
                                                        <div class="clear"></div>
                                                        <span class="circName">${imageAlt}</span> 
                                                    </a>
                                                </div>
                                            </div>
                                        </c:if>
										<c:if test="${count%2==0 || size == count}">
											</div>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
							</div>
						</div>
					</dsp:oparam>
				</dsp:droplet>
</jsp:body>
</bbb:pageContainer>
  		<script type="text/javascript">
           if(typeof s !=='undefined') {
			s.channel='Circular';
			s.pageName='Circular Landing Page';
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