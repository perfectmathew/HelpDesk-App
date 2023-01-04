// GLOBAL VARIABLES //
let response_content = null;
let current_tr = null
let current_td;
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


// CLOSE MODAL //

$(document).on('click','.close-modal',function () {
    $("#user-addition-modal").fadeOut(300);
    $("#user-edit-modal").fadeOut(300);
    $('#risk-modal').fadeOut(300);

})

// DELETE TICKET SECTION //

$(document).on('click','.deleteTicketBtn',function () {
    current_tr = $(this).closest("tr")
    let currentTD = $(this).closest("tr").find("td");
    $('.modal-content').empty().append("<input type='hidden' class='currentDeleteId' value='"+$(currentTD).eq(0).text()+"'> <p>Before deleting, make sure that no worker and no tasks are assigned to the ticket!</p>")
    $('#approve').attr('id','approve')
    $("#modal").fadeIn(300);
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
            notification("Successfully removed the ticket!")
        },
        error: function () {
            notification("Error during removal process!")
        }
    })
})

// CONFIG SECTION //

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
                            notification("An internal error occurred!")
                        }
                    })
            }else{
                $('.modal-content').append("<p class='text-red-700 font-bold'>Wrong credentials! Password not match.</p>")
            }
        },
        error: function (){
            notification("An internal error occurred!")
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
            }else if (response === "This app support only postgresql database at this moment!"){
                $('#messages').empty().append("<p class='mt-2 text-red-500 font-bold>'>This app supports only postgresql database at this moment!</p>")
            }
            else{
                $('#messages').empty().append("<p class='mt-2 text-red-500 font-bold>'>Error during the connection!</p>")
            }
        },
        error: function (){
            $('#messages').empty().append("<p class='mt-2 text-red-500 font-bold>'>Error during the connection!</p>")
        }
    })
})

// HR SECTION //
$(document).ready(function () {
    let totalPages = 1;

    function getPage(startPage){
        let type = $('#typeOfWorker').text();
        let request = null;
            if (type === "Workers") request = "/admin/hr/workers/getAll";
            else if (type === "Managers") request = "/admin/hr/managers/getAll";
        $.ajax({
            url: request,
            type: 'GET',
            data: {
                page: startPage,
                size: 10
            },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            }, success: function (response) {
                $('#users-hr-table > tbody').empty()
                $.each(response.content, (i,user) => {
                    $('#users-hr-table > tbody').append(user_table_template(user.id,user.name,user.surname,user.email,user.phone_number,user.department.name,user.enabled))
                })
                if ($('ul.pagination li').length - 2 !== response.totalPages){
                    $('ul.pagination').empty();
                    buildPagination(response);
                }
            }
        })
    }
    function buildPagination(response) {
        let totalPages = response.totalPages;
        let pageNumber = response.pageable.pageNumber;
        let numLinks = 10;
        let prev;
        if (pageNumber > 0) {
            prev = '<li><a class="prevPage block px-3 py-2 ml-0 leading-tight  border  rounded-l-lg bg-gray-800 border-gray-700 text-gray-400 hover:bg-gray-700 hover:text-white">\n' +
                '<span class="sr-only">Previous</span>\n' +
                '<svg aria-hidden="true" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd"></path></svg>\n' +
                '</a></li>';
        } else {
            prev = '';
        }
        let next = '';
        if (pageNumber < totalPages) {
            if(pageNumber !== totalPages - 1) {
                next = '<li><a class="nextPage block px-3 py-2 leading-tight border rounded-r-lg bg-gray-800 border-gray-700 text-gray-400 hover:bg-gray-700 hover:text-white">\n' +
                    '<span class="sr-only">Next</span>\n' +
                    '<svg aria-hidden="true" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd"></path></svg>\n' +
                    '</a></li>';
            }
        } else {
            next = '';
        }
        let start = pageNumber - (pageNumber % numLinks) + 1;
        let end = start + numLinks - 1;
        end = Math.min(totalPages, end);
        let pagingLink = '';
        for (let i = start; i <= end; i++) {
            if (i === pageNumber + 1) {
                pagingLink += '<li><a class="z-10 px-3 py-2 leading-tight border hover:bg-blue-100 hover:text-blue-700 border-gray-700 bg-gray-700 text-white"> ' + i + ' </a></li>';
            } else {
                pagingLink += '<li><a class="px-3 py-2 leading-tight border hover:text-gray-700 bg-gray-800 border-gray-700 text-gray-400 hover:bg-gray-700 hover:text-white"> ' + i + ' </a></li>';
            }
        }
        pagingLink =  prev + pagingLink + next;
        $("ul.pagination").append(pagingLink);
    }
    $(document).on("click", "ul.pagination li a", function() {
        let val = $(this).text();
        let startPage;
        if ($(this).hasClass('nextPage')) {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = $("li.active");
                startPage = activeValue;
                getPage(startPage);
                $("li.active").removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if ($(this).hasClass('prevPage')) {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                getPage(startPage);
                let currentActive = $("li.active");
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            getPage(startPage);
            $("li.active").removeClass("active");
            $(this).parent().addClass("active");
        }
    });
    getPage(0);
})

