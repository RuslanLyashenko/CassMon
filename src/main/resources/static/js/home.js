function setIOPerf(ip, value) {
    let ioPerfDialog = $('#ioPerfDialog');
    let ioPergButton = $('#setIOPerfButton');

    for (let i = 1; i <= 4; i++) {
        let checker = $('#ioPerfRB' + i);
        checker[0].checked = parseInt(checker.attr('ioPerfValue')) === value;
    }

    ioPergButton.off('click');
    ioPergButton.on("click", function(){
        ioPerfDialog.modal('hide');
        setIOPerfRest(ip, value);
    });

    ioPerfDialog.modal('show');
}

function setIOPerfRest(ip, value) {
    console.log('Set IO perf for: ' + ip);
    console.log('Current value: ' + value);

    let ioPerfValue = 16;

    for (let i = 1; i <= 4; i++) {
        let checker = $('#ioPerfRB' + i);
        if (checker[0].checked) {
            ioPerfValue = parseInt(checker.attr('ioPerfValue'));
        }
    }

    console.log('IO perf value: ' + ioPerfValue);
    console.log('Active cluster: ' + activeCluster);

    let url = '/setIOPerfValue/' + activeCluster + '/' + ip + '/' + ioPerfValue;

    $.ajax(url, {
        method: 'get',
        dataType: 'text',
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