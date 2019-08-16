function loadUsers() {
    fetch('/user')
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
    console.log("ready!");
    $('input#create-button').click(function (e) {
        e.preventDefault();
        $.post('/user', $('form#create-user-form').serialize(), function () {
                renderPage();
            }, 'json'
        );
    });
});
