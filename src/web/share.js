const qrCodeLength = 400;

let downloadQrButton, copyLinkButton, qrCodeElement, message;

window.onload = () => {
    let urlParts = window.location.href.split('/');
    let url = window.location.protocol + '//' + window.location.hostname + "/" + urlParts[urlParts.length - 1];

    downloadQrButton = document.getElementById("download-qr-button");
    copyLinkButton = document.getElementById("copy-link-button");
    qrCodeElement = document.getElementById('qr-code');
    message = document.getElementById('message');
    
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
        navigator.clipboard.writeText(url).then(function() {
            message.style.color = "rgb(17, 119, 202)";
            message.innerHTML = "Link successfully copied!";
        }, function(err) {
            message.style.color = "red";
            message.innerHTML = "Error copying link.";
        });
    });
}