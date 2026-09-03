<?php

session_start();

include("db.php");

/*
|--------------------------------------------------------------------------
| MONATE EVENTS
|--------------------------------------------------------------------------
| Add or remove events here.
|
| Put event images inside:
| images/events/
|--------------------------------------------------------------------------
*/

$events = [

    [
        "title" => "Monate Weekend Experience",
        "date" => "Every Weekend",
        "time" => "12:00 - Late",
        "description" => "Come through for good food, great music and the Monate experience.",
        "image" => "weekend.jpg",
        "status" => "upcoming"
    ],

    [
        "title" => "Monate Live Sessions",
        "date" => "Coming Soon",
        "time" => "18:00 - Late",
        "description" => "An evening of live entertainment, great food and good company.",
        "image" => "live-session.jpg",
        "status" => "upcoming"
    ],

    [
        "title" => "Monate Sunday Experience",
        "date" => "Every Sunday",
        "time" => "12:00 - Late",
        "description" => "Spend your Sunday with good food, music and the Monate atmosphere.",
        "image" => "sunday.jpg",
        "status" => "upcoming"
    ]

];

?>

<!DOCTYPE html>

<html lang="en">

<head>

<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Monate | Events</title>

<link rel="stylesheet"
href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">


<style>

/* =====================================================
   MONATE COLOURS
===================================================== */

:root {

    --dark: #211713;

    --dark2: #2b1d17;

    --brown: #34231c;

    --cream: #eee2d1;

    --cream-light: #f8f1e7;

    --tan: #c8ad8f;

    --orange: #a9613e;

    --orange-dark: #82452d;

    --olive: #626447;

    --white: #fffaf4;

}


/* =====================================================
   RESET
===================================================== */

* {

    margin: 0;

    padding: 0;

    box-sizing: border-box;

}

html {

    scroll-behavior: smooth;

}

body {

    background: var(--dark);

    color: var(--cream);

    font-family: Arial, Helvetica, sans-serif;

}

a {

    text-decoration: none;

    color: inherit;

}

img {

    width: 100%;

    display: block;

}


/* =====================================================
   HEADER
===================================================== */

header {

    height: 82px;

    padding: 0 5%;

    display: flex;

    align-items: center;

    justify-content: space-between;

    background: rgba(33,23,19,.97);

    border-bottom: 1px solid rgba(238,226,209,.1);

    position: sticky;

    top: 0;

    z-index: 1000;

}


/* LOGO */

.logo {

    display: flex;

    align-items: center;

    gap: 12px;

}

.logo img {

    width: 46px;

    height: 46px;

    object-fit: contain;

}

.logo h2 {

    font-family: Georgia, serif;

    font-size: 21px;

    font-weight: normal;

}

.logo h2 span {

    display: block;

    font-family: Arial, sans-serif;

    font-size: 8px;

    letter-spacing: 2px;

    text-transform: uppercase;

    color: var(--tan);

    margin-top: 3px;

}


/* NAVIGATION */

nav {

    display: flex;

    gap: 30px;

}

nav a {

    font-size: 10px;

    text-transform: uppercase;

    letter-spacing: 1px;

    font-weight: bold;

    color: #cdbdae;

    transition: .25s;

}

nav a:hover {

    color: var(--tan);

}


/* RIGHT */

.right {

    display: flex;

    align-items: center;

    gap: 8px;

}

.cart {

    position: relative;

    width: 40px;

    height: 40px;

    border: 1px solid rgba(238,226,209,.2);

    border-radius: 50%;

    display: flex;

    align-items: center;

    justify-content: center;

}

.cart span {

    position: absolute;

    right: -4px;

    top: -5px;

    width: 17px;

    height: 17px;

    background: var(--orange);

    border-radius: 50%;

    color: white;

    font-size: 9px;

    display: flex;

    align-items: center;

    justify-content: center;

}

.login {

    font-size: 9px;

    font-weight: bold;

    text-transform: uppercase;

    padding: 10px;

}

.register {

    background: var(--orange);

    padding: 11px 15px;

    font-size: 9px;

    font-weight: bold;

    text-transform: uppercase;

    transition: .25s;

}

.register:hover {

    background: var(--orange-dark);

}


/* =====================================================
   HERO
===================================================== */

.events-hero {

    min-height: 520px;

    display: flex;

    align-items: center;

    position: relative;

    overflow: hidden;

    background:

        linear-gradient(
            90deg,
            rgba(33,23,19,.98),
            rgba(33,23,19,.82),
            rgba(33,23,19,.35)
        ),

        url("images/events/events-hero.jpg")
        center/cover no-repeat;

}


