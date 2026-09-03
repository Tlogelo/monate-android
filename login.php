<?php

session_start();
include("db.php");

$error = "";

if (isset($_SESSION['user_id'])) {
    if (($_SESSION['user_role'] ?? '') === 'admin') {
        header("Location: dashboard.php");
    } else {
        header("Location: index.php");
    }
    exit();
}

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $email = trim($_POST['email'] ?? "");
    $password = $_POST['password'] ?? "";

    if ($email === "" || $password === "") {
        $error = "Please enter your email and password.";
    } else {
        $stmt = mysqli_prepare(
            $conn,
            "SELECT user_id, first_name, last_name, email, password, role
             FROM users
             WHERE email = ?
             LIMIT 1"
        );

        if ($stmt) {
            mysqli_stmt_bind_param($stmt, "s", $email);
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            $user = mysqli_fetch_assoc($result);

            if ($user && password_verify($password, $user['password'])) {
                session_regenerate_id(true);

                $_SESSION['user_id'] = $user['user_id'];
                $_SESSION['user_name'] = $user['first_name'] . " " . $user['last_name'];
                $_SESSION['user_email'] = $user['email'];
                $_SESSION['user_role'] = $user['role'];

                // Keep these too, since dashboard.php checks admin_id/admin_role
                if ($user['role'] === 'admin') {
                    $_SESSION['admin_id'] = $user['user_id'];
                    $_SESSION['admin_name'] = $_SESSION['user_name'];
                    $_SESSION['admin_email'] = $user['email'];
                    $_SESSION['admin_role'] = $user['role'];
                    header("Location: dashboard.php");
                } else {
                    header("Location: index.php");
                }
                exit();
            } else {
                $error = "Incorrect email or password.";
            }

            mysqli_stmt_close($stmt);
        } else {
            $error = "Database error. Please try again.";
        }
    }
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Monate | Login</title>
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
.login-container { width: 100%; max-width: 430px; }
.brand { text-align: center; margin-bottom: 25px; }
.brand img { width: 65px; height: 65px; object-fit: contain; margin-bottom: 12px; }
.brand h1 { font-family: Georgia, serif; font-size: 36px; font-weight: normal; }
.brand p { color: var(--tan); font-size: 9px; letter-spacing: 3px; text-transform: uppercase; margin-top: 6px; }
.login-card { background: rgba(45,30,24,.97); border: 1px solid rgba(238,226,209,.12); padding: 40px; box-shadow: 0 25px 70px rgba(0,0,0,.4); }
.login-card h2 { font-family: Georgia, serif; font-weight: normal; font-size: 27px; margin-bottom: 7px; }
.subtitle { color: #918278; font-size: 11px; line-height: 1.6; margin-bottom: 28px; }
.error { background: rgba(169,97,62,.15); border-left: 3px solid var(--orange); padding: 12px; color: #d9b7a5; font-size: 11px; margin-bottom: 20px; }
label { display: block; color: var(--tan); font-size: 9px; font-weight: bold; text-transform: uppercase; letter-spacing: 1.2px; margin-bottom: 8px; }
input { width: 100%; padding: 14px; margin-bottom: 20px; background: #211713; border: 1px solid rgba(238,226,209,.15); color: var(--cream); outline: none; font-size: 12px; }
input:focus { border-color: var(--tan); }
input::placeholder { color: #665950; }
button { width: 100%; padding: 15px; border: none; background: var(--orange); color: white; font-size: 9px; font-weight: bold; letter-spacing: 1.5px; text-transform: uppercase; cursor: pointer; transition: .25s; }
button:hover { background: var(--orange-dark); }
.back { text-align: center; margin-top: 22px; }
.back a { color: #83756c; font-size: 10px; text-decoration: none; }
.back a:hover { color: var(--cream); }
.switch { text-align: center; margin-top: 14px; font-size: 11px; color: #918278; }
.switch a { color: var(--tan); text-decoration: none; }
</style>
</head>
<body>
<div class="login-container">
    <div class="brand">
        <img src="images/logo.png" alt="Monate Logo">
        <h1>Monate</h1>
        <p>Sign In</p>
    </div>
    <div class="login-card">
        <h2>Welcome Back</h2>
        <p class="subtitle">Sign in to order or manage the Monate website.</p>

        <?php if ($error !== ""): ?>
            <div class="error"><?php echo htmlspecialchars($error); ?></div>
        <?php endif; ?>

        <form method="POST" action="">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" placeholder="Enter email"
                value="<?php echo htmlspecialchars($_POST['email'] ?? ''); ?>"
                autocomplete="email" required>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" placeholder="Enter password"
                autocomplete="current-password" required>

            <button type="submit">Sign In</button>
        </form>

        <div class="switch">
            Don't have an account? <a href="register.php">Register</a>
        </div>

        <div class="back">
            <a href="index.php">← Back to Monate Website</a>
        </div>
    </div>
</div>
</body>
</html>