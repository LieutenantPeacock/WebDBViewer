# Web DBViewer

Web DBViewer is a web application that mainly provides a convenient method to grant other users access to databases without the hassle of downloading database clients and giving out technical details such as the URL and credentials—only a browser is required. 

The designed purpose is to quickly give occasional users (e.g. QA team members) read-only access to certain databases. However, the application **does not prevent modification to tables**; indeed, users can execute statements that update tables if the connection user has the permissions. As such, it is incumbent on administrators to create database users with read-only privileges for this purpose.

Several features are implemented to make it easier for regular users to inspect databases to which they have access, including sorting tables, paging through tables, and executing queries.

## Installation
### WAR
The WAR file can be downloaded from any of the [releases](https://github.com/LieutenantPeacock/WebDBViewer/releases) or from [Maven Central](https://search.maven.org/artifact/com.lt-peacock/webdbviewer). 

### Docker Image
Use the following command to get the latest version:

```
docker pull ltpeacock/webdbviewer
```

Or specify a tag to get a particular version:

```
docker pull ltpeacock/webdbviewer:1.0.0
```

See all tags [here](https://hub.docker.com/r/ltpeacock/webdbviewer/tags).

## Build From Source
First, clone the project before following the instructions in the relevant section.

```
git clone https://github.com/LieutenantPeacock/WebDBViewer.git
```

### Build With Maven
After executing the following command in the project root, a WAR file will be generated in the target directory.

```
mvn clean package
```

### Build With Docker
Run the following command in the project root. A different image name can be used by changing the argument to `-t`.

```
docker build -t ltpeacock/webdbviewer .
```

## Configuration
### General
There are are many ways pass configuration values to the application. For those who want to build from source, these can be configured in `src/main/resources/application.properties`.

<ul>
<li>
Use a Java system property.

```
java -DpropName=propVal -jar webdbviewer.war
```

</li>
<li>
Use an environment variable (set it before running the WAR).

For Bash:

```
export varName="varVal"
```

For Windows CMD:

```
set varName="varVal"
```

</li>
<li>
Use a command-line argument.

```
java -jar webdbviewer.jar --varName=varVal
```

</li>
</ul>

This application is built upon Spring Boot, so any properties that it accepts can be configured. For instance, `server.port` can be set to change the port the application starts on (the default is 8080).

Web DBViewer additionally provides the following properties for configuration:

<table>
<tr><td><code>dbviewer.app-root</code></td><td>The root directory used to store application files. This can be a relative or absolute path. Default: <code>.dbviewer</code></td></tr>
<tr><td><code>dbviewer.db-dir</code></td><td>The path to store the database file at, if using the default embedded H2 database. Default: <code>./.dbviewer/db</code></td></tr>
<tr><td><code>dbviewer.drivers-dir</code></td><td>The directory to store the database driver JARs in. Default: <code>.dbviewer/ext/drivers</code></td></tr>
<tr><td><code>dbviewer.temp-password</code></td><td>The temporary password to use on the first run to setup the admin user. If not set, a random password will be generated.</td></tr>
<tr><td><code>dbviewer.conf-dir</code></td><td>The directory to store the application configuration files. Default: <code>.dbviewer/conf</code></td></tr>
<tr><td><code>dbviewer.show-raw-exceptions-to-users</code></td><td>Whether or not to show raw exceptions with stracktraces to regular (non-admin) users. Default: <code>false</code></td></tr>
</table>

### Databases Configuration

Place the database driver JARs inside the directory `.dbviewer/ext/drivers` or what the property `dbviewer.drivers-dir` is set to. If multiple JAR files are required to load a driver, place them in a folder with a descriptive name. (Folders with just a single JAR file are also accepted.) Only files present at the time the application is started will be picked up.

To configure the recognized database types, edit `dbconfig.xml` inside the directory `.dbviewer/conf` or what the property `dbviewer.conf-dir` points to. Check the default configuration for reference [here](src/main/resources/dbconfig.xml). On the first run, the application will create the file with all the defaults if it does not exist. To add another database type, create a new `<database>` element inside the root `<dbconfig>` element. The `name` attribute sets the displayed name for that database type. 

To further configure a database, there are certain child elements that will be read:

<table>
	<tr><td><code>&lt;url-format&gt;</code></td><td>Sets the default JDBC URL that will filled into the input field by default when that database type is selected.</td></tr>
	<tr><td><code>&lt;pagination-format&gt;</code></td><td>The part of the SQL query used for pagination in that database. Use <code>$offset</code> to represent the number of rows to skip and <code>$pagesize</code> for the number of rows to return per page.</td></tr>
</table>

## Running
### Using the WAR file
The WAR file is executable and can be run by Java (JRE 8 is required at a minimum).

```
java -jar webdbviewer.war
```

The WAR file can also be deployed on a servlet container like Tomcat. (For Tomcat, place it in the webapps directory.)

### Using Docker Image
```
docker run -dp 8080:8080 -v "path/to/app-root:/app/.dbviewer" ltpeacock/webdbviewer
```

To change the port to use, use `-p YOURPORT:8080`. The directory to store the application files is specified using a bind mount (`-v` option). To use the `.dbviewer` folder in the current directory, use `-v "$(pwd)/.dbviewer:/app/.dbviewer"` for Unix or `-v "%cd%\.dbviewer:/app/.dbviewer"` for Windows CMD.

To not detach the container (i.e. run it in the background) and see the logs while running, remove the `-d` flag (keep `-p`).

### Using Docker Compose
Clone the repository and run the following in the project root:

```
docker compose up -d
```

By default, the 8080 port is used for the application and the `.dbviewer` directory in the directory containing `docker-compose.yml` is used to store the application files. 

To override these values, set the `APP_PORT` and/or `APP_FOLDER_ROOT` environment variables before running `docker compose`.

To set environment variables for the application, edit the `.env` file in the project root, with each line in the format `VAR=VAL`. (Note that hyphens are not accepted in variable names, so use camel case instead; the application accepts relaxed property bindings.) A different environment file can be used by specifying the `ENV_FILE` environment variable.