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

		case 'iniciarsesion':
			validarUsuario($_GET["usuario"],$_GET["contrasenia"]);
			break;

		case 'validarinsercionreporte':
			validarinsercionreporte($_GET["nombre"],$_GET["correo"],$_GET["contrasenia"],$_GET["tipodocumento"],$_GET["cedula"],$_GET["celular"],$_GET["fechanacimiento"],$_GET["direccion"],$_GET["departamento"],$_GET["municipio"],$_GET["rol"]);
			break;
		case 'consultarfincas':
			consultarFincasCaficultor($_GET["caficultor"]);
			break;
		case 'consultarofertascaficultor':
			consultarOfertasCaficultor($_GET["idadministrador"]);
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
		case 'registrofinca':
			registrarFinca($_POST["idadministrador"],$_POST["nombrefinca"],$_POST["departamento"],$_POST["municipio"],$_POST["corregimiento"],$_POST["vereda"],$_POST["hectareas"],$_POST["telefono"]);
			break;
		case 'registroferta':
			registrarOferta($_POST["idadministrador"],$_POST["nombrefinca"],$_POST["idmodopago"],$_POST["valorpago"],$_POST["vacantes"],$_POST["diastrabajo"],$_POST["planta"],$_POST["servicios"]);
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
			$respuesta = "Actualizo";

		}else{
	
			$respuesta = "Error";
		}

		echo $respuesta;

}

function validarinsercionreporte($nombre, $correo, $contrasenia, $tipodocumento, $cedula, $celular, $fechanacimiento, $direccion, $departamento, $municipio, $rol)
{
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
	$sql = "call sp_validarinsercionusuario('{$nombre}','{$correo}','{$contrasenia}','{$tipodocumento}','{$cedula}','{$celular}','{$fechanacimiento}','{$direccion}','{$departamento}','{$municipio}',{$idrol})";
	
	leerRegistro($sql);
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
	$sql = "call sp_validarinsercionusuario('{$nombre}','{$correo}','{$contrasenia}','{$tipodocumento}','{$cedula}','{$celular}','{$fechanacimiento}','{$direccion}','{$departamento}','{$municipio}',{$idrol});";
	actualizarRegistro($sql);
	}

function validarUsuario($usuario,$contrasenia){
	$sql = "SELECT * FROM usuarios WHERE cedula = {$usuario} AND contrasenia = {$contrasenia}";
	leerRegistro($sql);
}

function registrarFinca($idadministrador,$nombrefinca,$departamento,$municipio,$corregimiento,$vereda,$hectareas,$telefono){
	$sql = "INSERT INTO fincas(idadministrador,nombre,departamento,municipio,corregimiento,vereda,hectareas,telefono) values({$idadministrador},'{$nombrefinca}','{$departamento}','{$municipio}','{$corregimiento}','{$vereda}',{$hectareas},'{$telefono}')";
	actualizarRegistro($sql);
}

function consultarFincasCaficultor($idCaficultor)
{
	$sql = "SELECT fincas.id, fincas.nombre FROM fincas WHERE fincas.idadministrador = {$idCaficultor}";
	leerRegistro($sql);
}

function registrarOferta($idadministrador,$nombrefinca,$idmodopago,$valorpago,$vacantes,$diastrabajo,$planta,$servicios)
{
	$idFinca = "SELECT fincas.id FROM fincas WHERE fincas.nombre LIKE '{$nombrefinca}' AND fincas.idadministrador = '{$idadministrador}'";
	$sql = "INSERT INTO ofertas(idfinca,idmodopago,valorpago,vacantes,diastrabajo,planta,servicios,fechacreacion) VALUES(({$idFinca}),{$idmodopago},{$valorpago},{$vacantes},{$diastrabajo},'{$planta}','{$servicios}', NOW())";
	actualizarRegistro($sql); 
}

function consultarOfertasCaficultor($idadministrador)
{
	$sql = "SELECT ofertas.id, fincas.nombre , ofertas.fechacreacion, ofertas.vacantes FROM ofertas 
	JOIN fincas ON  ofertas.idfinca = fincas.id 
    JOIN usuarios ON fincas.idadministrador = usuarios.id
    WHERE usuarios.id = {$idadministrador} 
    order by ofertas.fechacreacion asc"; //OJO cambiar a fecha de inicio
    leerRegistro($sql);
}

?>