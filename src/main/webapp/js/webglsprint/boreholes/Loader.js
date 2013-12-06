


/**
 * A friendly wrapper around an Ext.Ajax request to the GetBoreholes controller
 */
Ext.define('webglsprint.boreholes.Loader', {
    singleton: true
}, function() {
    /**
     *  @param maxBoreholes : [optional] Number - Limit on number of boreholes to request. Default is unboundd
     *  @param boreholeId : [optional] String - Unique ID of a borehole to request
     *  @param handler : function(success, message, boreholes) - Callback function
     */
    webglsprint.boreholes.Loader.getBoreholes = function(maxBoreholes, boreholeId, handler) {
        var params = {};
        if (maxBoreholes) {
            params.maxFeatures = maxBoreholes;
        }
        if (boreholeId) {
            params.featureId = boreholeId;
        }
       
        Ext.Ajax.request({
            url: 'getBoreholes.do',
            params: params,
            callback: function (options, success, response) {
                //This will occur if the backend rejects our AJAX request
                if (!success) {
                    handler(false, 'Couldnt hit backend', []);
                    return;
                }

                //Although we have a response, the remote service may still have
                //returned failure
                var responseJsonObject = Ext.JSON.decode(response.responseText);
                if (!responseJsonObject.success) {
                    handler(false, "Unsuccesful data retrieval.", []);
                    return;
                }
                
                //Pass on success
                handler(true, '', responseJsonObject.data);
            }
        });
    }
});