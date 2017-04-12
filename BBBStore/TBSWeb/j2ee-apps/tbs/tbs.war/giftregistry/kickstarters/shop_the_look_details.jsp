<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />	
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/kickstarters/droplet/TopSkusCombinedDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/com/bbb/kickstarters/droplet/KickStarterPagination" />
	<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />	
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
    <dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
    <dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>
	<dsp:getvalueof var="registryId" param="registryId"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	
	<dsp:droplet name="AddItemToGiftRegistryDroplet">
       	<dsp:param name="siteId" value="${appid}"/>	
        <dsp:oparam name="output">
	       	<c:set var="sizeValue"><dsp:valueof param="size" /></c:set>
        </dsp:oparam>
	</dsp:droplet>
	
    <c:choose>
        <c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:when test="${not empty eventType && eventType eq 'Baby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="pageVariation" value="br" scope="request" />
        </c:otherwise>
    </c:choose>
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
	<dsp:getvalueof var="profileId" bean="Profile.id"/>
	<bbb:pageContainer>
		<jsp:attribute name="section">browse</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">useFB useCertonaJs productDetails useBazaarVoice  </jsp:attribute>
	<jsp:body>

		<%-- TODO - load this in pagestart or section start 
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/product_details.css?v=${buildRevisionNumber}" />
		<script type="text/javascript" src="/_assets/global/js/browse.js?v=2"></script>
		--%>
		
		
		<%-- Droplet for showing error messages --%>
		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
				<dsp:param param="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
			<dsp:oparam name="output">	
				<div class="error"><dsp:valueof param="message"/></div>
			</dsp:oparam>				
		</dsp:droplet>	

		<dsp:getvalueof var="sortSeq" param="sorting"/>
		<dsp:getvalueof var="view" param="view"/>
		
		<div id="content" class="container_12 clearfix topConsultantsPicks" role="main">		
			<dsp:form method="post" id="frmRowAddToRegistry" action="${contextPath}/kickstarters/top_consultants_picks.jsp">
				<div class="registryDataItemsWrap listDataItemsWrap" style="display:none;">
					<input type="hidden" name="prodId" class="frmAjaxSubmitData _prodId addItemToRegis productId addItemToList" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internaldata="true" /> 
				    <input id="quantity" title="Enter Quantity" class="addItemToRegis _qty itemQuantity addItemToList" type="hidden" name="qty" value="1" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity"/> 
		            <input type="hidden" name="skuId"  class="frmAjaxSubmitData addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internaldata="true" /> 
		            <input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" /> 
		            <input type="hidden" name="parentProdId" class="frmAjaxSubmitData _prodId addItemToRegis addItemToList" />
		            <input type="hidden" name="heading1" class="frmAjaxSubmitData _heading1 addItemToRegis heading1 addItemToList"/> 
		            <input type="hidden" name="bts" class="addToCartSubmitData" value="false" />		         
		            <dsp:getvalueof var="regIdLst" value="${param.registryId}" />
		            <dsp:getvalueof var="registryNameLst" value="${param.eventType}" />
		            <c:if test="${not empty regIdLst}">
		            	<input type="hidden" class="frmAjaxSubmitData addItemToRegis addItemToList" name="registryId" value="${regIdLst}" data-change-store-submit="registryId"/>
		            </c:if>
		            <c:if test="${not empty registryNameLst}">
		            	<input type="hidden" name="registryName" class="frmAjaxSubmitData addItemToRegis" value="${registryNameLst}" />
		            </c:if>
		            <input type="hidden" name="price"  class="frmAjaxSubmitData addItemToList addItemToRegis" />
		            <dsp:include page="/addgiftregistry/add_item_gift_registry.jsp" >
		             	<dsp:param name="kickStarterPage" value="yes"/>
		            </dsp:include>
		     	</div>
			</dsp:form>	
        	<div class="grid_12 topPicksHeader">
				
                <div class="grid_9 alpha">
                	<h2 class="mainHeader"><bbbl:label key="lbl_shop_look_main_header" language="${pageContext.request.locale.language}" /></h2>
                </div>	        	
	        	 <%--Code to set previous and next consultant urls --%> 
	            	 <dsp:importbean bean="/com/bbb/kickstarters/droplet/KickStarterPagination" />
	            	<dsp:droplet name="KickStarterPagination">
					<dsp:param name="kickStarterId" value="${param.id}" />
					<dsp:param name="kickStarterType" value="Shop This Look" />
					<dsp:param name="siteId" value="${appid}" />
					<dsp:param name="eventType" value="${param.eventType}" />
					<dsp:param name="isTransient" value="${isTransient}" />
					<dsp:oparam name="output">
					<dsp:getvalueof var="nextId" param="nextId" />
					<dsp:getvalueof var="previousId" param="previousId" />
					</dsp:oparam>
					</dsp:droplet>
              <%-- End code to set previous and next consultant urls--%>
				<div class="grid_3 omega topConsultantsPicksNav marTop_20" >
               		
               		<c:if test="${not empty fn:trim(previousId)}">
               			<c:choose>
							<c:when test="${not empty param.eventType && not empty param.registryId }">
								<a href="${contextPath}/shopthislook/${previousId}/${param.registryId}/${param.eventType}"><bbbl:label key="lbl_top_consultants_picks_prev_list" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:when test="${not empty param.eventType && empty param.registryId }">
								<a href="${contextPath}/shopthislook/${previousId}/${param.eventType}"><bbbl:label key="lbl_top_consultants_picks_prev_list" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:when test="${empty param.eventType && not empty param.registryId }">
								<a href="${contextPath}/shopthislook/${previousId}/${param.registryId}"><bbbl:label key="lbl_top_consultants_picks_prev_list" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:otherwise>
								<a href="${contextPath}/shopthislook/${previousId}"><bbbl:label key="lbl_top_consultants_picks_prev_list" language="${pageContext.request.locale.language}" /></a>
							</c:otherwise>
						</c:choose>
					</c:if>     
					<c:if test="${not empty fn:trim(previousId) && not empty fn:trim(nextId)}">|</c:if>					           
               		<c:if test="${not empty fn:trim(nextId)}">
               			<c:choose>
							<c:when test="${not empty param.eventType && not empty param.registryId }">
								<a href="${contextPath}/shopthislook/${nextId}/${param.registryId}/${param.eventType}"><bbbl:label key="lbl_top_consultants_picks_next_list" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:when test="${not empty param.eventType && empty param.registryId }">
								<a href="${contextPath}/shopthislook/${nextId}/${param.eventType}"><bbbl:label key="lbl_top_consultants_picks_next_list" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:when test="${empty param.eventType && not empty param.registryId }">
								<a href="${contextPath}/shopthislook/${nextId}/${param.registryId}"><bbbl:label key="lbl_top_consultants_picks_next_list" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:otherwise>
								<a href="${contextPath}/shopthislook/${nextId}"><bbbl:label key="lbl_top_consultants_picks_next_list" language="${pageContext.request.locale.language}" /></a>
							</c:otherwise>
						</c:choose>
					</c:if>
                </div>
           	</div>           	
           	<%--Top consultant details imports --%>           	
           	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
			<dsp:importbean bean="/com/bbb/kickstarters/droplet/KickStarterDetailsDroplet" />
			<dsp:importbean bean="/com/bbb/kickstarters/droplet/TopSkusDroplet" />
			<dsp:importbean bean="/atg/multisite/Site"/>
			<dsp:getvalueof id="currentSiteId" bean="Site.id" />
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			<dsp:getvalueof var="id" param="id"/>
           	<%--End Top Consultant details imports --%>
           	<c:set var="scene7Path">
			    <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
			</c:set>
