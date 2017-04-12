package com.bbb.admin.droplet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import atg.service.scheduler.Schedulable;
import atg.service.scheduler.SchedulableService;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;

public class ListSchedulerJobs extends BBBDynamoServlet {

	private Scheduler scheduler = null;
	private List<String> scheduleJobsList = null;
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		//((ServiceAdminServlet)getScheduler().getAdminServlet()).printAdminService(pRequest, pResponse);
		printScheduleJobs(pRequest, pResponse);
	}

	  protected void printScheduleJobs(final DynamoHttpServletRequest pRequest,
				final DynamoHttpServletResponse pRespons)
	  {
		  	ServletOutputStream pOut;
		  	try {
				pOut = pRespons.getOutputStream();

				pOut.println("<h3>Current time: " + new Date().toString() + "<BR>");
		        pOut.println("Next scheduled time: " + new Date(getScheduler().getNextTime()).toString() + "</h3>");
		        pOut.println("<h2>Scheduled jobs</h2>");
		        pOut.println("<table border>");
		        pOut.print("<tr>");
		        pOut.print("<th>id</th>");
		        pOut.print("<th>Name</th>");
		        pOut.print("<th>Last time</th>");
		        pOut.print("<th>Next time</th>");
		        pOut.print("<th># times run</th>");
		        pOut.print("<th>Source</th>");
		        pOut.print("<th>Destination</th>");
		        pOut.print("<th>Description</th>");
		        pOut.print("<th>Thread method</th>");
		        pOut.print("<th>Repeating?</th>");
		        pOut.print("<th>Schedule type</th>");
		        pOut.print("<th>Schedule</th>");
		        pOut.println("</tr>");
		        
		        for (String compName : getScheduleJobsList()) {
		          logDebug("Schedule component Name" + compName);
		          Object object = pRequest.resolveName(compName);
		          if(object == null) {
		        	  logDebug(compName +" not configured on this server");
		        	  continue;
		          }
		          if(!(object instanceof  SchedulableService)) {
		        	  continue;
		          }
		          int jobId = ((SchedulableService)object).getJobId();
		          ScheduledJob e = getScheduler().findScheduledJob(jobId);
		          if(e==null) {
		        	  logDebug(compName +" is not scheduled on this server");
		        	  continue;
		          }
		          pOut.print("<tr>");
		          pOut.print("<td>" + e.getJobId() + "</td>");
		          pOut.print("<td>" + e.getJobName() + "</td>");
		          if (e.getLastJobTime() != 0L) {
		            pOut.print("<td>" + new Date(e.getLastJobTime()).toString() + "</td>");
		          }
		          else
		          {
		            pOut.print("<td>not yet run</td>");
		          }
		          pOut.print("<td>" + new Date(e.getNextJobTime()).toString() + "</td>");
	
		          pOut.print("<td>" + e.getRunCount() + "</td>");
	
		          String sourceName = e.getSourceName();
		          pOut.print("<td>" + sourceName + "</td>");
	
		          Schedulable s = e.getSchedulable();
		          pOut.print("<td>" + s + "</td>");
		          pOut.print("<td>" + e.getJobDescription() + "</td>");
		          pOut.print("<td>");
		          switch (e.getThreadMethod()) {
		          case 1:
		            pOut.print("scheduler");
		            break;
		          case 2:
		            pOut.print("separate");
		            break;
		          case 3:
		            pOut.print("reused");
		            break;
		          
		          default: // do nothing
		        	  break;
		          }
	
		          pOut.print("</td>");
		          pOut.print("<td>" + e.getSchedule().isRepeating() + "</td>");
		          pOut.print("<td>" + e.getSchedule().getClass().getName() + "</td>");
	
		          pOut.print("<td>" + e.getSchedule().toString() + "</td>");
		          pOut.println("</tr>");
		        }
		        pOut.println("</table>");
		        pOut.println("<input type=\"submit\" value=\"Delete checked jobs\"/>");
		        pOut.println("</form>");
	
		        pOut.println("<h2>Unscheduled jobs</h2>");
		        pOut.println("<table border>");
		        pOut.print("<tr>");
		        pOut.print("<th>id</th>");
		        pOut.print("<th>Name</th>");
		        pOut.print("<th>Last time</th>");
		        pOut.print("<th># times run</th>");
		        pOut.print("<th>Source</th>");
		        pOut.print("<th>Destination</th>");
		        pOut.print("<th>Description</th>");
		        pOut.print("<th>Thread method</th>");
		        pOut.print("<th>Repeating?</th>");
		        pOut.print("<th>Schedule type</th>");
		        pOut.print("<th>Schedule</th>");
		        pOut.println("</tr>");
		        pOut.println("</table>");
		  	} catch (IOException e1) {
				// TODO Auto-generated catch block
		  		this.logError(e1.getMessage(),e1);
			}
	  }
	  


	public Scheduler getScheduler() {
		return scheduler;
	}
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public List<String> getScheduleJobsList() {
		return scheduleJobsList;
	}

	public void setScheduleJobsList(List<String> scheduleJobsList) {
		this.scheduleJobsList = scheduleJobsList;
	}
	
	
}
