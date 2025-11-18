<?php
header('Content-Type: application/json; charset=utf-8');
error_reporting(0);

$conexion = mysqli_connect("localhost", "root", "", "bd_eventos");

if (!isset($_GET['evento_id'])) {
    echo json_encode(array());
    exit;
}

$evento_id = $_GET['evento_id'];

// 👇 Usamos 'AS' para cambiar el nombre al vuelo y que Android lo entienda
$sql = "SELECT c.texto as comentario, c.calificacion, c.fecha_comentario as fecha, u.nombre 
        FROM comentarios c 
        JOIN usuarios u ON c.usuario_id = u.id 
        WHERE c.evento_id = '$evento_id' 
        ORDER BY c.fecha_comentario DESC";

$resultado = mysqli_query($conexion, $sql);
$comentarios = array();

while($row = mysqli_fetch_assoc($resultado)) {
    $comentarios[] = $row;
}

echo json_encode($comentarios);
mysqli_close($conexion);
?>