package com.example.ac1_m;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText edtTitulo, edtAutor;
    private Spinner spnCategoria;
    private Switch switchLido;
    private Button btnSalvar;
    private ListView lvLivros;
    private BancoHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaLivros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtAutor = findViewById(R.id.edtAutor);
        spnCategoria = findViewById(R.id.spnCategoria);
        switchLido = findViewById(R.id.switchLido);
        btnSalvar = findViewById(R.id.btnSalvar);
        lvLivros = findViewById(R.id.lvLivros);

        dbHelper = new BancoHelper(this);
        listaLivros = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaLivros);
        lvLivros.setAdapter(adapter);

        String[] categorias = {"Ficção", "Técnico", "Autoajuda"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spnCategoria.setAdapter(spinnerAdapter);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = edtTitulo.getText().toString();
                String autor = edtAutor.getText().toString();
                String categoria = spnCategoria.getSelectedItem().toString();
                boolean lido = switchLido.isChecked();

                if (!titulo.isEmpty() && !autor.isEmpty()) {
                    dbHelper.inserirLivro(titulo, autor, categoria, lido);
                    carregarLivros();
                } else {
                    Toast.makeText(MainActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lvLivros.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String livroSelecionado = listaLivros.get(position);
                int idLivro = Integer.parseInt(livroSelecionado.split(" - ")[0]); // Pegando o ID
                dbHelper.excluirLivro(idLivro);
                carregarLivros();
                return true;
            }
        });

        carregarLivros();
    }

    private void carregarLivros() {
        listaLivros.clear();
        Cursor cursor = dbHelper.listarLivros();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String autor = cursor.getString(cursor.getColumnIndexOrThrow("autor"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                boolean lido = cursor.getInt(cursor.getColumnIndexOrThrow("lido")) == 1;

                listaLivros.add(id + " - " + titulo + " (" + categoria + ") - " + (lido ? "Lido" : "Não Lido"));
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}