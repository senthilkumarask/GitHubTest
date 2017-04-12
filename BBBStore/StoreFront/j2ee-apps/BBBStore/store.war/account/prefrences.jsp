<dsp:page>
 <c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BuyBuyBabySite">
			<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BedBathCanadaSite">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
	<bbb:pageContainer pageWrapper="wishlist myAccount hhPreferences" section="accounts" index="false" follow="false">
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

			<div class="grid_10 info">
           
            <c:if test="${HarteHanksOn}">
																			
            	<dsp:droplet name="/com/bbb/account/BBBStoreSessionDroplet">
            		<dsp:param name="currSite" value="${currentSiteId}"/>
            		<dsp:oparam name="success">
            			<dsp:getvalueof  id="URL" param="URL"/>
            			<div class="width_10">
				            	<iframe type="some_value_to_prevent_js_error_on_ie7" id="hhIframe" title='<bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/>' src='${URL}' width="810" height="1000" scrolling="auto" frameBorder="0" class=""/>
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
		
	</bbb:pageContainer>
</dsp:page>	
				
		