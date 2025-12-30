package com.christopherstore.app;

import java.util.List;

public class App {
    private String name;
    private String description;
    private String version;
    private String packageName;
    private String downloadUrl;
    private String iconUrl;
    private List<String> screenshots;
    private long size;

    public App(String name, String description, String version, String packageName, 
               String downloadUrl, String iconUrl, List<String> screenshots, long size) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.packageName = packageName;
        this.downloadUrl = downloadUrl;
        this.iconUrl = iconUrl;
        this.screenshots = screenshots;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public long getSize() {
        return size;
    }

    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
}
