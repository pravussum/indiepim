define([], function () {

    var chatWindow = function(id, userId, userName) {
        var self = this;
        self.userId = userId;
        self.id = id;
        self.userName = userName;
        self.sendMessage = function(message) {
            $("#" + self.id).chatbox("option", "boxManager").addMsg(self.userName, message);
        };
    }

    var ViewModel = function (moduleContext) {
        var self = this;

        self.chatWindows = ko.observableArray();
        self.addChat = function(userId, userName) {
            var existingWindow = self.getChatForUserId(userId);
            if(existingWindow) {
                $("#" + existingWindow.id).chatbox("option", "boxManager").showBox();
                return;
            }
            var newWindow = new chatWindow("chatWindow_" + self.chatWindows().length, userId, userName);
            self.chatWindows.push(newWindow);
            $("<div id='" + newWindow.id + "'/>").appendTo(document.body).chatbox({
                id: newWindow.id,
                title: "Chat with " + userName,
                offset: 300 * (self.chatWindows.length),
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
