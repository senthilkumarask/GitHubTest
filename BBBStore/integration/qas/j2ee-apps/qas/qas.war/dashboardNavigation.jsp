<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="atg.servlet.ServletUtil" %>
<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/dashboard/DashboardFormHandler" />
	<dsp:importbean bean="/com/bbb/integration/dashboard/DashboardDataDroplet" />
		<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<c:set var="userAuthenticated">
	<%=ServletUtil.getCurrentRequest().getSession().getAttribute("userAuthenticated")%>
</c:set>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>App Support | Dashboard</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="dist/css/AdminLTE.min.css">
  <!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
  <link rel="stylesheet" href="dist/css/skins/_all-skins.min.css">
  <!-- Morris chart -->
  <link rel="stylesheet" href="plugins/morris/morris.css">
  <!-- jvectormap -->
  <link rel="stylesheet" href="plugins/jvectormap/jquery-jvectormap-1.2.2.css">

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->
</head>
<body class="hold-transition skin-blue sidebar-mini mainDashbord">
<%--     <c:set var="userAuthenticated" scope="request"><c:out value="${param.userAuthenticated}"/></c:set> --%>
<%-- <c:if test = "${!userAuthenticated}"> --%>
<%-- 	      <dsp:droplet name="RedirectDroplet"> --%>
<%-- 	      	<dsp:param name="url" value="/dashboard_login.jsp" /> --%>
<%-- 	      </dsp:droplet> --%>
<%-- 	</c:if> --%>
<c:if test="${userAuthenticated}">
<div class="wrapper">

  <header class="main-header">
    <!-- Logo -->
    <%--! Prashant: SHould this be here?? --%>
    <a href="./dashboardNavigation.jsp" class="logo">
      <!-- mini logo for sidebar mini 50x50 pixels -->
      <span class="logo-mini"><i class="ion-speedometer"></i></span>
      <!-- logo for regular state and mobile devices -->
      <span class="logo-lg"><i class="ion-speedometer"></i> Dashboard</span>
    </a>
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
      <!-- Sidebar toggle button-->
      <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
        <span class="sr-only">Toggle navigation</span>
      </a>
    <h1 style=" font-size: 18px;color: #fff;text-align: center;">
    AppSupport <i class="ion-speedometer" style="margin: 0 8px;"></i> Overview
    </h1>
     </nav>
  </header>
  <!-- Left side column. contains the logo and sidebar -->
  <aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
      <!-- sidebar menu: : style can be found in sidebar.less -->
      <ul class="sidebar-menu">
        <li class="header">QUICK NAVIGATION</li>
        <li class="active">
          <a href="#">
            <i class="fa fa-dashboard"></i> <span>Dashboard</span>
            <span class="pull-right-container">
              <i class="fa fa-angle-right pull-right"></i>
            </span>
          </a>
        </li>
        <li class="">
          <a href="desktop.jsp">
            <i class="fa fa-desktop"></i> <span>Desktop</span>
            <span class="pull-right-container">
              <i class="fa fa-angle-right pull-right"></i>
            </span>
          </a>
        </li>
        <li class="">
          <a href="desktop.jsp">
            <i class="ion-android-phone-portrait" style="padding-right: 13px;"></i> <span>Mobile</span>
            <span class="pull-right-container">
              <i class="fa fa-angle-right pull-right"></i>
            </span>
          </a>
        </li>
        <li class="">
          <a href="desktop.jsp">
            <i class="fa fa-bank"></i> <span>InStore</span>
            <span class="pull-right-container">
              <i class="fa fa-angle-right pull-right"></i>
            </span>
          </a>
        </li>
      </ul>
    </section>
    <!-- /.sidebar -->
  </aside>
  
			

	       

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>Overall Statistics</h1>
      <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
        <li class="active">Dashboard</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <!-- Small boxes (Stat box) -->
       
      <div class="row">
		<div class="col-md-12">
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">Order Data Comparison</h3>
              <dsp:droplet name="/com/bbb/integration/dashboard/DashboardDataDroplet">
					<dsp:param name="operationRequired" value="orderComparision"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="orderCountCurrent" param="orderCountCurrent" />
			       			<dsp:getvalueof var="orderCountYesterday" param="orderCountYesterday" />
			       			<dsp:getvalueof var="orderCountLastWeek" param="orderCountLastWeek" />
			       			<table class="table no-margin">
		                    <thead>
		                    <tr>
		                      <th>Hour</th>
		                      <th>Time</th>
		                    </tr>
		                    </thead>
		                    <tbody>
		                    <tr>
			       			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param value="${orderCountCurrent}" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="timeDuration" param="key"/>
									<dsp:getvalueof var="orderCount" param="element"/>								
					                    <tr>
						                      <td>${timeDuration}</td>
						                      <td>${orderCount}</td>
					                    </tr>
								</dsp:oparam>
							</dsp:droplet>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param value="${orderCountYesterday}" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="timeDuration" param="key"/>
									<dsp:getvalueof var="orderCount" param="element"/>
										<tr>
						                      <td>${timeDuration}</td>
						                      <td>${orderCount}</td>
					                    </tr>
								</dsp:oparam>
							</dsp:droplet>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param value="${orderCountLastWeek}" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="timeDuration" param="key"/>
									<dsp:getvalueof var="orderCount" param="element"/>
										<tr>
						                      <td>${timeDuration}</td>
						                      <td>${orderCount}</td>
					                    </tr>
								</dsp:oparam>
							</dsp:droplet>
							</tr>
							</tbody>
					       </table>
						</dsp:oparam>
				</dsp:droplet>
             </div>
            </div>
      </div>
      	<dsp:droplet name="/com/bbb/integration/dashboard/DashboardDataDroplet">
			<dsp:param name="operationRequired" value="submittedOrderCount"/>
			<dsp:oparam name="output">
	       <dsp:getvalueof var="submittedOrderCountStore" param="submittedOrderCountStore" />
	       <dsp:getvalueof var="submittedOrderCountMobile" param="submittedOrderCountMobile" />
	       <dsp:getvalueof var="submittedOrderCountTBS" param="submittedOrderCountTBS" />
        <div class="col-md-12">      
          <div class="box box-success">
            <div class="box-header with-border">
              <h3 class="box-title">DC Status Summary</h3>
              <div class="box-tools pull-right">
                <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                </button>
                <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
              </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <div class="col-md-4">
                <div class="info-box bg-green">
                  <span class="info-box-icon"><i class="ion-ios-monitor"></i></span>
                  <div class="info-box-content">
                    <span class="info-box-text">AN</span>
                    <div class="dskAN">
                      <span class="rpm info-box-number"> RPM : -- </span>
                      <span class="apdex"> Apdex :  -- </span>
                    </div>
                    <div class="progress"><div class="progress-bar" style="width: 70%"></div></div>
                    <span class="info-box-text">SD</span>
                     <div class="dskSD">
                      <span class="rpm info-box-number"> RPM : -- </span>
                      <span class="apdex"> Apdex :  -- </span>
                    </div>
                  </div><!-- /.info-box-content -->
                </div><!-- /.info-box -->
              </div>

              <div class="col-md-4">
                <div class="info-box bg-green">
                  <span class="info-box-icon"><i class="ion-android-phone-portrait"></i></span>
                  <div class="info-box-content">
                    <span class="info-box-text">AN</span>
                    <div class="mobileAN">
                      <span class="rpm info-box-number"> RPM : -- </span>
                      <span class="apdex"> Apdex :  -- </span>
                    </div>
                    <div class="progress"><div class="progress-bar" style="width: 70%"></div></div>
                    <span class="info-box-text">SD</span>
                     <div class="mobileSD">
                      <span class="rpm info-box-number"> RPM : -- </span>
                      <span class="apdex"> Apdex :  -- </span>
                    </div>
                  </div><!-- /.info-box-content -->
                </div><!-- /.info-box -->
              </div>

              <div class="col-md-4">
                <div class="info-box bg-green">
                  <span class="info-box-icon"><i class="fa fa-bank"></i></span>
                   <div class="info-box-content" style="padding-top: 30px; line-height: 26px;">
                    <span class="info-box-text">Union</span>
                    <div class="union">
                      <span class="rpm info-box-number"> RPM : -- </span>
                      <span class="apdex"> Apdex :  -- </span>
                    </div>
                    <div class="progress"><div class="progress-bar" style="width: 70%"></div></div>
                    </div><!-- /.info-box-content -->
                </div><!-- /.info-box -->
              </div>

            </div>
          </div>

        </div>

        <div class="col-md-9">
          <div class="box">
            <div class="box-header with-border">
              <h3 class="box-title">Today's Order Summary</h3>
              <div class="box-tools pull-right">
                <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                </button>
                <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
              </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
                
                <div class="col-md-12" style="padding-top:8px">

                  <div class="col-md-4">
                                    <p class="text-center" style="padding-bottom:10px">
                                      <strong>Desktop Order Summary</strong>
                                    </p>
                                    <div class="progress-group">
                                      <span class="progress-text">Total Orders</span>
                                      <span class="progress-number"><b>${submittedOrderCountStore + unSubmittedOrderCountStore}</b></span>
                                      <div class="progress sm">
                                        <div class="progress-bar progress-bar-aqua" style="width: 100%"></div>
                                      </div>

                                      <span class="progress-text">Unsubmitted Order</span>
                                      <span class="progress-number"><b>${unSubmittedOrderCountStore}</b>/${submittedOrderCountStore + unSubmittedOrderCountStore}</span>
                                      <div class="progress  progress-striped sm">
                                        <div class="progress-bar progress-bar-yellow" style="width: 80%"></div>
                                      </div>

                                      <span class="progress-text">Failed Order</span>
                                      <span class="progress-number"><b>${unSubmittedOrderCountStore}</b></span>
                                      <div class="progress sm">
                                        <div class="progress-bar progress-bar-red" style="width: 60%"></div>
                                      </div>

                                    </div>
                  </div>  
                   <div class="col-md-4">
                                    <p class="text-center" style="padding-bottom:10px">
                                      <strong>Mobile Order Summary</strong>
                                    </p>                
                                   
                                    <div class="progress-group">
                                      <span class="progress-text">Total Orders</span>
                                      <span class="progress-number"><b>${submittedOrderCountMobile + unSubmittedOrderCountMobile}</b></span>

                                      <div class="progress sm">
                                        <div class="progress-bar progress-bar-green" style="width: 100%"></div>
                                      </div>
                                    <span class="progress-text">Unsubmitted Order</span>
                                      <span class="progress-number"><b>${unSubmittedOrderCountMobile}</b>/${submittedOrderCountMobile + unSubmittedOrderCountMobile}</span>
                                      <div class="progress  progress-striped sm">
                                        <div class="progress-bar progress-bar-yellow" style="width: 80%"></div>
                                      </div>

                                      <span class="progress-text">Failed Order</span>
                                      <span class="progress-number"><b>${unSubmittedOrderCountMobile}</b></span>
                                      <div class="progress sm">
                                        <div class="progress-bar progress-bar-red" style="width: 60%"></div>
                                      </div>

                                    </div>
                  </div>  
                   <div class="col-md-4">
                                    <p class="text-center" style="padding-bottom:10px">
                                      <strong>TBS Order Summary</strong>
                                    </p>                 

                                    <div class="progress-group">
                                      <span class="progress-text">Total Orders</span>
                                      <span class="progress-number"><b>${unSubmittedOrderCountTBS}</b>/${submittedOrderCountTBS + unSubmittedOrderCountTBS}</b></span>

                                      <div class="progress sm">
                                        <div class="progress-bar progress-bar-primary" style="width: 100%"></div>
                                      </div>
									  
									  <span class="progress-text">Unsubmitted Order</span>
                                      <span class="progress-number"><b>${unSubmittedOrderCountTBS}</b>/${submittedOrderCountTBS + unSubmittedOrderCountTBS}</span>
                                      <div class="progress sm  progress-striped">
                                        <div class="progress-bar progress-bar-yellow" style="width: 80%"></div>
                                      </div>

                                      <span class="progress-text">Failed Order</span>
                                      <span class="progress-number"><b>${unSubmittedOrderCountTBS}</b></span>
                                      <div class="progress sm">
                                        <div class="progress-bar progress-bar-red" style="width: 60%"></div>
                                      </div>
									  
                                    </div>

                  </div>
                </div>
                <!-- /.col -->
            </div>
            <!-- ./box-body -->
          </div>
          <!-- /.box -->
        </div>
        <!-- /.col -->




      <div class="col-md-3">
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">Endeca Baseline</h3>
              <div class="box-tools pull-right">
                <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                </button>
                <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
              </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
              <div class="table-responsive">
                  <table class="table no-margin">
                    <thead>
                    <tr>
                      <th>ID</th>
                      <th>Time</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                      <td>Projet -1 </td>
                      <td>2016-08-18 02:15:00.131</td>
                    </tr>
                    <tr>
                      <td>Projet -2 </td>
                      <td>2016-08-18 02:15:00.131</td>
                    </tr>
                    <tr>
                      <td>Projet -3</td>
                      <td>2016-08-18 02:15:00.131</td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <!-- /.box-body -->
          </div>
          <!-- /.box -->
      </div>

     <div class="col-md-12">
          <div class="box box-info">
            <div class="box-header with-border">
              <h3 class="box-title">Latest Feed Status</h3>

              <div class="box-tools pull-right">
                <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                </button>
                <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
              </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body no-padding">
              <div class="table-responsive">
                <table class="table no-margin">
                  <thead>
                  <tr>
                    
                    <th>Description</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Status</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr>
                    <td>RKGMarketingFeed</td>
                    <td>2016-08-18 02:15:00.001</td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-success">Success</span></td>
                    
                  </tr>
                  <tr>
                    <td>YourAmigoMarketingFeed </td>
                    <td>2016-08-18 02:15:00.001</td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-warning">Pending</span></td>
                   
                  </tr>
                  <tr>
                    <td>CJMarketingFeed </td>
                    <td>2016-08-18 02:15:00.001</td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-danger">Fail</span></td>
                    
                  </tr>
                  <tr>
                    <td>BVCatalogFeed </td>
                    <td>2016-08-18 02:15:00.001</td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-info">Processing</span></td>
                    
                  </tr>
                  <tr>
                    <td>FacebookFeed </td>
                    <td>2016-08-18 02:15:00.001</td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-warning">Pending</span></td>
                    
                  </tr>
                  <tr>
                    <td>CJMarketingFeed </td>
                    <td>2016-08-18 02:15:00.001</td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-danger">Fail</span></td>
                    
                  </tr>
                  <tr>
                    <td>BVClientFeed </td>
                    <td>2016-08-18 02:15:00.001</td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-success">Success</span></td>
                    
                  </tr>
                  </tbody>
                </table>
              </div>
              <!-- /.table-responsive -->
            </div>
            <!-- /.box-body -->
            <div class="box-footer clearfix">
              <a href="javascript:void(0)" class="btn btn-sm btn-default btn-flat pull-right">View All </a>
            </div>
            <!-- /.box-footer -->
          </div>
          <!-- /.box -->
      </div>



     <div class="col-md-8">
          <div class="box box-success">
            <div class="box-header with-border">
              <h3 class="box-title">Latest Checkin Projects</h3>

              <div class="box-tools pull-right">
                <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                </button>
                <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
              </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body no-padding">
              <div class="table-responsive">
                <table class="table no-margin">
                  <thead>
                  <tr>
                    <th>Project Name</th>
                    <th>Time</th>
                    <th>Status</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr>
                    <td>Projet -1 </td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-success">Success</span></td>
                    
                  </tr>
                  <tr>
                    <td>Projet -2 </td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-warning">Pending</span></td>
                   
                  </tr>
                  <tr>
                    <td>Projet -3</td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-success">Success</span></td>
                    
                  </tr>
                  <tr>
                    <td>Projet -4 </td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-info">Processing</span></td>
                    
                  </tr>
                  <tr>
                    <td>Projet -5 </td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-warning">Pending</span></td>
                    
                  </tr>
                  <tr>
                    <td>Projet -6 </td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-success">Success</span></td>
                    
                  </tr>
                  <tr>
                    <td>Projet -7 </td>
                    <td>2016-08-18 02:15:00.131</td>
                    <td><span class="label label-success">Success</span></td>
                    
                  </tr>
                  </tbody>
                </table>
              </div>
              <!-- /.table-responsive -->
            </div>
            <!-- /.box-body -->
            <div class="box-footer clearfix">
              <a href="javascript:void(0)" class="btn btn-sm btn-default btn-flat pull-right">View All </a>
            </div>
            <!-- /.box-footer -->
          </div>
          <!-- /.box -->
      </div>



     <div class="col-md-4">
         <div class="box box-warning">
            <div class="box-header">
              <h3 class="box-title">Current Task Status</h3>
              <div class="box-tools pull-right">
                  <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                    </button>      
                    <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i>
                    </button>
                  </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body no-padding">
              <div class="table-responsive">
                <table class="table no-margin">
                <tbody><tr>
                  <th style="width: 10px">#</th>
                  <th>Task</th>
                  <th>Progress</th>
                  <th style="width: 40px">Label</th>
                </tr>
                <tr>
                  <td>1.</td>
                  <td>Update software</td>
                  <td>
                    <div class="progress progress-xs">
                      <div class="progress-bar progress-bar-danger" style="width: 55%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-red">55%</span></td>
                </tr>
                <tr>
                  <td>2.</td>
                  <td>Clean database</td>
                  <td>
                    <div class="progress progress-xs">
                      <div class="progress-bar progress-bar-yellow" style="width: 70%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-yellow">70%</span></td>
                </tr>
                <tr>
                  <td>3.</td>
                  <td>Cron job running</td>
                  <td>
                    <div class="progress progress-xs progress-striped active">
                      <div class="progress-bar progress-bar-primary" style="width: 30%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-light-blue">30%</span></td>
                </tr>
                <tr>
                  <td>4.</td>
                  <td>Fix and squish bugs</td>
                  <td>
                    <div class="progress progress-xs progress-striped active">
                      <div class="progress-bar progress-bar-success" style="width: 90%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-green">90%</span></td>
                </tr>
                <tr>
                  <td>3.</td>
                  <td>Cron job running</td>
                  <td>
                    <div class="progress progress-xs progress-striped active">
                      <div class="progress-bar progress-bar-primary" style="width: 30%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-light-blue">30%</span></td>
                </tr>
                <tr>
                  <td>4.</td>
                  <td>Fix and squish bugs</td>
                  <td>
                    <div class="progress progress-xs progress-striped active">
                      <div class="progress-bar progress-bar-success" style="width: 90%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-green">90%</span></td>
                </tr>
                <tr>
                  <td>4.</td>
                  <td>Fix and squish bugs</td>
                  <td>
                    <div class="progress progress-xs progress-striped active">
                      <div class="progress-bar progress-bar-success" style="width: 90%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-green">90%</span></td>
                </tr>

