# raising-android

## Table of contents
1. [Initial Setup](#initial)
2. [Start the app](#start)
3. [Further development](#development)
4. [Documentation](#documentation)

### Initial Seutp <a name="initial"/>

1. Install the newest version of [Android Studio](https://developer.android.com/studio)
1. Open this repository in Android Studio
1. Wait for the initial gradle build to finish


### Start the app on your device <a name="start"/>

1. Download any drivers for the Android Device of your choice (Samsung, Huawei, etc), that are needed to connect to a laptop, from their respective website
1. On your device, go to `Developer Options` and enable `USB-Debugging`
1. Connect your chosen device to your laptop via USB-cable and select `Share data` on the Pop-Up-Window
1. Open Android Studio and load this repository
1. At the top you will see your connected device, click on the green arrow next to it to run the app ![](docs-ressources/)
1. After about 15 seconds the app automatically opens on your device

### Further development <a name="development"/>

Our inline code documentation should help you to get started.
For additional information visit [Android documentations](https://developer.android.com/docs).

### Documentation <a name="documentation"/>

If you want to create the newest documentation of our project, follow these steps:
1. In Android Studio, open the `Tools` menu and select `Generate JavaDoc..`
1. Enter your prefered scope and a directory, where the generated files should be stored
1. Also tick the box `Open generated documentation in browser`
