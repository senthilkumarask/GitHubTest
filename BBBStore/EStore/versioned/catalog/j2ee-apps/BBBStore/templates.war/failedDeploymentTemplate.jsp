
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
<dsp:importbean bean="/atg/dynamo/Configuration" />
<dsp:importbean bean="/com/sprint/web/Configuration" />
<dsp:importbean bean="/atg/dynamo/droplet/Range" />

<dsp:page>
	<%
		String url = "";
	%>
	<%
		String creatorName = "";
	%>
	<%
		String taskOwnerName = "";
	%>
	<%
		String creatorEmail = "";
	%>
	<dsp:getvalueof id="process"
		param="context.processInstance.contextStrings.process">
		<dsp:getvalueof id="projectName"
			param="context.processInstance.contextStrings.projectName">
			<dsp:getvalueof id="workspace"
				param="context.processInstance.contextStrings.workspace">
				<dsp:getvalueof id="failedState"
				param="context.processInstance.contextStrings.failedState">


				<dsp:setvalue
					value='<%="Project " + projectName+	" : Oh no! publishing failure at " +failedState%>'
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
									<dsp:param name="itemDescriptor" value="process" />
									<dsp:param name="queryRQL" value="id=:process" />
									<dsp:param name="process" value="<%=process%>" />
									<dsp:param name="elementName" value="element" />
									<dsp:oparam name="output">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="element.taskInfos" />
											<dsp:param name="elementName" value="processTaskInfo" />
											<dsp:oparam name="outputStart">
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
								
								
								Project Name: <%=projectName%><br>
								Creator Name: <%= creatorName%><br>
								Creator Email: <%=creatorEmail %><br>
					</dsp:getvalueof>			
			</dsp:getvalueof>
		</dsp:getvalueof>
	</dsp:getvalueof>
</dsp:page>