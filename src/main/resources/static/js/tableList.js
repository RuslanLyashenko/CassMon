let tableStatList;
let sortByField = 'tableName';
let sortOrder = 'asc';
let activeKS;

// извлечение данных о всех таблицах ноды
function getTableStat(ip) {
    let progress = $('#progress');
    progress.show();

    $.ajax('/tableStats/' + ip, {
        method: 'get',
        dataType: 'json',
        success: function (data) {
            tableStatList = data;
            makeTable('');
            progress.hide();
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

// отображение данных таблицы
function makeTable(activeKeySpace) {
    activeKS = activeKeySpace;

    // извлечение списка keySpace
    let keySpaces = Object.keys(tableStatList).sort();

    // если запуск первый, в качестве текущего выбирается первый keySpace из списка
    if (activeKeySpace === '') {
        activeKeySpace = keySpaces[0];
    }

    // очистка ранее заполненных таблиц
    let tab = $('#tab');
    let tableList = $('#tableList');
    tab.empty();
    tableList.empty();

    // построение закладок с keySpace
    $.each(keySpaces, function (index, keySpace) {
        // если закладка текущая - выделяем ее жирным шрифтом
        let activeClass = keySpace === activeKeySpace ? ' active fw-bold' : '';
        // добавление новой закладки
        let tabLink = $('<button class="nav-link ' + activeClass + '" onclick="makeTable(\'' + keySpace + '\');">' + keySpace + '</a>');
        tab.append($('<small></small>').append(tabLink));
    });

    // извлекаем информацию о таблицах
    $.each(tableStatList, function (key, tableStat) {
        // найден активный keySpace
        if (key === activeKeySpace) {
            // итоговые значения
            let tableCount = 0;
            let totalSpace = 0;
            let snapshotsTotalSpace = 0;
            let readRowCount = 0;
            let tombstoneCount = 0;
            $.each(tableStat, function (index, table) {
                if (!table.tableName.includes('.')) {
                    tableCount++;
                }
                totalSpace += table.space_used_total;
                snapshotsTotalSpace += table.space_used_by_snapshots_total;
                readRowCount += table.maximum_live_cells_per_slice_last_five_minutes;
                tombstoneCount += table.maximum_tombstones_per_slice_last_five_minutes;
            });

            $('#tableNameCount').empty().append(tableCount);
            $('#tableSpaceCount').empty().append(totalSpace);
            $('#snapshotsSpaceCount').empty().append(snapshotsTotalSpace);
            $('#readRowsCount').empty().append(readRowCount);
            $('#tombStoneRowsCount').empty().append(tombstoneCount);

            // сортировка по названию таблицы + накопление итогов
            tableStat.sort(function (a, b) {
                let aName;
                let bName;

                if (sortByField === 'tableName') {
                    aName = a.tableName.toLowerCase();
                    bName = b.tableName.toLowerCase();
                } else if (sortByField === 'tableSpace') {
                    aName = a.space_used_total;
                    bName = b.space_used_total;
                } else if (sortByField === 'snapshotsSpace') {
                    aName = a.space_used_by_snapshots_total;
                    bName = b.space_used_by_snapshots_total;
                } else if (sortByField === 'readRows') {
                    aName = a.maximum_live_cells_per_slice_last_five_minutes;
                    bName = b.maximum_live_cells_per_slice_last_five_minutes;
                } else if (sortByField === 'tombStoneRows') {
                    aName = a.maximum_tombstones_per_slice_last_five_minutes;
                    bName = b.maximum_tombstones_per_slice_last_five_minutes;
                }

                return ((aName < bName) ? (sortOrder === 'asc' ? -1 : 1) : ((aName > bName) ? (sortOrder === 'asc' ? 1 : -1) : 0));
            });

            // заполнение таблицы
            $.each(tableStat, function (index, table) {
                let tableName = table.tableName;
                let icon = tableName.includes('.')
                    ? $('<i class="bi bi-key-fill text-primary"></i>')
                    : $('<i class="bi bi-key-fill opacity-0"></i>');

                let row = $('<tr></tr>')
                row.append($('<td class="align-middle"></td>')
                    .append($('<small></small>')
                        .append($('<div></div>')
                            .append(icon)
                            .append(tableName)
                            .append($('<button type="button" class="btn btn-danger btn-sm float-end ms-2" ' +
                                'onclick="startCompacting(\'' + ip + '\', \'' + activeKeySpace +
                                '\', \'' + tableName +
                                '\');" data-bs-toggle="tooltip" data-bs-placement="top" title="Компактинг ' + tableName +
                                '">C</button>')))));
                row.append($('<td class="align-middle text-center"></td>')
                    .append($('<small></small>')
                        .append(table.space_used_total)));
                row.append($('<td class="align-middle text-center"></td>')
                    .append($('<small></small>')
                        .append(table.space_used_by_snapshots_total)));
                row.append($('<td class="align-middle text-center"></td>')
                    .append($('<small></small>')
                        .append(table.maximum_live_cells_per_slice_last_five_minutes)));
                row.append($('<td class="align-middle text-center"></td>')
                    .append($('<small></small>')
                        .append(table.maximum_tombstones_per_slice_last_five_minutes)));

                tableList.append(row);
            });
        }
    });
}

// изменение порядка сортировки
function sortTable(fieldName) {
    // поля, по которым выполняется сортировка
    let tableHeader = [$('#tableName'), $('#tableSpace'), $('#snapshotsSpace'), $('#readRows'), $('#tombStoneRows')];

    // установка порядка сортировки
    if (sortByField === fieldName) {
        sortOrder = sortOrder === 'asc' ? 'desc' : 'asc';
    } else {
        sortOrder = 'asc';
    }
    sortByField = fieldName;

    // установка в таблице маркера сортировки
    $.each(tableHeader, function (index, header) {
        // сохранение текста
        let textHeader = header.text();
        // очистка
        header.empty();
        // возвращаем текст
        header.append(textHeader);

        // найдено поле, по которому выполняется сортировка - установить маркер
        if (header.attr('id') === sortByField) {
            // добавление маркера
            header.append(sortOrder === 'asc'
                ? $('<i class="bi bi-arrow-up"></i>')
                : $('<i class="bi bi-arrow-down"></i>'));
        }
    });

    makeTable(activeKS);
}

// запуск компактинга
function startCompacting(ip, keySpace, tableName) {
    // сомпактинг на текущей ноде
    let compactCurrentNode = $('#compactCurrentNode');
    compactCurrentNode.off('click');
    compactCurrentNode.on("click", function(){
        $('#myModal').modal('hide');
        startCompactingRest(ip, keySpace, tableName, false);
    });

    // компактинг на всех нодах
    let compactAllNodes = $('#compactAllNodes');
    compactAllNodes.off('click');
    compactAllNodes.on("click", function(){
        $('#myModal').modal('hide');
        startCompactingRest(ip, keySpace, tableName, true);
    });

    $('#myModal').modal('show');
}

function startCompactingRest(ip, keySpace, tableName, compactAllNodes) {
    let url = '/startCompacting/' + ip + '/' + keySpace + '/' + tableName + '/' + compactAllNodes;

    $.ajax(url, {
        method: 'get',
        dataType: 'text',
        success: function (data) {
            console.log('compacting output: ' + data);
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


// function beep() {
//     var snd = new Audio("data:audio/wav;base64,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qJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=");
//     snd.play();
// }
