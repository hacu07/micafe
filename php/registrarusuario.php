<?php 

include("credenciales.php");
$conexion = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

/* Obtiene los parametros enviados y registra los datos del usuario en la BD */

//Validacion de parametros obligatorios enviados
if (isset($_POST['nombre']) && isset($_POST['contrasenia']) && isset($_POST['cedula']) && isset($_POST['celular']) && isset($_POST['direccion']) && isset($_POST['departamento']) && isset($_POST['municipio']) && isset($_POST['idrol'])) {
	//Concatenacion de sentencia sql con variables enviadas
	$sql =  "INSERT INTO usuarios(nombre,correo,contrasenia,cedula,celular,fechanacimiento,direccion, departamento,municipio,idrol) values(?,?,?,?,?,?,?,?,?,?)";
	$stmt = $conexion->prepare($sql);
	$stmt->bind_param('sssssssssi',$_POST['nombre'],$_POST['correo'],$_POST['contrasenia'],$_POST['cedula'],$_POST['celular'],$_POST['fechanacimiento'],$_POST['direccion'],$_POST['departamento'],$_POST['municipio'],$_POST['idrol']);
	if ($stmt->execute()) {//Verifica si se almaceno
		echo "Registro correcto";
	} else {
		echo "Error en el registro \n Intente nuevamente.";
	}
	mysqli_close($conexion);
} else {
	echo "Error! Faltan datos obligatorios.";
}


?>