// извлечение данных о компактинге нод
function getCompactionStat() {
    let spinner = $('#spinnerCompacting');
    let updateCompactingStat = $('#updateCompactingStat');
    updateCompactingStat.prop('disabled', true);
    spinner.show();

    $.ajax('/compactionStatByClusterName/' + clusterName, {
        method: 'get',
        dataType: 'json',
        success: function (data) {
            let compactionList = data.sort((a, b) => {
                let nodeA = a.node;
                let nodeB = b.node;

                if (nodeA.dataCenter > nodeB.dataCenter) {
                    return 1;
                } else if (nodeA.dataCenter < nodeB.dataCenter) {
                    return -1;
                } else {
                    if (nodeA.ip > nodeB.ip) {
                        return 1;
                    } else if (nodeA.ip < nodeB.ip) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            })

            // список хранилищ Кассандры
            let clusterNameList = $.unique($.map(compactionList, function (item) {
                return item.node.clusterName;
            })).sort();

            let table = $('#compactingTable');
            table.empty();

            // хранилища
            $.each(clusterNameList, function (index, clusterName) {
                table
                    .append($('<tr style="background: #989898"></tr>')
                        .append($('<td colspan="6"></td>')
                            .append($('<strong></strong>')
                                .append($('<small></small>')
                                    .append(clusterName)))));

                // для каждого хранилища находим состояние компактинга
                let found = $.grep(compactionList, function (compInfo) {
                    return compInfo.node.clusterName === clusterName;
                });

                // ноды
                $.each(found, function (index, compactingInfo) {

                    let nodeIP = $('<td><div class="row"><div class="col-9"><small><strong><a href="/tableList/' +
                        compactingInfo.node.ip +
                        '" target="_blank">' + compactingInfo.node.dataCenter + ' ' + compactingInfo.node.ip +
                        '</a></strong></small></div>' +

                        '<div class="col-3 d-grid gap-2 d-md-flex justify-content-md-end">' +
                        '    <button class="btn btn-danger me-md-2 btn-sm" type="button"' +
                        '       onclick="stopCompacting(\'' + compactingInfo.node.ip + '\');" ' +
                        'data-bs-toggle="tooltip" data-bs-placement="top" title="Остановить компактинг ноды ' +
                        compactingInfo.node.ip +
                        '"><i class="bi bi-stop-fill"></i></button></div></div></td>');

                    let row = $('<tr style="background: #c9c9c9"></tr>');
                    table.append(row
                        .append(nodeIP)
                        .append($('<td colspan="4"></td>').append($('<small></small>')
                            .append('Ожидание: ' + compactingInfo.pendingObjects.join(", "))))
                        .append($('<td class="text-center"></td>').append($('<small></small>')
                            .append(compactingInfo.timeRemaining)))
                    );

                    // таблицы, которые сейчас компактятся
                    let highLight = true;
                    $.each(compactingInfo.compactingObjects, function (index, compactingTable) {
                        let progress = $('<td class="text-center align-middle"><small>' +
                            '<div class="progress">' +
                            '    <div class="progress-bar" role="progressbar" style="width: ' + compactingTable.progress.replace(',', '.') +
                            ';" aria-valuenow="25"' +
                            '         aria-valuemin="0" aria-valuemax="100">' + compactingTable.progress +
                            '</div></div></small></td>');

                        highLight = false;
                        table.append($('<tr></tr>')
                            .append($('<td class="text-center"></td>').append($('<small></small>').append(compactingTable.status)))
                            .append($('<td class="text-center"></td>').append($('<small></small>').append(compactingTable.keySpace)))
                            .append($('<td class="text-center"></td>').append($('<small></small>').append(compactingTable.objectName)))
                            .append($('<td class="text-center"></td>').append($('<small></small>').append(compactingTable.currentBytes)))
                            .append($('<td class="text-center"></td>').append($('<small></small>').append(compactingTable.totalBytes)))
                            .append(progress)
                        );
                    });

                    // if (highLight === true) {
                    //     row.css("background-color", '#ec8080');
                    // }
                });

            });
            spinner.hide();
            updateCompactingStat.prop('disabled', false);
        },
        error: function (data, textStatus, jqXHR) {
            console.log('getCompactionStat error')
            console.log('data: ' + data);
            console.log('textStatus: ' + textStatus);
            console.log('jqXHR: ' + jqXHR);
        },
        complete: function () {
        }
    });
}

// остановка компактинга
function stopCompacting(ip) {
    if (confirm('Остановить все операции компактинга ноды ' + ip + '?') === false) {
        return;
    }

    $.ajax('/stopCompacting/' + ip, {
        method: 'get',
        dataType: 'text',
        success: function (data) {
            const result = JSON.parse(data);
            if (result.errorOutput !== '' || result.errorMessage !== '') {
                alert('Ошибка остановки компактинга. ErrorOutput: ' +
                    result.errorOutput + '. ErrorMessage: ' + result.errorMessage);
            }
        },
        error: function (data, textStatus, jqXHR) {
            console.log('getCompactionStat error')
            console.log('data: ' + data);
            console.log('textStatus: ' + textStatus);
            console.log('jqXHR: ' + jqXHR);
        },
        complete: function () {
        }
    });
}

