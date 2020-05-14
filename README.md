# <img src="https://github.com/simonesestito/shops-queue-android/blob/master/.github/app_logo.png" width="64"> Shops Queue (Android app)

[![Android CI](https://github.com/simonesestito/shops-queue-android/workflows/Android%20CI/badge.svg)](https://github.com/simonesestito/shops-queue-android/actions?query=workflow%3A%22Android+CI%22)

[![Download latest APK](https://github.com/simonesestito/shops-queue-android/blob/master/.github/badge.png?raw=true)](https://firebasestorage.googleapis.com/v0/b/shops-queue.appspot.com/o/app.apk?alt=media)

This repository is part of the **Shops Queue** project.

Other related repositories:
- [PHP backend repo](https://github.com/simonesestito/shops-queue-php)

## Table of contents

- [Introduction](#introduction)
- [Features](#features)
- [Project Architecture](#arch)
- [UI Design](#ui)
- [Navigation](#navigation)
- [Dependency Injection](#di)
- [Continuous Deployment](#cd)
- [License](#license)

<a name="introduction"></a>
## Introduction

An idea to save time and avoid queues outside supermarkets and other shops in general.

This is a project made for my graduation exam.
In the last year we usually study Java, and that's what determined the language to use for the app, instead of Kotlin.

It was created during Coronavirus lockdown in Italy. The idea came from [a post on Facebook](https://m.facebook.com/story.php?story_fbid=2814783488643375&id=310949775693438) written in March 2020 by the Italian minister of Agricultural, Food and Forestry Policies. It's also inspired by the system most post offices already use to save time.

<a name="features"></a>
## Features

A user on this platform can cover different roles:
- Simple user
- Shop owner
- Administrator

### User

A user must sign up to use the platform.

He can discover all the shops nearby which use this system to manage queues. Of course, shops can be found by their name or address too.

There's the possibility to queue at the store when you're still at home, arriving at the shop only when it's your turn.

A user will receive a push notification (via Firebase Cloud Messaging) when it's almost his turn.

### Shop owner

A shop owner must reach out an administrator to receive an appropriate account and to register his business in the platform.

After that, he can manage the queue of his shop and call the next customer.

### Administrator

An administrator can manage all accounts (both users and shop owners) and all shops.

For every user, the access token returned from the server during the login phase,
it's stored on the device using [EncryptedSharedPreferences](https://github.com/simonesestito/shops-queue-android/blob/master/app/src/main/java/com/simonesestito/shopsqueue/di/module/SharedPreferencesModule.java).

<a name="ui"></a>
## UI Design

The app heavily uses the Google Material Components library.

The app graphical layout is based on the principles of the Material Design.

It provides both a light and a dark theme. The theme to use is determined by looking at the current theme the user applied on the entire phone, if any.
By default, it uses the light theme.

<a name="arch"></a>
## Project architecture

This app follows the **MVVM** pattern, as recommended by Google in the last years.

The *View* layer is responsible for the interaction with the user. It handles click events and displays the data.

It stores and works with the data available in the *ViewModel* layer. A ViewModel is a special class which exposes the current state of the View, often using a LiveData object.
This project uses a custom LiveData, called [LiveResource](https://github.com/simonesestito/shops-queue-android/blob/master/app/src/main/java/com/simonesestito/shopsqueue/util/livedata/LiveResource.java).

Then, we have the *Model* layer, where all the data comes from. In this specific app we have no local SQL databases, so this layer includes SharedPreferences and Retrofit classes.

**General data flow in the app:**

![App data flow](https://github.com/simonesestito/shops-queue-android/blob/master/.github/mvvm_data_flow.png?raw=true)

<a name="navigation"></a>
## Navigation

This project follows the **Single Activity Architecture** and the **Jetpack Navigation Library**.

It has a single Activity, which is the entrypoint of the app. Every other piece of the user's flow is implemented as a Fragment.
Every part of the app is divided in [different sub-graphs](https://github.com/simonesestito/shops-queue-android/tree/master/app/src/main/res/navigation).

The app uses **Deep Links** to go immediately to the login fragment. It's used as a redirect URI after the email address is validated.

<a name="di"></a>
## Dependency Injection

Dependency Injection is a design pattern used in Object Oriented Programming. It allows the instatiation of a class which depends on another, and so on.

This project uses **Google Dagger**.

Everything that concerns dependency injection, is located [here](https://github.com/simonesestito/shops-queue-android/tree/master/app/src/main/java/com/simonesestito/shopsqueue/di).

<a name="cd"></a>
## Continuous Deployment (CD)

It's a release process that automatically deploys new versions of the software.

This project uses **GitHub Actions**.

Every time a new commit is pushed to this repository, a workflow is triggered.
A new version of the app is compiled on GitHub servers, shrinked using Android R8, signed with the release certificate and, finally, pushed to the app's storage bucket to let everyone download it.

The file which describes the workflow and the build process is [android.yaml](https://github.com/simonesestito/shops-queue-android/blob/master/.github/workflows/android.yml)

<a name="license"></a>
## License

    Copyright 2020 Simone Sestito
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
