<?php
session_start();

/*
|--------------------------------------------------------------------------
| MONATE CITY LIFESTYLE VILLAGE
| Homepage redesign
|--------------------------------------------------------------------------
| This page does NOT change or depend on your existing database.
| Your existing menu.php, cart.php, login.php etc. remain untouched.
|--------------------------------------------------------------------------
*/
?>

<!DOCTYPE html>
<html lang="en">
<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Monate City Lifestyle Village</title>

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

<link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Playfair+Display:wght@500;600;700;800&display=swap" rel="stylesheet">

<link rel="stylesheet"
href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

<style>

/* =========================================================
   COLOURS
   Based around the actual food photography
========================================================= */

:root {

    --dark: #171310;
    --dark-2: #211a16;
    --brown: #34251e;
    --brown-light: #5a4032;

    --cream: #eee2d1;
    --cream-light: #f7f0e6;

    --beige: #cdb89c;

    --terracotta: #a9653f;
    --terracotta-dark: #874c30;

    --olive: #62684a;

    --white: #fffaf3;
    --muted: #a99a8c;

}


/* =========================================================
   RESET
========================================================= */

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html {
    scroll-behavior: smooth;
}

body {

    font-family: 'DM Sans', sans-serif;

    background: var(--dark);

    color: var(--white);

    line-height: 1.6;

}

img {

    width: 100%;

    display: block;

}

a {

    color: inherit;

    text-decoration: none;

}

.container {

    width: 90%;

    max-width: 1320px;

    margin: auto;

}


/* =========================================================
   NAVBAR
========================================================= */

header {

    position: fixed;

    top: 0;

    left: 0;

    width: 100%;

    z-index: 999;

    background: rgba(23,19,16,.88);

    backdrop-filter: blur(16px);

    border-bottom: 1px solid rgba(238,226,209,.10);

}

.navbar {

    height: 82px;

    display: flex;

    align-items: center;

    justify-content: space-between;

}

.brand {

    display: flex;

    align-items: center;

    gap: 12px;

}

.brand-mark {

    width: 42px;

    height: 42px;

    border: 1px solid var(--beige);

    display: flex;

    align-items: center;

    justify-content: center;

    font-family: 'Playfair Display', serif;

    font-size: 20px;

    color: var(--cream);

}

.brand-text {

    line-height: 1;

}

.brand-text strong {

    display: block;

    font-family: 'Playfair Display', serif;

    font-size: 21px;

    letter-spacing: 1px;

}

.brand-text span {

    display: block;

    color: var(--beige);

    font-size: 8px;

    letter-spacing: 2px;

    text-transform: uppercase;

    margin-top: 5px;

}

.nav {

    display: flex;

    align-items: center;

    gap: 30px;

}

.nav a {

    font-size: 10px;

    font-weight: 700;

    text-transform: uppercase;

    letter-spacing: 1px;

    color: #ded2c4;

    transition: .25s;

}

.nav a:hover {

    color: var(--beige);

}

.nav-actions {

    display: flex;

    align-items: center;

    gap: 10px;

}

.circle-btn {

    width: 40px;

    height: 40px;

    border: 1px solid rgba(238,226,209,.2);

    border-radius: 50%;

    display: flex;

    align-items: center;

    justify-content: center;

    font-size: 12px;

    transition: .25s;

}

.circle-btn:hover {

    background: var(--terracotta);

    border-color: var(--terracotta);

}

.order-btn {

    background: var(--terracotta);

    padding: 12px 19px;

    font-size: 10px;

    font-weight: 800;

    text-transform: uppercase;

    letter-spacing: .7px;

    transition: .25s;

}

.order-btn:hover {

    background: var(--terracotta-dark);

}


/* =========================================================
   HERO
========================================================= */

.hero {

    min-height: 100vh;

    position: relative;

    display: flex;

    align-items: center;

    overflow: hidden;

    background:

        linear-gradient(
            90deg,
            rgba(23,19,16,.96) 0%,
            rgba(23,19,16,.78) 42%,
            rgba(23,19,16,.25) 100%
        ),

        url("pics/WhatsApp%20Image%202026-07-30%20at%2012.59.24%20(3).jpeg")
        center/cover no-repeat;

}

