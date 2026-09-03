<?php

session_start();
include("db.php");

$error = "";
$success = "";

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $name = trim($_POST['name'] ?? "");
    $email = trim($_POST['email'] ?? "");
    $phone = trim($_POST['phone'] ?? "");
    $subject = trim($_POST['subject'] ?? "");
    $message = trim($_POST['message'] ?? "");

    if (strlen($name) < 2) {
        $error = "Please enter your name.";
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $error = "Please enter a valid email address.";
    } elseif ($message === "") {
        $error = "Please enter a message.";
    } else {

        $stmt = mysqli_prepare(
            $conn,
            "INSERT INTO contact_messages (name, email, phone, subject, message)
             VALUES (?, ?, ?, ?, ?)"
        );
        mysqli_stmt_bind_param($stmt, "sssss", $name, $email, $phone, $subject, $message);

        if (mysqli_stmt_execute($stmt)) {
            $success = "Thanks, " . htmlspecialchars($name) . " — we've received your message and will be in touch soon.";
        } else {
            $error = "Something went wrong sending your message. Please try again.";
        }
        mysqli_stmt_close($stmt);
    }
}

// Cart badge count
$cartCount = 0;
if (isset($_SESSION['user_id'])) {
    $userId = (int) $_SESSION['user_id'];
    $cStmt = mysqli_prepare($conn, "SELECT COALESCE(SUM(quantity), 0) AS total FROM cart WHERE user_id = ?");
    mysqli_stmt_bind_param($cStmt, "i", $userId);
    mysqli_stmt_execute($cStmt);
    $cartCount = mysqli_stmt_get_result($cStmt)->fetch_assoc()['total'];
    mysqli_stmt_close($cStmt);
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Monate | Contact</title>
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

.contact-hero {
    min-height: 340px; display: flex; align-items: center; position: relative; overflow: hidden;
    background: linear-gradient(90deg, rgba(33,23,19,.98), rgba(33,23,19,.85), rgba(33,23,19,.25));
}
.contact-hero-content { width: 90%; max-width: 1250px; margin: auto; position: relative; z-index: 2; }
.contact-hero small { color: var(--tan); font-size: 10px; font-weight: bold; letter-spacing: 4px; text-transform: uppercase; }
.contact-hero h1 { font-family: Georgia, serif; font-weight: normal; font-size: clamp(50px, 7vw, 90px); line-height: .9; margin: 20px 0; }
.contact-hero h1 span { color: var(--tan); }

.contact-section { background: var(--brown-dark); padding: 80px 5%; }
.contact-wrap { max-width: 1100px; margin: auto; display: grid; grid-template-columns: 1fr 1.2fr; gap: 60px; }

.contact-info h2 { font-family: Georgia, serif; font-weight: normal; font-size: 30px; margin-bottom: 20px; }
.contact-info h2 span { color: var(--tan); }
.info-row { display: flex; align-items: flex-start; gap: 14px; margin-bottom: 24px; }
.info-row i { color: var(--orange); font-size: 16px; margin-top: 3px; width: 20px; }
.info-row div p:first-child { font-weight: bold; font-size: 12px; margin-bottom: 3px; }
.info-row div p:last-child { font-size: 11px; color: #a89a8e; line-height: 1.6; }

.contact-form-box { background: var(--brown); border: 1px solid rgba(238,226,209,.1); padding: 34px; }
.alert { padding: 12px 16px; font-size: 11px; margin-bottom: 20px; }
.alert-error { background: rgba(169,97,62,.15); border-left: 3px solid var(--orange); color: #d9b7a5; }
.alert-success { background: rgba(98,100,71,.2); border-left: 3px solid var(--olive); color: #cdd4b5; }

.form-row { display: flex; gap: 16px; }
.form-row .form-group { flex: 1; }
.form-group { margin-bottom: 18px; }
.form-group label {
    display: block; color: var(--tan); font-size: 9px; font-weight: bold;
    text-transform: uppercase; letter-spacing: 1.2px; margin-bottom: 8px;
}
.form-group input, .form-group textarea {
    width: 100%; padding: 12px; background: var(--brown-dark); border: 1px solid rgba(238,226,209,.15);
    color: var(--cream); font-size: 12px; outline: none; font-family: inherit;
}
.form-group input:focus, .form-group textarea:focus { border-color: var(--tan); }
.form-group textarea { resize: vertical; min-height: 120px; }

.submit-btn {
    width: 100%; padding: 15px; border: none; background: var(--orange); color: white;
    font-size: 10px; font-weight: bold; letter-spacing: 1.5px; text-transform: uppercase;
    cursor: pointer; transition: .25s;
}
.submit-btn:hover { background: var(--orange-dark); }

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
    .contact-wrap { grid-template-columns: 1fr; }
    .footer-content { grid-template-columns: 1fr 1fr; }
}
@media(max-width: 600px) {
    header { height: 70px; padding: 0 4%; }
    .logo img { width: 38px; height: 38px; }
    .logo h2 { font-size: 17px; }
    .login, .register { display: none; }
    .form-row { flex-direction: column; gap: 0; }
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

<section class="contact-hero">
<div class="contact-hero-content">
<small>Get In Touch</small>
<h1>Contact <span>Us</span></h1>
</div>
</section>

<section class="contact-section">
<div class="contact-wrap">

<div class="contact-info">
<h2>We'd Love To <span>Hear From You</span></h2>

<div class="info-row">
<i class="fa-solid fa-location-dot"></i>
<div>
<p>Address</p>
<p>Monate Chicken & Steak, Johannesburg, South Africa</p>
</div>
</div>

<div class="info-row">
<i class="fa-solid fa-phone"></i>
<div>
<p>Phone</p>
<p>011 123 4567</p>
</div>
</div>

<div class="info-row">
<i class="fa-solid fa-envelope"></i>
<div>
<p>Email</p>
<p>admin@monate.co.za</p>
</div>
</div>

<div class="info-row">
<i class="fa-solid fa-clock"></i>
<div>
<p>Hours</p>
<p>Mon – Sun, 10:00 – 22:00</p>
</div>
</div>

</div>

<div class="contact-form-box">

<?php if ($error !== ""): ?>
    <div class="alert alert-error"><?php echo htmlspecialchars($error); ?></div>
<?php endif; ?>

<?php if ($success !== ""): ?>
    <div class="alert alert-success"><?php echo $success; ?></div>
<?php endif; ?>

<form method="POST" action="">

<div class="form-row">
<div class="form-group">
<label for="name">Name</label>
<input type="text" id="name" name="name" value="<?php echo htmlspecialchars($_POST['name'] ?? ''); ?>" required>
</div>
<div class="form-group">
<label for="phone">Phone</label>
<input type="tel" id="phone" name="phone" value="<?php echo htmlspecialchars($_POST['phone'] ?? ''); ?>">
</div>
</div>

<div class="form-group">
<label for="email">Email</label>
<input type="email" id="email" name="email" value="<?php echo htmlspecialchars($_POST['email'] ?? ''); ?>" required>
</div>

<div class="form-group">
<label for="subject">Subject</label>
<input type="text" id="subject" name="subject" value="<?php echo htmlspecialchars($_POST['subject'] ?? ''); ?>">
</div>

<div class="form-group">
<label for="message">Message</label>
<textarea id="message" name="message" required><?php echo htmlspecialchars($_POST['message'] ?? ''); ?></textarea>
</div>

<button type="submit" class="submit-btn">Send Message</button>

</form>

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