<?php
header('Content-Type: application/json; charset=utf-8');
error_reporting(0);

$conexion = mysqli_connect("localhost", "root", "", "bd_eventos");

if (!$conexion) {
    echo json_encode(array("exito" => false, "mensaje" => "Fallo conexion BD"));
    exit;
}

$json = file_get_contents('php://input');
$datos = json_decode($json, true);

if (!isset($datos['usuario_id']) || !isset($datos['evento_id']) || !isset($datos['comentario'])) {
    echo json_encode(array("exito" => false, "mensaje" => "Faltan parametros"));
    exit;
}

$usuario_id = $datos['usuario_id'];
$evento_id = $datos['evento_id'];
$contenido = $datos['comentario']; // El texto que viene del celular
$calificacion = isset($datos['calificacion']) ? $datos['calificacion'] : 5;

// 👇 AQUÍ ESTABA EL ERROR: Cambiamos 'comentario' por 'texto'
$sql = "INSERT INTO comentarios (usuario_id, evento_id, texto, calificacion) 
        VALUES ('$usuario_id', '$evento_id', '$contenido', '$calificacion')";

if (mysqli_query($conexion, $sql)) {
    echo json_encode(array("exito" => true, "mensaje" => "Comentario guardado"));
} else {
    echo json_encode(array("exito" => false, "mensaje" => "Error SQL: " . mysqli_error($conexion)));
}
mysqli_close($conexion);
?>