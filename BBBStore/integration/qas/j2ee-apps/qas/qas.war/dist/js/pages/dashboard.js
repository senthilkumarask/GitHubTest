
$(function () {

  "use strict";

  window.appDashboard = {};

  //Make the dashboard widgets sortable Using jquery UI
  $(".connectedSortable").sortable({
    placeholder: "sort-highlight",
    connectWith: ".connectedSortable",
    handle: ".box-header, .nav-tabs",
    forcePlaceholderSize: true,
    zIndex: 999999
  });
  $(".connectedSortable .box-header, .connectedSortable .nav-tabs-custom").css("cursor", "move");

  //jQuery UI sortable for the todo list
  $(".todo-list").sortable({
    placeholder: "sort-highlight",
    handle: ".handle",
    forcePlaceholderSize: true,
    zIndex: 999999
  });

  
  /* jQueryKnob */
  $(".knob").knob();

 
  /* Morris.js Charts */
  // Sales chart
  if($('#revenue-chart')[0]){
  var area = new Morris.Area({
    element: 'revenue-chart',
    resize: true,
    data: [
      {y: '2015 Q1', item1: 2666, item2: 2666},
      {y: '2015 Q2', item1: 2778, item2: 2294},
      {y: '2015 Q3', item1: 4912, item2: 1969},
      {y: '2015 Q4', item1: 3767, item2: 3597},
      {y: '2016 Q1', item1: 6810, item2: 1914},
      {y: '2016 Q2', item1: 5670, item2: 4293},
      {y: '2016 Q3', item1: 4820, item2: 3795},
      {y: '2016 Q4', item1: 15073, item2: 5967},
      {y: '2017 Q1', item1: 10687, item2: 4460},
      {y: '2017 Q2', item1: 8432, item2: 5713}
    ],
    xkey: 'y',
    ykeys: ['item1', 'item2'],
    labels: ['Item 1', 'Item 2'],
    lineColors: ['#a0d0e0', '#3c8dbc'],
    hideHover: 'auto'
  });

}

  var line = new Morris.Line({
    element: 'line-chart',
    resize: true,
    data: [
      {y: '2015 Q1', Desktop: 2666, Mobile: 2666, TBS: 3666},
      {y: '2015 Q2', Desktop: 2778, Mobile: 2294, TBS: 3294},
      {y: '2015 Q3', Desktop: 4912, Mobile: 1969, TBS: 2969},
      {y: '2015 Q4', Desktop: 3767, Mobile: 3597, TBS: 4597},
      {y: '2016 Q1', Desktop: 6810, Mobile: 1914, TBS: 5914},
      {y: '2016 Q2', Desktop: 5670, Mobile: 4293, TBS: 2293},
      {y: '2016 Q3', Desktop: 4820, Mobile: 3795, TBS: 5795},
      {y: '2016 Q4', Desktop: 15073, Mobile: 5967, TBS: 1967}
    ],
    xkey: 'y',
    ykeys: ['Desktop', 'Mobile', 'TBS'],
    ykeys: ['Desktop', 'Mobile', 'TBS'],
    labels: ['Desktop', 'Mobile', 'TBS'],
    lineColors: ['#efefef', '#3c8dbc', '#f39c12'],
    lineWidth: 2,
    hideHover: 'auto',
    gridTextColor: "#fff",
    gridStrokeWidth: 0.4,
    pointSize: 4,
    pointStrokeColors: ["#efefef"],
    gridLineColor: "#efefef",
    gridTextFamily: "Open Sans",
    gridTextSize: 10
  });



  //Donut Chart
  
  //Fix for charts under tabs
  $('.box ul.nav a').on('shown.bs.tab', function () {
    area.redraw();
    donut.redraw();
    line.redraw();
  });

  
$("#time").html( Date() );



if($("#salesChart")[0]){
  // Get context with jQuery - using jQuery's .get() method.
  var salesChartCanvas = $("#salesChart").get(0).getContext("2d");
  // This will get the first returned node in the jQuery collection.
  var salesChart = new Chart(salesChartCanvas);

  var salesChartData = {
    labels: ["January", "February", "March", "April", "May", "June", "July"],
    datasets: [
      {
        label: "Desktop",
        fillColor: "rgb(210, 214, 222)",
        strokeColor: "rgb(210, 214, 222)",
        pointColor: "rgb(210, 214, 222)",
        pointStrokeColor: "#c1c7d1",
        pointHighlightFill: "#fff",
        pointHighlightStroke: "rgb(220,220,220)",
        data: [65, 59, 80, 81, 56, 55, 40]
      },
      {
        label: "Mobile",
        fillColor: "rgb(146, 195, 79)",
        strokeColor: "rgb(146, 195, 79)",
        pointColor: "rgb(146, 195, 79)",
        pointColor: "#92c34f",
        pointHighlightFill: "#fff",
        pointHighlightStroke: "rgba(202,57,163,0.9)",
        data: [12, 23, 74, 56, 23, 78, 45]
      },
      {
        label: "TBS",
        fillColor: "rgba(60,141,188,0.9)",
        strokeColor: "rgba(60,141,188,0.8)",
        pointColor: "#3b8bba",
        pointStrokeColor: "rgba(60,141,188,1)",
        pointHighlightFill: "#fff",
        pointHighlightStroke: "rgba(60,141,188,1)",
        data: [28, 48, 40, 19, 86, 27, 90]
      }
    ]
  };

  var salesChartOptions = {
    //Boolean - If we should show the scale at all
    showScale: true,
    //Boolean - Whether grid lines are shown across the chart
    scaleShowGridLines: false,
    //String - Colour of the grid lines
    scaleGridLineColor: "rgba(0,0,0,.05)",
    //Number - Width of the grid lines
    scaleGridLineWidth: 1,
    //Boolean - Whether to show horizontal lines (except X axis)
    scaleShowHorizontalLines: true,
    //Boolean - Whether to show vertical lines (except Y axis)
    scaleShowVerticalLines: true,
    //Boolean - Whether the line is curved between points
    bezierCurve: true,
    //Number - Tension of the bezier curve between points
    bezierCurveTension: 0.3,
    //Boolean - Whether to show a dot for each point
    pointDot: false,
    //Number - Radius of each point dot in pixels
    pointDotRadius: 4,
    //Number - Pixel width of point dot stroke
    pointDotStrokeWidth: 1,
    //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
    pointHitDetectionRadius: 20,
    //Boolean - Whether to show a stroke for datasets
    datasetStroke: true,
    //Number - Pixel width of dataset stroke
    datasetStrokeWidth: 2,
    //Boolean - Whether to fill the dataset with a color
    datasetFill: true,
    //String - A legend template
    legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].lineColor%>\"></span><%=datasets[i].label%></li><%}%></ul>",
    //Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
    maintainAspectRatio: true,
    //Boolean - whether to make the chart responsive to window resizing
    responsive: true
  };

  //Create the line chart
  salesChart.Line(salesChartData, salesChartOptions);
}
  //---------------------------
  //- END MONTHLY SALES CHART -
  //---------------------------

  //-------------
  //- PIE CHART -
  //-------------
  // Get context with jQuery - using jQuery's .get() method.
  if($("#pieChart")[0]){
  var pieChartCanvas = $("#pieChart").get(0).getContext("2d");
  var pieChart = new Chart(pieChartCanvas);
  var PieData = [
    {
      value: 700,
      color: "#f56954",
      highlight: "#f56954",
      label: "Chrome"
    },
    {
      value: 500,
      color: "#00a65a",
      highlight: "#00a65a",
      label: "IE"
    },
    {
      value: 400,
      color: "#f39c12",
      highlight: "#f39c12",
      label: "FireFox"
    },
    {
      value: 600,
      color: "#00c0ef",
      highlight: "#00c0ef",
      label: "Safari"
    },
    {
      value: 300,
      color: "#3c8dbc",
      highlight: "#3c8dbc",
      label: "Opera"
    },
    {
      value: 100,
      color: "#d2d6de",
      highlight: "#d2d6de",
      label: "Navigator"
    }
  ];
  var pieOptions = {
    //Boolean - Whether we should show a stroke on each segment
    segmentShowStroke: true,
    //String - The colour of each segment stroke
    segmentStrokeColor: "#fff",
    //Number - The width of each segment stroke
    segmentStrokeWidth: 1,
    //Number - The percentage of the chart that we cut out of the middle
    percentageInnerCutout: 50, // This is 0 for Pie charts
    //Number - Amount of animation steps
    animationSteps: 100,
    //String - Animation easing effect
    animationEasing: "easeOutBounce",
    //Boolean - Whether we animate the rotation of the Doughnut
    animateRotate: true,
    //Boolean - Whether we animate scaling the Doughnut from the centre
    animateScale: false,
    //Boolean - whether to make the chart responsive to window resizing
    responsive: true,
    // Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
    maintainAspectRatio: false,
    //String - A legend template
    legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<segments.length; i++){%><li><span style=\"background-color:<%=segments[i].fillColor%>\"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>",
    //String - A tooltip template
    tooltipTemplate: "<%=value %> <%=label%> users"
  };
  //Create pie or douhnut chart
  // You can switch between pie and douhnut using the method below.
  pieChart.Doughnut(PieData, pieOptions);
  }
  //-----------------
  //- END PIE CHART -
  //-----------------



var dskAN = '1322094',
    dskSD = '1322355',
    mobileAN = '1745893',
    mobileSD = '1745799',
    union = '7615113';

$.ajax({
  url: "https://api.newrelic.com/v2/applications.json?filter%5Bids%5D=1322094%2C1322355%2C7615113%2C1745893%2C1745799",
  type: "GET",
  beforeSend: function(xhr){
    xhr.setRequestHeader('Accept', 'application/json, text/javascript, */*; q=0.01')
    xhr.setRequestHeader('X-Api-Key', '05e6a96805b1c8815f8c14906f8d8227634ac405c85eba9')
  },
  success: function(data) {
    appDashboard.data= data;
    fillJsonData(data, dskAN, $('.dskAN'));
    fillJsonData(data, dskSD, $('.dskSD'));
    fillJsonData(data, mobileAN, $('.mobileAN'));
    fillJsonData(data, mobileSD, $('.mobileSD'));
    fillJsonData(data, union, $('.union'));
  }
});



var fillJsonData = function(data, id, el){
    var obj;
    for(var i=0; i<data.applications.length; i++){
      if((data.applications[i].id).toString() == id){
        obj = data.applications[i];
        break;
      }
    }
  var throughput = 'RPM : '+ (obj.application_summary.throughput/1000).toFixed(2) + 'k',
      apdex_target = 'Apdex : ' + obj.application_summary.apdex_score +' [' + obj.application_summary.apdex_target + ']';

  el.find('.rpm').html(throughput);
  el.find('.apdex').html(apdex_target);

  if(obj.application_summary.throughput/1000 > 70){
    el.parents('.info-box').removeClass('bg-green').addClass('bg-red'); 
  }
  if(obj.application_summary.throughput/1000 > 30 && !el.parents('.info-box').hasClass('bg-red')){
    el.parents('.info-box').removeClass('bg-green').addClass('bg-yellow'); 
  }

}


});