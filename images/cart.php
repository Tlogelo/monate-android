<?php

session_start();
include("db.php");

// Must be logged in to view/manage a cart
if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}

$userId = (int) $_SESSION['user_id'];
$error = "";
$success = "";

// ---- Handle actions: increase, decrease, remove, checkout ----
if ($_SERVER["REQUEST_METHOD"] === "POST") {

    if (isset($_POST['increase_qty'])) {
        $cartId = (int) $_POST['cart_id'];
        $stmt = mysqli_prepare($conn, "UPDATE cart SET quantity = quantity + 1 WHERE cart_id = ? AND user_id = ?");
        mysqli_stmt_bind_param($stmt, "ii", $cartId, $userId);
        mysqli_stmt_execute($stmt);
        mysqli_stmt_close($stmt);
    }

    if (isset($_POST['decrease_qty'])) {
        $cartId = (int) $_POST['cart_id'];

        $checkStmt = mysqli_prepare($conn, "SELECT quantity FROM cart WHERE cart_id = ? AND user_id = ?");
        mysqli_stmt_bind_param($checkStmt, "ii", $cartId, $userId);
        mysqli_stmt_execute($checkStmt);
        $row = mysqli_stmt_get_result($checkStmt)->fetch_assoc();
        mysqli_stmt_close($checkStmt);

        if ($row) {
            if ($row['quantity'] <= 1) {
                $delStmt = mysqli_prepare($conn, "DELETE FROM cart WHERE cart_id = ? AND user_id = ?");
                mysqli_stmt_bind_param($delStmt, "ii", $cartId, $userId);
                mysqli_stmt_execute($delStmt);
                mysqli_stmt_close($delStmt);
            } else {
                $updStmt = mysqli_prepare($conn, "UPDATE cart SET quantity = quantity - 1 WHERE cart_id = ? AND user_id = ?");
                mysqli_stmt_bind_param($updStmt, "ii", $cartId, $userId);
                mysqli_stmt_execute($updStmt);
                mysqli_stmt_close($updStmt);
            }
        }
    }

    if (isset($_POST['remove_item'])) {
        $cartId = (int) $_POST['cart_id'];
        $stmt = mysqli_prepare($conn, "DELETE FROM cart WHERE cart_id = ? AND user_id = ?");
        mysqli_stmt_bind_param($stmt, "ii", $cartId, $userId);
        mysqli_stmt_execute($stmt);
        mysqli_stmt_close($stmt);
    }

    if (isset($_POST['place_order'])) {

        $paymentMethod = $_POST['payment_method'] ?? "";
        $collectionTime = $_POST['collection_time'] ?? "";

        $validPayments = ['Cash', 'Card', 'EFT'];

        if (!in_array($paymentMethod, $validPayments)) {
            $error = "Please choose a valid payment method.";
        } elseif ($collectionTime === "") {
            $error = "Please choose a collection time.";
        } else {

            // Re-fetch cart with live prices, joined to menu_items
            $cartStmt = mysqli_prepare(
                $conn,
                "SELECT c.cart_id, c.menu_id, c.quantity, m.price
                 FROM cart c
                 JOIN menu_items m ON c.menu_id = m.menu_id
                 WHERE c.user_id = ?"
            );
            mysqli_stmt_bind_param($cartStmt, "i", $userId);
            mysqli_stmt_execute($cartStmt);
            $cartRows = mysqli_stmt_get_result($cartStmt)->fetch_all(MYSQLI_ASSOC);
            mysqli_stmt_close($cartStmt);

            if (empty($cartRows)) {
                $error = "Your cart is empty.";
            } else {

                $total = 0;
                foreach ($cartRows as $row) {
                    $total += $row['price'] * $row['quantity'];
                }

                // Use a transaction so the order + its items + clearing the cart
                // all succeed together, or none of them do
                mysqli_begin_transaction($conn);

                try {
                    $orderStmt = mysqli_prepare(
                        $conn,
                        "INSERT INTO orders (user_id, total, payment_method, collection_time, status)
                         VALUES (?, ?, ?, ?, 'Received')"
                    );
                    mysqli_stmt_bind_param($orderStmt, "idss", $userId, $total, $paymentMethod, $collectionTime);
                    mysqli_stmt_execute($orderStmt);
                    $orderId = mysqli_insert_id($conn);
                    mysqli_stmt_close($orderStmt);

                    $itemStmt = mysqli_prepare(
                        $conn,
                        "INSERT INTO order_items (order_id, menu_id, quantity, price, subtotal)
                         VALUES (?, ?, ?, ?, ?)"
                    );

                    foreach ($cartRows as $row) {
                        $subtotal = $row['price'] * $row['quantity'];
                        mysqli_stmt_bind_param(
                            $itemStmt, "iiidd",
                            $orderId, $row['menu_id'], $row['quantity'], $row['price'], $subtotal
                        );
                        mysqli_stmt_execute($itemStmt);
                    }
                    mysqli_stmt_close($itemStmt);

                    $clearStmt = mysqli_prepare($conn, "DELETE FROM cart WHERE user_id = ?");
                    mysqli_stmt_bind_param($clearStmt, "i", $userId);
                    mysqli_stmt_execute($clearStmt);
                    mysqli_stmt_close($clearStmt);

                    mysqli_commit($conn);

                    $success = "Order #$orderId placed successfully! We'll have it ready by your chosen collection time.";

                } catch (Exception $e) {
                    mysqli_rollback($conn);
                    $error = "Something went wrong placing your order. Please try again.";
                }
            }
        }
    }
}

