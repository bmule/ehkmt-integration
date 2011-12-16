//confluence http://confluence.atlassian.com/display/DOC/How+to+Force+Links+to+Open+in+a+New+Window
//see  http://plugins.jquery.com/project/link-manager
//http://www.jainaewen.com/files/javascript/jquery/link-manager.html
/* 
Visit Administration >> Custom HTML
Click Edit
In the At end of the HEAD field, insert this code:

For external links only, like [http://www.google.com]:
    <script>
    jQuery(document).ready(function() {
        jQuery(".external-link").attr("target", "_blank");
    });
    </script>
 */
/*
$(document).ready(function() {
   $("a[href^=http]").each(function(){
      if(this.href.indexOf(location.hostname) == -1) {
         $(this).attr({
            target: "_blank",
            title: "Opens in a new window"
         });
      }
   })
});
*/