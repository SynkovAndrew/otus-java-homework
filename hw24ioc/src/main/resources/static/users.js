function loadUsers() {
    const pathname = window.location.pathname;
    fetch(pathname + 'user')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            document.getElementById("userContainer").innerHTML = createUserTable(data);
        });
}

function createUserTable(users) {
    var table = "<b>Users</b>" +
        "<table id='users-table'>" +
        "<tr>" + th('Id') + th('Name') + th('Age') + " </tr>";
    for (var i = 0; i < users.length; i++) {
        table += "<tr>" + td(users[i].id) + td(users[i].name) + td(users[i].age) + "</tr>";
    }
    table += "</table>";
    return table;
}

function th(value) {
    return '<th id="users-th">' + value + '</th>';
}

function td(value) {
    return '<td id="users-td">' + value + '</td>';
}

function renderPage() {
    loadUsers();
}

$(function () {
    $('input#create-button').click(function (e) {
        e.preventDefault();
        const pathname = window.location.pathname;
        $.ajax({
            url: pathname + 'user',
            type: 'post',
            data: JSON.stringify({name: $('#name-input').val(), age: $('#age-input').val()}),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            success: function () {
                renderPage();
            }
        });
    });
});