.hero::after {

    content: "";

    position: absolute;

    right: -180px;

    bottom: -220px;

    width: 550px;

    height: 550px;

    border: 1px solid rgba(205,184,156,.25);

    border-radius: 50%;

}

.hero-content {

    position: relative;

    z-index: 2;

    max-width: 760px;

    padding-top: 70px;

}

.eyebrow {

    display: flex;

    align-items: center;

    gap: 12px;

    color: var(--beige);

    font-size: 10px;

    font-weight: 800;

    letter-spacing: 3px;

    text-transform: uppercase;

    margin-bottom: 22px;

}

.eyebrow::before {

    content: "";

    width: 38px;

    height: 1px;

    background: var(--terracotta);

}

.hero h1 {

    font-family: 'Playfair Display', serif;

    font-size: clamp(60px, 8vw, 108px);

    line-height: .92;

    font-weight: 700;

    margin-bottom: 25px;

}

.hero h1 span {

    color: var(--beige);

}

.hero p {

    max-width: 590px;

    color: #d7cabc;

    font-size: 15px;

    margin-bottom: 34px;

}

.hero-buttons {

    display: flex;

    flex-wrap: wrap;

    gap: 12px;

}

.hero-primary {

    background: var(--terracotta);

    padding: 15px 24px;

    font-size: 10px;

    font-weight: 800;

    text-transform: uppercase;

    letter-spacing: .8px;

}

.hero-secondary {

    border: 1px solid rgba(238,226,209,.4);

    padding: 14px 23px;

    font-size: 10px;

    font-weight: 800;

    text-transform: uppercase;

    letter-spacing: .8px;

}

.hero-secondary:hover {

    background: var(--cream);

    color: var(--dark);

}


/* =========================================================
   INTRO STRIP
========================================================= */

.intro-strip {

    background: var(--cream);

    color: var(--brown);

    padding: 23px 0;

}

.intro-strip-grid {

    display: grid;

    grid-template-columns: repeat(4, 1fr);

    gap: 15px;

}

.strip-item {

    display: flex;

    align-items: center;

    justify-content: center;

    gap: 10px;

    font-size: 10px;

    font-weight: 800;

    text-transform: uppercase;

    letter-spacing: 1px;

}

.strip-item i {

    color: var(--terracotta);

}


/* =========================================================
   ABOUT
========================================================= */

.about {

    background: var(--cream-light);

    color: var(--brown);

    padding: 115px 0;

}

.about-grid {

    display: grid;

    grid-template-columns: 1fr 1fr;

    gap: 80px;

    align-items: center;

}

.about-images {

    display: grid;

    grid-template-columns: 1fr .75fr;

    gap: 14px;

    align-items: end;

}

.about-images img {

    height: 510px;

    object-fit: cover;

}

.about-images img:last-child {

    height: 350px;

}

.section-label {

    color: var(--terracotta);

    font-size: 10px;

    font-weight: 900;

    letter-spacing: 3px;

    text-transform: uppercase;

    margin-bottom: 12px;

}

.about h2 {

    font-family: 'Playfair Display', serif;

    font-size: 55px;

    line-height: 1.03;

    margin-bottom: 23px;

}

.about h2 span {

    color: var(--terracotta);

}

.about p {

    color: #75665a;

    font-size: 14px;

    margin-bottom: 16px;

}

.text-link {

    display: inline-flex;

    align-items: center;

    gap: 9px;

    margin-top: 12px;

    color: var(--terracotta);

    font-size: 10px;

    font-weight: 900;

    text-transform: uppercase;

    letter-spacing: 1px;

}


/* =========================================================
   MENU PREVIEW
========================================================= */

.menu-section {

    background: var(--dark);

    padding: 115px 0;

}

.section-heading {

    text-align: center;

    max-width: 700px;

    margin: 0 auto 55px;

}

.section-heading .section-label {

    color: var(--beige);

}

.section-heading h2 {

    font-family: 'Playfair Display', serif;

    font-size: 55px;

    line-height: 1;

    margin-bottom: 16px;

}

.section-heading h2 span {

    color: var(--beige);

}

.section-heading p {

    color: var(--muted);

    font-size: 13px;

}


/* MENU IMAGE GRID */