<c:set var="kickStartersCacheTimeout"><bbbc:config key="kickStartersCacheTimeout" configName="HTMLCacheKeys" /></c:set>
<dsp:droplet name="/atg/dynamo/droplet/Cache">
	<dsp:param name="key" value="${id}_${currentSiteId}_${param.eventType}_${isTransient}" />
	<dsp:param name="cacheCheckSeconds" value="${kickStartersCacheTimeout}"/>
	<dsp:oparam name="output">
			<dsp:droplet name="KickStarterDetailsDroplet">
				<dsp:param name="consultantId" value="${id}" />
				<dsp:param name="siteId" value="${currentSiteId}" />
				<dsp:param name="eventType" value="${param.eventType}" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="kickStarterVO" param="kickStarterVO" />
				<dsp:getvalueof var="imageUrl" value="${kickStarterVO.imageUrl }"/>
				<dsp:getvalueof var="heroImageUrl" value="${kickStarterVO.heroImageUrl}"/>
				 <dsp:setvalue  bean="SessionBean.kickStarterEventType" value="${kickStarterVO.kickstarterType}"/>
				<c:choose>
				    <c:when test="${not empty heroImageUrl}">
				       <c:set var="imagePath">${heroImageUrl}</c:set>
				    </c:when>
				    <c:otherwise>
				        <c:set var="imagePath">${imageUrl}</c:set>
				    </c:otherwise>
				</c:choose>
				
		        <dsp:getvalueof var="id" value="${kickStarterVO.id }"/>
		        <dsp:getvalueof var="heading1" value="${kickStarterVO.heading1 }"/>
		        <dsp:getvalueof var="heading2" value="${kickStarterVO.heading2 }"/>
		        <dsp:getvalueof var="description" value="${kickStarterVO.description }"/>
		        <dsp:getvalueof var="pickLists" value="${kickStarterVO.kickStarterPicklists }"/>	
		        <dsp:getvalueof var="targeEventType" value="${param.eventType}" />	      
		        <c:if test="${empty param.eventType}"> 			
		             <dsp:getvalueof var="targeEventType" value="${kickStarterVO.kickStarterPicklists[0].registryTypes[0].registeryType}"/>
		        </c:if>
		        
		        <%-- need picklist description before entering sku loop --%>
		        <dsp:droplet name="IsNull">
				  <dsp:param value="kickStarterVO.kickStarterPicklists[0]" name="value"/>
				  <dsp:oparam name="true">
				  	<c:set var="pickListDescription" value="" scope="request" />
				  </dsp:oparam>
				  <dsp:oparam name="false">				  	
				  	<dsp:getvalueof var="pickListDescription" param="kickStarterVO.kickStarterPicklists[0].pickListDescription"/>
				  </dsp:oparam>
				</dsp:droplet>
		        
           	
           	<div class="grid_12 shopLookMainImageWrap">           		
           		<%-- <img class="topConsultantsPicksImage" src="${imagePath}" /> --%>
           		<%-- looking for a scene7 url, no params or protocol, ie: s7d9.scene7.com/is/image/BedBathandBeyond/flatware --%>
                <img class="shopLooksMainImage" src="${imagePath}?wid=953&hei=493" width="953" height="493" />
           	</div>
           	
           	<div class="grid_12 shopLooksTitleWrap">
           		<div class="grid_7 alpha">
                	<h3 class="shopLookTitle">${heading1}</h3>
                </div>
                <div class="grid_5 shopLookSocialLinks omega">
                	
                	<%-- make this URL rewrite value --%> 
                	<c:set var="shareURL" >${pageContext.request.requestURL}?${pageContext.request.queryString}</c:set>                	
                	<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
                        <dsp:param name="URL" value="${shareURL}"/>
                        <dsp:oparam name="output">
                            <dsp:getvalueof var="shareURL" param="encodedURL"/>
                        </dsp:oparam>
                    </dsp:droplet>              	
                    <dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
                        <dsp:param name="URL" value="${heading1}"/>
                        <dsp:oparam name="output">
                            <dsp:getvalueof var="encodedHeading" param="encodedURL"/>
                        </dsp:oparam>
                    </dsp:droplet>               	
                	
                	<%-- TODO - where does FBOn value come from? --%>
                	<c:set var="FBOn" value="true" />                	
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
                             <div class="fb-like">
                                 <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=button_count&amp;width=90&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=21&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:90px; height:21px;" allowTransparency="true"></iframe>
                             </div>
                         <![endif]-->
                         <!--[if !IE 7]><!-->
                             <div class="fb-like" style="vertical-align:middle;" data-layout="button_count" data-send="false" data-width="90" data-show-faces="false"></div>
                         <!--<![endif]-->
	                </c:if>
                	
                	<%--
                	Facebook: http://www.facebook.com/sharer.php?s=100&p[url]={url}&p[images][0]={img}&p[title]={title}&p[summary]={desc}
                	Twitter: https://twitter.com/share?url={url}&text={title}&via={via}&hashtags={hashtags}
                	Google + https://plus.google.com/share?url={url}
                	Pinterest: https://pinterest.com/pin/create/bookmarklet/?media={img}&url={url}&is_video={is_video}&description={title}
                	--%>                	
                	
                	<%-- <a href="#" class="facebook_like"><img src="/_assets/global/images/kickstarters/button_facebook_like.png" ></a>--%>
                	<a href="http://www.facebook.com/sharer.php?s=100&p[url]=${shareURL}&p[images][0]=${imagePath}&p[title]=${encodedHeading}" target="_blank" class="social_button"><img src="/_assets/global/images/kickstarters/button_facebook.png" ></a>
                	<a href="https://twitter.com/share?url=${shareURL}&text=${encodedHeading}" target="_blank" class="social_button"><img src="/_assets/global/images/kickstarters/button_twitter.png" ></a>
                	<a href="https://pinterest.com/pin/create/bookmarklet/?url=${shareURL}&media=${imagePath}&description=${encodedHeading}" target="_blank" class="social_button"><img src="/_assets/global/images/kickstarters/button_pinterest.png" ></a>
                	<a href="https://plus.google.com/share?url=${shareURL}" target="_blank" class="social_button"><img src="/_assets/global/images/kickstarters/button_google_plus.png" ></a>
                </div>
           	</div>
           	
           	
           	<div class="grid_12 shopLooksDetails">
                <div class="grid_7 alpha">
                	<p class="shopLookDescription">${description}</p>
					<div class="grid_3 alpha omega createRegistryorAddAll">
	                	<c:choose>
				        	<c:when test="${isTransient == 'false' && sizeValue>=1}">
								<dsp:include page="/addgiftregistry/add_kickstarter_items_gift_registry.jsp">
								<dsp:param name="heading1" value="${heading1}" />
								</dsp:include>
	                	    </c:when>
	                		<c:otherwise>
	                	    	<dsp:include page="create_registry_select.jsp">
	                				<dsp:param name="isTransient" value="${isTransient}" />
	                				<dsp:param name="showHeader" value="true" />
	                				<dsp:param name="showLoginLink" value="true" />                					
	                			</dsp:include>   
	                		</c:otherwise>
						</c:choose>
					</div>
                </div>	        	
	        	
	        	<%-- 
	        	<div class="grid_5 shopLooksCollectionSummary omega">	        		
	        		<h5><bbbl:label key="lbl_shop_look_collection_summary_header" language="${pageContext.request.locale.language}" /></h5>
	        		<p>${pickListDescription}</p>	        		 
	        	</div>
	        	--%>
           	</div>
           	
           	<div id="shopCollection" class="collectionItemsBox grid_12 clearfix   collectionGridView">		      

		            <h2 class="collectionHeader"><bbbl:label key="lbl_top_consultants_in_this_collection" language="${pageContext.request.locale.language}" /></h2>
		      
		               
							<%-- Start displaying collection Items dynamically --%>
							
					<dsp:droplet name="TopSkusCombinedDroplet">
						<dsp:param name="pickLists" value="${kickStarterVO.kickStarterPicklists}" />
						<dsp:oparam name="output">
						<dsp:getvalueof var="topSkus" param="topSkus"/>
						</dsp:oparam>
					</dsp:droplet>
	                <dsp:include page="product_details_grid.jsp">
              					<dsp:param name="topSkus" value="${topSkus}" />
              					<dsp:param name="sizeValue" value="${sizeValue}" />
              					<dsp:param name="heading1" value="${heading1}" />
              					<dsp:param name="registryType" value="${targeEventType}" />
              					<dsp:param name="kickStarterType" value="Shop This Look" />            						                					
              		</dsp:include>	<%-- ForEach kickStarterVO.kickStarterPicklists --%>
	              
		                
		            
		    </div>
		    </dsp:oparam>
           	</dsp:droplet>
	</dsp:oparam>
