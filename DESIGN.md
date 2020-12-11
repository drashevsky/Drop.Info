# Design of Drop.Info
This document will detail the technical planning and implementation that went into the creation of Drop.Info. The final product is available live [here](http://drop-info.live).

## The Specification
The core functionality of Drop.Info is to load and render a single screen of content (a card), consisting of a single picture and some markdown text. Depending on user preference, the picture can be large or small, and the text can fill the page or be restricted to an area in the middle. This screen will be retrievable by a unique short URL, and if created by an existing account would show the username/profile picture in the footer. The amount of information would be roughly equivalent to what can fit on a single index card. The user flow would begin with a simple form where a user can publish content to a new link. They would be then shown the published content under the link. A link to a page with a QR code would be shown, where the QR code can be saved or scanned for further use, and the link copied. We originally had user account features and server side rendering in our proposal, but did not have time to implement them.
## Mockups
Mockups of our user interface are available at this link: [https://github.com/drashevsky/Drop.Info/raw/main/mockups.png](https://github.com/drashevsky/Drop.Info/raw/main/mockups.png). We chose to go with a streamlined user interface due to clean looks, mobile and desktop compatability, and fast development time.

## Technical Design

### Frontend and backend
Drop.Info consists of a front-end which loads content and handles content submission, written in HTML/CSS/Javascript. This was chosen for the fastest possible development time, with the best possible UI. The backend stores and retrieves content, handling all API requests. It was written in Java, due to all of us being familiar with the language. Due to faster development time, we store user data in flat JSON files, rather than a dedicated database.

### Build system and dependencies
For our build system, we chose to write custom shell scripts for Windows and Linux. This was due to the low learning curve and maximum customization this provided. We did not have to learn and setup other build systems, which would have taken time. Simple scripts to run `javac`, copy dependencies, link everything to `CLASSPATH`, and run the final product worked best for us. The build system also worked very well cross-platform, allowing us to run it off of AWS EC2.

### API Table
![Drop.Info API](https://github.com/drashevsky/Drop.Info/raw/main/api_table.jpg)

These are our API endpoints. We chose this simple, streamlined format with the API call's name coming first, for example /static/ and /data/[card-identifier]. This was due to the ease of implementation. Due to our use of the basic HTTPServer class in Java provided by Sun, we could not afford a lot of time spent figuring out routing. Therefore, we went with this simplified design, serving all data through simple endpoints and leaving the client to do the hard work of rendering it.
