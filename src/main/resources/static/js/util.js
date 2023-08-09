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

const toBase64 = file => new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = () => {
        const base64String = reader.result
            .replace('data:', '')
            .replace(/^.+,/, '');

        resolve(base64String);
    };
    reader.onerror = reject;
});