<?php
// Configuración básica
header('Content-Type: application/json');
include 'conexion.php'; // Archivo de conexión a la base de datos

// 1. Cargar librerías de Composer
require 'vendor/autoload.php';

// 👇 REEMPLAZA ESTE VALOR CON TU ID DE CLIENTE WEB REAL
$WEB_CLIENT_ID = "1044979475293-ao3pafcign6d2k2tlldtouumm0imra5m.apps.googleusercontent.com";

$respuesta = array('exito' => false, 'mensaje' => 'Error desconocido', 'usuario' => null);

// Obtener el JSON enviado por Kotlin
$json = file_get_contents('php://input');
$data = json_decode($json);

if (!isset($data->token)) {
    $respuesta['mensaje'] = 'Token no recibido.';
    echo json_encode($respuesta);
    exit();
}

$id_token = $data->token;

// 2. Inicializar el cliente de Google y verificar el token
try {
    $client = new Google_Client(['client_id' => $WEB_CLIENT_ID]);
    $payload = $client->verifyIdToken($id_token);

    if ($payload) {
        $google_id = $payload['sub'];
        $email = $payload['email'];
        $name = $payload['name'];

        // 3. Buscar usuario en la base de datos
        $sql_check = "SELECT id, nombre, email FROM usuarios WHERE email = ?";
        $stmt_check = $conn->prepare($sql_check);
        $stmt_check->bind_param("s", $email);
        $stmt_check->execute();
        $result_check = $stmt_check->get_result();

        if ($result_check->num_rows > 0) {
            // Usuario encontrado (Iniciar sesión)
            $user = $result_check->fetch_assoc();

            $respuesta['exito'] = true;
            $respuesta['mensaje'] = 'Inicio de sesión con Google exitoso.';
            $respuesta['usuario'] = array(
                'id' => (string)$user['id'],
                'nombre' => $user['nombre'],
                'email' => $user['email']
            );

        } else {
            // Usuario nuevo (Registrar)
            // NOTA: Usaremos el ID de Google como contraseña temporal si lo deseas,
            // pero es MEJOR dejar la columna 'password' NULL o vacía para logins sociales.
            // Para simplicidad, solo registraremos el nombre y email.

            $sql_insert = "INSERT INTO usuarios (nombre, email, id_google) VALUES (?, ?, ?)";
            $stmt_insert = $conn->prepare($sql_insert);

            // Asumiendo que has agregado la columna 'id_google' a tu tabla 'usuarios' (ver Paso 4)
            $stmt_insert->bind_param("sss", $name, $email, $google_id);

            if ($stmt_insert->execute()) {
                $new_id = $conn->insert_id;

                $respuesta['exito'] = true;
                $respuesta['mensaje'] = 'Registro con Google exitoso.';
                $respuesta['usuario'] = array(
                    'id' => (string)$new_id,
                    'nombre' => $name,
                    'email' => $email
                );
            } else {
                $respuesta['mensaje'] = 'Error al registrar el nuevo usuario: ' . $conn->error;
            }
        }
        $stmt_check->close();

    } else {
        // Token inválido (posiblemente expirado o manipulado)
        $respuesta['mensaje'] = 'Token de Google inválido.';
    }

} catch (Exception $e) {
    // Error en la verificación (ej. problema de red o ID de cliente incorrecto)
    $respuesta['mensaje'] = 'Error al verificar el token de Google: ' . $e->getMessage();
}

$conn->close();
echo json_encode($respuesta);
?>