

<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"
	prefix="dsp"%>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"
	prefix="dspel"%>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="atg.repository.RepositoryItem"%>

<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/Format" />
<dsp:importbean bean="/atg/dynamo/droplet/Range" />
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<dsp:page>
	<%
		String url = "";
	%>
	<%
		String creatorName = "";
	%>
	<%
		String creatorEmail = "";
	%>
	<%
		String deployerName = "";
	%>
	<%
		String deployerEmail = "";
	%>
	 <dsp:droplet name="Format">
	  <dsp:param name="format" value="http://{host}/store"/>
  	  <dsp:param name="host" value="10.193.83.16:7001"/>
   		<dsp:oparam name="output">
		    <dsp:getvalueof id="urls" param="message">
		    <%url=(String) urls;%>
		    </dsp:getvalueof>
  		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof id="processId"
			param="context.processInstance.contextStrings.process">
		<dsp:getvalueof id="projectName"
			param="context.processInstance.contextStrings.projectName">
			<dsp:getvalueof id="workspace"
				param="context.processInstance.contextStrings.workspace">


				<dsp:setvalue
					value='<%="Project " + projectName+	" : Now available in Production environment "%>'
					param="messageSubject" />
				<dsp:setvalue value='<%="no-reply-ecom@bedbath.com"%>' param="messageFrom" />

			

						<%
							String projectId = workspace.toString();
													projectId = projectId.substring(5);
						%>


						<p>						
								<dsp:droplet name="RQLQueryForEach">
									<dsp:param name="repository"
										bean="/atg/epub/PublishingRepository" />
									<dsp:param name="itemDescriptor" value="project" />
									<dsp:param name="queryRQL" value="id=:proj"/>
									<dsp:param name="proj" value="<%=projectId %>"/>
									<dsp:param name="elementName" value="element" />
									<dsp:oparam name="output">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="element.history" />
											<dsp:param name="elementName" value="historyInfo" />
											<dsp:oparam name="outputStart">										 
											<dsp:droplet name="IsEmpty">
									 			<dsp:param name="value" param="historyInfo.profile"/>
									 				<dsp:oparam name="false">
														<dsp:getvalueof id="firstName"	param="historyInfo.profile.firstName">
															<%
															deployerName = (String) firstName;
															%>
														</dsp:getvalueof>
														<dsp:getvalueof id="deployEmail" param="historyInfo.profile.email">
															<%
															deployerEmail = (String) deployEmail;
															%>
														</dsp:getvalueof>
													</dsp:oparam>
													 <dsp:oparam name="true">
     													<dsp:getvalueof id="firstName" bean="Profile.firstName">
															<%
															deployerName=(String) firstName;
															%>
														</dsp:getvalueof>
														<dsp:getvalueof id="deployEmail" bean="Profile.email">
															<%
															deployerEmail=(String) deployEmail;
															%>
														</dsp:getvalueof>
 													  </dsp:oparam>
 												</dsp:droplet>		
										</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof id="firstName"
											param="element.creator.firstName">
											<%
												creatorName = (String) firstName;
											%>
										</dsp:getvalueof>
										<dsp:getvalueof id="email" param="element.creator.email">
											<%
												creatorEmail = (String) email;
											%>
										</dsp:getvalueof>
									</dsp:oparam>
								</dsp:droplet>
								<dsp:droplet name="RQLQueryForEach">
									<dsp:param name="repository"
										bean="/atg/epub/PublishingRepository" />
									<dsp:param name="itemDescriptor" value="process" />
									<dsp:param name="queryRQL" value="id=:processid" />
									<dsp:param name="processid" value="<%=processId%>" />
									<dsp:param name="elementName" value="element" />
									<dsp:oparam name="output">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="element.taskInfos" />
											<dsp:param name="elementName" value="processTaskInfo" />
											<dsp:oparam name="outputStart">
														<dsp:getvalueof id="lastActorName"	param="processTaskInfo.lastActorName">
															<%
															String deployerId = lastActorName.toString();
															deployerId = deployerId.substring(13);
															%>
															<dsp:droplet name="RQLQueryForEach">
																<dsp:param name="repository" bean="/atg/userprofiling/InternalProfileRepository" />
																<dsp:param name="itemDescriptor" value="user" />
																<dsp:param name="queryRQL" value="id=:deployer" />
																<dsp:param name="deployer" value="<%=deployerId%>" />
																<dsp:param name="elementName" value="userElement" />
																<dsp:oparam name="output">
																	<dsp:getvalueof id="firstName" param="userElement.firstName">
																		<%
																		deployerName = (String) firstName;
																		%>
																	
																	</dsp:getvalueof>
																	<dsp:getvalueof id="deployEmail" param="userElement.email">
																		<%
																			deployerEmail = (String) deployEmail;
																		%>
																		
																	</dsp:getvalueof>
																</dsp:oparam>
																</dsp:droplet>
													</dsp:getvalueof>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							
							
																	
																	Project Name: <%=projectName%><br>
																	Creator Name: <%= creatorName%><br>
																	Creator Email: <%=creatorEmail %><br>
																	Deployer Name: <%= deployerName%><br>
																	Deployer Email: <%= deployerEmail%><br>
																	
																

				
			</dsp:getvalueof>
		</dsp:getvalueof>
	</dsp:getvalueof>
</dsp:page>