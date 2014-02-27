define([], function () {

    var MessageAccount = function(data) {
        var self = this;
        self.id = data.id;
        self.email = data.email;
        self.accountName = data.accountName;
    };

    var ViewModel = function (moduleContext) {

        var self = this;

        self.subject = ko.observable();
        self.to = ko.observableArray();
        self.cc = ko.observableArray();
        self.bcc = ko.observableArray();
        self.messageAccounts = ko.observableArray();
        self.selectedAccount = ko.observable();

        self.select2options = {
            width: "100%",
            tags:true,
            minimumInputLength: 2,
            ajax : {
                url : "command/getEmailAddresses",
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
                //var imagePart = "<img src='" + item.imageUrl + "' title='" + item.contactName + "' height='25px' width='25px' />";
                var imagePart = "<i class='fa fa-user fa-2x'/>";
                var namePart;
                if(typeof item.contactName !== 'undefined' && item.contactName != null)
                    namePart = "<div class='full_name'>" + item.contactName + "</div>";
                else
                    namePart = "";
                var emailPart = "<div class='email'>" + item.emailAddress + "</div>"
                var dataPart = "<div style='display: inline-block; padding-left: 10px;'>" + namePart + emailPart + "</div>";
                return imagePart + dataPart;

                return item.emailAddress;
             },
             formatSelection : function(item) {
                 return item.emailAddress;
             },
             createSearchChoice: function (term, data) {
                 if ($(data).filter(function () {
                     return this.displayAddress.localeCompare(term) === 0;
                 }).length === 0) {
                     return {
                         id: term,
                         emailAddress: term
                     };
                 }
             }
        }

        self.removeEmailAddress = function(emailAddress) {
            self.to.remove(emailAddress);
        }

        self.sendMessage = function() {
            var requestData = {
                accountId: self.selectedAccount().id,
                subject: self.subject(),
                to: $.map(self.to(), function(element) {return element.emailAddress;}),
                cc: $.map(self.cc(), function(element) {return element.emailAddress;}),
                bcc: $.map(self.bcc(), function(element) {return element.emailAddress;}),
                content: $("#composeeditor").val(),
                isHtml: true
            }
            $.ajax(
                "command/sendMessage/",
                {
                    data: JSON.stringify(requestData),
                    type: "post",
                    contentType: "application/json",
                    success: function(data) {
                        toastr.info("Message sent successfully.");
                        window.location.href = contextPath + "/#maillist";
                    }
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
            });
        }

        self.initialize = function(origMessageId, replyMode) {
            // init message account selectbox
            $.getJSON("command/getMessageAccounts", function(data) {
                    if(typeof data.error != "undefined") {
                        toastr.error("Receiving the account list failed. See server log for details.");
                    } else {
                        var mappedAccounts = $.map(data.accounts, function(item) {return new MessageAccount(item)});
                        self.messageAccounts(mappedAccounts);
                        self.selectedAccount(self.messageAccounts()[0]);
                    }
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
            });

            if(!origMessageId) {
                // clear
                self.to.removeAll();
                self.subject("");
                $("#composeeditor").val("");
            }
            self.cc.removeAll();
            self.bcc.removeAll();

            $.getJSON(
                "command/createDraft" + (origMessageId ? ("?origMessageId=" + origMessageId) : ""),
                function(draft) {

                    if(draft.error) {
                        toastr.error(draft.error);
                        return;
                    }
                    if(origMessageId) {
                        var prefix;
                        var origMessage = draft.origMessage;
                        if(replyMode === "REPLY" || replyMode === "REPLYALL") {
                            prefix = "Re: ";
                            self.to.removeAll();
                            self.to.push({
                                id: origMessage.senderEmail,
                                emailAddress: origMessage.senderEmail
                            });
                            if(replyMode === "REPLYALL") {

                                for(var i=0;i<origMessage.receiver.length; i++) {
                                    // exclude myself
                                    var rec = origMessage.receiver[i];
                                    if(rec.contains("<") && rec.contains(">"))
                                        rec = (rec.substring(rec.indexOf("<")+1, rec.indexOf(">")));
                                    self.cc.push({
                                        id: rec,
                                        emailAddress: rec
                                    });
                                }
                            }
                        } else {
                            prefix = "Fwd: ";
                            self.to.removeAll();
                        }
                        self.subject(prefix + (origMessage.subject ? origMessage.subject : ""));

                        var content;
                        var citePrefix = origMessage.senderEmail + " wrote on " + moment(origMessage.dateReceived).format("LLL") + ":";
                        if(typeof origMessage.contentHtml !== 'undefined' && origMessage.contentHtml != null) {
                            content = citeHTML(origMessage.contentHtml, citePrefix);
                            //$("#composeeditor").val(content);
                            $("#composeeditor").ckeditor().editor.setData(content);
                            //CKEDITOR.instances.composeeditor.insertHtml( content );
                        } else if (typeof origMessage.contentText !== 'undefined' && origMessage.contentText != null) {
                            content = "<pre>" + citePrefix + "\n" + origMessage.contentText + "</pre>";
                            $("#composeeditor").val(content);
                        } else {
                            content = "The message contained no valid content.";
                        }
                    }
                    console.log("draftid:" + draft.id);
                    $("#fileupload").fileupload({
                        url: "/command/uploadAttachment?messageId=" + draft.id,
                        dropZone: $("#attachmentPanel"),
                        dataType: 'json',
                        done: function (e, data) {
                            $.each(data.result.files, function (index, file) {
                                $('<p/>').text(file.name).appendTo($("#attachmentPanel"));
                            });
                        },
                        progressall: function (e, data) {
                            var progress = parseInt(data.loaded / data.total * 100, 10);
                            $('#attachmentoverallprogress').css(
                                'width',
                                progress + '%'
                            );
                        }
                    });
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login";
                    else
                        toastr.error(JSON.stringify(textStatus));
                }
            );
        };
    };

    function citeHTML(html, citePrefix) {
        return "<br>" + citePrefix + "<blockquote>" + html + "</blockquote>";
    }

    return ViewModel;
});
