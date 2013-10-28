<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html">
    <head>
        <title>webgl-sprint</title>
        <meta charset="utf-8">
        <script src="http://scenejs.org/api/latest/scenejs.js"></script>
    </head>

    <body>
        <p>Hello World!</p>
        <canvas id="webgl-sprint" width="512px" height="512px"></canvas>
        <script>
            SceneJS.setConfigs({
                pluginPath:"http://scenejs.org/api/latest/plugins"
            });
            SceneJS.createScene({
                canvasId : "webgl-sprint",
                nodes:[ {
                    type:"cameras/orbit",
                    yaw:40,
                    pitch:-20,
                    zoom:10,
                    zoomSensitivity:10.0,
                    eye:{ x:0, y:0, z:10 },
                    look:{ x:0, y:0, z:0 },
                    nodes : [ {
                        type : "material",
                        color : {
                            r : 0.5,
                            g : 0.5,
                            b : 0.5
                        },
                        nodes : [ {
                            type : "geometry",
                            source : {
                                type : "sphere",
                                latitudeBands : 30,
                                longitudeBands : 30,
                                radius : 1
                            }
                        } ]
                    }, {
                        type : "material",
                        color : {
                            r : 1.0,
                            g : 0.0,
                            b : 0.0
                        },
                        nodes : [ {
                            type : "geometry",
                            primitive : "lines",
                            positions : [ 0.0, 0.0, 3.0, 0.0, 0.0, -2.0],
                            indices : [ 0, 1 ]
                        } ]
                    }, {
                        type : "material",
                        color : {
                            r : 0.0,
                            g : 1.0,
                            b : 0.0
                        },
                        nodes : [ {
                            type : "geometry",
                            primitive : "lines",
                            positions : [ 3.0, 0.0, 0.0, -2.0, 0.0, 0.0],
                            indices : [ 0, 1 ]
                        } ]
                    }, {
                        type : "material",
                        color : {
                            r : 0.0,
                            g : 0.0,
                            b : 1.0
                        },
                        nodes : [ {
                            type : "geometry",
                            primitive : "lines",
                            positions : [ 0.0, 3.0, 0.0, 0.0, -2.0, 0.0],
                            indices : [ 0, 1 ]
                        } ]
                    } ]
                } ]
            });
        </script>
    </body>

</html>
