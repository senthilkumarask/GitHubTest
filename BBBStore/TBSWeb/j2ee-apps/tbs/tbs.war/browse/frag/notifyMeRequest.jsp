
<dsp:page> 
	<dsp:importbean bean="/com/bbb/browse/BBBBackInStockFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site" />

	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof id="skuId" idtype="java.lang.String" param="skuId" />
	<dsp:getvalueof id="productId" idtype="java.lang.String" param="productId" />
	<dsp:getvalueof var="var_productId" param="productId"/>
	
	<c:set var="imagePath"><bbbc:config key="image_host" configName="ThirdPartyURLs" /></c:set>
	<c:set var="scene7Path"><bbbc:config key="scene7_url" configName="ThirdPartyURLs" /></c:set>
	<c:set var="BedBathCanadaSite" scope="request"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>

	<div title="<bbbl:label key='lbl_notifyme_email_me_when_instock' language="${pageContext.request.locale.language}" />">
		<p><bbbl:label key='lbl_notifyme_placeholder' language="${pageContext.request.locale.language}" /></p>
		
		<dsp:droplet name="ProductDetailDroplet">
		<dsp:param name="id" param="productId" />
		<dsp:param name="siteId" param="siteId"/>
		<dsp:param name="skuId" param="skuId"/>
		<dsp:param name="isDefaultSku" value="true"/>
		<dsp:oparam name="output">
			<div class="product small-4 columns">
				<dsp:getvalueof var="productMediumImageUrl" param="productVO.productImages.mediumImage" />
				<dsp:getvalueof var="imageAltText" param="productVO.name"/>
				<dsp:getvalueof var="productName" param="productVO.name"/>
				<div class="prodImg fl marRight_10">
					<c:choose>
						<c:when test="${not empty productMediumImageUrl}">
							<%-- Thumbnail image exists --%>
							<img class="productImage noImageFound" width="146" height="146" src='${scene7Path}/${productMediumImageUrl }' title="${getDetailsTitleText}" alt="${imageAltText}"/>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${imagePath != 'null'}">
									<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${imageAltText}" width="146" height="146" />
								</c:when>
								<c:otherwise>
									<img class="productImage" src='/_assets/global/images/no_image_available.jpg' alt="${imageAltText}" width="146" height="146" />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</div>
					
				<%-- End is Product thumbnail image empty --%>
				<div class="prodText fl width_4">
					<h3 class="productName"><dsp:valueof param="productVO.name" valueishtml="true"/></h3>
					<c:if test="${MapQuestOn && (appid ne BedBathCanadaSite)}">
						<a title="<bbbl:label key='lbl_notifyme_find_store_near_you' language="${pageContext.request.locale.language}" />" href="#" class="changeStore upperCase">
						<bbbl:label key='lbl_notifyme_find_store_near_you' language="${pageContext.request.locale.language}" /></a>
					</c:if>
				</div>
			</div>
		</dsp:oparam>
		</dsp:droplet>
		
		<%-- This empty form is needed in order for the actual form tag to show up for frmNotifyOOS --%>
		<form id="requiredEmptyForm" action="" method="post">
		</form>
  		<dsp:form id="frmNotifyOOS" method="post" action="${contextroot}/browse/notifyMeConfirmPopup.jsp">
			<input name="skuId" type="hidden" value="${skuId}"/>
    		<input name="productId" type="hidden" value="${productId}"/>
    		<input name="productName" type="hidden" value="${fn:escapeXml(productName)}"/>
 			
 			<div class="small-8 columns">
				<div class="input formRow">
					<div class="label">
						<label id="lblfullName" for="fullName"><bbbl:label key='lbl_notifyme_name' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
					</div>
					<div class="text">
						<dsp:getvalueof var="fName" bean="Profile.firstName"/>
						<input type="text" id="fullName" name="fullName" value="${fName}" aria-required="true" aria-labelledby="lblfullName errorfullName" />
					</div>			
				</div>
				<div class="input formRow">
					<div class="label">
						<label id="lblemail" for="email"><bbbl:label key='lbl_notifyme_email' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
					</div>
					<div class="text">
						<input type="text" id="email" name="email" aria-required="true" aria-labelledby="lblemail erroremail"/>
					</div>			
				</div>
				<div class="input formRow">
					<div class="label">
						<label id="lblemailConfirm" for="emailConfirm"><bbbl:label key='lbl_notifyme_confirm_email' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
					</div>
					<div class="text">
						<input type="text" id="emailConfirm" name="emailConfirm" aria-required="true" aria-labelledby="lblemailConfirm erroremailConfirm" />
					</div>			
				</div>
				<p class="formRow"><bbbl:label key='lbl_notifyme_your_email' language="${pageContext.request.locale.language}" /></p>
				<div class="formRow">
					<div class="button_active small-6 columns no-padding">
						<input id="sendNotificationEmailBtn" class="tiny button expand transactional" type="submit" value="SUBMIT" aria-pressed="false" role="button" />
					</div>
					<div class="small-6 columns no-padding-right">
						<a href="#" class="buttonTextLink tiny button expand secondary close-modal" role="link"><bbbl:label key='lbl_notifyme_cancel' language="${pageContext.request.locale.language}" /></a>
					</div>
				</div>
			</div>
		</dsp:form>
		<a class="close-reveal-modal">&#215;</a>
	</div>
</dsp:page>