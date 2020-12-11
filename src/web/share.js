const qrCodeLength = 400;

let downloadQrButton, copyLinkButton, qrCodeElement, message, title, tabtitle;

window.onload = () => {
    let urlParts = window.location.href.split('/');
    let url = window.location.protocol + '//' + window.location.hostname + "/" + urlParts[urlParts.length - 1];

    downloadQrButton = document.getElementById("download-qr-button");
    copyLinkButton = document.getElementById("copy-link-button");
    qrCodeElement = document.getElementById('qr-code');
    message = document.getElementById('message');
    title = document.getElementsByTagName('h1')[0];
    tabtitle = document.getElementsByTagName('title')[0];
    
    title.innerHTML = '';

    fetch('/data/' + urlParts[urlParts.length - 1]).then((response) => {
        if (response.status == 200) {
            response.json().then(data => {title.innerHTML = tabtitle.innerHTML = data.title});
        } else {
            title.innerHTML = tabtitle.innerHTML = "Could not fetch title for this page.";
        }
    });

    new QRious({
        element: qrCodeElement,
        value: url,
        size: qrCodeLength
    });

    downloadQrButton.addEventListener('click', (event) => {
        let link = document.createElement('a');
        let urlParts = url.split('/');

        link.download = urlParts[urlParts.length - 1] + ".png";
        link.href = qrCodeElement.toDataURL("image/png");
        link.click();
        
        message.style.color = "rgb(17, 119, 202)";
        message.innerHTML = "Created QR code download.";
    });

    copyLinkButton.addEventListener('click', (event) => {
        try {
            navigator.clipboard.writeText(url).then(function() {
                reportCopySuccess(true);
            }, function(err) {
                reportCopySuccess(false);
            });
        } catch (e) {
            reportCopySuccess(oldCopy(url));
        }
    });
}

function oldCopy(text) {
    let textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.top = "0";
    textArea.style.left = "0";
    textArea.style.position = "fixed";
    textArea.style.display = "hidden";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
  
    try {
        let res = document.execCommand('copy');
        document.body.removeChild(textArea);
        return res;
    } catch (e) {
        document.body.removeChild(textArea);
        return false;
    }   
}

function reportCopySuccess(success) {
    if (success) {
        message.style.color = "rgb(17, 119, 202)";
        message.innerHTML = "Link successfully copied!";
    } else {
        message.style.color = "red";
        message.innerHTML = "Error copying link.";
    }
}