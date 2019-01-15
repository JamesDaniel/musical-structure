package irish.james.musicalstructure.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import irish.james.musicalstructure.R;
import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {

  private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 11111;

  private ArrayAdapter<String> adapter;
  ArrayList<String> listItems=new ArrayList<String>();

  private Button playBtn;
  private Button prevBtn;
  private Button nextBtn;
  private ListView list;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_music_list);

    playBtn = findViewById(R.id.playBtn);
    prevBtn = findViewById(R.id.prevBtn);
    nextBtn = findViewById(R.id.nextBtn);
    list = findViewById(R.id.list);

    playBtn.setOnClickListener(v -> launchMusicPlayerActivity());

    requestPermissionForReadExtertalStorage();
  }

  private void launchMusicPlayerActivity() {
    Intent intent = new Intent(this, MusicPlayerActivity.class);
    startActivity(intent);
  }

  private void searchMusic() {
    Log.d("asd", "123");
    Cursor music = getContentResolver().query(
        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        new String[]{ "_id", "_data" },
        "is_music != 0",
        null, null);

    while (music.moveToNext()) {
      listItems.add(music.getString(0) + ": " + music.getString(1));
    }
    adapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_list_item_1,
        listItems);
    list.setAdapter(adapter);

    list.setOnItemClickListener((parent, view, position, id) -> {
      onItemClick(view);
    });
  }

  private void onItemClick(View view) {
    Uri openable = Uri.parse(Audio.Media.getContentUri("external").toString() + "/" + (((TextView)view).getText().toString().split(": ")[0]));
    Intent intent = new Intent(Intent.ACTION_VIEW);

    intent.setDataAndType(openable, "audio/*");
    startActivity(intent);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
      searchMusic();
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  public void requestPermissionForReadExtertalStorage() {
    try {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
          READ_STORAGE_PERMISSION_REQUEST_CODE);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
