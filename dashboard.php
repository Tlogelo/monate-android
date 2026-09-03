<?php

session_start();
include("db.php");

/*
|--------------------------------------------------------------------------
| ADMIN ACCESS
|--------------------------------------------------------------------------
*/

if (
    !isset($_SESSION['admin_id']) ||
    !isset($_SESSION['admin_role']) ||
    $_SESSION['admin_role'] !== 'admin'
) {
    header("Location: login.php");
    exit();
}


/*
|--------------------------------------------------------------------------
| HELPER FUNCTIONS
|--------------------------------------------------------------------------
*/

// Check whether a table exists
function tableExists($conn, $table)
{
    $table = mysqli_real_escape_string($conn, $table);

    $result = mysqli_query(
        $conn,
        "SHOW TABLES LIKE '$table'"
    );

    return $result && mysqli_num_rows($result) > 0;
}


// Count records in a table safely
function getCount($conn, $table)
{
    if (!tableExists($conn, $table)) {
        return 0;
    }

    $result = mysqli_query(
        $conn,
        "SELECT COUNT(*) AS total FROM `$table`"
    );

    if ($result) {
        $row = mysqli_fetch_assoc($result);
        return (int)$row['total'];
    }

    return 0;
}


/*
|--------------------------------------------------------------------------
| DATABASE COUNTS
|--------------------------------------------------------------------------
*/

$totalUsers = getCount($conn, "users");

$totalMenuItems = getCount($conn, "menu_items");

$totalCategories = getCount($conn, "categories");

$totalOrders = getCount($conn, "orders");

$totalReviews = getCount($conn, "reviews");


/*
|--------------------------------------------------------------------------
| CURRENT ADMIN
|--------------------------------------------------------------------------
*/

$adminName = $_SESSION['admin_name'] ?? "Administrator";

?>


<!DOCTYPE html>

<html lang="en">

<head>

<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Monate | Admin Dashboard</title>


<link
rel="stylesheet"
href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"
>


<style>

/* =====================================================
   MONATE ADMIN
===================================================== */

:root {

    --dark: #211713;
    --dark2: #2b1d17;
    --dark3: #36251e;

    --cream: #eee2d1;
    --cream-light: #f8f1e7;

    --tan: #c8ad8f;

    --orange: #a9613e;
    --orange-dark: #82452d;

    --border: rgba(238,226,209,.1);

}


/* =====================================================
   RESET
===================================================== */

* {

    margin: 0;

    padding: 0;

    box-sizing: border-box;

}


body {

    background: #18110e;

    color: var(--cream);

    font-family: Arial, Helvetica, sans-serif;

}


a {

    text-decoration: none;

    color: inherit;

}


/* =====================================================
   SIDEBAR
===================================================== */

.sidebar {

    position: fixed;

    left: 0;

    top: 0;

    width: 250px;

    height: 100vh;

    background: var(--dark);

    border-right: 1px solid var(--border);

    padding: 30px 18px;

    z-index: 100;

}


/* LOGO */

.logo {

    text-align: center;

    padding-bottom: 28px;

    border-bottom: 1px solid var(--border);

}


.logo img {

    width: 55px;

    height: 55px;

    object-fit: contain;

    margin-bottom: 8px;

}


.logo h2 {

    font-family: Georgia, serif;

    font-weight: normal;

    font-size: 25px;

}


.logo p {

    color: var(--tan);

    font-size: 7px;

    letter-spacing: 2px;

    text-transform: uppercase;

    margin-top: 4px;

}


/* NAVIGATION */

.sidebar-nav {

    margin-top: 30px;

}


.nav-title {

    color: #675850;

    font-size: 8px;

    text-transform: uppercase;

    letter-spacing: 2px;

    margin: 0 12px 12px;

}


.sidebar-nav a {

    display: flex;

    align-items: center;

    gap: 13px;

    padding: 13px 12px;

    margin-bottom: 4px;

    color: #9b8b80;

    font-size: 10px;

    transition: .25s;

}


