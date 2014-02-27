define([], function () {

    var User = function(userId, userName) {
        var self = this;
        self.id = ko.observable(userId);
        self.userName = ko.observable(userName);
    };

    var MessageAccount = function(data) {
        var self = this;
        self.id = ko.observable(data.id);
        self.accountName = ko.observable(data.accountName);
        self.tagHierarchy = new ko.ui.Tree({
            nodes : data.tagHierarchy.rootElement.nodes
        });

        self.url = ko.computed(function(){
            return '#maillist/account/' + self.id();
        });
        self.tagHierarchy.subscribe("click", function(event) {
            if(event.node.id) {
                window.location.href = "#maillist/taglineage/" + event.node.id;
            }
        });
        self.syncAccount = function() {
            $.getJSON(
                "command/syncMessageAccount/" + self.id(),
                function(result) {

                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
                });
        }
    };

    var Calendar = function(data) {
        var self = this;
        self.id = data.id;
        self.name = data.name;
        self.defaultCalendar = data.defaultCalendar;
        self.color = data.color;
        self.syncUrl = data.syncUrl; // TODO only show refresh button when syncUrl is set
        self.calendarSettings = function() {
            window.location.href = "#calendarsettings/" + self.id;
        }
    }

    var ViewModel = function (moduleContext) {

        var self = this;

        self.messageAccounts = ko.observableArray();
        self.calendars = ko.observableArray();
        self.users = ko.observableArray();
        self.selectedAccount = ko.observable();
        self.selectedCalendar = ko.observable();

        self.selectAccount = function(account) {
            self.selectedAccount(account);
        };
        self.selectCalendar = function(calendar) {
            self.selectedCalendar(calendar);
        }

        var initTree = function() {
            $.getJSON("command/getMessageAccounts",
                function(data) {
                    if(typeof data.error != "undefined") {
                        toastr.error("Receiving the account list failed. See server log for details.");
                    } else {
                        self.messageAccounts($.map(data.accounts, function(item) {return new MessageAccount(item)}));
                    }
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                if(xhr.status == 403){
                    window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                }
                else
                    toastr.error(JSON.stringify(textStatus));
            });
        };
        initTree();

        var initCalendarList = function() {
            $.getJSON("command/getCalendars",
                function(data) {
                    if(typeof data.error != "undefined") {
                        toastr.error("Receiving the calendars list failed. See server log for details.");
                    } else {
                        self.calendars($.map(data, function(item) {return new Calendar(item)}));
                    }
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403){
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    }
                    else
                        toastr.error(JSON.stringify(textStatus));
                });

        }
        initCalendarList();

        // init user list 4 chat
        var initUsers = function() {
            $.getJSON("command/getUsers",
                $.param({onlineOnly: true}),
                function(data) {
                    var mappedUsers = $.map(data, function(item) {return new User(item.id, item.userName)});
                    self.users(mappedUsers);
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                if(xhr.status == 403) {
                    window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                }
                else
                    toastr.error(JSON.stringify(textStatus));
            });
        };
        initUsers();

        moduleContext.listen("USER_ONLINE", function(data) {
            self.users.push(new User(data.userId, data.userName));
        });
        moduleContext.listen("USER_OFFLINE", function(data) {
            // TODO check knockout docs: is this correct? got no internet to confirm right now...
            self.users.remove(function(item) {
                if(item.id == data.id)
                    return true;
            });
        });

        self.openChat = function(user) {
            moduleContext.notify("OPEN_CHAT", {userId: user.id(), userName: user.userName()});
        }

    };
    
    return ViewModel;
});
