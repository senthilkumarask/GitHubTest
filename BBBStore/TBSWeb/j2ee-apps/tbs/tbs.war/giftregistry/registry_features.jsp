<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<dsp:importbean
	bean="/com/bbb/profile/session/SessionBean" />
	
<dsp:getvalueof var="pageName" param="pageName" scope="page"/>
<c:set var="section" value="cms" scope="request" />
	
<dsp:page>
     <dsp:importbean bean="/atg/multisite/Site"/>
     <dsp:importbean bean="/com/bbb/cms/droplet/RegistryTemplateDroplet" />
		<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof id="siteURL" bean="Site.productionURL"/>
     <dsp:getvalueof var="siteId" bean="Site.id" />
<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:droplet name="RegistryTemplateDroplet">
				<dsp:oparam name="output">
					<dsp:getvalueof var="pageName" param="RegistryTemplateVO.pageName" />
					<dsp:getvalueof var="bbbPageName" param="RegistryTemplateVO.bbbPageName" />
					<dsp:getvalueof var="pageType" param="RegistryTemplateVO.pageType" />
					<dsp:getvalueof var="pageWrapperFromConfigkeys" param="RegistryTemplateVO.pageWrapper" />
					<dsp:getvalueof var="pageVariationFromConfigkeys" param="RegistryTemplateVO.pageVariation" />
					<dsp:getvalueof var="omnitureData" param="RegistryTemplateVO.omnitureData" />
					<dsp:getvalueof var="headTagContent" param="RegistryTemplateVO.headTagContent" />
					<dsp:getvalueof var="bodyEndTagContent" param="RegistryTemplateVO.bodyEndTagContent" />
					<c:if test="${not empty omnitureData}">
					<c:set var="omniPageName">${omnitureData['pageName'] }</c:set>
						<c:set var="channel">${omnitureData['channel'] }</c:set>
						<c:set var="prop1">${omnitureData['prop1'] }</c:set>
						<c:set var="prop2">${omnitureData['prop2'] }</c:set>
						<c:set var="prop3">${omnitureData['prop3'] }</c:set>
						<c:set var="prop4">${omnitureData['prop4'] }</c:set>
					</c:if>
					<c:if test="${pageWrapperFromConfigkeys ne null }">
						<c:set var="pageWrapper" value="${pageWrapperFromConfigkeys}" scope="request" />
					</c:if>
					<c:if test="${pageVariationFromConfigkeys ne null }">
						<c:set var="pageVariation" value="${pageVariationFromConfigkeys}" scope="request" />
					</c:if>

 <c:if test="${pageName eq 'BabyRegistryChecklist'}">
                        <c:set var="pageVariation" value="br" scope="request" />                          
 </c:if>
	<c:set var="nbsp" value=" " scope="request" />
	<c:set var="pageWrapper" value="${pageWrapper}${nbsp}${pageName}" scope="request" />

    <c:set var="titleString"><dsp:valueof param="RegistryTemplateVO.pageTitle" valueishtml="true"/></c:set>
	<bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" titleString="${titleString}" pageVariation="${pageVariation}">
				<jsp:attribute name="headTagContent">${headTagContent}</jsp:attribute>
				<jsp:attribute name="bodyEndTagContent">${bodyEndTagContent}</jsp:attribute>
		<div id="content" class="container_12 clearfix" role="main">
					<div class="breadcrumbs grid_12">
					<c:set var="lbl_reg_feature_home">
					<bbbl:label key="lbl_reg_feature_home" language="${pageContext.request.locale.language}" />
					</c:set>
						<a href="${contextPath}" title="${lbl_reg_feature_home}"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></a> 
						<c:choose>
							<c:when test="${pageName eq 'CollegeChecklistPage' || pageType eq 'College'}">
								<a href="${contextPath}/page/College"><bbbl:label key="lbl_reg_feature_college_reg" language ="${pageContext.request.locale.language}"/></a>
							</c:when>
							<c:otherwise>

							    <c:choose>
								    <c:when test="${siteId eq 'BedBathUS'}">
									    <c:set var="lbl_reg_feature_bridal_reg">
										<bbbl:label key="lbl_reg_feature_bridal_reg" language="${pageContext.request.locale.language}" />
										</c:set>
									      <a href="${contextPath}/page/Registry"  title="${lbl_reg_feature_bridal_reg}"><bbbl:label key="lbl_reg_feature_bridal_reg" language ="${pageContext.request.locale.language}"/></a>
								    </c:when>
								    <c:otherwise>
									    
									    <c:choose>
									        <c:when test="${siteId eq 'BedBathCanada'}"> 
											    <c:choose>
												    <c:when test="${babyCAMode eq 'true'}">
													    <c:set var="lbl_reg_feature_reg">
														<bbbl:label key="lbl_reg_feature_reg" language="${pageContext.request.locale.language}" />
														</c:set>
												        <a href="${contextPath}/page/BabyCanadaRegistry"  title="${lbl_reg_feature_reg}"><bbbl:label key="lbl_reg_feature_reg" language ="${pageContext.request.locale.language}"/></a>
												    </c:when>
												    <c:otherwise>
												         <c:set var="lbl_reg_feature_reg">
														 <bbbl:label key="lbl_reg_feature_reg" language="${pageContext.request.locale.language}" />
														 </c:set>
												         <a href="${contextPath}/page/Registry"  title="${lbl_reg_feature_reg}"><bbbl:label key="lbl_reg_feature_reg" language ="${pageContext.request.locale.language}"/></a>
												    </c:otherwise>
											    </c:choose>									      
									        </c:when>
										    <c:otherwise>
										        <c:set var="lbl_reg_feature_baby_reg">
												<bbbl:label key="lbl_reg_feature_baby_reg" language="${pageContext.request.locale.language}" />
												</c:set>
										        <a href="${contextPath}/page/BabyRegistry"  title="${lbl_reg_feature_baby_reg}"><bbbl:label key="lbl_reg_feature_baby_reg" language ="${pageContext.request.locale.language}"/></a>
										    </c:otherwise>
									    </c:choose>
								      							  
								    </c:otherwise>
							    </c:choose>
							    
							</c:otherwise>
						</c:choose>
						 <span class="bold"><dsp:valueof param="RegistryTemplateVO.pageTitle" valueishtml="true"/></span>
					</div>
                <div class="row">
					<div class="jCMSLeftNav large-3 columns small-medium-right-off-canvas-menu left-nav" role="navigation">
					<c:choose>
					<c:when test="${pageName eq 'CollegeChecklistPage' || pageType eq 'College'}">
						<dsp:include page="/cms/college_left_navigation.jsp" />
					</c:when>
					<%-- Start - Changes for Group Event skedge me --%>
					<c:when test="${pageName eq 'RockYourRegistry'}">
					</c:when>
					<%-- End - Changes for Group Event skedge me --%>
					<c:otherwise>
						<dsp:include page="/cms/left_navigation.jsp" />
					</c:otherwise>
					</c:choose>
					</div>

					<div id="cmsPageHeadContentWrapper" class="small-12 large-9 columns product-grid-container">
                        <div id="cmsPageHead" class="grid_9" >
                            <dsp:getvalueof var="promoImageURL"	param="RegistryTemplateVO.promoImageURL" />
                            <dsp:getvalueof var="promoImageAltText"	param="RegistryTemplateVO.promoImageAltText" />
                            <dsp:getvalueof var="pageHeaderFeaturedContent"	param="RegistryTemplateVO.pageHeaderFeaturedContent" />

                            <c:if test="${pageName eq 'RegistryIncentives' || pageName eq 'FreeGoodyBag'}">
                                <div class="clearfix">
                                <h1>
                                    <dsp:valueof param="RegistryTemplateVO.pageTitle" valueishtml="true"/>
                                </h1>
                                <p class="noMarTop noMarLeft grid_6"><bbbl:label key="lbl_reg_feature_reg_incentive" language="${pageContext.request.locale.language}" /></p>
                                <div class="grid_4 share noMar">
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
                                            <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=standard&amp;width=312&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:312px; height:24px;" allowTransparency="true"></iframe>
                                        </div>
                                    <![endif]-->
                                    <!--[if !IE 7]><!-->
                                        <!-- <div class="fb-like" data-send="false" data-show-faces="false"></div> -->
                                    <!--<![endif]-->
                                    </c:if>
                                   </div>
                                   <div class="btn marTop_10 clearfix noMarLeft">
                                   <dsp:form method="post">

                                    <c:choose >
                                       <c:when test="${siteId eq 'BedBathUS' || (siteId eq 'BedBathCanada' and (pageName eq 'RegistryIncentives'))}">
                                        <dsp:input bean="GiftRegistryFormHandler.registryEventType" value="BRD" type="hidden"/>
                                       </c:when>
                                       <c:otherwise>
                                        <dsp:input bean="GiftRegistryFormHandler.registryEventType" value="BA1" type="hidden"/>
                                       </c:otherwise>
                                     </c:choose>
                                     <dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden" value="/store/giftregistry/registry_creation_form.jsp" />
                                      <%-- <div class="button_active">
                                        <dsp:input type="submit" id="registrySubmit" bean="GiftRegistryFormHandler.registryTypes" value="Create Registry" iclass="small button">
                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="registrySubmit"/>
                                            <dsp:tagAttribute name="role" value="button"/>
                                        </dsp:input>
                                      </div> --%>
                                    </dsp:form>
                                </div>
                                </div>
                                <c:if test="${promoImageURL != null}">
                                    <div class="grid_2 marTop_20">
                                        <img src="${promoImageURL}" height="146" width="146"
                                            alt="${promoImageAltText}" />
                                    </div>
                                </c:if>
                            </c:if>

                            <c:choose>
                            <c:when	test="${pageName eq 'BridalToolkit' || pageName eq 'PersonalizedInvitations' || pageName eq 'RegistryChecklist' || pageName eq 'BabyRegistryChecklist'}">
                                <dsp:setvalue value="BRD" bean="SessionBean.registryTypesEvent" />

                                    <div class="clearfix grid_9 omega bridalToolKit alpha">
                                        <h1 >
                                            <dsp:valueof param="RegistryTemplateVO.pageTitle" valueishtml="true"/>
                                        </h1>
                                        <dsp:valueof param="RegistryTemplateVO.pageHeaderCopy" valueishtml="true" />
                                        <c:if test="${pageName eq 'PersonalizedInvitations'}">
                                            <div class="grid_4 noMarLeft noMarRight share">
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
                                                        <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=standard&amp;width=312&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:312px; height:24px;" allowTransparency="true"></iframe>
                                                    </div>
                                                <![endif]-->
                                                <!--[if !IE 7]><!-->
                                                   <!--  <div class="fb-like" data-send="false" data-show-faces="false"></div> -->
                                                <!--<![endif]-->
                                                </c:if>
                                            </div>
                                        </c:if>

                                        
                                            <c:if test="${pageName eq 'RegistryChecklist' || pageName eq 'BabyRegistryChecklist'}">
                                            <div class="btn marTop_10 clearfix noMarLeft">
                                                <div class="button_active marRight_5 fl">
                                                    <a class="button small" href="javascript:void(0);" title="Print Checklist" onclick="window.print();return false;">Print Checklist</a>
                                                </div>
                                                <div class="grid_5 share marTop_5">
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
                                                    <div class="fb-like width_4">
                                                        <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=standard&amp;width=312&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:312px; height:24px;" allowTransparency="true"></iframe>
                                                    </div>
                                                <![endif]-->
                                                <!--[if !IE 7]><!-->
                                                    <!-- <div class="fb-like width_4" data-width="312" data-send="false" data-show-faces="false"></div> -->
                                                <!--<![endif]-->
                                                </c:if>
                                                </div>
                                                </div>
                                             </c:if>
                                             <c:if test="${pageName eq 'BridalToolkit'}">
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
                                                    <div class="fb-like width_4">
                                                        <iframe title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=standard&amp;width=312&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:312px; height:24px;" allowTransparency="true"></iframe>
                                                    </div>
                                                <![endif]-->
                                                <!--[if !IE 7]><!-->
                                                    <!-- <div class="fb-like width_4" data-width="312" data-send="false" data-show-faces="false"></div> -->
                                                <!--<![endif]-->
                                                </c:if>
                                                <bbbt:textArea key="txt_reg_template_bridal_toolkit" language ="${pageContext.request.locale.language}"/>
                                                <dsp:include page="../bridalregistries/bridal_toolkit_gift_registry.jsp">
                                                </dsp:include>
                                            </c:if>

                                    </div>
                                </c:when>


                                <c:when test="${pageName eq 'RegistryFeatures' || pageName eq 'Others' }">
                                    <div class="clearfix">
                                    <h1>
                                        <dsp:valueof param="RegistryTemplateVO.pageTitle" valueishtml="true"/>
                                    </h1>
                                    <div class="fl share marBottom_5">
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
                                        <div class="fb-like width_4">
                                            <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=standard&amp;width=312&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:312px; height:24px;" allowTransparency="true"></iframe>
                                        </div>
                                    <![endif]-->
                                    <!--[if !IE 7]><!-->
                                        <!-- <div class="fb-like width_4" data-width="312" data-send="false" data-show-faces="false"></div> -->
                                    <!--<![endif]-->
                                    </c:if>
                                    </div>
                                    </div>
                                    <dsp:valueof param="RegistryTemplateVO.pageHeaderCopy" valueishtml="true" />
                                </c:when>
                                <%-- Start - Changes for Group Event skedge me --%>
                                 <c:when test="${pageName eq 'RockYourRegistry'}">
                                 <div class="grid_12">
                                 	<c:set var="skedgeGroupEventsURL"><bbbc:config key="skedgeGroupEventsURL" configName="ThirdPartyURLs" /></c:set>
                                 	<iframe src="${skedgeGroupEventsURL}" border="0" width="976px" height="500" ></iframe>
                               	</div>
                                 </c:when>
                                 <%-- End - Changes for Group Event skedge me --%>
                                <c:otherwise>

                                        <c:if test="${pageName eq 'CollegeChecklistPage'}">
                                        <div class="grid_9 alpha clearfix">
                                        <h1 class="fl">
                                            <dsp:valueof param="RegistryTemplateVO.pageTitle" valueishtml="true"/>
                                        </h1>
                                            <div class="fl share">
                                                <dsp:getvalueof var="colgurl" param="collegePdfURL"/>
                                                <a href="#" class="print" title="Print" target="blank"></a>
                                                <a href="#" class="email" title="Email"></a>
                                                <%-- BBBSL-422 <a href="${colgurl}" class="pdf" title="Download PDF" target="_blank"></a> --%>
                                            </div>
                                            <div class="fl">
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
                                                    <div class="fb-like width_6">
                                                        <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=standard&amp;width=470&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:470px; height:24px;" allowTransparency="true"></iframe>
                                                    </div>
                                                <![endif]-->
                                                <!--[if !IE 7]><!-->
                                                    <div class="fb-like width_4" data-width="470" data-send="false" data-show-faces="false"></div>
                                                <!--<![endif]-->
                                                </c:if>
                                            </div>
                                            <dsp:valueof param="RegistryTemplateVO.pageHeaderCopy" valueishtml="true" />
                                    </div>
                                        </c:if>

                                </c:otherwise>
                            </c:choose>

                            <c:if test="${pageHeaderFeaturedContent != null}">
                                ${pageHeaderFeaturedContent}
                            </c:if>
                        </div>

                        <div id="cmsPageContent" class="grid_9 clearfix">
                           <dsp:getvalueof  var="pageCopy" param="RegistryTemplateVO.pageCopy"/>
                             <c:set var="pageCopytbs" value="${fn:replace(pageCopy,'/store', '/tbs')}"/>
                             <dsp:valueof value="${pageCopytbs}" valueishtml="true"/>
                            <dsp:getvalueof var="brandList" vartype="java.util.List"
                                param="RegistryTemplateVO.brands" />

                            <c:choose>
                                <c:when test="${not empty brandList}">
                                    <dsp:include page="/giftregistry/registry_brands.jsp">
                                        <dsp:param name="brandList" value="${brandList}" />
                                    </dsp:include>
                                </c:when>
                            </c:choose>

                        </div>
                    </div>
            </div>
		</div>
