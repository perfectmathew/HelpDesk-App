function user_table_template(id, name, surname, email, phone_number, department, enabled){
    let enable_text;
    if (enabled === true){
        enable_text =  "<button class='lockAccountBnt font-medium  text-gray-400 hover:underline'><span class='account-status-section'> <i class='fa-solid fa-lock'></i> Disable</span></button>"
    }else {
        enable_text = "<button class='unlockAccountBnt font-medium  text-green-400 hover:underline'><span class='account-status-section'><i class='fa-solid fa-lock-open'></i> Enable</span></button>"
    }
    return "<tr class='border-b bg-gray-900 border-gray-700'>" +
        "<td class='hidden'>" +
        "" + id +
        "</td>" +
        "<td class='py-4 px-6 font-medium text-white whitespace-nowrap'>" +
        "" + name +
        "</td>" +
        "<td class='py-4 px-6'>" +
        "" + surname +
        "</td>" +
        "<td class='py-4 px-6'>" +
        "" + email +
        "</td>" +
        "<td class='py-4 px-6'>" +
        "" + phone_number +
        "</td>\n" +
        "<td class='py-4 px-6'>" +
        "" + department +
        "</td>" +
        "<td class='py-4 px-6'>" +
        "<button  class='editUserBtn font-medium text-yellow-500 hover:underline'><i class='fa-solid fa-pen'></i> Edit</button>" +
        "<button  class='deleteUsrBtn font-medium text-red-500 hover:underline'><i class='fa-solid fa-trash'></i> Delete</button>" +
        enable_text +
        "</td>" +
        "</tr>"
}