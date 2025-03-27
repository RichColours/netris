# Netris

My first game, a Tetris clone which will eventually go 2-player head-to-head mode over a network.

Goals:
* Portable, should run on anything
* Runs cool - no fast loops
* Networked Tetris for multiplayer

Goals for server-side:
* Low latency and zero GC techniques

Non-goals:
* 

Learning opportunities:
* Get familiar with some game basics
* Show myself I can write something myself before one day using engines
* Explore rendering, hopefully run into problems

## How to run
* JRE 21
* Clone repo
* `./gradlew client:run`

## How to play
* `A` and `D` rotate left and right
* `Left` and `Right` move left and right
* `Down` falls the piece
* `Escape` to exit

## Evolution:

After about 8 hours:
* Left/right/clock tick downing works mostly.
* Colours, pieces, animation.

![img.png](/history-pics/img0.png)

Impressive breakage while developing the row scoring:

![img.png](/history-pics/img1.png)

