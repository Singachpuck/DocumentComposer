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

    submit.click((e) => {
        e.preventDefault();

        let templateData = fileField.prop('files')[0];

        let formData = new FormData();
        formData.append(fileField.attr('name'), templateData);
        formData.append(nameField.attr('name'), nameField.val());
        formData.append(formatField.attr('name'), formatField.val());
        formData.append('size', templateData.size);

        $.ajax({
            url: templateForm.attr("action"),
            method: templateForm.attr("method"),
            processData: false,
            contentType: false,
            data: formData,
            success: () => {
                displayMessageSuccess('Template created.');
            },
            error: () => {
                displayMessageError('Template creation error.');
            }
        });
    })
});