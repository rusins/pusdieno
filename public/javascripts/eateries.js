// wait for the DOM to be loaded
        $(document).ready(function() {
        // bind 'myForm' and provide a simple callback function
            $('.form-inline').ajaxForm(function(response) {
                var split = response.split(" ");
                var eatery = split[0];
                function updateButtons(on, off1, off2) {
                    $("#"+eatery+" ."+on).removeClass("inactive");
                    $("#"+eatery+" ."+on).blur();
                    $("#"+eatery+" ."+off1).addClass("inactive");
                    $("#"+eatery+" ."+off2).addClass("inactive");
                }
                switch (split[1]) {
                case "yes": $(".btn").addClass("inactive");
                $(".btn.no").removeClass("inactive");
                updateButtons("yes", "no", "maybe");
                $(".going").removeClass("going");
                $("#" + eatery).addClass("going")
                break;
                case "maybe": $(".going .yes").addClass("inactive");
                $(".going .maybe").removeClass("inactive");
                $(".going").removeClass("going");
                updateButtons("maybe", "yes", "no");
                break;
                case "no": updateButtons("no", "yes", "maybe");
                $("#"+eatery).removeClass("going");
                break;
                default: alert("Script error: " + response);
                }
            });
        });