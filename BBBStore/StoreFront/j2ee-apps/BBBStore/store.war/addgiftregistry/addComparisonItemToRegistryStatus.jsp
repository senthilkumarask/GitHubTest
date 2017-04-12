<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
		<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
		<dsp:getvalueof var="jasonObj" param="addItemResults" />
		<dsp:getvalueof var="regName" param="registryName" />
		<dsp:getvalueof var="registryId" param="registryId" />
		<dsp:getvalueof var="totQty" param="totQuantity" />
		<dsp:getvalueof var="ltlFlag" param="ltlFlag" />
		<dsp:getvalueof var="atrSuccess" param="atrSuccess" />
		<dsp:getvalueof var="fromGuestRegistry" param="fromGuestRegistry" />
		<dsp:setvalue bean="GiftRegistryFormHandler.jasonCollectionObj" value="${jasonObj}" />
		<dsp:setvalue bean="GiftRegistryFormHandler.ltlFlag" value="${ltlFlag}" />
		<dsp:setvalue bean="GiftRegistryFormHandler.addItemToGiftRegistry" />
		<c:set var="isEnableATRModal">
			<bbbc:config key="enableATRModal" configName="FlagDrivenFunctions" />
		</c:set>
		
		<dsp:getvalueof
			bean="GiftRegistryFormHandler.errorFlagAddItemToRegistry" var="flag" />
			<dsp:setvalue param="giftRegistryViewBean" beanvalue="SessionBean.giftRegistryViewBean" />
			<dsp:getvalueof id="registryName"  param="giftRegistryViewBean.registryName"/>
			<dsp:getvalueof id="totQuantity"  param="giftRegistryViewBean.totQuantity"/>
			<dsp:getvalueof id="regId"  param="giftRegistryViewBean.registryId"/>
			<dsp:getvalueof id="personalizedImageUrls"  param="giftRegistryViewBean.personalizedImageUrls"/>


			<dsp:getvalueof id="omnitureRegistryProd"  param="giftRegistryViewBean.omniProductList"/>
			<dsp:getvalueof id="omnitureRegistryId"  param="giftRegistryViewBean.registryId"/>
			<dsp:getvalueof id="omnitureRegistryName"  param="giftRegistryViewBean.registryName"/>
			<dsp:getvalueof id="registryName"  param="giftRegistryViewBean.registryName"/>
			<dsp:getvalueof id="totQuantity"  param="giftRegistryViewBean.totQuantity"/>
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
			    		<c:when test="${isEnableATRModal && atrSuccess && !fromGuestRegistry}">
			    			<dsp:include page="/browse/atr_success.jsp">
			    			 <dsp:param name="regName" value="${regName}"/>
			    			</dsp:include>
			    		</c:when>
			    		<c:when test="${isEnableATRModal && !fromGuestRegistry}">
			    			<dsp:include page="/browse/addtoregistry_modal.jsp">
			    				<dsp:param name="jsonObj" value="${jasonObj}"/>
			    				<dsp:param name="personalizedImageUrls" value="${personalizedImageUrls}"/>
			    			</dsp:include>
			    		</c:when>
			    		<c:otherwise>
			    			<div title='${totQty} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/>  ${regName} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/>'>
								<%-- Check for additional charges apply message in case of LTL Item added to registry --%>
		                 		<%-- <c:if test='${ltlFlag ne null && ltlFlag eq true}'>
		                           	<div class="ltlMessage padBottom_10"><bbbl:label key="lbl_addn_charges_apply_ltl_registry"
		                           				language ="${pageContext.request.locale.language}"/></div>
		                     	</c:if> --%>
						        <div class="fl noMarBot clearfix">
								<div class="fl button button_active button_active_orange">
										<input type="submit" value="Ok" name="Ok" class="close-any-dialog" role="button" aria-pressed="false">
								</div>
								<div class="fl bold marTop_5 marLeft_10">
										<dsp:a href="/store/giftregistry/view_registry_owner.jsp"><bbbt:textArea key="txt_pdp_reg_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
											<dsp:param name="registryId" value="${registryId}"/>
											<dsp:param name="eventType" value="${regName}"/>
										</dsp:a>
									<div class="clear"></div>
								</div>
								</div>
							</div>
			    		</c:otherwise>
			    	</c:choose>
			        
			    </c:otherwise>
			</c:choose>
		</c:when>
	<c:otherwise>
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
						<c:when test="${isEnableATRModal && atrSuccess && !fromGuestRegistry}">
			    			<dsp:include page="/browse/atr_success.jsp">
			    			 <dsp:param name="regName" value="${regName}"/>
			    			</dsp:include>
			    		</c:when>
			    		<c:when test="${isEnableATRModal && !fromGuestRegistry}">
			    			<dsp:include page="/browse/addtoregistry_modal.jsp">
			    				<dsp:param name="jsonObj" value="${jasonObj}"/>
			    				<dsp:param name="personalizedImageUrls" value="${personalizedImageUrls}"/>
			    			</dsp:include>
			    		</c:when>
			    		<c:otherwise>
							<div title='${totQuantity} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/> ${registryName} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/>'>
			                 		<%-- Check for additional charges apply message in case of LTL Item added to registry --%>
			                 		<%-- <c:if test='${ltlFlag ne null && ltlFlag eq true}'>
			                           	<div class="ltlMessage padBottom_10"><bbbl:label key="lbl_addn_charges_apply_ltl_registry"
			                           				language ="${pageContext.request.locale.language}"/></div>
			                     	</c:if> --%>
							<div class="fl noMarBot clearfix">
								<div class="fl button button_active">
								<input type="submit" value="CLOSE" name="CLOSE" class="close-any-dialog" role="button" aria-pressed="false">
								</div>
								<div class="fl bold marTop_5 marLeft_10">
										<dsp:a href="/store/giftregistry/view_registry_owner.jsp"><bbbt:textArea key="txt_pdp_reg_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
											<dsp:param name="registryId" value="${regId}"/>
											<dsp:param name="eventType" value="${registryName}"/>
										</dsp:a>
									<div class="clear"></div>
								</div>
							</div>
								 </div>
			    		</c:otherwise>
		    		</c:choose>
				</c:otherwise>
			</c:choose>
			<script type="text/javascript">
				var omniPageName, pageName = document.location.pathname.split('/')[2];
				if (pageName === ""){
					omniPageName = "Home";
				} else {
					var consultantName ="${consultantName}";
					if(!consultantName.length){
					omniPageName = pageName+'Page';
					}
					else{
						omniPageName =pageName+'-'+consultantName;
					}
				}
			    var BBB = BBB || {};
				BBB.registryInfo = {
					registryPagename: "registryAdd",
				    product:"${omnitureRegistryProd}",
				    var1  : "",
					var23 : '${omnitureRegistryName}',
					var24 : '${omnitureRegistryId}',
					var46 : omniPageName
				};
            </script>
		</c:otherwise>
	</c:choose>

	<dsp:setvalue bean="GiftRegistryFormHandler.clearSessionBeanData" />
</dsp:page>