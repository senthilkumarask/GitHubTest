<dsp:page>
<div title="Investor Relations">
		<div class="conferenceCall">
			<bbbt:textArea key="txtarea_investor_conferencecall" language ="${pageContext.request.locale.language}"/>
			<div class="webcastLive">
				<div class="grid_1 fl alpha">
					<img alt="Investor Relations" title="Investor Relations" src="${imagePath}/_assets/global/images/icons/mic.png"/>
				</div>			
				<div class="grid_5 fl">
					<bbbt:textArea key="txtarea_investor_webcastlive" language ="${pageContext.request.locale.language}"/>
					<p class="marTop_10">
					<c:set var="TBS_BuyBuyBabySite">
	<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	
					 <c:choose>
                		<c:when test="${currentSiteId ne TBS_BuyBuyBabySite}">
							<a href="<bbbc:config key="Investor_relations_URL" configName="ThirdPartyURLs" />" onclick="javascript:omnitureExternalLinks('Investor Relations: Investor Relations Link ')" target="_blank"><bbbl:label key="lbl_investor_clickhere" language ="${pageContext.request.locale.language}"/></a>
						</c:when>
                	</c:choose>
					
					<bbbl:label key="lbl_investor_forinverstorrelations" language ="${pageContext.request.locale.language}"/></p>
					
				</div>
			</div>	
			
			<div class="formRow fl clearfix prefix">				
				<div class="button fl">
				    <c:set var="closeKey">
							<bbbl:label key="lbl_investor_close" language ="${pageContext.request.locale.language}"/>
					</c:set>
					<input type="button" value="${closeKey}" class="close-any-dialog" role="button" aria-pressed="false" />
				</div>
			</div>
		</div>
</div>
<script type="text/javascript">
function omnitureExternalLinks(data){
  	if (typeof s !== 'undefined') {
  	externalLinks(data);
  	}
  }
</script>
</dsp:page>