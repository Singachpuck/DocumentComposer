$(document).ready(() => {
    let template = $('#template'),
        display = $('#display'),
        templateId = getLastPathSegment();

    (function load() {
        $.ajax({
            url: '/api/v1/templates/' + templateId,
            method: 'GET',
            success: (templateData) => {
                template.empty();
                $('<div></div>')
                    .append($(`<div>Id: ${templateData.id}</div>`))
                    .append($(`<div>Name: ${templateData.name}</div>`))
                    .append($(`<div>Created: ${templateData.created}</div>`))
                    .append($(`<div>Size: ${templateData.size}</div>`))
                    .appendTo(template);
            }
        });
        display.empty();
        $('<iframe></iframe>')
            .attr('src', `http://localhost:8080/templates/view/1` )
            .attr('width', '800px')
            .attr('height', '200px')
            .appendTo(display);
    })();
});