.events-hero-content {

    width: 90%;

    max-width: 1250px;

    margin: auto;

}


.events-hero small {

    color: var(--tan);

    font-size: 10px;

    font-weight: bold;

    text-transform: uppercase;

    letter-spacing: 4px;

}


.events-hero h1 {

    font-family: Georgia, serif;

    font-size: clamp(65px, 9vw, 120px);

    font-weight: normal;

    line-height: .88;

    margin: 20px 0;

}


.events-hero h1 span {

    color: var(--tan);

}


.events-hero p {

    max-width: 540px;

    color: #cdbeb1;

    font-size: 13px;

    line-height: 1.8;

}


/* =====================================================
   INTRO
===================================================== */

.intro {

    background: var(--cream);

    color: var(--brown);

    text-align: center;

    padding: 70px 20px;

}


.intro small {

    color: var(--orange);

    text-transform: uppercase;

    letter-spacing: 3px;

    font-size: 9px;

    font-weight: bold;

}


.intro h2 {

    font-family: Georgia, serif;

    font-size: clamp(38px, 5vw, 60px);

    font-weight: normal;

    margin-top: 10px;

}


.intro h2 span {

    color: var(--orange);

}


.intro p {

    max-width: 600px;

    margin: 18px auto 0;

    color: #75665a;

    font-size: 13px;

    line-height: 1.8;

}


/* =====================================================
   UPCOMING EVENTS
===================================================== */

.events-section {

    padding: 90px 5%;

    background: var(--dark);

}


.container {

    max-width: 1250px;

    margin: auto;

}


.section-heading {

    margin-bottom: 45px;

}


.section-heading small {

    color: var(--tan);

    font-size: 9px;

    text-transform: uppercase;

    letter-spacing: 3px;

    font-weight: bold;

}


.section-heading h2 {

    font-family: Georgia, serif;

    font-size: clamp(38px, 5vw, 58px);

    font-weight: normal;

    margin-top: 8px;

}


.section-heading h2 span {

    color: var(--tan);

}


/* =====================================================
   EVENTS GRID
===================================================== */

.events-grid {

    display: grid;

    grid-template-columns:
        repeat(3, 1fr);

    gap: 22px;

}


/* EVENT CARD */

.event-card {

    background: var(--dark2);

    border: 1px solid rgba(238,226,209,.09);

    overflow: hidden;

    transition: .3s;

}


.event-card:hover {

    transform: translateY(-7px);

    border-color: rgba(200,173,143,.35);

}


/* EVENT IMAGE */

.event-image {

    height: 340px;

    background: var(--brown);

    overflow: hidden;

}


.event-image img {

    width: 100%;

    height: 100%;

    object-fit: cover;

    transition: .5s;

}


.event-card:hover .event-image img {

    transform: scale(1.04);

}


/* IMAGE FALLBACK */

.event-placeholder {

    width: 100%;

    height: 100%;

    display: flex;

    flex-direction: column;

    align-items: center;

    justify-content: center;

    color: var(--tan);

}


.event-placeholder i {

    font-size: 45px;

    margin-bottom: 15px;

}


/* EVENT CONTENT */

.event-content {

    padding: 24px;

}


.event-date {

    color: var(--orange);

    font-size: 9px;

    font-weight: bold;

    text-transform: uppercase;

    letter-spacing: 1.5px;

}


.event-content h3 {

    font-family: Georgia, serif;

    font-size: 25px;

    font-weight: normal;

    margin: 8px 0 10px;

}


.event-content p {

    color: #918278;

    font-size: 11px;

    line-height: 1.7;

}


/* EVENT INFO */

.event-info {

    display: flex;

    gap: 18px;

    margin-top: 18px;

    padding-top: 16px;

    border-top: 1px solid rgba(238,226,209,.08);

}


.event-info span {

    color: #b8a99d;

    font-size: 9px;

}


.event-info i {

    color: var(--tan);

    margin-right: 5px;

}


/* =====================================================
   FEATURED EVENT
===================================================== */

.featured {

    background: var(--cream);

    color: var(--brown);

    padding: 90px 5%;

}


.featured-container {

    max-width: 1250px;

    margin: auto;

    display: grid;

    grid-template-columns: 1.2fr 1fr;

    min-height: 480px;

}


.featured-image {

    background:

        url("images/events/featured-event.jpg")
        center/cover no-repeat;

}


.featured-content {

    background: var(--brown);

    color: var(--cream);

    padding: 60px;

    display: flex;

    flex-direction: column;

    justify-content: center;

}


