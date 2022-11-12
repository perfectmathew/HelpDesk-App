let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
let department_name;
$(document).on('click','.assignUserBtn',function () {
    $.ajax({
        url: '/apiv2/assignWorker',
        type: 'post',
        data: { 'user_id' : $('#worker-select').val(), 'ticket_id' : $('#ticket-id').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function () {
            location.reload()
        },
        error: function (){
        }
    })
})
$(document).on('click','#cancel-edit-change',function () {
    $('#edit-department-name-section').empty().append("<p id='department-name'>"+department_name+"</p>")
    $('#edit-button-section').empty().append("<button  type='button' data-mdb-ripple='trie' data-mdb-ripple-color='light' class='editDepartmentBtn inline-block px-6 py-2.5 bg-yellow-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-yellow-700 hover:shadow-lg focus:bg-yellow-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-yellow-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
        "><i class='fa-solid fa-pen'></i> Change department</button>")
})
$(document).on('click','.editDepartmentBtn',function () {
    department_name = $('#department-name').text()
    $.ajax({
        url: '/admin/getAllDepartments',
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

        }
    })
})
$(document).on('click','#editButton',function () {
    let doc_description = $('#docs-description').text()
    $('#documentation-area').empty().append("<p class='font-bold'>Documentation content:</p>\n" +
        "              <form action='/api/editDocumentation' enctype='multipart/form-data' method='post'>\n" +
        "<input type='hidden' name='documentationid' value='"+$('#documentation-id').val()+"'>" +
        "                <input type='hidden' name='ticket' value='"+$('#ticket-id').val()+"'>\n" +
        "                <textarea name='content'  class='shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline' required>\n" + doc_description +
        "                     </textarea>\n" +
        "                <p class='font-bold'>Attachments:</p>\n" +
        "                <input type='file' id='attachments' name='attachments' multiple='multiple'>\n" +
        "                <button type='submit' class='bg-yellow-500 hover:bg-yellow-700 pl-2  text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline'><i class='fa-solid fa-pen'></i> Edit documentation</button>\n" +
        "              </form>")

})
$(document).on('click','.editProfileBtn',function () {
    $('#button-section').empty().append("<button type='button'  class='saveProfileBtn bg-green-500 hover:bg-green-700 focus:bg-green-800 text-white font-bold py-2 px-4 rounded-2xl focus:outline-none focus:shadow-outline mr-2'><i class='fa-solid fa-check'></i> Save</button>" +
        "<button type='button'  class='cancelEditProfileBtn bg-red-500 hover:bg-red-700 focus:bg-red-800 text-white font-bold py-2 px-4 rounded-2xl focus:outline-none focus:shadow-outline mr-2'><i class='fa-solid fa-xmark'></i> Cancel</button>")
})
$(document).on('click','#sort-by-id',function () {
    sortTable("id",this);
})
$(document).on('click','#sort-by-date',function () {
    sortTable("date",this);
})
$(document).on('change','#select-by-status',function () {
    $.ajax({
        url: "/api/findTicketsBy",
        type: 'GET',
        data: { "findBy" : "status", "findValue" : $('#select-by-status').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $('#table-content').empty();
            response_content = response;
            $.each(response,function (i,ticket) {
                $('#table-content').append("<tr  class='item flex w-full mb-4'>\n" +
                    "        <td  class='p-4 w-1/4'>"+ticket.id+"</td>\n" +
                    "        <td  class='p-4 w-1/4'>"+ticket.description+"</td>\n" +
                    "        <td  class='p-4 w-1/4'>"+ticket.notifier_name + '  '+ ticket.notifier_surname +"</td>\n" +
                    "        <td  class='p-4 w-1/4'>"+ moment(new Date(ticket.ticket_time)).format('MM dd YYYY, h:mm:ss') +"</td>\n" +
                    "        <td  class='p-4 w-1/4'>"+ticket.status.status+"</td>\n" +
                    "        <td  class='p-4 w-1/4'>\n" +
                    "            <button type='button' class='editTicketBtn text-white w-12 sm:w-6 bg-yellow-500 hover:text-black rounded ease-in-out'><a href='/t/"+ticket.id+"'><i class='fa-solid fa-pen-to-square'></i></a></button>\n" +
                    "            <button type='button' class='deleteTicketBtn text-white w-12 sm:w-6 bg-red-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-trash'></i></button>\n" +
                    "        </td>\n" +
                    "    </tr>")
                if (ticket.status.status === "NEW"){
                    $('.item').addClass('bg-green-500 bg-opacity-20');
                }
            })

        },
        error: function () {

        }
    })
})
function sortTable(column, me) {
    let table = $(me).parents('table').eq(0),
        rows = table.find('tr:gt(0)').toArray().sort(doComparer($(this).index()))
    me.asc = !me.asc
    if (!me.asc) {
        rows = rows.reverse()
    }

    for (var i = 0; i < rows.length; i++) {
        table.append(rows[i])
    }
}
function doComparer(index) {
    return function(a, b) {
        a = getCellValue(a, index);
        b = getCellValue(b, index);
        return $.isNumeric(a) && $.isNumeric(b) ? a - b : a.toString().localeCompare(b)
    }
}
function getCellValue(row, index) {
    return $(row).children('td').eq(index).text()
}