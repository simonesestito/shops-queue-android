# Shops Queue (Android app)

[![Android CI](https://github.com/simonesestito/shops-queue-android/workflows/Android%20CI/badge.svg)](https://github.com/simonesestito/shops-queue-android/actions?query=workflow%3A%22Android+CI%22)

This repository is part of the **Shops Queue** project.

Other related repositories:
- [PHP backend repo](https://github.com/simonesestito/shops-queue-php)

## Table of contents

- [Introduction](#introduction)
- [Features](#features)
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
