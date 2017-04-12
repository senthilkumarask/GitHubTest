<dsp:page>	
   <dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
   <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
   <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
   
   <div class="collegeFlyout omega"> 
	<form name="frmFindCollege" method="post" id="frmFindCollegeFly" class="frmFindCollege frmFindCollegeFly" action="#">
			<fieldset>
			<legend class="hidden"><bbbl:label key="lbl_findyourcollege_find_college" language="${pageContext.request.locale.language}"/></legend>
			<div class="txtOffScreen">
				<input aria-hidden="true"></input>
			</div>								
				<h3 class="findCollegeHead" ><bbbl:label key="lbl_findyourcollege_find_college" language="${pageContext.request.locale.language}"/></h3>
               <div class="findColgBox">
               	<dsp:getvalueof id="selState" param="selState"/>
               	<dsp:getvalueof id="selCollege" param="selCollege"/>
				<label class="txtOffScreen" for="selStateFly"><bbbl:label key="lbl_findyourcollege_select_state" language="${pageContext.request.locale.language}"/></label>
                   <select name="selState" id="selStateFly" class="uniform" aria-required="true" aria-labelledby="lblselStateFly errorselStateFly" >
	                    <option value=""><bbbl:label key="lbl_findyourcollege_select_state" language="${pageContext.request.locale.language}"/></option>
						<dsp:droplet name="StateDroplet">
							<dsp:param name="showMilitaryStates" value="false"/>
							<dsp:oparam name="output">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="location" />
									<dsp:param name="sortProperties" value="+stateName"/>
									<dsp:oparam name="output">
										<dsp:getvalueof param="element.stateName" id="stName"/>
										<dsp:getvalueof param="element.stateCode" id="cd"/>
										<c:choose>
											<c:when test="${selState eq cd}">
												<option value="<c:out value='${cd}'/>" selected="selected"><c:out value='${stName}'/></option>
											</c:when>
											<c:otherwise>
												<option value="<c:out value='${cd}'/>"><c:out value='${stName}'/></option>
											</c:otherwise>
										</c:choose>																						
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
                   </select>
                   <label id="errorselStateFly" for="selStateFly" generated="true" class="error"></label>
					<h2 id="lblselStateFly" class="offScreen"><bbbl:label key="lbl_findyourcollege_find_college" language="${pageContext.request.locale.language}"/></h2>
               </div>
               <div class="findColgBox">
			   <label class="txtOffScreen" for="selCollegeFly"><bbbl:label key="lbl_findyourcollege_college" language="${pageContext.request.locale.language}"/></label>
	                	<dsp:droplet name="IsEmpty">
	                		<dsp:param name="value" param="selCollege"/>
	                		<dsp:oparam name="false">
								<select id="selCollegeFly" name="selCollege" class="uniform" runat="server" aria-required="true" aria-labelledby="lblselCollege errorselCollege" >
	                			<dsp:getvalueof id="selState" param="selState"/>
	                			<dsp:getvalueof id="selCollege" param="selCollege"/>
								<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
									<dsp:param name="state2" value="${selState}"/>
									<dsp:param name="enable" value="true"/>
									<dsp:param name="queryRQL" value="hidden!=:enable and state=:state2"/>
							  		<dsp:param name="repository" value="/com/bbb/selfservice/repository/SchoolRepository"/>
							  		<dsp:param name="itemDescriptor" value="schools"/>
							  		<dsp:param name="elementName" value="item"/>
							  		<dsp:param name="sortProperties" value="+schoolName"/>
							  		<dsp:oparam name="output">
							  				<dsp:getvalueof id="id" param="item.id"/> 
						  					<dsp:getvalueof id="name" param="item.schoolName"/> 
						  					<c:choose>
						  						<c:when test="${selCollege eq id}">
						  							<option value='<c:out value="${id}"/>' selected="selected"><dsp:valueof value="${name}"/></option>
						  							<dsp:getvalueof id="collName" param="item.schoolName"/>	
						  						</c:when>
						  						<c:otherwise>
						  							<option value='<c:out value="${id}"/>'><dsp:valueof value="${name}"/></option>
						  						</c:otherwise>
						  					</c:choose>
							  		</dsp:oparam>
								</dsp:droplet>
								</select>
	                		</dsp:oparam>
	                		<dsp:oparam name="true">
								<select id="selCollegeFly" name="selCollege" class="uniform" aria-required="true" aria-labelledby="lblselCollegeFly errorselCollegeFly" disabled="disabled" >
	                			<option value="" selected="selected"><bbbl:label key="lbl_findyourcollege_college" language="${pageContext.request.locale.language}"/></option>	    
								</select>	
	                		</dsp:oparam>
	                	</dsp:droplet>
	                
               <label id="errorselCollegeFly" for="selCollegeFly" generated="true" class="error"></label>
				<h2 id="lblselCollegeFly" class="offScreen"><bbbl:label key="lbl_findyourcollege_find_college" language="${pageContext.request.locale.language}"/></h2>
               </div>
               <div class="txtCentre"><button class="findCollegeFly button-Med btnSecondary" > <bbbl:label key="lbl_confirm_submit" language="${pageContext.request.locale.language}"/> </button> </div>
               
			</fieldset>
           </form>

</div>
</dsp:page>
