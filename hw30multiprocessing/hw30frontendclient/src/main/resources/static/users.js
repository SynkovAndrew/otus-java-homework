const connect = () => {
    const pathname = window.location.pathname;
    stompClient = Stomp.over(new SockJS('/web-socket-connection'));
    stompClient.connect({}, (frame) => {
        stompClient.subscribe(pathname + 'topic/users', (users) => {
            document.getElementById("userContainer").innerHTML =
                createUserTable(JSON.parse(users.body).content);
        });
    });
};

const disconnect = () => {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
};

function createUserTable(users) {
    let table = "<b>Users</b>" +
        "<table id='users-table'>" +
        "<tr>" + th('Id') + th('Name') + th('Age') + " </tr>";
    for (let i = 0; i < users.length; i++) {
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
    connect();
}

$(function () {
    $('input#create-button').click(function (e) {
        e.preventDefault();
        const pathname = window.location.pathname;
        stompClient.send(
            pathname + "app/user",
            {},
            JSON.stringify({name: $('#name-input').val(), age: $('#age-input').val()})
        )
    });
});

$(window).on("beforeunload", function () {
    disconnect();
});


