<?php

session_start();
include("db.php");

$error = "";
$success = "";

if (isset($_SESSION['user_id'])) {
    header("Location: index.php");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $first_name = trim($_POST['first_name'] ?? "");
    $last_name = trim($_POST['last_name'] ?? "");
    $email = trim($_POST['email'] ?? "");
    $phone = trim($_POST['phone'] ?? "");
    $password = $_POST['password'] ?? "";
    $confirm_password = $_POST['confirm_password'] ?? "";

    if (strlen($first_name) < 2 || strlen($last_name) < 2) {
        $error = "First and last name must be at least 2 characters.";
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $error = "Enter a valid email address.";
    } elseif (
        strlen($password) < 8 ||
        !preg_match('/[0-9]/', $password) ||
        !preg_match('/[^a-zA-Z0-9]/', $password)
    ) {
        $error = "Password must be at least 8 characters, include 1 number and 1 special character.";
    } elseif ($password !== $confirm_password) {
        $error = "Passwords do not match.";
    } else {

        $checkStmt = mysqli_prepare($conn, "SELECT user_id FROM users WHERE email = ? LIMIT 1");
        mysqli_stmt_bind_param($checkStmt, "s", $email);
        mysqli_stmt_execute($checkStmt);
        mysqli_stmt_store_result($checkStmt);

        if (mysqli_stmt_num_rows($checkStmt) > 0) {
            $error = "An account with that email already exists.";
        } else {
            $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

            $insertStmt = mysqli_prepare(
                $conn,
                "INSERT INTO users (first_name, last_name, email, phone, password, role)
                 VALUES (?, ?, ?, ?, ?, 'customer')"
            );
            mysqli_stmt_bind_param(
                $insertStmt, "sssss",
                $first_name, $last_name, $email, $phone, $hashedPassword
            );

            if (mysqli_stmt_execute($insertStmt)) {
                $newUserId = mysqli_insert_id($conn);

                session_regenerate_id(true);
                $_SESSION['user_id'] = $newUserId;
                $_SESSION['user_name'] = $first_name . " " . $last_name;
                $_SESSION['user_email'] = $email;
                $_SESSION['user_role'] = 'customer';

                header("Location: index.php");
                exit();
            } else {
                $error = "Something went wrong creating your account. Please try again.";
            }
            mysqli_stmt_close($insertStmt);
        }
        mysqli_stmt_close($checkStmt);
    }
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Monate | Register</title>
<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
:root {
    --dark: #211713; --dark2: #2d1e18; --cream: #eee2d1;
    --tan: #c8ad8f; --orange: #a9613e; --orange-dark: #82452d;
}
body {
    min-height: 100vh;
    background: linear-gradient(rgba(33,23,19,.92), rgba(33,23,19,.96)),
        url("images/events/events-hero.jpg") center/cover no-repeat;
    font-family: Arial, Helvetica, sans-serif;
    color: var(--cream);
    display: flex; align-items: center; justify-content: center; padding: 20px;
}
.register-container { width: 100%; max-width: 430px; }
.brand { text-align: center; margin-bottom: 25px; }
.brand img { width: 65px; height: 65px; object-fit: contain; margin-bottom: 12px; }
.brand h1 { font-family: Georgia, serif; font-size: 36px; font-weight: normal; }
.brand p { color: var(--tan); font-size: 9px; letter-spacing: 3px; text-transform: uppercase; margin-top: 6px; }
.register-card { background: rgba(45,30,24,.97); border: 1px solid rgba(238,226,209,.12); padding: 40px; box-shadow: 0 25px 70px rgba(0,0,0,.4); }
.register-card h2 { font-family: Georgia, serif; font-weight: normal; font-size: 27px; margin-bottom: 7px; }
.subtitle { color: #918278; font-size: 11px; line-height: 1.6; margin-bottom: 24px; }
.error { background: rgba(169,97,62,.15); border-left: 3px solid var(--orange); padding: 12px; color: #d9b7a5; font-size: 11px; margin-bottom: 20px; }
.row { display: flex; gap: 12px; }
.row input { flex: 1; }
label { display: block; color: var(--tan); font-size: 9px; font-weight: bold; text-transform: uppercase; letter-spacing: 1.2px; margin-bottom: 8px; }
input { width: 100%; padding: 14px; margin-bottom: 18px; background: #211713; border: 1px solid rgba(238,226,209,.15); color: var(--cream); outline: none; font-size: 12px; }
input:focus { border-color: var(--tan); }
input::placeholder { color: #665950; }
button { width: 100%; padding: 15px; border: none; background: var(--orange); color: white; font-size: 9px; font-weight: bold; letter-spacing: 1.5px; text-transform: uppercase; cursor: pointer; transition: .25s; }
button:hover { background: var(--orange-dark); }
.switch { text-align: center; margin-top: 14px; font-size: 11px; color: #918278; }
.switch a { color: var(--tan); text-decoration: none; }
.back { text-align: center; margin-top: 22px; }
.back a { color: #83756c; font-size: 10px; text-decoration: none; }
.back a:hover { color: var(--cream); }
</style>
</head>
<body>
<div class="register-container">
    <div class="brand">
        <img src="images/logo.png" alt="Monate Logo">
        <h1>Monate</h1>
        <p>Create Account</p>
    </div>
    <div class="register-card">
        <h2>Join Monate</h2>
        <p class="subtitle">Create an account to order, track deliveries, and earn loyalty points.</p>

        <?php if ($error !== ""): ?>
            <div class="error"><?php echo htmlspecialchars($error); ?></div>
        <?php endif; ?>

        <form method="POST" action="">
            <div class="row">
                <div>
                    <label for="first_name">First Name</label>
                    <input type="text" id="first_name" name="first_name"
                        value="<?php echo htmlspecialchars($_POST['first_name'] ?? ''); ?>" required>
                </div>
                <div>
                    <label for="last_name">Last Name</label>
                    <input type="text" id="last_name" name="last_name"
                        value="<?php echo htmlspecialchars($_POST['last_name'] ?? ''); ?>" required>
                </div>
            </div>

            <label for="email">Email</label>
            <input type="email" id="email" name="email"
                value="<?php echo htmlspecialchars($_POST['email'] ?? ''); ?>" required>

            <label for="phone">Phone (optional)</label>
            <input type="tel" id="phone" name="phone"
                value="<?php echo htmlspecialchars($_POST['phone'] ?? ''); ?>">

            <label for="password">Password</label>
            <input type="password" id="password" name="password"
                placeholder="Min 8 chars, 1 number, 1 special char" required>

            <label for="confirm_password">Confirm Password</label>
            <input type="password" id="confirm_password" name="confirm_password" required>

            <button type="submit">Create Account</button>
        </form>

        <div class="switch">
            Already have an account? <a href="login.php">Sign In</a>
        </div>

        <div class="back">
            <a href="index.php">← Back to Monate Website</a>
        </div>
    </div>
</div>
</body>
</html>