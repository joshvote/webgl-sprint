Ext.define('webglsprint.boreholes.NVCLDetailsHandler', {
    singleton: true
}, function() {
    /**
     * Shows the NVCL dataset display window for the given dataset (belonging to the selected known layer feature)
     *
     * Note - AUS-2055 brought about the removal of the requirement for an open proxy - work still needs to be done
     *        to break this into more manageable pieces because the code is still very much a copy from the original source.
     */
    webglsprint.boreholes.NVCLDetailsHandler.showDetailsWindow = function(datasetId, datasetName, nvclDataServiceUrl, featureId) {

        //We create an instance of our popup window but don't show it immediately
        //We need to dynamically add to its contents
        var win = Ext.create('Ext.Window', {
            border      : true,
            layout      : 'fit',
            resizable   : false,
            modal       : true,
            plain       : false,
            title       : 'Borehole Id: ' + datasetName,
            height      : 600,
            width       : 820,
            items:[{
                xtype           : 'tabpanel',
                activeItem      : 0,
                enableTabScroll : true,
                buttonAlign     : 'center',
                items           : []
            }]
        });
        var tp = win.items.getAt(0); // store a reference to our tab panel for easy access

        //This store is for holding log info
        var logStore = Ext.create('Ext.data.Store', {
            model : 'auscope.knownlayer.nvcl.Log',
            proxy : {
                type : 'ajax',
                url : 'getNVCLLogs.do',
                extraParams : {
                    serviceUrl : nvclDataServiceUrl,
                    datasetId : datasetId,
                    mosaicService : true
                },
                reader : {
                    type : 'json',
                    root : 'data',
                    successProperty : 'success',
                    messageProperty : 'msg'
                }
            }
        });

        //Load our log store, populate our window when it finishes loading
        logStore.load({
            callback : function(recs, options, success) {
                //Find our 'mosaic' and 'imagery' record
                var mosaicRecord = null;
                var imageryRecord = null;
                for (var i = 0; i < recs.length; i++) {
                    switch(recs[i].get('logName')) {
                    case 'Mosaic':
                        mosaicRecord = recs[i];
                        break;
                    case 'Imagery':
                        imageryRecord = recs[i];
                        break;
                    }
                }

                //Add our mosaic tab (if available)
                if (mosaicRecord !== null) {
                    tp.add({
                        title : ' Mosaic ',
                        layout : 'fit',
                        html: '<iframe id="nav" style="overflow:auto;width:100%;height:100%;" frameborder="0" src="' +
                              'getNVCLMosaic.do?serviceUrl=' + escape(nvclDataServiceUrl) + '&logId=' + mosaicRecord.get('logId') +
                              '"></iframe>'
                    });
                }

                //Add our imagery tab (if available)
                if (imageryRecord !== null) {
                    var startSampleNo   = 0;
                    var endSampleNo     = 100;
                    var sampleIncrement = 100;
                    var totalCount      = imageryRecord.get('sampleCount');

                    //Navigation function for Imagery tab
                    var cardNav = function(incr) {

                        if ( startSampleNo >= 0 && endSampleNo >= sampleIncrement) {
                            startSampleNo = 1 * startSampleNo + incr;
                            endSampleNo = 1 * endSampleNo + incr;
                            Ext.getCmp('card-prev').setDisabled(startSampleNo < 1);
                            Ext.get('imageryFrame').dom.src = 'getNVCLMosaic.do?serviceUrl=' + escape(nvclDataServiceUrl) + '&logId=' + imageryRecord.get('logId') + '&startSampleNo=' + startSampleNo + '&endSampleNo=' + endSampleNo;

                            // Ext.fly ... does not work in IE7
                            //Ext.fly('display-count').update('Displayying Images: ' + startSampleNo + ' - ' + endSampleNo + ' of ' + totalCount);
                            Ext.getCmp('display-count').setText('Displaying Imagess: ' + startSampleNo + ' - ' + endSampleNo + ' of ' + totalCount);
                        }

                        Ext.getCmp('card-prev').setDisabled(startSampleNo <= 0);
                        Ext.getCmp('card-next').setDisabled(startSampleNo + sampleIncrement >= totalCount);
                    };

                    //Add the actual tab
                    tp.add({
                        title : ' Imagery ',
                        layout : 'fit',
                        html : '<iframe id="imageryFrame" style="overflow:auto;width:100%;height:100%;" frameborder="0" src="' +
                              'getNVCLMosaic.do?serviceUrl=' + escape(nvclDataServiceUrl) + '&logId=' + imageryRecord.get('logId') +
                              '&startSampleNo='+ startSampleNo +
                              '&endSampleNo=' + sampleIncrement +
                              '"></iframe>',
                        bbar: [{
                            id   : 'display-count',
                            text : 'Displaying Images: ' + startSampleNo + ' - ' + endSampleNo + ' of ' + totalCount
                        },
                        '->',
                        {
                            id  : 'card-prev',
                            text: '< Previous',
                            disabled: true,
                            handler: Ext.bind(cardNav, this, [-100])
                        },{
                            id  : 'card-next',
                            text: 'Next >',
                            handler: Ext.bind(cardNav, this, [100])
                        }]
                    });
                }

                //Add our scalars tab (this always exists
                var scalarGrid = Ext.create('Ext.grid.Panel', {
                    //This store will be loaded when this component renders
                    store : Ext.create('Ext.data.Store', {
                        autoLoad : true,
                        model : 'auscope.knownlayer.nvcl.Log',
                        proxy : {
                            type : 'ajax',
                            url : 'getNVCLLogs.do',
                            extraParams : {
                                serviceUrl : nvclDataServiceUrl,
                                datasetId : datasetId
                            },
                            reader : {
                                type : 'json',
                                root : 'data',
                                successProperty : 'success',
                                messageProperty : 'msg'
                            }
                        }
                    }),
                    selModel : Ext.create('Ext.selection.CheckboxModel', {}),
                    id : 'nvcl-scalar-grid',
                    loadMask : true,
                    plugins : [{
                        ptype: 'celltips'
                    }],
                    columns : [{
                        id: 'log-name-col',
                        header: 'Scalar',
                        flex: 1,
                        dataIndex: 'logName',
                        sortable: true,
                        hasTip : true,
                        tipRenderer : function(value, record, column, tip) {
                            //Load our vocab string asynchronously
                            var vocabsQuery = 'getScalar.do?repository=nvcl-scalars&label=' + escape(record.get('logName').replace(' ', '_'));
                            Ext.Ajax.request({
                                url : vocabsQuery,
                                success : function(pData, options) {
                                    var pResponseCode = pData.status;
                                    var updateTipText = function(tip, text) {
                                        tip.body.dom.innerHTML = text;
                                        tip.doLayout();
                                    };
                                    if(pResponseCode !== 200) {
                                        updateTipText(tip, 'ERROR: ' + pResponseCode);
                                        return;
                                    }

                                    var response = Ext.JSON.decode(pData.responseText);
                                    if (!response.success) {
                                        updateTipText(tip, 'ERROR: server returned error');
                                        return;
                                    }

                                    //Update tool tip
                                    if (response.data.definition && response.data.definition.length > 0) {
                                        updateTipText(tip, response.data.definition);
                                    } else if (response.data.scopeNote && response.data.scopeNote.length > 0) {
                                        updateTipText(tip, response.data.scopeNote);
                                    } else {
                                        updateTipText(tip, 'N/A');
                                    }
                                }
                           });
                           return 'Loading...';
                       }
                    }]
                });

                // Scalars Tab
                tp.add({
                    title : 'Scalars',
                    layout : 'fit',
                    border : false,
                    items : {
                        // Bounding form
                        id :'scalarsForm',
                        xtype :'form',
                        layout :'column',
                        frame : true,

                        // these are applied to columns
                        defaults:{
                            columnWidth : 0.5,
                            layout      : 'anchor',
                            hideLabels  : true,
                            border      : false,
                            bodyStyle   : 'padding:10px',
                            labelWidth  : 100
                        },

                        // Columns
                        items:[{ // column 1
                            // these are applied to fieldsets

                            // fieldsets
                            items:[{
                                xtype       : 'fieldset',
                                title       : 'List of Scalars',
                                anchor      : '100%',
                                //autoHeight  : true,
                                height      : 500,

                                // these are applied to fields
                                defaults    : {anchor:'-5', allowBlank:false},

                                // fields
                                layout      : 'fit',
                                items       : [scalarGrid]
                            }]
                        },{ // column 2
                            // these are applied to fieldsets
                            defaults:{
                                xtype: 'fieldset',
                                layout: 'form',
                                anchor: '100%',
                                autoHeight: true,
                                paddingRight: '10px'
                            },

                            // fieldsets
                            items:[{
                                title       : 'Hint',
                                defaultType : 'textfield',
                                // fields
                                items:[{
                                    xtype  : 'box',
                                    id     : 'scalarFormHint',
                                    autoEl : {
                                        tag  : 'div',
                                        html : 'Select a scalar(s) from the "Scalar List" table on the left and then click "Plot" button.<br><br>Leave the default depth values for the entire depth.'
                                    }
                                }]
                            },{
                                title       : 'Options',
                                defaultType : 'textfield',

                                // these are applied to fields
                                //,defaults:{anchor:'-20', allowBlank:false}
                                bodyStyle   : 'padding:0 0 0 45px',

                                // fields
                                items:[{
                                    xtype       : 'numberfield',
                                    fieldLabel  : 'Start Depth (m)',
                                    name        : 'startDepth',
                                    minValue    : 0,
                                    value       : 0,
                                    accelerate  : true
                                },{
                                    xtype       : 'numberfield',
                                    fieldLabel  : 'End Depth (m)',
                                    name        : 'endDepth',
                                    minValue    : 0,
                                    value       : 99999,
                                    accelerate  : true
                                },{
                                    xtype                   : 'numberfield',
                                    fieldLabel              : 'Interval (m)',
                                    name                    : 'samplingInterval',
                                    minValue                : 0,
                                    value                   : 1.0,
                                    allowDecimals           : true,
                                    decimalPrecision        : 1,
                                    step                    : 0.1,
                                    //alternateIncrementValue : 2.1,
                                    accelerate              : true
                                }]
                            },{
                                xtype       : 'fieldset',
                                title       : 'Graph Types',
                                autoHeight  : true,
                                items       :[{
                                    xtype   : 'radiogroup',
                                    id      : 'ts1',
                                    columns : 1,
                                    items   : [
                                        {boxLabel: 'Stacked Bar Chart', name: 'graphType', inputValue: 1, checked: true},
                                        {boxLabel: 'Scattered Chart', name: 'graphType', inputValue: 2},
                                        {boxLabel: 'Line Chart', name: 'graphType', inputValue: 3}
                                    ]
                                }]

                            }],
                            buttons:[{
                                text: 'Plot',
                                handler: function() {
                                    var sHtml = '';
                                    var item_count = scalarGrid.getSelectionModel().getCount();
                                    var scalarForm = Ext.getCmp('scalarsForm').getForm();
                                    var width = 300;
                                    var height = 600;

                                    if (item_count > 0) {
                                        var s = scalarGrid.getSelectionModel().getSelection();
                                        for(var i = 0, len = s.length; i < len; i++){
                                            sHtml +='<img src="';
                                            sHtml += 'getNVCLPlotScalar.do?logId=';
                                            sHtml += s[i].get('logId');
                                            sHtml += '&' + scalarForm.getValues(true);
                                            sHtml += '&width=' + width;
                                            sHtml += '&height=' + height;
                                            sHtml += '&serviceUrl=';
                                            sHtml += escape(nvclDataServiceUrl);
                                            sHtml += '" ';
                                            sHtml += 'onload="Ext.getCmp(\'plWindow\').doLayout();"';
                                            sHtml += '/>';
                                        }

                                        var winPlot = Ext.create('Ext.Window', {
                                            autoScroll  : true,
                                            border      : true,
                                            html        : sHtml,
                                            id          : 'plWindow',
                                            layout      : 'fit',
                                            maximizable : true,
                                            modal       : true,
                                            title       : 'Plot: ',
                                            autoHeight  : true,
                                            autoWidth   : true,
                                            x           : 10,
                                            y           : 10
                                          });

                                        winPlot.show();
                                    } else if (item_count === 0){
                                        Ext.Msg.show({
                                            title:'Hint',
                                            msg: 'You need to select a scalar(s) from the "List of Scalars" table to plot.',
                                            buttons: Ext.Msg.OK
                                        });
                                    }
                                }
                            }]
                        }]
                    }
                });

                if (mosaicRecord !== null || imageryRecord !== null) {
                    win.show();
                    win.center();
                } else {
                    Ext.MessageBox.alert('Info', 'Selected dataset is empty!');
                }
            }
        });
    };
});