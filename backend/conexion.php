<?php
$hostname = "localhost";
$database = "bd_eventos";
$username = "root";
$password = ""; // En XAMPP suele estar vacía por defecto

$conexion = mysqli_connect($hostname, $username, $password, $database);

if (!$conexion) {
    die("Error de conexión: " . mysqli_connect_error());
}
?>