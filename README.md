![Logo](app/src/main/res/mipmap-hdpi/icon256.png)

# Countdown
Newest version [HERE](app/release) \
At the moment Countdown will have two different modes to use:
## DateTime Reminder
This mode only shows a timer running down until a specific datetime is reached. You simply just open
the app and set a datetime. The chosen datetime is the end of the timer and after confirming the
datetime the timer will start counting down. You will now see the years, months, days, hours, 
minutes and seconds till the datetime is reached. When reaching this datetime you will get a
notification on your phone.
## Stop Watch
As the name already says, you simply just start a timer and can stop it anytime you want. Similar to
the first mode, it will show the seconds, minutes, days, months and even years which passed since the
last time you pressed "Start". You are able to reset and stop the stop watch too.

# Color Picking
I am using following dependency for my color picking dialog:\
[android-material-color-picker-dialog](https://github.com/Pes8/android-material-color-picker-dialog) \
I thought it would be easier if I just use an existing color picker instead of creating my own one.
It simply just lets you choose a color by adjusting the different values and returns
the color as an integer. As a result the user can customize the app as preferred.

# ToDos
- [x] keeping datetime when closing app
- [x] fixing bug of resetting time when choosing a new date time
- [x] fixing bug of not counting down when secconds are equal 0
- [ ] adding notification
- [x] customizable layout
    - [ ] more cutomizations
    - [ ] including custom background pictures
    - [x] customized layout should be used for all modes
- [x] adding stop watch mode
    - [x] saving time properly if stop watch is stopped
        - [x] using oldSeconds and newSeconds
- [x] adding toolbar for mode transition
- [x] added swipe gestures for navigation
- [ ] clean code D: