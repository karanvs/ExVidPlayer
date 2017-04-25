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
	        compile 'com.github.karanvs:ExVidPlayer:v2.8'
	}
```

## Usage

#  What's New

* ExVpSimpleFragment

On account of a case for simple video player without the need for brigtness and volume controls, this is the latest addition to exvidplayer, this fragment provides simple playback without controls. 


* New methods added for ExVpListener for more control.




# Add permissions to your manifest

```

  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.WRITE_SETTINGS" /> //this permission is not needed for ExSimpleVpFragment
```
Settings permission will used for brightness controls, If you are using only ExVidPlayer, then you may skip it.
This will work pre Marshmallow, however for Marshmallow and above, you will have to explicity demand permission at runtime.
Initialize your ExVidPlayer instance only when you have the permissions otherwise it may lead to runtime error.
ExVidPlayerActivity automatically handles this for you , but you may lose the flexbility to place the video in layout as per your need, you may achieve this either by ExVpCompleteFragment (with controls) and ExVpFragment with your custom controls.

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
fragmentTransaction.add(R.id.parent, exVpCompleteFragment);//replace parent with id of your framelayout
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
void setControlLayout(ViewGroup viewGroup);

  void play();

  void stop();

  void forward();

  void reverse();

  void nextTrack();

  void previousTrack();

  void setProgressBar(SeekBar seekBar);

  void setDurationText(TextView textView);

  void setCurrentText(TextView te);

  void changeQuality(View v);

  void addTrack(String url,String type);

  void setCurrent(int position);

  boolean isPlaying();

  void removeTrack(int position);

  void seekToProgress(int progress);

```

#  ExVidPlayerActivity 

Declare in your manifest

```

<activity android:name="com.veer.exvidplayer.VideoPlayer.ExVidPlayerActivity"
android:configChanges="orientation|screenSize"></activity>

```

Use as
 
```
Intent intent = new Intent(HomeDetailScreen.this, ExVidPlayerActivity.class);
intent.putStringArrayListExtra("urls", video_urls);
intent.putStringArrayListExtra("types", video_types);
intent.putExtra("currentIndex", postion);
startActivity(intent);

```


* Device orientation handling,

ExVidPlayer handler by default screen orientation, playback will continue without disruption. You need to make changes to your activity decleration in your manifest
```
Add this to your activity tag.
android:configChanges="orientation|screenSize"
```

### License

This project is licensed under the Apache 2.0 License - see the [LICENSE.txt](LICENSE.txt) file for details
