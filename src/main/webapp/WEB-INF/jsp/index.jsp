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
      
      <script type="text/javascript">
      
      Ext.application({
          name : 'portal',

          //Here we build our GUI from existing components - this function should only be assembling the GUI
          //Any processing logic should be managed in dedicated classes - don't let this become a
          //monolithic 'do everything' function
          launch : function() {

              var stats = new Stats();
              stats.setMode(0); // 0: fps, 1: ms

              // Align top-left
              stats.domElement.style.position = 'absolute';
              stats.domElement.style.right = '0px';
              stats.domElement.style.top = '0px';

              document.body.appendChild( stats.domElement );
              
              //This is the actual function request to the backend. The rest is just ExtJS boilerplate.
              Ext.Ajax.request({
                  url: 'getBoreholes.do',
                  params: {
           	          maxFeatures : 1000//,
           	       	  //featureId : 'boreholes.5271'
                  },
                  callback: function (options, success, response) {
                      if (!success) {
                          alert('Couldnt hit backend');
                          return;
                      }

                      var responseJsonObject = Ext.JSON.decode(response.responseText);

                      if (!responseJsonObject.success) {
                          alert("Unsuccesful data retrieval.");
                          return;
                      }
                      
                      Ext.get('loading-text').remove();
                      
                      alert('Rendering ' + responseJsonObject.data.length + ' boreholes now.\nTo look around, hold down the mouse and drag. To move the camera, use WASD');

                      var canvasEl = Ext.get('webgl-sprint-canvas').dom;
                      canvasEl.width = window.innerWidth;
                      canvasEl.height = window.innerHeight;
                      
                      var scene = new THREE.Scene();
                      var camera = new THREE.PerspectiveCamera(75, canvasEl.width / canvasEl.height, 0.1, 10000);
                      var renderer = new THREE.WebGLRenderer({
                          canvas : Ext.get('webgl-sprint-canvas').dom
                      });
                      
                      
                      for (i = 0; i < responseJsonObject.data.length; ++i) {
                          var points = [];
                          
                          var xm = 500;
                          var ym = 998;
                          var zm = 15.5;
                          
                          for (j = 0; j < responseJsonObject.data[i].points.length; ++j) {
                              var point = responseJsonObject.data[i].points[j];
                              var divisor = 100;
                              points.push(new THREE.Vector3(point.x / divisor - xm, point.y / divisor - ym, point.z / divisor - zm));
                          }

                          //The tube geometry will divide the points up into a set of n segments
                          //with a specified number of points on the radius. 
                          var segments = Math.ceil(Math.log(points.length) / Math.LN2); //lets decimate according to an arbitrary scale
                          var radius = 5;
                          var thickness = 0.1; //This doesn't increase/decrease polygons. Just their scale
                          
                          var path = new THREE.SplineCurve3(points);
                          var geometry = new THREE.TubeGeometry(path, segments, 0.1, radius, false);
                          var material = new THREE.MeshBasicMaterial( { color: Math.floor(Math.random()*16777215), wireframe: true} );
                          material.side = THREE.FrontSide;
                          var tube = new THREE.Mesh(geometry, material);
                          scene.add(tube);
                      }
                      
                      camera.position.z = 50;
                      
                      var controls = new THREE.FlyControls( camera );
                      controls.movementSpeed = 0.1;
                      controls.domElement = Ext.get('viewport').dom;
                      controls.rollSpeed = 0.01;
                      controls.autoForward = false;
                      controls.dragToLook = true;
                      
                      
                      var update = function() {
                          requestAnimationFrame( update );
                          
                          stats.begin();

                          controls.update(1);
                          renderer.render(scene, camera);
                          
                          stats.end();
                      };
                      
                      update();
                      
                      window.addEventListener( 'resize', onWindowResize, false );

                      function onWindowResize( event ) {
                          camera.aspect = window.innerWidth / window.innerHeight;
                          renderer.setSize( window.innerWidth, window.innerHeight );
                          renderer.render( scene, camera );
                      }
                  }
              });
          }
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
