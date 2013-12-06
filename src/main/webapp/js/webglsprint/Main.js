Ext.define('webglsprint.Main', {
    singleton: true
}, function() {
    /**
     * Main application launch function. To be called after page has finished loading.
     */
    webglsprint.Main.launch = function() {
        //Create a framerate tracker, throw it in the top right
        var stats = new Stats();
        stats.setMode(0); // 0: fps, 1: ms
        stats.domElement.style.position = 'absolute';
        stats.domElement.style.right = '0px';
        stats.domElement.style.top = '0px';
        document.body.appendChild( stats.domElement );

        var projector = new THREE.Projector();
        var raycaster = new THREE.Raycaster();

        //Make our request for a lot of boreholes...
        var maxBoreholes = 1000;
        var boreholeId = undefined; //'boreholes.5271';
        webglsprint.boreholes.Loader.getBoreholes(maxBoreholes, boreholeId, function(success, message, boreholes) {
            if (!success) {
                alert('ERROR: ' + message);
                return;
            }

            Ext.get('loading-text').remove();
            alert('Rendering ' + boreholes.length + ' boreholes now.'
                    + '\nTo look around, hold down the mouse and drag.'
                    + '\nTo move the camera, use the WASD keys.'
                    + '\nTo query a borehole, use mouse right-click.');
            if (!boreholes.length) {
                return;
            }

            //Just pull a reference the <canvas> element, set it to "full size" in
            //the browser window
            var canvasEl = Ext.get('webgl-sprint-canvas').dom;
            canvasEl.width = window.innerWidth;
            canvasEl.height = window.innerHeight;

            //Create the basic ThreeJS elements
            //See also: http://threejs.org/docs/#Manual/Introduction/Creating_a_scene
            var scene = new THREE.Scene();
            var camera = new THREE.PerspectiveCamera(75, canvasEl.width / canvasEl.height, 0.1, 10000);
            var renderer = new THREE.WebGLRenderer({
                canvas : Ext.get('webgl-sprint-canvas').dom
            });

            //We need to figure out the mean x,y,z values in order to point our camera
            //at something interesting
            var mean = {
                xTotal : 0,
                yTotal : 0,
                zTotal : 0,
                n : 0
            };


            var material = new THREE.MeshLambertMaterial({
            	needsUpdate: true,
                map: THREE.ImageUtils.loadTexture('getBoreholesImage.do?logId=fae8f90d-2015-4200-908a-b30da787f01&depth=20')
              });


            //Iterate over our JSON representation of N boreholes
            for (i = 0; i < boreholes.length; ++i) {
                var points = [];

                for (j = 0; j < boreholes[i].points.length; ++j) {
                    var point = boreholes[i].points[j];

                    mean.xTotal += point.x;
                    mean.yTotal += point.y;
                    mean.zTotal += point.z;
                    mean.n++;

                    points.push(new THREE.Vector3(point.x, point.y, point.z));
                }

                //The tube geometry will divide the points up into a set of n segments
                //with a specified number of points on the radius.
                var segments = Math.ceil(Math.log(points.length) / Math.LN2); //lets decimate according to an arbitrary scale
                var radius = 5;
                var thickness = 0.1; //This doesn't increase/decrease polygons. Just their scale

                var path = new THREE.SplineCurve3(points);
                var geometry = new THREE.TubeGeometry(path, segments, 10, radius, false);
                //var material = new THREE.MeshBasicMaterial( { color: Math.floor(Math.random()*16777215), wireframe: true} );
                var tube = new THREE.Mesh(geometry, material);
                tube.borehole = boreholes[i];
                scene.add(tube);

                //VT: add capping to the ends of the borehole(I can't get the cap to fit nicely
                var caps = new THREE.Mesh( new THREE.SphereGeometry(10), material );
                caps.position.set( points[0].x, points[0].y, points[0].z );
                caps.borehole=boreholes[i];
				scene.add( caps );


            }


            var light = new THREE.AmbientLight( 0xa0a0a0 ); // soft white light
            scene.add( light );


            var directionalLight = new THREE.DirectionalLight( 0xa0a0a0, 1 );
            directionalLight.position.set( 0, 1, 0 );
            scene.add( directionalLight );


            mean.x = mean.xTotal / mean.n;
            mean.y = mean.yTotal / mean.n;
            mean.z = mean.zTotal / mean.n;

            //Just plonk our camera right in the middle of the geometries
            //hopefully it looks roughly in the right direction
            camera.position.x = mean.x;
            camera.position.y = mean.y;
            camera.position.z = mean.z;

            // This is a plugin that makes the camera interactive
            // The user can look with mouse and pan with WASD
            var controls = new THREE.FlyControls( camera );
            controls.movementSpeed = 10;
            controls.domElement = Ext.get('viewport').dom;
            controls.rollSpeed = 0.01;
            controls.autoForward = false;
            controls.dragToLook = true;

            // We need a function that repeatedly loops
            // Drawing updates to our scene based on the current
            // camera position
            var update = function() {
                requestAnimationFrame( update );

                stats.begin();

                controls.update(1);
                renderer.render(scene, camera);

                stats.end();
            };
            update();

            //This is in case the user resizes their browser window
            window.addEventListener('resize', onWindowResize, false );
            function onWindowResize( event ) {
                camera.aspect = window.innerWidth / window.innerHeight;
                renderer.setSize( window.innerWidth, window.innerHeight );
                renderer.render( scene, camera );
            };

            // mouse-right click handler
            Ext.get('webgl-sprint-canvas').dom.addEventListener('mousedown', onMouseDown, false);
            function onMouseDown(event) {
                if (event.button == 2) {
                    var x = (event.clientX / window.innerWidth) * 2 - 1;
                    var y = -(event.clientY / window.innerHeight) * 2 + 1;
                    var vector = new THREE.Vector3(x, y, 1);
                    projector.unprojectVector(vector, camera);
                    raycaster.set(camera.position, vector.sub(camera.position).normalize());
                    var intersects = raycaster.intersectObjects(scene.children);
                    if (intersects.length > 0) {
                        // interactive cubes demo and testing suggests first is nearest
                        var bh = intersects[0].object.borehole;
                        Ext.create('Ext.window.Window', {
                            title: bh.name,
                            height: 200,
                            width: 400,
                            layout: 'fit',
                            items: [{
                                xtype: 'panel',
                                html:  '<ul><li>totalDepth: '+bh.totalDepth+'</li><li>points: '+bh.points.length+'</li></ul>'
                            }]
                        }).show();
                    }
                }
            };

        });
    }
});