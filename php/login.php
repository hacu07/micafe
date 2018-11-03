<?php 

include("credenciales.php");
$conexion = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

$json=array();

/* Obtiene los parametros enviados y registra los datos del usuario en la BD */
//Validacion de parametros obligatorios enviados
if (isset($_POST['cedula']) && isset($_POST['contrasenia'])) {
	$cedula = $_POST['cedula'];
	$contrasenia = $_POST['contrasenia'];
	$sql = "select * from usuarios where cedula = {$cedula} and contrasenia = {$contrasenia} ";
	$resultado = mysqli_fetch_array($conexion,$sql);//se ejecuta la consulta

	if ($registro = mysqli_fetch_array($result)) {
		$result["id"]=$registro['id'];
		$result["nombre"]=$registro['nombre'];
		$result["correo"]=$registro['correo'];
		$result["contrasenia"]=$registro['contrasenia'];
		$result["cedula"]=$registro['cedula'];
		$result["celular"]=$registro['celular'];
		$result["fechanacimiento"]=$registro['fechanacimiento'];
		$result["direccion"]=$registro['direccion'];
		$result["departamento"]=$registro['departamento'];
		$result["municipio"]=$registro['municipio'];
		$result["urlimagen"]=$registro['urlimagen'];
		$result["idrol"]=$registro['idrol'];
		$json['usuario'][]=$result;
	} else {
		$result["id"]=0;
		$result["nombre"]= 'no registra';
		$result["correo"]= 'no registra';
		$result["contrasenia"]= 'no registra';
		$result["cedula"]= 'no registra';
		$result["celular"]= 'no registra';
		$result["fechanacimiento"]= 'no registra';
		$result["direccion"]= 'no registra';
		$result["departamento"]= 'no registra';
		$result["municipio"]= 'no registra';
		$result["urlimagen"]= 'no registra';
		$result["idrol"]= 'no registra';
		$json['usuario'][]=$result;
	}
	mysqli_close($conexion);
	echo json_encode($json);
} else {
	$resultar["success"]=0;
	$resultar["message"]='Ws no Retorna';
	$json['usuario'][]=$resultar;
	echo json_encode($json);
}

?>