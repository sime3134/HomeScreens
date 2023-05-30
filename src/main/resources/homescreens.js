document.addEventListener("DOMContentLoaded", function () {
    connectUser();
});

function connectUser() {
    const ws = new WebSocket('ws://' + window.location.host + '/connect');

    ws.onmessage = function (event) {
        alert(event.data);
    };
}