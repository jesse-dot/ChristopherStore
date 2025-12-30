package com.christopherstore.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {
    private List<App> apps;
    private Context context;

    public AppListAdapter(Context context, List<App> apps) {
        this.context = context;
        this.apps = apps;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        App app = apps.get(position);
        holder.appName.setText(app.getName());
        holder.appDescription.setText(app.getDescription());
        holder.appVersion.setText("v" + app.getVersion());
        holder.appSize.setText(app.getFormattedSize());

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AppDetailsActivity.class);
            intent.putExtra("app_name", app.getName());
            intent.putExtra("app_description", app.getDescription());
            intent.putExtra("app_version", app.getVersion());
            intent.putExtra("app_package", app.getPackageName());
            intent.putExtra("app_download_url", app.getDownloadUrl());
            intent.putExtra("app_size", app.getSize());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView appName;
        TextView appDescription;
        TextView appVersion;
        TextView appSize;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            appName = itemView.findViewById(R.id.app_name);
            appDescription = itemView.findViewById(R.id.app_description);
            appVersion = itemView.findViewById(R.id.app_version);
            appSize = itemView.findViewById(R.id.app_size);
        }
    }
}
