<?php 
/* API para ejecutar sentencias SQL datos de la BD */

include("credenciales.php");

/****** EJECUTAR SENTENCIAS ENVIADAS  ******************************************************
* Segun la 'opc' = opcion enviada se ejecuta el metodo indicado
*****************************************************************************/
if (isset($_GET["opcion"])) {
	$opcion = $_GET["opcion"];

	switch($opcion) {
		case "consultaroles":
			consultarRoles();
			break;
		default:
			$respuesta = array('micafe' => 'error al enviar opcion' );
			echo json_encode($respuesta);
			break;
	}
}else if (isset($_POST["opcion"])) {//enviados por el metodo POST
	$opcion = $_POST["opcion"];


	switch($opcion) {
		case "registrousuario":
			registrarUsuario($_POST["rol"],$_POST["nombre"],$_POST["tipodocumento"],$_POST["cedula"],$_POST["correo"],$_POST["contrasenia"],$_POST["celular"],$_POST["fechanacimiento"],$_POST["direccion"],$_POST["departamento"],$_POST["municipio"]);
			break;
		default:
			$respuesta = array('micafe' => 'error al enviar opcion' );
			echo json_encode($respuesta);
			break;
	}
}
else{
	$respuesta = array('micafe' => 'Error! metodo no encontrado' );
	echo json_encode($respuesta);
}



/****** LEER REGISTRO   ******************************************************
* ejecuta la consulta y devuelve datos en formato JSON
*****************************************************************************/
function leerRegistro($sql){
	include("credenciales.php");   			//Conecta a la BD $conexion
	$result = $conexion->query($sql);

	$rows = array();
	if ($result != NULL && $result->num_rows > 0) {
    
		while(($r =  mysqli_fetch_assoc($result))) {
		  	$rows["micafe"][] = $r;	  	
		}
		mysqli_free_result($result);
	}
	mysqli_close($conexion);
	echo json_encode($rows);
}


/*****************************************************************************
INSERTA, ACTUALIZA O ELIMINA REGISTROS DE LA BASE DE DATOS
*****************************************************************************/
function actualizarRegistro($sql){

		include("credenciales.php");

		if ($conexion->query($sql) === TRUE) {	
			$respuesta = array('ok' => 'actualizo');

		}else  {
	
			$respuesta = array('ok' => 'error' );
		}

		echo json_encode($respuesta);

}

function consultarRoles()
{
	$sql = "SELECT * FROM roles";
	leerRegistro($sql);
}

function registrarUsuario($rol,$nombre,$tipodocumento,$cedula,$correo,$contrasenia,$celular,$fechanacimiento,$direccion,$departamento,$municipio){
	switch ($rol) {
		case 'Caficultor':
			$idrol = 2;
			break;
		case 'Recolector':
			$idrol = 3;
			break;
		case 'Comerciante':
			$idrol = 4;
			break;
	}
	$sql = "INSERT INTO usuarios(nombre,correo,contrasenia,tipodocumento,cedula,celular,fechanacimiento,direccion,departamento,municipio,idrol) values ('{$nombre}','{$correo}','{$contrasenia}','{$tipodocumento}','{$cedula}','{$celular}','{$fechanacimiento}','{$direccion}','{$departamento}','{$municipio}',{$idrol})";
	actualizarRegistro($sql);
}

?>