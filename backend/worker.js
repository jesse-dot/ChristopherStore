// Cloudflare Workers backend for ChristopherStore
// This worker serves the app manifest and provides REST endpoints

const CACHE_DURATION = 3600; // 1 hour in seconds

// CORS headers
const CORS_HEADERS = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': 'GET, OPTIONS',
  'Access-Control-Allow-Headers': 'Content-Type',
  'Access-Control-Max-Age': '86400',
};

// Main handler
addEventListener('fetch', event => {
  event.respondWith(handleRequest(event.request));
});

async function handleRequest(request) {
  const url = new URL(request.url);
  
  // Handle CORS preflight
  if (request.method === 'OPTIONS') {
    return new Response(null, { headers: CORS_HEADERS });
  }
  
  // Only allow GET requests
  if (request.method !== 'GET') {
    return jsonResponse({ error: 'Method not allowed' }, 405);
  }
  
  try {
    // Route handling
    if (url.pathname === '/apps' || url.pathname === '/apps/') {
      return await getApps();
    }
    
    const appMatch = url.pathname.match(/^\/apps\/([^\/]+)$/);
    if (appMatch) {
      return await getAppById(appMatch[1]);
    }
    
    const downloadMatch = url.pathname.match(/^\/apps\/([^\/]+)\/([^\/]+)\/download$/);
    if (downloadMatch) {
      return await getDownloadUrl(downloadMatch[1], downloadMatch[2]);
    }
    
    // Root endpoint - API info
    if (url.pathname === '/' || url.pathname === '') {
      return jsonResponse({
        name: 'ChristopherStore API',
        version: '1.0.0',
        endpoints: {
          apps: '/apps',
          app_detail: '/apps/:id',
          download: '/apps/:id/:version/download'
        }
      });
    }
    
    return jsonResponse({ error: 'Not found' }, 404);
    
  } catch (error) {
    console.error('Error handling request:', error);
    return jsonResponse({ error: 'Internal server error', message: error.message }, 500);
  }
}

// Fetch manifest data
async function getManifest() {
  // In Cloudflare Workers, you'd fetch this from KV storage or R2
  // For GitHub Pages, this would be served as a static file
  const manifestUrl = 'https://raw.githubusercontent.com/jesse-dot/ChristopherStore/main/manifest.json';
  
  const response = await fetch(manifestUrl, {
    cf: {
      cacheTtl: CACHE_DURATION,
      cacheEverything: true,
    }
  });
  
  if (!response.ok) {
    throw new Error('Failed to fetch manifest');
  }
  
  return await response.json();
}

// GET /apps - List all apps
async function getApps() {
  const manifest = await getManifest();
  
  const apps = manifest.apps.map(app => ({
    id: app.id,
    name: app.name,
    description: app.description,
    package_name: app.package_name,
    icon_url: app.icon_url,
    latest_version: app.versions[0]?.version || 'unknown',
    size: app.versions[0]?.size || 0,
  }));
  
  return jsonResponse({ apps }, 200, { 'Cache-Control': `public, max-age=${CACHE_DURATION}` });
}

// GET /apps/:id - Get app details
async function getAppById(id) {
  const manifest = await getManifest();
  const app = manifest.apps.find(a => a.id === id);
  
  if (!app) {
    return jsonResponse({ error: 'App not found' }, 404);
  }
  
  return jsonResponse({ app }, 200, { 'Cache-Control': `public, max-age=${CACHE_DURATION}` });
}

// GET /apps/:id/:version/download - Redirect to download URL
async function getDownloadUrl(id, version) {
  const manifest = await getManifest();
  const app = manifest.apps.find(a => a.id === id);
  
  if (!app) {
    return jsonResponse({ error: 'App not found' }, 404);
  }
  
  const versionData = app.versions.find(v => v.version === version);
  
  if (!versionData) {
    return jsonResponse({ error: 'Version not found' }, 404);
  }
  
  // Redirect to the actual download URL
  return Response.redirect(versionData.download_url, 302);
}

// Helper to create JSON responses with CORS
function jsonResponse(data, status = 200, additionalHeaders = {}) {
  return new Response(JSON.stringify(data, null, 2), {
    status,
    headers: {
      'Content-Type': 'application/json',
      ...CORS_HEADERS,
      ...additionalHeaders,
    },
  });
}