.menu-grid {

    display: grid;

    grid-template-columns: repeat(4, 1fr);

    gap: 16px;

}

.menu-card {

    background: var(--dark-2);

    border: 1px solid rgba(238,226,209,.08);

    overflow: hidden;

    transition: .3s;

}

.menu-card:hover {

    transform: translateY(-7px);

    border-color: rgba(205,184,156,.35);

}

.menu-image {

    height: 285px;

    overflow: hidden;

    position: relative;

}

.menu-image img {

    width: 100%;

    height: 100%;

    object-fit: cover;

    transition: .5s;

}

.menu-card:hover .menu-image img {

    transform: scale(1.06);

}

.menu-number {

    position: absolute;

    top: 12px;

    left: 12px;

    background: rgba(23,19,16,.85);

    color: var(--cream);

    padding: 7px 10px;

    font-size: 9px;

    font-weight: 800;

    letter-spacing: 1px;

}

.menu-info {

    padding: 20px;

}

.menu-info h3 {

    font-family: 'Playfair Display', serif;

    font-size: 21px;

    margin-bottom: 5px;

}

.menu-info p {

    color: #93857a;

    font-size: 10px;

}

.menu-button {

    margin-top: 14px;

    display: inline-block;

    color: var(--beige);

    font-size: 9px;

    font-weight: 900;

    text-transform: uppercase;

    letter-spacing: 1px;

}


/* =========================================================
   LIFESTYLE FEATURE
========================================================= */

.lifestyle {

    min-height: 650px;

    display: flex;

    align-items: center;

    position: relative;

    background:

        linear-gradient(
            90deg,
            rgba(31,24,20,.96),
            rgba(31,24,20,.68),
            rgba(31,24,20,.25)
        ),

        url("pics/WhatsApp%20Image%202026-07-30%20at%2012.59.22%20(2).jpeg")
        center/cover no-repeat;

}

.lifestyle-content {

    max-width: 680px;

}

.lifestyle h2 {

    font-family: 'Playfair Display', serif;

    font-size: 65px;

    line-height: .98;

    margin-bottom: 22px;

}

.lifestyle h2 span {

    color: var(--beige);

}

.lifestyle p {

    max-width: 570px;

    color: #d6c9bc;

    font-size: 14px;

    margin-bottom: 30px;

}


/* =========================================================
   EXPERIENCE
========================================================= */

.experience {

    background: var(--cream);

    color: var(--brown);

    padding: 105px 0;

}

.experience-grid {

    display: grid;

    grid-template-columns: repeat(3, 1fr);

    gap: 16px;

}

.experience-card {

    background: #f8f1e7;

    padding: 34px 28px;

    min-height: 210px;

    border-bottom: 3px solid transparent;

    transition: .25s;

}

.experience-card:hover {

    border-color: var(--terracotta);

    transform: translateY(-5px);

}

.experience-card i {

    color: var(--terracotta);

    font-size: 24px;

    margin-bottom: 20px;

}

.experience-card h3 {

    font-family: 'Playfair Display', serif;

    font-size: 23px;

    margin-bottom: 8px;

}

.experience-card p {

    color: #75675c;

    font-size: 11px;

}


/* =========================================================
   EVENTS
========================================================= */

.events {

    background: var(--brown);

    padding: 110px 0;

}

.event-grid {

    display: grid;

    grid-template-columns: 1.5fr 1fr 1fr;

    gap: 15px;

}

.event {

    min-height: 400px;

    position: relative;

    display: flex;

    align-items: flex-end;

    overflow: hidden;

    background-size: cover;

    background-position: center;

}

.event:nth-child(1) {

    background-image:

        linear-gradient(
            transparent,
            rgba(23,19,16,.95)
        ),

        url("pics/WhatsApp%20Image%202026-07-30%20at%2012.59.23%20(2).jpeg");

}

.event:nth-child(2) {

    background-image:

        linear-gradient(
            transparent,
            rgba(23,19,16,.95)
        ),

        url("pics/WhatsApp%20Image%202026-07-30%20at%2012.59.24.jpeg");

}

.event:nth-child(3) {

    background-image:

        linear-gradient(
            transparent,
            rgba(23,19,16,.95)
        ),

        url("pics/WhatsApp%20Image%202026-07-30%20at%2012.59.23%20(1).jpeg");

}

