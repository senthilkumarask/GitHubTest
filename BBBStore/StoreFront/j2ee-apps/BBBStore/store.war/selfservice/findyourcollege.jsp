<c:set var="section" value="college" scope="request" />
<c:set var="pageWrapper" value="findACollege useStoreLocator" scope="request" />
<c:set var="pageVariation" value="bc" scope="request" />
   
<dsp:page>
    <bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" pageVariation="${pageVariation}">
    <dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
    <dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	 <dsp:importbean bean="/atg/multisite/Site"/>
   <dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:a iclass="makeFavorite" href="${contextPath}/account/favoritestore.jsp?favouriteStoreId=dummyId"  id="storeLocatorHiddenLink" style="display:none;">
		<bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" />
		<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
	</dsp:a>	

    <div id="content" role="main">
        <div  class="container_12 clearfix">
            <div class="grid_7 clearfix">
           	<form name="frmFindCollege" method="post" id="frmFindYourCollege" class="frmFindCollege" action="findyourcollege.jsp">
				<fieldset>
					<h1><bbbl:label key="lbl_findyourcollege_find_college" language="${pageContext.request.locale.language}"/></h1>
	                <p> <bbbl:textArea key="txt_findyourcollege_info" language="${pageContext.request.locale.language}"/> </p>
	                <div class="grid_2 alpha">
	                	<dsp:getvalueof id="selState" param="selState"/>
	                	<dsp:getvalueof id="selCollege" param="selCollege"/>
	                    <select name="selState" id="selState" class="uniform" aria-required="true" aria-labelledby="lblselState errorselState" >
		                    <option value=""><bbbl:label key="lbl_findyourcollege_select_state" language="${pageContext.request.locale.language}"/></option>
							<dsp:droplet name="StateDroplet">
								<dsp:param name="showMilitaryStates" value="false"/>
								<dsp:oparam name="output">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="location" />
										<dsp:param name="sortProperties" value="+stateName"/>
										<dsp:oparam name="output">
											<dsp:getvalueof param="element.stateName" id="stName"/>
											<dsp:getvalueof param="element.stateCode" id="cd"/>
											<c:choose>
												<c:when test="${selState eq cd}">
													<option value="<c:out value='${cd}'/>" selected="selected"><c:out value='${stName}'/></option>
												</c:when>
												<c:otherwise>
													<option value="<c:out value='${cd}'/>"><c:out value='${stName}'/></option>
												</c:otherwise>
											</c:choose>																						
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
	                    </select>
	                    <label id="errorselState" for="selState" generated="true" class="error"></label>
						<h2 id="lblselState" class="offScreen"><bbbl:label key="lbl_findyourcollege_find_college" language="${pageContext.request.locale.language}"/></h2>
	                </div>
	                <div class="grid_3">
		                	<dsp:droplet name="IsEmpty">
		                		<dsp:param name="value" param="selCollege"/>
		                		<dsp:oparam name="false">
									<select id="selCollege" name="selCollege" class="uniform" runat="server" aria-required="true" aria-labelledby="lblselCollege errorselCollege" >
		                			<dsp:getvalueof id="selState" param="selState"/>
		                			<dsp:getvalueof id="selCollege" param="selCollege"/>
									<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
										<dsp:param name="state2" value="${selState}"/>
										<dsp:param name="enable" value="true"/>
										<dsp:param name="queryRQL" value="hidden!=:enable and state=:state2"/>
								  		<dsp:param name="repository" value="/com/bbb/selfservice/repository/SchoolRepository"/>
								  		<dsp:param name="itemDescriptor" value="schools"/>
								  		<dsp:param name="elementName" value="item"/>
								  		<dsp:param name="sortProperties" value="+schoolName"/>
								  		<dsp:oparam name="output">
								  				<dsp:getvalueof id="id" param="item.id"/> 
							  					<dsp:getvalueof id="name" param="item.schoolName"/> 
							  					<c:choose>
							  						<c:when test="${selCollege eq id}">
							  							<option value='<c:out value="${id}"/>' selected="selected"><dsp:valueof value="${name}"/></option>
							  							<dsp:getvalueof id="collName" param="item.schoolName"/>	
							  						</c:when>
							  						<c:otherwise>
							  							<option value='<c:out value="${id}"/>'><dsp:valueof value="${name}"/></option>
							  						</c:otherwise>
							  					</c:choose>
								  		</dsp:oparam>
									</dsp:droplet>
									</select>
		                		</dsp:oparam>
		                		<dsp:oparam name="true">
									<select id="selCollege" name="selCollege" class="uniform" disabled="disabled" aria-required="true" aria-labelledby="lblselCollege errorselCollege">
		                			<option value="" selected="selected"><bbbl:label key="lbl_findyourcollege_college" language="${pageContext.request.locale.language}"/></option>	    
									</select>	
		                		</dsp:oparam>
		                	</dsp:droplet>
		                
	                <label id="errorselCollege" for="selCollege" generated="true" class="error"></label>
					<h2 id="lblselCollege" class="offScreen"><bbbl:label key="lbl_findyourcollege_find_college" language="${pageContext.request.locale.language}"/></h2>
	                </div>
				</fieldset>
	            </form>
	            
   
	<c:if test="${empty selCollege}">
	<dsp:include page="/selfservice/store/find_store_pdp.jsp"></dsp:include>
	</c:if>
            </div>
             <bbbl:textArea key="txt_findyourcollege_image_grid" language="${pageContext.request.locale.language}"/>
              <dsp:droplet name="IsEmpty">
	            	<dsp:param name="value" param="selState"/>
	            	<dsp:oparam name="false">
	            		<dsp:droplet name="IsEmpty">
			            	<dsp:param name="value" param="selCollege"/>
			            	<dsp:oparam name="false">
			            		<dsp:include src="${contextPath}/selfservice/frags/findyourcollegefrag.jsp">
			            			<dsp:param name="state" param="selState"/>
			            			<dsp:param name="college" param="selCollege"/>
			            			<dsp:param name="pageKey" param="pageKey" />
									<dsp:param name="pageNumber" param="pageNumber" />
			            		</dsp:include>
			            	</dsp:oparam>
		            	</dsp:droplet>
	            	</dsp:oparam>
	            </dsp:droplet>
        </div>
    </div>
    		<c:choose>
				<c:when test="${currentSiteId eq BedBathUSSite}">
					<c:set var="radiusRange"><bbbc:config key="radius_college_store_us" configName="MapQuestStoreType" /></c:set> 
				</c:when>
				<c:otherwise>
					<c:set var="radiusRange"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
				</c:otherwise>
			</c:choose>
   <dsp:include src="${contextPath}/selfservice/store/p2p_directions_input.jsp" />
 
        <div class="noCollegeFound hidden" style="margin: 1%  0 0 15%;color: red;">We're sorry ! No stores were found within ${radiusRange} miles of your school.</div>
 
   <c:if test="${!(empty selCollege)}">
   		<script type="text/javascript">
			BBB.College = '${collName}';
		</script>
	</c:if>
<jsp:attribute name="footerContent">
<script type="text/javascript">
	if (typeof s !== 'undefined') {
		s.pageName = 'College>Find My College Locator'; 
		s.channel = 'College';
		s.prop1 = 'College';
		s.prop2 = 'College';
		s.prop3 = 'College';
		s.prop6 = '${pageContext.request.serverName}';
		s.eVar9 = '${pageContext.request.serverName}';
		var s_code = s.t();
		if (s_code)
			document.write(s_code);
	}
	if(window.location.href.indexOf("findyourcollege") > -1){
	setTimeout(function(){ if(!$(".storeLocatorLocationItem")[0]){
   	$(".noCollegeFound").removeClass("hidden");
   }}, 5000);
}
</script>
  </jsp:attribute>	
</bbb:pageContainer>
</dsp:page>
