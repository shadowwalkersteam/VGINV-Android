package com.techno.vginv.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.FullscreenImageView;
import com.techno.vginv.Model.ProjectAssets;
import com.techno.vginv.R;
import com.techno.vginv.features.demo.def.DefaultMessagesActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.ViewHolder> {
    private Context mContext;
    private List<ProjectAssets> assets;
    private String fileUrl = "";

    public AttachmentsAdapter(Context context, List<ProjectAssets> newsList) {
        mContext = context;
        assets = newsList;
    }

    @NonNull
    @Override
    public AttachmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachments_adapter, parent, false);
        return new AttachmentsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentsAdapter.ViewHolder holder, int position) {
        final ProjectAssets currentNews = assets.get(position);
        if (currentNews.getPath().endsWith("jpg") || currentNews.getPath().endsWith("jpeg") || currentNews.getPath().endsWith("png")) {
            Glide.with(mContext.getApplicationContext())
                    .load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + currentNews.getPath())
                    .into(holder.thumbnailImageView);
            holder.title.setText("Attachment" + position++ + ".png");
        } else {
            holder.thumbnailImageView.setVisibility(View.GONE);
            holder.title.setText("Attachment" + position++ + ".mp4");
        }

        holder.download.setOnClickListener(v -> {
            if (currentNews.getPath().endsWith("jpg") || currentNews.getPath().endsWith("jpeg") || currentNews.getPath().endsWith("png")) {
                new DownloadsImage().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + currentNews.getPath());
            } else {
                new DownloadFileFromURL().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + currentNews.getPath());
            }
        });

        holder.thumbnailImageView.setOnClickListener(v -> {
//            FullscreenImageView.open(mContext, ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + currentNews.getPath(), true);
            if (currentNews.getPath().endsWith("jpg") || currentNews.getPath().endsWith("jpeg") || currentNews.getPath().endsWith("png")) {
                new DownloadsImage().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + currentNews.getPath(), "hello", "hello");
            } else {
                new DownloadFileFromURL().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + currentNews.getPath());
            }
        });

        holder.title.setOnClickListener(v -> {
            if (currentNews.getPath().endsWith("jpg") || currentNews.getPath().endsWith("jpeg") || currentNews.getPath().endsWith("png")) {
                new DownloadsImage().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + currentNews.getPath());
            } else {
                new DownloadFileFromURL().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + currentNews.getPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return assets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private CardView parent;
        private ImageView download, thumbnailImageView;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail);
            parent = itemView.findViewById(R.id.card_view);
            download = itemView.findViewById(R.id.download);
        }
    }

    class DownloadsImage extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String pathFolder = "";
        String pathFile = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
            pd.setTitle(mContext.getResources().getString(R.string.downloading));
            pd.setMessage(mContext.getResources().getString(R.string.pleasewait));
            pd.setMax(100);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            int count;
//            try {
//                url = new URL(strings[0]);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            if (url != null) {
//                fileUrl = url.toString();
//            }
//            Bitmap bm = null;
//            try {
//                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/VGINV");
//
//            if(!path.exists()) {
//                path.mkdirs();
//            }
//
//            File imageFile = new File(path, System.currentTimeMillis() + ".png");
//            FileOutputStream out = null;
//            try {
//                out = new FileOutputStream(imageFile);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            try{
//                bm.compress(Bitmap.CompressFormat.PNG, 100, out);
//                out.flush();
//                out.close();
//                MediaScannerConnection.scanFile(mContext, new String[] { imageFile.getAbsolutePath() }, null, (path1, uri) -> {
//                    // Log.i("ExternalStorage", "Scanned " + path + ":");
//                });
//            } catch(Exception e) {
//            }

            try {
                pathFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/VGINV").getAbsolutePath();
                pathFile = new File(pathFolder, System.currentTimeMillis() + ".png").getAbsolutePath();
                File futureStudioIconFile = new File(pathFolder);
                if(!futureStudioIconFile.exists()){
                    futureStudioIconFile.mkdirs();
                }
                url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                FileOutputStream output = new FileOutputStream(pathFile);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                MediaScannerConnection.scanFile(mContext, new String[] { pathFile }, null, (path1, uri) -> {
                    // Log.i("ExternalStorage", "Scanned " + path + ":");
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pd!=null) {
                pd.dismiss();
            }
            Toast.makeText(mContext, mContext.getResources().getString(R.string.file_saved), Toast.LENGTH_SHORT).show();
            FullscreenImageView.open(mContext, pathFile, true);
        }

        protected void onProgressUpdate(String... progress) {
            pd.setProgress(Integer.parseInt(progress[0]));
        }
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String pathFolder = "";
        String pathFile = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
            pd.setTitle("Processing...");
            pd.setMessage("Please wait.");
            pd.setMax(100);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                pathFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + "/VGINV").getAbsolutePath();
                pathFile = new File(pathFolder, System.currentTimeMillis() + ".mp4").getAbsolutePath();
                File futureStudioIconFile = new File(pathFolder);
                if(!futureStudioIconFile.exists()){
                    futureStudioIconFile.mkdirs();
                }
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                FileOutputStream output = new FileOutputStream(pathFile);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return pathFile;
        }

        protected void onProgressUpdate(String... progress) {
            pd.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pd!=null) {
                pd.dismiss();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pathFile));
            intent.setDataAndType(Uri.parse(pathFile), "video/*");
            mContext.startActivity(intent);
        }
    }
}