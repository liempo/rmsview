# RmsView
An Android View for microphone feedback.

# Install

```
// Project level 
allprojects {
    repositories {
        google()
        jcenter()
        
        ...
        maven { url 'https://jitpack.io' }
    }
}

// Module level dependency
implementation 'com.github.liempo:rmsview:alpha-1.0'
```

# Usage

```
            <com.liempo.soundview.rmsview
                android:id="@+id/sound_view"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>
```
