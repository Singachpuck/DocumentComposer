'use strict'

const fadeTime = 500;

function displayMessageSuccess(text, delay=3000) {
    displayMessage(text, delay, 'alert-success');
}

function displayMessageError(text, delay=3000   ) {
    displayMessage(text, delay, 'alert-error');
}

function displayMessage(text, delay, modifier) {
    let messageBlock = $('<div></div>')
        .text(text)
        .addClass('message')
        .addClass('alert')
        .addClass(modifier);

    $('body').append(messageBlock);
    messageBlock.fadeOut(0);

    messageBlock.fadeIn(fadeTime, function () {
        setTimeout(() => {
            $(this).detach();
        }, delay);
    });
}