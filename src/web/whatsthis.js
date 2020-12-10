window.onload = () => {
    let text = document.getElementById('card-content-text').innerHTML,
        target = document.getElementById('card-content-text'),
        converter = new showdown.Converter(),
        html = converter.makeHtml(text);  
    target.innerHTML = html;
}