<jsp:attribute name="footerContent">
	<c:set var="titleString"><c:out value="${titleString}"  escapeXml="true"/></c:set>
<script type="text/javascript">
    if (typeof s !== 'undefined') {
        var omni_omniPageName='${fn:replace(fn:replace(omniPageName,'\'',''),'"','')}';
        var omni_channel='${fn:replace(fn:replace(channel,'\'',''),'"','')}';
        var omni_prop1='${fn:replace(fn:replace(prop1,'\'',''),'"','')}';
        var omni_prop2='${fn:replace(fn:replace(prop2,'\'',''),'"','')}';
        var omni_prop3='${fn:replace(fn:replace(prop3,'\'',''),'"','')}';
        var omni_prop4='${fn:replace(fn:replace(prop4,'\'',''),'"','')}';
        if(omni_omniPageName.length>0){
            s.pageName=omni_omniPageName; // pageName
            s.channel=omni_channel;
            s.prop1=omni_prop1;
            s.prop2=omni_prop2;
            s.prop3=omni_prop3;
            s.prop4=omni_prop4;
            s.prop6='${pageContext.request.serverName}';
            s.eVar9='${pageContext.request.serverName}';
            s.events = '';
            s.products = '';
            s.server='${pageContext.request.serverName}';
        } else {
            var customPageName = '${fn:replace(fn:replace(titleString,'\'',''),'"','')}';
            var pageName = '${fn:replace(fn:replace(pageName,'\'',''),'"','')}';
            var bbbPageName = '${fn:replace(fn:replace(bbbPageName,'\'',''),'"','')}';
            s.pageName = customPageName;
            s.channel = 'Registry';
            s.prop1 = 'Registry';
            s.prop2 = 'Registry';
            s.prop3 = 'Registry';
            s.prop4 = '';
            s.prop6 = '${pageContext.request.serverName}';
            s.eVar9 = '${pageContext.request.serverName}';
            if(pageName=='PrenatalWorkout' || pageName=='PaintingTips'){
                customPageName = 'Content Page > ' + customPageName;
                s.pageName = customPageName;
                s.channel = 'Guides & Advice';
                s.prop4 = 'Guides & Advice';
            }else if(pageName=='FreeGoodyBag' || pageName=='PersonalizedInvitations' || pageName=='RegistryIncentives'){
                s.prop1 = 'Registry';
                s.prop2 = 'Registry';
                s.prop3 = 'Registry';
                s.prop4 = '';
            }else if(pageName=='CollegeChecklistPage'){
                customPageName = 'College > ' + customPageName;
                s.channel = 'College';
                s.prop1 = 'Content Page';
                s.prop2 = 'Content Page';
                s.prop3 = 'Content Page';
                s.pageName = customPageName;
                s.prop4 = 'College';
            }else if(pageName=='Others'){
                if(bbbPageName == 'RegistryAnnouncementCards'){
                    s.prop1 = 'Registry';
                    s.prop2 = 'Registry';
                    s.prop3 = 'Registry';
                    s.prop4 = '';
                }else if(bbbPageName == 'CollegePracticalSolns'){
                    s.pageName = 'College > Practical Solutions';
                    s.channel = 'College';
                    s.prop1 = 'Sub Category Page';
                    s.prop2 = '';
                    s.prop3 = 'College>College>Practical Solutions';
                    s.prop4 = '';
                }else if(bbbPageName == 'CollegeAdmissionCalendar'){
                    s.channel = 'College';
                    s.prop4 = 'College';
                }else if(bbbPageName == 'CampusPhotoshoot'){
                    s.pageName = 'College > Campus Photoshoot > ' + customPageName;
                    s.prop4 = 'College';
                    s.channel = 'College';
                }else if(bbbPageName == 'CollegeInsider' || bbbPageName == 'MovingSolution' || bbbPageName == 'campuschecklist' || bbbPageName == 'apartmentchecklist'){
                    s.pageName = 'College > ' + customPageName;
                    s.channel = 'College';
                    s.prop1 = 'Content Page';
                    s.prop2 = 'Content Page';
                    s.prop3 = 'Content Page';
                    s.prop4 = 'College';
                }else if(bbbPageName == 'StudentCollegeExperience'){
                    s.pageName = 'College > Insider Guides > Life At College > ' + customPageName;
                    s.channel = 'College';
                    s.prop1 = 'Content Page';
                    s.prop2 = 'Content Page';
                    s.prop3 = 'Content Page';
                    s.prop4 = 'College';
                }
            }
        }
        fixOmniSpacing();
        var s_code = s.t();
        if (s_code) document.write(s_code);
    }
</script>
  </jsp:attribute>
</bbb:pageContainer>
</dsp:oparam>
<dsp:oparam name="empty">
		<dsp:droplet name="/atg/dynamo/droplet/Redirect">
			<dsp:param name="url" value="/"/>
		</dsp:droplet>
</dsp:oparam>
</dsp:droplet>
</dsp:page>