.event-content {

    padding: 25px;

}

.event-tag {

    display: inline-block;

    background: var(--terracotta);

    padding: 6px 9px;

    font-size: 8px;

    font-weight: 900;

    text-transform: uppercase;

    letter-spacing: 1px;

    margin-bottom: 10px;

}

.event h3 {

    font-family: 'Playfair Display', serif;

    font-size: 27px;

    margin-bottom: 6px;

}

.event p {

    color: #c3b4a7;

    font-size: 10px;

}


/* =========================================================
   CTA
========================================================= */

.cta {

    background: var(--terracotta);

    color: var(--white);

    padding: 90px 0;

    text-align: center;

}

.cta h2 {

    font-family: 'Playfair Display', serif;

    font-size: 62px;

    line-height: 1;

    margin-bottom: 14px;

}

.cta p {

    font-size: 13px;

    margin-bottom: 27px;

}

.cta a {

    display: inline-block;

    background: var(--dark);

    padding: 15px 25px;

    font-size: 10px;

    font-weight: 900;

    text-transform: uppercase;

    letter-spacing: 1px;

}


/* =========================================================
   LOCATION
========================================================= */

.location {

    background: var(--cream-light);

    color: var(--brown);

    padding: 110px 0;

}

.location-grid {

    display: grid;

    grid-template-columns: 1fr 1fr;

    gap: 70px;

    align-items: center;

}

.location h2 {

    font-family: 'Playfair Display', serif;

    font-size: 55px;

    line-height: 1;

    margin-bottom: 20px;

}

.location h2 span {

    color: var(--terracotta);

}

.location p {

    color: #74665b;

    font-size: 13px;

    margin-bottom: 22px;

}

.address {

    padding: 20px;

    border-left: 3px solid var(--terracotta);

    background: #fff;

    margin-bottom: 22px;

}

.address strong {

    display: block;

    font-size: 13px;

    margin-bottom: 5px;

}

.address span {

    color: #776a5e;

    font-size: 11px;

}

.map-box {

    min-height: 400px;

    background:

        linear-gradient(
            135deg,
            #49362b,
            #211914
        );

    display: flex;

    align-items: center;

    justify-content: center;

    text-align: center;

}

.map-box i {

    color: var(--beige);

    font-size: 42px;

    margin-bottom: 15px;

}

.map-box h3 {

    font-family: 'Playfair Display', serif;

    font-size: 24px;

}

.map-box p {

    color: #b5a497;

    margin-top: 5px;

}


/* =========================================================
   FOOTER
========================================================= */

footer {

    background: #0e0c0a;

    padding: 65px 0 20px;

}

.footer-grid {

    display: grid;

    grid-template-columns: 1.5fr 1fr 1fr 1fr;

    gap: 45px;

    padding-bottom: 45px;

}

.footer-brand-name {

    font-family: 'Playfair Display', serif;

    font-size: 28px;

    margin-bottom: 10px;

}

.footer-brand p {

    color: #82766b;

    font-size: 11px;

    max-width: 290px;

}

.footer-col h4 {

    color: var(--beige);

    font-size: 9px;

    letter-spacing: 2px;

    text-transform: uppercase;

    margin-bottom: 17px;

}

.footer-col a {

    display: block;

    color: #91847a;

    font-size: 10px;

    margin-bottom: 9px;

}

.footer-col a:hover {

    color: var(--cream);

}

.footer-bottom {

    border-top: 1px solid rgba(238,226,209,.08);

    padding-top: 20px;

    text-align: center;

    color: #625850;

    font-size: 9px;

}


/* =========================================================
   MOBILE
========================================================= */

@media(max-width:1000px) {

    .nav {

        display: none;

    }

    .menu-grid {

        grid-template-columns: repeat(2, 1fr);

    }

    .about-grid,

    .location-grid {

        grid-template-columns: 1fr;

    }

    .experience-grid {

        grid-template-columns: repeat(2, 1fr);

    }

    .event-grid {

        grid-template-columns: 1fr 1fr;

    }

    .event:first-child {

        grid-column: 1 / -1;

    }

    .footer-grid {

        grid-template-columns: repeat(2, 1fr);

    }

}


