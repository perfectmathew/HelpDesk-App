$(document).on('click','.editTicketBtn',function () {
    let currentTD = $(this).closest("tr").find("td");
    window.location.replace("/t/"+$(currentTD).eq(0).text())
})
$(document).on('click','.showTaskBtn',function () {
    $.ajax({
        url: '/api/getTaskInfo',
        type: 'get',
        data: { 'task_id' : $(this).val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            $('#show-task-id').val(response.id)
            $('#show-task-name').text(response.task)
            $('#show-task-description').text(response.description)
            $('#show-task-modal').fadeIn(300)
        },
        error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).on('click','#show-done-task-ticket',function () {
    $(this).empty().append("<i class='fa-solid fa-circle-notch fa-spin'></i>")
    $.ajax({
       url: '/worker/api/markTaskAsDone',
        type: 'patch',
        data: { 'task-id' : $('#show-task-id').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
           if (response === "Successfully"){
               let  tmp =  $('#show-task-id').val()
               $(".showTaskBtn[value='"+tmp+"']").remove()
               notification("Task accomplished! Time for some coffee :)")
           }

            $('#show-task-modal').fadeOut(300)
        },
        error: function () {
            notification("An internal error occurred!")
            $('#show-task-modal').fadeOut(300)
        }
    })
})
$(document).on('click','.editStatusBtn',function () {
    currentStatus =  $('.ticketStatusValue').text();
    $('#ticketStatus').empty().append("Status <span><select id='status-selector'></select></span>" +
        "<button type='button' class='ml-2 approveStatusBtn text-white w-6 bg-green-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-check'></i></button>" +
        "<button type='button' class='ml-2 cancelStatusBtn text-white w-6 bg-red-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-xmark'></i></button>")
    $.ajax({
        url: '/worker/api/getAllStatuses',
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
        url: '/worker/api/changeTicketTStatus',
        type: 'patch',
        data: { 'ticket-id': $('#ticket-id').val(), 'status-id' : $('#status-selector').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response){
            if(response === "SMTP ERROR"){
                notification("Smtp server error!");
            }
            $('#ticketStatus').empty().append("Status: <span class='ticketStatusValue'>"+response+"</span>")
            if (response != 'VERIFICATION'){
                $('#ticketStatus').append("<button type='button' class='ml-2 editStatusBtn text-white w-6 bg-yellow-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-pen-to-square'></i></button>")
            }
            notification("Successfully updated status!")
        },
        error: function (e) {
            notification(e.responseJSON.message)
        }
    })
})
$(document).on('click','.cancelStatusBtn',function () {
    $('#ticketStatus').empty().append("Status: <span class='ticketStatusValue'>"+currentStatus+"</span>" +
        "<button type='button' class='ml-2 editStatusBtn text-white w-6 bg-yellow-500 hover:text-black rounded ease-in-out'><i class='fa-solid fa-pen-to-square'></i></button>")
})
$(document).on('click','#editButton',function () {
    let doc_description = $('#docs-description').text()
    let doc_id = $('#documentation-id').val()
    $('#documentation-area').empty().append("<p class='font-bold'>Documentation content:</p>\n" +
        "              <form action='/worker/api/editDocumentation' enctype='multipart/form-data' method='post'>\n" +
        "<input type='hidden' name='documentationid' value='"+doc_id+"'>" +
        "                <input type='hidden' name='ticketid' value='"+$('#ticket-id').val()+"'>\n" +
        "                <textarea name='content'  class='shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline' required>\n" + doc_description +
        "                     </textarea>\n" +
        " <input type='hidden' name='_csrf' value='"+token+"' />"+
        "                <p class='font-bold'>Attachments:</p>\n" +
        "                <input type='file' id='attachments' name='attachments' multiple='multiple'>\n" +
        "                <button type='submit' class='bg-yellow-500 hover:bg-yellow-700 pl-2 mt-2 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline'><i class='fa-solid fa-pen'></i> Edit documentation</button>\n" +
        "              </form>")

})
$(document).on('click',".addSolution",function () {
    $.ajax({
        url: '/worker/api/solution/add',
        type: 'post',
        data: { 'solution_text' : $('#solution-text').val(), 'ticket_id': $('#ticket-id').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (solution) {
            $('#solution-section').empty().append("<p class='font-bold'>Your solution:</p>" +
                "<p class='hidden' id='solution-id'>"+solution.id+"</p> " +
                "<p class='font-light'>"+solution.solution+"</p>" +
                "<button type='button' data-mdb-ripple='true' data-mdb-ripple-color='light' class='editSolution mt-2 inline-block px-6 py-2.5 bg-yellow-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-yellow-600 hover:shadow-lg focus:bg-yellow-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-yellow-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
                "> <i class='fa-solid fa-pen'></i> Edit</button>\n" +
                "<button  type='button' data-mdb-ripple='true' data-mdb-ripple-color='light' class='deleteSolution mt-2 inline-block px-6 py-2.5 bg-red-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-red-700 hover:shadow-lg focus:bg-red-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-red-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
                "> <i class='fa-solid fa-trash'></i> Delete solution</button>")
        },
        error: function (e) {
            notification(e.responseJSON.message);
        }
    })
})
$(document).on('click','.deleteSolution',function () {
    $.ajax({
        url: '/worker/api/solution/delete',
        type: 'delete',
        data: { 'solution_id' : $('#solution-id').text(), 'ticket_id': $('#ticket-id').val() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            $('#solution-section').empty().append("<h3 class='font-bold'>Please enter the solution of the ticket <span class='text-red-500 text-sm'>(Without a solution, you cannot submit your ticket for verification)</span></h3>" +
                "<textarea id=\"solution-text\" placeholder=\"Solution content\" class=\"shadow appearance-none border rounded w-full mt-2 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline\" required></textarea>\n" +
                "<button type=\"button\" data-mdb-ripple=\"true\" data-mdb-ripple-color=\"light\" class=\"addSolution mt-2 inline-block px-6 py-2.5 bg-green-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-green-700 hover:shadow-lg focus:bg-green-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-green-800 active:shadow-lg transition duration-150 ease-in-out\"\n" +
                "> <i class='fa-solid fa-plus'></i> Add solution</button>\n" +
                "<button type=\"button\" data-mdb-ripple=\"true\" data-mdb-ripple-color=\"light\" class=\"solutionDbOpen mt-2 inline-block px-6 py-2.5 bg-blue-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out\"\n" +
                "> <i class='fa-solid fa-database'></i> Open solution database</button>")
        },
        error: function (e) {
            notification(e.responseJSON.message);
        }
    })
})
$(document).on('click','.editSolution',function () {
    $.ajax({
        url: '/worker/api/solution/get',
        type: 'get',
        data: { 'solution_id' : $('#solution-id').text() },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (solution) {
            $('#show-solution-id').val(solution.id)
            $('#show-solution-description').val(solution.solution);
            $('#show-solution-modal').fadeIn(300)
        },
        error: function (e) {
            notification(e.responseJSON.message)
        }
    })
})
$(document).on('click','#approve-edit-solution',function () {
    $.ajax({
        url: '/worker/api/solution/edit',
        type: 'patch',
        data: {
            'solution_id' : $('#show-solution-id').val(),
            'solution_text' : $('#show-solution-description').val()
        },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (solution) {
            $('#solution-content').text(solution.solution)
            $('#show-solution-modal').fadeOut(300)
            notification("The content of the solution has been changed correctly.")
        },
        error: function (e) {
            notification(e.responseJSON.message)
        }
    })
})
$(document).on('click','.solutionDbOpen',function () {
    $('#show-solution_db-modal').fadeIn(300)
})
$(document).on('click','.copySolution',function () {
    let currentTD = $(this).closest("tr").find("th");
    $('#solution-text').val($(currentTD).eq(0).text())
    $('#show-solution_db-modal').fadeOut(200)
})
$(document).on('input','#search-in-solution-db',function () {
    $.ajax({
        url: '/worker/api/solution/getAllByText',
        type: 'get',
        data: { 'text' : $(this).val() },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            if (response != null && response.length) {
                $('#solution-db-table').empty()
                $.each(response,function (i,solution) {
                    $('#solution-db-table').append(solution_table_template(solution.id,solution.solution))
                })
            }
        }, error: function () {
            notification("An internal error occurred!")
        }
    })
})
$(document).ready(function () {
    let totalPages = 1;
    function getPage(startPage){
        $.ajax({
            url: '/worker/api/solution/getAll',
            type: 'GET',
            data: {
                page: startPage,
                size: 25
            },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            }, success: function (response) {
                $('#solution-db-table > tbody').empty()
                $.each(response.content, (i,solution) => {
                    $('#solution-db-table > tbody').append(solution_table_template(solution.id,solution.solution))
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
