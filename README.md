# Nexconn Call Sample Android

A comprehensive demonstration application showcasing the capabilities of the Nexconn Call SDK for Android. Built with Jetpack Compose and Kotlin, this sample app demonstrates real-time audio and video calling features with a modern, reactive architecture.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Architecture](#architecture)
- [Permissions](#permissions)
- [Dependencies](#dependencies)
- [Documentation](#documentation)
- [License](#license)

## Features

### Authentication & Connection
- App Key and User Token authentication
- Automatic SDK initialization (Chat + Call)
- Connection state management with visual feedback
- Persistent login state across navigation

### 1-to-1 Calling
- **Audio/Video Calls**: Initiate and receive high-quality audio and video calls
- **Incoming Call Handling**: Accept or reject incoming calls with dialog interface
- **Device Controls**: 
  - Camera enable/disable and switching (front/back)
  - Microphone mute/unmute
  - Speaker/speakerphone toggle
- **Media Type Switching**: Seamlessly switch between audio-only and video calls during active sessions
- **Call History**: View detailed call logs with timestamps and participants

### Group Calling
- **Multi-Participant Calls**: Support for group video/audio conferences
- **Dynamic Participant Management**: 
  - Invite additional users to ongoing calls
  - Real-time participant list updates
  - Visual indication of participant status
- **Multi-Video Rendering**: Simultaneous video streams from multiple participants
- **Flexible Call Controls**: Same device controls available as 1-to-1 calls

### Device Management
- **Runtime Permissions**: Automatic permission requests for camera and microphone
- **Permission Validation**: Pre-call permission checks with user-friendly error messages
- **Auto-Enable Devices**: Automatic camera and microphone activation upon call connection
- **Dynamic Video Updates**: Seamless video view updates when participants join/leave

## Requirements

- **Minimum SDK**: 23 (Android 6.0 Marshmallow)
- **Target SDK**: 35 (Android 14)
- **Compile SDK**: 36
- **Java Version**: 11
- **Gradle**: 8.11.1
- **Android Studio**: Latest stable version recommended (Hedgehog or newer)
- **Device**: Physical device recommended for testing (emulator may have limited camera/audio support)

## Project Structure

```
app/src/main/java/ai/nexconn/call/sample/
├── MainActivity.kt                    # Application entry point
├── NexconnCallApp.kt                  # Application class (SDK installation)
├── ui/
│   ├── login/                         # Authentication flow
│   │   ├── LoginScreen.kt            # Login UI with Compose
│   │   ├── LoginViewModel.kt         # Login state management
│   │   ├── LoginUiState.kt           # Login state data class
│   │   └── LoginViewModelFactory.kt  # ViewModel factory for DI
│   ├── call/
│   │   ├── CallViewModel.kt          # Base ViewModel for call screens
│   │   ├── single/                   # 1-to-1 calling implementation
│   │   │   ├── SingleCallScreen.kt
│   │   │   ├── SingleCallViewModel.kt
│   │   │   └── SingleCallUiState.kt
│   │   ├── multi/                    # Group calling implementation
│   │   │   ├── MultiCallScreen.kt
│   │   │   ├── MultiCallViewModel.kt
│   │   │   └── MultiCallUiState.kt
│   │   └── components/               # Reusable UI components
│   │       ├── VideoView.kt          # Local/remote video rendering
│   │       ├── IncomingCallDialog.kt # Incoming call UI
│   │       ├── InviteUserDialog.kt   # User invitation UI
│   │       ├── MediaTypeChangeDialog.kt
│   │       └── CallHistoryList.kt    # Call history display
│   ├── navigation/
│   │   └── NavGraph.kt               # Navigation routes and setup
│   └── theme/                        # Material 3 theming
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── data/
│   └── model/                        # Data models
│       ├── CallLogItem.kt            # Call history data
│       └── UserVideoItem.kt          # Multi-call participant data
└── utils/                            # Utility classes
    ├── PermissionUtils.kt            # Runtime permission handling
    └── ToastUtils.kt                 # Toast notification helper
```

## Quick Start

### Option 1: Quick Demo Testing (Recommended for First Run)

For quick verification without going through the registration process:

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd nexconn-call-sample-android
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Configure credentials for quick testing**
   - Navigate to: `app/src/main/java/ai/nexconn/call/sample/ui/login/LoginUiState.kt`
   - Replace the default `appKey` and `userToken` values:
   
   ```kotlin
   data class LoginUiState(
       val appKey: String = "YOUR_APP_KEY",
       val userToken: String = "YOUR_USER_TOKEN",
       val isConnected: Boolean = false,
       val isLoading: Boolean = false,
       val errorMessage: String? = null
   )
   ```

4. **Run the application**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio
   - The app will automatically connect with your credentials

5. **Test calling features**
   - Navigate to "1v1 Call" or "Multi Call"
   - Grant camera and microphone permissions when prompted
   - Enter a user ID to call (use another device/account for testing)

### Option 2: Obtain Your Own Credentials

To get your own App Key and User Token:

1. Visit the [Nexconn User Registration Guide](https://docs.nexconn.ai/platform-chat-api/user/register)
2. Follow the registration process to obtain:
   - **App Key**: Your application identifier
   - **User Token**: Authentication token for your user
3. Use these credentials in `LoginUiState.kt` as described above

### Important Security Notes

⚠️ **For Production Use:**
- **Never hardcode credentials** in production builds
- Implement a proper authentication flow with secure token storage
- Use Android Keystore for sensitive data
- Consider implementing token refresh mechanisms
- Remove default credentials before releasing

## Configuration

### SDK Dependencies

The project uses Nexconn SDKs for calling functionality:

```gradle
dependencies {
    // Nexconn SDKs
    implementation("ai.nexconn:call:26.2.0")
    implementation("ai.nexconn:chat:26.2.0")
    
    // Other dependencies...
}
```

### Required Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<!-- Network permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

<!-- Media permissions -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<!-- Phone state permission -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

**Runtime Permissions**: The app automatically requests camera and microphone permissions when entering call screens.

## Architecture

### Design Pattern

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern with Jetpack Compose:

- **Model**: Data classes and SDK integration
- **View**: Composable functions for UI
- **ViewModel**: State management with Kotlin StateFlow

### Key Components

#### ViewModels
- **State Management**: Using `StateFlow` for reactive state updates
- **Lifecycle Awareness**: Proper handling of configuration changes
- **Event Handling**: SDK event callbacks processed and exposed as UI state

#### UI Layer
- **Declarative UI**: Built entirely with Jetpack Compose
- **Material 3**: Modern Material Design components
- **Reactive Updates**: UI automatically updates based on state changes

#### Navigation
- **Single Activity**: One activity with Compose Navigation
- **Type-Safe Routes**: Defined navigation routes
- **State Preservation**: ViewModel state persists across navigation

### SDK Initialization Flow

```
1. App Startup
   └─> NCCallEngine.install() (in NexconnCallApp.onCreate())

2. User Login
   └─> Chat SDK Initialization (NCEngine.initialize())
   └─> Chat SDK Connection (NCEngine.connect())
   └─> On Success: Call SDK Initialization (NCCallEngine.initialize())

3. Call Screen Entry
   └─> Handler Registration (registerHandlers())
   └─> Permission Requests (camera, microphone)

4. Call Screen Exit
   └─> Handler Unregistration (unregisterHandlers())
   └─> Resource Cleanup
```

### Event Handling

The app uses a handler-based event system:

```kotlin
// Event Handler Registration
override fun createEventHandler(): NCCallEventHandler {
    return object : NCCallEventHandler {
        override fun onCallReceived(event: NCCallReceivedEvent) { }
        override fun onCallConnected(event: NCCallConnectedEvent) { }
        override fun onCallEnded(event: NCCallEndedEvent) { }
        // ... other callbacks
    }
}
```

## Permissions

### Required Permissions

| Permission | Purpose | When Requested |
|------------|---------|----------------|
| `CAMERA` | Video calling | On call screen entry |
| `RECORD_AUDIO` | Audio calling | On call screen entry |
| `INTERNET` | Network communication | Granted at install |
| `ACCESS_NETWORK_STATE` | Network status monitoring | Granted at install |
| `MODIFY_AUDIO_SETTINGS` | Audio routing control | Granted at install |
| `READ_PHONE_STATE` | Call state management | Granted at install |

### Permission Handling

The app implements comprehensive permission handling:

1. **Automatic Requests**: Permissions requested when entering call screens
2. **Pre-Call Validation**: Checks permissions before initiating/accepting calls
3. **User-Friendly Errors**: Clear error messages when permissions are denied
4. **Graceful Degradation**: Audio-only mode available if camera permission denied

## Dependencies

### Core Android Libraries

```gradle
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.activity:activity-compose")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Lifecycle & ViewModel
implementation("androidx.lifecycle:lifecycle-runtime-ktx")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// AndroidX Core
implementation("androidx.core:core-ktx")
```

### Nexconn SDKs

```gradle
// Nexconn Call SDK
implementation("ai.nexconn:call:26.2.0")

// Nexconn Chat SDK (required for authentication)
implementation("ai.nexconn:chat:26.2.0")
```

### Utilities

```gradle
// Permission handling
implementation("com.google.accompanist:accompanist-permissions:0.34.0")
```

## Documentation

### Official Resources

- **Nexconn Platform Documentation**: [https://docs.nexconn.ai/callsdk-android](https://docs.nexconn.ai/callsdk-android)
- **User Registration Guide**: [https://docs.nexconn.ai/platform-chat-api/user/register](https://docs.nexconn.ai/platform-chat-api/user/register)
- **API Reference**: [NexconnCall](https://docs.nexconn.ai/apidoc/nexconncall-android/latest/en_US/)


## Testing

### Testing Checklist

- [ ] Login with valid credentials
- [ ] Navigate between screens (login state persists)
- [ ] Grant camera and microphone permissions
- [ ] Initiate 1-to-1 video call
- [ ] Accept incoming call
- [ ] Toggle camera on/off during call
- [ ] Switch between front/back camera
- [ ] Toggle microphone on/off
- [ ] Toggle speaker on/off
- [ ] Switch from audio to video call
- [ ] End call and verify cleanup
- [ ] Initiate group call with multiple users
- [ ] Invite users to ongoing call
- [ ] Verify multi-video rendering
- [ ] Check call history display

### Known Limitations

- Emulator testing may have limited camera/audio functionality
- Physical devices recommended for full feature testing
- Multiple devices/accounts needed for end-to-end call testing

## Troubleshooting

### Common Issues

**Issue**: App crashes on startup
- **Solution**: Verify SDK dependencies are correctly added and synced

**Issue**: Cannot connect to Nexconn services
- **Solution**: Check App Key and User Token are valid and correctly configured

**Issue**: No video/audio during call
- **Solution**: Ensure camera and microphone permissions are granted

**Issue**: Remote video not showing
- **Solution**: Verify both participants have granted camera permissions and are using video mode


## Support

For issues, questions, or feature requests:
- Check the [Nexconn Documentation](https://docs.nexconn.ai/)
- Contact Nexconn support through our official channels

---

**Built with ❤️ using Nexconn Call SDK**
