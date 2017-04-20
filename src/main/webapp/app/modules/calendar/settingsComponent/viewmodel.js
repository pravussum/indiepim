define([], function () {

    var ViewModel = function (moduleContext) {

        var self = this;

        self.id = ko.observable();
        self.name = ko.observable();
        self.defaultCalendar = ko.observable();
        self.color = ko.observable("#aaaaaa");
        self.syncUrl = ko.observable();
        self.syncPrincipalPath = ko.observable();
        self.userName = ko.observable();
        self.password = ko.observable();

        self.colorChanged = function(hsb, hex, rgb, el, bySetColor) {
            self.color("#" + hex);
            $("#calendarColor").colpickHide();
        };

        self.loadCalendar = function(calendarId) {
            $.getJSON(
                "command/getCalendar/" + calendarId,
                function(data) {
                    self.id(data.id);
                    self.name(data.name);
                    self.defaultCalendar(data.defaultCalendar);
                    self.color(data.color);
                    self.syncUrl(data.syncUrl);
                    self.syncPrincipalPath(data.syncPrincipalPath);
                    self.userName(data.userName);
                    self.password(data.password);
                    $("#calendarColor").colpickSetColor(data.color);
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
            });
        }

        self.clearCalendar = function() {
            self.id(undefined);
            self.name("New Calendar");
            self.defaultCalendar(false);
            self.color("#3289c7");
            self.syncUrl(undefined);
            self.syncPrincipalPath(undefined);
            self.userName(undefined);
            self.password(undefined);
        }

        self.removeCalendar = function() {
            $.getJSON(
                "command/deleteCalendar/" + self.id(),
                function(data) {
                    if(data) {
                        toastr.info("Calendar removed successfully.");
                        // TODO leave the form (go to calendar?)
                    }
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
            });
        };
        self.saveCalendar = function() {
            var requestData = {
                id: self.id(),
                name: self.name(),
                defaultCalendar: self.defaultCalendar(),
                color: self.color(),
                syncUrl: self.syncUrl(),
                syncPrincipalPath: self.syncPrincipalPath(),
                userName: self.userName(),
                password: self.password()
            };
            $.ajax(
                "command/createOrUpdateCalendar",
                {
                    data:JSON.stringify(requestData),
                    type: "post",
                    success : function(data) {
                        toastr.info("Calendar with id " + data + " successfully saved.");
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

        self.syncCalendar = function() {
            $.getJSON(
                "command/syncCalendar/" + self.id(),
                function(result) {
                    toastr.message("Calendar sync fired successfully.");
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
                });
        };
    };
    
    return ViewModel;
});