.sidebar-nav a i {

    width: 18px;

    text-align: center;

    font-size: 13px;

}


.sidebar-nav a:hover,
.sidebar-nav a.active {

    background: var(--dark3);

    color: var(--cream);

}


.sidebar-nav a.active {

    border-left: 3px solid var(--orange);

}


/* SIDEBAR BOTTOM */

.sidebar-bottom {

    position: absolute;

    left: 18px;

    right: 18px;

    bottom: 25px;

}


.sidebar-bottom a {

    display: flex;

    align-items: center;

    gap: 12px;

    color: #887970;

    font-size: 10px;

    padding: 12px;

}


.sidebar-bottom a:hover {

    color: var(--cream);

}


/* =====================================================
   MAIN
===================================================== */

.main {

    margin-left: 250px;

    min-height: 100vh;

}


/* =====================================================
   TOP BAR
===================================================== */

.topbar {

    height: 80px;

    padding: 0 40px;

    display: flex;

    align-items: center;

    justify-content: space-between;

    background: var(--dark);

    border-bottom: 1px solid var(--border);

}


.page-title h1 {

    font-family: Georgia, serif;

    font-weight: normal;

    font-size: 27px;

}


.page-title p {

    color: #776960;

    font-size: 9px;

    margin-top: 4px;

}


.admin-profile {

    display: flex;

    align-items: center;

    gap: 12px;

}


.admin-avatar {

    width: 36px;

    height: 36px;

    border-radius: 50%;

    background: var(--orange);

    display: flex;

    align-items: center;

    justify-content: center;

    font-family: Georgia, serif;

}


.admin-info {

    text-align: right;

}


.admin-info strong {

    display: block;

    font-size: 10px;

    color: var(--cream);

}


.admin-info span {

    display: block;

    color: var(--tan);

    font-size: 7px;

    text-transform: uppercase;

    letter-spacing: 1px;

    margin-top: 3px;

}


/* =====================================================
   CONTENT
===================================================== */

.content {

    padding: 40px;

    max-width: 1500px;

}


/* =====================================================
   WELCOME
===================================================== */

.welcome {

    background:

        linear-gradient(
            100deg,
            rgba(52,35,28,.98),
            rgba(52,35,28,.7)
        );

    border: 1px solid var(--border);

    padding: 35px;

    margin-bottom: 30px;

    position: relative;

    overflow: hidden;

}


.welcome::after {

    content: "MONATE";

    position: absolute;

    right: -20px;

    bottom: -35px;

    font-family: Georgia, serif;

    font-size: 130px;

    color: rgba(238,226,209,.035);

}


.welcome small {

    color: var(--tan);

    text-transform: uppercase;

    font-size: 8px;

    letter-spacing: 3px;

}


.welcome h2 {

    font-family: Georgia, serif;

    font-weight: normal;

    font-size: 35px;

    margin-top: 8px;

}


.welcome p {

    color: #96877e;

    font-size: 10px;

    margin-top: 8px;

}


/* =====================================================
   STAT CARDS
===================================================== */

.stats {

    display: grid;

    grid-template-columns:
        repeat(4, 1fr);

    gap: 16px;

    margin-bottom: 35px;

}


.stat {

    background: var(--dark2);

    border: 1px solid var(--border);

    padding: 25px;

    position: relative;

    transition: .25s;

}


.stat:hover {

    transform: translateY(-3px);

    border-color: rgba(200,173,143,.25);

}


.stat-icon {

    width: 36px;

    height: 36px;

    background: rgba(169,97,62,.15);

    color: var(--orange);

    display: flex;

    align-items: center;

    justify-content: center;

    margin-bottom: 18px;

}


.stat h3 {

    font-family: Georgia, serif;

    font-size: 30px;

    font-weight: normal;

}


.stat p {

    color: #776960;

    text-transform: uppercase;

    font-size: 8px;

    letter-spacing: 1px;

    margin-top: 5px;

}


/* =====================================================
   SECTION
===================================================== */

