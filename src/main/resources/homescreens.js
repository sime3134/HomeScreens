document.addEventListener("DOMContentLoaded", function () {
    connectUser();
});

function connectUser() {
    let displayId = localStorage.getItem('displayId');
    const ws = new WebSocket(`ws://` + window.location.host + `/connect/${displayId ? displayId : 'new'}`);

    ws.onmessage = function (event) {
        const data = JSON.parse(event.data);
        if (data.type === 'displayId') {
            localStorage.setItem('displayId', data.displayId);
        } 
    };
}