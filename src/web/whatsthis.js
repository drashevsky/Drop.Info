// Kavi Gill, Daniel Rashevsky, Joel Renish
// CSE 143 Final Project
// Renders "What Is" page explaining how to use Drop.Info

window.onload = () => {
    let text = document.getElementById('card-content-text').innerHTML,
        target = document.getElementById('card-content-text'),
        converter = new showdown.Converter({strikethrough: true}),
        html = converter.makeHtml(text);  
    target.innerHTML = html;
    target.style.visibility = 'visible';
}