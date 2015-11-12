# Code Overview

##Front end
All the HTML markups and basic GUI layout are in .JSP files. However, minimal Java is used in the front end (if any). The majority of GUI code is in Javascript that is in script tags in the .JSP files. The individual .JSP files which all handle one website page all have paired external CSS pages. 

##Test classes
Primitive JUnit test classes for a few of classes have been created. These tests should probably be reviewed for quality. In addition, more JUnit tests should be created to thoroughly test the project. 

##Entities
Entities are classes that are mapped through hibernate to the database tables used in this Project. The entity classes in this project include Image.java, Tag.java and TagRelation.java. Image.Java maps to the database table image, Tag maps to tags, and TagRelation maps to image_tag_relation. The current Entity interface is implemented by these three entities so that a DAO can use a single method to add any of those entity types to the database in their respective relations(tables).  

##Business Logic
This package (folder) is where most of the logic needed to process commands from the front end takes place. 

##Individual Java classes in "image" package

####RestAPI
All user Interaction with the GUI that interacts through the backend makes requests through this class. It is decoupled from the classes handling interaction with APIs and the database through Business Logic classes. 

####ImageDao(Interface)/DatabaseOperations
This implementation of a DAO handles Database Operations with Hibernate. 

####APIInteraction(Interface)/APIInteractionImpl
This class is used to use and call upon the URLs necessary to implement the APIs needed for the code. 
