![Screenshot of Drop.Info](http://drop-info.live/img/imagexkxaru4y.jpeg)
# Drop.Info
_By Kavi Gill, Daniel Rashevsky, and Joel Renish_

UW CSE 143 Project: Drop.Info is a website that allows users to quickly create index-card-sized posts of information about anything and share them with a short link. It is available for live preview at [http://drop-info.live/](http://drop-info.live/). A video demonstrating its use is located [here](https://youtu.be/3DnexE53rLw).

## Installation
To install and run Drop.Info, you first need to clone this Git repo. This can be done by simply running:
   

     git clone https://github.com/drashevsky/Drop.Info.git
The repo is comprised of several folders: 
- `src` contains the uncompiled web and Java source code of the project
- `build` contains the compiled server and files, will be filled when you build the project
- `deps` contains Java dependencies
- `runscripts` contains small scripts used in running the server

## Building and Running Drop.Info
To build and run the project on **Windows**:
1. Run `build.bat`. You may need to grant UAC and SmartScreen permissions for it to run successfully. It will produce a lot of output, and pause when it is complete. Exit it.
2. `build.bat` will output a server build in the `build\` folder.
3. Inside `build\`, run `runserver.bat`. You may also need to grant it UAC and SmartScreen permissions. If a prompt comes up asking for firewall access, allow it.
4. The server is now running on port 80, so head over to [http://localhost/](http://localhost/) to access the webapp.

To build and run the project on **Linux** (use the terminal):
1. Run `chmod +x ./build.sh`. This should give it the necessary permissions to start.
2. Run `sudo ./build.sh`. It will produce a lot of output, and pause when it is complete. Exit it.
3. `build.sh` will output a server build in the `build/` folder.
4. Inside `build/`, `build.sh` should have given `runserver.sh` necessary permissions to run, but if not, run `chmod +x ./runserver.sh`.
5. Run `sudo ./runserver.sh`. This should start the server. 
6. The server is now running on port 80, so head over to [http://localhost/](http://localhost/) to access the webapp.
- Note: This build process was not tested on Macintosh. 

Now that you have installed, built, and run the application, you can learn how to use it by going to the [http://localhost/what](http://localhost/what) page or watching this [video](https://youtu.be/3DnexE53rLw).
