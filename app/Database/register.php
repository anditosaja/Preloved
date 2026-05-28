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
    $nama = $_POST['nama'];
    $email = $_POST['email'];
    $password_plain = $_POST['password'];

    // PROSES HASHING PASSWORD
    // Menggunakan BCRYPT 
    $password_hash = password_hash($password_plain, PASSWORD_DEFAULT);

    // Cek apakah email sudah terdaftar
    $cek_email = $conn->query("SELECT * FROM users WHERE email = '$email'");
    
    if ($cek_email->num_rows > 0) {
        echo json_encode(["status" => "error", "message" => "Email sudah terdaftar"]);
    } else {
        // Simpan ke database
        $sql = "INSERT INTO users (nama, email, password) VALUES ('$nama', '$email', '$password_hash')";
        if ($conn->query($sql) === TRUE) {
            echo json_encode(["status" => "success", "message" => "Registrasi berhasil"]);
        } else {
            echo json_encode(["status" => "error", "message" => "Gagal mendaftar"]);
        }
    }
}
$conn->close();
?>