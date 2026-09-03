-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sep 03, 2026 at 07:37 PM
-- Server version: 10.4.10-MariaDB
-- PHP Version: 7.3.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `monate_chicken`
--

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
CREATE TABLE IF NOT EXISTS `cart` (
  `cart_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT 1,
  PRIMARY KEY (`cart_id`),
  KEY `user_id` (`user_id`),
  KEY `menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`) VALUES
(1, 'Chicken'),
(2, 'Steak'),
(3, 'Burgers'),
(4, 'Wraps'),
(5, 'Pizza'),
(6, 'Wings'),
(7, 'Sides'),
(8, 'Desserts'),
(9, 'Drinks');

-- --------------------------------------------------------

--
-- Table structure for table `contact_messages`
--

DROP TABLE IF EXISTS `contact_messages`;
CREATE TABLE IF NOT EXISTS `contact_messages` (
  `message_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `subject` varchar(100) DEFAULT NULL,
  `message` mediumtext DEFAULT NULL,
  `date_sent` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `contact_messages`
--

INSERT INTO `contact_messages` (`message_id`, `name`, `email`, `phone`, `subject`, `message`, `date_sent`) VALUES
(1, 'Nomvula Zulu', 'nomvula.z@gmail.com', '0731234567', 'Catering Enquiry', 'Hi, do you cater for events of 50+ people? Looking to book for a birthday.', '2026-09-03 19:37:28'),
(2, 'Johan van der Merwe', 'johanvdm@gmail.com', '0821119988', 'Feedback', 'Great service last night, just wanted to say thanks to the team.', '2026-09-03 19:37:28'),
(3, 'Precious Mahlangu', 'precious.m@gmail.com', '0761234567', 'Order Issue', 'My order was missing an item, please can someone contact me.', '2026-09-03 19:37:28'),
(4, 'David Naidoo', 'david.naidoo@gmail.com', '0827654321', 'General Enquiry', 'What are your hours on public holidays?', '2026-09-03 19:37:28'),
(5, 'Zanele Khumalo', 'zanele.k@gmail.com', '0731239876', 'Compliment', 'The wings are honestly the best in town, keep it up!', '2026-09-03 19:37:28'),
(6, 'Michael Botha', 'michael.botha@gmail.com', '0824567890', 'Wrong Order', 'Received someone else\'s order today, please look into this.', '2026-09-03 19:37:28'),
(7, 'Fatima Ismail', 'fatima.ismail@gmail.com', '0761239988', 'Allergy Question', 'Does the pizza contain any nut products?', '2026-09-03 19:37:28'),
(8, 'Tumelo Mahlaba', 'tumelo.m@gmail.com', '0821237766', 'Loyalty Points', 'How do I check how many loyalty points I currently have?', '2026-09-03 19:37:28'),
(9, 'Angela Pretorius', 'angela.p@gmail.com', '0731235544', 'Suggestion', 'Would love to see a vegetarian option added to the menu.', '2026-09-03 19:37:28'),
(10, 'Bongani Mthembu', 'bongani.m@gmail.com', '0827893344', 'Delivery Question', 'Do you deliver to Sandton on weekends?', '2026-09-03 19:37:28');

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
CREATE TABLE IF NOT EXISTS `inventory` (
  `inventory_id` int(11) NOT NULL AUTO_INCREMENT,
  `menu_id` int(11) NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `minimum_stock` int(11) DEFAULT 10,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`inventory_id`),
  KEY `menu_id` (`menu_id`),
  KEY `supplier_id` (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `inventory`
--

INSERT INTO `inventory` (`inventory_id`, `menu_id`, `supplier_id`, `quantity`, `minimum_stock`, `unit_price`, `last_updated`) VALUES
(1, 1, 1, 60, 20, '40.00', '2026-07-30 10:43:41'),
(2, 2, 1, 45, 20, '70.00', '2026-07-30 10:43:41'),
(3, 3, 1, 20, 10, '140.00', '2026-07-30 10:43:41'),
(4, 4, 2, 35, 10, '120.00', '2026-07-30 10:43:41'),
(5, 5, 2, 25, 10, '145.00', '2026-07-30 10:43:41'),
(6, 6, 1, 50, 15, '45.00', '2026-07-30 10:43:41'),
(7, 7, 1, 30, 10, '40.00', '2026-07-30 10:43:41'),
(8, 8, 1, 20, 5, '85.00', '2026-07-30 10:43:41'),
(9, 9, 1, 60, 20, '35.00', '2026-07-30 10:43:41'),
(10, 10, 1, 45, 15, '65.00', '2026-07-30 10:43:41'),
(11, 11, NULL, 150, 40, '12.00', '2026-07-30 10:43:41'),
(12, 12, NULL, 80, 20, '10.00', '2026-07-30 10:43:41'),
(13, 13, NULL, 40, 10, '12.00', '2026-07-30 10:43:41'),
(14, 14, 3, 200, 50, '9.00', '2026-07-30 10:43:41'),
(15, 15, 3, 150, 50, '9.00', '2026-07-30 10:43:41'),
(16, 16, 3, 120, 30, '7.00', '2026-07-30 10:43:41');

-- --------------------------------------------------------

--
-- Table structure for table `loyalty_transactions`
--

DROP TABLE IF EXISTS `loyalty_transactions`;
CREATE TABLE IF NOT EXISTS `loyalty_transactions` (
  `transaction_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `order_id` int(11) DEFAULT NULL,
  `points_earned` int(11) DEFAULT 0,
  `points_used` int(11) DEFAULT 0,
  `transaction_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`transaction_id`),
  KEY `user_id` (`user_id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `loyalty_transactions`
--

INSERT INTO `loyalty_transactions` (`transaction_id`, `user_id`, `order_id`, `points_earned`, `points_used`, `transaction_date`) VALUES
(1, 3, 2, 23, 0, '2026-09-03 19:37:28'),
(2, 4, 3, 10, 0, '2026-09-03 19:37:28'),
(3, 5, 4, 31, 0, '2026-09-03 19:37:28'),
(4, 6, 5, 15, 0, '2026-09-03 19:37:28'),
(5, 7, 6, 15, 0, '2026-09-03 19:37:28'),
(6, 2, 7, 16, 0, '2026-09-03 19:37:28'),
(7, 3, 8, 10, 0, '2026-09-03 19:37:28'),
(8, 5, 10, 7, 0, '2026-09-03 19:37:28'),
(9, 7, 11, 12, 0, '2026-09-03 19:37:28');

-- --------------------------------------------------------

--
-- Table structure for table `menu_items`
--

DROP TABLE IF EXISTS `menu_items`;
CREATE TABLE IF NOT EXISTS `menu_items` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `description` mediumtext DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `available` enum('Yes','No') DEFAULT 'Yes',
  PRIMARY KEY (`menu_id`),
  KEY `category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `menu_items`
--

INSERT INTO `menu_items` (`menu_id`, `category_id`, `item_name`, `description`, `price`, `image`, `available`) VALUES
(1, 1, 'Quarter Chicken', 'Flame grilled quarter chicken', '55.00', 'quarter.jpg', 'Yes'),
(2, 1, 'Half Chicken', 'Half flame grilled chicken', '95.00', 'half.jpg', 'Yes'),
(3, 1, 'Full Chicken', 'Whole flame grilled chicken', '180.00', 'full.jpg', 'Yes'),
(4, 2, 'Rump Steak', '300g Rump Steak', '145.00', 'rump.jpg', 'Yes'),
(5, 2, 'T-Bone Steak', '350g T-Bone Steak', '170.00', 'tbone.jpg', 'Yes'),
(6, 3, 'Chicken Burger', 'Chicken Burger & Chips', '75.00', 'burger.jpg', 'Yes'),
(7, 4, 'Chicken Wrap', 'Grilled Chicken Wrap', '70.00', 'wrap.jpg', 'Yes'),
(8, 5, 'Chicken Pizza', 'Chicken Pizza Large', '130.00', 'pizza.jpg', 'Yes'),
(9, 6, '6 Wings', 'Spicy Wings', '65.00', 'wings6.jpg', 'Yes'),
(10, 6, '12 Wings', 'Spicy Wings', '120.00', 'wings12.jpg', 'Yes'),
(11, 7, 'Large Chips', 'Large Chips', '35.00', 'chips.jpg', 'Yes'),
(12, 7, 'Onion Rings', 'Crispy Onion Rings', '30.00', 'rings.jpg', 'Yes'),
(13, 8, 'Ice Cream', 'Vanilla Ice Cream', '28.00', 'icecream.jpg', 'Yes'),
(14, 9, 'Coke 440ml', 'Soft Drink', '20.00', 'coke.jpg', 'Yes'),
(15, 9, 'Sprite 440ml', 'Soft Drink', '20.00', 'sprite.jpg', 'Yes'),
(16, 9, 'Water', 'Still Water', '15.00', 'water.jpg', 'Yes');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `payment_method` enum('Cash','Card','EFT') NOT NULL,
  `collection_time` datetime DEFAULT NULL,
  `status` enum('Received','Preparing','Cooking','Ready','Collected','Cancelled') DEFAULT 'Received',
  `order_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`order_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `user_id`, `total`, `payment_method`, `collection_time`, `status`, `order_date`) VALUES
(1, 2, '150.00', 'Card', '2026-01-20 12:00:00', 'Received', '2026-09-02 22:38:16'),
(2, 3, '230.00', 'Card', '2026-08-20 18:30:00', 'Collected', '2026-09-03 19:37:27'),
(3, 4, '105.00', 'Cash', '2026-08-21 12:15:00', 'Collected', '2026-09-03 19:37:27'),
(4, 5, '315.00', 'EFT', '2026-08-22 19:00:00', 'Collected', '2026-09-03 19:37:28'),
(5, 6, '150.00', 'Card', '2026-08-23 13:45:00', 'Ready', '2026-09-03 19:37:28'),
(6, 7, '155.00', 'Cash', '2026-08-24 20:00:00', 'Preparing', '2026-09-03 19:37:28'),
(7, 2, '165.00', 'EFT', '2026-08-25 17:30:00', 'Collected', '2026-09-03 19:37:28'),
(8, 3, '108.00', 'Card', '2026-08-26 14:00:00', 'Collected', '2026-09-03 19:37:28'),
(9, 4, '180.00', 'Cash', '2026-08-27 19:15:00', 'Cancelled', '2026-09-03 19:37:28'),
(10, 5, '75.00', 'Card', '2026-08-28 12:00:00', 'Cooking', '2026-09-03 19:37:28'),
(11, 7, '125.00', 'EFT', '2026-08-29 18:45:00', 'Received', '2026-09-03 19:37:28');

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
CREATE TABLE IF NOT EXISTS `order_items` (
  `order_item_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`order_item_id`, `order_id`, `menu_id`, `quantity`, `price`, `subtotal`) VALUES
(1, 1, 2, 1, '95.00', '95.00'),
(2, 1, 1, 1, '55.00', '55.00'),
(3, 2, 3, 1, '180.00', '180.00'),
(4, 2, 11, 1, '35.00', '35.00'),
(5, 2, 14, 1, '20.00', '20.00'),
(6, 3, 6, 1, '75.00', '75.00'),
(7, 3, 12, 1, '30.00', '30.00'),
(8, 4, 4, 1, '145.00', '145.00'),
(9, 4, 5, 1, '170.00', '170.00'),
(10, 5, 8, 1, '130.00', '130.00'),
(11, 5, 15, 1, '20.00', '20.00'),
(12, 6, 10, 1, '120.00', '120.00'),
(13, 6, 11, 1, '35.00', '35.00'),
(14, 7, 2, 1, '95.00', '95.00'),
(15, 7, 7, 1, '70.00', '70.00'),
(16, 8, 9, 1, '65.00', '65.00'),
(17, 8, 13, 1, '28.00', '28.00'),
(18, 8, 16, 1, '15.00', '15.00'),
(19, 9, 3, 1, '180.00', '180.00'),
(20, 10, 1, 1, '55.00', '55.00'),
(21, 10, 14, 1, '20.00', '20.00'),
(22, 11, 6, 1, '75.00', '75.00'),
(23, 11, 15, 1, '20.00', '20.00'),
(24, 11, 11, 1, '35.00', '35.00');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `payment_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_method` enum('Cash','Card','EFT') DEFAULT NULL,
  `payment_status` enum('Pending','Paid','Failed') DEFAULT 'Pending',
  `payment_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`payment_id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`payment_id`, `order_id`, `amount`, `payment_method`, `payment_status`, `payment_date`) VALUES
(1, 2, '230.00', 'Card', 'Paid', '2026-09-03 19:37:27'),
(2, 3, '105.00', 'Cash', 'Paid', '2026-09-03 19:37:28'),
(3, 4, '315.00', 'EFT', 'Paid', '2026-09-03 19:37:28'),
(4, 5, '150.00', 'Card', 'Pending', '2026-09-03 19:37:28'),
(5, 6, '155.00', 'Cash', 'Paid', '2026-09-03 19:37:28'),
(6, 7, '165.00', 'EFT', 'Paid', '2026-09-03 19:37:28'),
(7, 8, '108.00', 'Card', 'Paid', '2026-09-03 19:37:28'),
(8, 9, '180.00', 'Cash', 'Failed', '2026-09-03 19:37:28'),
(9, 10, '75.00', 'Card', 'Paid', '2026-09-03 19:37:28'),
(10, 11, '125.00', 'EFT', 'Pending', '2026-09-03 19:37:28');

-- --------------------------------------------------------

--
-- Table structure for table `promotions`
--

DROP TABLE IF EXISTS `promotions`;
CREATE TABLE IF NOT EXISTS `promotions` (
  `promotion_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `description` mediumtext DEFAULT NULL,
  `discount_percent` decimal(5,2) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`promotion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `promotions`
--

INSERT INTO `promotions` (`promotion_id`, `title`, `description`, `discount_percent`, `start_date`, `end_date`, `image`) VALUES
(1, 'Monday Special', '10% off all burgers', '10.00', '2026-01-01', '2026-12-31', 'burger_special.jpg'),
(2, 'Family Feast', '15% off Full Chicken', '15.00', '2026-01-01', '2026-12-31', 'family.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
CREATE TABLE IF NOT EXISTS `reviews` (
  `review_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  `rating` int(11) NOT NULL,
  `review` mediumtext DEFAULT NULL,
  `review_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`review_id`),
  KEY `user_id` (`user_id`),
  KEY `menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `reviews`
--

INSERT INTO `reviews` (`review_id`, `user_id`, `menu_id`, `rating`, `review`, `review_date`) VALUES
(1, 3, 3, 5, 'Best full chicken in the area, always juicy and perfectly seasoned.', '2026-09-03 19:37:28'),
(2, 4, 6, 4, 'Great burger, chips could be a bit crispier but still solid.', '2026-09-03 19:37:28'),
(3, 5, 4, 5, 'Rump steak was cooked exactly as I asked. Will order again.', '2026-09-03 19:37:28'),
(4, 6, 8, 4, 'Good pizza, generous toppings. Delivery was quick.', '2026-09-03 19:37:28'),
(5, 7, 10, 5, '12 wings and not one disappointing bite. Great value.', '2026-09-03 19:37:28'),
(6, 2, 2, 4, 'Half chicken hit the spot, portion size was fair for the price.', '2026-09-03 19:37:28'),
(7, 3, 9, 3, 'Wings were good but a little smaller than I expected.', '2026-09-03 19:37:28'),
(8, 5, 1, 5, 'Quarter chicken is my go-to lunch order, never disappoints.', '2026-09-03 19:37:28'),
(9, 4, 7, 4, 'Wrap was fresh and filling, good on-the-go option.', '2026-09-03 19:37:28'),
(10, 7, 13, 5, 'Ice cream was a perfect way to end the meal.', '2026-09-03 19:37:28'),
(11, 6, 5, 5, 'T-bone was excellent, great char on the outside.', '2026-09-03 19:37:28'),
(12, 2, 3, 5, 'Ordered for the family, everyone loved it.', '2026-09-03 19:37:28');

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE IF NOT EXISTS `suppliers` (
  `supplier_id` int(11) NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(100) NOT NULL,
  `contact_person` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` mediumtext DEFAULT NULL,
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`supplier_id`, `supplier_name`, `contact_person`, `phone`, `email`, `address`) VALUES
(1, 'Chicken Farm SA', 'John Smith', '0111234567', 'sales@chickenfarm.co.za', 'Johannesburg'),
(2, 'Fresh Meat Suppliers', 'Peter Jones', '0115554455', 'orders@freshmeat.co.za', 'Pretoria'),
(3, 'Cool Drinks Ltd', 'Sarah Adams', '0117778888', 'sales@cooldrinks.co.za', 'Johannesburg');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `loyalty_points` int(11) DEFAULT 0,
  `role` enum('customer','admin') DEFAULT 'customer',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `first_name`, `last_name`, `email`, `phone`, `password`, `loyalty_points`, `role`, `created_at`) VALUES
(1, 'Admin', 'User', 'admin@monate.co.za', '0111111111', '$2y$10$.7IS8qIEa876bKK6WDVNsevkybo8O/6FUxMr4jeAHdCwVfWEAS62i', 0, 'admin', '2026-07-30 10:43:41'),
(2, 'John', 'Doe', 'john@gmail.com', '0821234567', '$2y$10$gMIOgZNgUl.qrgunlb40X.GPpLCg79hEt8HyDB40lfjFmOnJpQov6', 0, 'customer', '2026-07-30 10:43:41'),
(3, 'Thabo', 'Nkosi', 'thabo.nkosi@gmail.com', '0821234501', '$2y$10$gMIOgZNgUl.qrgunlb40X.GPpLCg79hEt8HyDB40lfjFmOnJpQov6', 40, 'customer', '2026-09-03 19:37:27'),
(4, 'Lindiwe', 'Dlamini', 'lindiwe.d@gmail.com', '0821234502', '$2y$10$gMIOgZNgUl.qrgunlb40X.GPpLCg79hEt8HyDB40lfjFmOnJpQov6', 15, 'customer', '2026-09-03 19:37:27'),
(5, 'Sipho', 'Mokoena', 'sipho.mokoena@gmail.com', '0821234503', '$2y$10$gMIOgZNgUl.qrgunlb40X.GPpLCg79hEt8HyDB40lfjFmOnJpQov6', 60, 'customer', '2026-09-03 19:37:27'),
(6, 'Aisha', 'Patel', 'aisha.patel@gmail.com', '0821234504', '$2y$10$gMIOgZNgUl.qrgunlb40X.GPpLCg79hEt8HyDB40lfjFmOnJpQov6', 0, 'customer', '2026-09-03 19:37:27'),
(7, 'Karabo', 'Sithole', 'karabo.sithole@gmail.com', '0821234505', '$2y$10$gMIOgZNgUl.qrgunlb40X.GPpLCg79hEt8HyDB40lfjFmOnJpQov6', 25, 'customer', '2026-09-03 19:37:27');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `fk_cart_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu_items` (`menu_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `inventory`
--
ALTER TABLE `inventory`
  ADD CONSTRAINT `fk_inventory_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu_items` (`menu_id`),
  ADD CONSTRAINT `fk_inventory_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`);

--
-- Constraints for table `loyalty_transactions`
--
ALTER TABLE `loyalty_transactions`
  ADD CONSTRAINT `fk_loyalty_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `fk_loyalty_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD CONSTRAINT `fk_menu_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `fk_orderitems_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu_items` (`menu_id`),
  ADD CONSTRAINT `fk_orderitems_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE;

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `fk_reviews_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu_items` (`menu_id`),
  ADD CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
