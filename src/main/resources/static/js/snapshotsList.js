let snapshotsList;
let sortByField = 'tableName';
let sortOrder = 'asc';
let activeKS;

// извлечение данных о всех таблицах ноды
function getSnapshotsStat(clusterName) {
    let clearSnapshotButton = $('#clearSnapshotButton');
    clearSnapshotButton.prop('disabled', true);
    let progress = $('#progress');
    progress.show();

    $.ajax('/snapshotsStats/' + clusterName, {
        method: 'get',
        dataType: 'json',
        success: function (data) {
            snapshotsList = data;
            makeTable('');
            progress.hide();
            if (Object.keys(snapshotsList).length !== 0) {
                clearSnapshotButton.prop('disabled', false);
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

// отображение данных таблицы
function makeTable(activeKeySpace) {
    activeKS = activeKeySpace;

    // извлечение списка keySpace
    let keySpaces = Object.keys(snapshotsList).sort();

    // если запуск первый, в качестве текущего выбирается первый keySpace из списка
    if (activeKeySpace === '') {
        activeKeySpace = keySpaces[0];
        activeKS = activeKeySpace;
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
    $.each(snapshotsList, function (key, tableStat) {
        // найден активный keySpace
        if (key === activeKeySpace) {
            // итоговые значения
            let tableCount = 0;
            let totalSpace = 0;
            let snapshotsTotalSpace = 0;
            $.each(tableStat, function (index, table) {
                tableCount++;
                totalSpace += table.spaceTotal;
                snapshotsTotalSpace += table.snapshotsSpaceTotal;
            });

            $('#tableNameCount').empty().append(tableCount);
            $('#tableSpaceCount').empty().append(totalSpace);
            $('#snapshotsSpaceCount').empty().append(snapshotsTotalSpace);

            // сортировка по названию таблицы + накопление итогов
            tableStat.sort(function (a, b) {
                let aName;
                let bName;

                if (sortByField === 'tableName') {
                    aName = a.tableName.toLowerCase();
                    bName = b.tableName.toLowerCase();
                } else if (sortByField === 'tableSpace') {
                    aName = a.spaceTotal;
                    bName = b.spaceTotal;
                } else if (sortByField === 'snapshotsSpace') {
                    aName = a.snapshotsSpaceTotal;
                    bName = b.snapshotsSpaceTotal;
                }

                return ((aName < bName) ? (sortOrder === 'asc' ? -1 : 1) : ((aName > bName) ? (sortOrder === 'asc' ? 1 : -1) : 0));
            });

            // заполнение таблицы
            $.each(tableStat, function (index, table) {
                let row = $('<tr></tr>')
                row.append($('<td class="align-middle"></td>')
                    .append($('<small></small>')
                        .append(table.tableName)));
                row.append($('<td class="align-middle text-center"></td>')
                    .append($('<small></small>')
                        .append(table.spaceTotal)));
                row.append($('<td class="align-middle text-center"></td>')
                    .append($('<small></small>')
                        .append(table.snapshotsSpaceTotal)));
                tableList.append(row);
            });
        }
    });
}

// изменение порядка сортировки
function sortTable(fieldName) {
    // поля, по которым выполняется сортировка
    let tableHeader = [$('#tableName'), $('#tableSpace'), $('#snapshotsSpace')];

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

// очистка снепшотов
function clearSnapshots() {
    let confirmAction = confirm('Очистить все снепшоты в кластере ' + clusterName + '?');
    if (confirmAction) {
        $.ajax('/clearSnapshots/' + clusterName + '/' + activeKS, {
            method: 'get',
            dataType: 'json',
            success: function (data) {
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
}