.featured-content small {

    color: var(--tan);

    font-size: 9px;

    font-weight: bold;

    text-transform: uppercase;

    letter-spacing: 3px;

}


.featured-content h2 {

    font-family: Georgia, serif;

    font-size: clamp(38px, 5vw, 58px);

    font-weight: normal;

    line-height: 1;

    margin: 15px 0 20px;

}


.featured-content p {

    color: #b9aaa0;

    font-size: 12px;

    line-height: 1.8;

    max-width: 450px;

}


/* BUTTON */

.event-button {

    display: inline-block;

    width: fit-content;

    margin-top: 28px;

    background: var(--orange);

    color: white;

    padding: 13px 20px;

    font-size: 9px;

    font-weight: bold;

    text-transform: uppercase;

    letter-spacing: 1px;

    transition: .25s;

}


.event-button:hover {

    background: var(--orange-dark);

}


/* =====================================================
   PAST EVENTS
===================================================== */

.past-events {

    background: var(--brown);

    padding: 90px 5%;

}


.past-grid {

    display: grid;

    grid-template-columns:
        repeat(4, 1fr);

    gap: 15px;

}


.past-image {

    height: 260px;

    overflow: hidden;

}


.past-image img {

    height: 100%;

    object-fit: cover;

    transition: .5s;

}


.past-image:hover img {

    transform: scale(1.05);

}


/* =====================================================
   FOOTER
===================================================== */

footer {

    background: #120e0c;

    padding: 55px 5% 20px;

}


.footer-content {

    max-width: 1200px;

    margin: auto;

    display: grid;

    grid-template-columns: 2fr 1fr 1fr;

    gap: 50px;

    padding-bottom: 40px;

}


.footer-brand h2 {

    font-family: Georgia, serif;

    font-size: 28px;

    font-weight: normal;

    margin-bottom: 10px;

}


.footer-brand p {

    max-width: 320px;

    color: #766b63;

    font-size: 10px;

    line-height: 1.8;

}


.footer-column h4 {

    color: var(--tan);

    font-size: 9px;

    text-transform: uppercase;

    letter-spacing: 2px;

    margin-bottom: 16px;

}


.footer-column a {

    display: block;

    color: #786d66;

    font-size: 10px;

    margin-bottom: 10px;

}


.footer-bottom {

    border-top: 1px solid rgba(238,226,209,.08);

    padding-top: 18px;

    text-align: center;

    color: #514943;

    font-size: 9px;

}


/* =====================================================
   MOBILE
===================================================== */

@media(max-width: 900px) {

    nav {
        display: none;
    }

    .events-grid {
        grid-template-columns: 1fr 1fr;
    }

    .featured-container {
        grid-template-columns: 1fr;
    }

    .featured-image {
        min-height: 400px;
    }

    .past-grid {
        grid-template-columns: 1fr 1fr;
    }

    .footer-content {
        grid-template-columns: 1fr 1fr;
    }

}


@media(max-width: 600px) {

    header {
        height: 70px;
        padding: 0 4%;
    }

    .logo img {
        width: 38px;
        height: 38px;
    }

    .logo h2 {
        font-size: 17px;
    }

    .login,
    .register {
        display: none;
    }

    .events-hero {
        min-height: 430px;
    }

    .events-hero h1 {
        font-size: 65px;
    }

    .events-section {
        padding: 65px 4%;
    }

    .events-grid {
        grid-template-columns: 1fr;
    }

    .event-image {
        height: 380px;
    }

    .featured {
        padding: 65px 4%;
    }

    .featured-container {
        display: block;
    }

    .featured-image {
        min-height: 350px;
    }

    .featured-content {
        padding: 40px 25px;
    }

    .past-events {
        padding: 65px 4%;
    }

    .past-grid {
        grid-template-columns: 1fr 1fr;
    }

    .past-image {
        height: 200px;
    }

    .footer-content {
        grid-template-columns: 1fr;
    }

}

</style>

</head>


<body>


<!-- =====================================================
     HEADER
===================================================== -->

<header>


<div class="logo">

<img src="images/logo.png" alt="Monate">

<h2>

Monate

<span>Chicken & Steak</span>

</h2>

</div>


<nav>

<a href="index.php">
Home
</a>

<a href="menu.php">
Menu
</a>

<a href="about.php">
About
</a>

<a href="events.php">
Events
</a>

<a href="contact.php">
Contact
</a>

</nav>


<div class="right">


<a href="cart.php" class="cart">

<i class="fas fa-shopping-bag"></i>

<span>

<?php

echo isset($_SESSION['cart'])
    ? count($_SESSION['cart'])
    : 0;

?>

</span>

</a>


