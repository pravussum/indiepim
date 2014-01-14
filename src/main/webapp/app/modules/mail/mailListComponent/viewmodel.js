define([], function () {

    function mailListEntry (id, read, from, tags, subject, contentPreview, date, attachment	) {
        var self = this;
        self.id = id;
        self.read = ko.observable(read);
        self.unread = ko.computed(function(){ return !self.read();})
        self.readIcon = ko.computed(function(){
            return self.read() ? "fa-eye readIcon" : "fa-envelope-o";
        });
        self.from = from;
        self.tags = ko.observableArray(tags);
        self.subject = subject;
        self.contentPreview = contentPreview;
        self.date = ko.observable(date);
        self.displayDate = ko.computed(function() {
            if(moment(self.date()).isAfter(moment().subtract('hours', 6))) {
                return moment(self.date()).fromNow();
            }
            if(moment(self.date()).isAfter(moment().startOf('day'))) {
                return moment(self.date()).format('HH:mm');
            }
            if(moment(self.date()).isAfter(moment().startOf('year'))) {
                return moment(self.date()).format('DD. MMM ') + moment(self.date()).format('HH:mm');
            }
            else {
                return moment(self.date()).format('DD. MMM YYYY');
            }
        });
        self.attachment = ko.observable(attachment);
        self.displayTags = ko.computed(function(){
            return $.map(self.tags(), function(value, index) {
                return "<span class='tag' style='background-color:#" + value.color + "'>" + value.tag + "</span>";
            }).join("");
        });
    }

    function View(searchTerm, accountId, tagName, tagLineageId, readFlag) {
        var self = this;
        self.searchTerm = ko.observable(searchTerm);
        self.accountId = ko.observable(accountId);
        self.tagName = ko.observable(tagName);
        self.tagLineageId = ko.observable(tagLineageId);
        self.read = ko.observable(readFlag);

        self.isViewAll = ko.computed(function() {
            return !(self.searchTerm() || self.accountId() || self.tagName() || self.tagLineageId() || self.read());
        });

        self.displayText = ko.computed(function(){
            if(self.searchTerm()) {
                return "Search results for '" + self.searchTerm() + "'";
            }
            if(self.accountId()) {
                return "Showing all account messages."
            }
            if(self.tagName()) {
                return "Showing messages for tag " + self.tagName();
            }
            if(self.tagLineageId()) {
                return "Showing messages for tag hierarchy node.";
            }
            if(typeof self.read() != "undefined") {
                if(self.read() == "false" || !self.read())
                    return "Showing unread messages."
                else
                    return "Showing read messages.";
            }
            return "Showing all messages";
        });
    }

    var ViewModel = function (moduleContext) {

        var self = this;
        self.searchTermInput = ko.observable(); // just the input value, the current search term is held in currentView.searchTerm
        self.mailList = ko.observableArray();
        self.loading = ko.observable(false);
        self.pageSize = ko.observable(50);
        self.totalSize = ko.observable(undefined);
        self.resultSize = ko.observable(undefined);
        self.offset = ko.observable(0);
        self.from = ko.computed(function() {
            return self.offset() + 1;
        });
        self.to = ko.computed(function() {
            return Math.min(self.offset() + self.pageSize(), self.totalSize());
        });
        self.showFromTo = ko.computed(function(){
            return typeof self.resultSize() !== 'undefined';
        })

        self.currentView = ko.observable(new View());

        self.performSearch = function() {
            if(self.searchTermInput()) {
                window.location.href = '#maillist/search/' + self.searchTermInput();
            } else {
                window.location.href = '#maillist';
            }

        }


        self.getMails = function() {
            var url = "command/getMessages";
            self.loading(true);
            $.getJSON(url,
                $.param({
                    "accountId": self.currentView().accountId(),
                    "searchTerm": self.currentView().searchTerm(),
                    "tagLineageId": self.currentView().tagLineageId(),
                    "tagName": self.currentView().tagName(),
                    "read" : self.currentView().read(),
                    "offset": self.offset(),
                    "pageSize": self.pageSize()
                }),
                function(data){
                    var ml = $.map(data.messages, function(value, index) {
                        return new mailListEntry(
                            value.msgId,
                            value.read,
                            value.sender,
                            value.tags,
                            value.subject,
                            value.contentPreview,
                            value.dateReceived,
                            value.hasAttachment);
                    });
                    self.resultSize(data.messages.length);
                    self.totalSize(data.totalCount);
                    self.mailList(ml);

                }) // third param: observable gets called (= assigned) with result json data
                .fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403) {
                        // access denied = not logged in --> redirect to login
                        window.location.href = contextPath + "/login";
                    }
                    toastr.error(JSON.stringify(textStatus));
                }).always(function(){
                    self.loading(false);
                });

            self.updateMessageStats();
        }

        self.search = function(query){
            // reset total size
            self.currentView(new View(ko.utils.unwrapObservable(query)));
            self.totalSize(undefined);
            self.resultSize(undefined);
            self.offset(0);
            self.getMails();
        }

        self.getMailsForAccount = function(accId) {
            // reset total size
            self.currentView(new View(null, accId));
            self.totalSize(undefined);
            self.resultSize(undefined);
            self.offset(0);
            self.getMails();
        }

        self.getMailsForTagLineageId = function(tagLineageId) {
            // reset total size
            self.currentView(new View(null, null, null, tagLineageId));
            self.totalSize(undefined);
            self.resultSize(undefined);
            self.offset(0);
            self.getMails();
        }

        self.getMailsByReadFlag = function(readFlag) {
            self.currentView(new View(null, null, null, null, readFlag));
            self.totalSize(undefined);
            self.resultSize(undefined);
            self.offset(0);
            self.getMails();
        }

        self.getAllMails = function() {
            self.currentView(new View());
            self.totalSize(undefined);
            self.resultSize(undefined);
            self.offset(0);
            self.getMails();
        }

        self.viewSelectedMail = function(selectedMail){
            window.location.href="#mailview/" + selectedMail.id;
        };

        self.toggleRead = function(selectedMessage) {
            selectedMessage.read(!selectedMessage.read());
            var url = "command/markAsRead/" + selectedMessage.id;
            if(!selectedMessage.read()) {
                url += "?read=false";
            }
            $.getJSON(
                url,
                function(data) {
                   // TODO process the result message list DTOs and update the message list

                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
                });
        };

        self.nextPage = function() {
            var newOffset = self.offset() + self.pageSize();
            if(newOffset >= self.totalSize())
                return;
            self.offset(newOffset);
            self.getMails();
        }

        self.prevPage = function() {
            var newOffset = self.offset() - self.pageSize();
            if(newOffset < 0)
                return;
            self.offset(newOffset);
            self.getMails();
        };

        self.firstPage = function() {
            self.offset(0);
            self.getMails();
        }

        self.lastPage = function() {
            var newOffset = self.totalSize() - self.pageSize();
            if(newOffset < 0){
                newOffset = 0;
            }
            self.offset(newOffset);
            self.getMails();
        }

        self.updateMessageStats = function() {
            $.getJSON(
                "command/getMessageStats?type=COUNT_LAST_TEN_DAYS",
                function(result) {
                    $("#sparkline").sparkline(result, {
                        type: 'bar',
                        barWidth: 5});
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
                });
        }

        moduleContext.listen("NEW_MESSAGE", function(data) {
            if(self.currentView().isViewAll()) {
                self.getMails();
            }
        });

        moduleContext.listen("MESSAGE_READ", function(data) {
            for(var i=0; i<self.mailList().length;i++) {
                if(self.mailList()[i].id == data) {
                    self.mailList()[i].read(true);
                }
            }
        });

    };

    return ViewModel;
});
