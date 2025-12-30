package com.christopherstore.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppListAdapter adapter;
    private List<App> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data - in a real app, this would come from a server or database
        apps = createSampleApps();
        adapter = new AppListAdapter(this, apps);
        recyclerView.setAdapter(adapter);
    }

    private List<App> createSampleApps() {
        List<App> sampleApps = new ArrayList<>();
        
        sampleApps.add(new App(
                "Sample Calculator",
                "A simple and elegant calculator app for everyday calculations",
                "1.0.0",
                "com.example.calculator",
                "https://example.com/apps/calculator.apk",
                "",
                new ArrayList<>(),
                2457600  // 2.4 MB
        ));
        
        sampleApps.add(new App(
                "Note Keeper",
                "Keep track of your notes and ideas with this lightweight note-taking app",
                "2.1.5",
                "com.example.notekeeper",
                "https://example.com/apps/notekeeper.apk",
                "",
                new ArrayList<>(),
                5242880  // 5 MB
        ));
        
        sampleApps.add(new App(
                "Weather Now",
                "Get real-time weather updates and forecasts for your location",
                "3.2.1",
                "com.example.weather",
                "https://example.com/apps/weather.apk",
                "",
                new ArrayList<>(),
                8388608  // 8 MB
        ));
        
        sampleApps.add(new App(
                "Photo Gallery",
                "Browse and organize your photos with an intuitive interface",
                "1.5.0",
                "com.example.photogallery",
                "https://example.com/apps/photogallery.apk",
                "",
                new ArrayList<>(),
                12582912  // 12 MB
        ));
        
        sampleApps.add(new App(
                "Task Manager",
                "Manage your daily tasks and boost your productivity",
                "2.0.3",
                "com.example.taskmanager",
                "https://example.com/apps/taskmanager.apk",
                "",
                new ArrayList<>(),
                3145728  // 3 MB
        ));
        
        return sampleApps;
    }
}
