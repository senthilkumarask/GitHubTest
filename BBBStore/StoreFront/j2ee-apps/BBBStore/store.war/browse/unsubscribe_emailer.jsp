<dsp:page>
<dsp:importbean bean="/com/bbb/browse/BBBBackInStockFormHandler"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<head>
<c:set var="section" value="accounts" scope="request" />
<c:set var="pageWrapper" value="unsubscribeMail unsubscribeMailer unsubscribeOOSProduct" scope="request" />
</head>
   <bbb:pageContainer pageVariation="${pageVariation}">
	<jsp:attribute name="themeWrapper">${themeName}</jsp:attribute>
	<jsp:attribute name="pageWrapper">${pageWrapper} </jsp:attribute>
	<jsp:body>
	<dsp:getvalueof var="appid" bean="Site.id" />
    <c:import url="/_includes/header/header_${themeName}.jsp" />
    <dsp:getvalueof var="success" param="success"/>
        
    <div id="content" class="container_12 clearfix creditCard" role="main">
            
                <dsp:form id="unsubscribeMail">
                <div class="grid_10 marTop_20 suffix_1 prefix_1 clearfix">
                <c:choose>
				<c:when test="${success eq true }">
					<div class="containerBorder highlightBox">
						<h6 class="txtHighlight"><bbbl:label key='lbl_unsubscribe_email_unsubscribed' language="${pageContext.request.locale.language}" /></h6>
						<p><bbbl:label key='lbl_unsubscribe_email_thank_you' language="${pageContext.request.locale.language}" /></p>
					</div>
				</c:when>				
				<c:otherwise>
				
				<dsp:droplet name="SKUDetailDroplet">
				<dsp:param name="siteId" value="${appid}"/>
				<dsp:param name="skuId" param="skuId"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="SKUVO" param="pSKUDetailVO"/>
						<c:if test="${not empty SKUVO}">
							<dsp:getvalueof var="displayName" param="pSKUDetailVO.displayName" />
							<dsp:getvalueof var="imageURL" param="pSKUDetailVO.skuImages.largeImage" />
							<dsp:getvalueof var="productId" param="pSKUDetailVO.parentProdId"/>
								
					  		<dsp:input bean="BBBBackInStockFormHandler.productId" type="hidden" value="${productId}"/>
                            <dsp:input bean="BBBBackInStockFormHandler.productName" type="hidden" value="${displayName}"/>
							
							<h2 class="marTop_10 marBottom_20">${displayName}</h2>
							<div class="grid_5 alpha">
								<c:choose>
								<c:when test="${empty imageURL}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" class="noImageFound" height="380" width="380" alt="${displayName}"/>
								</c:when>
								<c:otherwise>
									<img src="${scene7Path}/${imageURL}" class="mainProductImage noImageFound" height="380" width="380" alt="${displayName}"/>
								</c:otherwise>
								</c:choose>
							</div>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
				
				<div class="grid_5 omega">
					<div class="checkboxItem input clearfix noBorder marBottom_10">
						<div class="checkbox">
                            <input name="unsubscribeMail" id="unsubscribeEmailer" type="checkbox" value="unsubscribeMail" role="checkbox" aria-labelledby="lblunsubscribeEmailer errorunsubscribeEmailer" />
                        </div>
                        <div class="label">
                            <label id="lblunsubscribeEmailer" for="unsubscribeEmailer"><bbbl:label key='lbl_unsubscribe_email_like_to_unsubscribe' language="${pageContext.request.locale.language}" /></label>
                        </div>
                        <label id="errorunsubscribeEmailer" for="unsubscribeEmailer" generated="true" class="error"></label>
					</div>
					
				    <div class="clear"></div>
				                   
                    <div class="input grid_3 suffix_2 alpha omega clearfix marBottom_10">
                        <div class="label">
                            <label id="lblemail" for="email"><bbbl:label key='lbl_unsubscribe_email_enter_email' language="${pageContext.request.locale.language}" /></label>
                        </div>
                        <div class="text">
                        	<dsp:getvalueof var="emailAddress" param="email"/>
                        	<c:choose>
                        		<c:when test="${not empty emailAddress }">
                        			<dsp:input bean="BBBBackInStockFormHandler.emailAddress" id="email" iclass="width_3" type="text" value="${emailAddress}" name="email">
                        			<dsp:tagAttribute name="placeholder" value="email"/>
                        			<dsp:tagAttribute name="aria-required" value="false"/>
                        			<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                        			</dsp:input>
                        		</c:when>
                        		<c:otherwise>
                        			<dsp:input bean="BBBBackInStockFormHandler.emailAddress" id="email" iclass="width_3" type="text" value="" name="email">
                                        <dsp:tagAttribute name="placeholder" value="email"/>
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                        			</dsp:input>
                        		</c:otherwise>
                        	</c:choose>                        
                        </div>
                    </div>
                    <div class="clear"></div>
                    <dsp:input bean="BBBBackInStockFormHandler.catalogRefId" type="hidden" paramvalue="skuId"/>
                    <!--<dsp:input bean="BBBBackInStockFormHandler.successURL" type="hidden" value="unsubscribe_emailer.jsp?success=true"/>
                    <dsp:input bean="BBBBackInStockFormHandler.errorURL" type="hidden" value="unsubscribe_emailer.jsp?success=false"/>-->
                    <dsp:input bean="BBBBackInStockFormHandler.fromPage" type="hidden" value="unsubscribeemail" />
                    <div class="marTop_10 clearfix">
                        <div class="button button_active">
                            <dsp:input bean="BBBBackInStockFormHandler.unSubscribeOOSEmail" type="submit" value="unsubscribe" id="unsubscribe" />
                        </div>
                    </div>
                  </div>
                </c:otherwise>
                </c:choose>
                </div>
                </dsp:form>
    </div>
    
    <c:import url="/_includes/footer/footer_${themeName}.jsp" />
</div>
<c:import url="/_includes/script_loader.jsp" />
</
</jsp:body>
</bbb:pageContainer>

</dsp:page>