// GLOBAL VARIABLES //
let current_td = null;
let current_tr = null;
let department_name;
let currentStatus;
let currentPriority;


// MANAGER HR SECTION//

$(document).on('click','.editTicketBtn',function () {
    let currentTD = $(this).closest("tr").find("td");
    window.location.replace("/t/"+$(currentTD).eq(0).text())
})
$(document).on('click','.deleteUsrBtn',function () {
    current_tr = $(this).closest("tr")
    let currentTD = $(this).closest("tr").find("td");
    $('.modal-content').empty().append("<input type='hidden' class='currentDeleteId' value='"+$(currentTD).eq(0).text()+"'> <p>Before deleting, make sure that this user is assigned to no active ticket.</p>")
    $('#approve').attr('id','approve-delete-user')
    $("#modal").fadeIn(300);
})
$(document).on('click','#approve-delete-user',function () {
    $.ajax({
        url: '/manager/api/deleteUser',
        type: 'post',
        data: { 'userid' : $('.currentDeleteId').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            if(response==="Successful"){
                $(current_tr).remove()
                $("#modal").fadeOut(300);
                notification("The user was properly removed!")
            }else{
                $("#modal").fadeOut(300);
                notification("You do not have permissions for this action!")
            }

        },
        error: function () {
            notification("Error during removal process!")
        }
    })
})
$(document).on('click','.editUserBtn',function (){
    let currentTD = $(this).closest("tr").find("td");
    current_td = currentTD;
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
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
    $('#user-edit-modal').fadeIn(300)
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
                "</td>" +
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

$(document).ready(function () {
    let totalPages = 1;
    function getPage(startPage){
        let request = "/manager/api/hr/getDepartmentWorkers";
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
                    $('#users-hr-table > tbody').append(user_table_template_m(user.id,user.name,user.surname,user.email,user.phone_number,user.department.name))
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

// CLOSE MODAL //

$(document).on('click','.close-modal',function () {
    $("#modal").fadeOut(300);
    $('#user-edit-modal').fadeOut(300)
    $('#user-addition-modal').fadeOut(300)
})
$(document).on('click','#hide-modal',function (){
    $('#Modal').hide();
    $('#user-edit-modal').fadeOut(300)
    $('#user-addition-modal').fadeOut(300)
})

// TICKET SECTION //

$(document).on('click','.addTaskBtn',function () {
    $('#task-modal').fadeIn(300)
})
$(document).on('click','.editTaskBtn',function () {
    $.ajax({
        url: '/api/getTaskInfo',
        type: 'get',
        data: { 'task_id' : $(this).val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $('#edit-task-id').val(response.id)
            $('#edit-task-name').val(response.task)
            $('#edit-task-description').val(response.description)
            $('#edit-task-modal').fadeIn(300)
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','#edit-task-ticket',function () {
    $(this).empty().append("<i class='fa-solid fa-circle-notch fa-spin'></i>")
    $.ajax({
        url: '/manager/api/updateTask',
        type: 'post',
        data: { 'task_id' : $('#edit-task-id').val(), 'task_name' : $('#edit-task-name').val(), 'task_description' : $('#edit-task-description').val(),
        'task_done' : $('#task-status-select').val() },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            let  tmp =  $('#edit-task-id').val()
            $(".editTaskBtn[value='"+tmp+"']").empty().append(response.task)
            if(response.done === true){
                $(".editTaskBtn[value='"+tmp+"']").attr('class','editTaskBtn m-2 inline-block px-6 py-2.5 bg-green-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-green-700 hover:shadow-lg focus:bg-green-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-green-800 active:shadow-lg transition duration-150 ease-in-out')
            }else {
                $(".editTaskBtn[value='"+tmp+"']").attr('class','editTaskBtn m-2 inline-block px-6 py-2.5 bg-blue-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out')
            }
            $('#edit-task-modal').fadeOut(300)
            notification("Successfully updated task")
            $('#edit-task-ticket').empty().append("<i class='fa-solid fa-pen'></i> Edit task")
        },
        error: function (e) {
            notification(e.responseJSON.message)
            $('#edit-task-ticket').empty().append("<i class='fa-solid fa-pen'></i> Edit task")
        }
    })
})
$(document).on('click','.delete-task',function () {
    $('.delete-task').empty().append("<i class='fa-solid fa-circle-notch fa-spin'></i>")
    $.ajax({
        url: '/manager/api/deleteTask',
        type: 'post',
        data: { 'ticket_id' : $('#ticket-id').val(), 'task_id': $('#edit-task-id').val() },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if(response === 'Successful'){
                $('.delete-task').empty().append(response)
              let  tmp =  $('#edit-task-id').val()
                $(".editTaskBtn[value='"+tmp+"']").remove()
                $('#edit-task-modal').fadeOut(300)
                notification("Successfully deleted task")
            }else {
                $('#edit-task-modal').fadeOut(300)
                notification("Insufficient authority!")
            }
            $('.delete-task').empty().append("<i class='fa-solid fa-trash'></i> Delete task")
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','#add-task-to-ticket',function () {
    $.ajax({
        url: '/manager/api/createTask',
        type: 'post',
        data: { 'ticket_id' : $('#ticket-id').val(), 'task_name' : $('#task-name').val(),
            'task_description' : $('#task-description').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            $('#task-section').append("<button type='button' value='"+response.id+"' class='editTaskBtn m-2 inline-block px-6 py-2.5 bg-blue-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
                ">"+response.task+"</button>")
            $('#task-modal').fadeOut(300)
            notification("Task successfully added.")
        },
        error: function (e){
            notification(e.responseJSON.message)
        }
    })
})
$(document).on('input','#search-for-worker',function () {
    $.ajax({
        url: '/manager/api/getWorkersToAssign',
        type: 'get',
        data: { 'search_term' : $('#search-for-worker').val(), 'department_name' : $('#department-name').text() },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if (response != null) {
                $('#available-workers').empty()
                $.each(response, function (i, user) {
                    $('#available-workers').append("<option value='" + user.id + "'>" + user.name + " " + user.surname + "</option>")
                })
            }
        }, error: function () {
            notification("An internal error occurred!")
        }
    })
})

$(document).on('click','.assignUserBtn',function () {
    $.ajax({
        url: '/manager/api/assignWorker',
        type: 'post',
        data: { 'user_id' : $('#worker-select').val(), 'ticket_id' : $('#ticket-id').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function () {
            location.reload()
        },
        error: function (){
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','.assignWorkerBtn',function () {
    if($('#available-workers option:selected').val() != null) {
        $.ajax({
            url: '/manager/api/assignWorker',
            type: 'post',
            data: {'user_id': $('#available-workers option:selected').val(), 'ticket_id': $('#ticket-id').val()},
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (response) {
                if(response === "Successful"){
                    $('#assigned-workers').append("<option value='" + $('#available-workers option:selected').val() + "'>" + $('#available-workers option:selected').text() + "</option>")
                    $('#available-workers option:selected').remove()
                    notification("Successfully assigned a worker!")
                }else if (response === "Duplicated"){
                    notification("This worker is already assigned to this ticket!")
                }else {
                    notification("An internal error occurred!")
                }
            },
            error: function () {
                notification("An internal error occurred!")
            }
        })

    }
})
$(document).on('click','.unassignWorkerBtn',function () {
    if ($('#assigned-workers option:selected').val() != null && $('#assigned-workers option:selected').val() != "NONE"){
        $.ajax({
            url: '/manager/api/unassignWorker',
            type: 'post',
            data: { 'user_id' : $('#assigned-workers option:selected').val(), 'ticket_id' : $('#ticket-id').val() },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function () {
                notification("Successfully unassigned a worker!")
            },
            error: function (){
                notification("An internal error occurred!")
            }
        })
        $('#available-workers').append("<option value='"+$('#assigned-workers option:selected').val()+"'>"+$('#assigned-workers option:selected').text()+"</option>")
        $('#assigned-workers option:selected').remove()
    }
})



$(document).on('click','#cancel-edit-change',function () {
    $('#edit-department-name-section').empty().append("<p id='department-name'>"+department_name+"</p>")
    $('#edit-button-section').empty().append("<button  type='button' data-mdb-ripple='trie' data-mdb-ripple-color='light' class='editDepartmentBtn inline-block px-6 py-2.5 bg-yellow-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-yellow-700 hover:shadow-lg focus:bg-yellow-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-yellow-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
        "><i class='fa-solid fa-pen'></i> Change department</button>")
})
$(document).on('click','.editDepartmentBtn',function () {
    department_name = $('#department-name').text()
    $.ajax({
        url: '/admin/api/getAllDepartments',
        type: 'get',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $('#edit-department-name-section').empty().append("<select name='department-selector' id='department-selector'></select>")
            $.each(response, function (i,department){
                $('#department-selector').append("<option value='"+department.id+"'>"+department.name+"</option>")
            })
            $('#edit-button-section').empty().append("<button id='approve-edit-change' type='button' data-mdb-ripple='trie' data-mdb-ripple-color='light' class='approve-edit-change m-2 inline-block px-6 py-2.5 bg-green-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-green-700 hover:shadow-lg focus:bg-green-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-green-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
                "><i class='fa-solid fa-check'></i> Approve</button>" +
                "<button id='cancel-edit-change' type='button' data-mdb-ripple='trie' data-mdb-ripple-color='light' class='m-2 inline-block px-6 py-2.5 bg-red-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-red-700 hover:shadow-lg focus:bg-red-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-red-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
                "><i class='fa-solid fa-xmark'></i> Cancel</button>");
        },
        error: function (){
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','.approve-edit-change',function () {
    $.ajax({
        url: '/admin/ticket/department/edit',
        type: 'post',
        data: { 'department_id' : $('#department-selector').val(), 'ticket_id' : $('#ticket-id').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            $('#edit-department-name-section').empty().append("<p id='department-name'>"+response+"</p>")
            $('#edit-button-section').empty().append("<button  type='button' data-mdb-ripple='trie' data-mdb-ripple-color='light' class='editDepartmentBtn inline-block px-6 py-2.5 bg-yellow-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-yellow-700 hover:shadow-lg focus:bg-yellow-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-yellow-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
                "><i class='fa-solid fa-pen'></i> Change department</button>")
            notification("Department changed successfully!")
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','#editButton',function () {
    let doc_description = $('#docs-description').text()
    let doc_id = $('#documentation-id').val()
    $('#documentation-area').empty().append("<p class='font-bold'>Documentation content:</p>\n" +
        "              <form action='/manager/api/editDocumentation' enctype='multipart/form-data' method='post'>\n" +
        "<input type='hidden' name='documentationid' value='"+doc_id+"'>" +
        "                <input type='hidden' name='ticketid' value='"+$('#ticket-id').val()+"'>\n" +
        "                <textarea name='content'  class='shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline' required>\n" + doc_description +
        "                     </textarea>\n" +
        " <input type='hidden' name='_csrf' value='"+token+"' />"+
        "                <p class='font-bold'>Attachments:</p>\n" +
        "                <input type='file' id='attachments' name='attachments' multiple='multiple'>\n" +
        "                <button type='submit' class='bg-yellow-500 hover:bg-yellow-700 pl-2 mt-2  text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline'><i class='fa-solid fa-pen'></i> Edit documentation</button>\n" +
        "              </form>")

})
$(document).on('click','.editStatusBtn',function () {
   currentStatus =  $('.ticketStatusValue').text();
    $('#ticketStatus').empty().append("Status <span><select id='status-selector'></select></span>" +
        "<button type='button' class='ml-2 approveStatusBtn text-white w-6 bg-green-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-check'></i></button>" +
        "<button type='button' class='ml-2 cancelStatusBtn text-white w-6 bg-red-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-xmark'></i></button>")
    $.ajax({
        url: '/manager/api/getAllStatuses',
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
        url: '/manager/api/changeTicketStatus',
        type: 'patch',
        data: { 'ticket-id': $('#ticket-id').val(), 'status-id' : $('#status-selector').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            if(response === "SMTP ERROR"){
                notification("Smtp server error!");
            }
            $('#ticketStatus').empty().append("Status: <span class='ticketStatusValue'>"+response+"</span>" +
                "<button type='button' class='ml-2 editStatusBtn text-white w-6 bg-yellow-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-pen-to-square'></i></button>")
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
$(document).on('click','.editPriorityBtn',function (){
    currentPriority =  $('.ticketPriorityValue').text();
    $('#ticketPriority').empty().append("Priority: <span><select id='priority-selector'></select></span>" +
        "<button type='button' class='ml-2 approvePriorityBtn text-white w-6 bg-green-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-check'></i></button>" +
        "<button type='button' class='ml-2 cancelPriorityBtn text-white w-6 bg-red-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-xmark'></i></button>")
    $.ajax({
        url: '/manager/api/getAllPriorities',
        type: 'get',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $.each(response,function (i,priority) {
                $('#priority-selector').append("<option value='"+priority.id+"'>"+priority.priority_name+"</option>")
            })
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','.approvePriorityBtn',function () {
    $.ajax({
        url: '/manager/api/changeTicketPriority',
        type: 'post',
        data: { 'ticket-id': $('#ticket-id').val(), 'priority-id' : $('#priority-selector').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $('#ticketPriority').empty().append("Status: <span class='ticketPriorityValue'>"+response+"</span>" +
                "<button type='button' class='ml-2 editPriorityBtn text-white w-6 bg-yellow-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-pen-to-square'></i></button>")
            notification("Successfully updated priority!")
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','.cancelPriorityBtn',function () {
    $('#ticketPriority').empty().append("Priority: <span class='ticketPriorityValue'>"+currentPriority+"</span>" +
        "<button type='button' class='ml-2 editPriorityBtn text-white w-6 bg-yellow-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-pen-to-square'></i></button>")
})
$(document).on('click','.deleteDocumentationAttachment',function () {
    let currentLi = $(this).closest("li")
    $.ajax({
        url: '/manager/api/deleteAttachmentFromDocumentation',
        type: 'post',
        data: { 'documentation_id' : $('#documentation-id').val(), 'attachment_id' : $(currentLi).val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            if(response === "Successfully"){
                notification("Attachment removed!")
                $(currentLi).remove()
            }
        },
        error: function (){
            notification("An internal error occurred!")
        }
    })

})