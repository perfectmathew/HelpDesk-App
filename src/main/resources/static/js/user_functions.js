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
        url: "/sendTicket",
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
                +"<div class='mb-6'><a href='/'><button class='bg-gray-800 hover:bg-gray-600  text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline'>Back to Home Page</button> </a></div>";
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
    let dziedzina = $('#dziedzina').val();
    let content = "<p  class='block font-bold text-gray-700'>Complete the form</p><hr>"
        + " <div class=\"mb-4\">"
        + "<label class=\"block text-gray-700 text-sm font-bold mb-2\" for='name'>Name</label>"
        + "<input type='text' placeholder='Name' class=\"shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline\" name='name' id='name' required>"
        + "</div>"
        + " <div class=\"mb-4\">"
        + "<label class=\"block text-gray-700 text-sm font-bold mb-2\" for='surname'>Surname</label>"
        + "<input type='text' placeholder='Surname' class=\"shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline\" name='surname' id='surname' required>"
        + "</div>"
        + "<div class='mb-6'>"
        + "<label class=\"block text-gray-700 text-sm font-bold mb-2\" for='email'>Contact</label>"
        + "<input type='email' placeholder='Email address' class=\"shadow appearance-none border rounded mb-3 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline\" name='email' id='email' required>"
        + "<input type='text' placeholder='Phone number' class=\"shadow appearance-none border rounded mb-3 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline\" name='phonenumber' id='phonenumber'>"
        + "</div>"
        + "<div class='mb-6'>"
        + "<textarea placeholder='Ticket content' class=\"shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline\" name='description' id='description' required></textarea>"
        + "</div>"
        +"<input type='hidden' value='"+dziedzina+"' id='selectedDepartment' name='selectedDepartment'>"
        + "<input type=\"file\" id='attachments' name='attachments' multiple=\"multiple\">"
        + "<button type='submit' id=\"submit-btn\" class=\"bg-gray-800 hover:bg-gray-600  text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline\">Send</button>"
    $('#content').fadeOut(1000,function (){
        $('#content').empty();
    });
    $('#content').fadeIn(500,function (){
        $('#content').append(content);
    });
});