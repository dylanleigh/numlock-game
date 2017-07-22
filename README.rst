
=======
NumLock
=======

This is a little client-server game created for an assessment when I
was studying Computer Network Engineering 1 at RMIT. The name
"NumLock" comes from the main strategy - block the other player from
accessing high numbers, while putting the other player in a position
where he would be forced to give them to you.

Requirements
============

Java 1.5 or higher, with Swing on the client.
Tested on FreeBSD 5.3, Solaris 9 and Windows XP.

Gameplay
========

You and the server take turns in picking numbers from the grid.

You may only pick numbers on the *row* that the server made it's move;
the server picks numbers on the *column* that you made your move on.
Thus a good player can lock the other player out of the higher
numbers.

The numbers you pick are added to your score and the highest score wins.

The game will end if it is impossible for either player to make a move
(no numbers left on the row/column they are required to move in).

The numbers in the highscore list are the player score minus the
computer score.

History
=======

The original client and server were written in Java and the assessment
only required the server to interact with one client at a time. During
the holidays immediately after I had a little free time and I fixed it
to support multiple connections (version 1.0.1).

Developer Documentation
=======================

My original report for EEET2094 (Computer Network Engineering 1) is in
the file report.html; amongst other things it describes the protocol.


Thanks
======

Mike (http://quex.org) for the game name.

David Marshall, Steven Buck and Emil Mikulic helped me test the program
(i.e. they played the game lots and tried to break it).