</dsp:droplet>		    
           	<%-- end pasted collection --%>
           	
           	<%-- 
           	<div id="pickListDescription" class="grid_12  clearfix">
           		<div class="grid_9 alpha">
           			<h2 class="pickListDescriptionHeader"><bbbl:label key="lbl_shop_look_collection_summary_header" language="${pageContext.request.locale.language}" /></h2>
           	   		<p class="pickListDescriptionText" >
           	   			${pickListDescription}
           	   		</p>
           	   	</div>
           	   	<div class="grid_3 omega createRegistryorAddAll">
                	<c:choose>
			        	<c:when test="${isTransient == 'false' && sizeValue>=1}">
							<dsp:include page="/addgiftregistry/add_kickstarter_items_gift_registry.jsp">
								<dsp:param name="heading1" value="${heading1}" />
								</dsp:include>
                	    </c:when>
                		<c:otherwise>
                	    	<dsp:include page="create_registry_select.jsp">
                				<dsp:param name="isTransient" value="${isTransient}" />
                				<dsp:param name="showHeader" value="true" />
                				<dsp:param name="showLoginLink" value="true" />                					
                			</dsp:include>   
                		</c:otherwise>
					</c:choose>
           	   	</div>
           	</div>
           	--%>
           	
           	<div class="grid_12 clearfix spacing spacingBottom" style="margin-top: 40px;"> 
				<dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">	
					<dsp:param name="pageName" value="shopThatLookDetails" />
					<dsp:param name="promoSpot" value="Bottom" />
					<dsp:param name="registryType" value="${targeEventType}" />
					<dsp:param name="siteId" value="${currentSiteId}" />
					<dsp:param name="channel" value="Desktop Web" />
				</dsp:include>
           	</div>	           		
           		<c:set var="omniDesc" value="Popular items (shop this look)-${heading1}"/> 		
           		<dsp:include page="popular_items_grid.jsp?eventType=${targeEventType}" flush="true" >
           		<dsp:param name="omniDesc" value="${omniDesc}" />
           		</dsp:include>		
           		
        </div><%-- close : <div id="content"> --%>
 <%-- Set Session attributes--%>
 
 <c:if test="${not empty regIdLst}">
           <dsp:setvalue  bean="SessionBean.registryId" value="${regIdLst}"/>
