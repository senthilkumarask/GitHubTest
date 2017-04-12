<dsp:page>
<dsp:importbean	bean="/com/bbb/commerce/common/BBBPopulateStatesDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:getvalueof param="country"  var="cc" />
<c:if test="${(cc == 'us') or (cc == 'ca')}">
		<dsp:droplet name="BBBPopulateStatesDroplet">
		<dsp:param name="NoShowUSTerr" value="noShowOnBilling" />
			<dsp:param name="cc" value="${cc}"/>
			<dsp:oparam name="output">
			<json:object>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="states" />
					<dsp:param name="elementName" value="state" />
					<c:set var="code"><dsp:valueof param="state.stateCode"/></c:set>
					<dsp:oparam name="output">
						<json:property name="${code}" escapeXml="false">
							<dsp:valueof param="state.stateName"/>		
						</json:property>
					</dsp:oparam>
				</dsp:droplet>
			</json:object>
			</dsp:oparam>
		</dsp:droplet>
</c:if>
</dsp:page>
