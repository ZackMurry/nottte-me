# nottte-me
An unfinished, shortcut-centered writing website with easy exporting to Google Docs, PDF, and more.

# Why not Google Docs?
As good as Google Docs is, there is almost no customizability. If you wanted to bind highlighting to a key, you'd be simply out of luck. I found myself clicking in the same
places over and over again, which made me think that there had to be a better way. I also wanted to be able to easily use these documents with Google Docs, as that's what
everyone seems to use.

# How does it work?
You start by binding a style to a key. The format for styles is the same as CSS, except in camelCase, making it easy to figure out what you need to do. Then, you can
simply toggle the style by pressing the shortcut and typing or selecting text and pressing the shortcut.

# How to build

## Downloads
To run this, you'll need [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html), [Maven](https://maven.apache.org/download.cgi), [Node.js](https://nodejs.org/en/download/), and [PostgreSQL](https://www.postgresql.org/download/) installed.

## Process
First, download the source code. Navigate into the project directory in a terminal 
and type `mvn install`. Then, run `java -jar target/nottte-me-0.01-SNAPSHOT.jar`. 
You now have the Java backend running.

To run the frontend, navigate into the "frontend" directory and type `npm install` 
to install its dependencies. Finally, enter `npm run dev` to get into a development environment. 
If you'd like to have a production environment, type `npm run build` and `npm run start` instead.  

You'll also want to set some environment variables. You can learn how to set environment variables on Windows [here](https://superuser.com/questions/949560/how-do-i-set-system-environment-variables-in-windows-10).
You need to set NOTTTE_JWT_SECRET_KEY to an arbitrary value,
preferably one which is cryptographically secure.
You should also set POSTGRES_USERNAME and POSTGRES_PASSWORD to your PostgreSQL username and password, respectively.

You can find the website running on localhost:3000.
