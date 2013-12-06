<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html">
    <head>
        <title>webgl-sprint</title>
        <meta charset="utf-8">

      <!-- link extjs 4 -->
      <link rel="stylesheet" type="text/css" href="js/ext-4.1.1a/resources/css/ext-all.css">
      <script type="text/javascript" src="js/ext-4.1.1a/ext-all.js"></script>
      
      <!-- Link three.js -->
      <script type="text/javascript" src="js/threejs/three.min.js"></script>
      <script type="text/javascript" src="js/threejs/controls/FlyControls.js"></script>
      <script type="text/javascript" src="js/threejs/Stats.js"></script>
      
      <!-- Link webgl-sprint application -->
      <script type="text/javascript" src="js/webglsprint/boreholes/Loader.js"></script>
      <script type="text/javascript" src="js/webglsprint/Main.js"></script>
      
      
      <script type="text/javascript">
      Ext.application({
          name : 'portal',

          //Here we build our GUI from existing components - this function should only be assembling the GUI
          //Any processing logic should be managed in dedicated classes - don't let this become a
          //monolithic 'do everything' function
          launch : webglsprint.Main.launch
      });
      </script>
      
   </head>

    <body>
    	<p id="loading-text">Requesting boreholes from remote service. Please be patient.</p>
        <div id="viewport" style="width: 100%; height: 100%">
        	<canvas id="webgl-sprint-canvas"></canvas>
        </div>
    </body>

</html>
