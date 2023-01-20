$(document).on('click','.editTicketBtn',function () {
    let currentTD = $(this).closest("tr").find("td");
    window.location.replace($(currentTD).eq(0).text())
})
// REGISTER SECTION //
$(document).on('submit','#registration-form',function (e) {
    e.preventDefault();
    let form = $(this);
    $('#register-button').hide()
    $('#register-post-button').show()
    $.ajax({
        url: '/signup',
        type: 'POST',
        data: form.serialize(),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (user) {
          let content = "<div class='mt-2 text-center'>" +
                "<p class='text-2xl font-bold'>Welcome "+user.name+" on board!</p>" +
                "<p>Your account has been created, but you can not yet log into it before it is approved by the administrator.</p>" +
                "<p>We will notify you when your account is active by sending an email to the email address you entered in the form.</p>" +
                "<p class='font-bold'>Thanks for yours trust, Help Desk System</p>" +
                " <button id='return-button' type='button' class='inline-block px-6 py-2.5 bg-green-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-green-700 hover:shadow-lg focus:bg-green-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-green-800 active:shadow-lg transition duration-150 ease-in-out'" +
                "><i class='fa-solid fa-check'></i> Return to Home page</button>" +
                "</div>";
          $('#form-content').empty().append(content)
        },
        error: function (e){
            $('#register-button').show()
            $('#email').removeClass('border').addClass('border-2 border-red-500')
            $('#register-post-button').hide()
           $('#responseMessage').removeClass('hidden').text(e.responseJSON.message)
          notification(e.responseJSON.message);
        }
    })
})

// TICKET HANDLER //
// SEND TICKET SECTION //

$( "#ticket-form" ).on( "submit", function(e) {
    $('#content').fadeOut(500,function (){
        $('#content').empty()
    })
    $('#content').fadeIn(250,function () {
        $('#content').append("<div class='mb-6 text-gray-800'><i class='fa-solid fa-cog fa-spin fa-2xl'></i></div>");
    });
    let form = $('#ticket-form')[0];
    let form_data = new FormData(form);
    $.ajax({
        type: "POST",
        url: "/user/api/sendTicket",
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        data: form_data,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function () {
            notification("Successfully sent ticket!")
            $('#content').fadeOut(1000,function (){
                $('#content').empty()
            })
            let content = "<div class='mb-6 pt-1 text-green-500'> <i class=\"fa-solid fa-circle-check fa-2xl\"></i> </div>"
                + "<div class='mb-6'><h1  class='block font-bold text-gray-700'>Thank you for submitting your ticket!</h1></div> "
                + "<div class='mb-6'><p class='block font-bold text-gray-700'>To the email address provided, a link was sent to track progress on this submission!</p> </div>"
                +"<div class='mb-6'><a href='/home'><button class='bg-gray-800 hover:bg-gray-600  text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline'>Back to Home Page</button> </a></div>";
            $('#content').fadeIn(500,function () {
                $('#content').append(content);
            });
        },
        error: function (){
            notification("An error occurred while sending the request!")
        }
    });
    e.preventDefault();
});
$(document).on('click','#next-btn',function (){
    let department = $('#department-select').val();
    let content = "<p  class='block font-bold text-gray-700'>Complete the form</p><hr>"
        + "<div class='mb-6'>"
        + "<textarea placeholder='Ticket content' class=\"shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline\" name='description' id='description' required></textarea>"
        + "</div>"
        +"<input type='hidden' value='"+department+"' id='selectedDepartment' name='selectedDepartment'>"
        + "<input type=\"file\" id='attachments' name='attachments' multiple=\"multiple\">"
        + "<button type='submit' id='submit-btn' class='bg-gray-800 hover:bg-gray-600  text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline'>Send</button>"
    $('#content').fadeOut(1000,function (){
        $('#content').empty();
    });
    $('#content').fadeIn(500,function (){
        $('#content').append(content);
    });
});

// TICKET HANDLERS //
// SOLUTION SECTION //

$(document).on('click','.acceptSolution',function () {
    let request = "/user/api/t/"+$('#ticket-id').val()+"/solution/true"
    $.ajax({
        url: request,
        type: 'patch',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (){
            $('#ticket-solution-section').empty()
            notification("The operation was successful!");
        },
        error: function (e) {
            notification(e.responseJSON.message);
        }
    })
})
$(document).on('click','.rejectSolution',function () {
    $('#reject-solution-id').val($('#solution-id-section').val())
    $('#reject-solution-modal').fadeIn(300)
})
$('#reject-solution-description').on('keypress keydown keyup',function () {
    $('#numbers-of-letters').text($(this).val().length)
    if ($(this).val().length > 255){
        $('#letters-total').addClass('text-red-500 font-bold')
    }else {
        $('#letters-total').removeClass('text-red-500 font-bold').addClass('text-black font-light')
    }
})

$(document).on('click','#approve-reject-solution',function () {
    if($('#reject-solution-description').val().length > 255){
        notification("Reject description must contain less letters than 256.")
    }
    else {
        $.ajax({
            url: '/user/api/solution/reject',
            type: 'patch',
            data: {
                'solution_id' : $('#reject-solution-id').val(),
                'reject_solution_text': $('#reject-solution-description').val(),
                'ticket_id': $('#ticket-id').val()
            },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (solution) {
                $('#ticket-solution-section').empty()
                $('.rejection-description').append("<p class='font-semibold text-red-500'>"+solution.rejection_description+"</p>")
                $('#reject-solution-modal').fadeOut(300)
            },
            error: function (e) {
                notification(e.responseJSON.message);
            }
        })
    }
})

// VALIDATION FORMS //
$(document).on('keypress keydown keyup','#name,#surname,#phone_number',function () {
    validate()
})
function validate(){
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