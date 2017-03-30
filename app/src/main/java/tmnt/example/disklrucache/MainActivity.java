package tmnt.example.disklrucache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private Button save;
    private Button load;
    private Button remove;
    private ImageView show;
    DiskLruCache cache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save= (Button) findViewById(R.id.save);
        load= (Button) findViewById(R.id.load);
        remove= (Button) findViewById(R.id.remove);
        show= (ImageView) findViewById(R.id.show);

        create();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeCache(cache);
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                   show.setImageBitmap(readCache(cache));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void create() {

        File file = Util.getDiskCacheFile(this, "bitmap");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            cache = DiskLruCache.open(file, Util.getVersionCode(this), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Bitmap readCache(DiskLruCache cache) throws IOException {
        Bitmap bitmap=null;
        final String key = Util.md5(CommonUtil.URL);
        DiskLruCache.Snapshot snapshot=cache.get(key);
        if (snapshot!=null){
           InputStream in= snapshot.getInputStream(0);
            bitmap= BitmapFactory.decodeStream(in);
        }
        return bitmap;
    }

    private void writeCache(final DiskLruCache cache) {
       final String key = Util.md5(CommonUtil.URL);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DiskLruCache.Editor editor = cache.edit(key);
                    if (editor != null) {
                        OutputStream out = editor.newOutputStream(0);
                        out.write(HttpUtils.doGet(CommonUtil.URL, null, "utf-8"));
                        editor.commit();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            cache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            cache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
