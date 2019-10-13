# Wikipedia Matrix : The Truth
![Quick illustration of the project](img/readme.png) <br>

The aim of this PDL project is to extract tables in CSV format from Wikipedia pages. Those pages can be analyzed in two different ways:
By searching for the corresponding Wikitext code
By exploiting the HTML rendering of the Wikipedia page 

Both approaches would be compared and tested (in order to have the same CSV output).

### But why extract tables in Wikipedia?

Wikipedia tables are difficult to exploit by statistical tools, visualization or any tool able to exploit tables (e.g., Excel, OpenOffice, RStudio, Jupyter). These tables are written in a syntax (Wikitext) difficult to analyze and not necessarily designed for the specification of tables. In addition, there is a strong heterogeneity in the way tables are written, further complicating Wikipedia's tabular data processing. Same can be said for HTML format. 

### Why CSV (Comma separated values) ?
It is very simple and above all supported by many tools.

This project is about implementing a solution and specify a ground truth ("ground truth") and thus evaluate different extractors by confronting them to the ground truth. Also, it must be able to extract several tables on the same Wikipedia page.


Last but not least, this project will propose a set of tools able to analyze the results of the extractors and thus specify a set of expected results (which will then be used during the automatic test phase). Among these tools, one of them will allows to visualize a matrix (resulting from an automatic extractor), possibly to correct the matrix, and then to export it in CSV format.

Finally, a most global suite of tests will demonstrate the quality of our tool.

### Final result
There will be three concrete results:
Extractors of much better quality (with source code, documentation, test suite, continuous integration, etc.)
A suite of tools to be able to more easily specify a ground truth and thus help the evaluation of extractors
A dataset reusable by anyone wanting to test an array extractor

This is a Master of Business Informatics' project which improve its latest version called "Wikipedia Matrix" (this actuel project has been forked from this one)<br>
(cf : https://github.com/mathieulehan/PDL_2018-2019_GR4). 

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

You can find more details in [INSTALL.md](https://github.com/Qt-tracker/PDL_2019-2020_GR5/blob/develop/INSTALL.md).


## Folders' structure

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
## Supported and unsupported features (actual state)

Extraction via wikitext does not work very well, especially table checking. Json Format is making trouble while extracting a table. 
However via HTML we do not encounter any problems.<br/>
If there is a table under a table, the CSV given is not valid.
Moreover, many times, extraction via wikitext compare to those via html do not give the same result.

Some little problems have been found as when a false URL is given, it pops out an error without precising which url/title is making trouble. When a page does not have any table it is not said clearly.
Until then there is no method to check if the CSV is good, so this is a new method considered. A method that compares two CSVs is well under consideration. 

## Built With

* [IntelliJ](https://www.jetbrains.com/idea/) - The IDE used
* [Maven](https://maven.apache.org/) - Dependency Management
* [JUnit](https://junit.org/junit5/) - Used to test
* [Mockito](https://site.mockito.org/) - Mocking framework
* [jsoup](https://jsoup.org/) - Java HTML parser
* [Apache Commons](https://commons.apache.org/) - Reusable Java components
git config --global user.email johndoe@example.com
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

