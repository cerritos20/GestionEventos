<?php
include 'conexion.php';

$json = file_get_contents('php://input');
$datos = json_decode($json, true);

if (!isset($datos['nombre']) || !isset($datos['email']) || !isset($datos['password'])) {
    echo json_encode(array("exito" => false, "mensaje" => "Faltan datos"));
    exit;
}

$nombre = $datos['nombre'];
$email = $datos['email'];
$password = $datos['password'];

// 1. Verificar si el correo ya existe
$checkEmail = "SELECT * FROM usuarios WHERE email = '$email'";
$checkResult = mysqli_query($conexion, $checkEmail);

if (mysqli_num_rows($checkResult) > 0) {
    echo json_encode(array("exito" => false, "mensaje" => "El correo ya está registrado"));
} else {
    // 2. Insertar usuario nuevo
    $sql = "INSERT INTO usuarios (nombre, email, password) VALUES ('$nombre', '$email', '$password')";
    
    if (mysqli_query($conexion, $sql)) {
        echo json_encode(array("exito" => true, "mensaje" => "Usuario registrado correctamente"));
    } else {
        echo json_encode(array("exito" => false, "mensaje" => "Error BD: " . mysqli_error($conexion)));
    }
}

mysqli_close($conexion);
?>