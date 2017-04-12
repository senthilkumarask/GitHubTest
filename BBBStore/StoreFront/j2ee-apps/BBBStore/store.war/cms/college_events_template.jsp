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
								<li class="map"><bbbl:label key="lbl_college_events_RSVP" language="<c:out param='${pageContext.request.locale.language}'/>"/></li>
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
								<dsp:getvalueof var="fromDate" param="element.fromDate" />

								<li class="date">${fromDate}

								<c:if test="${toDate != null && fromDate !=null}">
								  TO
								</c:if>
								<c:if test="${toDate != null}">
								  <dsp:valueof param="element.toDate" />
								</c:if>

								</li>
								<li class="name"><dsp:valueof param="element.name" /></li>
								<li class="time"><dsp:valueof param="element.time" /></li>
								<li class="address"><dsp:valueof param="element.address" /></li>
								<li class="phone"><dsp:valueof param="element.phone" /></li>
								<dsp:getvalueof var = "address" param ="element.address"></dsp:getvalueof>
								<c:set var="mailto"><bbbt:textArea key="txt_college_events_mailto" language="<c:out param='${pageContext.request.locale.language}'/>"/></c:set>
								<c:set var="subject"><bbbt:textArea key="txt_college_events_subject" language="<c:out param='${pageContext.request.locale.language}'/>"/> - <dsp:valueof param="element.name" /> - <dsp:valueof param="element.toDate" /></c:set>
								<li class="map"><div class="button"><a href='mailto:${mailto}?Subject=${subject}'><bbbl:label key="lbl_bridalshow_rsvp_now" language="${pageContext.request.locale.language}" /></a></div></li>
							</ul>
						</li>


						</dsp:oparam>
						<dsp:oparam name="empty">
							<bbbe:error key="err_bridalshow_noshow" language="<c:out param='${pageContext.request.locale.language}'/>"/>
						</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
			<dsp:oparam name="empty">
				<bbbe:error key="err_bridalshow_noshow" language="<c:out param='${pageContext.request.locale.language}'/>"/>
			</dsp:oparam>
		</dsp:droplet>
	</ul>



</dsp:page>

