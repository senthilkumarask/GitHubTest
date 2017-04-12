<dsp:page>
	<dsp:getvalueof var="giftPack" param="giftPack"/>
	<div class="grid_3 omega">
		<p><strong><bbbl:label key="lbl_preview_giftoptions" language="<c:out param='${language}'/>"/></strong></p>
		<div class="clearfix marTop_5">
			<p class="fl"><bbbl:label key="lbl_preview_giftpackage" language="<c:out param='${language}'/>"/></p>
			<p class="fr">
				<c:choose>
					<c:when test="${giftPack.giftPackagingFlag}">
						<bbbl:label key="lbl_survey_yes" language="${pageContext.request.locale.language}"/>
					</c:when>
					<c:otherwise>
						<bbbl:label key="lbl_survey_no" language="${pageContext.request.locale.language}"/>
					</c:otherwise>
				</c:choose>
			</p>
		</div>
		<p class="marTop_5"><bbbl:label key="lbl_preview_message" language="<c:out param='${language}'/>"/></p>
		<p class="marTop_5 smallText breakWord">${giftPack.giftMessage}</p>
	</div>
</dsp:page>

