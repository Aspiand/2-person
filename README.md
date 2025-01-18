# Tujuan
Dapat membuat Create, Read, Update, Delete (CRUD) **_sederhana_** dengan menggunakan `SQLiteHelper` di Android.

> [!NOTE]
> Untuk alasan kemudahan, kode pada repository ini tidak menerapkan prinsip-prinsip seperti `Clean Code`, `DRY`, dll.

# Skema Aplikasi
Pada halaman utama, terdapat judul, `ListView` yang berisi informasi siswa seperti nama dan nilai rata-rata yang dihitung berdasarkan input, jumlah siswa, nilai rata-rata kelas dan dua buah tombol.

Tombol pertama (tambah) ketika ditekan akan menampilkan `dialog_form` yang terdapat input nama, nilai, dan tombol simpan.

Tombol kedua adalah refresh, digunakan untuk memperbarui daftar pada `ListView`. Alasan penggunaannya adalah karena sederhana dibandingkan menggunakan `SwipeRefreshLayout`.

POV: Momen Ketika, Saat item pada `ListView` ditekan, akan muncul `dialog_form` dengan nilai berdasarkan item yang dipilih. Digunakan untuk memperbarui data.

MK: When ketika pada item pada `ListView` ditekan lama atau _hold_, item akan dihapus. (untuk konfirmasi penghapusan kerjakan sendiri)

Untuk database singkat saja, akan ada kolom nama, android, basis_data, dan web. Untuk nilai rata-rata akan dimasukkan pada query sql dengan nama kolom `nilai_rata_rata`. Alasannya agar kode java tidak terlalu berantakan.

# A. User Interface atau Tampilan
> [!WARNING]
> Desain disini hanya sebagai referensi saja. Tolong kembangkan dan jangan malas.

> [!NOTE]
> Pastikan id harus unik atau tidak boleh sama.

## 1. `activity_main`
Pada aplikasi ini, hanya terdapat 1 activity saja agar mengurangi kompleksitas.

```xml
```

## 2. `dialog_form`
Isi dari `dialog_form` simple saja. Disana terdapat 1 input untuk nama, 3 buah input untuk nilai dan sebuah button. Jadi `dialog_form` akan digunakan untuk dua hal, yaitu menambah dan memperbaharui data.

`dialog_form` akan tampil jika tombol tambah pada `activity_main` atau item pada `ListView` ditekan.

```xml
```

## 3. `list_data`

```xml
```

# B. Java
> [!CAUTION]
> Jangan copy paste sembarang kode karena perbedaan nama file, variable dll memungkinkan terjadinya error yang akan mempersulit diri sendiri.

## 1. Koneksi.java
Buat file baru dengan cara klik kanan pada directory/folder MainActivity, lalu pilih new, cari Java Class.
Isi nama file dengan `Koneksi` saja tanpa ekstensi java.

Contoh Lengkap `Koneksi.java`:
```java
package id.my.aspian.l011;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Koneksi extends SQLiteOpenHelper {
    public Koneksi(@Nullable Context context) {
        // abaikan konteks, factory,_ dan version
        // cukup ganti nama_database.
        super(context, "nama_database", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Dengan alasan 'kemudahan', tidak ada primary key.

        // Membuat table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS data_siswa (" +
                        "nama varchar(255)," +
                        "android int," +
                        "basis_data int," +
                        "web int" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Abaikan
        db.execSQL("DROP TABLE IF EXISTS data_siswa");
    }
}
```

Setelah itu, buka file `MainActivity.java` dan sesuaikan dengan kode dibawah:
> [!CAUTION]
> Pastikan besar kecil huruf diperhatikan
```java
package id.my.aspian.l011;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Koneksi koneksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        koneksi = new Koneksi(this);
    }
}
```

> [!IMPORTANT]
> Setelah membuat dan menyesuaikan file `MainActivity.java dan Koneksi.java`, disarankan untuk langsung menjalankan aplikasi agar error dapat dideteksi dengan segera.

Setelah aplikasi berjalan dan tidak mengalami crash, cek skema database yang telah dibuat dengan menggunakan `App Inspection` yang bersembunyi pada titik 3 (More tool windows) pada kiri atas.