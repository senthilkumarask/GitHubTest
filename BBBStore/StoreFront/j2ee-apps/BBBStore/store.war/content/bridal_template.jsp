<dsp:page>
	<dsp:importbean bean="/com/bbb/cms/droplet/BridalShowDetailDroplet" />
	<dsp:importbean bean="/com/bbb/cms/droplet/BridalShowStateDroplet" />
 	


	<ul>
	
						<li>
							<ul class="tableHead">
								<li class="date"><bbbl:label key="lbl_bridalshow_date" language="<c:out param='${pageContext.request.locale.language}'/>"/></li>
								<li class="name"><bbbl:label key="lbl_bridalshow_showname" language="<c:out param='${pageContext.request.locale.language}'/>"/></li>
								<li class="time"><bbbl:label key="lbl_bridalshow_time" language="<c:out param='${pageContext.request.locale.language}'/>"/></li>
								<li class="address"><bbbl:label key="lbl_bridalshow_address" language="<c:out param='${pageContext.request.locale.language}'/>"/></li>
								<li class="phone"><bbbl:label key="lbl_bridalshow_phone" language="<c:out param='${pageContext.request.locale.language}'/>"/></li>
								<li class="map"><bbbl:label key="lbl_bridalshow_map" language="<c:out param='${pageContext.request.locale.language}'/>"/></li>
							</ul>
						</li>
		
		
		<dsp:droplet name="BridalShowDetailDroplet">
			<dsp:param name="stateId" param="stateId" />
			
			<dsp:oparam name="output">

			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" param="stateItem"/>
					
						<dsp:oparam name="output">
						<li>
						
						<dsp:getvalueof var="countAttribute" param="count" />
														<c:choose>
															<c:when test="${countAttribute % 2==0}">
																<ul class="tableContents">
															</c:when>
															<c:otherwise>
																<ul class="tableContents row_odd">
															</c:otherwise>
														</c:choose>
								<dsp:getvalueof var="toDate" param="element.toDate" />
								<li class="date"><dsp:valueof param="element.fromDate" /> 
								<c:choose>
								<c:when test="${toDate != null}">	
								
								- <dsp:valueof param="element.toDate" />
								</c:when>
								</c:choose>
								</li>
								<li class="name"><dsp:valueof param="element.name" /></li>
								<li class="time"><dsp:valueof param="element.time" /></li>
								<li class="address"><dsp:valueof param="element.address" /></li>
								<li class="phone"><dsp:valueof param="element.phone" /></li>
								<li class="map"><a href='<dsp:valueof param="element.direction" />'><bbbl:label key="lbl_bridalshow_view_directions" language="${pageContext.request.locale.language}" /></a></li>
							</ul>
						</li>
						
						
						</dsp:oparam> 
						<dsp:oparam name="empty">
							123<bbbe:error key="err_bridalshow_noshow" language="<c:out param='${pageContext.request.locale.language}'/>"/>
						</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
			<dsp:oparam name="empty">
				456<bbbe:error key="err_bridalshow_noshow" language="<c:out param='${pageContext.request.locale.language}'/>"/>
			</dsp:oparam>
		</dsp:droplet>
	</ul>



</dsp:page>

