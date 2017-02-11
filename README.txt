Objective
======================
Collecting crime data for cities throughout the united states.



LIBRARIES
======================
Under lib directory, containing (1) opencsv-3.7, (2) mysql-connector-java-5.1.38-bin.jar, (3) org.json.jar



CODE FILES
======================

1- Crimes:

-For each city there is a class: CityName.java

-Main.java: is the main class that reads all of the files within the folder and insert the csv files contents to the database.

-violent.java: reads from business.violent table and business.nonviolent table to determine wheter a crime is considered as a violent crime or not.

-Util.java: coverts a date to timestamp

2-socrata

There are around 500 links(counties) at http://moto.data.socrata.com/ that contain crimes databases. 

downloadfull.sh is a script that downloads these 500 csv files. It should be executed on server in a folder called socrata2 by the following command:

bash downloadfull.sh

3-LLZ:

LLZ: A seperate process that fills empty latitude,longitude and zipcode.

-Location.java: geocoding (Google API) for latitude and longitude

-reverseGeoCode: reverse geocoding (Google API) for address

-Elevation.java: elevation (Google API) for elevation and elevation Type

-LocationCache.java: read and write to homeDB.locationCache table  

-ApiCall.java: calls either geocode api or reversegeocode api or elevation api

4- crimesMap is on v75.cs.ucr.edu/crimesMap.
The source code is on v75 at /var/www/html/crimesMap.
It is based on google maps api.

========================

crimes table:

+------------------+------------------+------+-----+---------------------+----------------+
| Field            | Type             | Null | Key | Default             | Extra          |
+------------------+------------------+------+-----+---------------------+----------------+
| id               | int(10) unsigned | NO   | PRI | NULL                | auto_increment |
| crime_type       | varchar(100)     | NO   |     | NA                  |                |
| crawl_time       | timestamp        | NO   |     | 0000-00-00 00:00:00 |                |
| address          | varchar(100)     | YES  |     | NULL                |                |
| city             | varchar(40)      | YES  |     | NULL                |                |
| state            | char(2)          | YES  |     | NULL                |                |
| zip              | varchar(5)       | YES  |     | NULL                |                |
| latitude         | decimal(9,6)     | YES  |     | NULL                |                |
| longitude        | decimal(9,6)     | YES  |     | NULL                |                |
| source_url       | varchar(200)     | NO   |     | NULL                |                |
| incident_time    | timestamp        | YES  |     | 0000-00-00 00:00:00 |                |
| locationAccuracy | varchar(20)      | YES  |     | NULL                |                |
| violent          | tinyint(1)       | NO   |     | 0                   |                |
+------------------+------------------+------+-----+---------------------+----------------+

