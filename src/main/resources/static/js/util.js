'use strict';

const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});

function convertRegisterFormToPayload(formData) {
    let payload = {};
    for (let field of formData) {
        payload[field.name] = field.value;
    }
    return JSON.stringify(payload);
}

function getLastPathSegment() {
    return window.location.pathname.split("/").pop();
}