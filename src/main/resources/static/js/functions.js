
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
                    "<td class='p-4 w-1/4'>" + ticket.priority.priority_name + "</td>" +
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