$(document).on('input','.phone-number', function () {
    validateEditForm()
});

$(document).on('click','.lockAccountBnt',function () {
    let currentTD = $(this).closest("tr").find("td");
    let userid = $(currentTD).eq(0).text()
    $.ajax({
        url: '/admin/hr/user/lock',
        type: 'post',
        data: { 'operation' : false, 'userid' : userid },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if(response===false){
                $(currentTD).find('.lockAccountBnt').attr("class","unlockAccountBnt font-medium text-green-400 hover:underline")
                $(currentTD).find('.account-status-section').empty().append("<i class='fa-solid fa-lock-open'></i> Enable")
                notification("Account successfully deactivated!")
            }
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','.unlockAccountBnt',function () {
    let currentTD = $(this).closest("tr").find("td");
    let userid = $(currentTD).eq(0).text()
    $.ajax({
        url: '/admin/hr/user/lock',
        type: 'post',
        data: { 'operation' : true, 'userid' : userid },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if(response===true){
                $(currentTD).find(".unlockAccountBnt").attr("class","lockAccountBnt font-medium text-gray-400 hover:underline")
                $(currentTD).find('.account-status-section').empty().append("<i class='fa-solid fa-lock'></i> Disable")
                notification("Account successfully activated!")
            }
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','.editUserBtn',function (){
    let currentTD = $(this).closest("tr").find("td");
    current_td = currentTD;
    // $(currentTD).eq(4).text()
    $.ajax({
        type: "GET",
        url: "/manager/api/getUserDetails",
        data: { "userid" : $(currentTD).eq(0).text() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (user){
            $('#edit-id').val(user.id)
            $('#edit-name').val(user.name)
            $('#edit-surname').val(user.surname)
            $('#edit-phone_number').val(user.phone_number)
            $('#edit-email').val(user.email)
            $('#edit-department').val(user.department.id)
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
    $.ajax({
        type: "GET",
        url: "/manager/api/getUserRoles",
        data: { "userid" : $(currentTD).eq(0).text() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $('.active-roles').empty();
            $.each(response, function (i, role){
                $('.active-roles').append("<span class='inline-flex items-center py-1 px-2 mr-2 text-sm font-medium text-blue-800 bg-blue-100 rounded dark:bg-blue-200 dark:text-blue-800'>\n" +
                    "  "+role.name+" "+
                    " <button value='"+role.id+"' type='button'  class='deleteRoleBtn inline-flex items-center p-0.5 ml-2 text-sm text-blue-400 bg-transparent rounded-sm hover:bg-blue-200 hover:text-blue-900 dark:hover:bg-blue-300 dark:hover:text-blue-900' data-dismiss-target='#badge-dismiss-default' aria-label='Remove'>\n" +
                    "<i class='fa-solid fa-xmark'></i>" +
                    "      <span class='sr-only'>Remove</span>\n" +
                    "  </button>\n" +
                    "</span>");

            })
            $('#user-edit-modal').fadeIn(300)
        },
        error: function (){
            notification("An internal error occurred!")
        }
    })
})
$("#user-edit-form").submit(function (e) {
    e.preventDefault();
    let form = $(this);
    $.ajax({
        type: "patch",
        url: "/manager/api/editUser",
        data: form.serialize(),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (user) {
            $(current_td).eq(1).text(user.name)
            $(current_td).eq(2).text(user.surname)
            $(current_td).eq(3).text(user.email)
            $(current_td).eq(4).text(user.phone_number)
            $(current_td).eq(5).text(user.department.name)
            notification("User data updated successfully!")
            $('#user-edit-modal').fadeOut(300);
        },
        error: function (e) {
            notification(e.responseJSON.message);
            $('#user-edit-modal').fadeOut(300);
        }
    })
})
$(document).on('click','.deleteRoleBtn',function () {
    $.ajax({
        url: "/manager/api/deleteUserRole",
        type: "POST",
        data: { "role" : $(this).val(), "userid" : $("#edit-id").val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (){
            notification("Role deleted successful");
            $('#user-edit-modal').fadeOut(300)
        },
        error: function (){
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','#addUserBtn',function (){
    $('#user-addition-modal').fadeIn(300);
})
$("#user-addition-form").submit(function (e) {
    e.preventDefault();
    let form = $(this);
    $.ajax({
        type: "POST",
        url: "/manager/api/addUser",
        data: form.serialize(),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (user) {
            $('#users-hr-table > tbody:last-child').append("" +
                "<tr class='border-b bg-gray-900 border-gray-700'>" +
                "<td class='hidden'>"+user.id+"</td>" +
                "<td class='py-4 px-6 font-medium text-white whitespace-nowrap'>"+user.name+"</td>" +
                "<td class='py-4 px-6'>"+user.surname+"</td>" +
                "<td class='py-4 px-6'>"+user.email+"</td>" +
                "<td class='py-4 px-6'>"+user.phone_number+"</td>" +
                "<td class='py-4 px-6'>"+user.department.name+"</td>" +
                "<td class='py-4 px-6'>" +
                "<button  class='editUserBtn font-medium text-blue-600 dark:text-blue-500 hover:underline'><i class='fa-solid fa-pen'></i> Edit</button>\n" +
                "<button class='deleteUsrBtn font-medium text-red-700 dark:text-red-500 hover:underline'><i class='fa-solid fa-trash'></i> Delete</button>\n" +
                "<button class='lockAccountBnt font-medium text-gray-800 dark:text-gray-400 hover:underline'><span class='account-status-section'> <i class='fa-solid fa-lock'></i> Disable</span></button><" +
                "/td>" +
                "</tr>")
            notification("User added successfully!")
            $('#user-addition-modal').fadeOut(300);
        },
        error: function (e) {
            notification(e.responseJSON.message);
            $('#user-addition-modal').fadeOut(300);
        }
    })
})
$(document).on('click','.deleteUsrBtn',function (){
    let currentTD = $(this).closest("tr").find("td");
    let userid = $(currentTD).eq(0).text()
    current_tr =  $(this).closest("tr")
    $('#user-id-to-delete').val(userid)
    $('#delete-user-modal').fadeIn(300)
})
$(document).on('click','#delete-user-permanently',function () {
    $.ajax({
        url: '/admin/api/deleteUser',
        type: 'post',
        data: { 'userid' : $('#user-id-to-delete').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if(response==="User not found"){
                notification("User not found! Try refresh the page.")
            }else if(response === "Successful"){
                $(current_tr).remove()
                notification("User successfully removed!")
            }else {
                notification("An internal error occurred!")
            }
            $('#delete-user-modal').fadeOut(300)
        },
        error: function () {
            notification("An internal error occurred!")
            $('#delete-user-modal').fadeOut(300)
        }
    })
})
$(document).on('input','.user-email-input',function () {
    $.ajax({
        url: '/manager/api/checkEmail',
        type: 'get',
        data: { 'email' : $(this).val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if(response===false){
                $('.addBtn').attr('enabled',true)
            }else{
                notification("This email is already taken!")
                $('.addBtn').attr('enabled',false)
            }
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('input, keydown, keyup','#user-search',function (event) {
    event.preventDefault()
    let type = $('#typeOfWorker').text();
    let request = null;
    if($(this).val().length !== 0) {
        if (type === "Workers") request = "/manager/hr/workers/" + $(this).val();
        else if (type === "Managers") request = "/manager/hr/managers/"+ $(this).val();
        $.ajax({
            url: request,
            type: 'GET',
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (response) {
                $('#users-hr-table > tbody').empty()
                $.each(response, function (i,user) {
                    $('#users-hr-table > tbody').append(user_table_template(user.id,user.name,user.surname,user.email,user.phone_number,user.department.name,user.enabled))
                })
            },
            error: function () {
                $('#users-hr-table > tbody').empty()
            }
        })
    }else {
        if (type === "Workers") request = "/admin/api/hr/workers/getAllUsers";
        else if (type === "Managers") request = "/admin/api/hr/managers/getAllUsers";
       $.ajax({
           url: request,
           type: 'GET',
           beforeSend: function(xhr) {
               xhr.setRequestHeader(header, token);
           },
           success: function (response) {
               $('#users-hr-table > tbody').empty()
               $.each(response, function (i,user) {
                   $('#users-hr-table > tbody').append(user_table_template(user.id,user.name,user.surname,user.email,user.phone_number,user.department.name,user.enabled))
               })
           }
       })

    }
})

// HR JS VALIDATOR
$(document).on('input','#name',function () {
    validateForm();
})
function validateForm(){
    if(!$('#name').val().match("^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*$") || !$('#surname').val().match("^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*$") || !$('#phone_number').val().match("^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{3,6}$")){
        $('#submit-btn').prop('disabled',true).removeClass('bg-gray-800').addClass('bg-red-500 border-2 border-red-500')
    }else{
        $('#submit-btn').prop('disabled',false).removeClass('bg-red-500 border-2 border-red-500').addClass('bg-gray-800')
    }
    if (!$('#name').val().match("^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*$")){
        $('#name').removeClass('border').addClass('border-2 border-red-500')
    }else {
        $('#name').removeClass('border-2 border-red-500').addClass('border')
    }
    if(!$('#surname').val().match("^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*$")){
        $('#surname').removeClass('border').addClass('border-2 border-red-500')
    }else {
        $('#surname').removeClass('border-2 border-red-500').addClass('border')
    }
    if(!$('#phone_number').val().match("^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{3,6}$")){
        $('#phone_number').removeClass('border').addClass('border-2 border-red-500')
    }else {
        $('#phone_number').removeClass('border-2 border-red-500').addClass('border')
    }
}

// DEPARTMENT SECTION //

$(document).on('click','#addDepartmentBtn',function () {
    $('#addDepartmentBtn').attr('disabled',true);
    $('#departments-table tbody>tr:last').after("<tr class='bg-white border-b dark:bg-gray-900 dark:border-gray-700'><td class='py-4 px-6 font-medium text-white whitespace-nowrap'><input type='text' placeholder='Department name' class='pl-2 text-black rounded w-full'></td><td class='py-4 px-6'><button class='addDepartment font-medium text-green-500'><i class='fa-solid fa-circle-check'></i> Add department</button> </td> </tr>")
})
$(document).on('click','.ediDepartmentBtn',function () {
    current_td = $(this).closest("tr").find("td");
    let department_name = $(current_td).eq(1).text()
    $(current_td).eq(1).empty().append("<input type='text' class='pl-2 text-black rounded w-full' value='"+department_name+"'>")
    $(current_td).eq(2).empty().append("<button class='editDepartment font-medium text-yellow-500'><i class='fa-solid fa-pen-to-square'></i> Edit department</button>" +
        "<button class='cancelEditDepartment pl-2 font-medium text-red-500'><i class='fa-solid fa-xmark'></i></button>")
})
$(document).on('click','.cancelEditDepartment',function () {
    current_td = $(this).closest("tr").find("td");
    let old_name = $(this).closest("tr").find("td>input");
    $(current_td).eq(1).empty().append("<p>"+old_name.val()+"</p>")
    $(current_td).eq(2).empty().append(" <a  class='ediDepartmentBtn font-medium text-blue-600 dark:text-blue-500 hover:underline'><i class='fa-solid fa-pen'></i> Edit</a>" +
        "<a  class='deleteDepartment font-medium text-red-700 dark:text-red-500 hover:underline'><i class='fa-solid fa-trash'></i> Delete</a>")
})
$(document).on('click','.editDepartment',function () {
    current_td = $(this).closest("tr").find("td");
    let new_name = $(this).closest("tr").find("td>input");
    current_tr = $(this).closest("tr");
    $.ajax({
        url: '/admin/api/department/edit',
        type: 'patch',
        data: { "department_id" : $(current_td).eq(0).text(), 'department_name' : new_name.val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            $(current_td).eq(1).empty().append("<p>"+response.name+"</p>")
            $(current_td).eq(2).empty().append(" <a  class='ediDepartmentBtn font-medium text-blue-600 dark:text-blue-500 hover:underline'><i class='fa-solid fa-pen'></i> Edit</a>" +
                "<a  class='deleteDepartment font-medium text-red-700 dark:text-red-500 hover:underline'><i class='fa-solid fa-trash'></i> Delete</a>")
            notification("Successfully edited "+response.name+" department")
        },
        error: function (e){
            notification(e.responseJSON.message);
        }
    })
})
$(document).on('click','.deleteDepartment',function (){
    current_td = $(this).closest("tr").find("td");
    current_tr = $(this).closest("tr");
    $.ajax({
        url: '/admin/api/department/delete',
        type: 'delete',
        data: { "department_id" : $(current_td).eq(0).text() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if(response==="Successful"){
                current_tr.remove();
                notification("Successfully removed department")
            }
        },
        error: function (e){
            notification(e.responseJSON.message);
        }
    })
})
$(document).on('click','.addDepartment',function () {
    let currentInput = $(this).closest("tr").find("td>input");
    let currenButton = $(this).closest("tr").find("td>button");
    $(currenButton).empty().append("<i class='fa-solid fa-circle-notch fa-spin'></i>")
    $.ajax({
        url: '/admin/api/department/add',
        type: 'post',
        data: { "department_name" : $(currentInput).val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if(response==="Successful"){
                $('#addDepartmentBtn').attr('disabled',false);
                notification("Successfully added new department")
                location.reload()
            }
        },
        error: function (e){
            $(currenButton).empty().append("<button class='addDepartment font-medium text-green-500'><i class='fa-solid fa-circle-check'></i> Add department</button>")
            notification(e.responseJSON.message);
        }
    })
})

