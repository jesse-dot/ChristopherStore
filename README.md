# ChristopherStore
A third party App Store for Android

## Overview

ChristopherStore is a clean and user-friendly Android application that allows users to browse, download, and install third-party apps (APK files). The app features a homepage listing available apps and detailed pages for each app with metadata display and installation capabilities.

## Features

### Homepage - App List
- Clean RecyclerView-based list of available apps
- Each app card displays:
  - App name
  - Short description
  - Version number
  - File size
- Tap on any app card to view details

### App Details Page
- Comprehensive app information:
  - Full app name
  - Complete description
  - Version number
  - Package name
  - File size
- Download & Install button
- Progress indicator during download

### APK Download & Installation
- **HTTP(S) Download Support**: Downloads APK files via Android's DownloadManager
- **Permission Handling**: 
  - Requests storage permissions (Android 6.0-9.0)
  - Handles "Install Unknown Apps" permission (Android 8.0+)
  - Guides users to enable installation from unknown sources
- **APK Installation**: 
  - Uses FileProvider for secure file sharing (Android 7.0+)
  - Invokes Android's PackageInstaller for APK installation
  - Handles installation across different Android versions

## Project Structure

```
ChristopherStore/
├── app/
│   ├── build.gradle                 # App-level Gradle configuration
│   ├── proguard-rules.pro           # ProGuard rules
│   └── src/main/
│       ├── AndroidManifest.xml      # App manifest with permissions
│       ├── java/com/christopherstore/app/
│       │   ├── App.java             # App data model
│       │   ├── MainActivity.java    # Homepage with app list
│       │   ├── AppListAdapter.java  # RecyclerView adapter
│       │   └── AppDetailsActivity.java  # App details & download
│       └── res/
│           ├── drawable/
│           │   └── button_primary.xml
│           ├── layout/
│           │   ├── activity_main.xml
│           │   ├── activity_app_details.xml
│           │   └── item_app.xml
│           ├── mipmap-mdpi/
│           │   ├── ic_launcher.xml
│           │   └── ic_launcher_round.xml
│           ├── values/
│           │   ├── colors.xml
│           │   ├── strings.xml
│           │   └── themes.xml
│           └── xml/
│               └── file_paths.xml   # FileProvider paths
├── build.gradle                     # Project-level Gradle configuration
└── settings.gradle                  # Gradle settings
```

## Technical Details

### Permissions Required
- `INTERNET`: For downloading APK files
- `WRITE_EXTERNAL_STORAGE`: For saving APKs (Android 6.0-9.0)
- `READ_EXTERNAL_STORAGE`: For reading APKs (Android 6.0-12)
- `REQUEST_INSTALL_PACKAGES`: For installing APKs

### Minimum Requirements
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 13 (API 33)
- **Compile SDK**: Android 13 (API 33)

### Dependencies
- AndroidX AppCompat 1.6.1
- Material Components 1.9.0
- ConstraintLayout 2.1.4
- RecyclerView 1.3.0
- CardView 1.0.0

## Building the App

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8 or later
- Android SDK with API 33

### Build Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/jesse-dot/ChristopherStore.git
   cd ChristopherStore
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the ChristopherStore directory
   - Wait for Gradle sync to complete

3. **Build the app**:
   ```bash
   ./gradlew build
   ```

4. **Install on device/emulator**:
   ```bash
   ./gradlew installDebug
   ```

   Or use Android Studio's Run button (Shift+F10)

## Usage

1. **Launch the app**: Open ChristopherStore from your app drawer
2. **Browse apps**: Scroll through the list of available apps on the homepage
3. **View details**: Tap any app to see detailed information
4. **Download & Install**:
   - Tap the "Download & Install" button
   - Grant storage permission if prompted (Android 6.0-9.0)
   - Wait for download to complete
   - If prompted, enable "Install from Unknown Sources" in Settings
   - Follow the installation prompts

## Customization

### Adding Apps to the Store

Currently, the app uses sample data in `MainActivity.createSampleApps()`. To connect to a real backend:

1. Replace the sample data with API calls to your server
2. Update the `App` model if needed for additional metadata
3. Consider adding image loading for app icons and screenshots (e.g., using Glide or Picasso)

### Styling

- **Colors**: Edit `app/src/main/res/values/colors.xml`
- **Themes**: Edit `app/src/main/res/values/themes.xml`
- **Strings**: Edit `app/src/main/res/values/strings.xml`
- **Layouts**: Modify XML files in `app/src/main/res/layout/`

## Security Considerations

- The app uses `usesCleartextTraffic="true"` to support HTTP downloads. For production, use HTTPS only.
- APK downloads should be verified (checksum, signature) before installation.
- Consider implementing app signing verification.
- The FileProvider configuration ensures secure file sharing on modern Android versions.

## License

This project is open source and available for educational and commercial use.
