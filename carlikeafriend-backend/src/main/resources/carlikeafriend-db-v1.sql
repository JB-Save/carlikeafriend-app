-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 14-01-2026 a las 18:03:44
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `carlikeafriend-db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `category`
--

CREATE TABLE `category` (
  `id` bigint(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `category_image_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `category`
--

INSERT INTO `category` (`id`, `description`, `name`, `version`, `category_image_id`) VALUES
(1, 'Vehículo Compactos', 'Compactos', 6, 19),
(3, 'Vehículo Suvs  ', 'Suvs', 1, 13),
(4, 'Vehículo Deportivos', 'Deportivos', 0, 4),
(6, 'Vehículo Furgonetas', 'Furgonetas', 0, 6),
(7, 'Vehículo Lujo', 'Lujo', 0, 7);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `category_image`
--

CREATE TABLE `category_image` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `original_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `category_image`
--

INSERT INTO `category_image` (`id`, `content_type`, `image_path`, `original_name`) VALUES
(4, 'image/jpeg', '/image/category_folder/7eaa612e-8f99-4f0c-a495-246f5742a00a_focus2.jpg', 'focus2.jpg'),
(6, 'image/jpeg', '/image/category_folder/8db42317-8638-4a53-a874-f59913025e8f_focus2.jpg', 'focus2.jpg'),
(7, 'image/jpeg', '/image/category_folder/e846772b-722b-4b22-bbc7-288522a81dfc_focus2.jpg', 'focus2.jpg'),
(13, 'image/jpeg', '/image/category_folder/401f2de7-7756-4a5c-905f-f1c7923d0c8d_focus1.jpg', 'focus1.jpg'),
(19, 'image/jpeg', '/image/category_folder/9d7b6d98-af26-452d-a10d-00a134d0b50b_focus2.jpg', 'focus2.jpg');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `feature`
--

CREATE TABLE `feature` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `feature_icon_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `feature`
--

INSERT INTO `feature` (`id`, `name`, `version`, `feature_icon_id`) VALUES
(5, 'Aire Acondicionado', 11, 31),
(6, 'Vidrios eléctricos', 2, 24),
(7, 'Caja mecánica', 1, 14);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `icon`
--

