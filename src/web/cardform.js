const maxlengths = [900, 1500, 1600, 2800];
var constrainInput, fileInput, titleInput, contentInput, deleteButton, previewButton,
    previewButtonVisible, previewButtonHidden, preview, submitButton;


window.onload = () => {
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
    
    constrainInput.addEventListener('change', changeMaxLength);
    fileInput.addEventListener('change', (event) => {
        changeMaxLength(event);
        deleteButton.style.display = (fileInput.value) ? 'inline-block' : 'none';
    });
    
    deleteButton.style.display = (fileInput.value) ? 'inline-block' : 'none';
    deleteButton.addEventListener('click', (event) => {
        fileInput.value = "";
        fileInput.dispatchEvent(new Event('change'));
    });

    previewButton.addEventListener('click', (event) => {
        if (previewButtonHidden.style.display != 'block') { 
            preview.innerHTML = new showdown.Converter().makeHtml(contentInput.value);
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

function changeMaxLength(event) {
    contentInput.maxLength = maxLengthSwitcher(fileInput.value, constrainInput.checked);
    if (contentInput.value.length > contentInput.maxLength) {
        contentInput.value = contentInput.value.substring(0, contentInput.maxLength);
    }
}

async function sendFormData() {
    if (!titleInput.value || !contentInput.value) {
        return;
    }

    let data = new Object();
    data["isTextConstrained"] = constrainInput.checked;
    data["title"] = titleInput.value;
    data["content"] = contentInput.value;
    data["image"] = (fileInput.files.length > 0) ? await toDataURL(fileInput.files[0]) : null;

    let xhr = new XMLHttpRequest();
    xhr.open("POST", '/create', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    
    xhr.onreadystatechange = () => {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            window.location.href = xhr.responseText;
        } else {
            postError(xhr.responseText);
        }
    }

    xhr.send(JSON.stringify(data));
}

function toDataURL(file) {
    return new Promise((resolve, reject) => {
        let reader = new FileReader();
        reader.addEventListener("load", () => {
            resolve(reader.result);
        }, false);
        reader.readAsDataURL(file);
    });
}

function postError(message) {
    document.getElementById('error').innerHTML = message;
}