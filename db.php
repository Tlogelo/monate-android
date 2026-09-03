<?php

$host = "localhost";
$user = "root";
$password = "";
$database = "monate_chicken";

$conn = mysqli_connect($host,$user,$password,$database);

if(!$conn){
    die("Connection Failed: ".mysqli_connect_error());
}

?>