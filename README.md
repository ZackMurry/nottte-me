# nottte-me
An unfinished, shortcut-centered writing website with easy exporting to Google Docs, PDF, and more.

# why not Google Docs?
As good as Google Docs is, there is almost no customizability. If you wanted to bind highlighting to a key, you'd be simply out of luck. I found myself clicking in the same
places over and over again, which made me think that there had to be a better way. I also wanted to be able to easily use these documents with Google Docs, as that's what
everyone seems to use.

# how does it work?
You start by binding a style to a key. The format for styles is the same as CSS, except in camelCase, making it easy to figure out what you need to do. Then, you can
simply toggle the style by pressing the shortcut and typing or selecting text and pressing the shortcut.

# how to build
First, download the source code. Navigate into the project directory in a terminal 
and type `mvn install`. Then, run `java -jar target/nottte-me-0.01-SNAPSHOT.jar`. 
You now have the Java backend running.

To run the frontend, navigate into the "frontend" directory and type `npm install` 
to install its dependencies. Finally, enter `npm run dev` to get into a development environment. 
If you'd like to have a production environment, type `npm run build` and `npm run start` instead.  
You can find the website running on localhost:3000
