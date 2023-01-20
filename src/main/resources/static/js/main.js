let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
function notification(message) {
    Toastify({
        text: message,
        duration: 3000,
        gravity: "bottom",
        position: "right",
        stopOnFocus: true,
        style: {
            background: "linear-gradient(to right, #00b09b, #96c93d)",
        },
        onClick: function(){}
    }).showToast();
}
$(document).on('click','#hide-modal',function (){
    $('#Modal').hide();
})
$(document).on('click','.close-modal',function () {
    $('#modal').fadeOut(300)
    $('#task-modal').fadeOut(300)
    $("#edit-task-modal").fadeOut(300);
    $("#show-task-modal").fadeOut(300);
    $('#show-solution-modal').fadeOut(300)
    $('#show-solution_db-modal').fadeOut(300)
    $("#delete-user-modal").fadeOut(300)
    $('#reject-solution-modal').fadeOut(300)
})