<p align="center">
  <a href="" rel="noopener">
 <img width=550px height=200px src="docs-resources/raising_schrift.PNG" alt="Project logo"></a>
</p>

<h3 align="center">RAI$ING - Android</h3>

---

<p align="center"> This is the Android part of our app Rai$ing. 
    <br> 
</p>

## 📝 Table of Contents
1. [Local Installation](#local)
1. [Download from Play Store](#download)
1. [Further development](#development)
1. [Documentation](#documentation)

## Local Installation <a name="local"/>

1. Install the newest version of [Android Studio](https://developer.android.com/studio)
1. Open this repository in Android Studio
1. Wait for the initial gradle build to finish


### Start the app on your device <a name="startDevice"/>

1. Download any drivers for the Android Device of your choice (Samsung, Huawei, etc), that are needed to connect to a laptop, from their respective website
1. On your device, go to `Developer Options` and enable `USB-Debugging`
1. Connect your chosen device to your laptop via USB-cable and select `Share data` on the Pop-Up-Window
1. Open Android Studio and load this repository
1. At the top you will see your connected device, click on the green arrow on the right to run the app ![](docs-resources/readme_connect_phone.PNG)
1. After about 15 seconds the app automatically opens on your device


### Start the app on an emulator <a name="startEmulator"/>

NOTE: Before using an emulator to start our app, keep in mind, that the emulator is pretty hungry for hardware resources and can quickly overload your system. We therefore recommend, if it is possible, that you start our app on a connected device.

1. Click here, to start with the setup of your emulator </br> ![](docs-resources/readme_setup_emulator.PNG)
1. A new window opens, click on `Create virtual device..` You can now select a phone of your choice. We recommend, that you use either `Pixel 3` oder `Nexus 5` for this application
1. In the next window you can select an API Level for your emulator. We recommend, that you use API Level 27 or higher. Click on `Download` to install the operating system on your virtual device
1. After everything is installed, which may take about 5 minutes, you can give your emulator a name, and then hit `Finish` to finish the installation
1. Your can now use your emulator to start our app by selecting it and clicking on the green arrow next to it ![](docs-resources/readme_select_emulator.PNG)


## Download from Play Store <a name="download">

1. Enable `Internal App Sharing`on your Android device.
1. Contact [Lorenz Caliezi](mailto:lorenz.caliezi@students.unibe.ch?subject=[GitHub]%20Rai$ing). Your E-Mail body should contain the address of your Google Account.
1. [Register as App-Tester](https://play.google.com/apps/internaltest/4700141989544110193)
1. Download the app with one of the two following links:
   
   [App with enabled subscriptions](https://play.google.com/apps/test/RQq5CHR3bp4/ahAJEhp-nFfHB_B3N5PCEw9YfftAHWOdRTuNFZOvp7w11ihraudI2cu6_zyAG2Z-6oDwn934mRQ4Mj5oT307gqb0tx)
   
   [App with disabled subscriptions](https://play.google.com/apps/test/RQq5CHR3bp4/ahAJEhp-kYrn1_PgQOFXdfkfZF_aG5OK6-hKvLe4gh7xc-YqRC677TEbF7o2RWUb89V07KQV0fp4ct-0MWwDvxtWtp)


## Further development <a name="development"/>

Our inline code documentation should help you to get started.
For additional information visit [Android documentations](https://developer.android.com/docs).

## Documentation <a name="documentation"/>

If you want to create the newest documentation of our project, follow these steps:
1. In Android Studio, open the `Tools` menu and select `Generate JavaDoc..`
1. Enter your prefered scope and a directory, where the generated files should be stored
1. Also tick the box `Open generated documentation in browser`
