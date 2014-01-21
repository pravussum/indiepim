/*
 * Definition of the base module. Base module contain some common components some one may use in
 * creating own application. These components are not a core part of BoilerplateJS, but available as samples.
 */
define(function(require) {

    // Load the dependencies
    var Boiler = require('Boiler'), 
		HeaderComponent = require('./headerComponent/component'),
		NavPanelComponent = require('./navPanelComponent/component');

    // Definition of the base Module as an object, this is the return value of this AMD script
    return {

        initialize : function(parentContext) {
            //create module context by assiciating with the parent context
            var context = new Boiler.Context(parentContext);

            $('#page-content').layout({
                north__initClosed: true,
                north__togglerLength_closed: '100%',
                north__size: 25,
                west__onresize: $.layout.callbacks.resizePaneAccordions
            });

            //scoped DomController that will be effective only on $('#page-content')
            var controller = new Boiler.DomController($('#page-content'));
            //add routes with DOM node selector queries and relevant components
            controller.addRoutes({
                "#navpanel" : new NavPanelComponent(context),
                "#header" : new HeaderComponent(context)
            });
            controller.start();

            // initialize comet system (server "push")
            var cometSetup = function() {
                $.ajax("command/getCometMessages")
                    .done(function(data){
                        if(data.isHeartbeat) {
                            toastr.info("Comet Heartbeat...");
                        } else if (data.isError) {
                            toastr.error("Commet Error " + data.errorMessage);
                        } else {
                            for(i=0; i<data.cometMessages.length; i++) {
                                if(data.cometMessages[i].messageType == "AccountSyncProgress") {
                                    context.notify("ACCOUNT_SYNC_PROGRESS", data.cometMessages[i]);
                                } else if(data.cometMessages[i].messageType == "AccountSynced") {
                                    context.notify("ACCOUNT_SYNCED", data.cometMessages[i]);
                                } else if(data.cometMessages[i].messageType == "NewMessage") {
                                    context.notify("NEW_MESSAGE", data.cometMessages[i]);
                                } else if(data.cometMessages[i].messageType == "NewChatMessage") {
                                    context.notify("NEW_CHAT_MESSAGE", data.cometMessages[i]);
                                } else if(data.cometMessages[i].messageType == "UserOnlineStateMessage") {
                                    if(data.cometMessages[i].online)
                                        context.notify("USER_ONLINE", data.cometMessages[i]);
                                    else
                                        context.notify("USER_OFFLINE", data.cometMessages[i]);
                                }
                            }
                        }
                        cometSetup();
                    })
                    .fail(function(error, textStatus, errorThrown) {
                        if(error.status == 403) {
                            // access denied = not logged in --> redirect to login
                            window.location.href = contextPath + "/login";
                        } else {
                            toastr.error("An error occured during comet connection setup: " + errorThrown);
                            // TODO retry later
                        }
                    });
            };
            cometSetup();


            //the landing page should respond to the root URL, so let's use an URLController too
            // var controller = new Boiler.UrlController($("#content"));
            // controller.addRoutes({
                // "/" : new LandingPageComponent(context)
            // });
            // controller.start();
	

        }
        
    }

});