define([], function () {

    ko.fullCalendar = {
        // Defines a view model class you can use to populate a calendar
        viewModel: function(configuration) {
            this.events = configuration.events;
            this.options = configuration.options;
            this.loadCallback = configuration.loadCallback;
            this.element;
            this.unselect = function() {
                console.log("unselect");
                if(this.element)
                    $(this.element).fullCalendar('unselect');
            };
            this.onEventChanged = configuration.eventChangedCallback;
            this.onEventClicked = configuration.eventClickCallback;
        }
    };

    var updatingFromVM = false;
    var updatingFromCal = false;

    // The "fullCalendar" binding
    ko.bindingHandlers.fullCalendar = {
        // This method is called to initialize the node, and will also be called again if you change what the grid is bound to
        init: function(element, viewModelAccessor, allBindings, data, bindingContext) {
            element.innerHTML = "";
            var viewModel = viewModelAccessor();
            viewModel.element = element;
            var options = ko.utils.unwrapObservable(viewModel.options);

            options.events = function(start, end, timezone, callback) {
                function registerSubscriptions() {
                    console.log("registering vm event subscriptions");
                    for (var i = 0; i < viewModel.events().length; i++) {
                        var vmEvent = viewModel.events()[i];
                        console.log("subscribing for updates from event " + vmEvent.id());
                        vmEvent.title.subscribe(function (newVal) {getCalEvent4VmEvent(vmEvent, function(calEvent, vmEvent){
                            console.log("updating title for event " + vmEvent.id());
                            calEvent.title = newVal;
                            $(element).fullCalendar('updateEvent', calEvent);
                        })});
                        vmEvent.color.subscribe(function (newVal) {getCalEvent4VmEvent(vmEvent, function(calEvent, vmEvent){
                            calEvent.color = newVal;
                            $(element).fullCalendar('updateEvent', calEvent);
                        })});
                        vmEvent.start.subscribe(function (newVal) {getCalEvent4VmEvent(vmEvent, function(calEvent, vmEvent){
                            calEvent.start = newVal;
                            $(element).fullCalendar('updateEvent', calEvent);
                        })});
                        vmEvent.end.subscribe(function (newVal) {getCalEvent4VmEvent(vmEvent, function(calEvent, vmEvent){
                            calEvent.end = newVal;
                            $(element).fullCalendar('updateEvent', calEvent);
                        })});
                    }
                }

                if(updatingFromVM) {
                    console.log("ko.init.fc.events(): Updating from VM - NOT firing VM update");
                    var unwrappedArray = ko.toJS(viewModel.events);
                    console.log("ko.init.fc.events(): updating cal with " + unwrappedArray.length + " events");
                    // register for event changes
                    registerSubscriptions();
                    callback(unwrappedArray);
                    return;
                } else {
                    console.log("ko.init.fc.events(): updatingFromCal = true");
                    updatingFromCal = true;
                    console.log("ko.init.fc.events(): give viewmodel chance to update");
                    // TODO give the viewmodel a chance to update itself from the backend first
                    viewModel.loadCallback(start, end, function() {
                        console.log("ko.init.fc.events()/loadCallback.callback: vm update finished, updating fullCalendar from from viewmodel events");
                        var unwrappedArray = ko.toJS(viewModel.events);
                        console.log("ko.init.fc.events()/loadCallback.callback: updating cal with " + unwrappedArray.length + " events");
                        registerSubscriptions();
                        callback(unwrappedArray);
                        console.log("ko.init.fc.events()/loadCallback.callback: updatingFromCal = false");
                        updatingFromCal = false;
                    });
                }
            };

            function getCalEvent4VmEvent(vmEvent, updateCallback) {
                console.log("getCalEvent4VmEvent");
                var calendarEvents = $(element).fullCalendar('clientEvents');
                for(var i=0; i<calendarEvents.length; i++) {
                    if(vmEvent.id() == calendarEvents[i].id) {
                        updateCallback(calendarEvents[i], vmEvent);
                        return;
                    }
                }
            };

            // TODO make function nullsafe
            // TODO handle all day events
            function updateVMEventTimes(calEvent) {
                for (var i = 0; i < viewModel.events().length; i++) {
                    if (viewModel.events()[i].id() === calEvent.id) {
                        console.log("updateVMEventTimes(): Updating view model event with id " + calEvent.id);
                        viewModel.events()[i].start(calEvent.start);
                        viewModel.events()[i].end(calEvent.end);
                        viewModel.onEventChanged(viewModel.events()[i]);
                        return;
                    }
                }
                console.log("updateVMEventTimes(): View model event with id " + calEvent.id + " not found to update.");
            };

            options.eventResize = function( calEvent, revertFunc, jsEvent, ui, view ){
                console.log("ko.init.fc.eventResize: updatingFromCal = true");
                updatingFromCal = true;
                // find the viewmodel event and update it
                updateVMEventTimes(calEvent);
                console.log("ko.init.fc.eventResize: updatingFromCal = false");
                updatingFromCal = false;
            };
            options.eventDrop = function( calEvent, revertFunc, jsEvent, ui, view ) {
                console.log("ko.init.fc.eventDrop: updatingFromCal = true");
                updatingFromCal = true;
                updateVMEventTimes(calEvent);
                console.log("ko.init.fc.eventDrop: updatingFromCal = false");
                updatingFromCal = false;
            };
            options.eventClick = function(calEvent, jsEvent, view) {
                for (var i = 0; i < viewModel.events().length; i++) {
                    if (viewModel.events()[i].id() === calEvent.id) {
                        viewModel.onEventClicked(viewModel.events()[i]);
                        return;
                    }
                }
            };
            console.log("ko.init: initially creating fc");

//            viewModelAccessor.subscribe(function(newValue){
//                console.log("VM updated with new value: " +newValue);
//            });


            viewModel.events.subscribe(function(newValue) {
                console.log("ko.update: entering");
                if(updatingFromCal) {
                    console.log("ko.update: request to update came from calendar. Not updating cal from view model.");
                    return;
                }
                console.log("ko.update: updatingFromVM = true");
                updatingFromVM = true;
                console.log("ko.update: viewmodel updated - triggering event refetch on fullcalendar");
                $(element).fullCalendar('refetchEvents');
                console.log("ko.update: updatingFromVM = false");
                updatingFromVM = false;
            });

            $(element).fullCalendar(options);
        },

        updateInt: function(element, viewModelAccessor) {
            console.log("ko.update original")
        }
    };
});