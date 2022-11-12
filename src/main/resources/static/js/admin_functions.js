let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
let response_content = null;
let current_tr = null
let db_connection_string = ""
let db_username = ""
let db_password = ""
let maintained_mode = ""
let server_port =""
let max_file_size = ""
let max_request_file_size = ""
let smtp_server_status = ""
let smtp_server = ""
let smtp_server_port = ""
let smtp_username = ""
let smtp_password = ""
$(document).on('click','.deleteTicketBtn',function () {
    current_tr = $(this).closest("tr")
    let currentTD = $(this).closest("tr").find("td");
    $('.modal-content').empty().append("<input type='hidden' class='currentDeleteId' value='"+$(currentTD).eq(0).text()+"'> <p>Before deleting, make sure that no worker and no tasks are assigned to the ticket!</p>")
    $('#approve').attr('id','approve')
    $("#modal").fadeIn(300);
})
$(document).on('click','.close-modal',function () {
    $("#modal").fadeOut(300);
    $('#risk-modal').fadeOut(300);
})
$(document).on('click','#approve',function () {
    $.ajax({
        url: "/admin/ticket/delete",
        type: "post",
        data: { "ticket_id" : $('.currentDeleteId').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (){
            $(current_tr).remove()
            $("#modal").fadeOut(300);
        },
        error: function (e) {
            notification("Error during removal process!")
        }
    })
})


$(document).on('click','#change-config',function () {
    $('.modal-content').empty().append("<p>To verify the operation, please re-enter your password.</p> <input type='password' placeholder='Password' id='password-input' name='password' class='w-full border border-b border-gray-200 m-4 p-2 rounded' />")
    $('#approve').attr('id','approve-password')
    $('#modal').fadeIn(300);
})
$(document).on('click','#approve-password',function () {

    $.ajax({
        url: '/admin/settings/checkPassword',
        type: 'POST',
        data: { "password" : $('#password-input').val()},
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            if(response === "Successful"){
                $('#modal-buttons-section').hide()
                $('.modal-content').empty().append("<p>Correct credentials. Now we check all the systems settings and copy the new configuration to startup configuration.... This may take some time. </p>" +
                    "<p class='font-bold text-green-500 text-2xl'><span class='text-sm'> Saving configuration file... </span> <i class='fa-solid fa-cog fa-spin'></i></p>" +
                    "<p>Once the process is complete, the server will restart on its own.</p>" +
                    "<p class='text-red-700 font-bold'>DO NOT REFRESH THIS PAGE!</p>")

                    $.ajax({
                        url: '/admin/settings/save-changes',
                        type: 'get',
                        data: { 'db_server' : $('#db-connection-string').val(), 'db_username' : $('#db-username').val(),
                        'db_password' : $('#db-password').val(), 'smtp_server_status' : $('#smtp-config-status').val(),
                        'smtp_server' : $('#smtp-config-connection').val(), 'smtp_server_port' : $('#smtp-config-server-port').val(),
                        'smtp_username' : $('#smtp-config-username').val(), 'smtp_password' : $('#smtp-config-password').val(),
                        'site_server_port' : $('#site-server-port').val(), 'site_maintenance_mode' : $('#site-maintenance').val(),
                        'site_max_file_size' : $('#site-file-size').val(), 'site_request_max_file_size' : $('#site-request-file-size').val()},
                        beforeSend: function(xhr) {
                            xhr.setRequestHeader(header, token);
                        },
                        success: function (){
                            location.reload()
                        },
                        error: function (){

                        }
                    })
            }else{
                $('.modal-content').append("<p class='text-red-700 font-bold'>Wrong credentials! Password not match.</p>")
            }
        },
        error: function (){

        }
    })
})
$(document).on('click','#cancel-changes',function () {
    location.reload()
})
$(document).on('click','#approve-risk',function (){
    $('#edit-config').hide();
    $("#check-update").hide();
    $('#restart-server').hide();
    $('#change-config').show();
    $('#backup-configuration').show();
    $('#cancel-changes').show();
    db_connection_string = $('#database-server').text();
    db_username = $('#database-username').text();
    db_password = $('#database-password').val();
    maintained_mode = $('#maintenance-mode').text();
    server_port = $('#server-port').text();
    max_file_size = $('#max-file-size').text();
    max_request_file_size = $('#max-request-file-size').text();
    smtp_server_status = $('#smtp-server-status').text();
    smtp_server = $('#smtp-server').text();
    smtp_server_port = $('#smtp-server-port').text();
    smtp_username = $('#smtp-username').text();
    smtp_password = $('#smtp-password').val();
    $('#site-config').empty().append("<p class='font-bold mt-2'> Maintenance mode (ON/OFF): </p>" +
        "<input type='text' id='site-maintenance' value='"+maintained_mode+"' class='mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> Server port (eg. 80): </p>" +
        "<input type='text' id='site-server-port' value='"+server_port+"' class='mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> Max file size for uploads: </p>" +
        "<input type='text' id='site-file-size' value='"+max_file_size+"' class=' mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> Max file size for request uploads: </p>" +
        "<input type='text' id='site-request-file-size' value='"+max_request_file_size+"' class=' mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>")
    $('#smtp-config').empty().append("<p class='font-bold mt-2'> SMTP Server status (ON/OFF): </p>" +
        "<input type='text' id='smtp-config-status' value='"+smtp_server_status+"' class='mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> SMTP Server (Only servers with SSL Encryption is supported): </p>" +
        "<input type='text' id='smtp-config-connection' value='"+smtp_server+"' class='mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> SMTP port: </p>" +
        "<input type='text' id='smtp-config-server-port' value='"+smtp_server_port+"' class='mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> SMTP username: </p>" +
        "<input type='text' id='smtp-config-username' value='"+smtp_username+"' class=' mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> SMTP password: </p>" +
        "<input type='text' id='smtp-config-password' value='"+smtp_password+"' class=' mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<button id='test-smtp' type='button' data-mdb-ripple='true' data-mdb-ripple-color='light' class='inline-block px-6 mt-2 py-2.5 bg-blue-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
        "          >  <i class='fa-solid fa-cloud-arrow-up'></i>  Test Connection</button><div id='messages-smtp'></div>")
    $('#db-config').empty().append("<p class='font-bold mt-2'> Database server (Only PostgresSQL is supported in curent build!): </p>" +
        "<input type='text' id='db-connection-string' value='"+db_connection_string+"' class='mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> Database username: </p>" +
        "<input type='text' id='db-username' value='"+db_username+"' class='mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<p class='font-bold mt-2'> Database password: </p>" +
        "<input type='text' id='db-password' value='"+db_password+"' class=' mt-2 border py-1 px-3 w-full rounded-2xl shadow-md'>" +
        "<button id='test-db' type='button' data-mdb-ripple='true' data-mdb-ripple-color='light' class='inline-block px-6 mt-2 py-2.5 bg-blue-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
        "          ><i class='fa-solid fa-database'></i> Test Connection</button><div id='messages'></div>")
    $('#risk-modal').fadeOut(300)
})
$(document).on('click','#edit-config',function (){
    $('#risk-modal').fadeIn(300)

})
$(document).on('click','#test-smtp',function (){
    $.ajax({
        url: "/admin/settings/check-smtp",
        type: "get",
        data: { "smtp_server" : $('#smtp-config-connection').val(), "smtp_server_port" : $('#smtp-config-server-port').val(),
            "smtp_username": $('#smtp-config-username').val(), "smtp_password" : $('#smtp-config-password').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            if(response === "Successful"){
                $('#messages-smtp').empty().append("<p class='mt-2 text-green-500 font-bold>'>Connected successful!</p>")
            }else{
                $('#messages-smtp').empty().append("<p class='mt-2 text-red-500 font-bold>'>Error during the connection!</p>")
            }
        },
        error: function (){
            $('#messages-smtp').empty().append("<p class='mt-2 text-red-500 font-bold>'>Error during the connection!</p>")
        }
    })
})
$(document).on('click','#test-db',function (){
    $.ajax({
        url: "/admin/settings/check-db",
        type: "get",
        data: { "url" : $('#db-connection-string').val(), "username" : $('#db-username').val(), "password" : $('#db-password').val()},
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            if(response === "Successful"){
                $('#messages').empty().append("<p class='mt-2 text-green-500 font-bold>'>Connected successful!</p>")
            }else{
                $('#messages').empty().append("<p class='mt-2 text-red-500 font-bold>'>Error during the connection!</p>")
            }
        },
        error: function (){
            $('#messages').empty().append("<p class='mt-2 text-red-500 font-bold>'>Error during the connection!</p>")
        }
    })
})