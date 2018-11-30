-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 30-11-2018 a las 04:40:23
-- Versión del servidor: 10.1.36-MariaDB
-- Versión de PHP: 7.2.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `dbs_micafe`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_desplegarmensaje` (IN `v_mensaje` VARCHAR(100))  begin 
	select v_mensaje as mensaje;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insertarusuario` (IN `v_nombre` VARCHAR(200), IN `v_correo` VARCHAR(100), IN `v_contrasenia` VARCHAR(100), IN `v_tipodocumento` VARCHAR(20), IN `v_cedula` VARCHAR(30), IN `v_celular` VARCHAR(30), IN `v_fechanacimiento` DATE, IN `v_direccion` VARCHAR(200), IN `v_departamento` VARCHAR(100), IN `v_municipio` VARCHAR(100), IN `v_idrol` INT)  begin 
	INSERT INTO usuarios(nombre,correo,contrasenia,tipodocumento,cedula,celular,fechanacimiento,direccion,departamento,municipio,idrol) values (v_nombre, v_correo, v_contrasenia , v_tipodocumento , v_cedula , v_celular , v_fechanacimiento , v_direccion , v_departamento , v_municipio , v_idrol);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_validarinsercionusuario` (IN `v_nombre` VARCHAR(200), IN `v_correo` VARCHAR(100), IN `v_contrasenia` VARCHAR(100), IN `v_tipodocumento` VARCHAR(20), IN `v_cedula` VARCHAR(30), IN `v_celular` VARCHAR(30), IN `v_fechanacimiento` DATE, IN `v_direccion` VARCHAR(200), IN `v_departamento` VARCHAR(100), IN `v_municipio` VARCHAR(100), IN `v_idrol` INT)  begin 
declare res boolean;
	call sp_validarusuario(v_cedula, res );  
    IF res = true THEN
    	call sp_insertarusuario(v_nombre, v_correo, v_contrasenia, v_tipodocumento, v_cedula, v_celular, v_fechanacimiento, v_direccion, v_departamento, v_municipio, v_idrol);
       	call sp_desplegarmensaje("Registro completo");
    ELSE 
    	call sp_desplegarmensaje("La cedula ya se encuentra registrada. No se logro registrar el usuario.");
    end if;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_validarusuario` (IN `v_cedula` VARCHAR(30), OUT `v_res` BOOLEAN)  BEGIN
	SELECT @existe := count(usuarios.cedula) as existen from usuarios where usuarios.cedula like v_cedula;
    
    IF @existe = 0 THEN
    	set v_res = true;
    ELSE
    	set v_res = false;
    END IF;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comentarios`
--

CREATE TABLE `comentarios` (
  `id` int(11) NOT NULL,
  `idadministrador` int(11) NOT NULL,
  `idrecolector` int(11) NOT NULL,
  `comentario` varchar(400) NOT NULL,
  `calificacion` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `comentarios`
--

INSERT INTO `comentarios` (`id`, `idadministrador`, `idrecolector`, `comentario`, `calificacion`) VALUES
(1, 30, 35, 'Buen recolector', 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `costos`
--

CREATE TABLE `costos` (
  `id` int(11) NOT NULL,
  `idoferta` int(11) NOT NULL,
  `titulo` varchar(100) NOT NULL,
  `descripcion` varchar(400) DEFAULT NULL,
  `valor` int(11) NOT NULL,
  `fecha` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `costos`
--

INSERT INTO `costos` (`id`, `idoferta`, `titulo`, `descripcion`, `valor`, `fecha`) VALUES
(1, 8, 'TRANSPORTE', 'TRANSPORTE A REUNION DE VENTA', 5000, '2018-11-29 11:54:32'),
(2, 8, 'transporte', 'transporte de cafe', 5000, '2018-11-29 18:46:23');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estados`
--

