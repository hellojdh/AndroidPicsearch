package hellojdh.wintercoding.net;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/*
 * FileDownload
 */
public class FileDownload {
    private static final String TAG = FileDownload.class.getSimpleName();

    public static void downFile(File target, ResponseBody body, Context context){
        InputStream is = null;
        OutputStream fos = null;
        try {
            try {
                is = body.byteStream();
                Log.d(TAG, "File target=" + target.getAbsolutePath());
                Log.d(TAG, "File size=" + body.contentLength());
                fos = new FileOutputStream(target);
                byte[] data = new byte[(int) body.contentLength()];
                int cnt = 0;
                while ((cnt = is.read(data)) > 0)
                    fos.write(data, 0, cnt);
                fos.flush();
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+target)));
                Log.d(TAG,"다운로드 성공");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"다운로드 실패");
            } finally {
                if (is != null) is.close();
                if (fos != null) fos.close();
            }
        }catch (Exception e){}
    }
}
