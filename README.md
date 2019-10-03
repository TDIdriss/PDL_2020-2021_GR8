# Wikipedia Matrix

This project has been realized during our Master of Business Informatics. Its objective is to parse tables from wikipedia into csv files. We had to extract those tables from HTML and Wikitext (the wiki markup language), parse them into csv files and compare the parsing quality, in order to choose which of those raw contents was able to give us the best csv files at the end of the process.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See parsing wikitables for notes on how to start the parsing on a live system.

After cloning the project into your IDE, run it as "Maven test" command. It will run the test classes and then parse tables from more than 300 wikipedia urls.

You can find a demo right [here](https://drive.google.com/open?id=1h7r2-48byKkTbeMmPoBI8VrkrjTLC73v).

### Prerequisites

An IDE with Maven.
Latest JDK to execute maven test.

```
Eclipse - https://www.eclipse.org/
```

### Installing

How to install it ?

Clone it from git into your IDE.

```
via git plugin for eclipse - https://www.eclipse.org/egit/
```
```
via git - https://git-scm.com/
```

Convert your project to a Maven one

```
On Eclipse : Right click on your project, Configure/Convert to Maven project
```

You are done !

## Project's architecture

Folders:
- the root contains some files, as :
  1. .gitignore, containing patterns of files that git should not push.
  2. LICENSE.md containing our project's license : the MIT license.
  3. pom.xml, containing our project's dependencies.
- /output contains two folders /HTML & /wikitext, that will contain the parsed wikipedia tables, and one file, url_file.txt, containing the 336 URLs to be parsed.
- the /src folder contains three folders :
  1. /main/java/com/wikipediaMatrix contains our Java files, except test ones.
  2. /ressources containing the html of a wikipedia page.
  3. /test/java/com/wikipediaMatrix contains our test files.

## Running the tests

On Eclipse : Right click on your project, Run as/"Maven test"

## Parsing wikitables

Extracting from both HTML and WIKITEXT

```
On Eclipse : Right click on your project, Run as/"Maven test"
```

Extracting the way you like it
```
Run the class WikiExtractMain. Then type :
- W to parse files from WIKITEXT to csv
- H to parse files from HTML to csv
- X to parse files from both WIKITEXT and HTML
```

## Built With

* [Eclipse](https://www.eclipse.org/) - The IDE used
* [Maven](https://maven.apache.org/) - Dependency Management
* [JUnit](https://junit.org/junit5/) - Used to test
* [Mockito](https://site.mockito.org/) - Mocking framework
* [jsoup](https://jsoup.org/) - Java HTML parser
* [Apache Commons](https://commons.apache.org/) - Reusable Java components
git config --global user.email johndoe@example.com
## Versioning

- prototype : the prototype built to test the concept

- V1 : in this version, most HTML tables are parsed successfully. The project's structure is a non-Maven one, we could not run "Maven test". Also, in this version, urls parsing was executed one at a time.

- V2 : This version supports the Maven test command & has a simple UI allowing interaction with the user.

- master : the lastest, stable version of the project.

## Authors

* **Thomas Guessant** - *Whole project* - [Thomas Guessant](https://github.com/thomasguessant35)
* **Mathieu Le Han** - *Whole project* - [Mathieu Le Han](https://github.com/mathieulehan)
* **Charlotte Laurensan** - *Whole project* - [Charlotte Laurensan](https://github.com/chach44)
* **Yann Bourhis** - *Whole project* - [Yann Bourhis](https://github.com/YannBourhis)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

