//callback handler for form submit
$("#confirm").submit(function(e)
{
    var postData = $(this).serializeArray();
    var formURL = $(this).attr("action");
    console.log("post data: ", postData)
    console.log("formURL: ", formURL)
    $.ajax(
    {
        url : formURL,
        type: "POST",
        data : postData,
        success:function(data, textStatus, jqXHR) 
        {
            location.reload()
        },
        error: function(jqXHR, textStatus, errorThrown) 
        {
        console.log("error: ", errorThrown)
            window.alert(`${errorThrown}`)
        }
    });
    e.preventDefault(); //STOP default action
    // e.unbind(); //unbind. to stop multiple form submit.
});
 
/*
$("#confirm").submit(); //Submit  the FORM
*/
//Fill modal with content from link href
$("#dataModal").on("show.bs.modal", function(e) {
    var link = $(e.relatedTarget);
    $(this).find(".modal-content").load(link.attr("href"));
});