<?php
include 'conexion.php';

// 1. Recibir los datos que manda Android (JSON)
$json = file_get_contents('php://input');
$datos = json_decode($json, true);

// 2. Validar que lleguen los datos obligatorios
if (!isset($datos['titulo']) || !isset($datos['descripcion']) || !isset($datos['ubicacion'])) {
    echo json_encode(array("exito" => false, "mensaje" => "Faltan datos"));
    exit;
}

$titulo = $datos['titulo'];
$descripcion = $datos['descripcion'];
$fecha = $datos['fecha_hora']; // Debe venir formato 'YYYY-MM-DD HH:MM:SS'
$ubicacion = $datos['ubicacion'];

// Valores por defecto (mientras no tengamos sistema de usuarios complejo)
$organizador_id = 1; 
$imagen_url = "https://via.placeholder.com/150"; // Imagen genérica

// 3. Insertar en la Base de Datos
$sql = "INSERT INTO eventos (titulo, descripcion, fecha_hora, ubicacion, organizador_id, imagen_url) 
        VALUES ('$titulo', '$descripcion', '$fecha', '$ubicacion', '$organizador_id', '$imagen_url')";

if (mysqli_query($conexion, $sql)) {
    // Si funcionó
    echo json_encode(array("exito" => true, "mensaje" => "Evento guardado correctamente"));
} else {
    // Si falló (error SQL)
    echo json_encode(array("exito" => false, "mensaje" => "Error BD: " . mysqli_error($conexion)));
}

mysqli_close($conexion);
?>