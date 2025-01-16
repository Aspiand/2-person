package id.my.aspian.l011;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Koneksi extends SQLiteOpenHelper {
    public Koneksi(@Nullable Context context) {
        // `DATAAAA` merupakan nama database.
        // abaikan konteks, factory, dan version
        super(context, "DATAAAA", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS data_siswa (" +
                        "nama varchar(255) PRIMARY KEY," +
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