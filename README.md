# numberpicker


## Gradle

``` groovy
repositories { 
    maven { url "https://jitpack.io" }
}
```  
    
``` groovy
dependencies {
    compile 'com.github.czy1121:numberpicker:1.0.0'
}
```
    
## Usage
    
**XML**

``` xml
<com.github.czy1121.numberpicker.NumberPicker
    android:id="@+id/np"
    style="@style/NumberPicker"
    app:npMaxValue="100"
    app:npMinValue="1"
    app:npStep="1"
    app:npValue="5"
    />
```

**Java**

``` java
npValue = (NumberPicker) findViewById(R.id.np);
npValue.setOnValueChangedListener(new NumberPicker.OnValueChangedListener() {
    @Override
    public void onValueChanged(NumberPicker view, int value) {
        txtValue.setText("" + value);
    }
});
// init(step, min, max, value)
npValue.init(5, -10, 100, 8); 
```

## Preview

![screenshot](screenshot.png)