<?php if(isset($_SESSION['user'])): ?>

<a href="profile.php" class="login">
Account
</a>

<a href="logout.php" class="register">
Logout
</a>

<?php else: ?>

<a href="login.php" class="login">
Login
</a>

<a href="register.php" class="register">
Register
</a>

<?php endif; ?>


</div>

</header>



<!-- =====================================================
     HERO
===================================================== -->

<section class="events-hero">

<div class="events-hero-content">

<small>
Monate City Lifestyle Village
</small>

<h1>

Good Food.<br>

<span>Good Times.</span>

</h1>

<p>

Discover what's happening at Monate.
From live entertainment and special occasions
to unforgettable weekends with good people.

</p>

</div>

</section>



<!-- =====================================================
     INTRO
===================================================== -->

<section class="intro">

<small>
What's Happening
</small>

<h2>

Experience <span>Monate</span>

</h2>

<p>

There's always something happening at Monate.
Keep up with upcoming experiences, entertainment
and special events.

</p>

</section>



<!-- =====================================================
     UPCOMING EVENTS
===================================================== -->

<section class="events-section">

<div class="container">


<div class="section-heading">

<small>
Coming Up
</small>

<h2>

Upcoming <span>Events</span>

</h2>

</div>


<div class="events-grid">


<?php foreach($events as $event): ?>


<div class="event-card">


<div class="event-image">


<?php

$imagePath = "images/events/" . $event["image"];

if (
    !empty($event["image"]) &&
    file_exists($imagePath)
) {

?>

<img

src="<?php echo htmlspecialchars($imagePath); ?>"

alt="<?php echo htmlspecialchars($event["title"]); ?>"

>

<?php

} else {

?>

<div class="event-placeholder">

<i class="fa-solid fa-calendar-star"></i>

<span>
Event image coming soon
</span>

</div>

<?php

}

?>


</div>


<div class="event-content">


<div class="event-date">

<?php echo htmlspecialchars($event["date"]); ?>

</div>


<h3>

<?php echo htmlspecialchars($event["title"]); ?>

</h3>


<p>

<?php echo htmlspecialchars($event["description"]); ?>

</p>


<div class="event-info">

<span>

<i class="fa-regular fa-clock"></i>

<?php echo htmlspecialchars($event["time"]); ?>

</span>


<span>

<i class="fa-solid fa-location-dot"></i>

Monate

</span>

</div>


</div>


</div>


<?php endforeach; ?>


</div>

</div>

</section>



<!-- =====================================================
     FEATURED EVENT
===================================================== -->

<section class="featured">

<div class="featured-container">


<div class="featured-image">

</div>


<div class="featured-content">

<small>
The Monate Experience
</small>

<h2>

More Than<br>

Just Food.

</h2>

<p>

Monate is about the complete experience —
great food, music, entertainment, people
and an atmosphere worth coming back to.

</p>

<a href="contact.php" class="event-button">

Get In Touch

</a>

</div>


</div>

</section>



<!-- =====================================================
     PAST EVENTS
===================================================== -->

<section class="past-events">

<div class="container">


<div class="section-heading">

<small>
Moments at Monate
</small>

<h2>

Past <span>Events</span>

</h2>

</div>


<div class="past-grid">


<div class="past-image">

<img

src="images/events/past-1.jpg"

alt="Monate event"

>

</div>


<div class="past-image">

<img

src="images/events/past-2.jpg"

alt="Monate event"

>

</div>


<div class="past-image">

<img

src="images/events/past-3.jpg"

alt="Monate event"

>

</div>


<div class="past-image">

<img

src="images/events/past-4.jpg"

alt="Monate event"

>

</div>


</div>

</div>

</section>



<!-- =====================================================
     FOOTER
===================================================== -->

<footer>


<div class="footer-content">


<div class="footer-brand">

<h2>
MONATE
</h2>

<p>

Monate City Lifestyle Village —
good food, good people and good times.

</p>

</div>


<div class="footer-column">

<h4>
Explore
</h4>

<a href="index.php">
Home
</a>

<a href="menu.php">
Menu
</a>

<a href="about.php">
About
</a>

<a href="events.php">
Events
</a>

<a href="contact.php">
Contact
</a>

</div>


<div class="footer-column">

<h4>
Connect
</h4>

<a href="contact.php">
Contact Us
</a>

<a href="#">
Instagram
</a>

<a href="#">
Facebook
</a>

</div>


</div>


<div class="footer-bottom">

© <?php echo date("Y"); ?>

Monate City Lifestyle Village.
All Rights Reserved.

</div>


</footer>


</body>

</html>