CREATE TABLE `estados` (
  `id` int(11) NOT NULL,
  `estado` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `estados`
--

INSERT INTO `estados` (`id`, `estado`) VALUES
(1, 'Habilitado'),
(2, 'No Habilitado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estadositio`
--

CREATE TABLE `estadositio` (
  `id` int(11) NOT NULL,
  `estado` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `experiencias`
--

CREATE TABLE `experiencias` (
  `id` int(11) NOT NULL,
  `idrecolector` int(11) NOT NULL,
  `empresa` varchar(100) DEFAULT NULL,
  `cargo` varchar(100) DEFAULT NULL,
  `funciones` varchar(400) NOT NULL,
  `tiempo` int(11) NOT NULL,
  `supervisor` varchar(100) DEFAULT NULL,
  `contactosupervisor` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `experiencias`
--

INSERT INTO `experiencias` (`id`, `idrecolector`, `empresa`, `cargo`, `funciones`, `tiempo`, `supervisor`, `contactosupervisor`) VALUES
(1, 35, 'Finca la esperanza', 'recolector de cafe', 'Recolecta el cafe durante el tiempo establecido', 2, 'Marco Perez', '3224567890'),
(2, 37, 'finca el rosario', 'recolector', 'recoger el cafeto', 3, 'Armando Casas', '3110987654'),
(3, 40, 'finca la rama', 'recolectoe', 'recolectar el cafe ', 3, 'Maria Toro', '3214586790'),
(4, 41, 'finca la esperanza', 'recolectora', 'recolectar cafe', 24, 'Jhon Diaz', '321548709');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `fincas`
--

CREATE TABLE `fincas` (
  `id` int(11) NOT NULL,
  `idadministrador` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `departamento` varchar(50) NOT NULL,
  `municipio` varchar(50) NOT NULL,
  `corregimiento` varchar(50) NOT NULL,
  `vereda` varchar(50) NOT NULL,
  `hectareas` int(11) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `latitud` varchar(50) DEFAULT NULL,
  `longitud` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `fincas`
--

INSERT INTO `fincas` (`id`, `idadministrador`, `nombre`, `departamento`, `municipio`, `corregimiento`, `vereda`, `hectareas`, `telefono`, `latitud`, `longitud`) VALUES
(1, 30, 'LEVA', 'TOL', 'IBA', '8', 'PASTALES', 5, '123', NULL, NULL),
(2, 30, 'El camaron', 'Cundinamarca', 'Salgar', '5', 'Las cruces', 300, '3223306218', NULL, NULL),
(3, 30, 'El suspiro', 'amazonas', 'leticia', '59', 'la anaconda', 600, '20394021', NULL, NULL),
(4, 33, 'modelia', 'tolima', 'ibague', '1', 'salado', 50, '23154678', NULL, NULL),
(5, 36, 'las palmas', 'tolima', 'ibague', 'bilbao', 'el jazmin', 3, '3224273427', NULL, NULL),
(6, 42, 'las margaritas', 'Huila', 'pitalito', 'pitufo', 'el carnen', 5, '3224273458', NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `modospago`
--

CREATE TABLE `modospago` (
  `id` int(11) NOT NULL,
  `modo` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `modospago`
--

INSERT INTO `modospago` (`id`, `modo`) VALUES
(1, 'Por Kilos'),
(2, 'Jornal');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ofertas`
--

CREATE TABLE `ofertas` (
  `id` int(11) NOT NULL,
  `idfinca` int(11) NOT NULL,
  `idmodopago` int(11) NOT NULL,
  `valorpago` int(11) NOT NULL,
  `vacantes` int(11) NOT NULL,
  `diastrabajo` int(11) NOT NULL,
  `planta` varchar(600) DEFAULT NULL,
  `servicios` varchar(600) DEFAULT NULL,
  `ventatotal` int(11) DEFAULT NULL,
  `fechacreacion` date NOT NULL,
  `fechainicio` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `ofertas`
--

INSERT INTO `ofertas` (`id`, `idfinca`, `idmodopago`, `valorpago`, `vacantes`, `diastrabajo`, `planta`, `servicios`, `ventatotal`, `fechacreacion`, `fechainicio`) VALUES
(1, 2, 2, 800, 20, 18, 'alta buena con hojas', 'comida dormida tv zonas de juego', NULL, '2018-11-14', '2018-11-21'),
(2, 3, 1, 800, 20, 18, 'alta buena con hojas', 'comida dormida tv zonas de juego', NULL, '2018-11-14', '2018-11-22'),
(3, 4, 2, 500, 50, 90, 'alta', 'juete', NULL, '2018-11-14', '2018-11-18'),
(4, 1, 2, 30000, 35, 20, 'seca y alta', 'comida', NULL, '2018-11-15', '2018-11-25'),
(7, 1, 1, 100, 100, 100, 'alta', 'luz', NULL, '2018-11-17', '2018-11-19'),
(8, 1, 1, 2000, 200, 200, 'alta y bonita', 'luz agua telefono internet', NULL, '2018-11-17', '2018-10-30'),
(9, 1, 1, 500, 50, 10, 'es una planta alta ', 'alojamiento, comida, espacios de recreacion', NULL, '2018-11-18', '2018-11-01'),
(10, 5, 1, 400, 10, 15, 'cultivo nuevo', 'Alimentacion \nHospedaje', NULL, '2018-11-20', '2018-10-26'),
(11, 6, 1, 400, 10, 10, 'cultivo nuevo', 'hospedaje\nalimentacion', NULL, '2018-11-28', '2018-11-03');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pesadas`
--

CREATE TABLE `pesadas` (
  `id` int(11) NOT NULL,
  `kilos` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `idrecolector` int(11) NOT NULL,
  `idoferta` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `pesadas`
--

INSERT INTO `pesadas` (`id`, `kilos`, `fecha`, `idrecolector`, `idoferta`) VALUES
(17, 50, '2018-11-28 19:25:37', 40, 11),
(18, 80, '2018-11-28 19:25:42', 40, 11),
(19, 45, '2018-11-28 19:31:29', 35, 11),
(20, 80, '2018-11-28 19:31:32', 35, 11),
(21, 40, '2018-11-28 20:30:36', 35, 8),
(22, 100, '2018-11-28 20:58:46', 35, 9),
(23, 30, '2018-11-28 21:16:15', 35, 8),
(24, 50, '2018-11-28 21:21:54', 40, 8),
(25, 60, '2018-11-28 21:22:10', 41, 8),
(26, 100, '2018-11-28 21:44:28', 37, 11),
(27, 55, '2018-11-29 09:58:03', 40, 8),
(28, 45, '2018-11-29 09:58:18', 41, 8),
(29, 47, '2018-11-29 10:05:27', 41, 8),
(30, 70, '2018-11-29 10:06:39', 41, 8),
(31, 202, '2018-11-29 14:35:13', 40, 8),
(32, 50, '2018-11-29 18:50:28', 35, 3),
(33, 25, '2018-11-29 19:02:34', 35, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recolectoresoferta`
--

CREATE TABLE `recolectoresoferta` (
  `idoferta` int(11) NOT NULL,
  `idrecolector` int(11) NOT NULL,
  `idestado` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;

--
-- Volcado de datos para la tabla `recolectoresoferta`
--

INSERT INTO `recolectoresoferta` (`idoferta`, `idrecolector`, `idestado`) VALUES
(3, 35, 1),
(3, 41, 1),
(8, 35, 1),
(8, 39, 1),
(8, 40, 1),
(8, 41, 1),
(9, 35, 1),
(10, 35, 1),
(10, 38, 1),
(11, 35, 1),
(11, 37, 1),
(11, 39, 1),
(11, 40, 1),
(11, 41, 1),
(1, 35, 2),
(1, 37, 2),
(1, 39, 2),
(1, 40, 2),
(2, 35, 2),
(2, 38, 2),
(2, 39, 2),
(2, 40, 2),
(3, 37, 2),
(3, 38, 2),
(3, 39, 2),
(3, 40, 2),
(4, 35, 2),
(4, 37, 2),
(4, 38, 2),
(4, 39, 2),
(4, 40, 2),
(7, 35, 2),
(7, 38, 2),
(7, 39, 2),
(7, 40, 2),
(7, 41, 2),
(8, 37, 2),
(8, 38, 2),
(9, 38, 2),
(9, 39, 2),
(9, 40, 2),
(10, 37, 2),
(10, 39, 2),
(10, 40, 2),
(11, 38, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `rol` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id`, `rol`) VALUES
(1, 'superadministrador'),
(2, 'Caficultor'),
(3, 'Recolector'),
(4, 'Comerciante');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sitioscompra`
--

CREATE TABLE `sitioscompra` (
  `id` int(11) NOT NULL,
  `idduenio` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `direccion` varchar(200) NOT NULL,
  `departamento` varchar(100) NOT NULL,
  `municipio` varchar(100) NOT NULL,
  `preciocompra` int(11) DEFAULT NULL,
  `descripcioncompra` varchar(300) DEFAULT NULL,
  `cantidadcompra` varchar(100) DEFAULT NULL,
  `horario` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `latitud` varchar(30) DEFAULT NULL,
  `longitud` varchar(30) DEFAULT NULL,
  `idestado` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) NOT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `contrasenia` varchar(100) NOT NULL,
  `tipodocumento` varchar(20) NOT NULL,
  `cedula` varchar(30) CHARACTER SET utf8 COLLATE utf8_spanish2_ci NOT NULL,
  `celular` varchar(30) NOT NULL,
  `fechanacimiento` date DEFAULT NULL,
  `direccion` varchar(200) NOT NULL,
  `departamento` varchar(100) NOT NULL,
  `municipio` varchar(100) CHARACTER SET utf8 COLLATE utf8_spanish_ci NOT NULL,
  `urlimagen` varchar(100) DEFAULT NULL,
  `idrol` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `correo`, `contrasenia`, `tipodocumento`, `cedula`, `celular`, `fechanacimiento`, `direccion`, `departamento`, `municipio`, `urlimagen`, `idrol`) VALUES
(30, 'HAROLD CUPITRA HERNANDEZ', 'harold@mail.com', '123456', 'Cedula Extranjeria', '30341076', '3222136657', '2018-11-11', 'MZN C CASA 12 VILLA LUZ', 'TOLIMA', 'IBAGUE', 'imagenes/30341076.jpg', 2),
(32, 'HERNANDO LOSADA RIVERA', 'hernandolosada94@gmail.com', '1110553310', 'Cedula Ciudadania', '1110553310', '3143219063', '2018-11-11', 'MZ A CS 8 B PARRALES', 'TOLIMA', 'IBAGUE', NULL, 2),
(33, 'ANDRES ARTURO BERMUDEZ PARRA', 'arthue.272aab@gmail.com', '123456', 'Cedula Ciudadania', '1110517304', '3216549870', '2018-11-13', 'MODELIA CITY', 'TOLIMA', 'IBAGUETO', 'imagenes/1110517304.jpg', 2),
(35, 'ANDRES FELIPE CUPITRA', 'mompi@gmail.com', '123456', 'Cedula Ciudadania', '1110459107', '3222134567', '1970-02-08', 'C 13 VILLA LUZ', 'TOLIMA', 'IBAGUE', 'imagenes/1110459107.jpg', 3),
(36, 'JHON ALEXANDER DIAZ QUINTERO', 'dijhonki@gmail.com', '123456', 'Cedula Ciudadania', '1110486758', '3224273427', '1989-11-27', 'ARBOLEDA DEL CAMPESTRE SAMAN', 'TOLIMA', 'IBAGUE', 'imagenes/1110486758.jpg', 2),
(37, 'CASIMIRO', '', '0123456', 'Cedula Ciudadania', '1110517305', '3177031045', '1990-11-23', 'CALLE FALSA 123', 'PA SABER', 'JUM', 'imagenes/1110517305.jpg', 3),
(38, 'ARMANDO CASAS', 'acasas@gmail.com', '123456', 'Cedula Ciudadania', '1110459108', '3222136657', '1955-11-23', 'MZN C CASAS 12 SIMON BOLIVAR', 'TOLIMA', 'IBAGUE', 'imagenes/1110459108.jpg', 3),
(39, 'JOSE ALVARO CORREA', 'jcorrea@hotmail.com', '123456', 'Cedula Ciudadania', '5831389', '3215469807', '1970-11-24', 'FINCA LA ALCANCIA CORREGIMIENTO 8 VEREDA PASTALES', 'TOLIMA', 'IBAGUE', 'imagenes/5831389.jpg', 3),
(40, 'CAMILA DIAZ', 'cdiaz@micafe.com', '123456', 'Cedula Ciudadania', '30341077', '3225876943', '1999-11-27', 'MANZA B CASA 56 VILLA MARINA', 'TOLIMA', 'IBAGUE', 'imagenes/30341077.jpg', 3),
(41, 'OLGA MANZANARES', 'oman@micafe.com', '123456', 'Cedula Ciudadania', '123456789', '32154679804', '1970-11-27', 'CASA 3 BARRIO TRA', 'TOLIMA', 'IBAGUE', 'imagenes/123456789.jpg', 3),
(42, 'KAREN DIAZ ', 'karen@gmail.com', '123456', 'Cedula Ciudadania', '1110551914', '3224273423', '1993-10-02', 'CRA 33 N 24-', 'TOLIMA', 'IBAGUE', 'imagenes/1110551914.jpg', 2);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `comentarios`
--
ALTER TABLE `comentarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idadministrador` (`idadministrador`),
  ADD KEY `idrecolector` (`idrecolector`);

--
-- Indices de la tabla `costos`
--
ALTER TABLE `costos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idoferta` (`idoferta`);

--
-- Indices de la tabla `estados`
--
ALTER TABLE `estados`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `estadositio`
--
ALTER TABLE `estadositio`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `experiencias`
--
ALTER TABLE `experiencias`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idrecolector` (`idrecolector`);

--
-- Indices de la tabla `fincas`
--
ALTER TABLE `fincas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idadministrador` (`idadministrador`);

--
-- Indices de la tabla `modospago`
--
ALTER TABLE `modospago`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `ofertas`
--
ALTER TABLE `ofertas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idfinca` (`idfinca`),
  ADD KEY `idmodopago` (`idmodopago`);

--
-- Indices de la tabla `pesadas`
--
ALTER TABLE `pesadas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idrecolector` (`idrecolector`),
  ADD KEY `idoferta` (`idoferta`);

--
-- Indices de la tabla `recolectoresoferta`
--
ALTER TABLE `recolectoresoferta`
  ADD PRIMARY KEY (`idoferta`,`idrecolector`),
  ADD KEY `idrecolector` (`idrecolector`),
  ADD KEY `idestado` (`idestado`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `sitioscompra`
--
ALTER TABLE `sitioscompra`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idduenio` (`idduenio`),
  ADD KEY `idestado` (`idestado`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idrol` (`idrol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `comentarios`
--
ALTER TABLE `comentarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `costos`
--
ALTER TABLE `costos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `estados`
--
ALTER TABLE `estados`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `estadositio`
--
ALTER TABLE `estadositio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `experiencias`
--
ALTER TABLE `experiencias`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `fincas`
--
ALTER TABLE `fincas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `modospago`
--
ALTER TABLE `modospago`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `ofertas`
--
ALTER TABLE `ofertas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `pesadas`
--
ALTER TABLE `pesadas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `sitioscompra`
--
ALTER TABLE `sitioscompra`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `comentarios`
--
ALTER TABLE `comentarios`
  ADD CONSTRAINT `comentarios_ibfk_1` FOREIGN KEY (`idadministrador`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `comentarios_ibfk_2` FOREIGN KEY (`idrecolector`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `costos`
--
ALTER TABLE `costos`
  ADD CONSTRAINT `costos_ibfk_1` FOREIGN KEY (`idoferta`) REFERENCES `ofertas` (`id`);

--
-- Filtros para la tabla `experiencias`
--
ALTER TABLE `experiencias`
  ADD CONSTRAINT `experiencias_ibfk_1` FOREIGN KEY (`idrecolector`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `fincas`
--
ALTER TABLE `fincas`
  ADD CONSTRAINT `fincas_ibfk_1` FOREIGN KEY (`idadministrador`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `ofertas`
--
ALTER TABLE `ofertas`
  ADD CONSTRAINT `ofertas_ibfk_1` FOREIGN KEY (`idfinca`) REFERENCES `fincas` (`id`),
  ADD CONSTRAINT `ofertas_ibfk_2` FOREIGN KEY (`idmodopago`) REFERENCES `modospago` (`id`);

--
-- Filtros para la tabla `pesadas`
--
ALTER TABLE `pesadas`
  ADD CONSTRAINT `pesadas_ibfk_1` FOREIGN KEY (`idrecolector`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `pesadas_ibfk_2` FOREIGN KEY (`idoferta`) REFERENCES `ofertas` (`id`);

--
-- Filtros para la tabla `recolectoresoferta`
--
ALTER TABLE `recolectoresoferta`
  ADD CONSTRAINT `recolectoresoferta_ibfk_1` FOREIGN KEY (`idoferta`) REFERENCES `ofertas` (`id`),
  ADD CONSTRAINT `recolectoresoferta_ibfk_2` FOREIGN KEY (`idrecolector`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `recolectoresoferta_ibfk_3` FOREIGN KEY (`idestado`) REFERENCES `estados` (`id`);

--
-- Filtros para la tabla `sitioscompra`
--
ALTER TABLE `sitioscompra`
  ADD CONSTRAINT `sitioscompra_ibfk_1` FOREIGN KEY (`idduenio`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `sitioscompra_ibfk_2` FOREIGN KEY (`idestado`) REFERENCES `estadositio` (`id`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`idrol`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