</c:if>
<c:if test="${not empty registryNameLst}">
           <dsp:setvalue  bean="SessionBean.eventType" value="${registryNameLst}"/>
</c:if>
 <c:if test="${not empty regId}">
           <dsp:setvalue  bean="SessionBean.registryId" value="${regId}"/>
</c:if>
<c:if test="${not empty registryName}">
           <dsp:setvalue  bean="SessionBean.eventType" value="${registryName}"/>
</c:if>
<dsp:setvalue  bean="SessionBean.kickStarterId" value="${param.id}"/>
<dsp:getvalueof var="addToList" param="addToList"/>
<dsp:getvalueof var="prodList" param="prodList"/>
<dsp:getvalueof var="showpopup" param="showpopup"/>
<dsp:getvalueof var="registryId" param="registryId"/>
<dsp:getvalueof var="registryName" param="registryName"/>
<dsp:getvalueof var="totQuantity" param="totQuantity"/>
<dsp:getvalueof id="omniList" value="${sessionScope.added}"/>
<script type="text/javascript">
$(function () {
	var registryId = '${registryId}';
	var addtoList='${addToList}';
	if(registryId.length>0){
	rkg_micropixel("${appid}","addtoregistry");
	}
	if(addtoList.length>0){
	rkg_micropixel("${appid}","wish");
	}
});