.section-title {

    display: flex;

    align-items: end;

    justify-content: space-between;

    margin-bottom: 16px;

}


.section-title h2 {

    font-family: Georgia, serif;

    font-size: 25px;

    font-weight: normal;

}


.section-title span {

    color: var(--tan);

    font-size: 9px;

}


/* =====================================================
   QUICK ACTIONS
===================================================== */

.quick-actions {

    display: grid;

    grid-template-columns:
        repeat(3, 1fr);

    gap: 16px;

    margin-bottom: 35px;

}


.action {

    background: var(--dark2);

    border: 1px solid var(--border);

    padding: 25px;

    display: flex;

    align-items: center;

    gap: 18px;

    transition: .25s;

}


.action:hover {

    background: var(--dark3);

    transform: translateY(-3px);

}


.action-icon {

    width: 45px;

    height: 45px;

    background: var(--orange);

    display: flex;

    align-items: center;

    justify-content: center;

    flex-shrink: 0;

}


.action h3 {

    font-family: Georgia, serif;

    font-size: 17px;

    font-weight: normal;

}


.action p {

    color: #74665e;

    font-size: 9px;

    margin-top: 4px;

}


/* =====================================================
   INFO BOX
===================================================== */

.info-grid {

    display: grid;

    grid-template-columns: 1fr 1fr;

    gap: 16px;

}


.info-box {

    background: var(--dark2);

    border: 1px solid var(--border);

    padding: 25px;

}


.info-box h3 {

    font-family: Georgia, serif;

    font-weight: normal;

    font-size: 20px;

    margin-bottom: 15px;

}


.info-row {

    display: flex;

    justify-content: space-between;

    padding: 12px 0;

    border-bottom: 1px solid var(--border);

}


.info-row:last-child {

    border-bottom: none;

}


.info-row span:first-child {

    color: #81736b;

    font-size: 9px;

}


.info-row span:last-child {

    color: var(--cream);

    font-size: 9px;

    font-weight: bold;

}


/* =====================================================
   MOBILE
===================================================== */

@media(max-width: 1000px) {

    .sidebar {

        width: 210px;

    }

    .main {

        margin-left: 210px;

    }

    .stats {

        grid-template-columns: 1fr 1fr;

    }

}


@media(max-width: 750px) {

    .sidebar {

        position: relative;

        width: 100%;

        height: auto;

        border-right: none;

        border-bottom: 1px solid var(--border);

    }

    .sidebar-bottom {

        display: none;

    }

    .sidebar-nav {

        display: flex;

        flex-wrap: wrap;

        gap: 4px;

    }

    .sidebar-nav a {

        margin: 0;

    }

    .main {

        margin-left: 0;

    }

    .topbar {

        padding: 0 20px;

    }

    .content {

        padding: 25px 20px;

    }

    .quick-actions {

        grid-template-columns: 1fr;

    }

    .info-grid {

        grid-template-columns: 1fr;

    }

}


@media(max-width: 500px) {

    .stats {

        grid-template-columns: 1fr;

    }

    .admin-info {

        display: none;

    }

    .welcome {

        padding: 25px;

    }

    .welcome h2 {

        font-size: 28px;

    }

}

</style>

</head>


<body>


<!-- =====================================================
     SIDEBAR
===================================================== -->

<aside class="sidebar">


<div class="logo">

<img src="images/logo.png" alt="Monate">

<h2>Monate</h2>

<p>Administration</p>

</div>


<nav class="sidebar-nav">


<div class="nav-title">
Main
</div>


<a href="dashboard.php" class="active">

<i class="fa-solid fa-gauge-high"></i>

Dashboard

</a>


<a href="index.php">

<i class="fa-solid fa-globe"></i>

View Website

</a>


<a href="menu.php">

<i class="fa-solid fa-utensils"></i>

Menu

</a>


<a href="events.php">

<i class="fa-solid fa-calendar-days"></i>

Events

</a>



<div class="nav-title" style="margin-top:25px;">
Management
</div>


