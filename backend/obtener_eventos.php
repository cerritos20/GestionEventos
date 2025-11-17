<?php
include 'conexion.php';

// Consultamos los eventos
$consulta = "SELECT * FROM eventos";
$resultado = mysqli_query($conexion, $consulta);

$eventos = array();

while ($fila = mysqli_fetch_assoc($resultado)) {
    $eventos[] = $fila;
}

// Convertimos los datos a formato JSON (El idioma que habla Android)
echo json_encode($eventos);
mysqli_close($conexion);
?>