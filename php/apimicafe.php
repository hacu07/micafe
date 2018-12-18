<?php 
/* API para ejecutar sentencias SQL datos de la BD */

include("credenciales.php");

/****** EJECUTAR SENTENCIAS ENVIADAS  ******************************************************
* Segun la 'opc' = opcion enviada se ejecuta el metodo indicado
*****************************************************************************/
if (isset($_GET["opcion"])) {
	$opcion = $_GET["opcion"];

	switch($opcion) {
		//Opcion de MODULO CAFICULTOR
		case "consultaroles":
			consultarRoles();
			break;

		case 'iniciarsesion':
			validarUsuario($_GET["usuario"],$_GET["contrasenia"]);
			break;

		case 'validarinsercionreporte':
			validarinsercionreporte($_GET["nombre"],"",$_GET["contrasenia"],$_GET["tipodocumento"],$_GET["cedula"],$_GET["celular"],"","","","",$_GET["rol"]);
			break;
		case 'consultarfincas':
			consultarFincasCaficultor($_GET["caficultor"]);
			break;
		case 'consultarofertascaficultor':
			consultarOfertasCaficultor($_GET["idadministrador"]);
			break;
		case 'cafconsultardetalleoferta':
			consultarDetalleOfertaCaficultor($_GET["idoferta"]);
			break;
		case 'consultarpostuladosofertacaficultor':
			consultarPostuladosOfertaCaficultor($_GET["idoferta"]);
			break;
			case 'consultaraceptadosofertacaficultor':
			consultarAceptadosOfertaCaficultor($_GET["idoferta"]);
			break;
		case 'consultardatospostulado':
			consultarDatosPostulado($_GET['cedulapostulado']);
			break;
		case 'consultarlistadopesadasrecolector':
			consultarListadoPesadasRecolector($_GET["idoferta"],$_GET["cedula"]);
			break;
		case 'consultarcalificacionpostulado':
		consultarcalificacionpostulado($_GET["cedularecolector"]);
		break;
		case 'consultarcostosoferta':
			consultarCostosOferta($_GET["idoferta"]);
		break;
		case 'consultarexperienciaspostulado':
			consultarExperienciasPostulado($_GET["cedulaPostulado"]);
		break;
		case 'consultarfincascaficultor':
			consultarFincasCaficultor($_GET["idcaficultor"]);
		break;
		case 'consultardetallefinca':
			consultarDetalleFinca($_GET["idfinca"]);
			break;


		//GET RECOLECTOR - OPCION DE MODULO RECOLECTOR
		case 'consultarofertasrecolector':
			consultarOfertasRecolector();
			break;
		case 'consultardetalleofertarecolector':
			consultarDetalleOfertaRecolector($_GET["idoferta"]);
			break;
		case 'consultartrabajosrecolector':
			consultarTrabajosRecolector($_GET["idrecolector"]);
			break;
		case 'consultarexperienciasrecolector':
			consultarExperienciasRecolector($_GET["idrecolector"]);
			break;
		case 'consultardetalletrabajorecolector':
			consultarDetalleTrabajorecolector($_GET["idoferta"]);
			break;
		case 'consultarlistadopesadasmodulorecolector':
			consultarListadoPesadasModuloRecolector($_GET["idoferta"],$_GET["idrecolector"]);
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
			registrarFinca($_POST["idadministrador"],$_POST["nombrefinca"],$_POST["departamento"],$_POST["municipio"],$_POST["corregimiento"],$_POST["vereda"],$_POST["hectareas"],$_POST["telefono"],$_POST["latitud"],$_POST["longitud"]);
			break;
		case 'registroferta':
			registrarOferta($_POST["idadministrador"],$_POST["nombrefinca"],$_POST["idmodopago"],$_POST["valorpago"],$_POST["vacantes"],$_POST["diastrabajo"],$_POST["planta"],$_POST["servicios"],$_POST["fechainicio"]);
			break;
		case 'cambiarestadopostulado':
			cambiarEstadoPostulado($_POST["idoferta"],$_POST["cedula"]);
			break;
		case 'registrarcostooferta':
			registrarCostoOferta($_POST["idoferta"],$_POST["titulo"],$_POST["valor"],$_POST["descripcion"]);
			break;
		case 'registrarcalificacion':
			registrarCalificacion($_POST["idadmon"],$_POST["cedularecolector"],$_POST["comentario"],$_POST["calificacion"]);
			break;
		case 'actualizardatosfinca':
			actualizarDatosFinca($_POST["idfinca"],$_POST["idadministrador"],$_POST["nombre"],$_POST["departamento"],$_POST["municipio"],$_POST["corregimiento"],$_POST["vereda"],$_POST["hectareas"],$_POST["telefono"]);
			break;
	

		//POST RECOLECTOR
		case 'postularrecolector':
			postularRecolector($_POST["idoferta"],$_POST["idrecolector"]);
			break;
		case 'actualizarperfilrecolector':
			actualizarPerfilRecolector($_POST["cedula"],$_POST["correo"],$_POST["celular"],$_POST["imagen"],$_POST["direccion"],$_POST["departamento"],$_POST["municipio"]);
			break;
		case 'registrarexperienciarecolector':
			registrarExperienciaRecolector($_POST["idrecolector"],$_POST["empresa"],$_POST["cargo"],$_POST["funciones"],$_POST["tiempo"],$_POST["supervisor"],$_POST["contactosupervisor"]);
			break;
		case 'registrarpesadarecolector':
			registrarPesadaRecolector($_POST["idoferta"],$_POST["cedula"],$_POST["kilos"]);
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

function registrarFinca($idadministrador,$nombrefinca,$departamento,$municipio,$corregimiento,$vereda,$hectareas,$telefono,$latitud,$longitud){
	$sql = "INSERT INTO fincas(idadministrador,nombre,departamento,municipio,corregimiento,vereda,hectareas,telefono,latitud,longitud) values({$idadministrador},'{$nombrefinca}','{$departamento}','{$municipio}','{$corregimiento}','{$vereda}',{$hectareas},'{$telefono}',{$latitud},{$longitud})";
	actualizarRegistro($sql);
}

function consultarFincasCaficultor($idCaficultor)
{
	$sql = "SELECT fincas.id, fincas.nombre FROM fincas WHERE fincas.idadministrador = {$idCaficultor}";
	leerRegistro($sql);
}

function registrarOferta($idadministrador,$nombrefinca,$idmodopago,$valorpago,$vacantes,$diastrabajo,$planta,$servicios,$fechainicio)
{
	$idFinca = "SELECT fincas.id FROM fincas WHERE fincas.nombre LIKE '{$nombrefinca}' AND fincas.idadministrador = '{$idadministrador}'";
	$sql = "INSERT INTO ofertas(idfinca,idmodopago,valorpago,vacantes,diastrabajo,planta,servicios,fechacreacion,fechainicio) VALUES(({$idFinca}),{$idmodopago},{$valorpago},{$vacantes},{$diastrabajo},'{$planta}','{$servicios}', NOW(),'{$fechainicio}')";
	actualizarRegistro($sql); 
}

function consultarOfertasCaficultor($idadministrador)
{
	$sql = "SELECT ofertas.id, fincas.nombre , ofertas.fechainicio, ofertas.vacantes FROM ofertas 
	JOIN fincas ON  ofertas.idfinca = fincas.id 
    JOIN usuarios ON fincas.idadministrador = usuarios.id
    WHERE usuarios.id = {$idadministrador} 
    order by ofertas.fechainicio asc"; //OJO cambiar a fecha de inicio
    leerRegistro($sql);
}

function consultarDetalleOfertaCaficultor($idOferta)
{
	$sql = "SELECT ofertas.fechainicio, modospago.modo as modopago, ofertas.valorpago, ofertas.vacantes, ofertas.diastrabajo, ofertas.planta, ofertas.servicios
		FROM ofertas JOIN modospago ON ofertas.idmodopago = modospago.id
		WHERE ofertas.id = {$idOferta}";
	leerRegistro($sql);
}

function consultarPostuladosOfertaCaficultor($idOferta)
{
	$sql = "SELECT usuarios.cedula, usuarios.nombre, usuarios.fechanacimiento, usuarios.urlimagen 
			FROM recolectoresoferta 
			JOIN usuarios ON recolectoresoferta.idrecolector = usuarios.id
			Where recolectoresoferta.idestado = 2
			AND recolectoresoferta.idoferta = {$idOferta}";
	leerRegistro($sql);
}

function consultarAceptadosOfertaCaficultor($idOferta)
{
	$sql = "SELECT usuarios.cedula, usuarios.nombre, usuarios.fechanacimiento, usuarios.urlimagen 
			FROM recolectoresoferta 
			JOIN usuarios ON recolectoresoferta.idrecolector = usuarios.id
			Where recolectoresoferta.idestado = 1
			AND recolectoresoferta.idoferta = {$idOferta}";
	leerRegistro($sql);
}


function consultarDatosPostulado($cedulaPostulado)
{
	$sql = "SELECT usuarios.nombre, usuarios.correo, usuarios.celular, usuarios.fechanacimiento, usuarios.direccion, usuarios.departamento, usuarios.municipio, usuarios.urlimagen
		FROM usuarios 
		WHERE usuarios.cedula = '{$cedulaPostulado}'";
	leerRegistro($sql);
}

function cambiarEstadoPostulado($idOferta,$cedula)
{
	$sql = "UPDATE recolectoresoferta SET recolectoresoferta.idestado = 1 WHERE recolectoresoferta.idoferta = {$idOferta} AND recolectoresoferta.idrecolector = (SELECT usuarios.id FROM usuarios WHERE usuarios.cedula = '{$cedula}')";
	actualizarRegistro($sql);
}

function registrarPesadaRecolector($idOferta, $cedula, $kilos)
{
	$sql = "INSERT INTO pesadas(pesadas.kilos,pesadas.fecha,pesadas.idrecolector,pesadas.idoferta) 
		VALUES({$kilos},NOW(),(SELECT usuarios.id FROM usuarios WHERE usuarios.cedula = {$cedula}), {$idOferta})";
	actualizarRegistro($sql);
}


/*SELECT pesadas.fecha, pesadas.kilos, ofertas.valorpago as valorkilo, (pesadas.kilos * ofertas.valorpago ) as valorpesada 
FROM pesadas JOIN ofertas ON pesadas.idoferta = ofertas.id
*/

function consultarListadoPesadasRecolector($idOferta, $cedula)
{
	$sql = "SELECT pesadas.fecha, pesadas.kilos, ofertas.valorpago as valorkilo, (pesadas.kilos 	* ofertas.valorpago ) as valorpesada 
			FROM pesadas JOIN ofertas ON pesadas.idoferta = ofertas.id 
			WHERE pesadas.idoferta = {$idOferta} AND pesadas.idrecolector = (SELECT usuarios.id FROM usuarios WHERE usuarios.cedula = '{$cedula}')";
	leerRegistro($sql);
}

function registrarCostoOferta($idOferta, $titulo, $valor, $descripcion)
{
	$sql = "INSERT INTO costos(idoferta, titulo, descripcion, valor, fecha) 
			VALUES({$idOferta}, '{$titulo}', '{$descripcion}', {$valor}, NOW());";
	actualizarRegistro($sql);
}

function registrarCalificacion($idAdministrador, $cedulaRecolector, $comentario, $calificacion)
{
	$sql = "INSERT INTO comentarios(idadministrador,idrecolector,comentario,calificacion) VALUES({$idAdministrador}, (SELECT usuarios.id FROM usuarios WHERE usuarios.cedula = '{$cedulaRecolector}'), '{$comentario}', {$calificacion} )";
	actualizarRegistro($sql);
}

function consultarcalificacionpostulado($cedulaRecolector){
    $sql = "SELECT usuarios.nombre, usuarios.celular, comentarios.calificacion, comentarios.comentario 
    FROM usuarios JOIN comentarios ON usuarios.id = comentarios.idadministrador
    WHERE comentarios.idrecolector = (SELECT usuarios.id FROM usuarios where usuarios.cedula = '{$cedulaRecolector}')";
    leerRegistro($sql);
}

function consultarCostosOferta($idOferta)
{
	$sql = "SELECT titulo,valor,descripcion,fecha FROM costos WHERE idoferta = {$idOferta} ORDER BY costos.fecha DESC";
	leerRegistro($sql);
}

function consultarExperienciasPostulado($cedulaPostulado)
{
	$sql = "SELECT empresa,cargo,funciones,tiempo, supervisor, contactosupervisor 
			FROM experiencias JOIN usuarios ON experiencias.idrecolector = usuarios.id 
			where usuarios.cedula like '{$cedulaPostulado}'";
	leerRegistro($sql);
}

function consultarDetalleFinca($idFinca)
{
	$sql = "SELECT nombre, departamento, municipio, corregimiento, vereda, hectareas, telefono FROM fincas WHERE id = {$idFinca}";
	leerRegistro($sql);
}


function actualizarDatosFinca($idfinca ,$idadministrador ,$nombre ,$departamento ,$municipio ,$corregimiento ,$vereda ,$hectareas ,$telefono )
{
	$sql = "UPDATE fincas SET nombre = '{$nombre}', departamento = '{$departamento}', municipio = '{$municipio}', corregimiento = '{$corregimiento}', vereda = '{$vereda}' , hectareas = {$hectareas}, telefono = '{$telefono}' WHERE id = $idfinca AND idAdministrador = $idadministrador";
	actualizarRegistro($sql);
}

/*function consultarFincasCaficultor($idCaficultor)
{
	$sql = "SELECT fincas.id, fincas.nombre FROM fincas WHERE fincas.id = {$idCaficultor}";
	leerRegistro($sql);
}*/

/*********** ****************  			  ************** *********************
								RECOLECTOR
******************************************************************************/

function consultarOfertasRecolector(){
	$sql = "SELECT ofertas.id, fincas.nombre , ofertas.fechainicio, ofertas.vacantes FROM ofertas 
	JOIN fincas ON  ofertas.idfinca = fincas.id 
    JOIN usuarios ON fincas.idadministrador = usuarios.id
    order by ofertas.fechainicio asc";
    leerRegistro($sql);
}

function consultarDetalleOfertaRecolector($idOferta){
	$sql = "SELECT ofertas.fechainicio, ofertas.idmodopago, ofertas.valorpago, ofertas.vacantes, ofertas.diastrabajo, ofertas.planta, ofertas.servicios, fincas.nombre as nombrefinca, usuarios.nombre as nombreadmin, fincas.telefono, fincas.departamento, fincas.municipio, fincas.corregimiento, fincas.vereda
		from ofertas JOIN fincas ON ofertas.idfinca = fincas.id
		JOIN usuarios ON fincas.idadministrador = usuarios.id
		WHERE ofertas.id = {$idOferta}";
	leerRegistro($sql);
}

function consultarDetalleTrabajorecolector($idOferta)
{
	$sql = "SELECT ofertas.fechainicio, modospago.modo, ofertas.valorpago, ofertas.vacantes, ofertas.diastrabajo, ofertas.planta, ofertas.servicios, fincas.nombre as nombrefinca, usuarios.nombre as nombreadmin, fincas.telefono, fincas.departamento, fincas.municipio, fincas.corregimiento, fincas.vereda from ofertas JOIN fincas ON ofertas.idfinca = fincas.id JOIN usuarios ON fincas.idadministrador = usuarios.id JOIN modospago ON ofertas.idmodopago = modospago.id WHERE ofertas.id = {$idOferta}";
	leerRegistro($sql);
}

function postularRecolector($idOferta,$idRecolector){
	$sql = "INSERT INTO recolectoresoferta(idoferta,idrecolector,idestado) values({$idOferta},{$idRecolector},2)";
	actualizarRegistro($sql);
}

function actualizarPerfilRecolector($cedula, $correo, $celular, $imagen, $direccion, $departamento, $municipio){
	$path = "imagenes/$cedula.jpg";
	$url = "imagenes/".$cedula.".jpg";//ruta de almacenamiento de la imagen
	file_put_contents($path,base64_decode($imagen));//mueve la foto a la ruta especificada
	$sql = "UPDATE usuarios SET correo='{$correo}' , celular='{$celular}' , urlimagen='{$url}', direccion='{$direccion}' , departamento='{$departamento}' , municipio='{$municipio}' WHERE cedula = '{$cedula}'";
	actualizarRegistro($sql);
}

function registrarExperienciaRecolector($idRecolector, $empresa, $cargo, $funciones, $tiempo, $supervisor, $contactoSupervisor)
{
	$sql = "INSERT INTO experiencias(idrecolector,empresa,cargo,funciones,tiempo,supervisor,contactosupervisor) VALUES({$idRecolector}, '{$empresa}', '{$cargo}', '{$funciones}', {$tiempo}, '{$supervisor}', '{$contactoSupervisor}')";
	actualizarRegistro($sql);
}

function consultarTrabajosRecolector($idRecolector)
{
	$sql = "SELECT recolectoresoferta.idoferta, fincas.nombre, ofertas.fechainicio, ofertas.vacantes 
		FROM recolectoresoferta JOIN ofertas ON recolectoresoferta.idoferta = ofertas.id
		JOIN fincas ON ofertas.idfinca = fincas.id
		WHERE recolectoresoferta.idrecolector = {$idRecolector}
		AND recolectoresoferta.idestado = 1";
	leerRegistro($sql);
}

function consultarExperienciasRecolector($idRecolector)
{
	$sql = "SELECT empresa,cargo,funciones,tiempo, supervisor, contactosupervisor FROM experiencias WHERE idrecolector = {$idRecolector}";
	leerRegistro($sql);
}

function consultarListadoPesadasModuloRecolector($idOferta, $idRecolector)
{
	$sql = "SELECT pesadas.fecha, pesadas.kilos, ofertas.valorpago as valorkilo, (pesadas.kilos 	* ofertas.valorpago ) as valorpesada 
			FROM pesadas JOIN ofertas ON pesadas.idoferta = ofertas.id 
			WHERE pesadas.idoferta = {$idOferta} AND pesadas.idrecolector = $idRecolector";
	leerRegistro($sql);
}

?>