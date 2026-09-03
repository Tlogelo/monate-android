<?php
session_start();
include("db.php");

/*
|--------------------------------------------------------------------------
| MONATE MENU — now pulls real items from menu_items / categories
|--------------------------------------------------------------------------
*/

// ---- Handle "Add to Cart" ----
$addMessage = "";

if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST['add_to_cart'])) {

    if (!isset($_SESSION['user_id'])) {
        header("Location: login.php");
        exit();
    }

    $menuId = (int) $_POST['menu_id'];
    $userId = (int) $_SESSION['user_id'];

    // Does this item already sit in the cart for this user?
    $checkStmt = mysqli_prepare(
        $conn,
        "SELECT cart_id, quantity FROM cart WHERE user_id = ? AND menu_id = ? LIMIT 1"
    );
    mysqli_stmt_bind_param($checkStmt, "ii", $userId, $menuId);
    mysqli_stmt_execute($checkStmt);
    $existing = mysqli_stmt_get_result($checkStmt)->fetch_assoc();
    mysqli_stmt_close($checkStmt);

    if ($existing) {
        $newQty = $existing['quantity'] + 1;
        $updateStmt = mysqli_prepare($conn, "UPDATE cart SET quantity = ? WHERE cart_id = ?");
        mysqli_stmt_bind_param($updateStmt, "ii", $newQty, $existing['cart_id']);
        mysqli_stmt_execute($updateStmt);
        mysqli_stmt_close($updateStmt);
    } else {
        $insertStmt = mysqli_prepare(
            $conn,
            "INSERT INTO cart (user_id, menu_id, quantity) VALUES (?, ?, 1)"
        );
        mysqli_stmt_bind_param($insertStmt, "ii", $userId, $menuId);
        mysqli_stmt_execute($insertStmt);
        mysqli_stmt_close($insertStmt);
    }

    $addMessage = "Item added to cart.";
}

// ---- Fetch categories for the filter bar ----
$categories = [];
$catResult = mysqli_query($conn, "SELECT category_id, category_name FROM categories ORDER BY category_name");
if ($catResult) {
    while ($row = mysqli_fetch_assoc($catResult)) {
        $categories[] = $row;
    }
}

// ---- Read filters from the URL ----
$selectedCategory = isset($_GET['category']) ? (int) $_GET['category'] : 0;
$searchTerm = isset($_GET['q']) ? trim($_GET['q']) : "";

// ---- Build the menu items query ----
$sql = "SELECT m.menu_id, m.item_name, m.description, m.price, m.image, c.category_name
        FROM menu_items m
        JOIN categories c ON m.category_id = c.category_id
        WHERE m.available = 'Yes'";

$params = [];
$types = "";

if ($selectedCategory > 0) {
    $sql .= " AND m.category_id = ?";
    $params[] = $selectedCategory;
    $types .= "i";
}

if ($searchTerm !== "") {
    $sql .= " AND m.item_name LIKE ?";
    $params[] = "%" . $searchTerm . "%";
    $types .= "s";
}

$sql .= " ORDER BY c.category_name, m.item_name";

$stmt = mysqli_prepare($conn, $sql);
if (!empty($params)) {
    mysqli_stmt_bind_param($stmt, $types, ...$params);
}
mysqli_stmt_execute($stmt);
$menuResult = mysqli_stmt_get_result($stmt);

$menuItems = [];
while ($row = mysqli_fetch_assoc($menuResult)) {
    $menuItems[] = $row;
}
mysqli_stmt_close($stmt);

// ---- Cart badge count ----
$cartCount = 0;
if (isset($_SESSION['user_id'])) {
    $cartCountStmt = mysqli_prepare(
        $conn,
        "SELECT COALESCE(SUM(quantity), 0) AS total FROM cart WHERE user_id = ?"
    );
    $uid = (int) $_SESSION['user_id'];
    mysqli_stmt_bind_param($cartCountStmt, "i", $uid);
    mysqli_stmt_execute($cartCountStmt);
    $cartCount = mysqli_stmt_get_result($cartCountStmt)->fetch_assoc()['total'];
    mysqli_stmt_close($cartCountStmt);
}

?>

<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Monate | Menu</title>

<link rel="stylesheet"
href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

<style>

