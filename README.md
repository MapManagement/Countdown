# Countdown
Countdown will be an application that will only show a timer running down until a specific datetime is
reached. You simply just open the app and set a datetime. The chosen datetime is the end of the timer
and after confirming the datetime the timer will start counting down. You will now see the left years,
monhts, days, hours, minutes and seconds till the datetime is reached. When reaching this datetime
you will get a notification on your phone.

# Color Picking
I am using following dependency for my color picking dialog:\
[android-material-color-picker-dialog](https://github.com/Pes8/android-material-color-picker-dialog) \
I thought it would be easier if I just use an existing color picker instead of creating my own one.
It simply just lets you choose a color by adjusting the different values (rgb and alpha) and returns
the color as an integer. As a result the user can customize the app as preferred.

# ToDos
- [x] keeping datetime when closing app
- [x] fixing bug of resetting time when choosing a new date time
- [ ] fixing bug of not counting down when secconds are equal 0
- [ ] adding notification
- [x] customizable layout?