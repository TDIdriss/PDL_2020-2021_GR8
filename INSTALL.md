# INSTALL GUIDE

## For experimented developers
 
 Clone project with this command `git clone https://github.com/TDIdriss/PDL_2020-2021_GR8.git`
 <br> and run `mvn compile` or `mvn test`.
 You will see csv files in **output/HTML and output/wikitext** 
 
 ---
## For beginners
 
 To install Wikipedia Matrix :
 
     1. Clone project with `git clone https://github.com/TDIdriss/PDL_2020-2021_GR8.git` if you don't have git follow [this guide](https://www.atlassian.com/git/tutorials/install-git).
     2. Use your IDE like Jetbrains Intellij Idea, you can download [here](https://www.jetbrains.com/idea/).
     3. Create a folder in the IdeaProjects directory.
     4. Open your IDE.
     5. In the file menu close project if it had an existing project then choose Get from version control
     6. Choose the Git control version
     7. Choose the link of the folder created in the Directory field
     8. Copy the link of the Git repository in the Url field
     9. Test the repository connection
     10. Press clone
     11. If your IDE don't support Maven project you can install Maven [here](https://maven.apache.org/install.html).
     12. Launch Maven with your IDE, if you can't convert your project a Maven one, you can either run `mvn compile` or `mvn test` in terminal you will see csv files in **output/HTML and output/wikitext**
 
 NB: For people who use intelliJ IDEA.  It may happen that the project cannot be executed with intelliJ IDEA: a Jsoup import bug.
     In this case, you should launch Maven by right-clicking on the pom.xml file located at the root of the project, then click Run Maven then Clean and install. This will install the missing dependencies in intelliJ IDEA.