<?php
include 'conexion.php';

$json = file_get_contents('php://input');
$datos = json_decode($json, true);

if (!isset($datos['usuario_id']) || !isset($datos['evento_id'])) {
    echo json_encode(array("exito" => false, "mensaje" => "Faltan datos"));
    exit;
}

$usuario_id = $datos['usuario_id'];
$evento_id = $datos['evento_id'];

// 1. Evitar duplicados (Si ya asiste, no lo registramos de nuevo)
$check = "SELECT * FROM asistencias WHERE usuario_id = '$usuario_id' AND evento_id = '$evento_id'";
$resultado_check = mysqli_query($conexion, $check);

if (mysqli_num_rows($resultado_check) > 0) {
    echo json_encode(array("exito" => false, "mensaje" => "Ya estás registrado en este evento"));
} else {
    // 2. Insertar asistencia
    $sql = "INSERT INTO asistencias (usuario_id, evento_id) VALUES ('$usuario_id', '$evento_id')";
    
    if (mysqli_query($conexion, $sql)) {
        echo json_encode(array("exito" => true, "mensaje" => "Asistencia confirmada"));
    } else {
        echo json_encode(array("exito" => false, "mensaje" => "Error BD: " . mysqli_error($conexion)));
    }
}

mysqli_close($conexion);
?>