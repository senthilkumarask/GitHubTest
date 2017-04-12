<dsp:page>
	<dsp:importbean bean="/com/bbb/cms/droplet/BridalShowStateDroplet" />
	<bbb:pageContainer>

	<jsp:attribute name="SEOTagRenderer">
    <dsp:include page="/global/gadgets/metaDetails.jsp" flush="true" />
    </jsp:attribute>
	<jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>

	<jsp:body>
	<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
		<dsp:oparam name="output">
			<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM" />
		</dsp:oparam>
	</dsp:droplet>
		<link href="${cssPath}/_assets/bbregistry/css/bridalshows.css?v=${buildRevisionNumber}" rel="stylesheet" type="text/css" />
		<link href="${cssPath}/_assets/bbregistry/css/theme.css?v=${buildRevisionNumber}" rel="stylesheet"	type="text/css" />
	
	 <script type="text/javascript">
			function callBirdalShowTemplate() {
				var state = document.getElementById("state").value;

				$.post("bridal_template.jsp", {
					stateId : state
				}, function(data) {

					$("#BridalDetail").empty().append($(data));
				});
			}
		</script>

<body>
	<div id="themeWrapper" class="br">
		<div id="content" class="container_12 clearfix" role="main">
		
		
				<div class="grid_10 bridalHeader clearfix">
					<h1><bbbl:label key="lbl_bridalshow_title" language="<c:out param='${pageContext.request.locale.language}'/>"/></h1>
					<ul>
						<li><a href="#"><img src="${imagePath}/_assets/global/images/icons/print.png" alt="Print" /></a></li>
						<li><a href="#"><img src="${imagePath}/_assets/global/images/icons/email.png" alt="Email" /></a></li>
						<li><a href="#"><img src="${imagePath}/_assets/global/images/icons/facebook.png" alt="Like us on Facebook" /></a></li>
						<li><a href="#"><img src="${imagePath}/_assets/global/images/icons/follow_us_facebook.png" alt="Follow us on Facebook" /></a></li>
						<li>Monica and 820 others like it</li>
					</ul>
				</div>
				<p class="grid_10"><bbbt:textArea key="txt_bridalshow_shortdesc" language="<c:out param='${pageContext.request.locale.language}'/>"/></p>

					<dsp:droplet name="BridalShowStateDroplet">
						<dsp:oparam name="output">
							<form id="bridalShow" action="bridal_show.jsp" method="post" class="grid_10 clearfix place">
								<label id="lblstate" for="state"><bbbl:label key="lbl_bridalshow_showin" language="<c:out param='${pageContext.request.locale.language}'/>"/></label>

								<select name="BridalShowDetailDropletState" id="state"	onChange="callBirdalShowTemplate()" aria-required="false" aria-labelledby="lblstate errorstate" >
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" param="stateMap"/>
											<dsp:getvalueof var="count" vartype="java.lang.String"	param="count" />
												<c:choose>
											      <c:when test="${count =='1'}">
											      	<c:set var="select" value="selected"/>
											      </c:when>
											      <c:otherwise>
											      	<c:set var="select" value=""/>
											      </c:otherwise>
											    </c:choose>
												<dsp:oparam name="output">
													<option value='<dsp:valueof param="key"/>'  selected="${select}">
														<dsp:valueof param="element"></dsp:valueof>
													</option>
												</dsp:oparam> 
												<dsp:oparam name="empty">
													<option selected="selected" value="">
														<bbbl:label key="lbl_compare_empty_table_attribute" language="${pageContext.request.locale.language}"/><bbbl:label key="lbl_No_Bridal_Shows_Avialable" language="${pageContext.request.locale.language}"/><bbbl:label key="lbl_compare_empty_table_attribute" language="${pageContext.request.locale.language}"/>
													</option>
												</dsp:oparam>
										</dsp:droplet>
								</select>
							</form>
						</dsp:oparam>
						<dsp:oparam name="empty">
						<div id="BridalDetail" class="grid_10 clearfix bridalDetails">
						<div id="BridalShowTemplateView" class="grid_10 clearfix bridalDetails alpha tableContents row_odd">
							<b><bbbe:error key="err_bridalshow_noshow" language="<c:out param='${pageContext.request.locale.language}'/>"/>
						</div></div>
						</dsp:oparam>
					</dsp:droplet>
					<div class="grid_10 clearfix bridalDetails" id="BridalDetail">
				</div>
			</div>
			</div>
			</body>
		</jsp:body>
	</bbb:pageContainer>
</dsp:page>