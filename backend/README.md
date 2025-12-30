# ChristopherStore Backend API

A lightweight, serverless backend for the ChristopherStore Android app that can be deployed to GitHub Pages or Cloudflare Workers.

## Features

- **REST API** for app listing and metadata
- **Caching** with configurable TTL
- **CORS** enabled for cross-origin requests
- **Error handling** with proper HTTP status codes
- **Redirect support** for APK downloads
- **Zero dependencies** - pure JavaScript

## API Endpoints

### GET `/apps`
Returns a list of all available apps.

**Response:**
```json
{
  "apps": [
    {
      "id": "calculator",
      "name": "Sample Calculator",
      "description": "A simple and elegant calculator app",
      "package_name": "com.example.calculator",
      "icon_url": "https://...",
      "latest_version": "1.0.0",
      "size": 2457600
    }
  ]
}
```

### GET `/apps/:id`
Returns detailed information about a specific app.

**Response:**
```json
{
  "app": {
    "id": "calculator",
    "name": "Sample Calculator",
    "description": "...",
    "package_name": "com.example.calculator",
    "icon_url": "https://...",
    "screenshots": [...],
    "versions": [
      {
        "version": "1.0.0",
        "version_code": 1,
        "size": 2457600,
        "download_url": "https://...",
        "min_sdk": 24,
        "target_sdk": 33,
        "release_notes": "Initial release",
        "release_date": "2024-01-15"
      }
    ]
  }
}
```

### GET `/apps/:id/:version/download`
Redirects to the download URL for a specific version.

**Response:** HTTP 302 redirect to the APK download URL

## Deployment Options

### Option 1: GitHub Pages (Static Hosting)

1. **Enable GitHub Pages** in your repository settings
2. **Copy files** to your repository:
   ```bash
   cp backend/index.html docs/index.html
   cp manifest.json docs/manifest.json
   ```
3. **Configure GitHub Pages** to serve from `/docs` or root
4. **Access your API** at `https://yourusername.github.io/ChristopherStore/`

**Limitations:**
- Client-side routing (slower)
- No server-side caching
- Limited to GET requests

### Option 2: Cloudflare Workers (Recommended)

1. **Install Wrangler CLI:**
   ```bash
   npm install -g wrangler
   ```

2. **Login to Cloudflare:**
   ```bash
   wrangler login
   ```

3. **Deploy the worker:**
   ```bash
   cd backend
   wrangler deploy
   ```

4. **Your API will be available at:**
   ```
   https://christopherstore-api.your-subdomain.workers.dev
   ```

**Benefits:**
- Server-side routing and caching
- Better performance with edge computing
- Automatic HTTPS
- Global CDN distribution
- Free tier: 100,000 requests/day

### Option 3: Custom Domain

With Cloudflare Workers, you can use a custom domain:

1. Add your domain to Cloudflare
2. Update `wrangler.toml`:
   ```toml
   routes = [
     { pattern = "api.yourstore.com/*", zone_name = "yourstore.com" }
   ]
   ```
3. Deploy: `wrangler deploy`

## Configuration

### Update Manifest

Edit `manifest.json` at the repository root to add/update apps:

```json
{
  "apps": [
    {
      "id": "your-app-id",
      "name": "Your App Name",
      "description": "App description",
      "package_name": "com.example.yourapp",
      "icon_url": "https://your-icon-url.png",
      "screenshots": ["url1", "url2"],
      "versions": [
        {
          "version": "1.0.0",
          "version_code": 1,
          "size": 1234567,
          "download_url": "https://your-apk-url.apk",
          "min_sdk": 24,
          "target_sdk": 33,
          "release_notes": "What's new",
          "release_date": "2024-01-01"
        }
      ]
    }
  ]
}
```

### Caching

**Cloudflare Workers:**
- Modify `CACHE_DURATION` in `worker.js` (default: 3600 seconds)
- Uses Cloudflare's edge cache for fast global delivery

**GitHub Pages:**
- Browser caching only
- Clear cache by appending `?v=timestamp` to requests

## Android App Configuration

Update the Android app to use your backend URL.

### 1. Add Configuration File

Create `app/src/main/res/values/config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Backend API URL - Update this to your deployed backend -->
    <string name="api_base_url">https://christopherstore-api.workers.dev</string>
    
    <!-- Or for GitHub Pages: -->
    <!-- <string name="api_base_url">https://yourusername.github.io/ChristopherStore</string> -->
</resources>
```

### 2. Update MainActivity.java

Replace the `createSampleApps()` method with API calls:

```java
private void loadAppsFromApi() {
    String apiUrl = getString(R.string.api_base_url) + "/apps";
    
    new Thread(() -> {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Parse JSON and update UI on main thread
            runOnUiThread(() -> {
                parseAndDisplayApps(response.toString());
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}
```

## Security Considerations

1. **CORS**: Currently set to `*` for development. Restrict in production:
   ```javascript
   'Access-Control-Allow-Origin': 'https://your-android-app-domain.com'
   ```

2. **Rate Limiting**: Cloudflare Workers has built-in DDoS protection. For GitHub Pages, implement client-side throttling.

3. **HTTPS**: Both GitHub Pages and Cloudflare Workers provide HTTPS by default.

4. **APK Signatures**: Verify APK signatures in the Android app before installation.

## Testing

### Local Testing (Cloudflare Workers)

```bash
cd backend
wrangler dev
```

Access at `http://localhost:8787`

### Test Endpoints

```bash
# List apps
curl https://your-api-url/apps

# Get app details
curl https://your-api-url/apps/calculator

# Test download redirect
curl -L https://your-api-url/apps/calculator/1.0.0/download
```

## Monitoring

### Cloudflare Workers
- View analytics in Cloudflare dashboard
- See request count, errors, and latency
- Set up email alerts for errors

### GitHub Pages
- Use GitHub Pages analytics
- Implement Google Analytics for detailed tracking

## Costs

### GitHub Pages
- **Free** for public repositories
- Bandwidth: 100 GB/month
- Storage: 1 GB

### Cloudflare Workers
- **Free Tier**: 100,000 requests/day
- **Paid**: $5/month for 10M requests
- No bandwidth charges

## Troubleshooting

**Q: API returns 404 on GitHub Pages**
- Ensure `manifest.json` and `index.html` are in the correct directory
- Check GitHub Pages settings for correct source folder

**Q: CORS errors in Android app**
- Verify CORS headers are set correctly
- Test with curl first to isolate the issue

**Q: Cloudflare Worker not deploying**
- Check `wrangler.toml` configuration
- Ensure you're logged in: `wrangler whoami`
- Check Cloudflare account limits

## License

This backend is part of the ChristopherStore project and follows the same license.
