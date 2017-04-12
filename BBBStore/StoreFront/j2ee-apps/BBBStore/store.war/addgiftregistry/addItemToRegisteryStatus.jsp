<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
		<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
		<%-- adding this flag for BSL-2710 --%>
		<c:set var="pixelFbOn" scope="request"><bbbc:config key="pixel_fb_enable" configName="FlagDrivenFunctions" /></c:set>
		<c:set var="pixelTwtOn" scope="request"><bbbc:config key="pixel_twt_enable" configName="FlagDrivenFunctions" /></c:set>
		<dsp:getvalueof var="jasonObj" param="addItemResults" />
		<dsp:getvalueof var="regId" param="registryId" />
		<dsp:getvalueof var="regName" param="registryName" />
		<dsp:getvalueof var="totQty" param="totQuantity" />
		<dsp:getvalueof var="ltlFlag" param="ltlFlag" />
		<dsp:getvalueof var="atrSuccess" param="atrSuccess" />
		<dsp:getvalueof var="fromGuestRegistry" param="fromGuestRegistry" />
		<dsp:getvalueof var="porchWarning" param="porchWarning" />
		
		<c:set var="isEnableATRModal">
			<bbbc:config key="enableATRModal" configName="FlagDrivenFunctions" />
		</c:set>
		

		<dsp:setvalue bean="GiftRegistryFormHandler.jasonCollectionObj" value="${jasonObj}" />
		<dsp:setvalue bean="GiftRegistryFormHandler.ltlFlag" value="${ltlFlag}" />
	<dsp:getvalueof var="addAll" value="${param.addAll}" />
		 <c:if test="${not empty addAll}">
             <dsp:setvalue  bean="GiftRegistryFormHandler.kickStarterAddAllAction" value="AddAllItems"/>
             <dsp:getvalueof var="consultantName" value="${param.heading1}" />
         </c:if>
		<c:if test="${not empty jasonObj}">
			<dsp:setvalue bean="GiftRegistryFormHandler.jasonCollectionObj" value="${jasonObj}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.addItemToGiftRegistry" />
		<dsp:getvalueof
			bean="GiftRegistryFormHandler.errorFlagAddItemToRegistry" var="flag" />
			<dsp:setvalue param="giftRegistryViewBean" beanvalue="SessionBean.giftRegistryViewBean" />
		</c:if>
		<dsp:getvalueof var="isDeclined" param="isDeclined" />
		<dsp:getvalueof var="isFromPendingTab" param="isFromPendingTab" />
		<dsp:getvalueof var="isFromDeclinedTab" param="isFromDeclinedTab" />

		<dsp:getvalueof id="omnitureRegistryProd"  param="giftRegistryViewBean.omniProductList"/>
		<dsp:getvalueof id="omnitureRegistryId"  param="giftRegistryViewBean.registryId"/>
		<dsp:getvalueof id="omnitureRegistryName"  param="giftRegistryViewBean.registryName"/>
		<dsp:getvalueof id="registryName"  param="giftRegistryViewBean.registryName"/>
		<dsp:getvalueof id="totQuantity"  param="giftRegistryViewBean.totQuantity"/>
		<dsp:getvalueof id="personalizedImageUrls"  param="giftRegistryViewBean.personalizedImageUrls"/>
			<c:if test="${empty addAll}">
			<dsp:getvalueof id="consultantName"  param="giftRegistryViewBean.consultantName"/>
			</c:if>
	<c:choose>
		<c:when test="${not empty param.additemflagerror}">
			<c:choose>
			    <c:when test="${param.additemflagerror == 'true'}">
			        <div title='<bbbe:error key="err_add_item_registry" language ="${pageContext.request.locale.language}"/>'>
			        <div class="fl noMarBot clearfix">
						<div class="fl button button_active">
								<input type="submit" value="CLOSE" name="CLOSE" class="close-any-dialog" role="button" aria-pressed="false">
						</div>
			        </div>
			        </div>
			    </c:when>
			    <c:otherwise>
			    	<c:choose>
			    	<c:when test="${empty addAll && isEnableATRModal && atrSuccess}">
			    			<dsp:include page="/browse/atr_success.jsp">
			    			 <dsp:param name="regName" value="${regName}"/>
			    			</dsp:include>
			    		</c:when>
			    		<c:when test="${empty addAll && isEnableATRModal}">
			    			<dsp:include page="/browse/addtoregistry_modal.jsp">
			    				<dsp:param name="jsonObj" value="${jasonObj}"/>
			    				<dsp:param name="personalizedImageUrls" value="${personalizedImageUrls}"/>
			    			</dsp:include>
			    		</c:when>
			    		<c:otherwise>
			    			<c:if test="${omnitureRegistryName eq 'Other'}">
								<c:set var="omnitureRegistryName" value="etc"/>
							</c:if>
					        <div title='${totQty} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/> ${fn:escapeXml(regName)} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/>'>
		                 		<%-- Check for additional charges apply message in case of LTL Item added to registry --%>
		                 		<%-- <c:if test="${ltlFlag eq true}">
			                           	<div class="ltlMessage padBottom_10"><bbbl:label key="lbl_addn_charges_apply_ltl_registry"
			                           				language ="${pageContext.request.locale.language}"/></div>
		                     	</c:if> --%>
		
		                     	<%-- Check if porch service was added to this product, show msg saying service does not get attached--%>
								
								<c:if test="${porchWarning eq true}">
					            	<h5 class="red padBottom_10">	            		
					            		<bbbl:label key="lbl_bbby_porch_pdp_add_to_registry" language ="${pageContext.request.locale.language}"/>
					            	</h5>
				                </c:if>
		
						        <div class="fl noMarBot clearfix">
								<div class="fl button button_active button_active_orange">
										<input type="submit" value="Ok" name="Ok" class="close-any-dialog" role="button" aria-pressed="false">
								</div>
								<div class="fl bold marTop_5 marLeft_10">
										<dsp:a href="/store/giftregistry/view_registry_owner.jsp"><bbbt:textArea key="txt_pdp_reg_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
											<dsp:param name="registryId" value="${regId}"/>
											<dsp:param name="eventType" value="${fn:escapeXml(regName)}"/>
										</dsp:a>
									<div class="clear"></div>
								</div>
								</div>
							</div>
							<script type="text/javascript">
							pageName = document.location.pathname.split('/')[2];
							/* var eVar1=""; */
							if (pageName === ""){
								omniPageName = "Home";
							} else {
								var consultantName ="${consultantName}";
								if(!consultantName.length){
									omniPageName = pageName+'Page';
									/* eVar1=""; */
									}else{
									omniPageName =pageName+'-'+consultantName;
									/* eVar1=omniPageName; */
								}
							}
								  var BBB = BBB || {};
									BBB.registryInfo = {
											registryPagename: "registryAdd",
										    product:"${omnitureRegistryProd}",
											var23 : '${fn:escapeXml(omnitureRegistryName)}',
											var24 : '${omnitureRegistryId}',
											var46 : omniPageName,
											kickStarterName:pageName
										};
				            </script>
		
				            <%--FB & Twitter Pixel tracking starts --%>
							<c:if test="${pixelFbOn}">
								<c:set var="fb_regAdd"><bbbc:config key="pixel_fb_regAdd" configName="ContentCatalogKeys" /></c:set>
		
								<script>(function() {
								  var _fbq = window._fbq || (window._fbq = []);
								  if (!_fbq.loaded) {
								    var fbds = document.createElement('script');
								    fbds.async = true;
								    fbds.src = '//connect.facebook.net/en_US/fbds.js';
								    var s = document.getElementsByTagName('script')[0];
								    s.parentNode.insertBefore(fbds, s);
								    _fbq.loaded = true;
								  }
								})();
								window._fbq = window._fbq || [];
								window._fbq[window._fbq.length] = ['track', '${fb_regAdd}', {'value':'0.00','currency':'USD'}];
								</script>
								<noscript><img height="1" width="1" alt="" style="display:none" src="https://www.facebook.com/tr?ev=${fb_regAdd}&amp;cd[value]=0.00&amp;cd[currency]=USD&amp;noscript=1" /></noscript>
							</c:if>
		
							<c:if test="${pixelTwtOn}">
								<c:set var="twt_regAdd"><bbbc:config key="pixel_twt_regAdd" configName="ContentCatalogKeys" /></c:set>
		
								<!-- script src="//platform.twitter.com/oct.js" type="text/javascript"></script -->
								<script type="text/javascript">
								if(BBB.twttrjs) { 
									twttr.conversion.trackPid('${twt_regAdd}');
								}
								</script>
								<noscript>
								<img height="1" width="1" style="display:none;" alt="" src="https://analytics.twitter.com/i/adsct?txn_id=${twt_regAdd}&p_id=Twitter" />
								<img height="1" width="1" style="display:none;" alt="" src="//t.co/i/adsct?txn_id=${twt_regAdd}&p_id=Twitter" />
								</noscript>
							</c:if>
							<%--FB & Twitter Pixel tracking ends --%>
			    		</c:otherwise>
			    	</c:choose>
					

			    </c:otherwise>
			</c:choose>
		</c:when>
	<c:otherwise>
			<c:if test="${omnitureRegistryName eq 'Other'}">
				<c:set var="omnitureRegistryName" value="etc"/>
			</c:if>
			<c:choose>
				<c:when test="${flag==true }">
					<div title='<bbbe:error key="err_add_item_registry" language ="${pageContext.request.locale.language}"/>'>
					<div class="clearfix marBottom_20">
						<dsp:droplet name="ErrorMessageForEach">
							<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions" />
								<dsp:oparam name="output">
									<p class="error"><dsp:valueof param="message"/></p>
								</dsp:oparam>
						</dsp:droplet>
					</div>
					<div class="clear"></div>
					<div class="fl noMarBot clearfix">
						<div class="fl button button_active button_active_orange">
								<input type="submit" value="Ok" name="Ok" class="close-any-dialog" role="button" aria-pressed="false">
						</div>
			        </div>
					</div>
				</c:when>
				<c:otherwise>
				<c:choose>
					<c:when test="${isFromPendingTab == 'true' || isFromDeclinedTab == 'true'}">
					<div class="pendingItemStatus">
						<c:choose>
						  <c:when test="${isDeclined =='true'}">
						  	  false
						  </c:when>
						  <c:otherwise>
						  	  true
						  </c:otherwise>
						</c:choose>
					</div>
				</c:when>
				<c:otherwise>
					<c:choose>
					<c:when test="${empty addAll && isEnableATRModal && atrSuccess}">
			    			<dsp:include page="/browse/atr_success.jsp">
			    			 <dsp:param name="regName" value="${regName}"/>
			    			</dsp:include>
			    		</c:when>
						<c:when test="${empty addAll && isEnableATRModal}">
							<dsp:include page="/browse/addtoregistry_modal.jsp">
			    				<dsp:param name="jsonObj" value="${jasonObj}"/>
			    				<dsp:param name="personalizedImageUrls" value="${personalizedImageUrls}"/>
			    			</dsp:include>
						</c:when>
						<c:otherwise>
							<div title='${totQuantity} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/> ${fn:escapeXml(registryName)} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/>'>
							<%-- Check for additional charges apply message in case of LTL Item added to registry --%>
							<%-- <c:if test="${ltlFlag eq true}">
				                           	<div class="ltlMessage padBottom_10"><bbbl:label key="lbl_addn_charges_apply_ltl_registry"
				                           				language ="${pageContext.request.locale.language}"/></div>
			                </c:if> --%>
			
							<%-- Check if porch service was added to this product, show msg saying service does not get attached--%>				
							<c:if test="${porchWarning eq true}">
				            	<h5 class="red padBottom_10">	            		
				            		<bbbl:label key="lbl_bbby_porch_pdp_add_to_registry" language ="${pageContext.request.locale.language}"/>
				            	</h5>
			                </c:if>
			
							<div class="fl noMarBot clearfix">
								<div class="fl button button_active">
								<input type="submit" value="CLOSE" name="CLOSE" class="close-any-dialog" role="button" aria-pressed="false">
								</div>
								<div class="fl bold marTop_5 marLeft_10">
										<dsp:a href="/store/giftregistry/view_registry_owner.jsp"><bbbt:textArea key="txt_pdp_reg_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
											<dsp:param name="registryId" value="${omnitureRegistryId}"/>
											<dsp:param name="eventType" value="${fn:escapeXml(registryName)}"/>
										</dsp:a>
									<div class="clear"></div>
								</div>
							</div>
								 </div>
			
					           <%--FB & Twitter Pixel tracking starts --%>
								<c:if test="${pixelFbOn}">
									<c:set var="fb_regAdd"><bbbc:config key="pixel_fb_regAdd" configName="ContentCatalogKeys" /></c:set>
			
									<script>(function() {
									  var _fbq = window._fbq || (window._fbq = []);
									  if (!_fbq.loaded) {
									    var fbds = document.createElement('script');
									    fbds.async = true;
									    fbds.src = '//connect.facebook.net/en_US/fbds.js';
									    var s = document.getElementsByTagName('script')[0];
									    s.parentNode.insertBefore(fbds, s);
									    _fbq.loaded = true;
									  }
									})();
									window._fbq = window._fbq || [];
									window._fbq[window._fbq.length] = ['track', '${fb_regAdd}', {'value':'0.00','currency':'USD'}];
									</script>
									<noscript><img height="1" width="1" alt="" style="display:none" src="https://www.facebook.com/tr?ev=${fb_regAdd}&amp;cd[value]=0.00&amp;cd[currency]=USD&amp;noscript=1" /></noscript>
								</c:if>
			
								<c:if test="${pixelTwtOn}">
									<c:set var="twt_regAdd"><bbbc:config key="pixel_twt_regAdd" configName="ContentCatalogKeys" /></c:set>
			
									<!-- script src="//platform.twitter.com/oct.js" type="text/javascript"></script -->
									<script type="text/javascript">
									if(BBB.twttrjs) {  twttr.conversion.trackPid('${twt_regAdd}'); }
									</script>
									<noscript>
									<img height="1" width="1" style="display:none;" alt="" src="https://analytics.twitter.com/i/adsct?txn_id=${twt_regAdd}&p_id=Twitter" />
									<img height="1" width="1" style="display:none;" alt="" src="//t.co/i/adsct?txn_id=${twt_regAdd}&p_id=Twitter" />
									</noscript>
								</c:if>
								<%--FB & Twitter Pixel tracking ends --%>
						</c:otherwise>
					</c:choose>
				 </c:otherwise>
		           </c:choose>
				</c:otherwise>
			</c:choose>
			<script type="text/javascript">
				pageName = document.location.pathname.split('/')[2];
				/* var eVar1=""; */
				if (pageName === ""){
					omniPageName = "Home";
				} else {
					var consultantName ="${consultantName}";
					if(!consultantName.length){
						omniPageName = pageName+'Page';
						/* eVar1=""; */
					} else {
							if(pageName === "shopthislook"){
								var cName = consultantName.replace("-"," ");
								omniPageName = "Shop this Look"+'-'+cName;
							} else if(pageName === "topconsultant") {
								omniPageName =pageName+'-'+consultantName;
						    }else {
								omniPageName ="Registry";
						    }
						/* eVar1=omniPageName; */
					 }
				}
			    var BBB = BBB || {};
				BBB.registryInfo = {
					registryPagename: "registryAdd",
				    product:"${omnitureRegistryProd}",
					var23 : '${fn:escapeXml(omnitureRegistryName)}',
					var24 : '${omnitureRegistryId}',
					var46 : omniPageName,
					kickStarterName:pageName
				};

	        </script>
		</c:otherwise>
		</c:choose>

	<dsp:setvalue bean="GiftRegistryFormHandler.clearSessionBeanData" />
</dsp:page>