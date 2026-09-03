<?php

session_start();
include("db.php");

// Cart badge count (same pattern as menu.php/cart.php)
$cartCount = 0;
if (isset($_SESSION['user_id'])) {
    $userId = (int) $_SESSION['user_id'];
    $stmt = mysqli_prepare($conn, "SELECT COALESCE(SUM(quantity), 0) AS total FROM cart WHERE user_id = ?");
    mysqli_stmt_bind_param($stmt, "i", $userId);
    mysqli_stmt_execute($stmt);
    $cartCount = mysqli_stmt_get_result($stmt)->fetch_assoc()['total'];
    mysqli_stmt_close($stmt);
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Monate | About</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

<style>
:root {
    --brown-dark: #211713; --brown: #34231c; --cream: #eee2d1; --cream-light: #f8f1e7;
    --tan: #c8ad8f; --orange: #a9613e; --orange-dark: #82452d; --olive: #626447; --white: #fffaf4;
}
* { margin: 0; padding: 0; box-sizing: border-box; }
html { scroll-behavior: smooth; }
body { background: var(--brown-dark); color: var(--cream); font-family: Arial, Helvetica, sans-serif; }

header {
    height: 82px; padding: 0 5%; display: flex; align-items: center; justify-content: space-between;
    background: rgba(33,23,19,.97); border-bottom: 1px solid rgba(238,226,209,.1);
    position: sticky; top: 0; z-index: 1000;
}
.logo { display: flex; align-items: center; gap: 12px; }
.logo img { width: 46px; height: 46px; object-fit: contain; }
.logo h2 { font-family: Georgia, serif; font-size: 21px; font-weight: normal; color: var(--cream); }
.logo h2 span { display: block; font-family: Arial, sans-serif; font-size: 8px; letter-spacing: 2px; text-transform: uppercase; color: var(--tan); margin-top: 3px; }
nav { display: flex; gap: 30px; }
nav a { font-size: 10px; text-transform: uppercase; letter-spacing: 1.2px; font-weight: bold; color: #cdbdae; transition: .25s; }
nav a:hover { color: var(--tan); }
.right { display: flex; align-items: center; gap: 8px; }
.cart { position: relative; width: 40px; height: 40px; border: 1px solid rgba(238,226,209,.2); border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.cart i { color: var(--cream); font-size: 14px; }
.cart span { position: absolute; right: -4px; top: -5px; width: 17px; height: 17px; display: flex; align-items: center; justify-content: center; background: var(--orange); border-radius: 50%; color: white; font-size: 9px; }
.login { color: var(--cream); font-size: 9px; font-weight: bold; text-transform: uppercase; padding: 10px; }
.register { background: var(--orange); color: white; padding: 11px 15px; font-size: 9px; font-weight: bold; text-transform: uppercase; transition: .25s; }
.register:hover { background: var(--orange-dark); }

.about-hero {
    min-height: 420px; display: flex; align-items: center; position: relative; overflow: hidden;
    background: linear-gradient(90deg, rgba(33,23,19,.98), rgba(33,23,19,.85), rgba(33,23,19,.25));
}
.about-hero-content { width: 90%; max-width: 1250px; margin: auto; position: relative; z-index: 2; }
.about-hero small { color: var(--tan); font-size: 10px; font-weight: bold; letter-spacing: 4px; text-transform: uppercase; }
.about-hero h1 { font-family: Georgia, serif; font-weight: normal; font-size: clamp(55px, 8vw, 100px); line-height: .9; margin: 20px 0; }
.about-hero h1 span { color: var(--tan); }
.about-hero p { color: #cbbdb1; max-width: 560px; font-size: 13px; line-height: 1.8; }

.story-section { background: var(--cream); color: var(--brown); padding: 80px 5%; }
.story-inner { max-width: 900px; margin: auto; text-align: center; }
.story-inner small { color: var(--orange); text-transform: uppercase; letter-spacing: 3px; font-size: 9px; font-weight: bold; }
.story-inner h2 { font-family: Georgia, serif; font-size: clamp(32px,4.5vw,48px); font-weight: normal; margin: 12px 0 20px; }
.story-inner h2 span { color: var(--orange); }
.story-inner p { font-size: 13px; color: #6b5c50; line-height: 1.9; margin-bottom: 16px; }

.values-section { background: var(--brown-dark); padding: 90px 5%; }
.values-inner { max-width: 1100px; margin: auto; }
.values-heading { text-align: center; margin-bottom: 55px; }
.values-heading small { color: var(--tan); text-transform: uppercase; font-size: 9px; letter-spacing: 3px; font-weight: bold; }
.values-heading h2 { font-family: Georgia, serif; font-size: clamp(32px,4.5vw,48px); font-weight: normal; margin-top: 10px; }
.values-heading h2 span { color: var(--tan); }

.values-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.value-card { background: var(--brown); border: 1px solid rgba(238,226,209,.1); padding: 34px 26px; text-align: center; }
.value-card i { font-size: 26px; color: var(--orange); margin-bottom: 16px; }
.value-card h3 { font-family: Georgia, serif; font-weight: normal; font-size: 18px; margin-bottom: 10px; }
.value-card p { font-size: 11px; color: #a89a8e; line-height: 1.7; }

.hours-section { background: var(--cream); color: var(--brown); padding: 70px 5%; }
.hours-inner { max-width: 700px; margin: auto; text-align: center; }
.hours-inner small { color: var(--orange); text-transform: uppercase; letter-spacing: 3px; font-size: 9px; font-weight: bold; }
.hours-inner h2 { font-family: Georgia, serif; font-size: clamp(30px,4vw,42px); font-weight: normal; margin: 10px 0 30px; }
.hours-list { display: inline-block; text-align: left; }
.hours-row { display: flex; justify-content: space-between; gap: 60px; padding: 10px 0; border-bottom: 1px solid rgba(52,35,28,.1); font-size: 12px; }
.hours-row span:first-child { font-weight: bold; }

footer { background: #120e0c; padding: 55px 5% 20px; }
.footer-content { max-width: 1200px; margin: auto; display: grid; grid-template-columns: 2fr 1fr 1fr; gap: 50px; padding-bottom: 40px; }
.footer-brand h2 { font-family: Georgia, serif; font-size: 28px; font-weight: normal; margin-bottom: 10px; }
.footer-brand p { max-width: 320px; color: #766b63; font-size: 10px; line-height: 1.8; }
.footer-column h4 { color: var(--tan); font-size: 9px; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 16px; }
.footer-column a { display: block; color: #786d66; font-size: 10px; margin-bottom: 10px; }
.footer-column a:hover { color: var(--cream); }
.footer-bottom { border-top: 1px solid rgba(238,226,209,.08); padding-top: 18px; text-align: center; color: #514943; font-size: 9px; }

@media(max-width: 900px) {
    nav { display: none; }
    .values-grid { grid-template-columns: 1fr; }
    .footer-content { grid-template-columns: 1fr 1fr; }
}
@media(max-width: 600px) {
    header { height: 70px; padding: 0 4%; }
    .logo img { width: 38px; height: 38px; }
    .logo h2 { font-size: 17px; }
    .login, .register { display: none; }
    .about-hero { min-height: 340px; }
    .hours-row { gap: 30px; }
    .footer-content { grid-template-columns: 1fr; }
}
</style>
</head>

<body>

<header>
<div class="logo">
<img src="images/logo.png" alt="Monate">
<h2>Monate<span>Chicken & Steak</span></h2>
</div>

<nav>
<a href="index.php">Home</a>
<a href="menu.php">Menu</a>
<a href="about.php">About</a>
<a href="contact.php">Contact</a>
</nav>

<div class="right">
<a href="cart.php" class="cart">
<i class="fas fa-shopping-bag"></i>
<span><?php echo (int) $cartCount; ?></span>
</a>

<?php if (isset($_SESSION['user_id'])): ?>
<a href="profile.php" class="login">Account</a>
<a href="logout.php" class="register">Logout</a>
<?php else: ?>
<a href="login.php" class="login">Login</a>
<a href="register.php" class="register">Register</a>
<?php endif; ?>
</div>
</header>

<section class="about-hero">
<div class="about-hero-content">
<small>Our Story</small>
<h1>About <span>Monate</span></h1>
<p>Flame-grilled chicken and steak, made fresh, served fast — Monate has been bringing bold flavour to the community, one plate at a time.</p>
</div>
</section>

<section class="story-section">
<div class="story-inner">
<small>Who We Are</small>
<h2>From Flame to <span>Table</span></h2>
<p>Monate Chicken & Steak started with a simple idea: great food shouldn't take forever, and it shouldn't cost you flavour to get it fast. Every order is flame-grilled fresh, never microwaved, never rushed.</p>
<p>What began as a single grill has grown into a full menu of chicken, steak, burgers, wings and sides — but the promise hasn't changed. Fresh ingredients, honest prices, and food that's actually worth queuing for.</p>
</div>
</section>

<section class="values-section">
<div class="values-inner">

<div class="values-heading">
<small>What We Stand For</small>
<h2>Our <span>Values</span></h2>
</div>

<div class="values-grid">

<div class="value-card">
<i class="fa-solid fa-fire"></i>
<h3>Flame-Grilled Fresh</h3>
<p>Every order is cooked to order, over an open flame — never pre-made, never sitting under a heat lamp.</p>
</div>

<div class="value-card">
<i class="fa-solid fa-leaf"></i>
<h3>Quality Ingredients</h3>
<p>We work with trusted local suppliers to keep every ingredient fresh, from the chicken to the sides.</p>
</div>

<div class="value-card">
<i class="fa-solid fa-heart"></i>
<h3>Community First</h3>
<p>Monate is built for the people who eat here — fair prices, fast service, and food worth coming back for.</p>
</div>

</div>
</div>
</section>

<section class="hours-section">
<div class="hours-inner">
<small>Visit Us</small>
<h2>Opening <span>Hours</span></h2>

<div class="hours-list">
<div class="hours-row"><span>Monday – Friday</span><span>10:00 – 21:00</span></div>
<div class="hours-row"><span>Saturday</span><span>10:00 – 22:00</span></div>
<div class="hours-row"><span>Sunday</span><span>11:00 – 20:00</span></div>
<div class="hours-row"><span>Public Holidays</span><span>11:00 – 20:00</span></div>
</div>

</div>
</section>

<footer>
<div class="footer-content">
<div class="footer-brand">
<h2>MONATE</h2>
<p>Monate Chicken & Steak — flame-grilled, fast, and full of flavour.</p>
</div>

<div class="footer-column">
<h4>Explore</h4>
<a href="index.php">Home</a>
<a href="menu.php">Menu</a>
<a href="about.php">About</a>
<a href="contact.php">Contact</a>
</div>

<div class="footer-column">
<h4>Account</h4>
<a href="login.php">Login</a>
<a href="register.php">Register</a>
<a href="cart.php">Cart</a>
</div>
</div>

<div class="footer-bottom">
© <?php echo date("Y"); ?> Monate Chicken & Steak. All Rights Reserved.
</div>
</footer>

</body>
</html>