let current_tr = null;
$(document).on('click','.deleteUserBtn',function () {
    current_tr = $(this).closest("tr")
    let currentTD = $(this).closest("tr").find("td");
    $('.modal-content').empty().append("<input type='hidden' class='currentDeleteId' value='"+$(currentTD).eq(0).text()+"'> <p>Before deleting, make sure that this user is assigned to no active ticket.</p>")
    $('#approve').attr('id','approve-delete-user')
    $("#modal").fadeIn(300);
})
$(document).on('click','.close-modal',function () {
    $("#modal").fadeOut(300);
})
$(document).on('click','#approve-delete-user',function () {
    $.ajax({
        url: '/manager/api/deleteUser',
        type: 'post',
        data: { 'userid' : $('.currentDeleteId').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (){
            $(current_tr).remove()
            $("#modal").fadeOut(300);
        },
        error: function () {
            notification("Error during removal process!")
        }
    })
})