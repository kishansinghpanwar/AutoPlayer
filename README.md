

# AutoPlayer [![](https://jitpack.io/v/kishansinghpanwar/AutoPlayer.svg)](https://jitpack.io/#kishansinghpanwar/AutoPlayer)
A player based on ExoPlayer for play video automatically in RecyclerView (Vertical scroll), when an item is visible.

<img src="https://github.com/kishansinghpanwar/AutoPlayer/blob/master/Screenshots/screenshot-1.png" width="180" >    <img src="https://github.com/kishansinghpanwar/AutoPlayer/blob/master/Screenshots/screenshot-2.png" width="180">

 **Features:**
 - This library supports the Auto-play video even though there are multiple View-Types and View-Holders in RecyclerView.
 -  Automatically detect the visible video and play it while pause the other videos.
- Fully customizable settings (Auto Play, Default Mute, Use Controller, Enable Caching etc.).
- Support Mute/Unmute.
- Support Attractive controller for video.
- Automatically detect the highest visible video if there are more than 1 player visible same time on the screen.


 **To add this Library in your project :**
 - Step 1. Add the JitPack repository to your build file
    
    
	```
	allprojects {
	    repositories {
	       ...
	       maven { url 'https://jitpack.io' }
	    }
	}
	 ```  
   
 - Step 2. Add the dependency
	```
	dependencies {
	    implementation 'com.github.kishansinghpanwar:AutoPlayer:v1.0.0'
	}
	```
**Basic Usage :**
- **Step 1.** Setup the AutoPlayerManager in your **Activity/Fragment** with your custom settings for Player :

	```
	AutoPlayerManager autoPlayerManager = new AutoPlayerManager(this);  
    autoPlayerManager.setAutoPlayerId(R.id.autoPlayer);  
    autoPlayerManager.setUseController(true);  
    autoPlayerManager.attachRecyclerView(rvFeeds);  
    autoPlayerManager.setup();
	```
	*See usage:* [MainActivity.java](https://github.com/kishansinghpanwar/AutoPlayer/blob/master/app/src/main/java/com/example/autoplayer/MainActivity.java)
	
  ------
  
- Step 2. Add AutoPlayer in your **Adapter XML** :
  ```
	<com.player.autoplayer.AutoPlayer  
	  android:id="@+id/autoPlayer"  
	  android:layout_width="match_parent"  
	  android:layout_height="match_parent" />
  ```
  *See usage:* [item_layout_video.xml](https://github.com/kishansinghpanwar/AutoPlayer/blob/master/app/src/main/res/layout/item_layout_video.xml)
  	
  ------
  
 - Step 2. Set Player URL and Placeholder in your **Adapter class** :
	  ```
	holder.autoPlayer.setUrl(feedBeanList.get(position).getUrl());  
	holder.autoPlayer.setAnimationTime(500);  
	holder.autoPlayer.setPlaceholderView(holder.placeHolderView);
	  ```
	  *See usage:* [FeedAdapter.java](https://github.com/kishansinghpanwar/AutoPlayer/blob/master/app/src/main/java/com/example/autoplayer/adapter/FeedAdapter.java)
	  
  ------
  
🌟 **Thank you for using this library and If this is useful for you, then please Star 🌟 this.**

