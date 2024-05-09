## TeamDex
Welcome to the TeamDex app for Android!

This is an open source Android app. The source code is available on GitHub under the GNU AGPL license (3.0 or later).

As an avid Android user myself, I take privacy very seriously.
I know how frustrating it is when apps collect your data without your knowledge.

### Data collected by the app

I hereby state, to the best of my knowledge and belief, that I have not programmed this app to collect any personally identifiable information. All data (app preferences (like theme) and alarms) created by the you (the user) is stored locally in your device only, and can be simply erased by clearing the app's data or uninstalling it. No analytics software is present in the app either.

### Explanation of permissions requested in the app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file:

<br/>

| Permission | Why it is required |
| :---: | --- |

| `android.permission.INTERNET` | To access API data for the app. Also used for sign in/log in |
| `android.permission.WAKE_LOCK` | Automatically granted by the system, required for internet connections while device is sleeping.

 <hr style="border:1px solid gray">

If you find any security vulnerability that has been inadvertently caused by me, or have any question regarding how the app protectes your privacy, please send me an email or post a discussion on GitHub, and I will surely try to fix it/help you.

Yours sincerely