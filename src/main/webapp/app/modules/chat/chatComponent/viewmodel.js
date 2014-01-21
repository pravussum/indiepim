define([], function () {

    var chatWindow = function(id, userId, userName) {
        var self = this;
        self.userId = userId;
        self.id = id;
        self.userName = userName;
        self.windowHtml = ko.computed(function() {
            return "<div id='" + self.id + "'></div>";
        });
        self.sendMessage = function(message) {
            $("#" + self.id).chatbox("option", "boxManager").addMsg(self.userName, message);
        };
    }

    var ViewModel = function (moduleContext) {
        var self = this;
        self.count = ko.observable(0);

        self.chatWindows = ko.observableArray();
        self.addChat = function(userId, userName) {
            if(self.getChatForUserId(userId))
                return;
            self.count(self.count() + 1);
            var newWindow = new chatWindow("chatWindow_" + self.count(), userId, userName);
            self.chatWindows.push(newWindow);
            $("#" + newWindow.id).chatbox({
                id: newWindow.id,
                title: "Chat with " + userName,
                offset: 300 * (self.count() - 1),
                boxClosed: function() {
                    self.chatWindows.remove(newWindow);
                },
                messageSent: function(id, user, msg) {
                    $.ajax(
                        "command/sendChatMessage/" + newWindow.userId,
                        {
                            data: msg,
                            type: "post",
                            contentType: "text/plain",
                            success: function(data) {
                                $("#" + newWindow.id).chatbox("option", "boxManager").addMsg("Me", msg);
                            }
                        }
                    ).fail(function(xhr, textStatus, errorThrown) {
                            if(xhr.status == 403)
                                window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                            else
                                toastr.error(JSON.stringify(textStatus));
                    });
                }
            });
        }

        self.sendMessage = function(userId, message) {
            var chatWindow = self.getChatForUserId(userId);
            chatWindow.sendMessage(message);
        }

        self.getChatForUserId = function(userId) {
            for(i=0; i<self.chatWindows().length; i++) {
                if(self.chatWindows()[i].userId == userId) {
                    return self.chatWindows()[i];
                }
            }
            return null;
        }
    };

    return ViewModel;
});
