(function(document, $) {
    "use strict";
    $(document).on("foundation-contentloaded", function(e) {
        showHide($(".cq-dialog-tab-showhide", e.target));
    });
    $(document).on("selected change", ".cq-dialog-tab-showhide", function(e) {
        showHide($(this));
    });

    function showHide(el) {
        el.each(function(i, element) {
            var target = $(element).data("cqDialogTabShowhideTarget");

            if ($(element).data("select")) {
                var value = $(element).data("select").getValue();
                $(target).addClass("hide");
                if (value && value !== "none" && value !== "option2") {
                    $(target + '.' + value).removeClass("hide");
                }
            } else if ($(element).is("coral-select")) {
                var value = $(element).find("coral-select-item:selected").attr("value");
                $(target).addClass("hide"); if (value && value !== "none" && value !== "option2") {
                    $(target + '.' + value).removeClass("hide");
                }
            }
        });
    }
})(document, Granite.$);
