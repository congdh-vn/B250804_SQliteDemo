package vn.haui.b250804_sqlitedemo;

import static java.util.stream.Collectors.mapping;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText edtId, edtName, edtPrice;
    private Button btnInsert, btnUpdate, btnDelete;
    private ListView lvProducts;
    private ArrayAdapter arrayAdapter;
    private List<Product> lstProducts;
    MyDatabaseHandle myDatabaseHandle;
    SQLiteDatabase sqLiteDatabase;
    private static final String DATABASE_NAME = "khong_gian_so2.db";


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
        mapping();
        //coppy csdl
        copyDatabase();

        myDatabaseHandle = new MyDatabaseHandle(MainActivity.this);

        //doc va hien thi du lieu len listView
        lstProducts = new ArrayList<>();
        lstProducts = myDatabaseHandle.getAllProduct(sqLiteDatabase, lstProducts);
        arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, lstProducts);
        lvProducts.setAdapter(arrayAdapter);

        if (!lstProducts.isEmpty()) {
            writeToView(lstProducts.get(0));
        }

        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                writeToView(lstProducts.get(i));
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long kq = myDatabaseHandle.insertProduct(sqLiteDatabase, readFromView());
                if (kq > 0) {
                    lstProducts = myDatabaseHandle.getAllProduct(sqLiteDatabase, lstProducts);
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Inserted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Inserted failute", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int kq = myDatabaseHandle.deleteProduct(sqLiteDatabase, Integer.valueOf(edtId.getText().toString()));
                if (kq > 0) {
                    lstProducts = myDatabaseHandle.getAllProduct(sqLiteDatabase, lstProducts);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Delete failute", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDatabaseHandle.updateProduct(sqLiteDatabase, readFromView());
                lstProducts = myDatabaseHandle.getAllProduct(sqLiteDatabase, lstProducts);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void writeToView(Product product) {
        edtId.setText(String.valueOf(product.getProductId()));
        edtName.setText(product.getProductName());
        edtPrice.setText(String.valueOf(product.getPrice()));
    }

    private Product readFromView() {
        Product obj = new Product();
        obj.setProductId(Integer.parseInt(edtId.getText().toString()));
        obj.setProductName(edtName.getText().toString());
        obj.setPrice(Float.parseFloat(edtPrice.getText().toString()));
        return obj;
    }

    private void mapping() {
        edtId = findViewById(R.id.edt_id);
        edtName = findViewById(R.id.edt_name);
        edtPrice = findViewById(R.id.edt_price);
        btnInsert = findViewById(R.id.btn_insert);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        lvProducts = findViewById(R.id.lv_products);
    }

    private void copyDatabase() {
        //tuong tac voi file thi can phai co try catch
        File dbPath = getDatabasePath(DATABASE_NAME);
        if (!dbPath.exists()) {
            dbPath.delete();
            dbPath.getParentFile().mkdirs(); // Tạo thư mục nếu chưa có
            InputStream is = null;
            OutputStream os = null;
            try {
                is = getAssets().open(DATABASE_NAME);
                os = new FileOutputStream(dbPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                Log.d("DBCopy", "Đã sao chép cơ sở dữ liệu thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DBCopy", "Lỗi khi sao chép cơ sở dữ liệu: " + e.getMessage());
            }
        } else {
            Log.e("DBcopy", "co so du lieu da co, khong sao chep");
        }
    }

}