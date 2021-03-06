package elegion.com.secondappforcontentprovidertest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String URI_PREFIX = "content://com.elegion.roomdatabase.musicprovider/";

    String[] entities = {"Albums", "Songs", "AlbumSongs"};
    String[] actions = {"query", "insert", "update", "delete"};
    private Spinner spinnerEnities;
    private Spinner spinnerActions;
    private Button buttonAction;
    private EditText editTextId;
    private EditText etVal1;
    private EditText etVal2;
    private EditText etVal3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> adapterEnities = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, entities);
        adapterEnities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEnities = findViewById(R.id.spinner_entity);
        spinnerEnities.setAdapter(adapterEnities);
        spinnerEnities.setSelection(0);

        ArrayAdapter<String> adapterActions = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actions);
        adapterActions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActions = findViewById(R.id.spinner_action);
        spinnerActions.setAdapter(adapterActions);
        spinnerActions.setSelection(0);

        editTextId = findViewById(R.id.et_id);
        etVal1 = findViewById(R.id.et_val1);
        etVal2 = findViewById(R.id.et_val2);
        etVal3 = findViewById(R.id.et_val3);

        buttonAction = findViewById(R.id.btn_action);
        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action = (String)spinnerActions.getSelectedItem();
                String enity = (String)spinnerEnities.getSelectedItem();
                String id = editTextId.getText().toString();

                if(!isInteger(id) && (action.equals("delete") || action.equals("update"))) {
                    showText("Ошибка id");
                    return;
                }

                if(TextUtils.isEmpty(id) && (action.equals("delete") || action.equals("update"))) {
                    showText("Действия delete и update с пустыми id невозможны!");
                    return;
                }

                switch (action) {
                    case "query" :
                        actionQuery(enity, id);
                        break;
                    case "insert" :
                        actionInsert(enity);
                        break;
                    case "update" :
                        actionUpdate(enity, id);
                        break;
                    case "delete" :
                        actionDelete(enity, id);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    //>>Query>>
    private void actionQuery(String enity, String id) {
        switch (enity) {
            case "Albums" :
                getContentQueryAndShowInfo("album", id,  "id", "name", "release");
                break;

            case "Songs" :
                getContentQueryAndShowInfo("song", id,  "id", "name", "duration");
                break;

            case "AlbumSongs" :
                getContentQueryAndShowInfo("albumsong", id,  "id", "album_id", "song_id");
                break;
            default:
                break;

        }
    }

    private void getContentQueryAndShowInfo(String table, String id, String col1, String col2, String col3) {
        Cursor data = getContentResolver().query(Uri.parse(TextUtils.isEmpty(id) ? URI_PREFIX + table : URI_PREFIX + table + "/" + id),
                null,
                null,
                null,
                null);
        if (data != null && data.moveToFirst()) {
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(data.getString(data.getColumnIndex(col1)) + " " +
                        data.getString(data.getColumnIndex(col2)) + " " +
                        data.getString(data.getColumnIndex(col3))
                ).append("\n");
            } while (data.moveToNext());
            showText(builder.toString());
        }
    }
    //<<Query<<

    //>>Delete>>
    private void actionDelete(String enity, String id) {
        switch (enity) {
            case "Albums" :
                getContentDelete("album", id);
                break;

            case "Songs" :
                getContentDelete("song", id);
                break;

            case "AlbumSongs" :
                getContentDelete("albumsong", id);
                break;
            default:
                break;

        }
    }

    private void getContentDelete(String table, String id) {
        try {
            getContentResolver().delete(Uri.parse(URI_PREFIX + table + "/" + id), null, null);
        } catch (Exception ex) {
            showText(ex.getMessage());
        }
    }
    //<<Delete<<

    //>>Insert>>
    private void actionInsert(String enity) {
        switch (enity) {
            case "Albums" :
                getContentInsert("album", "id", "name", "release");
                break;

            case "Songs" :
                getContentInsert("song", "id", "name", "duration");
                break;

            case "AlbumSongs" :
                getContentInsert("albumsong", "id", "album_id", "song_id");
                break;
            default:
                break;

        }
    }

    private void getContentInsert(String table, String col1, String col2, String col3) {
        ContentValues values = new ContentValues();
        values.put(col1, etVal1.getText().toString());
        values.put(col2, etVal2.getText().toString());
        values.put(col3, etVal3.getText().toString());

        try {
            getContentResolver().insert(Uri.parse(URI_PREFIX + table), values);
        } catch (Exception e) {
            showText(e.getMessage());
        }

    }
    //<<Insert<<

    //>>Update>>
    private void actionUpdate(String enity, String id) {
        switch (enity) {
            case "Albums" :
                getContentUpdate("album", id,  "name", "release");
                break;

            case "Songs" :
                getContentUpdate("song", id,  "name", "duration");
                break;

            case "AlbumSongs" :
                getContentUpdate("albumsong", id, "album_id", "song_id");
                break;
            default:
                break;

        }
    }

    private void getContentUpdate(String table, String id, String col2, String col3) {
        ContentValues values = new ContentValues();
        //values.put("id", etVal1.getText().toString());
        values.put(col2, etVal2.getText().toString());
        values.put(col3, etVal3.getText().toString());

        try {
            getContentResolver().update(Uri.parse(URI_PREFIX + table + "/" + id), values, null, null);
        } catch (Exception e) {
            showText(e.getMessage());
        }

    }
    //<<Update<<

    private void showText(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
}