function addToWishList(){
	   var qty = '${param.qty}';
	   var price = '${certonaPrice}';
	   totalPrice=qty*price;
	   var finalOut= ';'+'${param.productId}'+';;;event38='+'${param.qty}'+'|event39='+totalPrice+';eVar30='+'${param.skuId}';
	   additemtolist(finalOut);
}
</script>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
	<dsp:oparam name="false">

			<c:set var="check" value="true" scope="session"/>

	</dsp:oparam>
</dsp:droplet>
<c:choose>
<c:when test="${not empty collectionId_Omniture}">
<c:set var="omni_prod" >${fn:substring(collectionId_Omniture,0,fn:length(collectionId_Omniture)-1)}</c:set>
</c:when>
<c:otherwise>
<c:set var="omni_prod" >;${parentProductId}</c:set>
</c:otherwise>
</c:choose>
	<c:choose>
		<c:when test="${null ne addToList}">
		   <script type="text/javascript">
			   var resx = new Object();
			   resx.event = "wishlist";
			   resx.pageid = "";
			   var prodList = "<c:out value="${prodList}"/>";
			   resx.itemid = prodList.replace(/[\[\s]/g,'').replace(/[,\]]/g,';');
		   </script>
		</c:when>
		<c:otherwise>
		  <script type="text/javascript">
		           var resx = new Object();
		           resx.appid = "${appIdCertona}";
		           resx.event = "product";
		           resx.itemid = '${linkStringNonRecproduct}';
		           resx.links = '${linksCertona}' + '${linkStringNonRecproduct}' + '${productList}';
		       	   resx.pageid = "${pageIdCertona}";
		           resx.customerid = "${userId}";

		//In case of add to cart event, call from browse.js
		           function callCertonaResxRun(certonaString) {
		    			  resx.event = "shopping+cart";
		                  resx.itemid = certonaString;
		                  resx.pageid = "";
		                  if (typeof certonaResx === 'object') { certonaResx.run();  }
		           }

				   function certonaFeed(json) {

						var cfProdId = '',
							cfPrice = '',
							cfQty = '',
							cfRegistryId = '',
							cfRegistryName = '';

						for (var i in json.addItemResults) {
							var getPrice = json.addItemResults[i].price,
                                intPrice = getPrice.replace('$',''),
								intQty = parseInt(json.addItemResults[i].qty, 10);

							cfProdId += json.addItemResults[i].prodId + ';';
							cfQty +=  intQty + ';';
							cfPrice +=  (intPrice*intQty) + ';';
							cfRegistryId += json.addItemResults[i].registryId + ';';
						}

						cfRegistryName += json.registryName + ';';
						resx.appid = "${appIdCertona}";
					    resx.links = '${linksCertona}' + '${linkStringNonRecproduct}' + '${productList}';
					    resx.pageid = "${pageIdCertona}";
					    resx.customerid = "${userId}";

						resx.event = "op_Registry+" + cfRegistryName;
						resx.itemid = cfProdId;
						resx.qty = cfQty;
						resx.price = cfPrice;
						resx.transactionid = cfRegistryId;
						if (typeof certonaResx === 'object') { certonaResx.run();  }
					}
				   
				   function certonaFeedAddItem() {

					   var productId= '${param.productId}';
					   var skuId = '${param.skuId}';
					   var registryName= '${param.registryName}';
					   var registryId= '${param.registryIdR}';
					   var qty = '${param.totQuantity}';
					   var price = '${certonaPrice}';
						resx.appid = "${appIdCertona}";
					    resx.links = '${linksCertona}' + '${linkStringNonRecproduct}' + '${productList}';
					    resx.pageid = "${pageIdCertona}";
					    resx.customerid = "${userId}";

						resx.event = "op_Registry+" + registryName;
						resx.itemid = productId;
						resx.qty = qty;
						resx.price = price;
						resx.transactionid = registryId;
						if (typeof certonaResx === 'object') { certonaResx.run();  }
					}
				  
		    </script>
		</c:otherwise>
		</c:choose>
		
		<script type="text/javascript">
		 var BBB = BBB || {};
          var isTransient ='${isTransient}';
     		var profileStatus="non registered user";
     		if(isTransient=='false'){
     			profileStatus = "registered user"
     		}
     		var heading1 = "${heading1}";
     		heading1 = heading1.replace(/[^a-zA-Z0-9]/g,' ');
     		BBB.topConsultant_shopLook = {
					pageNameIdentifier: 'topConsultant_shopLook',
					pageName:'Registry Consultant>'+heading1,
					channel: 'Registry',
					prop1: 'Registry',
					prop2: 'Registry',
					prop3: 'Registry',
					var1 :'Registry Consultant>'+heading1,
					var16: profileStatus,
					var38:'${profileId}'
				};
        </script>
		<dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
		    <dsp:param name="configKey" value="DimNonDisplayConfig"/>
		    <dsp:oparam name="output">
		        <dsp:getvalueof var="configMap" param="configMap"/>
		    </dsp:oparam>
		</dsp:droplet>
		<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
			<dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="queryString" param="queryString"/>
            </dsp:oparam>
         </dsp:droplet>    
	</jsp:body>
	<jsp:attribute name="footerContent">   
    </jsp:attribute>

    </bbb:pageContainer>
</dsp:page>