@media(max-width:600px) {

    .navbar {

        height: 70px;

    }

    .brand-mark {

        width: 35px;

        height: 35px;

    }

    .brand-text strong {

        font-size: 17px;

    }

    .nav-actions .order-btn {

        display: none;

    }

    .hero {

        min-height: 90vh;

    }

    .hero h1 {

        font-size: 57px;

    }

    .hero p {

        font-size: 13px;

    }

    .intro-strip-grid {

        grid-template-columns: repeat(2, 1fr);

        gap: 18px;

    }

    .about,

    .menu-section,

    .experience,

    .events,

    .location {

        padding: 75px 0;

    }

    .about h2,

    .section-heading h2,

    .location h2 {

        font-size: 40px;

    }

    .about-images {

        grid-template-columns: 1fr 1fr;

    }

    .about-images img {

        height: 350px;

    }

    .about-images img:last-child {

        height: 240px;

    }

    .menu-grid {

        grid-template-columns: 1fr;

    }

    .menu-image {

        height: 330px;

    }

    .lifestyle {

        min-height: 600px;

    }

    .lifestyle h2 {

        font-size: 47px;

    }

    .experience-grid {

        grid-template-columns: 1fr;

    }

    .event-grid {

        grid-template-columns: 1fr;

    }

    .event:first-child {

        grid-column: auto;

    }

    .cta h2 {

        font-size: 45px;

    }

    .footer-grid {

        grid-template-columns: 1fr;

    }

}

</style>

</head>


<body>


<!-- =========================================================
     NAVIGATION
========================================================= -->

<header>

<div class="container navbar">


<a href="index.php" class="brand">

<div class="brand-mark">

M

</div>

<div class="brand-text">

<strong>MONATE</strong>

<span>City Lifestyle Village</span>

</div>

</a>


<nav class="nav">

<a href="index.php">Home</a>

<a href="menu.php">Menu</a>

<a href="#experience">Experience</a>

<a href="#events">Events</a>

<a href="#location">Find Us</a>

</nav>


<div class="nav-actions">


<a href="cart.php" class="circle-btn">

<i class="fa-solid fa-bag-shopping"></i>

</a>


<a href="login.php" class="circle-btn">

<i class="fa-solid fa-user"></i>

</a>


<a href="menu.php" class="order-btn">

Order Now

</a>


</div>

</div>

</header>



<!-- =========================================================
     HERO
========================================================= -->

<section class="hero">

<div class="container">

<div class="hero-content">


<div class="eyebrow">

Monate City Lifestyle Village

</div>


<h1>

Where Good Food
<br>

Meets The
<span>Good Life.</span>

</h1>


<p>

Come for the food. Stay for the atmosphere.
Enjoy good company, good music and the moments
that make Monate City more than just a place to eat.

</p>


<div class="hero-buttons">

<a href="menu.php" class="hero-primary">

<i class="fa-solid fa-utensils"></i>

Explore Menu

</a>


<a href="#experience" class="hero-secondary">

Discover Monate City

</a>

</div>


</div>

</div>

</section>



<!-- =========================================================
     QUICK STRIP
========================================================= -->

<section class="intro-strip">

<div class="container intro-strip-grid">


<div class="strip-item">

<i class="fa-solid fa-drumstick-bite"></i>

Food

</div>


<div class="strip-item">

<i class="fa-solid fa-music"></i>

Music

</div>


<div class="strip-item">

<i class="fa-solid fa-futbol"></i>

Live Sports

</div>


<div class="strip-item">

<i class="fa-solid fa-people-group"></i>

Family & Friends

</div>


</div>

</section>



<!-- =========================================================
     ABOUT
========================================================= -->

<section class="about">

<div class="container about-grid">


<div class="about-images">


<img
src="pics/WhatsApp%20Image%202026-07-30%20at%2012.59.22.jpeg"
alt="Monate food">


<img
src="pics/WhatsApp%20Image%202026-07-30%20at%2012.59.23.jpeg"
alt="Monate food">

</div>


<div>


<div class="section-label">

Welcome To Monate

</div>


<h2>

More Than Food.
<br>

<span>It's The Experience.</span>

</h2>


<p>

Monate City Lifestyle Village brings food,
entertainment and people together in one place.

