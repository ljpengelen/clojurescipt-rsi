# Simple Reagent apps

This is a collection of simple [Reagent](http://reagent-project.github.io/) apps that give you a starting point for experimentation,
without having to worry about project setup.

This collection was created for the [Clojure for beginners presentation](https://github.com/ljpengelen/clojure-for-beginners-presentation).

## Requirements

- [Java 8+](https://adoptium.net/)
- [Node.js and NPM](https://nodejs.org/)

## Development

Run `npm install` before you start development for the first time and each time you add a new JavaScript dependency.

If you're a beginner to Clojure and don't have a favorite setup yet, I recommend using [Visual Studio Code](https://code.visualstudio.com/) in combination with the [Calva extension](https://calva.io/).

Once you've installed Java, Node.js, NPM, Visual Studio Code, and Calva, you can open `src/rsi/core.clj`, [connect Calva to the project](https://calva.io/connect/), and start experimenting.

## Creating a release build

Execute the following command to create a release build:

```
npx shadow-cljs release app
```
The result can be found in `/dist`.
