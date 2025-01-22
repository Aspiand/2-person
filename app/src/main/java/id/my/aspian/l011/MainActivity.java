package id.my.aspian.l011;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Koneksi koneksi;
    SQLiteDatabase db;

    Dialog dialog;
    boolean aksi = false; // false untuk tambah dan true untuk mengedit
    // variable aksi dibuat agar dapat menentukan apakah form dialog digunakan
    // untuk menambah data atau memperbarui data.

    Button tombol_tambah, tombol_refresh, tombol_simpan;
    TextView total_jumlah_murid, total_rata_rata;
    EditText dialog_nama, dialog_android, dialog_basis_data, dialog_web;
    ListView list_view;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        koneksi.close();
        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // abaikan
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Database
        koneksi = new Koneksi(this);
        db = koneksi.getWritableDatabase();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_form);

        list_view = findViewById(R.id.list_data);
        tombol_tambah = findViewById(R.id.tambah_data);
        tombol_refresh = findViewById(R.id.refresh_data);
        total_jumlah_murid = findViewById(R.id.jumlah_murid);
        total_rata_rata = findViewById(R.id.total_rata_rata);
        tombol_simpan = dialog.findViewById(R.id.simpan);
        dialog_nama = dialog.findViewById(R.id.input_nama);
        dialog_android = dialog.findViewById(R.id.input_android);
        dialog_basis_data = dialog.findViewById(R.id.input_basis_data);
        dialog_web = dialog.findViewById(R.id.input_web);

        // Menampilkan teks yang sudah ditentukan ketika list kosong.
        list_view.setEmptyView(findViewById(R.id.empty_list));

        list_view.setOnItemClickListener((adapterView, view, i, l) -> {

            // Mengambil nama berdasarkan item yang diklik
            TextView nama = view.findViewById(R.id.list_nama);

            // Mencari data berdasarkan nama.
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM data_siswa WHERE nama = ?",
                    new String[]{nama.getText().toString()}
            );

            if (cursor.moveToFirst()) {
                dialog_nama.setText(cursor.getString(0));
                dialog_android.setText(cursor.getString(1));
                dialog_basis_data.setText(cursor.getString(2));
                dialog_web.setText(cursor.getString(3));

                dialog.show();
            }

            // Mengatur agar tombol pada dialog menjadi update.
            aksi = true;

            cursor.close();
        });

        list_view.setOnItemLongClickListener((adapterView, view, i, l) -> {
            TextView nama = view.findViewById(R.id.list_nama);
            db.execSQL(
                    "DELETE FROM data_siswa WHERE nama = ?",
                    new String[]{nama.getText().toString()}
            );

            Toast.makeText(this, nama.getText().toString() + " berhasil dihapus", Toast.LENGTH_SHORT).show();
            refresh();

            return true;
        });

        tombol_tambah.setOnClickListener(view -> {
            test(); // abaikan

            // Mengatur agar tombol pada dialog menjadi tambah.
            aksi = false;
            dialog.show();
        });

        // Hal yang dilakukan oleh tombol simpan pada dialog berbeda tergantung
        // dari bagaimana dialog ditampilkan.
        // Jika dialog dimunculkan dengan menekan tombol tambah, maka variable `aksi` akan
        // berisi nilai false dan tombol simpan akan menambahkan data.
        // Jika dialog muncul ketika menekan item pada `ListView`, maka
        // variable aksi akan berisi nilai true dan tombol tekan akan memperbarui data.
        tombol_simpan.setOnClickListener(view -> {
            // no validation

            // Mengambil teks dan mengubahnya menjadi String.
            String nama = dialog_nama.getText().toString();
            String android = dialog_android.getText().toString();
            String basis_data = dialog_basis_data.getText().toString();
            String web = dialog_web.getText().toString();

            // Jika aksi berisi nilai true, kode yang akan dijalankan adalah update.
            // Jika false, maka data akan ditambahkan.

            if (aksi) {
                db.execSQL(
                        "UPDATE data_siswa SET android = ?, basis_data = ?, web = ? WHERE nama = ?",
                        new String[]{android, basis_data, web, nama}
                );

                Toast.makeText(this, "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                refresh();
                dialog.dismiss();
            }

            else {
                // Secara sederhana, fungsi dari try dan catch disini agar tidak terjadi
                // crash pada aplikasi ketika memasukkan nama yang sama.
                // Dari pada melakukan validasi, cara ini sedikit lebih ringkas.
                // Pesan `Duplikat` akan muncul ketika memasukkan nama baru,
                // tetapi nama yang akan dimasukkan sudah ada di database.
                try {
                    db.execSQL(
                            "INSERT INTO data_siswa VALUES (?, ?, ?, ?)",
                            new String[]{nama, android, basis_data, web}
                    );

                    Toast.makeText(this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    refresh();
                    dialog.dismiss();
                } catch (android.database.sqlite.SQLiteConstraintException e) {
                    Toast.makeText(this, "Nama duplikat", Toast.LENGTH_SHORT).show();
                }
            }

            // Penjelasan query.
            // INSERT INTO data_siswa VALUES (?, ?, ?, ?)
            // Singkatnya ini digunakan untuk menambahkan data dan '?' digunakan sebagai
            // tempat menampung / placeholder sementara dan akan digantikan oleh array yang ada dibawahnya.
            // Contoh:
            //
            // db.execSQL(
            //     "UPDATE data_siswa SET android = ?, basis_data = ?, web = ? WHERE nama = ?",
            //     new String[]{android, basis_data, web, nama}
            // );
            //
            // android = '?', itu artinya '?' akan diganti nilainya dengan variable android.
            // Hal yang sama juga berlaku untuk yang lain.
            // Yang perlu diperhatikan disini adalah urutan peletakannya.
            // Jika urutannya diganti, maka posisi variable didalam array juga harus diganti.
            // Contoh:
            //
            // db.execSQL(
            //     "UPDATE data_siswa SET basis_data = ?, web = ?, android = ? WHERE nama = ?",
            //     new String[]{          basis_data,     web,     android,          nama}
            // );
            //
        });

        // memperbarui data yang ada ketika tombol refresh ditekan.
        tombol_refresh.setOnClickListener(view -> {
            refresh();
        });

        // Menghapus semua data ketika tombol refresh ditekan lama.
        tombol_refresh.setOnLongClickListener(view -> {

            db.execSQL("DELETE FROM data_siswa");

            Toast.makeText(this, "Semuda data berhasil dihapus", Toast.LENGTH_SHORT).show();
            refresh();

            return true;
        });

        refresh();
    }

    public void refresh() {
        show_list();
        show_total();
    }

    public void show_total() {
        // Menampilkan data spesifik untuk total_siswa dan total nilai rata-rata untuk satu kelas.

        // Mendapatkan akses baca ke database.
        // biasanya digunakan ketika melakukan `SELECT`

        // Dikarenakan sqlite tidak memiliki function `FORMAT`,
        // sebagai gantinya fungsi yang akan digunakan adalah `ROUND`.
        Cursor cursor = db.rawQuery("SELECT COUNT(*) as total_siswa, ROUND(SUM((android + basis_data + web) / 3) / COUNT(*), 0) AS rata_rata FROM data_siswa", null);
        cursor.moveToFirst(); // abaikan, tetapi pastikan dijalankan tiap melakukan `SELECT`.

        // Contoh jika menggunakan MySQL.
        // > SELECT COUNT(*) as total_siswa, FORMAT(SUM((android + basis_data + web) / 3) / COUNT(*), 0) AS rata_rata FROM dhlh;
        // +-------------+-----------+
        // | total_siswa | rata_rata |
        // +-------------+-----------+
        // | 4           | 63        |
        // +-------------+-----------+
        //
        // Breakdown query
        // Jika disederhanakan query tersebut hanya akan menjadi
        // SELECT kolom_1, kolom_2 FROM data_siswa;
        //
        // kolom_1
        // COUNT(*) as total_siswa
        // digunakan untuk menghitung total data yang ada pada table.
        //
        // kolom 2
        // FORMAT(SUM((android + basis_data + web) / 3) / COUNT(*), 0)
        // disini terdapat dua kali menghitung rata-rata
        //
        // yang pertama menghitung rata-rata nilai tiap murid:
        // (android + basis_data + web) / 3)
        //
        // kemudian menjumlahkan semuanya dengan dengan rata-rata murid
        // lain dan membaginya dengan jumlah murid yang ada dengan fungsi `SUM`
        // SUM((android + basis_data + web) / 3))
        //
        // lalu menggunakan `FORMAT` agar rata_rata tidak memiliki koma.
        // Contoh:
        //
        // 1
        // SELECT FORMAT(123.456, 0);
        // Output:
        // +--------------------+
        // | FORMAT(123.456, 0) |
        // +--------------------+
        // | 123                |
        // +--------------------+
        //
        // 2
        // SELECT FORMAT(3.141592653589793, 2);
        // Output:
        // +------------------------------+
        // | FORMAT(3.141592653589793, 2) |
        // +------------------------------+
        // | 3.14                         |
        // +------------------------------+
        //
        // Jika tidak menggunakan AS / Alias, maka output akan seperti ini:
        // +----------+-------------------------------------------------------------+
        // | COUNT(*) | FORMAT(SUM((android + basis_data + web) / 3) / COUNT(*), 0) |
        // +----------+-------------------------------------------------------------+
        // | 4        | 63                                                          |
        // +----------+-------------------------------------------------------------+

        // `cursor` berisi data dari hasil query `SELECT` sebelumnya.
        String jumlah_murid = cursor.getString(0); // 0 maksudnya total_siswa
        String rata_rata = cursor.getString(1); // 1 maksudnya rata_rata

        total_rata_rata.setText("Total rata-rata: " + rata_rata);
        total_jumlah_murid.setText("Jumlah murid: " + jumlah_murid);

        // selalu di akhir!.
        cursor.close(); // abaikan, tapi pastikan dijalankan tiap ada cursor.
    }

    public void show_list() {
        Cursor cursor = db.rawQuery("SELECT nama, (android + basis_data + web) / 3 as rata_rata FROM data_siswa", null);
        // Untuk penjelasan query, baca fungsi `show_total()`.

        // Operasi terhadap cursor harus didalam kondisi berikut agar tidak terjadi error seperti
        // android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0.
        // sederhananya jika data ada, program didalam blok if akan dijalankan.
        // jika tidak program didalam blok else yang akan dijalankan.

        if (cursor.moveToFirst()) {

            // Untuk menampilkan data dengan ListView, diperlukan Adapter agar-agar

            // Membuat adapter berdasarkan data pada database.
            ArrayList<HashMap<String, String>> list_data = new ArrayList<>();

            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("nama", cursor.getString(0));
                map.put("rata_rata", cursor.getString(1));

                list_data.add(map);
            } while (cursor.moveToNext());

            String[] kunci = new String[]{"nama", "rata_rata"};
            int[] id = new int[]{R.id.list_nama, R.id.list_nilai};

            SimpleAdapter adapter = new SimpleAdapter(
                    this, list_data, R.layout.list_data, kunci, id
            );

            list_view.setAdapter(adapter);
            cursor.close();
        }

        else {
            // Menghapus adapter untuk ListView agar teks 'Tidak ada data' tampil.
            list_view.setAdapter(null);
        }
    }

    public void test() {
        // abaikan.
        ((EditText) dialog.findViewById(R.id.input_nama)).setText("Aspian");

        for (int input_id : new int[]{
                R.id.input_android,
                R.id.input_basis_data,
                R.id.input_web
        }) {
            ((EditText) dialog.findViewById(input_id)).setText("100");
        }
    }
}