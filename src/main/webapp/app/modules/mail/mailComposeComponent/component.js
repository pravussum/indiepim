define(['Boiler', 'text!./view.html', './viewmodel', 'path!./style.css', 'i18n!./nls/resources'], function(Boiler, template, ViewModel, styleText, nls) {

	var Component = function(moduleContext) {
		var panel, vm = null;
		
		this.activate = function(parent, params) {
			if (!panel) {
				panel = new Boiler.ViewTemplate(parent, template, nls);
                panel.getJQueryElement().addClass("fillParent");
                Boiler.ViewTemplate.setStyleLink(styleText);
                vm = new ViewModel(moduleContext);

				ko.applyBindings(vm, panel.getDomElement());
//                $("#composeeditor").jqte({
//                    sub: false,
//                    sup: false,
//                    placeholder: "Compose new message..."
//                });
                $("#composeeditor").ckeditor({allowedContent: true});

                $('#typeheadtest').select2({width: "500px"  , tags:["red", "green", "blue"], placeholder:"email adress",
                    id: function(object) {
                        return object.emailAddress;
                    }
                });
			}

			if(params.replyid) {
                vm.initialize(params.replyid, "REPLY");
            } else if(params.forwardid) {
                vm.initialize(params.forwardid, "FORWARD");
            } else {
                vm.initialize();
            }

            panel.show();
		}

		this.deactivate = function() {
			if(panel) {
                panel.hide();
			}
		}
	};

	return Component;

});
