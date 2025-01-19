> [!NOTE]
> Untuk alasan kemudahan, kode pada repository ini tidak banyak menerapkan prinsip-prinsip seperti `Clean Code`, `DRY`, dll.

> [!NOTE]
> Untuk fitur yang belum lengkap harap dikerjakan sendiri.

> [!IMPORTANT]
> Jika terjadi masalah seperti `findViewById(R.id.total_rata_rata);` berwarna merah padahal pada xml sudah sama, lakukan clean build dengan cara klik main menu pada pojok paling kiri atas. Lalu arahkan kekanan hingga build, kemudian pilih clean project. 

# Tujuan
Dapat membuat Create, Read, Update, Delete (CRUD) **_sederhana_** dengan menggunakan `SQLiteHelper` di Android.

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

## 0. Pembuka
Nilai rata-rata dapat dihitung dengan menambahkan semua data dan membaginya dengan banyaknya jumlah data.

Contoh:

Seorang murid memiliki nilai 80 pada mata pelajaran Android, 50 pada Basis Data, dan 70 pada Web.
Maka cara menghitungnya adalah (80 + 50 + 70) / 3

80, 50, 70 didapat dari nilai dan 3 merupakan jumlah mata pelajaran yang ada.

> [!TIP]
> Direkomendasikan menambahkan kurung pada penjumlahan untuk menghindari kesalahan perhitungan.
> Tujuannya agar penjumlahan dilakukan terlebih dahulu, lalu kemudian dibagi.

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

### Informasi tambahan
#### 1
Disepanjang kode, akan terlihat banyak baris seperti
```java
SQLiteDatabase db = koneksi.getWritableDatabase();
```
atau
```java
SQLiteDatabase db = koneksi.getReadableDatabase();
```

Jadi, apa perbedannya?

Singkatnya `getWritableDatabase` digunakan ketika akan melakukan operasi
tulis seperti insert, update dan delete.

Sedangkan untuk `getReadableDatabase` biasanya hanya digunakan untuk read data `(SELECT)`.

`getWritableDatabase` menggunakan execSQL untuk menjalankan perintah sql.
`getReadableDatabase` menggunakan rawQuery agar dapat berinteraksi dengan datanya.

#### 2
Tiap `cursor` perlu ditutup ketika tidak digunakan lagi.
Biasanya diletakkan dipaling akhir.
Contoh:
```java
Cursor cursor = db.rawQuery("SELECT * FROM data_siswa", null);
cursor.moveToFirst();
cursor.close();
```

## 2. CRUD