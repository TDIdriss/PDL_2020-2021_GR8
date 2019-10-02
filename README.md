# Wikipedia Matrix : The Truth

This is a Master of Business Informatics' project which improve its latest version called "Wikipedia Matrix" <br>
(cf : https://github.com/mathieulehan/PDL_2018-2019_GR4). 

The aim of this project is to parse tables from wikipedia into csv files. It extracts those tables from HTML and Wikitext (the wiki markup language), parse them into csv files and compare the parsing quality, in order to choose which of those raw contents was able to give us the best csv files at the end of the process.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See "Parsing wikitables" to start the parsing on a live system.

After cloning the project into your computer, either you open it on your IDE or just run Maven tests. It will run the test classes and then parse tables from more than 300 wikipedia urls.

You can find a demo right [here](https://drive.google.com/open?id=1h7r2-48byKkTbeMmPoBI8VrkrjTLC73v).

### Prerequisites

An IDE and Maven.
JDK 8 to execute maven test.

```
IntelliJ - https://www.jetbrains.com/idea/
```
```
Maven - https://maven.apache.org
```

### Installing

How to install it ?

Clone it from git into your computer on your terminal with the following line

```
git clone https://github.com/Qt-tracker/PDL_2019-2020_GR5.git
```


Don't forget to convert your project to a Maven one


You are done !

## Project's architecture

Folders:
- the root contains some files, as :
  1. .gitignore, containing patterns of files that git should not push.
  2. INSTALL.md containing the install guide
  3. DESIGN.md cointaining projects' scope and its UML Model.
  2. LICENSE.md containing our project's license : the MIT license.
  3. pom.xml, containing our project's dependencies.
  
- /output contains two folders /HTML & /wikitext, that will contain the parsed wikipedia tables, and one file, url_file.txt, containing the 336 URLs to be parsed.

- the /src folder contains three folders :
  1. /main/java/com/wikipediaMatrix contains our Java files, except test ones.
  2. /ressources containing the html of a wikipedia page.
  3. /test/java/com/wikipediaMatrix contains our test files.

## Running the tests

On IntelliJ : Right click on your project, then choose Run 'All Tests'

## Parsing wikitables

For extracting from both HTML and WIKITEXT, you need to go:

On IntelliJ : Double click on the green play button on your right.

You can extract the way you like.
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

## Versioning

- prototype : the latest prototype built to test the concept

- V1 : in this version, last year group putted most HTML tables are parsed successfully. The project's structure is a non-Maven one, we could not run "Maven test". Also, in this version, urls parsing was executed one at a time.

- V2 : This version supports the Maven test command & has a simple UI allowing interaction with the user made by the earlier group.

- master : the lastest, stable version of the project.

- develop : our branch built to test the concept before committing it to master

## Authors

* **Koitrin KOFFI** - *Whole project* - [Koitrin Koffi](https://github.com/Qt-tracker)
* **William ZOUNON** - *Whole project* - [William Zounon](https://github.com/Wizo17)
* **Laeba  TALAT** - *Whole project* - [Laeba Talat](https://github.com/Laeba)
* **Yves KOUASSI** - *Whole project* - [Yves Kouassi](https://github.com/kouassives)
* **Nguyen-Anh CU** - *Whole project* - [Nguyen-Anh CU](https://github.com/NguyenAnhCu)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

