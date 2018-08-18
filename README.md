Chrome Custom Tabs - Kotlin and Lifecycle Aware
=================

This is a rewrite of the helpers offered by [Google](https://github.com/GoogleChrome/custom-tabs-client). 

The ones offered by Google are written in JAVA and require integration with the Activity lifecycle if the user needs to support warmup of the browser for performance gains.

- This new implementaion uses [Lifecycle](https://developer.android.com/reference/androidx/lifecycle/Lifecycle) to hide away the service binding logic.
- Rewritting in Kotlin makes the code consixe and offers a cleaner and easier to work with API, espcially with the support of optional paramters.


Usage
-------
Add a dependency to your `build.gradle`:

    dependencies {
        implementation 'com.saurabharora.customtabs:customtabs:1.0.0'
    }

Now in your Activity/Fragment from where you want to launch the Chrome Custom Tabs:

    private val  customTabActivityHelper: CustomTabActivityHelper =
        CustomTabActivityHelper(context = this, lifecycle = lifecycle, connectionCallback = this)
        
    //If you know the potential URL that will be loaded:
    customTabActivityHelper.mayLaunchUrl(uri)
        
    val customTabsIntent = CustomTabsIntent.Builder(customTabActivityHelper.session)
                           .build()
                           
    CustomTabActivityHelper.openCustomTab(activity = this, customTabsIntent = customTabsIntent, uri = uri)
            
See the demo app for more details.
