-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 11-11-2018 a las 00:47:58
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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estados`
--

CREATE TABLE `estados` (
  `id` int(11) NOT NULL,
  `estado` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `modospago`
--

CREATE TABLE `modospago` (
  `id` int(11) NOT NULL,
  `modo` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `planta` varchar(300) DEFAULT NULL,
  `servicios` varchar(300) DEFAULT NULL,
  `ventatotal` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recolectoresoferta`
--

CREATE TABLE `recolectoresoferta` (
  `idoferta` int(11) NOT NULL,
  `idrecolector` int(11) NOT NULL,
  `idestado` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `municipio` varchar(100) NOT NULL,
  `urlimagen` varchar(100) DEFAULT NULL,
  `idrol` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `correo`, `contrasenia`, `tipodocumento`, `cedula`, `celular`, `fechanacimiento`, `direccion`, `departamento`, `municipio`, `urlimagen`, `idrol`) VALUES
(27, 'HAROLD', 'harold@mail.com', '123456', 'Tarjeta_Identidad', '96070726065', '321', '2018-11-10', 'C12', 'TOLIMA', 'IBAGUE', NULL, 3),
(28, 'PEPE', 'hacu12@hotmail.com', '123456', 'Cedula_Ciudadania', '1110572361', '322', '2018-11-10', 'C13', 'TOL', 'IBA', NULL, 2),
(29, 'RENE DIAZ', 'rene@mail.com', '123456', 'Cedula_Extranjeria', '5831389', '3222136657', '2018-11-10', 'MANZANA C CASA 12 VILLA LUZ', 'TOLIMA', 'IBAGUE', NULL, 2);

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
  ADD KEY `idoferta` (`idoferta`),
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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `costos`
--
ALTER TABLE `costos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `estados`
--
ALTER TABLE `estados`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `estadositio`
--
ALTER TABLE `estadositio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `experiencias`
--
ALTER TABLE `experiencias`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `fincas`
--
ALTER TABLE `fincas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `modospago`
--
ALTER TABLE `modospago`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ofertas`
--
ALTER TABLE `ofertas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pesadas`
--
ALTER TABLE `pesadas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

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
