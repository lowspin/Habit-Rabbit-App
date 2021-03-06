# Habit-Rabbit-App
Capstone Project Submission for Udacity's Android Developer Nanodegree Program (Grow With Google)

## Description
According to Charles Duhigg’s book “The Power Of Habit”, habits are formed with repeated 3-step loops: trigger, action and reward, e.g. 

* Trigger - sitting down at your kitchen table to have breakfast every morning
* Action - making coffee 
* Reward - the rich smell of the coffee. 

Your brain’s activity only spikes twice during this loop. At the beginning, to figure out which habit to engage in, and at the end, when the link between cue and routine is reinforced. That’s how habits are built - the more the cycle repeats, the stronger this link gets and the more the habit will “stick”.

This app helps users form desired habits by encouraging repetitions, through providing the trigger and reward part of the habit cycle on a periodic basis. For each desired habit, users specify the number of repetitions (e.g. 100)  and the day/time of week he wants to be reminded of (e.g. weekday mornings at 7am). At the indicated time, the app will provide a notification to remind the user to perform the action. At end of the action, the user gets to add a virtual check or “sticker” to fill up a grid, and receive encouraging remarks along the way.

To further encourage the user, the app also provide a widget that shows the progress (in percentage) toward the final number of repetitions. 

Finally, the app features a journal entry format for users to enter any thoughts and observation after each repetition, and shows useful statistics of the progress, e.g. percentage completion, average days between actions, etc.

Java language is used for development

## Intended User
This app is intended for two groups of users:
1. self motivated people who actively wants to form a good habit for his or her own personal benefit, e.g. go to the gym every morning, spend 30 mins working on a coding project each night.
2. parents who want to cultivate good habits in their kids, e.g. daily music instrument practice, homework, exercise etc. To this end, the app provides a approval mode, so that a kid can log their practice themselves but requires a parent’s PIN to confirm the count in the final statistics.

## Features
Main UI/UX features of the app.
* Periodic reminder to trigger action using Android’s notification, 
* Reward action through adding virtual “stickers”,
* Widgets and summary screen to track progress.
* Reward completion with random images from the internet

Each habit is viewed as a separate project in this app.

To facilitate internationalization, app keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.

App includes support for accessibility by using SP (scalable point) units for text font sizes, which is sensitive to the user's display settings.

## Screen Shots
### Main Project Summary
The main project activity shows a summary of the progress for each project, in both percentage and absolute numbers, along with the average interval between practice.

On-Going Project            |  New Project
:-------------------------:|:-------------------------:
<img src="./img/img01-project-summary.png" width="240"> | <img src="./img/img02-project-summary2.png" width="240">

### Reward Sticker Book
The progress update activity consist of a grid of empty square corresponding to the number of repetitions. This is the main “reward” part of the app - having an grid of empty squares motivates a user to fill them with attractive virtual “stickers”. When user completes a practice/repetition, a new sticker is added. If parental approval is required, the chosen image is greyed out to indicate that it is waiting for approval. A parent can then click the same box, key in a password and approve the item.

Portrait Mode           |  Landscape Mode
:-------------------------:|:-------------------------:
<img src="./img/img03-reward-stickers.png" width="240"> | <img src="./img/img04-reward-stickers-landscape.png" width="480">

### Choose Stickers and Completion Reward
User clicks on an empty box and a secondary dialog allows him/her to choose a “sticker” design to use for the chosen box. When all boxes are filled, a special completion reward image is shown.

Choose Stickers           |  Completion Reward
:-------------------------:|:-------------------------:
<img src="./img/img05-choose-stickers.png" width="240"> | <img src="./img/img05-choose-stickers.png" width="240">

### Configure / Update Project
The project configuration sceen allows user to select the number of repetitions, days and time to set notification reminders, and whether or not parental approval is required.

Menu            |  Edit Project
:-------------------------:|:-------------------------:
<img src="./img/img07-menu.png" width="240"> | <img src="./img/img08-add-edit-project.png" width="240">

### Widgets and Notifications
The "Trigger" part of the habit loop is handled by a widget, which shows the number of on-going projects and the average completion rate of all projects, and a timely notification to remind the user to practice.

Widget            |  Notification
:-------------------------:|:-------------------------:
<img src="./img/img10-widgets.png" width="240"> | <img src="./img/img11-reminder-notifications.png" width="240">

### Miscellaneous Settings
User can change settings in the Settings screen.

<img src="./img/img09-settings.png" width="240">

## Key Considerations
### Data Persistence
Project configuration and completion status are stored locally using SQLite databases. Room will be used to provide an abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite. LiveData and ViewModel are used when required and no unnecessary calls to the database are made.

### Edge/corner cases in UX
If user clicks the back arrow while entering a new project, no data will be saved and the action is considered an abandonment, i.e. same as clicking the “CANCEL” button.

### Libraries and Tools
* Glide v4.8.0 (Aug 17, 2018) - handle the loading and caching of images from internet sources
* LeakCanary v1.6.1 (Jul 25, 2018) - detect memory leak
* Butterknife v8.8.1 - for data binding

Development environment and tools:
* Android Studio 3.1.4 - Best IDE for Android development
* Gradle 4.10 - Latest gradle with improved Java compiler

### Google Play Services
Two flavors of the app are made - the free version has limited functionality and is ad supported, while the paid version is ad-free and has full functionality. Also, to keep track of usage trends, analytics is also added. The following Google Play Services APIs are used:
* Google Mobile Ads 16.0.0 (com.google.android.gms:play-services-ads:16.0.0) - display ads in free version
* Google Analytics 16.0.4 (com.google.android.gms:play-services-analytics:16.0.4) - analytics to track usage and trends. 

## API Keys
Note: The following temporary IDs should be replaced.
* Google Analytics Tracking ID - [/res/global_tracker.xml](https://github.com/lowspin/Habit-Rabbit-App/blob/master/app/src/main/res/xml/global_tracker.xml)
* Google AdMob Ad ID - [DefaultApplication.java](https://github.com/lowspin/Habit-Rabbit-App/blob/master/app/src/main/java/com/teachableapps/capstoneproject/DefaultApplication.java)

## Image Credits
* Icons - www.flaticon.com 
* Reward Graphics - www.unsplash.com