:root {
    --brown-dark: #211713;
    --brown: #34231c;
    --cream: #eee2d1;
    --cream-light: #f8f1e7;
    --tan: #c8ad8f;
    --orange: #a9613e;
    --orange-dark: #82452d;
    --olive: #626447;
    --white: #fffaf4;
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

.menu-hero {
    min-height: 500px; display: flex; align-items: center; position: relative; overflow: hidden;
    background: linear-gradient(90deg, rgba(33,23,19,.98), rgba(33,23,19,.85), rgba(33,23,19,.25));
}
.menu-hero-content { width: 90%; max-width: 1250px; margin: auto; position: relative; z-index: 2; }
.menu-hero small { color: var(--tan); font-size: 10px; font-weight: bold; letter-spacing: 4px; text-transform: uppercase; }
.menu-hero h1 { font-family: Georgia, serif; font-weight: normal; font-size: clamp(65px, 9vw, 120px); line-height: .88; margin: 20px 0; }
.menu-hero h1 span { color: var(--tan); }
.menu-hero p { color: #cbbdb1; max-width: 520px; font-size: 13px; line-height: 1.8; }

.intro { background: var(--cream); color: var(--brown); text-align: center; padding: 65px 20px; }
.intro small { color: var(--orange); text-transform: uppercase; letter-spacing: 3px; font-size: 9px; font-weight: bold; }
.intro h2 { font-family: Georgia, serif; font-size: clamp(38px,5vw,58px); font-weight: normal; margin-top: 10px; }
.intro h2 span { color: var(--orange); }
.intro p { max-width: 600px; margin: 18px auto 0; font-size: 13px; color: #76675b; line-height: 1.8; }

/* ---- Filter bar: categories + search ---- */
.filter-wrapper { background: var(--cream); padding: 0 5% 45px; }
.filter-inner { max-width: 1100px; margin: auto; }
.search-row { display: flex; justify-content: center; margin-bottom: 20px; }
.search-row input {
    width: 100%; max-width: 420px; padding: 12px 16px; border: 1px solid #bca58d;
    background: var(--cream-light); color: var(--brown); font-size: 12px;
}
.categories { display: flex; flex-wrap: wrap; justify-content: center; gap: 8px; }
.category {
    padding: 11px 17px; border: 1px solid #bca58d; color: var(--brown); font-size: 9px;
    font-weight: bold; text-transform: uppercase; letter-spacing: 1px; cursor: pointer; transition: .25s;
    background: transparent; text-decoration: none;
}
.category:hover, .category.active { background: var(--brown); color: var(--cream); border-color: var(--brown); }

/* ---- Dish grid ---- */
.menu-gallery { background: var(--brown-dark); padding: 90px 5%; }
.gallery-container { max-width: 1250px; margin: auto; }
.gallery-heading { margin-bottom: 55px; }
.gallery-heading small { color: var(--tan); text-transform: uppercase; font-size: 9px; letter-spacing: 3px; font-weight: bold; }
.gallery-heading h2 { font-family: Georgia, serif; font-size: clamp(38px,5vw,60px); font-weight: normal; margin-top: 8px; }
.gallery-heading h2 span { color: var(--tan); }

.dish-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 24px; }

.dish-card {
    background: var(--brown); border: 1px solid rgba(238,226,209,.1); overflow: hidden;
    display: flex; flex-direction: column; transition: .3s;
}
.dish-card:hover { transform: translateY(-6px); border-color: rgba(200,173,143,.4); }
.dish-image { width: 100%; height: 180px; background: #2a1c16 center/cover no-repeat; }
.dish-body { padding: 18px; display: flex; flex-direction: column; gap: 8px; flex: 1; }
.dish-category { color: var(--tan); font-size: 9px; text-transform: uppercase; letter-spacing: 1.5px; }
.dish-name { font-size: 15px; font-weight: bold; color: var(--cream); }
.dish-desc { font-size: 11px; color: #b4a89d; line-height: 1.6; flex: 1; }
.dish-footer { display: flex; align-items: center; justify-content: space-between; margin-top: 8px; }
.dish-price { color: var(--orange); font-weight: bold; font-size: 14px; }
.dish-footer button {
    background: var(--orange); color: white; border: none; padding: 9px 14px;
    font-size: 9px; font-weight: bold; text-transform: uppercase; letter-spacing: .5px;
    cursor: pointer; transition: .25s;
}
.dish-footer button:hover { background: var(--orange-dark); }

.no-results { text-align: center; color: #887a6f; font-size: 12px; padding: 40px 0; }

.add-toast {
    max-width: 1250px; margin: 0 auto 30px; padding: 12px 18px; background: rgba(169,97,62,.15);
    border-left: 3px solid var(--orange); color: #d9b7a5; font-size: 11px;
}

.menu-note { text-align: center; margin-top: 65px; padding-top: 30px; border-top: 1px solid rgba(238,226,209,.1); color: #887a6f; font-size: 10px; letter-spacing: .5px; }

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
    .footer-content { grid-template-columns: 1fr 1fr; }
}
@media(max-width: 600px) {
    header { height: 70px; padding: 0 4%; }
    .logo img { width: 38px; height: 38px; }
    .logo h2 { font-size: 17px; }
    .login, .register { display: none; }
    .menu-hero { min-height: 400px; }
    .menu-hero h1 { font-size: 65px; }
    .menu-gallery { padding: 65px 4%; }
    .filter-wrapper { padding-left: 4%; padding-right: 4%; }
    .categories { justify-content: flex-start; }
    .footer-content { grid-template-columns: 1fr; }
}

</style>

</head>

<body>

<header>
<div class="logo">
<img src="images/logo.png" alt="Monate">
<h2>Monate<span>City Lifestyle Village</span></h2>
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

<section class="menu-hero">
<div class="menu-hero-content">
<small>Monate Chicken & Steak</small>
<h1>The <span>Menu</span></h1>
<p>Flame-grilled chicken, steak, wings, sides and more — order online and track your order in real time.</p>
</div>
</section>

<section class="intro">
<small>Eat. Drink. Enjoy.</small>
<h2>Welcome to <span>Monate</span></h2>
<p>Browse the full menu below, filter by category, and add items straight to your cart.</p>
</section>

<section class="filter-wrapper">
<div class="filter-inner">

<form method="GET" action="menu.php" class="search-row">
    <?php if ($selectedCategory > 0): ?>
        <input type="hidden" name="category" value="<?php echo $selectedCategory; ?>">
    <?php endif; ?>
    <input type="text" name="q" placeholder="Search menu item"
        value="<?php echo htmlspecialchars($searchTerm); ?>">
</form>

<div class="categories">
    <a href="menu.php<?php echo $searchTerm !== '' ? '?q=' . urlencode($searchTerm) : ''; ?>"
       class="category <?php echo $selectedCategory === 0 ? 'active' : ''; ?>">
       All
    </a>
    <?php foreach ($categories as $cat): ?>
        <a href="menu.php?category=<?php echo $cat['category_id']; ?><?php echo $searchTerm !== '' ? '&q=' . urlencode($searchTerm) : ''; ?>"
           class="category <?php echo $selectedCategory === (int) $cat['category_id'] ? 'active' : ''; ?>">
           <?php echo htmlspecialchars($cat['category_name']); ?>
        </a>
    <?php endforeach; ?>
</div>

</div>
</section>

<section class="menu-gallery" id="all">
<div class="gallery-container">

<div class="gallery-heading">
<small>Our Menu</small>
<h2>Choose Your <span>Flavour</span></h2>
</div>

<?php if ($addMessage !== ""): ?>
    <div class="add-toast"><?php echo htmlspecialchars($addMessage); ?></div>
<?php endif; ?>

<?php if (empty($menuItems)): ?>

    <div class="no-results">No menu items match your search.</div>

<?php else: ?>

<div class="dish-grid">

<?php foreach ($menuItems as $item): ?>

<div class="dish-card">

    <div class="dish-image" style="background-image: url('images/menu-items/<?php echo rawurlencode($item['image']); ?>');"></div>

    <div class="dish-body">
        <div class="dish-category"><?php echo htmlspecialchars($item['category_name']); ?></div>
        <div class="dish-name"><?php echo htmlspecialchars($item['item_name']); ?></div>
        <div class="dish-desc"><?php echo htmlspecialchars($item['description']); ?></div>

        <div class="dish-footer">
            <span class="dish-price">R<?php echo number_format($item['price'], 2); ?></span>

            <form method="POST" action="menu.php<?php echo $selectedCategory > 0 ? '?category=' . $selectedCategory : ''; ?>">
                <input type="hidden" name="menu_id" value="<?php echo $item['menu_id']; ?>">
                <button type="submit" name="add_to_cart">Add to Cart</button>
            </form>
        </div>
    </div>

</div>

<?php endforeach; ?>

</div>

<?php endif; ?>

<div class="menu-note">
<i class="fa-solid fa-circle-info"></i>
&nbsp;
Prices shown are in South African Rand and may vary based on availability.
</div>

</div>
</section>

<footer>
<div class="footer-content">
<div class="footer-brand">
<h2>MONATE</h2>
<p>Monate Chicken & Steak. flame-grilled, fast, and full of flavour.</p>
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
© <?php echo date("Y"); ?> Monate Chicken & Steak. All Rights Reserved.c</footer>

</body>
</html>