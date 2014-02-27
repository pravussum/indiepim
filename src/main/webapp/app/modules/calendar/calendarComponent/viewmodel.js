define([], function () {

    var Event = function(data) {
        var self = this;
        self.id = ko.observable(data.id);
        self.start = ko.observable(data.start);
        self.end = ko.observable(data.end);
        self.color = ko.observable(data.color);
        self.description = ko.observable(data.description);
        self.title = ko.observable(data.title);
        self.allDay = ko.observable(data.allDay);
        self.url = ko.observable(data.url);
        self.location = ko.observable(data.location);
        self.calendarId = ko.observable(data.calendarId);
    }

    var ViewModel = function (moduleContext) {

        var self = this;

        self.viewData = ko.observable(Date.now());
        self.events = ko.observableArray();
        self.currentEvent = ko.observable();

        self.load = function(start, end, callback) {
            console.log("vm.load callback " + start  + ", " + end);
            $.getJSON(
                "command/getEvents",
                {
                    start: start.valueOf(),
                    end: end.valueOf()
                },
                function(data){
                    self.events($.map(data, function(item,index) {
                        return new Event(item);
                    }));
                    console.log("vm.load.success: updated vm with " + self.events().length + " events.");
                    callback();
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
            });
        };

        self.select = function( start, end, jsEvent, view ) {
            self.calendarViewModel.unselect();
            var newEvent = new Event({
                title: "Tolles Event",
                start: start,
                end: end
            });
            self.events.push(newEvent);
            // TODO open edit dialog
            self.calEventChanged(newEvent);
        }

        self.calEventChanged = function(event) {
            var requestData = {
                id: event.id(),
                title: event.title(),
                start: event.start().valueOf(),
                end: event.end().valueOf(),
                allDay: event.allDay(),
                color: event.color(),
                url: event.url(),
                description: event.description(),
                location: event.location(),
                calendarId: event.calendarId()
            };
            $.ajax(
                "command/createOrUpdateEvent",
                {
                    data:JSON.stringify(requestData),
                    type: "post",
                    success : function(data) {
                        toastr.info("Event with id " + data + " successfully saved.");
                    },
                    contentType:"application/json; charset=UTF-8"
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
            });
        };

        self.calEventClicked = function(event) {
            self.currentEvent(event);
            $(".eventEditorDialog").dialog({
                autoOpen:false,
                height: 400,
                width: 500,
                modal:true
            });
            $(".eventEditorDialog").dialog("open");
        }

        self.saveEvent = function() {
            console.log("save event.");
        };

        self.calendarViewModel = new ko.fullCalendar.viewModel({
            events: self.events,
            loadCallback: self.load,
            eventChangedCallback: self.calEventChanged,
            eventClickCallback: self.calEventClicked,
            options:{

                select : self.select,
                header:{
                    left:"today",
                    center:"prev, title, next",
                    right:"month, agendaWeek, agendaDay"
                },
                theme: false,
                // TODO make configurable!
                firstDay: 1,
                weekMode: "variable",
                aspectRatio: 2.6,
                selectable: true,
                selectHelper: true,
                editable: true,
                defaultView: "agendaWeek",
                lang:'de',
                timezone: "local",
                weekNumbers:true,
                height:function() {
                    return $("#content").innerHeight();
                }
            }
        });
    };
    
    return ViewModel;
});