<a href="#">

<i class="fa-solid fa-users"></i>

Customers

</a>


<a href="#">

<i class="fa-solid fa-chart-line"></i>

Reports

</a>


</nav>


<div class="sidebar-bottom">

<a href="logout.php">

<i class="fa-solid fa-right-from-bracket"></i>

Logout

</a>

</div>


</aside>



<!-- =====================================================
     MAIN
===================================================== -->

<main class="main">


<!-- TOPBAR -->

<div class="topbar">


<div class="page-title">

<h1>
Dashboard
</h1>

<p>
Monate website administration
</p>

</div>


<div class="admin-profile">


<div class="admin-info">

<strong>
<?php echo htmlspecialchars($adminName); ?>
</strong>

<span>
Administrator
</span>

</div>


<div class="admin-avatar">

<i class="fa-solid fa-user"></i>

</div>


</div>


</div>



<!-- CONTENT -->

<div class="content">


<!-- WELCOME -->

<section class="welcome">

<small>
Monate Administration
</small>

<h2>
Welcome back, <?php echo htmlspecialchars($adminName); ?>.
</h2>

<p>
Manage and monitor your Monate website from one place.
</p>

</section>



<!-- STATISTICS -->

<div class="stats">


<div class="stat">

<div class="stat-icon">

<i class="fa-solid fa-users"></i>

</div>

<h3>
<?php echo $totalUsers; ?>
</h3>

<p>
Registered Users
</p>

</div>


<div class="stat">

<div class="stat-icon">

<i class="fa-solid fa-utensils"></i>

</div>

<h3>
<?php echo $totalMenuItems; ?>
</h3>

<p>
Menu Items
</p>

</div>


<div class="stat">

<div class="stat-icon">

<i class="fa-solid fa-layer-group"></i>

</div>

<h3>
<?php echo $totalCategories; ?>
</h3>

<p>
Categories
</p>

</div>


<div class="stat">

<div class="stat-icon">

<i class="fa-solid fa-bag-shopping"></i>

</div>

<h3>
<?php echo $totalOrders; ?>
</h3>

<p>
Orders
</p>

</div>


</div>



<!-- QUICK ACTIONS -->

<div class="section-title">

<h2>
Quick Access
</h2>

<span>
MONATE ADMIN
</span>

</div>


<div class="quick-actions">


<a href="menu.php" class="action">

<div class="action-icon">

<i class="fa-solid fa-utensils"></i>

</div>

<div>

<h3>
Menu
</h3>

<p>
View the restaurant menu
</p>

</div>

</a>


<a href="events.php" class="action">

<div class="action-icon">

<i class="fa-solid fa-calendar-days"></i>

</div>

<div>

<h3>
Events
</h3>

<p>
View Monate events
</p>

</div>

</a>


<a href="index.php" class="action">

<div class="action-icon">

<i class="fa-solid fa-arrow-up-right-from-square"></i>

</div>

<div>

<h3>
Website
</h3>

<p>
Open the public website
</p>

</div>

</a>


</div>



<!-- INFORMATION -->

<div class="info-grid">


<div class="info-box">

<h3>
Website
</h3>


<div class="info-row">

<span>
Menu Items
</span>

<span>
<?php echo $totalMenuItems; ?>
</span>

</div>


<div class="info-row">

<span>
Categories
</span>

<span>
<?php echo $totalCategories; ?>
</span>

</div>


<div class="info-row">

<span>
Registered Users
</span>

<span>
<?php echo $totalUsers; ?>
</span>

</div>


</div>



<div class="info-box">

<h3>
System
</h3>


<div class="info-row">

<span>
Logged in as
</span>

<span>
Admin
</span>

</div>


<div class="info-row">

<span>
Reviews
</span>

<span>
<?php echo $totalReviews; ?>
</span>

</div>


<div class="info-row">

<span>
Orders
</span>

<span>
<?php echo $totalOrders; ?>
</span>

</div>


</div>


</div>


</div>

</main>


</body>

</html>