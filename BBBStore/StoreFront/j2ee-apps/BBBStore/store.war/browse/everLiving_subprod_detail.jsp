<dsp:page>
	<dsp:getvalueof var="colorParam" param="color"/>
	<dsp:getvalueof var="sizeParam" param="size"/>
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="productVO.rollupAttributes" name="array" />
							<dsp:oparam name="outputStart">
							<dsp:getvalueof var="rollupAttributesColor" param="productVO.rollupAttributes.COLOR"/>
							<dsp:getvalueof var="rollupAttributesSize" param="productVO.rollupAttributes.SIZE"/>
							<dsp:getvalueof var="rollupAttributesFinish" param="productVO.rollupAttributes.FINISH"/>
							<c:set var="rollupAttributesDisplay" value="false" />
							<c:if test="${not empty rollupAttributesColor}">
								<c:set var="rollupAttributesDisplay" value="true" />
							</c:if>
							<c:if test="${not empty rollupAttributesSize}">
								<c:set var="rollupAttributesDisplay" value="true" />
							</c:if>
							<c:if test="${not empty rollupAttributesFinish}">
								<c:set var="rollupAttributesDisplay" value="true" />
							</c:if>
								<c:if test="${rollupAttributesDisplay == 'true'}">
									<div class="swatchPickers">
								</c:if>
							</dsp:oparam>
							<dsp:oparam name="output">
								<dsp:getvalueof var="menu" param="key"/>
								<c:choose>
									<c:when test="${menu eq 'SIZE'}">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />	
												<dsp:oparam name="outputStart">
													<div class="sizePicker clearfix">
															<label for="selectProductSize"><bbbl:label key="lbl_sizes_dropdown" language ="${pageContext.request.locale.language}"/></label>
														<div class="swatches">
														<select id="selectProductSize" name="selectProductSize" class="customSelectBox">
															<option value=""><bbbl:label key='lbl_pdp_select_size' language="${pageContext.request.locale.language}" /></option> 
												</dsp:oparam>										
												<dsp:oparam name="output">	
												<dsp:getvalueof var="attribute" param="element.rollupAttribute"/>
												<c:choose>
													<c:when test="${(sizeParam != null) && (sizeParam eq attribute)}">
													<%-- <a href="#" class="size fl selected" data="${attribute}" title="${attribute}"><span>${attribute}</span></a> --%>
													
													<option data-attr="${attribute}" value="${attribute}" selected="selected">${attribute}</option>
													</c:when>
													<c:otherwise> 
													<%-- <a href="#" class="size fl" data="${attribute}" title="${attribute}"><span>${attribute}</span></a>  --%>
													<option data-attr="${attribute}" value="${attribute}">${attribute}</option>
													</c:otherwise>
												</c:choose>
												</dsp:oparam>
												<dsp:oparam name="outputEnd">
														</select>
														</div>
													</div>
												</dsp:oparam>	
											</dsp:droplet>
											<c:choose>
												<c:when test="${(sizeParam != null)}">
												<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="${sizeParam}" />
												</c:when>
												<c:otherwise>
												<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
												</c:otherwise>
											</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
										<c:when test="${menu eq 'COLOR'}">											
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element" name="array" />	
												<dsp:oparam name="outputStart">
													<div class="colorPicker clearfix">
														<label><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
													<div class="width_5 swatches clearfix">
												</dsp:oparam>										
												<dsp:oparam name="output">
													<dsp:getvalueof var="colorImagePath" param="element.swatchImagePath"/>
													<dsp:getvalueof var="attribute" param="element.rollupAttribute"/>
													<dsp:getvalueof var="largeImagePath" param="element.largeImagePath"/>
													<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
													<c:choose>
														<c:when test="${(colorParam != null) && (colorParam eq attribute)}">
															<a href="#" class="fl selected" data-attr="${fn:toLowerCase(attribute)}" title="${attribute}" data-s7ImgsetID="${largeImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">
															<span>
																<c:choose>
																	<c:when test="${(colorImagePath != null) && (colorImagePath ne '')}">
																		<img src="${scene7Path}/${colorImagePath}" height="20" width="20" alt="${attribute}" class="noImageFound"/>
																	</c:when>
																	<c:otherwise>
																		<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}" />
																	</c:otherwise>
																	</c:choose>
															</span></a>
														</c:when>
														<c:otherwise>
															<a href="#" class="fl" data-attr="${fn:toLowerCase(attribute)}" title="${attribute}" data-s7ImgsetID="${largeImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">
															<span>
																<c:choose>
																	<c:when test="${(colorImagePath != null) && (colorImagePath ne '')}">
																		<img src="${scene7Path}/${colorImagePath}" height="20" width="20" alt="${attribute}" class="noImageFound" />
																	</c:when>
																	<c:otherwise>
																		<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}"/>
																	</c:otherwise>
																</c:choose>
															</span></a>
														</c:otherwise>	
													</c:choose>												
												</dsp:oparam>	
												<dsp:oparam name="outputEnd">
                                                    <div class="clear"></div>
														</div>
													</div>
												</dsp:oparam>							
											</dsp:droplet>
											<c:choose>
												<c:when test="${(colorParam != null)}">
													<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorParam}" />
												</c:when>
												<c:otherwise>
													<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="" />
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:if test="${menu eq 'FINISH'}">
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />	
													<dsp:oparam name="outputStart">
														<div class="finishPicker clearfix">
															<label><bbbl:label key="lbl_finish_dropdown" language ="${pageContext.request.locale.language}"/></label>
														<div class="width_5 swatches clearfix">
													</dsp:oparam>										
													<dsp:oparam name="output">
														<dsp:getvalueof var="finishImagePath" param="element.swatchImagePath"/>
														<dsp:getvalueof var="attribute" param="element.rollupAttribute"/>
														<dsp:getvalueof var="largeImagePath" param="element.largeImagePath"/>
														<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
														<c:choose>
															<c:when test="${(colorParam != null) && (colorParam eq attribute)}">
																<a href="#" class="${fn:toLowerCase(attribute)} fl selected" data-attr="${attribute}" title="${attribute}" data-s7ImgsetID="${largeImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">
																<span>
																	<c:choose>
																		<c:when test="${(finishImagePath != null) && (finishImagePath ne '')}">
																			<img src="${scene7Path}/${finishImagePath}" height="20" width="20" alt="${attribute}" class="noImageFound" />
																		</c:when>
																		<c:otherwise>
																			<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}" />
																		</c:otherwise>
																	</c:choose>
																</span></a>
															</c:when>
															<c:otherwise>
																<a href="#" class="${fn:toLowerCase(attribute)} fl" data-attr="${attribute}" title="${attribute}" data-s7ImgsetID="${largeImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">
																<span>
																	<c:choose>
																		<c:when test="${(finishImagePath != null) && (finishImagePath ne '')}">
																			<img src="${scene7Path}/${finishImagePath}" height="20" width="20" alt="${attribute}" class="noImageFound" />
																		</c:when>
																		<c:otherwise>
																			<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}" />
																		</c:otherwise>
																	</c:choose>
																</span></a>
															</c:otherwise>		
														</c:choose>											
													</dsp:oparam>	
													<dsp:oparam name="outputEnd">
                                                        <div class="clear"></div>
															</div>
														</div>
													</dsp:oparam>
												</dsp:droplet>
												<c:choose>
													<c:when test="${(colorParam != null)}">
														<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="${colorParam}" />
													</c:when>
													<c:otherwise>
														<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="" />
													</c:otherwise>
												</c:choose>														
											</c:if>
										</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>	
							</dsp:oparam>
							<dsp:oparam name="outputEnd">
								<c:if test="${rollupAttributesDisplay == 'true'}">
									</div>
								</c:if>
							</dsp:oparam>							
						</dsp:droplet>
</dsp:page>	