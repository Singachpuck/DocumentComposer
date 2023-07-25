$(document).ready(() => {
    $.getJSON('api/v1/users', (data) => {
        for (let index in data) {
            $('#users').append('<div><div>' + data[index].username + '</div><div>' + data[index].password + '</div></div>')
        }
    })
});