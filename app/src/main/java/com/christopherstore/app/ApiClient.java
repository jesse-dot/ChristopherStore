package com.christopherstore.app;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {
    private final String baseUrl;
    private final Context context;

    public ApiClient(Context context) {
        this.context = context;
        this.baseUrl = context.getString(R.string.api_base_url);
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    // Fetch all apps from the API
    public void getApps(ApiCallback<List<App>> callback) {
        new Thread(() -> {
            try {
                String response = makeRequest("/apps");
                JSONObject json = new JSONObject(response);
                JSONArray appsArray = json.getJSONArray("apps");
                
                List<App> apps = new ArrayList<>();
                for (int i = 0; i < appsArray.length(); i++) {
                    JSONObject appJson = appsArray.getJSONObject(i);
                    apps.add(parseApp(appJson));
                }
                
                callback.onSuccess(apps);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    // Fetch a specific app by ID
    public void getApp(String id, ApiCallback<App> callback) {
        new Thread(() -> {
            try {
                String response = makeRequest("/apps/" + id);
                JSONObject json = new JSONObject(response);
                JSONObject appJson = json.getJSONObject("app");
                
                App app = parseAppDetails(appJson);
                callback.onSuccess(app);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    // Make HTTP GET request
    private String makeRequest(String endpoint) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP error: " + responseCode);
        }
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();
        
        return response.toString();
    }

    // Parse app from JSON (simplified version from /apps endpoint)
    private App parseApp(JSONObject json) throws Exception {
        return new App(
            json.getString("name"),
            json.getString("description"),
            json.getString("latest_version"),
            json.getString("package_name"),
            baseUrl + "/apps/" + json.getString("id") + "/" + 
                json.getString("latest_version") + "/download",
            json.optString("icon_url", ""),
            new ArrayList<>(),
            json.getLong("size")
        );
    }

    // Parse app with full details from /apps/:id endpoint
    private App parseAppDetails(JSONObject json) throws Exception {
        List<String> screenshots = new ArrayList<>();
        JSONArray screenshotsArray = json.optJSONArray("screenshots");
        if (screenshotsArray != null) {
            for (int i = 0; i < screenshotsArray.length(); i++) {
                screenshots.add(screenshotsArray.getString(i));
            }
        }
        
        // Get the latest version details
        JSONArray versions = json.getJSONArray("versions");
        JSONObject latestVersion = versions.getJSONObject(0);
        
        return new App(
            json.getString("name"),
            json.getString("description"),
            latestVersion.getString("version"),
            json.getString("package_name"),
            baseUrl + "/apps/" + json.getString("id") + "/" + 
                latestVersion.getString("version") + "/download",
            json.optString("icon_url", ""),
            screenshots,
            latestVersion.getLong("size")
        );
    }
}
