package com.playerhub.downloadservice;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadIntentService extends IntentService {

    private static final String TAG = "DownloadIntentService";

    private static final String DOWNLOAD_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Download_path";
    private static final String DOWNLOAD_POSITION = "com.spartons.androiddownloadmanager_DownloadSongService_Download_position";
    private static final String DESTINATION_PATH = "com.spartons.androiddownloadmanager_DownloadSongService_Destination_path";
    private static final String DESTINATION_CHAT_USER_POSITION = "com.spartons.androiddownloadmanager_DownloadSongService_chatUserPosition";


    public DownloadIntentService() {
        super("DownloadSongService");
    }

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath, @NonNull int position, @NonNull int chatUserPosition) {
        return new Intent(callingClassContext, DownloadIntentService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath)
                .putExtra(DOWNLOAD_POSITION, position)
                .putExtra(DESTINATION_CHAT_USER_POSITION, chatUserPosition);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        if (intent != null && intent.hasExtra(DOWNLOAD_PATH)) {

            String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
            int position = intent.getIntExtra(DOWNLOAD_POSITION, -1);
            int chatUserPosition = intent.getIntExtra(DESTINATION_CHAT_USER_POSITION, -1);
            String destinationPath = intent.getStringExtra(DESTINATION_PATH);
            startDownload(downloadPath, destinationPath, position, chatUserPosition);

        } else {

            Log.e(TAG, "onHandleIntent: no intent called..... ");

        }
    }

    private void startDownload(String downloadPath, String destinationPath, final int position, final int chatUserPosition) {

//        downloadPath = "https://firebasestorage.googleapis.com/v0/b/playerhub-prod.appspot.com/o/videos%2F20191911174647540933273550420?alt=media&token=7a2401eb-bc54-4963-b957-bf434be76cf5";
//
//
//        Log.e(TAG, "startDownload: " + downloadPath);


////        downloadPath = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.mp4";
////        Log.e(TAG, "startDownload: " + downloadPath);
////        Log.e(TAG, "startDownload: " + destinationPath);
////        Log.e(TAG, "startDownload: " + position);
////        String[] params = new String[]{downloadPath, String.valueOf(position)};
////        new DownloadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        String filename = downloadPath.substring(73, 111);
        File file = getExternalCacheDir();
        String tempDes = file.getPath() + "/Playerhub/videos/";
        String originalDes = file.getPath() + "/Playerhub/videos/";
        filename = filename + ".mp4";//System.currentTimeMillis() + "" + new Date().getTime() + ".mp4";
        String tempname = filename + ".temp";//System.currentTimeMillis() + "" + new Date().getTime() + ".mp4";
        tempDes = tempDes + tempname;
        originalDes = originalDes + filename;

        if (!new File(originalDes).exists()) {

            try {

                //create url and connect
                URL url = new URL(downloadPath);
                URLConnection connection = url.openConnection();
                File file1 = new File(tempDes);
                int downloaded = 0;


                if (file1.exists()) {
                    downloaded = (int) file1.length();
                    connection.setAllowUserInteraction(true);
                    connection.setRequestProperty("Range", "bytes=" + (file1.length()) + "-");
                }

//            // this will be useful so that you can show a typical 0-100% progress bar
//            int fileLength = connection.getContentLength();
//
//            if (downloaded < fileLength) {
////                connection.setAllowUserInteraction(true);
//                connection.setRequestProperty("Range", "bytes=" + (file1.length()) + "-");
//            } else {
//                throw new IOException("File Download already completed.");
//            }
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                connection.setConnectTimeout(14000);
//                connection.setReadTimeout(20000);
                connection.connect();

                int fileLength = connection.getContentLength();

                if (!(downloaded < fileLength + downloaded)) {
                    Long values[] = new Long[]{Long.valueOf(100), Long.valueOf(position), Long.valueOf(chatUserPosition)};
                    publishProgress(values);
                    throw new IOException("File Download already completed.");
                }


                Log.e(TAG, "startDownload: sizes  " + fileLength + "  " + downloaded);

                // download the file
                InputStream input = new BufferedInputStream(connection.getInputStream());

//            String path = "/sdcard/BarcodeScanner-debug.apk" ;
                OutputStream output;

                if (downloaded > 0) {
                    output = new FileOutputStream(tempDes, true);
                } else {
                    output = new FileOutputStream(tempDes);
                }


                byte data[] = new byte[1024];
                long totalDownload = downloaded + fileLength;
                long total = downloaded;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;

//                Log.e(TAG, "startDownload: " + total);
                    Long values[] = new Long[]{Long.valueOf((int) (total * 100 / totalDownload)), Long.valueOf(position), Long.valueOf(chatUserPosition)};
                    publishProgress(values);
                    output.write(data, 0, count);

                    // publishing the progress....
//                Bundle resultData = new Bundle();
//                resultData.putInt("progress", (int) (total * 100 / fileLength));
//                receiver.send(UPDATE_PROGRESS, resultData);
//                output.write(data, 0, count);
                }

                File file2 = new File(tempDes);
                file2.renameTo(new File(originalDes));
                // close streams
                output.flush();
                output.close();
                input.close();

                Long values[] = new Long[]{Long.valueOf(100), Long.valueOf(position), Long.valueOf(chatUserPosition)};
                publishProgress(values);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Long values[] = new Long[]{Long.valueOf(100), Long.valueOf(position), Long.valueOf(chatUserPosition)};
            publishProgress(values);
            Log.e(TAG, "File already downloaded");
        }


    }


