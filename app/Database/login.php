<?php
// Koneksi ke database
$host = "localhost";
$user = "root";
$pass = "";
$db   = "db_preloved";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Koneksi gagal: " . $conn->connect_error);
}

// Menerima request POST dari Android
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $_POST['email'];
    $password_plain = $_POST['password'];

    // Cari user berdasarkan email
    $sql = "SELECT * FROM users WHERE email = '$email'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $password_hash = $row['password'];

        // PENGECEKAN PASSWORD 
        if (password_verify($password_plain, $password_hash)) {
            // Jika password cocok
            echo json_encode([
                "status" => "success", 
                "message" => "Login berhasil",
                "nama" => $row['nama'] // mengirim nama user kembali ke Android
            ]);
        } else {
            // Jika password salah
            echo json_encode(["status" => "error", "message" => "Kata sandi salah!"]);
        }
    } else {
        // Jika email tidak ditemukan
        echo json_encode(["status" => "error", "message" => "Email tidak terdaftar!"]);
    }
}
$conn->close();
?>