CREATE TABLE `icon` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `original_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `icon`
--

INSERT INTO `icon` (`id`, `content_type`, `image_path`, `original_name`) VALUES
(14, 'image/png', '/image/feature_folder/bea15b52-ad6a-4899-a2d7-2f0a82edaf77_icons8-caja-de-cambios-32.png', 'icons8-caja-de-cambios-32.png'),
(24, 'image/png', '/image/feature_folder/436928d3-d255-4305-964f-538314debf2e_icons8-puerta-de-automóvil-48.png', 'icons8-puerta-de-automóvil-48.png'),
(31, 'image/png', '/image/feature_folder/7143302d-48cb-43ff-aa8e-25ddf4143d20_icons8-refrigeración-48.png', 'icons8-refrigeración-48.png');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `image`
--

CREATE TABLE `image` (
  `id` bigint(20) NOT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `original_name` varchar(255) DEFAULT NULL,
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `image`
--

INSERT INTO `image` (`id`, `content_type`, `image_path`, `original_name`, `product_id`) VALUES
(44, 'image/png', '/image/product_folder/9ddca851-4c68-43d3-b1da-9e62da5417da_Stepway_BackSide_Open.png', 'Stepway_BackSide_Open.png', 20),
(45, 'image/png', '/image/product_folder/56da5db2-08be-41dd-a59b-b7247104a4cc_Stepway_LeftSide_Open.png', 'Stepway_LeftSide_Open.png', 20),
(46, 'image/png', '/image/product_folder/6c84df03-8342-4c42-bcf7-d4343f6677cb_Stepway_LeftSide.png', 'Stepway_LeftSide.png', 20),
(47, 'image/png', '/image/product_folder/d1805913-63ea-48d7-9c4d-c550c1a0af0e_Stepway_RightSide.png', 'Stepway_RightSide.png', 20),
(49, 'image/png', '/image/product_folder/85b8b614-8ed1-4ad6-b95e-c0638aa4af72_Stepway_BackSide_Open.png', 'Stepway_BackSide_Open.png', 21),
(50, 'image/png', '/image/product_folder/4e5dd15f-dc9a-430f-ab74-46a20c7557c5_Stepway_LeftSide_Open.png', 'Stepway_LeftSide_Open.png', 21),
(51, 'image/png', '/image/product_folder/8fe92264-a9c3-4265-aeb6-5f79f9de522d_Stepway_LeftSide.png', 'Stepway_LeftSide.png', 21),
(52, 'image/png', '/image/product_folder/a0093a28-8bf1-49f1-ad56-167cc9fcd0d8_Stepway_RightSide.png', 'Stepway_RightSide.png', 21),
(53, 'image/png', '/image/product_folder/4973fc12-d3f5-4000-96b0-f2f1d4c27fc0_Stepway_FrontSide.png', 'Stepway_FrontSide.png', 21),
(65, 'image/png', '/image/product_folder/1b34273a-97e9-46e4-a267-59f2fadda77a_Stepway_RightSide.png', 'Stepway_RightSide.png', 20),
(66, 'image/png', '/image/product_folder/d086fd99-13f2-4518-8466-95d713cbc146_Stepway_LeftSide.png', 'Stepway_LeftSide.png', 10),
(86, 'image/png', '/image/product_folder/3152819a-573f-4745-b6c8-bfa8641b71de_Stepway_BackSide_Open.png', 'Stepway_BackSide_Open.png', 10),
(91, 'image/png', '/image/product_folder/1be4196f-f7ee-4507-9c16-1f718e547d2e_Stepway_LeftSide_Open.png', 'Stepway_LeftSide_Open.png', 11),
(92, 'image/png', '/image/product_folder/1b73dce0-83aa-49ce-ba49-bdfeb1f1d9fd_Stepway_RightSide.png', 'Stepway_RightSide.png', 12);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permission`
--

CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `permission`
--

