package id.my.aspian.l011;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Koneksi koneksi;
    Dialog dialog;

    Button tombol_tambah, tombol_refresh, tombol_simpan;

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
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_form);

        tombol_tambah = findViewById(R.id.tambah_data);
        tombol_refresh = findViewById(R.id.refresh_data);
        tombol_simpan = dialog.findViewById(R.id.simpan);

        tombol_tambah.setOnClickListener(view -> {
            dialog.show();
        });

        tombol_simpan.setOnClickListener(view -> {
            EditText dialog_nama = dialog.findViewById(R.id.input_nama);
            EditText dialog_android = dialog.findViewById(R.id.input_android);
            EditText dialog_basis_data = dialog.findViewById(R.id.input_basis_data);
            EditText dialog_web = dialog.findViewById(R.id.input_web);

            // no validation

            // Mengambil teks dan mengubahnya menjadi String.
            String nama = dialog_nama.getText().toString();
            String android = dialog_android.getText().toString();
            String basis_data = dialog_basis_data.getText().toString();
            String web = dialog_web.getText().toString();

            SQLiteDatabase db = koneksi.getWritableDatabase();
            // nama akan menggantikan '?' pertama,
            // android akan menggantikan '?' kedua dan seterusnya.
            db.execSQL(
                    "INSERT INTO data_siswa VALUES (?, ?, ?, ?)",
                    new String[]{nama, android, basis_data, web}
            );

            dialog.dismiss();
        });

        // Menghapus semua data ketika tombol refresh ditekan lama.
        tombol_refresh.setOnLongClickListener(view -> {
            SQLiteDatabase db = koneksi.getWritableDatabase();

            db.execSQL("DELETE FROM data_siswa");

            return true;
        });



        test(); // abaikan
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