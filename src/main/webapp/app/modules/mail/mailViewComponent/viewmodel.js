define([], function () {

    function Message(data) {
        var self = this;
        self.id = data.id;
        self.subject = ko.observable(data.subject);
        self.tags = ko.observableArray(data.tags);
        self.sender = ko.observable(data.sender);
       	self.receiver = ko.observableArray(data.receiver);
       	self.cc = ko.observableArray(data.cc);
        self.bcc = ko.observableArray(data.bcc);
       	self.dateReceived = ko.observable(data.dateReceived);
       	self.contentHtml = ko.observable(data.contentHtml);
       	self.contentText = ko.observable(data.contentText);
       	self.accountId = ko.observable(data.accountId);
       	self.read = ko.observable(data.read);
       	self.hasAttachment = ko.observable(data.hasAttachment);
       	self.star = ko.observable(data.star);
        self.attachments = ko.observableArray(data.attachments);
    }

    var ViewModel = function (moduleContext) {

        var self = this;
        self.tagsReadOnly = true;
        self.messageId = ko.observable();
        self.message = ko.observable();

        self.editMessageTags = function(){
            self.tagsReadOnly = !self.tagsReadOnly;
            $("#editTagsButton i").removeClass(self.tagsReadOnly ? "fa-check" : "fa-pencil");
            $("#editTagsButton i").addClass(self.tagsReadOnly ? "fa-pencil" : "fa-check");
            $("#tagBox").select2("readonly", self.tagsReadOnly);
        };

        self.select2Options = {
            tags:true,
            minimumInputLength: 2,
            id: function(item) {
                return item.tag;
            },
            ajax : {
                url : "command/getTags",
                dataType : "json",
                quietMillis : 300,
                data : function(term, page) {           // Function to generate query parameters for the ajax request.
                    return {
                        query : term
                    }
                },
                results : function(data, page) {
                    return {
                        results: data
                    }
                }
            },
            formatResult : function(item) {
                return item.tag;
            },
            formatSelection : function(item, container) {
                container.parent().css("border", "none");
                if(item.color) {
                    container.parent().css("background-Color", "#" + item.color);
                }
                return item.tag;
            },
            createSearchChoice: function (term, data) {
                if ($(data).filter(function () {
                    return this.tag.localeCompare(term) === 0;
                }).length === 0) {
                    return {
                        id: term,
                        tag: term
                    };
                }
            },
            containerCssClass: "mailViewTagBox"
        };

        self.initialize = function(msgId) {
            self.messageId(msgId);
            $("#messageViewFrame").hide();

            if(typeof msgId === 'undefined') {
                toastr.error("Not a valid messag id!");
                return;
            }
            var url = "command/getMessage/" + msgId;
            $.getJSON(
                url,
                function(result) {
                    if(result.error) {
                        toastr.error(result.error);
                        return;
                    }
                    self.message(new Message(result));
                    self.tagsReadOnly = true;
                    $("#tagBox").select2("readonly", true);

                    var content;
                    if(typeof result.contentHtml !== 'undefined' && result.contentHtml != null) {
                        content = result.contentHtml;
                    } else if (typeof result.contentText !== 'undefined' && result.contentText != null) {
                        content = "<pre>" + result.contentText + "</pre>";
                    } else {
                        content = "The message contained no valid content.";
                    }
                    var doc = $("#messageIFrame")[0].contentWindow.document;
                    var $body = $('body', doc);
                    $("#messageViewFrame").fadeIn(600);
                    $body.html(content);
					/*.ready(function(){
                            $("#messageContent").height((($("#messageContent")[0].contentWindow.document.body.scrollHeight) + 60) + "px");
                            $("#messageContent").width((($("#messageContent")[0].contentWindow.document.body.scrollWidth) + 60) + "px");
                        });
                    $("#messageContent").height($body.scrollHeight); */
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login";
                    else
                        toastr.error(JSON.stringify(textStatus));
                }
            );

            self.markReadTimer = window.setTimeout(function(){
                moduleContext.notify("MESSAGE_READ", self.messageId());
                $.getJSON(
                    "command/markAsRead/" + self.messageId(),
                    function(data) {
                        // parse the result MessageDTO list and update list view?
                    }
                ).fail(function(xhr, textStatus, errorThrown) {
                        if(xhr.status == 403)
                            window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                        else
                            toastr.error(JSON.stringify(textStatus));
                });
            },1000);
        }

        self.reply = function() {
            window.location.href="#mailcompose/reply/" + self.messageId();
        };

        self.forward = function() {
            window.location.href="#mailcompose/forward/" + self.messageId();
        };

        self.openAttachment = function(attachment) {
            alert("opening attachment with id " + attachment.id);
        }

        self.delete = function() {
            var url = "command/deleteMessage/" + self.messageId();
            $.getJSON(
                url,
                function(data) {
                    if(data.result) {
                        toastr.info("Message deleted.");
                        window.location.href= contextPath + "/#maillist";
                    } else {
                        toastr.info("Message deletion failed. See server log for details.");
                    }
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
                });
        }

        this.cleanup = function() {
            window.clearTimeout(self.markReadTimer);
        }

    };
    
    return ViewModel;
});
