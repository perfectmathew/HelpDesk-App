let long = false;
$(document).on("click", ".ticket-description", function (e) {
    if(!long){
        long=true;
        let max = 0;
        $(this).css("-webkit-line-clamp", "99");
        $($(this).parent()).find(".elementText").each(function(){
            $(this).css("-webkit-line-clamp", "99");
            max = Math.max(max, parseInt($(this).css( "height" )) );
        });
        $($(this).parent()).find(".elementText").each(function(){
            $(this).height(max);
        });
    }
    else{
        long=false;
        $(this).css("-webkit-line-clamp", "1");
        $(this).height('auto');
        $($(this).parent()).find(".elementText").each(function(){
            $(this).css("-webkit-line-clamp", "1").height('auto');
        });
    }
});
$(document).on('click','.editProfileBtn',function () {
    $.ajax({
        url: '/api/getUserDetails',
        type: 'get',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (user) {
            $('#user-details-section').empty().append("<div class='mb-6'>" +
                "<p class='block text-gray-800 text-sm font-bold mb-2'>Name</p>" +
                "<input type='text' value='"+user.name+"' id='profile-user-name' class='shadow-md mb-2 border p-2 rounded w-full' required>" +
                "<p class='block text-gray-800 text-sm font-bold mb-2'>Surname</p>" +
                "<input type='text' value='"+user.surname+"' id='profile-user-surname' class='shadow-md mb-2 border p-2 rounded w-full' required>" +
                "<p class='block text-gray-800 text-sm font-bold mb-2'>E-mail</p>" +
                "<input type='email' value='"+user.email+"' id='profile-user-email' class='shadow-md mb-2 border p-2 rounded w-full' required>" +
                "<p class='block text-gray-800 text-sm font-bold mb-2'>Phone number</p>" +
                "<input type='tel' value='"+user.phone_number+"' id='profile-user-phone' class='shadow-md mb-2 border p-2 rounded w-full' required>" +
                "<p class='block text-gray-800 text-sm font-bold mb-2'>New password (if you don't want to change it leave this field blank)</p>" +
                "<input type='password' id='profile-user-password' class='shadow-md mb-2 border p-2 rounded w-full'>" +
                "</div>")
            $('#button-section').empty().append("<button type='button'  class='saveProfileBtn bg-green-500 hover:bg-green-700 focus:bg-green-800 text-white font-bold py-2 px-4 rounded-2xl focus:outline-none focus:shadow-outline mr-2'><i class='fa-solid fa-check'></i> Save</button>" +
                "<button type='button'  class='cancelEditProfileBtn bg-red-500 hover:bg-red-700 focus:bg-red-800 text-white font-bold py-2 px-4 rounded-2xl focus:outline-none focus:shadow-outline mr-2'><i class='fa-solid fa-xmark'></i> Cancel</button>")
        },
        error: function () {

        }
    })
})
$(document).on('click','.saveProfileBtn',function () {
    let oldPassword = $('#profile-user-password').val()
    let newPassword;
    newPassword = !$.trim(oldPassword).length ? "null" : oldPassword;
    $.ajax({
        url: '/api/updateUserDetails',
        type: 'patch',
        data: { 'user-name' : $('#profile-user-name').val(), 'user-surname' : $('#profile-user-surname').val(),
        'user-email' : $('#profile-user-email').val(), 'user-phone' : $('#profile-user-phone').val(), 'user-password' : newPassword },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if(response === "This email address already exists"){
                $('#profile-user-email').addClass("border-2 border-red-500")
            }
            if(response === "Data has been updated successfully"){
                $.ajax({
                    url: '/api/getUserDetails',
                    type: 'get',
                    beforeSend: function(xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success: function (user){
                        $('#user-details-section').empty().append("<h1 class=\"block text-gray-700 text-4xl font-bold mb-2\">"+user.name +" "+user.surname+"</h1>\n" +
                            "          <hr>\n" +
                            "          <div class=\"mb-6\">\n" +
                            "            <p class=\"block text-gray-800 text-sm font-bold mb-2\">Email: <span class=\"text-gray-800 text-sm font-medium\">"+user.email+"</span></p>\n" +
                            "            <p class=\"block text-gray-800 text-sm font-bold mb-2\">Phone number: <span class=\"text-gray-800 text-sm font-medium\">"+user.phone_number+"</span></p>\n" +
                            "            <p class=\"block text-gray-800 text-sm font-bold mb-2\">Department: <span class=\"text-gray-800 text-sm font-medium\">"+user.department.name+"</span></p>\n" +
                            "            <p class=\"block text-gray-800 text-sm font-bold mb-2\">Active roles:</p>\n" +
                            "<section id='profile-roles-section'></section>" +
                            "</div>")
                        $.each(user.roleSet, function (i, role){
                            $('#profile-roles-section').append("<span class='inline-flex items-center py-1 px-2 mr-2 text-sm font-medium text-blue-800 bg-blue-100 rounded dark:bg-blue-200 dark:text-blue-800'>\n" + role.name +
                                " </span>")
                        })
                        $('#button-section').empty().append("<button type='button' class='editProfileBtn bg-yellow-500 hover:bg-yellow-600 focus:bg-yellow-800 text-white font-bold py-2 px-4 rounded-2xl focus:outline-none focus:shadow-outline'><i class='fa-solid fa-pen'></i> Edit profile</button>")
                    }
                })
            }
            notification(response)
        },
        error: function () {

        }
    })
})  
$(document).on('click','.cancelEditProfileBtn',function () {
    location.reload();
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

    for (let i = 0; i < rows.length; i++) {
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
function validateEditForm(){
    if (!$('.username').val().match("^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*$")){
        $('.username').removeClass('border').addClass('border-2 border-red-500')
    }else {
        $('.username').removeClass('border-2 border-red-500').addClass('border')
    }
    if(!$('.surname').val().match("^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*$")){
        $('.surname').removeClass('border').addClass('border-2 border-red-500')
    }else {
        $('.surname').removeClass('border-2 border-red-500').addClass('border')
    }
    if(!$('.phone-number').val().match("^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{3,6}$")){
        $('.phone-number').removeClass('border').addClass('border-2 border-red-500')
    }else {
        $('.phone-number').removeClass('border-2 border-red-500').addClass('border')
    }
}