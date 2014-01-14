define([], function () {

    var ViewModel = function (moduleContext) {

        var self = this;

        self.subject = ko.observable();
        self.to = ko.observableArray();
        self.cc = ko.observableArray();
        self.bcc = ko.observableArray();

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
                accountId: 1,
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

        self.initialize = function(msgId, replyMode) {
            if(msgId) {
                $.getJSON(
                    "command/getMessage/" + msgId,
                    function(origMessage) {
                        if(origMessage.error) {
                            toastr.error(origMessage.error);
                            return;
                        }
                        var prefix;
                        if(replyMode === "REPLY") {
                            prefix = "Re: ";
                            self.to.removeAll();
                            self.to.push({
                                id: origMessage.senderEmail,
                                emailAddress: origMessage.senderEmail
                            });
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
                ).fail(function(xhr, textStatus, errorThrown) {
                        if(xhr.status == 403)
                            window.location.href = contextPath + "/login";
                        else
                            toastr.error(JSON.stringify(textStatus));
                    }
                );

            } else {
                // clear
                self.to.removeAll();
                self.subject("");
                $("#composeeditor").val("");
            }
            self.cc.removeAll();
            self.bcc.removeAll();
        };
    };

    function citeHTML(html, citePrefix) {
        return "<br>" + citePrefix + "<blockquote>" + html + "</blockquote>";
    }

    return ViewModel;
});
