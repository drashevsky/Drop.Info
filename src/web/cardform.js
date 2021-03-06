// Kavi Gill, Daniel Rashevsky, Joel Renish
// CSE 143 Final Project
// Drives Card Creation page, provides a form to create new cards with

const markdownSettings = {
    simplifiedAutoLink: true,
    tables: true,
    tasklists: true,
    emoji: true,
    strikethrough: true, 
    simpleLineBreaks: true 
}
const maxlengths = [900, 1500, 1600, 2800];

var constrainInput, fileInput, titleInput, contentInput, deleteButton, previewButton,
    previewButtonVisible, previewButtonHidden, preview, submitButton;

window.onload = () => {

    //Get all DOM elements
    constrainInput = document.getElementById('constain-text-setting-input');
    fileInput = document.getElementById('file-input');
    titleInput = document.getElementById('title-input');
    contentInput = document.getElementById('content-input');
    deleteButton = document.getElementById('file-input-delete-button');
    previewButton = document.getElementById('content-input-preview-button');
    previewButtonVisible = document.getElementById('content-input-preview-button-visible');
    previewButtonHidden = document.getElementById('content-input-preview-button-hidden');
    preview = document.getElementById('content-input-preview');
    submitButton = document.getElementById('submit-input');
    
    postError('');
    
    //Change max length of content textbox based on constrain toggle
    constrainInput.addEventListener('change', changeMaxLength);

    //Show delete button if image was added to card
    fileInput.addEventListener('change', (event) => {
        changeMaxLength(event);
        deleteButton.style.display = (fileInput.value) ? 'inline-block' : 'none';
    });
    
    //Delete image if delete button pressed
    deleteButton.style.display = (fileInput.value) ? 'inline-block' : 'none';
    deleteButton.addEventListener('click', (event) => {
        fileInput.value = "";
        fileInput.dispatchEvent(new Event('change'));
    });

    //Preview button: show markdown preview of content
    previewButton.addEventListener('click', (event) => {
        if (previewButtonHidden.style.display != 'block') { 
            preview.innerHTML = new showdown.Converter(markdownSettings).makeHtml(contentInput.value);
            previewButtonVisible.style.display = 'none';
            previewButtonHidden.style.display = 'block';
            contentInput.style.display = 'none';
            preview.style.display = 'block';
        } else {
            previewButtonVisible.style.display = 'block';
            previewButtonHidden.style.display = 'none';
            contentInput.style.display = 'block';
            preview.style.display = 'none';
        }
    });

    submitButton.addEventListener('click', sendFormData);
}

//Controls max length of content based on image and constrain toggle state
function maxLengthSwitcher(hasImage, hasConstraint) {
    if (hasImage && hasConstraint) {
        return maxlengths[0];
    } else if (hasImage && !hasConstraint) {
        return maxlengths[1];
    } else if (hasConstraint) {
        return maxlengths[2];
    } else {
        return maxlengths[3];
    }
}

//Change max length of content textbox and chop off extra text
function changeMaxLength(event) {
    contentInput.maxLength = maxLengthSwitcher(fileInput.value, constrainInput.checked);
    if (contentInput.value.length > contentInput.maxLength) {
        contentInput.value = contentInput.value.substring(0, contentInput.maxLength);
    }
}

//Send completed form to API if it is valid
async function sendFormData() {
    if (!titleInput.value || !contentInput.value) {
        return;
    }

    let data = new Object();
    data["isTextConstrained"] = constrainInput.checked;
    data["title"] = titleInput.value;
    data["content"] = contentInput.value;
    data["image"] = (fileInput.files.length > 0) ? await toDataURL(fileInput.files[0]) : "";

    let xhr = new XMLHttpRequest();
    xhr.open("POST", '/create', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    
    xhr.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            window.location.href = "/" + xhr.responseText;
        } else if (this.readyState === 4) {
            postError(xhr.responseText);
        }
    }

    xhr.send(JSON.stringify(data));
}

//Converts user-selected image to base64 data url
function toDataURL(file) {
    return new Promise((resolve, reject) => {
        let reader = new FileReader();
        reader.addEventListener("load", () => {
            resolve(reader.result);
        }, false);
        reader.readAsDataURL(file);
    });
}

//Render error message
function postError(message) {
    document.getElementById('error').innerHTML = message;
}