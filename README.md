# ExVidPlayer
ExVidPlayer is simple library that abstracts ExoPlayer.

## Getting Started

*  Add this to build.gradle
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

*  Add dependency for library
```
dependencies {
	        compile 'com.github.karanvs:ExVidPlayer:V1.1'
	}
```

## Usage

# Add permissions to your manifest

```

  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.WRITE_SETTINGS" />
```
Settings permission will used for brightness controls, If you are using only ExVidPlayer, then you may skip it.

#  ExVidPlayer(Simple)

* Define an instance of ExVidPlayer and set player event listener,

```
// initialize the new player instance
ExVidPlayer exVidPlayer = ExVidPlayer.Factory.newInstance(getActivity(), surfaceView, mainHandler,aspectRatioFrameLayout);
exVidPlayer.setListener(mPlayerListener);
exVidPlayer.setSource(video_url, video_type,currentTrackIndex);

//define listener

ExVidPlayerListener mPlayerListener = new ExVidPlayerListener() {
      @Override public void onError(String message) {

      }

      @Override public void onBufferingStarted() {
      
      }

      @Override public void onBuffering(String percentage) {
        
      }

      @Override public void onBufferingFinished() {
        
      }

      @Override public void onRendereingstarted() {

      }

      @Override public void onCompletion() {
              }

      @Override public void hideControls() {
              }

      @Override public void showControls() {
        
      }

      @Override public void onProgressChanged(int progress) {
     
      }
    };

```

#  ExVidPlayerFragment(Complete) 

* Gesture controls.
* Brightness controls with one single touch vertical scroll.
* Volume controls with multitouch vertical scroll.
* Forward on left swipe. 
* Reverse on right swipe.
* Player controls 

* Usage

```
//set up fragment
private ExVidPlayerListener mPlayerListener;


FragmentManager fragmentManager=getSupportFragmentManager();
FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
ExVpCompleteFragment exVpCompleteFragment =new ExVpCompleteFragment();
mPlayerListener=exVpCompleteFragment.
Bundle bundle=new Bundle();
bundle.putStringArrayList("urls",video_url);
bundle.putStringArrayList("type",video_type);
bundle.putInt("currentIndex",0);
exVpCompleteFragment.setArguments(bundle);
fragmentTransaction.add(R.id.parent, exVpCompleteFragment);
fragmentTransaction.commit();

```

#  ExVidPlayerFragment (without controls) 

* Gesture controls.
* Brightness controls with one single touch vertical scroll.
* Volume controls with multitouch vertical scroll.
* Forward on left swipe. 
* Reverse on right swipe.

* Usage

```
ExVpListener exVpControls;

FragmentManager fragmentManager = getSupportFragmentManager();
FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
ExVpFragment exVpFragment = new ExVpFragment();
exVpControls=exVpFragment.getExVpListener();
Bundle bundle = new Bundle();
bundle.putStringArrayList("urls", video_url);
bundle.putStringArrayList("type", video_type);
bundle.putInt("currentIndex",0);
exVpFragment.setArguments(bundle);
fragmentTransaction.add(R.id.parent, exVpFragment);
fragmentTransaction.commit();
```

// set up controls layouts
```
exVpControls.setControlLayout(root);
exVpControls.setProgressBar(mProgress);
exVpControls.setCurrentText(tvCurrent);
exVpControls.setDurationText(tvTotal);
```

ExVpListener has following events, you can call them on yout events.

```
void play();
void stop();
void forward();
void reverse();
void nextTrack();
void previousTrack();

```

### License

This project is licensed under the Apache 2.0 License - see the [LICENSE.txt](LICENSE.txt) file for details
