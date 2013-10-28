<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html">
    <head>
        <title>webgl-sprint</title>
        <meta charset="utf-8">
        <script src="http://scenejs.org/api/latest/scenejs.js"></script>

      <!-- link extjs 4 -->
      <link rel="stylesheet" type="text/css" href="js/ext-4.1.1a/resources/css/ext-all.css">
      <script type="text/javascript" src="js/ext-4.1.1a/ext-all.js"></script>
      
      <script type="text/javascript">
      Ext.application({
          name : 'portal',

          //Here we build our GUI from existing components - this function should only be assembling the GUI
          //Any processing logic should be managed in dedicated classes - don't let this become a
          //monolithic 'do everything' function
          launch : function() {

              //This is the actual function request to the backend. The rest is just ExtJS boilerplate.
              Ext.Ajax.request({
                  url: 'getBoreholes.do',
                  params: {
           	          maxFeatures : 2
                  },
                  callback: function (options, success, response) {
                      if (!success) {
                          console.log('couldnt hit backend');
                          return;
                      }

                      var responseJsonObject = Ext.JSON.decode(response.responseText);

                      if (!responseJsonObject.success) {
                          console.log("unsuccesful data retrieval.");
                          return;
                      }

                      console.log("succesful data retrieval.");

                      //make scene configuration with globe
                      var configScene = {
                          canvasId: "webgl-sprint",
                          id: "scene",
                          nodes: [{
                              type: "cameras/orbit",
                              id: "camera",
                              yaw: 40,
                              pitch: -20,
                              zoom: 10,
                              zoomSensitivity: 10.0,
                              eye: {
                                  x: 0,
                                  y: 0,
                                  z: 10
                              },
                              look: {
                                  x: 0,
                                  y: 0,
                                  z: 0
                              },
                              nodes: [{
                                  type: "material",
                                  color: {
                                      r: 0.5,
                                      g: 0.5,
                                      b: 0.5
                                  },
                                  nodes: [{
                                      type: "geometry",
                                      source: {
                                          type: "sphere",
                                          latitudeBands: 30,
                                          longitudeBands: 30,
                                          radius: 1
                                      }
                                  }]
                              }]
                          }]
                      };

                      for (i = 0; i < responseJsonObject.data.length; ++i) {
                          var posarray = new Array();
                          for (j = 0; j < responseJsonObject.data[i].points.length; ++j) {
                              var point = responseJsonObject.data[i].points[j];
                              posarray.push(point.x);
                              posarray.push(point.y);
                              posarray.push(point.z);
                              console.log("added a point with coordinate (" + point.x + "," + point.y + "," + point.z + ")");
                          }

                          configScene.nodes[0].nodes.push({
                              type: "material",
                              color: {
                                  r: 0.0,
                                  g: 0.0,
                                  b: 1.0
                              },
                              nodes: [{
                                  type: "geometry",
                                  primitive: "lines",
                                  positions: posarray,
                                  indices: [0, 1]
                              }]
                          });

                          console.log("created borehole with lat/long = " + responseJsonObject.data[i].latitude + "/" + responseJsonObject.data[i].longitude + " and positions " + posarray);

                      }

                      //make scene
                      SceneJS.setConfigs({
                          pluginPath: "http://scenejs.org/api/latest/plugins"
                      });
                      SceneJS.createScene(configScene);
                  }
              });
          }
      });
      </script>
      
   </head>

    <body>
        <p>Hello World!</p>
        <canvas id="webgl-sprint" width="512px" height="512px"></canvas>
    </body>

</html>
