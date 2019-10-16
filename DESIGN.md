# Wikipedia Matrix : The Truth

This project has been realized during our Master of Business Informatics. Its objective is to parse tables from wikipedia into csv files. We had to extract those tables from HTML and Wikitext (the wiki markup language), parse them into csv files and compare the parsing quality, in order to choose which of those raw contents was able to give us the best csv files at the end of the process.


## Scope
This project allows the client to be able to extract data in the form of tables within a Wikipedia page and to return this data in a CSV file in order to be able to open and consult them in tools using the CSV format, for example Excel.

![Context diagram associate to system](img/diagram_scope_en.png) <br>
_Context diagram associate to system_

First, the user retrieves the URL of the Wikipedia page and inserts it into the field dedicated to its use. Thus the system recovers the path giving access to all the information of the Wikipedia page, the system is then able to analyze the URL and the elements of its page.


## UML Models"

### Class diagram
![Class diagram associate to system](img/diagram_class.png) <br>
_Class diagram associate to system_

The two main entities to be considered are Wikitext data (named `Donnees_wikitext`) and HTML data (named `Donnees_html`). The `Donnees_html` class consists of a `htmlVersCSV()` function to retrieve the data and put it in the form of a CSV. The `Donnees_wikitable` class, on the other hand, is first composed of a data recovery function because Wikitext data needs additional processing compared to HTML data. It also has a function converting these data to CSV format.

These two classes inherit a parent class called `Data` representing all the information contained in a table on a Wikipedia page. It consists of a `tempsExecution()` function to calculate the time of the two conversion methods and a `pageComporteTableau()` function to check whether there is a table in the URL page filled in.

Directly linked to this class, the URL class allows to process the URL entered by the user, in particular thanks to the `urlValide()` function, which aims to check whether the URL returns to an existing Wikipedia page.

### Use Case diagram
![Use Case diagram associate to system](img/diagram_use_case_en.jpeg) <br>
_Use Case diagram associate to system_

Here we will consider that our user, external to the system of our program, is Mr Acher. This actor must therefore perform two actions in order for the processing to start:

* __Enter the Wikipedia page URL :__ <br>
    As a user, I retrieve the URL of my Wikipedia page and insert it in the associated field.
    
* __Generate CSV file :__ <br>
    As a user, I generate the CSV file by clicking a button and am able to download it. <br>
    This action includes two complementary actions :
    * _Open CSV file :_ As a user, I only want to open my CSV file to view it.
    * _Save CSV file :_ As a user, I want to save my CSV file to my local computer.
    