// ---- Fetch current cart contents (after any action above) ----
$cartItems = [];
$grandTotal = 0;

$fetchStmt = mysqli_prepare(
    $conn,
    "SELECT c.cart_id, c.menu_id, c.quantity, m.item_name, m.price, m.image
     FROM cart c
     JOIN menu_items m ON c.menu_id = m.menu_id
     WHERE c.user_id = ?
     ORDER BY c.cart_id"
);
mysqli_stmt_bind_param($fetchStmt, "i", $userId);
mysqli_stmt_execute($fetchStmt);
$result = mysqli_stmt_get_result($fetchStmt);

while ($row = mysqli_fetch_assoc($result)) {
    $row['line_total'] = $row['price'] * $row['quantity'];
    $grandTotal += $row['line_total'];
    $cartItems[] = $row;
}
mysqli_stmt_close($fetchStmt);

// ---- Cart badge count (for header, same as menu.php) ----
$cartCount = array_sum(array_column($cartItems, 'quantity'));

?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Monate | Cart</title>
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

.page-wrap { max-width: 950px; margin: 60px auto; padding: 0 5%; }
.page-title { font-family: Georgia, serif; font-size: 36px; font-weight: normal; margin-bottom: 30px; }
.page-title span { color: var(--tan); }

.alert { padding: 14px 18px; font-size: 12px; margin-bottom: 24px; }
.alert-error { background: rgba(169,97,62,.15); border-left: 3px solid var(--orange); color: #d9b7a5; }
.alert-success { background: rgba(98,100,71,.2); border-left: 3px solid var(--olive); color: #cdd4b5; }

.empty-cart { text-align: center; padding: 80px 0; color: #887a6f; }
.empty-cart i { font-size: 42px; margin-bottom: 16px; display: block; }
.empty-cart a { color: var(--tan); text-decoration: none; font-weight: bold; }

.cart-row {
    display: flex; align-items: center; gap: 18px; padding: 18px 0;
    border-bottom: 1px solid rgba(238,226,209,.1);
}
.cart-row-image {
    width: 70px; height: 70px; background: #2a1c16 center/cover no-repeat; flex-shrink: 0;
}
.cart-row-info { flex: 1; }
.cart-row-name { font-weight: bold; font-size: 14px; margin-bottom: 4px; }
.cart-row-price { color: var(--tan); font-size: 11px; }

.qty-controls { display: flex; align-items: center; gap: 10px; }
.qty-controls button {
    width: 28px; height: 28px; border: 1px solid rgba(238,226,209,.2); background: transparent;
    color: var(--cream); cursor: pointer; font-size: 14px;
}
.qty-controls button:hover { background: var(--brown); }
.qty-value { min-width: 20px; text-align: center; font-weight: bold; }

.line-total { min-width: 80px; text-align: right; font-weight: bold; color: var(--orange); }

.remove-btn {
    background: none; border: none; color: #887a6f; cursor: pointer; font-size: 14px; margin-left: 12px;
}
.remove-btn:hover { color: var(--orange); }

.cart-summary {
    margin-top: 30px; padding-top: 20px; border-top: 1px solid rgba(238,226,209,.15);
    display: flex; justify-content: flex-end;
}
.cart-summary-inner { min-width: 260px; }
.summary-row { display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 10px; }
.summary-row.total { font-size: 18px; font-weight: bold; color: var(--orange); margin-top: 10px; }

.checkout-box {
    background: var(--brown); border: 1px solid rgba(238,226,209,.1); padding: 30px; margin-top: 40px;
}
.checkout-box h3 { font-family: Georgia, serif; font-weight: normal; font-size: 22px; margin-bottom: 20px; }
.form-group { margin-bottom: 18px; }
.form-group label {
    display: block; color: var(--tan); font-size: 9px; font-weight: bold;
    text-transform: uppercase; letter-spacing: 1.2px; margin-bottom: 8px;
}
.form-group select, .form-group input {
    width: 100%; padding: 12px; background: var(--brown-dark); border: 1px solid rgba(238,226,209,.15);
    color: var(--cream); font-size: 12px; outline: none;
}
.form-group select:focus, .form-group input:focus { border-color: var(--tan); }

.place-order-btn {
    width: 100%; padding: 15px; border: none; background: var(--orange); color: white;
    font-size: 10px; font-weight: bold; letter-spacing: 1.5px; text-transform: uppercase;
    cursor: pointer; transition: .25s;
}
.place-order-btn:hover { background: var(--orange-dark); }

footer { background: #120e0c; padding: 55px 5% 20px; margin-top: 60px; }
.footer-content { max-width: 1200px; margin: auto; display: grid; grid-template-columns: 2fr 1fr 1fr; gap: 50px; padding-bottom: 40px; }
.footer-brand h2 { font-family: Georgia, serif; font-size: 28px; font-weight: normal; margin-bottom: 10px; }
.footer-brand p { max-width: 320px; color: #766b63; font-size: 10px; line-height: 1.8; }
.footer-column h4 { color: var(--tan); font-size: 9px; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 16px; }
.footer-column a { display: block; color: #786d66; font-size: 10px; margin-bottom: 10px; }
.footer-column a:hover { color: var(--cream); }
.footer-bottom { border-top: 1px solid rgba(238,226,209,.08); padding-top: 18px; text-align: center; color: #514943; font-size: 9px; }

@media(max-width: 700px) {
    nav { display: none; }
    .cart-row { flex-wrap: wrap; }
    .line-total { margin-left: auto; }
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
<a href="profile.php" class="login">Account</a>
<a href="logout.php" class="register">Logout</a>
</div>
</header>

<div class="page-wrap">

<h1 class="page-title">Your <span>Cart</span></h1>

<?php if ($error !== ""): ?>
    <div class="alert alert-error"><?php echo htmlspecialchars($error); ?></div>
<?php endif; ?>

<?php if ($success !== ""): ?>
    <div class="alert alert-success"><?php echo htmlspecialchars($success); ?></div>
<?php endif; ?>

<?php if (empty($cartItems)): ?>

    <div class="empty-cart">
        <i class="fa-solid fa-cart-shopping"></i>
        Your cart is empty.
        <br><br>
        <a href="menu.php">Browse the menu →</a>
    </div>

<?php else: ?>

    <div class="cart-list">
        <?php foreach ($cartItems as $item): ?>

        <div class="cart-row">

            <div class="cart-row-image" style="background-image: url('images/menu-items/<?php echo rawurlencode($item['image']); ?>');"></div>

            <div class="cart-row-info">
                <div class="cart-row-name"><?php echo htmlspecialchars($item['item_name']); ?></div>
                <div class="cart-row-price">R<?php echo number_format($item['price'], 2); ?> each</div>
            </div>

            <form method="POST" class="qty-controls">
                <input type="hidden" name="cart_id" value="<?php echo $item['cart_id']; ?>">
                <button type="submit" name="decrease_qty">−</button>
                <span class="qty-value"><?php echo $item['quantity']; ?></span>
            </form>

            <form method="POST" class="qty-controls">
                <input type="hidden" name="cart_id" value="<?php echo $item['cart_id']; ?>">
                <button type="submit" name="increase_qty">+</button>
            </form>

            <div class="line-total">R<?php echo number_format($item['line_total'], 2); ?></div>

            <form method="POST">
                <input type="hidden" name="cart_id" value="<?php echo $item['cart_id']; ?>">
                <button type="submit" name="remove_item" class="remove-btn">
                    <i class="fa-solid fa-trash"></i>
                </button>
            </form>

        </div>

        <?php endforeach; ?>
    </div>

    <div class="cart-summary">
        <div class="cart-summary-inner">
            <div class="summary-row total">
                <span>Total</span>
                <span>R<?php echo number_format($grandTotal, 2); ?></span>
            </div>
        </div>
    </div>

    <div class="checkout-box">
        <h3>Checkout</h3>

        <form method="POST">

            <div class="form-group">
                <label for="payment_method">Payment Method</label>
                <select name="payment_method" id="payment_method" required>
                    <option value="">Select payment method</option>
                    <option value="Cash">Cash</option>
                    <option value="Card">Card</option>
                    <option value="EFT">EFT</option>
                </select>
            </div>

            <div class="form-group">
                <label for="collection_time">Collection Time</label>
                <input type="datetime-local" name="collection_time" id="collection_time" required>
            </div>

            <button type="submit" name="place_order" class="place-order-btn">Place Order</button>

        </form>
    </div>

<?php endif; ?>

</div>

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