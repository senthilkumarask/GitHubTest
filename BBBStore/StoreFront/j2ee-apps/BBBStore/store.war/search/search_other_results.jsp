<dsp:page>
	<dsp:getvalueof var="queryString" bean="/OriginatingRequest.queryString"/>
	<bbb:pageContainer>
		<jsp:attribute name="section">search</jsp:attribute>
		<jsp:attribute name="pageWrapper">searchList Other Results</jsp:attribute>
		<jsp:attribute name="titleString">${searchTerm}: Other Results</jsp:attribute>
		<jsp:attribute name="follow">true</jsp:attribute>
		<%-- 504D implementation --%>
		<jsp:attribute name="index">true</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute>  
		<jsp:body>
		
		<input type="hidden" name="fromPage" value="searchPage"/>
		<input type="hidden" name="inStoreValue" value="${inStore}"/>
		<input type="hidden" name="queryStringFromSearchPage" value="${queryString}"/>
		
		<c:set var="localStorePLPFlag" scope="request">
			<bbbc:config key="LOCAL_STORE_PLP_FLAG" configName="FlagDrivenFunctions" />
		</c:set>
		
		<c:if test="${localStorePLPFlag}">
		<c:set var="radius_default_us">
			<bbbc:config key="radius_default_us" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_default_baby">
			<bbbc:config key="radius_default_baby" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_default_ca">
			<bbbc:config key="radius_default_ca" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_range_us">
			<bbbc:config key="radius_range_us" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_range_baby">
			<bbbc:config key="radius_range_baby" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_range_ca">
			<bbbc:config key="radius_range_ca" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_range_type">
			<bbbc:config key="radius_range_type" configName="MapQuestStoreType" />
		</c:set>
		<c:choose>
			<c:when test="${currentSiteId eq 'BedBathUS'}">
				<c:set var="radius_default_selected">${radius_default_us}</c:set>
				<c:set var="radius_range">${radius_range_us}</c:set>
			</c:when>
			<c:when test="${currentSiteId eq 'BuyBuyBaby'}">
				<c:set var="radius_default_selected">${radius_default_baby}</c:set>
				<c:set var="radius_range">${radius_range_baby}</c:set>
			</c:when>
			<c:when test="${currentSiteId eq 'BedBathCanada'}">
				<c:set var="radius_default_selected">${radius_default_ca}</c:set>
				<c:set var="radius_range">${radius_range_ca}</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="radius_default_selected">${radius_default_us}</c:set>
				<c:set var="radius_range">${radius_range_us}</c:set>
			</c:otherwise>
		</c:choose>
		<input type="hidden" value="${radius_default_selected}" id="defaultRadius" name="defaultRadius">
		</c:if>
		
			<dsp:getvalueof id="searchTerm" value="${Keyword}"/>
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
			<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
			<dsp:getvalueof var="url" param="url"/>
				     <%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commented below code and moved to search_autophrase_frag.jsp--%>
			
			<%-- <c:set var="prodCount"><dsp:valueof param="browseSearchVO.assetMap.Product.count"/></c:set>
			<c:set var="videoCount"><dsp:valueof param="browseSearchVO.assetMap.Media.count"/></c:set>
			<c:set var="guideCount"><dsp:valueof param="browseSearchVO.assetMap.Guide.count"/></c:set>
			<c:set var="otherCount"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></c:set>
			<c:if test="${empty otherCount }">
			<c:set var="otherCount" value="0"/>
			</c:if>
			<c:if test="${empty videoCount }">
			<c:set var="videoCount" value="0"/>
			</c:if>
			<c:if test="${empty prodCount }">
			<c:set var="prodCount" value="0"/>
			</c:if>
			<c:if test="${empty guideCount }">
			<c:set var="guideCount" value="0"/>
			</c:if> --%>
				     <%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commented below code and moved to search_autophrase_frag.jsp--%>
			
			<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/search_frag.jsp" >
					<dsp:param name="searchTerm" value="${searchTerm}"/>
				</dsp:include>
			</c:if>
			
			<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyLeft"><bbbl:label key="lbl_promo_key_left" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
			
			<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
			<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
            <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
				     <%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commented below code and moved to search_autophrase_frag.jsp--%>
			
			<%-- <dsp:droplet name="ForEach">
	      		<dsp:param name="array" param="browseSearchVO.autoSuggest" />
	      		<dsp:oparam name="output">
	      			<dsp:getvalueof var="spellCorrection" param="element.spellCorrection"/>
	      			<dsp:droplet name="IsNull">
	    				<dsp:param name="value" value="${spellCorrection}" />
	    				<dsp:oparam name="false">
	    					<dsp:getvalueof var="searchTerm" value="${spellCorrection}"/>
	    					<dsp:getvalueof var="autoCorrected" value="true"/>
	    				</dsp:oparam>
	    			</dsp:droplet>
	    			<dsp:getvalueof var="didYouMeanTerm" param="element.dymSuggestion"/>
	    			<dsp:droplet name="IsNull">
	    				<dsp:param name="value" value="${didYouMeanTerm}" />
	    				<dsp:oparam name="false">
	    					<dsp:getvalueof var="dymSuggestion" value="true"/>
	    				</dsp:oparam>
	    			</dsp:droplet>
					<dsp:getvalueof var="autoPhrase" value="false"/>
					<dsp:getvalueof var="isAutoPhrase" param="element.autoPhrase"/>
					<dsp:getvalueof var="autoPhrase" value="${isAutoPhrase}"/>
	      		</dsp:oparam>
	      	</dsp:droplet> --%>
           		     <%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commented below code and moved to search_autophrase_frag.jsp--%>
           	
			<div id="content" class="subCategory container_12 clearfix" role="main">
			<%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue  --%>
			<dsp:include page="search_autophrase_frag.jsp"/>
			<%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue --%>
				     <%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commented below code and moved to search_autophrase_frag.jsp--%>
			
				<%-- div class="breadcrumbs grid_12">
					<a href="${contextPath}"><bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" /></a>
					<span class="rightCarrot">&gt;</span>
					<span><bbbl:label key="lbl_header_search_results_page_1" language="${pageContext.request.locale.language}" /> <span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></span> 
				</div>
				<div class="pageTitle grid_12">
					<h1 class="mainTitle"><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" /></h1>
					<h2 class="subTitle marLeft_5"><bbbl:label key="lbl_header_search_results_page_3" language="${pageContext.request.locale.language}" />&nbsp;${otherCount+videoCount+prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;<span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></h2>
					<h2 class="subTitle block noMar">
						<c:if test="${autoPhrase eq 'false'}"> 
	                    <c:if test="${autoCorrected eq 'true'}">
	                     <c:set var="keyword"><dsp:valueof param="Keyword"/></c:set>
	                    	<bbbl:label key="lbl_spell_correction_result" language="${pageContext.request.locale.language}" />
	                    	<span class="bold">&ldquo;<c:out value="${keyword}" escapeXml="false"/>&rdquo;</span>
	                    	<bbbl:label key="lbl_spell_correction_2" language="${pageContext.request.locale.language}" />&ldquo; 
	                    	<span class="bold">&rdquo;<dsp:valueof value="${spellCorrection}"/>&ldquo;</span>&#46;
	                    	
	                    </c:if>
	                    <c:if test="${dymSuggestion eq 'true'}">
	                    	<dsp:droplet name="ForEach">
				         		<dsp:param name="array" param="browseSearchVO.autoSuggest" />
				         		<dsp:oparam name="outputStart"><bbbl:label key="lbl_did_you_mean_1" language="${pageContext.request.locale.language}" />
				         		</dsp:oparam>
				           		<dsp:oparam name="output">
				           			<dsp:getvalueof var="totalSuggestions" param="size"/>
				           			<dsp:getvalueof var="currentCount" param="count"/>
				         			R2.2 Story - SEO Friendly URL changes
				         			<a href="${urlPrefixForSuggestion}<dsp:valueof param="element.dymSuggestion"/>${searchQueryParamsForSuggestion}" title="<dsp:valueof param="element.dymSuggestion"/>" class="bold"><dsp:valueof param="element.dymSuggestion"/></a>
				         			<c:if test="${totalSuggestions ne currentCount}"><bbbl:label key="lbl_did_you_mean_2" language="${pageContext.request.locale.language}" /></c:if>
				           		</dsp:oparam>
				           		<dsp:oparam name="outputEnd"><bbbl:label key="lbl_did_you_mean_3" language="${pageContext.request.locale.language}" />
				         		</dsp:oparam>
				           	</dsp:droplet>
	                    </c:if>
						</c:if>
	                </h2>
				</div> --%>
					     <%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commented below code and moved to search_autophrase_frag.jsp--%>
				
	<dsp:droplet name="ForEach">
				<dsp:param name="array" param="browseSearchVO.promoMap" />
                  	<dsp:oparam name="output">
				  		<dsp:getvalueof var="elementList" param="element"/>
                         	<dsp:getvalueof var="key1" param="key"/>
                         	  <c:if test="${key1 eq promoKeyTop &&  not empty elementList}">
	                           		<div class="grid_12 clearfix promo-12col">
		                           		<dsp:droplet name="ForEach">
		                                	<dsp:param name="array" value="${elementList}" />
		                                    <dsp:oparam name="output">
				                            	<dsp:getvalueof var="imageHREF" param="element.imageHref"/>
				                            	<c:if test="${not empty imageHREF}">
								                    <dsp:droplet name="AddContextPathDroplet">
				                            			<dsp:param name="InputLink" value="${imageHREF}" />
				                            			<dsp:oparam name="output">
				                            				<dsp:getvalueof var="imageHREF" param="OutputLink"/>
				                            			</dsp:oparam>
				                            		</dsp:droplet>
				                            	</c:if>
							                    <dsp:getvalueof var="imageURL" param="element.imageSrc"/>
							                    <c:choose>
							                    	<c:when test="${not empty imageHREF}">
			                                          	<a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>">
		                                          			<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
		                                          		</a>
							                    	</c:when>
							                    	<c:otherwise>
			                                          	<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
							                    	</c:otherwise>
							                    </c:choose>
		                                  	</dsp:oparam>
		                            	</dsp:droplet>
		                            </div>
	                         	</c:if>
								<c:if test="${key1 eq promoKeyCenter &&  not empty elementList}">
						  			<div class="grid_12 clearfix promo-12col1 relatedSearch">                                                   	
	                      	     		<dsp:droplet name="ForEach">
	                                 		<dsp:param name="array" value="${elementList}" />
												<dsp:oparam name="output">	
													<dsp:getvalueof  var="SeperatedValue" param="element.RelatedSeperated" />
													<c:if test="${not empty SeperatedValue}">
														<span class="bold marRight_10">${promoKeyRelated} :</span>
														    <dsp:droplet name="ForEach">
																<dsp:param name="array" value="${SeperatedValue}" />
																	<c:set var="currentCount">
																		<dsp:valueof param="count"/></c:set>
																		<c:set var="sanitizedKeyword"><dsp:valueof param="key"/></c:set>
																<dsp:oparam name="output">
																				<dsp:getvalueof  var="keyWords" param="element" />
																					<c:set var="urlPrefixForSuggestion1" value="${contextPath}/s/" scope="request" />
																						<a href="${urlPrefixForSuggestion1}${sanitizedKeyword}?relatedsearch=true${vendorParam}" title="">${keyWords}<c:if test="${fn:length(SeperatedValue) ne currentCount}">,</c:if></a>
																							
																</dsp:oparam>
															</dsp:droplet>
													</c:if>
											    </dsp:oparam>
	                                	</dsp:droplet>
                                	</div>
                            	</c:if>
                   </dsp:oparam>
 	</dsp:droplet>
				<div class="grid_2">
					<dsp:droplet name="ForEach">
                     	<dsp:param name="array" param="browseSearchVO.promoMap" />
                        <dsp:oparam name="output">
                        <dsp:getvalueof var="elementList" param="element"/>
                            <dsp:getvalueof var="key2" param="key"/>
                            <c:if test="${key2 eq promoKeyLeft}">
                            	<dsp:droplet name="ForEach">
                                   <dsp:param name="array" value="${elementList}" />
                                    <dsp:oparam name="output">
                                    	<dsp:getvalueof var="imageHREF" param="element.imageHref"/>
                                    	<c:if test="${not empty imageHREF}">
								                    <dsp:droplet name="AddContextPathDroplet">
				                            			<dsp:param name="InputLink" value="${imageHREF}" />
				                            			<dsp:oparam name="output">
				                            				<dsp:getvalueof var="imageHREF" param="OutputLink"/>
				                            			</dsp:oparam>
				                            		</dsp:droplet>
				                          </c:if>
                            			<dsp:getvalueof var="imageURL" param="element.imageSrc"/>
										<div class="promo-2col">
										<c:choose>
											<c:when test="${not empty imageHREF}">
		                                		<a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>"><img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" /></a>
		                               	 	</c:when>
		                                <c:otherwise>
		                                	<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
		                                </c:otherwise>	
                                         </c:choose>
                                          	</div>
                                   	</dsp:oparam>
                             	</dsp:droplet>
                          	</c:if>
                    	</dsp:oparam>
                    </dsp:droplet>
				</div>
				<div class="grid_10 omega">
					<div id="pagTop" class="grid_10 alpha omega marBottom_20">
						<%-- <c:import url="/_includes/modules/top_bar_otherResults.jsp" /> --%>
				
						<dsp:include page="/_includes/modules/top_bar_otherResults.jsp" >
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />	
						</dsp:include>
					</div>
					<div class="clear"></div>
					<dsp:valueof param="elementList"/>
					<dsp:droplet name="ForEach">
                     	<dsp:param name="array" param="browseSearchVO.bbbProducts.otherResults" />
                        <dsp:oparam name="output">					
					<dsp:droplet name="ForEach">
                     	<dsp:param name="array" param="element" />
                     	<dsp:getvalueof var="otherPageType" param="element.otherPageType"/>
                        <dsp:oparam name="output">
                        			<c:choose>
										<c:when test="${otherPageType eq '202'}">
		                                	<a href="<dsp:valueof param="element.othResLink"/>" class="popup marBottom_20 block" title="<dsp:valueof param="key"/>"><h3 class="inline noMar noPad"><dsp:valueof param="key" valueishtml="true"/></h3></a>
					<p class="noMar"><a href="<dsp:valueof param="element.othResLink"/>" title="<dsp:valueof param="element.othResTitle"/>"><dsp:valueof param="element.othResTitle" valueishtml="true"/></a></p>
		                               	</c:when>
		                                <c:otherwise>
		                                	<a href="<dsp:valueof param="element.othResLink"/>" title="<dsp:valueof param="key"/>" class="marBottom_20 block"><h3 class="inline noMar noPad"><dsp:valueof param="key" valueishtml="true"/></h3></a>
		                                	<p class="noMar"><a href="<dsp:valueof param="element.othResLink"/>" title="<dsp:valueof param="element.othResTitle"/>"><dsp:valueof param="element.othResTitle" valueishtml="true"/></a></p>
		                                </c:otherwise>	
									</c:choose>
					</dsp:oparam>
					</dsp:droplet>
					</dsp:oparam>
					</dsp:droplet>
				</div>
			</div>
		</jsp:body>
	</bbb:pageContainer>
</dsp:page>