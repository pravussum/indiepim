define([], function () {

    var User = function(data) {
        var self = this;
        self.id = ko.observable(data.id);
        self.userName = ko.observable(data.userName);
        self.isAdmin = ko.observable(data.isAdmin);
        self.password = ko.observable(undefined);
    };

    var ViewModel = function (moduleContext) {

        var self = this;
        //Implement the viewmodel here
        self.users = ko.observableArray();
        self.selectedUser = ko.observable();

        self.selectUser = function(user) {
            self.selectedUser(user);
        };

        self.addUser = function() {
            self.users.push(new User({userName : "New User"}));
        }

        self.removeUser = function(user) {
            self.selectedUser(undefined);
            self.users.remove(user);
            // TODO implement user removal
        }

        self.saveUser = function(user) {
            var url = "command/createOrUpdateUser";
            $.ajax(url, {
                data: ko.toJSON(user),
                type: "post",
                contentType: "application/json",
                success : function(result) {
                    if(typeof result.error != "undefined")
                        toastr.error("Saving user failed. See server log for details." + JSON.stringify(result.error));
                    else {
                        if(typeof result.id === 'undefined' || result.id == null) {
                            toastr.error("No valid user id received.");
                        } else {
                            user.id = result.id;
                            toastr.success("User saved successfully.");
                        }
                    }
                },
                error : function(error, textStatus, errorThrown) {
                    if(error.status == 403)
                        window.location.href = contextPath + "/login";
                    else
                        toastr.error("Saving user failed. See server log for details.");
                }
            });
        }

        var initUsers = function() {
            var url = "command/getUsers";
            $.getJSON(url, function(data) {
                    if(typeof data.error != "undefined") {
                        toastr.error("Receiving the user list failed. See server log for details.");
                    } else {
                        var mappedUsers = $.map(data, function(item) {return new User(item)});
                        self.users(mappedUsers);
                    }
                }
            ).error(function (error) {
                    if(error.status == 403)
                        window.location.href = contextPath + "/login";
                    else
                        toastr.error("Receiving the user list failed. See server log for details.");
                });
        }
        initUsers();

    };
    
    return ViewModel;
});
