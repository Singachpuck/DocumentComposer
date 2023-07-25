'use strict'

$(document).ready(() => {
    let $document = $(document),
        form = $document.find('#register-user'),
        error = form.find('.error'),
        submit = form.find('button.sign-up');

    error.hide();

    submit.click((e) => {
        e.preventDefault();
        let userData = convertRegisterFormToPayload(form.serializeArray());
        $.ajax({
            type: 'POST',
            url: '/api/v1/users',
            data: userData,
            contentType: 'application/json',
            success: () => {
                window.location.replace('/login?created');
            },
            error: (xhr, textStatus, errorMsg) => {
                displayMessageError('Registration failed!');
            }
        });
    })
})