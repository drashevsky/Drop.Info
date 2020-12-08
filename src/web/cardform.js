const maxlengths = [900, 1500, 1600, 2800];
var constrainInput, fileInput, contentInput, deleteButton, previewButton,
    previewButtonVisible, previewButtonHidden, preview;


window.onload = () => {
    constrainInput = document.getElementById('constain-text-setting-input');
    fileInput = document.getElementById('file-input');
    contentInput = document.getElementById('content-input');
    deleteButton = document.getElementById('file-input-delete-button');
    previewButton = document.getElementById('content-input-preview-button');
    previewButtonVisible = document.getElementById('content-input-preview-button-visible');
    previewButtonHidden = document.getElementById('content-input-preview-button-hidden');
    preview = document.getElementById('content-input-preview');
    
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