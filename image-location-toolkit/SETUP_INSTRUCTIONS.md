# - Overview -

## Table of Contents

This file contains instructions on how to install, setup, and run the Image Location Toolkit. 
Please view the [README](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/documentation/README.md) file for more information on the purpose of the Tookit.

* [Cloning the Git Repository](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#cloning-the-git-repo)
* [Installing Java](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#installing-java)
* [Installing Tesseract](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#installing-tesseract)
* [Installing ImageMagick](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#installing-imagemagick)
* [Installing HeidiSQL](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#installing-heidisql)
* [Installing Apache Tomcat](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#installing-apache-tomcat)
* [Installing MariaDB](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#installing-mariadb)
* [Configuring Hibernate](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#configuring-hibernate)
* [Building the WAR File](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/SETUP_INSTRUCTIONS.md#building-the-war-file)

## Important Links:

**Libraries, Frameworks and Software**

The following is a list of noteable libraries, frameworks and software used by the Toolkit:

* [Java 8u45 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java Development Kit
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) - The IDEA that the Toolkit was built with.
* [Apache Tika](http://tika.apache.org/) - A Java Library used for parsing metadata from images and integration with OCR frameworks
* [Tesseract](https://code.google.com/p/tesseract-ocr/) - OCR framework for parsing text from images
* [HeidiSQL](http://www.heidisql.com/) - Database viewer/editor
* [Apache Tomcat version 8.0.24](http://tomcat.apache.org/) - Web hosting service
* [MariaDB version 10.0.19](https://mariadb.org/) - Database


**External APIs**

* [Imagga Image Tagging API](http://imagga.com/solutions/auto-tagging.html) - Used to generate tags for images
* [Big Huge Thesaurus API](https://words.bighugelabs.com/api.php) - Used to find synonyms for searching
* [Google Geocoding API](https://developers.google.com/maps/documentation/geocoding/) - Used to convert location text to geographic coordinates
* [Imgur API](https://api.imgur.com/) - Used to upload images to web address so that Imagga API can use the web address to tag images

API keys for these APIs are currently. hardcoded in the java as constants. However, if you find yourself getting an SSLHandshake exception [PKIX building path error] you may have to register for some of these APIs to obtain API keys. The good news is they are all free. Imgur and Imagga keys can be changed by editing them at the top of the APIInteractionImpl.Java class. 

# - Building from Source -

## Cloning the Git Repo

We used git as our content management system (CMS) for this project. All code is up in the git repository. You will need a github account and access in order to clone the repo.

If you would like to request access, email Wahab Jilani at Jilani_Wahab@bah.com.

* If you are using IntelliJ, you can clone the repo from the IDE, instructions can be found [here](https://www.jetbrains.com/idea/help/cloning-a-repository-from-github.html).
* Otherwise clone the repository into the location of your choosing:


&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`> git clone https://github.com/booz-allen-hamilton/image-location-toolkit`

## Installing Java

The first step is to download and install the Java Development Kit (JDK). 
This will allow you to compile and build the files necessary to deploy the web application.

* Our project uses the Java version 8 development kit which can be found at [Java SE 8u45](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
* Download and install somewhere on your computer. We recommend in a dev tool folder, but anywhere will work. Just remember the installation path.
* You will need to include the installation path to Java's /bin folder in the "PATH" environmental variable for your system. 
    If you are unsure how to do this, you can find instructions via a quick Google search or check out ["How do I set the PATH system variable?"](https://www.java.com/en/download/help/path.xml) from the Java website.
 

## Installing Tesseract

Tesseract OCR is a framework that is used for parsing image text. It can be downloaded from [Tesseract at Google Code](https://code.google.com/p/tesseract-ocr/).
The Tesseract ReadMe has instructions on how to download an install for Windows, MacOS, and Linux.

Once you have Tesseract installed, locate the path to the installation folder. In the [ImageOperations.java](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/src/main/java/image/ImageOperations.java) file you will need to edit this line:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`String TESSERACT_PATH = "C:\\webapp\\Tesseract-ocr";`
    
found [here](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/src/main/java/image/ImageOperations.java#L217). Change `"C:\\webapp\\Tesseract-ocr"` to the filepath for your Tesseract folder on your local machine.


## Installing HeidiSQL

Head on over to [HeidiSQL](http://www.heidisql.com/) and download the latest version of the database viewer/editor. This will be used to setup the database tables used in the application.
You'll need to connect to your database using the username and password you entered when you setup the database. If you are using default settings these will be username: root, password: password.

## Installing Apache Tomcat

You will need a host service in order to deploy the web application. We used Apache Tomcat which can be downloaded from [here](http://tomcat.apache.org/).

Once downloaded, navigate to the your installation location and find locate the "tomcat-users" xml configuration file (example filepath: C:\apache-tomcat-8.x.xx\config\tomcat-users.xml).
Replace the xml code in the file with the following code:

		<?xml version='1.0' encoding='utf-8'?>
		<tomcat-users>
		<role rolename="manager-gui"/>
		<role rolename="manager-script"/>
		<role rolename="manager-jmx"/>
		<role rolename="manager-status"/>
		<user username="admin" password="admin" roles="manager-gui, manager-script, manager-jmx, manager-status"/>
		</tomcat-users>

This will allow you to login to your tomcat server using "admin" as both your username and password. These are just simple default settings, we would highly recommend you create your own.
Once you are logged into Apache Tomcat, you can deploy WAR files and have access to other admin features. You should try

## Installing MariaDB

Now comes setting up the database. For this project we used MariaDB, a drop-in replacement for MySQL.

Head on over to [MariaDB](https://mariadb.org/)'s website and download the latest version of the software.
If you are not familiar with the Hibernate framework (used for mapping objects to a database/tables) we recommend you use our table configuration when setting up your database. Now if you have experience with Hibernate, feel free to use your own table names, however you will have to edit the hbm.xml configuration files, as well as the Hibernate mapping in the POJO classes. 

**During the first image upload, our project will automatically create the database and tables to store the image data if they are not already created. If you want to do it yourself, create a database named "image-location-toolkit" via a database accessor (HeidiSQL) and execute the SQL queries below to create the required tables manually:**

**Please be aware that our project will NOT install MariaDB, or start up your database. You will have to manually create the connection and run the command to get the database running.**

When installing MariaDB make sure to enter a username and password if prompted, this will allow you to connect to the database.
If you did not get prompted, it may have just set it to the default credentials, `username: root` and `password: password`. You will need to configure your [hibernate.cfg.xml](https://github.com/booz-allen-hamilton/image-location-toolkit/blob/master/src/main/resources/hibernate.cfg.xml) file with the connection url, as well as the username and password you selected for your database.
We ***highly recommend*** that you let hibernate auto-create the database and tables.


* **"image"** table is where the image itself and it's metadata will be stored.

		CREATE TABLE image (
			id INT(11) NOT NULL AUTO_INCREMENT,
			image MEDIUMBLOB NULL,
			date VARCHAR(100) NULL DEFAULT NULL,
			longitude VARCHAR(100) NULL DEFAULT NULL,
			latitude VARCHAR(100) NULL DEFAULT NULL,
			location VARCHAR(255) NULL DEFAULT NULL,
			text VARCHAR(100) NULL DEFAULT NULL,
			PRIMARY KEY (id)
		)
		COLLATE='latin1_swedish_ci'
		ENGINE=InnoDB
		;

* **"tag"** table is where each tag will be stored.

		CREATE TABLE tags (
			id INT(11) NOT NULL AUTO_INCREMENT,
			tag VARCHAR(50) NULL DEFAULT NULL,
			PRIMARY KEY (id)
		)
		COLLATE='latin1_swedish_ci'
		ENGINE=InnoDB
		;

* **"image_tag_relation"** table is where the relation between images and tags will be stored.

		CREATE TABLE image_tag_relation (
			relation_id INT(11) NOT NULL AUTO_INCREMENT,
			image_id INT(11) NULL DEFAULT NULL,
			tag_confidence DOUBLE NULL DEFAULT NULL,
			tag_id INT(11) NULL DEFAULT NULL,
			PRIMARY KEY (relation_id)
		)
		COLLATE='latin1_swedish_ci'
		ENGINE=InnoDB
		;

Now we need to open HeidiSQL and connect to the database to configure the character set. 
You should see your database on the left-hand navigation bar. Right click the database name and select "Bulk table editor".
Click the "Convert to charset" box, then scroll down to "UTF-8 Unicode (utf8)" and click update. 
This will change the encoding of the database and allow it to accept non-english characters.

That's it! Your database should have all the tables ready to use with the default configurations.

Note: In order for the application to function properly, one should add the following to the my.ini file in the program files for MariaDB:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`> max_allowed_packet = 256M`

The variable needs to be changed for both the database server and the MySQL (forMariaDB) client. Log in to the client and type this command: 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`> set global max_allowed_packet = 1024*1024*256`


It needs to be "global" so it handles all client connections. 

These commands help allow the database to more smoothly handle the data of type "blob". This data type is needed to save actual images in the database. 

However, if you are using your database locally, the variable may reset everytime you restart your computer. To see what the current state of the max_allowed_packet variable is use the command: 

>`show variables like 'max_allowed_packet'`




## Configuring Hibernate

The next step is altering the hibernate configuration XML file (hibernate.cfg.xml) to allow it to connect to your database. The file is located at src/main/resources/hibernate.cfg.xml

You should see the following code:

		<property name="hibernate.connection.driver_class">org.mariadb.jdbc.Driver</property>
		<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/image-location-toolkit</property>
		<property name="hibernate.connection.username">root</property>
		<property name="hibernate.connection.password">password</property>
		
Replace the default MariaDB credentials `root` and `password` with your account credentials. 
If you setup the database with the default configurations, then skip this step and leave the credentials as `root` and `password`.

## Building the WAR File

Once you have everything configured and your database running, it's time to build the WAR (Web application ARchive) file. 
This is the file that will be deployed to the web server.
Open up a console (linux, MacOS) or Git Shell/PowerShell (Windows).
Navigate into your local "image-location-toolkit" git directory for the project.

1. **Clean** (good practice to clean the directory before each new build)

		gradle clean		
		
2. **Build** (build the WAR file for deployment)

		gradle build
		
		*Note: This may cause some errors to appear in Intellij; specifically, the IDE may claim that import resources do not exist. This can be fixed by going to View -> Tool Windows -> Gradle. In the Gradle tool window, click the refresh button at the top left. This should update the IDE and fix such errors.
		
3. **Deploy the WAR**

	* Navigate to and copy the freshly built WAR file located in your project's git directory. (example path: ~/image-location-toolkit/build/libs/image-location-toolkit.war)
    * Next, navigate into your Apache Tomcat installation directory.
    * Continue down the directories until you see a "webapps" folder, paste your WAR file INTO the "webapps" folder and give Tomcat a few seconds to deploy the war file.
    ***Note***: You may also deploy the WAR file via Tomcat's deployment manager. Instructions can be found [here](https://tomcat.apache.org/tomcat-8.0-doc/manager-howto.html).

Copy and paste `localhost:8080/image-location-toolkit` into a web browser, and the webapp should be visible and running.

If you have any questions, comments or concerns, feel free to contact any member of the team.
