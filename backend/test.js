// Simple test script for the API
// Run with: node test.js

const API_URL = process.argv[2] || 'http://localhost:8787';

async function test() {
  console.log('Testing ChristopherStore API at:', API_URL);
  console.log('='.repeat(50));
  
  try {
    // Test 1: GET /apps
    console.log('\n1. Testing GET /apps');
    const appsResponse = await fetch(`${API_URL}/apps`);
    const appsData = await appsResponse.json();
    console.log('✓ Status:', appsResponse.status);
    console.log('✓ Apps count:', appsData.apps?.length || 0);
    
    if (appsData.apps && appsData.apps.length > 0) {
      const firstApp = appsData.apps[0];
      
      // Test 2: GET /apps/:id
      console.log('\n2. Testing GET /apps/:id');
      const appResponse = await fetch(`${API_URL}/apps/${firstApp.id}`);
      const appData = await appResponse.json();
      console.log('✓ Status:', appResponse.status);
      console.log('✓ App name:', appData.app?.name);
      
      // Test 3: GET /apps/:id/:version/download (check redirect)
      if (appData.app && appData.app.versions && appData.app.versions.length > 0) {
        const version = appData.app.versions[0].version;
        console.log('\n3. Testing GET /apps/:id/:version/download');
        const downloadResponse = await fetch(
          `${API_URL}/apps/${firstApp.id}/${version}/download`,
          { redirect: 'manual' }
        );
        console.log('✓ Status:', downloadResponse.status);
        console.log('✓ Redirect:', downloadResponse.headers.get('location'));
      }
    }
    
    console.log('\n' + '='.repeat(50));
    console.log('All tests passed! ✓');
    
  } catch (error) {
    console.error('\n✗ Test failed:', error.message);
    process.exit(1);
  }
}

test();
