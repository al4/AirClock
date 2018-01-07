---
layout: page
title: AirClock
permalink: /
---
AirClock is a simple app I wrote to help combat jet-lag.

## Why?

Being a New Zealander living in London, I've done the long-haul flights from London to Auckland several times. Fortunately, being on almost the polar opposite side of the globe to the UK, flights to New Zealand must travel as much north-south as they do east-west, so you don't travel quite as fast laterally as you would going, say, from London to LA. However the jet-lag is still severe, and in the interests of making the most of my annual leave, I've experimented with several strategies for dealing with it.

## What is jet-lag?
In a nut-shell, jet-lag is when your body's circadian rhythm (sleep cycle) is out of step with where you are on the globe, meaning you want to sleep at times that you shouldn't and are wide awake when you should be asleep.

## Preventing jet-lag

Up until now, the best way I've found is to set your clocks to the destination time zone as soon as you board the plane, and pretend you are in that time zone. The idea is that you "think" in the destination time zone and act as you would if you were actually there. I've found this method to be pretty effective, and results in my body clock being only a few hours out by the time I get to Auckland. However, staying awake at 3pm NZ time when it's 3am in London is pretty difficult.

So really, this strategy doesn't prevent jet lag, it merely pays it forward, so you suffer the jet lag on the plane rather than at the destination.

The airlines don't help this strategy either, by serving meals at strange times and turning the lights on when your clocks say you should sleep!

## A Better Way

There is no magical reset button for your circadian rhythm, it needs to adjust gradually. Thus, I decided to write a clock app which gradually shifts the time between your origin and destination, the idea being that we reduce the effects of jet-lag by adjusting more gradually during the journey.

At the moment, it is very simple. Set your takeoff and landing times, and the app will show you the “effective” time that you should be operating on in that moment. It works by calculating a time offset, and applying that as a “time zone” to the clock widget on display (it does not alter the actual time on your phone).

As an example, here I've set AirClock to shift during a flight which takes off from London at 21:00 UTC, and lands in Auckland two days later at 09:00 NZST, for a journey time of 24 hours, which is about average for a direct flight to Auckland:

![]({{site.url}}{{site.baseurl}}/assets/img/example_1.png)

You can see that as at 23:35 it has calculated a time zone of GMT+0127 (i.e. 1h 30 mins ahead of London), and applied that to my phone's internal time, resulting in an effective time of 01:02 am.

The time zone offset continues to increase throughout the flight, until you land, at which point the effective time will be New Zealand time.

## Caveats

When travelling forward (time zone offset increasing), this effectively speeds up time, meaning a shorter daily cycle. When travelling backwards (time zone offset decreasing), it will effectively slow down time. In the future, I plan to make this an option so that you can choose to either speed up or slow down time to match the destination.

At the moment the logic does not handle crossing the international date line. If you are doing this (e.g. Auckland to Los Angeles), I suggest you fudge the date by setting the take-off time correctly and landing time as the correct time of day, but the on next chronological date rather than skipping a day or going backwards.

## Tips

To make the change more gradual, you could set the departure time to well before you leave, so that you can start adjusting early.

Because the app uses the phone's internal time, it is independant of the time zone. So you can change your time zone to that of your destination when you take off without affecting the calculations.

## Disclaimer

I am not a professional app developer, so please don't expect a professional-quality app! This is a hobbyist project written in my own free time, and it does have bugs. No warranty is given, express or implied.

I am also not a doctor, or a scientist conducting research into jet-lag, circadian rhythms or sleep cycles, or an authority on any such subjects. Use of this app should not be taken as medical advice, and it will not prevent jet lag.

If you expect anything more than a slightly smarter clock you may be disappointed. 😄

## Getting AirClock

 * [Google Play](https://play.google.com/store/apps/details?id=nz.al4.airclock)
 * [Github](https://github.com/al4/AirClock)
 * F-Droid coming soon

AirClock is open source, and you are free to modify it as you see fit. See Github for the source code.

If you like AirClock, please leave a review on the Play Store. Feedback and suggestions are always welcome, but will probably test the limits of my ability as an Android developer! 😬