<tr>
                  <td>3.</td>
                  <td>Cron job running</td>
                  <td>
                    <div class="progress progress-xs progress-striped active">
                      <div class="progress-bar progress-bar-primary" style="width: 30%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-light-blue">30%</span></td>
                </tr>
                <tr>
                  <td>4.</td>
                  <td>Fix and squish bugs</td>
                  <td>
                    <div class="progress progress-xs progress-striped active">
                      <div class="progress-bar progress-bar-success" style="width: 90%"></div>
                    </div>
                  </td>
                  <td><span class="badge bg-green">90%</span></td>
                </tr>              
                </tbody>

                </table>
                </div>
            </div>
            <!-- /.box-body -->
             <div class="box-footer clearfix">
              <a href="javascript:void(0)" class="btn btn-sm btn-default btn-flat pull-right">View All </a>
            </div>
          </div>
      </div>












 </div>
      <!-- /.row -->













      <div class="row">
        <div class="col-md-8 col-xs-12">
          <div class="box">
            <div class="box-header with-border">
              <h3 class="box-title">Monthly Recap Report</h3>

              <div class="box-tools pull-right">
                <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                </button>
               
                <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
              </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
                <div class="">
                  <p class="text-center">
                    <strong>Sales: 1 Jan, 2016 - 30 Jul, 2016</strong>
                  </p>

                  <div class="chart">
                    <!-- Sales Chart Canvas -->
                    <canvas id="salesChart" style="height: 180px;"></canvas>
                  </div>
                  <!-- /.chart-responsive -->
                </div>
                <!-- /.col -->
                
            </div>
            <!-- ./box-body -->
            
            <!-- /.box-footer -->
          </div>
          <!-- /.box -->
        </div>

        <div class="col-md-4 col-xs-12 connectedSortable">
          <div class="box box-solid bg-teal-gradient">
            <div class="box-header">
              <i class="fa fa-th"></i>

              <h3 class="box-title">Sales Graph</h3>

              <div class="box-tools pull-right">
                <button type="button" class="btn bg-teal btn-sm" data-widget="collapse"><i class="fa fa-minus"></i>
                </button>
                <button type="button" class="btn bg-teal btn-sm" data-widget="remove"><i class="fa fa-times"></i>
                </button>
              </div>
            </div>
            <div class="box-body border-radius-none">
              <div class="chart" id="line-chart" style="height: 250px;"></div>
            </div>
            <!-- /.box-body -->
            <div class="box-footer no-border">
              <div class="row">
                <div class="col-xs-4 text-center" style="border-right: 1px solid #f4f4f4">
                  <input type="text" class="knob" data-readonly="true" value="20" data-width="60" data-height="60" data-fgColor="#39CCCC">

                  <div class="knob-label">Desktop Orders</div>
                </div>
                <!-- ./col -->
                <div class="col-xs-4 text-center" style="border-right: 1px solid #f4f4f4">
                  <input type="text" class="knob" data-readonly="true" value="50" data-width="60" data-height="60" data-fgColor="#39CCCC">

                  <div class="knob-label">Mobile Orders</div>
                </div>
                <!-- ./col -->
                <div class="col-xs-4 text-center">
                  <input type="text" class="knob" data-readonly="true" value="30" data-width="60" data-height="60" data-fgColor="#39CCCC">

                  <div class="knob-label">In-Store Orders</div>
                </div>
                <!-- ./col -->
              </div>
              <!-- /.row -->
            </div>
            <!-- /.box-footer -->
          </div>
          <!-- /.box -->
        </div>

      </div>
      <!-- /.row -->





    </section>
    <!-- /.content -->
  </div>
  	     </dsp:oparam>
		</dsp:droplet>
  <!-- /.content-wrapper -->
  <footer class="main-footer">
    <div class="pull-right" id="time" style=" margin: -10px"></div>
  </footer>

  <!-- Control Sidebar -->
  <aside class="control-sidebar control-sidebar-dark">
    <!-- Create the tabs -->
    <ul class="nav nav-tabs nav-justified control-sidebar-tabs">
      <li><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-home"></i></a></li>
      <li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-gears"></i></a></li>
    </ul>
    <!-- Tab panes -->
    <div class="tab-content">
      <!-- Home tab content -->
      <div class="tab-pane" id="control-sidebar-home-tab">
        <h3 class="control-sidebar-heading">Recent Activity</h3>
        <ul class="control-sidebar-menu">
          <li>
            <a href="javascript:void(0)">
              <i class="menu-icon fa fa-birthday-cake bg-red"></i>

              <div class="menu-info">
                <h4 class="control-sidebar-subheading">Langdon's Birthday</h4>

                <p>Will be 23 on April 24th</p>
              </div>
            </a>
          </li>
          <li>
            <a href="javascript:void(0)">
              <i class="menu-icon fa fa-user bg-yellow"></i>

              <div class="menu-info">
                <h4 class="control-sidebar-subheading">Frodo Updated His Profile</h4>

                <p>New phone +1(800)555-1234</p>
              </div>
            </a>
          </li>
          <li>
            <a href="javascript:void(0)">
              <i class="menu-icon fa fa-envelope-o bg-light-blue"></i>

              <div class="menu-info">
                <h4 class="control-sidebar-subheading">Nora Joined Mailing List</h4>

                <p>nora@example.com</p>
              </div>
            </a>
          </li>
          <li>
            <a href="javascript:void(0)">
              <i class="menu-icon fa fa-file-code-o bg-green"></i>

              <div class="menu-info">
                <h4 class="control-sidebar-subheading">Cron Job 254 Executed</h4>

                <p>Execution time 5 seconds</p>
              </div>
            </a>
          </li>
        </ul>
        <!-- /.control-sidebar-menu -->

        <h3 class="control-sidebar-heading">Tasks Progress</h3>
        <ul class="control-sidebar-menu">
          <li>
            <a href="javascript:void(0)">
              <h4 class="control-sidebar-subheading">
                Custom Template Design
                <span class="label label-danger pull-right">70%</span>
              </h4>

              <div class="progress progress-xxs">
                <div class="progress-bar progress-bar-danger" style="width: 70%"></div>
              </div>
            </a>
          </li>
          <li>
            <a href="javascript:void(0)">
              <h4 class="control-sidebar-subheading">
                Update Resume
                <span class="label label-success pull-right">95%</span>
              </h4>

              <div class="progress progress-xxs">
                <div class="progress-bar progress-bar-success" style="width: 95%"></div>
              </div>
            </a>
          </li>
          <li>
            <a href="javascript:void(0)">
              <h4 class="control-sidebar-subheading">
                Laravel Integration
                <span class="label label-warning pull-right">50%</span>
              </h4>

              <div class="progress progress-xxs">
                <div class="progress-bar progress-bar-warning" style="width: 50%"></div>
              </div>
            </a>
          </li>
          <li>
            <a href="javascript:void(0)">
              <h4 class="control-sidebar-subheading">
                Back End Framework
                <span class="label label-primary pull-right">68%</span>
              </h4>

              <div class="progress progress-xxs">
                <div class="progress-bar progress-bar-primary" style="width: 68%"></div>
              </div>
            </a>
          </li>
        </ul>
        <!-- /.control-sidebar-menu -->

      </div>
      <!-- /.tab-pane -->
      <!-- Stats tab content -->
      <div class="tab-pane" id="control-sidebar-stats-tab">Stats Tab Content</div>
      <!-- /.tab-pane -->
      <!-- Settings tab content -->
      <div class="tab-pane" id="control-sidebar-settings-tab">
        <form method="post">
          <h3 class="control-sidebar-heading">General Settings</h3>

          <div class="form-group">
            <label class="control-sidebar-subheading">
              Report panel usage
              <input type="checkbox" class="pull-right" checked>
            </label>

            <p>
              Some information about this general settings option
            </p>
          </div>
          <!-- /.form-group -->

          <div class="form-group">
            <label class="control-sidebar-subheading">
              Allow mail redirect
              <input type="checkbox" class="pull-right" checked>
            </label>

            <p>
              Other sets of options are available
            </p>
          </div>
          <!-- /.form-group -->

          <div class="form-group">
            <label class="control-sidebar-subheading">
              Expose author name in posts
              <input type="checkbox" class="pull-right" checked>
            </label>

            <p>
              Allow the user to show his name in blog posts
            </p>
          </div>
          <!-- /.form-group -->

          <h3 class="control-sidebar-heading">Chat Settings</h3>

          <div class="form-group">
            <label class="control-sidebar-subheading">
              Show me as online
              <input type="checkbox" class="pull-right" checked>
            </label>
          </div>
          <!-- /.form-group -->

          <div class="form-group">
            <label class="control-sidebar-subheading">
              Turn off notifications
              <input type="checkbox" class="pull-right">
            </label>
          </div>
          <!-- /.form-group -->

          <div class="form-group">
            <label class="control-sidebar-subheading">
              Delete chat history
              <a href="javascript:void(0)" class="text-red pull-right"><i class="fa fa-trash-o"></i></a>
            </label>
          </div>
          <!-- /.form-group -->
        </form>
      </div>
      <!-- /.tab-pane -->
    </div>
  </aside>
  <!-- /.control-sidebar -->
  <!-- Add the sidebar's background. This div must be placed
       immediately after the control sidebar -->
  <div class="control-sidebar-bg"></div>
</div>
</c:if>
<!-- ./wrapper -->








































<!-- jQuery 2.2.3 -->
<script src="plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- jQuery UI 1.11.4 -->
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<script>
  $.widget.bridge('uibutton', $.ui.button);
</script>
<!-- Bootstrap 3.3.6 -->
<script src="bootstrap/js/bootstrap.min.js"></script>
<!-- Morris.js charts -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/raphael/2.1.0/raphael-min.js"></script>
<script src="plugins/morris/morris.min.js"></script>
<!-- jQuery Knob Chart -->
<script src="plugins/knob/jquery.knob.js"></script>
<!-- Slimscroll -->
<script src="plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- ChartJS 1.0.1 -->
<script src="plugins/chartjs/Chart.min.js"></script>
<!-- AdminLTE App -->
<script src="dist/js/app.min.js"></script>
<script src="dist/js/pages/dashboard.js"></script>

</body>
</dsp:page>

