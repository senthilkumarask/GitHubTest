<%@page import="atg.deployment.server.Target"%>
<%@page import="atg.deployment.server.DeploymentServer"%>
<%@page import="atg.repository.RepositoryException"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.bbb.importprocess.manager.BBBPIMProdCatalogImportManager"%>
<%@page import="atg.repository.rql.RqlStatement"%>
<%@page import="atg.repository.RepositoryView"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="atg.repository.RepositoryItem"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.bbb.importprocess.tools.BBBPIMFeedTools"%>
<%@page import="java.util.List"%>
<%@page import="com.bbb.importprocess.schedular.BBBCatalogFeedJob"%>
<%@page import="com.bbb.importprocess.schedular.BBBProjectDeploymentScheduler"%>
<%@page import="java.util.Date"%>
<%@page import="atg.service.scheduler.ScheduledJob"%>
<%@page import="atg.servlet.ServletUtil"%>
<%@page import="atg.service.scheduler.SingletonSchedulableService"%>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"
	prefix="dsp"%>
	
<dsp:page>
<html>
	<head>
		<style>
			*{font-family:Arial;}
			.wrap{width: 80%; margin: auto;}
			table{border-collapse:collapse; border:2px solid black; width:100%}
			tr, th, td { padding: 5px 10px; text-align: center;}
			th { background-color:#39B7CD; color:#FFFFFF;}
			h1 {text-align: center;padding-top:20px;}
			.success {background-color: #4AC948; color: #FFFFFF;}
			.error {background-color: #FF7256; color: #FFFFFF;}
		</style>
		<script type="text/javascript">
			function validateNumProjectsField() {
				var numProjectsInput = document.getElementsByName('numProjects')[0].value;
				if(isNaN(numProjectsInput)) {
					alert("Oops!! you entered a character. \nYou need to enter a number greater than 0.");
					return false;
				} else if(numProjectsInput <= 0) {
					alert("Oops!! you entered a number which is 0 or less. \nYou need to enter a number greater than 0.");
					return false;
				} else {
					return true;
				}
			}
		</script>
		<title>PIM Schedulers Dashboard</title>
	</head>
	<body>
		<h1>PIM Schedulers Dashboard</h1>
		<hr/>
		<div class="wrap">
			<h2>Scheduler Details:</h2>
			<%
				String schedulableServices[] = {"/com/bbb/importprocess/schedular/BBBProductionPIMImportScheduler", "/com/bbb/importprocess/schedular/BBBProjectDeploymentScheduler", "/com/bbb/importprocess/schedular/BBBEmergencySchedular"};
				
				out.println("<table border=\"1\">");
				out.println("<tr>");
				out.println("<th>Scheduler Name</th>");
				out.println("<th>Last Run</th>");
				out.println("<th>Next Run</th>");
				out.println("<th>Schedule</th>");
				out.println("<th>Scheduler State</th>");
				out.println("</tr>");
				for(int i = 0; i < schedulableServices.length; i++) {
					SingletonSchedulableService schedulableService = (SingletonSchedulableService)(ServletUtil.getCurrentRequest()).resolveName(schedulableServices[i]);
					if(schedulableService != null) {
						int jobId = schedulableService.getJobId();
						ScheduledJob scheduledJob = schedulableService.getScheduler().findScheduledJob(jobId);
						out.println("<tr>");
						if(scheduledJob != null) {
							out.println("<td>"+ scheduledJob.getJobName() +"</td>");
							out.println("<td>"+ new Date(scheduledJob.getLastJobTime()).toString() +"</td>");
							out.println("<td>"+ new Date(scheduledJob.getNextJobTime()).toString() +"</td>");
							out.println("<td>"+ scheduledJob.getSchedule() +"</td>");
							
							if(new Date().compareTo(new Date(scheduledJob.getNextJobTime())) < 0) {
								out.println("<td class=\"success\">Running</td>");
							}
							else {
								out.println("<td class=\"error\">Stuck</td>");
							}
						} else {
							out.println("<td colspan=\"5\" class=\"error\">NO schedule available for " + schedulableServices[i] + "</td>");
						}
						out.println("</tr>");
					}
					else {
						out.println("<tr><td colspan=\"5\" class=\"error\">Cannot find " + schedulableServices[i] + "</td></tr>");
					}
				}
				out.println("</table>");
			%>
			<br/>

			<h2>BCC Agent Status:</h2>
			<%
				DeploymentServer deploymentServer = (DeploymentServer)(ServletUtil.getCurrentRequest().resolveName("/atg/epub/DeploymentServer"));
				Target[] availableTargets = deploymentServer.getTargets();
				out.println("<table border=\"1\">");
				out.println("<tr>");
				out.println("<th>Agent Name</th>");
				out.println("<th>Site Status</th>");
				out.println("<th>Deployment Status</th>");
				out.println("<th>Agent Status</th>");
				out.println("</tr>");
				if(availableTargets != null) {
					if(availableTargets.length == 0) {
						out.println("<tr>");
						out.println("<td colspan=\"2\">There are no sites defined or there was an error initializing the deployment topology.<br/>Use the Configuration section to create site definitions.");
						out.println("</td>");
						out.println("</tr>");
					}
					for(int i = 0; i < availableTargets.length; i++) {
						out.println("<tr>");
						out.println("<td>");
						out.println(availableTargets[i].getName());
						out.println("</td>");
						if(!availableTargets[i].isAccessible()) {
							out.println("<td class=\"error\">Inaccessible</td>");
						} else if(availableTargets[i].isHalted()) {
							out.println("<td class=\"error\">Deployment Halted</td>");
						} else {
							out.println("<td class=\"success\">Healthy</td>");
						}
						if(availableTargets[i].getCurrentDeployment() != null) {
							if(availableTargets[i].getCurrentDeployment().getStatus().isStateError()) {
								out.println("<td class=\"error\">" + availableTargets[i].getCurrentDeployment().getStatus().getUserStateString() + "</td>");
							} else if(!availableTargets[i].getCurrentDeployment().getStatus().isStateError()) {
								out.println("<td class=\"success\">" + availableTargets[i].getCurrentDeployment().getStatus().getUserStateString() + "</td>");
							}
						} else {
							out.println("<td>Not Deploying</td>");
						}
						if(availableTargets[i].isAccessible()) {
							if(deploymentServer.getAllowMissingNonEssentialAgents()) {
								out.println("<td>All essential agents accessible</td>");
							} else {
								out.println("<td class=\"success\">All agents accessible</td>");
							}
						} else {
							for(int j = 0; j < availableTargets[i].getAgents().length; j++) {
								if((availableTargets[i].getAgents())[j].getStatus().isStateError()) {
									out.println("<td class=\"error\">" + (availableTargets[i].getAgents())[j].getName() + " in error state</td>");
								}
							}
						}
						out.println("</tr>");
					}
				}
				out.println("</table>");
			%>
			<br/>

			<h2>Current Import Project Details:</h2>
			<%
				BBBCatalogFeedJob importScheduler = (BBBCatalogFeedJob)(ServletUtil.getCurrentRequest()).resolveName(schedulableServices[0]);
				BBBProjectDeploymentScheduler deploymentScheduler = (BBBProjectDeploymentScheduler)(ServletUtil.getCurrentRequest()).resolveName(schedulableServices[1]);
				BBBPIMFeedTools pimFeedTools = importScheduler.getImportManager().getPimFeedTools();
				Connection connection = null;
				PreparedStatement preparedStatement = null;
				PreparedStatement getTimingDetailsNodeSkuStmt = null;
				PreparedStatement getMaxRunsStmt = null;
				ResultSet resMaxRuns = null;
				ResultSet resTimingDetailsNodeSku = null;
				PreparedStatement getTimingDetailsItemsStmt = null;
				ResultSet resTimingDetailsItems = null;
				ResultSet resultSet = null;
				try {
					connection = pimFeedTools.openConnection();
					RepositoryView view = deploymentScheduler.getProjectDeploymentManager().getPublishingRepository().getView("project");
					RqlStatement rql = RqlStatement.parseRqlStatement(((BBBPIMProdCatalogImportManager)(importScheduler.getImportManager())).getProjectRQL());
					Object params[] = new Object[1];
					RepositoryItem[] projectsToDeploy = rql.executeQuery(view, params);
					if(projectsToDeploy != null) {
						out.println("<table border=\"1\">");
						out.println("<tr>");
						out.println("<th>Project Name</th>");
						out.println("<th>Feeds Imported With Status</th>");
						out.println("</tr>");
						for(int i = 0; i < projectsToDeploy.length; i++) {
							RepositoryItem project = projectsToDeploy[i];
							String projectId = project.getRepositoryId();
							String projectName = project.getItemDisplayName();
							List<String> importedFeedList = pimFeedTools.getProdImportedFeedList(connection, projectId);
							out.println("<tr>");
							out.println("<td>" + projectName + "</td>");
							out.println("<td><table border=\"1\">");
							for(int j = 0; j < importedFeedList.size(); j++) {
								out.println("<tr>");
								out.println("<td>" + importedFeedList.get(j) + "</td>");
								out.println("<td>" + pimFeedTools.getPIMFeedStatus(importedFeedList.get(j), connection) + "</td>");
								out.println("</tr>");
							}
							out.println("</table>");
							out.println("</td></tr>");
						}
						out.println("</table>");
					}
					else {
						out.println("<table><tr><th>No Active Projects</th></tr></table>");
					}
			%>
			<br/><br/>
			<table style="border:none;">
			<tr>
				<td style="text-align:left; padding:0px"><h2>Feeds In Exception</h2></td>
				<td style="text-align:left; padding:0px"><h2>Feeds In Progress</h2></td>
			</tr>
			<tr>
			<td style="text-align:left; vertical-align:top; padding: 0px;padding-right:10px;">
			<%
					final String SELECT_FEEDS_IN_EXCEPTION = "SELECT FEED_ID,FEED_STATUS,FEED_TYPE FROM ECP_FEED_MONITORING WHERE FEED_STATUS LIKE '%ERROR%' OR FEED_STATUS LIKE '%SYSTEM%'";
					preparedStatement = connection.prepareStatement(SELECT_FEEDS_IN_EXCEPTION, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					resultSet = preparedStatement.executeQuery();
					out.println("<table border=\"1\">");
					out.println("<tr>");
					out.println("<th style=\"background-color:#FFFFFF; color:#000000\">Feed ID</th>");
					out.println("<th style=\"background-color:#FFFFFF; color:#000000\">Feed Type</th>");
					out.println("<th style=\"background-color:#FFFFFF; color:#000000\">Status</th>");
					out.println("</tr>");
					while(resultSet != null && resultSet.next()) {
						out.println("<tr>");
						out.println("<td class=\"error\">" + resultSet.getInt(1) + "</td>");
						out.println("<td class=\"error\">" + resultSet.getString(3) + "</td>");
						out.println("<td class=\"error\">" + resultSet.getString(2) + "</td>");
						out.println("</tr>");
					}
					out.println("</table>");
			%>
			</td>
			<td style="text-align:left; vertical-align:top; padding: 0px">
			<%
					final String SELECT_FEEDS_IN_PROGRESS = "SELECT FEED_ID,FEED_STATUS,FEED_TYPE FROM ECP_FEED_MONITORING WHERE FEED_STATUS LIKE '%_IN_PROGRESS'";
					preparedStatement = connection.prepareStatement(SELECT_FEEDS_IN_PROGRESS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					resultSet = preparedStatement.executeQuery();
					out.println("<table border=\"1\">");
					out.println("<tr>");
					out.println("<th>Feed ID</th>");
					out.println("<th>Feed Type</th>");
					out.println("<th>Status</th>");
					out.println("</tr>");
					while(resultSet != null && resultSet.next()) {
						out.println("<tr>");
						out.println("<td>" + resultSet.getInt(1) + "</td>");
						out.println("<td>" + resultSet.getString(3) + "</td>");
						out.println("<td>" + resultSet.getString(2) + "</td>");
						out.println("</tr>");
					}
					out.println("</table>");
			%>
			</td>
			</tr>
			</table>
			<br/>
			
			<h2>Previous Project Details</h2>
			<form>
				<label for="numProjects">How many previous projects would you like to see?</label>
				<input type="text" name="numProjects"/>
				<input type="submit" value="Submit" onClick="return validateNumProjectsField()"/>
			</form>

			<%
					int numProjects = 2;
					if(request.getParameter("numProjects") == null) {
						//will only occur at first load
						//do nothing
					} else if(request.getParameter("numProjects").isEmpty()||!request.getParameter("numProjects").matches("^[0-9]*$")) {
						out.println("<h2 class=\"error\" style=\"padding:20px;text-align:center;\">Oops!! you did not enter anything or entered a character. <br/>You need to enter a number greater than 0.</h2>");
					} else if(numProjects <= 0) {
						out.println("<h2 class=\"error\" style=\"padding:20px;text-align:center;\">Oops!! you entered a value 0 or less. <br/>You need to enter a number greater than 0.</h2>");
					} else {
						numProjects = Integer.parseInt(request.getParameter("numProjects"));
					}
					final String SELECT_LAST_N_PROJECTS = "select * from (select distinct(project_id),project_name,deployed_time from ecp_prj_imported_feeds where deployed_time is not null order by deployed_time desc) where rownum <= " + numProjects;
					final String SELECT_FEED_TIME_INFO = "select no_of_rows,start_time,end_time from ecp_feed_time_details where is_prod_import=1 and table_processed=? and feed_id=? order by run_id asc";
					final String SELECT_NUM_ROWS_PER_FEED = "select feed_id, count(*) from ecp_feed_time_details where table_processed='ECP_NODE_SKU' and is_prod_import=1 and feed_id=? group by feed_id";
					final String SELECT_MAX_RUNS = "select sum(numrows) from (select count(*) as numrows from ecp_feed_time_details where table_processed='ECP_NODE_SKU' and is_prod_import=1 and feed_id in (select feed_id from ecp_prj_imported_feeds where project_id in (select project_id from (select distinct(project_id),project_name,deployed_time from ecp_prj_imported_feeds where deployed_time is not null order by deployed_time desc) where project_id=?)) group by feed_id)";
					preparedStatement = connection.prepareStatement(SELECT_LAST_N_PROJECTS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					resultSet = preparedStatement.executeQuery();
					out.println("<h3>Last " + numProjects + " project details</h3>");
					out.println("<table border=\"1\">");
					out.println("<tr>");
					out.println("<th rowspan=\"3\">Project Name</th>");
					out.println("<th rowspan=\"3\">Feed ID</th>");
					out.println("<th colspan=\"5\">Details</th>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<th colspan=\"1\" rowspan=\"2\">Run #</th>");
					out.println("<th colspan=\"2\">ECP_NODE_SKU</th>");
					out.println("<th colspan=\"2\">ECP_ITEMS</th>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<th>No of Rows</th>");
					out.println("<th>Time (in mins)</th>");
					out.println("<th>No of Rows</th>");
					out.println("<th>Time (in mins)</th>");
					out.println("</tr>");
					while(resultSet != null && resultSet.next()) {
						List<String> importedFeedList = pimFeedTools.getProdImportedFeedList(connection, resultSet.getString(1));
						getMaxRunsStmt = connection.prepareStatement(SELECT_MAX_RUNS);
						getMaxRunsStmt.setString(1, resultSet.getString(1));
						resMaxRuns = getMaxRunsStmt.executeQuery();
						int numRuns = 0;
						if(resMaxRuns != null && resMaxRuns.next()) {
							numRuns = resMaxRuns.getInt(1);
						}
						if(numRuns == 0) {
							numRuns = importedFeedList.size();
						}
						out.println("<tr>");
						out.println("<td rowspan=\""+ numRuns +"\">" + resultSet.getString(2) + "</td>");
						for(int i = 0; i < importedFeedList.size(); i++) {
							getTimingDetailsNodeSkuStmt = connection.prepareStatement(SELECT_FEED_TIME_INFO, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
							getTimingDetailsNodeSkuStmt.setString(1, "ECP_NODE_SKU");
							getTimingDetailsNodeSkuStmt.setInt(2,Integer.parseInt(importedFeedList.get(i)));
							resTimingDetailsNodeSku = getTimingDetailsNodeSkuStmt.executeQuery();
							getTimingDetailsItemsStmt = connection.prepareStatement(SELECT_FEED_TIME_INFO, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
							getTimingDetailsItemsStmt.setString(1, "ECP_ITEMS");
							getTimingDetailsItemsStmt.setInt(2,Integer.parseInt(importedFeedList.get(i)));
							resTimingDetailsItems = getTimingDetailsItemsStmt.executeQuery();
							int count = 0;
							if(resTimingDetailsNodeSku != null && resTimingDetailsNodeSku.next() && resTimingDetailsItems != null) {
								resTimingDetailsNodeSku.last();
								count = resTimingDetailsNodeSku.getRow();
								resTimingDetailsNodeSku.beforeFirst();
							
								out.println("<td rowspan=\"" + count + "\">" + importedFeedList.get(i) + "</td>");
								for(int j = 1; j <= count && resTimingDetailsNodeSku.next() && resTimingDetailsItems.next(); j++) {
									out.println("<td>" + j + "</td>");
									out.println("<td>" + resTimingDetailsNodeSku.getInt(1) + "</td>");
									long timeReq = resTimingDetailsNodeSku.getTimestamp(3).getTime() - resTimingDetailsNodeSku.getTimestamp(2).getTime();
									double timeReqMin = (timeReq / 60000.00);
									DecimalFormat df = new DecimalFormat("#.00");
									out.println("<td>" + df.format(timeReqMin) + "</td>");
									
									out.println("<td>" + resTimingDetailsItems.getInt(1) + "</td>");
									timeReq = resTimingDetailsItems.getTimestamp(3).getTime() - resTimingDetailsItems.getTimestamp(2).getTime();
									timeReqMin = (timeReq / 60000.00);
									out.println("<td>" + df.format(timeReqMin) + "</td>");
									out.println("</tr>");
								}
							} else if(count == 0) {
								out.println("<td>" + importedFeedList.get(i) + "</td>");
							}
							
							out.println("</tr>");
						}
					}
					out.println("</table>");
				} catch (SQLException sqlex) {
					out.println("<h2 class=\"error\" style=\"padding:20px;text-align:center;\">Oops something seems wrong with the database :(<br/>Please try again</h2>");
				} catch (RepositoryException rex) {
					out.println("<h2 class=\"error\" style=\"padding:20px;text-align:center;\">Oops something seems went wrong while talking to repository :(<br/>Please try again</h2>");
				} catch (NumberFormatException rex) {
					out.println("<h2 class=\"error\" style=\"padding:20px;text-align:center;\">Oops!! you entered a character. <br/>You need to enter a number greater than 0.</h2>");
				} catch (Exception ex) {
					out.println("<h2 class=\"error\" style=\"padding:20px;text-align:center;\">Somethings wrong :(<br/>Please try again</h2>");
				} finally {
					if(preparedStatement != null && !preparedStatement.isClosed()) {
						preparedStatement.close();
					}
					if(getTimingDetailsNodeSkuStmt != null && !getTimingDetailsNodeSkuStmt.isClosed()) {
						getTimingDetailsNodeSkuStmt.close();
					}
					if(getMaxRunsStmt != null && !getMaxRunsStmt.isClosed()) {
						getMaxRunsStmt.close();
					}
					if(resMaxRuns != null && !resMaxRuns.isClosed()) {
						resMaxRuns.close();
					}
					if(resTimingDetailsNodeSku != null && !resTimingDetailsNodeSku.isClosed()) {
						resTimingDetailsNodeSku.close();
					}
					if(getTimingDetailsItemsStmt != null && !getTimingDetailsItemsStmt.isClosed()) {
						getTimingDetailsItemsStmt.close();
					}
					if(resTimingDetailsItems != null && !resTimingDetailsItems.isClosed()) {
						resTimingDetailsItems.close();
					}
					if(resultSet != null && !resultSet.isClosed()) {
						resultSet.close();
					}
					if(connection != null && !connection.isClosed()) {
						connection.close();
					}
				}
			%>
		</div>
	</body>
</html>
</dsp:page>