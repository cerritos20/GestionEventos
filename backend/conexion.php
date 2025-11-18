<?php
// Aseguramos que siempre se trate como JSON
header('Content-Type: application/json; charset=utf-8');

$hostname = "localhost";
$database = "bd_eventos";
$username = "root";
$password = "";

// Ocultar errores de PHP para no romper el JSON
error_reporting(0);

$conexion = mysqli_connect($hostname, $username, $password, $database);

// Si falla la conexión, respondemos JSON y cerramos
if (!$conexion) {
    echo json_encode(array("exito" => false, "mensaje" => "Error conectando a BD"));
    exit;
}
// SIN ETIQUETA DE CIERRE