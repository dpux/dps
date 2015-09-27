# Android app to get your DPS driving test appointment at the earliest !

- Note that this project has a ton of dependencies on the DPS website, so it's highly unlikely that this will last for long.
- This app polls to fetch all the available dates for a DPS driving test. This is a hack around the way DPS website works, I am not using any public API.

As a prep for this app, I logged into DPS website, parsed their HTML content and tried to understand their request structure. I coded for a similar structure in the app which will make these calls periodically to see all the available dates in the months I am looking for. If there is a new date available, the app will notify me and my friends over SMS.

I coded this in a few hours, for my personal use, so don't expect perfect code - it just works for me.
I have added comments which should help understand what things to update for your use.
Before you use this code, I would recommend you to visit the website for your DPS center and navigate to the appointment page using some proxy (I used Charles). You will find all the relevant information which can be used to update the code to work for you.

I would be happy to help if you face any issues.