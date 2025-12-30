# ChristopherStore - Architecture Overview

## Application Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        MainActivity                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    App Store Homepage                     │  │
│  │                                                            │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │  RecyclerView - List of Available Apps           │    │  │
│  │  │                                                    │    │  │
│  │  │  ┌─────────────────────────────────────────┐     │    │  │
│  │  │  │ Sample Calculator      │ v1.0.0 │ 2.4MB│ ◄──────┐  │  │
│  │  │  └─────────────────────────────────────────┘     │    │  │
│  │  │  ┌─────────────────────────────────────────┐     │    │  │
│  │  │  │ Note Keeper           │ v2.1.5 │ 5.0MB│     │    │  │
│  │  │  └─────────────────────────────────────────┘     │    │  │
│  │  │  ┌─────────────────────────────────────────┐     │    │  │
│  │  │  │ Weather Now           │ v3.2.1 │ 8.0MB│     │    │  │
│  │  │  └─────────────────────────────────────────┘     │    │  │
│  │  │                                                    │    │  │
│  │  └────────────────────────────────────────────────┬─┘    │  │
│  │                                                     │      │  │
│  └─────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────┬───────────────────────────┘
                                      │
                                      │ Click on App Card
                                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                     AppDetailsActivity                           │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   App Details Page                        │  │
│  │                                                            │  │
│  │  App Name:        Sample Calculator                       │  │
│  │  Version:         1.0.0                                   │  │
│  │  Package:         com.example.calculator                  │  │
│  │  Size:            2.4 MB                                  │  │
│  │  ──────────────────────────────────────────────────       │  │
│  │  Description:                                             │  │
│  │  A simple and elegant calculator app for everyday        │  │
│  │  calculations                                             │  │
│  │                                                            │  │
│  │  [████████████████] ← Progress Bar (when downloading)     │  │
│  │                                                            │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │        📥 Download & Install                      │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │                                                            │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────┬───────────────────────────┘
                                      │
                                      │ Click Download & Install
                                      ▼
                            ┌──────────────────┐
                            │ Permission Check  │
                            └────────┬──────────┘
                                     │
                  ┌──────────────────┴──────────────────┐
                  │                                      │
                  ▼                                      ▼
        ┌─────────────────┐                  ┌─────────────────┐
        │ Storage Perm OK │                  │ Request Storage │
        │ (Android 6-9)   │                  │   Permission    │
        └────────┬─────────┘                  └────────┬────────┘
                 │                                      │
                 │         ┌────────────────────────────┘
                 │         │
                 ▼         ▼
        ┌─────────────────────────┐
        │  Download APK via       │
        │  DownloadManager        │
        │  (HTTP/HTTPS)           │
        └────────┬─────────────────┘
                 │
                 │ Download Complete
                 ▼
        ┌─────────────────────────┐
        │  Check Install Perm     │
        │  (Android 8.0+)         │
        └────────┬─────────────────┘
                 │
    ┌────────────┴────────────┐
    │                         │
    ▼                         ▼
┌────────────┐       ┌─────────────────────┐
│ Perm OK    │       │ Show Settings Dialog │
└──────┬─────┘       │ "Enable Unknown Apps"│
       │             └──────────┬───────────┘
       │                        │
       │                        │ User Enables in Settings
       │                        │
       └────────────┬───────────┘
                    ▼
           ┌─────────────────┐
           │  Install APK    │
           │  via FileProvider│
           │  & PackageInstaller│
           └─────────────────┘
```

## Key Components

### 1. Data Model
- **App.java**: Represents an app with metadata (name, description, version, download URL, etc.)

### 2. Activities
- **MainActivity.java**: 
  - Entry point of the app
  - Displays list of available apps using RecyclerView
  - Contains sample app data

- **AppDetailsActivity.java**:
  - Shows detailed information about selected app
  - Handles APK download using DownloadManager
  - Manages permissions (storage, install packages)
  - Triggers APK installation

### 3. Adapters
- **AppListAdapter.java**:
  - RecyclerView adapter for app list
  - Handles click events to navigate to details

### 4. Layouts
- **activity_main.xml**: Homepage layout with RecyclerView
- **item_app.xml**: Individual app card layout
- **activity_app_details.xml**: App details page layout

## Permission Flow

### Storage Permission (Android 6.0-9.0)
```
User clicks Download → Check WRITE_EXTERNAL_STORAGE
                     ↓
              Permission Granted? 
                     ↓
           Yes → Download     No → Request Permission
```

### Install Permission (Android 8.0+)
```
Download Complete → Check canRequestPackageInstalls()
                  ↓
           Permission OK?
                  ↓
    Yes → Install APK    No → Show Settings Dialog
                              ↓
                    User enables in Settings
                              ↓
                         Install APK
```

## Technical Implementation Details

### APK Download
- Uses Android's **DownloadManager** for reliable background downloads
- Supports both HTTP and HTTPS URLs
- Shows notification when download completes
- Stores APKs in public Downloads directory

### APK Installation
- **Android 6.0 and below**: Direct file URI
- **Android 7.0+**: Uses FileProvider for secure URI sharing
- **Android 8.0+**: Checks and requests "Install Unknown Apps" permission
- Launches system PackageInstaller for actual installation

### File Provider Configuration
- Defined in `AndroidManifest.xml`
- Authority: `com.christopherstore.app.fileprovider`
- Paths defined in `res/xml/file_paths.xml`
- Grants read URI permission for installation

## Security Considerations

1. **FileProvider**: Securely shares APK files without exposing internal paths
2. **Permission Checks**: Validates all required permissions before operations
3. **User Consent**: Uses system dialogs for critical permissions
4. **Download Verification**: Shows download progress and notifies on completion

## Future Enhancements

- Add image loading for app icons and screenshots (Glide/Picasso)
- Implement backend API integration for dynamic app catalog
- Add APK signature verification
- Support for app updates and version checking
- Search and filter functionality
- App categories and ratings
- User reviews and comments
