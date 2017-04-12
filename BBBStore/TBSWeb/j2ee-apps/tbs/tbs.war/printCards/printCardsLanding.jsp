<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean var="storeConfig" bean="/atg/store/StoreConfiguration"/>  	
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />		
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/PrintAtHomeDroplet" />
	<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="registryId" param="registryId"/>
	<dsp:getvalueof var="appid" bean="Site.id" />	
	<c:choose>
        <c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:when test="${not empty eventType && eventType eq 'Baby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="pageVariation" value="br" scope="request" />
        </c:otherwise>
    </c:choose>
	<bbb:pageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">printCardsAtHome</jsp:attribute>
	<jsp:body>
	<div class="row">
		<dsp:droplet name="PrintAtHomeDroplet">
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:oparam name="output">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="repoItems" />
					<dsp:oparam name="output">						 
						<div id="printAtHomeLandingHero" class="small-12 columns" style="background: url('<dsp:valueof param="element.heroImageURL" valueishtml="true" />')">		
			            	<bbbl:label key="lbl_card_design_editor_landing_printyourown_announcement_cards" language="${pageContext.request.locale.language}" />
			                <p id="printHomeHeroDescription" class="small-12 large-6 columns "><dsp:valueof param="element.introductoryMessage" valueishtml="true" /></p>			
			                <p id="printHomeAvery" class="small-12 columns">
				              <bbbl:label key="lbl_card_design_editor_landing_gettheaverycardprintersheets" language="${pageContext.request.locale.language}" />
			               </p>
		                </div>
             
             			<dsp:droplet name="ForEach">
							<dsp:param name="array" param="element.announcementCards" />	
			 				<dsp:oparam name="outputStart">		
                        		<dsp:getvalueof var="cardsCount" param="count"/>								 
		     					<div id="printAtHomeLandingThumbs">
			   						<bbbl:label key="lbl_card_design_editor_landing_selectatemplate" language="${pageContext.request.locale.language}" />		
			   						<ul id="thumbList" class="small-block-grid-1 medium-block-grid-2 large-block-grid-4">		
             				</dsp:oparam>
			 				<dsp:oparam name="output">	
			 					<dsp:getvalueof var="cardIndex" param="index"/>
			 					<dsp:getvalueof param="element.Id" var="cardId" />	
                					<li>
			 						<a class="thumbLink" href="<dsp:valueof param="element.thumbnailImage" valueishtml="true" />" >
										<img src="<dsp:valueof param="element.thumbnailImage" valueishtml="true" />" alt="Select This Design"/>
										<!-- <p class="zoom">											
											<span class="icon-fallback-text"> 
												<span class="icon-zoomin" aria-hidden="true"></span> 
												<span class="icon-text">zoom in</span>
											</span>
										</p> -->
									</a>
									<a href="printCards.jsp?cardId=${cardId}" class="selectCardButton tiny button" title="Select This Design" alt="Select This Design"><bbbl:label key="lbl_card_design_editor_landing_selectthisdesign" language="${pageContext.request.locale.language}" /></a>						
								</li>
							</dsp:oparam>
							<dsp:oparam name="outputEnd">
                  				</ul>
		          				</div>
               				</dsp:oparam>
						</dsp:droplet>
               		</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
	     </dsp:droplet>
	</div>
	<div id="cardPreviewDialog" style="display:none;">
		<a href="#" class="prev move" alt="Previous"><span class="icon-fallback-text"> 
				<span class="icon-chevron-left" aria-hidden="true"></span> 
				<span class="icon-text">previous</span>
			</span>	
		</a>
		<img id="cardPreviewImg" src="" alt="Card Preview Image" />
		<a href="#" class="next move" alt="Next "><span class="icon-fallback-text"> 
				<span class="icon-chevron-right" aria-hidden="true"></span> 
				<span class="icon-text">next</span>
			</span>
		</a>
	</div>
</jsp:body>
<jsp:attribute name="footerContent">
    <script type="text/javascript">
		if (typeof s !== 'undefined') {
			s.pageName = 'Registry Cards Landing page';
			s.channel = 'Registry';
			s.prop1 = 'Registry';
			s.prop2 = 'Registry';
			s.prop3 = 'Registry';
			s.prop4 = '';
			s.prop5 = '';			
			var s_code = s.t();
			if (s_code)
				document.write(s_code);

		}
	</script>
    </jsp:attribute>
</bbb:pageContainer>	
</dsp:page>