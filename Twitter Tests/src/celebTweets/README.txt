PROBLEM:
Problem Statement
One of the magical things with Twitter is when famous users publicly converse with each other via tweets!

Find 10 such separate examples of conversations in the last 2 weeks where one example is the triple
 (A, B, T1, T2) such that A and B are the two users conversing and T1 and T2 are any two tweets in
 their conversation such that T1 is sent by A to B and T2 is sent by B to A. A should have more
 than a million followers on twitter, and B should have at least 125,000. Please include your code,
 comments on how to run it, and its output (i.e., the 10 such examples you find). For T1 and T2,
 include the URL, text and the time of the tweet in your output.
 

INCLUDED FILES:
README.txt - this file
StarConvos.java - the main executable
twitter4j.properties - contains information for oAuth purposes

INSTRUCTIONS ON HOW TO RUN:
This Java program uses the Twitter4j library, and the REST Twitter API. (twitter4j-2.2.6)
http://twitter4j.org/en/index.html

Simply download the Twitter4j Library and add twitter4j-core-2.2.6.jar to your application classpath.

How this was done in Eclipse:
> Open StarConvos.java
> Project -> Properties
> Projects Tab
> Add External JARs
> Add twitter4j-async-2.2.6-SNAPSHOT, twitter4j-core-2.2.6-SNAPSHOT, twitter4j-examples-2.2.6-SNAPSHOT, twitter4j-media-support-2.2.6-SNAPSHOT, twitter4j-stream-2.2.6-SNAPSHOT
> Note, those JARs are found in the 'lib' folder of twitter4j-2.2.6-SNAPSHOT.zip


Simply execute the StarConvos.java file, by running

% javac StarConvos.java
% java StarConvos

Or run this application in eclipse

The program will dynamically scan the twitter databases for conversations between celebreties
which follow the given rules stated above


SOME SAMPLE OUTPUT:

(
UsherRaymondIV
,
scooterbraun
,
Time: Sun Jun 24 04:34:20 EDT 2012, URL: https://twitter.com/UsherRaymondIV/status/216856763494969344, Tweet: @scooterbraun....Love you brother! Happy Birthday!?
,
Time: Sun Jun 24 12:45:17 EDT 2012, URL: https://twitter.com/scooterbraun/status/216799203014352897, Tweet: so i finally come home and....well @usherraymondiv @justinbieber and @adambraun well played. well played. ?#PRANKEDforMyBDAY?
)

(
justinbieber
,
scooterbraun
,
Time: Sun Jun 24 08:05:22 EDT 2012, URL: https://twitter.com/justinbieber/status/216909941292802049, Tweet: @scooterbraun GOT YAH. haha. @UsherRaymondIV @adambraun #prankstersontheloooooose
,
Time: Sun Jun 24 12:45:17 EDT 2012, URL: https://twitter.com/scooterbraun/status/216799203014352897, Tweet: so i finally come home and....well @usherraymondiv @justinbieber and @adambraun well played. well played. ?#PRANKEDforMyBDAY?
)

(
KimKardashian
,
KhloeKardashian
,
Time: Sun Jun 24 21:29:35 EDT 2012, URL: https://twitter.com/KimKardashian/status/217066981080301569, Tweet: OMG @KhloeKardashian & KylieJenner hacked moms twitter account! #NOTcool
,
Time: Sun Jun 24 21:48:07 EDT 2012, URL: https://twitter.com/KhloeKardashian/status/217071642541694976, Tweet: Ohhhh tattle LOL "@KimKardashian: OMG @KhloeKardashian & KylieJenner hacked moms twitter account! #NOTcool"
)

(
KimKardashian
,
KrisJenner
,
Time: Sun Jun 24 21:05:59 EDT 2012, URL: https://twitter.com/KimKardashian/status/217061040477843456, Tweet: Mom!!! Are you ok? LOL "@KrisJenner: My vajajay is throbbing!!!!"
,
Time: Sun Jun 24 21:27:06 EDT 2012, URL: https://twitter.com/KrisJenner/status/217066353914413057, Tweet: Uh... Hacked and I think its @KimKardashian
)

(
KimKardashian
,
KrisJenner
,
Time: Sun Jun 24 20:45:03 EDT 2012, URL: https://twitter.com/KimKardashian/status/217055771219865600, Tweet: Vacation surprise for @KrisJenner http://t.co/EQKkBWUz Tune in to E! after Oprah for brand new episode of Keeping Up!
,
Time: Sun Jun 24 21:27:06 EDT 2012, URL: https://twitter.com/KrisJenner/status/217066353914413057, Tweet: Uh... Hacked and I think its @KimKardashian
)

(
KimKardashian
,
ScottDisick
,
Time: Sun Jun 24 20:35:22 EDT 2012, URL: https://twitter.com/KimKardashian/status/217053334023049216, Tweet: Ok folks. Its @ScottDisick and @KourtneyKardash turn.... Scotts interview might be crazy LOL
,
Time: Sat Jun 23 20:36:14 EDT 2012, URL: https://twitter.com/ScottDisick/status/216691165565685760, Tweet: Fuck it! Lets all get 1 RT“@crystalvill96: @ScottDisick @kimkardashian why don't you just buy an airplane together? lol #makesurlifeeasier
)

(
PerezHilton
,
UsherRaymondIV
,
Time: Sun May 20 18:59:51 EDT 2012, URL: https://twitter.com/PerezHilton/status/204390970056384512, Tweet: @UsherRaymondIV Watch Usher's performance at the #BillboardMusicAwards HERE: http://perez.ly/LsbTu9 Yeahman!!
,
Time: Sun May 20 22:06:35 EDT 2012, URL: https://twitter.com/UsherRaymondIV/status/204392696847147009, Tweet: @PerezHilton....Thanx Perez
)

(
InStyle
,
minkakelly
,
Time: Sun Jun 24 15:00:20 EDT 2012, URL: https://twitter.com/InStyle/status/216969022267330560, Tweet: Happy Birthday, @minkakelly! http://t.co/1C6hGK2l
,
Time: Sun Jun 24 15:03:03 EDT 2012, URL: https://twitter.com/minkakelly/status/216969703552323584, Tweet: @InStyle Thank you!!!
)

(
delta_goodrem
,
PerezHilton
,
Time: Sun Jun 24 15:55:19 EDT 2012, URL: https://twitter.com/delta_goodrem/status/217028253905457154, Tweet: @PerezHilton I am totally a #HugWhore #Love2Hug :) xx
,
Time: Sun Jun 24 18:07:55 EDT 2012, URL: https://twitter.com/PerezHilton/status/217061411581472768, Tweet: @delta_goodrem We need to hug soon! Been too long!! Xoox


(
PerezHilton
,
katyperry
,
Time: Wed Jun 13 19:56:02 EDT 2012, URL: https://twitter.com/PerezHilton/status/213102675317882880, Tweet: There's a reason why @KatyPerry is the current #1 pop star on the planet! Lots of talent + authenticity + likeability + ?#GoodGirlsDoWin?!
,
Time: Thu Jun 14 04:22:13 EDT 2012, URL: https://twitter.com/katyperry/status/213184625038721024, Tweet: @PerezHilton thank you for all the kind words. You're one of the 1st reasons WHY I exist. #loveperez
)
FINISHED