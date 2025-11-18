<?php
include 'conexion.php';

$json = file_get_contents('php://input');
$datos = json_decode($json, true);

if (!isset($datos['email']) || !isset($datos['password'])) {
    echo json_encode(array("exito" => false, "mensaje" => "Faltan datos"));
    exit;
}

$email = $datos['email'];
$password = $datos['password'];

$consulta = "SELECT * FROM usuarios WHERE email = '$email' AND password = '$password'";
$resultado = mysqli_query($conexion, $consulta);

if ($fila = mysqli_fetch_assoc($resultado)) {
    $respuesta['exito'] = true;
    $respuesta['mensaje'] = " " . $fila['nombre'];
    $respuesta['usuario'] = $fila; 
} else {
    $respuesta['exito'] = false;
    $respuesta['mensaje'] = "Correo o contraseña incorrectos";
}

echo json_encode($respuesta);
mysqli_close($conexion);
?>