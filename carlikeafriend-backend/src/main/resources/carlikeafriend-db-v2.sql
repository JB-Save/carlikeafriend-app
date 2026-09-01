-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 01-09-2026 a las 03:02:07
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
-- Estructura de tabla para la tabla `addon`
--

CREATE TABLE `addon` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `current_price` double NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `charge_type` enum('FLAT_FEE','PER_DAY') NOT NULL,
  `max_chargeable_days` int(11) DEFAULT NULL,
  `max_quantity_per_reservation` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `addon`
--

INSERT INTO `addon` (`id`, `created_at`, `created_by`, `deleted`, `modified_at`, `modified_by`, `version`, `current_price`, `description`, `name`, `charge_type`, `max_chargeable_days`, `max_quantity_per_reservation`) VALUES
(1, '2026-04-02 10:25:44.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-07-06 15:16:38.000000', 'jorge.saavedra@ejemplo.com', 3, 5000, 'Silla para bebes.', 'Silla Para Bebe', 'PER_DAY', 15, 2),
(2, '2026-07-06 16:33:10.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-07-06 16:37:20.000000', 'jorge.saavedra@ejemplo.com', 4, 6000, 'Silla para niños', 'Silla Para Niño', 'PER_DAY', 15, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `branch`
--

CREATE TABLE `branch` (
  `id` bigint(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `latitude` decimal(10,8) NOT NULL,
  `longitude` decimal(11,8) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `city_id` bigint(20) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `branch`
--

INSERT INTO `branch` (`id`, `address`, `latitude`, `longitude`, `name`, `version`, `city_id`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`) VALUES
(1, 'Calle 29A #50-85', 6.31787575, -75.55765727, 'Branch Bello', 6, 2, '', '2026-03-11 17:35:52.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-07 20:02:06.000000'),
(2, 'Cl. 66a #55A-51', 6.26410915, -75.56928615, 'Branch Medellín', 0, 1, '', '2026-03-11 17:35:52.000000', b'0', '', '2026-03-11 17:37:08.000000'),
(5, 'Calle 36 D Sur 27ª- 105, Loma del Escobero', 6.16503545, -75.56950378, 'Branch Envigado', 0, 4, 'jorge.saavedra@ejemplo.com', '2026-03-12 16:26:19.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-03-12 16:26:19.000000');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `branch_addon`
--

CREATE TABLE `branch_addon` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `total_stock` int(11) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `addon_id` bigint(20) NOT NULL,
  `branch_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `branch_addon`
--

INSERT INTO `branch_addon` (`id`, `created_at`, `created_by`, `deleted`, `modified_at`, `modified_by`, `total_stock`, `version`, `addon_id`, `branch_id`) VALUES
(1, '2026-04-02 10:51:21.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-07-06 21:20:42.000000', 'jorge.saavedra@ejemplo.com', 20, 4, 1, 1),
(2, '2026-07-08 19:00:31.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-07-08 19:00:31.000000', 'jorge.saavedra@ejemplo.com', 20, 0, 2, 1),
(3, '2026-08-12 16:36:39.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-08-12 16:36:39.000000', 'jorge.saavedra@ejemplo.com', 20, 0, 2, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `branch_transfer_fee`
--

CREATE TABLE `branch_transfer_fee` (
  `id` bigint(20) NOT NULL,
  `fee_amount` double DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `destination_branch_id` bigint(20) NOT NULL,
  `origin_branch_id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `branch_transfer_fee`
--

INSERT INTO `branch_transfer_fee` (`id`, `fee_amount`, `version`, `destination_branch_id`, `origin_branch_id`, `created_at`, `created_by`, `deleted`, `modified_at`, `modified_by`) VALUES
(1, 50000, 0, 2, 1, '2026-03-13 10:32:54.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-03-13 10:32:54.000000', 'jorge.saavedra@ejemplo.com'),
(2, 51100, 1, 5, 1, '2026-03-13 12:37:01.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-03-13 12:48:45.000000', 'jorge.saavedra@ejemplo.com'),
(3, 50000, 0, 1, 2, '2026-03-13 12:45:16.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-03-13 12:45:16.000000', 'jorge.saavedra@ejemplo.com'),
(4, 50000, 0, 5, 2, '2026-03-13 12:45:29.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-03-13 12:45:29.000000', 'jorge.saavedra@ejemplo.com'),
(7, 50000, 0, 2, 5, '2026-07-03 19:16:03.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-07-03 19:16:03.000000', 'jorge.saavedra@ejemplo.com'),
(8, 51100, 0, 1, 5, '2026-07-03 19:16:43.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-07-03 19:16:43.000000', 'jorge.saavedra@ejemplo.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `category`
--

CREATE TABLE `category` (
  `id` bigint(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `category_image_id` bigint(20) NOT NULL,
  `base_daily_rate` double DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `base_deposit_amount` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `category`
--

INSERT INTO `category` (`id`, `description`, `name`, `version`, `category_image_id`, `base_daily_rate`, `priority`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`, `base_deposit_amount`) VALUES
(1, 'Compactos Actualizado con Put', 'Compactos', 25, 29, 127000, 51, '', '2026-03-11 17:43:11.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-07 20:50:51.000000', 908500),
(3, 'Vehículo Suvs', 'Suvs', 4, 13, 300000, 60, '', '2026-03-11 17:43:11.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-04-08 16:40:36.000000', 1453600),
(4, 'Deportivos', 'Deportivos', 4, 4, 908000, 70, '', '2026-03-11 17:43:11.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-04-08 16:42:18.000000', 4360800),
(6, 'Furgonetas', 'Furgonetas', 3, 6, 399000, 50, '', '2026-03-11 17:43:11.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-04-08 16:43:37.000000', 2362100),
(7, 'Lujo asdasdasdasda', 'Lujo', 3, 7, 1636000, 100, '', '2026-03-11 17:43:11.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-04-08 16:45:08.000000', 9085000),
(17, 'Is an Example.', 'Category Example', 3, 30, 103000, 51, 'jorge.saavedra@ejemplo.com', '2026-03-12 11:44:07.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-04-08 14:45:53.000000', 127000);

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
(29, 'image/jpeg', '/image/category_folder/4bca939e-ce08-418c-9f68-5c4b75e02fa1_focus2.jpg', 'focus2.jpg'),
(30, 'image/jpeg', '/image/category_folder/afb142b3-da2e-421c-8ee3-f973e5ab67ce_focus1.jpg', 'focus1.jpg');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `city`
--

CREATE TABLE `city` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `city`
--

INSERT INTO `city` (`id`, `name`, `version`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`) VALUES
(1, 'Medellín, Antioquia. Colombia', 2, '', '2026-03-11 17:44:18.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-07-01 16:38:04.000000'),
(2, 'Bello, Antioquia. Colombia', 0, '', '2026-03-11 17:44:18.000000', b'0', '', '2026-03-11 17:44:38.000000'),
(4, 'Envigado, Antioquia. Colombia', 0, '', '2026-03-11 17:44:18.000000', b'0', '', '2026-03-11 17:44:38.000000');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `feature`
--

CREATE TABLE `feature` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `feature_icon_id` bigint(20) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `feature`
--

INSERT INTO `feature` (`id`, `name`, `version`, `feature_icon_id`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`) VALUES
(5, 'Aire Acondicionado', 12, 41, '', '2026-03-11 17:45:15.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-05 16:21:03.000000'),
(6, 'Puerta', 3, 24, '', '2026-03-11 17:45:15.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-05 19:59:38.000000'),
(7, 'Caja mecánica', 1, 14, '', '2026-03-11 17:45:15.000000', b'0', '', '2026-03-11 17:45:31.000000'),
(16, 'Sunroof._DELETED_1775142448924', 2, 34, 'jorge.saavedra@ejemplo.com', '2026-04-02 09:50:56.000000', b'1', 'jorge.saavedra@ejemplo.com', '2026-04-02 10:07:28.000000'),
(17, 'Vidrios Eléctricos', 2, 37, 'jorge.saavedra@ejemplo.com', '2026-06-05 20:00:13.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-05 20:21:26.000000'),
(18, 'Caja Automática', 0, 38, 'jorge.saavedra@ejemplo.com', '2026-06-05 20:27:11.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-05 20:27:11.000000'),
(19, 'Equipaje', 0, 39, 'jorge.saavedra@ejemplo.com', '2026-06-05 20:30:38.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-05 20:30:38.000000'),
(20, 'Pasajero', 1, 40, 'jorge.saavedra@ejemplo.com', '2026-06-05 21:26:12.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-10 16:26:29.000000');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `financial_configuration`
--

CREATE TABLE `financial_configuration` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `basic_insurance_deposit_multiplier` double NOT NULL,
  `cancellation_penalty_rate` double NOT NULL,
  `default_transfer_fee` double NOT NULL,
  `full_coverage_deposit_multiplier` double NOT NULL,
  `insurance_basic_rate` double NOT NULL,
  `insurance_full_coverage_rate` double NOT NULL,
  `insurance_premium_rate` double NOT NULL,
  `no_show_penalty_rate` double NOT NULL,
  `premium_insurance_deposit_multiplier` double NOT NULL,
  `tax_rate` double NOT NULL,
  `max_rental_days` int(11) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `penalty_window_hours` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `financial_configuration`
--

INSERT INTO `financial_configuration` (`id`, `created_at`, `created_by`, `deleted`, `modified_at`, `modified_by`, `basic_insurance_deposit_multiplier`, `cancellation_penalty_rate`, `default_transfer_fee`, `full_coverage_deposit_multiplier`, `insurance_basic_rate`, `insurance_full_coverage_rate`, `insurance_premium_rate`, `no_show_penalty_rate`, `premium_insurance_deposit_multiplier`, `tax_rate`, `max_rental_days`, `version`, `penalty_window_hours`) VALUES
(1, '2026-04-07 13:57:58.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-08-07 23:32:36.000000', 'jorge.saavedra@ejemplo.com', 1, 0.2, 50000, 0, 10000, 45000, 25000, 1, 0.5, 0.19, 30, 3, 24);

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
(34, 'image/png', '/image/feature_folder/0d2341c7-2d7a-4b16-9fd2-ffbaa2f87963_icons8-caja-de-cambios-32.png', 'icons8-caja-de-cambios-32.png'),
(37, 'image/png', '/image/feature_folder/8005b861-54a2-4041-bbd5-8628d7483733_power_window.png', 'power_window.png'),
(38, 'image/png', '/image/feature_folder/53553cae-18c9-49d8-8f07-0a28dae57cdf_switch.png', 'switch.png'),
(39, 'image/png', '/image/feature_folder/88e4f20c-193c-45e1-8ef8-31eed67cb559_baggage.png', 'baggage.png'),
(40, 'image/png', '/image/feature_folder/a4795482-fd8b-4dfa-aabe-0b7914c6a883_passenger.png', 'passenger.png'),
(41, 'image/png', '/image/feature_folder/1a71b576-0691-4622-b2c3-f89c99292c28_icons8-refrigeración-48.png', 'icons8-refrigeración-48.png');

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
(92, 'image/png', '/image/product_folder/1b73dce0-83aa-49ce-ba49-bdfeb1f1d9fd_Stepway_RightSide.png', 'Stepway_RightSide.png', 12),
(125, 'image/png', '/image/product_folder/17c271d5-4941-4f3d-b550-b0702c885e18_Stepway_LeftSide_Open.png', 'Stepway_LeftSide_Open.png', 51),
(126, 'image/png', '/image/product_folder/2c605386-387a-4c96-abb5-fb87709047a6_Stepway_LeftSide.png', 'Stepway_LeftSide.png', 51),
(127, 'image/png', '/image/product_folder/b6e9ffa8-4b75-4961-847f-d65a1d6098a0_Stepway_RightSide.png', 'Stepway_RightSide.png', 51),
(128, 'image/png', '/image/product_folder/97da4ac5-3882-4e6c-b6f1-a549f8908ccf_Stepway_FrontSide.png', 'Stepway_FrontSide.png', 51),
(133, 'image/png', '/image/product_folder/522003b3-5e37-48ea-bd6e-5a0209b226e1_Stepway_LeftSide_Open.png', 'Stepway_LeftSide_Open.png', 53),
(134, 'image/png', '/image/product_folder/56994585-46a1-4c51-a1b6-17768662c48c_Stepway_RightSide.png', 'Stepway_RightSide.png', 54),
(137, 'image/png', '/image/product_folder/7caed6da-0c11-431a-89db-aad3cd08235c_Stepway_LeftSide_Open.png', 'Stepway_LeftSide_Open.png', 56),
(138, 'image/png', '/image/product_folder/171a7032-3f21-453a-a6b1-9e109c88b481_Stepway_LeftSide.png', 'Stepway_LeftSide.png', 56),
(139, 'image/png', '/image/product_folder/849b6691-bf61-465a-b238-0e2c8af807b5_Stepway_RightSide.png', 'Stepway_RightSide.png', 56),
(140, 'image/png', '/image/product_folder/4f9b5a52-b63f-4982-ba4a-0a65490302e2_Stepway_FrontSide.png', 'Stepway_FrontSide.png', 56),
(141, 'image/png', '/image/product_folder/5c9d0604-831b-4d71-842c-d3b0d4f62742_Stepway_LeftSide_Open.png', 'Stepway_LeftSide_Open.png', 57),
(142, 'image/png', '/image/product_folder/93155a04-3272-46d8-80f0-e62057a3cba7_Stepway_LeftSide.png', 'Stepway_LeftSide.png', 57),
(143, 'image/png', '/image/product_folder/2b118df3-27a9-45e5-9653-0dcdb084a7d9_Stepway_RightSide.png', 'Stepway_RightSide.png', 57),
(144, 'image/png', '/image/product_folder/7d491159-d019-46fc-9014-5b00b585a16f_Stepway_FrontSide.png', 'Stepway_FrontSide.png', 57),
(147, 'image/png', '/image/product_folder/498c000a-9611-4b9c-a07c-ea12cdc86f16_default-car.png', 'default-car.png', 11);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `inspection`
--

CREATE TABLE `inspection` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `damage_description` text DEFAULT NULL,
  `fuel_level` int(11) DEFAULT NULL,
  `has_damage` bit(1) DEFAULT NULL,
  `inspection_type` enum('PICKUP','RETURN') NOT NULL,
  `mileage` int(11) DEFAULT NULL,
  `inspector_id` bigint(20) NOT NULL,
  `reservation_id` binary(16) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `maintenance_log`
--

CREATE TABLE `maintenance_log` (
  `id` bigint(20) NOT NULL,
  `actual_end_date` datetime(6) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `maintenance_type_id` bigint(20) DEFAULT NULL,
  `vehicle_id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `cost` double NOT NULL,
  `maintenance_date` date NOT NULL,
  `mileage_at_maintenance` int(11) NOT NULL,
  `technician_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `maintenance_type`
--

CREATE TABLE `maintenance_type` (
  `id` bigint(20) NOT NULL,
  `code` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `maintenance_type`
--

INSERT INTO `maintenance_type` (`id`, `code`, `description`, `version`, `created_at`, `created_by`, `deleted`, `modified_at`, `modified_by`) VALUES
(1, 'MP', 'Mantenimiento Preventivo (Ciclo de Vida): Cambio de aceite, filtros, revisión de niveles cada 10,000 km. Rotación de neumáticos, alineación y balanceo para evitar desgaste irregular. Inspección y cambio preventivo de pastillas y discos de freno. Alertas generadas por el sistema de monitoreo remoto. Escaneo con computadora.', 0, '2026-04-07 18:37:36.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-04-07 18:37:36.000000', 'jorge.saavedra@ejemplo.com'),
(2, 'MC', 'Mantenimiento Correctivo (Reparaciones): Reparación inmediata por avería mecánica que dejó el coche fuera de servicio. Fallos en luces, sensores, batería o sistema de infoentretenimiento. Reparación de abolladuras, rayones o golpes tras un siniestro.', 3, '2026-04-07 18:39:41.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-04-07 19:28:23.000000', 'jorge.saavedra@ejemplo.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `make`
--

CREATE TABLE `make` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `make`
--

INSERT INTO `make` (`id`, `name`, `version`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`) VALUES
(2, 'Toyota', 2, '', '2026-03-11 17:47:00.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-05 16:46:55.000000'),
(3, 'Mazda', 2, '', '2026-03-11 17:47:00.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-30 16:19:56.000000');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permission`
--

CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `permission`
--

INSERT INTO `permission` (`id`, `name`, `version`, `description`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`) VALUES
(1, 'READ_PRODUCT', 4, 'Ver Producto.', '', '2026-03-11 18:02:23.288213', b'0', 'jorge.saavedra@ejemplo.com', '2026-07-22 23:16:01.000000'),
(2, 'CREATE_PRODUCT', 3, 'Crear un nuevo producto', '', '2026-03-11 18:02:23.288213', b'0', 'jorge.saavedra@ejemplo.com', '2026-07-22 23:16:09.000000'),
(3, 'UPDATE_PRODUCT', 0, 'Actualizar producto existente', '', '2026-03-11 18:02:23.288213', b'0', '', '2026-03-11 18:02:56.915345'),
(31, 'WRITE_PRODUCT_DELETED_1773336970511', 2, 'Modificar un nuevo producto.', 'jorge.saavedra@ejemplo.com', '2026-03-12 12:28:25.000000', b'1', 'jorge.saavedra@ejemplo.com', '2026-03-12 12:36:10.000000');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `policy`
--

CREATE TABLE `policy` (
  `id` bigint(20) NOT NULL,
  `content` mediumtext DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `policy_type_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `policy`
--

INSERT INTO `policy` (`id`, `content`, `version`, `policy_type_id`, `name`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`) VALUES
(1, 'What is Lorem Ipsum? Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.', 0, 1, 'Política # 1', '', '2026-03-11 18:04:00.372433', b'0', '', '2026-03-11 18:04:41.318683'),
(2, 'Where does it come from? Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \'de Finibus Bonorum et Malorum\' (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \'Lorem ipsum dolor sit amet..\', comes from a line in section 1.10.32. The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \'de Finibus Bonorum et Malorum\' by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.', 0, 2, 'Política # 2', '', '2026-03-11 18:04:00.372433', b'0', '', '2026-03-11 18:04:41.318683'),
(3, 'Why do we use it? It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using \'Content here, content here\', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for \'lorem ipsum\' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).', 0, 3, 'Política # 3', '', '2026-03-11 18:04:00.372433', b'0', '', '2026-03-11 18:04:41.318683'),
(4, 'Where can I get some? There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don\'t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn\'t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc.', 0, 4, 'Política # 4', '', '2026-03-11 18:04:00.372433', b'0', '', '2026-03-11 18:04:41.318683'),
(5, 'The standard Lorem Ipsum passage, used since the 1500s Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', 0, 5, 'Política # 5', '', '2026-03-11 18:04:00.372433', b'0', '', '2026-03-11 18:04:41.318683'),
(8, 'Política 0', 2, 8, 'Política # 0', 'jorge.saavedra@ejemplo.com', '2026-07-01 13:34:29.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-07-01 14:56:38.000000');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `policy_type`
--

CREATE TABLE `policy_type` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `policy_type`
--

INSERT INTO `policy_type` (`id`, `name`, `version`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`) VALUES
(1, 'REQUISITOS', 0, '', '2026-03-11 18:05:33.903470', b'0', '', '2026-03-11 18:06:03.664948'),
(2, 'GARANTÍA', 0, '', '2026-03-11 18:05:33.903470', b'0', '', '2026-03-11 18:06:03.664948'),
(3, 'COMBUSTIBLE', 0, '', '2026-03-11 18:05:33.903470', b'0', '', '2026-03-11 18:06:03.664948'),
(4, 'SEGUROS', 0, '', '2026-03-11 18:05:33.903470', b'0', '', '2026-03-11 18:06:03.664948'),
(5, 'USO', 0, '', '2026-03-11 18:05:33.903470', b'0', '', '2026-03-11 18:06:03.664948'),
(6, 'CANCELACIÓN', 2, '', '2026-03-11 18:05:33.903470', b'0', '', '2026-03-11 18:06:03.664948'),
(8, 'POLÍTICA # 0', 2, '', '2026-03-11 18:05:33.903470', b'0', 'jorge.saavedra@ejemplo.com', '2026-07-01 14:53:08.000000');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `product`
--

CREATE TABLE `product` (
  `id` bigint(20) NOT NULL,
  `description` text DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `make_id` bigint(20) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `average_rating` double DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `base_deposit_amount` double NOT NULL,
  `baggage_capacity` int(11) DEFAULT NULL,
  `passenger_capacity` int(11) DEFAULT NULL,
  `number_of_doors` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `product`
--

INSERT INTO `product` (`id`, `description`, `name`, `version`, `make_id`, `price`, `average_rating`, `total_reviews`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`, `base_deposit_amount`, `baggage_capacity`, `passenger_capacity`, `number_of_doors`) VALUES
(10, 'sdfsdfsdfsdf', 'Carro15_DELETED_1773331868826', 8, 3, NULL, 0, 0, 'jorge.saavedra@ejemplo.com', '2026-03-11 18:07:10.914344', b'1', 'jorge.saavedra@ejemplo.com', '2026-03-12 11:11:08.000000', 0, NULL, NULL, NULL),
(11, 'sdfasfasdfsdf', 'Default Car', 22, 2, 127000, 0, 0, 'jorge.saavedra@ejemplo.com', '2026-03-11 18:07:10.914344', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-08 00:21:41.000000', 908500, 2, 5, 5),
(12, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce gravida faucibus mi. Quisque non eros viverra, cursus felis at, sagittis turpis. In tristique nisi nulla, sit amet suscipit felis porta at. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aliquam leo odio, dictum a erat interdum, rhoncus rutrum neque. Proin at ornare lorem. Curabitur facilisis facilisis nisl vel commodo. Maecenas ut fringilla ligula, sed commodo sapien. Sed luctus nibh pharetra, sodales massa in, volutpat dolor. Etiam vel maximus sapien. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin ac leo non arcu accumsan pharetra. Sed odio erat, elementum rutrum nibh nec, rutrum varius mauris. Donec bibendum porta venenatis. Praesent ac urna vitae tellus elementum iaculis vel vitae neque.', 'Carro5', 28, 2, 300000, 5, 2, 'jorge.saavedra@ejemplo.com', '2026-03-11 18:07:10.914344', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-04 22:21:44.000000', 1453600, 2, 5, 5),
(20, 'carro CARRO', 'Carro16', 7, 2, 300000, 0, 0, 'jorge.saavedra@ejemplo.com', '2026-03-11 18:07:10.914344', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-09 19:37:44.000000', 1453600, 2, 4, 5),
(21, 'carro carro', 'Carro17', 18, 2, 127000, 0, 0, 'jorge.saavedra@ejemplo.com', '2026-03-11 18:07:10.914344', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-07 20:50:51.000000', 908500, 4, 6, 4),
(51, 'New Car 2026', 'NewCar', 12, 3, 127000, 4.2, 21, 'jorge.saavedra@ejemplo.com', '2026-03-11 18:07:10.914344', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-07 20:50:51.000000', 908500, 3, 5, 5),
(53, 'New Car2 2026', 'NewCar2', 4, 3, 300000, 0, 0, 'jorge.saavedra@ejemplo.com', '2026-03-11 18:07:10.914344', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-09 19:40:15.000000', 1453600, 2, 4, 5),
(54, 'New Car3 2026', 'NewCar3', 4, 3, 300000, 0, 0, 'jorge.saavedra@ejemplo.com', '2026-03-11 18:07:10.914344', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-09 19:40:29.000000', 1453600, 1, 4, 5),
(56, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce gravida faucibus mi. Quisque non eros viverra, cursus felis at, sagittis turpis. In tristique nisi nulla, sit amet suscipit felis porta at. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aliquam leo odio, dictum a erat interdum, rhoncus rutrum neque. Proin at ornare lorem. Curabitur facilisis facilisis nisl vel commodo. Maecenas ut fringilla ligula, sed commodo sapien. Sed luctus nibh pharetra, sodales massa in, volutpat dolor. Etiam vel maximus sapien. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin ac leo non arcu accumsan pharetra. Sed odio erat, elementum rutrum nibh nec, rutrum varius mauris. Donec bibendum porta venenatis. Praesent ac urna vitae tellus elementum iaculis vel vitae neque.', 'Mazda 2', 12, 3, 127000, 0, 0, 'jorge.saavedra@ejemplo.com', '2026-03-12 10:25:31.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-07 20:50:51.000000', 908500, 2, 5, 5),
(57, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce gravida faucibus mi. Quisque non eros viverra, cursus felis at, sagittis turpis. In tristique nisi nulla, sit amet suscipit felis porta at. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aliquam leo odio, dictum a erat interdum, rhoncus rutrum neque. Proin at ornare lorem. Curabitur facilisis facilisis nisl vel commodo. Maecenas ut fringilla ligula, sed commodo sapien. Sed luctus nibh pharetra, sodales massa in, volutpat dolor. Etiam vel maximus sapien. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin ac leo non arcu accumsan pharetra. Sed odio erat, elementum rutrum nibh nec, rutrum varius mauris. Donec bibendum porta venenatis. Praesent ac urna vitae tellus elementum iaculis vel vitae neque.', 'Mazda 3', 5, 3, 300000, 0, 0, 'jorge.saavedra@ejemplo.com', '2026-03-12 10:30:59.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-06-30 21:00:07.000000', 1453600, 2, 5, 4);

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
(7, 10),
(3, 20),
(1, 21),
(1, 51),
(1, 53),
(3, 53),
(1, 54),
(3, 54),
(1, 56),
(1, 57),
(3, 57),
(1, 12),
(3, 12),
(1, 11);

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
(51, 5),
(51, 6),
(51, 7),
(53, 5),
(53, 6),
(53, 7),
(54, 5),
(54, 6),
(54, 7),
(56, 17),
(56, 18),
(56, 19),
(56, 20),
(56, 5),
(56, 6),
(57, 17),
(57, 18),
(57, 19),
(57, 20),
(57, 5),
(57, 6),
(12, 17),
(12, 19),
(12, 20),
(12, 5),
(12, 6),
(12, 7),
(11, 17),
(11, 19),
(11, 20),
(11, 5),
(11, 6),
(11, 7);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `product_policy`
--

CREATE TABLE `product_policy` (
  `product_id` bigint(20) NOT NULL,
  `policy_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `product_policy`
--

INSERT INTO `product_policy` (`product_id`, `policy_id`) VALUES
(56, 1),
(56, 2),
(56, 3),
(56, 4),
(56, 5),
(57, 1),
(57, 2),
(57, 3),
(57, 4),
(57, 5),
(12, 1),
(12, 2),
(12, 3),
(12, 4),
(12, 5),
(11, 1),
(11, 2),
(11, 3),
(11, 4),
(11, 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservation`
--

CREATE TABLE `reservation` (
  `id` binary(16) NOT NULL,
  `pickup_datetime` datetime(6) DEFAULT NULL,
  `return_datetime` datetime(6) DEFAULT NULL,
  `reservation_status` enum('PENDING_CONFIRMATION','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL,
  `pickup_branch_id` bigint(20) DEFAULT NULL,
  `return_branch_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `base_cost` double DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `transfer_fee` double DEFAULT NULL,
  `vehicle_id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `cancellation_date` datetime(6) DEFAULT NULL,
  `cancellation_policy_applied_snapshot` double DEFAULT NULL,
  `cancellation_reason` varchar(255) DEFAULT NULL,
  `fuel_level_at_pickup_snapshot` int(11) DEFAULT NULL,
  `fuel_level_at_return_snapshot` int(11) DEFAULT NULL,
  `insurance_cost` double DEFAULT NULL,
  `insurance_type` enum('BASIC','FULL_COVERAGE','PREMIUM') NOT NULL,
  `pickup_branch_name_snapshot` varchar(255) DEFAULT NULL,
  `policies_snapshot` mediumtext DEFAULT NULL,
  `policy_hash` varchar(255) DEFAULT NULL,
  `product_name_snapshot` varchar(255) DEFAULT NULL,
  `renter_address_snapshot` varchar(255) DEFAULT NULL,
  `renter_birth_date_snapshot` date DEFAULT NULL,
  `renter_driver_license_expiry_snapshot` date DEFAULT NULL,
  `renter_driver_license_snapshot` varchar(255) DEFAULT NULL,
  `renter_email_snapshot` varchar(255) DEFAULT NULL,
  `renter_full_name_snapshot` varchar(255) DEFAULT NULL,
  `renter_id_number_snapshot` varchar(255) DEFAULT NULL,
  `renter_phone_snapshot` varchar(255) DEFAULT NULL,
  `reservation_date` datetime(6) DEFAULT NULL,
  `return_branch_name_snapshot` varchar(255) DEFAULT NULL,
  `vehicle_license_plate_snapshot` varchar(255) DEFAULT NULL,
  `arrival_flight_number` varchar(255) DEFAULT NULL,
  `deposit_amount` double DEFAULT NULL,
  `expiration_date` datetime(6) DEFAULT NULL,
  `extras_cost` double DEFAULT NULL,
  `is_user_the_main_driver` bit(1) NOT NULL,
  `payment_gateway_reference` varchar(255) DEFAULT NULL,
  `payment_intent_id` varchar(255) DEFAULT NULL,
  `payment_status` enum('AUTHORIZED','FAILED','PAID','PENDING','REFUNDED') NOT NULL,
  `renter_city_snapshot` varchar(255) DEFAULT NULL,
  `renter_country_snapshot` varchar(255) DEFAULT NULL,
  `renter_emergency_contact_name_snapshot` varchar(255) DEFAULT NULL,
  `renter_emergency_contact_phone_snapshot` varchar(255) DEFAULT NULL,
  `renter_id_number_type_snapshot` varchar(255) DEFAULT NULL,
  `renter_nationality_snapshot` varchar(255) DEFAULT NULL,
  `renter_state_or_department_snapshot` varchar(255) DEFAULT NULL,
  `renter_zip_code_snapshot` varchar(255) DEFAULT NULL,
  `subtotal` double DEFAULT NULL,
  `tax_amount` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reservation`
--

INSERT INTO `reservation` (`id`, `pickup_datetime`, `return_datetime`, `reservation_status`, `pickup_branch_id`, `return_branch_id`, `user_id`, `version`, `base_cost`, `total_price`, `transfer_fee`, `vehicle_id`, `created_at`, `created_by`, `deleted`, `modified_at`, `modified_by`, `cancellation_date`, `cancellation_policy_applied_snapshot`, `cancellation_reason`, `fuel_level_at_pickup_snapshot`, `fuel_level_at_return_snapshot`, `insurance_cost`, `insurance_type`, `pickup_branch_name_snapshot`, `policies_snapshot`, `policy_hash`, `product_name_snapshot`, `renter_address_snapshot`, `renter_birth_date_snapshot`, `renter_driver_license_expiry_snapshot`, `renter_driver_license_snapshot`, `renter_email_snapshot`, `renter_full_name_snapshot`, `renter_id_number_snapshot`, `renter_phone_snapshot`, `reservation_date`, `return_branch_name_snapshot`, `vehicle_license_plate_snapshot`, `arrival_flight_number`, `deposit_amount`, `expiration_date`, `extras_cost`, `is_user_the_main_driver`, `payment_gateway_reference`, `payment_intent_id`, `payment_status`, `renter_city_snapshot`, `renter_country_snapshot`, `renter_emergency_contact_name_snapshot`, `renter_emergency_contact_phone_snapshot`, `renter_id_number_type_snapshot`, `renter_nationality_snapshot`, `renter_state_or_department_snapshot`, `renter_zip_code_snapshot`, `subtotal`, `tax_amount`) VALUES
(0x8897b252d5a946d2b24bc57ac6081587, '2026-04-12 14:30:00.000000', '2026-04-15 15:30:00.000000', 'COMPLETED', 1, 2, 3, 1, 1200000, 1558900, 50000, 1, '2026-04-09 11:33:59.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-04-09 11:44:42.000000', 'SYSTEM_USER', '2026-04-09 11:44:42.000000', 0, 'Expiración de tiempo en pasarela (10 min).', NULL, NULL, 40000, 'BASIC', 'Branch Bello.', 'Términos y condiciones estándar aplicados.', 'NsAq/NWunx/t/BecNTOGRWWa1VwKpTOARU46TZlPIQA=', 'Carro5', 'Calle 29A No. 50-85', '1985-10-10', '2028-10-01', '1222222222', 'jorge.saavedra@ejemplo.com', 'Juan Fernando Vélez Castro', '1222222222', '+573001234568', '2026-04-09 11:33:59.000000', 'Branch Medellín', 'AAA-000', 'FLY001', 1453600, '2026-04-09 11:43:59.000000', 20000, b'0', NULL, NULL, 'PENDING', 'Bello', 'CO', 'Lina María Castaño Bermudez', '+573001234569', 'CC', 'Colombiano', 'Antioquia', '051052', 1310000, 248900),
(0xc1862286f8aa4ee9af3c90cf3596bf34, '2026-04-16 14:30:00.000000', '2026-04-19 15:30:00.000000', 'COMPLETED', 1, 2, 3, 1, 1200000, 1558900, 50000, 1, '2026-04-09 10:47:54.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-04-09 10:58:42.000000', 'SYSTEM_USER', '2026-04-09 10:58:42.000000', 0, 'Expiración de tiempo en pasarela (10 min).', NULL, NULL, 40000, 'BASIC', 'Branch Bello.', 'Términos y condiciones estándar aplicados.', 'NsAq/NWunx/t/BecNTOGRWWa1VwKpTOARU46TZlPIQA=', 'Carro5', 'Calle 29A No. 50-85', '1980-04-03', '2029-01-01', '1111111', 'jorge.saavedra@ejemplo.com', 'Jorge Armando Saavedra Balanta', '1111111', '+573001234567', '2026-04-09 10:47:54.000000', 'Branch Medellín', 'AAA-000', 'FLY001', 1453600, '2026-04-09 10:57:54.000000', 20000, b'1', NULL, NULL, 'PENDING', 'Bello', 'CO', 'Erica Cadavid', '+573001234567', 'CC', 'Colombiano', 'Antioquia', '051052', 1310000, 248900);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservation_extra`
--

CREATE TABLE `reservation_extra` (
  `id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price_snapshot` double NOT NULL,
  `addon_id` bigint(20) NOT NULL,
  `reservation_id` binary(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reservation_extra`
--

INSERT INTO `reservation_extra` (`id`, `quantity`, `unit_price_snapshot`, `addon_id`, `reservation_id`) VALUES
(1, 1, 5000, 1, 0xc1862286f8aa4ee9af3c90cf3596bf34),
(2, 1, 5000, 1, 0x8897b252d5a946d2b24bc57ac6081587);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reviews`
--

CREATE TABLE `reviews` (
  `id` bigint(20) NOT NULL,
  `comment` text DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `stars` int(11) DEFAULT NULL,
  `reservation_id` binary(16) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `vehicle_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reviews`
--

INSERT INTO `reviews` (`id`, `comment`, `created_at`, `stars`, `reservation_id`, `user_id`, `vehicle_id`) VALUES
(1, 'Excelente', '2026-06-23 19:31:23.000000', 5, 0x8897b252d5a946d2b24bc57ac6081587, 3, 1),
(3, 'Excelente', '2026-07-16 21:37:08.000000', 5, 0xc1862286f8aa4ee9af3c90cf3596bf34, 3, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `role`
--

CREATE TABLE `role` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `role`
--

INSERT INTO `role` (`id`, `name`, `version`, `description`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`) VALUES
(1, 'ADMIN', 13, 'Usuario Administrador.', '', '2026-03-11 18:09:50.385020', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-08 00:23:14.000000'),
(4, 'USER', 2, 'Usuario Usuario', '', '2026-03-11 18:09:50.385020', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-05 18:28:36.000000'),
(15, 'EXAMPLE_USER_DELETED_1773337548660', 2, 'User description.', 'jorge.saavedra@ejemplo.com', '2026-03-12 12:42:28.000000', b'1', 'jorge.saavedra@ejemplo.com', '2026-03-12 12:45:48.000000');

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
(1, 3),
(15, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `share_interactions`
--

CREATE TABLE `share_interactions` (
  `id` bigint(20) NOT NULL,
  `custom_message` varchar(255) DEFAULT NULL,
  `platform` varchar(255) NOT NULL,
  `shared_at` datetime(6) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `share_interactions`
--

INSERT INTO `share_interactions` (`id`, `custom_message`, `platform`, `shared_at`, `product_id`, `user_id`) VALUES
(1, 'Confortable y buen precio', 'facebook', '2026-02-13 16:47:25.000000', 54, 3),
(2, '¡Mira este increíble Carro5 que encontré para rentar!', 'whatsapp', '2026-06-11 17:25:44.000000', 12, 3),
(3, '¡Mira este increíble Carro5 que encontré para rentar!', 'facebook', '2026-06-11 17:25:57.000000', 12, 3),
(4, '¡Mira este increíble Carro5 que encontré para rentar!', 'twitter', '2026-06-11 17:26:08.000000', 12, 3),
(5, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 17:26:17.000000', 12, 3),
(6, '¡Mira este increíble Carro5 que encontré para rentar!', 'whatsapp', '2026-06-11 17:26:35.000000', 12, 3),
(7, '¡Mira este increíble Carro5 que encontré para rentar!', 'whatsapp', '2026-06-11 17:27:11.000000', 12, 3),
(8, '¡Mira este increíble Carro5 que encontré para rentar!', 'facebook', '2026-06-11 17:27:32.000000', 12, 3),
(9, '¡Mira este increíble Carro5 que encontré para rentar!', 'twitter', '2026-06-11 17:27:43.000000', 12, 3),
(10, '¡Mira este increíble Carro5 que encontré para rentar!', 'whatsapp', '2026-06-11 17:30:02.000000', 12, 3),
(11, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 17:37:55.000000', 12, 3),
(12, '¡Mira este increíble Carro5 que encontré para rentar!', 'whatsapp', '2026-06-11 17:39:37.000000', 12, 3),
(13, '¡Mira este increíble Carro5 que encontré para rentar!', 'facebook', '2026-06-11 17:39:42.000000', 12, 3),
(14, '¡Mira este increíble Carro5 que encontré para rentar!', 'twitter', '2026-06-11 17:39:46.000000', 12, 3),
(15, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 17:39:51.000000', 12, 3),
(16, '¡Mira este increíble Carro5 que encontré para rentar!', 'whatsapp', '2026-06-11 19:40:11.000000', 12, 3),
(17, '¡Mira este increíble Carro5 que encontré para rentar!', 'facebook', '2026-06-11 19:40:39.000000', 12, 3),
(18, '¡Mira este increíble Carro5 que encontré para rentar!', 'twitter', '2026-06-11 19:41:06.000000', 12, 3),
(19, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 19:41:27.000000', 12, 3),
(20, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 19:41:59.000000', 12, 3),
(21, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 19:42:48.000000', 12, 3),
(22, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 19:43:23.000000', 12, 3),
(23, '¡Mira este increíble Carro5 que encontré para rentar!', 'facebook', '2026-06-11 19:44:28.000000', 12, 3),
(24, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 19:53:48.000000', 12, 3),
(25, '¡Mira este increíble Carro5 que encontré para rentar!', 'whatsapp', '2026-06-11 19:55:36.000000', 12, 3),
(26, '¡Mira este increíble Carro5 que encontré para rentar!', 'facebook', '2026-06-11 19:55:42.000000', 12, 3),
(27, '¡Mira este increíble Carro5 que encontré para rentar!', 'twitter', '2026-06-11 19:55:46.000000', 12, 3),
(28, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-11 19:55:52.000000', 12, 3),
(29, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:48:22.000000', 12, 3),
(30, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:49:40.000000', 12, 3),
(31, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:50:59.000000', 12, 3),
(32, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:51:18.000000', 12, 3),
(33, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:51:46.000000', 12, 3),
(34, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:52:17.000000', 12, 3),
(35, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:52:47.000000', 12, 3),
(36, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:53:12.000000', 12, 3),
(37, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:55:52.000000', 12, 3),
(38, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:57:37.000000', 12, 3),
(39, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:58:48.000000', 12, 3),
(40, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 20:59:55.000000', 12, 3),
(41, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:06:15.000000', 12, 3),
(42, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:09:40.000000', 12, 3),
(43, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:13:29.000000', 12, 3),
(44, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:14:58.000000', 12, 3),
(45, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:15:33.000000', 12, 3),
(46, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:18:18.000000', 12, 3),
(47, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:33:21.000000', 12, 3),
(48, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:35:36.000000', 12, 3),
(49, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:37:05.000000', 12, 3),
(50, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:37:53.000000', 12, 3),
(51, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:38:46.000000', 12, 3),
(52, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:38:57.000000', 12, 3),
(53, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:39:03.000000', 12, 3),
(54, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:39:20.000000', 12, 3),
(55, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:42:49.000000', 12, 3),
(56, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:43:42.000000', 12, 3),
(57, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:48:24.000000', 12, 3),
(58, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:48:30.000000', 12, 3),
(59, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:48:45.000000', 12, 3),
(60, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:52:16.000000', 12, 3),
(61, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:53:03.000000', 12, 3),
(62, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-11 21:54:14.000000', 12, 3),
(63, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-12 14:27:43.000000', 12, 3),
(64, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-12 14:38:01.000000', 12, 3),
(65, '¡Mira lo que encontré para rentar!', 'whatsapp', '2026-06-12 14:53:34.000000', 12, 3),
(66, '¡Mira este increíble Carro5 que encontré para rentar!', 'facebook', '2026-06-12 14:55:56.000000', 12, 3),
(67, '¡Mira este increíble Carro5 que encontré para rentar!', 'copy', '2026-06-12 14:56:33.000000', 12, 3),
(68, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-12 15:04:42.000000', 12, 3),
(69, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-12 15:12:58.000000', 12, 3),
(70, '¡Mira este increíble Carro5 que encontré para rentar!', 'whatsapp', '2026-06-12 17:14:14.000000', 12, 3),
(71, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-12 17:22:28.000000', 12, 3),
(72, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-12 17:22:59.000000', 12, 3),
(73, 'Confortable y buen precio', 'facebook', '2026-06-16 12:53:53.000000', 12, 3),
(74, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-16 13:13:31.000000', 12, 3),
(75, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-16 13:17:22.000000', 12, 3),
(76, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-16 13:18:12.000000', 12, 3),
(77, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-16 19:26:25.000000', 12, 3),
(78, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-06-30 20:42:09.000000', 56, 3),
(79, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-07-21 19:17:57.000000', 56, 3),
(80, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-08-13 17:20:15.000000', 12, 3),
(81, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-08-13 17:20:54.000000', 12, 3),
(82, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-08-13 17:21:30.000000', 12, 3),
(83, 'Confortable y buen precio', 'facebook', '2026-08-27 14:24:20.000000', 12, 12),
(84, 'Interacción nativa desde dispositivo móvil.', 'NATIVE_MOBILE', '2026-08-27 18:48:44.000000', 56, 3),
(85, '¡Mira este increíble Mazda 2 que encontré para rentar!', 'whatsapp', '2026-08-27 18:53:56.000000', 56, 12);

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
  `password` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `driver_license_number` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `document_number` varchar(255) DEFAULT NULL,
  `driver_license_expiry` date DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `country_code` varchar(255) DEFAULT NULL,
  `emergency_contact_name` varchar(255) DEFAULT NULL,
  `emergency_contact_phone` varchar(255) DEFAULT NULL,
  `nationality` varchar(255) DEFAULT NULL,
  `state_code` varchar(255) DEFAULT NULL,
  `stripe_customer_id` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  `document_type` enum('CC','CE','DNI','NIT','PASSPORT') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `user`
--

INSERT INTO `user` (`id`, `email`, `is_account_non_expired`, `is_account_non_locked`, `is_credentials_non_expired`, `is_enabled`, `last_name`, `name`, `password`, `version`, `driver_license_number`, `created_by`, `created_at`, `deleted`, `modified_by`, `modified_at`, `address`, `birth_date`, `city`, `document_number`, `driver_license_expiry`, `phone_number`, `country_code`, `emergency_contact_name`, `emergency_contact_phone`, `nationality`, `state_code`, `stripe_customer_id`, `zip_code`, `document_type`) VALUES
(3, 'jorge.saavedra@ejemplo.com', b'1', b'1', b'1', b'1', 'Saavedra Balanta', 'Jorge Armando', '$2a$10$5qnrfwZepqAjzshPQxHpeuqVK/mKTGM4P0tDXFFKaveXoAshcY6c.', 45, '1111111', '', '2026-03-11 18:14:30.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-12 17:34:29.000000', 'Calle 29A No. 50-85', '1980-04-03', 'Bello', '1111111', '2029-01-01', '+573001234567', 'CO', 'Erica Cadavid', '+573001234567', 'Colombiano', 'ANT', NULL, '051052', 'CC'),
(4, 'juan.velez@ejemplo.com', b'1', b'1', b'1', b'1', 'Velez Perez', 'Juan Fernando', '$2a$10$fycYBKOOwQm.pnubu2KR6.BTGL3PrUs9ztLQfBxeIXQuKKSc6fuIW', 37, '2222222', '', '2026-03-11 18:14:46.000000', b'0', 'jorge.saavedra@ejemplo.com', '2026-08-08 00:41:14.000000', 'Calle 30A No. 50-86', '1980-04-03', 'Medellín', '2222222', '2029-01-01', '+573001234569', 'CO', 'Carlos Zambrano', '+573001234569', 'Colombiano', 'ANT', NULL, '050015', 'CC'),
(5, 'maria.gomez@ejemplo.com', b'1', b'1', b'1', b'1', 'Gomez Berrio', 'Maria Eugenia', '$2a$10$AOU79y1z.zjQK5Bte1lFce326VMHPw/1hpn1HFwg4QQRd6SMmKdXW', 0, NULL, '', '2026-03-11 18:14:54.000000', b'0', '', '2026-03-11 18:16:41.474433', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC'),
(6, 'lina.betancour@ejemplo.com', b'1', b'1', b'1', b'1', 'Betancour Gonzalez', 'Lina Maria', '$2a$10$AaLQiG5RudZsKs2xCEQqSuHpe2F4L6IGraqL1oPMSxdEBaN4QrlKm', 0, NULL, '', '2026-03-11 18:15:01.000000', b'0', '', '2026-03-11 18:16:41.474433', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC'),
(8, 'juan.restrepo@ejemplo.com', b'1', b'1', b'1', b'1', 'Restrepo Úsuga', 'Juan Camilo', '$2a$10$w.vocA1AoEBKzBowVMaURec4hTv3zydTcdQCkB0Du.s5Gwe2aXOqK', 0, NULL, '', '2026-03-11 18:15:06.000000', b'0', '', '2026-03-11 18:16:41.474433', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC'),
(10, 'lina.betancour@dominio.com', b'1', b'1', b'1', b'1', 'Betancour Gonzalez', 'Lina Maria.', '$2a$10$3vMFWaOUKDCOe3FEOmNoe.oO9uyPy8ahGYb0J36QdaDOgKgP0WSbu', 0, NULL, '', '2026-03-11 18:15:11.000000', b'0', '', '2026-03-11 18:16:41.474433', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC'),
(12, 'bertha.sanchez@dominio.com', b'1', b'1', b'1', b'1', 'Sanchez', 'Bertha', '$2a$10$4v0Mx9f.6VVz0XvTF1qJLeIVBl/ennYPxtTnb07yfBW8eP8YDgMVm', 0, NULL, '', '2026-03-11 18:15:15.000000', b'0', '', '2026-03-11 18:16:41.474433', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC'),
(37, 'jb.save@yahoo.com', b'1', b'1', b'1', b'1', 'Ejemplo', 'Ejemplo', '$2a$10$SX.7JncyPz.QXdWQsKSgzurHRpztzo0l7Ygx.MB.zkKvuEgp0890e', 2, NULL, '', '2026-03-11 18:15:27.000000', b'0', '', '2026-03-11 18:16:41.474433', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC'),
(38, 'anonimo_1df51c78_38@carlikeafriend.com', b'1', b'1', b'1', b'0', 'ELIMINADO', 'USUARIO', '********', 6, '********', '', '2026-02-03 18:41:49.000000', b'1', 'Roberto.cardenas@dominio.com', '2026-07-17 21:18:16.000000', '********', '1984-07-10', 'Medellín', '********', '2029-01-10', '********', 'CO', '********', '********', 'Colombiano', 'ANT', '********', '********', 'CC'),
(39, 'roberto.cardenas@dominio.com', b'1', b'1', b'1', b'1', 'Cardenas Cardenas', 'Roberto', '$2a$10$VFe3uH8q4xbws.KcDdDxmubWFAnHqVg.VPfSzlITUWLohehn0GLRq', 0, NULL, 'bertha.sanchez@dominio.com', '2026-08-27 14:16:01.000000', b'0', 'bertha.sanchez@dominio.com', '2026-08-27 14:16:01.000000', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC'),
(40, 'juan.montero@dominio.com', b'1', b'1', b'1', b'1', 'Montero Fernandez', 'Juan Esteban', '$2a$10$O/KZO3p1rZuRX/Ht7GH9me.KKy.gpYDq3ABpL9V3vAZUbHPmF7mPe', 0, NULL, 'SYSTEM_USER', '2026-08-27 18:21:34.000000', b'0', 'SYSTEM_USER', '2026-08-27 18:21:34.000000', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC'),
(41, 'ana.berrio@dominio.com', b'1', b'1', b'1', b'1', 'Berrio Cardenas', 'Ana Lucía', '$2a$10$eRaYuGeUtp3SEjoheZGEAeiHiE7IxUxZ.AY/BvUXaFTwjERPpmzNW', 0, NULL, 'SYSTEM_USER', '2026-08-27 18:25:39.000000', b'0', 'SYSTEM_USER', '2026-08-27 18:25:39.000000', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CC');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user_favorites`
--

CREATE TABLE `user_favorites` (
  `id` bigint(20) NOT NULL,
  `date_of_addition` datetime(6) DEFAULT NULL,
  `product_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `user_favorites`
--

INSERT INTO `user_favorites` (`id`, `date_of_addition`, `product_id`, `user_id`) VALUES
(99, '2026-08-27 18:53:04.000000', 56, 12),
(100, '2026-08-27 18:58:37.000000', 12, 3),
(101, '2026-08-27 18:58:48.000000', 56, 3),
(102, '2026-08-27 18:58:50.000000', 57, 3);

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
(37, 4),
(38, 4),
(3, 1),
(4, 4),
(39, 4),
(40, 4),
(41, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehicle`
--

CREATE TABLE `vehicle` (
  `id` bigint(20) NOT NULL,
  `color` varchar(255) DEFAULT NULL,
  `current_mileage` int(11) DEFAULT NULL,
  `license_plate` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `vin` varchar(255) NOT NULL,
  `year` int(11) DEFAULT NULL,
  `current_branch_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `modified_at` datetime(6) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `vehicle_status` enum('AVAILABLE','MAINTENANCE','OUT_OF_SERVICE','RENTED') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `vehicle`
--

INSERT INTO `vehicle` (`id`, `color`, `current_mileage`, `license_plate`, `version`, `vin`, `year`, `current_branch_id`, `product_id`, `created_at`, `created_by`, `deleted`, `modified_at`, `modified_by`, `vehicle_status`) VALUES
(1, 'Red', 0, 'AAA-000', 4, '5YJ3E1EA8NF123456', 2026, 1, 12, '2026-04-07 20:05:51.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-08-07 18:36:30.000000', 'jorge.saavedra@ejemplo.com', 'AVAILABLE'),
(2, 'Black', 0, 'AAA-001', 0, '5YJ3E1EA8NF123457', 2026, 2, 56, '2026-06-30 20:35:29.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-06-30 20:35:29.000000', 'jorge.saavedra@ejemplo.com', 'AVAILABLE'),
(3, 'Blue', 0, 'AAA-002', 0, '5YJ3E1EA8NF123458', 2026, 5, 57, '2026-06-30 20:39:16.000000', 'jorge.saavedra@ejemplo.com', b'0', '2026-06-30 20:39:16.000000', 'jorge.saavedra@ejemplo.com', 'AVAILABLE');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `addon`
--
ALTER TABLE `addon`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKtmgeiqbq040ufp2ouhharrbyg` (`name`);

--
-- Indices de la tabla `branch`
--
ALTER TABLE `branch`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2qdmejoguc37exo9i2fjxb0qo` (`name`),
  ADD KEY `FK6g9ajd8aywbcn39ikrl2sbf4s` (`city_id`);

--
-- Indices de la tabla `branch_addon`
--
ALTER TABLE `branch_addon`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKljiq4tbgoit2q5rvbur0l89xk` (`branch_id`,`addon_id`),
  ADD KEY `FK1n3q4ggwlsc0de3c8ftpx93j2` (`addon_id`);

--
-- Indices de la tabla `branch_transfer_fee`
--
ALTER TABLE `branch_transfer_fee`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2gissa1ul5nnf0p2ono8t4b6m` (`origin_branch_id`,`destination_branch_id`),
  ADD KEY `FKbofll5vd5v6p6b36q7exrters` (`destination_branch_id`);

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
-- Indices de la tabla `city`
--
ALTER TABLE `city`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKqsstlki7ni5ovaariyy9u8y79` (`name`);

--
-- Indices de la tabla `feature`
--
ALTER TABLE `feature`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKjhueeftkn8ve8th8m8a2878dr` (`name`),
  ADD UNIQUE KEY `UKhecknf8lleb5frrn73xohti6j` (`feature_icon_id`);

--
-- Indices de la tabla `financial_configuration`
--
ALTER TABLE `financial_configuration`
  ADD PRIMARY KEY (`id`);

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
-- Indices de la tabla `inspection`
--
ALTER TABLE `inspection`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK8go7s8n2r9rb4rthpkos8nb63` (`inspector_id`),
  ADD KEY `idx_inspection_reservation` (`reservation_id`);

--
-- Indices de la tabla `maintenance_log`
--
ALTER TABLE `maintenance_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKtih6immnuhgq0dj3x264u6ysa` (`maintenance_type_id`),
  ADD KEY `FK9wm51rheasl2qycbvfcwnm8fk` (`technician_id`),
  ADD KEY `idx_maintenance_vehicle` (`vehicle_id`);

--
-- Indices de la tabla `maintenance_type`
--
ALTER TABLE `maintenance_type`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKc6yyyds966o8arb5b1poef470` (`code`);

--
-- Indices de la tabla `make`
--
ALTER TABLE `make`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK8khs2v2ojx27e4ig20kkxf3mm` (`name`);

--
-- Indices de la tabla `permission`
--
ALTER TABLE `permission`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2ojme20jpga3r4r79tdso17gi` (`name`);

--
-- Indices de la tabla `policy`
--
ALTER TABLE `policy`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK8400610a8nl6feew9oty0mgyf` (`name`),
  ADD KEY `FKo1xjitah0i8u19u5380o7382w` (`policy_type_id`);

--
-- Indices de la tabla `policy_type`
--
ALTER TABLE `policy_type`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK29v9uf5cvnrvy1n5l26g7dwdk` (`name`);

--
-- Indices de la tabla `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKjmivyxk9rmgysrmsqw15lqr5b` (`name`),
  ADD KEY `idx_product_price` (`price`),
  ADD KEY `idx_product_make` (`make_id`),
  ADD KEY `idx_product_deleted` (`deleted`);

--
-- Indices de la tabla `product_category`
--
ALTER TABLE `product_category`
  ADD KEY `idx_pc_category` (`category_id`),
  ADD KEY `idx_pc_product` (`product_id`);

--
-- Indices de la tabla `product_feature`
--
ALTER TABLE `product_feature`
  ADD KEY `idx_pf_feature` (`feature_id`),
  ADD KEY `idx_pf_product` (`product_id`);

--
-- Indices de la tabla `product_policy`
--
ALTER TABLE `product_policy`
  ADD KEY `idx_pp_policy` (`policy_id`),
  ADD KEY `idx_pp_product` (`product_id`);

--
-- Indices de la tabla `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKgk66rou7hxnarpce5q5wp3jrr` (`payment_intent_id`),
  ADD KEY `FK47jbmf9sxew9rsayg8jc6ln3d` (`return_branch_id`),
  ADD KEY `idx_reservation_status_dates` (`reservation_status`,`pickup_datetime`,`return_datetime`),
  ADD KEY `idx_reservation_availability` (`pickup_branch_id`,`reservation_status`,`pickup_datetime`,`return_datetime`),
  ADD KEY `idx_reservation_vehicle_dates` (`vehicle_id`,`reservation_status`,`pickup_datetime`,`return_datetime`),
  ADD KEY `idx_reservation_user` (`user_id`),
  ADD KEY `idx_reservation_user_status_date` (`user_id`,`reservation_status`,`pickup_datetime`,`return_datetime`);

--
-- Indices de la tabla `reservation_extra`
--
ALTER TABLE `reservation_extra`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_res_extra_addon` (`addon_id`),
  ADD KEY `idx_res_extra_reservation` (`reservation_id`);

--
-- Indices de la tabla `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKlrro92kb91nud7g0x8dcuw3kc` (`reservation_id`),
  ADD KEY `idx_review_vehicle_date` (`vehicle_id`,`created_at`),
  ADD KEY `FKsdlcf7wf8l1k0m00gik0m6b1m` (`user_id`);

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
-- Indices de la tabla `share_interactions`
--
ALTER TABLE `share_interactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_share_product` (`product_id`),
  ADD KEY `idx_share_user` (`user_id`),
  ADD KEY `idx_share_platform` (`platform`);

--
-- Indices de la tabla `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  ADD UNIQUE KEY `UKl1mnqnjv3oriwaapue9b151vv` (`stripe_customer_id`);

--
-- Indices de la tabla `user_favorites`
--
ALTER TABLE `user_favorites`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK4eldcqop16hm9dafduktofge4` (`user_id`,`product_id`),
  ADD KEY `FK36xmy3t451svlu3i8eehg4jod` (`product_id`);

--
-- Indices de la tabla `user_role`
--
ALTER TABLE `user_role`
  ADD KEY `FKa68196081fvovjhkek5m97n3y` (`role_id`),
  ADD KEY `FK859n2jvi8ivhui0rl0esws6o` (`user_id`);

--
-- Indices de la tabla `vehicle`
--
ALTER TABLE `vehicle`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKqqgt2xw93ac4tgx36bux4kavu` (`license_plate`),
  ADD UNIQUE KEY `UK3vyjrop7rn1kcnfdhvlfthoc3` (`vin`),
  ADD KEY `FKqtgeesfg7i1recyk6e4ur3uj1` (`current_branch_id`),
  ADD KEY `idx_vehicle_availability` (`product_id`,`current_branch_id`,`vehicle_status`,`deleted`) USING BTREE;

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `addon`
--
ALTER TABLE `addon`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `branch`
--
ALTER TABLE `branch`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `branch_addon`
--
ALTER TABLE `branch_addon`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `branch_transfer_fee`
--
ALTER TABLE `branch_transfer_fee`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `category`
--
ALTER TABLE `category`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `category_image`
--
ALTER TABLE `category_image`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT de la tabla `city`
--
ALTER TABLE `city`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `feature`
--
ALTER TABLE `feature`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de la tabla `icon`
--
ALTER TABLE `icon`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT de la tabla `image`
--
ALTER TABLE `image`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=148;

--
-- AUTO_INCREMENT de la tabla `inspection`
--
ALTER TABLE `inspection`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `maintenance_log`
--
ALTER TABLE `maintenance_log`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `maintenance_type`
--
ALTER TABLE `maintenance_type`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `make`
--
ALTER TABLE `make`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `permission`
--
ALTER TABLE `permission`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT de la tabla `policy`
--
ALTER TABLE `policy`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `policy_type`
--
ALTER TABLE `policy_type`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `product`
--
ALTER TABLE `product`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=58;

--
-- AUTO_INCREMENT de la tabla `reservation_extra`
--
ALTER TABLE `reservation_extra`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `role`
--
ALTER TABLE `role`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `share_interactions`
--
ALTER TABLE `share_interactions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=86;

--
-- AUTO_INCREMENT de la tabla `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT de la tabla `user_favorites`
--
ALTER TABLE `user_favorites`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=103;

--
-- AUTO_INCREMENT de la tabla `vehicle`
--
ALTER TABLE `vehicle`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `branch`
--
ALTER TABLE `branch`
  ADD CONSTRAINT `FK6g9ajd8aywbcn39ikrl2sbf4s` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`);

--
-- Filtros para la tabla `branch_addon`
--
ALTER TABLE `branch_addon`
  ADD CONSTRAINT `FK1n3q4ggwlsc0de3c8ftpx93j2` FOREIGN KEY (`addon_id`) REFERENCES `addon` (`id`),
  ADD CONSTRAINT `FKemqd74qkgwot3jt7cp7iv7hoq` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`);

--
-- Filtros para la tabla `branch_transfer_fee`
--
ALTER TABLE `branch_transfer_fee`
  ADD CONSTRAINT `FKbofll5vd5v6p6b36q7exrters` FOREIGN KEY (`destination_branch_id`) REFERENCES `branch` (`id`),
  ADD CONSTRAINT `FKilniyumetbggtlplwxd0xnxk5` FOREIGN KEY (`origin_branch_id`) REFERENCES `branch` (`id`);

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
-- Filtros para la tabla `inspection`
--
ALTER TABLE `inspection`
  ADD CONSTRAINT `FK8go7s8n2r9rb4rthpkos8nb63` FOREIGN KEY (`inspector_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKh953y5082op53j69e54sg1efr` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`);

--
-- Filtros para la tabla `maintenance_log`
--
ALTER TABLE `maintenance_log`
  ADD CONSTRAINT `FK9wm51rheasl2qycbvfcwnm8fk` FOREIGN KEY (`technician_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKo75wir137anijcnq9k3c74thy` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`),
  ADD CONSTRAINT `FKtih6immnuhgq0dj3x264u6ysa` FOREIGN KEY (`maintenance_type_id`) REFERENCES `maintenance_type` (`id`);

--
-- Filtros para la tabla `policy`
--
ALTER TABLE `policy`
  ADD CONSTRAINT `FKo1xjitah0i8u19u5380o7382w` FOREIGN KEY (`policy_type_id`) REFERENCES `policy_type` (`id`);

--
-- Filtros para la tabla `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `FK8wocr3j0go4nnukaclnwc1r5s` FOREIGN KEY (`make_id`) REFERENCES `make` (`id`);

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
-- Filtros para la tabla `product_policy`
--
ALTER TABLE `product_policy`
  ADD CONSTRAINT `FK3919ltbrimg4ht0ekg71lties` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  ADD CONSTRAINT `FKgl9tch8ippp98wfecvsl3robc` FOREIGN KEY (`policy_id`) REFERENCES `policy` (`id`);

--
-- Filtros para la tabla `reservation`
--
ALTER TABLE `reservation`
  ADD CONSTRAINT `FK47jbmf9sxew9rsayg8jc6ln3d` FOREIGN KEY (`return_branch_id`) REFERENCES `branch` (`id`),
  ADD CONSTRAINT `FK5gjpg12pkc013i2ethdo2v5u1` FOREIGN KEY (`pickup_branch_id`) REFERENCES `branch` (`id`),
  ADD CONSTRAINT `FKm4oimk0l1757o9pwavorj6ljg` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKrm327sr0rb11mme0kbsm37od5` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`);

--
-- Filtros para la tabla `reservation_extra`
--
ALTER TABLE `reservation_extra`
  ADD CONSTRAINT `FK8ydrtir8c8i2pssexnj8xsnbw` FOREIGN KEY (`addon_id`) REFERENCES `addon` (`id`),
  ADD CONSTRAINT `FKd58sbnyf0x63yi2dl4e78uitu` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`);

--
-- Filtros para la tabla `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `FKm0561e5jp0qts53839dj2uu3v` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`),
  ADD CONSTRAINT `FKsdlcf7wf8l1k0m00gik0m6b1m` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKsyg6w09fx5ermikgl651of0i0` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`);

--
-- Filtros para la tabla `role_permission`
--
ALTER TABLE `role_permission`
  ADD CONSTRAINT `FKa6jx8n8xkesmjmv6jqug6bg68` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  ADD CONSTRAINT `FKf8yllw1ecvwqy3ehyxawqa1qp` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`);

--
-- Filtros para la tabla `share_interactions`
--
ALTER TABLE `share_interactions`
  ADD CONSTRAINT `FK5qtnv6sbymgh75yt5f6iqt8t6` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  ADD CONSTRAINT `FKf4vl889kxa7m7aj27af8oappt` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Filtros para la tabla `user_favorites`
--
ALTER TABLE `user_favorites`
  ADD CONSTRAINT `FK36xmy3t451svlu3i8eehg4jod` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  ADD CONSTRAINT `FK848qdyqh37xmekek29npyyjuo` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Filtros para la tabla `user_role`
--
ALTER TABLE `user_role`
  ADD CONSTRAINT `FK859n2jvi8ivhui0rl0esws6o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKa68196081fvovjhkek5m97n3y` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);

--
-- Filtros para la tabla `vehicle`
--
ALTER TABLE `vehicle`
  ADD CONSTRAINT `FKn1isbveh2hikmxn228ilei8ok` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  ADD CONSTRAINT `FKqtgeesfg7i1recyk6e4ur3uj1` FOREIGN KEY (`current_branch_id`) REFERENCES `branch` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
