document.addEventListener("DOMContentLoaded", function () {
    displayScreens();

    document.getElementById('registerForm').addEventListener('submit', async function (event) {
        event.preventDefault();
        const name = event.target.name.value;
        registerDisplay(name);
    });
});

async function displayScreens() {
    let displayElement = document.getElementById('displayList');

    try {
        const response = await fetch(`http://` + window.location.host + `/api/v1/displays`);

        if (response.ok) {
            const data = await response.json();
            if(data.length > 0) {
                displayElement.innerHTML = '';
                data.map(display => {
                    const li = document.createElement('li');
                    li.innerHTML = `${display.name} - ${display.displayId}`;
                    displayElement.appendChild(li);
                });
            }
        }else {
            console.log(response);
        }
    } catch (error) {
        console.log(error);
    }

}

async function registerDisplay(name) {
    const formData = new FormData();
    formData.append('name', name);
    formData.append('displayId', localStorage.getItem('displayId'));

    try {
        const response = await fetch(`http://` + window.location.host + `/api/v1/displays`, {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            displayScreens();
        } else {
            alert('Something went wrong registering the display.');
        }
    } catch (error) {
        alert('Error: Something went wrong registering the display.');
    }
}