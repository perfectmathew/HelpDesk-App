$(document).on('click','.showTaskBtn',function () {
    $.ajax({
        url: '/api/getTaskInfo',
        type: 'get',
        data: { 'task_id' : $(this).val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $('#show-task-id').val(response.id)
            $('#show-task-name').text(response.task)
            $('#show-task-description').text(response.description)
            $('#show-task-modal').fadeIn(300)
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','#show-done-task-ticket',function () {
    $(this).empty().append("<i class='fa-solid fa-circle-notch fa-spin'></i>")
    $.ajax({
       url: '/worker/api/markTaskAsDone',
        type: 'patch',
        data: { 'task-id' : $('#show-task-id').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
           if (response === "Successfully"){
               let  tmp =  $('#show-task-id').val()
               $(".showTaskBtn[value='"+tmp+"']").remove()
               notification("Task accomplished! Time for some coffee :)")
           }

            $('#show-task-modal').fadeOut(300)
        },
        error: function () {
            notification("An internal error occurred!")
            $('#show-task-modal').fadeOut(300)
        }
    })
})
$(document).on('click','.editStatusBtn',function () {
    currentStatus =  $('.ticketStatusValue').text();
    $('#ticketStatus').empty().append("Status <span><select id='status-selector'></select></span>" +
        "<button type='button' class='ml-2 approveStatusBtn text-white w-6 bg-green-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-check'></i></button>" +
        "<button type='button' class='ml-2 cancelStatusBtn text-white w-6 bg-red-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-xmark'></i></button>")
    $.ajax({
        url: '/worker/api/getAllStatuses',
        type: 'get',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $.each(response,function (i,status) {
                $('#status-selector').append("<option value='"+status.id+"'>"+status.status+"</option>")
            })
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','.approveStatusBtn',function () {
    $.ajax({
        url: '/worker/api/changeTicketTStatus',
        type: 'patch',
        data: { 'ticket-id': $('#ticket-id').val(), 'status-id' : $('#status-selector').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            if(response === "SMTP ERROR"){
                notification("Smtp server error!");
            }
            $('#ticketStatus').empty().append("Status: <span class='ticketStatusValue'>"+response+"</span>")
            if (response != 'VERIFICATION'){
                $('#ticketStatus').append("<button type='button' class='ml-2 editStatusBtn text-white w-6 bg-yellow-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-pen-to-square'></i></button>")
            }
            notification("Successfully updated status!")
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','.cancelStatusBtn',function () {
    $('#ticketStatus').empty().append("Status: <span class='ticketStatusValue'>"+currentStatus+"</span>" +
        "<button type='button' class='ml-2 editStatusBtn text-white w-6 bg-yellow-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-pen-to-square'></i></button>")
})
$(document).on('click','#editButton',function () {
    let doc_description = $('#docs-description').text()
    let doc_id = $('#documentation-id').val()
    $('#documentation-area').empty().append("<p class='font-bold'>Documentation content:</p>\n" +
        "              <form action='/worker/api/editDocumentation' enctype='multipart/form-data' method='post'>\n" +
        "<input type='hidden' name='documentationid' value='"+doc_id+"'>" +
        "                <input type='hidden' name='ticketid' value='"+$('#ticket-id').val()+"'>\n" +
        "                <textarea name='content'  class='shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline' required>\n" + doc_description +
        "                     </textarea>\n" +
        " <input type='hidden' name='_csrf' value='"+token+"' />"+
        "                <p class='font-bold'>Attachments:</p>\n" +
        "                <input type='file' id='attachments' name='attachments' multiple='multiple'>\n" +
        "                <button type='submit' class='bg-yellow-500 hover:bg-yellow-700 pl-2 mt-2 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline'><i class='fa-solid fa-pen'></i> Edit documentation</button>\n" +
        "              </form>")

})