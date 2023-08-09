'use strict'

$(document).ready(() => {
    let templateForm = $('#template-upload'),
        fileField = templateForm.find('#template'),
        nameField = templateForm.find('#template-name'),
        formatField = templateForm.find('#template-format'),
        submit = templateForm.find('.save-template'),
        refreshTemplatesBtn = $('#refresh-templates'),
        templatesSection = $('.templates');

    refreshTemplatesBtn.click(() => {
        $.ajax({
            url: 'api/v1/templates',
            method: 'GET',
            success: (data) => {
                templatesSection.empty();
                for (let template of data) {
                    $('<div></div>')
                        .append($(`<div>Id: ${template.id}</div>`))
                        .append($(`<div>Name: ${template.name}</div>`))
                        .append($(`<div>Created: ${template.created}</div>`))
                        .append($(`<div>Size: ${template.size}</div>`))
                        .appendTo(templatesSection);
                }
            }
        })
    });

    refreshTemplatesBtn.click();

    submit.click(async e => {
        e.preventDefault();
        let templateFile = fileField.prop('files')[0];

        let formData = new FormData(templateForm[0]);
        // formData.append(fileField.attr('name'), templateData);
        // formData.append(nameField.attr('name'), nameField.val());
        // formData.append(formatField.attr('name'), formatField.val());
        formData.append('size', templateFile.size);

        let templateObject = {};
        formData.forEach((value, key) => templateObject[key] = value);
        templateObject['bytes'] = await toBase64(templateFile);
        delete templateObject.template;
        console.log(templateObject);
        let payload = JSON.stringify(templateObject);

        $.ajax({
            url: templateForm.attr("action"),
            method: templateForm.attr("method"),
            // processData: false,
            contentType: 'application/json',
            data: payload,
            success: () => {
                displayMessageSuccess('Template created.');
            },
            error: () => {
                displayMessageError('Template creation error.');
            }
        });
    })
});