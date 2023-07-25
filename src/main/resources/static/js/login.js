'use strict'

$(document).ready(() => {
    if (params.error !== null) {
        displayMessageError('Invalid username and password.')
    }
    if (params.logout !== null) {
        displayMessageSuccess('You have been logged out.')
    }
    if (params.created !== null) {
        displayMessageSuccess('User created. Please Log In.')
    }
});