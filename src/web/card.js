// Kavi Gill, Daniel Rashevsky, Joel Renish
// CSE 143 Final Project
// Renders a specific card

const markdownSettings = {
    simplifiedAutoLink: true,
    tables: true,
    tasklists: true,
    emoji: true,
    strikethrough: true, 
    simpleLineBreaks: true 
}

let image, imageWrapper, title, tabtitle, content, contentWrapper, share;

window.onload = () => {

    //Get DOM elements
    image = document.getElementById('card-image-content');
    imageWrapper = document.getElementById('card-image');
    title = document.getElementById('card-header-title');
    tabtitle = document.getElementsByTagName('title')[0];
    content = document.getElementById('card-content-text');
    contentWrapper = document.getElementById('card-content');
    share = document.getElementById('share-link');

    title.innerHTML = '';

    //Get card id
    let urlParts = window.location.href.split('/');
    let profileId = urlParts[urlParts.length - 1];

    //Fetch and render card data
    fetch('/data/' + profileId).then((response) => {
        if (response.status == 200) {
            response.json().then(data => render(data));
        } else {
            render({title: "Error", content: "Error: could not fetch the card for this page.", isTextConstrained: "true"});
        }
    });
}

//Render card data and display on page
function render(json) {
    let constrained = json.isTextConstrained == 'true';

    title.innerHTML = tabtitle.innerHTML = json.title;
    content.innerHTML = new showdown.Converter(markdownSettings).makeHtml('' + json.content);
    content.className = (constrained) ? 'constrain' : 'unconstrain';

    if (json.imageId) {
        image.src = '/img/' + json.imageId;
    } else {
        contentWrapper.className = 'expand';
        imageWrapper.className = 'hidden';
    }   

    share.href = window.location.protocol + '//' + window.location.hostname + "/share/" + json.profileId;
}