# Google Maps Setup for Driverr Mobile App

## Overview
The Driverr mobile app now includes Google Maps integration in three activities:
1. **MainActivity** - Read-only map view (can pan and zoom)
2. **RideOrderActivity** - Interactive map with pickup (green) and destination (red) markers
3. **FavoriteRoutesActivity** - Interactive map with pickup, destination, and stop markers

## Prerequisites
- Google Cloud Platform account
- Google Maps Android API enabled

## Setup Instructions

### 1. Get Google Maps API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the **Maps SDK for Android** API:
   - Navigate to "APIs & Services" > "Library"
   - Search for "Maps SDK for Android"
   - Click "Enable"
4. Create an API key:
   - Go to "APIs & Services" > "Credentials"
   - Click "Create Credentials" > "API Key"
   - Copy the generated API key
5. (Recommended) Restrict the API key:
   - Click on the API key to edit it
   - Under "Application restrictions", select "Android apps"
   - Add your app's package name: `com.example.driverr_mobile`
   - Add your SHA-1 certificate fingerprint (get it using `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`)

### 2. Add API Key to AndroidManifest.xml

1. Open `frontend/mobile/app/src/main/AndroidManifest.xml`
2. Find the line:
   ```xml
   android:value="YOUR_GOOGLE_MAPS_API_KEY" />
   ```
3. Replace `YOUR_GOOGLE_MAPS_API_KEY` with your actual API key from step 1

### 3. Build and Run

```bash
cd frontend/mobile
./gradlew clean
./gradlew installDebug
```

Or on Windows:
```bash
cd frontend\mobile
.\gradlew.bat clean
.\gradlew.bat installDebug
```

## Map Features

### MainActivity
- **View Only**: Displays a map centered on Novi Sad, Serbia (45.2671, 19.8335)
- **Pan/Zoom**: Users can move around and zoom the map
- **No Markers**: No interaction beyond viewing

### RideOrderActivity
- **Interactive Map**: Click anywhere on the map to select pickup/destination
- **Search Addresses**: Use text search to find locations
- **Green Marker**: Pickup location
- **Red Marker**: Destination location
- **Reverse Geocoding**: Click on map to get address for that location
- **Auto-Assignment**: First click assigns pickup, second click assigns destination

### FavoriteRoutesActivity
- **Save Routes**: Create favorite routes with pickup, destination, and optional stops
- **Interactive Map**: Same as RideOrderActivity
- **Multiple Markers**: Green (pickup), Red (destination), Blue (stops - future)
- **Quick Order**: Order rides from saved favorite routes

## Permissions
The app requests the following permissions:
- `ACCESS_FINE_LOCATION` - For precise location tracking
- `ACCESS_COARSE_LOCATION` - For approximate location
- `INTERNET` - For map tiles and geocoding

These are automatically added to AndroidManifest.xml.

## Troubleshooting

### Map shows gray screen
- Verify your API key is correct in AndroidManifest.xml
- Ensure Maps SDK for Android is enabled in Google Cloud Console
- Check that your API key restrictions (if any) allow your app's package name and SHA-1 fingerprint

### "Authorization failure" error
- Your API key may be restricted. Check the restrictions in Google Cloud Console
- Ensure the package name matches: `com.example.driverr_mobile`
- Verify your SHA-1 fingerprint is added to the API key restrictions

### Map loads but markers don't appear
- Check that the backend geocoding service is running
- Verify the backend URL in `Constants.BASE_URL` is correct
- Check network connectivity

## Backend Integration
The maps interact with the backend through two main endpoints:
- `POST /api/addresses/geocode` - Convert address text to coordinates and save
- `GET /api/addresses/{id}` - Retrieve address details by ID

These are handled by the `AddressApi` interface and `MapController` in the backend.

## Cost Considerations
Google Maps API usage may incur costs based on the number of:
- Map loads
- Geocoding requests
- Reverse geocoding requests

See [Google Maps Platform Pricing](https://developers.google.com/maps/billing-and-pricing/pricing) for details.

## Notes
- The default map center is Novi Sad, Serbia (45.2671, 19.8335)
- Maps use standard Google Maps styling (can be customized)
- Markers use default Google Maps marker colors (green, red, blue)
- The app requires an active internet connection for maps to work
