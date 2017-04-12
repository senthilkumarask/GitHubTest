<dsp:page>
	<json:object>
		<dsp:getvalueof id="selState" param="state"/>
		 <json:array name="colleges">
		<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
			<dsp:param name="state2" value="${selState}"/>
			<dsp:param name="enable" value="true"/>
			<dsp:param name="queryRQL" value="hidden!=:enable and state=:state2"/>
	  		<dsp:param name="repository" value="/com/bbb/selfservice/repository/SchoolRepository"/>
	  		<dsp:param name="itemDescriptor" value="schools"/>
	  		<dsp:param name="elementName" value="schoolItem"/>
	  		<dsp:param name="sortProperties" value="+schoolName"/>
	  		<dsp:oparam name="output">
	  		     <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
			     <dsp:param name="collegeId" param="schoolItem.id"/>
			     <dsp:param name="queryRQL" value="schools=:collegeId"/>
			     <dsp:param name="repository" value="/com/bbb/selfservice/repository/SchoolVerRepository"/>
			     <dsp:param name="itemDescriptor" value="schoolsVer"/>
		  	     <dsp:param name="elementName" value="item"/>
			     <dsp:oparam name="output">
					<dsp:getvalueof id="schoolSeoName" param="item.schoolSeoName"/>
				<json:object>
	  			<dsp:getvalueof id="id" param="schoolItem.id"/> 
                    <json:property name="collegeID" escapeXml="false">${id}</json:property>
                    <json:property name="collegeName" escapeXml="false"><dsp:valueof param="schoolItem.schoolName"/></json:property>
                    <json:property name="collegeSeoName" escapeXml="false">${schoolSeoName}</json:property>
                </json:object>
                </dsp:oparam>
        		</dsp:droplet>
	  		</dsp:oparam>
		</dsp:droplet>
		 </json:array>
	</json:object>
</dsp:page>
