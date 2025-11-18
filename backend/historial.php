<?php
header('Content-Type: application/json; charset=utf-8');
error_reporting(0);

include 'conexion.php';

// Consulta: Traer eventos donde la fecha sea MENOR a hoy (pasados)
// Ordenados del más reciente al más antiguo
$sql = "SELECT * FROM eventos WHERE fecha_hora < NOW() ORDER BY fecha_hora DESC";

$resultado = mysqli_query($conexion, $sql);
$eventos = array();

if (mysqli_num_rows($resultado) > 0) {
    while($row = mysqli_fetch_assoc($resultado)) {
        $eventos[] = $row;
    }
}

echo json_encode($eventos);
mysqli_close($conexion);
?>