</p>


<p>

Whether you're meeting friends, spending time
with family, catching the game or simply looking
for somewhere to enjoy a great meal, there's always
a reason to come through.

</p>


<a href="about.php" class="text-link">

Discover Our Story

<i class="fa-solid fa-arrow-right"></i>

</a>


</div>

</div>

</section>



<!-- =========================================================
     MENU
========================================================= -->

<section class="menu-section" id="menu">

<div class="container">


<div class="section-heading">

<div class="section-label">

From Our Kitchen

</div>


<h2>

Good Food.
<span>Done Right.</span>

</h2>


<p>

A look at some of what you can expect when you
sit down at Monate City.

</p>

</div>


<div class="menu-grid">


<!-- ITEM 01 -->

<div class="menu-card">

<div class="menu-image">

<img
src="pics/WhatsApp%20Image%202026-07-30%20at%2012.59.22%20(1).jpeg"
alt="Monate menu item">

<div class="menu-number">

01

</div>

</div>

<div class="menu-info">

<h3>

Monate Favourite

</h3>

<p>

One of the flavours you'll find at Monate City.

</p>

<a href="menu.php" class="menu-button">

View Menu →

</a>

</div>

</div>


<!-- ITEM 02 -->

<div class="menu-card">

<div class="menu-image">

<img
src="pics/WhatsApp%20Image%202026-07-30%20at%2012.59.22%20(2).jpeg"
alt="Monate menu item">

<div class="menu-number">

02

</div>

</div>

<div class="menu-info">

<h3>

From Our Kitchen

</h3>

<p>

Made for sharing, enjoying and coming back for.

</p>

<a href="menu.php" class="menu-button">

View Menu →

</a>

</div>

</div>


<!-- ITEM 03 -->

<div class="menu-card">

<div class="menu-image">

<img
src="pics/WhatsApp%20Image%202026-07-30%20at%2012.59.22%20(3).jpeg"
alt="Monate menu item">

<div class="menu-number">

03

</div>

</div>

<div class="menu-info">

<h3>

The Monate Taste

</h3>

<p>

Food that brings people together.

</p>

<a href="menu.php" class="menu-button">

View Menu →

</a>

</div>

</div>


<!-- ITEM 04 -->

<div class="menu-card">

<div class="menu-image">

<img
src="pics/WhatsApp%20Image%202026-07-30%20at%2012.59.23.jpeg"
alt="Monate menu item">

<div class="menu-number">

04

</div>

</div>

<div class="menu-info">

<h3>

Good Food

</h3>

<p>

Come hungry. Leave satisfied.

</p>

<a href="menu.php" class="menu-button">

View Menu →

</a>

</div>

</div>


</div>


<div style="text-align:center;margin-top:45px;">

<a href="menu.php" class="order-btn">

View Full Menu

<i class="fa-solid fa-arrow-right"></i>

</a>

</div>


</div>

</section>



<!-- =========================================================
     LIFESTYLE
========================================================= -->

<section class="lifestyle">

<div class="container">

<div class="lifestyle-content">


<div class="section-label">

The Monate Experience

</div>


<h2>

Come For The Food.
<br>

<span>Stay For The Vibe.</span>

</h2>


<p>

There's more to Monate City than what's on your
plate. It's the atmosphere, the people, the music,
the games and the memories you make while you're here.

</p>


<a href="#events" class="order-btn">

What's Happening

<i class="fa-solid fa-arrow-right"></i>

</a>


</div>

</div>

</section>



<!-- =========================================================
     EXPERIENCE
========================================================= -->

<section class="experience" id="experience">

<div class="container">


<div class="section-heading">

<div class="section-label">

Why Monate City

</div>


<h2>

There's Always A
<span>Reason To Stay.</span>

</h2>


<p>

More than a restaurant. More than a meal.

</p>

</div>


<div class="experience-grid">


<div class="experience-card">

<i class="fa-solid fa-tv"></i>

<h3>

Big Screen Sports

</h3>

<p>

Watch the biggest games with the atmosphere
of a crowd around you.

</p>

</div>


<div class="experience-card">

<i class="fa-solid fa-music"></i>

<h3>

Music & Entertainment

</h3>

<p>

