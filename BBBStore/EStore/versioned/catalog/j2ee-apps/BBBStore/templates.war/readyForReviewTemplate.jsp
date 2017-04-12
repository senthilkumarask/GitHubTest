
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
		String taskOwnerName = "";
	%>
	<%
		String creatorEmail = "";
	%>
	
	 <dsp:droplet name="Format">
	  <dsp:param name="format" value="http://{host}:{port,number,#}/atg"/>
	  <dsp:param name="host" bean="Configuration.siteHttpServerName"/>
	  <dsp:param name="port" bean="Configuration.siteHttpServerPort"/>
	  <dsp:oparam name="output">
	    <dsp:getvalueof id="urls" param="message">
	    <%url=(String) urls;%>
	    </dsp:getvalueof>
  </dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof id="process"
		param="context.processInstance.contextStrings.process">
		<dsp:getvalueof id="projectName"
			param="context.processInstance.contextStrings.projectName">
			<dsp:getvalueof id="workspace"
				param="context.processInstance.contextStrings.workspace">


				<dsp:setvalue
					value='<%="Project " + projectName+	" : Ready for review "%>'
					param="messageSubject" />
				<dsp:setvalue value='<%="no-reply-ecom@bedbath.com"%>' param="messageFrom" />
					<%
						StringBuffer str=new StringBuffer();
					%>
						<%
							String projectId = workspace.toString();
													projectId = projectId.substring(5);
						%>


						<p><dsp:getvalueof id="body" param="email.emailBody"
							idtype="String">
							<dsp:getvalueof id="subject" param="email.subject"
								idtype="String">
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
															taskOwnerName = (String) firstName;
															%>
														</dsp:getvalueof>
													</dsp:oparam>
													 <dsp:oparam name="true">
     													<dsp:getvalueof id="firstName" bean="Profile.firstName">
															<%
															taskOwnerName=(String) firstName;
															%>
														</dsp:getvalueof>
 													  </dsp:oparam>
 												</dsp:droplet>		
										</dsp:oparam>
										</dsp:droplet>
										<dsp:droplet name="Range">
											<dsp:param name="array" param="element.assets"/>
											<dsp:param name="howMany" value="10"/>
											<dsp:param name="elementName" value="asset"/>
											<dsp:oparam name="outputStart">
											<%
											str.append("<b><table border='2'><tr><td><b>Asset-Id</b></td><td><b>Asset-Name</b></td><td><b>Asset-Type</b></td><td><b>Asset-Status</b></td>");
											%>
											</dsp:oparam>
											<dsp:oparam name="output">
											<%
											str.append("<tr>");
											%>
											<dsp:getvalueof id="assetId" param="asset.repositoryItem.repositoryId">
											<%
											str.append("<td>"+assetId+"</td>");
											%>
											</dsp:getvalueof>  
											<dsp:getvalueof id="assetType" param="asset.repositoryItem.itemDescriptor.itemDescriptorName" idtype="String"/>
											
											<dsp:getvalueof id="name" param="asset.displayName">
												<%
												str.append("<td>"+name+"</td>");
												%>
											</dsp:getvalueof> 
											<dsp:getvalueof id="assetType" param="asset.repositoryItem.itemDescriptor.itemDescriptorName">
													<%
													str.append("<td>"+assetType+"</td>");
													%>
											</dsp:getvalueof> 
											<dsp:getvalueof id="status" param="asset.status">
												<%
												str.append("<td>"+status+"</td>");
												%>
											</dsp:getvalueof>
											  
											 <%
											 str.append("</tr>");
											
											%>
											 </dsp:oparam>
											<dsp:oparam name="outputEnd">
											<% str.append("</table></b>");
											%>
											</dsp:oparam>
											
											 <dsp:oparam name="empty">
											 <%
											 	str.append(" <table><tr><td>&nbsp;</td><td><b>No assets modified or created in this project.</b></td></tr></table>");
											%>
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
													TaskOwner Name: <%= taskOwnerName%><br>
													To Verify the project <a href="<%= url%>">Click Here</a><br>
													Asset Information: <%=str.toString()%><br>
							</dsp:getvalueof>
						</dsp:getvalueof>
					
			</dsp:getvalueof>
		</dsp:getvalueof>
	</dsp:getvalueof>
</dsp:page>