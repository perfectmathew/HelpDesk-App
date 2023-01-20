function solution_section(){
    return "<h3 class='font-bold'>Please enter the solution of the ticket <span class='text-red-500 text-sm'>(Without a solution, you cannot submit your ticket for verification)</span></h3>" +
        "<textarea id=\"solution-text\" placeholder=\"Solution content\" class=\"shadow appearance-none border rounded w-full mt-2 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline\" required></textarea>\n" +
        "<button type=\"button\" data-mdb-ripple=\"true\" data-mdb-ripple-color=\"light\" class=\"addSolution mt-2 inline-block px-6 py-2.5 bg-green-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-green-700 hover:shadow-lg focus:bg-green-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-green-800 active:shadow-lg transition duration-150 ease-in-out\"\n" +
        "> <i class='fa-solid fa-plus'></i> Add solution</button>\n" +
        "<button type=\"button\" data-mdb-ripple=\"true\" data-mdb-ripple-color=\"light\" class=\"solutionDbOpen mt-2 inline-block px-6 py-2.5 bg-blue-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out\"\n" +
        "> <i class='fa-solid fa-database'></i> Get from solution database</button>";
}
function solution_table_template(id, solution) {
    return "<tr class='border-b border-gray-200 dark:border-gray-700'>" +
        "<td class='hidden'>"+id+"</td>" +
        "<th scope='row' class='px-6 py-4 font-medium text-gray-900 whitespace-nowrap bg-gray-50 dark:text-white dark:bg-gray-800'>"+solution+"</th>" +
        "<td class='px-6 py-3'><button type='button' class='copySolution inline-block px-6 py-2.5 bg-blue-500 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out'\n" +
        "> <i class='fa-solid fa-copy'></i> Copy solution</button></td> " +
        "</tr>"
}