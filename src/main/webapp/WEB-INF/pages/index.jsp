<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>IndiePIM</title>
    <!--[if lt IE 9]>
    <script type="text/javascript" charset="utf-8" src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <script type="text/javascript" charset="utf-8" src="http://cdnjs.cloudflare.com/ajax/libs/json2/20110223/json2.js"></script>
    <script type="text/javascript" charset="utf-8" src="http://explorercanvas.googlecode.com/svn/trunk/excanvas.js"></script>
    <![endif]-->
    <link href="style.css" rel="stylesheet" />
    <link rel="icon" href="favicon.ico" type="image/x-icon">
    <!-- optional static style definition to render the presentation fast, before styling get applied via JS code.
    see 'src/modules/baseModule/theme/component.js' to see dynamic stylesheet setup on themes, TODO find a better way to supportstyles -->
    <link href="libs/jquery/css/smoothness/jquery-ui-1.10.3.custom.css" rel="stylesheet" />
    <!--<link href="libs/DataTables/media/css/jquery.dataTables.css" rel="stylesheet" />-->
    <link href="libs/toastr/toastr.min.css" rel="stylesheet"/>
    <link href="libs/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="libs/fullcalendar/fullcalendar.css" rel="stylesheet">
    <link href="libs/jquery/tokeninput/token-input-facebook.css" rel="stylesheet">
    <link href="libs/knockout/css/ui-tree.css" rel="stylesheet">
    <link href="libs/select2-3.4.5/select2.css" rel="stylesheet">
    <link href="libs/jquery.ui.chatbox/jquery.ui.chatbox.css" rel="stylesheet" />
</head>

<body>

    <section id="page-content" style="display:none">
        <header class="ui-layout-north" id="header"></header>
		<section class="ui-layout-center" id="content"></section>
		<aside class="ui-layout-west" id="navpanel"></aside>
	</section>
    <div id="progressbar"></div>

<%--<div class="headerPanel" id="header"></div>--%>
<%--<div class="mainPanel" id="mainPanel">--%>
    <%--<div class="navigationPanel" id="navigationPanel"></div>--%>
    <%--<div class="contentPanel" id="content"></div>--%>
<%--</div>--%>
<script type="text/javascript">
    var contextPath = "${pageContext.request.contextPath}"
</script>
<!-- we use jquery and underscore as 2 utilities in the BoilerplateJS core -->
<script src="libs/jquery/jquery-1.10.2.min.js" type="text/javascript" charset="utf-8"></script>
<script src="libs/jquery/jqueryui/jquery-ui-1.10.3.custom.js" charset="utf-8"></script>

<script type="text/javascript">
    var libcnt = 25;
    var libi = 0;
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>

<script src="libs/underscore/underscore-1.3.3.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
    <!-- following libraries are used by the UrlController for client routing and browser history -->
<script src="libs/signals/signals.min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/crossroads/crossroads.min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/hasher/hasher.min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<!-- following library is used by the Mediator class for pub-sub event handling -->
<script src="libs/pubsub/pubsub-20120708.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<!-- sample UI component implementations use knockout for MVVM bindings, but not necessary for BPJS core -->
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/knockout/knockout-2.2.1.debug.js" type="text/javascript" charset="utf-8"></script>
<!-- BPJS initializer scripts-->
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/boilerplate/groundwork.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/jquery/jquerylayout/jquery.layout-latest.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/jquery/jquerylayout/jquery.layout.resizePaneAccordions-latest.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/jquery/jQuery-slimScroll-1.3.0/jquery.slimscroll.min.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<!-- For Knockout UI Tree component -->
<script src="libs/knockout/ui-core.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/knockout/ui-tree.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/moment/moment-with-langs.min.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
    moment.lang('de');
</script>
<script src="libs/toastr/toastr.min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/fullcalendar/fullcalendar.min.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/sparklines/jquery.sparkline.min.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/ckeditor/ckeditor.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/ckeditor/adapters/jquery.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/jquery/tokeninput/jquery.tokeninput.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/select2-3.4.5/select2.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<!-- Knockout Tokeninput binding -->
<script src="libs/knockout/tokeninput/ko-tokeninput.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/knockout/ko-select2.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
</script>
<script src="libs/jquery.ui.chatbox/jquery.ui.chatbox.js" charset="utf-8"></script>
<script type="text/javascript">
    $("#progressbar").progressbar({value: Math.ceil(100/libcnt * ++libi)});
    $("#progressbar").hide();
    $("#page-content").show();
</script>


<!-- following is the main entry script to the application code. we use requirejs to load main.js -->
<script type="text/javascript" data-main="main.js" src="libs/require/require.js"></script>

</body>
</html>
<!-- BoilerplateJS v0.3rc -->