Good food gets even better when the atmosphere
is right.

</p>

</div>


<div class="experience-card">

<i class="fa-solid fa-people-group"></i>

<h3>

Family & Friends

</h3>

<p>

A place to bring your people together and
create memories.

</p>

</div>


<div class="experience-card">

<i class="fa-solid fa-martini-glass"></i>

<h3>

Food & Drinks

</h3>

<p>

Enjoy your favourite meals and drinks while
taking in the Monate atmosphere.

</p>

</div>


<div class="experience-card">

<i class="fa-solid fa-calendar-days"></i>

<h3>

Events

</h3>

<p>

Keep an eye out for special events, promotions
and experiences.

</p>

</div>


<div class="experience-card">

<i class="fa-solid fa-car"></i>

<h3>

Secure Parking

</h3>

<p>

Arrive, settle in and enjoy your time at
Monate City.

</p>

</div>


</div>

</div>

</section>



<!-- =========================================================
     EVENTS
========================================================= -->

<section class="events" id="events">

<div class="container">


<div class="section-heading">

<div class="section-label">

What's Happening

</div>


<h2>

Make It A
<span>Monate Moment.</span>

</h2>


<p>

Something for the weekend, something for the
family and something worth coming back for.

</p>

</div>


<div class="event-grid">


<div class="event">

<div class="event-content">

<div class="event-tag">

Live Sports

</div>

<h3>

Watch The Game With Us

</h3>

<p>

Big screens. Good food.
Great atmosphere.

</p>

</div>

</div>


<div class="event">

<div class="event-content">

<div class="event-tag">

Weekend

</div>

<h3>

Weekend Vibes

</h3>

<p>

Music, food and good company.

</p>

</div>

</div>


<div class="event">

<div class="event-content">

<div class="event-tag">

Family

</div>

<h3>

Sunday At Monate

</h3>

<p>

Bring the people who matter.

</p>

</div>

</div>


</div>

</div>

</section>



<!-- =========================================================
     CTA
========================================================= -->

<section class="cta">

<div class="container">


<h2>

Your Table Is Waiting.

</h2>


<p>

Good food is better when it's shared.

</p>


<a href="menu.php">

Explore The Menu

<i class="fa-solid fa-arrow-right"></i>

</a>


</div>

</section>



<!-- =========================================================
     LOCATION
========================================================= -->

<section class="location" id="location">

<div class="container location-grid">


<div>


<div class="section-label">

Come Find Us

</div>


<h2>

Welcome To
<span>Monate City.</span>

</h2>


<p>

Ready to experience it yourself?

Come through to Monate City Lifestyle Village
in Lufule, Thohoyandou.

</p>


<div class="address">

<strong>

Monate City Lifestyle Village

</strong>

<span>

Lufule 2, 236 R524,
Tshilungoma, Thohoyandou, 0950

</span>

</div>


<a href="tel:0661844870" class="order-btn">

<i class="fa-solid fa-phone"></i>

066 184 4870

</a>


</div>


<div class="map-box">

<div>

<i class="fa-solid fa-location-dot"></i>

<h3>

Monate City

</h3>

<p>

Lufule 2 • Thohoyandou

</p>

</div>

</div>


</div>

</section>



<!-- =========================================================
     FOOTER
========================================================= -->

<footer>

<div class="container">


<div class="footer-grid">


<div>

<div class="footer-brand-name">

MONATE

</div>

<p>

City Lifestyle Village.

Where food, people,
music and good times come together.

</p>

</div>


<div class="footer-col">

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

<a href="#events">

Events

</a>

</div>


<div class="footer-col">

<h4>

Account

</h4>

<a href="login.php">

Login

</a>

<a href="register.php">

Register

</a>

<a href="cart.php">

Cart

</a>

<a href="profile.php">

My Account

</a>

</div>


<div class="footer-col">

<h4>

Contact

</h4>

<a href="tel:0661844870">

066 184 4870

</a>

<a href="#location">

Lufule 2, Thohoyandou

</a>

<a href="#location">

Find Us

</a>

</div>


</div>


<div class="footer-bottom">

© <?php echo date("Y"); ?>

Monate City Lifestyle Village.

All Rights Reserved.

</div>


</div>

</footer>



</body>
</html>