//
//    FirebaseStorage storage = FirebaseStorage.getInstance();
//    StorageReference storageRef = storage.getReferenceFromUrl(downloadPath);
//
////        ProgressDialog pd = new ProgressDialog(this);
////        pd.setTitle("Nature.jpg");
////        pd.setMessage("Downloading Please Wait!");
////        pd.setIndeterminate(true);
////        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////        pd.show();
////
//
//    File file = new File(getExternalCacheDir().getPath() + "/Playerhub/videos/");
//
//        if (!file.exists()) {
//        file.mkdirs();
//    }
//
//
//    final File localFile = new File(file, "test.mp4");
//
//        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//        @Override
//        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//            Log.e("firebase ", ";local tem file created  created " + localFile.toString());
//
////                if (!isVisible()) {
////                    return;
////                }
////
////                if (localFile.canRead()) {
////
////                    pd.dismiss();
////                }
////
////                Toast.makeText(this, "Download Completed", Toast.LENGTH_SHORT).show();
////                Toast.makeText(this, "Internal storage/MADBO/Nature.jpg", Toast.LENGTH_LONG).show();
//
//        }
//    }).addOnFailureListener(new OnFailureListener() {
//        @Override
//        public void onFailure(@NonNull Exception exception) {
//            Log.e("firebase ", ";local tem file not created  created " + exception.toString());
////                Toast.makeText(this, "Download Incompleted", Toast.LENGTH_LONG).show();
//        }
//    });


    private void publishProgress(Long[] values) {

        Intent intent = new Intent("download_progress");
        intent.putExtra("percentage", values[0]);
        intent.putExtra("position", values[1]);
        intent.putExtra("chatUserPosition", values[2]);
        LocalBroadcastManager.getInstance(DownloadIntentService.this).sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
    }

    /* Todo Download file With resume test function


      try {

            //create url and connect
            URL url = new URL(downloadPath);
            URLConnection connection = url.openConnection();
            File file1 = new File(des);
            if (file1.exists()) {
//                downloaded = (int) file.length();
                connection.setRequestProperty("Range", "bytes=" + (file.length()) + "-");
            }
            connection.connect();


            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());

//            String path = "/sdcard/BarcodeScanner-debug.apk" ;
            OutputStream output = new FileOutputStream(des);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;

                Long values[] = new Long[]{Long.valueOf((int) (total * 100 / fileLength)), Long.valueOf(position)};
                publishProgress(values);
                output.write(data, 0, count);

                // publishing the progress....
//                Bundle resultData = new Bundle();
//                resultData.putInt("progress", (int) (total * 100 / fileLength));
//                receiver.send(UPDATE_PROGRESS, resultData);
//                output.write(data, 0, count);
            }

            // close streams
            output.flush();
            output.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


     */




    /* Todo File Download example

    try {
            File outputFile = new File(des, filename);
            URL url = new URL(downloadPath);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            // getting file length
            int lenghtOfFile = urlConnection.getContentLength();
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
//            long total = 0;
            long download_percentage_old = 00;
            while ((len1 = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, len1);

                long download_percentage_new = (100 * len1) / lenghtOfFile;
                if (download_percentage_old != download_percentage_new) {
                    download_percentage_old = download_percentage_new;
                    Long values[] = new Long[]{download_percentage_old, Long.valueOf(position)};
                    publishProgress(values);
                }

            }
            fos.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



     */


//    class DownloadingTask extends AsyncTask<String, Long, Void> {
//
//        @Override
//        protected Void doInBackground(String... urls) {
//
//            Log.d(TAG, "doInBackground: " + urls[0]);
//
//            String url = urls[0];
//            int position = Integer.valueOf(urls[1]);
//
//            int count;
//            try {
//
//                // String root = Environment.getExternalStorageDirectory().toString();
//
//                System.out.println("Downloading: " + url);
//                URL urlFormed = new URL(url);
//
//                URLConnection conection = urlFormed.openConnection();
//                conection.connect();
//
//                // getting file length
//                int lenghtOfFile = conection.getContentLength();
//
//                // input stream to read file - with 8k buffer
//                InputStream input = new BufferedInputStream(urlFormed.openStream(), 8192);
//
//                // Output stream to write file
//
//                //OutputStream output = new FileOutputStream(root+"temp.apk");
//                byte data[] = new byte[1024];
//
//                long total = 0;
//                long download_percentage_old = 00;
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//
//                    // writing data to file
//                    //output.write(data, 0, count);
//                    long download_percentage_new = (100 * total) / lenghtOfFile;
//                    if (download_percentage_old != download_percentage_new) {
//                        download_percentage_old = download_percentage_new;
//                        Long values[] = new Long[]{download_percentage_old, Long.valueOf(position)};
//                        publishProgress(values);
//                    }
//                }
//                // flushing output
//                //output.flush();
//
//                // closing streams
//                //output.close();
//                input.close();
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Long... values) {
//            super.onProgressUpdate(values);
//
//            Intent intent = new Intent("download_progress");
//            intent.putExtra("percentage", values[0]);
//            intent.putExtra("position", values[1]);
//            LocalBroadcastManager.getInstance(DownloadVideosWithResumeIntentService.this).sendBroadcast(intent);

//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            Log.d(TAG, "onPostExecute");
//            stopSelf();
//        }
//    }
}