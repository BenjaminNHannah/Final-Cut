# Final-Cut

##  Overview
Final-Cut is a useful application intended to offer users the ability to quickly select a movie to watch and avoid endless scrolling. It features an interactive genre filtering system that curates recommendations according to simple preferences. It also offers links to trailers and current streaming services for ease of access.

---

##  Problem Addressed
Final-Cut ends the endless scrolling that plagues users when trying to find something interesting to watch by offering immediate recommendations, better enabling them to quickly discover desirable content.

---

##  Platform
The application will be available on modern mobile Operating Systems such as Android and iOS.

---

##  Front/Back End Support
* **Front-End:** A pleasant User Interface that is intuitive and simple that also displays relevant content without overwhelming the user will be used to present movie recommendations, trailers, posters, and streaming services.
* **Back-End:** The application will require a cloud hosting service of some kind, such as AWS, GCP, or Azure. 
* **APIs:** We will require some sort of APIs in order to pull data from movie databases to sync with trailers and databases regarding where the movie is available.

---

##  Functionality
* **Discovery:** Movie recommendation buttons and filtering by genre and other specified categories.
* **Directory:** An up-to-date directory pointing to where the titles are currently available.
* **Personalization:** User personalization such as enabling them to set which streaming services they have so that content which is available for them can be prioritized if desired.

---

##  Design
A nice UI that lets users easily navigate the application without being lost in difficult menus or having to encounter a learning curve just to utilize the application.


## Module 1 Breakdown:

**'Initial project outline'**: I submitted the initial project outline and description.
**'Created initial ReadMe and Wiki Page'**

## Module 2 Breakdown: 

**'AndroidManifest.xml:'** Declared 'MainActivity' as primary launch entry point for the app.
**'MainActivity.java:'** Is the UI Shell, it uses FragmentManager and FragmentTransaction to create the user interface screen configuration. 
**'DiscoveryFragment.java' & 'fragment_discovery.xml'**: Add a UI screen structure to create the "Welcome to Final-Cut!" interface.
**'Intents/Bundles Blueprint'**: Added intent frameworks to pass data to external media players or web browsers to allow the video trailers to playback.


## Module 3 Breakdown: 

**'ScrollView Framework'**: To ensure responsive screen we added support for various displays and aspect ratios to the application
**'LinearLayout Configuration'**: We added a model that allows boundaries to be clearly delineated without clogging up the screen.
**'TextView Architecture'**: We added a stylish set of text views that look clean and are easy to read.
**'RadioGroup & RadioButtons'**: Added user input buttons to permit the filtering of genres.
**'CheckBox Widgets'**: Added input check boxes for users to input their personal streaming service filters as well.
**'Button UI Action Trigger'**: Added a "Generate Movie Recommendation" button which will map to randomly select a movie once the database is connected.

## Module 4 Breakdown: 

**'ImageView Framework'**: Added an ImageView container that will be a placeholder for movie posters in the future.
**'WebView Layout'**: Added a WebView layout to be a place holder for upcoming trailers in the future.
**'Updated Layout'**: Added some updated layout structure for neater UI elements.|

## Module 5 Breakdown: 

**'Persistent UI States'**: Added persistent streaming service checkboxes and radio buttons for preferred genres in the UI for users.
**'Data Architecture Outline'**: Added a structure such that later on we will be able to utilize a database and content query management service for large lists of movies and data in an effective way.
**'SharedPreferences'**: Added simple data preservation to keep user's filter choices stored under MODE_PRIVATE.