INSERT INTO `permission` (`id`, `name`, `version`, `description`) VALUES
(1, 'READ_PRODUCT', 1, 'Ver Producto.'),
(2, 'CREATE_PRODUCT', 1, 'Crear un nuevo producto'),
(3, 'UPDATE_PRODUCT', 0, 'Actualizar producto existente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `product`
--

CREATE TABLE `product` (
  `id` bigint(20) NOT NULL,
  `description` text DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `version` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `product`
--

INSERT INTO `product` (`id`, `description`, `name`, `price`, `version`) VALUES
(10, 'Descripción Carro15', 'Carro15', 100000, 6),
(11, 'Descripción Carro14', 'Carro4', 150000, 5),
(12, 'Descripción Carro5', 'Carro5', 300000, 5),
(20, 'Descripción Carro16', 'Carro16', 150100, 5),
(21, 'Descripción Carro17', 'Carro17', 250000, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `product_category`
--

CREATE TABLE `product_category` (
  `category_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `product_category`
--

INSERT INTO `product_category` (`category_id`, `product_id`) VALUES
(1, 12),
(3, 12),
(6, 11),
(3, 20),
(1, 21),
(7, 10);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `product_feature`
--

CREATE TABLE `product_feature` (
  `product_id` bigint(20) NOT NULL,
  `feature_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `product_feature`
--

INSERT INTO `product_feature` (`product_id`, `feature_id`) VALUES
(10, 5),
(10, 6),
(10, 7),
(20, 5),
(20, 7),
(21, 5),
(21, 6),
(21, 7),
(11, 5),
(11, 7),
(12, 5),
(12, 6),
(12, 7);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `role`
--

CREATE TABLE `role` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `role`
--

INSERT INTO `role` (`id`, `name`, `version`, `description`) VALUES
(1, 'ADMIN', 8, 'Usuario Administrador.'),
(4, 'USER', 1, 'Usuario Usuario');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `role_permission`
--

CREATE TABLE `role_permission` (
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `role_permission`
--

INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
(4, 1),
(1, 1),
(1, 2),
(1, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user`
--

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_account_non_expired` bit(1) NOT NULL,
  `is_account_non_locked` bit(1) NOT NULL,
  `is_credentials_non_expired` bit(1) NOT NULL,
  `is_enabled` bit(1) NOT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `user`
--

INSERT INTO `user` (`id`, `email`, `is_account_non_expired`, `is_account_non_locked`, `is_credentials_non_expired`, `is_enabled`, `last_name`, `name`, `password`) VALUES
(3, 'jorge.vargas@ejemplo.com', b'1', b'1', b'1', b'1', 'Vargas', 'Jorge Armando', '$2a$10$Gz4.KNR0N6iQx1oURsVNsOYlc9FBFyumLbbGVvgNNUt3gML6GrOAW'),
(4, 'juan.velez@ejemplo.com', b'1', b'1', b'1', b'1', 'Velez Perez', 'Juan Fernando', '$2a$10$fycYBKOOwQm.pnubu2KR6.BTGL3PrUs9ztLQfBxeIXQuKKSc6fuIW'),
(5, 'maria.gomez@ejemplo.com', b'1', b'1', b'1', b'1', 'Gomez Berrio', 'Maria Eugenia', '$2a$10$AOU79y1z.zjQK5Bte1lFce326VMHPw/1hpn1HFwg4QQRd6SMmKdXW'),
(6, 'lina.betancour@ejemplo.com', b'1', b'1', b'1', b'1', 'Betancour Gonzalez', 'Lina Maria', '$2a$10$AaLQiG5RudZsKs2xCEQqSuHpe2F4L6IGraqL1oPMSxdEBaN4QrlKm'),
(8, 'juan.restrepo@ejemplo.com', b'1', b'1', b'1', b'1', 'Restrepo Úsuga', 'Juan Camilo', '$2a$10$w.vocA1AoEBKzBowVMaURec4hTv3zydTcdQCkB0Du.s5Gwe2aXOqK'),
(10, 'lina.betancour@dominio.com', b'1', b'1', b'1', b'1', 'Betancour Gonzalez', 'Lina Maria.', '$2a$10$3vMFWaOUKDCOe3FEOmNoe.oO9uyPy8ahGYb0J36QdaDOgKgP0WSbu'),
(12, 'bertha.sanchez@dominio.com', b'1', b'1', b'1', b'1', 'Sanchez', 'Bertha', '$2a$10$4v0Mx9f.6VVz0XvTF1qJLeIVBl/ennYPxtTnb07yfBW8eP8YDgMVm'),
(37, 'jose@dominio.com', b'1', b'1', b'1', b'1', 'Velez Velez', 'Jose', '$2a$10$SX.7JncyPz.QXdWQsKSgzurHRpztzo0l7Ygx.MB.zkKvuEgp0890e');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user_role`
--

CREATE TABLE `user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `user_role`
--

INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
(6, 4),
(8, 4),
(10, 4),
(5, 4),
(12, 4),
(3, 1),
(4, 1),
(37, 4);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK46ccwnsi9409t36lurvtyljak` (`name`),
  ADD UNIQUE KEY `UKjix5vj27d3bxv0npm70abp1vv` (`category_image_id`);

--
-- Indices de la tabla `category_image`
--
ALTER TABLE `category_image`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `feature`
--
ALTER TABLE `feature`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKjhueeftkn8ve8th8m8a2878dr` (`name`),
  ADD UNIQUE KEY `UKhecknf8lleb5frrn73xohti6j` (`feature_icon_id`);

--
-- Indices de la tabla `icon`
--
ALTER TABLE `icon`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `image`
--
ALTER TABLE `image`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKgpextbyee3uk9u6o2381m7ft1` (`product_id`);

--
-- Indices de la tabla `permission`
--
ALTER TABLE `permission`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2ojme20jpga3r4r79tdso17gi` (`name`);

--
-- Indices de la tabla `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKjmivyxk9rmgysrmsqw15lqr5b` (`name`);

--
-- Indices de la tabla `product_category`
--
ALTER TABLE `product_category`
  ADD KEY `FK2k3smhbruedlcrvu6clued06x` (`product_id`),
  ADD KEY `FKkud35ls1d40wpjb5htpp14q4e` (`category_id`);

--
-- Indices de la tabla `product_feature`
--
ALTER TABLE `product_feature`
  ADD KEY `FKgv1xq970xwg0q5k3jn9i23cc1` (`feature_id`),
  ADD KEY `FKp5iv62sge9f7yw66e5w2i2rhx` (`product_id`);

--
-- Indices de la tabla `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK8sewwnpamngi6b1dwaa88askk` (`name`);

--
-- Indices de la tabla `role_permission`
--
ALTER TABLE `role_permission`
  ADD KEY `FKf8yllw1ecvwqy3ehyxawqa1qp` (`permission_id`),
  ADD KEY `FKa6jx8n8xkesmjmv6jqug6bg68` (`role_id`);

--
-- Indices de la tabla `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`);

--
-- Indices de la tabla `user_role`
--
ALTER TABLE `user_role`
  ADD KEY `FKa68196081fvovjhkek5m97n3y` (`role_id`),
  ADD KEY `FK859n2jvi8ivhui0rl0esws6o` (`user_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `category`
--
ALTER TABLE `category`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `category_image`
--
ALTER TABLE `category_image`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de la tabla `feature`
--
ALTER TABLE `feature`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `icon`
--
ALTER TABLE `icon`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT de la tabla `image`
--
ALTER TABLE `image`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=123;

--
-- AUTO_INCREMENT de la tabla `permission`
--
ALTER TABLE `permission`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT de la tabla `product`
--
ALTER TABLE `product`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT de la tabla `role`
--
ALTER TABLE `role`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `category`
--
ALTER TABLE `category`
  ADD CONSTRAINT `FKs9ssgru44s0cuitghuhivbdtm` FOREIGN KEY (`category_image_id`) REFERENCES `category_image` (`id`);

--
-- Filtros para la tabla `feature`
--
ALTER TABLE `feature`
  ADD CONSTRAINT `FKat9fd4invrkfn4me2jifesmyn` FOREIGN KEY (`feature_icon_id`) REFERENCES `icon` (`id`);

--
-- Filtros para la tabla `image`
--
ALTER TABLE `image`
  ADD CONSTRAINT `FKgpextbyee3uk9u6o2381m7ft1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

--
-- Filtros para la tabla `product_category`
--
ALTER TABLE `product_category`
  ADD CONSTRAINT `FK2k3smhbruedlcrvu6clued06x` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  ADD CONSTRAINT `FKkud35ls1d40wpjb5htpp14q4e` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);

--
-- Filtros para la tabla `product_feature`
--
ALTER TABLE `product_feature`
  ADD CONSTRAINT `FKgv1xq970xwg0q5k3jn9i23cc1` FOREIGN KEY (`feature_id`) REFERENCES `feature` (`id`),
  ADD CONSTRAINT `FKp5iv62sge9f7yw66e5w2i2rhx` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

--
-- Filtros para la tabla `role_permission`
--
ALTER TABLE `role_permission`
  ADD CONSTRAINT `FKa6jx8n8xkesmjmv6jqug6bg68` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  ADD CONSTRAINT `FKf8yllw1ecvwqy3ehyxawqa1qp` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`);

--
-- Filtros para la tabla `user_role`
--
ALTER TABLE `user_role`
  ADD CONSTRAINT `FK859n2jvi8ivhui0rl0esws6o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKa68196081fvovjhkek5m97n3y` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
