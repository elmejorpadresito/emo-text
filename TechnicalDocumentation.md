

# Technical Documentation #

Technical documentation for Emo-Text Version 0.1.0

# Basic Operation #

  1. User Authentication
    * If the user is authenticated return friends list
  1. Sign Up User
  1. Add New Friend
  1. Response of Friends Requests
  1. Test Web API

# Packages & Classes #

  * `communication`
    * `Socketer.java`
  * `gui`
    * `FriendAdder.java`
    * `ListOfFriends.java`
    * `Login.java`
    * `PerformingMessaging.java`
    * `SignUp.java`
    * `WaitingListFriends.java`
  * `into_type`
    * `infoOfFriend.java`
    * `InfoOfMessage.java`
    * `InfoStatus.java`
  * `interphase`
    * `Manager.java`
    * `SocketInterphase.java`
    * `Updater.java`
  * `service`
    * `MessagingService.java`
  * `toolbox`
    * `ControllerOfFriend.java`
    * `HandlerXML.java`
    * `MessageController.java`
    * `StorageManipulater.java`

Each package has been ordered according to function, and it can be customized to enhance emo-text. We have yet to integrate the camera components as we are still researching ways of integrating this functionality.

## Login Screen ##

Layout:
  * `loggin_in.xml`

Image:
https://lh5.googleusercontent.com/-acv17qy3XFU/U2CfCsv7d6I/AAAAAAAALb8/bEauNXDark4/w360-h593-no/Login.PNG

In this screen the user will enter the username and credentials in order to login for the project.

Update:
The team is currently working on creating a friendlier way of logging in and or creating a new account on our servers.

We hope to be able to integrate the facebook-android-sdk-3.7 to allow users to login via Facebook. This will lead to wider audiences and better overall experience of the application, as we hope it will allow us to send request based on your pre-established friend list.

## Sign-up Screen ##

Layout:
  * `signingup.xml`

https://lh3.googleusercontent.com/-v75Fr39SnZM/U2CfBoY_17I/AAAAAAAALbw/ismjE9xKqJg/w360-h593-no/Create+and+Account.PNG

This is the screen where new users will be allowed to create an account to begin to use Emo-Text.

## Friend List Screen ##

Layout:
  * `friend_list_screen.xml`

https://lh6.googleusercontent.com/-FHf4P1N8BNc/U2CfBjroKUI/AAAAAAAALb0/_06uGHcUIJI/w373-h593-no/Friend+List.PNG

This is the considered to be the 'Main Menu' of the application. This is where all friends will be displayed as well as their 'live' availability.

## Messenger ##

Layout:
  * `message.xml`

![http://www.vale.com/EN/aboutvale/initiatives/itabiritos/PublishingImages/ProjetoItabiritos/loading.gif](http://www.vale.com/EN/aboutvale/initiatives/itabiritos/PublishingImages/ProjetoItabiritos/loading.gif)

The message.xml layout allows the users to communicate by providing the 'canvas' where the messages are to be posted.