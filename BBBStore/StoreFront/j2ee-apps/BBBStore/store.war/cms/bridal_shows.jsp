<dsp:page>
	<dsp:importbean bean="/com/bbb/cms/droplet/BridalShowStateDroplet" />
	<dsp:importbean bean="/com/bbb/cms/droplet/BridalShowDetailDropletState" />
	<dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:getvalueof var="siteId" bean="Site.id" />
    <dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
	<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>

     <c:if test="${siteId eq 'BedBathUS' || siteId eq 'BedBathCanada'}">
	   <c:set var="pageVariation" value="br" scope="request" />
     </c:if>
	 
	<bbb:pageContainer>
		<jsp:attribute name="section">cms</jsp:attribute>
		<jsp:attribute name="pageWrapper">bridal bridalShows useFB</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<dsp:getvalueof id="siteURL" bean="Site.productionURL"/>
		<jsp:body >

			<div id="content" class="container_12 clearfix" role="main">

			<div class="breadcrumbs grid_12">
			
			<a href="${scheme}://${servername}"><bbbl:label key="lbl_pdp_breadcrumb_home" language="${pageContext.request.locale.language}"/></a> <span class="rightCarrot">
				&gt;
			</span>
	       <c:choose>
		    <c:when test="${siteId eq 'BedBathUS' || siteId eq 'BedBathCanada'}">
		     <c:set var="lbl_reg_feature_bridal_reg">
			<bbbl:label key="lbl_reg_feature_bridal_reg" language="${pageContext.request.locale.language}" />
			</c:set>
		      <a href="${contextPath}/page/Registry"  title="${lbl_reg_feature_bridal_reg}"><bbbl:label key="lbl_reg_feature_bridal_reg" language ="${pageContext.request.locale.language}"/></a> <span class="rightCarrot">
		    </c:when>
		    <c:otherwise>
		     <c:set var="lbl_reg_feature_baby_reg">
			<bbbl:label key="lbl_reg_feature_baby_reg" language="${pageContext.request.locale.language}" />
			</c:set>
		      <a href="${contextPath}/page/BabyRegistry"  title="${lbl_reg_feature_baby_reg}"><bbbl:label key="lbl_reg_feature_baby_reg" language ="${pageContext.request.locale.language}"/></a>
		    </c:otherwise>
		    </c:choose>
					
		 	&gt;
				</span> <span class="bold"><bbbl:label key="lbl_leftnavguide_bridalshows" language ="${pageContext.request.locale.language}"/></span>
			</div>
					 <dsp:include page="/cms/left_navigation.jsp" />

			<div id="cmsPageHead" class="grid_9">
					<div class="clearfix">
						<h1 class="fl"><bbbl:label key="lbl_bridalshow_title" language="<c:out param='${pageContext.request.locale.language}'/>"/></h1>
						<div class="fl share">
							<a href="#" class="print" title="Print"></a>
							<a href="#" class="email" title="Email"></a>
							  <c:if test="${FBOn}">
                                <!--[if IE 7]>
                                    <dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
                                        <dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
                                        <dsp:oparam name="output">
                                            <dsp:getvalueof id="encodedURL" param="encodedURL"/>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                    <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
                                        <dsp:param name="configKey" value="FBAppIdKeys"/>
                                        <dsp:oparam name="output">
                                            <dsp:getvalueof var="fbAppIDConfigMap" param="configMap"/>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                    <div class="fb-like fl width_4">
                                        <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=standard&amp;width=312&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:312px; height:24px;" allowTransparency="true"></iframe>
                                    </div>
                                <![endif]-->
                                <!--[if !IE 7]><!-->
                                    <div class="fb-like fl width_4" data-send="false" data-width="312" data-show-faces="false"></div>
                                <!--<![endif]-->
                            </c:if>
						</div>
					</div>
					<p><bbbt:textArea key="txt_bridalshow_shortdesc" language="<c:out param='${pageContext.request.locale.language}'/>"/>  </p>

				</div>

				<div id="cmsPageContent" class="grid_9 clearfix">
					<div class="showsIn input alpha grid_4">
						<dsp:droplet name="BridalShowStateDroplet">
							<dsp:oparam name="output">

								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" param="stateMap"/>
									<dsp:getvalueof var="stateMap" param="stateMap"/>									

									<dsp:getvalueof var="count" vartype="java.lang.String"	param="count" />
										<c:choose>
									      <c:when test="${count =='1'}">
									      	<c:set var="select" value="selected"/>
									      </c:when>
									      <c:otherwise>
									      	<c:set var="select" value=""/>
									      </c:otherwise>
									    </c:choose>
										<dsp:oparam name="outputStart">
										<div class="label grid_1 alpha">
										<label id="lblshowType" for="showType"><bbbl:label key="lbl_bridalshow_showin" language="<c:out param='${pageContext.request.locale.language}'/>"/> </label>
										</div>

										<form id="bridalShw" action="bridal_shows.jsp" method="post">
											<div class="select grid_3 omega">
												<select id="showType" name="showType" class="uniform" aria-required="false" aria-labelledby="lblshowType" >
												<option value="ALL" >ALL</option>
												</dsp:oparam>
												<dsp:oparam name="output">
													<dsp:getvalueof var="elementval" param="element" />
													<option value="<dsp:valueof param="key"/>"><dsp:valueof param="element"/></option>
												</dsp:oparam>
												<dsp:oparam name="outputEnd">
												</select>
											</div>
										</form>

										</dsp:oparam>
										<dsp:oparam name="empty">
											<option selected="selected" value="">
												<bbbl:label key="lbl_compare_empty_table_attribute" language="${pageContext.request.locale.language}"/><bbbl:label key="lbl_No_Bridal_Shows_Avialable" language="${pageContext.request.locale.language}"/><bbbl:label key="lbl_compare_empty_table_attribute" language="${pageContext.request.locale.language}"/>
											</option>
										</dsp:oparam>
								</dsp:droplet>

							</dsp:oparam>
						</dsp:droplet>
					</div> 
					<div id="bridalDetails" class="grid_9 clearfix bridalDetails noMar">
					<c:if test="${empty stateId}">
						<c:set  var="stateId" value="ALL"/>
					</c:if>
						<dsp:include src="${contextPath}/cms/bridal_template.jsp" flush="false">
							<dsp:param name="stateId" value="${stateId}"/>
						</dsp:include>
					</div>
				</div>
			</div>
		</jsp:body>
 			<jsp:attribute name="footerContent">
				<script type="text/javascript">
				
				   if(typeof s !=='undefined') {
				s.channel='Registry';
				s.pageName='Registry Baby Shows'; // pagename
				<c:if test="${siteId eq 'BedBathUS' || siteId eq 'BedBathCanada'}">
				s.pageName='Registry Bridal Shows'; // pagename
			    </c:if>
				s.prop1='Content Page';
				s.prop2='Content Page'; 
				s.prop3='Content Page';
				s.prop4='Registry';
				s.prop6='${pageContext.request.serverName}';
				s.eVar9='${pageContext.request.serverName}';
				var s_code=s.t();
				if(s_code)document.write(s_code);	
				   }
				</script>
		     </jsp:attribute>
</bbb:pageContainer>


</dsp:page>