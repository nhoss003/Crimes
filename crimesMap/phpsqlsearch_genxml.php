<?php
//require("phpsqlsearch_dbinfo.php");

// Get parameters from URL
$state = $_GET["state"];
$date1 = $_GET["date1"];
$date2 = $_GET["date2"];

//////////////
function parseToXML($htmlStr)
{
$xmlStr=str_replace('<','&lt;',$htmlStr);
$xmlStr=str_replace('>','&gt;',$xmlStr);
$xmlStr=str_replace('"','&quot;',$xmlStr);
$xmlStr=str_replace("'",'&#39;',$xmlStr);
$xmlStr=str_replace("&",'&amp;',$xmlStr);
return $xmlStr;
}

// Opens a connection to a MySQL server
$connection=mysql_connect ('localhost','root','home123');
if (!$connection) {
  die('Not connected : ' . mysql_error());
}
// Set the active MySQL database
$db_selected = mysql_select_db('business', $connection);
if (!$db_selected) {
  die ('Can\'t use db : ' . mysql_error());
}
// Select all the rows in the markers table
$query = sprintf("SELECT crime_type, address, latitude, longitude,incident_time,violent, state FROM `business`.`crimes` where state='%s' and incident_time between '%s' and '%s' limit 30000",
 
  mysql_real_escape_string($state),
   mysql_real_escape_string($date1),
   mysql_real_escape_string($date2)
  );


$result = mysql_query($query);
if (!$result) {
  die('Invalid query: ' . mysql_error());
}

//header("Content-type: text/xml");

// Start XML file, echo parent node
echo '<markers>';

// Iterate through the rows, printing XML nodes for each
while ($row = @mysql_fetch_assoc($result)){
  // ADD TO XML DOCUMENT NODE
  echo '<marker ';
  echo 'name="' . parseToXML($row['crime_type']) . '" ';
  echo 'address="' . parseToXML($row['address']) . '" ';
  echo 'lat="' . $row['latitude'] . '" ';
  echo 'lng="' . $row['longitude'] . '" ';
  echo 'type="' . $row['violent'] . '" ';
  echo 'date="' . $row['incident_time'] . '" ';
  echo '/>';
}

// End XML file
echo '</markers>';

?>

